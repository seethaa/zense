package edu.cmu.sv.mobisens.content;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import edu.cmu.sv.mobisens.MobiSensService;
import edu.cmu.sv.mobisens.io.MobiSensLog;

public class WifiWidget extends SystemWidget {
	
	private WifiManager mWifi;
	
	public static final String WIFISCAN_TYPE = "wifiscan";
    public static final String WIFIACINFO_TYPE = "wifi_accesspoint_info";
    
    public static final String JSONARRAY_KEY = "list";
    public static final String JSON_BSSID_KEY = "BSSID";
    public static final String JSON_SSID_KEY = "SSID";
    public static final String JSON_SIGNAL_STRENGTH_KEY = "level";
    
	
	public void register(ContextWrapper contextWrapper){
		mWifi = (WifiManager)contextWrapper.getSystemService(Context.WIFI_SERVICE);
		
		IntentFilter filter = new IntentFilter();
		filter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
		this.setFilter(filter);
		
		super.register(contextWrapper);
		
		
		
	}
	
	
	@Override
    public void onReceive(Context context, Intent intent)
    {
        String action = intent.getAction();
        
        if (action.equals(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION))
        {
            List<ScanResult> results = mWifi.getScanResults();
            
            if(results == null)
            	return;

            HashMap<String, Integer> scanRes = 
                new HashMap<String, Integer>();
            
            
            JSONObject apListObject = new JSONObject();
            JSONArray apList = new JSONArray();
            HashSet<String> currentAccessPoints = new HashSet<String>();
            
            for (ScanResult result : results)
            {
            	try
            	{
                	JSONObject ap = new JSONObject();
                	ap.put("SSID", result.SSID);
                	ap.put("BSSID", result.BSSID);
                	ap.put("capacities", result.capabilities);
                	ap.put("frequency", String.valueOf(result.frequency));
                	ap.put("level", String.valueOf(result.level));
                	apList.put(ap);
                	
                	currentAccessPoints.add(result.BSSID);
                	
            	}catch(Exception ex){
            		MobiSensLog.log(ex);
            	}
            	
            	
                scanRes.put(result.BSSID, result.level);
               
            }
            
            if(apList != null && apListObject != null){
            	try {
					apListObject.put("list", apList);
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
            }

            String dataRecord = constructDataRecord( 
                        scanRes.toString(), 
                        WIFISCAN_TYPE,
                        getDeviceID());
            
            this.broadcastDataRecord(dataRecord, WIFISCAN_TYPE);
            
            dataRecord = constructDataRecord( 
                    apListObject.toString(), 
                    WIFIACINFO_TYPE,
                    getDeviceID());
            
            this.broadcastDataRecord(dataRecord, WIFIACINFO_TYPE);
            
        }


    }
}
