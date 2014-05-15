
package edu.cmu.sv.mobisens;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import android.app.AlarmManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.SystemClock;
import android.util.Log;
import android.location.*;
import android.net.wifi.WifiManager;
import android.net.wifi.ScanResult;
import edu.cmu.sv.mobisens.content.ActivityWidget;
import edu.cmu.sv.mobisens.content.BatteryInfoWidget;
import edu.cmu.sv.mobisens.content.CallInfoWidget;
import edu.cmu.sv.mobisens.content.LocationClusteringWidget;
import edu.cmu.sv.mobisens.content.LocationScanWidget;
import edu.cmu.sv.mobisens.content.NetworkInfoWidget;
import edu.cmu.sv.mobisens.content.ProcessInfoWidget;
import edu.cmu.sv.mobisens.content.ProfileWidget;
import edu.cmu.sv.mobisens.content.ScreenInfoWidget;
import edu.cmu.sv.mobisens.content.SystemDataDumpingWidget;
import edu.cmu.sv.mobisens.content.SystemWidget;
import edu.cmu.sv.mobisens.content.UploadControllerWidget;
import edu.cmu.sv.mobisens.content.Widget;
import edu.cmu.sv.mobisens.content.WifiScannerWidget;
import edu.cmu.sv.mobisens.content.WifiWidget;
import edu.cmu.sv.mobisens.content.LocationScanWidget.LocationSelector;
import edu.cmu.sv.mobisens.content.LocationScanWidget.RequestParams;
import edu.cmu.sv.mobisens.io.MobiSensLog;
import edu.cmu.sv.mobisens.net.Network;
import edu.cmu.sv.mobisens.power.Alarm;
import edu.cmu.sv.mobisens.settings.LocalSettings;
import edu.cmu.sv.mobisens.settings.ServiceParameters;
import edu.cmu.sv.mobisens.threading.CyclingWakeUpThread;
import edu.cmu.sv.mobisens.ui.MessageDialogActivity;
import edu.cmu.sv.mobisens.util.LocationFilter;
import edu.cmu.sv.mobisens.util.Sharing;
import edu.cmu.sv.mobisens.util.SlowStartAlgorithm;

/**
 * SystemSensServices runs locally in the same process as the
 * SystemSense.
 * It collects system information and stores in a local file.
 * 
 */
