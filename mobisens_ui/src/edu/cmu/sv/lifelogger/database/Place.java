package edu.cmu.sv.lifelogger.database;

//http://www.mkyong.com/java/jaxb-hello-world-example/
/**
 * Represents a single Result of a places API call
 * https://developers.google.com/places/documentation/search
 */
public class Place {
    public String vicinity;
    public float[] geometry; //array(0 => lat, 1 => lng)
    public String id;
    public String name;
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

