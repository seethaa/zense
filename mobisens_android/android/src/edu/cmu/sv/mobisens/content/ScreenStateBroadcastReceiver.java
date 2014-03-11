package edu.cmu.sv.mobisens.content;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;




// This class used to deal with a bug of Android
// Please see: http://code.google.com/p/android/issues/detail?id=3708
public class ScreenStateBroadcastReceiver extends BroadcastReceiver
{
	public ScreenStateBroadcastReceiver()
	{
	}

	
	@Override
	public void onReceive(Context context, Intent intent)
	{
		String action = intent.getAction();
		
		if (Intent.ACTION_SCREEN_OFF.equals(action)) {
			onScreenOff(context, intent);
		}
		
		if (Intent.ACTION_SCREEN_ON.equals(action)) {
			onScreenOn(context, intent);
		}
		
		return;
	} //end of method
	
	
	protected void onScreenOn(Context context, Intent intent){
		
	}
	
	protected void onScreenOff(Context context, Intent intent){
		
	}

	

} //end of class
