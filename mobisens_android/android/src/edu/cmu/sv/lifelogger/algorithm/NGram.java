package edu.cmu.sv.lifelogger.algorithm;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import android.util.Log;

import edu.cmu.sv.lifelogger.util.ClusterResult;
import edu.cmu.sv.lifelogger.util.DataCollector;
import edu.cmu.sv.lifelogger.util.SegmentInterval;
import edu.cmu.sv.mobisens.io.MobiSensLog;
import edu.cmu.sv.mobisens.settings.ServiceParameters;

public class NGram {
	private final static String TAG = "NGram";
	
	public static double getNGramSimilarity(ArrayList<Integer> p, ArrayList<Integer> q, int N){
		
		HashMap<ArrayList<Integer>, Integer> ngramspP = new HashMap<ArrayList<Integer>, Integer>();
		int sentenceLength = Math.min(p.size(), q.size());
		
		for(int i = 0; i < sentenceLength - N + 1;i++){
			ArrayList<Integer> ngram = new ArrayList<Integer>();
			for(int w = 0; w<N;w++){
				ngram.add(p.get(i+w));
			}
			
			if(ngramspP.containsKey(ngram)){
				ngramspP.put(ngram, ngramspP.get(ngram) + 1);
			}else{
				ngramspP.put(ngram, 1);
			}
		}
		
		HashMap<ArrayList<Integer>, Integer> ngramsqQ = new HashMap<ArrayList<Integer>, Integer>();
		for(int i = 0; i < sentenceLength - N + 1;i++){
			ArrayList<Integer> ngram = new ArrayList<Integer>();
			for(int w = 0; w<N;w++){
				ngram.add(q.get(i+w));
			}
			
			if(ngramsqQ.containsKey(ngram)){
				ngramsqQ.put(ngram, ngramsqQ.get(ngram) + 1);
			}else{
				ngramsqQ.put(ngram, 1);
			}
		}
		
		int sumpPQ = 0;
		int sumpP = 0;
		for(ArrayList<Integer> key:ngramspP.keySet()){
			if(ngramsqQ.containsKey(key)){
				sumpPQ += Math.min(ngramspP.get(key), ngramsqQ.get(key));
			}
			sumpP += ngramspP.get(key);
		}
		
		return (double)sumpPQ / (double)sumpP;
	}
	
	public static boolean isSimiarActivityByNBins(DataCollector<double[]> annoCollector, 
			DataCollector<double[]> dataCollector, 
			double threshold,
			int maxN,
			long dataType){
		
		return NGram.getActivitySimilarityByNBins(annoCollector, dataCollector, threshold, maxN, dataType) >= threshold;
		
	}
	
	public static boolean isSimiarActivityByKMeans(DataCollector<double[]> annoCollector, 
			DataCollector<double[]> dataCollector, 
			double threshold,
			int maxN,
			long dataType){
		
		return NGram.getActivitySimilarityByKMeans(annoCollector, dataCollector, threshold, maxN, dataType) >= threshold;
		
	}
	
	public static boolean isSimiarActivitySinglePass(DataCollector<double[]> annoCollector, 
			DataCollector<double[]> dataCollector, 
			double clusterThreshold,
			double threshold,
			int maxN,
			long dataType){
		
		return NGram.getActivitySimilarityBySinglePass(annoCollector, dataCollector, threshold, clusterThreshold, maxN, dataType) >= threshold;
		
	}
	
