package edu.cmu.sv.lifelogger.entities;

public class ActivityItem {
	private String mActivity_icon;
	private String mActivity_name;
	private int mTime;
	private int mPercentage;
	

	public ActivityItem(String activity_name, int time, int percentage) {
		this.mActivity_name = activity_name;
		this.mTime = time;
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

	public int getmPercentage() {
		return mPercentage;
	}

	public void setmPercentage(int mPercentage) {
		this.mPercentage = mPercentage;
	}



}
