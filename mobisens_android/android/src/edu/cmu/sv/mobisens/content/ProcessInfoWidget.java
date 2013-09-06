package edu.cmu.sv.mobisens.content;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.SystemClock;
import edu.cmu.sv.mobisens.MobiSensService;
import edu.cmu.sv.mobisens.power.Alarm;
import edu.cmu.sv.mobisens.settings.ServiceParameters;
import edu.cmu.sv.mobisens.threading.CyclingWakeUpThread;
import edu.cmu.sv.mobisens.util.ApplicationInfo;

public class ProcessInfoWidget extends SystemWidget {
	private long PROC_INTERVAL = ServiceParameters.DEFAULT_PROC_SCAN_INTERVAL;
	//private static final int PROCESSINFO_MSG = 0;
	
	public static final String RUNNING_APPINFO_TYPE = "application";
    public static final String APP_TRAFFIC_TYPE = "app_traffic";
	
    
    
//    private RequestingThread scanningThread;
	
//	private class RequestingThread extends CyclingWakeUpThread{
//
//		public RequestingThread() {
//			super(PROC_INTERVAL, 1000);
//			// TODO Auto-generated constructor stub
//		}
//		
//		@Override
//		protected void onWake(long sleepTime){
//			doScan();
//		}
//	};
	
	private long scanTimeMillions = 0;
    
    
    public void register(ContextWrapper contextWrapper){

		IntentFilter filter = new IntentFilter(ProfileWidget.ACTION_SERVICE_PARAMETER_UPDATED);
		filter.addAction(Alarm.ACTION_ALARM);
		
		this.setFilter(filter);
		
		super.register(contextWrapper);
		
		// Start getting network device information
        //Message msg3 = handler.obtainMessage(PROCESSINFO_MSG);
        //handler.sendMessageDelayed(msg3, PROC_INTERVAL);
		
//		this.scanningThread = new RequestingThread();
//		this.scanningThread.start();

	}
    
    public void unregister(){
//    	this.scanningThread.exit();
//    	this.scanningThread = null;
    	
    	super.unregister();
    	//handler.removeMessages(PROCESSINFO_MSG);
    }
    
    @Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
    	String action = intent.getAction();
    	if(action.equals(ProfileWidget.ACTION_SERVICE_PARAMETER_UPDATED)){
    		refreshSettings();
    	}
    	
    	if(Alarm.ACTION_ALARM.equals(action)){
    		if(SystemClock.elapsedRealtime() >= this.scanTimeMillions){
    			this.doScan();
    			this.scanTimeMillions = SystemClock.elapsedRealtime() + this.PROC_INTERVAL;
    		}
    	}
	}
    
    private void refreshSettings(){
    	ServiceParameters params = MobiSensService.getParameters();
    	PROC_INTERVAL =  params.getServiceParameter(ServiceParameters.PROC_SCAN_INTERVAL);
    	
//    	if(this.scanningThread != null){
//    		this.scanningThread.setInterval(PROC_INTERVAL);
//    	}
    }
    
    private void doScan(){
    	String applicationInfo = ApplicationInfo.getRunningApplicationInfoCSV(getContext().getApplicationContext());
        String dataRecord = SystemWidget.constructDataRecord( 
        		applicationInfo, 
                RUNNING_APPINFO_TYPE,
                getDeviceID());
        
        
        ProcessInfoWidget.this.broadcastDataRecord(dataRecord, RUNNING_APPINFO_TYPE);
        
        String applicationTrafficInfo = ApplicationInfo.getApplicationTrafficInfoCSV(getContext().getApplicationContext());
        dataRecord = SystemWidget.constructDataRecord( 
        		applicationTrafficInfo, 
        		APP_TRAFFIC_TYPE,
        		getDeviceID());
        ProcessInfoWidget.this.broadcastDataRecord(dataRecord, APP_TRAFFIC_TYPE);
        
        // Schedule a new timer
        //msg = obtainMessage(PROCESSINFO_MSG);

        //this.sendMessageDelayed(msg, PROC_INTERVAL);
    }
}
