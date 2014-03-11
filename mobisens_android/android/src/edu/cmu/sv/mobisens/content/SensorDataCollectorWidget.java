package edu.cmu.sv.mobisens.content;

import java.util.List;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.IntentFilter;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Handler;
import edu.cmu.sv.mobisens.MobiSensService;
import edu.cmu.sv.mobisens.SensorService;
import edu.cmu.sv.mobisens.io.MobiSensLog;
import edu.cmu.sv.mobisens.power.Alarm;
import edu.cmu.sv.mobisens.settings.ServiceParameters;

public class SensorDataCollectorWidget extends Widget implements SensorEventListener{
	public final static String CLASS_PREFIX = SensorDataCollectorWidget.class.getName();
	public final static String ACTION_DESTROY = CLASS_PREFIX + ".action_destroy";
	private static final int DEFAULT_SAMPLING_MAGNITUDE = (int) (
			(ServiceParameters.CYCLING_BASE_MS) / MobiSensService.getParameters().getServiceParameter(ServiceParameters.PHONE_WAKEUP_DURATION));
	
	private SensorManager sensorManager;
	private float[] acc = new float[3];  //ms_f__acc_x, ms_f__acc_y, ms_f__acc_z;
	private float[] compass = new float[3];  //ms_f__compass_x, ms_f__compass_y, ms_f__compass_z;
	private float[] orientation = new float[3];  //ms_f__orientation_x, ms_f__orientation_y, ms_f__orientation_z;
	private float[] gyro = new float[3]; // ms_f__gyro_x, ms_f__gyro_y, ms_f__gyro_z;
	private float temperature;
	private float light;
	
	private Sensor regAcc = null;
	private Sensor regOrientation = null;
	private Sensor regCompass = null;
	private Sensor regGyro = null;
	private Sensor regTemp = null;
	private Sensor regLight = null;
	
	private final int sensorSamplingDelay = SensorManager.SENSOR_DELAY_NORMAL;
	
	private long pollingInterval = MobiSensService.getParameters().getServiceParameter(ServiceParameters.ACCELEROMETER) / DEFAULT_SAMPLING_MAGNITUDE;
	//private long pollingInterval = MobiSensService.getParameters().getServiceParameter(ServiceParameters.ACCELEROMETER);
	private PollingThread pollingThread = null;
	
	private void resetBuffer(){
		acc = new float[3];
		compass = new float[3];
		orientation = new float[3];
		gyro = new float[3];
		temperature = 0f;
		light = 0f;
	}
	
	public class PollingThread extends Thread{
		private boolean canExit = false;

