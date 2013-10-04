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

import org.achartengine.chart.BarChart.Type;
import java.util.ArrayList;
import java.util.List;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.chart.PointStyle;

import org.achartengine.model.CategorySeries;
import org.achartengine.model.SeriesSelection;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.SimpleSeriesRenderer;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.Paint.Align;
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

public class BarChartBuilder extends Activity {
  /** The main dataset that includes all the series that go into a chart. */
  private XYMultipleSeriesDataset mDataset = new XYMultipleSeriesDataset();
  /** The main renderer that includes all the renderers customizing a chart. */
  private XYMultipleSeriesRenderer mRenderer = new XYMultipleSeriesRenderer();
  /** The most recently added series. */
  private XYSeries mCurrentSeries;
  /** The most recently created renderer, customizing the current series. */
  private XYMultipleSeriesRenderer mCurrentRenderer;
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
    mCurrentRenderer = (XYMultipleSeriesRenderer) savedState.getSerializable("current_renderer");
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.xy_chart);



    // set some properties on the main renderer
    mRenderer.setApplyBackgroundColor(true);
    mRenderer.setBackgroundColor(Color.argb(100, 255, 255, 255));
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


    
  }

  
  
  @Override
  protected void onResume() {
    super.onResume();
    if (mChartView == null) {
      LinearLayout layout = (LinearLayout) findViewById(R.id.chart);
      //mChartView = ChartFactory.getLineChartView(this, mDataset, mRenderer);
      mChartView = ChartFactory.getBarChartView(this, mDataset, mRenderer, Type.STACKED);
      // enable the chart click events
      mRenderer.setClickEnabled(true);
      mRenderer.setSelectableBuffer(10);
      mChartView.setOnClickListener(new View.OnClickListener() {
        public void onClick(View v) {
          // handle the click event on the chart
          SeriesSelection seriesSelection = mChartView.getCurrentSeriesAndPoint();
          if (seriesSelection == null) {
            Toast.makeText(BarChartBuilder.this, "No chart element", Toast.LENGTH_SHORT).show();
          } else {
            // display information of the clicked point
            Toast.makeText(
                BarChartBuilder.this,
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
    int[] colors = new int[] { Color.BLUE, Color.CYAN };
   

    XYMultipleSeriesRenderer renderer = buildBarRenderer(colors);
    setChartSettings(renderer, "Monthly sales in the last 2 years", "Month", "Units sold", 0.5,
        12.5, 0, 24000, Color.GRAY, Color.LTGRAY);
    renderer.getSeriesRendererAt(0).setDisplayChartValues(true);
    renderer.setXLabels(12);
    renderer.setYLabels(10);
    renderer.setXLabelsAlign(Align.LEFT);
    renderer.setYLabelsAlign(Align.LEFT);
    renderer.setPanEnabled(true, false);
    // renderer.setZoomEnabled(false);
    renderer.setZoomRate(1.1f);
    renderer.setBarSpacing(0.5f);
    
    mCurrentRenderer = renderer;
    
    mChartView.repaint();
    createBarChart();
  }

  /**
   * Sets a few of the series renderer settings.
   * 
   * @param renderer the renderer to set the properties to
   * @param title the chart title
   * @param xTitle the title for the X axis
   * @param yTitle the title for the Y axis
   * @param xMin the minimum value on the X axis
   * @param xMax the maximum value on the X axis
   * @param yMin the minimum value on the Y axis
   * @param yMax the maximum value on the Y axis
   * @param axesColor the axes color
   * @param labelsColor the labels color
   */
  protected void setChartSettings(XYMultipleSeriesRenderer renderer, String title, String xTitle,
      String yTitle, double xMin, double xMax, double yMin, double yMax, int axesColor,
      int labelsColor) {
    renderer.setChartTitle(title);
    renderer.setXTitle(xTitle);
    renderer.setYTitle(yTitle);
    renderer.setXAxisMin(xMin);
    renderer.setXAxisMax(xMax);
    renderer.setYAxisMin(yMin);
    renderer.setYAxisMax(yMax);
    renderer.setAxesColor(axesColor);
    renderer.setLabelsColor(labelsColor);
  }
  
  

  public void createBarChart(){  
	  String[] titles = new String[] { "2008", "2007" };
	    List<double[]> values = new ArrayList<double[]>();
	    values.add(new double[] { 14230, 12300, 14240, 15244, 15900, 19200, 22030, 21200, 19500, 15500,
	        12600, 14000 });
	    renderBarChart(values);
	    

  }
  

  /**
   * Builds a bar multiple series dataset using the provided values.
   * 
   * @param titles the series titles
   * @param values the values
   * @return the XY multiple bar dataset
   */
  protected XYMultipleSeriesDataset buildBarDataset(String titles, List<double[]> values) {
    XYMultipleSeriesDataset dataset = new XYMultipleSeriesDataset();
    int length = titles.length();

      CategorySeries series = new CategorySeries(titles);
      double[] v = values.get(0);
      int seriesLength = v.length;
      for (int k = 0; k < seriesLength; k++) {
        series.add(v[k]);
      }
      dataset.addSeries(series.toXYSeries());
    
    return dataset;
  }


  /**
   * Function to rendder the line chart after addition of every point
      * 
   * @param x
   * @param y
   */
  private void renderBarChart( List<double[]> values){
	  mDataset =  buildBarDataset("Activity Level", values);  
	  int[] colors = new int[] { Color.BLUE, Color.CYAN };
	    XYMultipleSeriesRenderer renderer = buildBarRenderer(colors);
	    setChartSettings(renderer, "Monthly sales in the last 2 years", "Month", "Units sold", 0.5,
	        12.5, 0, 24000, Color.GRAY, Color.LTGRAY);
	    renderer.getSeriesRendererAt(0).setDisplayChartValues(true);
	    renderer.setXLabels(12);
	    renderer.setYLabels(10);
	    renderer.setXLabelsAlign(Align.LEFT);
	    renderer.setYLabelsAlign(Align.LEFT);
	    renderer.setPanEnabled(true, false);
	    // renderer.setZoomEnabled(false);
	    renderer.setZoomRate(1.1f);
	    renderer.setBarSpacing(0.5f);  
	  mRenderer = renderer;
      mChartView.repaint();
  }
  
  
  
  
  
  /**
   * Builds a bar multiple series renderer to use the provided colors.
   * 
   * @param colors the series renderers colors
   * @return the bar multiple series renderer
   */
  protected XYMultipleSeriesRenderer buildBarRenderer(int[] colors) {
    XYMultipleSeriesRenderer renderer = new XYMultipleSeriesRenderer();
    renderer.setAxisTitleTextSize(16);
    renderer.setChartTitleTextSize(20);
    renderer.setLabelsTextSize(15);
    renderer.setLegendTextSize(15);
    int length = colors.length;
    for (int i = 0; i < length; i++) {
      SimpleSeriesRenderer r = new SimpleSeriesRenderer();
      r.setColor(colors[i]);
      renderer.addSeriesRenderer(r);
    }
    return renderer;
  }

  /**
   * Builds a bar multiple series dataset using the provided values.
   * 
   * @param titles the series titles
   * @param values the values
   * @return the XY multiple bar dataset
   */
  protected XYMultipleSeriesDataset buildBarDataset(String[] titles, List<double[]> values) {
    XYMultipleSeriesDataset dataset = new XYMultipleSeriesDataset();
    int length = titles.length;
    for (int i = 0; i < length; i++) {
      CategorySeries series = new CategorySeries(titles[i]);
      double[] v = values.get(i);
      int seriesLength = v.length;
      for (int k = 0; k < seriesLength; k++) {
        series.add(v[k]);
      }
      dataset.addSeries(series.toXYSeries());
    }
    return dataset;
  }

  
  
  

}