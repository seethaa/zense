package edu.cmu.sv.mobisens.content;

import java.util.Hashtable;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;

import edu.cmu.sv.mobisens.MobiSensMessageBox;
import edu.cmu.sv.mobisens.MobiSensService;
import edu.cmu.sv.mobisens.R;
import edu.cmu.sv.mobisens.io.Directory;
import edu.cmu.sv.mobisens.io.MobiSensLog;
import edu.cmu.sv.mobisens.net.HttpGetRequest;
import edu.cmu.sv.mobisens.net.HttpRequestForCMUSVProjects;
import edu.cmu.sv.mobisens.net.URLs;
import edu.cmu.sv.mobisens.settings.ServiceParameters;
import edu.cmu.sv.mobisens.util.Annotation;

public class MessageBoxWidget extends PeriodicNetworkRequestWidget {
	private static final String TAG = "MessageBoxWidget";
	
	private static final String CLASS_PREFIX = MessageBoxWidget.class.getName();
	public static final String ACTION_REFRESH_MESSAGEBOX_NOTIFY = CLASS_PREFIX + ".action_refresh_messagebox_notify";
	public static final String ACTION_REMOVE_MESSAGEBOX_NOTIFY = CLASS_PREFIX + ".action_remove_messagebox_notify";
	public static final String ACTION_UPDATE_UNREAD_MESSAGE_COUNT = CLASS_PREFIX + ".action_update_unread_message";
	
	public static final String EXTRA_UNREAD_MESSAGE_COUNT = CLASS_PREFIX + ".extra_unread_message_count";
	
	private final static int GET_UNREAD_MESSAGE_COUNT_COMPLETED = 1;
	
	private boolean annotationNeedUpload = false;
	private int lastUnreadCount = -1;

	private boolean shouldSleep = false;

	private long lastScreenOn = 0;
	
	public MessageBoxWidget(){
		super(MobiSensService.getParameters()
				.getServiceParameter(
						ServiceParameters.GET_PROFILE_INTERVAL));
		
	}
	
	private int getUnreadMessageCount(){
		HttpGetRequest httpGet = new HttpGetRequest();
		Hashtable<String, String> params = new Hashtable<String,String>();
		params.put("upload_key", HttpRequestForCMUSVProjects.DEFAULT_UPLOADKEY);
		params.put("device_id", this.getDeviceID());
		
		String result = httpGet.send(URLs.GET_UNREAD_MESSAGECOUNT_URL, params);
		if(result.equals(""))
			return -1;
		
		try{
			Integer count = Integer.parseInt(result);
			return count.intValue();
			
		}catch(Exception ex){
			Log.e(TAG, ex.getMessage(), ex);
		}
		
		return -1;
	}
	
	protected void onWake(){
		
		if(this.getContext() == null)
			return;
		
		if(this.isShouldSleep())
			return;
		
		makeMessageBoxNotification();
		
		
		MobiSensLog.log("Get messagebox info done.");
	}
	
	public void register(ContextWrapper contextWrapper){
		IntentFilter filter = new IntentFilter(MessageBoxWidget.ACTION_REFRESH_MESSAGEBOX_NOTIFY);
		filter.addAction(MessageBoxWidget.ACTION_REMOVE_MESSAGEBOX_NOTIFY);
		filter.addAction(ProfileWidget.ACTION_SERVICE_PARAMETER_UPDATED);
		
		filter.addAction(ActivityWidget.ACTION_REFRESH_ANNO);
		filter.addAction(UploadWidget.ACTION_UPLOAD_DONE);
		
		filter.addAction(Intent.ACTION_SCREEN_OFF);
		filter.addAction(Intent.ACTION_SCREEN_ON);

		this.setFilter(filter);
		
		super.register(contextWrapper);
		
	}
	
	public void unregister(){
		this.cancelMessageBoxNotification();
		super.unregister();
	}
	
	public void onReceive(Context context, Intent intent) {
		super.onReceive(context, intent);
		
		String action = intent.getAction();
		if(action.equals(ACTION_REMOVE_MESSAGEBOX_NOTIFY)){
			this.cancelMessageBoxNotification();
		}
		
		if(action.equals(MessageBoxWidget.ACTION_REFRESH_MESSAGEBOX_NOTIFY)){
			this.checkMessageBoxNotificationNow();
		}
		
		if(action.equals(ActivityWidget.ACTION_REFRESH_ANNO)){
			
			String annoName = intent.getStringExtra(Annotation.EXTRA_ANNO_NAME);
			if(!annoName.equals(Annotation.UNKNOWN_ANNOTATION_NAME)){
				annotationNeedUpload = true;
			}
		}
		
		/*
		if(action.equals(ActivityWidget.ACTION_MODEL_NAME_CHANGED)){
			annotationNeedUpload = true;
		}
		*/
		
		if(Intent.ACTION_SCREEN_OFF.equals(action)){
			this.setShouldSleep(true);
		}
		
		if(Intent.ACTION_SCREEN_ON.equals(action)){
			this.setShouldSleep(false);
			
			if(System.currentTimeMillis() - lastScreenOn > 60 * 60 * 1000){
				this.setForceWakeUp(true);
				MobiSensLog.log("Set force get message box info.");
			}
			
			this.lastScreenOn = System.currentTimeMillis();
		}
		
	}
	
