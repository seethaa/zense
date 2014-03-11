package edu.cmu.sv.mobisens.content;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import edu.cmu.sv.lifelogger.algorithm.DMW;
import edu.cmu.sv.lifelogger.util.DataCollector;
import edu.cmu.sv.lifelogger.util.NGramModel;
import edu.cmu.sv.mobisens.MobiSensService;
import edu.cmu.sv.mobisens.io.DataShrinker;
import edu.cmu.sv.mobisens.io.Directory;
import edu.cmu.sv.mobisens.io.MobiSensData;
import edu.cmu.sv.mobisens.io.MobiSensDataHolder;
import edu.cmu.sv.mobisens.io.MobiSensLog;
import edu.cmu.sv.mobisens.settings.ServiceParameters;
import edu.cmu.sv.mobisens.ui.RendererWidget;
import edu.cmu.sv.mobisens.util.Annotation;
import edu.cmu.sv.mobisens.util.KeyValuePair;
import edu.cmu.sv.mobisens.util.MachineAnnotation;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;

public class AnnotationWidget extends Widget {
	private static final String TAG = "AnnotationWidget";
	private static final String CLASS_PREFIX = AnnotationWidget.class.getName();
	public static final String ACTION_SHRINK_FILE = CLASS_PREFIX + ".action_shrink_file";
	public static final String ACTION_APPEND_ANNO = CLASS_PREFIX + ".action_append_anno";
	public static final String ACTION_APPEND_ANNO_DONE = CLASS_PREFIX + ".action_append_anno_done";
	public static final String ACTION_TELL_ANNO_BOUNDS = CLASS_PREFIX + ".action_tell_anno_bounds";
	public static final String ACTION_READ_ALL_ANNO_DONE = CLASS_PREFIX + ".action_read_all_anno_done";
	public static final String ACTION_GET_ALL_NAMES = CLASS_PREFIX + ".action_get_all_names";
	public static final String ACTION_GET_ALL_NAMES_DONE = CLASS_PREFIX + ".action_get_all_names_done";
	public static final String ACTION_GET_ALL_ANNOS = CLASS_PREFIX + ".action_get_all_annos";
	
	public static final String EXTRA_MIN_LINE = CLASS_PREFIX + ".extra_min_line";
	public static final String EXTRA_ANNO_BOUNDS = CLASS_PREFIX + ".extra_anno_bounds";
	public static final String EXTRA_SHRINK_DEVICEID = CLASS_PREFIX + ".extra_shrink_device_id";
	public static final String EXTRA_RESERVE_TIME = CLASS_PREFIX + ".extra_reservce_time";
	public static final String EXTRA_MERGE_WITH_LAST_ANNO = CLASS_PREFIX + ".extra_merge_anno";
	public static final String EXTRA_GET_NAME_SIZE = CLASS_PREFIX + ".extra_get_name_size";
	public static final String EXTRA_GET_NAME_ID = CLASS_PREFIX + ".extra_get_name_id";
	public static final String EXTRA_ANNO_NAMES = CLASS_PREFIX + ".extra_anno_names";
	
	public static final String EXTRA_ANNO_STRINGS = CLASS_PREFIX + ".extra_anno_strings";
	public static final String EXTRA_ANNO_MERGED = CLASS_PREFIX + ".extra_anno_merged";
	
	public static final int DEFAULT_GET_NAME_SIZE = 1000;
	
	private MobiSensDataHolder db;
	
	public void register(ContextWrapper contextWrapper){
		super.register(contextWrapper);
		
		db = new MobiSensDataHolder(contextWrapper);
		try{
			db.open();
		}catch(Exception ex){
			MobiSensLog.log(ex);
		}
		

	}
	
	public void unregister(){
		super.unregister();
		db.close();
	}
	
	
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
    	String action = intent.getAction();
    	
    	if(action.equals(ACTION_SHRINK_FILE)){
    		//final String deviceID = intent.getStringExtra(EXTRA_SHRINK_DEVICEID);
    		final long reserveTime = intent.getLongExtra(EXTRA_RESERVE_TIME, -1);
    		//final int minLine = intent.getIntExtra(EXTRA_MIN_LINE, -1);
    		Thread workingThread = new Thread(){
    			public void run(){
    	    		db.batchRemove(reserveTime);
    			}
    		};
    		
    		workingThread.start();
    		
		}
    	
    	if(ACTION_APPEND_ANNO.equals(action)){
    		onAppendAnnotation(intent);
    	}
    	
    	
    	if(action.equals(RendererWidget.ACTION_CLEAR)){
    		db.clear();
    	}
    	
