package edu.cmu.sv.mobisens.util;

import edu.cmu.sv.mobisens.io.MobiSensLog;
import android.location.Location;

public class LocationFilter {
	private Location currentBestLocation = null;
	
	public boolean isBetterLocation(Location location, long maxInterval) {
	    if (currentBestLocation == null) {
	        // A new location is always better than no location
	    	
	    	this.currentBestLocation = location;
	        return true;
	    }

	    // Check whether the new location fix is newer or older
	    long timeDelta = location.getTime() - currentBestLocation.getTime();
	    boolean isSignificantlyNewer = timeDelta > maxInterval;
	    boolean isSignificantlyOlder = timeDelta < -maxInterval;
	    boolean isNewer = timeDelta > 0;

	    // If it's been more than two minutes since the current location, use the new location
	    // because the user has likely moved
	    if (isSignificantlyNewer) {
	    	this.currentBestLocation = location;
	        return true;
	    // If the new location is more than two minutes older, it must be worse
	    } else if (isSignificantlyOlder) {
	    	MobiSensLog.log("Location too old.");
	        return false;
	    }

	    // Check whether the new location fix is more or less accurate
	    int accuracyDelta = (int) (location.getAccuracy() - currentBestLocation.getAccuracy());
	    boolean isLessAccurate = accuracyDelta > 0;
	    boolean isMoreAccurate = accuracyDelta < 0;
	    boolean isSignificantlyLessAccurate = accuracyDelta > 200;

	    // Check if the old and new location are from the same provider
	    boolean isFromSameProvider = isSameProvider(location.getProvider(),
	            currentBestLocation.getProvider());

	    // Determine location quality using a combination of timeliness and accuracy
	    if (isMoreAccurate) {
	    	this.currentBestLocation = location;
	        return true;
	    } else if (isNewer && !isLessAccurate) {
	    	this.currentBestLocation = location;
	        return true;
	    } else if (isNewer && !isSignificantlyLessAccurate && isFromSameProvider) {
	    	this.currentBestLocation = location;
	    	return true;
	    }
	    return false;
	}

	/** Checks whether two providers are the same */
	private boolean isSameProvider(String provider1, String provider2) {
	    if (provider1 == null) {
	      return provider2 == null;
	    }
	    return provider1.equals(provider2);
	}
}
