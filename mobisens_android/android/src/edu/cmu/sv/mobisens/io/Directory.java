package edu.cmu.sv.mobisens.io;

import java.io.*;

import edu.cmu.sv.mobisens.MobiSensService;
import edu.cmu.sv.mobisens.settings.ServiceParameters;

import android.os.Environment;
import android.util.Log;

public class Directory {
	private static final String TAG = "Directory";

	private static final String EXTERNAL_STORAGE = Environment.getExternalStorageDirectory().getAbsolutePath();
	public static final String MOBISENS_ROOT = EXTERNAL_STORAGE + "/MobiSens/";
	public static final String SENSORS_DEFAULT_DATA_FOLDER =  MOBISENS_ROOT + "Sensors/";
	public static final String SYSTEM_DEFAULT_DATA_FOLDER = MOBISENS_ROOT + "System/";
	public static final String AUDIO_DEFAULT_DATA_FOLDER =  MOBISENS_ROOT + "Audio/";
	public static final String ANNOTATION_DEFAULT_DATA_FOLDER = MOBISENS_ROOT + "Annotation/";
	public static final String SHORTVIDEO_DEFAULT_DATA_FOLDER = MOBISENS_ROOT + "Video/";
	public static final String GEO_DATA_FOLDER = MOBISENS_ROOT + "Geo/";
	public static final String ANNOLIB_DATA_FOLDER = MOBISENS_ROOT + "Libs/";
	
	
	public static final String SHORTVIDEO_TMP_OUTPUT = SHORTVIDEO_DEFAULT_DATA_FOLDER + "tmp.3gp";
	public static final String SPECIAL_PROFILE = MOBISENS_ROOT + "special_profiles.json";
	
	public static final String MODEL_INDEX_V1_FILENAME = "modelIndex.csv";
	public static final String MOTION_MODEL_INDEX_FILENAME = "modelIndex2.csv";
	public static final String GEO_MODEL_INDEX_FILENAME = "geoModelIndex.csv";
	public static final String DMW_MODEL_FILENAME = "dmw.csv";
	public static final String ANNOTATION_DATALIB_FILENAME = "annoLib2.csv";
	public static final String CENTROIDS_DB_FILENAME = "centroids.csv";
	public static final String LOCATION_CENTROIDS_FILENAME = "location_centroids.csv";
	
	
	public static final String RECORDER_TYPE_AUDIO = "audio";
	public static final String RECORDER_TYPE_VIDEO = "video";
	public static final String RECORDER_TYPE_SENSORS = "sensor";
	public static final String RECORDER_TYPE_NEOROSKY_EEG = "neuroskyeeg";
	public static final String RECORDER_TYPE_SYSTEM = "system";
	public static final String RECORDER_TYPE_ANNOTATION = "annotation";
	
	public static final int MAX_VIDEO_BIT_RATE = 600000;
	public static final int MIN_VIDEO_BIT_RATE = 255000;
	public static final long MAX_RECORDFILE_SIZE = 2 * (2 << 100);
	
	// The following values must be the same as those defined in MobiSens upload.rb
	public static final int FILE_TYPE_SYSTEM_SENSE = 1;
	public static final int FILE_TYPE_SENSOR = 2;
	public static final int FILE_TYPE_ANNOTATION = 3;
	public static final int FILE_TYPE_VIDEO = 4;
	public static final int FILE_TYPE_LOG = 5;
	public static final int FILE_TYPE_PC_SYSETEM = 6;
	public static final int FILE_TYPE_AUDIO = 7;
	
	
	public static void clearDataFiles()
	{
		//The following line only used in Lifelogger
		deleteFilesInDirectory(SYSTEM_DEFAULT_DATA_FOLDER);
		deleteFilesInDirectory(SENSORS_DEFAULT_DATA_FOLDER);
		deleteFilesInDirectory(AUDIO_DEFAULT_DATA_FOLDER);
		deleteFilesInDirectory(ANNOTATION_DEFAULT_DATA_FOLDER);
	}
	
	public static String constructPrefix(String recorderType){
		ServiceParameters params = MobiSensService.getParameters();
		String position = params.getServiceParameterString(ServiceParameters.PHONE_POSITION);
		return recorderType + "_" + position.replace(" ", "-");
	}
	
	private static void deleteFilesInDirectory(String path){
		File file = new File(path);
		if(file.exists()){
			File[] dataFiles = file.listFiles();
			for(int i = 0; i < dataFiles.length; i++){
				if(dataFiles[i].isFile())
					dataFiles[i].delete();
				if(dataFiles[i].isDirectory()){
					deleteFilesInDirectory(dataFiles[i].getAbsolutePath());
				}
			}
		}
	}
	
	
	public static File openFile(String path, String prefix, String extention) throws Exception{
		int index = 1001;
		File dataFile = null;
		
		try
		{
			do
			{
				dataFile = new File( path + Directory.constructPrefix(prefix) + "_" + index + extention );
				index++;
			}while( dataFile.exists() );
			(new File( path )).mkdirs();
		}
		catch( Exception ex )
		{
			Log.e( TAG, "ERROR CREATING DATA FILE", ex );
			throw ex;
		}
		return dataFile;
	}
	
	public static File openFile(String path, String name) throws Exception{
		File dataFile = null;
		
		try
		{
			(new File(path)).mkdirs();
			dataFile = new File(path + name);
		}
		catch( Exception ex )
		{
			Log.e( TAG, "ERROR CREATING DATA FILE", ex );
			throw ex;
		}
		return dataFile;
	}
	
	public static void createFolderForShortVideo() throws Exception{

		try
		{
			(new File(SHORTVIDEO_DEFAULT_DATA_FOLDER)).mkdirs();
		}
		catch( Exception ex )
		{
			Log.e( TAG, "ERROR CREATING SHORT VIDEO FOLDER", ex );
			throw ex;
		}
	}
	
	
}
