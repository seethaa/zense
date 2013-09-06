package edu.cmu.sv.lifelogger.algorithm;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.TreeMap;

import edu.cmu.sv.lifelogger.util.EqualProbabilitySegmentResult;
import edu.cmu.sv.lifelogger.util.SegmentInterval;

public class EqualProbability {

	public static EqualProbabilitySegmentResult GetSegmentation(double[] data, int segmentCount, String symbolPrefix, double minDistance){
        int dataCount = data.length;

        int segmentLeft = segmentCount;
        int dataCountPerSegment = dataCount / segmentCount;
        double startPoint = 0;
        double endPoint = Double.MIN_VALUE;
        ArrayList<SegmentInterval> result = new ArrayList<SegmentInterval>(10);
        TreeMap<Double, ArrayList<Integer>> dataDistribution = new TreeMap<Double, ArrayList<Integer>>();
        

        int currentSegDataCount = 0;
        Integer index = 0;
        for (double value:data) {
            if (!dataDistribution.containsKey(Double.valueOf(value))){
            	ArrayList<Integer> indexList = new ArrayList<Integer>();
            	indexList.add(index);
                dataDistribution.put(Double.valueOf(value), indexList);
            }else{
                dataDistribution.get(Double.valueOf(value)).add(index);
            }
            index++;

        }
        
        Iterator<Entry<Double, ArrayList<Integer>>> i = 
        	dataDistribution.entrySet().iterator();
        int[] dataClusterIndexMap = new int[data.length];

        Arrays.fill(dataClusterIndexMap, -1);
        
        int clusterIndex = 0;
        Entry<Double, ArrayList<Integer>> entry = null;
        ArrayList<Integer> dataIndecies = new ArrayList<Integer>(data.length);
        
        // Display elements
        while(i.hasNext()) {
        	entry = (Entry<Double, ArrayList<Integer>>)i.next(); 
            if (currentSegDataCount == 0)
                startPoint = entry.getKey();

            ArrayList<Integer> currentIndecies = entry.getValue();
            currentSegDataCount += currentIndecies.size();
            endPoint = entry.getKey();
            dataIndecies.addAll(currentIndecies);

            if (currentSegDataCount >= dataCountPerSegment && segmentLeft > 0 && endPoint - startPoint >= minDistance) {
                
            	clusterIndex = result.size();
            	
            	result.add(new SegmentInterval(startPoint, endPoint, currentSegDataCount, symbolPrefix + String.valueOf(segmentCount - segmentLeft)));
                segmentLeft--;
                dataCount -= currentSegDataCount;
                dataCountPerSegment = segmentLeft == 0 ? dataCount : dataCount / segmentLeft;
                currentSegDataCount = 0;
                
                for(Integer dataIndex:dataIndecies){
                	dataClusterIndexMap[dataIndex] = clusterIndex;
                }
                
                dataIndecies.clear();
            }
            
            
        }

        if (currentSegDataCount > 0) {
        	clusterIndex = result.size();
            result.add(new SegmentInterval(startPoint, endPoint, currentSegDataCount, symbolPrefix + String.valueOf(segmentCount - segmentLeft)));

            if(entry != null){
	            dataIndecies.addAll(entry.getValue());
	            for(Integer dataIndex:dataIndecies){
	            	dataClusterIndexMap[dataIndex] = clusterIndex;
	            }
            }
        }
        
        EqualProbabilitySegmentResult returnValue = new EqualProbabilitySegmentResult();
        returnValue.dataClusterIndexMap = dataClusterIndexMap;
        returnValue.intervals = result;
        
        return returnValue;

    }
	
	public static int[] Quantization(double[] data, int start, int end, ArrayList<SegmentInterval> intervals){
		// We can optimize it later.
		
		int[] result = new int[end - start + 1];
		for(int i = start; i <= end; i++){
			int intervalIndex = 0;
			for(SegmentInterval interval:intervals){
				if(interval.isInSegment(data[i])){
					result[i - start] = intervalIndex;
				}
				intervalIndex++;
			}
		}
		
		return result;
	}
}
