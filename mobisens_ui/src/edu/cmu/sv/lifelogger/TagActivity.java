package edu.cmu.sv.lifelogger;

import android.app.Activity;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import edu.cmu.sv.lifelogger.entities.TimelineItem;
import edu.cmu.sv.lifelogger.helpers.TimelineItemHelper;
import edu.cmu.sv.mobisens_ui.R;


public class TagActivity extends Activity{
	String topTxt;
	String bottomTxt;
	


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.tag_layout);
		
		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			topTxt = extras.getString("top_txt");
			bottomTxt = extras.getString("bottom_txt");
		}
		
		LinearLayout main_layout = (LinearLayout) findViewById(R.id.mainLayout);
		
		
		Toast.makeText(TagActivity.this, bottomTxt + " " + topTxt, Toast.LENGTH_SHORT).show();

//		TimelineItem i = new TimelineItem(topTxt, bottomTxt);
		
//		TimelineItemHelper tmh = new TimelineItemHelper(this, i, main_layout, null);

		RelativeLayout newView0 = (RelativeLayout) View.inflate(this,
				R.layout.timeline_item, null);
		main_layout.addView(newView0);


		TextView name = (TextView) newView0.findViewById(R.id.name); // name of activity
		name.setText(topTxt);
		TextView bottom_txt = (TextView) newView0.findViewById(R.id.bottomTxt); // start time
		bottom_txt.setText(bottomTxt);

		ImageView activity_icon = (ImageView) newView0.findViewById(R.id.activity_icon); // start time

		
		

		
	}

}
