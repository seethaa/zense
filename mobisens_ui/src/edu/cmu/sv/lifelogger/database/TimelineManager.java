package edu.cmu.sv.lifelogger.database;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import edu.cmu.sv.lifelogger.TimelineActivity;
import edu.cmu.sv.lifelogger.entities.TimelineItem;
import edu.cmu.sv.lifelogger.entities.TimelineSegment;

public class TimelineManager {

	//TODO: Change this to get real data
	public static ArrayList<TimelineSegment> getAllTimelineItems() {

		//ArrayList<TimelineSegment> ts = getAllTimelineSegments();
		ArrayList<TimelineSegment> ts = getAllTimelineSegmentsDynamic();

		return ts;


	}
	
	private static ArrayList<TimelineSegment> getAllTimelineSegmentsDynamic() {
		ArrayList<TimelineSegment> ts = new ArrayList<TimelineSegment>();
		ArrayList<TimelineItem> data1 = new ArrayList<TimelineItem>();
		data1 = TimelineActivity.db.getAllTimelineActivity();
		/*data1 is timeline item, sorted by activities time(desc)*/
		/* Algorithm to create dynamic segments
		 * 1) Loop through the itemlist
		 * 2) Keep on adding the items to a dummy timelineItem arraylist
		 * 3) Whenever new date is encountered, make a new segment for the previous date
		 * 4) Create a new dummy itemlist, and continue the same process */
		
		int counter = 0;
		String curDate = null;
		String prevDate = null;
		ArrayList<TimelineItem> tempData1 = new ArrayList<TimelineItem>();
		for(TimelineItem t1 : data1) //use for-each loop
		{
			counter++;
			tempData1.add(t1);
			prevDate = curDate;
			/* Get date out of current timeline item*/
			curDate = getStrDateFromString(t1.getmEnd_time());
			
			/*Handle the first iteration. PrevDate should be equal to currentDate*/
			if(counter == 1){
				prevDate = curDate;
			}
			if(!curDate.equals(prevDate)){
				// Create New segment
				TimelineSegment ts1 = new TimelineSegment(tempData1, prevDate);
				// Add to our TimelineSegment ArrayList
				ts.add(ts1);
				// Flush out the old tempData1
				tempData1 = new ArrayList<TimelineItem>();
			}
		}
		
		//Handle the last segment here, as it would not have been written till now
		TimelineSegment ts1 = new TimelineSegment(tempData1, curDate);
		// Add to our TimelineSegment ArrayList
		ts.add(ts1);

		return ts;
	
	}
	

	private static String getStrDateFromString(String strDate){

		DateFormat df = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss"); 
		Date date = null;
		String returnDate = null;
		try {
			date = df.parse(strDate);
			df = new SimpleDateFormat("MM/dd/yyyy");
			returnDate = df.format(date);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return returnDate;
	}

	private static Date getDateFromString(String strDate){

		DateFormat df = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss"); 
		Date date = null;
		try {
			date = df.parse(strDate);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return date;
	}
	private static ArrayList<TimelineSegment> getAllTimelineSegments() {
		ArrayList<TimelineSegment> ts = new ArrayList<TimelineSegment>();
		ArrayList<TimelineItem> data1 = new ArrayList<TimelineItem>();
		// segment 1
		data1 = TimelineActivity.db.getAllTimelineActivity();

		DateFormat dateFormat = new SimpleDateFormat("EEE, MMM dd");
		Date today = new Date();
		Calendar cal = Calendar.getInstance();
		String todayStr = dateFormat.format(cal.getTime());
		
		cal.add(Calendar.DATE, -1);    
		String yesterdayStr = dateFormat.format(cal.getTime());
		
		cal.add(Calendar.DATE, -1);    
		String dayb4yesterdayStr = dateFormat.format(cal.getTime());

		

		TimelineSegment ts1 = new TimelineSegment(data1, todayStr);

		//segment 2
		ArrayList<TimelineItem> data2 = new ArrayList<TimelineItem>();
		/*data2.add((TimelineItem)data1.get(0));
		data2.add((TimelineItem)data1.get(3));
		data2.add((TimelineItem)data1.get(4));*/
		TimelineSegment ts2 = new TimelineSegment(data2, yesterdayStr);

		// segment 3
		ArrayList<TimelineItem> data3 = new ArrayList<TimelineItem>();
		/*data3.add((TimelineItem) data1.get(1));
		data3.add((TimelineItem) data1.get(2));
		data3.add((TimelineItem) data1.get(3));*/
		TimelineSegment ts3 = new TimelineSegment(data3, dayb4yesterdayStr);
 
		// add all segments
		ts.add(ts1);
		ts.add(ts2);
		ts.add(ts3);

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
