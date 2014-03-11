package edu.cmu.sv.mobisens.content;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.LinkedList;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.PowerManager;
import android.util.Log;
import edu.cmu.sv.lifelogger.algorithm.DMW;
import edu.cmu.sv.lifelogger.algorithm.NGram;
import edu.cmu.sv.lifelogger.util.Centroids;
import edu.cmu.sv.lifelogger.util.DataCollector;
import edu.cmu.sv.lifelogger.util.NGramModel;
import edu.cmu.sv.mobisens.MobiSensService;
import edu.cmu.sv.mobisens.io.Directory;
import edu.cmu.sv.mobisens.io.FileOperation;
import edu.cmu.sv.mobisens.io.MobiSensLog;
import edu.cmu.sv.mobisens.net.HttpGetRequest;
import edu.cmu.sv.mobisens.net.URLs;
import edu.cmu.sv.mobisens.settings.ServiceParameters;
import edu.cmu.sv.mobisens.util.Annotation;
import edu.cmu.sv.mobisens.util.MachineAnnotation;

public class ActivityWidget extends Widget {
	
	private static final String TAG = "ActivityWidget";
	
	private static final String CLASS_PREFIX = ActivityWidget.class.getName();
	public static final String ACTION_ANNO_COMPLETED = CLASS_PREFIX + ".action_anno_completed";
	public static final String ACTION_REFRESH_ANNO = CLASS_PREFIX + ".action_refresh_anno";
	public static final String ACTION_ABNORMAL_DETECTED = CLASS_PREFIX + ".action_abnormal_detected";
	
	private int dataProcessingThreadCount = 0;
	private final static int MAX_PROCESSING_THREAD = 3;
	
	
	private DataCollector<double[]> previousMotionData = null;
	private DataCollector<double[]> previousGeoData = null;
	
	private DataCollector<double[]> motionDataCollector = null;
	private Centroids centroids = new Centroids();

	private DataCollector<double[]> geoDataCollector = new DataCollector<double[]>(-1);
	public void register(ContextWrapper contextWrapper){
		super.register(contextWrapper);
		
		this.loadCentroids(); // MUST come right after the super.register(contextWrapper);
		this.ioWidget.register(contextWrapper);
		
	}
	
	public void unregister(){
		// This will call cleanUp, which will trigger super.unregister()

		//this.modelSetSynchronizer.unregister();
		this.ioWidget.unregister();
		super.unregister();
	}
	
	
	public void onReceive(Context context, Intent intent) {
		String action = intent.getAction();
		
		if(LocationClusteringWidget.ACTION_RAW_LOCATION_DATA.equals(action)){
    		double[] latlng = intent.getDoubleArrayExtra(LocationClusteringWidget.EXTRA_LAT_LNG);
    		long fixTime = intent.getLongExtra(LocationClusteringWidget.EXTRA_FIX_TIME, System.currentTimeMillis());
    		this.geoDataCollector.collect(new double[]{latlng[0], latlng[1], (double)fixTime / 1000.0});
    	}
		
	}
	
	
	private SensorDataDumpingWidget ioWidget = new SensorDataDumpingWidget(){
		protected void onAccelerometerDataPolled(float[] acc){

			collectData(new double[]{acc[0], acc[1], acc[2]}, ServiceParameters.ACCELEROMETER);
		}
		
		protected void onExit(){
			/*
			synchronized(currentActivityData){
				collectDone(currentActivityData, REASON_SERVICE_END);
				currentActivityData.clear();
			}*/
			
			generateActivityModel(motionDataCollector.clone());
			
		}
	};
	
	public void setShouldCollect(boolean value){
		this.ioWidget.setSkipCollection(!value);
	}
	