		public void run(){
			
			while(!this.canExit){
				onDataPolled(acc, orientation, compass, gyro, temperature, light);
				
				try {
					Thread.sleep(getPollingInterval());
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
			onExit();
		}
		
		public void exit(){
			this.canExit  = true;
		}
	}
	

	
	protected void onDataPolled(float[] acc, float[] orientation, float[] compass, float[] gyro, float temperature, float light){
		
	}
	
	protected void onExit(){
		
	}
	
	
	public void register(ContextWrapper contextWrapper){
		super.register(contextWrapper);
		this.sensorManager = (SensorManager)this.getContext().getSystemService(Context.SENSOR_SERVICE );

		this.reregisterSensorListener(MobiSensService.getParameters());
		this.pollingThread = new PollingThread();
		this.pollingThread.start();
//		this.handler = new Handler();
//		handler.postDelayed(pollDataRunnable, pollingInterval);

	}
	
	public void unregister(){
		
		if(this.pollingThread != null){
			this.pollingThread.exit();
			this.pollingThread = null;
		}
		
//		if(this.handler != null){
//			this.handler.removeCallbacks(pollDataRunnable);
//		}

		if(this.regAcc != null)
			this.sensorManager.unregisterListener(this, this.regAcc);
		if(this.regGyro != null)
			this.sensorManager.unregisterListener(this, this.regGyro);
		if(this.regCompass != null)
			this.sensorManager.unregisterListener(this, this.regCompass);
		if(this.regOrientation != null)
			this.sensorManager.unregisterListener(this, this.regOrientation);
		if(this.regTemp != null)
			this.sensorManager.unregisterListener(this, this.regTemp);
		if(this.regLight != null)
			this.sensorManager.unregisterListener(this, this.regLight);
		
		this.sensorManager = null;
		super.unregister();
	}
	

	protected void setPollingInterval(long value){
		this.pollingInterval = value;
	}
	
	protected long getPollingInterval(){
		return this.pollingInterval;
	}

	public void reregisterSensorListener(ServiceParameters params){
		Sensor sensor = null;
		resetBuffer();
		
		try{
			if(params.getServicesStatus(ServiceParameters.ACCELEROMETER)){
				sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER );
				if(sensor != null) {
					if(this.regAcc != null)
						sensorManager.unregisterListener(this, regAcc);
					regAcc = sensor;
					sensorManager.registerListener(this, regAcc, sensorSamplingDelay);
				}
			}
			
			
			if(params.getServicesStatus(ServiceParameters.GYRO)){
				sensor = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
				if(sensor != null)
				{
					if(this.regGyro != null)
						sensorManager.unregisterListener(this, regGyro);
					regGyro = sensor;
					sensorManager.registerListener(this, regGyro, sensorSamplingDelay);
				}
			}

			if(params.getServicesStatus(ServiceParameters.COMPASS)){
				
				sensor = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
				if(sensor != null)
				{
					if(this.regCompass != null)
						sensorManager.unregisterListener(this, regCompass);
					regCompass = sensor;
					sensorManager.registerListener(this, regCompass, sensorSamplingDelay );
				}
				
			}

			
			if(params.getServicesStatus(ServiceParameters.ORIENTATION)){
				
				sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);
				if(sensor != null)
				{
					if(this.regOrientation != null)
						sensorManager.unregisterListener(this, regOrientation);
					regOrientation = sensor;
					sensorManager.registerListener( this, regOrientation, sensorSamplingDelay );
				}
				
			}
			
			
			if(params.getServicesStatus(ServiceParameters.TEMPERATURE)){
				sensor = sensorManager.getDefaultSensor(Sensor.TYPE_TEMPERATURE );
				if(sensor != null)
				{
					if(this.regTemp != null)
						sensorManager.unregisterListener(this, regTemp);
					regTemp = sensor;
					sensorManager.registerListener(this, regTemp, sensorSamplingDelay);
				}
			}
			
			if(params.getServicesStatus(ServiceParameters.LIGHT)){
				sensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
				if(sensor != null)
				{
					if(this.regLight != null)
						sensorManager.unregisterListener(this, regLight);
					regLight = sensor;
					sensorManager.registerListener( this, regLight, sensorSamplingDelay );
				}
			}
		}catch(Exception ex){
			MobiSensLog.log(ex);
			
		}
	}

	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// TODO Auto-generated method stub
		
	}

	public void onSensorChanged(SensorEvent event) {
		// TODO Auto-generated method stub
		float[] values = event.values;
		
		if( event.sensor.getType() == Sensor.TYPE_ACCELEROMETER )
		{
			acc[0] = values[0];
			acc[1] = values[1];
			acc[2] = values[2];
		}
		
		if( event.sensor.getType() == Sensor.TYPE_ORIENTATION )
		{
			orientation[0] = values[0];
			orientation[1] = values[1];
			orientation[2] = values[2];
		}
		
		
		if( event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD )
		{
			compass[0] = values[0];
			compass[1] = values[1];
			compass[2] = values[2];

		}
		
		if( event.sensor.getType() == Sensor.TYPE_GYROSCOPE )
		{
			gyro[0] = values[0];
			gyro[1] = values[1];
			gyro[2] = values[2];

		}
		
		
		if( event.sensor.getType() == Sensor.TYPE_TEMPERATURE ){
			temperature = values[0];
		}
		
		if( event.sensor.getType() == Sensor.TYPE_LIGHT ){
			light = values[0];
		}
		
		return;
	}

	@Override
	protected String[] getActions() {
		// TODO Auto-generated method stub
		return new String[]{ Alarm.ACTION_ALARM };
	}
}