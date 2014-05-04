package edu.cmu.sv.lifelogger;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.achartengine.renderer.SimpleSeriesRenderer;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ClipDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RoundRectShape;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import edu.cmu.sv.lifelogger.database.DashboardActivityLevelManager;
import edu.cmu.sv.lifelogger.database.DashboardManager;
import edu.cmu.sv.lifelogger.entities.ActivityItem;
import edu.cmu.sv.lifelogger.helpers.DefinitionHelper;
import edu.cmu.sv.mobisens_ui.R;

public class BarChartActivityLevel extends Activity {

	/*Activity Summary Objects*/
	private LinearLayout activitySummaryLayout;
	ListView activitySummaryListView;

	ArrayList<ActivityItem> dataList  = DashboardManager.getAllPieChartData();
	int[] activitySummarySeriesPoints = getSeriesPoints();
	int[][] activitySummarySeriesDataset = DashboardActivityLevelManager.getSeriesDataset();
	int[] activitySummaryActivityArray = activitySummarySeriesDataset[0];
	
	public int[] getSeriesPoints () {
		int seriesLength = dataList.size() ;
		int x[] = new int [seriesLength];		
		for (int i = 0; i < x.length; i++) {
			x[i] = i;
		}
		return x;
	}
	
	/*Activity Level Objects*/
	private LinearLayout activityLevelLayout;
	HorizontalListView activityLevelListView;
	
	int[] seriesPoints = DashboardActivityLevelManager.getSeriesPoints();
	int[][] seriesDataset = DashboardActivityLevelManager.getSeriesDataset();
	int[] activityArray = seriesDataset[0];

	// Array to support 11 different activities, including null activity(0)
	int colorArray[] = { Color.TRANSPARENT, Color.rgb(71,60,139), //purple
			Color.rgb(238,201,0),  //gold
			Color.rgb(238, 118,0), //orange
			Color.RED,
			Color.rgb(0,139,69), //green
			Color.BLUE,
			Color.DKGRAY,
			Color.rgb(0,104,139), //blue
			Color.YELLOW };

	static int colCount = 0;

