package edu.cmu.sv.mobisens.ui;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;


import edu.cmu.sv.lifelogger.api.LocationBasedRecognizer;
import edu.cmu.sv.mobisens.MobiSensLauncher;
import edu.cmu.sv.mobisens.MobiSensService;
import edu.cmu.sv.mobisens.R;
import edu.cmu.sv.mobisens.content.ActivityWidget;
import edu.cmu.sv.mobisens.content.AnnotationWidget;
import edu.cmu.sv.mobisens.content.ModelWidget;
import edu.cmu.sv.mobisens.io.Directory;
import edu.cmu.sv.mobisens.io.MobiSensLog;
import edu.cmu.sv.mobisens.util.Annotation;
import edu.cmu.sv.mobisens.util.AnnotationColor;
import edu.cmu.sv.mobisens.util.MachineAnnotation;
import edu.cmu.sv.mobisens.util.Sharing;
import edu.cmu.sv.mobisens.util.Sharing.OnProcessCompleted;
import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ActionBar.OnNavigationListener;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnClickListener;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Message;
import android.text.SpannableString;
import android.text.format.DateFormat;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup.MarginLayoutParams;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TableLayout.LayoutParams;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap.InfoWindowAdapter;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.GroundOverlayOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapAnnotationActivity extends BaseMapActivity {

	private Annotation selectedAnno = null;
	private static final String CLASS_PREFIX = MapAnnotationActivity.class.getName();
	
	public final static String EXTRA_GEO_LOCATIONS = CLASS_PREFIX + ".extra_locations";
	public final static String EXTRA_GEO_TIESTAMP_START = CLASS_PREFIX + ".extra_start_timestamp";
	public final static String EXTRA_GEO_TIESTAMP_END = CLASS_PREFIX + ".extra_end_timestamp";
	
	public final static String EXTRA_ANNO = CLASS_PREFIX + ".extra_annotation";
	public final static String EXTRA_ANNO_INDEX = CLASS_PREFIX + ".extra_anno_index";
	public final static String EXTRA_ANNO_COLOR = CLASS_PREFIX + ".extra_anno_color";
	
	
	private GeoPathOverlay pathOverlay = null;
	private long nameListRequestId = 0;
	private String[] nameList;
	
	private BroadcastReceiver receiver = new BroadcastReceiver(){

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			String action = intent.getAction();
			if(AnnotationWidget.ACTION_GET_ALL_NAMES_DONE.equals(action)){
				onNamesArrived(intent);
			}
		}
		
	};
	
	private void onNamesArrived(Intent intent){
		long id = intent.getLongExtra(AnnotationWidget.EXTRA_GET_NAME_ID, 0);
		if(id != nameListRequestId)  // This message is not requested by this app.
			return;
		final String[] names = intent.getStringArrayExtra(AnnotationWidget.EXTRA_ANNO_NAMES);
		
		
		AsyncTask<Annotation, Void, String> task = new AsyncTask<Annotation, Void, String>(){

			@Override
			protected String doInBackground(Annotation... params) {
				// TODO Auto-generated method stub
				
				if(params.length == 0)
					return "";
				LocationBasedRecognizer recognizer = new LocationBasedRecognizer(
						MobiSensService.getDeviceID(getApplicationContext()),
						params[0].getLocations());
				String activity = recognizer.recognize();
				
				return Annotation.getEscapedName(activity);
			}
			
			@Override
			protected void onPostExecute(String activity){
				
				//Hashtable<String, Boolean> uniqueNames = new Hashtable<String, Boolean>();
				ArrayList<String> uniqueNames = new ArrayList<String>();
				
				// Make the recognized name first.
				if(!activity.equals("")){
					if(!uniqueNames.contains(activity)){
						uniqueNames.add(activity);
					}
				}
				
				for(String name:names){
					if(!uniqueNames.contains(name)){
						uniqueNames.add(name);
					}
				}
				
				setProgressBarIndeterminateVisibility(false);
				String[] nameArray = uniqueNames.toArray(new String[0]);
				onNamesArrival(nameArray);
			}
			
		};
		
		
		if(this.getSelectedAnnotation() != null){
			task.execute(new Annotation[]{this.getSelectedAnnotation()});
		}
	}
	
	protected void onNamesArrival(String[] possibleNames){
		ArrayList<String> names = new ArrayList<String>(possibleNames.length + 2);
		
		
		names.add(this.getSelectedAnnotation().getName());
		
		for(String name:possibleNames){
			if(!name.equals(this.getSelectedAnnotation().getName())){
				names.add(name);
			}
		}
		
		names.add(getResources().getString(R.string.name_picker_input_new_item));
		nameList = names.toArray(new String[0]);
		
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(getBaseContext(), 
        		android.R.layout.simple_spinner_item,
        		android.R.id.text1,
        		this.nameList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        
		getActionBar().setListNavigationCallbacks(adapter, navigationListener);
	}
	
	protected Annotation getSelectedAnnotation(){
		return this.selectedAnno;
	}
	
	private void requestPossibleNames(){
		this.setProgressBarIndeterminateVisibility(true);
		this.nameListRequestId = new Date().getTime();
    	Intent requestIntent = new Intent(AnnotationWidget.ACTION_GET_ALL_NAMES);
    	requestIntent.putExtra(AnnotationWidget.EXTRA_GET_NAME_ID, this.nameListRequestId);
    	requestIntent.putExtra(AnnotationWidget.EXTRA_GET_NAME_SIZE, AnnotationWidget.DEFAULT_GET_NAME_SIZE);
    	this.sendBroadcast(requestIntent);
	}
	
	protected AnnotationOverlayMarker getOverlayPath(){
		return new AnnotationOverlayMarker(this.getSelectedAnnotation(), BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
	}

	protected void drawOverlayAndZoomMapView(){
		this.getMapView().clear();
		
		int locationLength  = this.getSelectedAnnotation().getLocations().getDataSize();
		if(locationLength > 0){
			LinkedList<double[]> locations = this.getSelectedAnnotation().getLocations().getData();
			this.reDrawOverlay(getSelectedAnnotation());
			
			AnnotationOverlayMarker annoMarker = getOverlayPath();
			annoMarker.draw(getMapView());
			
			getMapView().setInfoWindowAdapter(new InfoWindowAdapter() {

				@Override
				public View getInfoContents(Marker arg0) {
					final Annotation anno = selectedAnno;
					
					
					if(anno == null)
						return null;
					
					View v = getLayoutInflater().inflate(R.layout.activity_infor_window, null);
					TextView txtTitle = (TextView) v.findViewById(R.id.ac_title);
					txtTitle.setText(anno.getName());
					
					TextView txtSubtitle = (TextView) v.findViewById(R.id.ac_subtitle);
					txtSubtitle.setText(anno.getActivityName());
					if(anno.getActivityName().equals("")){
						txtSubtitle.setVisibility(View.GONE);
					}else{
						txtSubtitle.setVisibility(View.VISIBLE);
					}
					TextView txtTo = (TextView) v.findViewById(R.id.ac_info_to);
					txtTo.setText(DateFormat.format("MMM dd, h:mmaa", anno.getEnd()).toString());
					
					TextView txtFrom = (TextView) v.findViewById(R.id.ac_infor_from);
					txtFrom.setText(DateFormat.format("MMM dd, h:mmaa", anno.getStart()).toString());
					
					TextView txtLabel = (TextView) v.findViewById(R.id.ac_label_link);
					txtLabel.setVisibility(View.GONE);
					
					
					
					// TODO Auto-generated method stub
					return v;
				}

				@Override
				public View getInfoWindow(Marker arg0) {
					// TODO Auto-generated method stub
					return null;
				}
				
			});
			
			//mapView.getController().setZoom(18);
			zoomInBounds(locations);
		}else{
			Toast.makeText(this, "No location data found in this activity segment.", Toast.LENGTH_SHORT).show();
		}
	}
	
	private OnNavigationListener navigationListener = new OnNavigationListener() {
    	
        @Override
        public boolean onNavigationItemSelected(int position, long id) {
        	
            if(nameList == null)
            	return false;
            String name = nameList[position];
            if(position == nameList.length - 1){
            	
        		showRenameDialog(getSelectedAnnotation(), getSelectedAnnotation().getName());
            }else{
            	String originalName = getSelectedAnnotation().getName();
            	updateUIByName(originalName, name);
            	
            }
            return true;
        }
    };
    
    protected void onNameSelected(String oldName, String newName){
    	boradcastActivitySegmentNameChanged(oldName, this.getSelectedAnnotation());
    }
	
	protected void onCreate(Bundle savedInstanceState) {
		
		// This line must be put before super.onCreate,
		// because there is setContentView in its parent class.
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		super.onCreate(savedInstanceState);
	
		
		ActionBar actionBar = getActionBar();
		if(actionBar != null){
			actionBar.setDisplayHomeAsUpEnabled(true);
		}
		
	}

	public boolean onOptionsItemSelected(MenuItem item) {
		if(this.selectedAnno == null)
			return true;
		
	    switch (item.getItemId()) {
		    
		    case android.R.id.home:
		    	this.onBackPressed();
	            return true;
		    default:
		        return super.onOptionsItemSelected(item);
	    }
	}

	protected void onResume(){
		super.onResume();
		
		Intent intent = getIntent();
		String annoString = intent.getStringExtra(Annotation.EXTRA_ANNO_STRING);
		
		this.pathOverlay = null;
		Annotation anno = Annotation.fromString(annoString);
		this.selectedAnno = anno;
		
		
		//List<Overlay> overlays = getMapView().getOverlays();
		//overlays.clear();
		this.drawOverlayAndZoomMapView();
		
		if(this.selectedAnno != null){
			this.setTitle(this.selectedAnno.getName());
		}else{
			Toast.makeText(this, "Error on deserializing event.", Toast.LENGTH_SHORT).show();
		}
		
		
		this.nameList = new String[]{this.getSelectedAnnotation().getName(), getResources().getString(R.string.name_picker_input_new_item)};
		
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(getBaseContext(), 
        		android.R.layout.simple_spinner_item,
        		android.R.id.text1,
        		nameList );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        
		getActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
		getActionBar().setListNavigationCallbacks(adapter, navigationListener);
		getActionBar().setDisplayShowTitleEnabled(false);
		
		this.registerReceiver(receiver, new IntentFilter(AnnotationWidget.ACTION_GET_ALL_NAMES_DONE));
		requestPossibleNames();
	}
	
	protected void onPause(){
		super.onPause();
		this.setTitle(getResources().getString(R.string.app_name));
		this.unregisterReceiver(receiver);
	}
	
	protected void onDestroy(){
		super.onDestroy();
	}

	
	protected void onMessageArrival(Message msg){
		
	}
	
	
	private Dialog showRenameDialog(final Annotation mAnno, final String originalName){

		final AutoCompleteTextView atxtActivity = new AutoCompleteTextView(this);
		String[] activities = getResources().getStringArray(R.array.default_activities);
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.list_item, activities);
		atxtActivity.setAdapter(adapter);
		if(!mAnno.getName().equals(Annotation.UNKNOWN_ANNOTATION_NAME))
			atxtActivity.setText(mAnno.getName());
		atxtActivity.selectAll();
		
		LinearLayout container = new LinearLayout(this);
		MarginLayoutParams margin = new MarginLayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
		//margin.setMargins(100, 0, 100, 0);
		
		container.addView(atxtActivity, margin);
		container.setPadding(20, 0, 20, 0);

		
		return new AlertDialog.Builder(this)
	    .setTitle("Update Annotation")
	    .setIcon(R.drawable.anno_dlg)
	    .setMessage("Please input a new name for activity \"" + originalName + "\"")
	    .setView(container)
	    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
	        public void onClick(DialogInterface dialog, int whichButton) {
	        	updateUIByName(originalName, atxtActivity.getText().toString());
	        	requestPossibleNames();
	        }
	    }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
	        public void onClick(DialogInterface dialog, int whichButton) {
	            getActionBar().setSelectedNavigationItem(0);
	        }
	    }).show();
	}

	private void updateUIByName(String originalName, String newName){

		if(newName.equalsIgnoreCase(""))
			newName = Annotation.UNKNOWN_ANNOTATION_NAME;
		
		
		try {
			
			this.getSelectedAnnotation().setName(newName);
			int newColor = AnnotationColor.getColor(newName, this);
			this.getSelectedAnnotation().setColor(newColor);
			
			if(originalName.equals(newName) == false && this.pathOverlay != null){
				this.reDrawOverlay(this.getSelectedAnnotation());
			}
			
			this.setTitle(newName);
			onNameSelected(originalName, newName);
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			MobiSensLog.log(e);
		}
		
	}
	
	private int reDrawOverlay(Annotation anno){

		this.getMapView().clear();
		
		int locationLength  = this.getSelectedAnnotation().getLocations().getDataSize();
		if(locationLength > 0){
			
			LinkedList<double[]> locations = this.getSelectedAnnotation().getLocations().getData();
			if(locationLength > 1){
				this.pathOverlay = new GeoPathOverlay(anno, anno.getColor());
				this.pathOverlay.draw(getMapView());
			}else{
				getMapView().addMarker(new MarkerOptions()
		        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
		        .position(new LatLng(locations.get(0)[0], locations.get(0)[1]))
		        );
		        
			}
		
		}
		
		return anno.getColor();
	}
	
	
	protected void boradcastActivitySegmentNameChanged(String originalName, Annotation mAnno){
		
		Log.i("Color", "color: " + mAnno.getColor());
		
		// Tell the ModelWidget update the model name and color
		Intent modelNameChangedIntent = new Intent(ModelWidget.ACTION_UPDATE_MODEL_NAME);
		modelNameChangedIntent.putExtra(Annotation.EXTRA_ANNO_STRING, mAnno.toString());
		
		sendBroadcast(modelNameChangedIntent);
		
		
		Intent updateNameIntent = new Intent(RendererWidget.ACTION_UPDATE_ANNO);
		updateNameIntent.putExtra(Annotation.EXTRA_ANNO_STRING, mAnno.toString());
		
		// This message will also received by all kinds of renderer
		// which will refresh the UI.
		updateNameIntent.putExtra(RendererWidget.EXTRA_RENDERER_TYPE, RendererWidget.ALL_RENDERER_TYPES);
		sendBroadcast(updateNameIntent);
		
	}
	
	

}
