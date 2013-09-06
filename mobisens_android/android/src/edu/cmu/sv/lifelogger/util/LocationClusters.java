package edu.cmu.sv.lifelogger.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.LinkedList;


public class LocationClusters {
	private static final String TAG = "LocationClusters";
	
	public static final double DEFAULT_EPSILON = 100.0;
	public static final int DEFUALT_MINPOINTS = 2;
	
	private double epsilon = DEFAULT_EPSILON;
	private int minPoints = DEFUALT_MINPOINTS;
	private LinkedList<LocationCentroid> centroids = 
		new LinkedList<LocationCentroid>();
	
	private LinkedList<PendingLocationCentroid> pendingCentroids = 
		new LinkedList<PendingLocationCentroid>();
	
	
	
	/*
	 * Set the epsilon parameters in DBSCAN 
	 * (the minimum distance that two cluster's boundary can have)
	 */
	public void setEpsilon(double epsilon) {
		this.epsilon = epsilon;
	}
	
	public double getEpsilon() {
		return epsilon;
	}
	
	/*
	 * Set the minPoints parameter for DBSCAN.
	 * (The minimum number of data that a cluster should have)
	 */
	public void setMinPoints(int minPoints) {
		this.minPoints = minPoints;
	}
	
	public int getMinPoints() {
		return minPoints;
	}
	
	public static LocationClusters loadFromFile(File indexFile){
		LocationClusters clusters = new LocationClusters();
		
		if(!indexFile.exists())
			return clusters;
		
        StringBuffer fileData = new StringBuffer(1000);
        try{
        	BufferedReader reader = new BufferedReader(new FileReader(indexFile));
            char[] buf = new char[1024];
            int numRead=0;
            while((numRead=reader.read(buf)) != -1){
                String readData = String.valueOf(buf, 0, numRead);
                fileData.append(readData);
                buf = new char[1024];
            }
            reader.close();
        }catch(Exception ex){
        	ex.printStackTrace();
        }

		String indexContent = fileData.toString();
		String[] lines = indexContent.split("\r\n");
		clusters = new LocationClusters(lines, DEFAULT_EPSILON, DEFUALT_MINPOINTS); // use the default settings.
		return clusters;
	}
	
	
	
	public void save(File indexFile){
		try {
			FileWriter fileWritter = new FileWriter(indexFile, true);
	        BufferedWriter bufferWritter = new BufferedWriter(fileWritter, 100 * 1024);
		     
			int index = 0;
			for(LocationCentroid centroid:this.centroids){
				if(!centroid.isLoadFromFile()){
					centroid.setIndex(index);
					bufferWritter.write(centroid.toString() + "\r\n");
					centroid.setLoadFromFile(true);
				}
				index++;
			}
			
			bufferWritter.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			//MobiSensLog.log(e);
		}
	}
	
	public LocationClusters(){
		this.setMinPoints(LocationClusters.DEFUALT_MINPOINTS);
		this.setEpsilon(LocationClusters.DEFAULT_EPSILON);
	}
	
	
	public LocationClusters(String[] linesFromFile, double epsilon, int minPoints){
		for(String line:linesFromFile){
			this.centroids.add(new LocationCentroid(line));
		}
		
		if(epsilon > 0.0)
			this.setEpsilon(epsilon);
		
		if(minPoints >= LocationClusters.DEFUALT_MINPOINTS)
			this.setMinPoints(minPoints);
	}
	
	
	/*
	 * Add a location into the model, and return its cluster index(label)
	 */
	public int add(Location location){
		if(location.getSpeed() > 0)
			return -1;  // The location has speed, return as noise.
		
		synchronized(this.centroids){
			Location compareLocation = new Location();
			double minDistance = Double.MAX_VALUE;
			int minIndex = -1;
			int index = 0;
			
			for(LocationCentroid centroid:this.centroids){
				compareLocation.setLatitude(centroid.getLatitude());
				compareLocation.setLongitude(centroid.getLongitude());
				double distance = location.distanceTo(compareLocation);
				
				if(distance < this.getEpsilon()){
					if(distance < minDistance){
						minDistance = distance;
						minIndex = index;
					}
				}
				
				index++;
			}
			
			index = 0;
			if(minIndex == -1){  
				// No nearest neighbor found in the cenroid list, 
				// look into pending centroid list.
				for(PendingLocationCentroid centroid:this.pendingCentroids){
					compareLocation.setLatitude(centroid.getLatitude());
					compareLocation.setLongitude(centroid.getLongitude());
					double distance = location.distanceTo(compareLocation);
					
					if(distance < this.getEpsilon()){
						if(distance < minDistance){
							minDistance = distance;
							minIndex = index;
							
						}
					}
					
					index++;
				}
				
				if(minIndex != -1){
					PendingLocationCentroid pendingCentroid = this.pendingCentroids.get(minIndex);
					pendingCentroid.setMemberCount(pendingCentroid.getMemberCount() + 1);
					if(pendingCentroid.getMemberCount() >= this.getMinPoints()){
						// The pending centroid has become a true location centroid.
						this.centroids.add(pendingCentroid);
						
						// Once we get a real centroid from pending centroids,
						// that means other pending centroids are no more needed
						// so just clear them.
						this.pendingCentroids.clear();
						
						return this.centroids.size() - 1;
					}else{
						// if the pending centroid has not enough members, return current location as noise.
						return -1;
					}
				}else{
					// No nearest neighbor found in the pending centroid list.
					// Add the current location as a pending centroid.
					PendingLocationCentroid pendingCentroid = 
						new PendingLocationCentroid(location.getLatitude(), location.getLongitude());
					this.pendingCentroids.add(pendingCentroid);
					
					return -1;  // Still return the current location as noise.
				}
			}
			
			
			// We match a centroid from the centroid list.
			// return the centroid index.
			return minIndex;
		}
	}
	
}
