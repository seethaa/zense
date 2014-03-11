package edu.cmu.sv.mobisens.io;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import edu.cmu.sv.mobisens.MobiSensService;
import edu.cmu.sv.mobisens.content.AnnotationWidget;
import edu.cmu.sv.mobisens.threading.CyclingWakeUpThread;

public class DataShrinker extends CyclingWakeUpThread {
	public static final long RESERVE_TIME = 48 * 60 * 60 * 1000;  // Reserve two days data
	public static final int MIN_LINE = 10;
	
	private int count = 0;
	private ContextWrapper context;
	
	@Override
	protected void onWake(long sleepTime){
		count++;
		
		if(count >= 12){
			shrinkData(this.context);
			count = 0;
			
		}
	}
	
	protected void onExit(){
		
	}
	
	public DataShrinker(ContextWrapper context) {
		super(60 * 60 * 1000, 60 * 1000);
		// TODO Auto-generated constructor stub
		
		this.context = context;
		
		shrinkData(this.context);
	}

	
	public static void shrinkData(Context context){
		
		Intent annoShrinkIntent = new Intent(AnnotationWidget.ACTION_SHRINK_FILE);
		annoShrinkIntent.putExtra(AnnotationWidget.EXTRA_SHRINK_DEVICEID, MobiSensService.getDeviceID(context));
		annoShrinkIntent.putExtra(AnnotationWidget.EXTRA_RESERVE_TIME, RESERVE_TIME);
		annoShrinkIntent.putExtra(AnnotationWidget.EXTRA_MIN_LINE, MIN_LINE);
		
		context.sendBroadcast(annoShrinkIntent);
		
		
		
		MobiSensLog.log("Reducing annotations...");
	}

}
