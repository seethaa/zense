package edu.cmu.sv.mobisens.content;

import edu.cmu.sv.mobisens.io.MobiSensLog;
import edu.cmu.sv.mobisens.ui.RendererWidget;
import edu.cmu.sv.mobisens.util.Annotation;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;

public class ActivityReader extends Widget {
	
	protected static final String TAG = "ActivityReader";
	
	private static final String CLASS_PREFIX = ActivityReader.class.getName();

	//public static final String EXTRA_ACTIVITY_NAME = CLASS_PREFIX + ".extra_activity_name";
	//public static final String EXTRA_LOCATION_DATA = CLASS_PREFIX + ".extra_location_data";
	public static final String EXTRA_RENDER_COLOR = CLASS_PREFIX + ".extra_color";
	//public static final String EXTRA_TIME_BOUNDARIES = CLASS_PREFIX + ".extra_time_boundaries";
	//public static final String EXTRA_INDEX = CLASS_PREFIX + ".extra_index";

	private long startTime = 0;
	private long endTime = 0;
	
	private String rendererType = RendererWidget.ALL_RENDERER_TYPES;
	
	private boolean abort = false;
	
	public ActivityReader(){
		
	}
	
	public ActivityReader(long startTime, long endTime){

		this.setStartTime(startTime);
		this.setEndTime(endTime);
	}
	
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		String action = intent.getAction();
		if(action.equals(AnnotationWidget.ACTION_READ_ALL_ANNO_DONE)){
			String annoStrings = intent.getStringExtra(AnnotationWidget.EXTRA_ANNO_STRINGS);
			String renderType = intent.getStringExtra(RendererWidget.EXTRA_RENDERER_TYPE);
			if(!this.getRendererType().equals(renderType))
				return;
			
			getActivity(annoStrings, renderType);
		}
		
	}
	
	
	public void unregister(){
		super.unregister();
	}

	private void setStartTime(long startTime) {
		this.startTime = startTime;
	}

	public long getStartTime() {
		return startTime;
	}

	private void setEndTime(long endTime) {
		this.endTime = endTime;
	}

	public long getEndTime() {
		return endTime;
	}

	
	public void readAsync(){
		
		Intent requestIntent = new Intent(AnnotationWidget.ACTION_GET_ALL_ANNOS);
		requestIntent.putExtra(RendererWidget.EXTRA_RENDERER_TYPE, this.getRendererType());
		this.getContext().sendBroadcast(requestIntent);
	}
	
	private void getActivity(String annoStrings, String rendererType){

		try {
			MobiSensLog.log("Parsing annotations...");
			
			Intent renderIntent = new Intent(RendererWidget.ACTION_RENDER_ITEMS);
			renderIntent.putExtra(AnnotationWidget.EXTRA_ANNO_STRINGS, "");
			renderIntent.putExtra(RendererWidget.EXTRA_RENDERER_TYPE, rendererType);
			
			this.getContext().sendBroadcast(renderIntent);
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			MobiSensLog.log(e);
		}
		
		MobiSensLog.log("Parse and broadcast annotation done.");
		
	}

	public void setRendererType(String rendererType) {
		this.rendererType = rendererType;
	}

	public String getRendererType() {
		return rendererType;
	}

	@Override
	protected String[] getActions() {
		// TODO Auto-generated method stub
		return new String[]{ AnnotationWidget.ACTION_READ_ALL_ANNO_DONE };
	}
}
