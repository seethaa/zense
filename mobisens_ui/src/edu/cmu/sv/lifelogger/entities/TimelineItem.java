package edu.cmu.sv.lifelogger.entities;

import android.graphics.drawable.Drawable;

public class TimelineItem {
	private Drawable mActivity_icon;
	private String mActivity_name;
	private String mStart_time;
	private String mEnd_time;
	private String mStart_location;
	private String mEnd_location;


	public TimelineItem(String activity_name, String start_time, String end_time,
			String start_location, String end_location) {
		this.mActivity_name = activity_name;
		this.mStart_time = start_time;
		this.mEnd_time = end_time;
		this.mStart_location = start_location;
		this.mEnd_location = end_location;
	}

	public Drawable getmActivity_icon() {
		return mActivity_icon;
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
		return this.mActivity_name + " " + this.mStart_time + " - " + this.mEnd_time;
	}

	public String getTimelineSubText(){
		String subtext = "";
		if (this.mStart_location.equalsIgnoreCase(this.mEnd_location)){
			subtext = mStart_location; 
		}
		else{
			subtext = this.mStart_location + " to " + this.mEnd_location;
		}
		return subtext;
	}


}