	public static double getActivitySimilarityByNBinsFast(DataCollector<double[]> annoCollector, 
			DataCollector<double[]> dataCollector, 
			double threshold,
			int maxN,
			long dataType){
		double ngramPrec = -1;
		int[] dataIndexRange = new int[]{0,0};
		if(dataType == ServiceParameters.ACCELEROMETER){
			dataIndexRange = new int[]{0,2};
		}

		for(int valIndex = dataIndexRange[0]; valIndex <= dataIndexRange[1]; valIndex++){
			
			// If the two size don't match.
			int halfDataSize = Math.min(annoCollector.getDataSize(), dataCollector.getDataSize());
			double[] data = new double[halfDataSize * 2];
			
			int index = 0;
			LinkedList<double[]> dataSeq = annoCollector.getData();
			
			for(int i = 0; i < halfDataSize; i++){
				double[] value = dataSeq.get(i);
				data[index] = value[valIndex];
				index++;
			}
			
			dataSeq = dataCollector.getData();
			for(int i = 0; i < halfDataSize; i++){
				double[] value = dataSeq.get(i);
				data[index] = value[valIndex];
				index++;
			}
			
			int binNumber = (int) (Numerical.getStandardDeviation(data) + 1);
			Log.i(TAG, "BinNumber: " + binNumber + ", valIndex = " + valIndex);
			Log.i(TAG, "preparing quantization..");
			int[] sequences = EqualProbability.GetSegmentation(data, binNumber, "A", -1).dataClusterIndexMap;
			
			
			Log.i(TAG, "quantization done.");
			
			ArrayList<Integer> p = new ArrayList<Integer>();
			ArrayList<Integer> q = new ArrayList<Integer>();
			
			//String annoSeqString = "";
			//String dataSeqString = "";
			
			for(int i=0; i < sequences.length; i++){
				int seq = sequences[i];
				
				if(i < halfDataSize){
					//annoSeqString += String.valueOf(seq);
					p.add(seq);
				}else{
					//dataSeqString += String.valueOf(seq);
					q.add(seq);
				}
			}
			
			
			//Log.i(TAG, annoSeqString);
			//Log.i(TAG, dataSeqString);
			
			//MobiSensLog.log("val: " + valIndex + ", anno: " + annoSeqString);
			//MobiSensLog.log("val: " + valIndex + ", data: " +  dataSeqString);
			
			double ngramValPrec = 0;
			
			for(int i=1;i<=maxN;i++){
				ngramValPrec += NGram.getNGramSimilarity(p, q, i);
			}
			
			if(ngramPrec == -1)
				ngramPrec = ngramValPrec;
			else
				ngramPrec += ngramValPrec;
		}
		
		ngramPrec = ngramPrec / 3.0 / (double)maxN;
		
		//Log.i(TAG, (new Date()).toString() + ": NBins-Fast n-gram similarity: " + ngramPrec + " N: " + maxN);
		//MobiSensLog.log((new Date()).toString() + ": NBins-Fast n-gram similarity: " + ngramPrec + " N: " + maxN);
		return ngramPrec;
	}
	
	public static double getActivitySimilarityByNBins(DataCollector<double[]> annoCollector, 
			DataCollector<double[]> dataCollector, 
			double threshold,
			int maxN,
			long dataType){
		double ngramPrec = -1;
		int[] dataIndexRange = new int[]{0,0};
		if(dataType == ServiceParameters.ACCELEROMETER){
			dataIndexRange = new int[]{0,2};
		}

		for(int valIndex = dataIndexRange[0]; valIndex <= dataIndexRange[1]; valIndex++){
			
			// If the two size don't match.
			int halfDataSize = Math.min(annoCollector.getDataSize(), dataCollector.getDataSize());
			double[] data = new double[halfDataSize * 2];
			
			int index = 0;
			LinkedList<double[]> dataSeq = annoCollector.getData();
			
			for(int i = 0; i < halfDataSize; i++){
				double[] value = dataSeq.get(i);
				data[index] = value[valIndex];
				index++;
			}
			
			dataSeq = dataCollector.getData();
			for(int i = 0; i < halfDataSize; i++){
				double[] value = dataSeq.get(i);
				data[index] = value[valIndex];
				index++;
			}
			
			int binNumber = (int) (Numerical.getStandardDeviation(data) + 1);
			Log.i(TAG, "BinNumber: " + binNumber + ", valIndex = " + valIndex);
			Log.i(TAG, "preparing quantization..");
			ArrayList<SegmentInterval> intervals = EqualProbability.GetSegmentation(data, binNumber, "A", -1).intervals;
			
			
			int[] annoSequence = EqualProbability.Quantization(data, 0, halfDataSize - 1, intervals);
			int[] dataSequence = EqualProbability.Quantization(data, halfDataSize, halfDataSize * 2 - 1, intervals);
			Log.i(TAG, "quantization done.");
			
			ArrayList<Integer> p = new ArrayList<Integer>();
			ArrayList<Integer> q = new ArrayList<Integer>();
			
			//String annoSeqString = "";
			for(int seq:annoSequence){
				//annoSeqString += String.valueOf(seq);
				p.add(seq);
			}
			
			//String dataSeqString = "";
			for(int seq:dataSequence){
				//dataSeqString += String.valueOf(seq);
				q.add(seq);
			}
			
			//Log.i(TAG, annoSeqString);
			//Log.i(TAG, dataSeqString);
			
			//MobiSensLog.log("val: " + valIndex + ", anno: " + annoSeqString);
			//MobiSensLog.log("val: " + valIndex + ", data: " +  dataSeqString);
			
			double ngramValPrec = 0;
			
			for(int i=1;i<=maxN;i++){
				ngramValPrec += NGram.getNGramSimilarity(p, q, i);
			}
			
			if(ngramPrec == -1)
				ngramPrec = ngramValPrec;
			else
				ngramPrec += ngramValPrec;
		}
		
		ngramPrec = ngramPrec / 3.0 / (double)maxN;
		
		Log.i(TAG, (new Date()).toString() + ": NBins n-gram similarity: " + ngramPrec + " N: " + maxN);
		//MobiSensLog.log((new Date()).toString() + ": NBins n-gram similarity: " + ngramPrec + " N: " + maxN);
		return ngramPrec;
	}
	
