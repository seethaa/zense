package edu.cmu.sv.lifelogger.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;

import edu.cmu.sv.mobisens.io.MobiSensLog;

import android.graphics.Color;
import android.util.Log;

public class NGramModel {
	
	private static final String TAG = "NGramModel";
	private static final String CLASS_PREFIX = NGramModel.class.getName();
	
	public static final String EXTRA_MODEL_COLOR = CLASS_PREFIX + ".extra_model_color";
	public static final String EXTRA_MODEL_ANNO = CLASS_PREFIX + ".extra_model_annotation";
	public static final String EXTRA_MODEL_STRING = CLASS_PREFIX + ".extra_model_string";
	public static final String EXTRA_MODEL_NAME = CLASS_PREFIX + ".extra_model_name";
	
	private HashMap<ArrayList<Integer>, Integer> module = 
		new HashMap<ArrayList<Integer>, Integer>();
	private HashMap<ArrayList<Integer>, Integer> counts = 
		new HashMap<ArrayList<Integer>, Integer>();
	
	private long startTime = 0;
	private long endTime = 0;
	
	private int color = Color.BLACK;
	

	public long getStartTime(){
		return this.startTime;
	}
	
	public long getEndTime(){
		return this.endTime;
	}

	
	private void setStartTime(long value){
		this.startTime = value;
	}
	
	private void setEndTime(long value){
		this.endTime = value;
	}
	
	public static NGramModel fromRawData(LinkedList<DataCollector<double[]>> data, Centroids centroids, int windowSize, int stepSize, int maxN){
		
		DataCollector<double[]> combinedBlock = new DataCollector<double[]>(data);
		
		return fromRawData(combinedBlock, centroids, windowSize, stepSize, maxN);
	}
	
	public static NGramModel fromRawData(DataCollector<double[]> combinedBlock, Centroids centroids, int windowSize, int stepSize, int maxN){
		
		if(combinedBlock.getDataSize() == 0)
			return null;
			
		NGramModel result = new NGramModel();
		HashMap<ArrayList<Integer>, Integer> module = 
			new HashMap<ArrayList<Integer>, Integer>();
		
		HashMap<ArrayList<Integer>, Integer> counts = 
			new HashMap<ArrayList<Integer>, Integer>();
		
		
		int[] seq = centroids.getSequences(combinedBlock, windowSize, stepSize);
		
		for(int n = 1; n <= maxN; n++){
			//HashMap<ArrayList<Integer>, Double> ngramsqQ = new HashMap<ArrayList<Integer>, Double>();
			int ngramCount = seq.length - n + 1;
			for(int i = 0; i < ngramCount; i++){
				ArrayList<Integer> ngram = new ArrayList<Integer>();
				for(int w = 0; w < n; w++){
					ngram.add(seq[i+w]);
				}
				
				if(module.containsKey(ngram)){
					module.put(ngram, module.get(ngram) + 1);
				}else{
					module.put(ngram, 1);
					counts.put(ngram, ngramCount);
				}
			}
			
		}
		
		result.module = module;
		result.counts = counts;
		result.setStartTime(combinedBlock.getFirstDataCollectedTime());
		result.setEndTime(combinedBlock.getLastDataCollectedTime());
		
		return result;
	}
	
	private NGramModel(){
		
	}
	
