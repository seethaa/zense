package edu.cmu.sv.lifelogger.entities;
/**
 * This file is just for the dashboard purposes, as it is supposed to store 
 * aggregated data. 
 * @author himanshu
 *
 */
public class ActivityItem {
	private String mActivity_icon;
	private String mActivity_name;
	private int mTime;
	private double mPercentage;
	

	public ActivityItem(String activity_name, int time, double percentage) {
		this.mActivity_name = activity_name;
		this.mTime = time;
		this.mPercentage = percentage;
	}
	
	public ActivityItem(String activity_name,  double percentage) {
		this.mActivity_name = activity_name;
		this.mTime = 0;
		this.mPercentage = percentage;
	}
	public String getmActivity_icon() {
		return mActivity_icon;
	}


	public void setmActivity_icon(String mActivity_icon) {
		this.mActivity_icon = mActivity_icon;
	}


	public String getmActivity_name() {
		return mActivity_name;
	}


	public void setmActivity_name(String mActivity_name) {
		this.mActivity_name = mActivity_name;
	}

	public int getmTime() {
		return mTime;
	}

	public void setmTime(int mTime) {
		this.mTime = mTime;
	}

	public double getmPercentage() {
		return mPercentage;
	}

	public void setmPercentage(double mPercentage) {
		this.mPercentage = mPercentage;
	}



}
