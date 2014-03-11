package edu.cmu.sv.mobisens;


import edu.cmu.sv.mobisens.content.ActivityWidget;
import edu.cmu.sv.mobisens.content.AudioFeatureDumpingWidget;
import edu.cmu.sv.mobisens.power.Alarm;
import edu.cmu.sv.mobisens.settings.ServiceParameters;
import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.util.Log;


public class SensorService extends MobiSensService implements Alarm.IAlarmListener {
	private static final String TAG = "SensorService";
	//private static Handler 						ms_handler__main;
	
	
	private static Context context = null;
	
	private static SensorService instance = null;

	private AudioFeatureDumpingWidget audioWidget = 
		new AudioFeatureDumpingWidget();
	private WakeLock cpuWakeLock = null;
	
	private ActivityWidget activityWidget = new ActivityWidget();


	public static SensorService getInstance(){
		return instance;
	}
	
	public static Context getServiceContext(){
		return context;
	}
	
	public static void setServiceContext(Context c){
		context = c;
	}
	
	public static boolean needScreenOn(){
		return Build.VERSION.SDK_INT <= 7;
	}
	
	
	@Override
	public void onCreate()
	{
		super.onCreate();

		if(getServiceContext() == null ) {
			setServiceContext(this);
		}
		
		
		instance = this;
		this.activityWidget.register(this);
		this.audioWidget.register(this);
		
		long alarmInterval = ServiceParameters.CYCLING_BASE_MS - MobiSensService.getParameters().getServiceParameter(ServiceParameters.PHONE_WAKEUP_DURATION);
		this.alarm.set(this, alarmInterval, this);
		
	}

	@Override
	public void onDestroy()
	{

		this.alarm.remove(this, this);
		
		if(this.cpuWakeLock != null){
			if(this.cpuWakeLock.isHeld())
				this.cpuWakeLock.release();
		}
		this.activityWidget.unregister();
		this.audioWidget.unregister();

		Log.d( "UsageSignatureSensor", "Sensor Service stopped" );

		super.onDestroy();
		instance = null;
		
		return;
	}
	
	private Alarm alarm = new Alarm();

	private Runnable releaseRunnable = new Runnable(){

		public void run() {
			activityWidget.setShouldCollect(false);
			
			long alarmInterval = ServiceParameters.CYCLING_BASE_MS - MobiSensService.getParameters().getServiceParameter(ServiceParameters.PHONE_WAKEUP_DURATION);
			alarm.set(SensorService.this, alarmInterval, SensorService.this);
			// TODO Auto-generated method stub
			if(cpuWakeLock != null){
				if(cpuWakeLock.isHeld()){
					cpuWakeLock.release();
				}
			}
		}
		
	};
	
	private Handler releaseHandler = new Handler();
	
	public void onArlarm() {
		// TODO Auto-generated method stub
		this.cpuWakeLock = MobiSensService.acquireWakeLockForService(SensorService.this, PowerManager.PARTIAL_WAKE_LOCK, "onAlarm");
		this.activityWidget.setShouldCollect(true);
		this.releaseHandler.postDelayed(releaseRunnable, 
				MobiSensService.getParameters().getServiceParameter(ServiceParameters.PHONE_WAKEUP_DURATION));
	}

	
}

