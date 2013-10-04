/**
 * Class to supply seed values for activity level graph on the dashboard. 
 * 
 */

package edu.cmu.sv.lifelogger.database;

import edu.cmu.sv.lifelogger.helpers.DefinitionHelper;
import edu.cmu.sv.lifelogger.helpers.UtilityHelper;

import java.util.ArrayList;

import edu.cmu.sv.lifelogger.entities.ActivityItem;
import edu.cmu.sv.lifelogger.entities.MoodItem;
import edu.cmu.sv.lifelogger.entities.TimelineItem;

public class DashboardActivityLevelManager {
	
	final private static int TIME_GRANULARITY = 20; // in minutes
	final private static int NO_OF_SERIES = 1; //Currently supported by achartEngine
	// TODO: Change this to get real data

	public static int[][] getSeriesDataset(){
/**
 * Decision about granularity level of the charts to be made here. We have taken 
 * it to be	currently 10 minutes. Change the Constants to reflect other 
 * granularity level.
 */
		int seriesLength = DefinitionHelper.HOURS_IN_DAY * 60 /TIME_GRANULARITY;
		
				

		int dataset[][] = new int [NO_OF_SERIES][seriesLength];
		//Initialize the dataset
		clearCompleteDataset(dataset);
		
		/** Seed the dataset **/
		// Activity 0 Dining 9 to 9.30 am 
		dataset = RangeToSeries(dataset, 1, 9.00, 9.30);
		dataset = RangeToSeries(dataset, 1, 13.00, 14.00);
		dataset = RangeToSeries(dataset, 1, 20.00, 21.00);
		
		// Activity 1 Driving 9.45 to 10.00 am and 17.45 to 18.00 
		dataset = RangeToSeries(dataset, 2, 9.45, 10.00);
		dataset = RangeToSeries(dataset, 2, 17.45, 18.00);
		
		// Activity 2 Shopping 19.00 to 21.00  
		dataset = RangeToSeries(dataset, 3, 19.00, 21.00);
		
			
		
		
		return dataset;
	}

	public static int[] getSeriesPoints () {
		int seriesLength = DefinitionHelper.HOURS_IN_DAY * 60 /TIME_GRANULARITY;
		int x[] = new int [seriesLength];
		
		for (int i = 0; i < x.length; i++) {
			x[i] = i;
		}
		return x;
	}
	public static void clearCompleteDataset(int dataset[][]) {

		for (int i = 0; i < dataset.length; i++) {
			for (int j = 0; j < dataset.length; j++) {
				dataset[i][j] = 0;
			}
		}
	}

	public static void clearSeriesDataset(int dataset[][], int seriesNumber) {
		for (int i = 0; i < dataset.length; i++) {
			dataset[seriesNumber][i] = 0;
		}
	}
	
	
	/**
	 * Function converts the time range to a series of 10 mins for dashboard
	 * activity level data series
	 * Function expects the time to be sent to use in double and in 24 hour 
	 * format. So for 1.35 pm , we expect 13.35 to be sent.
	 * @param startTime
	 * @param endTime
	 */
	public static int[][] RangeToSeries(int dataset[][], int activityNumber, 
			double startTime, double endTime){

		double levelsInHour = 60/TIME_GRANULARITY;
		
		//find the array location for the series
		Double dStartTime = new Double(startTime);

	//	int seriesStartLocation = (int)startTime * 6 +  (int)((startTime - (int) startTime)*10);
	//	int seriesEndLocation = (int)endTime * 6 +  (int)((endTime - (int) endTime)*10);
	
		
		
		double seriesStartLocation = (double) ((int) startTime * levelsInHour + (int) ((startTime - (int) startTime) * (100 / TIME_GRANULARITY)));
		double seriesEndLocation = (double) ((int) endTime * levelsInHour + (int) ((endTime - (int) endTime) * (100 / TIME_GRANULARITY)));

		//Put values inside the array
		
		for (int i = (int) seriesStartLocation; i <= (int)seriesEndLocation; i++) {
			dataset[0][i] = activityNumber;
		}
		return dataset; 
		
	}
	
	public void setSeriesDataset(int dataset[][], int seriesNumber, 
			int value) {
		for (int i = 0; i < dataset.length; i++) {
			dataset[seriesNumber][i] = value;
		}
	}

}