	public boolean getIsCollecting(){
		return !this.ioWidget.isSkippingCollection();
	}

	
	private void loadCentroids(){
		BufferedReader in = null;
        try {
            in = new BufferedReader(new InputStreamReader(this.getContext().getAssets().open(Centroids.ASSET_CENTROIDS_JOY)));
            String centroidsString = in.readLine();
            centroids = Centroids.fromString(centroidsString);
            
        } catch (IOException e) {
        	e.printStackTrace();
            MobiSensLog.log(e);
        } finally {
            if(in != null){
            	try {
					in.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					MobiSensLog.log(e);
				}
            }
        }
	}
	
	protected void getCentroidsFromMobiSensServerAsync(){
		Thread loadCentroidsThread = new Thread(){
			public void run(){
				HttpGetRequest getRequest = new HttpGetRequest();
				Hashtable<String, String> params = new Hashtable<String, String>();
				params.put("key", "pang.wu@sv.cmu.edu");
				params.put("device_id", getDeviceID());
				String sessionIdString = getRequest.send(URLs.GET_RANDOM_PREPROCESSED_SESSION_URL, params);
				if(!(sessionIdString.equals("-1") || sessionIdString.equals(""))){
					params.put("id", sessionIdString);
					String centroidsString = getRequest.send(URLs.GET_CENTROIDS_URL, params);
					if(!centroidsString.equals("")){
						try {
							File cenroidDB = Directory.openFile(Directory.MOBISENS_ROOT, Directory.CENTROIDS_DB_FILENAME);
							FileOperation.writeStringToFile(cenroidDB, centroidsString);
							centroids = Centroids.fromString(centroidsString);
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
							MobiSensLog.log(e);
						}
						
						Log.i(TAG, "Update cenroids done.");
					}
				}else{
					try {
						File centroidDB = Directory.openFile(Directory.MOBISENS_ROOT, Directory.CENTROIDS_DB_FILENAME);
						String centroidsString = FileOperation.readFileAsString(centroidDB);
						centroids = Centroids.fromString(centroidsString);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						MobiSensLog.log(e);
					}
					Log.i(TAG, "Load centroids from file done.");
				}
				
				////initializeAnnoDataLibrary();
			}
		};
		
		loadCentroidsThread.start();
		
	}
	
	private void collectData(double[] value, long type){
		
		if(motionDataCollector != null){
			boolean isFull = (!motionDataCollector.collect(value));
			if(isFull){ // The collector is full.
				// Process
				compareDataBlocksAsync(previousMotionData, motionDataCollector, previousGeoData, geoDataCollector);
				
				/*
				if(previousMotionData != null){
					compareDataBlocksAsync(previousMotionData, motionDataCollector, previousGeoData, geoDataCollector);
				}else{
					
					synchronized(currentActivityMotionData){
						currentActivityMotionData.add(motionDataCollector.clone());
						currentActivityGeoData.add(geoDataCollector.clone());
					}
				}
				*/
				
				previousMotionData = motionDataCollector.clone();
				previousGeoData = geoDataCollector.clone();
				
				if(motionDataCollector.getCollectInterval() > 0){
					motionDataCollector = new DataCollector<double[]>(motionDataCollector.getCollectInterval());
				}else if(motionDataCollector.getDataSizeLimit() > 0){
					//motionDataCollector = new DataCollector<double[]>(motionDataCollector.getDataSizeLimit());
					/*int samplingMagnitude = (int) ((ServiceParameters.CYCLING_BASE_MS) / 
							MobiSensService.getParameters().getServiceParameter(
									ServiceParameters.PHONE_WAKEUP_DURATION));*/
					
					motionDataCollector = new DataCollector<double[]>(
							(int)MobiSensService.getParameters().getServiceParameter(
									ServiceParameters.COLLECTION_DATA_SIZE)
							);
				}
				
				geoDataCollector = new DataCollector<double[]>(-1);
				
			}
		}else{
			int samplingMagnitude = (int) ((ServiceParameters.CYCLING_BASE_MS) / 
					MobiSensService.getParameters().getServiceParameter(
							ServiceParameters.PHONE_WAKEUP_DURATION));
			
			motionDataCollector = new DataCollector<double[]>(
					(int)MobiSensService.getParameters().getServiceParameter(
							ServiceParameters.COLLECTION_DATA_SIZE) / samplingMagnitude
					);
			geoDataCollector = new DataCollector<double[]>(-1);
		}
		
		
	}
	
