package edu.cmu.sv.mobisens.util;

import java.util.ArrayList;
import java.util.Date;

public class MachineAnnotation extends Annotation {

	private double similarity = 0;
	private String[] alternativeNames = new String[0];
	
	public MachineAnnotation(String name, long start, long end, double similarity) {
		super(name.split("\\|")[0], new Date(start), new Date(end));
		// TODO Auto-generated constructor stub
		this.setSimilarity(similarity);
		String[] names = name.split("\\|");
		if(names.length > 1){
			this.setAlternativeNames(names);
		}
	}
	

	public void setSimilarity(double similarity) {
		this.similarity = similarity;
	}

	public double getSimilarity() {
		return similarity;
	}
	
	public boolean hasAlternativeNames(){
		return !(this.alternativeNames == null || this.alternativeNames.length == 0);
	}
	
	protected String getAnnotationPartString(){
		if(!this.isValid())
			return "";

		long startTimestamp = this.getStart().getTime();
		long endTimestamp = this.getEnd().getTime();
		StringBuilder buffer = new StringBuilder(1024);
		buffer.append(startTimestamp).append(",")
			.append(endTimestamp).append(",")
			.append(this.getEscapedName()).append(",")
			.append(true).append(",")
			.append(this.getSimilarity());
		
		return buffer.toString();
		
	}

	public void setAlternativeNames(String[] alternativeNames) {
		
		if(alternativeNames == null)
			return;
		
		if(alternativeNames.length == 0)
			return;
		
		ArrayList<String> names = new ArrayList<String>(alternativeNames.length);
		for(String name:alternativeNames){
			if(name == null)
				continue;
			
			if(name.equals(getName()) == false && names.indexOf(name) == -1){
				names.add(name);
			}
		}
		this.alternativeNames = new String[names.size()];
		this.alternativeNames = names.toArray(this.alternativeNames);
	}
	
	public void appendAlternativeNames(String[] alternativeNames) {
		if(!this.hasAlternativeNames()){
			this.setAlternativeNames(alternativeNames);
			return;
		}
		
		ArrayList<String> names = new ArrayList<String>(alternativeNames.length);
		for(String name:alternativeNames){
			if(name.equals(getName()) == false && names.indexOf(name) == -1){
				names.add(name);
			}
		}
		

		for(String name:this.alternativeNames){
			if(names.indexOf(name) == -1){
				names.add(name);
			}
		}
		
		this.alternativeNames = new String[names.size()];
		this.alternativeNames = names.toArray(this.alternativeNames);
	}

	public String[] getAlternativeNames() {
		String[] copy = new String[this.alternativeNames.length];
		
		for(int i=0; i<this.alternativeNames.length; i++){
			copy[i] = new String(this.alternativeNames[i]);
		}
		return copy;
	}
	
	public void swapAlternativeToMainName(int alterNameIndex){
		if(this.hasAlternativeNames()){
			String tmp = getName();
			super.setName(this.alternativeNames[alterNameIndex]);
			this.alternativeNames[alterNameIndex] = tmp;
		}
	}
	
	public void setName(String newName){
		if(this.hasAlternativeNames()){
			String[] alterNames = this.getAlternativeNames();
			for(int i = 0; i<alterNames.length; i++){
				if(alterNames[i].equals(newName)){
					this.swapAlternativeToMainName(i);
					return;
				}
			}
		}
		
		super.setName(newName);
	}

}