	/*
	private void uploadAnnotation(){

        this.boradcastFileList(
        		new String[]{ Directory.ANNOTATION_DEFAULT_DATA_FOLDER + 
        				AnnotationWidget.getAnnotationFileName(this.getContext()) }, 
        		String.valueOf(Directory.FILE_TYPE_ANNOTATION), 
        		false,
        		true);
	}
	
	private void uploadLog(){
        this.boradcastFileList(
        		new String[]{ MobiSensLog.LOG_FILE_PATH }, 
        		String.valueOf(Directory.FILE_TYPE_LOG), 
        		true,
        		false);
	}
	
	
	
	protected void boradcastFileList(String[] fileList, String listType, boolean deletAfterUpload, boolean tellAfterUploaded){
		Intent uploadIntent = new Intent(UploadWidget.ACTION_ADD_FILES);
		uploadIntent.putExtra(UploadWidget.EXTRA_DELETE_AFTER_UPLOAD, deletAfterUpload);
		uploadIntent.putExtra(UploadWidget.EXTRA_FILE_TYPE, listType);
		uploadIntent.putExtra(UploadWidget.EXTRA_SENDER_APPNAME, this.getContext().getApplicationInfo().processName);
		uploadIntent.putExtra(UploadWidget.EXTRA_UPLOAD_FILES, fileList);
		uploadIntent.putExtra(UploadWidget.EXTRA_TELL_AFTER_UPLOADED, tellAfterUploaded);
		
		this.getContext().sendBroadcast(uploadIntent);
	}
	*/
	
	
	private void checkMessageBoxNotificationNow(){
		Thread checkNotifyThread = new Thread(){
			public void run(){
				makeMessageBoxNotification();
				MobiSensLog.log("Check messagebox done.");
			}
		};
		
		checkNotifyThread.start();
	}
	
	private void cancelMessageBoxNotification(){
		NotificationManager mNotificationManager = (NotificationManager) this.getContext().getSystemService(Context.NOTIFICATION_SERVICE);
		mNotificationManager.cancel(GET_UNREAD_MESSAGE_COUNT_COMPLETED);
	}
	
	private void createMessageBoxNotification(CharSequence title, int icon){
		NotificationManager mNotificationManager = (NotificationManager) this.getContext().getSystemService(Context.NOTIFICATION_SERVICE);

		long when = System.currentTimeMillis();
		Notification notification = new Notification(icon, this.getContext().getString(R.string.messagebox_notify_ticker_text), when);
		notification.flags = Notification.FLAG_AUTO_CANCEL;

		CharSequence contentTitle = title;
		CharSequence contentText = this.getContext().getString(R.string.messagebox_notify_content_text);
		
		Intent messageboxIntent = new Intent(this.getContext(), MobiSensMessageBox.class);
		messageboxIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		
		PendingIntent contentIntent = PendingIntent.getActivity(this.getContext(), 0, messageboxIntent, 0);

		notification.setLatestEventInfo(getContext(), contentTitle, contentText, contentIntent);
		mNotificationManager.notify(GET_UNREAD_MESSAGE_COUNT_COMPLETED, notification);
	}
	
	private int makeMessageBoxNotification(){
		int unreadCount = getUnreadMessageCount();  //This line will block you
		if(unreadCount == -1)
			unreadCount = 0;
		int newMessageCount = this.shouldShowNotify(unreadCount);
		if(newMessageCount > 0){
    		if(newMessageCount > 1){
        		createMessageBoxNotification("MobiSens: You have " + String.valueOf(newMessageCount) + " new messages.", R.drawable.message_icon);
        	}else if(newMessageCount == 1){
        		createMessageBoxNotification("MobiSens: You have " + String.valueOf(newMessageCount) + " new message.", R.drawable.message_icon);
        	}
		}
		
		ServiceParameters params = MobiSensService.getParameters();
		long getMessageInterval = params.getServiceParameter(ServiceParameters.GET_PROFILE_INTERVAL);
		this.setWakeupInterval(getMessageInterval);
		
		if(this.lastUnreadCount != unreadCount){
			broadcastUnreadMessageCount(unreadCount);
		}
		
		this.lastUnreadCount  = unreadCount;
		
		return unreadCount;
	}
	
	private int shouldShowNotify(int unreadCount){

		SharedPreferences settings = this.getContext().getSharedPreferences(this.getClass().getName(), 0);
		int lastUnreadCount = settings.getInt(MobiSensService.SETTING_LAST_UNREAD_MESSAGE_COUNT, 0);
		
		Editor edit = settings.edit();
		edit.putInt(MobiSensService.SETTING_LAST_UNREAD_MESSAGE_COUNT, unreadCount);
		edit.commit();
		
		return unreadCount - lastUnreadCount;
	}
	
	private void broadcastUnreadMessageCount(int count){
		if(count < 0)
			count = 0;
		
		Intent intent = new Intent(MessageBoxWidget.ACTION_UPDATE_UNREAD_MESSAGE_COUNT);
		intent.putExtra(MessageBoxWidget.EXTRA_UNREAD_MESSAGE_COUNT, count);
		this.getContext().sendBroadcast(intent);
	}
	
	private void setShouldSleep(boolean shouldSleep) {
		this.shouldSleep = shouldSleep;
	}

	private boolean isShouldSleep() {
		return shouldSleep;
	}
}
