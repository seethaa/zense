package edu.cmu.sv.mobisens.settings;

import java.io.File;
import java.net.URL;
import java.util.Hashtable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import edu.cmu.sv.mobisens.io.Directory;
import edu.cmu.sv.mobisens.io.FileOperation;
import edu.cmu.sv.mobisens.net.HttpGetRequest;
import edu.cmu.sv.mobisens.net.HttpRequestForCMUSVProjects;
import edu.cmu.sv.mobisens.net.URLs;
import edu.cmu.sv.mobisens.util.Base64;

public class ServiceParameters {
	public static final long DISABLE_ALL_SERVICES = 0;
	public static final long ACCELEROMETER = 1;
	public static final long COMPASS = 1 << 1;
	public static final long GYRO = 1 << 2;
	public static final long ORIENTATION = 1 << 3;
	public static final long WIFI_SCAN = 1 << 4;
	public static final long BATTERY_STATUS = 1 << 5;
	public static final long GPS = 1 << 6;
	public static final long CALL_MONITOR = 1 << 7;
	public static final long AUDIO_RECORDING = 1 << 8;
	public static final long VIDEO_RECORDING = 1 << 9;
	public static final long VIDEO_PREVIEW = 1 << 10;
	public static final long PHOTO_WIDTH = 1 << 11;
	public static final long PHOTO_HEIGHT = 1 << 12;
	public static final long PHTOT_QUALITY = 1 << 13;
	public static final long PHOTO_ROTATION = 1 << 14;
	public static final long PHONE_POSITION = 1 << 15;
	public static final long ENABLE_SYSTEM_DUMP_BY_INTERVAL = 1 << 16;
	public static final long SYSTEM_DUMP_INTERVAL = 1 << 17;
	public static final long VIDEO_RECORDING_MODE = 1 << 18;
	public static final long TEMPERATURE = 1 << 19;
	public static final long LIGHT = 1 << 20;
	public static final long NEUROSKY_EEG = 1 << 21;
	public static final long NEUROSKY_MINDBAND_ADDRESS = 1 << 22;
	public static final long DEBUG_MODE = 1 << 23;
	public static final long GET_PROFILE_INTERVAL = 1 << 24;
	public static final long ANNO_REQUEST_INTERVAL = 1 << 25;
	public static final long GPS_SLOWSTART_THRESHOLD = 1 << 26;
	public static final long GPS_SAMELOCATION_THRESHOLD = 1 << 27;
	public static final long OUTDOOR_WIFI_SCAN_INTERVAL = 1 << 28;
	public static final long WIFI_SAMELOCATION_THRESHOLD = 1 << 29;
	public static final long ACTIVITY_MERGE_THRESHOLD = 1L << 30;
	public static final long SIMILAR_ACTIVITY_THRESHOLD = 1L << 31;
	public static final long COLLECTION_DATA_SIZE = 1L << 32;
	public static final long WINDOW_SIZE = 1L << 33;
	public static final long STEP_SIZE = 1L << 34;
	public static final long NGRAM_MAX_N = 1L << 35;
	
	// For activity recognition.
	public static final long COSINE_SIM_THRESHOLD = 1L << 36;
	
	// For block comparison.
	public static final long SIMILAR_ACTIVITY_THRESHOLD_2 = 1L << 37;
	public static final long EDGE_DISTANCE = 1L << 38;
	public static final long MAX_SAMPLE_LENGTH = 1L << 39;
	public static final long PROC_SCAN_INTERVAL = 1L << 40;
	public static final long GPS_OPEN_WINDOW = 1L << 41;
	public static final long PHONE_WAKEUP_DURATION = 1L << 42;
	public static final long MAX_GPS_CYCLE = 1L << 43;
	public static final long AUDIO_SAMPLING_INTERVAL = 1L << 44;
	
	// For special profile
	public static final long PROFILE_TIMEOUT = 1L << 45;
	public static final long SWITCH_MESSAGE = 1L << 46;
	
	
	
	public static final long DEFAULT_SERVICE_STATUS = ACCELEROMETER |
														//ORIENTATION |
														//COMPASS |
														//GYRO |
														AUDIO_RECORDING |
														BATTERY_STATUS |
														CALL_MONITOR |
														GPS |
														VIDEO_RECORDING |
														WIFI_SCAN |
														TEMPERATURE |
														LIGHT |
														NEUROSKY_EEG;
	
	public static final int SENSORS_DEFAULT_SAMPLEINTERVAL = 200;  // For lifelogger, this should be 50
	public static final int DEFAULT_WIFI_SCANINTERVAL = 360000;
	public static final int BATTERY_STATUS_DEFAULT_SCANINTERVAL = 120000;
	public static final int GPS_DEFAULT_SCANINTERVAL = 120000;
	public static final int AUDIO_DEFAULT_SAMPLINGRATE = 8000;
	public static final int PHOTO_DEFAULT_CAPTURINGRATE = 10000;
	public static final int PREVIEW_DEFAULT_FRAMERATE = 15;
	public static final int PHOTO_DEFAULT_WIDTH = 320;
	public static final int PHOTO_DEFAULT_HEIGHT = 240;
	public static final int PHOTO_DEFAULT_JPEG_QUALITY = 70;
	public static final int PHOTO_DEFAULT_ROTATION_DEGREE = 90;
	public static final String PHONE_DEFAULT_POSITION = "Head";
	public static final int DEFAULT_UPLOAD_INTERVAL = 3600000;
	public static final int DEFAULT_GET_PROFILE_INTERVAL = 600000;
	public static final boolean SYSTEM_DEFAULT_ENABLE_DUMP_BY_INTERVAL = true;
	public static final int VIDEO_RECORDING_PHOTO_MODE = 1;
	public static final int VIDEO_RECORDING_MPEG4_FILM_MODE = 2;
	public static final String DEFAULT_NEUROSKY_MINDBAND_ADDRESS = "";
	public static final boolean DEBUG_MODE_DEFAULT_VALUE = false;
	public static final long DEFAULT_ANNO_REQUEST_INTERVAL = 30 * 60 * 1000;
	public static final long DEFAULT_GPS_SLOWSTART_THRESHOLD = 20 * 60 * 1000;
	public static final int DEFAULT_GPS_SAMELOCATION_THRESHOLD = 60;
	public static final long DEFAULT_OUTDOOR_WIFI_SCANINTERVAL = DEFAULT_WIFI_SCANINTERVAL;
	public static final int DEFAULT_WIFI_SAMELOCATION_THRESHOLD = 70;
	public static final long DEFAULT_ACTIVITY_MERGE_THRESHOLD = 60000;
	public static final int DEFAULT_SIMILAR_ACTIVITY_THRESHOLD = 60;
	public static final long DEFAULT_COLLECTION_DATA_SIZE = 5 * 60 * 2; // 5 points per second, and collect two minutes
	public static final long DEFAULT_WINDOW_SIZE = 26;
	public static final long DEFAULT_STEP_SIZE = 13;
	public static final long DEFAULT_NGRAM_MAX_N = 4;
	public static final long DEFAULT_COSINE_SIM_THRESHOLD = 80;
	public static final long DEFAULT_SIMILAR_ACTIVITY_THRESHOLD_2 = 60;
	public static final long DEFAULT_EDGE_DISTANCE = 3;
	public static final long DEFAULT_MAX_SAMPLE_LENGTH = 5;
	public static final long DEFAULT_PROC_SCAN_INTERVAL = 60000;
	public static final long DEFAULT_GPS_OPEN_WINDOW = 10 * 1000;
	public static final long DEFAULT_PHONE_WAKEUP_DURATION = 15 * 1000;
	public static final long DEFAULT_MAX_GPS_CYCLE = 30;
	public static final long DEFAULT_AUDIO_SAMPLING_INTERVAL = 5000;
	public static final long DEFAULT_PROFILE_TIMEOUT = -1;
	public static final String DEFAULT_SWITCH_MESSAGE = "Sensing profile switched!";
	
	
	public static final long CYCLING_BASE_MS = 60 * 1000;
	
	public static final String ACCELEROMETER_STRING = "accelerometer";
	public static final String GYRO_STRING = "gyro";
	public static final String COMPASS_STRING = "compass";
	public static final String ORIENTATION_STRING = "orientation";
	public static final String TEMPERATURE_STRING = "temperature";
	public static final String LIGHT_STRING = "light";
	public static final String WIFI_SCAN_STRING = "wifi_scan";
	public static final String BATTERY_STATUS_STRING = "battery_status";
	public static final String GPS_STRING = "gps";
	public static final String CALL_MONITOR_STRING = "call_monitor";
	
