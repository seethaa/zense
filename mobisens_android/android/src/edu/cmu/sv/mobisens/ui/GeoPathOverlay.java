package edu.cmu.sv.mobisens.ui;

import java.util.LinkedList;

import android.graphics.Canvas;
import android.graphics.Color;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import edu.cmu.sv.mobisens.util.Annotation;

public class GeoPathOverlay implements MapOverlay<Polyline> {
	private Annotation anno;
	private int color = Color.RED;
	private Polyline polyline;
	
	public Annotation getAnnotation(){
		return this.anno;
	}
	
	public GeoPathOverlay(Annotation anno, int color){
		super();
		this.anno = anno;
		
		this.color = color;
		
		
    }   

	@Override
    public Polyline draw(GoogleMap mapView){
        /*super.draw(canvas, mapView, shadow);

        Projection projection = mapView.getProjection();
        Paint polygonPaint = new Paint();
        polygonPaint.setDither(true);
        polygonPaint.setColor(this.color);
        polygonPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        polygonPaint.setStrokeJoin(Paint.Join.ROUND);
        polygonPaint.setStrokeCap(Paint.Cap.ROUND);
        polygonPaint.setStrokeWidth(4);
        polygonPaint.setAntiAlias(true);
        polygonPaint.setAlpha(120);

        GeoPoint gP1 = null;
        GeoPoint currentPoint = null;
		*/
    	
        //int index = 0;
        LinkedList<double[]> points = this.anno.getLocations().getData();
        PolylineOptions lineOptions = new PolylineOptions();
        
        for(double[] latlng:points){
        	//int lat = (int) (latlng[0] * 1E6);
        	//int lng = (int) (latlng[1] * 1E6);
        	
        	LatLng point = new LatLng(latlng[0], latlng[1]);
        	lineOptions.add(point);
        	
        }
        
        lineOptions.color(color);
        lineOptions.width(4.0f);
        Polyline line = mapView.addPolyline(lineOptions);
        /*
        
        if(gP1 != null && index == 1){
        	drawPath(polygonPaint, canvas, projection, gP1, currentPoint);
    	}
    	*/
        this.polyline = line;
        return line;
        
    }

	@Override
	public void removeFromMap() {
		// TODO Auto-generated method stub
		if(this.polyline != null)
			this.polyline.remove();
		
		this.polyline = null;
	}

	@Override
	public boolean isRendered() {
		// TODO Auto-generated method stub
		return this.polyline != null;
	}
    
    /*
    private void drawPath(Paint pathPaint, Canvas canvas, Projection projection, GeoPoint gp1, GeoPoint gp2){
    	Point lastPoint = new Point();
        Point thisPoint = new Point();

        Path path = new Path();

        lastPoint = projection.toPixels(gp2, lastPoint);
        thisPoint = projection.toPixels(gp1, thisPoint);

        path.moveTo(thisPoint.x, thisPoint.y);
        path.lineTo(lastPoint.x, lastPoint.y);
        
        canvas.drawPath(path, pathPaint);
    }
	*/
}
