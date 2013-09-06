package edu.cmu.sv.mobisens.util;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Date;
import java.util.Hashtable;
import java.util.LinkedList;

import android.util.Log;

import edu.cmu.sv.mobisens.net.HttpGetRequest;
import edu.cmu.sv.mobisens.net.HttpPostRequest;
import edu.cmu.sv.mobisens.net.URLs;

public class Sharing {
	
	private final static String TAG = "Sharing";
	private static long lastLocationUploadTime = 0;
	
	public interface OnProcessCompleted {
        void onSuccessed(String text);
        void onError();
    }
	
	public static void shareLocation(final String device_id, final double latitude, final double longitude){
		
		long currentTime = new Date().getTime();
		
		if(lastLocationUploadTime == 0)
			lastLocationUploadTime = currentTime;
		if(currentTime - lastLocationUploadTime < 60 * 1000)
			return;
		
		
		Thread uploadThread = new Thread(){
			public void run(){
				HttpGetRequest getRequest = new HttpGetRequest();
				Hashtable<String, String> params = new Hashtable<String, String>();
				
				params.put("key", "pang.wu@sv.cmu.edu");
				params.put("device_id", device_id);
				params.put("lat", String.valueOf(latitude));
				params.put("lng", String.valueOf(longitude));
				params.put("timestamp", String.valueOf(new Date().getTime()));
				
				
				getRequest.send(URLs.SHARE_LOCATION_URL, params);
			}
		};
		
		uploadThread.start();
		lastLocationUploadTime = currentTime;
	}
	
	
	public static void shareActivity(final String device_id, final String activity, final long timestamp){

		Thread uploadThread = new Thread(){
			public void run(){
				HttpGetRequest getRequest = new HttpGetRequest();
				Hashtable<String, String> params = new Hashtable<String, String>();
				
				params.put("key", "pang.wu@sv.cmu.edu");
				params.put("device_id", device_id);
				try {
					params.put("activity", URLEncoder.encode(activity.replace(",", "")
							.replace(";", ""), "utf-8"));
				} catch (UnsupportedEncodingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				params.put("timestamp", String.valueOf(timestamp));
				
				
				String shareResult = getRequest.send(URLs.SHARE_ACTIVITY_URL, params);
				Log.i(TAG, "--- Share result: " + shareResult);
			}
		};
		
		uploadThread.start();
	}
	
	
	public static void bundleShare(final String device_id, 
			final String activity,
			final LinkedList<double[]> locations,
			final long start_time,
			final long end_time,
			final OnProcessCompleted handler
			){
		Thread uploadThread = new Thread(){
			public void run(){
				HttpPostRequest postRequest = new HttpPostRequest();
				Hashtable<String, String> params = new Hashtable<String, String>();
				
				params.put("key", "pang.wu@sv.cmu.edu");
				params.put("device_id", device_id);
				try {
					params.put("activity", URLEncoder.encode(activity.replace(",", "")
							.replace(";", ""), "utf-8"));
				} catch (UnsupportedEncodingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				params.put("start_time", String.valueOf(start_time));
				params.put("end_time", String.valueOf(end_time));
				
				StringBuilder locationString = new StringBuilder();
				int locationLength = locations.size();
				
				for(int i = 0; i < locationLength; i++){
					
					if(i == 0)
						locationString.append(locations.get(i)[0])
						.append(",").append(locations.get(i)[1]);
					else
						locationString.append(",").append(locations.get(i)[0])
						.append(",").append(locations.get(i)[1]);
				}
				params.put("location", locationString.toString());
				
				String shareResult = postRequest.send(URLs.BUNDLE_SHARE_URL, params);
				Log.i(TAG, "--- Bundle Share result: " + shareResult);
				
				if(shareResult != null){
					if(shareResult.length() > 0){
						handler.onSuccessed(shareResult);
						return;
					}
				}
				
				handler.onError();
			}
		};
		
		uploadThread.start();
	}
	
	
	public static void createSharingSession(final String device_id, final OnProcessCompleted handler){
		Thread uploadThread = new Thread(){
			public void run(){
				HttpGetRequest getRequest = new HttpGetRequest();
				Hashtable<String, String> params = new Hashtable<String, String>();
				
				params.put("key", "pang.wu@sv.cmu.edu");
				params.put("device_id", device_id);
				
				
				String shareResult = getRequest.send(URLs.CREATE_SHARING_SESSION_URL, params);
				Log.i(TAG, "--- Share result: " + shareResult);
				
				if(shareResult.length() == 0)
					handler.onError();
				else
					handler.onSuccessed(shareResult);
			}
		};
		
		uploadThread.start();
	}
	
	public static void getSharingSessions(final String device_id, final OnProcessCompleted handler){
		Thread uploadThread = new Thread(){
			public void run(){
				HttpGetRequest getRequest = new HttpGetRequest();
				Hashtable<String, String> params = new Hashtable<String, String>();
				
				params.put("key", "pang.wu@sv.cmu.edu");
				params.put("device_id", device_id);
				
				
				String shareResult = getRequest.send(URLs.GET_SHARING_SESSIONsS_URL, params);
				Log.i(TAG, "--- Share result: " + shareResult);
				
				if(shareResult.length() == 0)
					handler.onError();
				else
					handler.onSuccessed(shareResult);
			}
		};
		
		uploadThread.start();
	}
	
	
	public static void joinSharingSession(final String device_id, final String sessionId, final OnProcessCompleted handler){
		Thread uploadThread = new Thread(){
			public void run(){
				HttpGetRequest getRequest = new HttpGetRequest();
				Hashtable<String, String> params = new Hashtable<String, String>();
				
				params.put("key", "pang.wu@sv.cmu.edu");
				params.put("device_id", device_id);
				params.put("session_id", sessionId);
				
				
				String shareResult = getRequest.send(URLs.JOIN_SHARING_SESSION_URL, params);
				Log.i(TAG, "--- Share result: " + shareResult);
				
				if(shareResult.length() == 0)
					handler.onError();
				else
					handler.onSuccessed(shareResult);
			}
		};
		
		uploadThread.start();
	}
	
	public static void leftSharingSession(final String device_id, final String sessionId, final OnProcessCompleted handler){
		Thread uploadThread = new Thread(){
			public void run(){
				HttpGetRequest getRequest = new HttpGetRequest();
				Hashtable<String, String> params = new Hashtable<String, String>();
				
				params.put("key", "pang.wu@sv.cmu.edu");
				params.put("device_id", device_id);
				params.put("session_id", sessionId);
				
				String shareResult = getRequest.send(URLs.LEFT_SHARING_SESSION_URL, params);
				Log.i(TAG, "--- Share result: " + shareResult);
				
				if(shareResult.length() == 0)
					handler.onError();
				else
					handler.onSuccessed(shareResult);
			}
		};
		
		uploadThread.start();
	}
}