	public static final String SENSORS_SAMPLEINTERVAL_STRING = "sensor_sampling_interval";
	public static final String WIFI_SCANINTERVAL_STRING = "wifi_scan_interval";
	public static final String BATTERY_STATUS_SCANINTERVAL_STRING = "battery_status_scan_interval";
	public static final String GPS_SCANINTERVAL_STRING = "gps_dump_interval";
	public static final String UPLOAD_INTERVAL_STRING = "upload_interval";
	public static final String ENABLE_DUMP_BY_INTERVAL = "upload_when_charging_only";
	public static final String DEBUG_MODE_STRING = "debug_mode";
	public static final String GET_PROFILE_INTERVAL_STRING = "get_profile_interval";
	public static final String ANNO_REQUEST_INTERVAL_STRING = "annotation_request_interval";
	public static final String GPS_SLOWSTART_THRESHOLD_STRING = "gps_slowstart_threshold";
	public static final String GPS_SAMELOCATION_THRESHOLD_STRING = "gps_samelocation_distance";
	public static final String OUTDOOR_WIFI_SCANINTERVAL_STRING = "wifi_outdoor_scan_interval";
	public static final String WIFI_SAMELOCATION_THRESHOLD_STRING = "wifi_samelocation_intersect_percentage";
	public static final String ACTIVITY_MERGE_THRESHOLD_STRING = "activity_merge_threshold";
	public static final String SIMILAR_ACTIVITY_THRESHOLD_STRING = "similar_activity_threshold";
	public static final String COLLECTION_DATA_SIZE_STRING = "collection_data_size";
	public static final String WINDOW_SIZE_STRING = "lifelogger_window_size";
	public static final String STEP_SIZE_STRING = "lifelogger_step_size";
	public static final String NGRAM_MAX_N_STRING = "ngram_max_n";
	public static final String COSINE_SIM_THRESHOLD_STRING = "cos_similarity_threshold";
	public static final String SIMILAR_ACTIVITY_THRESHOLD_2_STRING = "similar_activity_threshold_2";
	public static final String EDGE_DISTANCE_STRING = "edge_distance";
	public static final String MAX_SAMPLE_LENGTH_STRING = "max_sample_length";
	public static final String PROC_SCAN_INTERVAL_STRING = "proc_scan_interval";
	public static final String GPS_OPEN_WINDOW_STRING = "gps_open_window";
	public static final String PHONE_WAKEUP_DURATION_STRING = "phone_wakeup_duration";
	public static final String MAX_GPS_CYCLE_STRING = "max_gps_cycle";
	public static final String AUDIO_SAMPLING_INTERVAL_STRING = "audio_record_interval";
	public static final String PROFILE_TIMEOUT_STRING = "profile_timeout";
	public static final String SWITCH_MESSAGE_STRING = "switch_message";
	public static final String DEFAULT_PROFILE_NAME = "Default (From Server)";
	public static final String ID_STRING = "id";
	
	public static final String ON = "on";
	public static final String OFF = "off";
	
	public static final String PROFILE_LINE_DELIMITER = ";";
	public static final String PROFILE_KEYVALUE_DELIMITER = ":";
	
	public static final String DEFAULT_PROFILE_STRING = 
		constructServiceStatusProfileString(DEFAULT_SERVICE_STATUS) +
		constructServiceParametersProfileString(new ServiceParameters()) +
		(new ServiceParameters()).getIDFieldString();
	
	
	private long sensorsSamplingRate = SENSORS_DEFAULT_SAMPLEINTERVAL;
	private long wifiScanSamplingRate = DEFAULT_WIFI_SCANINTERVAL;
	private long batteryStatusSamplingRate = BATTERY_STATUS_DEFAULT_SCANINTERVAL;
	private long gpsSamplingRate = GPS_DEFAULT_SCANINTERVAL;
	private long audioSamplingRate = AUDIO_DEFAULT_SAMPLINGRATE;
	private long photoCapturingRate = PHOTO_DEFAULT_CAPTURINGRATE;
	private long previewVideoFrameRate = PREVIEW_DEFAULT_FRAMERATE;
	private long photoWidth = PHOTO_DEFAULT_WIDTH;
	private long photoHeight = PHOTO_DEFAULT_HEIGHT;
	private long photoQuality = PHOTO_DEFAULT_JPEG_QUALITY;
	private long photoRotationDegree = PHOTO_DEFAULT_ROTATION_DEGREE;
	private String phonePosition = PHONE_DEFAULT_POSITION;
	private boolean enbleSystemInfoDumpByInterval = SYSTEM_DEFAULT_ENABLE_DUMP_BY_INTERVAL;
	private long systemInfoDumpInterval = DEFAULT_UPLOAD_INTERVAL;
	private long videoRecordingMode = VIDEO_RECORDING_MPEG4_FILM_MODE;
	private String neuroSkyInputDeviceAddress = "";
	private boolean debugMode = DEBUG_MODE_DEFAULT_VALUE;
	private long getProfileInterval = DEFAULT_GET_PROFILE_INTERVAL;
	private long annoRequestInterval = DEFAULT_ANNO_REQUEST_INTERVAL;
	private long gpsSlowStartThreshold = DEFAULT_GPS_SLOWSTART_THRESHOLD;
	private long gpsSameLocationThreshold = DEFAULT_GPS_SAMELOCATION_THRESHOLD;
	private long wifiOutdoorScanInterval = DEFAULT_OUTDOOR_WIFI_SCANINTERVAL;
	private long wifiSameLocationThreshold = DEFAULT_WIFI_SAMELOCATION_THRESHOLD;
	private long activityMergeThreshold = DEFAULT_ACTIVITY_MERGE_THRESHOLD;
	private long similarActivityThreshold = DEFAULT_SIMILAR_ACTIVITY_THRESHOLD;
	private long collectionSize = DEFAULT_COLLECTION_DATA_SIZE;
	private long lifeloggerWindowSize = DEFAULT_WINDOW_SIZE;
	private long lifeloggerStepSize = DEFAULT_STEP_SIZE;
	private long ngramMaxN = DEFAULT_NGRAM_MAX_N;
	private long cosineThreshold = DEFAULT_COSINE_SIM_THRESHOLD;
	private long similarActivityThreshold2 = DEFAULT_SIMILAR_ACTIVITY_THRESHOLD_2;
	private long edgeDistance = DEFAULT_EDGE_DISTANCE;
	private long maxSampleLength = DEFAULT_MAX_SAMPLE_LENGTH;
	private long procScanInterval = DEFAULT_PROC_SCAN_INTERVAL;
	private long gpsOpenWindow = DEFAULT_GPS_OPEN_WINDOW;
	private long phoneWakeupDuration = DEFAULT_PHONE_WAKEUP_DURATION;
	private long maxGPSCycle = DEFAULT_MAX_GPS_CYCLE;
	private long audioSampleInterval = DEFAULT_AUDIO_SAMPLING_INTERVAL;
	private long profileTimeOut = DEFAULT_PROFILE_TIMEOUT;
	private String switchMessage = DEFAULT_SWITCH_MESSAGE;
	
	
	private long serviceStatus = DEFAULT_SERVICE_STATUS;
	