	public static double getActivitySimilarityByCombinedNBins(DataCollector<double[]> annoCollector, 
			DataCollector<double[]> dataCollector, 
			double threshold,
			int maxN,
			long dataType){
		double ngramPrec = -1;
		int[] dataIndexRange = new int[]{0,0};
		if(dataType == ServiceParameters.ACCELEROMETER){
			dataIndexRange = new int[]{0,2};
		}

		ArrayList<int[]> sequences = new ArrayList<int[]>();
		
		// If the two size don't match.
		int halfDataSize = Math.min(annoCollector.getDataSize(), dataCollector.getDataSize());
		
		for(int valIndex = dataIndexRange[0]; valIndex <= dataIndexRange[1]; valIndex++){
			
			
			double[] data = new double[halfDataSize * 2];
			
			int index = 0;
			LinkedList<double[]> dataSeq = annoCollector.getData();
			
			for(int i = 0; i < halfDataSize; i++){
				double[] value = dataSeq.get(i);
				data[index] = value[valIndex];
				index++;
			}
			
			dataSeq = dataCollector.getData();
			for(int i = 0; i < halfDataSize; i++){
				double[] value = dataSeq.get(i);
				data[index] = value[valIndex];
				index++;
			}
			
			int binNumber = (int) (Numerical.getStandardDeviation(data) + 1);
			Log.i(TAG, "BinNumber: " + binNumber + ", valIndex = " + valIndex);
			Log.i(TAG, "preparing quantization..");
			ArrayList<SegmentInterval> intervals = EqualProbability.GetSegmentation(data, binNumber, "A", -1).intervals;
			
			
			int[] sequence = EqualProbability.Quantization(data, 0, halfDataSize * 2 - 1, intervals);
			
			Log.i(TAG, "quantization done.");
			
			sequences.add(sequence);
		}
		
		ArrayList<Integer> p = new ArrayList<Integer>();
		ArrayList<Integer> q = new ArrayList<Integer>();
		HashMap<ArrayList<Integer>, Integer> combinedData = new HashMap<ArrayList<Integer>, Integer>();
		
		int dataSize = halfDataSize * 2;
		String annoSeqString = "";
		String dataSeqString = "";
		
		for(int i = 0; i < dataSize; i++){
			
			ArrayList<Integer> vector = new ArrayList<Integer>();

			for(int d =0 ; d<sequences.size(); d++){
				vector.add(sequences.get(d)[i]);
			}
			
			if(!combinedData.containsKey(vector)){
				combinedData.put(vector, combinedData.size());
			}
			
			Integer index = combinedData.get(vector);
			if(i < halfDataSize){
				p.add(index);
				annoSeqString += String.valueOf(index) + ",";
			}else{
				q.add(index);
				dataSeqString += String.valueOf(index) + ",";
			}
			
		}
		
		
		Log.i(TAG, annoSeqString);
		Log.i(TAG, dataSeqString);
		
		MobiSensLog.log("CombinedNBins anno: " + annoSeqString);
		MobiSensLog.log("CombinedNBins data: " +  dataSeqString);
		
		ngramPrec = NGram.getNGramPrecision(p, q, maxN, threshold);
		
		Log.i(TAG, (new Date()).toString() + ": CombinedNBins n-gram similarity: " + ngramPrec + " N: " + maxN);
		//MobiSensLog.log((new Date()).toString() + ": CombinedNBins n-gram similarity: " + ngramPrec + " N: " + maxN);
		return ngramPrec;
	}
	
