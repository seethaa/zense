package edu.cmu.sv.lifelogger.algorithm;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.LinkedList;

import android.content.Context;

import edu.cmu.sv.mobisens.io.Directory;
import edu.cmu.sv.mobisens.io.MobiSensData;
import edu.cmu.sv.mobisens.io.MobiSensDataHolder;
import edu.cmu.sv.mobisens.io.MobiSensLog;
import edu.cmu.sv.mobisens.util.Annotation;
import edu.cmu.sv.mobisens.util.KeyValuePair;

public class DMW {
	public final double MOTION_THRESHOLD = 0.5;
	public final double GEO_THRESHOLD = 0.6;
	
	private Hashtable<String, double[]> weightVectors = null;
	private LinkedList<Annotation> humanAnnos = null;
	private final static int MODALITY_COUNT = 2;
	private MobiSensDataHolder db;
	
	private DMW(){
		
	}
	
	public static DMW create(Context context){
		DMW model = new DMW();
		model.db = new MobiSensDataHolder(context);
		
		model.db.open();
		
		Hashtable<String, double[]> result = new Hashtable<String, double[]>();
		model.weightVectors = result;
		
		String[] humanAnnoStrings = model.db.searchByType(MobiSensData.TYPE_HUMAN);
		model.db.close();
		
		LinkedList<Annotation> humanAnnos = new LinkedList<Annotation>();
		for(String annoString:humanAnnoStrings){
			Annotation anno = Annotation.fromString(annoString);
			if(anno != null){
				humanAnnos.add(anno);
			}
		}
		model.humanAnnos = humanAnnos;
		
		if(humanAnnos.size() == 0)
			return model;
		
		ArrayList<Annotation> posInsts = new ArrayList<Annotation>();
		ArrayList<Annotation> negInsts = new ArrayList<Annotation>();
		
		HashMap<String, ArrayList<Annotation>> set = new HashMap<String, ArrayList<Annotation>>();
		
		// Recover the motion models into Annotation.
		for(int i=0; i<humanAnnos.size(); i++){
			Annotation humanAnno = humanAnnos.get(i);
			
			if(!set.containsKey(humanAnno.getEscapedName())){
				set.put(humanAnno.getEscapedName(), new ArrayList<Annotation>());
			}
			
			set.get(humanAnno.getEscapedName()).add(humanAnno);
		}
		
		for(String anno:set.keySet()){
			posInsts = set.get(anno);
			for(String negAnno:set.keySet()){
				if(!negAnno.equals(anno)){
					negInsts.addAll(set.get(negAnno));
				}
			}
			
			// Now the pos and neg sets are ready.
			double[] muPlus = new double[2];
			
			for(int i = 0; i < posInsts.size() - 1; i++){
				for(int j = i + 1; j < posInsts.size(); j++){
					Annotation a1 = posInsts.get(i);
					Annotation a2 = posInsts.get(j);
					
					// Get the motion similarity
					double motionSim = 0D;
					if(a1.getMotionModel() != null && a2.getMotionModel() != null){
						motionSim = a1.getMotionModel().getDistance(a2.getMotionModel());
					}
					muPlus[0] += motionSim;
					
					// Get the geo similarity
					double geoSim = 0D;
					if(a1.getLocations() != null && a2.getLocations() != null){
						geoSim = Path.compare(a1.getLocations().getData(), a2.getLocations().getData(), 100.0);
					}
					muPlus[1] += geoSim;
				}
			}
			
			// Compute the centroid.
			if(posInsts.size() > 1){
				double itemCount = (double)(1 + posInsts.size() - 1) * (double)(posInsts.size() - 1) / 2.0;
				muPlus[0] /= itemCount;
				muPlus[1] /= itemCount;
			}else{
				muPlus = new double[]{1.0, 1.0};
			}
			
			if(muPlus[0] < 0)
				muPlus[0] = 0;
			if(muPlus[1] < 0)
				muPlus[1] = 0;
			
			// Get the centroid of negative set.
			double[] muMinus = new double[2];
			
			for(int i = 0; i < posInsts.size(); i++){
				for(int j = 0; j < negInsts.size(); j++){
					Annotation a1 = posInsts.get(i);
					Annotation a2 = negInsts.get(j);
					
					// Get the motion similarity
					double motionSim = 0D;
					if(a1.getMotionModel() != null && a2.getMotionModel() != null){
						motionSim = a1.getMotionModel().getDistance(a2.getMotionModel());
					}
					muMinus[0] += motionSim;
					
					// Get the geo similarity
					double geoSim = 0D;
					if(a1.getLocations() != null && a2.getLocations() != null){
						geoSim = Path.compare(a1.getLocations().getData(), a2.getLocations().getData(), 100.0);
					}
					muMinus[1] += geoSim;
				}
			}
			
			// Compute the centroid.
			if(posInsts.size() * negInsts.size() > 0){
				muMinus[0] /= (double)(posInsts.size() * negInsts.size());
				muMinus[1] /= (double)(posInsts.size() * negInsts.size());
			}else{
				muMinus = new double[]{0, 0};
			}
			
			if(muMinus[0] < 0)
				muMinus[0] = 0;
			if(muMinus[1] < 0)
				muMinus[1] = 0;
			
			double[] weightVector = new double[]{muPlus[0] - muMinus[0], muPlus[1] - muMinus[1]};
			double l2Norm = Math.sqrt(weightVector[0] * weightVector[0] + weightVector[1] * weightVector[1]);
			weightVector[0] /= l2Norm;
			weightVector[1] /= l2Norm;
			
			double max = Numerical.getMax(weightVector);
			weightVector[0] /= max;
			weightVector[1] /= max;
			
			result.put(anno, weightVector);
			
			// reset.
			posInsts.clear();
			negInsts.clear();
		}
		
		return model;
	}
	
	
	public static DMW fromString(String modelContent, Context context){
		DMW model = new DMW();
		model.humanAnnos = new LinkedList<Annotation>();
		model.db = new MobiSensDataHolder(context);
		
		Hashtable<String, double[]> weights = new Hashtable<String, double[]>();
		model.weightVectors = weights;
		
		if(modelContent == null)
			return model;
		
		String[] modelParts = modelContent.split("\r\n\r\n");
		if(modelParts.length == 0)
			return model;
		
		String[] lines = modelParts[0].split("\r\n");
		for(String line:lines){
			String[] columns = line.split(",");
			if(columns.length != 3)
				continue;
			double[] weightVector = new double[]{Double.valueOf(columns[1]),
					Double.valueOf(columns[2])};
			
			
			weights.put(columns[0], weightVector);
			
		}
		
		lines = modelParts[1].split("\r\n");
		
		for(String line:lines){
			Annotation anno = Annotation.fromString(line);
			if(anno == null)
				continue;
			
			model.humanAnnos.add(anno);
			
		}
		
		return model;
	}
	
