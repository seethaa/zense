package edu.cmu.sv.mobisens;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;

public class LifeStreamActivity extends Activity {
	private TextView txtUsername = null;
	private TextView txtPassword = null;
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    switch (item.getItemId()) {
	        case android.R.id.home:
	            // app icon in action bar clicked; go home
	            this.onBackPressed();
	            return true;
	        default:
	            return super.onOptionsItemSelected(item);
	    }
	}
	
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.life_stream);
		
		ActionBar actionBar = getActionBar();
		if(actionBar != null){
			actionBar.setDisplayHomeAsUpEnabled(true);
			actionBar.setDisplayShowHomeEnabled(true);
		}
		
		this.txtUsername = (TextView) this.findViewById(R.id.txtUserName);
		this.txtPassword = (TextView) this.findViewById(R.id.txtPassword);
		
		
		String deviceID = MobiSensService.getDeviceID(this);
		this.txtUsername.setText(deviceID);
		this.txtPassword.setText(deviceID);
	}
}
