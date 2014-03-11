package edu.cmu.sv.mobisens.threading;

import edu.cmu.sv.mobisens.io.MobiSensLog;
import android.util.Log;

public abstract class CyclingWakeUpThread extends Thread {

	private static final String TAG = "CyclingWakeUpThread";
	private long wakeUpInterval = 0;
	private long checkInterval = 100;
	private volatile long lastSleepTimestamp = System.currentTimeMillis();
	private boolean canExit = false;
	
	public CyclingWakeUpThread(long wakeUpInterval, long checkInterval){
		if(checkInterval < 100)
			checkInterval = 100;
		
		this.checkInterval = checkInterval;
		
		if(wakeUpInterval < checkInterval)
			wakeUpInterval = checkInterval;
		
		if(wakeUpInterval < 0)
			canExit = true; // So the remote end can terminate any upload/download.
		this.wakeUpInterval = wakeUpInterval;
		
	}
	
	public void setInterval(long interval){
		
		if(interval < 0)
			canExit = true; // So the remote end can terminate any upload/download.
		
		if(interval < getCheckInterval())
			interval = getCheckInterval();
		
		if(this.wakeUpInterval != interval){
			long oldInterval = this.wakeUpInterval;
			this.wakeUpInterval = interval;
			this.onIntervalChanged(oldInterval, interval);
		}
	}
	
	public long getInterval(){
		return this.wakeUpInterval;
	}
	
	public long getCheckInterval(){
		return this.checkInterval;
	}
	
	@Override
	public void run(){
		long sleepTime = 0;
		onStart();
		
		while(!canExit){
			
			if(sleepTime >= getInterval()){
				onWake(sleepTime);
				sleepTime = 0;
				
				if(canExit){
					break;
				}
			}

			try {
				sleepTime += System.currentTimeMillis() - this.lastSleepTimestamp;
				this.lastSleepTimestamp = System.currentTimeMillis();
				onChecked(sleepTime);
				sleep(getCheckInterval());
			} catch (Exception ex) {
				// TODO Auto-generated catch block
				Log.e(TAG, "Exception", ex);
				MobiSensLog.log(ex);
			}
		}
		

		onExit();
	}
	
	public void exit(){
		canExit = true;
	}
	
	protected abstract void onWake(long sleepTime);
	
	protected void onExit(){
		
	}
	
	protected void onStart(){
		
	}
	
	protected void onChecked(long sleepTime){
		
	}
	
	protected void onIntervalChanged(long oldInterval, long newInterval){
		
	}
}