public class SystemSensService 
	extends MobiSensService 
	implements LocationSelector
{
	private static final String CLASS_PREFIX = SystemSensService.class.getName();
	
	/** Action Strings */
    public final static String ACTION_USER_MOVING = CLASS_PREFIX + ".moving";
    public final static String ACTION_USER_STATIONARY = CLASS_PREFIX + ".stationary";
    
    
    /** Name of the service used for logging */
    private static final String TAG = "MobiSensService";
    
    static final String GPSSTAT_TYPE = "gps";
    private static final String LOCATION_TYPE = "location";
    private static final int MAX_FINE_LOCATIONS = 3;

    private long serviceStatus = 0;
    private int sattleliteCount = 0;

    /** Battery History object */
    //private History mHistoryStat;
    
    //private SystemSensDumpingThread uploadThread = null;

    private Location lastBestLocation = null;
    

    private LocationClusteringWidget locationClusteringWidget = new LocationClusteringWidget();
    private CallInfoWidget callInfoWidget = new CallInfoWidget();
    private ScreenInfoWidget screenInfoWidget = new ScreenInfoWidget();
    private BatteryInfoWidget batteryInfoWidget = new BatteryInfoWidget();
    private NetworkInfoWidget networkInfoWidget = new NetworkInfoWidget();
    private WifiScannerWidget wifiScannerWidget = new WifiScannerWidget();
    private WifiWidget wifiWidget = new WifiWidget();
    private ProcessInfoWidget processInfoWidget = new ProcessInfoWidget();
    
    private SystemDataDumpingWidget ioWidget = new SystemDataDumpingWidget();

    //private SystemSensLogger logger;

	private static Context context = null;
	private static SystemSensService instance = null;
	
	//private long lastScanTime = 0;
	
	private LinkedList<Location> locationPool = new LinkedList<Location>();
	private LocationScanWidget.RequestParams params = null;
	private int fineLocationCount = 0;

	private Widget abnormalActivityWidget = new Widget(){

		public void onReceive(Context context, Intent intent) {
			super.onReceive(context, intent);
			
			// TODO Auto-generated method stub
	    	String action = intent.getAction();
	    	if(action.equals(ActivityWidget.ACTION_ABNORMAL_DETECTED)){
	    		locationScanWidget.requestImmediately();
	    	}
		}

		@Override
		protected String[] getActions() {
			// TODO Auto-generated method stub
			return new String[]{ ActivityWidget.ACTION_ABNORMAL_DETECTED };
		}
	};

	
	private LocationScanWidget locationScanWidget = new LocationScanWidget();
	

	public static SystemSensService getInstance(){
		return instance;
	}
	
	public static Context getServiceContext()
	{
		return context;
	}
	
	public static void setServiceContext(Context c)
	{
		context = c;
	}
    
    public int onStartCommand(Intent intent, int flags, int startId) {
    	super.onStartCommand(intent, flags, startId);
    	
    	String sysStatus = "state,started";
    	
    	if(intent == null){
    		sysStatus = "state,restarted";
    	}
    	
    	// Log a message indicating starting SystemSens
        String dataRecord = SystemWidget.constructDataRecord( 
        		sysStatus, SystemWidget.SYSTEMSENS_TYPE, getDeviceID());
        this.broadcastDataRecord(dataRecord);
        
        dataRecord = SystemWidget.constructDataRecord( 
        		"location_provider," + this.locationScanWidget.getLocationProvider(), SystemWidget.SYSTEMSENS_TYPE, getDeviceID());
        this.broadcastDataRecord(dataRecord);

    	return START_STICKY;
    }
    
    public void onLowMemory(){
    	super.onLowMemory();
    	String sysStatus = "state,low_memory";
    	String dataRecord = SystemWidget.constructDataRecord( 
        		sysStatus, SystemWidget.SYSTEMSENS_TYPE, getDeviceID());
    	this.broadcastDataRecord(dataRecord);
    }
    
    @Override
    public void onCreate() {
    	//android.os.Debug.waitForDebugger();
        super.onCreate();

        Log.i(TAG, "onCreate");
        
        this.ioWidget.register(this);
        instance = this;
        
        this.processInfoWidget.register(this);
        
        
        
        System.out.println("Here SystemSens service created");
        ServiceParameters params = MobiSensService.getParameters();
        
        // Register for screen updates
        this.screenInfoWidget.register(this);
        
        if(params.getServicesStatus(ServiceParameters.BATTERY_STATUS)){
	        // Register for battery updates
	        this.batteryInfoWidget.register(this);
        }

        
        if(params.getServicesStatus(ServiceParameters.WIFI_SCAN)){
	        // Wi-Fi has been enabled, disabled, enabling, disabling, or unknown.
	        this.wifiScannerWidget.register(this);
	        this.networkInfoWidget.register(this);
	        this.wifiWidget.register(this);
        }


        if(params.getServicesStatus(ServiceParameters.CALL_MONITOR)){
	
	        this.callInfoWidget.register(this);
        }

        serviceStatus = params.getAllServicesStatus();

        //this.locationClusteringWidget.register(this);
        

        this.locationScanWidget.register(this);
        this.locationScanWidget.setListener(this);
        this.locationScanWidget.requestImmediately();

        
        this.abnormalActivityWidget.register(this);

    }
    
    @Override
	public void onDestroy() {
	
	    // Clear the message handler's pending messages
	    
//    	this.gpsRequestTimer.exit();
    	this.locationScanWidget.unregister();
    	this.abnormalActivityWidget.unregister();
	    this.screenInfoWidget.unregister();
	            
	    if((serviceStatus & ServiceParameters.BATTERY_STATUS) != 0){
	        this.batteryInfoWidget.unregister();
	    }
	    
	    if((serviceStatus & ServiceParameters.WIFI_SCAN) != 0){

	        this.wifiScannerWidget.unregister();
	        this.networkInfoWidget.unregister();
	        this.wifiWidget.unregister();
	    }
	    
	    if((serviceStatus & ServiceParameters.CALL_MONITOR) != 0){
	    	this.callInfoWidget.unregister();
	    }

	    
	    
	    this.locationClusteringWidget.unregister();
	    this.processInfoWidget.unregister();
	    
	    
	    this.ioWidget.unregister();
//	    this.profileHandler.unregister();
	
	    super.onDestroy();
	    
	    Log.i(TAG, "Killed");
	    
	    instance = null;
	}
    
    

    //private Date lastLocationChangeTime = new Date();
	public synchronized void onLocationChanged(Location location) {
		// TODO Auto-generated method stub
		if( location != null ) {
			this.locationPool.add(location);
			if(this.params != null){
				if(location.getProvider().equals(params.getFineProvider())){
					this.fineLocationCount++;
				}
			}
			
			if(this.fineLocationCount >= MAX_FINE_LOCATIONS){
				this.locationScanWidget.closeGPSRequest();
				this.fineLocationCount = 0;
			}
		}
		
	}
	
	
	private void broadcastRawlocationData(Location location){
		Intent locationDataIntent = new Intent(LocationClusteringWidget.ACTION_RAW_LOCATION_DATA);
		locationDataIntent.putExtra(LocationClusteringWidget.EXTRA_LAT_LNG, new double[]{location.getLatitude(), location.getLongitude()});
		locationDataIntent.putExtra(LocationClusteringWidget.EXTRA_HAS_SPEED, location.hasSpeed());
		locationDataIntent.putExtra(LocationClusteringWidget.EXTRA_SPEED, location.getSpeed());
		locationDataIntent.putExtra(LocationClusteringWidget.EXTRA_ACCURACY, location.getAccuracy());
		locationDataIntent.putExtra(LocationClusteringWidget.EXTRA_FIX_TIME, location.getTime());
		
		sendBroadcast(locationDataIntent);
	}
	
	
	//private long lastInterval = 0;
	
	

	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub
		
	}

	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub
		
	}

	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub
		//Log.i(TAG, provider + ": " + String.valueOf(status));
	}


	protected void broadcastDataRecord(String record){
		Intent dataIntent = new Intent(SystemWidget.ACTION_SYSTEM_DATA_EMITTED);
		dataIntent.putExtra(SystemWidget.EXTRA_SYSTEM_DATA, record);
		sendBroadcast(dataIntent);
		
	}

	public void prepare(LocationScanWidget.RequestParams params) {
		// TODO Auto-generated method stub
		locationPool.clear();
		this.params = params;
	}

	public void end(LocationScanWidget.RequestParams resultParams) {
		// TODO Auto-generated method stub
		LinkedList<Location> finePool = new LinkedList<Location>();
		LinkedList<Location> coarsePool = new LinkedList<Location>();
		for(Location item:locationPool){
			if(item.getProvider().equals(resultParams.getFineProvider())){
				finePool.add(item);
			}else{
				coarsePool.add(item);
			}
		}
		
		String logString = "Fine Data Count: " + finePool.size() + ", Window: " + MobiSensService.getParameters().getServiceParameter(ServiceParameters.GPS_OPEN_WINDOW) / 1000;
		Log.i(TAG, logString);
		MobiSensLog.log(logString);
		
		logString = "Coarse Data Count: " + coarsePool.size() + ", Window: " + MobiSensService.getParameters().getServiceParameter(ServiceParameters.GPS_OPEN_WINDOW) / 1000;
		Log.i(TAG, logString);
		MobiSensLog.log(logString);
		
		// Select a pool to display on lifelogger, you can't display both of the fine and coarse
		// pool because they may have very different accuracy.
		LinkedList<Location> selectedPool = finePool.size() > 0 ? finePool : coarsePool;
		
		// Only broadcast the fine pool
		for(Location item:selectedPool){
			try
			{
				StringBuilder data = new StringBuilder();
				data.append("longitude," + item.getLongitude());
				data.append(",latitude," + item.getLatitude());
				data.append(",speed," + item.getSpeed());
				data.append(",has_speed," + item.hasSpeed());
				data.append(",altitude," + item.getAltitude());
				data.append(",has_altitude," + item.hasAltitude());
				data.append(",bearing," + item.getBearing());
				data.append(",has_bearing," + item.hasBearing());
				data.append(",accuracy," + item.getAccuracy());
				data.append(",sattlelites," + this.sattleliteCount);
				
				String dataRecord = SystemWidget.constructDataRecord(item.getTime(),
						data.toString(), LOCATION_TYPE, getDeviceID());
				this.broadcastDataRecord(dataRecord);
				
			} catch(Exception ex) {
				Log.e(CLASS_PREFIX, "Exception", ex);
			}
			broadcastRawlocationData(item);
		}
		
		// Sometimes the WIFI and network location will be better than GPS..
		Location location = null;
		for(Location item:locationPool){
			if(location != null){
				if(item.getAccuracy() > location.getAccuracy())
					location = item;
			}else{
				location = item;
			}
		}
		
		if(location == null)
			return; //No location data.
		
		
		Intent movementIntent = new Intent();
		movementIntent.putExtra(LocationClusteringWidget.EXTRA_LAT_LNG, 
				new double[]{location.getLatitude(), 
				location.getLongitude()});
		
		if(lastBestLocation == null){
			// The first location data, set the state to moving
			
			movementIntent.setAction(ACTION_USER_MOVING);
			
		}else{
			
			float distance = location.distanceTo(lastBestLocation);
			if(distance >= MobiSensService.getParameters().getServiceParameter(
						ServiceParameters.GPS_SAMELOCATION_THRESHOLD)){
				//The location is changing
				movementIntent.setAction(ACTION_USER_MOVING);
				this.locationScanWidget.requestImmediately();
				
			}else{
				movementIntent.setAction(ACTION_USER_STATIONARY);
			}
			
			logString = "Distance: " + distance;
			Log.i(TAG, logString);
			MobiSensLog.log(logString);
		}

		sendBroadcast(movementIntent);
		
		// Broadcast the location data LocationBroadcastReceiver
		Intent locationDataIntent = new Intent(LocationClusteringWidget.ACTION_BEST_LOCATION_DATA);
		locationDataIntent.putExtra(LocationClusteringWidget.EXTRA_LAT_LNG, new double[]{location.getLatitude(), location.getLongitude()});
		locationDataIntent.putExtra(LocationClusteringWidget.EXTRA_HAS_SPEED, location.hasSpeed());
		locationDataIntent.putExtra(LocationClusteringWidget.EXTRA_SPEED, location.getSpeed());
		locationDataIntent.putExtra(LocationClusteringWidget.EXTRA_ACCURACY, location.getAccuracy());
		
		sendBroadcast(locationDataIntent);
		
		
		
		if(LocalSettings.isSharing(getApplication())){
			Sharing.shareLocation(getDeviceID(), location.getLatitude(), location.getLongitude());
		}

		lastBestLocation = location;
	}

	public void onGpsStatusChanged(int event) {
		// TODO Auto-generated method stub
		LocationManager locationManager = (LocationManager)this.getBaseContext().getSystemService(Context.LOCATION_SERVICE);
        if(locationManager != null) {
			GpsStatus status = locationManager.getGpsStatus(null);
			if(status == null)
				return;
			this.sattleliteCount  = 0;
			Iterable<GpsSatellite> sats = status.getSatellites();
			for (GpsSatellite sat:sats){ 
				if(sat.usedInFix()){
					this.sattleliteCount++;
				}
			}
			
			Log.i(TAG, "Stattlelite count: " + this.sattleliteCount + " in event: " + event);
			MobiSensLog.log("Stattlelite count: " + this.sattleliteCount + " in event: " + event);
        }
		
	}

}

