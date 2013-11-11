package edu.cmu.sv.lifelogger;

import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.util.ArrayList;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import com.google.android.gms.maps.model.LatLng;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import edu.cmu.sv.lifelogger.database.ActivityLocationManager;
import edu.cmu.sv.lifelogger.entities.TimelineItem;
import edu.cmu.sv.lifelogger.helpers.TimelineItemHelper;
import edu.cmu.sv.mobisens_ui.R;


public class TagActivity extends Activity{
	String topTxt;
	String bottomTxt;

	ImageView mImageView ;
	Bitmap bm;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		ActionBar actionBar = getActionBar();

		actionBar.setDisplayShowTitleEnabled(true);

		setContentView(R.layout.description_page);

		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			topTxt = extras.getString("top_txt");
			bottomTxt = extras.getString("bottom_txt");
		}

		//		LinearLayout main_layout = (LinearLayout) findViewById(R.id.mainLayout);


		Toast.makeText(TagActivity.this, bottomTxt + " " + topTxt, Toast.LENGTH_SHORT).show();

		////		TimelineItem i = new TimelineItem(topTxt, bottomTxt);
		//		
		////		TimelineItemHelper tmh = new TimelineItemHelper(this, i, main_layout, null);

		//		RelativeLayout newView0 = (RelativeLayout) View.inflate(this,
		//				R.layout.description_page, null);
		//		main_layout.addView(newView0);


		TextView name = (TextView) findViewById(R.id.name); // name of activity
		name.setText(topTxt);
		TextView bottom_txt = (TextView) findViewById(R.id.bottomTxt); // start time
		bottom_txt.setText(bottomTxt);

		//		ImageView activity_icon = (ImageView) findViewById(R.id.activity_icon); // start time
		//		activity_icon.setVisibility(View.GONE);

		mImageView = (ImageView) findViewById(R.id.staticIV);


		mImageView.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View view) {
				Intent intent = new Intent(TagActivity.this, GoogleMapActivity.class);
				startActivity(intent);
			}

		});

		new LongOperation().execute();



	}

	private class LongOperation extends AsyncTask<String, Void, Bitmap> {

		@Override
		protected Bitmap doInBackground(String... params) {
			//	        	String URL = "http://maps.google.com/maps/api/staticmap?center=" +latitude + "," + longitude + "&zoom=15&size=200x200&sensor=false";

			//get all points first
			ArrayList<LatLng> locations  = ActivityLocationManager.getAllLocations();
			String allpoints = getStringLocs(locations);

			String URL = "http://maps.googleapis.com/maps/api/staticmap?" +
					//	              		"center=Brooklyn+Bridge,New+York,NY" +
					"&zoom=8" +
					"&size=300x160" +
					"&maptype=roadmap" +
					"&path=" + URLEncoder.encode("color:0x0000ff|weight:5")+
					URLEncoder.encode(allpoints)+
					"&sensor=false";

			Bitmap bmp = null;
			HttpClient httpclient = new DefaultHttpClient();   
			HttpGet request = new HttpGet(URL); 

			InputStream in = null;
			try {
				in = httpclient.execute(request).getEntity().getContent();
				bmp = BitmapFactory.decodeStream(in);
				in.close();
			} catch (IllegalStateException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ClientProtocolException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return bmp;
		}

		private String getStringLocs(ArrayList<LatLng> locations) {
			String result = "";
			for (LatLng point: locations){
				result = result + "|"+ point.latitude + ","+ point.longitude;

			}
			return result;
		}

		@Override
		protected void onPostExecute(Bitmap result) {
			mImageView.setImageBitmap(result);
		}

		@Override
		protected void onPreExecute() {}

		@Override
		protected void onProgressUpdate(Void... values) {}
	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		// getMenuInflater().inflate(R.menu.login, menu);
		getMenuInflater().inflate(R.menu.action_bar, menu);

		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		if (item.getItemId() == R.id.timeline)
		{
			Intent intent = new Intent(this, TimelineTestActivity.class);
			startActivity(intent);
		} else if (item.getItemId() == R.id.profile)
		{
			Intent intent = new Intent(this, PieChartBuilderActivity.class);
			startActivity(intent);
		}else if (item.getItemId() == R.id.settings)
		{
			Intent intent = new Intent(this, GoogleMapActivity.class);
			startActivity(intent);
		}

		//TODO: Add Settings activity piece
		//TODO: CHoose correct drawables in action_bar in res/menu
		return true;
	}

}
