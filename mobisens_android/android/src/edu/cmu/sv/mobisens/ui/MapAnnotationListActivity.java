package edu.cmu.sv.mobisens.ui;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.LinkedList;

import com.google.android.gms.maps.GoogleMap.InfoWindowAdapter;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.Polyline;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;
import edu.cmu.sv.lifelogger.api.ActivityRecognitionRequestScheduler;
import edu.cmu.sv.lifelogger.util.DataCollector;
import edu.cmu.sv.mobisens.R;
import edu.cmu.sv.mobisens.content.ActivityReader;
import edu.cmu.sv.mobisens.io.DataShrinker;
import edu.cmu.sv.mobisens.io.MobiSensLog;
import edu.cmu.sv.mobisens.ui.RangeSeekBar.OnRangeSeekBarChangeListener;
import edu.cmu.sv.mobisens.util.Annotation;

public class MapAnnotationListActivity extends BaseMapActivity {
	private static final String TAG = "MapAnnotationListActivity";
	
	private MapViewRendererWidget renderWidget = new MapViewRendererWidget();
	private ActivityReader reader = new ActivityReader(0, 0);
	private AnnotationRangeSeekBar seekBar;
	
	private LinkedList<AnnotationOverlayMarker> annotationOverlays = new LinkedList<AnnotationOverlayMarker>();
	private LinkedList<GeoPathOverlay> polylineOverlays = new LinkedList<GeoPathOverlay>();
	
	private Hashtable<Long, String> renderedActivities = new Hashtable<Long, String>();
	
	private LinkedList<AsyncTask<?,?,?>> uiUpdateTasks = new LinkedList<AsyncTask<?,?,?>>();
	
	
	public void reDraw(){
		
		seekBar.setDateRangeWithSelectionDelta(minDate, maxDate, 0d);
				
		seekBar.setSelectedMaxValue(maxSelectionDate.getTime());
		
		if(maxSelectionDate.getTime() - minSelectionDate.getTime() < 12 * 60 * 60 * 1000){
			if(maxSelectionDate.getTime() - 12 * 60 * 60 * 1000 > minDate.getTime()){
				seekBar.setSelectedMinValue(maxSelectionDate.getTime() - 12 * 60 * 60 * 1000);
			}else{
				seekBar.setSelectedMinValue(minDate.getTime());
			}
		}else{
			seekBar.setSelectedMinValue(minSelectionDate.getTime());
		}
		
		
		drawOverlaysInRange(seekBar.getSelectedMinValue(), seekBar.getSelectedMaxValue());
	}
	
