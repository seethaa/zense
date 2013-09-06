package edu.cmu.sv.lifelogger.util;

import java.util.Random;

import android.graphics.Color;

public class RandomColorGenerator {
	public static int getRandomColor(){
		Random color = new Random();
		int randomColor = Color.argb(255, color.nextInt(256),color.nextInt(256),color.nextInt(256));
		
		return randomColor;
	}
}
