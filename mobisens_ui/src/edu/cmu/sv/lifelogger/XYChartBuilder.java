/**
 * Copyright (C) 2009 - 2013 SC 4ViewSoft SRL
 *  
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *  
 *      http://www.apache.org/licenses/LICENSE-2.0
 *  
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


package edu.cmu.sv.lifelogger;


import java.util.ArrayList;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.chart.PointStyle;

import org.achartengine.model.SeriesSelection;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.SimpleSeriesRenderer;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

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
import edu.cmu.sv.lifelogger.entities.MoodItem;
import edu.cmu.sv.mobisens_ui.R;

public class XYChartBuilder extends Activity {
  /** The main dataset that includes all the series that go into a chart. */
  private XYMultipleSeriesDataset mDataset = new XYMultipleSeriesDataset();
  /** The main renderer that includes all the renderers customizing a chart. */
  private XYMultipleSeriesRenderer mRenderer = new XYMultipleSeriesRenderer();
  /** The most recently added series. */
  private XYSeries mCurrentSeries;
  /** The most recently created renderer, customizing the current series. */
  private XYSeriesRenderer mCurrentRenderer;
  /** Button for creating a new series of data. */
  private Button mNewSeries;
  /** Button for adding entered data to the current series. */
  private Button mAdd;
  /** Edit text field for entering the X value of the data to be added. */
  private EditText mX;
  /** Edit text field for entering the Y value of the data to be added. */
  private EditText mY;
  /** The chart view that displays the data. */
  private GraphicalView mChartView;

  @Override
  protected void onSaveInstanceState(Bundle outState) {
    super.onSaveInstanceState(outState);
    // save the current data, for instance when changing screen orientation
    outState.putSerializable("dataset", mDataset);
    outState.putSerializable("renderer", mRenderer);
    outState.putSerializable("current_series", mCurrentSeries);
    outState.putSerializable("current_renderer", mCurrentRenderer);
  }

  @Override
  protected void onRestoreInstanceState(Bundle savedState) {
    super.onRestoreInstanceState(savedState);
    // restore the current data, for instance when changing the screen
    // orientation
    mDataset = (XYMultipleSeriesDataset) savedState.getSerializable("dataset");
    mRenderer = (XYMultipleSeriesRenderer) savedState.getSerializable("renderer");
    mCurrentSeries = (XYSeries) savedState.getSerializable("current_series");
    mCurrentRenderer = (XYSeriesRenderer) savedState.getSerializable("current_renderer");
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.xy_chart);



    // set some properties on the main renderer
    mRenderer.setApplyBackgroundColor(true);
   // mRenderer.setBackgroundColor(Color.argb(100, 255, 255, 255));
    mRenderer.setBackgroundColor(Color.WHITE);
    mRenderer.setAxesColor(Color.RED);
    mRenderer.setGridColor(Color.BLUE);
    //mRenderer.setXAxisMax(24);
    mRenderer.setXAxisMin(0);
    mRenderer.setYAxisMax(2);
    mRenderer.setXAxisMin(-2);
  
    mRenderer.setAxisTitleTextSize(16);
    mRenderer.setChartTitleTextSize(20);
    mRenderer.setLabelsTextSize(15);
    mRenderer.setLegendTextSize(15);
    mRenderer.setMargins(new int[] { 20, 30, 15, 0 });
    mRenderer.setZoomButtonsVisible(true);
    mRenderer.setPointSize(5);

    // the button that handles the new series of data creation
   // mNewSeries = (Button) findViewById(R.id.new_series);
    final Button button = (Button) findViewById(R.id.button1);
    button.setOnClickListener(new View.OnClickListener() {
        public void onClick(View v) {
        	
            // Perform action on click   
            Intent activityChangeIntent = new Intent(XYChartBuilder.this, PieChartBuilderActivity.class);

            // currentContext.startActivity(activityChangeIntent);

            XYChartBuilder.this.startActivity(activityChangeIntent);
        }
    });

    
  }

  /**
   * Function to renderer the line chart after addition of every point
   * 
   * @param x
   * @param y
   */
  private void renderLine(double x, double y){
	  mCurrentSeries.add(x, y);
     // mX.setText("");
     // mY.setText("");
     // mX.requestFocus();
      // repaint the chart such as the newly added point to be visible
      mChartView.repaint();
  }
  
  
  
  @Override
  protected void onResume() {
    super.onResume();
    if (mChartView == null) {
      LinearLayout layout = (LinearLayout) findViewById(R.id.chart);
      layout.setBackgroundColor(Color.WHITE);
      mChartView = ChartFactory.getLineChartView(this, mDataset, mRenderer);
      mChartView.setBackgroundColor(Color.WHITE);
      // enable the chart click events
      mRenderer.setClickEnabled(true);
      mRenderer.setSelectableBuffer(10);
      mChartView.setOnClickListener(new View.OnClickListener() {
        public void onClick(View v) {
          // handle the click event on the chart
          SeriesSelection seriesSelection = mChartView.getCurrentSeriesAndPoint();
          if (seriesSelection == null) {
            Toast.makeText(XYChartBuilder.this, "No chart element", Toast.LENGTH_SHORT).show();
          } else {
            // display information of the clicked point
            Toast.makeText(
                XYChartBuilder.this,
                "Chart element in series index " + seriesSelection.getSeriesIndex()
                    + " data point index " + seriesSelection.getPointIndex() + " was clicked"
                    + " closest point value X=" + seriesSelection.getXValue() + ", Y="
                    + seriesSelection.getValue(), Toast.LENGTH_SHORT).show();
          }
        }
      });
      layout.addView(mChartView, new LayoutParams(LayoutParams.FILL_PARENT,
          LayoutParams.FILL_PARENT));
      boolean enabled = mDataset.getSeriesCount() > 0;
     
    } else {
      mChartView.repaint();
    }
  
  
    String seriesTitle = "Series " + (mDataset.getSeriesCount() + 1);
    // create a new series of data
    XYSeries series = new XYSeries(seriesTitle);
    mDataset.addSeries(series);
    mCurrentSeries = series;
    // create a new renderer for the new series
    XYSeriesRenderer renderer = new XYSeriesRenderer();
    mRenderer.addSeriesRenderer(renderer);
    mRenderer.setMarginsColor(Color.argb(0x00,0x01,0x01,0x01));
    //mRenderer.setBackgroundColor(Color.CYAN);
    // set some renderer properties
    renderer.setPointStyle(PointStyle.CIRCLE);
    renderer.setFillPoints(true);
    renderer.setDisplayChartValues(true);
    renderer.setDisplayChartValuesDistance(10);
    mCurrentRenderer = renderer;
    
    mChartView.repaint();
    createLineChart();
  }

  public void createLineChart(){  
	ArrayList<MoodItem> dataList  = new ArrayList<MoodItem>();
	dataList = DashboardManager.getAllLineChartData();
	double timex = 0;
	double moody = 0;
	int i =0;
	for (MoodItem a: dataList){
		timex= (double)a.getmTime();
		moody = (double)a.getmMood();
		renderLine(timex, moody);    	       
	}  
  }
  

}