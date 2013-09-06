package edu.cmu.sv.mobisens.net;

import java.io.File;
import java.util.Hashtable;

import edu.cmu.sv.mobisens.io.FileOperation;

public class FileToUpload {
	private String path = "";
	private String deviceID = "";
	
	private String fileType = "";
	private boolean deleteAfterUpload = false;
	private boolean tellAfterFinsished = false;
	
	protected FileToUpload(){
		
	}
	
	public FileToUpload(String path, String deviceID, String fileType, boolean deleteAfterUpload){
		this.setDeviceID(deviceID);
		this.setFileType(fileType);
		this.setPath(path);
		this.setDeleteAfterUpload(deleteAfterUpload);
	}
	
	public void deleteFile(){
		if(this.getPath().equals(""))
			return;
		FileOperation.deleteFile(this.getPath());
	}
	
	public boolean upload(HttpRequestForCMUSVProjects request){
		Hashtable<String, String> additionalParams = new Hashtable<String, String>();
	    additionalParams.put("device_id", this.getDeviceID());
	    additionalParams.put("file_type", this.getFileType());
	    
	    return request.uploadCSVFile(additionalParams, getPath(), this.isDeleteAfterUpload());
	}

	private void setPath(String path) {
		this.path = path;
	}

	public String getPath() {
		return path;
	}

	private void setDeviceID(String deviceID) {
		this.deviceID = deviceID;
	}

	public String getDeviceID() {
		return deviceID;
	}

	private void setFileType(String fileType) {
		this.fileType = fileType;
	}

	public String getFileType() {
		return fileType;
	}

	private void setDeleteAfterUpload(boolean deleteAfterUpload) {
		this.deleteAfterUpload = deleteAfterUpload;
	}

	public boolean isDeleteAfterUpload() {
		return deleteAfterUpload;
	}

	public void setTellAfterFinsished(boolean tellAfterFinsished) {
		this.tellAfterFinsished = tellAfterFinsished;
	}

	public boolean needTellAfterFinsished() {
		return tellAfterFinsished;
	}
	
	public boolean exist(){
		File file = new File(this.getPath());
		return file.exists();
	}
	
}
