package org.mobisens.chartview.chart.plot;

import android.graphics.RectF;

public class PiePlotState {
	
	/** The total of the values in the dataset. */
	private double mTotal;
	
	/** The latest angle. */
	private double mLatestAngle;
	
	/** The pie area. */
	private RectF mPieArea;
	
	
	/**
	 * Creates a new object for recording temporary state information 
	 */
	public PiePlotState(){
		this.mTotal = 0.0;
	}
	
	/**
	 * Returns the total of the values in the dataset.
	 * 
	 * @return The total.
	 */
	public double getTotal(){
		return this.mTotal;
	}
	
	/**
	 * Sets the total.
	 * 
	 * @param total   the total.
	 */
	public void setTotal(double total){
		this.mTotal = total;
	}
	
	/**Returns the latest angle.
	 * 
	 * @return The lastest angle.
	 */
	public double getLastAngle(){
		return this.mLatestAngle;
	}
	
	
	/**
	 * Sets the latest angle.
	 * 
	 * @param angle  the angle.
	 */
	public void setLastestAngle(double angle){
		this.mLatestAngle = angle;
	}

	
	/**
	 * Returns the pie area.
	 * 
	 * @return The pie area.
	 */
	public RectF getPieArea(){
		return this.mPieArea;
	}
	
	/**
	 * Sets the pie area.
	 * 
	 * @param pieArea  the area.
	 */
	public void setPieArea(RectF area){
		this.mPieArea = area;
	}
	
	
	
	
	
	
	
	
	
	
}
