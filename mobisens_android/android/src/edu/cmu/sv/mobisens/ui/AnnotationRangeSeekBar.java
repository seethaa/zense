package edu.cmu.sv.mobisens.ui;

import java.util.Date;

import edu.cmu.sv.mobisens.util.Annotation;

import android.content.Context;
import android.util.AttributeSet;

public class AnnotationRangeSeekBar extends RangeSeekBar<Long> {
	
	public AnnotationRangeSeekBar(Context context, AttributeSet attributeSet) {
        super(0L, 100L, context, attributeSet);
        
    }
	
	public void setDateRangeWithSelectionDelta(Date startDate, Date endDate, double deltaFactor){
		
		long timeSpan = endDate.getTime() - startDate.getTime();
    	long delta = (long)((double)timeSpan * deltaFactor);
    	// Allow some delta.
    	Date minDate = new Date(startDate.getTime() - delta);
		Date maxDate = new Date(endDate.getTime() + delta);
		
		if(maxDate.getTime() > System.currentTimeMillis()){
			maxDate = new Date(System.currentTimeMillis());  // border case, you cannot label something in the future.
		}
		
		setMinValue(minDate.getTime());
    	setMaxValue(maxDate.getTime());
    	setSelectedMinValue(startDate.getTime());
    	setSelectedMaxValue(endDate.getTime());
	}

	private void setMaxValue(long time) {
		// TODO Auto-generated method stub
		this.absoluteMaxValue = time;
		this.init();
	}

	private void setMinValue(long time) {
		// TODO Auto-generated method stub
		this.absoluteMinValue = time;
		this.init();
	}
}
