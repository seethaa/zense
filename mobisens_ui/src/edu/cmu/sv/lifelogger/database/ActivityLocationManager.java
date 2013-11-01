package edu.cmu.sv.lifelogger.database;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import com.google.android.gms.maps.model.LatLng;

import edu.cmu.sv.lifelogger.entities.TimelineItem;
import edu.cmu.sv.lifelogger.entities.TimelineSegment;

public class ActivityLocationManager {

	//TODO: Change this to get real data

	public static ArrayList<LatLng> getAllLocations(){
		
		ArrayList<LatLng> locations = new ArrayList<LatLng>();
		LatLng newPoint = new LatLng( 37.418709,-122.057419);
		locations.add(newPoint);
		newPoint = new LatLng( 38.418709,-121.057419);
		locations.add(newPoint);
		newPoint = new LatLng( 37.379297,-122.060680);
		locations.add(newPoint);

		return locations;
		
	}
	
	public static String[] getLocationType(){
		
		ArrayList<String> types = new ArrayList<String>();
		
		
		types.add("Home");
		
		types.add("Work");
		types.add("Shop");
		types.add("Restaurant");
		types.add("Hotel");
		types.add("Motel");
		types.add("Grocery Store");
		types.add("Other");
		
		String[] typesStringArray =(String[]) types.toArray(new String[types.size()]); 

		return typesStringArray;
		
	}
		

}
