package edu.cmu.sv.lifelogger;

import java.io.File;
import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.ViewSwitcher;

import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiscCache;
import com.nostra13.universalimageloader.cache.memory.impl.WeakMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;

import edu.cmu.sv.mobisens_ui.R;

public class OpenGalleryActivity extends Activity {

	GridView gridGallery;
	Handler handler;
	GalleryAdapter adapter;

	ImageView imgSinglePick;
	Button btnGalleryPickMul;

	String action;
	ViewSwitcher viewSwitcher;
	ImageLoader imageLoader;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.photo_main);

        initImageLoader();
        init();
//        Intent i = new Intent(Action.ACTION_MULTIPLE_PICK);
//		startActivityForResult(i, 200);
        
	}

	private void initImageLoader() {
		DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder()
				.cacheOnDisc().imageScaleType(ImageScaleType.EXACTLY_STRETCHED)
				.bitmapConfig(Bitmap.Config.RGB_565).build();
		ImageLoaderConfiguration.Builder builder = new ImageLoaderConfiguration.Builder(
				this).defaultDisplayImageOptions(defaultOptions).memoryCache(
				new WeakMemoryCache());
		
		File cacheDir = new File(this.getCacheDir(), "imgcachedir");
	    if (!cacheDir.exists())
	        cacheDir.mkdir();
		
		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
	            this).threadPoolSize(5)
	            .threadPriority(Thread.MIN_PRIORITY + 3)
	            .denyCacheImageMultipleSizesInMemory()
	            // .memoryCache(new UsingFreqLimitedMemoryCache(2000000)) // You
	            // can pass your own memory cache implementation
	            .memoryCacheSize(1048576 * 10)
	            // 1MB=1048576 *declare 20 or more size if images are more than
	            // 200
	            .discCache(new UnlimitedDiscCache(cacheDir))
	            // You can pass your own disc cache implementation
	            // .defaultDisplayImageOptions(DisplayImageOptions.createSimple())
	            .build();
		
		
		//ImageLoaderConfiguration config = builder.build();
		imageLoader = ImageLoader.getInstance();
		imageLoader.init(config);
	}

	private void init() {

		handler = new Handler();
		
		ScrollView descPage = (ScrollView) View.inflate(this, R.layout.description_page, null);
				
	//	gridGallery = (GridView) descPage.findViewById(R.id.gridGallery);
		gridGallery = (GridView) findViewById(R.id.gridGallery);

		
		gridGallery.setFastScrollEnabled(true);
		adapter = new GalleryAdapter(getApplicationContext(), imageLoader);
		adapter.setMultiplePick(false);
		gridGallery.setAdapter(adapter);

		viewSwitcher = (ViewSwitcher) findViewById(R.id.viewSwitcher);
		viewSwitcher.setDisplayedChild(1);

		imgSinglePick = (ImageView) findViewById(R.id.imgSinglePick);

	
		Intent i = new Intent(Action.ACTION_MULTIPLE_PICK);
		startActivityForResult(i, 200);
//		
//		btnGalleryPickMul = (Button) findViewById(R.id.btnGalleryPickMul);
//		btnGalleryPickMul.setOnClickListener(new View.OnClickListener() {
//
//			@Override
//			public void onClick(View v) {
//				Intent i = new Intent(Action.ACTION_MULTIPLE_PICK);
//				startActivityForResult(i, 200);
//			}
//		});

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
			
			TagActivity.setPhotos(dataT);
		}
	}
}
