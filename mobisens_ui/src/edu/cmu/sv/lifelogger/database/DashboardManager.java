package edu.cmu.sv.lifelogger.database;

import edu.cmu.sv.lifelogger.helpers.App;
import edu.cmu.sv.lifelogger.helpers.DefinitionHelper;

import java.util.ArrayList;

import android.content.Context;
import edu.cmu.sv.lifelogger.entities.ActivityItem;
import edu.cmu.sv.lifelogger.entities.MoodItem;
import edu.cmu.sv.lifelogger.entities.TimelineItem;

public class DashboardManager {
	static App app;

/*	public static ArrayList<ActivityItem> getAllPieChartData() {
		ArrayList<ActivityItem> data  = new ArrayList<ActivityItem>();
		
		 Care should be taken to add correct percentage for each value. 
		 * @ToDo write a function to convert a value into percentage
		 * 
		// Iterate through the db and get all the rows
		ActivityItem t1 = new ActivityItem("Dining", 2, 22);
		data.add(t1);
		ActivityItem t2 = new ActivityItem("Driving", 1, 15);
		data.add(t2);
		ActivityItem t3 = new ActivityItem("Shopping", 1, 4);
		data.add(t3);
		ActivityItem t4 = new ActivityItem("Sleeping", 7, 27);
		data.add(t4);
	
		ActivityItem t6 = new ActivityItem("Working", 8, 26);
		data.add(t6);
		ActivityItem t7 = new ActivityItem("Walking", 1, 1);
		data.add(t7);
		ActivityItem t5 = new ActivityItem("Misc", 1, 5);
		data.add(t5);
		return data;
	}*/
	
	
	//TODO: Add variable for icon location
	public static ArrayList<ActivityItem> getAllPieChartData( Context ctx) {
		app = ((App)ctx.getApplicationContext());
		return app.db.getAllDashboardSummary();
	}
	
	public static void doDashboardSummaryAnalysis( Context ctx ){
		app = ((App)ctx.getApplicationContext());
		app.db.doDashboardSummaryAnalysis();
		return;
	}
	
	
	
	
	
	
	public static ArrayList<MoodItem> getAllLineChartData() {
		
		ArrayList<MoodItem> data  = new ArrayList<MoodItem>();
		MoodItem t1 = new  MoodItem(DefinitionHelper.SUPER_EXCITING, 4 );
		data.add(t1);
		MoodItem t2 = new  MoodItem(DefinitionHelper.SAD,6 );
		data.add(t2);
		MoodItem t3 = new  MoodItem(DefinitionHelper.HAPPY, 7 );
		data.add(t3);
		MoodItem t4 = new  MoodItem(DefinitionHelper.DEPRESSED, 8 );
		data.add(t4);
		MoodItem t5 = new  MoodItem(DefinitionHelper.NEUTRAL, 9 );
		data.add(t5);
		
		
		return data;
	}
	
	
	
	

}
