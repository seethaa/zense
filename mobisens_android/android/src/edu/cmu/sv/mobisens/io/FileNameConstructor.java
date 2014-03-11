package edu.cmu.sv.mobisens.io;

public class FileNameConstructor {
	public static String getLocationFileName(long start, long end){
		return start + "_" + end + ".geo";
	}
}