	public static double getActivitySimilarityByKMeans(DataCollector<double[]> annoCollector, 
			DataCollector<double[]> dataCollector, 
			double threshold,
			int maxN,
			long dataType){
		ArrayList<double[]> data = new ArrayList<double[]>();
		
		int windowSize = 20;
		double[] windowX = new double[windowSize];
		double[] windowY = new double[windowSize];
		double[] windowZ = new double[windowSize];
		
		int halfDataSize = Math.min(annoCollector.getDataSize(), dataCollector.getDataSize());
		LinkedList<double[]> dataSeq = annoCollector.getData();

		
		for(int i = 0; i < halfDataSize - windowSize; i++){
			//data.add(new double[]{value});
			for(int w = 0; w < windowSize; w++){
				windowX[w] = dataSeq.get(w+i)[0];
				windowY[w] = dataSeq.get(w+i)[1];
				windowZ[w] = dataSeq.get(w+i)[2];
			}
			
			data.add(new double[]{
					Numerical.getAverage(windowX),
					Numerical.getAverage(windowY),
					Numerical.getAverage(windowZ),
					Numerical.getStandardDeviation(windowX),
					Numerical.getStandardDeviation(windowY),
					Numerical.getStandardDeviation(windowZ)
					});

		}
		
		dataSeq = dataCollector.getData();
		
		
		int splitterIndex = data.size();
		
		for(int i = 0; i < halfDataSize - windowSize; i++){
			//data.add(new double[]{value});
			for(int w = 0; w < windowSize; w++){
				windowX[w] = dataSeq.get(w+i)[0];
				windowY[w] = dataSeq.get(w+i)[1];
				windowZ[w] = dataSeq.get(w+i)[2];
			}
			
			data.add(new double[]{
					Numerical.getAverage(windowX),
					Numerical.getAverage(windowY),
					Numerical.getAverage(windowZ),
					Numerical.getStandardDeviation(windowX),
					Numerical.getStandardDeviation(windowY),
					Numerical.getStandardDeviation(windowZ)
					});
		}
		
		double[] x = new double[data.size()];
		double[] y = new double[data.size()];
		double[] z = new double[data.size()];

		for(int i=0;i<data.size();i++){
			x[i] = data.get(i)[0];
			y[i] = data.get(i)[1];
			z[i] = data.get(i)[2];
		}
		
		int k = (int)(Numerical.getStandardDeviation(x) + 1) *
				(int)(Numerical.getStandardDeviation(y) + 1) *
				(int)(Numerical.getStandardDeviation(z) + 1);
		if(k>5)
			k = 5;
		
		//Log.i(TAG, "k: " + k);
		//MobiSensLog.log("k = " + k);
		
		//String annoSeqString = "";
        //String dataSeqString = "";
        
        Log.i(TAG, "processing kmeans..");
		ClusterResult result = KMeans.GetClusters(data, k, 5);
		
		
		Log.i(TAG, "kmeans done.");
		ArrayList<Integer> p = new ArrayList<Integer>();
		ArrayList<Integer> q = new ArrayList<Integer>();
		
		for(int i = 0; i < data.size(); i++){
			if(i < splitterIndex){
				//annoSeqString += result.VectorClusterIndexMap[i] + ",";
				p.add(result.VectorClusterIndexMap[i]);
			}else{
				//dataSeqString += result.VectorClusterIndexMap[i] + ",";
				q.add(result.VectorClusterIndexMap[i]);
			}
		}
		
		//Log.i(TAG, "anno: " + annoSeqString);
		//Log.i(TAG, "data: " + dataSeqString);
		
		//MobiSensLog.log("anno: " + annoSeqString);
		//MobiSensLog.log("anno: " + annoSeqString);
		

		double ngramPrec = getNGramPrecision(p, q, maxN, threshold);
		
		Log.i(TAG, (new Date()).toString() + ", KMeans n-gram similarity: " + ngramPrec + ", N: " + maxN);
		//MobiSensLog.log((new Date()).toString() + ", KMeans n-gram similarity: " + ngramPrec + ", N: " + maxN);
		
		return ngramPrec;
	}
	
