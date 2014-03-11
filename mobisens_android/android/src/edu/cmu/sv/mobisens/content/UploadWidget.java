package edu.cmu.sv.mobisens.content;

import java.util.LinkedList;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.BatteryManager;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.os.SystemClock;
import android.util.Log;

import edu.cmu.sv.mobisens.MobiSensService;
import edu.cmu.sv.mobisens.SystemSensService;
import edu.cmu.sv.mobisens.io.MobiSensLog;
import edu.cmu.sv.mobisens.net.FileToUpload;
import edu.cmu.sv.mobisens.net.HttpRequestForCMUSVProjects;
import edu.cmu.sv.mobisens.net.Network;
import edu.cmu.sv.mobisens.net.URLs;
import edu.cmu.sv.mobisens.settings.LocalSettings;
import edu.cmu.sv.mobisens.settings.ServiceParameters;
import edu.cmu.sv.mobisens.threading.CyclingWakeUpThread;
import edu.cmu.sv.mobisens.ui.UploadProgressRenderWidget;
import edu.cmu.sv.mobisens.util.GeoIndex;

public class UploadWidget extends PeriodicNetworkRequestWidget {
	private static final String TAG = "UploadWidget";
	
	private static final String CLASS_PREFIX = UploadWidget.class.getName();
	public static final String ACTION_ADD_FILES = CLASS_PREFIX + ".action_add_files";
	public static final String ACTION_FORCE_DUMP = CLASS_PREFIX + ".action_force_dump";
	public static final String ACTION_UPLOAD_DONE = CLASS_PREFIX + ".action_upload_done";
	public static final String ACTION_USER_FORCE_UPLOAD = CLASS_PREFIX + ".action_user_force_upload";
	public static final String ACTION_USER_END_UPLOAD = CLASS_PREFIX + ".action_user_end_upload";
	public static final String ACTION_USER_UPLOAD_END = CLASS_PREFIX + ".action_user_upload_ended";
	public static final String ACTION_UPLOAD_PROGRESS = CLASS_PREFIX + ".action_upload_progress";
	public static final String ACTION_NETWORK_ERROR = CLASS_PREFIX + ".action_network_error";
	
	public static final String EXTRA_UPLOAD_FILES = CLASS_PREFIX + ".extra_upload_files";
	public static final String EXTRA_UPLOADED_FILE = CLASS_PREFIX + ".extra_uploaded_file";
	public static final String EXTRA_DELETE_AFTER_UPLOAD = CLASS_PREFIX + ".extra_delete_after_upload";
	public static final String EXTRA_SENDER_APPNAME = CLASS_PREFIX + ".extra_sender_pid";
	public static final String EXTRA_FILE_TYPE = CLASS_PREFIX + ".extra_filetype";
	public static final String EXTRA_TELL_AFTER_UPLOADED = CLASS_PREFIX + ".extra_tell_after_uploaded";
	public static final String EXTRA_UPLOAD_PROGRESS = CLASS_PREFIX + ".extra_upload_progress";
	
	private static final int MAX_UPLOAD_PERIOD = 2 * 60 * 1000;
	private int maxWaitingFile = 1000;
	private int queueSizeSnapshot = 0;
	private int fileUploaded = 0;
	
	private long lastScreenOn = 0;
	private long lastWakeupInterval = 0;
	
	private boolean canUpload = true;
	private boolean powerSaveMode = false;
	private boolean isUploading = false;
	private boolean isCharging = false;
	private boolean isRequestByUser = false;
	
	private LinkedList<FileToUpload> uploadQueue = new LinkedList<FileToUpload>();
	private WakeLock userUploadWakelock = null;

	protected void onWake(){
		
		if(!canUpload() && this.isRequestByUser == false)
			return;
		
		if(Network.isWificonnected(getContext())
				&& this.isRequestByUser == false)
			return;
		
		this.doUpload();
		
		//MobiSensLog.log("Check upload done.");
		
	}
	
	protected void onExiting(){
		this.onWake();
	}
	
	public UploadWidget(){
		super(1000 /* MAX_UPLOAD_PERIOD */);
		this.setMaxWaitingFile(1000);
	}
	
	public UploadWidget(int maxWaitingFile){
		super(1000 /* MAX_UPLOAD_PERIOD */);
		this.setMaxWaitingFile(maxWaitingFile);
		
	}

