package edu.cmu.sv.lifelogger;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;

import org.achartengine.model.CategorySeries;
import org.achartengine.model.MultipleCategorySeries;
import org.achartengine.model.SeriesSelection;
import org.achartengine.renderer.DefaultRenderer;
import org.achartengine.renderer.SimpleSeriesRenderer;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;
import edu.cmu.sv.lifelogger.database.DashboardManager;
import edu.cmu.sv.lifelogger.entities.ActivityItem;
import edu.cmu.sv.mobisens_ui.R;

public class PieChartBuilderActivity extends Activity {
	/** Colors to be used for the pie slices. */
	private static int[] COLORS = new int[] { Color.GREEN, Color.BLUE,
		Color.MAGENTA, Color.CYAN };
	/** The main series that will include all the data. */
	private CategorySeries mSeries = new CategorySeries("");
	/** The main renderer for the main dataset. */
	private DefaultRenderer mRenderer = new DefaultRenderer();
	/** Button for adding entered data to the current series. */
	/** Edit text field for entering the slice value. */
	private EditText mValue;
	/** The chart view that displays the data. */
	private GraphicalView mChartView;

	static int isFirstRun = 0;

	@Override
	protected void onRestoreInstanceState(Bundle savedState) {
		super.onRestoreInstanceState(savedState);
		ActionBar actionBar = getActionBar();

		actionBar.setDisplayShowTitleEnabled(true);

		mSeries = (CategorySeries) savedState.getSerializable("current_series");
		mRenderer = (DefaultRenderer) savedState
				.getSerializable("current_renderer");
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putSerializable("current_series", mSeries);
		outState.putSerializable("current_renderer", mRenderer);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.xy_chart);
		// Set the running count of the chart as first time
		isFirstRun = 0;
		
		mRenderer.setZoomButtonsVisible(true);
		mRenderer.setStartAngle(180);
		mRenderer.setDisplayValues(false);

		mRenderer.setBackgroundColor(Color.WHITE);
		mRenderer.setLabelsTextSize(20);
		mRenderer.setLabelsColor(getResources().getColor(R.color.black));
		mRenderer.setLegendTextSize(20);

		

		final Button button = (Button) findViewById(R.id.button1);
		button.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {

				// Perform action on click
				Intent activityChangeIntent = new Intent(
						PieChartBuilderActivity.this,
						BarChartActivityLevel.class);

				// currentContext.startActivity(activityChangeIntent);

				PieChartBuilderActivity.this
				.startActivity(activityChangeIntent);
			}
		});
	}

	public void createPieChart() {
		ArrayList<ActivityItem> dataList = new ArrayList<ActivityItem>();
		dataList = DashboardManager.getAllPieChartData();
		double value = 0;
		int i = 0;
		isFirstRun++;
		for (ActivityItem a : dataList) {

			//value = (double) a.getmTime();
			value  = (double) a.getmPercentage();
			mSeries.add(a.getmActivity_name() + " " + value + "%", value);
			SimpleSeriesRenderer renderer = new SimpleSeriesRenderer();
			renderer.setColor(COLORS[(mSeries.getItemCount() - 1)
			                         % COLORS.length]);
			mRenderer.addSeriesRenderer(renderer);
			mChartView.repaint();
		}
	}

	
	
	@Override
	public void onPause() {
	    super.onPause();  // Always call the superclass method first

	    // Release the Camera because we don't need it when paused
	    // and other activities might need to use it.
	    
	    //mRenderer.removeAllRenderers();
	}
	
	
	
	@Override
	protected void onResume() {
		super.onResume();

		if (mChartView == null) {
			LinearLayout layout = (LinearLayout) findViewById(R.id.chart);
			mChartView = ChartFactory.getPieChartView(this, mSeries, mRenderer);
			// getPieChartView(this, mSeries, mRenderer);
			mRenderer.setClickEnabled(true);
			mChartView.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					SeriesSelection seriesSelection = mChartView
							.getCurrentSeriesAndPoint();
					if (seriesSelection == null) {
						/*Toast.makeText(PieChartBuilderActivity.this,
								"No chart element selected", Toast.LENGTH_SHORT)
								.show();*/
					} else {
						mChartView.repaint();
						/*Toast.makeText(
								PieChartBuilderActivity.this,
								"You clicked on Activity " 
										+ mChartView.getc
										+ " with % value as ="
										+ seriesSelection.getValue(),
										Toast.LENGTH_SHORT).show();*/
					}
				}
			});
			layout.addView(mChartView, new LayoutParams(
					LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
		} else {
			mChartView.repaint();
		}
		if(isFirstRun==0)	
			createPieChart();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		// getMenuInflater().inflate(R.menu.login, menu);
		getMenuInflater().inflate(R.menu.action_bar, menu);

		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == R.id.timeline) {
			Intent intent = new Intent(this, TimelineTestActivity.class);
			startActivity(intent);
		} else if (item.getItemId() == R.id.profile) {
			Intent intent = new Intent(this, PieChartBuilderActivity.class);
			startActivity(intent);
		} else if (item.getItemId() == R.id.settings) {
			Intent intent = new Intent(this, SettingsActivity.class);
			startActivity(intent);
		}

		// TODO: Add Settings activity piece
		// TODO: CHoose correct drawables in action_bar in res/menu
		return true;
	}

}
