
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

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;
import edu.cmu.sv.lifelogger.database.DashboardManager;
import edu.cmu.sv.lifelogger.entities.ActivityItem;
import edu.cmu.sv.mobisens_ui.R;

public class PieChartBuilder extends Activity {
	/** Colors to be used for the pie slices. */
	private static int[] COLORS = new int[] { Color.GREEN, Color.BLUE, Color.MAGENTA, Color.CYAN };
	/** The main series that will include all the data. */
	private CategorySeries mSeries = new CategorySeries("");
	/** The main renderer for the main dataset. */
	private DefaultRenderer mRenderer = new DefaultRenderer();  /** Button for adding entered data to the current series. */
	/** Edit text field for entering the slice value. */
	private EditText mValue;
	/** The chart view that displays the data. */
	private GraphicalView mChartView;

	@Override
	protected void onRestoreInstanceState(Bundle savedState) {
		super.onRestoreInstanceState(savedState);
		mSeries = (CategorySeries) savedState.getSerializable("current_series");
		mRenderer = (DefaultRenderer) savedState.getSerializable("current_renderer");
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
		//mValue = (EditText) findViewById(R.id.xValue);
		mRenderer.setZoomButtonsVisible(true);
		mRenderer.setStartAngle(180);
		mRenderer.setDisplayValues(true);
		mRenderer.setBackgroundColor(Color.WHITE);
		
	    //mRenderer.setMarginsColor(Color.argb(0x00,0x01,0x01,0x01));
		//mValue.setEnabled(true);

		final Button button = (Button) findViewById(R.id.button1);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	
                // Perform action on click   
                Intent activityChangeIntent = new Intent(PieChartBuilder.this, BarChartActivityLevel.class);

                // currentContext.startActivity(activityChangeIntent);

                PieChartBuilder.this.startActivity(activityChangeIntent);
            }
        });
	}


	public void createPieChart(){
		ArrayList<ActivityItem> dataList  = new ArrayList<ActivityItem>();
		dataList = DashboardManager.getAllPieChartData();
		double value = 0;    
		int i =0;
		for (ActivityItem a: dataList){
			value = (double)a.getmTime();
			mSeries.add(a.getmActivity_name() +"", value);
			SimpleSeriesRenderer renderer = new SimpleSeriesRenderer();
			renderer.setColor(COLORS[(mSeries.getItemCount() - 1) % COLORS.length]);
			mRenderer.addSeriesRenderer(renderer);
			mChartView.repaint();    	       
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (mChartView == null) {
			LinearLayout layout = (LinearLayout) findViewById(R.id.chart);
			mChartView = ChartFactory.getPieChartView(this, mSeries, mRenderer);
			//getPieChartView(this, mSeries, mRenderer);
			mRenderer.setClickEnabled(true);
			mChartView.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					SeriesSelection seriesSelection = mChartView.getCurrentSeriesAndPoint();
					if (seriesSelection == null) {
						Toast.makeText(PieChartBuilder.this, "No chart element selected", Toast.LENGTH_SHORT)
						.show();
					} else {
						mChartView.repaint();
						Toast.makeText(
								PieChartBuilder.this,
								"Chart data point index " + seriesSelection.getPointIndex() + " selected"
										+ " point value=" + seriesSelection.getValue(), Toast.LENGTH_SHORT).show();
					}
				}
			});
			layout.addView(mChartView, new LayoutParams(LayoutParams.FILL_PARENT,
					LayoutParams.FILL_PARENT));
		} else {
			mChartView.repaint();
		}

		createPieChart();
	}




}