	// Following is the fields corresponding to the MobiSens Profile Table.
	private String name = "";
	private long id = 0;  // 0 means default profile
	private Bitmap profileImage = null;
	
	
	// Don't have enough time to write a beautiful parser, can be improved in the future.
	private static String constructServiceStatusProfileString(long serviceStatus){
		String serviceStatusString = "";
		if((serviceStatus & ServiceParameters.ACCELEROMETER) != 0){
			serviceStatusString += ServiceParameters.ACCELEROMETER_STRING + 
			ServiceParameters.PROFILE_KEYVALUE_DELIMITER + 
			ServiceParameters.ON + 
			ServiceParameters.PROFILE_LINE_DELIMITER;
		}else{
			serviceStatusString += ServiceParameters.ACCELEROMETER_STRING + 
			ServiceParameters.PROFILE_KEYVALUE_DELIMITER + 
			ServiceParameters.OFF + 
			ServiceParameters.PROFILE_LINE_DELIMITER;
		}
		
		if((serviceStatus & ServiceParameters.COMPASS) != 0){
			serviceStatusString += ServiceParameters.COMPASS_STRING + 
			ServiceParameters.PROFILE_KEYVALUE_DELIMITER + 
			ServiceParameters.ON + 
			ServiceParameters.PROFILE_LINE_DELIMITER;
		}else{
			serviceStatusString += ServiceParameters.COMPASS_STRING + 
			ServiceParameters.PROFILE_KEYVALUE_DELIMITER + 
			ServiceParameters.OFF + 
			ServiceParameters.PROFILE_LINE_DELIMITER;
		}
		
		if((serviceStatus & ServiceParameters.BATTERY_STATUS) != 0){
			serviceStatusString += ServiceParameters.BATTERY_STATUS_STRING + 
			ServiceParameters.PROFILE_KEYVALUE_DELIMITER + 
			ServiceParameters.ON + 
			ServiceParameters.PROFILE_LINE_DELIMITER;
		}else{
			serviceStatusString += ServiceParameters.BATTERY_STATUS_STRING + 
			ServiceParameters.PROFILE_KEYVALUE_DELIMITER + 
			ServiceParameters.OFF + 
			ServiceParameters.PROFILE_LINE_DELIMITER;
		}
		
		if((serviceStatus & ServiceParameters.CALL_MONITOR) != 0){
			serviceStatusString += ServiceParameters.CALL_MONITOR_STRING + 
			ServiceParameters.PROFILE_KEYVALUE_DELIMITER + 
			ServiceParameters.ON + 
			ServiceParameters.PROFILE_LINE_DELIMITER;
		}else{
			serviceStatusString += ServiceParameters.CALL_MONITOR_STRING + 
			ServiceParameters.PROFILE_KEYVALUE_DELIMITER + 
			ServiceParameters.OFF + 
			ServiceParameters.PROFILE_LINE_DELIMITER;
		}
		
		if((serviceStatus & ServiceParameters.GPS) != 0){
			serviceStatusString += ServiceParameters.GPS_STRING + 
			ServiceParameters.PROFILE_KEYVALUE_DELIMITER + 
			ServiceParameters.ON + 
			ServiceParameters.PROFILE_LINE_DELIMITER;
		}else{
			serviceStatusString += ServiceParameters.GPS_STRING + 
			ServiceParameters.PROFILE_KEYVALUE_DELIMITER + 
			ServiceParameters.OFF + 
			ServiceParameters.PROFILE_LINE_DELIMITER;
		}
		
		if((serviceStatus & ServiceParameters.GYRO) != 0){
			serviceStatusString += ServiceParameters.GYRO_STRING + 
			ServiceParameters.PROFILE_KEYVALUE_DELIMITER + 
			ServiceParameters.ON + 
			ServiceParameters.PROFILE_LINE_DELIMITER;
		}else{
			serviceStatusString += ServiceParameters.GYRO_STRING + 
			ServiceParameters.PROFILE_KEYVALUE_DELIMITER + 
			ServiceParameters.OFF + 
			ServiceParameters.PROFILE_LINE_DELIMITER;
		}
		
		if((serviceStatus & ServiceParameters.LIGHT) != 0){
			serviceStatusString += ServiceParameters.LIGHT_STRING + 
			ServiceParameters.PROFILE_KEYVALUE_DELIMITER + 
			ServiceParameters.ON + 
			ServiceParameters.PROFILE_LINE_DELIMITER;
		}else{
			serviceStatusString += ServiceParameters.LIGHT_STRING + 
			ServiceParameters.PROFILE_KEYVALUE_DELIMITER + 
			ServiceParameters.OFF + 
			ServiceParameters.PROFILE_LINE_DELIMITER;
		}
		
		if((serviceStatus & ServiceParameters.ORIENTATION) != 0){
			serviceStatusString += ServiceParameters.ORIENTATION_STRING + 
			ServiceParameters.PROFILE_KEYVALUE_DELIMITER + 
			ServiceParameters.ON + 
			ServiceParameters.PROFILE_LINE_DELIMITER;
		}else{
			serviceStatusString += ServiceParameters.ORIENTATION_STRING + 
			ServiceParameters.PROFILE_KEYVALUE_DELIMITER + 
			ServiceParameters.OFF + 
			ServiceParameters.PROFILE_LINE_DELIMITER;
		}
		
		if((serviceStatus & ServiceParameters.TEMPERATURE) != 0){
			serviceStatusString += ServiceParameters.TEMPERATURE_STRING + 
			ServiceParameters.PROFILE_KEYVALUE_DELIMITER + 
			ServiceParameters.ON + 
			ServiceParameters.PROFILE_LINE_DELIMITER;
		}else{
			serviceStatusString += ServiceParameters.TEMPERATURE_STRING + 
			ServiceParameters.PROFILE_KEYVALUE_DELIMITER + 
			ServiceParameters.OFF + 
			ServiceParameters.PROFILE_LINE_DELIMITER;
		}
		
		if((serviceStatus & ServiceParameters.WIFI_SCAN) != 0){
			serviceStatusString += ServiceParameters.WIFI_SCAN_STRING + 
			ServiceParameters.PROFILE_KEYVALUE_DELIMITER + 
			ServiceParameters.ON + 
			ServiceParameters.PROFILE_LINE_DELIMITER;
		}else{
			serviceStatusString += ServiceParameters.WIFI_SCAN_STRING + 
			ServiceParameters.PROFILE_KEYVALUE_DELIMITER + 
			ServiceParameters.OFF + 
			ServiceParameters.PROFILE_LINE_DELIMITER;
		}
		
		return serviceStatusString;
	}
	
	private static long getServiceStatusFromProfileString(String profileString){
		long serviceStatus = ServiceParameters.DEFAULT_SERVICE_STATUS;
		String[] lines = profileString.split(ServiceParameters.PROFILE_LINE_DELIMITER);
		for(String line : lines){
			if(line.equalsIgnoreCase(ServiceParameters.ACCELEROMETER_STRING + 
					ServiceParameters.PROFILE_KEYVALUE_DELIMITER + 
					ServiceParameters.OFF)){
				serviceStatus &= 0xffffffff ^ ServiceParameters.ACCELEROMETER;
			}else if(line.equalsIgnoreCase(ServiceParameters.ACCELEROMETER_STRING + 
					ServiceParameters.PROFILE_KEYVALUE_DELIMITER + 
					ServiceParameters.ON)){
				serviceStatus |= ServiceParameters.ACCELEROMETER;
			}
			
			if(line.equalsIgnoreCase(ServiceParameters.BATTERY_STATUS_STRING + 
					ServiceParameters.PROFILE_KEYVALUE_DELIMITER + 
					ServiceParameters.OFF)){
				serviceStatus &= 0xffffffff ^ ServiceParameters.BATTERY_STATUS;
			}else if(line.equalsIgnoreCase(ServiceParameters.BATTERY_STATUS_STRING + 
					ServiceParameters.PROFILE_KEYVALUE_DELIMITER + 
					ServiceParameters.ON)){
				serviceStatus |= ServiceParameters.BATTERY_STATUS;
			}
			
			if(line.equalsIgnoreCase(ServiceParameters.CALL_MONITOR_STRING + 
					ServiceParameters.PROFILE_KEYVALUE_DELIMITER + 
					ServiceParameters.OFF)){
				serviceStatus &= 0xffffffff ^ ServiceParameters.CALL_MONITOR;
			}else if(line.equalsIgnoreCase(ServiceParameters.CALL_MONITOR_STRING + 
					ServiceParameters.PROFILE_KEYVALUE_DELIMITER + 
					ServiceParameters.ON)){
				serviceStatus |= ServiceParameters.CALL_MONITOR;
			}
			
			if(line.equalsIgnoreCase(ServiceParameters.COMPASS_STRING + 
					ServiceParameters.PROFILE_KEYVALUE_DELIMITER + 
					ServiceParameters.OFF)){
				serviceStatus &= 0xffffffff ^ ServiceParameters.COMPASS;
			}else if(line.equalsIgnoreCase(ServiceParameters.COMPASS_STRING + 
					ServiceParameters.PROFILE_KEYVALUE_DELIMITER + 
					ServiceParameters.ON)){
				serviceStatus |= ServiceParameters.COMPASS;
			}
			
			if(line.equalsIgnoreCase(ServiceParameters.GPS_STRING + 
					ServiceParameters.PROFILE_KEYVALUE_DELIMITER + 
					ServiceParameters.OFF)){
				serviceStatus &= 0xffffffff ^ ServiceParameters.GPS;
			}else if(line.equalsIgnoreCase(ServiceParameters.GPS_STRING + 
					ServiceParameters.PROFILE_KEYVALUE_DELIMITER + 
					ServiceParameters.ON)){
				serviceStatus |= ServiceParameters.GPS;
			}
			
			if(line.equalsIgnoreCase(ServiceParameters.GYRO_STRING + 
					ServiceParameters.PROFILE_KEYVALUE_DELIMITER + 
					ServiceParameters.OFF)){
				serviceStatus &= 0xffffffff ^ ServiceParameters.GYRO;
			}else if(line.equalsIgnoreCase(ServiceParameters.GYRO_STRING + 
					ServiceParameters.PROFILE_KEYVALUE_DELIMITER + 
					ServiceParameters.ON)){
				serviceStatus |= ServiceParameters.GYRO;
			}
			
			if(line.equalsIgnoreCase(ServiceParameters.LIGHT_STRING + 
					ServiceParameters.PROFILE_KEYVALUE_DELIMITER + 
					ServiceParameters.OFF)){
				serviceStatus &= 0xffffffff ^ ServiceParameters.LIGHT;
			}else if(line.equalsIgnoreCase(ServiceParameters.LIGHT_STRING + 
					ServiceParameters.PROFILE_KEYVALUE_DELIMITER + 
					ServiceParameters.ON)){
				serviceStatus |= ServiceParameters.LIGHT;
			}
			
			if(line.equalsIgnoreCase(ServiceParameters.ORIENTATION_STRING + 
					ServiceParameters.PROFILE_KEYVALUE_DELIMITER + 
					ServiceParameters.OFF)){
				serviceStatus &= 0xffffffff ^ ServiceParameters.ORIENTATION;
			}else if(line.equalsIgnoreCase(ServiceParameters.ORIENTATION_STRING + 
					ServiceParameters.PROFILE_KEYVALUE_DELIMITER + 
					ServiceParameters.ON)){
				serviceStatus |= ServiceParameters.ORIENTATION;
			}
			
			if(line.equalsIgnoreCase(ServiceParameters.TEMPERATURE_STRING + 
					ServiceParameters.PROFILE_KEYVALUE_DELIMITER + 
					ServiceParameters.OFF)){
				serviceStatus &= 0xffffffff ^ ServiceParameters.TEMPERATURE;
			}else if(line.equalsIgnoreCase(ServiceParameters.TEMPERATURE_STRING + 
					ServiceParameters.PROFILE_KEYVALUE_DELIMITER + 
					ServiceParameters.ON)){
				serviceStatus |= ServiceParameters.TEMPERATURE;
			}
			
			if(line.equalsIgnoreCase(ServiceParameters.WIFI_SCAN_STRING + 
					ServiceParameters.PROFILE_KEYVALUE_DELIMITER + 
					ServiceParameters.OFF)){
				serviceStatus &= 0xffffffff ^ ServiceParameters.WIFI_SCAN;
			}else if(line.equalsIgnoreCase(ServiceParameters.WIFI_SCAN_STRING + 
					ServiceParameters.PROFILE_KEYVALUE_DELIMITER + 
					ServiceParameters.ON)){
				serviceStatus |= ServiceParameters.WIFI_SCAN;
			}
		}
		
		
		return serviceStatus;
		
	}
	
