package edu.cmu.sv.mobisens.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.WifiManager;
import android.util.Log;
import android.view.Window;

import edu.cmu.sv.mobisens.MobiSensMessageBox;
import edu.cmu.sv.mobisens.R;
import edu.cmu.sv.mobisens.content.UploadWidget;
import edu.cmu.sv.mobisens.content.Widget;
import edu.cmu.sv.mobisens.io.MobiSensLog;

public class UploadProgressRenderWidget extends Widget {
	private static final String TAG = "UploadProgressRenderWidget";
	
	private final static String CLASS_PREFIX = UploadProgressRenderWidget.class.getName();
	public static final int MAX_PROGRESS = 10000;
	
	private Activity getActivity(){
		return (Activity) this.getContext();
	}
	
	public void beforeRegistered(ContextWrapper contextWrapper){
		
		if(!(contextWrapper instanceof Activity)){
			throw new IllegalArgumentException("The contextWrapper must be an Activity.");
		}
		
	}
	
	
	@Override
    public void onReceive(Context context, Intent intent){
		String action = intent.getAction();
		if(UploadWidget.ACTION_UPLOAD_PROGRESS.equals(action)){
			this.displayProgress(intent.getIntExtra(UploadWidget.EXTRA_UPLOAD_PROGRESS, 0));
		}
		
		
		if(UploadWidget.ACTION_NETWORK_ERROR.equals(action)){
			this.displayNetworkErrorDialog();
			this.hideProgressBar();
		}
		
		if(UploadWidget.ACTION_USER_END_UPLOAD.equals(action)){
			this.hideProgressBar();
		}
    }
	
	private void displayNetworkErrorDialog(){
		try{
			AlertDialog alertDialog = new AlertDialog.Builder(this.getActivity()).create();
	    	alertDialog.setTitle(this.getActivity().getString(R.string.message_title_general_network_error));
	    	alertDialog.setMessage(this.getActivity().getString(R.string.message_general_network_error));
	    	alertDialog.setButton(DialogInterface.BUTTON_NEUTRAL, "OK", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					
				}
	    	});
	    	alertDialog.setIcon(R.drawable.abort_dlg);
	    	
	    	alertDialog.show();
		}catch(Exception ex){
			ex.printStackTrace();
			MobiSensLog.log(ex);
		}
	}
	
	private void displayProgress(int progress){
		
		
		this.getActivity().setProgressBarIndeterminateVisibility(true); //setProgressBarIndeterminate(false);
		this.getActivity().setProgress(progress);
		this.getActivity().setProgressBarVisibility(true);
		
		if(progress >= MAX_PROGRESS){
			this.hideProgressBar();
		}
	}
	
	private void hideProgressBar(){
		
		this.getActivity().setProgress(MAX_PROGRESS);
		this.getActivity().setProgressBarVisibility(false);
		this.getActivity().setProgressBarIndeterminateVisibility(false);
	}

	@Override
	protected String[] getActions() {
		// TODO Auto-generated method stub
		return new String[]{UploadWidget.ACTION_UPLOAD_PROGRESS, 
				UploadWidget.ACTION_USER_END_UPLOAD, 
				UploadWidget.ACTION_NETWORK_ERROR};
	}
}
