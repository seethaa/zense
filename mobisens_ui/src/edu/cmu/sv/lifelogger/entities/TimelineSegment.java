package edu.cmu.sv.lifelogger.entities;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class TimelineSegment{
	private ArrayList<TimelineItem> data;
	private String d;

	public TimelineSegment(ArrayList<TimelineItem> data1, String todayStr){
		data  = data1;
		
		/* The date being supplied is string. Convert it to get the 
		 * date in required format*/
		DateFormat df = new SimpleDateFormat("MM/dd/yyyy"); 
		Date date;
		String segmentDate = todayStr;
		try {
			date = df.parse(segmentDate);
			df = new SimpleDateFormat("E, MMM dd"); 
			segmentDate = df.format(date);   
		} catch (Exception e) {
			e.printStackTrace();
		}
		d = segmentDate;
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

	public void setDate(String strDate) {
		/*Date is in the format MM/dd/yyyy*/
		DateFormat df = new SimpleDateFormat("MM/dd/yyyy"); 
		Date date;
		String segmentDate = strDate;
		try {
			date = df.parse(segmentDate);
			df = new SimpleDateFormat("E, MMM dd"); 
			segmentDate = df.format(date);   
		} catch (Exception e) {
			e.printStackTrace();
		}	
		
		this.d = segmentDate;
	}







}