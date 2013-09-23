package org.mobisens.chartview.chart.plot;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.mobisens.chartview.chart.ChartColor;
import org.mobisens.chartview.data.general.DatasetUtilities;
import org.mobisens.chartview.data.general.PieDataset;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.FontMetrics;
import android.graphics.Point;
import android.graphics.RectF;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;

/**
 *A plot that displays data in the form of pie chart component
 */
public class PiePlot extends View {
	
	private static final String TAG = "PiePlot";
	
	private static final String ACTIVITY_TAG = "Log_PiePlot";
	
	/** the context of activity **/
	private Context mContext;
	
	/** The default starting angle for the pie chart. */
	public static final double DEFAULT_START_ANGLE = 20.0;
	
	/** The percentage of chart */
	private static final double DEFAULT_DISPLAY_PERCENT = 0.3;
	
	/** The percentage of chart */
	private static final double DEFAULT_INNERDIAM_PERCENTAGE = 0.6;
	
	/** The dataset for the pie chart. */
	protected PieDataset mDataset = new PieDataset();
	
	/** The starting angle. */
	private double mStartAgnle;
	
	/** The width of Screen */
	protected int mScreenWidth;
	
	/** The height of Screen */
	protected int mScreenHeight;
	
	/** The width of parent */
	protected int mParentWidth;
	
	/** The height of parent view */
	protected int mParentHeight;
	
	/** The section paint map. */
	private Map<String, Integer> mSectionColorMap;
	
	/** The color set. 	 */
	private int[] mColorSet;
	
	/** The section-angle map */ 
	private Map<String, Double> mSectionAngleMap;
	
	/** The diameter of the pie chart */
	private float mPieDiameter;
	
	/** The diameter of the outer text chart */
	private double mOuterDiameter;
	
	/**The diameter of the inner blank part**/
	private float mInnerDiameter;
	
	
	/** The pie area. */
	protected RectF mPieArea;
	
	/** The inner blank area. */
	protected RectF mInnerArea;
	
	/** the center of the pie chart */
	private Point mCenterPoint;
	
	/**the paint of pie **/
	private Paint mPiePaint;
	
	/**the paint of text **/
	private TextPaint mTextPaint;
	
	/**text size, same as ring**/
	private float mTextSize;
	
	/** draw ring or not **/
	public boolean misRing;
	
	/**
	 * Creates a new plot. The dataset is initally set to <code>null<code>
	 * 
	 * @param context
	 */
	public PiePlot(Context context) {
		
		super(context);
		// TODO Auto-generated constructor stub	
		
		mDataset = new PieDataset();

		Log.i(ACTIVITY_TAG, "size of dataset: " + String.valueOf(mDataset.getItemCount()) );
		
		init(context);	
	}
	
	/**
	 * Creates a plot that willl draw a pie chart for the specified dataset.
	 * 
	 * @param context
	 * @param dataset
	 */
	public PiePlot(Context context, PieDataset dataset) {
		
		super(context);
		// TODO Auto-generated constructor stub	
				
		Log.i(ACTIVITY_TAG, "begin  PiePlot(Context context, PieDataset dataset) ");
		
		this.mDataset  = dataset;
		
		misRing = false;
		
		Log.i(ACTIVITY_TAG, "size of dataset: " + String.valueOf(mDataset.getItemCount()) );
		
		init(context);	
	}
	
	/**
	 * Creates a new plot. The dataset is initally set to <code>null<code>
	 * 
	 * @param context
	 * @param attrs
	 */
	public PiePlot(Context context, AttributeSet attrs ) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		
		mDataset = new PieDataset();
		Log.i(ACTIVITY_TAG, "begin  PiePlot(Context context, AttributeSet attrs)");
		
		misRing = false;
		