	public void addToUpload(FileToUpload file){
		if(file == null)
			return;
		
		synchronized(this.uploadQueue){
			for(FileToUpload existingFile:this.uploadQueue){
				if(existingFile.getPath().equals(file.getPath())){
					return;
				}
			}
			this.uploadQueue.add(file);
		}
		
		MobiSensLog.log("file: " + file.getPath() + " added to upload queue.");
	}
	
	
	public void register(ContextWrapper contextWrapper){
		IntentFilter filter = new IntentFilter(ACTION_ADD_FILES);
		filter.addAction(UploadWidget.ACTION_USER_FORCE_UPLOAD);
		filter.addAction(UploadWidget.ACTION_USER_END_UPLOAD);
		
		filter.addAction(Intent.ACTION_BATTERY_CHANGED);
		filter.addAction(ProfileWidget.ACTION_SERVICE_PARAMETER_UPDATED);
		
		filter.addAction(Intent.ACTION_SCREEN_OFF);
		filter.addAction(Intent.ACTION_SCREEN_ON);
		
		this.setFilter(filter);
		
		super.register(contextWrapper);
		
	}
	
	public void unregister(){
		super.unregister();
	}
	
	public void onReceive(Context context, Intent intent) {
		super.onReceive(context, intent);
		
		// TODO Auto-generated method stub
    	String action = intent.getAction();
    	if(action.equals(ACTION_ADD_FILES)){
    		this.addUploadFiles(intent);
    	}
    	
    	if (action.equals(Intent.ACTION_BATTERY_CHANGED)) {
    		if(this.isRequestByUser)
    			return;
    		
    		this.processBatteryStatus(intent);
        }
    	
    	if(action.equals(ProfileWidget.ACTION_SERVICE_PARAMETER_UPDATED)){
    		if(this.canPeriodicallyDump() && this.canUpload() == false){
        		//If we don't do this, once the upload_when_charging was set, we cannot recover.
        		setCanUpload(true);
        		
        	}
    	}
    	
    	if(Intent.ACTION_SCREEN_OFF.equals(action)){
    		if(this.isRequestByUser)
    			return;
    		
    		if(!this.isCharging){
				this.powerSaveMode = true;
				//this.resetUploadRate();
				
				MobiSensLog.log("Screen off and not charging, uploader turns to power saving mode.");
    		}else{
    			MobiSensLog.log("Screen off but charging, uploader keeps in full power mode.");
    		}
		}
		
		if(Intent.ACTION_SCREEN_ON.equals(action)){
			if(this.isRequestByUser){
				this.lastScreenOn = SystemClock.elapsedRealtime();
				return;
			}
			
			// No need to check this.isCharging, always
			// exit powerSaving mode
			this.powerSaveMode  = false;
			MobiSensLog.log("Screen on, uploader turns to full power mode. QueueSize: " + this.uploadQueue.size());
			
			if(SystemClock.elapsedRealtime() - lastScreenOn > this.getWakeupInterval()){
				//this.forceCheckUpload();
				MobiSensLog.log("Set force check upload.");
			}
			
			this.lastScreenOn = SystemClock.elapsedRealtime();
		}
		
		if(UploadWidget.ACTION_USER_FORCE_UPLOAD.equals(action)){
			triggerUserUpload();
		}
		
		if(UploadWidget.ACTION_USER_END_UPLOAD.equals(action)){
			endUserUpload();
		}
	}
	
	private void triggerUserUpload(){
		if(this.isRequestByUser)
			return;
		
		this.isRequestByUser = true;
		this.requestWakeLock();
		
		
		this.queueSizeSnapshot = this.uploadQueue.size();
		this.fileUploaded = 0;
		
		LocalSettings.setPushing(getContext(), true);
		
		String log = "User request upload. Queue size: " + this.queueSizeSnapshot;
		Log.i(TAG, log);
		MobiSensLog.log(log);
		
		AsyncTask<String, Void, Boolean> task = new AsyncTask<String, Void, Boolean>(){

			@Override
			protected Boolean doInBackground(String... params) {
				// TODO Auto-generated method stub
				return Network.canConnectToServer(URLs.DEFAULT_UPLOAD_URL);
			}
			
			protected void onPostExecute(Boolean result) {
				if(!result){
					tellNetworkFailure();
					endUserUpload();
				}
		    }

			
		};
		
		task.execute(URLs.MESSAGEBOX_CONNECTION_URL);
		
	}
	
	private void endUserUpload(){
		// Set the interval back! REMEMBER!!!
		//resetUploadRate();  // To save more power, we can put it here.
		
		if(!this.isRequestByUser)
			return;
		
		this.isRequestByUser = false;
		this.releaseWakeLock();
		
		
		LocalSettings.setPushing(getContext(), false);
		
		
		// Notify the UI
		Intent uploadEndedIntent = new Intent(ACTION_USER_UPLOAD_END);
		this.getContext().sendBroadcast(uploadEndedIntent);
		
		
		
		String log = "User upload ended.";
		Log.i(TAG, log);
		MobiSensLog.log(log);
	}
	
