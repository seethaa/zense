package edu.cmu.sv.mobisens.ui;

import java.util.LinkedList;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.InfoWindowAdapter;
import com.google.android.gms.maps.GoogleMap.OnCameraChangeListener;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;


import edu.cmu.sv.mobisens.R;
import edu.cmu.sv.mobisens.io.MobiSensLog;
import edu.cmu.sv.mobisens.settings.LocalSettings;

public class BaseMapActivity extends FragmentActivity {

	protected final static String EXTRA_TEXT = "text";
	protected final static int MSG_SHOW_TOAST = 2;
	
	private GoogleMap mapView = null;
	private Spinner spMapType;
	
	private LinkedList<double[]> lastZoomedLocations;
	
	
	public final static int OVERLAY_CONTROL_ALPHA = 190;
	
	protected Handler handler = new Handler(){
		public void handleMessage(Message msg){
			if(msg.what == MSG_SHOW_TOAST){
				String text = msg.getData().getString(EXTRA_TEXT);
				showMessage(text);
			}
			
			onMessageArrival(msg);
		}
	};
	
	protected void onMessageArrival(Message msg){
		
	}
	
	protected int getContentViewResourceId(){
		return R.layout.map_anno_activity;
	}
	
	
	public void setMapTypeSelectorControl(boolean visibility){
		if(visibility){
			this.spMapType.setVisibility(View.VISIBLE);
		}else{
			this.spMapType.setVisibility(View.GONE);
		}
	}
	
	protected void OnMapTypeChanged(int type){
		
	}
	
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(this.getContentViewResourceId());
		FragmentManager fregmentManager = this.getSupportFragmentManager();
		
		if(fregmentManager != null){
			this.mapView = this.getMapView();
		}
		
		if(this.mapView != null){
			this.mapView.setMapType(GoogleMap.MAP_TYPE_NORMAL);
		}
		
		
		this.spMapType = (Spinner) this.findViewById(R.id.spMapType);
		this.spMapType.getBackground().setAlpha(OVERLAY_CONTROL_ALPHA);
		
		String[] mapTypes = getResources().getStringArray(R.array.map_types);
		
		
		ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(this, 
				R.layout.map_selector_spinner_item,
				android.R.id.text1,
				mapTypes);
		spinnerAdapter.setDropDownViewResource(R.layout.map_selector_spinner_dropdown_item);
		this.spMapType.setAdapter(spinnerAdapter);
		
		this.spMapType.setOnItemSelectedListener(new OnItemSelectedListener(){

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int position, long id) {
				int mapType = GoogleMap.MAP_TYPE_NORMAL;
				// TODO Auto-generated method stub
				switch(position){
				case 0:
					mapType = GoogleMap.MAP_TYPE_NORMAL;
					break;
				case 1:
					mapType = GoogleMap.MAP_TYPE_TERRAIN;
					break;
				case 2:
					mapType = GoogleMap.MAP_TYPE_HYBRID;
					break;
				case 3:
					mapType = GoogleMap.MAP_TYPE_SATELLITE;
					break;
				}
				
				GoogleMap map = getMapView();
				
				if(map != null){
					if(map.getMapType() != mapType){
						getMapView().setMapType(mapType);
						if(lastZoomedLocations != null)
							zoomInBounds(lastZoomedLocations);
						LocalSettings.setSelectedMapType(BaseMapActivity.this, position);
						OnMapTypeChanged(mapType);
					}
				}
				
				
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub
				
			}
			
		});
		
		int selectedIndex = LocalSettings.getSelectedMapType(this);
		
		// Have to check here, because the async error is uncatchable.
		if(this.spMapType.getAdapter().getCount() > selectedIndex){  
			this.spMapType.setSelection(selectedIndex);
		}
		
		
		
		
	}
	
	
	protected void showMessage(String message){
		Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
	}
	
	public double[] zoomInBounds(final LinkedList<double[]> locations) {
		
		if(locations.size() == 0){
			CameraUpdate zoom = CameraUpdateFactory.zoomTo(0);
			this.mapView.animateCamera(zoom);
			return new double[0];
		}
		
	    double minLat = 0;
	    double minLng = 0;
	    double maxLat = 0;
	    double maxLng = 0;

	    int length = locations.size();
	    
	    for (int i = 0; i< length; i++) {
	    	double[] latlng = locations.get(i);
	    	
	    	if(i == 0){
	    		minLat = latlng[0];
	    		maxLat = latlng[0];
	    		minLng = latlng[1];
	    		maxLng = latlng[1];
	    	}else{
	    	
		        minLat = Math.min(latlng[0], minLat);
		        maxLat = Math.max(latlng[0], maxLat);
	    	
		        minLng = Math.min(latlng[1], minLng);
		        maxLng = Math.max(latlng[1], maxLng);
	    	
	    	}
	    }
	    
	    
	    

	    /*final MapController controller = getMapView().getController();
	    controller.zoomToSpan(
	                       Math.abs(minLat - maxLat), Math.abs(minLong - maxLong));*/
	    
	    final LatLngBounds zoomBounds = new LatLngBounds(new LatLng(minLat, minLng), new LatLng(maxLat, maxLng));
	    
	    CameraUpdate center = CameraUpdateFactory.newLatLng(new LatLng((maxLat + minLat) / 2,
	        						(maxLng + minLng) / 2));
	    this.mapView.animateCamera(center);
	    
	    // What a hack!
	    // Solution from: http://stackoverflow.com/questions/13692579/movecamera-with-cameraupdatefactory-newlatlngbounds-crashes
	    getMapView().setOnCameraChangeListener(new OnCameraChangeListener() {

		    @Override
		    public void onCameraChange(CameraPosition arg0) {
		        // Move camera.
		    	if(zoomBounds != null){
		    		try{
				    	getMapView().moveCamera(CameraUpdateFactory.newLatLngBounds(zoomBounds, 50));
				        // Remove listener to prevent position reset on camera move.
				    	getMapView().setOnCameraChangeListener(null);
				    	lastZoomedLocations = locations;
		    		}catch(Exception ex){
		    			ex.printStackTrace();
		    			MobiSensLog.log(ex);
		    		}
		    	}
		    }
		});
	    
	    
	    
	    
	    return new double[]{(maxLat + minLat) / 2, (maxLng + minLng) / 2};
	}
	
	
	
	
	public void resetMap(){
		if(this.mapView != null){
			this.mapView.clear();
			CameraUpdate zoom = CameraUpdateFactory.zoomTo(0);
			this.mapView.animateCamera(zoom);
		}
		
	}
	
	
	public GoogleMap getMapView() {
		FragmentManager fregmentManager = this.getSupportFragmentManager();
		
		if(fregmentManager != null){
			SupportMapFragment mapFragment = ((SupportMapFragment)fregmentManager.findFragmentById(R.id.mapview));
			try{
				GoogleMap map = mapFragment.getMap();
				return map;
			}catch(Exception ex){
				ex.printStackTrace();
				MobiSensLog.log(ex);
			}
		}
		
		return null;
	}

}
