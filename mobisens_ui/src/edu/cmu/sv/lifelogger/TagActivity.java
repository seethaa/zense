package edu.cmu.sv.lifelogger;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
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
		
		ActionBar actionBar = getActionBar();

		actionBar.setDisplayShowTitleEnabled(true);
		
		setContentView(R.layout.description_page);
		
		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			topTxt = extras.getString("top_txt");
			bottomTxt = extras.getString("bottom_txt");
		}
		
//		LinearLayout main_layout = (LinearLayout) findViewById(R.id.mainLayout);
		
		
		Toast.makeText(TagActivity.this, bottomTxt + " " + topTxt, Toast.LENGTH_SHORT).show();

////		TimelineItem i = new TimelineItem(topTxt, bottomTxt);
//		
////		TimelineItemHelper tmh = new TimelineItemHelper(this, i, main_layout, null);

//		RelativeLayout newView0 = (RelativeLayout) View.inflate(this,
//				R.layout.description_page, null);
//		main_layout.addView(newView0);


		TextView name = (TextView) findViewById(R.id.name); // name of activity
		name.setText(topTxt);
		TextView bottom_txt = (TextView) findViewById(R.id.bottomTxt); // start time
		bottom_txt.setText(bottomTxt);

//		ImageView activity_icon = (ImageView) findViewById(R.id.activity_icon); // start time
//		activity_icon.setVisibility(View.GONE);
		
		
 
		
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		// getMenuInflater().inflate(R.menu.login, menu);
		getMenuInflater().inflate(R.menu.action_bar, menu);

		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		if (item.getItemId() == R.id.timeline)
		{
			Intent intent = new Intent(this, TimelineTestActivity.class);
			startActivity(intent);
		} else if (item.getItemId() == R.id.profile)
		{
			Intent intent = new Intent(this, PieChartBuilderActivity.class);
			startActivity(intent);
		}else if (item.getItemId() == R.id.settings)
		{
			Intent intent = new Intent(this, GoogleMapActivity.class);
			startActivity(intent);
		}

		//TODO: Add Settings activity piece
		//TODO: CHoose correct drawables in action_bar in res/menu
		return true;
	}

}
