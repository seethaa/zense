package edu.cmu.sv.mobisens.content;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.util.Log;

import edu.cmu.sv.mobisens.io.Directory;
import edu.cmu.sv.mobisens.io.MobiSensLog;

public abstract class DataDumpingWidget extends Widget {
	
	private static final String TAG = "DataDumpingWidget";
	public static final long FILE_SPLIT_INTERVAL_MS = 1000 * 60 * 59;
	
	private BufferedWriter bufferedWriter = null;
	private File currentDataFile = null;
	private FileWriter currentDataFileWriter = null;
	protected Object syncObject = new Object();
	
	private String processName = null;
	
	protected String getCurrentProcessName(){
		return this.processName;
	}
	
	protected void setCurrentProcessName(String value){
		this.processName = value;
	}
	
	public static void boradcastFileList(String[] fileList, String listType, DataDumpingWidget dumper){
		Intent uploadIntent = new Intent(UploadWidget.ACTION_ADD_FILES);
		uploadIntent.putExtra(UploadWidget.EXTRA_DELETE_AFTER_UPLOAD, true);
		uploadIntent.putExtra(UploadWidget.EXTRA_FILE_TYPE, listType);
		uploadIntent.putExtra(UploadWidget.EXTRA_SENDER_APPNAME, dumper.getCurrentProcessName());
		uploadIntent.putExtra(UploadWidget.EXTRA_UPLOAD_FILES, fileList);
		
		dumper.getContext().sendBroadcast(uploadIntent);
	}
	
	public static void boradcastFileList(String[] fileList, String listType, Context dumper){
		Intent uploadIntent = new Intent(UploadWidget.ACTION_ADD_FILES);
		uploadIntent.putExtra(UploadWidget.EXTRA_DELETE_AFTER_UPLOAD, true);
		uploadIntent.putExtra(UploadWidget.EXTRA_FILE_TYPE, listType);
		uploadIntent.putExtra(UploadWidget.EXTRA_SENDER_APPNAME, dumper.getApplicationInfo().processName);
		uploadIntent.putExtra(UploadWidget.EXTRA_UPLOAD_FILES, fileList);
		
		dumper.sendBroadcast(uploadIntent);
	}
	
	public static void boradcastFileList(String[] fileList, String listType, boolean deleteAfterUploaded, Context dumper){
		Intent uploadIntent = new Intent(UploadWidget.ACTION_ADD_FILES);
		uploadIntent.putExtra(UploadWidget.EXTRA_DELETE_AFTER_UPLOAD, deleteAfterUploaded);
		uploadIntent.putExtra(UploadWidget.EXTRA_FILE_TYPE, listType);
		uploadIntent.putExtra(UploadWidget.EXTRA_SENDER_APPNAME, dumper.getApplicationInfo().processName);
		uploadIntent.putExtra(UploadWidget.EXTRA_UPLOAD_FILES, fileList);
		
		dumper.sendBroadcast(uploadIntent);
	}
	
	public void register(ContextWrapper contextWrapper){
		super.register(contextWrapper);
		
		this.setCurrentProcessName(this.getContext().getApplicationInfo().processName);
	}
	
	
	public void writeData(String data){
		synchronized(syncObject){
			if(bufferedWriter != null){
				try {
					bufferedWriter.write(data);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					MobiSensLog.log(e);
				}
			}
		}
	}
	
	protected abstract void switchRecordFile();
	
	protected void switchRecordFile(String dataDirectory, String filePrefix, String fileExtension) {
		synchronized(syncObject){
				closeCurrentFileStream();
			
				if(currentDataFile != null)
					Log.i(TAG, "last file: " + currentDataFile.getAbsolutePath());
				
				try {
					currentDataFile = Directory.openFile(
							dataDirectory, 
							//getDeviceID() + "_" + Recorder.RECORDER_TYPE_SYSTEM  //system data
							//getDeviceID() + "_" + Recorder.RECORDER_TYPE_SENSORS //sensor data
							filePrefix.replace(":", "_"),  // It might be a mac addr as device id. 
							//".csv"
							fileExtension);
					currentDataFileWriter = new FileWriter(currentDataFile);
					bufferedWriter = new BufferedWriter(currentDataFileWriter, 100 * 1024);
					
					Log.i(TAG, "current file: " + currentDataFile.getAbsolutePath());
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					MobiSensLog.log(e);
				}
				
		}
	}
	
	
	protected String getCurrentFileName(){
		synchronized(syncObject){
			if(this.currentDataFile == null)
				return null;
			
			return this.currentDataFile.getAbsolutePath();
		}
	}
	
	protected void flushCurrentFileStream(){
		synchronized(syncObject){
			if(bufferedWriter != null) {
				try {
					bufferedWriter.flush();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					Log.e(TAG, e.getMessage(), e);
					MobiSensLog.log(e);
				}
			}
		}
	}
	
	protected void closeCurrentFileStream(){
		synchronized(syncObject){
			if(bufferedWriter != null){
				try {
					bufferedWriter.close();
					bufferedWriter = null;
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					MobiSensLog.log(e);
				}
			}
		}
	}
}
