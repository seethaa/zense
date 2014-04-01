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
	public double latitude;
	public double longitude;
	public long timestamp;
	

}
