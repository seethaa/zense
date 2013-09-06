package edu.cmu.sv.mobisens.content;

import edu.cmu.sv.mobisens.MobiSensService;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.IntentFilter;
import android.telephony.TelephonyManager;

public class CallInfoWidget extends SystemWidget {
	private TelephonyManager telManager;
	public static final String CALL_TYPE = "call";
	
	public void register(ContextWrapper contextWrapper){
		IntentFilter callIntentFilter = new IntentFilter(TelephonyManager.ACTION_PHONE_STATE_CHANGED);
		this.setFilter(callIntentFilter);
		
		super.register(contextWrapper);
		
		telManager = (TelephonyManager)contextWrapper.getSystemService(Context.TELEPHONY_SERVICE);
	}
	
	@Override
    public void onReceive(Context context, Intent intent) 
    {
        int callState = telManager.getCallState(); 
        String state = " ";


        if (callState == TelephonyManager.CALL_STATE_OFFHOOK)
        {
            state = "started";
        }
        else if (callState == TelephonyManager.CALL_STATE_RINGING)
        {
            state = "ringing";
        }
        else if (callState == TelephonyManager.CALL_STATE_IDLE)
        {
            state = "ended";
        }


       String callStatus = "state," + state;
       String dataRecord = constructDataRecord( 
    		   callStatus, CALL_TYPE, this.getDeviceID());
       this.broadcastDataRecord(dataRecord, CALL_TYPE);
    }
}
