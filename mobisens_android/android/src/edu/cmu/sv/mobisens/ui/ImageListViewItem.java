package edu.cmu.sv.mobisens.ui;

import android.graphics.Color;
import android.view.View;

public class ImageListViewItem {
	private String title = "";
	private String description = "";
	private int iconResourceId = 0;
	
	private long startTime = 0;
	private long endTime = 0;
	private int color = Color.BLACK;
	
	public ImageListViewItem(String title, String description, int iconResource){
		this.setTitle(title);
		this.setDescription(description);
		this.setIconResourceId(iconResource);
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getTitle() {
		return title;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getDescription() {
		return description;
	}

	public void setIconResourceId(int iconResourceId) {
		this.iconResourceId = iconResourceId;
	}

	public int getIconResourceId() {
		return iconResourceId;
	}

	public void setStartTime(long startTime) {
		this.startTime = startTime;
	}

	public long getStartTime() {
		return startTime;
	}

	public void setEndTime(long endTime) {
		this.endTime = endTime;
	}

	public long getEndTime() {
		return endTime;
	}

	public void setColor(int color) {
		this.color = color;
	}

	public int getColor() {
		return color;
	}
	
	public boolean isOwnerDraw(){
		return false;
	}
	
	public void draw(View self, View parent){
		
	}
}
