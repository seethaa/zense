package edu.cmu.sv.lifelogger;

import java.io.IOException;
import java.util.List;

import org.mobisens.chartview.chart.plot.PiePlot;
import org.mobisens.chartview.data.general.PieDataset;


import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Menu;
import android.view.MenuItem;

import edu.cmu.sv.mobisens_ui.R;


public class DashboardActivity extends Activity {

    

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ActionBar actionBar = getActionBar();

		actionBar.setDisplayShowTitleEnabled(true);
		setContentView(R.layout.pie_chart);

		//TODO: Change to appropriate xml layout
		//Set dataset: item and value
		
	/*	try {
	//		List<ActivitySummary> ActivitySummaryList = PercentageDataset.FetchPercentageDatat("1111", "1", "2");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
*/				PieDataset dataset = new PieDataset();

				dataset.setValue("driving", 20.0);
				dataset.setValue("work", 20.0);
				dataset.setValue("shop", 20.0);
				dataset.setValue("other", 40.0);
			
				
//				RelativeLayout relative = (RelativeLayout)findViewById(R.id.relative);
//				relative.addView(new PiePlot(this, dataset));
				
//				RingPlot ringchart = (RingPlot)findViewById(R.id.test_chart);
//				ringchart.setDataset(dataset);		
				
				// Draw the ring chart
				PiePlot ringchart = (PiePlot)findViewById(R.id.test_chart);
				ringchart.setDataset(dataset);
				// Draw piechart or ringchat
				ringchart.setisRingChart(true);


		/*
				WindowManager winManager = (WindowManager)getSystemService(Context.WINDOW_SERVICE);
				@SuppressWarnings("deprecation")
				int winWidth = winManager.getDefaultDisplay().getWidth();
				int winHeight = winManager.getDefaultDisplay().getHeight();

				Log.d(TAG, "widht = " +winWidth+ "height" +winHeight);
		 */

		/*		For automatically refreshing		*/

		//				mHandler.sendEmptyMessageDelayed(1, 3000);

	}


	private Handler mHandler = new Handler(){
		public void handleMessage(Message msg){
			PiePlot ringchart = (PiePlot)findViewById(R.id.test_chart);
			PieDataset dataset = new PieDataset();
			dataset.setValue("home", 10.0);
			dataset.setValue("work", 20.0);
			dataset.setValue("shop", 30.0);
			dataset.setValue("other", 40.0);
			dataset.setValue("new", 40.0);
			ringchart.setDataset(dataset);
		}
	};



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