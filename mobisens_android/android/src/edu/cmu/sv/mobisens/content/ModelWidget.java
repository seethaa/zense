package edu.cmu.sv.mobisens.content;

import java.io.File;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.util.Log;
import edu.cmu.sv.lifelogger.algorithm.DMW;
import edu.cmu.sv.lifelogger.util.NGramModel;
import edu.cmu.sv.mobisens.io.Directory;
import edu.cmu.sv.mobisens.io.FileOperation;
import edu.cmu.sv.mobisens.io.MobiSensData;
import edu.cmu.sv.mobisens.io.MobiSensDataHolder;
import edu.cmu.sv.mobisens.io.MobiSensLog;
import edu.cmu.sv.mobisens.util.Annotation;
import edu.cmu.sv.mobisens.util.MachineAnnotation;

public class ModelWidget extends Widget {
	private static final String CLASS_PREFIX = ModelWidget.class.getName();
	
	public static final String ACTION_NEED_REFRESH = CLASS_PREFIX + ".action_modelset_need_refresh";
	
	public static final String ACTION_UPDATE_MODEL_NAME = CLASS_PREFIX + ".action_update_model_name";
	public static final String ACTION_UPDATE_DWM_MODEL = CLASS_PREFIX + ".action_update_dwm_model";
	public static final String ACTION_CLEAR_MODEL = CLASS_PREFIX + ".action_clear_model";
	public static final String ACTION_ADD_MODEL = CLASS_PREFIX + ".action_add_model";
	public static final String ACTION_SHRINK_MODEL = CLASS_PREFIX + ".action_shrink_model";
	
	public static final String EXTRA_SHRINK_BOUNDARIES = CLASS_PREFIX + ".extra_shrink_boundaries";
	public static final String EXTRA_DWM_MODEL = CLASS_PREFIX + ".extra_dwm_model";
	
	private MobiSensDataHolder db;
	
	public void register(ContextWrapper contextWrapper){
		super.register(contextWrapper);
		
		try{
			db = new MobiSensDataHolder(contextWrapper);
			db.open();
		}catch(Exception ex){
			MobiSensLog.log(ex);
		}
		
	}
	
	public void unregister(){
		super.unregister();
		db.close();
	}
	
	
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
    	String action = intent.getAction();
    	
    	if(ACTION_UPDATE_MODEL_NAME.equals(action)){
    		String annoString = intent.getStringExtra(Annotation.EXTRA_ANNO_STRING);
    		Annotation anno = Annotation.fromString(annoString);
    		if(anno != null){
    			broadcastDWMModel(updateDMWModel());
    		}
    	}
    	

	}
	
	private void broadcastDWMModel(String modelContent){
		Intent intent = new Intent(ACTION_UPDATE_DWM_MODEL);
		intent.putExtra(EXTRA_DWM_MODEL, modelContent);
		this.getContext().sendBroadcast(intent);
	}
	
	
	private String updateDMWModel(){
		String content = DMW.create(this.getContext()).toString();
		String indexFileName = Directory.MOBISENS_ROOT + Directory.DMW_MODEL_FILENAME;
		
		File indexFile = new File(indexFileName);
		FileOperation.writeStringToFile(indexFile, content);
		return content;
	}
	
	
	private void tellRefresh(){
		Intent requestIntent = new Intent(ACTION_NEED_REFRESH);
		this.getContext().sendBroadcast(requestIntent);
	}

	@Override
	protected String[] getActions() {
		// TODO Auto-generated method stub
		return new String[]{ACTION_UPDATE_MODEL_NAME};
	}
	
}
