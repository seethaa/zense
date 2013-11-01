package sv.cmu.edu.dataquery;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;

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
	public static int calculatePieDatasetTotal(Map<String, Integer> dataset){
		if (dataset == null){
			throw new IllegalArgumentException("Null 'dataset' argument.");
		}
		
		Set<String> keys = dataset.keySet();
		int totalValue = 0;
		Iterator<String> iterator = keys.iterator();
		while (iterator.hasNext()){
			String current = (String) iterator.next();
			if (current != null){
				int iValue = dataset.get(current);
				if (iValue > 0){
					totalValue = totalValue + iValue;
				}
			}			
		}		
		return totalValue;
	}
	
	

}
