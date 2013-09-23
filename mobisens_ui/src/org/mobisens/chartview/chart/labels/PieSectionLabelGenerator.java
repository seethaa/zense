package org.mobisens.chartview.chart.labels;

import java.util.HashMap;
import java.util.Map;

import org.mobisens.chartview.data.general.DatasetUtilities;
import org.mobisens.chartview.data.general.PieDataset;

import android.content.Context;
import android.util.Log;
import android.widget.TextView;

public class PieSectionLabelGenerator{
	
	private static final String ACTIVITY_TAG = "Log_PieSectionLabelGenerator";
	
	/** The label format string. */
	private String mLabelFormat;
	
	/** The label color */
	private int mLabelColor;
	
	/** The section-text map  */
	private Map<String, TextView> mSectionTextMap;
	
	/** The sectoin-color map */
	private Map<String, Integer> mSectionColorMap;
	
	
	public PieSectionLabelGenerator(){	
	}
	
	/**
	 * Creates an item label generator using the specified number color.
	 * 
	 * @param sectionColorMap
	 */
	public PieSectionLabelGenerator(Map<String, Integer> sectionColorMap){
		
		mSectionTextMap = new HashMap<String, TextView>();
		
		mSectionColorMap = sectionColorMap;		
	}
	
	
	/**
	 * Sets color for each item.
	 * 
	 * @param sectionColorMap
	 */
	public void setPieSectionLabelColor(Map<String, Integer> sectionColorMap){
		
		mSectionTextMap = new HashMap<String, TextView>();
		
		mSectionColorMap = sectionColorMap;	
	}
		
	/**
	 * Generates a label for a pie section.
	 * 
	 * @param context
	 * @param dataset
	 * @param key
	 * @return  The label.
	 */
	public TextView generateSectionLabel(Context context, PieDataset dataset, String key){
		
		Log.i(ACTIVITY_TAG, "begin generateSectionLabel key:" + key);
		
		TextView labelbox = new TextView(context);
		
		Log.i(ACTIVITY_TAG, "begin TextView setting");
		
//		labelbox.setWidth(150);
//		labelbox.setHeight(100);
		labelbox.setTextSize(18);
		
		Log.i(ACTIVITY_TAG, "dataset.getItemCount() " + dataset.getItemCount());
		Log.i(ACTIVITY_TAG, "dataset.getValue(key) " + dataset.getValue(key));
		
		double total = DatasetUtilities.calculatePieDatasetTotal(dataset);
		int percentValue = (int)((double)dataset.getValue(key) / total * 100);
		Log.i(ACTIVITY_TAG, "value: " + String.valueOf(percentValue));
		
		labelbox.setText( key + ": " + String.valueOf(percentValue) + "%");
		
		labelbox.setTextColor(mSectionColorMap.get(key));
		
		return labelbox;
	}
	
}