	public double getDistance(NGramModel ngramModule){
		double length1 = 0;
		for(ArrayList<Integer> ngram:this.module.keySet()){
			length1 += Math.pow((double)this.module.get(ngram) / (double)this.counts.get(ngram), 2);
		}
		
		length1 = Math.sqrt(length1);
		
		double length2 = 0;
		for(ArrayList<Integer> ngram:ngramModule.module.keySet()){
			length2 += Math.pow((double)ngramModule.module.get(ngram) / (double)ngramModule.counts.get(ngram), 2);
		}
		
		length2 = Math.sqrt(length2);
		double denominator = length1 * length2;
		
		if(denominator == 0)
			return 0;
		
		double nominator = 0;
		for(ArrayList<Integer> ngram:this.module.keySet()){
			if(ngramModule.module.containsKey(ngram)){
				nominator += (double)(this.module.get(ngram) * ngramModule.module.get(ngram)) / (double)(this.counts.get(ngram) * ngramModule.counts.get(ngram));
			}
		}
		
		double distance = nominator / denominator;
		return distance;
	}
	
	
	// This merge algorithm will lose some ngram info
	// but its good enough to use.
	public NGramModel merge(NGramModel model){
		HashMap<ArrayList<Integer>, Integer> newModel = 
			new HashMap<ArrayList<Integer>, Integer>(this.module);
		HashMap<ArrayList<Integer>, Integer> newCounts = 
			new HashMap<ArrayList<Integer>, Integer>(this.counts);
		
		Log.i(TAG, "This: " + this.toString());
		Log.i(TAG, "To be merged: " + model.toString());
		
		int totalCount = -1;
		for(ArrayList<Integer> ngram:this.module.keySet()){
			Integer ngramCount = newCounts.get(ngram);

			if(totalCount == -1 && ngram.size() == 1){
				totalCount = ngramCount;
			}
		}
		
		boolean renewTotal = true;
		for(ArrayList<Integer> ngram:model.module.keySet()){
			Integer count = 0;
			if(newModel.containsKey(ngram)){
				count = newModel.get(ngram);
			}
			
			count += model.module.get(ngram);
			
			if(renewTotal && ngram.size() == 1){
				Integer ngramCount = model.counts.get(ngram);
				totalCount += ngramCount;
				renewTotal = false;
			}
			
			newModel.put(ngram, count);
		}
		

		for(ArrayList<Integer> ngram:newModel.keySet()){
			Integer count = totalCount - ngram.size() + 1;
			newCounts.put(ngram, count);
		}
		
		this.module = newModel;
		this.counts = newCounts;

		this.setStartTime(this.getStartTime() < model.getStartTime() ? this.getStartTime() : model.getStartTime());
		this.setEndTime(this.getEndTime() > model.getEndTime() ? this.getEndTime() : model.getEndTime());
		Log.i(TAG, this.toString());
		
		return this;
	}
	
	public static NGramModel fromString(String modelString){
		
		try{
			NGramModel model = new NGramModel();
			String[] numbers = modelString.split(",");
			
			model.setStartTime(Long.valueOf(numbers[0]));
			model.setEndTime(Long.valueOf(numbers[1]));
			
			// Yes yes yes, I know I can use a state machine to solve this problem
			// but writing a bunch of if can make the shit easier to read.
			boolean isNGramLength = true;
			boolean isValue = false;
			boolean isLength = false;
			
			int len = 0;
			int ngramLength = 0;
			int value = 0;
			
			ArrayList<Integer> key = null;
			
			for(int i=2; i<numbers.length; i++){
				if(isNGramLength){
					ngramLength = Integer.valueOf(numbers[i]);
					isNGramLength = false;
					isValue = true;
					continue;
				}
				
				if(isValue){
					value = Integer.valueOf(numbers[i]);
					isValue = false;
					isLength = true;
					key = new ArrayList<Integer>();
					continue;
				}
				
				if(isLength){
					len = Integer.valueOf(numbers[i]);
					isLength = false;
					continue;
				}
				
				key.add(Integer.valueOf(numbers[i]));
				len--;
				
				if(len == 0){
					model.module.put(key, value);
					model.counts.put(key, ngramLength);
					isNGramLength = true;
				}
			}
			
			return model;
		}catch(Exception ex){
			MobiSensLog.log(ex);
			return null;
		}
	}
	
	public String toString(){
		StringBuilder builder = new StringBuilder(1000);
		builder.append(this.getStartTime()).append(",");
		builder.append(this.getEndTime()).append(",");
		
		for(ArrayList<Integer> ngram:this.module.keySet()){
			builder.append(this.counts.get(ngram)).append(",")  // The number of 'N' gram in the text.
			.append(this.module.get(ngram)).append(",") // Occurrence time of current ngram
			.append(ngram.size()).append(","); // N
			for(Integer index:ngram){ // n-gram
				builder.append(index).append(",");
			}
		}
		return builder.toString();
	}

	public void setColor(int color) {
		this.color = color;
	}

	public int getColor() {
		return color;
	}
	
}
