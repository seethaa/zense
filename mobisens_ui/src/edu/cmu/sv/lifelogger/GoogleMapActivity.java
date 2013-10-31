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
 
import android.app.Dialog;
import android.content.Context;
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
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
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
    TextView tvDistanceDuration;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_googlemap);
 
        final Context context = this;
        tvDistanceDuration = (TextView) findViewById(R.id.tv_distance_time);
            
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
        
        
        
   /*     map.setOnInfoWindowClickListener(new OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
            	 Toast.makeText(getApplicationContext(),
            		     "isGooglePlayServicesAvailable SUCCESS",
            		     Toast.LENGTH_LONG).show();
            	 
            	 
            	 final Dialog dialog = new Dialog(GoogleMapActivity.this);
 				dialog.setContentView(R.layout.map_popup_dialogue);
 				dialog.setTitle("Title...");
 				// set the custom dialog components - text, image and button
 				TextView text = (TextView) dialog.findViewById(R.id.text);
 				text.setText("Android custom dialog example!");
 				Button dialogButton = (Button) dialog.findViewById(R.id.dialogButtonOK);
 				// if button is clicked, close the custom dialog
 				dialogButton.setOnClickListener(new OnClickListener() {
 					@Override
 					public void onClick(View v) {
 						dialog.dismiss();
 					}
 				});
 	 
 				dialog.show();
            	 
            	 
            	 
            	 

            }
        });*/
     
        
        final LocationMetaData locData = new LocationMetaData();
   
        
        
        
        map.setOnMarkerClickListener(new OnMarkerClickListener(){
        	
			@Override
			public boolean onMarkerClick(Marker marker) {
				
				
				final Marker m1 = marker;
				// TODO Auto-generated method stub
				// custom dialog
				final Dialog dialog = new Dialog(GoogleMapActivity.this);
				dialog.setContentView(R.layout.map_popup_dialogue);
				dialog.setTitle("Title...");
				// set the custom dialog components - text, image and button
				TextView text = (TextView) dialog.findViewById(R.id.text);
				text.setText("Android custom dialog example!");
				Button dialogButton = (Button) dialog.findViewById(R.id.dialogButtonOK);
				// if button is clicked, close the custom dialog
				dialogButton.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						m1.setTitle("Test Markere title");
						locData.setLoc(m1.getPosition());
						locData.setAnnotation("Test Data");
						dialog.dismiss();
					}
				});
	 
				dialog.show();
				return false;
			}
        	
        });

        
        
        
        
        // Setting onclick event listener for the map
        map.setOnMapClickListener(new OnMapClickListener() {
 
            @Override
            public void onMapClick(LatLng point) {
 
                // Already two locations
                if(markerPoints.size()>1){
                    markerPoints.clear();
                    map.clear();
                }
 
                // Adding new item to the ArrayList
                markerPoints.add(point);
 
                // Creating MarkerOptions
                MarkerOptions options = new MarkerOptions();
 
                // Setting the position of the marker
                options.position(point);
 
                /**
                * For the start location, the color of marker is GREEN and
                * for the end location, the color of marker is RED.
                */
                if(markerPoints.size()==1){
                    options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
                }else if(markerPoints.size()==2){
                    options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
                }
 
                // Add new marker to the Google Map Android API V2
                map.addMarker(options);
 
                // Checks, whether start and end locations are captured
                if(markerPoints.size() >= 2){
                    LatLng origin = markerPoints.get(0);
                    LatLng dest = markerPoints.get(1);
 
                    // Getting URL to the Google Directions API
                    String url = getDirectionsUrl(origin, dest);
 
                    DownloadTask downloadTask = new DownloadTask();
 
                    // Start downloading json data from Google Directions API
                    downloadTask.execute(url);
                }
            }
        });
    }
 
    private OnInfoWindowClickListener OnInfoWindowClickListener() {
		// TODO Auto-generated method stub
		return null;
	}

	private String getDirectionsUrl(LatLng origin,LatLng dest){
 
        // Origin of route
        String str_origin = "origin="+origin.latitude+","+origin.longitude;
 
        // Destination of route
        String str_dest = "destination="+dest.latitude+","+dest.longitude;
 
        // Sensor enabled
        String sensor = "sensor=false";
 
        // Building the parameters to the web service
        String parameters = str_origin+"&"+str_dest+"&"+sensor;
 
        // Output format
        String output = "json";
 
        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/"+output+"?"+parameters;
 
        return url;
    }
 
    /** A method to download json data from url */
    private String downloadUrl(String strUrl) throws IOException{
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try{
            URL url = new URL(strUrl);
 
            // Creating an http connection to communicate with url
            urlConnection = (HttpURLConnection) url.openConnection();
 
            // Connecting to url
            urlConnection.connect();
 
            // Reading data from url
            iStream = urlConnection.getInputStream();
 
            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));
 
            StringBuffer sb  = new StringBuffer();
 
            String line = "";
            while( ( line = br.readLine())  != null){
                sb.append(line);
            }
 
            data = sb.toString();
 
            br.close();
 
        }catch(Exception e){
            Log.d("Exception while downloading url", e.toString());
        }finally{
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
    }
 
    // Fetches data from url passed
    private class DownloadTask extends AsyncTask<String, Void, String>{
 
        // Downloading data in non-ui thread
        @Override
        protected String doInBackground(String... url) {
 
            // For storing data from web service
            String data = "";
 
            try{
                // Fetching the data from web service
                data = downloadUrl(url[0]);
            }catch(Exception e){
                Log.d("Background Task",e.toString());
            }
            return data;
        }
 
        // Executes in UI thread, after the execution of
        // doInBackground()
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
 
            ParserTask parserTask = new ParserTask();
 
            // Invokes the thread for parsing the JSON data
            parserTask.execute(result);
        }
    }
 
    /** A class to parse the Google Places in JSON format */
    private class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String,String>>> >{
 
        // Parsing the data in non-ui thread
        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {
 
            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;
 
            try{
                jObject = new JSONObject(jsonData[0]);
                DirectionsJSONParser parser = new DirectionsJSONParser();
 
                // Starts parsing data
                routes = parser.parse(jObject);
            }catch(Exception e){
                e.printStackTrace();
            }
            return routes;
        }
 
        // Executes in UI thread, after the parsing process
        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> result) {
            ArrayList<LatLng> points = null;
            PolylineOptions lineOptions = null;
            MarkerOptions markerOptions = new MarkerOptions();
            String distance = "";
            String duration = "";
 
            if(result.size()<1){
                Toast.makeText(getBaseContext(), "No Points", Toast.LENGTH_SHORT).show();
                return;
            }
 
            // Traversing through all the routes
            for(int i=0;i<result.size();i++){
                points = new ArrayList<LatLng>();
                lineOptions = new PolylineOptions();
 
                // Fetching i-th route
                List<HashMap<String, String>> path = result.get(i);
 
                // Fetching all the points in i-th route
                for(int j=0;j<path.size();j++){
                    HashMap<String,String> point = path.get(j);
 
                    if(j==0){    // Get distance from the list
                        distance = (String)point.get("distance");
                        continue;
                    }else if(j==1){ // Get duration from the list
                        duration = (String)point.get("duration");
                        continue;
                    }
 
                    double lat = Double.parseDouble(point.get("lat"));
                    double lng = Double.parseDouble(point.get("lng"));
                    LatLng position = new LatLng(lat, lng);
 
                    points.add(position);
                }
 
                // Adding all the points in the route to LineOptions
                lineOptions.addAll(points);
                lineOptions.width(20);
                lineOptions.color(Color.YELLOW);
            }
 
            tvDistanceDuration.setText("Distance:"+distance + ", Duration:"+duration);
 
            // Drawing polyline in the Google Map for the i-th route
            map.addPolyline(lineOptions);
        }
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.googlemap , menu);
        return true;
    }
}

