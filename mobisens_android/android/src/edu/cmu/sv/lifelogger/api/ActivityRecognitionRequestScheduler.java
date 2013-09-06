package edu.cmu.sv.lifelogger.api;

import java.util.LinkedList;

import edu.cmu.sv.mobisens.MobiSensService;
import edu.cmu.sv.mobisens.io.MobiSensDataHolder;
import edu.cmu.sv.mobisens.io.MobiSensLog;
import edu.cmu.sv.mobisens.net.Network;
import edu.cmu.sv.mobisens.threading.CyclingWakeUpThread;
import edu.cmu.sv.mobisens.ui.RendererWidget;
import edu.cmu.sv.mobisens.util.Annotation;
import edu.cmu.sv.mobisens.util.MachineAnnotation;
import android.content.Context;
import android.content.Intent;

public final class ActivityRecognitionRequestScheduler {
	private volatile static Context context = null;
	private volatile static CyclingWakeUpThread workingThread = null;
	private static LinkedList<Annotation> annos = new LinkedList<Annotation>();
	private volatile static MobiSensDataHolder db = null;
	
	private static final String CLASS_PREFIX = ActivityRecognitionRequestScheduler.class.getName();
	
	
	public static void init(Context context){
		ActivityRecognitionRequestScheduler.context = context;
		if(workingThread != null){
			workingThread.exit();
		}
		
		workingThread = new CyclingWakeUpThread(1000, 500){

			@Override
			protected void onWake(long sleepTime) {
				// TODO Auto-generated method stub
				try{
					request();
				}catch(Exception ex){
					ex.printStackTrace();
					MobiSensLog.log(ex);
				}
				
			}
			
		};
		
		if(annos != null){
			annos.clear();
			
		}
		
		if(db != null){
			db.close();
			db = new MobiSensDataHolder(context);
			db.open();
		}
		
		
		
		workingThread.start();
	}
	
	
	public static void close(){
		if(db != null){
			db.close();
		}
		
		
		synchronized(annos){
			if(annos != null){
				annos.clear();
			}
		}
		
		if(workingThread != null){
			workingThread.exit();
		}
		
	}
	
	
	public static void push(Annotation anno){
		synchronized(annos){
			annos.add(anno);
		}
	}
	
	@SuppressWarnings("unchecked")
	private static void request(){
		if(context == null)
			return;
		
		String deviceId = MobiSensService.getDeviceID(context);
		boolean broadcastNotify = false;
		LinkedList<Annotation> annosClone = null;
		
		synchronized(annos){
			annosClone = (LinkedList<Annotation>) annos.clone();  // don't hold the lock too long.
		}
		
		// flood the server.
		for(Annotation anno:annosClone){
			
			// Let the cache deal with the connectivity.
			//if(Network.isNetworkConnected(ActivityRecognitionRequestScheduler.context)){
				String activityName = anno.guessActivity(deviceId);
				if(!activityName.equals("")){
					//db.set(anno.getStart().getTime(), anno.getEnd().getTime(), anno.toString());
					broadcastNotify = true;
				}
				
				synchronized(annos){
					annos.remove(anno);
				}
			//}
			
			
		}
			
		
		if(broadcastNotify){
			LocationBasedRecognizer.FlushCache();
			Intent clearNotificationIntent = new Intent(RendererWidget.ACTION_DATA_MODIFIED_OUTSIDE);
			clearNotificationIntent.putExtra(RendererWidget.EXTRA_RENDERER_TYPE, RendererWidget.ALL_RENDERER_TYPES);
			context.sendBroadcast(clearNotificationIntent);
		}
	}

}
