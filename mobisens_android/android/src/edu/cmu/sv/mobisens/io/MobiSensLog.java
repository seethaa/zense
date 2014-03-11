package edu.cmu.sv.mobisens.io;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Date;

public class MobiSensLog {
	private static final String LOG_FILE_NAME = "log.txt";
	public static final String LOG_FILE_PATH = Directory.MOBISENS_ROOT + LOG_FILE_NAME;
	
	public static void log(String message){
		try{
			File debugLog = new File(LOG_FILE_PATH);
			if(!debugLog.exists()){
				debugLog = Directory.openFile(Directory.MOBISENS_ROOT, LOG_FILE_NAME);
			}
			FileWriter fileWritter = new FileWriter(debugLog,true);
	        BufferedWriter bufferWritter = new BufferedWriter(fileWritter, 1 * 1024);
	        Date currentTime = new Date(System.currentTimeMillis());
	        
	        bufferWritter.write(currentTime.toString() + ": " + message + "\r\n");
	        bufferWritter.close();
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
	public static void log(Throwable throwable){
		Writer writer = new StringWriter();
		PrintWriter printWriter = new PrintWriter(writer);
		throwable.printStackTrace(printWriter);
		log(writer.toString());
	}
}
