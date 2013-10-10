package edu.cmu.sv.lifelogger.helpers;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Typeface;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import edu.cmu.sv.lifelogger.entities.TimelineItem;
import edu.cmu.sv.mobisens_ui.R;


/**
 * TextViewHelper is a layout that contains a single textview based on user's input
 * TODO: Take options for layout params, color, etc. ?
 * --not for use now
 *
 */
public class TimelineItemHelper {
	private ArrayList<String> mResult;
	private Context cxt;
	private String text;
	private LinearLayout mainlayout;

	public TimelineItemHelper(Context cxt, TimelineItem item, LinearLayout mainlayout, OnClickListener listen ){


		this.cxt = cxt;
		this.text = text;
		this.mainlayout = mainlayout;

		RelativeLayout newView0 = (RelativeLayout) View.inflate(cxt,
				R.layout.timeline_item, null);
		mainlayout.addView(newView0);

		//		TextView tv1 = (TextView) newView0.findViewById(R.id.centeredTextToShow);
		//		tv1.setText(text);

		TextView name = (TextView) newView0.findViewById(R.id.name); // name of activity
		Typeface typeface = Typeface.createFromAsset(cxt.getAssets(), "fonts/Roboto-Regular.ttf" );
		name.setTypeface(typeface);
		//		 name.setTextSize(14);
		//		 name.setTextColor(activity.getResources().getColor(R.color.activity_black));
		TextView email_address = (TextView) newView0.findViewById(R.id.address); // start time


		ImageView activity_icon = (ImageView) newView0.findViewById(R.id.activity_icon); // start time

		settimelineItemList(item, name, email_address, activity_icon);

		newView0.setClickable(true);
		newView0.setOnClickListener(listen);

	}

	private void settimelineItemList(TimelineItem item, TextView name,
			TextView address, ImageView activity_icon) {
		//TODO: Add rest of the pieces
		String activity_name = item.getmActivity_name();
		name.setText(item.getTimelineTopText());
		Typeface typeface = Typeface.createFromAsset(cxt.getAssets(), "fonts/Roboto-Regular.ttf" );
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
