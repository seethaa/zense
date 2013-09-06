package edu.cmu.sv.mobisens.util;

import android.os.Handler;
import android.os.Message;

public class Timer {
	private static int TIMER_MSG = 0;
	
	private long interval = 0;
	private boolean started = false;
	
	private Handler handler = new Handler(){
		@Override
        public void handleMessage(Message msg)
        {
			if(msg.what == TIMER_MSG){
				if(started){
					onTimer();
					Message message = this.obtainMessage(TIMER_MSG);
					this.sendMessageDelayed(message, getInterval());
				}else{
					this.removeMessages(TIMER_MSG);
				}
			}
        }
	};
	
	protected void onTimer(){
		
	}
	
	
	public Timer(long interval){
		this.setInterval(interval);
	}
	
	public void start(){
		if(started)
			return;
		
		Message message = this.handler.obtainMessage(TIMER_MSG);
		this.handler.sendMessageDelayed(message, getInterval());
		started = true;
	}
	public void stop(){
		this.handler.removeMessages(TIMER_MSG);
		started = false;
	}


	public void setInterval(long interval) {
		this.interval = interval;
	}


	public long getInterval() {
		return interval;
	}
}
