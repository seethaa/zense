package edu.cmu.sv.mobisens.content;

import java.io.File;
import java.util.LinkedList;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.IntentFilter;
import edu.cmu.sv.mobisens.MobiSensService;
import edu.cmu.sv.mobisens.io.Directory;
import edu.cmu.sv.mobisens.io.MobiSensDataHolder;
import edu.cmu.sv.mobisens.io.MobiSensLog;
import edu.cmu.sv.mobisens.power.Alarm;
import edu.cmu.sv.mobisens.settings.ServiceParameters;

/*
 * Upload Human annotation, logs.. etc.
 */
public class MiscDataDumpingWidget extends DataDumpingWidget {

	private static final String CLASS_PREFIX = MiscDataDumpingWidget.class.getName();
	private long waitCycleCount = 0;
	
	public void onReceive(Context context, Intent intent) {
		super.onReceive(context, intent);
		
		// TODO Auto-generated method stub
    	String action = intent.getAction();
    	
    	if(Alarm.ACTION_ALARM.equals(action)){
			this.waitCycleCount++;
			if(this.waitCycleCount >= FILE_SPLIT_INTERVAL_MS / ServiceParameters.CYCLING_BASE_MS){
				dumpAnnotation();
				broadcastUploadNotification();
				waitCycleCount = 0;
				MobiSensLog.log(CLASS_PREFIX + ", upload requested.");
			}
		}
	}
	
	private void dumpAnnotation(){
		try {
			File annoFile = Directory.openFile(
					Directory.ANNOTATION_DEFAULT_DATA_FOLDER, 
					MobiSensService.getDeviceID(this.getContext()) + "_" + Directory.RECORDER_TYPE_ANNOTATION + ".csv");
			MobiSensDataHolder db = new MobiSensDataHolder(this.getContext());
			db.open();
			db.dumpAllToFile(annoFile);
			db.close();
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			MobiSensLog.log(e);
		}
		
	}
	
	protected void broadcastUploadNotification(){
			
			// And upload the annotation file.
	    	boradcastFileList(
	        		new String[]{ Directory.ANNOTATION_DEFAULT_DATA_FOLDER + MobiSensService.getDeviceID(this.getContext()) + "_" + Directory.RECORDER_TYPE_ANNOTATION + ".csv" }, 
	        		String.valueOf(Directory.FILE_TYPE_ANNOTATION), 
	        		true,  // FARK!!!!
	        		this.getContext());
	    	
	    	// Upload the log file as well.
	    	boradcastFileList(
	        		new String[]{ MobiSensLog.LOG_FILE_PATH }, 
	        		String.valueOf(Directory.FILE_TYPE_LOG), 
	        		true,
	        		this.getContext());
		
		
	}
	
	
	public void unregister(){
		broadcastUploadNotification();
		super.unregister();
		
	}
	
	public String getUploadAction() {
		return null;
	}

	@Override
	protected void switchRecordFile() {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected String[] getActions() {
		// TODO Auto-generated method stub
		return new String[]{ Alarm.ACTION_ALARM };
	}

}
