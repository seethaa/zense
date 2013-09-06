package edu.cmu.sv.mobisens.net;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.Hashtable;

import android.util.Log;

public class HttpGetRequest {
	private static final String TAG = "HttpGetRequest";
	
	public HttpGetRequest(){
		
	}
	
	public String send(String baseURL, Hashtable<String, String> params){
		String result = "";
		HttpURLConnection conn = null;
		
		try
		{
			// Send data
			String urlStr = baseURL;
			boolean isFirstParam = true;
			for(String param:params.keySet()){
				if(isFirstParam){
					urlStr += "?" + param + "=" + params.get(param);
					isFirstParam = false;
				}else{
					urlStr += "&" + param + "=" + params.get(param);
				}
			}
			
			URL url = new URL(urlStr);
			conn = (HttpURLConnection) url.openConnection();
	
			// Get the response
			BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			StringBuffer stringBuffer = new StringBuffer();
			String line;
			while ((line = reader.readLine()) != null){
				stringBuffer.append(line);
			}
			
			reader.close();
			result = stringBuffer.toString();
		
		} catch (Exception e) {
			Log.e(TAG, e.getMessage(), e);
		}finally{
			conn.disconnect();
		}
		
		return result;
	}

}
