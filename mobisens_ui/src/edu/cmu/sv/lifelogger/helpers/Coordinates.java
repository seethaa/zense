/*
 * Coordinates.java
 *
 *  Created on: Mar 30, 2014
 *      Author: Himanshu
 */
package edu.cmu.sv.lifelogger.helpers;

/**
 * @author Himanshu
 * 
 */
public class Coordinates {
	public Coordinates(double latitude, double longitude) {
		this.latitude = latitude;
		this.longitude = longitude;
	}
	public Coordinates() {
		super();
	}
	public double getLatitude() {
		return latitude;
	}
	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}
	public double getLongitude() {
		return longitude;
	}
	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}
	public long getTimestamp() {
		return timestamp;
	}
	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}
	public double latitude;
	public double longitude;
	public long timestamp;
	

}
