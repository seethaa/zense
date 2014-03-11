package edu.cmu.sv.mobisens.util;

import java.util.ArrayList;

public class ArrayConverter {
	public static ArrayList<Integer> toIntegerArrayList(int[] array){
		ArrayList<Integer> result = new ArrayList<Integer>(array.length);
		for(int value:array){
			result.add(Integer.valueOf(value));
		}
		
		return result;
	}
}
