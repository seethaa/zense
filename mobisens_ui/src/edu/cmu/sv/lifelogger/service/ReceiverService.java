package edu.cmu.sv.lifelogger.service;
/**
 * Receiver Class for broadcast intents from mobisens app. This also takes care
 * of properly storing the activities in local database.
 * @author himanshu
 */

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import edu.cmu.sv.lifelogger.entities.Activity;
import edu.cmu.sv.lifelogger.helpers.App;
import edu.cmu.sv.lifelogger.helpers.Coordinates;
import edu.cmu.sv.mobisens.content.AnnotationWidget;
import edu.cmu.sv.mobisens.util.Annotation;

public class ReceiverService extends BroadcastReceiver {

	private static final String UNKNOWN = "Unknown";
	private static final String UNKNOWN_ANNOTATION_NAME = "Unknown Activity";
	App app ;
	private static int instanceCount = 0;

	/* Some Variables to store activity data, current and previous */

	Activity prevActivity = null;
	Activity currActivity = null;

	private static int lastActivityId = 0;
	private ArrayList<Coordinates> coordinates = new ArrayList<Coordinates>();


	@Override
	public void onReceive(Context context, Intent intent) {

		/* Open the db adapter -- @ToDO when change to service, add the context
		 * of service and uncomment the following lines */
		instanceCount++;
		this.app = ((App)context.getApplicationContext());

		if(instanceCount == 1) {
			lastActivityId = app.db.fetchRowCountActivityTable();//Row count of activity table = last activity id
		}

		Bundle extras = intent.getExtras();
		String annotationString = (String) extras.get(Annotation.EXTRA_ANNO_STRING);
		Annotation anno = Annotation.fromString(annotationString);
		Boolean mergeWithLastAnno = extras.getBoolean(AnnotationWidget.EXTRA_MERGE_WITH_LAST_ANNO);

		/* Fetch the Activity data from the anno into the currActivity */
		currActivity =  new Activity();
		createCurrActivityFromAnno(anno, currActivity);

		/* If it is first receive, create a new activity */
		if(prevActivity == null ) {
			prevActivity = currActivity;
		}

		if(mergeWithLastAnno){
			/* We have to just merge the activity with previous activity 
			 * 1) Copy all the data of currActivity to prevActivity
			 * 2) Except Start time 
			 * 3) Keep adding the time until, you get into else loop
			 * */
			mergeCurrToPrevActivity(currActivity, prevActivity);



		} else {
			/* It is a new Activity - no merging
			 * 1) Save the prevActivity to database
			 * 2) Flush prevActivity Data, store currActivity in prevActivity
			 * */
			/* Save start and end locations in the activity */
			int dataSize = coordinates.size();
			if(dataSize>0){
				prevActivity.setStartCoordinates(coordinates.get(0));
				prevActivity.setEndCoordinates(coordinates.get(dataSize -1));
				// Fetch Area location from google, for the start and end coordinates
				try{
					Geocoder geocoder;
					List<Address> addressesStart;
					List<Address> addressesEnd;
					geocoder =new Geocoder(app,Locale.getDefault());
					addressesStart = geocoder.getFromLocation(coordinates.get(0).latitude, coordinates.get(0).longitude,1);
					addressesEnd = geocoder.getFromLocation(coordinates.get(dataSize-1).latitude, coordinates.get(dataSize-1).longitude,1);
					//String address = addressesStart.get(0).getAddressLine(0);
					String startCity = addressesStart.get(0).getAddressLine(1);
					String endCity = addressesEnd.get(0).getAddressLine(1);
					String startLocality = addressesStart.get(0).getLocality();
					String endLocality = addressesEnd.get(0).getLocality();
					prevActivity.setmStart_location(startLocality);
					prevActivity.setmEnd_location(endLocality);
					//String country = addresses.get(0).getAddressLine(2);
				} catch(Exception e) {
					System.out.println("Cannot fetch the address");
				}
			} else {
				/* If no gps coordinate in the activity, then save null for the points */
				prevActivity.setStartCoordinates(new Coordinates(0, 0));
				prevActivity.setEndCoordinates(new Coordinates(0, 0));
			}
			
			/*Create the activity*/
			app.db.createActivityRow(prevActivity);
			lastActivityId++;
			
			/*Store all the coordinates in the database for the activity*/
			app.db.storeLocation(prevActivity.getmActivity_id(), coordinates);
			
			/* So, We have, stored all locations in db, stored start and end location in db*/
			prevActivity = currActivity;

			/* TESTING PURPOSE ONLY. REMOVE ASAP*/
			/* Adding following activities for testing dynamicSegment*/
			/*		Activity dummyActivity =  new Activity();
			DateFormat df = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss"); 
			Date startTime = null;

			Date endTime = null ;
			try {
				startTime = df.parse("03/19/2014 01:01:01");
				endTime = df.parse("03/19/2014 01:05:01"); 
			} catch (Exception e) {
				e.printStackTrace();
			}
			dummyActivity.setmStart_time(startTime);
			dummyActivity.setmEnd_time(endTime);
			dummyActivity.setmActivity_id(lastActivityId+1);
			lastActivityId++;*/

		}
		/* 
		 * We have the activity data here. We need to create a activity item
		 * which we can use directly to store data in the activity table
		 * */
		System.out.println("Here" + intent.getAction());

	}