	private void requestWakeLock(){
		PowerManager powerManager = (PowerManager) this.getContext().getSystemService(Context.POWER_SERVICE);
		this.userUploadWakelock = powerManager.newWakeLock(PowerManager.FULL_WAKE_LOCK, CLASS_PREFIX);
		this.userUploadWakelock.acquire();
		 
		String log = "Full wakelock acquired.";
		Log.i(TAG, log);
		MobiSensLog.log(log);
	}
	
	private void releaseWakeLock(){
		
		if(this.userUploadWakelock != null){
			this.userUploadWakelock.release();
			this.userUploadWakelock = null;
		}
		
		String log = "Full wakelock released.";
		Log.i(TAG, log);
		MobiSensLog.log(log);
	}

	
	private void setMaxWaitingFile(int maxWaitingFile) {
		this.maxWaitingFile = maxWaitingFile;
	}

	public int getMaxWaitingFile() {
		return maxWaitingFile;
	}
	
	private void addUploadFiles(Intent intent){
		boolean deleteAfterUpload = intent.getBooleanExtra(EXTRA_DELETE_AFTER_UPLOAD, true);
		boolean tellAfterUploaded = intent.getBooleanExtra(UploadWidget.EXTRA_TELL_AFTER_UPLOADED, false);
		
		String deviceID = getDeviceID();
		String fileType = intent.getStringExtra(EXTRA_FILE_TYPE);
		String[] paths = intent.getStringArrayExtra(EXTRA_UPLOAD_FILES);
		String senderApp = intent.getStringExtra(EXTRA_SENDER_APPNAME);
		
		if(senderApp == null || 
			senderApp.equals(this.getContext().getApplicationInfo().processName) == false){
			//This intent is not for us.
			
			return;
		}
		
		for(String path:paths){
			FileToUpload fileToUpload = new FileToUpload(path, deviceID, fileType, deleteAfterUpload);
			fileToUpload.setTellAfterFinsished(tellAfterUploaded);
			
			addToUpload(fileToUpload);
		}
		
		
	}
	
	
	protected boolean canUpload(){
    	return this.canUpload ;
    }
    
    private void setCanUpload(boolean value){
    	this.canUpload = value;
    }
    
    protected boolean canPeriodicallyDump(){
    	ServiceParameters params = MobiSensService.getParameters();
    	return params.getServiceParameterBoolean(ServiceParameters.ENABLE_SYSTEM_DUMP_BY_INTERVAL);
    }
    
	private void processBatteryStatus(Intent intent){

    	int status = intent.getIntExtra(BatteryManager.EXTRA_STATUS,
                BatteryManager.BATTERY_STATUS_UNKNOWN);
    	
    	
    	switch(status){
        	case BatteryManager.BATTERY_STATUS_NOT_CHARGING:
        	case BatteryManager.BATTERY_STATUS_DISCHARGING:
        		this.isCharging = false;
        		
        		// When the app is set to upload
        		// only when charging, disable upload 
        		// when the phone is discharging.
        		if(!canPeriodicallyDump()) {
					setCanUpload(false);
				}else{
					
					// "reset" the value to true
					setCanUpload(true);
				}
        		
        		//this.resetUploadRate();
    		break;
        	case BatteryManager.BATTERY_STATUS_FULL:
        	case BatteryManager.BATTERY_STATUS_CHARGING:
        		// When charging, always enable uploading.
        		setCanUpload(true);
        		this.isCharging = true;
        		
        		// If MobiSens is under power saving mode, 
        		// goes into full power mode.
        		if(this.powerSaveMode){
        			this.powerSaveMode = false;
        		}
        		
        		// Fire the upload!
        		//this.forceCheckUpload();
        		
    		break;
    	}
	}
	
	private void tellAfterUploaded(FileToUpload uploadedFile){
		Intent uploadDoneIntent = new Intent(UploadWidget.ACTION_UPLOAD_DONE);
		uploadDoneIntent.putExtra(UploadWidget.EXTRA_UPLOADED_FILE, uploadedFile.getPath());
		uploadDoneIntent.putExtra(UploadWidget.EXTRA_FILE_TYPE, uploadedFile.getFileType());
		
		this.getContext().sendBroadcast(uploadDoneIntent);
	}
	
