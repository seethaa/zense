package edu.cmu.sv.lifelogger.entities;

import java.util.Date;
import java.util.List;
import java.util.Locale;

import edu.cmu.sv.lifelogger.util.DataCollector;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
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
	private Drawable mActivity_icon;
	private String mActivity_name;
	private Date mStart_time;
	private Date mEnd_time;
	
	private String mStart_location;
	private String mEnd_location;
	private DataCollector<double[]> locations = new DataCollector<double[]>(-1);
	

	private String mActivityType;
	private String mDescription;
	private int mactivity_id;

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
	

	//to make it easier
	private String mTopTxt;
	private String mBottomTxt;

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
			/*Geocoder geocoder;
			List<Address> addresses;
			geocoder =new Geocoder(this,Locale.getDefault());
			addresses = geocoder.getFromLocation(latitude, longitude,1);
			String address = addresses.get(0).getAddressLine(0);
			String city = addresses.get(0).getAddressLine(1);
			String country = addresses.get(0).getAddressLine(2);*/
			/*For now add dummy value*/
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


}