	private void createCurrActivityFromAnno(Annotation anno,
			Activity currActivity2) {

		currActivity2.setmActivity_name(anno.getActivityName());
		currActivity2.setmStart_time(anno.getStart());
		currActivity2.setmEnd_time(anno.getEnd());
		/* Handle activityType value 
		 * If activity has unknown name, then put type as "unknown"
		 * else activity type = activity name*/

		if(UNKNOWN_ANNOTATION_NAME.equals(anno.getName().toString())){
			currActivity2.setmActivityType(UNKNOWN);
		} else {
			currActivity2.setmActivityType(anno.getName());
		}

		/* By default there is no description for the activity. Setting, 
		 * escaped name as description by default */
		currActivity2.setmDescription(anno.getEscapedName());

		/* Handle activityID case*/
		currActivity2.setmActivity_id(lastActivityId+1);
		/*@TODO Hack again, for timeline acctivity to work correctly, there 
		should be no spaces in activity name -- fix it soooooon. Uncomment the 
		next line and delete the line after it when fixed*/
		//currActivity2.setmActivity_name(anno.getEscapedName());
		currActivity2.setmActivity_name(UNKNOWN);
		/* @ToDo Handle Start and end location cases here */

		/* Algorithm: Keep storing the locations in a huge double array.
		 *  When activityiss complete, write all the locaitons to the database*/

		LinkedList<double[]> dataClone = anno.getLocations().getData();
		int dataLength = anno.getLocations().getDataSize();

		if(dataLength > 0){
			for(double[] location:dataClone){
				long timeStamp = 0;
				if(location.length < 3){
					// No timestamp, old version
					timeStamp = 0;
				}else{
					timeStamp = (long) (location[2] * 1000);
				}

				Coordinates cc = new Coordinates();

				cc.latitude = location[0];
				cc.longitude = location[1];
				cc.timestamp = timeStamp;
				this.coordinates.add(cc);
			}
		}

	}

	private void mergeCurrToPrevActivity(Activity currActivity2,
			Activity prevActivity2) {
		/*
		 * As the activity is in progress, keep updating the end location, 
		 * and end time of prevActivity - do not change start time and 
		 * start location
		 */
		prevActivity2.setmEnd_location(currActivity2.getmEnd_location());
		prevActivity2.setmEnd_time(currActivity2.getmEnd_time());

		//Rest of the things, are not changing, so no need to actually copy(do it for fun)
		prevActivity2.setmActivity_name(currActivity2.getmActivity_name());
		prevActivity2.setmDescription(currActivity2.getmDescription());
		prevActivity2.setmActivity_id(currActivity2.getmActivity_id());

	}
}







