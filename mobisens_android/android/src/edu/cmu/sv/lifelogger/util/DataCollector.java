package edu.cmu.sv.lifelogger.util;

import java.util.ArrayList;
import java.util.LinkedList;

import android.util.Log;

public class DataCollector<T> {
	
	private static final String TAG = "DataCollector";
	private long collectInterval = 0;
	private long firstDataCollectedTime = 0;
	private long lastDataCollectedTime = 0;
	private int dataSizeLimit = 0;
	
	private LinkedList<T> data = new LinkedList<T>();
	
	public DataCollector(long collectInterval){
		this.setCollectInterval(collectInterval);
	}
	
	public DataCollector(int dataSize){
		this.setDataSizeLimit(dataSize);
	}
	
	public DataCollector(LinkedList<DataCollector<T>> data){
		int dataSize = 0;
		for(DataCollector<T> block:data){
			dataSize += block.getDataSize();
		}
		
		int index = 0;
		int blockCount = data.size();
		
		this.setDataSizeLimit(dataSize);
		for(DataCollector<T> block:data){
			dataSize += block.getDataSize();
			LinkedList<T> rawData = block.getData();
			for(T vector:rawData){
				this.collect(vector);
			}
			
			if(index == 0){
				this.setFirstDataCollectedTime(block.getFirstDataCollectedTime());
			}
			
			if(index == blockCount - 1){
				this.setLastDataCollectedTime(block.getLastDataCollectedTime());
			}
			index++;
		}
	}
	
	public boolean collect(T value){
		long currentTime = System.currentTimeMillis();
		if(this.getFirstDataCollectedTime() == 0){
			this.setFirstDataCollectedTime(currentTime);
		}
		
		if(this.getCollectInterval() > 0){
			if(currentTime - this.getFirstDataCollectedTime() > this.getCollectInterval()){
				return false;
			}
		}
		
		//Log.i(TAG, "interval: " + (currentTime - this.getFirstDataCollectedTime()));
		
		if(this.getDataSizeLimit() > 0){
			if(this.getDataSize() == this.getDataSizeLimit()){
				return false;
			}
		}
		
		this.setLastDataCollectedTime(currentTime);
		return data.add(value);
	}
	
	public DataCollector<T> clone(){
		DataCollector<T> copy = new DataCollector<T>(this.getDataSize());
		copy.setDataSizeLimit(this.getDataSizeLimit());
		copy.data = this.getData();
		copy.setFirstDataCollectedTime(getFirstDataCollectedTime());
		copy.setLastDataCollectedTime(getLastDataCollectedTime());
		copy.setCollectInterval(getCollectInterval());
		return copy;
	}

	private void setFirstDataCollectedTime(long firstDataCollectedTime) {
		this.firstDataCollectedTime = firstDataCollectedTime;
	}

	public long getFirstDataCollectedTime() {
		return firstDataCollectedTime;
	}
	
	private void setLastDataCollectedTime(long value) {
		this.lastDataCollectedTime = value;
	}

	public long getLastDataCollectedTime() {
		return lastDataCollectedTime;
	}

	private void setCollectInterval(long collectInterval) {
		this.collectInterval = collectInterval;
	}

	public long getCollectInterval() {
		return collectInterval;
	}
	
	public LinkedList<T> getData(){
		return new LinkedList<T>(data);
	}
	
	public int getDataSize(){
		return data.size();
	}

	private void setDataSizeLimit(int dataSizeLimit) {
		this.dataSizeLimit = dataSizeLimit;
	}

	public int getDataSizeLimit() {
		return dataSizeLimit;
	}
	
	public static String toStringWhenTIsDoubleArray(DataCollector<double[]> collector){

		LinkedList<double[]> actualData = collector.getData();
		StringBuilder result = new StringBuilder(collector.getDataSize() * 2 * 15 + 10);
		
		result.append("double[]");
		result.append(",").append(String.valueOf(collector.getFirstDataCollectedTime()))
		.append(",").append(String.valueOf(collector.getLastDataCollectedTime()))
		.append(",").append(String.valueOf(collector.getDataSize()));
		
		for(double[] values:actualData){
			result.append(",").append(String.valueOf(values.length));
			for(double value:values){
				result.append(",").append(String.valueOf(value));
			}
		}
		
		return result.toString();
	}
	
	public static DataCollector<double[]> fromStringWhenTIsDoubleArray(String line){
		String[] columns = line.split(",");
		
		long start = Long.valueOf(columns[1]);
		long end = Long.valueOf(columns[2]);
		
		int dataSize = Integer.valueOf(columns[3]).intValue();
		DataCollector<double[]> collector = new DataCollector<double[]>(dataSize);
		
		int lengthIndex = 4;
		if(columns[0].equals("double[]") && columns.length > lengthIndex + 1){
			int readLen = Integer.valueOf(columns[lengthIndex]).intValue();
			while(lengthIndex < columns.length && readLen > 0){
				double[] values = new double[readLen];
				for(int i = lengthIndex + 1; i < lengthIndex + 1 + readLen; i++){
					values[i - lengthIndex - 1] = Double.valueOf(columns[i]).doubleValue();
				}
				collector.data.add(values);
				lengthIndex += readLen + 1;
			}
		}
		
		collector.setFirstDataCollectedTime(start);
		collector.setLastDataCollectedTime(end);
		
		return collector;
	}
}
