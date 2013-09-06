package edu.cmu.sv.lifelogger.api;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.LinkedList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import edu.cmu.sv.lifelogger.util.DataCollector;
import edu.cmu.sv.mobisens.io.CacheItem;
import edu.cmu.sv.mobisens.io.DataShrinker;
import edu.cmu.sv.mobisens.io.Directory;
import edu.cmu.sv.mobisens.io.FileCache;
import edu.cmu.sv.mobisens.io.FileCache.FileSerializer;
import edu.cmu.sv.mobisens.io.FileOperation;
import edu.cmu.sv.mobisens.io.MobiSensLog;
import edu.cmu.sv.mobisens.net.HttpPostRequest;
import edu.cmu.sv.mobisens.net.URLs;

public class LocationBasedRecognizer {
	
	private final static String CLASS_PREFIX = LocationBasedRecognizer.class.getName();
	private final static String TAG = CLASS_PREFIX;
	
	private static FileSerializer<String, String> serializer = new FileSerializer<String, String>(){
		public static final String KEY_KEY = "key";
		public static final String KEY_VALUE = "value";
		public static final String KEY_TIMESTAMP = "create_time";
		private final static String CACHE_FILE_NAME = "activity_cache.csv";
		
		private volatile File file;
		
		@Override
		public HashMap<String, CacheItem<String>> Serialize() {
			// TODO Auto-generated method stub
			HashMap<String, CacheItem<String>> data = new HashMap<String, CacheItem<String>>();
			
			try {
				file = Directory.openFile(Directory.MOBISENS_ROOT, CACHE_FILE_NAME);
				ArrayList<String> lines = FileOperation.getLastNLines(file, -1);
				for(String line:lines){
					try {
						JSONObject cacheItemInJSON = new JSONObject(line);
						CacheItem<String> item = new CacheItem<String>(cacheItemInJSON.getString(KEY_VALUE), 
								cacheItemInJSON.getLong(KEY_TIMESTAMP),
								DataShrinker.RESERVE_TIME / 3
								);
						data.put(cacheItemInJSON.getString(KEY_KEY), item);
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				MobiSensLog.log(e);
			}
			return data;
		}

		@Override
		public File Deserialize(HashMap<String, CacheItem<String>> data) {
			// TODO Auto-generated method stub
			BufferedWriter bufferWritter = null;
			try {
				FileWriter fileWritter = new FileWriter(file, false);
				bufferWritter = new BufferedWriter(fileWritter, 100 * 1024);
				
				for(String key:data.keySet()){
					JSONObject cacheItemInJSON = new JSONObject();
					try {
						cacheItemInJSON.put(KEY_KEY, key);
						cacheItemInJSON.put(KEY_VALUE, data.get(key).getContent());
						cacheItemInJSON.put(KEY_TIMESTAMP, data.get(key).getCreatedTime());
						
						try{
							
					        bufferWritter.append(cacheItemInJSON.toString());
					        bufferWritter.append("\r\n");
					        
						}catch(Exception ex){
							ex.printStackTrace();
							MobiSensLog.log(ex);
							
						}
						
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						MobiSensLog.log(e);
					}
					
				}
				
				bufferWritter.close();
				
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
				MobiSensLog.log(e1);
			}
			
			return file;
		}
		
	};
	
	private volatile static FileCache<String, String> cache = new FileCache<String, String>(serializer);
	
	/*
	 * This is ugly, feel free to redesign.
	 */
	public static void FlushCache(){
		cache.flush();
	}
	
	private DataCollector<double[]> data;
	private String deviceId = "";
	
	
	
	public LocationBasedRecognizer(String deviceId, DataCollector<double[]> data){
		this.data = data;
		this.deviceId = deviceId;
	}
	
	public String recognize(){
		// see if we need to clear the cache.
		
		long timeSpan = data.getLastDataCollectedTime() - data.getFirstDataCollectedTime();
		int dataLength = data.getDataSize();
		String activityName = "";
		
		if(dataLength > 0){
			long timeStep = timeSpan / dataLength;
			JSONArray locationArray = new JSONArray();
			LinkedList<double[]> dataClone = data.getData();
			
			int index = 0;
			for(double[] location:dataClone){
				long timeStamp = 0;
				if(location.length < 3){
					// No timestamp, old version
					timeStamp = data.getFirstDataCollectedTime() + timeStep * index;
				}else{
					timeStamp = (long) (location[2] * 1000);
				}
				
				FakeLocation locationObject = new FakeLocation(location[0], location[1], timeStamp);
				locationArray.put(locationObject.toJSON());
			}
			
			String requestJSON = locationArray.toString();
			//Log.i(TAG, locationArray.toString());
			activityName = cache.get(requestJSON);
			if(activityName != null){
				Log.i(TAG, "Cache hit!");
				
			}else{
				activityName = "";
				
				HttpPostRequest post = new HttpPostRequest();
				Hashtable<String, String> params = new Hashtable<String, String>();
				params.put("device_id", this.deviceId);
				params.put("locations", requestJSON);
				String resultJSONString = post.send(URLs.LOCATION_BASED_RECOGNITION_URL, params);
				if(resultJSONString != null){
					Log.i(TAG, resultJSONString);
					try {
						JSONObject resultJSON = new JSONObject(resultJSONString);
						activityName = resultJSON.getString("type");
						
						
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} finally {
						cache.add(requestJSON, activityName, DataShrinker.RESERVE_TIME / 3);
					}
				}
				
				
			}
			
		}
		
		cache.swapExpired();
		return activityName;
	}

}
