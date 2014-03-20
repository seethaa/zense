package edu.cmu.sv.lifelogger.database;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import android.content.Context;
import edu.cmu.sv.lifelogger.TimelineActivity;
import edu.cmu.sv.lifelogger.entities.TimelineItem;
import edu.cmu.sv.lifelogger.entities.TimelineSegment;
import edu.cmu.sv.lifelogger.helpers.App;

public class TimelineManager {
	

	public static ArrayList<TimelineSegment> getAllTimelineItems() {
		ArrayList<TimelineSegment> ts = getAllTimelineSegmentsDynamic();
		return ts;
	}
	
	/**
	 * Function to return dynamic timeline segments
	 * @return ArrayList<TimelineSegment> 
	 */
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

	/**
	 * Function to return Date in string format from given data+time 
	 * @param strDate
	 * @return Date in String format
	 */
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

	/**
	 * Function to return date from string
	 * @param strDate
	 * @return
	 */
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

	/* 
	 * IT is the function to 'seed' Timeline segments initially. Not needed now
	 * Delete it when want
	 */

	/*private static ArrayList<TimelineSegment> getAllTimelineSegments() {
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
		data2.add((TimelineItem)data1.get(0));
		data2.add((TimelineItem)data1.get(3));
		data2.add((TimelineItem)data1.get(4));
		TimelineSegment ts2 = new TimelineSegment(data2, yesterdayStr);

		// segment 3
		ArrayList<TimelineItem> data3 = new ArrayList<TimelineItem>();
		data3.add((TimelineItem) data1.get(1));
		data3.add((TimelineItem) data1.get(2));
		data3.add((TimelineItem) data1.get(3));
		TimelineSegment ts3 = new TimelineSegment(data3, dayb4yesterdayStr);

		// add all segments
		ts.add(ts1);
		ts.add(ts2);
		ts.add(ts3);

		return ts;

	}
	 */	



}
