package edu.cmu.sv.lifelogger;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import edu.cmu.sv.mobisens_ui.R;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.content.res.AssetManager;
import android.text.method.ScrollingMovementMethod;
import android.widget.TextView;


public class TextReaderActivity extends Activity
{

	private TextView mTextView;
	String textType = "EULA_formal";
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_text_reader);
		//    ActionBar actionBar = getActionBar();
		//    actionBar.setDisplayShowTitleEnabled(false);
		Intent i =  getIntent();
		Bundle extras = i.getExtras();
		textType = extras.getString("textType");
		mTextView = (TextView) findViewById(R.id.reader);
		mTextView.setMovementMethod(new ScrollingMovementMethod());
		mTextView.setText(readTxt(textType));///

	}

	private String readTxt(String textType){
		BufferedReader reader = null;
		StringBuilder returnString = new StringBuilder();
		try {
			reader = new BufferedReader(
					new InputStreamReader(getAssets().open(textType)));

			// do reading, usually loop until end of file reading  
			String mLine = reader.readLine();
			while (mLine != null) {
				returnString.append(mLine);
				returnString.append("\n");
				mLine = reader.readLine(); 
			}
		} catch (IOException e) {
			//log the exception
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		return returnString.toString();
	}
	
	private String readTxt()
	{
		AssetManager assetManager = getAssets();
		InputStream inputStream = null;
		try {
			inputStream = assetManager.open("EULA_formal");
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
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