	private void tellProgress(int progress){
		if(this.isRequestByUser){
			Intent progressIntent = new Intent(UploadWidget.ACTION_UPLOAD_PROGRESS);
			progressIntent.putExtra(UploadWidget.EXTRA_UPLOAD_PROGRESS, progress);
			this.getContext().sendBroadcast(progressIntent);
			
			Log.i(TAG, "Progress: " + (double)progress / 10000.0);
		}
	}
	
	private void tellNetworkFailure(){
		if(this.isRequestByUser){
			Intent progressIntent = new Intent(UploadWidget.ACTION_NETWORK_ERROR);
			this.getContext().sendBroadcast(progressIntent);
		}
	}
	
	private WakeLock uploadLock = null;
	private void releaseCPULock(){
		if(this.uploadLock != null){
			if(this.uploadLock.isHeld())
				this.uploadLock.release();
		}
	}
	
	private WakeLock acquireCPULock(){
		if(this.uploadLock != null){
			if(!this.uploadLock.isHeld())
				this.uploadLock = MobiSensService.acquireWakeLockForService(getContext(), PowerManager.PARTIAL_WAKE_LOCK, CLASS_PREFIX);
		}else{
			this.uploadLock = MobiSensService.acquireWakeLockForService(getContext(), PowerManager.PARTIAL_WAKE_LOCK, CLASS_PREFIX);
		}
		
		return this.uploadLock;
	}
	
	private void doUpload(){
		if(this.isUploading)
			return;
		
		this.isUploading = true;
		synchronized(uploadQueue){
			if(uploadQueue.size() == 0){
				this.isUploading = false;
				tellProgress(UploadProgressRenderWidget.MAX_PROGRESS);
				endUserUpload();
				
				releaseCPULock();
				return;
			}
		}
		
		FileToUpload uploadFile = null;
		HttpRequestForCMUSVProjects httpUploadFileRequest = 
	    	new HttpRequestForCMUSVProjects(
	    			URLs.DEFAULT_GET_PROFILE_URL,
	    			URLs.DEFAULT_UPLOAD_URL, 
	    			HttpRequestForCMUSVProjects.DEFAULT_UPLOADKEY);
		
		if(!Network.canConnectToServer(httpUploadFileRequest.getUploadURL())){
			this.isUploading = false;
			tellNetworkFailure();
			endUserUpload();
			releaseCPULock();
			return;
		}
		
		//MobiSensLog.log("Trying to upload...");
		//Log.i(TAG, "Trying to upload...");
		
		synchronized(uploadQueue){
			if(uploadQueue.size() > 0){
				uploadFile = uploadQueue.poll();
				if(uploadQueue.size() > UploadWidget.this.getMaxWaitingFile() && 
						this.isRequestByUser == false){  // since the user is aware of this, go ahead.
					// if the number of files that are waiting to upload exceeded
					// the max size, don't upload the older ones.
					
					Log.i(TAG, "Too many files to be upload, file " + uploadFile.getPath() + " dropped");
					MobiSensLog.log("Too many files to be upload, file " + uploadFile.getPath() + " dropped");
					
					this.isUploading = false;
					return;
				}
				acquireCPULock();
			}
		}
		
		if(uploadFile != null){
			
			// If the file was deleted before upload, mark it as uploaded,
			// Or we will have a file that can never been upload.
			boolean uploadResult = uploadFile.exist() == false ? true : uploadFile.upload(httpUploadFileRequest);
			if(!uploadResult){
				uploadQueue.add(uploadFile);
			}
			
			if(uploadResult){
				if(uploadFile.needTellAfterFinsished()){
					this.tellAfterUploaded(uploadFile);
				}
				
				this.fileUploaded++;
				this.tellProgress(this.queueSizeSnapshot != 0 ? (this.fileUploaded * 
						UploadProgressRenderWidget.MAX_PROGRESS / 
						this.queueSizeSnapshot) : UploadProgressRenderWidget.MAX_PROGRESS);
				
				if(this.fileUploaded >= this.queueSizeSnapshot || this.uploadQueue.size() == 0){
					this.tellProgress(UploadProgressRenderWidget.MAX_PROGRESS);
					endUserUpload();
					releaseCPULock();
				}
				
				if(this.isRequestByUser){
					Log.i(TAG, "File uploaded: " + uploadFile.getPath());
				}
				//MobiSensLog.log("try to upload " + uploadFile.getPath() + ", upload result: " + uploadResult);
			}
		}else{
			//Log.i(TAG, "Nothing to upload.");
		}
		
		// We don't need to init() the GeoIndex.
		// Just let the save skip when it's uninitialized.
		// We can save multiple times since the save has change protection.
		//GeoIndex.save();
		this.isUploading = false;
	}
	
}
