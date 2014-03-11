package edu.cmu.sv.mobisens.content;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Criteria;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Handler;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.os.SystemClock;
import android.util.Log;
import edu.cmu.sv.mobisens.MobiSensService;
import edu.cmu.sv.mobisens.SensorService;
import edu.cmu.sv.mobisens.io.MobiSensLog;
import edu.cmu.sv.mobisens.power.Alarm;
import edu.cmu.sv.mobisens.settings.ServiceParameters;
import edu.cmu.sv.mobisens.ui.MessageDialogActivity;

public class LocationScanWidget extends Widget {
	
	private static final String TAG = "LocationScanWidget";
	private static final String CLASS_PREFIX = LocationScanWidget.class.getName();
	
	private long lastInterval = 60 * 1000;
	private long scanInterval = 0; //ServiceParameters.GPS_DEFAULT_SCANINTERVAL;
	private long scanTimeMillions = 0;
	private String currentLocationProvider = LocationManager.GPS_PROVIDER;
	private boolean keepFastScan = true;
	
	private long initialInterval = 1; //30 * 1000;
	private long pv = 0;
	private Object syncObject = new Object();
	
	private Handler handler = new Handler();
	private class EndRequestRunnable implements Runnable{
		private WakeLock lock = null;
		private RequestParams result = null;
		private LocationSelector selector = null;
		
		public EndRequestRunnable(RequestParams result, LocationSelector selector){
			this.lock = MobiSensService.acquireWakeLockForService(getContext(), 
					PowerManager.PARTIAL_WAKE_LOCK,
					LocationScanWidget.class.getName()
				);
			
			this.result = result;
			this.selector = selector;
		}
		public void run() {
			// TODO Auto-generated method stub
			synchronized(syncObject){
				if(this.result.getLocationManager() != null && this.selector != null){
					this.result.getLocationManager().removeUpdates(selector);
					this.selector.end(this.result);
				}
				
				this.lock.release();
				endRunnable = null;
			}
		}
		
	};
	
	private EndRequestRunnable endRunnable = null;
	private long skippedTime = 0;

	private LocationSelector listener = null;
	
	public void setLocationProvider(String newProvider){
		this.currentLocationProvider = newProvider;
	}
	
	public String getLocationProvider(){
		return this.currentLocationProvider;
	}
	
	public void setListener(LocationSelector value){
		if(this.listener != null){
			LocationManager locationManager = (LocationManager)this.getContext().getSystemService(Context.LOCATION_SERVICE);
			locationManager.removeUpdates(listener);
		}
		listener = value;
	}
	
	
	public void requestImmediately(){
		// If the system is not in fast scan mode,
		// reset it to fast scan and request a scan immediately.
		// else, keep it in fast scan mode.
		if(this.hasRegistered()){
			if(this.pv > 0){
				this.pv = 0;
			}else{
				// We are fast enough, skip!
			}
			
		}
		
		keepFastScan = true;
	}
	
