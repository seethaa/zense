package edu.cmu.sv.mobisens.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Hashtable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import edu.cmu.sv.mobisens.net.HttpGetRequest;
import edu.cmu.sv.mobisens.net.HttpRequestForCMUSVProjects;
import edu.cmu.sv.mobisens.net.URLs;

public class ServerMessage {
	private int id = 0;
	private String title;
	private String url;
	private boolean read = false;
	private Date createdAt;
	
	public interface AsyncCallbackHandler{
		void onComplete(ServerMessage sender);
	};
	
	private ServerMessage(){
		
	}
	
	public ServerMessage(int id, String title, String URL, boolean read, Date createdAt){
		this.id = id;
		this.title = title;
		this.url = URL;
		this.read = read;
		this.createdAt = createdAt;
	}
	
	public int getId(){
		return this.id;
	}
	
	public String getTitle(){
		return this.title;
	}
	
	public String getMessageURL(){
		return this.url;
	}
	
	public boolean isRead(){
		return this.read;
	}
	
	public Date getCreatedDate(){
		return this.createdAt;
	}
	
	public void setAsReadAsync(final AsyncCallbackHandler callBack){
		
		Thread workingThread = new Thread(){
			public void run(){
				HttpGetRequest request = new HttpGetRequest();
				Hashtable<String, String> params = new Hashtable<String, String>();
				params.put("key", HttpRequestForCMUSVProjects.DEFAULT_UPLOADKEY);
				params.put("id", String.valueOf(ServerMessage.this.getId()));
				request.send(URLs.SET_MESSAGES_AS_READ_URL, params);
				callBack.onComplete(ServerMessage.this);
			}
		};
		
		workingThread.run();
		
	}
	
	public static ServerMessage[] getMessagesFromServer(String deviceId){
		Hashtable<String, String> params = new Hashtable<String, String>();
		params.put("key", HttpRequestForCMUSVProjects.DEFAULT_UPLOADKEY);
		params.put("device_id", deviceId);
		
		HttpGetRequest request = new HttpGetRequest();
		
		String response = request.send(URLs.GET_SERVER_MESSAGES_URL, params);
	
		if(response.equals("")){
			return new ServerMessage[0];
		}else{
			return parseMessageJSON(response);
		}
		
	}
	
	private static ServerMessage[] parseMessageJSON(String response){
		try {
			JSONArray jsonArray = new JSONArray(response);
			ServerMessage[] messages = new ServerMessage[jsonArray.length()];
			for(int i = 0; i < messages.length; i++){
				JSONObject profileJSON = jsonArray.getJSONObject(i);
				//JSONObject profileJSON = jsonObject.getJSONObject("message");
				String URL = profileJSON.getString("url");
				String title = profileJSON.getString("title");
				int id = profileJSON.getInt("id");
				boolean read = profileJSON.getBoolean("read");
				String createDateString = profileJSON.getString("created_at");
				SimpleDateFormat  format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");  
				try {  
				    Date date = format.parse(createDateString);  
				    messages[i] = new ServerMessage(id, title, URL, read, date);
				     
				} catch (Exception e) {  
				    // TODO Auto-generated catch block  
				    e.printStackTrace();  
				}
				
				
				
			}
			
			return messages;
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return new ServerMessage[0];
	}

}
