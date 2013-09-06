package edu.cmu.sv.mobisens.content;

import edu.cmu.sv.mobisens.MobiSensService;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.IntentFilter;

public class ScreenInfoWidget extends SystemWidget {
	public static final String SCREEN_TYPE = "screen";
	
	public void register(ContextWrapper contextWrapper){
		IntentFilter filter = new IntentFilter();
		filter.addAction(Intent.ACTION_SCREEN_OFF);
		filter.addAction(Intent.ACTION_SCREEN_ON);
		
		this.setFilter(filter);
		
		super.register(contextWrapper);
		
	}
	
	@Override
    public void onReceive(Context context, Intent intent) 
    {
        String action = intent.getAction();
        String status = "";
        if (action.equals(Intent.ACTION_SCREEN_OFF)) 
        { 
            status = "OFF";

        }
        else if (action.equals(Intent.ACTION_SCREEN_ON)) 
        { 
            status = "ON";
        }

        String screenStatus = "status," + status;
        String dataRecord = constructDataRecord( 
        		screenStatus, SCREEN_TYPE, getDeviceID());
        
        this.broadcastDataRecord(dataRecord, SCREEN_TYPE);
    }
}
