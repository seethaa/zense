package edu.cmu.sv.mobisens.ui;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.LinkedList;

import com.google.android.gms.maps.GoogleMap.InfoWindowAdapter;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.model.Marker;
import android.content.ContextWrapper;
import android.content.Intent;
import android.text.SpannableString;
import android.text.format.DateFormat;
import android.text.style.UnderlineSpan;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;
import edu.cmu.sv.lifelogger.api.ActivityRecognitionRequestScheduler;
import edu.cmu.sv.mobisens.R;
import edu.cmu.sv.mobisens.util.Annotation;

public class MapViewRendererWidget extends RendererWidget {

	private MapAnnotationListActivity renderer;
	private Double[] lastLatLng = new Double[2];
	
	private int renderedItemCount = 0;
	
	
	protected void beforeRegistered(ContextWrapper contextWrapper){
		if(!(contextWrapper instanceof MapAnnotationListActivity)){
			throw new IllegalArgumentException("context must be an instance of BaseMapActivity.");
		}
		
		this.renderer = (MapAnnotationListActivity) contextWrapper;
	}
	
	protected int getProgressiveRenderItemCount(){
		return 30000;
	}
	
	protected long getProgressiveRenderSleepInterval(){
		return 1;
	}
	
	protected void onDone(Intent intent){
		this.renderer.reDraw();
		
	}
	
	protected void onClear(Intent intent){
		super.onClear(intent);
		
		this.renderer.resetMap();
	}
	
	protected void onPrepareRendering(){

		this.renderer.getMapView().clear();
		this.renderedItemCount = 0;
	}

	
	protected void onRenderItem(Intent intent){
		String annoString = intent.getStringExtra(Annotation.EXTRA_ANNO_STRING);
		//int color = intent.getIntExtra(ActivityReader.EXTRA_RENDER_COLOR, Color.BLACK);
		
		this.renderedItemCount++;
		Annotation anno = Annotation.fromString(annoString);
		this.renderer.renderAnnotation(anno);
		
	}
	
	protected boolean onUpdateItem(Intent intent){
		
		String annoString = intent.getStringExtra(Annotation.EXTRA_ANNO_STRING);
		Annotation updatedAnno = Annotation.fromString(annoString);
		this.renderer.updateRendering(updatedAnno);
		
		return false;
	}


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
