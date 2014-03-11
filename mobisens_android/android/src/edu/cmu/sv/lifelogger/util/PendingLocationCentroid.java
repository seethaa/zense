package edu.cmu.sv.lifelogger.util;

public class PendingLocationCentroid extends LocationCentroid {

	private int memberCount = 1;
	
	public PendingLocationCentroid(double lat, double lng) {
		super(lat, lng);
		// TODO Auto-generated constructor stub
	}

	public void setMemberCount(int memberCount) {
		this.memberCount = memberCount;
	}

	public int getMemberCount() {
		return memberCount;
	}

}
