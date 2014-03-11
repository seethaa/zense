package org.mobisens.chartview.chart.plot;

import java.util.Iterator;
import java.util.List;

import org.mobisens.chartview.data.general.PieDataset;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;

/**
 * A customised pie plot that leaves a hole in the middle
 *
 */
public class RingPlot extends PiePlot {
	
	/** The log tag */
	private static final String ACTIVITY_TAG = "Log_PiePlot";
	
	/** The diameter of the middle hole */
	private static final double DEFAULT_INNERDIAM_PERCENTAGE = 0.2;
	
	/** the arear of the middle hole */
	private RectF mInnerArea;
	

	/**
	 * Creates a new plot with a <code>null</code> dataset
	 * 
	 * @param context
	 */
	public RingPlot(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}
	
	/**
	 * Creates a new plot for the specified dataset.
	 * 
	 * @param context
	 * @param dataset
	 */
	public RingPlot(Context context, PieDataset dataset) {
		
		super(context);
		// TODO Auto-generated constructor stub	
		
		Log.i(ACTIVITY_TAG, "begin  RingPlot(Context context, PieDataset dataset) ");
		
		this.mDataset  = dataset;
		
		Log.i(ACTIVITY_TAG, "size of dataset: " + String.valueOf(mDataset.getItemCount()) );

	}
	
	
	/**
	 * Creates a new plot. The dataset is initally set to <code>null<code>
	 * 
	 * @param context
	 * @param attrs
	 */
	public RingPlot(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		
		Log.i(ACTIVITY_TAG, "begin  RingPlot(Context context, AttributeSet attrs)");
	}
	 
	
	/* 
	 * Draws the Ring
	 * 
	 * @see org.mobisens.chartview.chart.plot.PiePlot#onDraw(android.graphics.Canvas)
	 */
	protected void onDraw(Canvas canvas){
		super.onDraw(canvas);		

		if ((this.mDataset != null) && (this.mDataset.getKeys().size() >0)){
			
			PiePlotState state = PieStateInitialize(mPieArea, this);
			
			Log.i(ACTIVITY_TAG, "onDraw data.size: " + String.valueOf(mDataset.getItemCount()));
			
			int section = 0;
			List keys = this.mDataset.getKeys();
			Iterator iterator = keys.iterator();
			while(iterator.hasNext()){
				String current = (String) iterator.next();
				double value = this.mDataset.getValue(current);
				if(value > 0.0){
					drawItem(canvas, section, state, current, value);
				}
				section++;
			}
		}
		else{
			Log.i(ACTIVITY_TAG, "Begin drawNoDataPie");
			drawNoDataPie(canvas);
		}
		
		// Draws the inner circle
		float minScreenLen = Math.min(mScreenWidth, mScreenHeight);
		float innerDiam = minScreenLen * (float)DEFAULT_INNERDIAM_PERCENTAGE;			
		float centerX = (float)(mScreenWidth / 2.0);
		float centerY = (float)(mScreenHeight / 2.0);
		
		float dLeft = (float) (centerX - innerDiam / 2.0);
		float dTop = (float) (centerY - innerDiam / 2.0);
		float dRight = (float) (centerX + innerDiam / 2.0);
		float dBottom = (float) (centerY + innerDiam / 2.0);
		
		mInnerArea = new RectF(dLeft, dTop, dRight, dBottom);
		
		Paint paint = new Paint();
		paint.setAntiAlias(true);
		paint.setColor(Color.WHITE);
		
		canvas.drawArc(mInnerArea, 0, 360, true, paint);
	}
	
	

}



