	private void drawOverlaysInRange(long minValue, long maxValue){
		final TextView fromView = (TextView) findViewById(R.id.txtFrom);
    	final TextView toView = (TextView) findViewById(R.id.txtTo);
    	
		fromView.setText(DateFormat.format("MMM dd, h:mm:ssaa", new Date(minValue)).toString());
    	toView.setText(DateFormat.format("MMM dd, h:mm:ssaa", new Date(maxValue)).toString());
    	
    	Log.i(TAG, "selection start: " + fromView.getText() + ", selection end: " + toView.getText());
    	
    	
    	AsyncTask<Long, ArrayList<MapOverlay<?>>, Void> uiUpdateTask = new AsyncTask<Long, ArrayList<MapOverlay<?>>, Void>(){
			
    		volatile LinkedList<double[]> selectedLocations = new LinkedList<double[]>();
    		
    		@Override
			protected void onPreExecute(){
				this.selectedLocations.clear();
			}
    		
			@SuppressWarnings("unchecked")
			@Override
			protected Void doInBackground(Long... timeBoundary) {
				// TODO Auto-generated method stub
				ArrayList<MapOverlay<?>> overlaysToShow = new ArrayList<MapOverlay<?>>();
				ArrayList<MapOverlay<?>> overlaysToHide = new ArrayList<MapOverlay<?>>();
				int length = polylineOverlays.size();
				for(int i = 0; i < length; i++){
	        		GeoPathOverlay path = polylineOverlays.get(i);
	        		AnnotationOverlayMarker marker = annotationOverlays.get(i);
	        		
	        		Annotation annoInOverlay = path.getAnnotation();
	        		if(annoInOverlay.getStart().getTime() >= timeBoundary[0] &&
	        				annoInOverlay.getEnd().getTime() <= timeBoundary[1]){
	        			if(!path.isRendered()){
	        				overlaysToShow.add(path);
	        				overlaysToShow.add(marker);
	        				ActivityRecognitionRequestScheduler.push(annoInOverlay);
	        			}
	        			
	        			
						LinkedList<double[]> locationsInAnno = path.getAnnotation().getLocations().getData();
						for(double[] data:locationsInAnno){
							selectedLocations.add(data);
						}
							
	        		}else{
	        			if(path.isRendered()){
	        				overlaysToHide.add(path);
	        				overlaysToHide.add(marker);
	        			}
	        		}
	        		
	        		if(overlaysToShow.size() + overlaysToHide.size() % 5 == 0 || i == length - 1){
	        			publishProgress((ArrayList<MapOverlay<?>>)overlaysToShow.clone(), 
	        					(ArrayList<MapOverlay<?>>)overlaysToHide.clone());
	        			overlaysToShow.clear();
	        			overlaysToHide.clear();
	        			try {
							Thread.sleep(100);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
	        		}
	        	}
				
				return null;
			}
			
			@SuppressWarnings("unchecked")
			@Override
		    protected void onProgressUpdate(ArrayList<MapOverlay<?>>... overlays) {
				ArrayList<MapOverlay<?>> overlaysToShow = overlays[0];
				ArrayList<MapOverlay<?>> overlaysToHide = overlays[1];
				for(MapOverlay<?> overlay:overlaysToShow){
					overlay.draw(getMapView());
				}
				
				for(MapOverlay<?> overlay:overlaysToHide){
					overlay.removeFromMap();
				}
				
				if(selectedLocations.size() > 0){
					zoomInBounds((LinkedList<double[]>) selectedLocations.clone());
				}
				
		    }
			
			@Override
		    protected void onPostExecute(Void unused) {
				if(selectedLocations.size() > 0){
					zoomInBounds(selectedLocations);
				}
				
				uiUpdateTasks.remove(this);
			}
			
			
		};
		
		uiUpdateTasks.add(uiUpdateTask);
		uiUpdateTask.execute(minValue, maxValue);
	}
	
	protected int getContentViewResourceId(){
		return R.layout.map_anno_range_activity;
	}
	
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		super.onCreate(savedInstanceState);
		
		Toast.makeText(this, 
				"You can tab on the marker to change acvitity label."
				+ "\r\nPress Menu Key for switching to other view(s).", 
				Toast.LENGTH_LONG).show();
		
		reader.register(this);
		this.seekBar = (AnnotationRangeSeekBar) findViewById(R.id.seek_bar);
		
		this.getMapView().setInfoWindowAdapter(new InfoWindowAdapter() {

			@Override
			public View getInfoContents(Marker arg0) {
				Annotation anno = null;
				int index = 0;
				
				for(AnnotationOverlayMarker overlayMarker:annotationOverlays){
					if(overlayMarker.equals(arg0)){
						anno = overlayMarker.getAnnotation();
						break;
					}
					index++;
				}
				
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
				
				final String annoString = anno.toString();
				final int annoColor = anno.getColor();
				final int annoIndex = index;
				getMapView().setOnInfoWindowClickListener(new OnInfoWindowClickListener(){

					@Override
					public void onInfoWindowClick(Marker arg0) {
						// TODO Auto-generated method stub
						Intent mapAnnoIntent = new Intent(MapAnnotationListActivity.this, MapAnnotationTimeRangeActivity.class);
						
						mapAnnoIntent.putExtra(Annotation.EXTRA_ANNO_STRING, annoString);
						mapAnnoIntent.putExtra(MapAnnotationActivity.EXTRA_ANNO_INDEX, annoIndex);
						mapAnnoIntent.putExtra(MapAnnotationActivity.EXTRA_ANNO_COLOR, annoColor);
			
						MapAnnotationListActivity.this.startActivity(mapAnnoIntent);
					}
					
				});
				// TODO Auto-generated method stub
				return v;
			}

			@Override
			public View getInfoWindow(Marker arg0) {
				// TODO Auto-generated method stub
				return null;
			}
			
		});
		
		seekBar.setOnRangeSeekBarChangeListener(new OnRangeSeekBarChangeListener<Long>() {
	        @Override
	        public void onRangeSeekBarValuesChanged(RangeSeekBar<?> bar, Long minValue, Long maxValue) {
	            // handle changed range values
	        	drawOverlaysInRange(minValue, maxValue);
				
	        }
		});
		
		
		renderWidget.register(this);
		
		reader.setRendererType(renderWidget.getRenderType());
		reader.readAsync();
		Log.i(TAG, "event read.");
		Log.i(TAG, "MapView created.");
	}
	
	
	
	protected void onResume(){
		super.onResume();
		
		
	}
	
	protected void onPause(){
		DataShrinker.shrinkData(this);
		
		
		super.onPause();
	}
	