	public void onCreate(Bundle savedInstance) {

		super.onCreate(savedInstance);
		
		ActionBar actionBar = getActionBar();

		actionBar.setDisplayShowTitleEnabled(true);
		
		setContentView(R.layout.activity_bar_chart_activity_level);
		activityLevelLayout = (LinearLayout) findViewById(R.id.linearlay);
		
		activitySummaryListView = (ListView) findViewById(R.id.activitySummaryBarChart);
		activitySummaryListView.setDividerHeight(0);
		activityLevelListView = (HorizontalListView) findViewById(R.id.listview);

		final Button button1 = (Button) findViewById(R.id.button1);
		button1.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {

				// Perform action on click
				Intent activityChangeIntent = new Intent(
						BarChartActivityLevel.this,
						PieChartBuilderActivity.class);

				// currentContext.startActivity(activityChangeIntent);

				BarChartActivityLevel.this.startActivity(activityChangeIntent);
			}
		});

	}

	public class bsAdapter extends BaseAdapter {
		Activity cntx;
		int[] array;

		public bsAdapter(Activity context, int[] arr) {
			// TODO Auto-generated constructor stub
			this.cntx = context;
			this.array = arr;

		}

		public int getCount() {
			// TODO Auto-generated method stub
			return array.length;
		}

		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return array[position];
		}

		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return array.length;
		}

		public View getView(final int position, View convertView,
				ViewGroup parent) {
			View row = null;

			LayoutInflater inflater = cntx.getLayoutInflater();
			row = inflater.inflate(R.layout.simplerow, null);

			DecimalFormat df = new DecimalFormat("#.##");
			TextView tvcol2 = (TextView) row.findViewById(R.id.colortext02);

			tvcol2.setHeight(500);
			tvcol2.setWidth(7);
			
			// Set the color of the bar as per the activity number, which corresponds to the color array
			tvcol2.setBackgroundColor(colorArray[activityArray[position]]);

			tvcol2.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
				}
			});

			return row;
		}
	}

	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		// TODO Auto-generated method stub
		super.onWindowFocusChanged(hasFocus);
		updateSizeInfo();
	}

	private void updateSizeInfo() {
		/**
		 * This is onWindowFocusChanged method is used to allow the chart to
		 * update the chart according to the orientation. Here h is the integer
		 * value which can be updated with the orientation
		 */
		int h;
		if (getResources().getConfiguration().orientation == 1) {
			h = (int) (activityLevelLayout.getWidth() * 0.01);
			if (h == 0) {
				h = 30;
			}
		} else {
			h = (int) (activityLevelLayout.getWidth() * 0.01);
			if (h == 0) {
				h = 20;
			}
		}
		activityLevelListView.setAdapter(new bsAdapter(this, seriesPoints));
		activitySummaryListView.setAdapter(new activitySummaryAdapter(this, activitySummarySeriesPoints));
	}

	
	
	/*Class for handling drawing of horizontal bar chart for Summary*/
	
	public class activitySummaryAdapter extends BaseAdapter {
		Activity cntx;
		int[] array;

		public activitySummaryAdapter(Activity context, int[] arr) {
			// TODO Auto-generated constructor stub
			this.cntx = context;
			this.array = arr;

		}

		public int getCount() {
			// TODO Auto-generated method stub
			return array.length;
		}

		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return array[position];
		}

		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return array.length;
		}

		public View getView(final int position, View convertView,
				ViewGroup parent) {
			View row = null;

			LayoutInflater inflater = cntx.getLayoutInflater();
			row = inflater.inflate(R.layout.simple_row_with_text, null);

			DecimalFormat df = new DecimalFormat("#.##");
			TextView txtActvityName = (TextView) row.findViewById(R.id.label);
			ImageView imgActivityIcon =  (ImageView)row.findViewById(R.id.imageView1);
			TextView tvcol2 = (TextView) row.findViewById(R.id.colortext02);
			/*ProgressBar pBar = (ProgressBar) row.findViewById(R.id.progressBar1);
			
		    // Define a shape with rounded corners
		    final float[] roundedCorners = new float[] { 5, 5, 5, 5, 5, 5, 5, 5 };
		    ShapeDrawable pgDrawable = new ShapeDrawable(new RoundRectShape(roundedCorners,     null, null));

		    // Sets the progressBar color
		    pgDrawable.getPaint().setColor(colorArray[position + 1]);
		    //pgDrawable.getPaint().setColor(Color.parseColor(color));

		    // Adds the drawable to your progressBar
		    ClipDrawable progress = new ClipDrawable(pgDrawable, Gravity.LEFT, ClipDrawable.HORIZONTAL);
		    pBar.setProgressDrawable(progress);

		    // Sets a background to have the 3D effect
		    pBar.setBackgroundDrawable(getResources()
		            .getDrawable(android.R.drawable.progress_horizontal));
		    pgDrawable.getPaint().setColor(colorArray[position + 1]);
			pBar.setProgress(dataList.get(position).getmPercentage());
		            */

		    
		    /*Set the activity Name */
		    txtActvityName.setText(dataList.get(position).getmActivity_name().toString());
		    
		    
		    /*Set the activity Icon */
		    //The hack to set the activity icon. No need to supply a separate icon name :)
		    String activity_name = dataList.get(position).getmActivity_name();
		    
		    if (activity_name.toString().equalsIgnoreCase("driving")){
		    	imgActivityIcon.setImageResource(R.drawable.driving);
			}
			else if(activity_name.toString().equalsIgnoreCase("dining")){
				imgActivityIcon.setImageResource(R.drawable.dining);
			}
			else if (activity_name.toString().equalsIgnoreCase("working")){
				imgActivityIcon.setImageResource(R.drawable.working);
			}
			else if (activity_name.toString().equalsIgnoreCase("walking")){
				imgActivityIcon.setImageResource(R.drawable.walking);
			}
			else if (activity_name.toString().equalsIgnoreCase("sleeping")){
				imgActivityIcon.setImageResource(R.drawable.sleeping);
			}
			else if (activity_name.toString().equalsIgnoreCase("shopping")){
				imgActivityIcon.setImageResource(R.drawable.shopping);
			}
			else{
				imgActivityIcon.setImageResource(R.drawable.unknown);
			}
			
			tvcol2.setHeight(20);

			
			
			/*Take the dataList activity item array list and set the widht according to it*/
			if(position >= 7)
				return row;
			//tvcol2.setWidth(dataList.get(position).getmPercentage()*2);
			tvcol2.setWidth(calculateWidthOfBar(dataList.get(position).getmPercentage()));
			
			//Compensate for color array start from 1 for activities(using 0 for no activity)
			tvcol2.setBackgroundColor(colorArray[position + 1]);

		/*	tvcol2.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
				}
			});*/

			return row;
		}
		
		public int calculateWidthOfBar(double percentage){
			/* Find the maximum value of all the percentages for the activities */
			double maxVal = 0;
			for (ActivityItem a: dataList){
				if(a.getmPercentage() > maxVal)
					maxVal = a.getmPercentage();
			}
			
			/* Extrapolate the percentage to fit maximum width */
			
			
			return (int) ((int) 200*percentage/maxVal) ;
			
			
		}
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
			Intent intent = new Intent(this, TimelineActivity.class);
			startActivity(intent);
		} else if (item.getItemId() == R.id.profile)
		{
			Intent intent = new Intent(this, ProfileActivity.class);
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
