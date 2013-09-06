package edu.cmu.sv.mobisens.ui;

import edu.cmu.sv.mobisens.util.Annotation;
import android.graphics.Color;
import android.text.format.DateFormat;

public class HeaderListViewItem {
	private Annotation anno;
	private String description = "";

	private int color = Color.BLACK;
	
	private HeaderListViewItem(){}
	
	public HeaderListViewItem(Annotation anno, int color){
		this.anno = anno;
		
		generateDescription(this.anno);
		this.setColor(color);
	}
	
	private void generateDescription(Annotation anno){
		String from = DateFormat.format("MMM dd, h:mmaa", anno.getStart()).toString();
        String to = DateFormat.format("MMM dd, h:mmaa", anno.getEnd()).toString();
        
		this.setDescription(from + " - " + to);
	}
	
	public void setAnnotation(Annotation newAnno){
		this.anno = newAnno;
		generateDescription(this.anno);
	}
	
	public Annotation getAnnotation(){
		return this.anno;
	}

	public String getTitle() {
		return anno.getName();
	}
	
	public String getSubTitle(){
		return anno.getActivityName();
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getDescription() {
		return description;
	}


	public long getStartTime() {
		return anno.getStart().getTime();
	}


	public long getEndTime() {
		return anno.getEnd().getTime();
	}

	public void setColor(int color) {
		this.color = color;
	}

	public int getColor() {
		return color;
	}
}