	private String currentActivityName = null;
	
	private void compareDataBlocksAsync(DataCollector<double[]> previousMotionData, 
			DataCollector<double[]> currentMotionData,
			DataCollector<double[]> previousGeoData,
			DataCollector<double[]> currentGeoData){
		
		MobiSensService.acquireTimeoutWakeLockForService(getContext(), 
				PowerManager.PARTIAL_WAKE_LOCK, 
				3 * 1000,
				"compareDataBlocksAsync");
		
		final int windowSize = (int)MobiSensService.getParameters().getServiceParameter(ServiceParameters.WINDOW_SIZE);
		final int stepSize = (int)MobiSensService.getParameters().getServiceParameter(ServiceParameters.STEP_SIZE);
		final int ngramMaxN = (int)MobiSensService.getParameters().getServiceParameter(ServiceParameters.NGRAM_MAX_N);
		
		
		
		final DataCollector<double[]> previousMotionDataClone = previousMotionData ==  null ? null : previousMotionData.clone();
		final DataCollector<double[]> currentMotionDataClone = currentMotionData.clone();
		final DataCollector<double[]> currentGeoDataClone = currentGeoData.clone();
		
		Thread processThread = new Thread(){
			public void run(){
				
				
				Log.i(TAG, "process thread started...");
				
				if(previousMotionDataClone == null){
					NGramModel ngramModel = generateActivityModel(currentMotionDataClone);
					if(ngramModel != null){
						currentActivityName = newBlockDone(ngramModel, currentGeoDataClone).getName();
					}
					dataProcessingThreadCount--;
					Log.i(TAG, "process thread ended.");
					return;
				}
				
				int[] seq1 = centroids.getSequences(previousMotionDataClone, windowSize, stepSize);
				int[] seq2 = centroids.getSequences(currentMotionDataClone, windowSize, stepSize);
				ArrayList<Integer> arr1 = new ArrayList<Integer>(seq1.length);
				ArrayList<Integer> arr2 = new ArrayList<Integer>(seq2.length);
				
				//String strSeq1 = "";
				//String strSeq2 = "";
				
				for(int val:seq1){
					arr1.add(val);
					//strSeq1 += val + ",";
				}
				
				for(int val:seq2){
					arr2.add(val);
					//strSeq2 += val + ",";
				}
				
				double similarity = NGram.getNGramSimilarity(arr1, arr2, ngramMaxN);
				//double similarity = NGram.getNGramPrecision(arr1, arr2, ngramMaxN, 0);
				
				double thredshold = (double)MobiSensService.getParameters().getServiceParameter(ServiceParameters.SIMILAR_ACTIVITY_THRESHOLD_2) / (double)100;
				Log.i(TAG, "Block similarity: " + String.valueOf(similarity) + ", threshold: " + thredshold);
				MobiSensLog.log("Block similarity: " + String.valueOf(similarity) + ", threshold: " + thredshold);
				
				if(similarity < thredshold){
					NGramModel ngramModel = generateActivityModel(currentMotionDataClone);
					
					if(ngramModel != null){
						// All the newly generated model should be saved as unknown.
						//broadcastMotionModel(ngramModel, Annotation.UNKNOWN_ANNOTATION_NAME);
						
						currentActivityName = newBlockDone(ngramModel, currentGeoDataClone).getName();
					}
					
					// Tell the system activity changed.
					broadcastAbnormalDetected(currentActivityName);
					
				}else{
					
				
					NGramModel ngramModel = generateActivityModel(currentMotionDataClone);
					if(ngramModel != null){
						// All the newly generated model should be saved as unknown.
						//broadcastMotionModel(ngramModel, Annotation.UNKNOWN_ANNOTATION_NAME);
						similarBlockDone(ngramModel, currentGeoDataClone);
					}
					
				}
					
				
				dataProcessingThreadCount--;
				Log.i(TAG, "process thread ended.");
				
			}
		};
		
		if(this.dataProcessingThreadCount <= MAX_PROCESSING_THREAD){
			this.dataProcessingThreadCount++;
			processThread.start();
		}
	}
	
