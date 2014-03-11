package edu.cmu.sv.mobisens.ui;

import com.google.android.gms.maps.GoogleMap;

public interface MapOverlay<T> {
	
	/*
	 * Draw the overlay on the map
	 */
	T draw(GoogleMap map);
	void removeFromMap();
	boolean isRendered();

}
