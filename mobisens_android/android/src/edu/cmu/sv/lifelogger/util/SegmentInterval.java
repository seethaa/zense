package edu.cmu.sv.lifelogger.util;

public class SegmentInterval {
	private double start = 0;

    public double getStart() {
        return start;
    }
    
    private void setStart(double value){
    	start = value;
    }
    
    private double end = 0;

    public double getEnd() {
        return end;
    }
    
    private void setEnd(double value){
    	end = value;
    }

    private int dataCount = 0;

    public int getDataCount(){
        return dataCount;
    }
    
    
    private void setDataCount(int value){
    	dataCount = value;
    }

    private String name = "";

    public String getName(){
        return name;
    }
    
    private void setName(String value){
    	name = value;
    }



    protected SegmentInterval()
    {
    }

    public SegmentInterval(double start, double end, int dataCount, String name)
    {
        setStart(start);
        setEnd(end);
        setDataCount(dataCount);
        setName(name);
    }
    
    public boolean isInSegment(double value){
    	if(value >= this.getStart() && value <= this.getEnd()){
    		return true;
    	}
    	
    	return false;
    }
}