	public static void clear(){
		try {
			File dmwFile = Directory.openFile(Directory.MOBISENS_ROOT, Directory.DMW_MODEL_FILENAME);
			dmwFile.delete();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public double[] getWeight(String activity){
		if(this.weightVectors.contains(activity)){
			return this.weightVectors.get(activity);
		}
		
		double[] initWeights = new double[DMW.MODALITY_COUNT];
		initWeights[0] = 1.0;
		return initWeights;
	}
	
	public KeyValuePair<Annotation, Double> getNearestNeighbor(Annotation unknownActivity){
		synchronized(this.humanAnnos){
			String log = "";
			ArrayList<KeyValuePair<Annotation, Double>> distances = new ArrayList<KeyValuePair<Annotation, Double>>();
			
			for(Annotation anno:this.humanAnnos){
				double[] weights = this.getWeight(anno.getEscapedName());

				double motionDistance  = unknownActivity.getMotionModel().getDistance(anno.getMotionModel());
				double geoDistance = Path.compare(unknownActivity.getLocations().getData(), anno.getLocations().getData(), Path.SAME_LOCATION_THRESHOLD);

				double weightedDistance = motionDistance * weights[0] + geoDistance * weights[1];
				KeyValuePair<Annotation, Double> item = new KeyValuePair<Annotation, Double>(anno, weightedDistance);
				distances.add(item);
				
				// Now what we use is SMW, all weights are 1.0
				/*
				if(motionDistance > MOTION_THRESHOLD && geoDistance > GEO_THRESHOLD){
					KeyValuePair<Annotation, Double> item = new KeyValuePair<Annotation, Double>(anno, motionDistance + geoDistance);
					distances.add(item);
				}
				*/
				log += anno.getName() + ", motion: " + motionDistance + ", geo: " + geoDistance + "\r\n";
			}
			
			KeyValuePair<Annotation, Double> maxItem = null;
			for(KeyValuePair<Annotation, Double> item:distances){
				if(maxItem == null)
					maxItem = item;
				if(item.getValue() > maxItem.getValue()){
					maxItem = item;
				}
			}
			
			if(maxItem != null){
				log += "Selected: " + maxItem.getKey().getName() + ", distance: " + maxItem.getValue();
				MobiSensLog.log(log);
			}else{
				maxItem = new KeyValuePair<Annotation, Double>(unknownActivity, 0.0);
			}
			
			return maxItem;
		}
	}
	
	public String toString(){
		StringBuilder builder = new StringBuilder(1024);
		for(String anno:this.weightVectors.keySet()){
			builder.append(anno).append(",")
			.append(this.weightVectors.get(anno)[0]).append(",")
			.append(this.weightVectors.get(anno)[1]).append("\r\n");
		}
		
		builder.append("\r\n\r\n");
		for(Annotation anno:this.humanAnnos){
			builder.append(anno.toString()).append("\r\n");
		}
		return builder.toString();
	}
}
