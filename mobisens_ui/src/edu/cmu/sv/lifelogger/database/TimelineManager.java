package edu.cmu.sv.lifelogger.database;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import edu.cmu.sv.lifelogger.entities.TimelineItem;
import edu.cmu.sv.lifelogger.entities.TimelineSegment;

public class TimelineManager {

	//TODO: Change this to get real data
	public static ArrayList<TimelineSegment> getAllTimelineItems() {

		ArrayList<TimelineSegment> ts = createDummyData();

		return ts;


	}

	private static ArrayList<TimelineSegment> createDummyData() {
		ArrayList<TimelineSegment> ts = new ArrayList<TimelineSegment>();
		
		//Segment 1
		ArrayList<TimelineItem> data1 = new ArrayList<TimelineItem>();

		TimelineItem t1 = new TimelineItem(1, "Driving", "9:00 AM", "9:30 AM", "Santa Clara", "Palo Alto");     
		TimelineItem t2 = new TimelineItem(2, "Working", "9:30 AM", "11:30 AM", "University Ave, Palo Alto", "University Ave, Palo Alto");
		TimelineItem t3 = new TimelineItem(3, "Dining", "12:00 PM", "1:00 PM", "Starbucks, Palo Alto", "Starbucks, Palo Alto");
		TimelineItem t4 = new TimelineItem(4, "Walking", "1:00 PM", "1:30 PM", "University Ave, Palo Alto", "University Ave, Palo Alto");
		TimelineItem t5 = new TimelineItem(5, "Work Meeting", "9:30 PM", "11:30 PM", "Moffett Field, Mountain View", "Moffett Field, Mountain View");

		data1.add(t1); 
		data1.add(t2);
		data1.add(t3);
		data1.add(t4);
		data1.add(t5); 
		
		DateFormat dateFormat = new SimpleDateFormat("EEE, MMM dd");
		Date today = new Date();
		String todayStr = dateFormat.format(today);
		
		TimelineSegment ts1 = new TimelineSegment(data1, todayStr);
		

		//segment 2   
		ArrayList<TimelineItem> data2 = new ArrayList<TimelineItem>();
		
		data2.add(t1);
		data2.add(t4);
		data2.add(t5);
		
		TimelineSegment ts2 = new TimelineSegment(data2, todayStr);

		
		//segment 3
		ArrayList<TimelineItem> data3 = new ArrayList<TimelineItem>();
		data3.add(t2);
		data3.add(t3);
		data3.add(t4);
		TimelineSegment ts3 = new TimelineSegment(data3, todayStr);

		
		//add all segments
		ts.add(ts1);
		ts.add(ts2);
		ts.add(ts3);
		
		return ts;


		

	}

	

}
