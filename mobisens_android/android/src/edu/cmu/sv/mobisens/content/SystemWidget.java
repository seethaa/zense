package edu.cmu.sv.mobisens.content;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import android.content.Intent;

import edu.cmu.sv.mobisens.MobiSensService;

public class SystemWidget extends Widget {
	private final static String CLASS_PREFIX = SystemWidget.class.getName();
	public final static String ACTION_SYSTEM_DATA_EMITTED = CLASS_PREFIX + ".action_system_data_emitted";
	
	public final static String EXTRA_SYSTEM_DATA = CLASS_PREFIX + ".extra_system_data";
	public final static String EXTRA_DATA_TYPE = CLASS_PREFIX + ".extra_data_type";
	public static final String SYSTEMSENS_TYPE = "systemsens";
	
	public final static int START_DATA_COLUMN_INDEX = 4;
	
	public static String constructDataRecord(String data, String type, String deviceID)
    {
    	return constructDataRecord(System.currentTimeMillis(), data, type, deviceID);
    }
	
	public static String constructDataRecord(long timestamp, String data, String type, String deviceID)
    {
    	StringBuilder dataRecord = new StringBuilder();
    	Date date = new Date(timestamp);
    	
        // First thing, get the current time
    	SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String timeStr = format.format(date);

    	dataRecord.append(type);
        dataRecord.append("," + timeStr);
        dataRecord.append("," + timestamp);
        dataRecord.append("," + deviceID);
        dataRecord.append("," + data);
        dataRecord.append("\r\n");
        
        return dataRecord.toString();
    }
	
	protected void broadcastDataRecord(String record, String type){
		Intent dataIntent = new Intent(ACTION_SYSTEM_DATA_EMITTED);
		dataIntent.putExtra(EXTRA_SYSTEM_DATA, record);
		dataIntent.putExtra(EXTRA_DATA_TYPE, type);
		
		if(this.getContext() != null){
			this.getContext().sendBroadcast(dataIntent);
		}
	}

	@Override
	protected String[] getActions() {
		// TODO Auto-generated method stub
		return new String[0];
	}
}
