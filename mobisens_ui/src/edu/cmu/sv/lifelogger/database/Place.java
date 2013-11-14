package edu.cmu.sv.lifelogger.database;

import com.google.android.gms.maps.model.LatLng;

//http://www.mkyong.com/java/jaxb-hello-world-example/
/**
 * Represents a single Result of a places API call
 * https://developers.google.com/places/documentation/search
 */
public class Place {
    public String vicinity;
    public float[] geometry; //array(0 => lat, 1 => lng)
    public LatLng point ;  //-- convenient variable to return point as latlng(will be same as geometry 
	public String id;
    public String name;			//Address
    public float rating;
    public String reference;
    public String[] types;
    
    public Place(){
    	super();
    }
    public Place(String name){
    	this.name = name;
    }
    public String getVicinity() {
        return vicinity;
    }

    public void setVicinity(String vicinity) {
        this.vicinity = vicinity;
    }

    public float[] getGeometry() {
        return geometry;
    }

    public void setGeometry(float[] geometry) {
        this.geometry = geometry;
        // Now also set the new point in LatLng -- convenience method
        LatLng newPoint = new LatLng(geometry[0], geometry[1]);
        this.setPoint(newPoint);
    }

    public LatLng getPoint() {
		return point;
	}
	public void setPoint(LatLng point) {
		this.point = point;
	}

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public String[] getTypes() {
        return types;
    }

    public void setTypes(String[] types) {
        this.types = types;
    }
}

