package edu.cmu.sv.lifelogger.algorithm;

public class Numerical {
	public static double getStandardDeviation(double[] data, int start, int end)
    {
        double sum = 0;
        double length = (double)(end - start + 1);

        for (int i = start; i <= end; i++)
        {
            sum += data[i];
        }

        double avg = sum / length;
        double std = 0;
        for (int i = start; i <= end; i++)
        {
            std += Math.pow(data[i] - avg, 2);
        }
        std = Math.sqrt(std / length);
        return std;
    }
	
	public static double getStandardDeviation(double[] data){
		if(data.length == 0)
			return Double.NaN;
		
		return getStandardDeviation(data, 0, data.length - 1);
	}
	
	public static double getAverage(double[] data, int start, int end){
		double sum = 0;
        double length = (double)(end - start + 1);

        for (int i = start; i <= end; i++)
        {
            sum += data[i];
        }

        double avg = sum / length;
        return avg;
	}
	
	public static double getAverage(double[] data){
		if(data.length == 0)
			return Double.NaN;
		
		return getAverage(data, 0, data.length - 1);
	}
	
	public static double getRootMeanSquare(double[] data){
		return getRootMeanSquare(data, 0, data.length - 1);
	}
	
	public static double getRootMeanSquare(double[] data, int start, int end){
		double sum = 0;
        double length = (double)(end - start + 1);
        
        if(length <= 0)
        	return 0.0;

        for (int i = start; i <= end; i++)
        {
        	double value = data[i];
            sum += value * value;
        }

        double rms = Math.sqrt(sum / length);
        return rms;
	}
	
	public static double getMin(double[] data){
		double min = Double.MAX_VALUE;
		if(data == null)
			return min;
		for(int i=0; i<data.length; i++){
			if(data[i] < min)
				min = data[i];
		}
		
		return min;
	}
	
	public static double getMax(double[] data){
		double max = Double.MIN_VALUE;
		if(data == null)
			return max;
		for(int i=0; i<data.length; i++){
			if(data[i] > max)
				max = data[i];
		}
		
		return max;
	}
}
