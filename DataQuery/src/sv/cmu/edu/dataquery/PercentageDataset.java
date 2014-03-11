package sv.cmu.edu.dataquery;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;



/**
 * @author Ming
 * 
 * Use this method for querying data and convert to percentage format
 *
 */
public final class PercentageDataset {
	
	final static String FILE_NAME = 
			"test_activity.txt";
	
	final static String OUTPUT_FILE_NAME = "output.txt";
	
	/**
	 * Returns the total time for each activity
	 * 
	 * @param Uid
	 * @param startTime
	 * @param endTime
	 * @return
	 * @throws IOException
	 */
	public static List<ActivitySummary> FetchPercentageDatat(String Uid, String startTime, 
			String endTime) throws IOException{
		
		List<ActivitySummary> ActivitySummaryList = new ArrayList<ActivitySummary>();
		
		Map<String, Integer> ActivityTimeMap = 
				fetchDatafromFile(Uid, startTime,endTime);
		
		int iTotalValue = DatasetUtilities.calculatePieDatasetTotal(ActivityTimeMap);
		
		System.out.println("total_activityTime: " + 
					Integer.toString(iTotalValue));
		
		System.out.println("len of ActivityTimeMap: " + Integer.toString(ActivityTimeMap.size()));

		Double dCumulate = 0.0;
		Iterator<Entry<String, Integer>> it = ActivityTimeMap.entrySet().iterator();
		while ( it.hasNext() ){
			ActivitySummary ActSum = new ActivitySummary();
			Entry thisEntry = (Entry)it.next();
			
			ActSum.Activity = (String) thisEntry.getKey();
			ActSum.ActivityTotalTime = ((Integer) thisEntry.getValue()).intValue();
			Double dPercent = (double)ActSum.ActivityTotalTime / (double)iTotalValue * 100;
			if( it.hasNext() ){
				ActSum.ActivityPercentage = 
						(double) Math.round(dPercent);
				dCumulate += ActSum.ActivityPercentage;
			}
			else{
				ActSum.ActivityPercentage = 
						100.0 - dCumulate;
			}
			ActivitySummaryList.add(ActSum);
		}
		return ActivitySummaryList;
	}
	
	
	/**
	 * Returns the acivity-time list for a given user and timespan
	 * 
	 * @param Uid
	 * @param startTime
	 * @param endTime
	 * @return
	 * @throws IOException
	 */
	private static Map<String, Integer> fetchDatafromFile(String Uid, String startTime,
			String endTime) throws IOException{
		
		Map<String, Integer> ActTimeMap = new HashMap<String, Integer>();

		File file = new File(FILE_NAME);
		
		try {
			if ( !file.exists()){
				return ActTimeMap;
			}
			
			BufferedReader br = new BufferedReader(new FileReader(FILE_NAME));
			String line;
			while (( line = br.readLine() ) != null){
				String[] tokens = line.split(" ");
				
				if (tokens.length < 3){
					break;
				}
				
				if ( tokens[0].equals(Uid) ){
					ActTimeMap.put(tokens[1], Integer.parseInt(tokens[2]));
				}
			}
			br.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return ActTimeMap;
	}
	
	private static Map<String, Integer> fetchDatafromDB(String Uid, String startTime,
			String endTime){
		
		Map<String, Integer> ActTimeMap = new HashMap<String, Integer>();	
		Timestamp startTimestamp = 
				Timestamp.valueOf(
						new SimpleDateFormat("yyyy-MM-dd hh:mm:ss")
						.format(new Date())
						.concat(startTime)
						);
		Timestamp endTimestamp = 
				Timestamp.valueOf(
						new SimpleDateFormat("yyyy-MM-dd hh:mm:ss")
						.format(new Date())
						.concat(endTime)
						);
		
		return ActTimeMap;
	}
	
/*	
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
*/
}
