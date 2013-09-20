package edu.cmu.sv.lifelogger;

import edu.cmu.sv.mobisens_ui.R;
import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;

public class Wizard1 extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_wizard1);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.wizard1, menu);
		return true;
	}

}
