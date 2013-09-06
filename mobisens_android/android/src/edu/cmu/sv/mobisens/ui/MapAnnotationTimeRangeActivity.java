package edu.cmu.sv.mobisens.ui;

import java.util.Date;
import java.util.LinkedList;
import edu.cmu.sv.lifelogger.util.DataCollector;
import edu.cmu.sv.mobisens.R;
import edu.cmu.sv.mobisens.io.MobiSensDataHolder;
import edu.cmu.sv.mobisens.ui.RangeSeekBar.OnRangeSeekBarChangeListener;
import edu.cmu.sv.mobisens.util.Annotation;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;

public class MapAnnotationTimeRangeActivity extends MapAnnotationActivity {

	private static final String TAG = MapAnnotationTimeRangeActivity.class.getName();
	
	private LinkedList<double[]> locationsInTimeRange;
	private AnnotationRangeSeekBar seekBar;
	private Annotation copyOfSelectedAnno;
	private boolean shouldNotifySave = false;
	
	
	
	@Override
	protected int getContentViewResourceId(){
		// This can prevent changing all the MobiSens BaseMapView Activities. Feel free to debug on this Activity.
		// After the debug finished, we can replace the old MapAnnotationActivity.
		return R.layout.map_anno_range_activity;
	}
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		this.seekBar = (AnnotationRangeSeekBar) findViewById(R.id.seek_bar);
		
	}
	
	protected void onResume(){
		super.onResume();
		
		
		Annotation anno = this.getSelectedAnnotation();
		this.copyOfSelectedAnno = Annotation.fromString(anno.toString());  // so we can reset the selection.
		
		seekBar.setDateRangeWithSelectionDelta(anno.getStart(), anno.getEnd(), 1d);
		this.locationsInTimeRange = this.getLocationInTimeRangeFromDB(seekBar.getAbsoluteMinValue(), seekBar.getAbsoluteMaxValue());
		
		final TextView fromView = (TextView) findViewById(R.id.txtFrom);
    	final TextView toView = (TextView) findViewById(R.id.txtTo);
		
    	fromView.setText(DateFormat.format("MMM dd, h:mm:ssaa", anno.getStart()).toString());
    	toView.setText(DateFormat.format("MMM dd, h:mm:ssaa", anno.getEnd()).toString());
    	
    	seekBar.setOnRangeSeekBarChangeListener(new OnRangeSeekBarChangeListener<Long>() {
    	        @Override
    	        public void onRangeSeekBarValuesChanged(RangeSeekBar<?> bar, Long minValue, Long maxValue) {
    	                // handle changed range values
    	        	fromView.setText(DateFormat.format("MMM dd, h:mm:ssaa", new Date(minValue)).toString());
    	        	toView.setText(DateFormat.format("MMM dd, h:mm:ssaa", new Date(maxValue)).toString());
    	        	
    	        	
    	        	LinkedList<double[]> selectedLocations = getLocationInTimeRange(locationsInTimeRange, minValue, maxValue);
    	        	Log.i(TAG, "Selected location length: " + selectedLocations.size());
    	        	getSelectedAnnotation().setStart(new Date(minValue));
    	        	getSelectedAnnotation().setEnd(new Date(maxValue));
    	        	DataCollector<double[]> locationCollector = new DataCollector<double[]>(selectedLocations.size());
    	        	for(double[] latlngTimestamp:selectedLocations){
    	        		locationCollector.collect(latlngTimestamp);
    	        	}
    	        	getSelectedAnnotation().setLocations(locationCollector);
    	        	
    	        	drawOverlayAndZoomMapView();
    	        	shouldNotifySave = true;
//    	            Log.i(TAG, "User selected new date range: MIN=" + new Date(minValue) + ", MAX=" + new Date(maxValue));
    	        }
    	});
		
	}

	@Override
	public void onBackPressed() {
		
		this.checkSaveAndExit();
		
	}
	
	@Override
	protected void onNameSelected(String oldName, String newName){
		// Do nothing, the save will happen in save menu.
		this.shouldNotifySave = true;
	}

	private void checkSaveAndExit(){
		if(!this.shouldNotifySave){
			super.onBackPressed();
			return;
		}
		
		if(!(copyOfSelectedAnno.getStart().equals(this.getSelectedAnnotation().getStart()) && 
				copyOfSelectedAnno.getEnd().equals(this.getSelectedAnnotation().getEnd()) &&
				copyOfSelectedAnno.getName().equals(this.getSelectedAnnotation().getName()))){
			AlertDialog dialog = new AlertDialog.Builder(this)
			.setTitle("Save Annotation")
			.setIcon(R.drawable.anno_dlg)
			
		    .setMessage("You have unsaved change(s) in current segment, do you want to save it?")
		    .setPositiveButton("Save", new DialogInterface.OnClickListener() {
		        public void onClick(DialogInterface dialog, int whichButton) {
		        	boradcastActivitySegmentNameChanged(copyOfSelectedAnno.getName(), 
			    			getSelectedAnnotation());
		        	MapAnnotationTimeRangeActivity.super.onBackPressed();
		        }
		    }).setNegativeButton("Discard", new DialogInterface.OnClickListener() {
		        public void onClick(DialogInterface dialog, int whichButton) {
		        	MapAnnotationTimeRangeActivity.super.onBackPressed();
		        }
		    }).create();
		    dialog.show();
		    
		}else{
			super.onBackPressed();
		}
		
	}
	
	
	
	private LinkedList<double[]> getLocationInTimeRange(LinkedList<double[]> fullOverlayLocations, 
			long startTime, long endTime){
		
		LinkedList<double[]> locations = new LinkedList<double[]>();
		for(double[] latlngTimestamp:fullOverlayLocations){
			if(latlngTimestamp.length < 3)
				continue;
			
			long timestamp = (long) (latlngTimestamp[2] * 1000);
			if(timestamp >= startTime && timestamp <= endTime){
				locations.add(latlngTimestamp);
			}
		}
		
		return locations;
	}
	
	private LinkedList<double[]> getLocationInTimeRangeFromDB(long startTime, long endTime){
		MobiSensDataHolder db = new MobiSensDataHolder(this);
		db.open();
		LinkedList<double[]> locations = new LinkedList<double[]>();
		
		String[] annoStringInTimeRange = db.search(startTime, endTime);
		Log.i(TAG, "Length: " + annoStringInTimeRange.length);
		
		for(String annoString:annoStringInTimeRange){
			Annotation anno = Annotation.fromString(annoString);
			LinkedList<double[]> locationsInAnno = anno.getLocations().getData();
			if(locationsInAnno != null){
				locations.addAll(locationsInAnno);
			}
		}
		
		db.close();
		Log.i(TAG, "Location length: " + locations.size());
		return locations;
	}
	
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.map_time_range_anno_option_menu, menu);
	    return true;
	}
	
	public boolean onOptionsItemSelected(MenuItem item) {
		if(this.getSelectedAnnotation() == null)
			return true;
		
	    switch (item.getItemId()) {
		    case R.id.reset_changes:
		    	this.resetChangedAnno();
		    	break;
		    case R.id.save_changes:
		    	this.boradcastActivitySegmentNameChanged(this.copyOfSelectedAnno.getName(), 
		    			this.getSelectedAnnotation());
		    	this.shouldNotifySave = false;
		    	break;
		    case android.R.id.home:
		    	
	    		this.checkSaveAndExit();
		    	
		    	break;
		    default:
		        return super.onOptionsItemSelected(item);
	    }
	    
	    return true;
	}
	
	
	private void resetChangedAnno(){
		LinkedList<double[]> selectedLocations = this.copyOfSelectedAnno.getLocations().getData();
    	getSelectedAnnotation().setStart(new Date(this.copyOfSelectedAnno.getStart().getTime()));
    	getSelectedAnnotation().setEnd(new Date(this.copyOfSelectedAnno.getEnd().getTime()));
    	DataCollector<double[]> locationCollector = new DataCollector<double[]>(selectedLocations.size());
    	for(double[] latlngTimestamp:selectedLocations){
    		locationCollector.collect(latlngTimestamp);
    	}
    	getSelectedAnnotation().setLocations(locationCollector);
    	getSelectedAnnotation().setName(this.copyOfSelectedAnno.getName());
    	
    	drawOverlayAndZoomMapView();
    	this.seekBar.setSelectedMinValue(this.copyOfSelectedAnno.getStart().getTime());
    	this.seekBar.setSelectedMaxValue(this.copyOfSelectedAnno.getEnd().getTime());
    	
    	this.shouldNotifySave = false;
	}
	
	
 

}