	public static double getNGramPrecision(ArrayList<Integer> p, ArrayList<Integer> q, int maxN, double threshold){
		double ngramPrec = 0;
		
		for(int i=1; i <= maxN; i++){
			ngramPrec += NGram.getNGramSimilarity(p, q, i);
			
			if(ngramPrec / (double)i < threshold){
				Log.i(TAG, "n-gram skipped.");
				return ngramPrec / (double)i;
			}
		}
		
		return ngramPrec / (double)maxN;
	}
	
	public static double getActivitySimilarityBySinglePass(DataCollector<double[]> annoCollector, 
			DataCollector<double[]> dataCollector, 
			double clusterThreshold,
			double threshold,
			int maxN,
			long dataType){
		ArrayList<double[]> data = new ArrayList<double[]>();
		

		int halfDataSize = Math.min(annoCollector.getDataSize(), dataCollector.getDataSize());
		double ngramPrec = 0;

		for(int valIndex = 0; valIndex < 3; valIndex++){
			LinkedList<double[]> dataSeq = annoCollector.getData();
			
			for(int i = 0; i < halfDataSize; i++){
				//data.add(new double[]{value});
				double[] vector = dataSeq.get(i);
				data.add(new double[]{ vector[valIndex] });
	
			}
			
			dataSeq = dataCollector.getData();
			
			
			int splitterIndex = data.size();
			
			for(int i = 0; i < halfDataSize; i++){
				//data.add(new double[]{value});
				double[] vector = dataSeq.get(i);
				data.add(new double[]{ vector[valIndex] });
	
			}
			
			//String annoSeqString = "";
	        //String dataSeqString = "";
	        
	        Log.i(TAG, "processing singlepass..");
			ClusterResult result = SinglePass.getCluster(data, clusterThreshold);
			
			
			Log.i(TAG, "singlepass done.");
			ArrayList<Integer> p = new ArrayList<Integer>();
			ArrayList<Integer> q = new ArrayList<Integer>();
			
			for(int i = 0; i < data.size(); i++){
				if(i < splitterIndex){
					//annoSeqString += result.VectorClusterIndexMap[i] + ",";
					p.add(result.VectorClusterIndexMap[i]);
				}else{
					//dataSeqString += result.VectorClusterIndexMap[i] + ",";
					q.add(result.VectorClusterIndexMap[i]);
				}
			}
			
			//Log.i(TAG, "SinglePass anno: " + annoSeqString);
			//Log.i(TAG, "SinglePass data: " + dataSeqString);
			
			//MobiSensLog.log("SinglePass anno: " + annoSeqString);
			//MobiSensLog.log("SinglePass data: " + dataSeqString);
			
	
			ngramPrec += getNGramPrecision(p, q, maxN, threshold);
		}
		
		ngramPrec = ngramPrec / 3.0;
		
		Log.i(TAG, (new Date()).toString() + ", SinglePass n-gram similarity: " + ngramPrec + ", N: " + maxN);
		//MobiSensLog.log((new Date()).toString() + ", SinglePass n-gram similarity: " + ngramPrec + ", N: " + maxN);
		
		return ngramPrec;
	}
}
