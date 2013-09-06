package edu.cmu.sv.mobisens.util;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;

import edu.cmu.sv.lifelogger.util.DataCollector;
import edu.cmu.sv.mobisens.io.Directory;
import edu.cmu.sv.mobisens.io.FileOperation;
import edu.cmu.sv.mobisens.io.MobiSensLog;

public class GeoIndex {
	private static final String TAG = "GeoIndex";
	
	private static boolean inited = false;
	private static boolean changed = false;
	private static LinkedList<String> dataInMemory = new LinkedList<String>();

	public static synchronized void writeData(DataCollector<double[]> locationData, long start, long end){
		//File indexFile = getIndexFile();
		if(locationData.getDataSize() == 0)
			return;
		
		if(!inited)
			return;
		
		String content = DataCollector.toStringWhenTIsDoubleArray(locationData);
		//String fileName = FileNameConstructor.getLocationFileName(start, end);
		
		String data = start + "," + end + "," + content;
		dataInMemory.add(data);
		
		changed = true;

	}
	
	public static synchronized ArrayList<double[]> getLocations(long start, long end){
		ArrayList<double[]> locations = new ArrayList<double[]>();
		
		if(!inited)
			return locations;
		
		for(String line:dataInMemory){
			String columns[] = line.split(",");
			
			if(columns.length < 3){
				continue;
			}
			
			long startTime = Long.valueOf(columns[0]).longValue();
	        long endTime = Long.valueOf(columns[1]).longValue();
	        if(endTime < start)
	        	continue;
	        if(end < startTime)
	        	break;
	        
	        String dataString = line.substring(columns[0].length() + columns[1].length() + 2);
	        DataCollector<double[]> locationData = DataCollector.fromStringWhenTIsDoubleArray(dataString);
	        LinkedList<double[]> rawData = locationData.getData();
	        for(double[] gps:rawData){
	        	double[] gpsClone = new double[gps.length];
	        	for(int i = 0; i< gps.length; i++){
	        		gpsClone[i] = gps[i];
	        	}
	        	locations.add(gpsClone);
	        }
		}
		
		
		return locations;

	}
	
	public static synchronized void removeLocationData(long from, long to){
		if(!inited)
			return;
		
		LinkedList<String> linesToDelete = new LinkedList<String>();
		
		for(String line:dataInMemory){
			String columns[] = line.split(",");
			long startTime = Long.valueOf(columns[0]).longValue();
	        long endTime = Long.valueOf(columns[1]).longValue();
	        if(endTime < from)
	        	continue;
	        if(to < startTime)
	        	break;
	        
	        linesToDelete.add(line);
		}
		
		if(linesToDelete.size() > 0){
			dataInMemory.removeAll(linesToDelete);
			changed = true;
		}

	}
	
	public static synchronized void init(){
		if(inited)
			return;
		
		inited = true;
		
		File indexFile = getIndexFile();
		if(!indexFile.exists())
			return;
		
		String content = FileOperation.readFileAsString(indexFile);
		String[] lines = content.split("\r\n");
		for(String line:lines){
			dataInMemory.add(line);
		}
	}
	
	public static synchronized void close(){
		if(!inited)
			return;
		
		inited = false;
		dataInMemory.clear();
	}
	
	public static synchronized void save(){
		if(!inited)
			return;
		
		if(!changed)
			return;
		
		StringBuilder builder = new StringBuilder(100 * dataInMemory.size());
		for(String line:dataInMemory){
			builder.append(line).append("\r\n");
		}
		File indexFile = getIndexFile();
		FileOperation.writeStringToFile(indexFile, builder.toString());
		
		changed = true;
	}
	
	public static synchronized void reset(){
		File indexFile = getIndexFile();
		if(!indexFile.exists())
			return;
		indexFile.delete();
		
		dataInMemory.clear();
		inited = false;
		
		changed = true;
	}
	
	
	public static synchronized void shrink(long timeSpanInMS){
		if(!inited)
			return;
		
		LinkedList<String> dataToRemove = new LinkedList<String>();
		long boundary = System.currentTimeMillis() - timeSpanInMS;
		
		for(String line:dataInMemory){
			String[] columns = line.split(",");
			if(columns.length < 2){
				dataToRemove.add(line);
				continue;
			}
			
			long startTime = Long.parseLong(columns[0]);
			if(startTime < boundary){
				dataToRemove.add(line);
			}
		}
		
		if(dataToRemove.size() > 0){
			dataInMemory.removeAll(dataToRemove);
			changed = true;
		}
	}
	
	private static File getIndexFile(){
		try {
			File indexFile = Directory.openFile(Directory.GEO_DATA_FOLDER, "index.geo2.csv");
			return indexFile;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			MobiSensLog.log(e);
		}
		
		
		return null;
	}
}
