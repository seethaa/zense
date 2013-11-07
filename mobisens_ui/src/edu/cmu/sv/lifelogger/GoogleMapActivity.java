package edu.cmu.sv.lifelogger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
 
import org.achartengine.renderer.SimpleSeriesRenderer;
import org.json.JSONObject;
 
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;

import org.json.JSONObject;
import android.graphics.Color;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
 
//import com.frank.gmap.demo.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;

import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import edu.cmu.sv.lifelogger.database.ActivityLocationManager;
import edu.cmu.sv.lifelogger.entities.ActivityItem;
import edu.cmu.sv.mobisens_ui.R;
 
class LocationMetaData {

	LatLng loc;
	String annotation;
	
	public LatLng getLoc() {
		return loc;
	}
	public void setLoc(LatLng loc) {
		this.loc = loc;
	}
	public String getAnnotation() {
		return annotation;
	}
	public void setAnnotation(String annotation) {
		this.annotation = annotation;
	}
}


public class GoogleMapActivity extends FragmentActivity {
 
    GoogleMap map;
    ArrayList<LatLng> markerPoints;
    //TextView tvDistanceDuration;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_googlemap);
         final Context context = this;
        //tvDistanceDuration = (TextView) findViewById(R.id.tv_distance_time);
            
        // Initializing
        markerPoints = new ArrayList<LatLng>();
        // Getting reference to SupportMapFragment of the activity_main
        SupportMapFragment fm = (SupportMapFragment)getSupportFragmentManager().findFragmentById(R.id.map);
 
        // Getting Map for the SupportMapFragment
        map = fm.getMap();
 
        // Enable MyLocation Button in the Map
        map.setMyLocationEnabled(true);
        
        ArrayList<LatLng> locations  = ActivityLocationManager.getAllLocations();
        
        PolylineOptions pol = new PolylineOptions();
        pol.color(Color.BLUE);
        pol.width((float) 5.1);
        pol.addAll(locations);
        
        map.addPolyline(pol)  ;
        MarkerOptions markerOptions = new MarkerOptions();
        for (LatLng a : locations) {
        	markerOptions.position(a).title("Test MEssage");
            map.addMarker(markerOptions);
            
            
		}
                
        final ArrayList <LocationMetaData> locData = new ArrayList <LocationMetaData>();
   
        
        
        
        map.setOnMarkerClickListener(new OnMarkerClickListener(){
        	
			@Override
			public boolean onMarkerClick(Marker marker) {
				final Marker m1 = marker;
				// TODO Auto-generated method stub
				// custom dialog
				final Dialog dialog = new Dialog(GoogleMapActivity.this);
				dialog.setContentView(R.layout.map_popup_dialogue);

		        ListView list;
		        //Get all basic types of places 
		        String list_array[]= ActivityLocationManager.getLocationType();
		        
		        
		        list=(ListView)dialog.findViewById(R.id.ListView1);
		        
		        ArrayAdapter adapter = new ArrayAdapter<String>(GoogleMapActivity.this,android.R.layout.simple_list_item_1 ,list_array);
		          //ArrayAdapter adapter = new ArrayAdapter<String>(GoogleMapActivity.this,dialog.R.layout.simple_list_item_1 ,list_array);
		        
		        list.setAdapter(adapter);
		        
		        list.setOnItemClickListener(new OnItemClickListener() {
		        	LocationMetaData locDataTemp = new LocationMetaData();
					@Override
					public void onItemClick(AdapterView<?> arg0, View arg1,
							int arg2, long arg3) {
						// TODO Auto-generated method stub
						/*The current location has been annotated with the selected item
						 * Return the data back to the main activity 
						 */
						String selectedText = arg0.getItemAtPosition(arg2).toString();
						if(!selectedText.equals("Other")) {
							locDataTemp = new LocationMetaData();
							locDataTemp.setAnnotation(arg0.getItemAtPosition(arg2).toString());
							locDataTemp.setLoc(m1.getPosition());
							locData.add(locDataTemp);
							m1.setTitle(locDataTemp.getAnnotation().toString());
							m1.hideInfoWindow();
							m1.showInfoWindow();
						} else {
							// Selected text is equal to Other - popup to ask user to input the location
						/*	AlertDialog.Builder alert = new AlertDialog.Builder(GoogleMapActivity.this);
						    EditText input = new EditText(GoogleMapActivity.this);
						    final int inputID = View.generateViewId();
						    
						    input.setId(inputID);
						    //input.setId("myInput");        
						    alert.setView(input);
						    String out;
						    alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
						        //@Override
						        public void onClick(DialogInterface dialoge, int which) {
						        	
						            EditText input = (EditText) dialoge.findViewById(inputID);
						            Editable value = input.getText();
						            out = value.toString();               

						        }
						    });*/
							
							final Dialog dialog = new Dialog(GoogleMapActivity.this);
							dialog.setContentView(R.layout.usertag_dialog);
							dialog.setTitle("Please tag the location");


							final EditText editText = (EditText)dialog.findViewById(R.id.activity);
							
							Button dialogButton = (Button) dialog.findViewById(R.id.dialogButtonOK);
							// if button is clicked, close the custom dialog
							dialogButton.setOnClickListener(new OnClickListener() {
								@Override
								public void onClick(View v) {
									locDataTemp = new LocationMetaData();
									String annotationString = editText.getText().toString();
									locDataTemp.setAnnotation(annotationString);
									locDataTemp.setLoc(m1.getPosition());
									locData.add(locDataTemp);
									m1.setTitle(locDataTemp.getAnnotation().toString());
									m1.hideInfoWindow();
									m1.showInfoWindow();
									dialog.dismiss();
									
								}
							});

							dialog.show();
							
						}

						dialog.dismiss();
					}
				});
		        
		        
		        
		        dialog.setTitle("Tag the location type");
	 
				dialog.show();
				return false;
			}
        	
        });
    }    

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		// getMenuInflater().inflate(R.menu.login, menu);
		getMenuInflater().inflate(R.menu.action_bar, menu);

		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == R.id.timeline) {
			Intent intent = new Intent(this, TimelineTestActivity.class);
			startActivity(intent);
		} else if (item.getItemId() == R.id.profile) {
			Intent intent = new Intent(this, GoogleMapActivity.class);
			startActivity(intent);
		} else if (item.getItemId() == R.id.settings) {
			Intent intent = new Intent(this, SettingsActivity.class);
			startActivity(intent);
		}

		// TODO: Add Settings activity piece
		// TODO: CHoose correct drawables in action_bar in res/menu
		return true;
	}

}

