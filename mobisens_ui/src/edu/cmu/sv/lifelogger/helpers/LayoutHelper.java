package edu.cmu.sv.lifelogger.helpers;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

import android.content.Context;
import android.widget.LinearLayout;

/**
 * This abstract class is meant to characterize specific layout pieces to be added to a
 * main layout.
 * -- not for use now.
 *
 */
public abstract class LayoutHelper  {

	private String mTitle;
	private LinearLayout mainlayout;
	private int view_id;
	protected Context cxt;
	private String mResult; 
	
	public LayoutHelper(Context cxt, String text, LinearLayout mainlayout){
		this.cxt = cxt;
		this.mTitle = text;
		this.mainlayout = mainlayout;
		this.view_id = new AtomicInteger().incrementAndGet();//currently not used
	}

	public int get_ParentView_ID(){
		return this.view_id;
	}

	public String getmTitle(){
		return this.mTitle;
	}

	public abstract ArrayList<String> getmResult();





}
