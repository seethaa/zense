package edu.cmu.sv.mobisens.settings;

import android.content.Context;
import android.location.LocationManager;
import android.net.wifi.WifiManager;

public class SystemServiceStatus {
	public static boolean isGPSEnabled(Context context){
		LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
		if(locationManager == null)
			return false;
		return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
	}
	
	public static boolean isNetworkLocationEnabled(Context context){
		LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
		if(locationManager == null)
			return false;
		return locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
	}
	
	public static boolean isWIFIEnabled(Context context){
		WifiManager wifiManager = (WifiManager)context.getSystemService(Context.WIFI_SERVICE);
		if(wifiManager == null)
			return false;
		return wifiManager.isWifiEnabled();
	}
}
