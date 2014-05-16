package edu.cmu.sv.lifelogger;


import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import edu.cmu.sv.lifelogger.helpers.App;
import edu.cmu.sv.lifelogger.helpers.DefinitionHelper;
import edu.cmu.sv.mobisens_ui.R;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.view.View.OnClickListener;


public class SettingsActivity extends Activity
{
	TextView txtName ;
	TextView txtEmail ;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_settings);
		//    ActionBar actionBar = getActionBar();
		//    actionBar.setDisplayShowTitleEnabled(false);
		txtName = (TextView)findViewById(R.id.textView1);
		txtEmail = (TextView) findViewById(R.id.textView2);
		if (DefinitionHelper.currentUserName != null) {
			txtName.setText(DefinitionHelper.currentUserName);
			txtEmail.setText(DefinitionHelper.currentUserEmailID);
		}
		
		final Button btnStartStop = (Button)findViewById(R.id.btnStartStop);
		if(App.serviceStarted == false) {
			btnStartStop.setText("Start Service");
		} else {
			btnStartStop.setText("Stop Service");
		}
		btnStartStop.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// Check the service state. 
				btnStartStop.setEnabled(false);
				if(App.serviceStarted == false) {
					// The services are not running, start them 
					Intent playerservice = new Intent();
					playerservice.setAction("MyNotificationService");
					startService(playerservice);
					playerservice = new Intent();
					playerservice.setAction("MySystemSensService");
					startService(playerservice);
					playerservice = new Intent();
					playerservice.setAction("MySensorService");
					startService(playerservice);
					startService(playerservice);
					btnStartStop.setText("Stop Service");
					btnStartStop.setEnabled(true);
					App.serviceStarted = true;
					System.out.println("Here All the services started");
				} else {
					// Stop the services
					Intent playerservice = new Intent();
					playerservice.setAction("MyNotificationService");
		            stopService(playerservice);
		            playerservice = new Intent();
		            playerservice.setAction("MySystemSensService");
					stopService(playerservice);
					playerservice = new Intent();
					playerservice.setAction("MySensorService");
					stopService(playerservice);
		            System.out.println("Here All the services stopped");
		            App.serviceStarted = false;
		            btnStartStop.setEnabled(true);
					btnStartStop.setText("Start Service");
				}				
			}
		});
		
 
	}
	
	public void tosClicked(final View view){
		Intent intent1 = new Intent(this, TextReaderActivity.class);
		startActivity(intent1);
	}

	private String readTxt()
	{

		InputStream inputStream = getResources().openRawResource(R.raw.terms_service);
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

		int i;
		try
		{
			i = inputStream.read();
			while (i != -1)
			{
				byteArrayOutputStream.write(i);
				i = inputStream.read();
			}
			inputStream.close();
		} catch (IOException e)
		{
			e.printStackTrace();
		}

		return byteArrayOutputStream.toString();
	}



}
