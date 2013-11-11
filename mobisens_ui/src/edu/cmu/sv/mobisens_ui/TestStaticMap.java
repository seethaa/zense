package edu.cmu.sv.mobisens_ui;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import com.google.android.gms.maps.model.LatLng;

import edu.cmu.sv.lifelogger.GoogleMapActivity.QueryGooglePlaces;
import edu.cmu.sv.lifelogger.database.ActivityLocationManager;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.widget.ImageView;

public class TestStaticMap extends Activity {
	ImageView img ;
	Bitmap bm;
	double latitude;
	double longitude;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_test_static_map);
		img = (ImageView)findViewById(R.id.imageView1);
		latitude = 38.418709;
		longitude = -121.057419;
		new LongOperation().execute();
		//bm = getGoogleMapThumbnail(38.418709, -121.057419);
		//img.setImageBitmap(bm);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.test_static_map, menu);
		return true;
	}
	
	


    private class LongOperation extends AsyncTask<String, Void, Bitmap> {

        @Override
        protected Bitmap doInBackground(String... params) {
//        	String URL = "http://maps.google.com/maps/api/staticmap?center=" +latitude + "," + longitude + "&zoom=15&size=200x200&sensor=false";
        	
        	//get all points first
            ArrayList<LatLng> locations  = ActivityLocationManager.getAllLocations();
            String allpoints = getStringLocs(locations);
        	/*
        	String URL = "http://maps.googleapis.com/maps/api/staticmap?" +
//              		"center=Brooklyn+Bridge,New+York,NY" +
              		"&zoom=8" +
              		"&size=200x200" +
              		"&maptype=roadmap" +
              		"&path=color:0x0000ff|weight:5" + //add string here
              		allpoints+
              		"&sensor=false";*/
            
            String URL = "http://maps.googleapis.com/maps/api/staticmap?" +
            		"center=Brooklyn+Bridge,New+York,NY" +
            		"&zoom=13" +
            		"&size=600x300" +
            		"&maptype=roadmap" +
            		"&path=color:0x0000ff|weight:5" +
            		"|40.737102,-73.990318|40.749825,-73.987963|40.752946,-73.987384|40.755823,-73.986397" +
            		"&sensor=false";
//        	System.out.println(URL);
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
            img.setImageBitmap(result);
        }

        @Override
        protected void onPreExecute() {}

        @Override
        protected void onProgressUpdate(Void... values) {}
    }

	
	/*public static Bitmap getGoogleMapThumbnail(double lati, double longi){
//        String URL = "http://maps.google.com/maps/api/staticmap?center=" +lati + "," + longi + "&zoom=15&size=200x200&sensor=false";
        String URL = "http://maps.googleapis.com/maps/api/staticmap?" +
        		"center=Brooklyn+Bridge,New+York,NY" +
        		"&zoom=13" +
        		"&size=600x300" +
        		"&maptype=roadmap" +
        		"&path=color:0x0000ff|weight:5" +
        		"|40.737102,-73.990318|40.749825,-73.987963|40.752946,-73.987384|40.755823,-73.986397" +
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
    } */
}
