package org.mobisens.chartview.chart;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.mobisens.chartview.chart.labels.PieSectionLabelGenerator;
import org.mobisens.chartview.chart.plot.PiePlot;
import org.mobisens.chartview.data.general.PieDataset;

import android.content.Context;
import android.graphics.Point;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 *A plot that displays data in the form of pie chart 
 *
 */
public class PieChart extends ViewGroup {
	
	private static final String ACTIVITY_TAG = "Log_PieChart";
	
	/** The dataset. */
	protected PieDataset mDataset;
	
	/** The section-color map */
	private Map<String, Integer> mSectionColorMap;
	
	/** The pie plot.  */
	private PiePlot mPieplot;
	
	/** The section label geneterot. */
	private PieSectionLabelGenerator mLabelGenerator;	
	
	/** The context */
	private Context mContext;
	
	/** The section-position map */ 
	private Map<String, Point> mSectionPosMap;
	
	/** the item list for the label's position */
	private List<String> mItemList = new ArrayList<String>();

	/**
	 * Creates a new chart. The dataset is initally set to <code>null<code>
	 * 
	 * @param context
	 */
	public PieChart(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		
		mPieplot = new PiePlot(context);
		
		mContext = context;
		
		Log.i(ACTIVITY_TAG, "begin PieChart(Context context)");
		
		this.initComponents(context);
	}
	
	/**
	 * Creates a new chart.
	 * 
	 * @param context
	 * @param attrs
	 */
	public PieChart(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		
		mPieplot = new PiePlot(context);
		
		mContext = context;
		
		Log.i(ACTIVITY_TAG, "begin PieChart(Context context, AttributeSet attrs)");
		
		this.initComponents(context);	

	}
	
	
	/**
	 * Initializes each components for the chart: pie, label, etc.
	 * 
	 * @param context
	 */
	private void initComponents(Context context) {
		// TODO Auto-generated method stub
		
		Log.i(ACTIVITY_TAG, "begin initComponents(Context context)");
		
		mItemList = new ArrayList<String>();
		
		mPieplot = new PiePlot(context);
		
		mSectionColorMap = new HashMap<String, Integer>();
	
		mLabelGenerator = new PieSectionLabelGenerator();
		
		mSectionPosMap = new HashMap<String, Point>();
		
		Log.i(ACTIVITY_TAG, "begin addView(pieplot)");
		addView(mPieplot);
	}
	
	
	/**
	 * Sets the dataset.
	 * 
	 * @param dataset
	 */
	public void setDataset(PieDataset dataset){
		this.mDataset = dataset;	
		if (mPieplot != null){
			mPieplot.setDataset(dataset);
			mSectionColorMap = mPieplot.getSectionColorMap();
			mLabelGenerator.setPieSectionLabelColor(mSectionColorMap);
		}		
		
	}
	
	/**
	 * Sets the lable for each item.
	 */
	private void setLabelView(){
		mItemList.clear();
		removeAllViews();
		addView(mPieplot);
		List keys = this.mDataset.getKeys();
		for (Iterator it = keys.iterator(); it.hasNext();){
			String key = (String)it.next();
			TextView tv = mLabelGenerator.generateSectionLabel(this.mContext, this.mDataset, key);
			Log.i(ACTIVITY_TAG, "TextView: " + tv.getText());
			this.addView(tv);	
			mItemList.add(key);
		}
		
		Map<String, Double> sectionAngleMap = mPieplot.getSectionAngleMap();
		Point centerPoint = mPieplot.getPieCenterPoint();
		double outerDiameter = mPieplot.getOuterDiameter();
		
		Log.d(ACTIVITY_TAG, "setLabelView: size of sectionAngleMap = " + sectionAngleMap.size());
		mSectionPosMap = getLabelPosMap(sectionAngleMap, centerPoint, outerDiameter);		
	}
	
