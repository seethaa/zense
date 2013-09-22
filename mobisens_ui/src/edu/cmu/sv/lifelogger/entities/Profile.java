package edu.cmu.sv.lifelogger.entities;

/**
 * List of Profile items based on Screen P1:Profile Details
 * TODO: --Add items as necessary, and incorporate with FB User profile
 * @author seetha
 *
 */
public class Profile {
	String mName;
	String mEmailAddress;
	int mNumPhotos;
	int mNumLifePatters;
	int mNumLocations;
	String mStatus; //should this be same as FB status?
	
	public String getmName() {
		return mName;
	}


	public void setmName(String mName) {
		this.mName = mName;
	}


	public String getmEmailAddress() {
		return mEmailAddress;
	}


	public void setmEmailAddress(String mEmailAddress) {
		this.mEmailAddress = mEmailAddress;
	}


	public int getmNumPhotos() {
		return mNumPhotos;
	}


	public void setmNumPhotos(int mNumPhotos) {
		this.mNumPhotos = mNumPhotos;
	}


	public int getmNumLifePatters() {
		return mNumLifePatters;
	}


	public void setmNumLifePatters(int mNumLifePatters) {
		this.mNumLifePatters = mNumLifePatters;
	}


	public int getmNumLocations() {
		return mNumLocations;
	}


	public void setmNumLocations(int mNumLocations) {
		this.mNumLocations = mNumLocations;
	}


	public String getmStatus() {
		return mStatus;
	}


	public void setmStatus(String mStatus) {
		this.mStatus = mStatus;
	}



	
	
	//TODO: Empty constructor for now. fill in when implementing P1-P2
	public Profile(){
		
	}

}
