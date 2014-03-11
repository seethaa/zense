package edu.cmu.sv.mobisens.content;

import edu.cmu.sv.mobisens.MobiSensService;
import edu.cmu.sv.mobisens.io.Directory;
import edu.cmu.sv.mobisens.io.FileOperation;
import edu.cmu.sv.mobisens.io.MobiSensLog;
import edu.cmu.sv.mobisens.power.Alarm;
import edu.cmu.sv.mobisens.settings.ServiceParameters;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.util.Log;

public class SystemDataDumpingWidget extends DataDumpingWidget {
	
	private static final String TAG = "SystemDataDumpingWidget";
	
	public final static String CLASS_PREFIX = SystemDataDumpingWidget.class.getName();
	public final static String ACTION_DESTROY = CLASS_PREFIX + ".action_destroy";
	public final static String ACTION_PREPARE_UPLOAD = CLASS_PREFIX + ".action_prepare_upload";
	
	private long waitCycleCount = 0;

	public static String getDataDirectory(){
    	return Directory.SYSTEM_DEFAULT_DATA_FOLDER;
    }
	
	protected void switchRecordFile(){
		synchronized(syncObject){
			if(this.getContext() == null)
				return;
		}
		
		this.switchRecordFile(SystemDataDumpingWidget.getDataDirectory(), 
				this.getDeviceID() + "_" + Directory.RECORDER_TYPE_SYSTEM, 
				".csv");
	}
	
	
	public void register(ContextWrapper contextWrapper){
		super.register(contextWrapper);
		this.switchRecordFile();
		
	}
	
	public void unregister(){
		// Log a message indicating killing SystemSens
	    String sysStatus = "state,killed";
		String dataRecord = SystemWidget.constructDataRecord( 
	    		sysStatus, SystemWidget.SYSTEMSENS_TYPE, getDeviceID());
		this.writeData(dataRecord);

		this.closeCurrentFileStream();
		
		String[] fileList = FileOperation.getFilesInDirectory(getDataDirectory());
		boradcastFileList(fileList, String.valueOf(Directory.FILE_TYPE_SYSTEM_SENSE), this);
		super.unregister();
	}
	
	public void onReceive(Context context, Intent intent){
		super.onReceive(context, intent);
		
		String action = intent.getAction();
		
		if(action.equals(SystemWidget.ACTION_SYSTEM_DATA_EMITTED)){
			WakeLock lock = MobiSensService.acquireWakeLockForService(this.getContext(), 
					PowerManager.PARTIAL_WAKE_LOCK, this.getClass().getName());
			
    		String dataRecord = intent.getStringExtra(SystemWidget.EXTRA_SYSTEM_DATA);
    		this.writeData(dataRecord);
    		
    		lock.release();
		}
		
		if(Alarm.ACTION_ALARM.equals(action)){
			this.waitCycleCount++;
			if(this.waitCycleCount >= FILE_SPLIT_INTERVAL_MS / ServiceParameters.CYCLING_BASE_MS){
				SystemDataDumpingWidget.broadcastUploadFiles(intent, this);
				waitCycleCount = 0;
				MobiSensLog.log(CLASS_PREFIX + ", record file switched.");
			}
		}
		
		if(ACTION_PREPARE_UPLOAD.equals(action)){
			String[] fileList = FileOperation.getFilesInDirectory(getDataDirectory());
			if(this.getCurrentFileName() != null){
				String[] tmp = fileList;
				fileList = new String[tmp.length - 1];
				int index = 0;
				for(String path:tmp){
					if(!path.equals(this.getCurrentFileName())){
						fileList[index] = path;
						index++;
					}
				}
				
				boradcastFileList(fileList, String.valueOf(Directory.FILE_TYPE_SYSTEM_SENSE), this);
				
				Log.i(TAG, "Upload requested: " + CLASS_PREFIX);
				MobiSensLog.log("Upload requested: " + CLASS_PREFIX);
			}
		}
	}
	
	public static void broadcastUploadFiles(Intent intent, DataDumpingWidget dumper) {
		boolean isCleanUp = intent.getBooleanExtra(UploadControllerWidget.EXTRA_CLEANUP, false);
		
		synchronized(dumper.syncObject){
			String[] fileList = FileOperation.getFilesInDirectory(getDataDirectory());
			dumper.flushCurrentFileStream();
			
			if(isCleanUp){
				dumper.closeCurrentFileStream();
			}else{
				dumper.switchRecordFile();
			}
			
			boradcastFileList(fileList, String.valueOf(Directory.FILE_TYPE_SYSTEM_SENSE), dumper);
			
			Log.i(TAG, "Upload requested: " + CLASS_PREFIX);
			MobiSensLog.log("Upload requested: " + CLASS_PREFIX);
		}
		
	}
	
	public static void broadcastUploadFiles(Intent intent, Context dumper) {
		// TODO Auto-generated method stub
		String[] fileList = FileOperation.getFilesInDirectory(getDataDirectory());

		boradcastFileList(fileList, String.valueOf(Directory.FILE_TYPE_SYSTEM_SENSE), dumper);
		
		Log.i(TAG, "Upload requested: " + CLASS_PREFIX);
		MobiSensLog.log("Upload requested: " + CLASS_PREFIX);
		
	}

	@Override
	protected String[] getActions() {
		// TODO Auto-generated method stub
		return new String[]{
				Alarm.ACTION_ALARM,
				SystemWidget.ACTION_SYSTEM_DATA_EMITTED,
				ACTION_DESTROY,
				ACTION_PREPARE_UPLOAD
		};
	}
}
