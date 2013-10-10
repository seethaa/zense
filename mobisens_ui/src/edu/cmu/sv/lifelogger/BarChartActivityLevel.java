package edu.cmu.sv.lifelogger;

import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import edu.cmu.sv.lifelogger.database.DashboardActivityLevelManager;
import edu.cmu.sv.mobisens_ui.R;

public class BarChartActivityLevel extends Activity {
	private LinearLayout lay;
	HorizontalListView listview;
	private double highest;
	private int[] grossheight;
	private int[] netheight;

	int[] x = DashboardActivityLevelManager.getSeriesPoints();
	int[][] seriesDataset = DashboardActivityLevelManager.getSeriesDataset();
	int[] activityArray = seriesDataset[0];

	// Array to support 11 different activities, including null activity(0)
	int colorArray[] = { Color.TRANSPARENT, Color.RED, Color.BLUE, Color.GREEN,
			Color.CYAN, Color.DKGRAY, Color.GRAY, Color.MAGENTA, Color.YELLOW,
			Color.LTGRAY, Color.BLACK };

	static int colCount = 0;

	public void onCreate(Bundle savedInstance) {

		super.onCreate(savedInstance);
		setContentView(R.layout.activity_bar_chart_activity_level);
		lay = (LinearLayout) findViewById(R.id.linearlay);
		listview = (HorizontalListView) findViewById(R.id.listview);

		highest = 1;

		netheight = new int[activityArray.length];
		
		final Button button1 = (Button) findViewById(R.id.button1);
        button1.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	
                // Perform action on click   
                Intent activityChangeIntent = new Intent(BarChartActivityLevel.this, PieChartBuilderActivity.class);

                // currentContext.startActivity(activityChangeIntent);

                BarChartActivityLevel.this.startActivity(activityChangeIntent);
            }
        });

        
        final Button button2 = (Button) findViewById(R.id.button2);
        button2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	
                // Perform action on click   
                Intent activityChangeIntent = new Intent(BarChartActivityLevel.this, XYChartBuilder.class);

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
			h = (int) (lay.getWidth() * 0.01);
			if (h == 0) {
				h = 30;
			}
		} else {
			h = (int) (lay.getWidth() * 0.01);
			if (h == 0) {
				h = 20;
			}
		}
		for (int i = 0; i < activityArray.length; i++) {
			netheight[i] = (int) ((h * activityArray[i]) / highest);
		}
		listview.setAdapter(new bsAdapter(this, x));
	}

}
