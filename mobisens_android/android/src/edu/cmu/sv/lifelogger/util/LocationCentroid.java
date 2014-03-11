package edu.cmu.sv.lifelogger.util;

public class LocationCentroid {
	private boolean isLoadFromFile = false;
	private double latitude = 0.0;
	private double longitude = 0.0;
	private int index = -1;
	
	
	public void setLoadFromFile(boolean isLoadFromFile) {
		this.isLoadFromFile = isLoadFromFile;
	}
	
	public boolean isLoadFromFile() {
		return isLoadFromFile;
	}

	protected void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	public double getLatitude() {
		return latitude;
	}

	protected void setLongitude(double longitude) {
		this.longitude = longitude;
	}

	public double getLongitude() {
		return longitude;
	}

	protected void setIndex(int index) {
		this.index = index;
	}

	public int getIndex() {
		return index;
	}
	
	
	public LocationCentroid(double lat, double lng){
		this.setLatitude(lat);
		this.setLongitude(lng);
		this.setLoadFromFile(false);
	}
	
	public LocationCentroid(String line){
		String[] columns = line.split(",");
		if(columns.length >= 3){
			this.setIndex(Integer.valueOf(columns[0]));
			this.setLatitude(Double.valueOf(columns[1]));
			this.setLongitude(Double.valueOf(columns[2]));
			this.setLoadFromFile(true);
		}
	}
	
	public String toString(){
		StringBuilder builder = new StringBuilder(100);
		builder.append(this.getIndex()).append(",").append(this.getLatitude())
			.append(",").append(this.getLongitude());
		return builder.toString();
	}
	
	
}
