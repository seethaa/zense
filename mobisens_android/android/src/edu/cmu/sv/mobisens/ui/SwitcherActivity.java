package edu.cmu.sv.mobisens.ui;

import java.util.ArrayList;


import edu.cmu.sv.lifelogger.util.AnnotationDataCollectorSet;
import edu.cmu.sv.mobisens.MobiSensLauncher;
import edu.cmu.sv.mobisens.R;
import edu.cmu.sv.mobisens.content.ModelWidget;
import edu.cmu.sv.mobisens.net.Network;
import edu.cmu.sv.mobisens.settings.LocalSettings;
import android.app.ActionBar;
import android.app.ActionBar.OnNavigationListener;
import android.app.ActivityGroup;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.DialogInterface.OnClickListener;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.Window;
import android.widget.Adapter;
import android.widget.ArrayAdapter;

public class SwitcherActivity extends ActivityGroup {
	
	public static final String MAP_VIEW = "mapView";
	public static final String LIST_VIEW = "listView";
	public static final String WEB_VIEW = "webView";
	public static final String NONE_VIEW = "none";
	
	private static final String CLASS_PREFIX = SwitcherActivity.class.getName();
	public static final String ACTION_GO_LISTVIEW = CLASS_PREFIX + ".action_listview";
	public static final String ACTION_GO_MAPVIEW = CLASS_PREFIX + ".action_mapview";
	public static final String ACTION_GO_WEBVIEW = CLASS_PREFIX + ".action_webview";
	protected static final String TAG = CLASS_PREFIX;
	
	
	private String currentView = NONE_VIEW;
	
	private BroadcastReceiver appController = new BroadcastReceiver(){

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			String action = intent.getAction();
			
			// Why the hell the listview will be selected twice if it is the one
			// loaded from settings?
			// Now I have to use currentView.equals(...) to avoid double selection, same as
			// the approach used in MobiSensLauncher's navigation bar.
			// Am I have something wrong or it is a bug in Android API?
			if(action.equals(ACTION_GO_LISTVIEW) && currentView.equals(LIST_VIEW) == false){
				replaceContentView(LIST_VIEW, 
						new Intent(SwitcherActivity.this, 
								ListAnnotationActivity.class));
			}
			
			if(action.equals(ACTION_GO_MAPVIEW) && currentView.equals(MAP_VIEW) == false){
				replaceContentView(MAP_VIEW, 
						new Intent(SwitcherActivity.this, 
								MapAnnotationListActivity.class));
			}
			
			if(action.equals(ACTION_GO_WEBVIEW)){
				replaceContentView(WEB_VIEW, 
						new Intent(SwitcherActivity.this, 
								WebViewActivity.class));
			}
		}
		
	};
	
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.switcher);
		
		//final Boolean initialized = false;
		
		/** Create an array adapter to populate dropdownlist */
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getBaseContext(), 
        		android.R.layout.simple_spinner_item,
        		android.R.id.text1,
        		getResources().getStringArray(R.array.switcher_app_action_list));
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        
		
        
        /** Enabling dropdown list navigation for the action bar */
        getActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
        
        getActionBar().setDisplayShowHomeEnabled(true);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setDisplayShowTitleEnabled(false);
 
        /** Setting dropdown items and item navigation listener for the actionbar */
        //getActionBar().setSelectedNavigationItem(Adapter.NO_SELECTION);
        getActionBar().setListNavigationCallbacks(adapter, navigationListener);
        
		IntentFilter filter = new IntentFilter(ACTION_GO_MAPVIEW);
		filter.addAction(ACTION_GO_LISTVIEW);
		filter.addAction(ACTION_GO_WEBVIEW);
		
		this.registerReceiver(appController, filter);
		
		
	}
	
	
	protected void onDestroy(){
		super.onDestroy();
		
		this.unregisterReceiver(appController);
	}
	
	/** Defining Navigation listener */
    private OnNavigationListener navigationListener = new OnNavigationListener() {
    	
        @Override
        public boolean onNavigationItemSelected(int position, long id) {
        	
            switch(position){
                case 1:
        	    	Intent listViewIntent = new Intent(SwitcherActivity.ACTION_GO_LISTVIEW);
        	    	//mapViewIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        	    	sendBroadcast(listViewIntent);
        	    	
        	    	Log.i(TAG, "Go listview");
        	    	break;
        	    case 0:
        	    	Intent mapViewIntent = new Intent(SwitcherActivity.ACTION_GO_MAPVIEW);
        	    	//listViewIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        	    	sendBroadcast(mapViewIntent);
        	    	
        	    	Log.i(TAG, "Go mapview");
        	    	break;
            }
            return false;
        }
    };
	
	protected void onResume(){
		super.onResume();
		final String currentView = LocalSettings.getView(this);
		
		AsyncTask<String, Void, Boolean> task = new AsyncTask<String, Void, Boolean>(){

			@Override
			protected Boolean doInBackground(String... arg0) {
				// TODO Auto-generated method stub
				return Network.canConnectToServer("http://www.google.com");
				
			}
			
			@Override
			protected void onPostExecute(Boolean result){
				
				ActionBar actionBar = SwitcherActivity.this.getActionBar();
				if(result && currentView.equals(MAP_VIEW)){
					if(actionBar.getSelectedNavigationIndex() != 0){
						actionBar.setSelectedNavigationItem(0);
						Log.i(TAG, "Init set to mapview");
					}
					
				}else{
					if(actionBar.getSelectedNavigationIndex() != 1){
						actionBar.setSelectedNavigationItem(1);
						Log.i(TAG, "Init set to listview");
					}
					
				}
				
			}
		};
		
		task.execute("http://www.google.com");
		
		
	}

	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.annotationlist_option_menu, menu);
	    return true;
	}
	
	public boolean onOptionsItemSelected(MenuItem item) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(item.getTitle());
    	
	    switch (item.getItemId()) {
	    case R.id.reset_training_menu:

	    	builder.setMessage("You need to retrain the phone to recognize activities after reset the training data.\r\nAre you sure to continue?");
	    	builder.setPositiveButton("Yes", new OnClickListener(){

				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					AnnotationDataCollectorSet.clearDataFiles();

					Intent requestClearModelIntent = new Intent(ModelWidget.ACTION_CLEAR_MODEL);
					sendBroadcast(requestClearModelIntent);
				}
	    		
	    	});
	    	builder.setNegativeButton("No", new OnClickListener(){
				public void onClick(DialogInterface dialog, int which) { }	
	    	});
	    	builder.show();
	        return true;
	    case R.id.clear_annolist_menu:

	    	builder.setMessage("All the annotations will be deleted and can not recover, are you sure to continue?");
	    	builder.setPositiveButton("Yes", new OnClickListener(){

				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					
					Intent intent = new Intent(RendererWidget.ACTION_CLEAR);
					intent.putExtra(RendererWidget.EXTRA_RENDERER_TYPE, RendererWidget.ALL_RENDERER_TYPES);
					sendBroadcast(intent);
				}
	    		
	    	});
	    	builder.setNegativeButton("No", new OnClickListener(){
				public void onClick(DialogInterface dialog, int which) { }	
	    	});
	    	
	    	builder.show();
	        return true;
	    
	    case android.R.id.home:
	    	this.onBackPressed();
            return true;

	    		
	    default:
	        return super.onOptionsItemSelected(item);
	    }
	}
	
	
	private void replaceContentView(String id, Intent intent) {

		Window window = getLocalActivityManager().startActivity(id, intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
		if (window != null) {
			setContentView(window.getDecorView());
		}
		currentView = id;
		LocalSettings.setView(SwitcherActivity.this, id);
	}

}
