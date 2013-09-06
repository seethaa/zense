package edu.cmu.sv.mobisens.content;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import edu.cmu.sv.mobisens.io.MobiSensLog;
import edu.cmu.sv.mobisens.threading.CyclingWakeUpThread;

public class PeriodicNetworkRequestWidget extends Widget {
	private long wakeupInterval = 1000;

	private RequestingThread requestingThread = null;
	private boolean sleep = false;
	private boolean forceWakeUp = false;
	
	class RequestingThread extends CyclingWakeUpThread{

		public RequestingThread(long wakeupInterval) {
			super(wakeupInterval, 1000);
			// TODO Auto-generated constructor stub
		}
		
		@Override
		protected void onWake(long sleepTime){
			if(isSleeping())
				return;
			
			PeriodicNetworkRequestWidget.this.onWake();
		}
		
		@Override
		protected void onExit(){
			PeriodicNetworkRequestWidget.this.onExiting();
		}
		
		@Override
		protected void onChecked(long sleepTime){
			PeriodicNetworkRequestWidget.this.onChecked(sleepTime);
		}
		
	};
	
	protected void onChecked(long sleepTime){}
	
	protected void onWake(){
		
	}
	
	protected void onExiting(){
		
	}

	protected void setWakeupInterval(long wakeupInterval) {
		this.wakeupInterval = wakeupInterval;
		
		if(this.requestingThread != null){
			this.requestingThread.setInterval(wakeupInterval);
		}
	}

	public long getWakeupInterval() {
		return wakeupInterval;
	}
	
	protected PeriodicNetworkRequestWidget(){}
	
	public PeriodicNetworkRequestWidget(long wakeupInterval){
		this.setWakeupInterval(wakeupInterval);
	}
	
	public void register(ContextWrapper contextWrapper){
		super.register(contextWrapper);
		
		this.requestingThread = new RequestingThread(this.getWakeupInterval());
		this.requestingThread.start();
		
	}
	
	public void unregister(){

		//Force dump
		if(this.requestingThread != null){
			this.requestingThread.exit();
			this.requestingThread = null;
		}
		
		super.unregister();
	}
	
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
    	String action = intent.getAction();
    	if(action.equals(ConnectivityManager.CONNECTIVITY_ACTION)){
    		this.onConnectionStatusChanged(intent);
    	}
    	
	}
	
	private void onConnectionStatusChanged(Intent intent){
		boolean haveConnectedWifi = false;
	    boolean haveConnectedMobile = false;
	    boolean haveConnectedWiMax = false;

	    ConnectivityManager cm = (ConnectivityManager) this.getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
	    NetworkInfo[] netInfo = cm.getAllNetworkInfo();
	    for (NetworkInfo ni : netInfo)
	    {
	        if (ni.getType() == ConnectivityManager.TYPE_WIFI)
	            if (ni.isConnected())
	                haveConnectedWifi = true;
	        if (ni.getType() == ConnectivityManager.TYPE_MOBILE)
	            if (ni.isConnected())
	                haveConnectedMobile = true;
	        if (ni.getType() == ConnectivityManager.TYPE_WIMAX)
	            if (ni.isConnected())
	            	haveConnectedWiMax = true;
	    }
	    
	    boolean shouldSleep = (!(haveConnectedWifi || haveConnectedMobile || haveConnectedWiMax));
	    this.setSleep(shouldSleep);

		if(!shouldSleep && this.shouldForceWakeUp()){
			Thread wakeThread = new Thread(){
				public void run(){
					onWake();
					setForceWakeUp(false);  // clear the flag.
					MobiSensLog.log("Force wake up done.");
				}
			};
			
			wakeThread.start();
		}
	}


	protected void setSleep(boolean sleep) {
		this.sleep = sleep;
		
		if(this.sleep){
			MobiSensLog.log(this.getClass().getName() + ": Sleep, no network connectivity.");
		}else{
			MobiSensLog.log(this.getClass().getName() + ": Wakeup, network connected.");
		}
	}

	protected boolean isSleeping() {
		return sleep;
	}

	protected void setForceWakeUp(boolean forceWakeUp) {
		this.forceWakeUp = forceWakeUp;
	}

	private boolean shouldForceWakeUp() {
		return forceWakeUp;
	}

	@Override
	protected String[] getActions() {
		// TODO Auto-generated method stub
		return new String[]{ ConnectivityManager.CONNECTIVITY_ACTION };
	}
		
}
