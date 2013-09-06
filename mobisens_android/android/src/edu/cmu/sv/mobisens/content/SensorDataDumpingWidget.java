package edu.cmu.sv.mobisens.content;

import java.util.List;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;
import edu.cmu.sv.mobisens.MobiSensService;
import edu.cmu.sv.mobisens.SensorService;
import edu.cmu.sv.mobisens.io.Directory;
import edu.cmu.sv.mobisens.io.FileOperation;
import edu.cmu.sv.mobisens.io.MobiSensLog;
import edu.cmu.sv.mobisens.power.Alarm;
import edu.cmu.sv.mobisens.settings.ServiceParameters;

public class SensorDataDumpingWidget extends DataDumpingWidget {
	private static final String TAG = "SensorDataDumpingWidget";
	
	public final static String CLASS_PREFIX = SensorDataDumpingWidget.class.getName();
	
	public final static String ACTION_PREPARE_UPLOAD = CLASS_PREFIX + ".action_prepare_upload";
	public final static String ACTION_DESTROY = CLASS_PREFIX + ".action_destroy";
	
	private long waitCycleCount = 0;
	private boolean skipWrite = false;
	private boolean skipCollect = true;
	private StringBuilder buffer = new StringBuilder(3 * 1024);
	private long dataCollected = 0;
	
	private SensorDataCollectorWidget dataCollectorWidget = new SensorDataCollectorWidget(){
		protected void onDataPolled(float[] acc, float[] orientation, float[] compass, float[] gyro, float temperature, float light){
			if(!isSkippingCollection()){
				
				
				buffer.append(System.currentTimeMillis()).append(",");
				buffer.append(acc[0]).append(",").append(acc[1]).append(",").append(acc[2]).append(",");
				buffer.append(orientation[0]).append(",").append(orientation[1]).append(",").append(orientation[2]).append(",");
				buffer.append(compass[0]).append(",").append(compass[1]).append(",").append(compass[2]).append(",");
				buffer.append(gyro[0]).append(",").append(gyro[1]).append(",").append(gyro[2]).append(",");
				buffer.append(temperature).append(",");
				buffer.append(light);
				buffer.append("\r\n");
				dataCollected++;
				
				onAccelerometerDataPolled(acc);
				
				ServiceParameters params = MobiSensService.getParameters();
				long dataCollectPerCycle = 1000 / dataCollectorWidget.getPollingInterval() * (params.getServiceParameter(ServiceParameters.PHONE_WAKEUP_DURATION) / 1000);
				boolean shouldWrite = (dataCollected >= dataCollectPerCycle);
				if(skipWrite == false && shouldWrite){
					dump();
				}
				
			}
			
		}
	};
	
	private void dump(){
		SensorDataDumpingWidget.this.writeData(buffer.toString());
		buffer = new StringBuilder(buffer.length());
		this.dataCollected = 0;
	}

	protected void onAccelerometerDataPolled(float[] acc){
		
	}
	
	protected void onExit(){
		
	}
	
	public void register(ContextWrapper contextWrapper){
		super.register(contextWrapper);
		
		this.switchRecordFile();
		this.dataCollectorWidget.register(contextWrapper);
		
	}
	
	public void unregister(){
		this.dump();
		this.dataCollectorWidget.unregister();
		this.closeCurrentFileStream();
		
		String[] fileList = FileOperation.getFilesInDirectory(getDataDirectory());
		boradcastFileList(fileList, String.valueOf(Directory.FILE_TYPE_SENSOR), this);
		
		super.unregister();
	}
	
