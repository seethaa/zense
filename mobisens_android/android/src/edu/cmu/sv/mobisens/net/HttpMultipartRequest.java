package edu.cmu.sv.mobisens.net;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.Hashtable;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import android.util.Log;

 
public class HttpMultipartRequest
{
	private static final String TAG = "HttpMultipartRequest";

	private MultipartEntity requestEntity = null;
	String url = null;
 
	public HttpMultipartRequest(String url, Hashtable<String, String> params, String fileField, File file)
	{
		this.url = url;

		this.requestEntity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);
		FileBody bin = new FileBody(file);
		this.requestEntity.addPart(fileField, bin);
		
		for(String key:params.keySet()){
			try {
				this.requestEntity.addPart(key, new StringBody(params.get(key)));
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

 
	public byte[] send() throws Exception
	{
		byte[] returnValue = null;
		
		HttpClient httpclient = new DefaultHttpClient();
        try {
            HttpPost httppost = new HttpPost(this.url);

            httppost.setEntity(this.requestEntity);

            //Log.i(TAG, "executing request " + httppost.getRequestLine());
            HttpResponse response = httpclient.execute(httppost);
            HttpEntity resEntity = response.getEntity();

            Log.i(TAG, response.getStatusLine().toString());
            if (resEntity != null && response.getStatusLine().getStatusCode() == 200) {
            	
            	int ch;
            	 
    			InputStream is = resEntity.getContent();
    			ByteArrayOutputStream bos = new ByteArrayOutputStream();
    			
    			while ((ch = is.read()) != -1)
    			{
    				bos.write(ch);
    			}
    			
    			returnValue = bos.toByteArray();
            }
        } finally {
            try { 
            	httpclient.getConnectionManager().shutdown(); 
        	} catch (Exception ignore) {
        		
        	}
        }
        
        return returnValue;
	}
}