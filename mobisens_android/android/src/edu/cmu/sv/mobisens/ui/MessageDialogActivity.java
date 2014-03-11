package edu.cmu.sv.mobisens.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.util.Log;
import java.util.ArrayList;


import edu.cmu.sv.mobisens.R;
import edu.cmu.sv.mobisens.MobiSensService;
import edu.cmu.sv.mobisens.settings.ServiceParameters;

public class MessageDialogActivity extends Activity {
	
	private final static String TAG = "AnnotationNotifyActivity";
	public final static String DIALOG_TYPE = "type";
	public final static int DIALOGTYPE_ANNO_REQUEST = 0;
	public final static int DIALOGTYPE_WIFIENABLE_REQUEST = 1;
	public final static int DIALOGTYPE_GPSENABLE_REQUEST = 2;
	
	private static ArrayList<Intent> messageBoxQueue = new ArrayList<Intent>();
	private static boolean shouldStart = true;
	
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		
		Intent intent = getIntent();
		int type = intent.getIntExtra(DIALOG_TYPE, DIALOGTYPE_ANNO_REQUEST);

		AlertDialog alertDialog = new AlertDialog.Builder(this).create();
    	alertDialog.setTitle(getString(R.string.message_dialog_title));
    	
    	switch(type){
    	case DIALOGTYPE_ANNO_REQUEST:
    	{
    		long interval = MobiSensService.getParameters().getServiceParameter(ServiceParameters.ANNO_REQUEST_INTERVAL);
    		alertDialog.setMessage(getString(R.string.annotation_request).replace("{0}", String.valueOf(interval / 60000)));
    		alertDialog.setIcon(R.drawable.anno_dlg);
    		alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, "Yes", new DialogInterface.OnClickListener(){

    			public void onClick(DialogInterface dialog, int which) {
    				Intent annotationIntent = new Intent(MessageDialogActivity.this, SwitcherActivity.class);
    				annotationIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
    				startActivity(annotationIntent);
    				showNextDialog();
    				finish();
    			}
        		
        	});
        	
        	alertDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "No, not this time.", new DialogInterface.OnClickListener(){
    			public void onClick(DialogInterface dialog, int which) {
    				Log.i(TAG, "annotation refused.");
    				showNextDialog();
    				finish();
    			}
        	});
    	}
	    	break;
    	case DIALOGTYPE_WIFIENABLE_REQUEST:
    	{
    		alertDialog.setMessage(getString(R.string.request_to_enable_wifi));
        	alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, "Yes", new DialogInterface.OnClickListener(){

    			public void onClick(DialogInterface dialog, int which) {
    				WifiManager wifiManager = (WifiManager)getSystemService(Context.WIFI_SERVICE);
					wifiManager.setWifiEnabled(true);
					showNextDialog();
					finish();
    			}
        		
        	});
        	
        	alertDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "No", new DialogInterface.OnClickListener(){
    			public void onClick(DialogInterface dialog, int which) {
    				Log.i(TAG, "Enable WIFI refused.");
    				showNextDialog();
    				finish();
    			}
        	});
    	}
    		break;
    	case DIALOGTYPE_GPSENABLE_REQUEST:
    	{
    		alertDialog.setMessage(getString(R.string.request_to_enable_gps));
    		alertDialog.setIcon(R.drawable.warning_dlg);
        	alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener(){

    			public void onClick(DialogInterface dialog, int which) {
    				showNextDialog();
    				finish();
    			}
        		
        	});
    	}
    		break;
    	}
    	
    	alertDialog.show();
	}
	
	public void showNextDialog(){
		synchronized(messageBoxQueue){
			if(messageBoxQueue.size() > 0){
				startActivity(messageBoxQueue.remove(0));
			}else{
				shouldStart = true;
			}
		}
	}
	
	public static void showDialog(Context context, int dialogType){
		Intent dialogIntent = new Intent(context, MessageDialogActivity.class);
		dialogIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		dialogIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		dialogIntent.putExtra(MessageDialogActivity.DIALOG_TYPE, dialogType);
		synchronized(messageBoxQueue){
			messageBoxQueue.add(dialogIntent);
			
			if(shouldStart){
				shouldStart = false;
				context.startActivity(messageBoxQueue.remove(0));
			}
			
		}
	}

}
