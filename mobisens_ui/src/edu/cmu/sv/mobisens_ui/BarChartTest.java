package edu.cmu.sv.mobisens_ui;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.widget.Button;
import org.achartengine.ChartFactory;
import org.achartengine.chart.BarChart.Type;
import org.achartengine.chart.PointStyle;
import org.achartengine.model.CategorySeries;
import org.achartengine.model.MultipleCategorySeries;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;
import org.achartengine.renderer.XYMultipleSeriesRenderer.Orientation;

import edu.cmu.sv.lifelogger.database.DashboardActivityLevelManager;
 
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
 

public class BarChartTest extends Activity {
    private String[] mMonth = new String[] {
            "Jan", "Feb" , "Mar", "Apr", "May", "Jun",
            "Jul", "Aug" , "Sep", "Oct", "Nov", "Dec"
        };
     
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_bar_chart_test);
     
            // Getting reference to the button btn_chart
            Button btnChart = (Button) findViewById(R.id.btn_chart);
     
            // Defining click event listener for the button btn_chart
           // OnClickListener clickListener = new OnClickListener()
            
            btnChart.setOnClickListener( new View.OnClickListener() {
     
                @Override
                public void onClick(View v) {
                    // Draw the Income vs Expense Chart
                    openChart();
                }
            });
     
            // Setting event click listener for the button btn_chart of the MainActivity layout
           // btnChart.setOnClickListener(clickListener);
     
        }
     
        private void openChart(){
            //int[] x = { 0,1,2,3,4,5,6,7 };
            int []x = DashboardActivityLevelManager.getSeriesPoints();
            int [][]seriesDataset = DashboardActivityLevelManager.getSeriesDataset();
            int [] activity0 = seriesDataset[0];
            int [] activity1 = seriesDataset[1];
           
            
            
            //int[] income = { 2000,2500,2700,3000,2800,3500,3700,3800};
   //         int[] expense = {2200, 2700, 2900, 2800, 2600, 3000, 3300, 3400 };
     
            // Creating an  XYSeries for Income
            XYSeries activity0Series = new XYSeries("Activity 0");
            // Creating an  XYSeries for Expense
            XYSeries activity1Series = new XYSeries("Activity 1");
            // Adding data to Income and Expense Series
            for(int i=0;i<x.length;i++){
                activity0Series.add(i,activity0[i]);
                activity1Series.add(i, activity1[i]);
            }
     
            // Creating a dataset to hold each series
            XYMultipleSeriesDataset dataset = new XYMultipleSeriesDataset();
            // Adding Income Series to the dataset
            dataset.addSeries(activity0Series);
            // Adding Expense Series to dataset
          dataset.addSeries(activity1Series);
     
            // Creating XYSeriesRenderer to customize incomeSeries
            XYSeriesRenderer activity0Renderer = new XYSeriesRenderer();
            activity0Renderer.setColor(Color.rgb(130, 130, 230));
            activity0Renderer.setFillPoints(true);
            activity0Renderer.setLineWidth(2);
            activity0Renderer.setDisplayChartValues(true);
            //incomeRenderer.setChartValuesTextSize(50);
            activity0Renderer.setPointStrokeWidth(30);
            
            // Creating XYSeriesRenderer to customize expenseSeries
            XYSeriesRenderer activity1Renderer = new XYSeriesRenderer();
            activity1Renderer.setColor(Color.rgb(220, 80, 80));
            activity1Renderer.setFillPoints(true);
            activity1Renderer.setLineWidth(2);
            activity1Renderer.setDisplayChartValues(true);
     
            // Creating a XYMultipleSeriesRenderer to customize the whole chart
            XYMultipleSeriesRenderer multiRenderer = new XYMultipleSeriesRenderer();
            multiRenderer.setXLabels(0);
            multiRenderer.setChartTitle("Income vs Expense Chart");
            multiRenderer.setXTitle("Year 2012");
            multiRenderer.setYTitle("Amount in Dollars");
            multiRenderer.setZoomButtonsVisible(true);
            multiRenderer.setXLabelsColor(Color.RED);
            multiRenderer.setPointSize(60);
            multiRenderer.setBarWidth(10);
            //multiRenderer.setChartValuesTextSize(100);
            //multiRenderer.setOrientation(Orientation.VERTICAL);
            multiRenderer.setPointSize(60);
           /* for(int i=0; i< x.length;i++){
                multiRenderer.addXTextLabel(i, mMonth[i]);
                   
                //multiRenderer.setBarWidth(20);
            }
     */
            // Adding incomeRenderer and expenseRenderer to multipleRenderer
            // Note: The order of adding dataseries to dataset and renderers to multipleRenderer
            // should be same
            multiRenderer.addSeriesRenderer(activity0Renderer);
            multiRenderer.addSeriesRenderer(activity1Renderer);
     
            // Creating an intent to plot bar chart using dataset and multipleRenderer
            Intent intent = ChartFactory.getBarChartIntent(getBaseContext(), dataset, multiRenderer, Type.DEFAULT);
     
            // Start Activity
            startActivity(intent);
     
        }
     
        @Override
        public boolean onCreateOptionsMenu(Menu menu) {
            getMenuInflater().inflate(R.menu.activity_main, menu);
            return true;
        }

}
