package edu.cmu.sv.lifelogger.algorithm;

import java.util.HashSet;
import java.util.LinkedList;

import android.util.Log;

import edu.cmu.sv.lifelogger.util.Location;

public class Path {
	public static final int SAME_LOCATION_THRESHOLD = 100;
	
	private static final String CLASS_PREFIX = Path.class.getName();
	private static final String TAG = CLASS_PREFIX;
	
	public static double compare(LinkedList<double[]> basePath, LinkedList<double[]> pathToCompare, double maxDistance){
		if(pathToCompare == null || basePath == null)
			return 0D;
		
		if(pathToCompare.size() == 0 || basePath.size() == 0)
			return 0D;
		
		double[][] basePathArray = new double[basePath.size()][];
		for(int i = 0; i < basePath.size(); i++){
			double[] latlng = basePath.get(i);
			basePathArray[i] = latlng;
		}
		
		double[][] pathToCompareArray = new double[pathToCompare.size()][];
		for(int i = 0; i < pathToCompare.size(); i++){
			double[] latlng = pathToCompare.get(i);
			pathToCompareArray[i] = latlng;
		}
		
		HashSet<Location> overlapSet = new HashSet<Location>();
		for(int i = 0; i < basePath.size(); i++){
			Location basePoint = new Location(basePathArray[i][0], basePathArray[i][1], 0D, 0F);
			for(int k = 0; k < pathToCompare.size(); k++){
				Location pointToCompare = new Location(pathToCompareArray[k][0], pathToCompareArray[k][1], 0D, 0F);
				double distance = basePoint.distanceTo(pointToCompare);
				Log.i(TAG, String.valueOf(distance));
				if(distance <= maxDistance){
					overlapSet.add(pointToCompare);
				}
			}
		}
		
		for(int i = 0; i < pathToCompare.size(); i++){
			Location basePoint = new Location(pathToCompareArray[i][0], pathToCompareArray[i][1], 0D, 0F);
			for(int k = 0; k < basePath.size(); k++){
				Location pointToCompare = new Location(basePathArray[k][0], basePathArray[k][1], 0D, 0F);
				double distance = basePoint.distanceTo(pointToCompare);
				Log.i(TAG, String.valueOf(distance));
				if(distance <= maxDistance){
					overlapSet.add(pointToCompare);
				}
			}
		}
		
		return (double)overlapSet.size() / (double)(pathToCompare.size() + basePath.size());
	}
}
