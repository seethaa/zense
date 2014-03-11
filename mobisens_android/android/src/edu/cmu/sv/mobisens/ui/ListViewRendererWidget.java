package edu.cmu.sv.mobisens.ui;

import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;

import android.app.ListActivity;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.text.format.DateFormat;

import edu.cmu.sv.lifelogger.api.ActivityRecognitionRequestScheduler;
import edu.cmu.sv.lifelogger.util.NGramModel;
import edu.cmu.sv.mobisens.R;
import edu.cmu.sv.mobisens.content.ActivityReader;
import edu.cmu.sv.mobisens.content.Widget;
import edu.cmu.sv.mobisens.io.MobiSensLog;
import edu.cmu.sv.mobisens.util.Annotation;

public class ListViewRendererWidget extends RendererWidget {
	private ListActivity renderer;
	//private ImageListViewAdapter adapter;
	private ActivityListAdapter adapter;

	private Hashtable<String, String> renderedActivities = new Hashtable<String, String>();
	
	public void beforeRegistered(ContextWrapper contextWrapper){
		if(!(contextWrapper instanceof ListActivity)){
			throw new IllegalArgumentException("context must be an instance of ListActivity.");
		}
		
		this.renderer = (ListActivity)contextWrapper;
		adapter = new ActivityListAdapter(this.renderer, 
        		R.id.item_title,
        		new ArrayList<HeaderListViewItem>());
        
        this.renderer.setListAdapter(adapter);
	}
	
	protected void onDataChanged(){
		
		try{
			if(this.renderer != null){
				this.renderer.getListView().invalidate();
			}
		}catch(Exception ex){
			ex.printStackTrace();
			MobiSensLog.log(ex);
		}
	}
	
	
	public void unregister(){
		this.renderer = null;
		this.adapter = null;
		
		super.unregister();
	}
	
	protected void onClear(Intent intent){
		super.onClear(intent);
		this.renderedActivities.clear();
		this.adapter.clear();
	}
	
	protected void onRenderItem(Intent intent){
		
		if(this.adapter == null)
			return;
		
		String annoString = intent.getStringExtra(Annotation.EXTRA_ANNO_STRING);
		Annotation anno = Annotation.fromString(annoString);
		
		if(anno == null)
			return;
		
		String timeBoundariesString = anno.getStart().getTime() + "_" + anno.getEnd().getTime();
		if(this.renderedActivities.containsKey(timeBoundariesString)){
			// this is an activity that already rendered.
			return;
			
		}
		
		ActivityRecognitionRequestScheduler.push(anno);
        HeaderListViewItem newItem = new HeaderListViewItem(anno, anno.getColor());
        //newItem.setColor(color);

        this.adapter.insert(newItem, 0);
        
        //this.adapter.add(newItem);

	}
	
	protected boolean onUpdateItem(Intent intent){
		
		if(this.adapter == null)
			return true;
		
		String annoString = intent.getStringExtra(Annotation.EXTRA_ANNO_STRING);
		Annotation updatedAnnotation = Annotation.fromString(annoString);
		if(updatedAnnotation == null)
			return true;
		
		boolean appendResult = false;
		
		
		int length = this.adapter.getCount();
		for(int i = 0; i < length; i++){
			HeaderListViewItem item = (HeaderListViewItem)this.adapter.getItem(i);
			if(item == null)
				continue;
			if(item.getAnnotation().getDBId() == updatedAnnotation.getDBId()){
				item.setAnnotation(updatedAnnotation);
				item.setColor(updatedAnnotation.getColor());
				
				this.renderer.getListView().invalidateViews();
				appendResult = true;
			}
		}
		
		return appendResult;
	}
//	
//	protected void onDone(Intent intent){
//		this.renderer.getListView().invalidateViews();
//	}


	@Override
	public String getRenderType() {
		// TODO Auto-generated method stub
		return this.getClass().getName();
	}

	@Override
	protected String[] getActions() {
		// TODO Auto-generated method stub
		return new String[0];
	}
}
