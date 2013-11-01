package edu.cmu.sv.lifelogger;

import java.util.ArrayList;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import edu.cmu.sv.lifelogger.database.TimelineManager;
import edu.cmu.sv.lifelogger.entities.TimelineItem;
import edu.cmu.sv.lifelogger.entities.TimelineSegment;
import edu.cmu.sv.lifelogger.helpers.TimelineItemHelper;
import edu.cmu.sv.lifelogger.helpers.TimelineSegmentHeader;
import edu.cmu.sv.mobisens_ui.R;



public class TimelineTestActivity extends Activity{
	private static LinearLayout MY_MAIN_LAYOUT;
	private ArrayList<TimelineSegment> timelineItemList;
	Context cxt;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		cxt=this;

		setContentView(R.layout.main_vert_lin_layout);
		MY_MAIN_LAYOUT = (LinearLayout) findViewById(R.id.mainLayout);

		timelineItemList = TimelineManager.getAllTimelineItems();

		for (TimelineSegment tls: timelineItemList){
			//add timeline segment
			TimelineSegmentHeader tsh = new TimelineSegmentHeader(this, tls.getDate().toString(), MY_MAIN_LAYOUT);

			ArrayList<TimelineItem> tlItems= tls.getData();
			for (TimelineItem item: tlItems){
				//add its items
				TimelineItemHelper tmh = new TimelineItemHelper(this, item, MY_MAIN_LAYOUT, itemListener);

			}

		}
		//		TextViewHelper ctvp = new TextViewHelper(this, "BLAHBLAH", MY_MAIN_LAYOUT);



	}


	View.OnClickListener itemListener = new View.OnClickListener() {
		public void onClick(View view) {

			TextView t = (TextView) view.findViewById(R.id.name);
			String txt = t.getText().toString();
			

			TextView bottom = (TextView) view.findViewById(R.id.bottomTxt);
			String bottomtxt = bottom.getText().toString();

			System.out.println("printing name: " + bottomtxt + ", " + txt);
			
			Toast.makeText(TimelineTestActivity.this, bottomtxt + " " + txt, Toast.LENGTH_SHORT).show();

			

			Intent intent = new Intent(TimelineTestActivity.this,TagActivity.class);
			intent.putExtra("top_txt", txt);
			intent.putExtra("bottom_txt", bottomtxt);
			
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
}
