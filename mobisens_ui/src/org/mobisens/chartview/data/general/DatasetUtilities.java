package org.mobisens.chartview.data.general;

import java.util.Iterator;
import java.util.List;

/**
 * @author Ming
 * 
 * Utility methods for use with some of the data class.
 *
 */
public final class DatasetUtilities {
	
	/**
	 * Returns the total of the values in one column of the supplied data.
	 * 
	 * @param dataset
	 * @return
	 */
	public static double calculatePieDatasetTotal(PieDataset dataset){
		if (dataset == null){
			throw new IllegalArgumentException("Null 'dataset' argument.");
		}
		
		List keys = dataset.getKeys();
		double totalValue = 0.0;
		Iterator iterator = keys.iterator();
		while (iterator.hasNext()){
			String current = (String) iterator.next();
			if (current != null){
				double dValue = dataset.getValue(current);
				if (dValue > 0){
					totalValue = totalValue + dValue;
				}
			}			
		}		
		return totalValue;
	}
	
	

}
