package edu.cmu.sv.lifelogger.algorithm;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Random;
import java.util.Vector;

import edu.cmu.sv.lifelogger.util.ClusterResult;
import edu.cmu.sv.lifelogger.util.GetNearestCenterResult;
import edu.cmu.sv.lifelogger.util.GetVectorResult;

import android.util.Log;

public class KMeans
{

    protected KMeans()
    {
      
    }

    public static GetVectorResult GetVectors(Vector<Double> data, int vectorLen)
    {
        GetVectorResult result = new GetVectorResult();
        result.Vectors = new Vector<double[]>();

        int vectorLength = vectorLen;
        result.VectorLength = vectorLength;

        double[] vector = new double[vectorLength];

        int index = 0;
        for (int i = 0; i < data.size(); i++)
        {
            if (index == 0)
            {
                vector = new double[vectorLength];
            }

            vector[index] = data.get(i);

            if (index == vectorLength - 1)
            {

                //vector = AlignVectorElements(vector, 0, vector.Length - 1);

                result.Vectors.add(vector);
                index = 0;

            }
            else
            {

                index++;
            }

        }

        if (index != 0)
        {
            //vector = AlignVectorElements(vector, 0, index);

            result.Vectors.add(vector);
        }

        return result;
    }


    private static double[] AlignVectorElements(double[] vector, int startIndex, int endIndex)
    {
        double minValue = Double.POSITIVE_INFINITY;
        if (vector.length > 1)
        {
            for (int i = startIndex; i < endIndex; i++)
                if (vector[i] < minValue)
                    minValue = vector[i];

            for (int i = startIndex; i < endIndex; i++)
                vector[i] -= minValue;
        }

        return vector;
    }

    private static double[] NormalizeVector(double[] vector)
    {
        //I found that normalizing the vector will cause the 2nd problem - Pang Wu, 2011/3/5
        double maxValue = Double.NEGATIVE_INFINITY;
        double[] result = new double[vector.length];

        if (vector.length > 1)
        {
            for (int i = 0; i < vector.length; i++)
                if (vector[i] > maxValue)
                    maxValue = vector[i];

            for (int i = 0; i < vector.length; i++)
                result[i] = vector[i] / maxValue;
        }

        return result;
    }

    public static void PrintClusterMeanDistance(ClusterResult clusterResult)
    {
        String printString = "Print Cluster Mean Distance Started, " + "C = " + clusterResult.Clusters.size() + "\r\n";
        Vector<Double> sortedDistances = new Vector<Double>(clusterResult.MeanDistances);
        Collections.sort(sortedDistances);
        double sum = 0;

        for (int clusterIndex = 0; clusterIndex < clusterResult.MeanDistances.size(); clusterIndex++)
        {
            printString += clusterIndex + "," + clusterResult.MeanDistances.get(clusterIndex) + "\r\n";
            sum += sortedDistances.get(clusterIndex);
        }


        double tmpSum = 0;
        for (int i = sortedDistances.size() - 1; i >= 0; i--)
        {
            tmpSum += sortedDistances.get(i);
            if (tmpSum / sum > 0.8)
            {
                printString += "Count: " + (sortedDistances.size() - i) + "\r\n";
                break;
            }
        }

        printString += "Print Ended.";

        Log.i("KMeans", printString);
    }

    public static ClusterResult GetClusters(ArrayList<double[]> vectors, int clusterCount, int iterationCount)
    {
        return GetClusters(vectors, clusterCount, iterationCount, null);
    }

