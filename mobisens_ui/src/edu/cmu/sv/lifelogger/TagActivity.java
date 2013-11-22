package edu.cmu.sv.lifelogger;

import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.util.ArrayList;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.NavUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.Gallery;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import com.google.android.gms.maps.model.LatLng;
import com.nostra13.universalimageloader.cache.memory.impl.WeakMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;

import edu.cmu.sv.lifelogger.database.ActivityLocationManager;
import edu.cmu.sv.mobisens_ui.R;


public class TagActivity extends Activity{
	String topTxt;
	String bottomTxt;

	ImageView mImageView ;
	Bitmap bm;

	GridView gridGallery;
	Handler handler;
	GalleryAdapter adapter;

	ImageView imgSinglePick;
	Button btnGalleryPickMul;

	String action;
	ViewSwitcher viewSwitcher;
	ImageLoader imageLoader;

	public static int zoomlvl = 8;

	//YOU CAN EDIT THIS TO WHATEVER YOU WANT
	private static final int SELECT_PICTURE = 1;

	private String selectedImagePath;
	//ADDED
	private String filemanagerstring;

	public static ArrayList<CustomGallery> photos = null;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		ActionBar actionBar = getActionBar();

		actionBar.setDisplayShowTitleEnabled(false);
		actionBar.setDisplayHomeAsUpEnabled(true);
		 getActionBar().setDisplayHomeAsUpEnabled(true);
		 
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
		    getActionBar().setDisplayHomeAsUpEnabled(true);
		}
		
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

		Gallery photoGallery = (Gallery) findViewById(R.id.photoGallery);

		if (photos!=null){
			photoGallery.setAdapter(new ImageAdapter(this));  
			photoGallery.setFadingEdgeLength(40);  
		}
		photoGallery.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				//TimelineActivity.db.close();
				Intent intent = new Intent(TagActivity.this, FacebookShare.class);
				// TODO Currently hardcoded activity id. Get it dynamically from current activity id
				intent.putExtra("activityID", 2);
				startActivity(intent);
				
			}
		});
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
					//"&zoom="+zoomlvl +
					"&size=350x200" +
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
		getMenuInflater().inflate(R.menu.tag_bar, menu);

		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		
		 switch (item.getItemId()) {
		    // Respond to the action bar's Up/Home button
		    case android.R.id.home:
		        NavUtils.navigateUpFromSameTask(this);
		        return true; 
		        
		    case R.id.attachpics:
		    	Intent intent = new Intent(this, OpenGalleryActivity.class);
				startActivity(intent);
				 return true; 
		    case R.id.profile:
		    	Intent intent1 = new Intent(this, PieChartBuilderActivity.class);
				startActivity(intent1);
				 return true; 
		    case R.id.fbshare:
		    	Intent intent2 = new Intent(this, FacebookShare.class);
				startActivity(intent2);
				 return true; 
		    }
		    return super.onOptionsItemSelected(item);
	  
		
	}



	private void initImageLoader() {
		DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder()
		.cacheOnDisc().imageScaleType(ImageScaleType.EXACTLY_STRETCHED)
		.bitmapConfig(Bitmap.Config.RGB_565).build();
		ImageLoaderConfiguration.Builder builder = new ImageLoaderConfiguration.Builder(
				this).defaultDisplayImageOptions(defaultOptions).memoryCache(
						new WeakMemoryCache());

		ImageLoaderConfiguration config = builder.build();
		imageLoader = ImageLoader.getInstance();
		imageLoader.init(config);
	}

	private void init() {
		
		LinearLayout photo_dialog = (LinearLayout) View.inflate(this, R.layout.description_page, null);


		handler = new Handler();
		gridGallery = (GridView) photo_dialog.findViewById(R.id.gridGallery);
		gridGallery.setFastScrollEnabled(true);
		adapter = new GalleryAdapter(getApplicationContext(), imageLoader);
		adapter.setMultiplePick(false);
		gridGallery.setAdapter(adapter);

		viewSwitcher = (ViewSwitcher) photo_dialog.findViewById(R.id.viewSwitcher);
		viewSwitcher.setDisplayedChild(1);

		imgSinglePick = (ImageView) photo_dialog.findViewById(R.id.imgSinglePick);

		Intent i = new Intent(Action.ACTION_MULTIPLE_PICK);
		startActivityForResult(i, 200);


	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (requestCode == 100 && resultCode == Activity.RESULT_OK) {
			adapter.clear();

			viewSwitcher.setDisplayedChild(1);
			String single_path = data.getStringExtra("single_path");
			imageLoader.displayImage("file://" + single_path, imgSinglePick);

		} else if (requestCode == 200 && resultCode == Activity.RESULT_OK) {
			String[] all_path = data.getStringArrayExtra("all_path");

			ArrayList<CustomGallery> dataT = new ArrayList<CustomGallery>();

			for (String string : all_path) {
				CustomGallery item = new CustomGallery();
				item.sdcardPath = string;
				TimelineTestActivity.db.createImageRow("", item.sdcardPath, 2);
				dataT.add(item);
			}

			viewSwitcher.setDisplayedChild(0);
			adapter.addAll(dataT);
		}
	}

	public static void setPhotos(ArrayList<CustomGallery> dataT) {
		TagActivity.photos = dataT;
	}


	public class ImageAdapter extends BaseAdapter{  

		int mGalleryItemBackground;  
		public ImageAdapter(Context c)  {     
			mContext = c;     
		}  
		public int getCount(){  
			return photos.size();  
		}  
		public Object getItem(int position){  
			return position;  
		}  
		public long getItemId(int position) {  
			return position;  
		}  
		public View getView(int position, View convertView, ViewGroup parent){  
			ImageView i = new ImageView(mContext);  

			
			i.setImageURI(Uri.parse("file://" + photos.get(position).sdcardPath));  
			i.setScaleType(ImageView.ScaleType.FIT_XY);  
			i.setLayoutParams(new Gallery.LayoutParams(260, 210));  
			return i;  
		}     
		private Context mContext;  
	}     

}
