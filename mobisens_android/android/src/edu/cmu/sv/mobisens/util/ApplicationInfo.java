package edu.cmu.sv.mobisens.util;

import java.util.List;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.TrafficStats;
import android.os.Process;
import android.util.Log;

public class ApplicationInfo {
	private static ActivityManager activityManager = null;
	
	public static String getRunningApplicationInfoCSV(Context context){
		if(activityManager == null){
			activityManager = (ActivityManager)context.getSystemService(Context.ACTIVITY_SERVICE);
		}

		List<RunningAppProcessInfo> runningProcesses = activityManager.getRunningAppProcesses();
		
		if(runningProcesses == null){
			return "";
		}
		StringBuilder result = new StringBuilder();
		for (RunningAppProcessInfo process:runningProcesses) {
			result.append(process.processName).append(",").append(getImportanceString(process.importance)).append(",");
		}
		return result.toString();
	}
	
	  //Get the Names of All Applications
	public static String[] getAllAppNames(Context caller) {
		
		PackageManager packageManager = caller.getPackageManager();
		
		if(packageManager == null){
			return new String[0];
		}
	    List<PackageInfo> packages = packageManager.getInstalledPackages(0);
	    
	    String[] appNames = new String[packages.size()];
	    
	    for(int i=0; i<packages.size(); i++) { 
	      PackageInfo packageInfo = packages.get(i); 
	      appNames[i] = packageInfo.applicationInfo.loadLabel(packageManager).toString(); 
	    }
	    
	    return appNames;
	}
	
	
	public static String[] getApplicationTraffic(int[] uids){
		String[] traffic = new String[uids.length];
		StringBuilder builder = new StringBuilder();
		
		for(int i=0; i<uids.length; i++){
			builder.append("total_rcv").append(",").append(TrafficStats.getUidRxBytes(uids[i])).append(",")
			.append("total_send").append(",").append(TrafficStats.getUidTxBytes(uids[i]));
			traffic[i] = builder.toString();
			builder = new StringBuilder();
		}
		
		return traffic;
		
	}
	
	public static String getApplicationTrafficInfoCSV(Context caller){
		
		if(activityManager == null){
			activityManager = (ActivityManager)caller.getSystemService(Context.ACTIVITY_SERVICE);
		}

		List<RunningAppProcessInfo> runningProcesses = activityManager.getRunningAppProcesses();
		if(runningProcesses == null)
			return "";
		
		
		String[] runningAppNames = new String[runningProcesses.size()];
		
		int[] uids = new int[runningAppNames.length];
		int index = 0;
		for (RunningAppProcessInfo process:runningProcesses) {
			runningAppNames[index] = process.processName;
			uids[index] = process.uid;
			index++;
		}

		
		String[] traffic = getApplicationTraffic(uids);
		StringBuilder builder = new StringBuilder(1000);
		
		for(int i = 0; i < runningAppNames.length; i++){
			builder.append("app").append(",").append(runningAppNames[i]).append(",")
			.append("uid").append(",").append(uids[i]).append(",")
			.append(traffic[i]).append(",");
		}
		
		return builder.toString();
	}
	
	private static String getImportanceString(int importance){
		switch(importance){
		case RunningAppProcessInfo.IMPORTANCE_BACKGROUND:
			return "IMPORTANCE_BACKGROUND";
		case RunningAppProcessInfo.IMPORTANCE_EMPTY:
			return "IMPORTANCE_EMPTY";
		case RunningAppProcessInfo.IMPORTANCE_FOREGROUND:
			return "IMPORTANCE_FOREGROUND";
		case RunningAppProcessInfo.IMPORTANCE_SERVICE:
			return "IMPORTANCE_SERVICE";
		case RunningAppProcessInfo.IMPORTANCE_VISIBLE:
			return "IMPORTANCE_VISIBLE";
		}
		
		return "IMPORTANCE_EMPTY";
	}
}
