package edu.cmu.sv.lifelogger.helpers;

/*
 * LazyAdapter.java
 *
 * Copyright (c) 2013 Amazon.com, Inc. or its affiliates. All Rights Reserved.
 *
 * PROPRIETARY/CONFIDENTIAL
 *
 * Use is subject to license terms.
 */
import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import edu.cmu.sv.lifelogger.entities.TimelineItem;
import edu.cmu.sv.mobisens_ui.R;

/**
 * Custom adapter class to take care of the TimelineItem listview
 * 
 * 
 */
public class LazyAdapter extends ArrayAdapter<TimelineItem> {

	private final ArrayList<TimelineItem> data;
	private final Activity activity;
	private LayoutInflater inflater = null;

	
	public LazyAdapter(Activity a, ArrayList<TimelineItem> timelineItemList) {
		super(a, R.layout.timeline_item, timelineItemList);
		activity = a;
		data = timelineItemList;
		inflater = (LayoutInflater) activity
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override 
	public int getCount() {
		return data.size();
	}

	@Override
	public TimelineItem getItem(int position) {
		return data.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View vi = convertView;
		if (convertView == null){
			vi = inflater.inflate(R.layout.timeline_item, parent, false);
		} 

		//TODO: Check if other fields need to be added in the listview item
		TextView name = (TextView) vi.findViewById(R.id.name); // name of activity
		Typeface typeface = Typeface.createFromAsset(activity.getAssets(), "fonts/Roboto-Regular.ttf" );
		name.setTypeface(typeface);
		//		 name.setTextSize(14);
		//		 name.setTextColor(activity.getResources().getColor(R.color.activity_black));
		TextView email_address = (TextView) vi.findViewById(R.id.address); // start time

		TimelineItem item;
		item = data.get(position);

		ImageView activity_icon = (ImageView) vi.findViewById(R.id.activity_icon); // start time

		settimelineItemList(item, name, email_address, activity_icon);

		return vi;
	}

	private void settimelineItemList(TimelineItem item, TextView name,
			TextView address, ImageView activity_icon) {
		//TODO: Add rest of the pieces
		String activity_name = item.getmActivity_name();
		name.setText(item.getTimelineTopText());
		Typeface typeface = Typeface.createFromAsset(activity.getAssets(), "fonts/Roboto-Regular.ttf" );
		name.setTypeface(typeface);
		name.setTypeface(Typeface.DEFAULT_BOLD);
 
		String addr = item.getTimelineSubText();
		address.setText(addr); 
        
		if (activity_name.toString().equalsIgnoreCase("driving")){
			activity_icon.setImageResource(R.drawable.driving);
		}
		else if(activity_name.toString().equalsIgnoreCase("dining")){
			activity_icon.setImageResource(R.drawable.dining);
		}
		else if (activity_name.toString().equalsIgnoreCase("working")){
			activity_icon.setImageResource(R.drawable.working);
		}
		else if (activity_name.toString().equalsIgnoreCase("walking")){
			activity_icon.setImageResource(R.drawable.walking);
		}
		else{
			activity_icon.setImageResource(R.drawable.unknown);

		}


	}

}
