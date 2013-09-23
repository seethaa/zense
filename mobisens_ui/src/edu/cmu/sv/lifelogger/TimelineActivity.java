package edu.cmu.sv.lifelogger;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.Toast;
import edu.cmu.sv.lifelogger.database.TimelineManager;
import edu.cmu.sv.lifelogger.entities.TimelineItem;
import edu.cmu.sv.lifelogger.helpers.LazyAdapter;
import edu.cmu.sv.mobisens_ui.R;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

public class TimelineActivity extends Activity {
	
	protected static final String TAG_ACTIVITY = "activity_name";
	protected static final String TAG_START_TIME = "start_time";
	protected static final String TAG_END_TIME = "end_time";
	protected static final String TAG_START_LOCATION = "start_location";
	protected static final String TAG_END_LOCATION = "end_location";
	protected static final String TAG_ACTIVITY_ICON = "activity_icon";
	
	
	private LazyAdapter adapter;
	protected ListView list;
	
	private ArrayList<TimelineItem> timelineItemList;
	

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.result_list_view);
		timelineItemList = new ArrayList<TimelineItem>();
		
		updateTimelineItems();
		
		if (timelineItemList == null){
			Log.d("LIST ", "NO Timeline Items");
			Toast.makeText(TimelineActivity.this, "NO Timeline Items???", Toast.LENGTH_SHORT).show();
		}
		list = (ListView) findViewById(R.id.list);

		// Getting adapter by passing data ArrayList
		adapter = new LazyAdapter(this, timelineItemList);
		list.setAdapter(adapter);

		//TODO: add gesutre listeners as needed later
		
//		list.setOnTouchListener(new GestureSwipeListener() {
//			@Override
//			public void onSwipeUp() {
//				// Toast.makeText(timelineItemListActivity.this, "top", Toast.LENGTH_SHORT).show();
//			}
//			@Override
//			public void onSwipeRight() {
//				// Toast.makeText(timelineItemListActivity.this, "left", Toast.LENGTH_SHORT).show();
//			}
//			@Override
//			public void onSwipeLeft() {
//				//Toast.makeText(timelineItemListActivity.this, "right", Toast.LENGTH_SHORT).show();
//
//				Intent intent = new Intent(timelineItemListActivity.this, SomeActivity.class);
//				startActivity(intent);
//			}
//			@Override
//			public void onSwipeDown() {
//				// Toast.makeText(timelineItemListActivity.this, "bottom", Toast.LENGTH_SHORT).show();
//			}
//		});

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
		timelineItemList = TimelineManager.getAllCandidates();
	}
}