		init(context);
	}
	
	/**
	 * Sets the dataset.
	 * 
	 * @param dataset
	 */
	public void setDataset(PieDataset dataset){
		this.mDataset = dataset;		
		this.setDefaultColor();
		requestLayout();
		invalidate();
	}
	
	
	/**
	 * draw ring or pie
	 * 
	 * @param isRing
	 */
	public void setisRingChart(boolean isring){
		misRing = isring;
	}
	
	/**
	 * Sets the color for pie chart by default.
	 */
	public void setDefaultColor(){
		ChartColor colors = new ChartColor();
		mColorSet =  new int[] {
				colors.RED,
				colors.SEA_GREEN,
				colors.DODGER_BLUE,
				colors.INDIGO,
				colors.LIGHT_GREY,
				colors.GOLDENOR,
				colors.DARK_ORANGE,
        };
		if (this.mDataset != null){
			this.setDefaultSectionColor();
		}
	}
	
	/**
	 * Returns the set of color
	 * 
	 * @return the set of color
	 */
	public int[] getColors(){
		return mColorSet;
	}
	
	/**
	 * Returns the dataset.
	 * 
	 * @return the dataset
	 */
	public PieDataset getDataset(){
		return this.mDataset;
	}
	
	/**
	 * Sets colors for each section.
	 */
	public void setDefaultSectionColor(){
		
		List keys = this.mDataset.getKeys();
		int i = 0;
		for (Iterator it = keys.iterator(); it.hasNext();){
			String key = (String)it.next();
//			Paint p = new Paint();
//			p.setAntiAlias(true);
			int iColorIndex = i % mColorSet.length;
//			p.setColor(mColorSet[iColorIndex]);
			
			mSectionColorMap.put(key, mColorSet[iColorIndex]);		
			
			i++;
		}
	}

	/**
	 * Returns section-color map.
	 * 
	 * @return section-color map
	 */
	public Map<String, Integer> getSectionColorMap(){
		return this.mSectionColorMap;
	}
	
	/**
	 * Initializes parameters.
	 * 
	 * @param context
	 */
	@SuppressWarnings("deprecation")
	protected void init(Context context){
		
		if (this.mDataset == null){
			Log.i(ACTIVITY_TAG, "NULL Dataset");
		}
		
		this.mSectionAngleMap = new HashMap<String, Double>();
		
		this.mCenterPoint = new Point();
		
		this.mSectionColorMap = new HashMap<String, Integer>();
		
		this.mStartAgnle = DEFAULT_START_ANGLE;
		
		this.mPiePaint = new Paint();
		
		this.mTextPaint = new TextPaint();
	}
	
	
	/**
	 * Calculates the middle angle for each section for label's position
	 */
	private void calSectionAngleMap(){
		
		int angle1 = (int)DEFAULT_START_ANGLE;
		int angle2 = 0;
		double total = DatasetUtilities.calculatePieDatasetTotal(this.mDataset);
		
		List keys = this.mDataset.getKeys();
		Iterator iterator = keys.iterator();
		while(iterator.hasNext()){
			String current = (String) iterator.next();
			double value = this.mDataset.getValue(current);
			if(value > 0.0){
				angle2 = (int)(angle1 + value / total * 360.0);	
				double midAngle = ((double)angle1 + (double)angle2) / 2.0;
				mSectionAngleMap.put(current, midAngle);
			}
			angle1 = angle2;
		}
	}
	
	/**
	 * Returns section-angle map
	 * 
	 * @return The section-angle map
	 */
	public Map<String, Double> getSectionAngleMap(){
		
		calSectionAngleMap();
		
		return mSectionAngleMap;
	}
	
	
	/**
	 * Returns the pie chart diameter.
	 * 
	 * @return The pie chart diameter
	 */
	public double getOuterDiameter(){
		return mOuterDiameter;
	}
	
	/**
	 * Returns the center of the pie chart
	 * 
	 * @return The center of the pie chart
	 */
	public Point getPieCenterPoint(){
		return mCenterPoint;
	}
	
	/**
	 * Initializes the drawing procedure. This method will be called
	 * before the first drawing item.
	 * 
	 * @param plotArea
	 * @param plot
	 * @return A state object (maintains state information relevant
	 * 			one chart drawing)
	 */
	public PiePlotState PieStateInitialize(RectF plotArea, PiePlot plot){
		
		Log.i(ACTIVITY_TAG, "PieStateInitialize data.size: " + String.valueOf(mDataset.getItemCount()));
		
		PiePlotState state = new PiePlotState();
		if (this.mDataset != null){
			state.setTotal(DatasetUtilities.calculatePieDatasetTotal(mDataset));			
		}
		state.setLastestAngle(mStartAgnle);	
		
		Log.i(ACTIVITY_TAG, "PieStateInitialize: mStartAgnle:" + String.valueOf(mStartAgnle));		
		
		return state;		
	}
	
	@Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		Log.d(ACTIVITY_TAG, "onMeasure widthMeasureSpec = "+widthMeasureSpec+" heightMeasureSpec = "+heightMeasureSpec);
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		View parent = (View)getParent();
		if (parent != null){
			Log.i(TAG, "onMeasure parent not null");				
			mParentWidth = parent.getWidth();
			mParentHeight = parent.getHeight();
		}else {
			//XXX parent is null and use display zone as default
			Log.i(TAG, "onMeasure parent is null");		
			DisplayMetrics displayMetrics = mContext.getResources().getDisplayMetrics();
			mParentWidth = displayMetrics.widthPixels;
			mParentHeight = displayMetrics.heightPixels;
		}
		
		Log.i(TAG, "onMeasure parentW: " + mParentWidth + " parentH: " + mParentHeight);
		mPieDiameter = Math.min(mParentWidth, mParentHeight) * (float)DEFAULT_DISPLAY_PERCENT;
		Log.i(TAG, "onMeasure mPieDiameter: " + mPieDiameter);
		mInnerDiameter = mPieDiameter * (float)DEFAULT_INNERDIAM_PERCENTAGE;			
		mOuterDiameter = mPieDiameter + mPieDiameter - mInnerDiameter;
		
		mTextSize = (mPieDiameter - mInnerDiameter)/2;
		
		int centerX = (int)(mParentWidth / 2.0);
		int centerY = (int)(mParentHeight / 2.0);
		mCenterPoint.set(centerX, centerY);
		Log.i(TAG, "onMeasure mCenterPoint X: " + centerX + " Y: " + centerY);
		
		float dLeft = (float) (centerX - mPieDiameter / 2.0);
		float dTop = (float) (centerY - mPieDiameter / 2.0);
		float dRight = (float) (centerX + mPieDiameter / 2.0);
		float dBottom = (float) (centerY + mPieDiameter / 2.0);	
		mPieArea = new RectF(dLeft, dTop, dRight, dBottom);
		
		
		dLeft = (float) (centerX - mInnerDiameter / 2.0);
		dTop = (float) (centerY - mInnerDiameter / 2.0);
		dRight = (float) (centerX + mInnerDiameter / 2.0);
		dBottom = (float) (centerY + mInnerDiameter / 2.0);
		
		mInnerArea = new RectF(dLeft, dTop, dRight, dBottom);

		setMeasuredDimension(mParentWidth, mParentHeight);
    }
	
	@Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
		Log.i(ACTIVITY_TAG, "onLayout changed =" +changed+ " left = " +l+ " top = " +t+ " right = " +r+ " botom = " +b);
		super.onLayout(changed, l, t, r, b);
