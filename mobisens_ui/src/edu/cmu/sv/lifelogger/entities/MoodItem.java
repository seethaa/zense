package edu.cmu.sv.lifelogger.entities;

import edu.cmu.sv.lifelogger.helpers.DefinitionHelper;

public class MoodItem {

	


	private int mMood;
	private int mTime;


	public MoodItem(int mood, int time) {
		this.mMood = mood;
		this.mTime = time;
	}


	public int getmMood() {
		return mMood;
	}


	public void setmMood(int mMood) {		
		this.mMood = mMood;
	}


	public int getmTime() {
		return mTime;
	}


	public void setmTime(int mTime) {
		this.mTime = mTime;
	}
}