	private void similarBlockDone(NGramModel model, DataCollector<double[]> geoData){
		Annotation anno = new MachineAnnotation(Annotation.UNKNOWN_ANNOTATION_NAME,
				model.getStartTime(),
				model.getEndTime(),
					-1);
		
		// Log.i(TAG, model.toString());
		anno.setMotionModel(model);
		anno.setLocations(geoData);
		anno.setColor(Color.BLACK);

		//if(anno != null)
		// When a similar block done, always merge to previous activity.
		this.broadcastNewActivity(anno, true);
		
	}
	
	public static String getAnnotationFileName(Context context){
		return MobiSensService.getDeviceID(context) + "_" + Directory.RECORDER_TYPE_ANNOTATION + ".csv";
	}
	
	public static String getHumanAnnotationFileName(Context context){
		return MobiSensService.getDeviceID(context) + "_" + Directory.RECORDER_TYPE_ANNOTATION + ".labels.csv";
	}
	
	private Annotation newBlockDone(NGramModel motionModel, DataCollector<double[]> geoData){
		MachineAnnotation anno = new MachineAnnotation(Annotation.UNKNOWN_ANNOTATION_NAME, 
				motionModel.getStartTime(),
				motionModel.getEndTime(),
				0);
		anno.setLocations(geoData);
		
		//Log.i(TAG, motionModel.toString());
		anno.setMotionModel(motionModel);
		anno.setColor(Color.BLACK);
		
		this.broadcastNewActivity(anno, false);
		return anno;
	}
	
	private void broadcastNewActivity(Annotation anno, boolean mergeWithLastAnno){
		Intent appendAnnoIntent = new Intent(AnnotationWidget.ACTION_APPEND_ANNO);
		appendAnnoIntent.putExtra(Annotation.EXTRA_ANNO_STRING, anno.toString());
		appendAnnoIntent.putExtra(AnnotationWidget.EXTRA_MERGE_WITH_LAST_ANNO, mergeWithLastAnno);
		appendAnnoIntent.setAction("edu.cmu.sv.mobisens.content.ActivityWidget.broadcast_msg");
		// Log.i(TAG, anno.toString());
		this.getContext().sendBroadcast(appendAnnoIntent);
	}
	
	private NGramModel generateActivityModel(DataCollector<double[]> combinedData){
		
		//DataCollector<double[]> combinedData = new DataCollector<double[]>(data);
		int windowSize = (int)MobiSensService.getParameters().getServiceParameter(ServiceParameters.WINDOW_SIZE);  // 12
		int stepSize = (int)MobiSensService.getParameters().getServiceParameter(ServiceParameters.STEP_SIZE); // 6
		int ngramMaxN = (int)MobiSensService.getParameters().getServiceParameter(ServiceParameters.NGRAM_MAX_N);
		
		// generate the ngram model
		return NGramModel.fromRawData(combinedData, centroids, windowSize, stepSize, ngramMaxN);
		
	}
	
	
	private void broadcastAbnormalDetected(String newActivityName){
		
		if(this.getContext() == null)
			return;
		
		Intent notifyIntent = new Intent(ActivityWidget.ACTION_ABNORMAL_DETECTED);
		notifyIntent.putExtra(Annotation.EXTRA_ANNO_NAME, newActivityName);
		this.getContext().sendBroadcast(notifyIntent);
	}

	@Override
	protected String[] getActions() {
		// TODO Auto-generated method stub
		
		return new String[]{
				SystemWidget.ACTION_SYSTEM_DATA_EMITTED,
				ProfileWidget.ACTION_SERVICE_PARAMETER_UPDATED,
				ModelWidget.ACTION_UPDATE_DWM_MODEL,
				LocationClusteringWidget.ACTION_RAW_LOCATION_DATA
		};
	}
	
}