//		int h = b-t;
//		if (h > 0){
//			int centerY = (int)mCenterPoint.y;
//			Log.i(ACTIVITY_TAG, "onLayout changed =" +changed+ " left = " +l+ " top = " +(centerY - h/2)+ " right = " +r+ " botom = " +(centerY + h/2));
//			super.onLayout(changed, l, centerY - h/2, r, centerY + h/2);
//		}else {
//			super.onLayout(changed, l, t, r, b);			
//		}
    }
	/*
	 * Draws the pie
	 * 
	 * @see android.view.View#onDraw(android.graphics.Canvas)
	 */
	protected void onDraw(Canvas canvas){
		Log.i(TAG, "onDraw mDataset: " + mDataset);
		super.onDraw(canvas);		
			
//		if(mPieArea == null){
//			View parent = (View)getParent();
//			if (parent != null){
//				Log.i(TAG, "onMeasure parent not null");				
//				mParentWidth = parent.getWidth();
//				mParentHeight = parent.getHeight();
//			}else {
//				//XXX parent is null and use display zone as default
//				Log.i(TAG, "onMeasure parent is null");		
//				DisplayMetrics displayMetrics = mContext.getResources().getDisplayMetrics();
//				mParentWidth = displayMetrics.widthPixels;
//				mParentHeight = displayMetrics.heightPixels;
//			}
//			
//			Log.i(TAG, "onMeasure parentW: " + mParentWidth + " parentH: " + mParentHeight);
//			mPieDiameter = Math.min(mParentWidth, mParentHeight) * (float)DEFAULT_DISPLAY_PERCENT;
//			Log.i(TAG, "onMeasure mPieDiameter: " + mPieDiameter);
//			mInnerDiameter = mPieDiameter * (float)DEFAULT_INNERDIAM_PERCENTAGE;			
//			mOuterDiameter = mPieDiameter + mPieDiameter - mInnerDiameter;
//			
//			mTextSize = (mPieDiameter - mInnerDiameter)/2;
//			
//			int centerX = (int)(mParentWidth / 2.0);
//			int centerY = (int)(mParentHeight / 2.0);
//			mCenterPoint.set(centerX, centerY);
//			Log.i(TAG, "onMeasure mCenterPoint X: " + centerX + " Y: " + centerY);
//			
//			float dLeft = (float) (centerX - mPieDiameter / 2.0);
//			float dTop = (float) (centerY - mPieDiameter / 2.0);
//			float dRight = (float) (centerX + mPieDiameter / 2.0);
//			float dBottom = (float) (centerY + mPieDiameter / 2.0);	
//			mPieArea = new RectF(dLeft, dTop, dRight, dBottom);
//			
//			
//			dLeft = (float) (centerX - mInnerDiameter / 2.0);
//			dTop = (float) (centerY - mInnerDiameter / 2.0);
//			dRight = (float) (centerX + mInnerDiameter / 2.0);
//			dBottom = (float) (centerY + mInnerDiameter / 2.0);
//			
//			mInnerArea = new RectF(dLeft, dTop, dRight, dBottom);
//			
//			requestLayout();
//			invalidate();
//			Log.i(TAG, "onDraw return");
//			return;
//		}
//		
		mTextPaint.setTextSize(mTextSize);
		Log.i(ACTIVITY_TAG, "canvas width = " + canvas.getWidth()+ " height = " + canvas.getHeight());
		if ((this.mDataset != null) && (this.mDataset.getKeys().size() >0)){
			
			PiePlotState state = PieStateInitialize(mPieArea, this);
			
			
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
		
			if (misRing){
				mPiePaint.setColor(Color.WHITE);
				canvas.drawArc(mInnerArea, 0, 360, true, mPiePaint);
			}
		}
		else{
			Log.i(ACTIVITY_TAG, "Begin drawNoDataPie");
			drawNoDataPie(canvas);
		}		
	}
	
	/**
	 * Draws a single data item.
	 * 
	 * @param canvas	the canvas.
	 * @param section	the section index.
	 * @param state  	state information for one chart.	
	 * @param item		the item name
	 * @param value		the value for the item
	 */
	protected void drawItem(Canvas canvas, int section, PiePlotState state, String item, double value){
				
		int angle1 = (int)state.getLastAngle();
		int angle2 = (int)(angle1 + value / state.getTotal() * 360.0);		
		int angle = (int)(value / state.getTotal() * 360.0);
		
		double midAngle = ((double)angle1 + (double)angle2) / 2.0;
		mSectionAngleMap.put(item, midAngle);
		
		Log.i(ACTIVITY_TAG, "drawItem angle" + String.valueOf(angle));
		
		mPiePaint.setColor((int)mSectionColorMap.get(item));
		
		canvas.drawArc(mPieArea, angle1, angle, true, mPiePaint);
		
		int percentValue = (int)(value * 100 / state.getTotal());
		Log.i(ACTIVITY_TAG, "value: " + value + " percentValue: " + String.valueOf(percentValue));
		String text = item + ": " + percentValue +"%";
		
		float x0 = (float)mCenterPoint.x;
		float y0 = (float)mCenterPoint.y;
		Log.i(ACTIVITY_TAG, "mCenterPoint: x0=" +(x0)+ " y0=" +(y0));
		
		float xx = (float) (mOuterDiameter / 2.0 * Math.cos(Math.toRadians(midAngle)));
		float yy = (float) (mOuterDiameter / 2.0 * Math.sin(Math.toRadians(midAngle)));
		Log.i(ACTIVITY_TAG, "item: " + item +" xx=" +(xx)+ " yy=" +(yy));

		// the default point is left bottom, so adjustment is needed
		FontMetrics fontMetrics = mTextPaint.getFontMetrics();
		float h = (fontMetrics.bottom - fontMetrics.top)/2;
		if(xx > 0){	
			mTextPaint.setTextAlign(Paint.Align.LEFT);			
		}else {
			mTextPaint.setTextAlign(Paint.Align.RIGHT);
		}
		if (yy > 0){
			yy += h;
		}
		mTextPaint.setColor((int)mSectionColorMap.get(item));
		canvas.drawText(text, x0+xx, y0+yy, mTextPaint);

		state.setLastestAngle(angle2);		
	}

	/**
	 * Draws the empty pie if the dataset is null
	 * 
	 * @param canvas	the canvas
	 */
	protected void drawNoDataPie(Canvas canvas){
		int angle1 = 0;
		int angle = 360;
		Paint paint = new Paint();
		paint.setAntiAlias(true);
		paint.setColor(Color.rgb(208, 208, 208));
		
		canvas.drawArc(mPieArea, angle1, angle, true, paint);
//		canvas.drawRect(0,0,mScreenWidth,mScreenHeight, paint);
//		paint.setColor(Color.RED);
//		canvas.drawRect(mScreenWidth/2 - 50,mScreenHeight/2 -50,mScreenWidth/2 + 50,mScreenHeight/2 + 50, paint);

	}

}


















