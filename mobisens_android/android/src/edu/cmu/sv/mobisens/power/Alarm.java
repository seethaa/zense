package edu.cmu.sv.mobisens.power;

import java.util.LinkedList;

import edu.cmu.sv.mobisens.MobiSensService;
import edu.cmu.sv.mobisens.io.MobiSensLog;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.os.SystemClock;

public class Alarm extends BroadcastReceiver {
	private static final String CLASS_PREFIX = Alarm.class.getName();
	public static final String ACTION_ALARM = CLASS_PREFIX + ".action_alarm";
	
	public interface IAlarmListener{
		void onArlarm();
	}
	
	
	private LinkedList<IAlarmListener> listeners = new LinkedList<IAlarmListener>();
	private LinkedList<Context> contextList = new LinkedList<Context>();
	
	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		String action = intent.getAction();
		
		if(ACTION_ALARM.equals(action)){
			final WakeLock lock = MobiSensService.acquireWakeLockForService(context, PowerManager.PARTIAL_WAKE_LOCK, ACTION_ALARM);
			
			Thread workingThread = new Thread(){
				public void run(){
					MobiSensLog.log("Alarm triggered!");
					
					for(IAlarmListener listener:listeners){
						listener.onArlarm();
					}
					
					if(lock != null){
						lock.release();
					}
				}
			};
			
			workingThread.start();
		}
		
	}
	
	
	public void set(Context context, long delayMillionSec, IAlarmListener listener){
		if(context == null || listener == null)
			return;
		
		if(this.listeners.contains(listener) == false){
			this.listeners.add(listener);
		}
		
		if(!this.contextList.contains(context)){
			this.contextList.add(context);
			context.registerReceiver(this, new IntentFilter(ACTION_ALARM));
		}
		
		AlarmManager mgr = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
		Intent i = new Intent(ACTION_ALARM);
		PendingIntent pi = PendingIntent.getBroadcast(context, 0, i, 0);

		mgr.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime() + delayMillionSec, pi);

	}
	
	public void remove(Context context, IAlarmListener listener){
		if(this.contextList.remove(context)){
			context.unregisterReceiver(this);
		}
		this.listeners.remove(listener);
	}
}