	public void onReceive(Context context, Intent intent) {
		super.onReceive(context, intent);
		
		// TODO Auto-generated method stub
    	String action = intent.getAction();
    	if(action.equals(ProfileWidget.ACTION_SERVICE_PARAMETER_UPDATED)){
    		updatePollingInterval();
    		this.dataCollectorWidget.reregisterSensorListener(MobiSensService.getParameters());
    	}
    	
    	if(Alarm.ACTION_ALARM.equals(action)){
			this.waitCycleCount++;
			if(this.waitCycleCount >= FILE_SPLIT_INTERVAL_MS / ServiceParameters.CYCLING_BASE_MS){
				SensorDataDumpingWidget.broadcastUploadFiles(intent, this);
				waitCycleCount = 0;
				MobiSensLog.log(CLASS_PREFIX + ", record file switched.");
			}
		}
    	
    	if(ACTION_PREPARE_UPLOAD.equals(action)){
			String[] fileList = FileOperation.getFilesInDirectory(getDataDirectory());
			if(this.getCurrentFileName() != null){
				String[] tmp = fileList;
				fileList = new String[tmp.length - 1];
				int index = 0;
				for(String path:tmp){
					if(!path.equals(this.getCurrentFileName())){
						fileList[index] = path;
						index++;
					}
				}
				
				boradcastFileList(fileList, String.valueOf(Directory.FILE_TYPE_SENSOR), this);
				
				Log.i(TAG, "Upload requested: " + CLASS_PREFIX);
				MobiSensLog.log("Upload requested: " + CLASS_PREFIX);
			}
		}
	}
	
	public static void broadcastUploadFiles(Intent intent, DataDumpingWidget dumper) {
		// TODO Auto-generated method stub
		boolean isCleanUp = intent.getBooleanExtra(UploadControllerWidget.EXTRA_CLEANUP, false);

		if(dumper != null){
			synchronized(dumper.syncObject){
				String[] fileList = FileOperation.getFilesInDirectory(getDataDirectory());
				dumper.flushCurrentFileStream();
				
				if(isCleanUp){
					dumper.closeCurrentFileStream();
				}else{
					dumper.switchRecordFile();
				}
	
				boradcastFileList(fileList, String.valueOf(Directory.FILE_TYPE_SENSOR), dumper);
			}
		}
		
		Log.i(TAG, "Upload requested: " + CLASS_PREFIX);
		MobiSensLog.log("Upload requested: " + CLASS_PREFIX);
		
	}
	
	public static void broadcastUploadFiles(Intent intent, Context dumper) {
		// TODO Auto-generated method stub
		String[] fileList = FileOperation.getFilesInDirectory(getDataDirectory());

		boradcastFileList(fileList, String.valueOf(Directory.FILE_TYPE_SENSOR), dumper);
		
		Log.i(TAG, "Upload requested: " + CLASS_PREFIX);
		MobiSensLog.log("Upload requested: " + CLASS_PREFIX);
		
	}

	private void updatePollingInterval(){
		ServiceParameters newParams = MobiSensService.getParameters();
		
		int newSamplingMagnitude = (int) ((ServiceParameters.CYCLING_BASE_MS) / newParams.getServiceParameter(ServiceParameters.PHONE_WAKEUP_DURATION));
		long newPollingInterval =  newParams.getServiceParameter(ServiceParameters.ACCELEROMETER) / newSamplingMagnitude;
		
		this.dataCollectorWidget.setPollingInterval(newPollingInterval);
	}
	
	
	protected void switchRecordFile(){
		synchronized(syncObject){
			if(this.getContext() == null)
				return;
		}
		
		dump();
		this.skipWrite = true;
		
		this.switchRecordFile(SensorDataDumpingWidget.getDataDirectory(), 
				this.getDeviceID() + "_" + Directory.RECORDER_TYPE_SENSORS, 
				".csv");
		this.writeIndicatorLine();
		this.skipWrite = false;
	}