	public void register(ContextWrapper contextWrapper){
		super.register(contextWrapper);
		
		LocationManager locationManager = (LocationManager)this.getContext().getSystemService(Context.LOCATION_SERVICE);
        
        if(locationManager != null){
	        if(locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) == false){
	        	this.setLocationProvider(LocationManager.NETWORK_PROVIDER);
	        	
	        	if(locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER) == false){
	        		
	        		this.setLocationProvider(LocationManager.GPS_PROVIDER);
        			MessageDialogActivity.showDialog(this.getContext(), MessageDialogActivity.DIALOGTYPE_GPSENABLE_REQUEST);

	        	}
	        }
        }
		
		
	}
	
	public void onReceive(Context context, Intent intent) {
		super.onReceive(context, intent);
		
		// TODO Auto-generated method stub
    	String action = intent.getAction();
    	if(action.equals(Alarm.ACTION_ALARM)){
    		ServiceParameters profileParams = MobiSensService.getParameters();
    		if(!profileParams.getServicesStatus(ServiceParameters.GPS)){
    			return;  // The GPS is set to off.
    		}
    		
    		if(this.skippedTime >= this.pv){
    			//onWake();
    			
    			final RequestParams params = getRequestParams();
    			this.listener.prepare(params);
    			requestLocationScan(0, params);

    			if(this.keepFastScan){
    				// Already scan using fast settings
    				// Try to release the scanning cycle
    				
    				//this.keepFastScan = false;
    				//this.pv = 1;
    			}else{
    				// In the same location, no motion change.
    				// Double the scanning cycle.
    				
    				/*
    				this.pv *= 2;
    				
    				long maxCycle = MobiSensService.getParameters().getServiceParameter(ServiceParameters.MAX_GPS_CYCLE);
    				if(this.pv > maxCycle){
    					this.pv = maxCycle;
    				}*/
    			}
    			
    			if(params != null && this.listener != null){
    				synchronized(syncObject){

	    				this.endRunnable = new EndRequestRunnable(params, this.listener);
	    				this.handler.postDelayed(this.endRunnable, 
	    						MobiSensService.getParameters().getServiceParameter(ServiceParameters.GPS_OPEN_WINDOW)
	    						);
    				}
    			}
    			
    			this.skippedTime = 0;
    		}else{
    			this.skippedTime++;
    		}
    	}
	}
	
	public void closeGPSRequest(){
		
		synchronized(syncObject){
			if(this.endRunnable != null){
				this.handler.removeCallbacks(endRunnable);
				this.endRunnable.run();
				this.endRunnable = null;
			}
		}
	}
	
	private Criteria getHighAccuracyCriteria(){
		Criteria criteria = new Criteria();
    	criteria.setAccuracy(Criteria.ACCURACY_FINE);
        criteria.setAltitudeRequired(false);
        criteria.setBearingRequired(false);
        criteria.setCostAllowed(false);
        criteria.setPowerRequirement(Criteria.POWER_HIGH);
        return criteria;
	}
	
	private Criteria getLowAccuracyCriteria(){
		Criteria criteria = new Criteria();
    	criteria.setAccuracy(Criteria.ACCURACY_COARSE);
        criteria.setAltitudeRequired(false);
        criteria.setBearingRequired(false);
        criteria.setCostAllowed(false);
        criteria.setPowerRequirement(Criteria.POWER_HIGH);
        return criteria;
	}
	
	private RequestParams getRequestParams(){
		LocationManager locationManager = (LocationManager)this.getContext().getSystemService(Context.LOCATION_SERVICE );
		if(locationManager != null) {
        	Criteria fineCriteria = getHighAccuracyCriteria();
        	Criteria coarseCriteria = getLowAccuracyCriteria();

                
        	String bestAvailableProvider = locationManager.getBestProvider(fineCriteria, true);
        	String coarseAvailableProvider = locationManager.getBestProvider(coarseCriteria, true);
        	return new RequestParams(locationManager, bestAvailableProvider, coarseAvailableProvider);
		}
		
		return null;
	}
	
	private RequestParams requestLocationScan(long interval, RequestParams params){
    	
        try {
        	LocationManager locationManager = params.getLocationManager();
	        if(locationManager != null && this.listener != null) {
	        	locationManager.removeUpdates(this.listener);
	        	locationManager.removeGpsStatusListener(this.listener);

	        	String bestAvailableProvider = params.getFineProvider();
	        	String coarseAvailableProvider = params.getCoarseProvider();
	        	locationManager.addGpsStatusListener(this.listener);
	        	
	        	if (bestAvailableProvider != null){
	        		
	        		locationManager.requestLocationUpdates(bestAvailableProvider, 
	        			  interval, 
	        			  0, 
	        			  this.listener, 
	        			  this.getContext().getMainLooper());
	        		
	        	}
	        	Log.i(CLASS_PREFIX, "Fine location " + bestAvailableProvider + " interval : " + String.valueOf(pv + 1) + " minutes.");
		        MobiSensLog.log("Fine location " + bestAvailableProvider + " interval : " + String.valueOf(pv + 1) + " minutes.");
		        
		        
		        if (coarseAvailableProvider != null){
	        		locationManager.requestLocationUpdates(coarseAvailableProvider, 
	        			  interval, 
	        			  0, 
	        			  this.listener, 
	        			  this.getContext().getMainLooper());
	        	}
	        	Log.i(TAG, "Coarse location " + coarseAvailableProvider + " interval : " + String.valueOf(pv + 1) + " minutes.");
		        MobiSensLog.log("Coarse location " + coarseAvailableProvider + " interval : " + String.valueOf(pv + 1) + " minutes.");
		        
	        }
	        
	        
	        
        }catch(Exception ex) {
        	Log.e(TAG, "Exception", ex);
        }
        
        return params;
        
    }
	
	private void broadcastLastKnownLocation(){
		LocationManager locationManager = (LocationManager)this.getContext().getSystemService(Context.LOCATION_SERVICE);
        if(locationManager != null) {
			Location lastKnownLocation = locationManager.getLastKnownLocation(currentLocationProvider);

    		if(this.listener != null){
    			this.listener.onLocationChanged(lastKnownLocation);
    		}
        }
	}
	
	public interface LocationSelector extends LocationListener, GpsStatus.Listener {
		void prepare(RequestParams params);
		void end(RequestParams params);
	}
	
	public class RequestParams{
		private LocationManager manager;
		private String fineProvider;
		private String coarseProvider;
		
		@SuppressWarnings("unused")
		private RequestParams(){
			
		}
		
		public RequestParams(LocationManager manager, String fineProvider, String coarseProvider){
			this.setManager(manager);
			this.setFineProvider(fineProvider);
			this.setCoarseProvider(coarseProvider);
		}
		
		public void setManager(LocationManager manager) {
			this.manager = manager;
		}
		public LocationManager getLocationManager() {
			return manager;
		}
		public void setFineProvider(String fineProvider) {
			this.fineProvider = fineProvider;
		}
		public String getFineProvider() {
			return fineProvider;
		}
		public void setCoarseProvider(String coarseProvider) {
			this.coarseProvider = coarseProvider;
		}
		public String getCoarseProvider() {
			return coarseProvider;
		}
	}

	@Override
	protected String[] getActions() {
		// TODO Auto-generated method stub
		return new String[]{ Alarm.ACTION_ALARM };
	}
	
	
}
