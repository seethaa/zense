package edu.cmu.sv.mobisens.util;

import java.util.Hashtable;

import org.json.JSONObject;

import edu.cmu.sv.mobisens.net.HttpGetRequest;
import edu.cmu.sv.mobisens.net.HttpPostRequest;
import edu.cmu.sv.mobisens.net.HttpRequestForCMUSVProjects;
import edu.cmu.sv.mobisens.net.URLs;

import android.util.Log;

public class Address {
	private final static String TAG = "Address";
	
	private String street = "";
	private String city = "";
	private String state = "";
	private String zipcode = "";
	private String email = "";
	private String country = "";
	
	
	public Address(String street, String city, String state, String country, String zipcode, String email){
		this.setStreet(street);
		this.setCity(city);
		this.setState(state);
		this.setZipcode(zipcode);
		this.setEmail(email);
		this.setCountry(country);
	}

	public Address() {
		// TODO Auto-generated constructor stub
	}

	public void setStreet(String street) {
		this.street = street;
	}

	public String getStreet() {
		return street;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getCity() {
		return city;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getState() {
		return state;
	}

	public void setZipcode(String zipcode) {
		this.zipcode = zipcode;
	}

	public String getZipcode() {
		return zipcode;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getEmail() {
		return email;
	}
	
	public void setCountry(String country) {
		this.country = country;
	}

	public String getCountry() {
		return country;
	}

	public boolean upload(String deviceId){
		HttpPostRequest post = new HttpPostRequest();
		Hashtable<String, String> params = new Hashtable<String,String>();
		params.put("street", this.getStreet());
		params.put("city", this.getCity());
		params.put("state", this.getState());
		params.put("country", this.getCountry());
		params.put("zipcode", this.getZipcode());
		params.put("email", this.getEmail());
		params.put("device_id", deviceId);
		params.put("upload_key", HttpRequestForCMUSVProjects.DEFAULT_UPLOADKEY);
		
		String result = post.send(URLs.ADDRESS_UPLOAD_URL, params);
		
		if(result.equals("")){
			return true;
		}
		
		return false;
	}
	
	public static Address download(String deviceId){
		HttpGetRequest httpGet = new HttpGetRequest();
		Hashtable<String, String> params = new Hashtable<String,String>();
		params.put("upload_key", HttpRequestForCMUSVProjects.DEFAULT_UPLOADKEY);
		params.put("device_id", deviceId);
		
		String result = httpGet.send(URLs.ADDRESS_DOWNLOAD_URL, params);
		try{
			JSONObject addressJSON = new JSONObject(result).getJSONObject("address");
			return new Address(addressJSON.getString("street"), 
					addressJSON.getString("city"), 
					addressJSON.getString("state"), 
					addressJSON.getString("country"), 
					addressJSON.getString("zipcode"), 
					addressJSON.getString("email"));
			
		}catch(Exception ex){
			Log.e(TAG, ex.getMessage(), ex);
		}
		
		
		return null;
	}
	

}
