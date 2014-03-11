package edu.cmu.sv.mobisens.content;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.WifiManager;
import android.net.wifi.WifiManager.WifiLock;
import android.os.SystemClock;
import edu.cmu.sv.mobisens.MobiSensService;
import edu.cmu.sv.mobisens.io.MobiSensLog;
import edu.cmu.sv.mobisens.net.Network;
import edu.cmu.sv.mobisens.power.Alarm;
import edu.cmu.sv.mobisens.settings.ServiceParameters;
import edu.cmu.sv.mobisens.threading.CyclingWakeUpThread;

public class WifiScannerWidget extends Widget {
	
	private static final String CLASS_PREFIX = WifiScannerWidget.class.getName();
	
	private long wifiScanInterval = MobiSensService.getParameters().
		getServiceParameter(
				ServiceParameters.WIFI_SCAN);
	
	private boolean wifiScanOpened = false;
    private static final int WIFISCAN_MSG = 0;
    private WifiManager wifiManager;
    //protected WifiLock wifiLock;
    
    
    private long upTimeMillions = 0;
    
    
    protected void beforeRegistered(ContextWrapper contextWrapper){
    	wifiManager = (WifiManager)contextWrapper.getSystemService(Context.WIFI_SERVICE);
    }
    
    public void unregister(){
    	this.stopWifiScan();
    	super.unregister();
    	
//    	if(wifiLock != null){
//			wifiLock.release();
//			wifiLock = null;
//		}
    }

	@Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (action.equals(WifiManager.WIFI_STATE_CHANGED_ACTION)) {
            int wifiState = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, 0);

            switch (wifiState)
            {
                case WifiManager.WIFI_STATE_DISABLED: 
                	
                    stopWifiScan();
                    break;
                
                case WifiManager.WIFI_STATE_ENABLED:
                	
                    startWifiScan();
                    break;
                    
            }
        }
        
        if(Alarm.ACTION_ALARM.equals(action)){
        	
        	if(SystemClock.elapsedRealtime() >= this.upTimeMillions){
        		
        		ServiceParameters params = MobiSensService.getParameters();
        		if(params.getServicesStatus(ServiceParameters.WIFI_SCAN)){
        			startWifiScan();
            	}
        		
        		this.upTimeMillions = SystemClock.elapsedRealtime() + this.wifiScanInterval;
        	}
        	
        }
        
        if(ProfileWidget.ACTION_SERVICE_PARAMETER_UPDATED.equals(action)){
        	refreshSettings();
        }
    }
    

	private void refreshSettings(){
    	ServiceParameters params = MobiSensService.getParameters();
    	this.wifiScanInterval =  params.getServiceParameter(ServiceParameters.WIFI_SCAN);
    	
//    	if(this.scanningThread != null){
//    		this.scanningThread.setInterval(wifiScanInterval);
//    		MobiSensLog.log("New WIFI scan interval set: " + this.wifiScanInterval);
//    	}
    }
	
    private void startWifiScan()
    {
    	if(this.wifiScanOpened)
    		return;
    	
        if(!Network.isWificonnected(this.getContext())){
        	WifiLock wifiLock = wifiManager.createWifiLock(WifiManager.WIFI_MODE_SCAN_ONLY, CLASS_PREFIX);
    		wifiLock.acquire();
        	wifiManager.startScan();
        	wifiLock.release();
        	
        }
        
        this.wifiScanOpened = true;
        //Message wifimsg = handler.obtainMessage(WIFISCAN_MSG);

        //handler.sendMessageDelayed(wifimsg, MobiSensService.getParameters().getServiceParameter(
				//ServiceParameters.WIFI_SCAN));
        

    }
    
    private void stopWifiScan()
    {
    	if(!this.wifiScanOpened)
    		return;
    	
        //handler.removeMessages(WIFISCAN_MSG);
        //unregisterReceiver(mWifiScanReceiver);
    	
//    	if(this.scanningThread != null){
//    		this.scanningThread.exit();
//    		this.scanningThread = null;
//    		
//    	}
        this.wifiScanOpened = false;
    }

	@Override
	protected String[] getActions() {
		// TODO Auto-generated method stub
		return new String[]{
				Alarm.ACTION_ALARM,
				WifiManager.WIFI_STATE_CHANGED_ACTION,
				ProfileWidget.ACTION_SERVICE_PARAMETER_UPDATED
		};
	}
}
