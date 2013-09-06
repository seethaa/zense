package edu.cmu.sv.mobisens.util;

import java.util.Date;

import edu.cmu.sv.mobisens.io.MobiSensLog;

public class SlowStartAlgorithm {
	private long ceilingThreshold = 0;
	private long thresholdWinSize = 0;
	private long initialWinSize = 0;
	
	private long currentWinSize = 0;
	private boolean isInCongustionAoidance = false;
	
	private long lastRequestTime = new Date().getTime();
	private long lastInterval = 0;
	private boolean windowSizeChanged = false;
	
	
	public SlowStartAlgorithm(long threshold, long initialValue){
		this.thresholdWinSize = threshold;
		this.initialWinSize = initialValue;
		this.ceilingThreshold = this.thresholdWinSize;
		
		this.currentWinSize = this.initialWinSize;
		this.lastRequestTime = new Date().getTime();
		this.lastInterval = this.currentWinSize;
	}
	
	public synchronized long getWindowSize(){
		long size = this.currentWinSize;
		
		if(Long.MAX_VALUE / 2 < size){
			this.currentWinSize = Long.MAX_VALUE;
		}else{
			currentWinSize = currentWinSize * 2;
		}
		
		// We no more need CA and celling value now!
		/*
		if(!this.isInCongustionAoidance){  // We are "slow starting"
			currentWinSize = currentWinSize * 2;
			if(this.currentWinSize > this.thresholdWinSize)
				this.currentWinSize = this.thresholdWinSize;
				
			if(this.currentWinSize > this.getCellingThreshold())
				this.currentWinSize = this.getCellingThreshold();
		}else{
			// Additive increase
			currentWinSize += this.initialWinSize;
			if(this.currentWinSize > this.getCellingThreshold())
				this.currentWinSize = this.getCellingThreshold();
		}
		
		if(this.currentWinSize >= this.thresholdWinSize || this.currentWinSize >= this.getCellingThreshold())
			this.setToCAState();
		*/
		
		this.lastRequestTime = new Date().getTime();
		this.lastInterval = size;
		this.windowSizeChanged = (this.lastInterval != this.currentWinSize);
		return size;
		
	}
	
	public boolean shouldRequest(){
		return System.currentTimeMillis() - this.lastRequestTime > this.lastInterval;
	}
	
	public boolean isInCycleGap(){
		return (System.currentTimeMillis() - this.lastRequestTime > this.getInitialWinSize()) && this.shouldRequest() == false;
	}
	
	public boolean hasWindowSizeChanged(){
		return this.windowSizeChanged;
	}
	
	public synchronized long setToSlowStartState(){
		this.isInCongustionAoidance = false;
		
		// For the GPS location TCP Tahoe is better than Reno.
		// If we implement as Reno, we might lost the GPS data at the first
		// currentWinSize / 2 period.
		this.thresholdWinSize = this.currentWinSize / 2;
		
		MobiSensLog.log("Threshold: " + this.thresholdWinSize + " currentWindows: " + this.currentWinSize);
		this.currentWinSize = this.initialWinSize;
		
		if(this.thresholdWinSize < this.initialWinSize)
			this.thresholdWinSize = this.initialWinSize;
		
		
		return this.currentWinSize;
	}
	
	private long setToCAState(){  // The to congestion avoidance state.
		this.isInCongustionAoidance = true;
		//this.thresholdWinSize = (this.currentWinSize / 2 > 0 ? this.currentWinSize / 2 : this.initialWinSize);
		//MobiSensLog.log("Threshold: " + this.thresholdWinSize);
		
		return this.currentWinSize;
	}

	public synchronized void setCeilingThreshold(long ceilingThreshold) {
		this.ceilingThreshold = ceilingThreshold;
	}

	public synchronized long getCeilingThreshold() {
		return ceilingThreshold;
	}

	public synchronized void setInitialWinSize(long initialWinSize) {
		this.initialWinSize = initialWinSize;
	}

	public synchronized long getInitialWinSize() {
		return initialWinSize;
	}
}
