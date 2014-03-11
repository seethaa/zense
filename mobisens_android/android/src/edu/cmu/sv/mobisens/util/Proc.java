package edu.cmu.sv.mobisens.util;

import java.io.File;
import java.io.InputStream;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;
import java.lang.ProcessBuilder;
import android.content.Context;
import android.util.Log;
import org.json.JSONObject;
import org.json.JSONException;

import edu.cmu.sv.mobisens.net.Network;

/**
 * Reads varios information from the /proc file system. 
 *
 * After an object of this class is constructed, each call to
 * get*() methods returns a HashMap containing some information from
 * the /proc of the Linux kernel.
 * 
 */
public class Proc
{
    /** TAG of this class for logging */
    private static final String TAG="SystemSens:Proc";


    /** Text string for the cat command */
    private static final String CAT_CMD = "/system/bin/cat";

    /** Address of the network devices stat */
    private static final String NETDEV_PATH  = "/proc/net/dev";
    
    private Context context;


    /** File handler for network device stats*/
    File mNetDev;


 
    /**
     * Constructs a Proc object. 
     */
    public Proc(Context context)
    {
    	this.context = context;
        mNetDev = new File(NETDEV_PATH);
    }


    /**
     * Parses and returns the contents of /proc/net/dev.
     * It first reads the content of the file in /proc/net/dev. 
     * This file contains a row for each network interface. 
     * Each row contains the number of bytes and packets that have
     * been sent and received over that network interface. This method
     * parses this file and returns a JSONObject that maps the network
     * interface name to this information.
     *
     * @return          JSONObject containing en entry for each
     *                      physical interface. 
     */
    public JSONObject getNetDev()
    {
        // Debug
        //Log.i(TAG, "getting netdev info");

        JSONObject result = new JSONObject();
        JSONObject data, recvObject, sentObject;
        StringTokenizer linest;
        String devName, recvBytes, recvPackets, 
               sentBytes, sentPackets, zero;

        String[] args = {CAT_CMD, NETDEV_PATH};
        ProcessBuilder cmd;

        try
        {
           
            cmd = new ProcessBuilder(args);
      
            Process process = cmd.start();
            InputStream devstream = process.getInputStream();


//            FileInputStream devstream = new FileInputStream(mNetDev);
            int flen = devstream.available();
            // Debug
            //Log.i(TAG, "Available size: " + flen);

            StringBuilder sysOutput = new StringBuilder();
            String tmpString = "";
            
            byte[] buffer = new byte[2024];
            
            int readlen = 0;
			while((readlen  = devstream.read(buffer)) != -1)
            {
            	tmpString = new String(buffer);
            	buffer = new byte[2024];
            	sysOutput.append(tmpString);
            	Log.i(TAG, "Read " + readlen + " bytes.");
            }
            
            //Log.i(TAG, sysOutput.toString());
            
            StringTokenizer st = new StringTokenizer(
            		sysOutput.toString(), "\n", false);

            //The first two lines of the file are headers
            zero = st.nextToken();
            zero = st.nextToken();

            try
            {
            	while(true)
                {
                    // Debug
                    //Log.i(TAG, "getting a new line");
                    linest = new StringTokenizer(st.nextToken());
                    devName = linest.nextToken();
                    recvBytes = linest.nextToken();
                    recvPackets = linest.nextToken();


                    // Debug
                    //Log.i(TAG, "=======================");
                    //Log.i(TAG, "interface: " + devName);
                    //Log.i(TAG, "recvBytes: " + recvBytes);
                    //Log.i(TAG, "recvPackets: " + recvPackets);

                    // Skip six tokens
                    for (int i = 0; i < 6; i++) 
                        zero = linest.nextToken();

                    sentBytes = linest.nextToken();
                    sentPackets = linest.nextToken();

                    // Debug
                    //Log.i(TAG, "sentBytes: " + sentBytes);
                    //Log.i(TAG, "sentPackets: " + sentPackets);



                    // Pack the results 
                    sentObject = new JSONObject();
                    recvObject = new JSONObject();
                    data = new JSONObject();

                    try
                    {
                        recvObject.put("bytes", recvBytes);
                        recvObject.put("packets", recvPackets);

                        sentObject.put("bytes", sentBytes);
                        sentObject.put("packets", sentPackets);

                        data.put("sent", sentObject);
                        data.put("recv", recvObject);
                        
                        result.put(devName, data);

                    }
                    catch (JSONException je)
                    {
                        Log.e(TAG, "Exception", je);
                    }

                }
            }catch(NoSuchElementException ex){
            	Log.i(TAG, "Read to the end.");
            }

        }
        catch (Exception e)
        {

            Log.e(TAG, "Exception", e);
        }


        // Debug
        //Log.i(TAG, "returning: " + result.toString());

        return result;
    }

    public String getNetDevCSVString()
    {
        // Debug
        //Log.i(TAG, "getting netdev info");

    	
        StringBuilder result = new StringBuilder();
        StringBuilder data;
        StringTokenizer linest;
        String devName, recvBytes, recvPackets, 
               sentBytes, sentPackets;

        String[] args = {CAT_CMD, NETDEV_PATH};
        ProcessBuilder cmd;

        try
        {
            cmd = new ProcessBuilder(args);
      
            Process process = cmd.start();
            InputStream devstream = process.getInputStream();

            StringBuilder sysOutput = new StringBuilder();
            String tmpString = "";
            byte[] buffer = new byte[2024];

			while(devstream.read(buffer) != -1)
            {
            	tmpString = new String(buffer);
            	buffer = new byte[2024];
            	sysOutput.append(tmpString);
            }
            
            //Log.i(TAG, sysOutput.toString());
            
            StringTokenizer st = new StringTokenizer(
            		sysOutput.toString(), "\n", false);
            try{
            	for(int skipHeaderLineCount = 0; skipHeaderLineCount < 2; skipHeaderLineCount++){
            		st.nextToken();
            	}
            	
            	boolean isFirstInterface = true;
            	
	            while (true)
	            {
            		linest = new StringTokenizer(st.nextToken(), " :");  // Both space and ":" are delimiters.
                    devName = linest.nextToken();
                    
                    String wifiInterfaceName = Network.getWifiInterfaceName(context);
                    
                    if(!devName.equals(wifiInterfaceName))
                    	continue;
                    
                    recvBytes = linest.nextToken();
                    recvPackets = linest.nextToken();
                    
                    // Skip six tokens
                    for (int i = 0; i < 6; i++) {
                    	linest.nextToken();
    				}
                    
                    sentBytes = linest.nextToken();
                    sentPackets = linest.nextToken();

                    // Debug
                    //Log.i(TAG, "sentBytes: " + sentBytes);
                    //Log.i(TAG, "sentPackets: " + sentPackets);

                    data = new StringBuilder();

                    data.append("interface," + devName);
                	data.append(",recv_bytes," + recvBytes);
                	data.append(",recv_packets," + recvPackets);

                	data.append(",sent_bytes," + sentBytes);
                	data.append(",sent_packets," + sentPackets);

                    result.append((isFirstInterface ? "" : ",") + data);
                    isFirstInterface = false;
	            }
            }catch(NoSuchElementException noElementException){
        		Log.i(TAG, "Read to the end of output.");
        	}
        }
        catch (Exception e)
        {

            Log.e(TAG, "Exception", e);
        }


        // Debug
//        String debugResult = result.toString();
//        Log.i(TAG, "returning: " + result.toString());

        return result.toString();
    }
}