	private static String constructServiceParametersProfileString(ServiceParameters params){
		String serviceParametersProfileString = "";
		serviceParametersProfileString += ServiceParameters.BATTERY_STATUS_SCANINTERVAL_STRING +
			ServiceParameters.PROFILE_KEYVALUE_DELIMITER +
			String.valueOf(params.getServiceParameter(ServiceParameters.BATTERY_STATUS)) +
			ServiceParameters.PROFILE_LINE_DELIMITER;
		
		serviceParametersProfileString += ServiceParameters.GPS_SCANINTERVAL_STRING +
		ServiceParameters.PROFILE_KEYVALUE_DELIMITER +
		String.valueOf(params.getServiceParameter(ServiceParameters.GPS)) +
		ServiceParameters.PROFILE_LINE_DELIMITER;
		
		serviceParametersProfileString += ServiceParameters.SENSORS_SAMPLEINTERVAL_STRING +
		ServiceParameters.PROFILE_KEYVALUE_DELIMITER +
		String.valueOf(params.getServiceParameter(ServiceParameters.ACCELEROMETER |
				ServiceParameters.COMPASS |
				ServiceParameters.GYRO |
				ServiceParameters.ORIENTATION |
				ServiceParameters.TEMPERATURE |
				ServiceParameters.LIGHT)) +
		ServiceParameters.PROFILE_LINE_DELIMITER;
		
		serviceParametersProfileString += ServiceParameters.WIFI_SCANINTERVAL_STRING +
		ServiceParameters.PROFILE_KEYVALUE_DELIMITER +
		String.valueOf(params.getServiceParameter(ServiceParameters.WIFI_SCAN)) +
		ServiceParameters.PROFILE_LINE_DELIMITER;
		
		serviceParametersProfileString += ServiceParameters.UPLOAD_INTERVAL_STRING +
		ServiceParameters.PROFILE_KEYVALUE_DELIMITER +
		String.valueOf(params.getServiceParameter(ServiceParameters.SYSTEM_DUMP_INTERVAL)) +
		ServiceParameters.PROFILE_LINE_DELIMITER;
		
		serviceParametersProfileString += ServiceParameters.ENABLE_DUMP_BY_INTERVAL +
		ServiceParameters.PROFILE_KEYVALUE_DELIMITER +
		String.valueOf(!params.getServiceParameterBoolean(ServiceParameters.ENABLE_SYSTEM_DUMP_BY_INTERVAL)) +
		ServiceParameters.PROFILE_LINE_DELIMITER;
		
		serviceParametersProfileString += ServiceParameters.DEBUG_MODE_STRING +
		ServiceParameters.PROFILE_KEYVALUE_DELIMITER +
		String.valueOf(params.getServiceParameterBoolean(ServiceParameters.DEBUG_MODE)) +
		ServiceParameters.PROFILE_LINE_DELIMITER;
		
		serviceParametersProfileString += ServiceParameters.GET_PROFILE_INTERVAL_STRING +
		ServiceParameters.PROFILE_KEYVALUE_DELIMITER +
		String.valueOf(params.getServiceParameter(ServiceParameters.GET_PROFILE_INTERVAL)) +
		ServiceParameters.PROFILE_LINE_DELIMITER;
		
		serviceParametersProfileString += ServiceParameters.ANNO_REQUEST_INTERVAL_STRING +
		ServiceParameters.PROFILE_KEYVALUE_DELIMITER +
		String.valueOf(params.getServiceParameter(ServiceParameters.ANNO_REQUEST_INTERVAL)) +
		ServiceParameters.PROFILE_LINE_DELIMITER;
		
		serviceParametersProfileString += ServiceParameters.GPS_SLOWSTART_THRESHOLD_STRING +
		ServiceParameters.PROFILE_KEYVALUE_DELIMITER +
		String.valueOf(params.getServiceParameter(ServiceParameters.GPS_SLOWSTART_THRESHOLD)) +
		ServiceParameters.PROFILE_LINE_DELIMITER;
		
		serviceParametersProfileString += ServiceParameters.GPS_SAMELOCATION_THRESHOLD_STRING +
		ServiceParameters.PROFILE_KEYVALUE_DELIMITER +
		String.valueOf(params.getServiceParameter(ServiceParameters.GPS_SAMELOCATION_THRESHOLD)) +
		ServiceParameters.PROFILE_LINE_DELIMITER;
		
		serviceParametersProfileString += ServiceParameters.OUTDOOR_WIFI_SCANINTERVAL_STRING +
		ServiceParameters.PROFILE_KEYVALUE_DELIMITER +
		String.valueOf(params.getServiceParameter(ServiceParameters.OUTDOOR_WIFI_SCAN_INTERVAL)) +
		ServiceParameters.PROFILE_LINE_DELIMITER;
		
		serviceParametersProfileString += ServiceParameters.WIFI_SAMELOCATION_THRESHOLD_STRING +
		ServiceParameters.PROFILE_KEYVALUE_DELIMITER +
		String.valueOf(params.getServiceParameter(ServiceParameters.WIFI_SAMELOCATION_THRESHOLD)) +
		ServiceParameters.PROFILE_LINE_DELIMITER;
		
		serviceParametersProfileString += ServiceParameters.ACTIVITY_MERGE_THRESHOLD_STRING +
		ServiceParameters.PROFILE_KEYVALUE_DELIMITER +
		String.valueOf(params.getServiceParameter(ServiceParameters.ACTIVITY_MERGE_THRESHOLD)) +
		ServiceParameters.PROFILE_LINE_DELIMITER;
		
		serviceParametersProfileString += ServiceParameters.SIMILAR_ACTIVITY_THRESHOLD_STRING +
		ServiceParameters.PROFILE_KEYVALUE_DELIMITER +
		String.valueOf(params.getServiceParameter(ServiceParameters.SIMILAR_ACTIVITY_THRESHOLD)) +
		ServiceParameters.PROFILE_LINE_DELIMITER;
		
		serviceParametersProfileString += ServiceParameters.COLLECTION_DATA_SIZE_STRING +
		ServiceParameters.PROFILE_KEYVALUE_DELIMITER +
		String.valueOf(params.getServiceParameter(ServiceParameters.COLLECTION_DATA_SIZE)) +
		ServiceParameters.PROFILE_LINE_DELIMITER;
		
		serviceParametersProfileString += ServiceParameters.WINDOW_SIZE_STRING +
		ServiceParameters.PROFILE_KEYVALUE_DELIMITER +
		String.valueOf(params.getServiceParameter(ServiceParameters.WINDOW_SIZE)) +
		ServiceParameters.PROFILE_LINE_DELIMITER;
		
		serviceParametersProfileString += ServiceParameters.STEP_SIZE_STRING +
		ServiceParameters.PROFILE_KEYVALUE_DELIMITER +
		String.valueOf(params.getServiceParameter(ServiceParameters.STEP_SIZE)) +
		ServiceParameters.PROFILE_LINE_DELIMITER;
		
		serviceParametersProfileString += ServiceParameters.NGRAM_MAX_N_STRING +
		ServiceParameters.PROFILE_KEYVALUE_DELIMITER +
		String.valueOf(params.getServiceParameter(ServiceParameters.NGRAM_MAX_N)) +
		ServiceParameters.PROFILE_LINE_DELIMITER;
		
		serviceParametersProfileString += ServiceParameters.COSINE_SIM_THRESHOLD_STRING +
		ServiceParameters.PROFILE_KEYVALUE_DELIMITER +
		String.valueOf(params.getServiceParameter(ServiceParameters.COSINE_SIM_THRESHOLD)) +
		ServiceParameters.PROFILE_LINE_DELIMITER;
		
		serviceParametersProfileString += ServiceParameters.SIMILAR_ACTIVITY_THRESHOLD_2_STRING +
		ServiceParameters.PROFILE_KEYVALUE_DELIMITER +
		String.valueOf(params.getServiceParameter(ServiceParameters.SIMILAR_ACTIVITY_THRESHOLD_2)) +
		ServiceParameters.PROFILE_LINE_DELIMITER;
		
		serviceParametersProfileString += ServiceParameters.EDGE_DISTANCE_STRING +
		ServiceParameters.PROFILE_KEYVALUE_DELIMITER +
		String.valueOf(params.getServiceParameter(ServiceParameters.EDGE_DISTANCE)) +
		ServiceParameters.PROFILE_LINE_DELIMITER;
		
		serviceParametersProfileString += ServiceParameters.MAX_SAMPLE_LENGTH_STRING +
		ServiceParameters.PROFILE_KEYVALUE_DELIMITER +
		String.valueOf(params.getServiceParameter(ServiceParameters.MAX_SAMPLE_LENGTH)) +
		ServiceParameters.PROFILE_LINE_DELIMITER;
		
		serviceParametersProfileString += ServiceParameters.PROC_SCAN_INTERVAL_STRING +
		ServiceParameters.PROFILE_KEYVALUE_DELIMITER +
		String.valueOf(params.getServiceParameter(ServiceParameters.PROC_SCAN_INTERVAL)) +
		ServiceParameters.PROFILE_LINE_DELIMITER;
		
		serviceParametersProfileString += ServiceParameters.GPS_OPEN_WINDOW_STRING +
		ServiceParameters.PROFILE_KEYVALUE_DELIMITER +
		String.valueOf(params.getServiceParameter(ServiceParameters.GPS_OPEN_WINDOW)) +
		ServiceParameters.PROFILE_LINE_DELIMITER;
		
		serviceParametersProfileString += ServiceParameters.PHONE_WAKEUP_DURATION_STRING +
		ServiceParameters.PROFILE_KEYVALUE_DELIMITER +
		String.valueOf(params.getServiceParameter(ServiceParameters.PHONE_WAKEUP_DURATION)) +
		ServiceParameters.PROFILE_LINE_DELIMITER;
		
		serviceParametersProfileString += ServiceParameters.MAX_GPS_CYCLE_STRING +
		ServiceParameters.PROFILE_KEYVALUE_DELIMITER +
		String.valueOf(params.getServiceParameter(ServiceParameters.MAX_GPS_CYCLE)) +
		ServiceParameters.PROFILE_LINE_DELIMITER;
		
		serviceParametersProfileString += ServiceParameters.AUDIO_SAMPLING_INTERVAL_STRING +
		ServiceParameters.PROFILE_KEYVALUE_DELIMITER +
		String.valueOf(params.getServiceParameter(ServiceParameters.AUDIO_SAMPLING_INTERVAL)) +
		ServiceParameters.PROFILE_LINE_DELIMITER;
		
		serviceParametersProfileString += ServiceParameters.PROFILE_TIMEOUT_STRING +
		ServiceParameters.PROFILE_KEYVALUE_DELIMITER +
		String.valueOf(params.getServiceParameter(ServiceParameters.PROFILE_TIMEOUT)) +
		ServiceParameters.PROFILE_LINE_DELIMITER;
		
		serviceParametersProfileString += ServiceParameters.SWITCH_MESSAGE_STRING +
		ServiceParameters.PROFILE_KEYVALUE_DELIMITER +
		String.valueOf(params.getServiceParameterString(ServiceParameters.SWITCH_MESSAGE)) +
		ServiceParameters.PROFILE_LINE_DELIMITER;
		
		return serviceParametersProfileString;
	}
	