    public static ClusterResult GetClusters(ArrayList<double[]> vectors, int clusterCount, int iterationCount, Vector<double[]> initialCenterSet)
    {
        ClusterResult returnValue = new ClusterResult();
        returnValue.Clusters = new ArrayList<int[]>();
        
        if (vectors.size() < clusterCount)
            clusterCount = vectors.size();
        if (clusterCount == 0)
            clusterCount = 1;

        //Initialize k random centers.
        Random ran = new Random();
        ArrayList<double[]> centers = new ArrayList<double[]>(clusterCount);
        double[] centerDistances = null;

        int[] clusters = new int[vectors.size()];
        for (int i = 0; i < clusters.length; i++)
            clusters[i] = -1; //set the initial value of vectorIndex - centerIndex

        //Select random centers.
        HashMap<Integer, Boolean> existingRanNum = new HashMap<Integer, Boolean>();
        for (int center = 0; center < clusterCount; center++)
        {
            ran = new Random();
            Integer k = 0;

            if (initialCenterSet == null)
            {
                while (existingRanNum.containsKey((k = ran.nextInt(Integer.MAX_VALUE) % vectors.size()))) ;
                existingRanNum.put(k, true);

                if (centers.size() == center)
                {
                    double[] vectorClone = new double[vectors.get(k).length];

                    double[] vector = vectors.get(k);
                    for(int i = 0; i<vector.length; i++){
                    	vectorClone[i] = vector[i];
                    }
                    centers.add(vectorClone);
                }
            }
            else
            {
                while (existingRanNum.containsKey((k = ran.nextInt(Integer.MAX_VALUE) % initialCenterSet.size()))) ;
                existingRanNum.put(k, true);

                if (centers.size() == center)
                {
                    double[] vectorClone = new double[initialCenterSet.get(k).length];

                    double[] vector = initialCenterSet.get(k);
                    for(int i = 0; i<vector.length; i++){
                    	vectorClone[i] = vector[i];
                    }

                    centers.add(vectorClone);
                }
            }
        }

        for (int iteration = 0; iteration < iterationCount; iteration++)  //Do iterationCount times k-means.
        {
            for (int i = 0; i < clusters.length; i++)
                clusters[i] = -1; //set the initial value of vectorIndex - centerIndex

            if (iteration == iterationCount - 1)
                centerDistances = new double[centers.size()];

            for (int index = 0; index < vectors.size(); index++) //get the centroid the vector belongs to.
            {
                GetNearestCenterResult getCenterResult = GetNearestCenterIndex(vectors.get(index), centers);
                clusters[index] = getCenterResult.CenterIndex;

                if (iteration == iterationCount - 1)
                    centerDistances[getCenterResult.CenterIndex] += getCenterResult.DistanceToCenter;

            }

            centers = UpdateCentersGeneral(vectors, clusters, centers);
        }

        returnValue.Centroids = centers;

        HashMap<Integer, Integer> clusterCounter = new HashMap<Integer, Integer>();
        for (int i = 0; i < clusters.length; i++)
        {
            if (clusters[i] != -1)
            {
                if (!clusterCounter.containsKey(clusters[i]))
                {
                    clusterCounter.put(clusters[i], 1);
                }
                else
                {
                    clusterCounter.put(clusters[i], clusterCounter.get(clusters[i]) + 1);
                }
            }
        }

        ArrayList<int[]> result = returnValue.Clusters;

        for (int clusterIndex = 0; clusterIndex < clusterCounter.size(); clusterIndex++)
        {
            result.add(new int[clusterCounter.get(clusterIndex)]);
            centerDistances[clusterIndex] /= clusterCounter.get(clusterIndex);
        }

        returnValue.MeanDistances = new ArrayList<Double>();
        for(double value:centerDistances){
        	returnValue.MeanDistances.add(Double.valueOf(value));
        }
        
        returnValue.VectorClusterIndexMap = clusters;

        int[] pos = new int[clusterCounter.size()];

        for (int vectorIndex = 0; vectorIndex < clusters.length; vectorIndex++)
        {
            int clusterIndex = clusters[vectorIndex];

            if (clusterIndex == -1)
                continue;  //We ignore points with infinite distance.

            result.get(clusterIndex)[pos[clusterIndex]] = vectorIndex;
            pos[clusterIndex]++;
        }

        return returnValue;
    }

    private static GetNearestCenterResult GetNearestCenterIndex(double[] vector, ArrayList<double[]> centers)
    {
        double minDistance = Double.MAX_VALUE;  //The smaller, the closer.
        int nearestCenter = 0;

        boolean inifinite = true;

        for (int i = 0; i < centers.size(); i++)
        {
            if (centers.get(i) == null)
                continue;

            double distance = GetDistanceGeneral(vector, centers.get(i));

            if (distance < minDistance)
            {
                nearestCenter = i;
                minDistance = distance;
            }
        }

        GetNearestCenterResult result = new GetNearestCenterResult();
        result.CenterIndex = nearestCenter;
        result.DistanceToCenter = minDistance;

        return result;
    }

    

    private static double GetDistanceGeneral(double[] vector, double[] center)
    {
        double result = 0;
        for (int i = 0; i < vector.length; i++)
        {
            result += Math.pow(vector[i] - center[i], 2);
        }

        return Math.sqrt(result);
    }


    private static ArrayList<double[]> UpdateCentersGeneral(ArrayList<double[]> vectors, int[] clusters, ArrayList<double[]> centers)
    {
        int centerIndex = 0;
        ArrayList<double[]> result = new ArrayList<double[]>();
        for (int i = 0; i < centers.size(); i++)
            result.add(null);

        int[] vectorCountInCenters = new int[centers.size()];

        for (int vectorIndex = 0; vectorIndex < clusters.length; vectorIndex++)
        {
            centerIndex = clusters[vectorIndex];
            if (centerIndex == -1)  //This vector doesn't belongs to any cluster.
                continue;

            if (result.get(centerIndex) == null)
                result.set(centerIndex, new double[vectors.get(clusters[vectorIndex]).length]);

            double[] newCenterVector = result.get(centerIndex);
            //first add the values to new center vector.
            for (int elementIndex = 0; elementIndex < vectors.get(vectorIndex).length; elementIndex++)
            {
                newCenterVector[elementIndex] += vectors.get(vectorIndex)[elementIndex];
            }

            vectorCountInCenters[centerIndex]++;

        }

        for (int i = 0; i < vectorCountInCenters.length; i++)
        {
            int vectorCount = vectorCountInCenters[i];
            double[] center = result.get(i);
            if (vectorCount == 0)
            {
                continue;
            }

            for (int elementIndex = 0; elementIndex < center.length; elementIndex++)
            {
                center[elementIndex] /= (double)vectorCount;  //calculate the mean
            }
        }

        while (result.remove(null)) ;

        return result;
    }
}
