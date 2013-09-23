package edu.cmu.sv.lifelogger.database;

import java.util.ArrayList;

import edu.cmu.sv.lifelogger.entities.TimelineItem;

public class TimelineManager {

	//TODO: Change this to get real data
	public static ArrayList<TimelineItem> getAllCandidates() {
		ArrayList<TimelineItem> data  = new ArrayList<TimelineItem>();
		
		TimelineItem t1 = new TimelineItem("Driving", "9:00 AM", "9:30 AM", "Santa Clara", "Palo Alto");
		data.add(t1);
		 
		TimelineItem t2 = new TimelineItem("Working", "9:30 AM", "11:30 AM", "University Ave, Palo Alto", "University Ave, Palo Alto");
		data.add(t2);
		 
		TimelineItem t3 = new TimelineItem("Dining", "12:00 PM", "1:00 PM", "Starbucks, Palo Alto", "Starbucks, Palo Alto");
		data.add(t3);
		
		TimelineItem t4 = new TimelineItem("Walking", "1:00 PM", "1:30 PM", "University Ave, Palo Alto", "University Ave, Palo Alto");
		data.add(t4);
		
//		TimelineItem t5 = new TimelineItem("Walking", "1:00 PM", "1:30 PM", "University Ave, Palo Alto", "University Ave, Palo Alto");
//		data.add(t5);
//		
		  
		return data;
	}

}