	private void setAdditionalInfoFromProfileString(String profileString){
		String[] lines = profileString.split(ServiceParameters.PROFILE_LINE_DELIMITER);
		for(String line : lines){
			// I has such a bug that no one even find it....
			String[] keyValue = line.replace("\r", "").replace("\n", "").split(ServiceParameters.PROFILE_KEYVALUE_DELIMITER);
			int value = 0;
			
			if(keyValue.length != 2)
				continue;
			
			if(keyValue[0].equalsIgnoreCase(ServiceParameters.ID_STRING)){
				value = Integer.parseInt(keyValue[1]);
				setId(value);
			}
		}
	}
	
	private void setServiceParametersFromProfileString(String profileString){
		String[] lines = profileString.split(ServiceParameters.PROFILE_LINE_DELIMITER);
		for(String line : lines){
			// I has such a bug that no one even find it....
			String[] keyValue = line.replace("\r", "").replace("\n", "").split(ServiceParameters.PROFILE_KEYVALUE_DELIMITER);
			int value = 0;
			
			if(keyValue.length != 2)
				continue;
			
			if(keyValue[0].equalsIgnoreCase(ServiceParameters.BATTERY_STATUS_SCANINTERVAL_STRING)){
				value = Integer.parseInt(keyValue[1]);
				if(value > ServiceParameters.BATTERY_STATUS_DEFAULT_SCANINTERVAL){
					this.setServiceParameters(ServiceParameters.BATTERY_STATUS, value);
				}
			}
			
			if(keyValue[0].equalsIgnoreCase(ServiceParameters.GPS_SCANINTERVAL_STRING)){
				value = Integer.parseInt(keyValue[1]);
				if(value > ServiceParameters.GPS_DEFAULT_SCANINTERVAL){
					this.setServiceParameters(ServiceParameters.GPS, value);
				}
			}
			
			if(keyValue[0].equalsIgnoreCase(ServiceParameters.SENSORS_SAMPLEINTERVAL_STRING)){
				value = Integer.parseInt(keyValue[1]);
				if(value > ServiceParameters.SENSORS_DEFAULT_SAMPLEINTERVAL){
					this.setServiceParameters(ServiceParameters.ACCELEROMETER |
							ServiceParameters.COMPASS |
							ServiceParameters.GYRO |
							ServiceParameters.ORIENTATION |
							ServiceParameters.TEMPERATURE |
							ServiceParameters.LIGHT, 
							value);
				}
			}
			
			if(keyValue[0].equalsIgnoreCase(ServiceParameters.WIFI_SCANINTERVAL_STRING)){
				value = Integer.parseInt(keyValue[1]);
				if(value > ServiceParameters.DEFAULT_WIFI_SCANINTERVAL){
					this.setServiceParameters(ServiceParameters.WIFI_SCAN, value);
				}
			}
			
			if(keyValue[0].equalsIgnoreCase(ServiceParameters.UPLOAD_INTERVAL_STRING)){
				value = Integer.parseInt(keyValue[1]);
				if(value > ServiceParameters.DEFAULT_UPLOAD_INTERVAL){
					this.setServiceParameters(ServiceParameters.SYSTEM_DUMP_INTERVAL, value);
				}
			}
			
			if(keyValue[0].equalsIgnoreCase(ServiceParameters.GET_PROFILE_INTERVAL_STRING)){
				value = Integer.parseInt(keyValue[1]);
				if(value > ServiceParameters.DEFAULT_GET_PROFILE_INTERVAL){
					this.setServiceParameters(ServiceParameters.GET_PROFILE_INTERVAL, value);
				}
			}
			
			if(keyValue[0].equalsIgnoreCase(ServiceParameters.ENABLE_DUMP_BY_INTERVAL)){

				this.setServiceParameters(ServiceParameters.ENABLE_SYSTEM_DUMP_BY_INTERVAL, 
						!Boolean.parseBoolean(keyValue[1]));
			}
			
			if(keyValue[0].equalsIgnoreCase(ServiceParameters.DEBUG_MODE_STRING)){
				this.setServiceParameters(ServiceParameters.DEBUG_MODE, 
						Boolean.parseBoolean(keyValue[1]));
			}
			
			if(keyValue[0].equalsIgnoreCase(ServiceParameters.ANNO_REQUEST_INTERVAL_STRING)){
				value = Integer.parseInt(keyValue[1]);
				if(value > ServiceParameters.DEFAULT_ANNO_REQUEST_INTERVAL){
					this.setServiceParameters(ServiceParameters.ANNO_REQUEST_INTERVAL, value);
				}
			}
			
			if(keyValue[0].equalsIgnoreCase(ServiceParameters.GPS_SLOWSTART_THRESHOLD_STRING)){
				value = Integer.parseInt(keyValue[1]);
				if(value > ServiceParameters.DEFAULT_GPS_SLOWSTART_THRESHOLD){
					this.setServiceParameters(ServiceParameters.GPS_SLOWSTART_THRESHOLD, value);
				}
			}
			
			if(keyValue[0].equalsIgnoreCase(ServiceParameters.GPS_SAMELOCATION_THRESHOLD_STRING)){
				value = Integer.parseInt(keyValue[1]);
				if(value > ServiceParameters.DEFAULT_GPS_SAMELOCATION_THRESHOLD){
					this.setServiceParameters(ServiceParameters.GPS_SAMELOCATION_THRESHOLD, value);
				}
			}
			
			if(keyValue[0].equalsIgnoreCase(ServiceParameters.OUTDOOR_WIFI_SCANINTERVAL_STRING)){
				value = Integer.parseInt(keyValue[1]);
				if(value > ServiceParameters.DEFAULT_OUTDOOR_WIFI_SCANINTERVAL){
					this.setServiceParameters(ServiceParameters.OUTDOOR_WIFI_SCAN_INTERVAL, value);
				}
			}
			
			if(keyValue[0].equalsIgnoreCase(ServiceParameters.WIFI_SAMELOCATION_THRESHOLD_STRING)){
				value = Integer.parseInt(keyValue[1]);
				if(value > ServiceParameters.DEFAULT_WIFI_SAMELOCATION_THRESHOLD){
					this.setServiceParameters(ServiceParameters.WIFI_SAMELOCATION_THRESHOLD, value);
				}
			}
			
			if(keyValue[0].equalsIgnoreCase(ServiceParameters.ACTIVITY_MERGE_THRESHOLD_STRING)){
				value = Integer.parseInt(keyValue[1]);
				//if(value > ServiceParameters.DEFAULT_ACTIVITY_MERGE_THRESHOLD){
				this.setServiceParameters(ServiceParameters.ACTIVITY_MERGE_THRESHOLD, value);
				//}
			}
			
			if(keyValue[0].equalsIgnoreCase(ServiceParameters.SIMILAR_ACTIVITY_THRESHOLD_STRING)){
				value = Integer.parseInt(keyValue[1]);
				//if(value > ServiceParameters.DEFAULT_SIMILAR_ACTIVITY_THRESHOLD){
				this.setServiceParameters(ServiceParameters.SIMILAR_ACTIVITY_THRESHOLD, value);
				//}
			}
			
			if(keyValue[0].equalsIgnoreCase(ServiceParameters.COLLECTION_DATA_SIZE_STRING)){
				value = Integer.parseInt(keyValue[1]);
				//if(value > ServiceParameters.DEFAULT_COLLECTION_DATA_SIZE){
				this.setServiceParameters(ServiceParameters.COLLECTION_DATA_SIZE, value);
				//}
			}
			
			if(keyValue[0].equalsIgnoreCase(ServiceParameters.WINDOW_SIZE_STRING)){
				value = Integer.parseInt(keyValue[1]);
				//if(value > ServiceParameters.DEFAULT_WINDOW_SIZE){
				this.setServiceParameters(ServiceParameters.WINDOW_SIZE, value);
				//}
			}
			
			if(keyValue[0].equalsIgnoreCase(ServiceParameters.STEP_SIZE_STRING)){
				value = Integer.parseInt(keyValue[1]);
				//if(value > ServiceParameters.DEFAULT_STEP_SIZE){
				this.setServiceParameters(ServiceParameters.STEP_SIZE, value);
				//}
			}
			
			if(keyValue[0].equalsIgnoreCase(ServiceParameters.NGRAM_MAX_N_STRING)){
				value = Integer.parseInt(keyValue[1]);
				//if(value > ServiceParameters.DEFAULT_NGRAM_MAX_N){
				this.setServiceParameters(ServiceParameters.NGRAM_MAX_N, value);
				//}
			}
			
			if(keyValue[0].equalsIgnoreCase(ServiceParameters.COSINE_SIM_THRESHOLD_STRING)){
				value = Integer.parseInt(keyValue[1]);
				//if(value > ServiceParameters.DEFAULT_COSINE_SIM_THRESHOLD){
				this.setServiceParameters(ServiceParameters.COSINE_SIM_THRESHOLD, value);
				//}
			}
			
			if(keyValue[0].equalsIgnoreCase(ServiceParameters.SIMILAR_ACTIVITY_THRESHOLD_2_STRING)){
				value = Integer.parseInt(keyValue[1]);
				//if(value > ServiceParameters.DEFAULT_SIMILAR_ACTIVITY_THRESHOLD_2){
				this.setServiceParameters(ServiceParameters.SIMILAR_ACTIVITY_THRESHOLD_2, value);
				//}
			}
			
			if(keyValue[0].equalsIgnoreCase(ServiceParameters.EDGE_DISTANCE_STRING)){
				value = Integer.parseInt(keyValue[1]);
				//if(value > ServiceParameters.DEFAULT_EDGE_DISTANCE){
				this.setServiceParameters(ServiceParameters.EDGE_DISTANCE, value);
				//}
			}
			
			if(keyValue[0].equalsIgnoreCase(ServiceParameters.MAX_SAMPLE_LENGTH_STRING)){
				value = Integer.parseInt(keyValue[1]);
				//if(value > ServiceParameters.DEFAULT_MAX_SAMPLE_LENGTH){
				this.setServiceParameters(ServiceParameters.MAX_SAMPLE_LENGTH, value);
				//}
			}
			
			if(keyValue[0].equalsIgnoreCase(ServiceParameters.PROC_SCAN_INTERVAL_STRING)){
				value = Integer.parseInt(keyValue[1]);
				if(value > ServiceParameters.DEFAULT_PROC_SCAN_INTERVAL){
					this.setServiceParameters(ServiceParameters.PROC_SCAN_INTERVAL, value);
				}
			}
			
			if(keyValue[0].equalsIgnoreCase(ServiceParameters.GPS_OPEN_WINDOW_STRING)){
				value = Integer.parseInt(keyValue[1]);
				if(value > ServiceParameters.DEFAULT_GPS_OPEN_WINDOW && value < ServiceParameters.CYCLING_BASE_MS){
					this.setServiceParameters(ServiceParameters.GPS_OPEN_WINDOW, value);
				}
			}
			
			if(keyValue[0].equalsIgnoreCase(ServiceParameters.PHONE_WAKEUP_DURATION_STRING)){
				value = Integer.parseInt(keyValue[1]);
				if(value > ServiceParameters.DEFAULT_PHONE_WAKEUP_DURATION && value < ServiceParameters.CYCLING_BASE_MS){
					this.setServiceParameters(ServiceParameters.PHONE_WAKEUP_DURATION, value);
				}
			}
			
			if(keyValue[0].equalsIgnoreCase(ServiceParameters.MAX_GPS_CYCLE_STRING)){
				value = Integer.parseInt(keyValue[1]);
				if(value > ServiceParameters.DEFAULT_MAX_GPS_CYCLE){
					this.setServiceParameters(ServiceParameters.MAX_GPS_CYCLE, value);
				}
			}
			
			if(keyValue[0].equalsIgnoreCase(ServiceParameters.AUDIO_SAMPLING_INTERVAL_STRING)){
				value = Integer.parseInt(keyValue[1]);
				//if(value > ServiceParameters.DEFAULT_AUDIO_SAMPLING_INTERVAL){
					this.setServiceParameters(ServiceParameters.AUDIO_SAMPLING_INTERVAL, value);
				//}
			}
			
			if(keyValue[0].equalsIgnoreCase(ServiceParameters.PROFILE_TIMEOUT_STRING)){
				value = Integer.parseInt(keyValue[1]);
				this.setServiceParameters(ServiceParameters.PROFILE_TIMEOUT, value);
			}
			
			if(keyValue[0].equalsIgnoreCase(ServiceParameters.SWITCH_MESSAGE_STRING)){
				this.setServiceParameters(ServiceParameters.SWITCH_MESSAGE, keyValue[1]);
			}
		}
	}
	
