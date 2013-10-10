package edu.cmu.sv.lifelogger.helpers;

import android.content.Context;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import edu.cmu.sv.mobisens_ui.R;

public class TimelineSegmentHeader {
	private Context cxt;
	private String text;
	private LinearLayout mainlayout;

	public TimelineSegmentHeader(Context cxt, String text, LinearLayout mainlayout ){
		this.cxt = cxt;
		this.text = text;
		this.mainlayout = mainlayout;

		RelativeLayout newView0 = (RelativeLayout) View.inflate(cxt,
				R.layout.timeline_segment_header, null);
		mainlayout.addView(newView0);   

		TextView tv1 = (TextView) newView0.findViewById(R.id.centeredTextToShow);
		tv1.setText(text);

	}

}
