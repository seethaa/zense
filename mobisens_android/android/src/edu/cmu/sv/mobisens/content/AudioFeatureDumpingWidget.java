package edu.cmu.sv.mobisens.content;

import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.Random;

import edu.cmu.sv.mobisens.MobiSensService;
import edu.cmu.sv.mobisens.io.AudioProcessor;
import edu.cmu.sv.mobisens.io.Directory;
import edu.cmu.sv.mobisens.io.FileOperation;
import edu.cmu.sv.mobisens.io.MobiSensLog;
import edu.cmu.sv.mobisens.power.Alarm;
import edu.cmu.sv.mobisens.settings.ServiceParameters;
import edu.cmu.sv.mobisens.util.KeyValuePair;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.util.Log;

public class AudioFeatureDumpingWidget extends DataDumpingWidget {

	private static final String TAG = "AudioFeatureDumpingWidget";
	
	public final static String CLASS_PREFIX = AudioFeatureDumpingWidget.class.getName();
	public final static String ACTION_PREPARE_UPLOAD = CLASS_PREFIX + ".action_prepare_upload";
	
	private boolean dumpRaw = false;
	private DataOutputStream outStream = null;

	private AudioProcessor audioProcessor = null;
	
	private void writeSamples(ArrayList<KeyValuePair<Long,short[]>> samples){
		StringBuilder builder = new StringBuilder(samples.size() * 1024);
		Log.i(TAG, "Sample size: " + samples.size());
		
		for(KeyValuePair<Long,short[]> sample:samples){
		
			builder.append(sample.getKey());
			short[] data = sample.getValue();
			for(short value:data){
				builder.append(",").append(value);
				
				if(dumpRaw){
					try {
						this.outStream.writeShort(value);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
			builder.append("\r\n");
		}
		
		writeData(builder.toString());
	}
	
	private ArrayList<KeyValuePair<Long,short[]>> selectSamples(ArrayList<KeyValuePair<Long,short[]>> samples, int sampleNumber){
		if(sampleNumber == 0)
			return samples;

		Random rand = new Random();

		while(samples.size() > sampleNumber){
			int indexToRemove = rand.nextInt(samples.size());

			//Log.i(TAG, "index: " + indexToRemove);
			samples.remove(indexToRemove);
		}
		
		return samples;
	}
	
	private int waitCycleCount = 0;

	private Handler exitHandler = new Handler();
	private Runnable stopRecordRunable = new Runnable(){

		public void run() {
			// TODO Auto-generated method stub
			audioProcessor.stopRecord();
			Log.i(CLASS_PREFIX, "Recording audio snapshot ended...");
		}
		
	};
	
	private void dumpAudioData(AudioProcessor processor){
		ArrayList<KeyValuePair<Long, short[]>> samples = audioProcessor.getSamples();
		writeSamples(samples);
	}

	public void register(ContextWrapper contextWrapper){
		super.register(contextWrapper); // This line MUST go before the switchRecordFile
		
		this.switchRecordFile();
		
		
		
		if(dumpRaw){
			try{
				OutputStream os = new FileOutputStream(new File(Directory.MOBISENS_ROOT + "audio.raw"));
				BufferedOutputStream bos = new BufferedOutputStream(os);
				this.outStream = new DataOutputStream(bos);
			}catch(Exception ex){
				ex.printStackTrace();
			}
		}

	}
	
	public void onReceive(Context context, Intent intent) {
		super.onReceive(context, intent);
		
		// TODO Auto-generated method stub
    	String action = intent.getAction();
    	
    	if(Alarm.ACTION_ALARM.equals(action)){
    		
    		this.waitCycleCount++;
			if(this.waitCycleCount >= FILE_SPLIT_INTERVAL_MS / ServiceParameters.CYCLING_BASE_MS){
				AudioFeatureDumpingWidget.broadcastUploadFiles(intent, this);
				waitCycleCount = 0;
				MobiSensLog.log(CLASS_PREFIX + ", record file switched.");
			}
			
			
    		audioProcessor = new AudioProcessor(){
    			protected void onRecordEnded(){
    				dumpAudioData(this);
    			}
    		};
    		
    		audioProcessor.startRecord();
    		
    		ServiceParameters params = MobiSensService.getParameters();
    		long interval = params.getServiceParameter(ServiceParameters.AUDIO_SAMPLING_INTERVAL);
    		
    		this.exitHandler.postDelayed(stopRecordRunable, interval);
    		Log.i(CLASS_PREFIX, "Recording audio snapshot...");
		}
    	
    	if(ACTION_PREPARE_UPLOAD.equals(action)){
			String[] fileList = FileOperation.getFilesInDirectory(getDataDirectory());
			if(this.getCurrentFileName() != null){
				String[] tmp = fileList;
				fileList = new String[tmp.length - 1];
				int index = 0;
				for(String path:tmp){
					if(!path.equals(this.getCurrentFileName())){
						fileList[index] = path;
						index++;
					}
				}
				
				boradcastFileList(fileList, String.valueOf(Directory.FILE_TYPE_AUDIO), this);
				
				Log.i(TAG, "Upload requested: " + CLASS_PREFIX);
				MobiSensLog.log("Upload requested: " + CLASS_PREFIX);
			}
		}
	}
	
	public void unregister(){
		this.exitHandler.removeCallbacks(stopRecordRunable);
		this.closeCurrentFileStream();
		
		if(dumpRaw){
			try {
				this.outStream.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		String[] fileList = FileOperation.getFilesInDirectory(getDataDirectory());
		boradcastFileList(fileList, String.valueOf(Directory.FILE_TYPE_AUDIO), this);
		super.unregister();
	}
	
	public static String getDataDirectory(){
		return Directory.AUDIO_DEFAULT_DATA_FOLDER;
	}
	
	protected void switchRecordFile(){
		synchronized(syncObject){
			if(this.getContext() == null)
				return;
		}
		

		this.switchRecordFile(AudioFeatureDumpingWidget.getDataDirectory(), 
				this.getDeviceID() + "_" + Directory.RECORDER_TYPE_AUDIO, 
				".csv");
	}
	
	public static void broadcastUploadFiles(Intent intent, DataDumpingWidget dumper) {
		boolean isCleanUp = intent.getBooleanExtra(UploadControllerWidget.EXTRA_CLEANUP, false);
		
		synchronized(dumper.syncObject){
			String[] fileList = FileOperation.getFilesInDirectory(getDataDirectory());
			dumper.flushCurrentFileStream();
			
			if(isCleanUp){
				dumper.closeCurrentFileStream();
			}else{
				dumper.switchRecordFile();
			}
			
			boradcastFileList(fileList, String.valueOf(Directory.FILE_TYPE_AUDIO), dumper);
			
			Log.i(TAG, "Upload requested: " + CLASS_PREFIX);
			MobiSensLog.log("Upload requested: " + CLASS_PREFIX);
		}
		
	}
	
	public static void broadcastUploadFiles(Intent intent, Context dumper) {
		// TODO Auto-generated method stub
		String[] fileList = FileOperation.getFilesInDirectory(getDataDirectory());

		boradcastFileList(fileList, String.valueOf(Directory.FILE_TYPE_AUDIO), dumper);
		
		Log.i(TAG, "Upload requested: " + CLASS_PREFIX);
		MobiSensLog.log("Upload requested: " + CLASS_PREFIX);
		
	}
	
	@Override
    protected String[] getActions() {
        // TODO Auto-generated method stub
        return new String[]{ 
            Alarm.ACTION_ALARM,
            SystemWidget.ACTION_SYSTEM_DATA_EMITTED,
            ACTION_PREPARE_UPLOAD
        };
    }
}
