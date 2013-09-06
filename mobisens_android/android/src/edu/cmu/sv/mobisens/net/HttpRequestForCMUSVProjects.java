package edu.cmu.sv.mobisens.net;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.util.Hashtable;

import edu.cmu.sv.mobisens.io.FileOperation;
import edu.cmu.sv.mobisens.io.MobiSensLog;

import android.util.Log;

public class HttpRequestForCMUSVProjects {
	
	
	public static final String DEFAULT_UPLOADKEY = "pang.wu@sv.cmu.edu";
	
	
	
	private static final String TAG = "HTTPUploaderForCMUSVProjects";
	
	
	private String projectUploadKey = "";
	private String uploadURL = "";
	private String getProfileURL = "";
	
	public String getUploadKey(){
		return this.projectUploadKey;
	}
	
	public void setUploadKey(String uploadKey){
		this.projectUploadKey = uploadKey;
	}
	
	public String getProfileURL(){
		return this.getProfileURL;
	}
	
	public void setGetProfileURL(String url){
		this.getProfileURL = url;
	}
	
	public String getUploadURL(){
		return this.uploadURL;
	}
	
	public void setUploadURL(String URL){
		this.uploadURL = URL;
	}
	
	protected String getUploadKeyParamName(){
		return "upload_password";
	}
	
	protected String getUploadFileFieldParamName(){
		return "file";
	}
	
	protected String getUploadFileType(){
		return "application/zip";  //"text/csv";
	}
	
	protected String getDeviceIDParamName(){
		return "device_id";
	}
	
	public HttpRequestForCMUSVProjects(){
		setUploadURL(URLs.DEFAULT_UPLOAD_URL);
		setUploadKey(HttpRequestForCMUSVProjects.DEFAULT_UPLOADKEY);
		setGetProfileURL(URLs.DEFAULT_GET_PROFILE_URL);
	}
	
	public HttpRequestForCMUSVProjects(String getProfileURL, String uploadURL, String projectUploadKey){
		setUploadURL(uploadURL);
		setUploadKey(projectUploadKey);
		setGetProfileURL(getProfileURL);
	}
	
	public boolean uploadCSVFilesInDirectory(Hashtable<String, String> additionalParams, String directoryPath, String[] exceptionFiles, boolean deleteAfterUpload){
		File directory = new File(directoryPath);
		if(!directory.isDirectory())
			return false;
		File[] files = directory.listFiles();
		boolean allSucceed = true;
		
		for(File file:files){
			String fileName = file.getName();
			int mid = fileName.lastIndexOf(".");
			
			if(mid == -1)
				continue;
			
			if(exceptionFiles != null){
				boolean isExceptionFile = false;
				for(String exceptionFile:exceptionFiles){
					if(exceptionFile.equals(file.getAbsolutePath())){
						isExceptionFile = true;
						break;
					}
				}
				
				if(isExceptionFile){
					continue;
				}
			}
			
			String fileExtention = fileName.substring(mid + 1, fileName.length());
		    
			if(fileExtention.equalsIgnoreCase("csv")){
				allSucceed |= uploadCSVFile(additionalParams, file.getAbsolutePath(), deleteAfterUpload);
			}
		}
		
		Log.i(TAG, "Upload files in directory: " + directoryPath + ", all done: " + String.valueOf(allSucceed));
		return allSucceed;
	}
	
	public boolean uploadCSVFile(Hashtable<String, String> additionalParams, String filePath, boolean deleteAfterUpload){
		return uploadFile(additionalParams, filePath, deleteAfterUpload);
	}
	
	public String getDeviceProfile(String deviceID){
		if(deviceID.equals(null) || deviceID.equals("")){
			return "";
		}
		
		Hashtable<String, String> params = new Hashtable<String, String>();
		params.put(getDeviceIDParamName(), deviceID);
		HttpGetRequest request = new HttpGetRequest();
		String profileString = request.send(getProfileURL(), params);
		if(profileString.equals("")){
			return "";
		}
		
		return profileString;
	}
	
	public boolean uploadCSVFileList(Hashtable<String, String> additionalParams, String[] files, boolean deleteAfterUpload){
		Hashtable<String, String> additionalParamsClone = new Hashtable<String, String>(additionalParams);
		boolean allSucceed = true;
		
		for(String filePath:files){

			int mid = filePath.lastIndexOf(".");
			
			if(mid == -1)
				continue;
			
			String fileExtention = filePath.substring(mid + 1, filePath.length());
		    
			if(fileExtention.equalsIgnoreCase("csv")){
				allSucceed |= uploadCSVFile(additionalParamsClone, filePath, deleteAfterUpload);
			}
		}
		
		//Log.i(TAG, "Upload files in directory: " + directoryPath + ", all done: " + String.valueOf(allSucceed));
		return allSucceed;
	}
	
	public boolean uploadFile(Hashtable<String, String> additionalParams, String filePath, boolean deleteAfterUpload){
		
		File file = new File(filePath);
		File compressedFile = FileOperation.GzipCompress(file);
		
		try{
			
			if(!file.exists() || file.isDirectory())
				return false;
			
			if(file.length() == 0 && deleteAfterUpload){
				file.delete();
				return true;
			}
			
			
			Hashtable<String, String> params = new Hashtable<String, String>();
			params.put(getUploadKeyParamName(), getUploadKey());
			
			if(!file.equals(compressedFile)){
				// If the compression doesn't fail
				params.put("compressed", "true");
			}
			
			for(String key:additionalParams.keySet()){
				params.put(key, additionalParams.get(key));
			}
			 
			HttpMultipartRequest req = new HttpMultipartRequest(
				getUploadURL(),
				params,
				getUploadFileFieldParamName(), 
				compressedFile
			);
			
			MobiSensLog.log(filePath + " start to upload.");
			byte[] response = req.send();
			if(response == null || response.length == 0)
				return false;
			
			//String responsePageContent = new String(response);
			//Log.i(TAG, responsePageContent);
		}catch(Exception ex){
			return false;
		}
		
		if(!file.equals(compressedFile)){
			// The compressed temp file is in the MobiSens's root path.
			compressedFile.delete();
		}
		
		if(deleteAfterUpload){
			MobiSensLog.log(filePath + " deleted.");
			file.delete();
		}
		
		return true;
	}
}
