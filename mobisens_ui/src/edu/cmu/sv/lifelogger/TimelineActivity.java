package edu.cmu.sv.lifelogger;

import java.util.ArrayList;
import java.util.List;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import edu.cmu.sv.lifelogger.database.LocalDbAdapter;
import edu.cmu.sv.lifelogger.database.TimelineManager;
import edu.cmu.sv.lifelogger.entities.TimelineItem;
import edu.cmu.sv.lifelogger.entities.TimelineSegment;
import edu.cmu.sv.lifelogger.helpers.TimelineItemHelper;
import edu.cmu.sv.lifelogger.helpers.TimelineSegmentHeader;
import edu.cmu.sv.mobisens_ui.R;



public class TimelineActivity extends Activity{
	private static LinearLayout MY_MAIN_LAYOUT;
	private ArrayList<TimelineSegment> timelineItemList;
	Context cxt;
	public static LocalDbAdapter db;
	public static int maxActivityId;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		cxt=this;
		db = new LocalDbAdapter(this);
		db.open();
		ActionBar actionBar = getActionBar();

		actionBar.setDisplayShowTitleEnabled(false);

		setContentView(R.layout.main_vert_lin_layout);
		db = new LocalDbAdapter(this);
		db.open();

		MY_MAIN_LAYOUT = (LinearLayout) findViewById(R.id.mainLayout);
		
/*		Populate the timeline with data
		timelineItemList = TimelineManager.getAllTimelineItems();
		
		 @issue: Store the latest activity. Solutions:
		 * 1) store the max activityID. This will be an issue, if we permit deletion of activities later
		 * 2) Store the last time stamp of the latest activity
		 * 
		// Store the max activity id .
		for (TimelineSegment tls: timelineItemList){
			
			//add timeline segment
			TimelineSegmentHeader tsh = new TimelineSegmentHeader(this, tls.getDate().toString(), MY_MAIN_LAYOUT);

			ArrayList<TimelineItem> tlItems= tls.getData();
			for (TimelineItem item: tlItems){
				//add its items
				TimelineItemHelper tmh = new TimelineItemHelper(this, item, MY_MAIN_LAYOUT, itemListener);

			}

		}*/

	}

	
	public void refreshTimelineSegments(){
		//Clear the Main Layout
		MY_MAIN_LAYOUT.removeAllViews();
		
		/*Re-Populate the timeline with data*/
		timelineItemList = TimelineManager.getAllTimelineItems();
		for (TimelineSegment tls: timelineItemList){
			
			//add timeline segment
			TimelineSegmentHeader tsh = new TimelineSegmentHeader(this, tls.getDate().toString(), MY_MAIN_LAYOUT);

			ArrayList<TimelineItem> tlItems= tls.getData();
			for (TimelineItem item: tlItems){
				//add its items
				if(item.getmActivity_id() > maxActivityId){
					maxActivityId = item.getmActivity_id(); // maxActivityId is the latest activity
				}
				TimelineItemHelper tmh = new TimelineItemHelper(this, item, MY_MAIN_LAYOUT, itemListener);

			}

		}
		
		
		
	}
	
	@Override
	protected void onResume() {
		super.onResume();

		// Normal case behavior follows

		refreshTimelineSegments();

		// @TODO Hardcoded stuf .. correct it soon
		List <String> locations = db.getImagesForActivity(2);

		System.out.println("dummy");
	}




	View.OnClickListener itemListener = new View.OnClickListener() {



		public void onClick(View view) {

			TextView t = (TextView) view.findViewById(R.id.name);
			String txt = t.getText().toString();


			TextView bottom = (TextView) view.findViewById(R.id.bottomTxt);
			String bottomtxt = bottom.getText().toString();

			System.out.println("printing name: " + bottomtxt + ", " + txt);

			//			Toast.makeText(TimelineTestActivity.this, bottomtxt + " " + txt, Toast.LENGTH_SHORT).show();



			Intent intent = new Intent(TimelineActivity.this,TagActivity.class);
			intent.putExtra("top_txt", txt);
			intent.putExtra("bottom_txt", bottomtxt);

			//@TODO Change code to forward the real activity id on wich click is done. 
			//Find the acticity id for the item 
			String activityType = null, startLocation = null, endLocation = null, startTime = null, endTime = null;
			// Parse top_txt for activityType, startTime and endTime
			String[] splitTxtStr  = txt.split("\\s+");

			activityType = splitTxtStr[0];
			startTime = splitTxtStr[1] + " "+  splitTxtStr[2];
			endTime = splitTxtStr[4]+  " " +  splitTxtStr[5];

			// For bottom, split after 'to'
			String[] splitBottomTxtStr  = bottomtxt.split(" to ");
			startLocation = splitBottomTxtStr[0] ;
			if(splitBottomTxtStr.length == 1) {
				endLocation = startLocation;
			}else{

				endLocation = splitBottomTxtStr[1] ;
			}

			String activityIDStr = "";
			int activityID = 1;
			activityIDStr = db.getActivityID(activityType, startLocation, endLocation, startTime, endTime);
			if(activityIDStr!=null && !activityIDStr.equals(""))
				activityID = (int)Integer.parseInt(activityIDStr);
			intent.putExtra("activityID", activityID);

			startActivity(intent);

			/*
			// custom dialog
			final Dialog dialog = new Dialog(cxt);
			dialog.setContentView(R.layout.timeline_dialog);
			dialog.setTitle(txt);

			// set the custom dialog components - text, image and button
			//			TextView text = (TextView) dialog.findViewById(R.id.text);
			//			text.setText("Android custom dialog example!");
			//			ImageView image = (ImageView) dialog.findViewById(R.id.image);
			//			image.setImageResource(R.drawable.ic_launcher);

			Button dialogButton = (Button) dialog.findViewById(R.id.dialogButtonOK);
			// if button is clicked, close the custom dialog
			dialogButton.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					dialog.dismiss();
				}
			});

			dialog.show(); */
		}


	};


	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		// getMenuInflater().inflate(R.menu.login, menu);
		getMenuInflater().inflate(R.menu.action_bar_timeline, menu);

		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		if (item.getItemId() == R.id.timeline)
		{
			Intent intent = new Intent(this, TimelineActivity.class);
			startActivity(intent);
		} else if (item.getItemId() == R.id.profile)
		{
			Intent intent = new Intent(this, PieChartBuilderActivity.class);
			startActivity(intent);
		}else if (item.getItemId() == R.id.settings)
		{
			Intent intent = new Intent(this, SettingsActivity.class);
			startActivity(intent);
		}

		//TODO: Add Settings activity piece
		//TODO: CHoose correct drawables in action_bar in res/menu
		return true;
	}
}
