package edu.cmu.sv.mobisens.content;

import edu.cmu.sv.mobisens.MobiSensService;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

public class NetworkInfoWidget extends SystemWidget {

	public static final String NET_TYPE = "network";
	
	public void register(ContextWrapper contextWrapper){
		IntentFilter filter = new IntentFilter();
		filter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
		filter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
		
		this.setFilter(filter);
		
		super.register(contextWrapper);
		
	}
	
	private String getWifiStateString(){
		String wifiState = "";
		ConnectivityManager connManager = (ConnectivityManager) getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
	    NetworkInfo networkInfo = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
	    WifiManager wifiManager = (WifiManager)getContext().getSystemService(Context.WIFI_SERVICE);
	    WifiInfo wifiInfo = wifiManager.getConnectionInfo();
	    
	    wifiState += "network_type," + networkInfo.getTypeName();
	    wifiState += ",network_subtype," + networkInfo.getSubtypeName();
	    wifiState += ",network_raoming," + String.valueOf(networkInfo.isRoaming());
	    wifiState += ",network_state," + networkInfo.getDetailedState().toString();
	    wifiState += ",wifi_mac_addr," + wifiInfo.getMacAddress();

	    if (networkInfo.isConnected()){
	    	int ipAddress = wifiInfo.getIpAddress();
	    	wifiState += ",wifi_ip_int," + Long.toString(ipAddress & 0xffffffffL);
	    	
	    	int intIp3 = ipAddress & 0x000000ff;		    	
	    	int intIp2 = (ipAddress & 0x0000ff00) >> 8;
	    	int intIp1 = (ipAddress & 0x00ff0000) >> 16;
	    	int intIp0 = (ipAddress & 0xff000000) >> 24;
		  
	    	wifiState += ",wifi_ip," +
	    	Long.toString(intIp3 & 0x000000ffL)
    		     + "." + Long.toString(intIp2 & 0x000000ffL)
    		     + "." + Long.toString(intIp1 & 0x000000ffL)
    		     + "." + Long.toString(intIp0 & 0x000000ffL);
		  
	    	wifiState += ",wifi_SSID," + wifiInfo.getSSID();
	    	wifiState += ",wifi_BSID," + wifiInfo.getBSSID();
		  
	    	wifiState += ",wifi_link_speed," + String.valueOf(wifiInfo.getLinkSpeed()) + " " + WifiInfo.LINK_SPEED_UNITS;
	    	wifiState += ",wifi_RSSI," + Integer.toString(wifiInfo.getRssi());
	    }
	    
	    return wifiState;
	}
	
	
	@Override
    public void onReceive(Context context, Intent intent) {
		
		super.onReceive(context, intent);
		
        String action = intent.getAction();
        String state = getWifiStateString();
        String wifiStateString = "";

        if (action.equals(WifiManager.WIFI_STATE_CHANGED_ACTION)) 
        {
            int wifiState = intent.getIntExtra
                (WifiManager.EXTRA_WIFI_STATE, 0);

            switch (wifiState)
            {
                case WifiManager.WIFI_STATE_DISABLED: 
                	wifiStateString += ",wifi_state," + "DISABLED";
                    break;
                
                case WifiManager.WIFI_STATE_ENABLED:
                	wifiStateString += ",wifi_state,ENABLED";
                    break;
                    
                case WifiManager.WIFI_STATE_DISABLING:
                	wifiStateString += ",wifi_state,DISABLING";
                	break;
                case WifiManager.WIFI_STATE_ENABLING:
                	wifiStateString += ",wifi_state,ENABLING";
                	break;
                case WifiManager.WIFI_STATE_UNKNOWN:
                default:
                	wifiStateString += ",wifi_state,UNKNOWN";
            }
        }

       String netState = state + wifiStateString;

       String dataRecord = constructDataRecord( 
        		netState, NET_TYPE, this.getDeviceID());
       this.broadcastDataRecord(dataRecord, NET_TYPE);
    }
	
}
