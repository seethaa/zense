package edu.cmu.sv.lifelogger.algorithm;

import java.util.ArrayList;

import edu.cmu.sv.lifelogger.util.ClusterResult;

public class SinglePass {
	public static ClusterResult getCluster(ArrayList<double[]> data, double threshold){
		int dataSize = data.size();
		ArrayList<double[]> centroids = new ArrayList<double[]>();
		ArrayList<ArrayList<Integer>> cluster = new ArrayList<ArrayList<Integer>>();

		for(int i = 0; i < dataSize; i++){
			double[] vector = data.get(i);
			int centroidIndex = getNearestCentroid(vector, centroids, threshold);
			centroidIndex = updateCentroid(vector, centroids, centroidIndex);
			if(cluster.size() < centroidIndex + 1){
				cluster.add(new ArrayList<Integer>());
			}
			cluster.get(centroidIndex).add(i);
			
		}
		
		ClusterResult result = new ClusterResult();
		ArrayList<int[]> clusters = new ArrayList<int[]>();
		int[] dataClusterIndexMap = new int[data.size()];
		
		for(ArrayList<Integer> list:cluster){
			int[] item = new int[list.size()];
			for(int i = 0; i<list.size(); i++){
				item[i] = list.get(i);
				dataClusterIndexMap[item[i]] = clusters.size();
			}
			clusters.add(item);
		}
		
		result.Centroids = centroids;
		result.Clusters = clusters;
		result.VectorClusterIndexMap = dataClusterIndexMap;
		
		
		return result;
	}
	
	private static int updateCentroid(double[] data, ArrayList<double[]> centroids, int centroidIndex){
		
		int returnValue = centroids.size();
		
		if(centroidIndex == -1){
			double[] copy = new double[data.length + 1];
			int index = 0;
			for(double value:data){
				copy[index] = value;
				index++;
			}
			copy[copy.length - 1] = 1; // Number of data in centroid.
			
			centroids.add(copy);
		}else{
			double[] centroid = centroids.get(centroidIndex);
			int length = centroid.length;
			
			for(int i =0;i<length - 2; i++){
				centroid[i] = (centroid[i] * centroid[length - 1] + data[i]) / (centroid[length - 1] + 1.0);
			}
			centroid[length - 1] += 1;
			centroids.set(centroidIndex, centroid);
			returnValue = centroidIndex;
		}
		
		return returnValue;
	}
	
	private static int getNearestCentroid(double[] vector, ArrayList<double[]> centroids, double threshold){
		double minDistance = Double.MAX_VALUE;
		int minIndex = -1;
		for(int centroidIndex = 0; centroidIndex < centroids.size(); centroidIndex++){
			double distance = getDistance(vector, centroids.get(centroidIndex));
			if(distance < threshold){
				if(distance < minDistance){
					minIndex = centroidIndex;
					minDistance = distance;
				}
			}
		}
		
		return minIndex;
	}
	
	private static double getDistance(double[] a, double[] b){
		double result = 0;
		
		if(a.length > 1){
	        for (int i = 0; i < a.length; i++)
	        {
	            result += Math.pow(a[i] - b[i], 2);
	        }
		}else{
			result = Math.abs(a[0] - b[0]);
		}

        return result;  // To save the computation, we don't square it.
	}
}
