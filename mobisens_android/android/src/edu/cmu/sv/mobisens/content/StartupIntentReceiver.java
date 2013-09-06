package edu.cmu.sv.mobisens.content;

import edu.cmu.sv.mobisens.MobiSensService;
import edu.cmu.sv.mobisens.NotificationService;
import edu.cmu.sv.mobisens.SensorService;
import edu.cmu.sv.mobisens.SystemSensService;
import edu.cmu.sv.mobisens.settings.LocalSettings;
import edu.cmu.sv.mobisens.ui.Eula;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

public class StartupIntentReceiver extends BroadcastReceiver {

	protected boolean getShouldBootWithSystem(Context context, String key){
    	SharedPreferences settings = context.getSharedPreferences(key, 0);
        return settings.getBoolean(MobiSensService.SETTING_BOOT_WITH_SYSTEM, false);
    }
	
	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		
		if(Eula.hasAccepted(context)){
			// Reset the uploading flag in settings, or the user cannot
			// upload data after the phone reboot abnormally.
			if(LocalSettings.isPushing(context)){
				LocalSettings.setPushing(context, false);
			}
			
			Intent notifyServiceIntent = new Intent(context, NotificationService.class);
			context.startService(notifyServiceIntent);
		
			if(getShouldBootWithSystem(context, SystemSensService.class.getName())){
				SystemSensService.setServiceContext(context);
				Intent systemSensServiceIntent = new Intent(context, SystemSensService.class);
				systemSensServiceIntent.putExtra(MobiSensService.EXTRA_KEY_START_BY, MobiSensService.EXTRA_KEY_START_BY_BOOT);
				context.startService(systemSensServiceIntent);
				
				NotificationService.makeDataServiceRunningNotification();
			}
			
			if(getShouldBootWithSystem(context, SensorService.class.getName())){
				SensorService.setServiceContext(context);
				Intent sensorServiceIntent = new Intent(context, SensorService.class);
				sensorServiceIntent.putExtra(MobiSensService.EXTRA_KEY_START_BY, MobiSensService.EXTRA_KEY_START_BY_BOOT);
	
				context.startService(sensorServiceIntent);
				
				NotificationService.makeDataServiceRunningNotification();
			}
		}
	}

}
