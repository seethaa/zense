package edu.cmu.sv.mobisens.content;

import java.io.File;
import java.util.Date;

import edu.cmu.sv.lifelogger.util.DataCollector;
import edu.cmu.sv.lifelogger.util.LocationClusters;
import edu.cmu.sv.mobisens.MobiSensService;
import edu.cmu.sv.mobisens.SensorService;
import edu.cmu.sv.mobisens.SystemSensService;
import edu.cmu.sv.mobisens.io.Directory;
import edu.cmu.sv.mobisens.io.MobiSensLog;
import edu.cmu.sv.mobisens.settings.ServiceParameters;
import edu.cmu.sv.mobisens.util.GeoIndex;
import edu.cmu.sv.mobisens.util.LocationConverter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.util.Log;

public class LocationClusteringWidget extends Widget {

	private static final String TAG = "LocationBroadcastReceiver";
	private static final String CLASS_PREFIX = LocationClusteringWidget.class.getName();
	
	
	public static final String ACTION_EMIT_LOCATION_CLUSTER = CLASS_PREFIX + ".edmit_location_cluster";
	public final static String ACTION_BEST_LOCATION_DATA = CLASS_PREFIX + ".location_data";
    public final static String ACTION_RAW_LOCATION_DATA = CLASS_PREFIX + ".raw_location_data";
    
    
    /** Extra fields */
    public final static String EXTRA_LAT_LNG = CLASS_PREFIX + ".extra_lat_lng";
    public final static String EXTRA_HAS_SPEED = CLASS_PREFIX + ".extra_has_speed";
    public final static String EXTRA_SPEED = CLASS_PREFIX + ".extra_speed";
    public final static String EXTRA_ACCURACY = CLASS_PREFIX + ".extra_accuracy";
    public final static String EXTRA_FIX_TIME = CLASS_PREFIX + ".extra_fix_time";
    
    
	public static final String EXTRA_LOCATION_CLUSTER = "extra_location_cluster";
	
	private LocationClusters locationClusters = null;
	private DataCollector<double[]> locationDataCollector = 
		new DataCollector<double[]>(-1);
		
	public void register(ContextWrapper contextWrapper){
		super.register(contextWrapper);
		
		String indexFileName = Directory.MOBISENS_ROOT + Directory.LOCATION_CENTROIDS_FILENAME;
		File indexFile = new File(indexFileName);
		
		locationClusters = LocationClusters.loadFromFile(indexFile);
	}
	
	public void unregister(){
		super.unregister();
		
		if(this.locationClusters != null){
			try {
				File indexFile = Directory.openFile(Directory.MOBISENS_ROOT, Directory.LOCATION_CENTROIDS_FILENAME);
				locationClusters.save(indexFile);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				MobiSensLog.log(e);
			}
			
		}
		
		GeoIndex.save();
		GeoIndex.close();
		
		locationClusters = null;
	}
	
	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		if(intent.getAction().equals(LocationClusteringWidget.ACTION_BEST_LOCATION_DATA)){
			this.processLocatonData(intent);
		}
		
		if(intent.getAction().equalsIgnoreCase(SystemSensService.ACTION_USER_MOVING) ||
				intent.getAction().equalsIgnoreCase(SystemSensService.ACTION_USER_STATIONARY)
				){
			this.processMovementData(intent);
		}
		
		if(intent.getAction().equals(ProfileWidget.ACTION_SERVICE_PARAMETER_UPDATED)){
			this.refreshLocationSettings();
		}
		
		if(intent.getAction().equals(LocationClusteringWidget.ACTION_RAW_LOCATION_DATA)){
			this.processRawLocationData(intent);
		}
		
	}
	
	private void processLocatonData(Intent intent){
		Location location = new Location(TAG);
		double[] latlng = intent.getExtras().getDoubleArray(LocationClusteringWidget.EXTRA_LAT_LNG);
		
		if(latlng != null){
			if(latlng.length >= 2){
				location.setLatitude(latlng[0]);
				location.setLongitude(latlng[1]);
			}
		}
		
		float speed = intent.getFloatExtra(LocationClusteringWidget.EXTRA_SPEED, 0.0f);
		location.setSpeed(speed);
		
		float accuracy = intent.getFloatExtra(LocationClusteringWidget.EXTRA_ACCURACY, 0.0f);
		location.setAccuracy(accuracy);
		int clusterIndex = this.locationClusters.add(LocationConverter.toLifeloggerLocation(location));
		
		//Log.i(TAG, "Cluster Index: " + clusterIndex);
		MobiSensLog.log("Cluster Index: " + clusterIndex);
		
		Intent locationClusterIntent = new Intent(ACTION_EMIT_LOCATION_CLUSTER);
		locationClusterIntent.putExtra(EXTRA_LOCATION_CLUSTER, clusterIndex);
		
		locationClusterIntent.putExtra(LocationClusteringWidget.EXTRA_LAT_LNG, latlng);
		locationClusterIntent.putExtra(LocationClusteringWidget.EXTRA_SPEED, speed);
		locationClusterIntent.putExtra(LocationClusteringWidget.EXTRA_ACCURACY, accuracy);
		
		this.getContext().sendBroadcast(locationClusterIntent);
	}
	
	private void processMovementData(Intent intent){
		if(intent.getAction().equalsIgnoreCase(SystemSensService.ACTION_USER_MOVING) ||
				intent.getAction().equalsIgnoreCase(SystemSensService.ACTION_USER_STATIONARY)){

			String source = intent.getExtras().getString("source");
			//MobiSensLog.log(currentTime.toLocaleString() + ": " + source + "," + intent.getAction());
			Log.i(TAG, source + "," + intent.getAction());
			
			double[] latlng = intent.getExtras().getDoubleArray(LocationClusteringWidget.EXTRA_LAT_LNG);
			if(latlng == null)
				return;
			MobiSensLog.log("lat: " + latlng[0] + ", lng: " + latlng[1]);
			Log.i(TAG, "lat: " + latlng[0] + ", lng: " + latlng[1]);
		}
	}
	
	private void processRawLocationData(Intent intent){
		double[] latlng = intent.getExtras().getDoubleArray(LocationClusteringWidget.EXTRA_LAT_LNG);
		if(latlng == null)
			return;
		
		long fixTimeStamp = intent.getLongExtra(LocationClusteringWidget.EXTRA_FIX_TIME, System.currentTimeMillis());

		locationDataCollector.collect(latlng);
		
		GeoIndex.init();
		GeoIndex.writeData(locationDataCollector, fixTimeStamp, fixTimeStamp + 1);
		locationDataCollector = new DataCollector<double[]>(-1);
	}
	
	
	private void refreshLocationSettings(){
		this.locationClusters.setEpsilon(MobiSensService.getParameters().getServiceParameter(
						ServiceParameters.GPS_SAMELOCATION_THRESHOLD));
	}

	@Override
	protected String[] getActions() {
		// TODO Auto-generated method stub
		
		return new String[]{
				LocationClusteringWidget.ACTION_BEST_LOCATION_DATA,
				LocationClusteringWidget.ACTION_RAW_LOCATION_DATA,
				SystemSensService.ACTION_USER_MOVING,
				SystemSensService.ACTION_USER_STATIONARY,
				ProfileWidget.ACTION_SERVICE_PARAMETER_UPDATED
		};
	}

}
