package org.mobisens.chartview.data.general;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Ming
 *
 */
public class RingDataset {
	
	/**The dataset where values are associated with item name(key) */
	private Map<String, Integer> mDataset;	
	
	/**
	 * Constructs a new dataset, initially empty.
	 */
	public RingDataset(){
		this.mDataset = new HashMap<String, Integer>();
	}

	/**
	 * Sets the data value for a key.
	 * 
	 * @param sItem
	 * @param iValue
	 */
	public void setValue(String sItem, int iValue){
		this.mDataset.put(sItem, iValue);
	}
	
	/**
	 * Returns the data value associated with a key.
	 * 
	 * @param sItem   the value index.
	 * @return The value.
	 */
	public int getValue(String sItem){
		return this.mDataset.get(sItem); 
	}
	
	/**
	 * Returns the number of items in the dataset.
	 * 
	 * @return The item count.
	 */
	public int getItemCount(){
		return this.mDataset.size();
	}
	
	/**
	 * Returns the categories in the dataset.
	 * 
	 * @return The categories in the dataset.
	 */
	public List getKeys(){
		List<String> lKeylist = new ArrayList<String>();
		for ( String key : this.mDataset.keySet()){
			lKeylist.add(key);			
		}
		return lKeylist;
	}	
}










