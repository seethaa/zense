package edu.cmu.sv.lifelogger.util;

import java.io.File;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.LinkedList;

import edu.cmu.sv.lifelogger.algorithm.Numerical;
import edu.cmu.sv.mobisens.io.Directory;
import edu.cmu.sv.mobisens.io.FileOperation;
import edu.cmu.sv.mobisens.io.MobiSensLog;

public class Centroids {
	
	public static final String ASSET_CENTROIDS_JOY = "Centroids_FlyingJoy";
	public static final String ASSET_MAGNITUDE_CENTROIDS_30 = "centroids_test_30.csv";
	
	private LinkedList<double[]> centroids = null;
	
	
	public boolean add(double[] newCentroid){
		if(this.centroids == null){
			this.centroids = new LinkedList<double[]>();
		}
		
		for(double[] centroid: this.centroids){
			boolean allEqual = true;
			for(int i=0;i<centroid.length;i++){
				if(centroid[i] != newCentroid[i]){
					allEqual |= false;
				}
			}
			
			if(allEqual){
				return false;
			}
		}
		
		double[] clone = new double[newCentroid.length];
		for(int i = 0; i<clone.length; i++){
			clone[i] = newCentroid[i];
		}
		
		this.centroids.add(clone);
		return true;
	}
	
	/*
	 * Centroids string format:
	 * Length,value[1],value[2],value[3],...,value[Length-1],Length2,...
	 */
	public static Centroids fromString(String csvInput){
		Centroids centroids = new Centroids();
		centroids.centroids = new LinkedList<double[]>();
		
		int index = 0;
		String[] numbers = csvInput.split(",");
		int length = 0;
		
		while(index < numbers.length){
			length = Integer.valueOf(numbers[index]);
			
			if(length > 0){
				double[] vector = new double[length];
				for(int i=1; i<=length; i++){
					vector[i-1] = Double.valueOf(numbers[index+i]);
				}
				centroids.centroids.add(vector);
			}
			index += length + 1;
			
		}
		
		return centroids;
	}
	
	public int assignCentroid(double[] vector){
		double minDistance = Double.MAX_VALUE;
		int selectedIndex = -1;
		
		for(int i=0;i<vector.length;i++)
		{
			double currentDistance = this.getDistance(this.centroids.get(i), vector);
			if(currentDistance < minDistance){
				minDistance = currentDistance;
				selectedIndex = i;
			}
		}
		
		return selectedIndex;
	}
	
	public int[] getSequences(DataCollector<double[]> dataList, int windowSize, int step){
		int dataLen = dataList.getDataSize();
		if(dataLen - windowSize + 1 <= 0)
			return new int[0];
		int sequenceLen = (dataLen - windowSize + 1) / step;
		
		int[] sequence = new int[sequenceLen];
		LinkedList<double[]> rawData = dataList.getData();
		
		double[] x = new double[windowSize];
		double[] y = new double[windowSize];
		double[] z = new double[windowSize];
		
		int index = 0;
		ArrayList<double[]> data = new ArrayList<double[]>(rawData);
		int i = 0;
		
		while(index < sequenceLen){
			
			for(int w=0;w < windowSize; w++){
				try{
					double[] vector = data.get(i+w);
					x[w] = vector[0];
					y[w] = vector[1];
					z[w] = vector[2];
				}catch(Exception ex){
					int t = 0;
				}
				
			}
			
			
			double[] result = getSixDimFeature(x, y, z); // getMagnitudeFeature(x, y, z); //getSixDimFeature(x, y, z);
			sequence[index] = this.assignCentroid(result);
			index++;
			i += step;
		}
		
		return sequence;
	}
	
	public static double[] getSixDimFeature(double[] x, double[] y, double[] z){
		double[] result = new double[6];
		
		result[0] = Numerical.getAverage(x);
		result[1] = Numerical.getAverage(y);
		result[2] = Numerical.getAverage(z);
		
		result[3] = Numerical.getStandardDeviation(x);
		result[4] = Numerical.getStandardDeviation(y);
		result[5] = Numerical.getStandardDeviation(z);
		
		return result;
	}
	
	public static double[] getMagnitudeFeature(double[] x, double[] y, double[] z){
		double[] result = new double[2];
		
		if(x.length != y.length || x.length != z.length || y.length != z.length)
			return result;
		
		double[] magnitudes = new double[x.length];
		for(int i = 0; i < magnitudes.length; i++){
			magnitudes[i] = Math.sqrt(x[i] * x[i] + y[i] * y[i] + z[i] * z[i]);
		}

		result[0] = Numerical.getAverage(magnitudes);
		result[1] = Numerical.getStandardDeviation(magnitudes);

		return result;
	}
	
	private double getDistance(double[] vector1, double[] vector2){
		double distance = 0;
		for(int i=0;i<vector1.length;i++){
			distance += Math.pow(vector1[i] - vector2[i], 2);
		}
		
		return distance;
	}
	
	
	
	
	
}
