package edu.cmu.sv.mobisens.net;

import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.URL;
import java.nio.ByteOrder;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.util.Log;

public class Network {
	
	private static final String TAG = "NetworkUtil";
	
	public static boolean isNetworkConnected(Context context){
		ConnectivityManager cm =
	        (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
	    NetworkInfo netInfo = cm.getActiveNetworkInfo();
	    if (netInfo != null && netInfo.isConnectedOrConnecting()) {
	        return true;
	    }
	    return false;
	}
	
	public static boolean isWificonnected(Context context){
    	ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
    	if(!ConnectivityManager.isNetworkTypeValid(ConnectivityManager.TYPE_WIFI))
    		return false;
    	NetworkInfo netInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
    	if (netInfo != null && netInfo.isConnectedOrConnecting()) {
	        return true;
	    }
    	return false;
    }
	
	public static boolean canConnectToServer(String url){
		HttpURLConnection conn = null;
		
		try{
			URL mURL = new URL(url);
			conn = (HttpURLConnection) mURL.openConnection();
			conn.connect();
			
			
		}catch(Exception ex){
			//Log.i(TAG, "Cannot connect to " + url);
			return false;
		}finally{
			if(conn != null){
				conn.disconnect();
			}
		}
		
		//Log.i(TAG, "Connection test to " + url + " succeeded.");
		return true;
	}
	
	public static String getWifiInterfaceName(Context context){
    	
    	//Crappy Android!
    	
    	WifiManager wifiManager = (WifiManager)context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
    	WifiInfo wifiInfo = wifiManager.getConnectionInfo();
    	int wifiIPAddress = wifiInfo.getIpAddress();
    	
    	byte[] bytes = new byte[4];
    	
    	if(ByteOrder.nativeOrder() == ByteOrder.LITTLE_ENDIAN){
    		bytes[0] = (byte) (wifiIPAddress & 0x0000ff);
    		bytes[1] = (byte) ((wifiIPAddress >> 8) & 0x0000ff);
    		bytes[2] = (byte) ((wifiIPAddress >> 16) & 0x0000ff);
    		bytes[3] = (byte) ((wifiIPAddress >> 24) & 0x0000ff);
    	}
    	
    	if(ByteOrder.nativeOrder() == ByteOrder.BIG_ENDIAN){
    		bytes[3] = (byte) (wifiIPAddress & 0x0000ff);
    		bytes[2] = (byte) ((wifiIPAddress >> 8) & 0x0000ff);
    		bytes[1] = (byte) ((wifiIPAddress >> 16) & 0x0000ff);
    		bytes[0] = (byte) ((wifiIPAddress >> 24) & 0x0000ff);
    	}
    	
    	InetAddress address;
		try {
			address = InetAddress.getByAddress(bytes);
			NetworkInterface wifiInterface = NetworkInterface.getByInetAddress(address);
			if(wifiInterface != null)
	    		return wifiInterface.getName();
		} catch (Exception ex) {
			Log.e(TAG, "Exception", ex);
		}
    	return "";
    	
    }
}
