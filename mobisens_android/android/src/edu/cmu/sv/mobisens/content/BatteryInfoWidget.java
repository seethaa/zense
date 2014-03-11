package edu.cmu.sv.mobisens.content;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;

import edu.cmu.sv.mobisens.MobiSensService;
import edu.cmu.sv.mobisens.R;

public class BatteryInfoWidget extends SystemWidget {
	
	public static final String BATTERY_TYPE = "battery";
	
	public void register(ContextWrapper contextWrapper){
		IntentFilter filter = new IntentFilter();
		filter.addAction(Intent.ACTION_BATTERY_CHANGED);
		
		this.setFilter(filter);
		
		super.register(contextWrapper);
		
	}
	
	@Override
    public void onReceive(Context context, Intent intent) 
    {
        String action = intent.getAction();
        
        if (Intent.ACTION_BATTERY_CHANGED.equals(action)) 
        {
            StringBuilder batteryStatus = new StringBuilder();

            // Get battery level
            int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0);
            int scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, 100);

            batteryStatus.append("level," + (level * 100 / scale));

            // Get Battery status
            int plugType = intent.getIntExtra(BatteryManager.EXTRA_PLUGGED, 0);
            int status = intent.getIntExtra(BatteryManager.EXTRA_STATUS,
                    BatteryManager.BATTERY_STATUS_UNKNOWN);
            String statusString = ""; 

            if (status == BatteryManager.BATTERY_STATUS_CHARGING) 
            {
                statusString =
                    this.getContext().getString(R.string.battery_info_status_charging);
                if (plugType > 0) 
                {
                    statusString = statusString + " " + this.getContext().getString(
                            (plugType ==
                             BatteryManager.BATTERY_PLUGGED_AC)
                            ?
                            R.string.battery_info_status_charging_ac
                            :
                            R.string.battery_info_status_charging_usb
                            );
                }
            } 
            else if (status == 
                    BatteryManager.BATTERY_STATUS_DISCHARGING) 
            {
                statusString =
                	this.getContext().getString(
                            R.string.battery_info_status_discharging);
            } 
            else if (status == 
                    BatteryManager.BATTERY_STATUS_NOT_CHARGING) 
            {
                statusString = this.getContext().getString(
                            R.string.battery_info_status_not_charging
                            );
            } 
            else if (status == BatteryManager.BATTERY_STATUS_FULL) 
            {
                statusString =
                	this.getContext().getString(R.string.battery_info_status_full);
            } else 
            {
                statusString =
                	this.getContext().getString(R.string.battery_info_status_unknown);
            }

            batteryStatus.append(",status," + statusString);
            
            // Get Battery health status
            int health = intent.getIntExtra("health",
                    BatteryManager.BATTERY_HEALTH_UNKNOWN);
            String healthString = " ";
            if (health == BatteryManager.BATTERY_HEALTH_GOOD) 
            {
                healthString =
                	this.getContext().getString(R.string.battery_info_health_good);
            } 
            else if (health == 
                    BatteryManager.BATTERY_HEALTH_OVERHEAT) 
            {
                healthString =
                	this.getContext().getString(R.string.battery_info_health_overheat);
            } 
            else if (health == BatteryManager.BATTERY_HEALTH_DEAD) 
            {
                healthString =
                	this.getContext().getString(R.string.battery_info_health_dead);
            } 
            else if (health ==
                    BatteryManager.BATTERY_HEALTH_OVER_VOLTAGE)
            {
                healthString =
                	this.getContext().getString(R.string.battery_info_health_over_voltage);
            } 
            else if (health ==
                    BatteryManager.BATTERY_HEALTH_UNSPECIFIED_FAILURE)
            {
                healthString =
                	this.getContext().getString(R.string.battery_info_health_unspecified_failure);
            } 
            else 
            {
                healthString =
                	this.getContext().getString(R.string.battery_info_health_unknown);
            }

            batteryStatus.append(",health," + healthString);

            // Get battery temperture
            batteryStatus.append(",temperature," + tenthsToFixedString(
                    intent.getIntExtra("temperature", 0)));

            // Get battery voltage
            batteryStatus.append(",voltage," + intent.getIntExtra( 
                           "voltage", 0));

            String dataRecord = constructDataRecord( 
            		batteryStatus.toString(), BATTERY_TYPE, this.getDeviceID());
            this.broadcastDataRecord(dataRecord, BATTERY_TYPE);
             
        }
    }
	
	/** Format a number of tenths-units as a decimal string without using a
     *  conversion to float.  E.g. 347 -> "34.7"
     *  
     *  @param		intVal
     *  @return		String representing the decimal
     */
    private final String tenthsToFixedString(int intVal) {
        int tens = intVal / 10;
        return new String("" + tens + "." + (intVal - 10*tens));
    }
}
