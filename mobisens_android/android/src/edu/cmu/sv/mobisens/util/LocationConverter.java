package edu.cmu.sv.mobisens.util;

public class LocationConverter {
	private static final String TAG = "LifeloggerLocationConverter";
	
	
	public static edu.cmu.sv.lifelogger.util.Location toLifeloggerLocation(
			android.location.Location androidLocation
			){
		edu.cmu.sv.lifelogger.util.Location lifeloggerLocation = new edu.cmu.sv.lifelogger.util.Location(
				androidLocation.getLatitude(),
				androidLocation.getLongitude(),
				androidLocation.getAltitude(),
				androidLocation.getSpeed()
		);
		
		return lifeloggerLocation;
	}
	
	public android.location.Location toAndroidLocation(
			edu.cmu.sv.lifelogger.util.Location lifeloggerLocation
			){
		android.location.Location androidLocation = new android.location.Location(TAG);
		androidLocation.setAltitude(lifeloggerLocation.getAltitude());
		androidLocation.setLatitude(lifeloggerLocation.getLatitude());
		androidLocation.setLongitude(lifeloggerLocation.getLongitude());
		androidLocation.setSpeed(lifeloggerLocation.getSpeed());
		
		return androidLocation;
	}
}