	/**
	 * Returns the label associtated with the specified key.
	 * 
	 * @param sectionAngleMap
	 * @param centerPoint
	 * @param outerDiameter
	 * @return
	 */
	private Map<String, Point> getLabelPosMap(Map<String, Double> sectionAngleMap, 
												Point centerPoint, double outerDiameter){
		
		Map<String, Point> sectionPosMap = new HashMap<String, Point>();		
	
		double surroundDiameter = outerDiameter + 350;
		
		double x0 = (double)centerPoint.x;
		double y0 = (double)centerPoint.y;
		
		Set<String> keys =sectionAngleMap.keySet();
		Log.i(ACTIVITY_TAG, "getLabelPosMap size of keys" + keys.size());
		Log.i(ACTIVITY_TAG, "center: x=" +(x0)+ "y=" +(y0));
		for (Iterator it = keys.iterator(); it.hasNext();){
			String item = (String)it.next();
			
			Log.i(ACTIVITY_TAG, "getLabelPosMap it" + item);
			
			double angle = sectionAngleMap.get(item);			
			double xx = surroundDiameter / 2.0 * Math.cos(Math.toRadians(angle));
			double yy = surroundDiameter / 2.0 * Math.sin(Math.toRadians(angle));
			
			Log.i(ACTIVITY_TAG, "getLabelPosMap it" + item + " angle: " + angle);
			
			Point pos = new Point();
			pos.set((int)(x0+xx), (int)(y0+yy));
			
			Log.i(ACTIVITY_TAG, "offset: xx=" +(xx)+ "yy=" +(yy));
			Log.i(ACTIVITY_TAG, "pos: x=" +(x0+xx)+ "y=" +(y0+yy));
			
			sectionPosMap.put(item, pos);			
		}
		
		return sectionPosMap;
	}
	
	/* 
	 * Customize the onMeasure
	 * 
	 * @see android.view.View#onMeasure(int, int)
	 */
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec){
		Log.d(ACTIVITY_TAG, "widthMeasureSpec = "+widthMeasureSpec+" heightMeasureSpec = "+heightMeasureSpec);
		
		for (int index = 0; index < getChildCount(); index++){
			final View child = getChildAt(index);
			
			//measure
			child.measure(MeasureSpec.UNSPECIFIED, MeasureSpec.UNSPECIFIED);
		}
		
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);		
	}
	

	/* 
	 * Layout for each component.
	 * 
	 * @see android.view.ViewGroup#onLayout(boolean, int, int, int, int)
	 */
	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		// TODO Auto-generated method stub			
		
		Log.d(ACTIVITY_TAG, "changed =" +changed+ " left = " +l+ " top = " +t+ " right = " +r+ " botom = " +b);
		if (this.mDataset != null){
			setLabelView();
		}
		
		int childCount = getChildCount();
		Point centerPoint = mPieplot.getPieCenterPoint();
		int centerX = centerPoint.x; 
		int centerY = centerPoint.y; 
		
		Log.d(ACTIVITY_TAG, "onLayout childCount = " + childCount);
		
		for(int i = 0; i < childCount; i++){
			View child = getChildAt(i);
			int width = child.getMeasuredWidth();
			int height = child.getMeasuredHeight();
			
			Log.d(ACTIVITY_TAG, "child_width = " +width+ " child_height = " +height);			
			
//			child.measure(r-l, b-t);
//			child.layout(0, 0, 1000, 1000);

			if (i > 0){
				Log.d(ACTIVITY_TAG, "size of mItemList: " + mItemList.size());
				Log.d(ACTIVITY_TAG, "size of mSectionPosMap: " + mSectionPosMap.size());
				
				Point pos = new Point();
				String  item = mItemList.get(i-1);				
				Log.d(ACTIVITY_TAG, "item: " + item);
				
				pos = (Point)mSectionPosMap.get(item);
				int x = pos.x;
				int y = pos.y; 
				
//				if (x < centerX && width > 0){
//					x -= width;
//				}
//				
//				if (y < centerY && height > 0){
//					y -= height;
//				}
				
				Log.i(ACTIVITY_TAG, "pos1 : x=" +x+ " y = " +y);
				
				
				child.layout(x, y, x+150, y+100);
			}
			else{
				child.layout(l, t, r, b);
			}
			
			
			Log.d("RingChart child.getMeasuredWidth and Height", 
					String.valueOf(child.getMeasuredWidth()) +"," +  String.valueOf(child.getMeasuredHeight()));
		}	

	}

}
















