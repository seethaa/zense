package edu.cmu.sv.mobisens.io;

public class MobiSensData {
	public static final int TYPE_HUMAN = 0;
	public static final int TYPE_MACHINE = 1;
	
	private static final int TYPE_FIRST = TYPE_HUMAN;
	private static final int TYPE_LAST = TYPE_MACHINE;
	
	private long startTime;
	private long endTime;
	private String annotation;
	private int type;	// 0: human, 1: machine
	
	public MobiSensData(long s, long e, String a, int t) {
		startTime = s;
		endTime = e;
		annotation = a;
		type = t;
	}
	
	public long getStartTime() {
		return startTime;
	}
	
	public long getEndTime() {
		return endTime;
	}
	
	public String getAnnotation() {
		return annotation;
	}
	
	public void setAnnotation(String a) {
		annotation = a;
	}
	
	public int getType() {
		return type;
	}
	
	public static boolean isValid(long sTime, long eTime, String a, int t) {
		if (sTime > eTime)
			return false;
		else if (t<TYPE_FIRST || t>TYPE_LAST)
			return false;
		else 
			return true;
	}
}