	public static ServiceParameters fromProfileString(String profileString){
		ServiceParameters params = new ServiceParameters();
		params.setAllServicesStatus(ServiceParameters.getServiceStatusFromProfileString(profileString));
		params.setServiceParametersFromProfileString(profileString);
		params.setAdditionalInfoFromProfileString(profileString);
		
		return params;
	}
	
	public interface GetSpecialProfileCallback {
		void done(ServiceParameters[] specialProfiles);
	}
	
	public static ServiceParameters[] parseSpecialProfile(String response){
		try {
			JSONArray jsonArray = new JSONArray(response);
			ServiceParameters[] profiles = new ServiceParameters[jsonArray.length()];
			for(int i = 0; i < profiles.length; i++){
				JSONObject jsonObject = jsonArray.getJSONObject(i);
				JSONObject profileJSON = jsonObject.getJSONObject("profile");
				String profileString = profileJSON.getString("config");
				String profileName = profileJSON.getString("name");
				long id = profileJSON.getLong("id");
				
				
				ServiceParameters profile = ServiceParameters.fromProfileString(profileString);
				profile.setName(profileName);
				profile.setId(id);
				String message = profile.getServiceParameterString(ServiceParameters.SWITCH_MESSAGE);
				Log.i("ServiceParameter", message);
				
				String base64Photo = profileJSON.getString("photo");
				if(base64Photo.length() > 0){
					byte[] binaryPhoto = Base64.decode(base64Photo);
					Bitmap profileImage = BitmapFactory.decodeByteArray(binaryPhoto, 0, binaryPhoto.length);
					profile.setProfileImage(profileImage);
				}
				
				profiles[i] = profile;
				
			}
			
			return profiles;
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return new ServiceParameters[0];
	}
	
	/*
	 * Honestly speaking, I think this function is crappy because it brings in
	 * all kinds of dependencies from the mobisens.net package.
	 */
	public static ServiceParameters[] getSpecialProfiles(){
		Hashtable<String, String> params = new Hashtable<String, String>();
		params.put("key", HttpRequestForCMUSVProjects.DEFAULT_UPLOADKEY);
		HttpGetRequest request = new HttpGetRequest();
		
		File profileFile = new File(Directory.SPECIAL_PROFILE);
		
		
		String response = request.send(URLs.GET_SPECIAL_PROFILE_URL, params);
		
		
		
		if(response.equals("")){
			return new ServiceParameters[0];
		}else{
			FileOperation.writeStringToFile(profileFile, response);
			return parseSpecialProfile(response);
		}
	}
	
	public static ServiceParameters[] getCachedSpecialProfiles(){
		
		final File profileFile = new File(Directory.SPECIAL_PROFILE);
		
		String response = FileOperation.readFileAsString(profileFile);
		if(response.equals("")){
			return new ServiceParameters[0];
		}else{
			return parseSpecialProfile(response);
		}
	}
	
	public void setName(String name){
		this.name = name;
	}
	
	public String getName(){
		return this.name;
	}
	
	public void setId(long id){
		this.id = id;
	}
	
	public long getId(){
		return this.id;
	}
	
	public Bitmap getProfileImage(){
		return this.profileImage;
	}
	
	public void setProfileImage(Bitmap image){
		this.profileImage = image;
	}
	
	public void setAllServicesStatus(long status){
		serviceStatus = status;
	}
	
	public long getAllServicesStatus(){
		return serviceStatus;
	}
	
	public boolean getServicesStatus(long service){
		return (getAllServicesStatus() & service) != 0;
	}
	
	public void setServicesStatusEnabled(long service){
		if(!getServicesStatus(service)){
			setAllServicesStatus(getAllServicesStatus() | service);
		}
	}
	
	public void setServicesStatusDisabled(long service){
		if(getServicesStatus(service)){
			setAllServicesStatus(getAllServicesStatus() - service);
		}
	}
	
	public void setServiceParameters(long params, boolean value){
		if((params & ServiceParameters.ENABLE_SYSTEM_DUMP_BY_INTERVAL) != 0){
			this.enbleSystemInfoDumpByInterval = value;
		}
		
		if((params & ServiceParameters.DEBUG_MODE) != 0){
			this.debugMode = value;
		}
	}
	
	public void setServiceParameters(long params, String value){
		if((params & ServiceParameters.PHONE_POSITION) != 0){
			this.phonePosition = value;
		}
		
		if((params & ServiceParameters.NEUROSKY_MINDBAND_ADDRESS) != 0){
			this.neuroSkyInputDeviceAddress = value;
		}
		
		if((params & ServiceParameters.SWITCH_MESSAGE) != 0){
			this.switchMessage = value.replace("\\r", "\r").replace("\\n", "\n");
		}
	}
	
	public void setServiceParameters(long params, long value){
		if((params & ServiceParameters.ACCELEROMETER) != 0 ||
			(params & ServiceParameters.GYRO) != 0 ||
			(params & ServiceParameters.COMPASS) != 0 ||
			(params & ServiceParameters.ORIENTATION) != 0 ||
			(params & ServiceParameters.TEMPERATURE) != 0 ||
			(params & ServiceParameters.LIGHT) != 0
			){
			this.sensorsSamplingRate = value;
		}
			
		if((params & ServiceParameters.WIFI_SCAN) != 0){
			this.wifiScanSamplingRate = value;
		}
		
		if((params & ServiceParameters.BATTERY_STATUS) != 0){
			this.batteryStatusSamplingRate = value;
		}
		
		if((params & ServiceParameters.GPS) != 0){
			this.gpsSamplingRate = value;
		}
		
		if((params & ServiceParameters.AUDIO_RECORDING) != 0){
			this.audioSamplingRate = value;
		}
		
		if((params & ServiceParameters.VIDEO_RECORDING) != 0){
			this.photoCapturingRate = value;
		}
		
		if((params & ServiceParameters.VIDEO_PREVIEW) != 0){
			this.previewVideoFrameRate = value;
		}
		
		if((params & ServiceParameters.PHOTO_WIDTH) != 0){
			this.photoWidth = value;
		}
		
		if((params & ServiceParameters.PHOTO_HEIGHT) != 0){
			this.photoHeight = value;
		}
		
		if((params & ServiceParameters.PHTOT_QUALITY) != 0){
			this.photoQuality = value;
		}
		
		if((params & ServiceParameters.PHOTO_ROTATION) != 0){
			this.photoRotationDegree = value;
		}
		
		if((params & ServiceParameters.SYSTEM_DUMP_INTERVAL) != 0){
			this.systemInfoDumpInterval = value;
		}
		
		if((params & ServiceParameters.VIDEO_RECORDING_MODE) != 0){
			this.videoRecordingMode = value;
		}
		
		if((params & ServiceParameters.GET_PROFILE_INTERVAL) != 0){
			this.getProfileInterval = value;
		}
		
		if((params & ServiceParameters.ANNO_REQUEST_INTERVAL) != 0){
			this.annoRequestInterval = value;
		}
		
		if((params & ServiceParameters.GPS_SLOWSTART_THRESHOLD) != 0){
			this.gpsSlowStartThreshold = value;
		}
		
		if((params & ServiceParameters.GPS_SAMELOCATION_THRESHOLD) != 0){
			this.gpsSameLocationThreshold = value;
		}
		
		if((params & ServiceParameters.OUTDOOR_WIFI_SCAN_INTERVAL) != 0){
			this.wifiOutdoorScanInterval = value;
		}
		
		if((params & ServiceParameters.WIFI_SAMELOCATION_THRESHOLD) != 0){
			this.wifiSameLocationThreshold = value;
		}
		
		if((params & ServiceParameters.ACTIVITY_MERGE_THRESHOLD) != 0){
			this.activityMergeThreshold = value;
		}
		
		if((params & ServiceParameters.SIMILAR_ACTIVITY_THRESHOLD) != 0){
			this.similarActivityThreshold = value;
		}
		
		if((params & ServiceParameters.COLLECTION_DATA_SIZE) != 0){
			this.collectionSize = value;
		}
		
		if((params & ServiceParameters.WINDOW_SIZE) != 0){
			this.lifeloggerWindowSize = value;
		}
		
		if((params & ServiceParameters.STEP_SIZE) != 0){
			this.lifeloggerStepSize = value;
		}
		
		if((params & ServiceParameters.NGRAM_MAX_N) != 0){
			this.ngramMaxN = value;
		}
		
		if((params & ServiceParameters.COSINE_SIM_THRESHOLD) != 0){
			this.cosineThreshold = value;
		}
		
		if((params & ServiceParameters.SIMILAR_ACTIVITY_THRESHOLD_2) != 0){
			this.similarActivityThreshold2 = value;
		}
		
		if((params & ServiceParameters.EDGE_DISTANCE) != 0){
			this.edgeDistance = value;
		}
		
		if((params & ServiceParameters.MAX_SAMPLE_LENGTH) != 0){
			this.maxSampleLength = value;
		}
		
		if((params & ServiceParameters.PROC_SCAN_INTERVAL) != 0){
			this.procScanInterval = value;
		}
		
		if((params & ServiceParameters.GPS_OPEN_WINDOW) != 0){
			this.gpsOpenWindow = value;
		}
		
		if((params & ServiceParameters.PHONE_WAKEUP_DURATION) != 0){
			this.phoneWakeupDuration = value;
		}
		
		if((params & ServiceParameters.MAX_GPS_CYCLE) != 0){
			this.maxGPSCycle = value;
		}
		
		if((params & ServiceParameters.AUDIO_SAMPLING_INTERVAL) != 0){
			this.audioSampleInterval = value;
		}
		
		if((params & ServiceParameters.PROFILE_TIMEOUT) != 0){
			this.profileTimeOut = value;
		}
	}
	
	public String getServiceParameterString(long param){
		if((param & ServiceParameters.PHONE_POSITION) != 0){
			return this.phonePosition;
		}else if((param & ServiceParameters.NEUROSKY_MINDBAND_ADDRESS) != 0){
			return this.neuroSkyInputDeviceAddress;
		}else if((param & ServiceParameters.ENABLE_SYSTEM_DUMP_BY_INTERVAL) != 0 ||
				(param & ServiceParameters.DEBUG_MODE) != 0
				){
			return String.valueOf(getServiceParameterBoolean(param));
		}else if((param & ServiceParameters.SWITCH_MESSAGE) != 0){
			return this.switchMessage;
		}else{
			return String.valueOf(getServiceParameter(param));
		}
	}
	
	public boolean getServiceParameterBoolean(long param){
		if((param & ServiceParameters.ENABLE_SYSTEM_DUMP_BY_INTERVAL) != 0){
			return this.enbleSystemInfoDumpByInterval;
		}
		
		if((param & ServiceParameters.DEBUG_MODE) != 0){
			return this.debugMode;
		}
		
		return false;
	}
	
	public long getServiceParameter(long param){
		if((param & ServiceParameters.ACCELEROMETER) != 0 ||
			(param & ServiceParameters.GYRO) != 0 ||
			(param & ServiceParameters.COMPASS) != 0 ||
			(param & ServiceParameters.ORIENTATION) != 0 ||
			(param & ServiceParameters.TEMPERATURE) != 0 ||
			(param & ServiceParameters.LIGHT) != 0
			){
			return this.sensorsSamplingRate;
		}
			
		if((param & ServiceParameters.WIFI_SCAN) != 0){
			return this.wifiScanSamplingRate;
		}
		
		if((param & ServiceParameters.BATTERY_STATUS) != 0){
			return this.batteryStatusSamplingRate;
		}
		
		if((param & ServiceParameters.GPS) != 0){
			return this.gpsSamplingRate;
		}
		
		if((param & ServiceParameters.AUDIO_RECORDING) != 0){
			return this.audioSamplingRate;
		}
		
		if((param & ServiceParameters.VIDEO_RECORDING) != 0){
			return this.photoCapturingRate;
		}
		
		if((param & ServiceParameters.VIDEO_PREVIEW) != 0){
			return this.previewVideoFrameRate;
		}
		
		if((param & ServiceParameters.PHOTO_WIDTH) != 0){
			return this.photoWidth;
		}
		
		if((param & ServiceParameters.PHOTO_HEIGHT) != 0){
			return this.photoHeight;
		}
		
		if((param & ServiceParameters.PHTOT_QUALITY) != 0){
			return this.photoQuality;
		}
		
		if((param & ServiceParameters.PHOTO_ROTATION) != 0){
			return this.photoRotationDegree;
		}
		
		if((param & ServiceParameters.SYSTEM_DUMP_INTERVAL) != 0){
			return this.systemInfoDumpInterval;
		}
		
		if((param & ServiceParameters.VIDEO_RECORDING_MODE) != 0){
			return this.videoRecordingMode;
		}
		
		if((param & ServiceParameters.GET_PROFILE_INTERVAL) != 0){
			return this.getProfileInterval;
		}
		
		if((param & ServiceParameters.ANNO_REQUEST_INTERVAL) != 0){
			return this.annoRequestInterval;
		}
		
		if((param & ServiceParameters.GPS_SLOWSTART_THRESHOLD) != 0){
			return this.gpsSlowStartThreshold;
		}
		
		if((param & ServiceParameters.GPS_SAMELOCATION_THRESHOLD) != 0){
			return this.gpsSameLocationThreshold;
		}
		
		if((param & ServiceParameters.OUTDOOR_WIFI_SCAN_INTERVAL) != 0){
			return this.wifiOutdoorScanInterval;
		}
		
		if((param & ServiceParameters.WIFI_SAMELOCATION_THRESHOLD) != 0){
			return this.wifiSameLocationThreshold;
		}
		
		if((param & ServiceParameters.ACTIVITY_MERGE_THRESHOLD) != 0){
			return this.activityMergeThreshold;
		}
		
		if((param & ServiceParameters.SIMILAR_ACTIVITY_THRESHOLD) != 0){
			return this.similarActivityThreshold;
		}
		
		if((param & ServiceParameters.COLLECTION_DATA_SIZE) != 0){
			return this.collectionSize;
		}
		
		if((param & ServiceParameters.WINDOW_SIZE) != 0){
			return this.lifeloggerWindowSize;
		}
		
		if((param & ServiceParameters.STEP_SIZE) != 0){
			return this.lifeloggerStepSize;
		}
		
		if((param & ServiceParameters.NGRAM_MAX_N) != 0){
			return this.ngramMaxN;
		}
		
		if((param & ServiceParameters.COSINE_SIM_THRESHOLD) != 0){
			return this.cosineThreshold;
		}
		
		if((param & ServiceParameters.SIMILAR_ACTIVITY_THRESHOLD_2) != 0){
			return this.similarActivityThreshold2;
		}
		
		if((param & ServiceParameters.EDGE_DISTANCE) != 0){
			return this.edgeDistance;
		}
		
		if((param & ServiceParameters.MAX_SAMPLE_LENGTH) != 0){
			return this.maxSampleLength;
		}
		
		if((param & ServiceParameters.PROC_SCAN_INTERVAL) != 0){
			return this.procScanInterval;
		}
		
		if((param & ServiceParameters.GPS_OPEN_WINDOW) != 0){
			return this.gpsOpenWindow;
		}
		
		if((param & ServiceParameters.PHONE_WAKEUP_DURATION) != 0){
			return this.phoneWakeupDuration;
		}
		
		if((param & ServiceParameters.MAX_GPS_CYCLE) != 0){
			return this.maxGPSCycle;
		}
		
		if((param & ServiceParameters.AUDIO_SAMPLING_INTERVAL) != 0){
			return this.audioSampleInterval;
		}
		
		if((param & ServiceParameters.PROFILE_TIMEOUT) != 0){
			return this.profileTimeOut;
		}
		
		return 0;
	}
	
	public void resetAllParameters(){
		this.audioSamplingRate = ServiceParameters.AUDIO_DEFAULT_SAMPLINGRATE;
		this.batteryStatusSamplingRate = ServiceParameters.BATTERY_STATUS_DEFAULT_SCANINTERVAL;
		this.gpsSamplingRate = ServiceParameters.GPS_DEFAULT_SCANINTERVAL;
		this.sensorsSamplingRate = ServiceParameters.SENSORS_DEFAULT_SAMPLEINTERVAL;
		this.photoCapturingRate = ServiceParameters.PHOTO_DEFAULT_CAPTURINGRATE;
		this.wifiScanSamplingRate = ServiceParameters.DEFAULT_WIFI_SCANINTERVAL;
		this.previewVideoFrameRate = ServiceParameters.PREVIEW_DEFAULT_FRAMERATE;
		this.photoWidth = ServiceParameters.PHOTO_DEFAULT_WIDTH;
		this.photoHeight = ServiceParameters.PHOTO_DEFAULT_HEIGHT;
		this.photoQuality = ServiceParameters.PHOTO_DEFAULT_JPEG_QUALITY;
		this.photoRotationDegree = ServiceParameters.PHOTO_DEFAULT_ROTATION_DEGREE;
		this.phonePosition = ServiceParameters.PHONE_DEFAULT_POSITION;
		this.enbleSystemInfoDumpByInterval = ServiceParameters.SYSTEM_DEFAULT_ENABLE_DUMP_BY_INTERVAL;
		this.systemInfoDumpInterval = ServiceParameters.DEFAULT_UPLOAD_INTERVAL;
		this.videoRecordingMode = ServiceParameters.VIDEO_RECORDING_PHOTO_MODE;
		this.debugMode = ServiceParameters.DEBUG_MODE_DEFAULT_VALUE;
		this.annoRequestInterval = ServiceParameters.DEFAULT_ANNO_REQUEST_INTERVAL;
		this.gpsSlowStartThreshold = ServiceParameters.DEFAULT_GPS_SLOWSTART_THRESHOLD;
		this.gpsSameLocationThreshold = ServiceParameters.DEFAULT_GPS_SAMELOCATION_THRESHOLD;
		this.wifiOutdoorScanInterval = ServiceParameters.DEFAULT_OUTDOOR_WIFI_SCANINTERVAL;
		this.wifiSameLocationThreshold = ServiceParameters.DEFAULT_WIFI_SAMELOCATION_THRESHOLD;
		this.activityMergeThreshold = ServiceParameters.DEFAULT_ACTIVITY_MERGE_THRESHOLD;
		this.similarActivityThreshold = ServiceParameters.DEFAULT_SIMILAR_ACTIVITY_THRESHOLD;
		this.collectionSize = ServiceParameters.DEFAULT_COLLECTION_DATA_SIZE;
		this.lifeloggerWindowSize = ServiceParameters.DEFAULT_WINDOW_SIZE;
		this.lifeloggerStepSize = ServiceParameters.DEFAULT_STEP_SIZE;
		this.ngramMaxN = ServiceParameters.DEFAULT_NGRAM_MAX_N;
		this.cosineThreshold = ServiceParameters.DEFAULT_COSINE_SIM_THRESHOLD;
		this.similarActivityThreshold2 = ServiceParameters.DEFAULT_SIMILAR_ACTIVITY_THRESHOLD_2;
		this.edgeDistance = ServiceParameters.DEFAULT_EDGE_DISTANCE;
		this.maxSampleLength = ServiceParameters.DEFAULT_MAX_SAMPLE_LENGTH;
		this.procScanInterval = ServiceParameters.DEFAULT_PROC_SCAN_INTERVAL;
		this.gpsOpenWindow = ServiceParameters.DEFAULT_GPS_OPEN_WINDOW;
		this.phoneWakeupDuration = ServiceParameters.DEFAULT_PHONE_WAKEUP_DURATION;
		this.maxGPSCycle = ServiceParameters.DEFAULT_MAX_GPS_CYCLE;
		this.audioSampleInterval = ServiceParameters.DEFAULT_AUDIO_SAMPLING_INTERVAL;
		this.profileTimeOut = ServiceParameters.DEFAULT_PROFILE_TIMEOUT;
		this.switchMessage = ServiceParameters.DEFAULT_SWITCH_MESSAGE;
	}
	
	private String getIDFieldString(){
		return ID_STRING + PROFILE_KEYVALUE_DELIMITER + String.valueOf(getId()) + ServiceParameters.PROFILE_LINE_DELIMITER;
	}
	
	public String toString(){
		StringBuilder buffer = new StringBuilder(1024 * 8);
		buffer.append(ServiceParameters.constructServiceStatusProfileString(this.getAllServicesStatus()))
		.append(ServiceParameters.constructServiceParametersProfileString(this))
		.append(this.getIDFieldString());
		
		return buffer.toString();
	}
}
