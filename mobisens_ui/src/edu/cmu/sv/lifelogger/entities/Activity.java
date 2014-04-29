package edu.cmu.sv.lifelogger.entities;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.graphics.drawable.Drawable;
import edu.cmu.sv.lifelogger.helpers.Coordinates;
import edu.cmu.sv.lifelogger.util.DataCollector;
/**
 * Creating this Activity Class, as a perfect copy of TimelineItem. 
 * Purpose of this class is to match perfectly with how activity is declared,
 * in mobisens originally, with all the data. When this class is perfect and 
 * general enough, TimelineItem and ActivityItem classes should become redundant
 * and in future versions, they should be deprecated
 *  
 * @author himanshu
 *
 */
public class Activity {

	private String mActivityType;
	private String mDescription;
	private int mactivity_id;
	private Drawable mActivity_icon;
	private String mActivity_name;
	private Date mStart_time;
	private Date mEnd_time;

	private String mStart_location;
	private String mEnd_location;
	// Wrapper start/end co-ordinates. 
	private Coordinates startCoordinates;
	private Coordinates endCoordinates;

	public Activity(int activityID, String activityName, String description,
			String activityType, String startLocation, String endLocation, String startTime,
			String endTime, String startLocationLat, String startLocationLng, String endLocationLat,
			String endLocationLng) {
		this.mactivity_id = activityID;
		this.mActivity_name = activityName;
		this.mDescription = description;
		this.mActivityType = activityType;
		DateFormat df = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss"); 
		Date startDateTmp=null;
		Date endDateTmp=null;
		try {
			startDateTmp = df.parse(startTime);
			endDateTmp = df.parse(endTime);
		} catch (Exception e) {
			e.printStackTrace();
		}
		this.mStart_time = startDateTmp;
		this.mEnd_time = endDateTmp;
		this.mStart_location = startLocation;
		this.mEnd_location = endLocation;
		this.startCoordinates = new Coordinates();
		this.endCoordinates = new Coordinates();
		try {
			double startLocationLatitude = Double.parseDouble(startLocationLat);
			this.startCoordinates.setLatitude(startLocationLatitude);
			this.startCoordinates.longitude= Double.parseDouble(startLocationLng);
			this.endCoordinates.latitude = Double.parseDouble(endLocationLat);
			this.endCoordinates.longitude= Double.parseDouble(endLocationLng);
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	//to make it easier
	private String mTopTxt;
	private String mBottomTxt;

	public Coordinates getStartCoordinates() {
		return startCoordinates;
	}

	public void setStartCoordinates(Coordinates startCoordinates) {
		this.startCoordinates = startCoordinates;
	}

	public Coordinates getEndCoordinates() {
		return endCoordinates;
	}

	public void setEndCoordinates(Coordinates endCoordinates) {
		this.endCoordinates = endCoordinates;
	}


	private DataCollector<double[]> locations = new DataCollector<double[]>(-1);



	public int getmActivity_id() {
		return mactivity_id;
	}

	public void setmActivity_id(int activity_id) {
		this.mactivity_id = activity_id;
	}

	public String getmActivityType() {
		return mActivityType;
	}

	public void setmActivityType(String mActivityType) {
		this.mActivityType = mActivityType;
	}



	public Activity(){
		super();
	}

	public Activity(Activity origActivity){
		this.mactivity_id = origActivity.getmActivity_id();
		this.mActivity_name = origActivity.getmActivity_name();
		this.mStart_time = origActivity.getmStart_time();
		this.mEnd_time = origActivity.getmEnd_time();
		this.mStart_location = origActivity.getmStart_location();
		this.mEnd_location = origActivity.getmEnd_location();
	}

	public Activity(int id, String activity_name, Date start_time, Date end_time,
			String start_location, String end_location) {
		this.mactivity_id = id;
		this.mActivity_name = activity_name;
		this.mStart_time = start_time;
		this.mEnd_time = end_time;
		this.mStart_location = start_location;
		this.mEnd_location = end_location;
	}

	public Activity(int id, String activity_name, String description, String activityType,Date start_time, Date end_time,
			String start_location, String end_location) {
		this.mactivity_id = id;
		this.mActivity_name = activity_name;
		this.mDescription = description;
		this.mActivityType = activityType;
		this.mStart_time = start_time;
		this.mEnd_time = end_time;
		this.mStart_location = start_location;
		this.mEnd_location = end_location;
	}

	public String getmDescription() {
		return mDescription;
	}

	public void setmDescription(String mDescription) {
		this.mDescription = mDescription;
	}

	public Activity(String toptxt, String bottomtxt) {
		this.mTopTxt = toptxt;
		this.mBottomTxt = bottomtxt;
	}


	public Drawable getmActivity_icon() {
		return mActivity_icon;
	}

	public int getId() {
		return this.mactivity_id;
	}

	public void setId(int newID) {
		this.mactivity_id = newID;
	}

	public void setmActivity_icon(Drawable mActivity_icon) {
		this.mActivity_icon = mActivity_icon;
	}


	public String getmActivity_name() {
		return mActivity_name;
	}


	public void setmActivity_name(String mActivity_name) {
		this.mActivity_name = mActivity_name;
	}


	public Date getmStart_time() {
		return mStart_time;
	}


	public void setmStart_time(Date mStart_time) {
		this.mStart_time = mStart_time;
	}


	public Date getmEnd_time() {
		return mEnd_time;
	}


	public void setmEnd_time(Date mEnd_time) {
		this.mEnd_time = mEnd_time;
	}


	public String getmStart_location() {
		/* @TODO Use GoogleApi to get Area/City Name for the start location*/
		if(mStart_location == null) {
			mStart_location = "Sunnyvale";
		}
		return mStart_location;
	}


	public void setmStart_location(String mStart_location) {
		this.mStart_location = mStart_location;
	}


	public String getmEnd_location() {
		/* @TODO Use GoogleApi to get Area/City Name for the start location*/
		if(mEnd_location == null){
			mEnd_location = "Mountain View";
		}
		return mEnd_location;
	}


	public void setmEnd_location(String mEnd_location) {
		this.mEnd_location = mEnd_location;
	}


	public DataCollector<double[]> getLocations() {
		return locations;
	}

	public void setLocations(DataCollector<double[]> locations) {
		this.locations = locations;
	}


	public String getmStartTimeFormatted() {

		DateFormat df = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss"); 
		Date date;
		String startDateStrFormatted = null;
		try {
			date = this.mStart_time;
			df = new SimpleDateFormat("hh:mm a");
			startDateStrFormatted = df.format(date);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return startDateStrFormatted;
	}

	public String getmEndTimeFormatted() {
		DateFormat df = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss"); 
		Date date;
		String endDateStrFormatted = null;
		try {
			date = this.mEnd_time;
			df = new SimpleDateFormat("hh:mm a");
			endDateStrFormatted = df.format(date);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return endDateStrFormatted;
	}


}
