package edu.cmu.sv.lifelogger;

import java.util.ArrayList;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;
import edu.cmu.sv.lifelogger.database.LocalDbAdapter;
import edu.cmu.sv.lifelogger.database.TimelineManager;
import edu.cmu.sv.lifelogger.entities.TimelineItem;
import edu.cmu.sv.lifelogger.entities.TimelineSegment;
import edu.cmu.sv.lifelogger.helpers.LazyAdapter;
import edu.cmu.sv.lifelogger.helpers.TimelineSegmentHeader;
import edu.cmu.sv.mobisens_ui.R;

public class TimelineActivity extends Activity {

	protected static final String TAG_ACTIVITY = "activity_name";
	protected static final String TAG_START_TIME = "start_time";
	protected static final String TAG_END_TIME = "end_time";
	protected static final String TAG_START_LOCATION = "start_location";
	protected static final String TAG_END_LOCATION = "end_location";
	protected static final String TAG_ACTIVITY_ICON = "activity_icon";

	private static LinearLayout MY_MAIN_LAYOUT;

	private LazyAdapter adapter;
	protected ListView list;

	private ArrayList<TimelineItem> timelineItemList;
	public static LocalDbAdapter db;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.result_list_view);
		//Create the new database
	 db = new LocalDbAdapter(this);
     db.open();
		//		setContentView(R.layout.main_vert_lin_layout);
		//		MY_MAIN_LAYOUT = (LinearLayout) findViewById(R.id.mainLayout);

		//TimelineSegmentHeader rbq = new TimelineSegmentHeader(this, "today", MY_MAIN_LAYOUT);

		timelineItemList = new ArrayList<TimelineItem>();

		updateTimelineItems();

		if (timelineItemList == null){
			Log.d("LIST ", "NO Timeline Items");
			Toast.makeText(TimelineActivity.this, "NO Timeline Items???", Toast.LENGTH_SHORT).show();
		}

		//		View child = (View) View.inflate(this,R.layout.result_list_view, null);

		list = (ListView) findViewById(R.id.list);


		// Getting adapter by passing data ArrayList
		adapter = new LazyAdapter(this, timelineItemList);
		
//		ArrayList<TimelineSegment> allSegments = TimelineManager.getAllTimelineItems();
//		for (int i = 1; i < 50; i++) {
//			adapter.addItem(timelineItemList.get(0));
//			if (i % 4 == 0) {
//				adapter.addSeparatorItem(allSegments.get(0).getDate().toString());
//			}
//		}
//		
		list.setAdapter(adapter);

		// Click list item row
		list.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {

				//				timelineItemList.get(position));

				//				Intent intent = new Intent(TimelineActivity.this,GatherInfoActivity.class);
				//				intent.putExtra(TAG_NAME, timelineItemList.get(position).getName());
				//				intent.putExtra(TAG_EMAIL, timelineItemList.get(position).getEmail());
				//				intent.putExtra(TAG_GRADDATE, timelineItemList.get(position).getGradDate());
				//				intent.putExtra(TAG_AREAINTEREST, timelineItemList.get(position).getAreaInterest());
				//				//				Toast.makeText(getApplicationContext(), timelineItemList.get(position).get("name"),
				//				//						Toast.LENGTH_LONG).show();
				//
				//				startActivity(intent);
				
				
			}
		});

		
		this.adapter.notifyDataSetChanged();
		//		MY_MAIN_LAYOUT.addView(list);

	}

	@Override
	protected void onResume() {
		super.onResume();
	}  

	@Override
	protected void onPause() {
		super.onPause();
	}

	@Override
	protected void onStop() {
		super.onStop();    
	}

	/**
	 * Adds all timeline items to array list
	 */

	private void updateTimelineItems() {

		// TODO Auto-generated method stub
		timelineItemList = TimelineManager.getAllTimelineItems().get(0).getData();
	} 
	
	public static class ViewHolder {
        public LinearLayout llView;
    }
}