	private void writeIndicatorLine(){
		synchronized(syncObject){
			List<Sensor> sensorList = null;
			
			String indicatorLine = "Timestamp,";
			ServiceParameters params = MobiSensService.getParameters();
			SensorManager sensorManager = (SensorManager)this.getContext().getSystemService(Context.SENSOR_SERVICE );
			
			if(params.getServicesStatus(ServiceParameters.ACCELEROMETER)){
				sensorList = sensorManager.getSensorList( Sensor.TYPE_ACCELEROMETER );
				if( (sensorList != null) && (!sensorList.isEmpty()) )
				{
					indicatorLine += "Accelerometer X,Accelerometer Y,Accelerometer Z";
					
				}else
				{
					indicatorLine += "Accelerometer Not Supported,Accelerometer Not Supported,Accelerometer Not Supported";
					Log.d( "UsageSignatureSensor", "No accelerometer detected" );
				}
			}else{
				indicatorLine += "Accelerometer Disabled,Accelerometer Disabled,Accelerometer Disabled";
				Log.d( "UsageSignatureSensor", "Accelerometer disabled" );
			}
			

			if(params.getServicesStatus(ServiceParameters.ORIENTATION)){
				sensorList = sensorManager.getSensorList( Sensor.TYPE_ORIENTATION );
				if( (sensorList != null) && (!sensorList.isEmpty()) )
				{
					indicatorLine += ",Orientation Azimuth,Orientation Pitch,Orientation Roll";
					
				}else
				{
					indicatorLine += ",Orientation Not Supported,Orientation Not Supported,Orientation Not Supported";
					Log.d( "UsageSignatureSensor", "No orientation sensor detected" );
				}
			}else{
				indicatorLine += ",Orientation Disabled,Orientation Disabled,Orientation Disabled";
				Log.d( "UsageSignatureSensor", "Orientation disabled" );
			}
			

			if(params.getServicesStatus(ServiceParameters.COMPASS)){
				sensorList = sensorManager.getSensorList( Sensor.TYPE_MAGNETIC_FIELD );
				if( (sensorList != null) && (!sensorList.isEmpty()) )
				{
					indicatorLine += ",Compass X,Compass Y,Compass Z";
					
				}else
				{
					indicatorLine += ",Compass Not Supported,Compass Not Supported,Compass Not Supported";
					Log.d( "UsageSignatureSensor", "No compass detected" );
				}
			}else{
				indicatorLine += ",Compass Disabled,Compass Disabled,Compass Disabled";
				Log.d( "UsageSignatureSensor", "Compass disabled" );
			}

			

			if(params.getServicesStatus(ServiceParameters.GYRO)){
				sensorList = sensorManager.getSensorList( Sensor.TYPE_GYROSCOPE );
				if( (sensorList != null) && (!sensorList.isEmpty()) )
				{
					indicatorLine += ",Gyro X,Gyro Y,Gyro Z";
					
				}else
				{
					indicatorLine += ",Gyro Not Supported,Gyro Not Supported,Gyro Not Supported";
					Log.d( "UsageSignatureSensor", "No gyroscope detected" );
				}
			}else{
				indicatorLine += ",Gyro Disabled,Gyro Disabled,Gyro Disabled";
				Log.d( "UsageSignatureSensor", "Gyro disabled" );
			}
			
			
			if(params.getServicesStatus(ServiceParameters.TEMPERATURE)){
				sensorList = sensorManager.getSensorList( Sensor.TYPE_TEMPERATURE );
				if( (sensorList != null) && (!sensorList.isEmpty()) )
				{
					indicatorLine += ",Temperature";
					
				}else
				{
					indicatorLine += ",Temperature Not Supported";
					Log.d( "UsageSignatureSensor", "No temperature sensor detected" );
				}
			}else{
				indicatorLine += ",Temperature Disabled";
				Log.d( "UsageSignatureSensor", "Temperature disabled" );
			}
			
			if(params.getServicesStatus(ServiceParameters.LIGHT)){
				sensorList = sensorManager.getSensorList( Sensor.TYPE_LIGHT );
				if( (sensorList != null) && (!sensorList.isEmpty()) )
				{
					indicatorLine += ",Light";
					
				}else
				{
					indicatorLine += ",Light Not Supported";
					Log.d( "UsageSignatureSensor", "No light sensor detected" );
				}
			}else{
				indicatorLine += ",Light Disabled";
				Log.d( "UsageSignatureSensor", "Light disabled" );
			}

			indicatorLine += "\r\n";
			this.writeData(indicatorLine);
		}
	}

	public static String getDataDirectory(){
		return Directory.SENSORS_DEFAULT_DATA_FOLDER;
	}
	
	public void setSkipCollection(boolean skip) {
		this.skipCollect = skip;
	}

	public boolean isSkippingCollection() {
		return this.skipCollect;
	}

	@Override
	protected String[] getActions() {
		// TODO Auto-generated method stub
		
		return new String[]{
				Alarm.ACTION_ALARM,
				SystemWidget.ACTION_SYSTEM_DATA_EMITTED,
				SensorDataDumpingWidget.ACTION_DESTROY,
				ProfileWidget.ACTION_SERVICE_PARAMETER_UPDATED,
				ACTION_PREPARE_UPLOAD
		};
	}
	
	
}