	protected void onDestroy(){
		renderWidget.unregister();
		reader.unregister();
		for(AsyncTask<?,?,?> unfinishUIUpdateTask:uiUpdateTasks){
			unfinishUIUpdateTask.cancel(true);
		}
		
		super.onDestroy();
		Log.i(TAG, "MapView destroyed.");
	}
	
	
	public void updateRendering(Annotation anno){
		int length = this.annotationOverlays.size();
		
		boolean appendResult = true;
		//updateSeekBarRange(anno);
		
		for(int i = 0; i < length; i++){
			AnnotationOverlayMarker overlayItem = this.annotationOverlays.get(i);
			if(overlayItem.getAnnotation().getDBId() == anno.getDBId()){
				
				overlayItem.setAnnotation(anno);
				
				if(anno.getStart().getTime() >= seekBar.getSelectedMinValue() &&
						anno.getEnd().getTime() <= seekBar.getSelectedMaxValue()){
					overlayItem.draw(this.getMapView());  // redraw the marker.
					ActivityRecognitionRequestScheduler.push(anno);
				}
				appendResult = false;
			}
		}
		
		if(appendResult){
			Log.i(TAG, "update error, anno will be appended");
			this.renderAnnotation(anno);
		}
		
	}
	
	/*
	private void updateSeekBarRange(Annotation anno){
		if(seekBar.getAbsoluteMinValue() == 0)
			seekBar.setDateRangeWithSelectionDelta(anno.getStart(), anno.getEnd(), 0d);
		if(seekBar.getAbsoluteMinValue() > anno.getStart().getTime())
			seekBar.setDateRangeWithSelectionDelta(anno.getStart(), new Date(seekBar.getAbsoluteMaxValue()), 0d);
		if(seekBar.getAbsoluteMaxValue() < anno.getEnd().getTime())
			seekBar.setDateRangeWithSelectionDelta(new Date(seekBar.getAbsoluteMinValue()), anno.getEnd(), 0d);
		
		long minSelected = (seekBar.getAbsoluteMaxValue() - seekBar.getAbsoluteMinValue() > 12 * 60 * 60 * 1000 ? 
				seekBar.getAbsoluteMaxValue() - 12 * 60 * 60 * 1000 : 
					seekBar.getAbsoluteMinValue());
				
		seekBar.setSelectedMaxValue(seekBar.getAbsoluteMaxValue());
		seekBar.setSelectedMinValue(minSelected);
		
		((TextView)this.findViewById(R.id.txtFrom)).setText(DateFormat.format("MMM dd, h:mm:ssaa", new Date(seekBar.getSelectedMinValue())).toString());
		((TextView)this.findViewById(R.id.txtTo)).setText(DateFormat.format("MMM dd, h:mm:ssaa", new Date(seekBar.getSelectedMinValue())).toString());
		
		//Log.i(TAG, seekBar.getAbsoluteMinValue().toString());
	}
	*/
	
	private Date minDate = new Date(System.currentTimeMillis());
	private Date maxDate = new Date(System.currentTimeMillis());
	private Date minSelectionDate = new Date(System.currentTimeMillis());
	private Date maxSelectionDate = new Date(System.currentTimeMillis());
	
	public void renderAnnotation(Annotation anno){
		if(anno.getDBId() != -1){
			if(this.renderedActivities.containsKey(anno.getDBId())){
				// this is an activity that already rendered.
				return;
				
			}else{
				this.renderedActivities.put(anno.getDBId(), anno.toString());
			}
		}
		
		if(minDate == null)
			minDate = anno.getStart();
		if(maxDate == null)
			maxDate = anno.getEnd();
		if(anno.getStart().getTime() < minDate.getTime())
			minDate = anno.getStart();
		if(anno.getEnd().getTime() > maxDate.getTime())
			maxDate = anno.getEnd();
		

		if(anno.getLocations().getDataSize() > 0){
			if(minSelectionDate == null)
				minSelectionDate = anno.getStart();
			if(maxSelectionDate == null)
				maxSelectionDate = anno.getEnd();
			
			if(anno.getEnd().getTime() > maxSelectionDate.getTime()){
				maxSelectionDate = anno.getEnd();
				minSelectionDate = anno.getStart();
			}
			
			
			LinkedList<double[]> locations = anno.getLocations().getData();
			//double[] locationWidthLastPoint = locations.get(locations.size() - 1);
			
			
			
			if(locations.size() > 0){
				GeoPathOverlay path = new GeoPathOverlay(anno, anno.getColor());
				AnnotationOverlayMarker overlayItem = new AnnotationOverlayMarker(anno);
				
		        
		        this.annotationOverlays.add(overlayItem);
		        this.polylineOverlays.add(path);

			}
		}
	}
	
	
	public void resetMap(){
		super.resetMap();
		
		this.annotationOverlays.clear();
		this.polylineOverlays.clear();
		
	}

}
