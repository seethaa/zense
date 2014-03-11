package edu.cmu.sv.lifelogger.util;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;

import android.util.Log;

import edu.cmu.sv.mobisens.io.Directory;
import edu.cmu.sv.mobisens.io.FileOperation;
import edu.cmu.sv.mobisens.io.MobiSensLog;

public class AnnotationDataCollectorSet {
	private static final String TAG = "AnnotationDataCollectorSet";
	
	private HashMap<String, LinkedList<DataCollector<double[]>>> set = new HashMap<String, LinkedList<DataCollector<double[]>>>();
	
	public AnnotationDataCollectorSet(){
	}
	
	public synchronized boolean put(String anno, DataCollector<double[]> data){
		return this.put(anno, data, true);
	}
	
	public synchronized boolean put(String anno, DataCollector<double[]> data, boolean save){
		if(!set.containsKey(anno)){
			set.put(anno, new LinkedList<DataCollector<double[]>>());
		}
		
		boolean returnValue = set.get(anno).add(data);
		
		if(save){
			String fileName = this.getCollectorFileName(data);
			try {
				File dataFile = Directory.openFile(Directory.ANNOLIB_DATA_FOLDER, fileName);
				FileOperation.writeStringToFile(dataFile, DataCollector.toStringWhenTIsDoubleArray(data));
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				MobiSensLog.log(e);
			}
			
			
			try {
				File indexFile = Directory.openFile(Directory.MOBISENS_ROOT, Directory.ANNOTATION_DATALIB_FILENAME);
				FileOperation.writeStringToFile(indexFile, this.unsynchronizedToString());
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				MobiSensLog.log(e);
			}
		}
		
		return returnValue;
		
	}

	
	public synchronized LinkedList<DataCollector<double[]>> remove(String anno){
		LinkedList<DataCollector<double[]>> removeItems = set.remove(anno);
		
		if(removeItems != null){
			for(DataCollector<double[]> data:removeItems){
				String filePath = this.getCollectorFilePath(data);
				FileOperation.deleteFile(filePath);
			}
		}
		
		try {
			File indexFile = Directory.openFile(Directory.MOBISENS_ROOT, Directory.ANNOTATION_DATALIB_FILENAME);
			FileOperation.writeStringToFile(indexFile, this.unsynchronizedToString());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			MobiSensLog.log(e);
		}
		
		return removeItems;
	}
	
	private String getCollectorFilePath(DataCollector<double[]> collector){
		StringBuilder builder = new StringBuilder(100);
		builder.append(Directory.ANNOLIB_DATA_FOLDER).append(this.getCollectorFileName(collector));
		return builder.toString();
	}
	
	private String getCollectorFileName(DataCollector<double[]> collector){
		StringBuilder builder = new StringBuilder(100);
		builder.append(String.valueOf(collector.getFirstDataCollectedTime()))
		.append("_").append(String.valueOf(collector.getLastDataCollectedTime())).append(".csv");
		return builder.toString();
	}
	
	private String unsynchronizedToString(){
		StringBuilder result = new StringBuilder(1000);
		for(String anno:set.keySet()){
			LinkedList<DataCollector<double[]>> collectorList = set.get(anno);
			for(DataCollector<double[]> collector:collectorList){
				result.append(anno.replace(",", "_")).append(",").append(this.getCollectorFilePath(collector)).append("\r\n");
			}
		}
		return result.toString();
	}
	
	public String toString(){
		StringBuilder result = new StringBuilder(1000);
		synchronized(this){
			for(String anno:set.keySet()){
				LinkedList<DataCollector<double[]>> collectorList = set.get(anno);
				for(DataCollector<double[]> collector:collectorList){
					result.append(anno.replace(",", "_")).append(",").append(this.getCollectorFilePath(collector)).append("\r\n");
				}
			}
		}
		return result.toString();
	}
	
	public static AnnotationDataCollectorSet loadFromFile(){
		AnnotationDataCollectorSet resultSet = new AnnotationDataCollectorSet();
		
		try {
			File indexFile = Directory.openFile(Directory.MOBISENS_ROOT, Directory.ANNOTATION_DATALIB_FILENAME);
			String csvInput = FileOperation.readFileAsString(indexFile);
			if(csvInput.equals(""))
				return resultSet;
			String[] lines = csvInput.split("\r\n");
			if(lines.length == 0){
				return resultSet;
			}
			
			for(String line:lines){
				String[] columns = line.split(",");
				
				
				try{
					String anno = columns[0];
					String dataFilePath = columns[1];
					
					File dataFile = new File(dataFilePath);
					String collectorString = FileOperation.readFileAsString(dataFile);
					DataCollector<double[]> collector = DataCollector.fromStringWhenTIsDoubleArray(collectorString);
					
					resultSet.put(anno, collector, false);
				}catch(Exception ex){
					ex.printStackTrace();
					MobiSensLog.log(ex);
				}
				
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			MobiSensLog.log(e);
		}
		
		
		
		
		return resultSet;
	}
	
	private static void doClearDataFiles(){
		try {
			File indexFile = Directory.openFile(Directory.MOBISENS_ROOT, Directory.ANNOTATION_DATALIB_FILENAME);
			if(indexFile.exists()){
				String csvInput = FileOperation.readFileAsString(indexFile);
				if(csvInput.equals(""))
					return;
				String[] lines = csvInput.split("\r\n");
				if(lines.length == 0){
					return;
				}
				
				for(String line:lines){
					String[] columns = line.split(",");
					String dataFilePath = columns[1];
					
					try{
						File dataFile = new File(dataFilePath);
						dataFile.delete();
					}catch(Exception ex){
						ex.printStackTrace();
						MobiSensLog.log(ex);
					}
					
				}
				
				indexFile.delete();
			}
			
			String[] geoFiles = FileOperation.getFilesInDirectory(Directory.GEO_DATA_FOLDER);
			if(geoFiles.length == 0)
				return;
			for(String geoFile:geoFiles){
				FileOperation.deleteFile(geoFile);
			}
			Log.i(TAG, "Clean geo files done.");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			MobiSensLog.log(e);
		}
	}
	
	public static void clearDataFiles(){
		Thread workingThread = new Thread(){
			public void run(){
				doClearDataFiles();
			}
		};
		
		workingThread.start();
	}
	
	public HashMap<String, LinkedList<DataCollector<double[]>>> getAnnoData(){
		return new HashMap<String, LinkedList<DataCollector<double[]>>>(this.set);
	}
}
