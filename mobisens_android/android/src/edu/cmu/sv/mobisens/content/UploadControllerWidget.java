package edu.cmu.sv.mobisens.content;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import edu.cmu.sv.mobisens.MobiSensService;
import edu.cmu.sv.mobisens.io.MobiSensLog;
import edu.cmu.sv.mobisens.power.Alarm;
import edu.cmu.sv.mobisens.settings.ServiceParameters;
import edu.cmu.sv.mobisens.threading.CyclingWakeUpThread;

public class UploadControllerWidget extends Widget {
	
	private static final String CLASS_PREFIX = UploadControllerWidget.class.getName();
	public static final String EXTRA_CLEANUP = CLASS_PREFIX + ".extra_cleanup";
	
	private String uploadAction = "action_unknown";
	private long uploadIntervalByWakeUpCycle = 60;
	private long alarmCount = 0;
	
	
	private UploadControllerWidget(){}
	
	public UploadControllerWidget(String uploadAction){
		this.setUploadAction(uploadAction);
	}
	
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		String action = intent.getAction();
		if(action.equals(ProfileWidget.ACTION_SERVICE_PARAMETER_UPDATED)){
			this.onSettingsChanged();
		}
		
		if(Alarm.ACTION_ALARM.equals(action)){
			alarmCount++;
			if(alarmCount >= this.uploadIntervalByWakeUpCycle){
				UploadControllerWidget.this.broadcastUploadNotification(false);
				MobiSensLog.log("Alarcount: " + alarmCount + ", upload requested");
				alarmCount = 0;
			}
		}

	}
	
	private void onSettingsChanged(){
		Log.i("Test", "Upload cycle:" + MobiSensService.getParameters().getServiceParameter(ServiceParameters.SYSTEM_DUMP_INTERVAL) / ServiceParameters.CYCLING_BASE_MS);
		
		this.uploadIntervalByWakeUpCycle = MobiSensService.getParameters().getServiceParameter(
				ServiceParameters.SYSTEM_DUMP_INTERVAL) / ServiceParameters.CYCLING_BASE_MS;
		
	}

	private void setUploadAction(String uploadAction) {
		this.uploadAction = uploadAction;
	}
	
	/*
	 * isCleanUp = true when it's the last upload request when MobiSens
	 * is going to close.
	 */
	protected void broadcastUploadNotification(boolean isCleanUp){
		Intent uploadNotifyIntent = new Intent(this.uploadAction);
		uploadNotifyIntent.putExtra(UploadControllerWidget.EXTRA_CLEANUP, isCleanUp);
		
		this.getContext().sendBroadcast(uploadNotifyIntent);
		
		MobiSensLog.log(this.uploadAction + ": upload request sent.");
	}

	public String getUploadAction() {
		return uploadAction;
	}

	@Override
	protected String[] getActions() {
		// TODO Auto-generated method stub
		
		return new String[]{
				Alarm.ACTION_ALARM,
				ProfileWidget.ACTION_SERVICE_PARAMETER_UPDATED
		};
	}
}
