package edu.cmu.sv.mobisens;

import edu.cmu.sv.mobisens.io.Directory;
import edu.cmu.sv.mobisens.io.MobiSensLog;
import edu.cmu.sv.mobisens.settings.ServiceParameters;
import android.app.Notification;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Binder;
import android.os.IBinder;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.telephony.TelephonyManager;
import android.util.Log;

public class MobiSensService extends Service {

	private static final String CLASS_PREFIX = MobiSensService.class.getName();
	
	
	public final static String EXTRA_KEY_START_BY = CLASS_PREFIX + ".start_by";
	public final static String EXTRA_VALUE_START_BY_ACTIVITY = CLASS_PREFIX + ".start_by_activity";
	public final static String EXTRA_KEY_START_BY_BOOT = CLASS_PREFIX + ".start_by_boot";
	
	private final IBinder binder = new LocalBinder();
	private static ServiceParameters parameters = new ServiceParameters();
	private static final String TAG = "MobiSensService";
	
	
	public static final String SETTING_BOOT_WITH_SYSTEM = CLASS_PREFIX + ".boot_with_system";
	public static final String SETTING_LAST_UNREAD_MESSAGE_COUNT = CLASS_PREFIX + ".unread_message_count";
	
	private String IMEI = null;
	private static String globalDeviceID = null;
	
	
    /** Power manager object used to acquire a partial wakeLock */
    protected PowerManager powerManager = null;

    /** WakeLock object */
    protected PowerManager.WakeLock wakeLock = null;
    
    
    protected boolean getShouldBootWithSystem(){
    	String key = this.getClass().getName();
    	SharedPreferences settings = getSharedPreferences(key, 0);
        return settings.getBoolean(SETTING_BOOT_WITH_SYSTEM, false);
    }
    
    public void setShouldBootWithSystem(boolean value){
    	String key = this.getClass().getName();
    	SharedPreferences settings = getSharedPreferences(key, 0);
    	SharedPreferences.Editor editor = settings.edit();
        editor.putBoolean(SETTING_BOOT_WITH_SYSTEM, value);
        editor.commit();
    }
    
    
	/**
     * Class for clients to access.  Because we know this service always
     * runs in the same process as its clients, we don't need to deal with
     * IPC.
     */
    public class LocalBinder extends Binder {
    	MobiSensService getService() {
            return MobiSensService.this;
        }
    }
    
	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return binder;
	}
	
	public static ServiceParameters getParameters(){
		synchronized(parameters){
			return parameters;
		}
	}
	
	
	public static void setParameters(ServiceParameters parameters){
		synchronized(MobiSensService.parameters){
			MobiSensService.parameters = parameters;
		}
	}
	
	public static void clearDataFiles()
	{
		Directory.clearDataFiles();
	}
	
	
	public static WakeLock acquireTimeoutWakeLockForService(Context context, int wakeLockType, long timeout, String tag){
		String internalTag = "MobiSens";
		if(tag != null){
			internalTag = tag;
		}
		PowerManager powerManager = (PowerManager)context.getSystemService(Context.POWER_SERVICE);
		WakeLock wakeLock = powerManager.newWakeLock(wakeLockType, 
				internalTag);

		wakeLock.acquire(timeout);
		return wakeLock;

	}
	
	public static WakeLock acquireWakeLockForService(Context context, int wakeLockType, String tag){
		String internalTag = "MobiSens";
		if(tag != null){
			internalTag = tag;
		}
		PowerManager powerManager = (PowerManager)context.getSystemService(Context.POWER_SERVICE);
		WakeLock wakeLock = powerManager.newWakeLock(wakeLockType, 
				internalTag);

		wakeLock.acquire();
		return wakeLock;

	}
	
	public static WakeLock acquireWakeLockForService(Context context, int wakeLockType){
		
		return acquireWakeLockForService(context, wakeLockType, "MobiSense");

	}
	
	public void releaseWakeLockForService(WakeLock wakeLock){
		if(wakeLock != null){
			wakeLock.release();
			wakeLock = null;
		}
		
		
	}
	
	public String getDeviceID(){
		
		if(IMEI != null){
			return IMEI;
		}
		
		TelephonyManager telManager = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
		
		String deviceID = telManager.getDeviceId();
		
        
        if(deviceID == null){
        	WifiManager wifiMan = (WifiManager)getSystemService(Context.WIFI_SERVICE);
		    WifiInfo wifiInf = wifiMan.getConnectionInfo();
		    deviceID = wifiInf.getMacAddress();
        }
        
        IMEI = deviceID;
        return IMEI; 
	}
	
	public static String getDeviceID(Context context){
		
		/*
		 * The drawback of this approach is, if the service was opened when
		 * the device is in airplane mode. MobiSens will think this is a 
		 * different phone.
		 */
		if(globalDeviceID != null){
			return globalDeviceID;
		}
		
		TelephonyManager telManager = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
		
        String deviceID = telManager.getDeviceId();
        
        if(deviceID == null){
        	WifiManager wifiMan = (WifiManager) context.getSystemService(
                    Context.WIFI_SERVICE);
		    WifiInfo wifiInf = wifiMan.getConnectionInfo();
		    deviceID = wifiInf.getMacAddress();
        }
        
        globalDeviceID = deviceID;
        return globalDeviceID;
	}
	
	
	
	@Override
	public void onCreate(){
		//this.wakeLock = acquireWakeLockForService(this, PowerManager.PARTIAL_WAKE_LOCK);
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {

    	
    	setShouldBootWithSystem(true);
    	
    	Notification notify = NotificationService.makeDataServiceRunningNotification();
    	
    	// The following line is important. Keeping the service not to killed
    	// by OS.
    	if(notify != null){
    		startForeground(NotificationService.DATA_SERVICE_RUNNING, notify);
    	}else{
    		String logMessage = this.getClass().getName() + " created as BACKGROUND service, notify is null.";
    		MobiSensLog.log(logMessage);
    		Log.w(TAG, logMessage);
    		
    	}

	    return START_STICKY;
	}
	
	@Override
	public void onDestroy(){
		//releaseWakeLockForService(this.wakeLock);
		setShouldBootWithSystem(false);
		
		NotificationService.removeDataServiceRunningNotification();
		
	}
}
