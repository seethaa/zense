package edu.cmu.sv.lifelogger.entities;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.graphics.drawable.Drawable;

public class TimelineItem {
	private Drawable mActivity_icon;
	private String mActivity_name;
	private String mStart_time;
	private String mEnd_time;
	private String mStart_location;
	private String mEnd_location;
	private int mactivity_id;
	
	public int getmActivity_id() {
		return mactivity_id;
	}

	public void setmActivity_id(int activity_id) {
		this.mactivity_id = activity_id;
	}

	private String mActivityType;
	private String mDescription;

	public String getmActivityType() {
		return mActivityType;
	}

	public void setmActivityType(String mActivityType) {
		this.mActivityType = mActivityType;
	}

	//to make it easier
	private String mTopTxt;
	private String mBottomTxt;

	public TimelineItem(){
		super();
	}

	public TimelineItem(int id, String activity_name, String start_time, String end_time,
			String start_location, String end_location) {
		this.mactivity_id = id;
		this.mActivity_name = activity_name;
		this.mStart_time = start_time;
		this.mEnd_time = end_time;
		this.mStart_location = start_location;
		this.mEnd_location = end_location;
	}

	public TimelineItem(int id, String activity_name, String description, String activityType,String start_time, String end_time,
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

	public TimelineItem(String toptxt, String bottomtxt) {
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


	public String getmStart_time() {
		return mStart_time;
	}


	public void setmStart_time(String mStart_time) {
		this.mStart_time = mStart_time;
	}


	public String getmEnd_time() {
		return mEnd_time;
	}


	public void setmEnd_time(String mEnd_time) {
		this.mEnd_time = mEnd_time;
	}


	public String getmStart_location() {
		return mStart_location;
	}


	public void setmStart_location(String mStart_location) {
		this.mStart_location = mStart_location;
	}


	public String getmEnd_location() {
		return mEnd_location;
	}


	public void setmEnd_location(String mEnd_location) {
		this.mEnd_location = mEnd_location;
	}


	public String getTimelineTopText(){	

		
		String startTime = this.mStart_time;
		String endTime = this.mEnd_time;

		/* Date is returned in String form of Date. Convert it to date, to extract Time in correct format*/

		DateFormat df = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss"); 
		Date date;
		try {
			date = df.parse(startTime);
			df = new SimpleDateFormat("hh:mm a");
			startTime = df.format(date);
			
			df = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
			date = df.parse(endTime);
			df = new SimpleDateFormat("hh:mm a");
			endTime = df.format(date);	        
		} catch (Exception e) {
			e.printStackTrace();
		}
		return this.mActivity_name + " " + startTime + " - " + endTime;
	}

	public String getTimelineSubText(){
//		if (mBottomTxt==null){

			String subtext = "";
			if (this.mStart_location.equalsIgnoreCase(this.mEnd_location)){
				subtext = mStart_location; 
			}
			else{
				subtext = this.mStart_location + " to " + this.mEnd_location;
			}
			return subtext;

//		}
//		return mBottomTxt;
	}


}
