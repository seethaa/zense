package edu.cmu.sv.lifelogger;


import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import edu.cmu.sv.mobisens_ui.R;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.TextView;


public class SettingsActivity extends Activity
{


	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_settings);
		//    ActionBar actionBar = getActionBar();
		//    actionBar.setDisplayShowTitleEnabled(false);

	
 
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
