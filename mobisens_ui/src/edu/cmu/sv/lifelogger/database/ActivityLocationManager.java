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
		LatLng newPoint = new LatLng( 37.418709,-122.057419);// CMU School
		locations.add(newPoint);
		newPoint = new LatLng( 37.418709,-122.057423);
		locations.add(newPoint);
		newPoint = new LatLng( 37.418709,-122.057680);
		locations.add(newPoint);
		
		// Locations for final demo
		newPoint = new LatLng(37.417709,-122.056419);// Practicum Meeting
		locations.add(newPoint);
		newPoint = new LatLng(37.417243,-122.057569);// Walking...
		locations.add(newPoint);
		newPoint = new LatLng(37.417584,-122.059028);// Walking...
		locations.add(newPoint);
		newPoint = new LatLng(37.419049,-122.05905);// Walking...
		locations.add(newPoint);
		newPoint = new LatLng(37.419101,-122.061903);// Walking...
		locations.add(newPoint);
		newPoint = new LatLng(37.41928,-122.062408);// Mobile HW Meeting
		locations.add(newPoint);
		newPoint = new LatLng(37.419373,-122.06318);// Machine Learning Workshop
		locations.add(newPoint);
		newPoint = new LatLng(37.417447,-122.063963);// GA Meeting
		locations.add(newPoint);
		
		newPoint = new LatLng(37.417575,-122.061893);// Driving...
		locations.add(newPoint);
		newPoint = new LatLng(37.416161,-122.061882);// Driving...
		locations.add(newPoint);
		newPoint = new LatLng(37.413792,-122.061914);// Driving...
		locations.add(newPoint);
		newPoint = new LatLng(37.412531,-122.062215);// Driving...
		locations.add(newPoint);
		newPoint = new LatLng(37.411883,-122.063137);// Driving...
		locations.add(newPoint);
		newPoint = new LatLng(37.411346,-122.062526);// Driving...
		locations.add(newPoint);
		newPoint = new LatLng(37.410119,-122.062751);// Driving...
		locations.add(newPoint);
		newPoint = new LatLng(37.408943,-122.065734);// Driving...
		locations.add(newPoint);
		newPoint = new LatLng(37.404614,-122.067933);// Driving...
		locations.add(newPoint);
		newPoint = new LatLng(37.400369,-122.073061);// Driving...
		locations.add(newPoint);
		newPoint = new LatLng(37.397676,-122.076473);// Driving...
		locations.add(newPoint);
		newPoint = new LatLng(37.393823,-122.078984);// Driving...
		locations.add(newPoint);
		newPoint = new LatLng(37.393601,-122.078394);// Parking...
		locations.add(newPoint);
		newPoint = new LatLng(37.392843,-122.078855);// Walking...
		locations.add(newPoint);
		newPoint = new LatLng(37.392383,-122.078984);// Lunch at Castro St.
		locations.add(newPoint);
		
		//

		return locations;
		
	}

	
	public static ArrayList<Place> getTaggedLocations(){
		
		ArrayList<Place> locations = new ArrayList<Place>();
		// Name = address, Geometry for latitude and longitude
		
		// THis is the start position of the path
		LatLng newPoint = new LatLng( 37.418709,-122.057419);
		Place place = new Place();
		place.setPoint(newPoint);
		place.setName("CMU School");
		locations.add(place);
		
		newPoint = new LatLng(37.417709,-122.056419);
		place = new Place("Practicum Meeting");
		place.setPoint(newPoint);
		locations.add(place);
		
		// Locations for final demo
		newPoint = new LatLng(37.417243,-122.057569);
		place = new Place("Walking...");
		place.setPoint(newPoint);
		locations.add(place);
		newPoint = new LatLng(37.417584,-122.059028);
		place = new Place("Walking...");
		place.setPoint(newPoint);
		locations.add(place);
		newPoint = new LatLng(37.419049,-122.05905);
		place = new Place("Walking...");
		place.setPoint(newPoint);
		locations.add(place);
		newPoint = new LatLng(37.419101,-122.061903);
		place = new Place("Walking...");
		place.setPoint(newPoint);
		locations.add(place);
		newPoint = new LatLng(37.41928,-122.062408);
		place = new Place("Mobile HW Meeting");
		place.setPoint(newPoint);
		locations.add(place);
		newPoint = new LatLng(37.419373,-122.06318);
		place = new Place("Machine Learning Workshop");
		place.setPoint(newPoint);
		locations.add(place);
		newPoint = new LatLng(37.417447,-122.063963);
		place = new Place("GA Meeting");
		place.setPoint(newPoint);
		locations.add(place);
		
		newPoint = new LatLng(37.417575,-122.061893);
		place = new Place("Driving...");
		place.setPoint(newPoint);
		locations.add(place);
		newPoint = new LatLng(37.416161,-122.061882);
		place = new Place("Driving...");
		place.setPoint(newPoint);
		locations.add(place);
		newPoint = new LatLng(37.413792,-122.061914);
		place = new Place("Driving...");
		place.setPoint(newPoint);
		locations.add(place);
		newPoint = new LatLng(37.412531,-122.062215);
		place = new Place("Driving...");
		place.setPoint(newPoint);
		locations.add(place);
		newPoint = new LatLng(37.411883,-122.063137);
		place = new Place("Driving...");
		place.setPoint(newPoint);
		locations.add(place);
		newPoint = new LatLng(37.411346,-122.062526);
		place = new Place("Driving...");
		place.setPoint(newPoint);
		locations.add(place);
		newPoint = new LatLng(37.410119,-122.062751);
		place = new Place("Driving...");
		place.setPoint(newPoint);
		locations.add(place);
		newPoint = new LatLng(37.408943,-122.065734);
		place = new Place("Driving...");
		place.setPoint(newPoint);
		locations.add(place);
		newPoint = new LatLng(37.404614,-122.067933);
		place = new Place("Driving...");
		place.setPoint(newPoint);
		locations.add(place);
		newPoint = new LatLng(37.400369,-122.073061);
		place = new Place("Driving...");
		place.setPoint(newPoint);
		locations.add(place);
		newPoint = new LatLng(37.397676,-122.076473);
		place = new Place("Driving...");
		place.setPoint(newPoint);
		locations.add(place);
		newPoint = new LatLng(37.393823,-122.078984);
		place = new Place("Driving...");
		place.setPoint(newPoint);
		locations.add(place);
		newPoint = new LatLng(37.393601,-122.078394);
		place = new Place("Parking...");
		place.setPoint(newPoint);
		locations.add(place);
		newPoint = new LatLng(37.392843,-122.078855);
		place = new Place("Walking...");
		place.setPoint(newPoint);
		locations.add(place);
		newPoint = new LatLng(37.392383,-122.078984);
		place = new Place("Lunch at Castro St....");
		place.setPoint(newPoint);
		locations.add(place);
		
		
		// This is the end point
		newPoint = new LatLng( 37.417297,-122.060680);
		place = new Place("End Location");
		place.setPoint(newPoint);
		locations.add(place);
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