    	if(action.equals(RendererWidget.ACTION_UPDATE_ANNO)){
    		String annoString = intent.getStringExtra(Annotation.EXTRA_ANNO_STRING);
    		Annotation anno = Annotation.fromString(annoString);
    		if(anno == null)
    			return;
    		
    		db.set(anno.getDBId(), anno.getStart().getTime(), anno.getEnd().getTime(), anno.toString());
    	}
    	
    	if(action.equals(ACTION_GET_ALL_ANNOS)){
    		this.getAllAnnosAsync(intent);
    	}
    	
    	if(ACTION_GET_ALL_NAMES.equals(action)){
    		intent.getIntExtra(EXTRA_GET_NAME_SIZE, DEFAULT_GET_NAME_SIZE);
    		long requestId = intent.getLongExtra(EXTRA_GET_NAME_ID, 0);
    		
    		String[] annoStrings = db.searchByType(MobiSensData.TYPE_HUMAN);
    		String[] names = new String[annoStrings.length];
    		int index = 0;
    		for(String annoString:annoStrings){
    			Annotation anno = Annotation.fromString(annoString);
    			names[index] = anno.getName();
    			index++;
    		}
    		
    		Intent replyIntent = new Intent(ACTION_GET_ALL_NAMES_DONE);
    		replyIntent.putExtra(EXTRA_ANNO_NAMES, names);
    		replyIntent.putExtra(EXTRA_GET_NAME_ID, requestId);
    		this.getContext().sendBroadcast(replyIntent);
    	}
    	
	}
	
	public static String getAnnotationFileName(Context context){
		return MobiSensService.getDeviceID(context) + "_" + Directory.RECORDER_TYPE_ANNOTATION + ".csv";
	}
	
	private void getAllAnnosAsync(final Intent intent){
		Thread workingThread = new Thread(){
			public void run(){
				
    			Intent allAnnoIntent = new Intent(ACTION_READ_ALL_ANNO_DONE);
    			allAnnoIntent.putExtra(EXTRA_ANNO_STRINGS, "");
    			
    			String rendererType = intent.getStringExtra(RendererWidget.EXTRA_RENDERER_TYPE);
    			if(rendererType == null)
    				rendererType = RendererWidget.ALL_RENDERER_TYPES;
    			
    			allAnnoIntent.putExtra(RendererWidget.EXTRA_RENDERER_TYPE, rendererType);
    			getContext().sendBroadcast(allAnnoIntent);
			}
		};
		workingThread.start();
	}
	
	private void onAppendAnnotation(Intent requestIntent){
		String annoString = requestIntent.getStringExtra(Annotation.EXTRA_ANNO_STRING);
		boolean merge = requestIntent.getBooleanExtra(EXTRA_MERGE_WITH_LAST_ANNO, false);
		Annotation anno = Annotation.fromString(annoString);
		
		if(anno == null)
			return;
		
		
		if(!merge){
			int type = (anno instanceof MachineAnnotation) ? 1 : 0;
			if(type == MobiSensData.TYPE_MACHINE){
				// If it is a newly generated activity segment, label it.
				// Disable the machine annotation feature. Note by Pang Wu 2013-1-14.
				// anno = this.getNearestNeighbor((MachineAnnotation)anno);
			}
			db.add(anno.getStart().getTime(), anno.getEnd().getTime(), anno.toString());
			MobiSensLog.log("Annotation add requested, results in add.");
		}else{
			MobiSensData last = db.getLast();
			
			if(last != null){
				// TODO: Merge the Motion and Geo models
				// TODO: Update the DB based on startTime.
				Annotation lastAnno = Annotation.fromString(last.getAnnotation());
				Log.i(TAG, "Before merge: " + last.getAnnotation());
				lastAnno.setEnd(anno.getEnd());
				lastAnno.getMotionModel().merge(anno.getMotionModel());
				LinkedList<DataCollector<double[]>> mergedLocations = new LinkedList<DataCollector<double[]>>();
				mergedLocations.add(lastAnno.getLocations());
				mergedLocations.add(anno.getLocations());
				DataCollector<double[]> mergedCollector = new DataCollector<double[]>(mergedLocations);
				lastAnno.setLocations(mergedCollector);
			
			
				if(anno.getName() != Annotation.UNKNOWN_ANNOTATION_NAME){
					lastAnno.setName(anno.getName());
				}
				
				Log.i(TAG, "After merge: " + lastAnno.toString());
				
				db.set(lastAnno.getDBId(), lastAnno.getStart().getTime(), lastAnno.getEnd().getTime(), lastAnno.toString());
				anno = lastAnno;
				MobiSensLog.log("Annotation merged.");
			}else{
				db.add(anno.getStart().getTime(), anno.getEnd().getTime(), anno.toString());
				MobiSensLog.log("Annotation merged requested, results in add.");
			}
			
		}
		
		Intent feedbackIntent = new Intent(ACTION_APPEND_ANNO_DONE);
		feedbackIntent.putExtra(Annotation.EXTRA_ANNO_STRING, anno.toString());
		feedbackIntent.putExtra(EXTRA_ANNO_MERGED, merge);
		
		// Since we don't know which type of the renderer might be opened,
		// we broadcast to all renderer types.
		feedbackIntent.putExtra(RendererWidget.EXTRA_RENDERER_TYPE, RendererWidget.ALL_RENDERER_TYPES);
		
		//NGramModelSet.init();
		//feedbackIntent.putExtra(ActivityReader.EXTRA_RENDER_COLOR, anno.getColor());
		getContext().sendBroadcast(feedbackIntent);
	}
	

	
	private MachineAnnotation getNearestNeighborDMW(MachineAnnotation anno){

		DMW weightedModels = DMW.create(getContext());
		//get all similar activities
		double thredshold = (double)MobiSensService.getParameters().getServiceParameter(ServiceParameters.COSINE_SIM_THRESHOLD) / (double)100;
		KeyValuePair<Annotation, Double> nearestNeighbor = weightedModels.getNearestNeighbor(anno);
		
		if(nearestNeighbor.getValue() < thredshold){
			anno.setName(Annotation.UNKNOWN_ANNOTATION_NAME);
		}else{
			anno.setName(nearestNeighbor.getKey().getName());
			anno.setSimilarity(nearestNeighbor.getValue());
		}
		
		
		return anno;
	}
	
	
	private MachineAnnotation getNearestNeighbor(MachineAnnotation anno){
		//get all similar activities
		double threshold = (double)MobiSensService.getParameters().getServiceParameter(ServiceParameters.COSINE_SIM_THRESHOLD) / (double)100;
		ArrayList<Annotation> humanLabels = new ArrayList<Annotation>();
		String[] annoStrings = db.searchByType(MobiSensData.TYPE_HUMAN);
		for(String annoString:annoStrings){
			humanLabels.add(Annotation.fromString(annoString));
		}
		
		ArrayList<KeyValuePair<String,Double>> similarActivities = new ArrayList<KeyValuePair<String,Double>>();
		for(Annotation humanLabel:humanLabels){
			NGramModel motionModel = humanLabel.getMotionModel();
			if(motionModel == null || anno.getMotionModel() == null){
				continue;
			}
			
			double similarity  = motionModel.getDistance(anno.getMotionModel());
			if(similarity >= threshold){
				KeyValuePair<String,Double> entry = new KeyValuePair<String,Double>(humanLabel.getName(), similarity);
				similarActivities.add(entry);
			}
		}
		
		
		if(similarActivities.size() > 0){
			
			HashMap<String, Integer> vote = new HashMap<String, Integer>();
			for(KeyValuePair<String,Double> activity:similarActivities){
				if(!vote.containsKey(activity.getKey())){
					vote.put(activity.getKey(), 0);
				}
				
				vote.put(activity.getKey(), vote.get(activity.getKey()) + 1);
			}
			
			int maxVote = 0;
			String selectedName = "";
			for(String name:vote.keySet()){
				int currentVote = vote.get(name);
				if(currentVote > maxVote){
					maxVote = currentVote;
					selectedName = name;
				}
			}
			
			double similarity = 0.0;
			for(KeyValuePair<String,Double> activity:similarActivities){
				if(activity.getKey().equals(selectedName)){
					similarity = activity.getValue();
					break;
				}
			}
			
			// Detect something similar, do machine annotation.
			anno.setName(selectedName);
			anno.setSimilarity(similarity);
			

			String log = "";
			
			//int index = 0;
			//String[] alterNames = new String[similarActivities.size() - 1];
			for(KeyValuePair<String,Double> activity:similarActivities){
				log += activity.getKey() + "," + activity.getValue() + "\r\n";
			}
			
			Log.i(TAG, log);
			MobiSensLog.log(log);
			
		}else{
			// Nothing similar and nothing force to set: Mark as unknown
			// Something similar and something force to annotate: Mark as the given label

			anno.setName(Annotation.UNKNOWN_ANNOTATION_NAME);

		}
		
		
		return anno;
	}

	@Override
	protected String[] getActions() {
		// TODO Auto-generated method stub
		
		return new String[]{
				AnnotationWidget.ACTION_SHRINK_FILE,
				AnnotationWidget.ACTION_APPEND_ANNO,
				RendererWidget.ACTION_CLEAR,
				LocationClusteringWidget.ACTION_RAW_LOCATION_DATA,
				RendererWidget.ACTION_UPDATE_ANNO,  //User update annotation
				ACTION_GET_ALL_ANNOS, //request all annotation
				ACTION_GET_ALL_NAMES //request the existing annotation names
		};
	}
	
	
}
