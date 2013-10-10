package edu.cmu.sv.lifelogger.entities;

import java.util.ArrayList;
import java.util.Date;

public class TimelineSegment{
	private ArrayList<TimelineItem> data;
	private Date d;

	public TimelineSegment(ArrayList<TimelineItem> data1, Date today){
		data  = data1;
		d = today;
	}

	public ArrayList<TimelineItem> getData() {
		return data;
	}

	public void setData(ArrayList<TimelineItem> data) {
		this.data = data;
	}

	public Date getDate() {
		return d;
	}

	public void setDate(Date d) {
		this.d = d;
	}







}