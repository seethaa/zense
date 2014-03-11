package edu.cmu.sv.lifelogger.api;

import org.json.JSONObject;

public class FakeLocation {
	private double lat;
	private double lng;
	private long timestamp;
	
	public static final String LAT = "latitude";
	public static final String LNG = "longitude";
	public static final String TIMESTAMP = "timestamp";
	
	public FakeLocation(double lat, double lng, long timestamp){
		this.lat = lat;
		this.lng  = lng;
		this.timestamp = timestamp;
	}
	
	public JSONObject toJSON(){
		JSONObject json = new JSONObject();
		try{
			json.put(LAT, this.lat);
			json.put(LNG, this.lng);
			json.put(TIMESTAMP, this.timestamp);
		}catch(Exception ex){
			ex.printStackTrace();
		}
		
		return json;
		
	}

}
