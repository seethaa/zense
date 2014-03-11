package edu.cmu.sv.mobisens.ui;

import java.util.LinkedList;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import edu.cmu.sv.mobisens.R;
import edu.cmu.sv.mobisens.util.Annotation;

public class AnnotationOverlayMarker implements MapOverlay<Marker> {

	private Annotation anno;
	private Marker marker;
	private BitmapDescriptor icon;
	
	public AnnotationOverlayMarker(Annotation anno) {
		// TODO Auto-generated constructor stub
		this.setAnnotation(anno);
		this.icon = BitmapDescriptorFactory.fromResource(R.drawable.geo_marker);
	}
	
	public AnnotationOverlayMarker(Annotation anno, BitmapDescriptor icon) {
		// TODO Auto-generated constructor stub
		this.setAnnotation(anno);
		this.icon = icon;
	}
	
	public void setAnnotation(Annotation newAnno){
		this.anno = newAnno;
	}
	
	public Annotation getAnnotation(){
		return this.anno;
	}
	
	public long getStartTime() {
		return anno.getStart().getTime();
	}


	public long getEndTime() {
		return anno.getEnd().getTime();
	}


	public int getColor() {
		return this.anno.getColor();
	}
	
	private double[] getCentroid(LinkedList<double[]> locations) {

		double minLat = 0;
	    double minLng = 0;
	    double maxLat = 0;
	    double maxLng = 0;

	    int length = locations.size();
	    
	    for (int i = 0; i< length; i++) {
	    	double[] latlng = locations.get(i);
	    	
	    	if(i == 0){
	    		minLat = latlng[0];
	    		maxLat = latlng[0];
	    		minLng = latlng[1];
	    		maxLng = latlng[1];
	    	}else{
	    	
	    	//if(i % 2 == 0){
		        minLat = Math.min(latlng[0], minLat);
		        maxLat = Math.max(latlng[0], maxLat);
	    	//}else{
	        
		        minLng = Math.min(latlng[1], minLng);
		        maxLng = Math.max(latlng[1], maxLng);
	    	//}
	    	}
	    }
	    
	    double[] centroid = new double[]{(maxLat + minLat) / 2, (maxLng + minLng) / 2};
	    return centroid;
	}
	
	private String getSnippet(){
		return "From: " + anno.getStart().toLocaleString() + "\r\nTo: " + anno.getEnd().toLocaleString();
	}

	@Override
	public Marker draw(GoogleMap map) {
		// TODO Auto-generated method stub
		double[] centroid = getCentroid(anno.getLocations().getData());
		
		
		if(marker == null){
			marker = map.addMarker(new MarkerOptions()
		        .icon(this.icon)
		        .position(new LatLng(centroid[0], centroid[1]))
		        .title(anno.getName())
		        .snippet(this.getSnippet())
		        );
		}else{
			marker.setTitle(anno.getName());
			marker.setSnippet(this.getSnippet());
			marker.setPosition(new LatLng(centroid[0], centroid[1]));
		}
		
		
		return marker;
	}
	
	public boolean equals(Marker marker){
		return marker.equals(this.marker);
	}

	@Override
	public void removeFromMap() {
		// TODO Auto-generated method stub
		if(this.marker != null)
			marker.remove();
		marker = null;
	}

	@Override
	public boolean isRendered() {
		// TODO Auto-generated method stub
		return marker != null;
	}

}
