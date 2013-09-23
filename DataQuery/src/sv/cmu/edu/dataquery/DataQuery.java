package sv.cmu.edu.dataquery;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;

/**
 * 
 */

/**
 * @author Ming
 *
 */
public class DataQuery {
	
	public static void main(String[] args) throws IOException{
		List<ActivitySummary> ActivitySummaryList = PercentageDataset.FetchPercentageDatat("1111", "1", "2");
		Iterator<ActivitySummary> it = ActivitySummaryList.iterator();
		System.out.println("size of list " + ActivitySummaryList.size());
		while ( it.hasNext() ){
			ActivitySummary entry = it.next();
			System.out.println(
					entry.Activity + " " + 
					entry.ActivityTotalTime + " " + entry.ActivityPercentage);
		}
	}

}
