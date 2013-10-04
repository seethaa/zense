package edu.cmu.sv.mobisens_ui;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;

public class VerticalBarChart extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_vertical_bar_chart);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.vertical_bar_chart, menu);
		return true;
	}

}
