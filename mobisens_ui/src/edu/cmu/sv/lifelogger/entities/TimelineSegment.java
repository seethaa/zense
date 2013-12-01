package edu.cmu.sv.lifelogger.entities;

import java.util.ArrayList;
import java.util.Date;

public class TimelineSegment{
	private ArrayList<TimelineItem> data;
	private String d;

	public TimelineSegment(ArrayList<TimelineItem> data1, String todayStr){
		data  = data1;
		d = todayStr;
	}

	public ArrayList<TimelineItem> getData() {
		return data;
	}

	public void setData(ArrayList<TimelineItem> data) {
		this.data = data;
	}

	public String getDate() {
		return d;
	}

	public void setDate(String d) {
		this.d = d;
	}







}