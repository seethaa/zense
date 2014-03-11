package edu.cmu.sv.mobisens.util;

import java.util.ArrayList;
import java.util.HashMap;

import edu.cmu.sv.lifelogger.util.RandomColorGenerator;
import edu.cmu.sv.mobisens.io.MobiSensData;
import edu.cmu.sv.mobisens.io.MobiSensDataHolder;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;

public class AnnotationColor {
	@SuppressLint("UseSparseArrays")
	public static int getColor(String annoName, Context context){
		if(annoName.equals(Annotation.UNKNOWN_ANNOTATION_NAME))
			return Color.BLACK;
		
		MobiSensDataHolder db = new MobiSensDataHolder(context);
		db.open();
		
		String[] humanAnnoStrings = db.searchByType(MobiSensData.TYPE_HUMAN);
		ArrayList<Annotation> humanAnnos = new ArrayList<Annotation>();
		for(String annoString:humanAnnoStrings){
			humanAnnos.add(Annotation.fromString(annoString));
		}
		db.close();
		
		HashMap<String, Integer> modelColors = new HashMap<String, Integer>();
		HashMap<Integer, String> inverseModelColors = new HashMap<Integer, String>();
		
		for(Annotation anno:humanAnnos){
			if(!modelColors.containsKey(anno.getName())){
				modelColors.put(anno.getName(), anno.getColor());
			}
			
			if(!inverseModelColors.containsKey(anno.getColor())){
				inverseModelColors.put(anno.getColor(), anno.getName());
			}
		}
		
		int colorCode = Color.BLACK;
		if(modelColors.containsKey(annoName)){
			colorCode = modelColors.get(annoName);
		}else{
			colorCode = RandomColorGenerator.getRandomColor();
			while(inverseModelColors.containsKey(Integer.valueOf(colorCode))){
				colorCode = RandomColorGenerator.getRandomColor();
			}
			
			inverseModelColors.put(Integer.valueOf(colorCode), annoName);
			modelColors.put(annoName, Integer.valueOf(colorCode));
		}
		
		return colorCode;
	}
}
