package edu.cmu.sv.mobisens;


import edu.cmu.sv.mobisens.util.Address;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MobiSensRegister extends Activity {
	
	private final static String TAG = "MobiSensRegister";
	private final static int GET_PROFILE_FAILED = 0;
	private final static int GET_PROFILE_SUCCEED = 1;
	private final static int UPDATE_PROFILE_FAILED = 2;
	private final static int UPDATE_PROFILE_SUCCEED = 3;
	
	private EditText etxtAddress = null;
	private EditText etxtCity = null;
	private EditText etxtState = null;
	private EditText etxtZipcode = null;
	private EditText etxtEmail = null;
	private EditText etxtCountry = null;
	private LinearLayout lyAddressPanel = null;
	private LinearLayout lyProgressPanel = null;
	private TextView tvInfo = null;
	
	
	private final Handler handler = new Handler()
    {
		private static final String TAG = "UI Handler";
		
        @Override
        public void handleMessage(Message msg)
        {
            
            switch(msg.what){
            case GET_PROFILE_FAILED:
            	// Do not let the user open this activity if get profile failed.
            	AlertDialog alertDialog = new AlertDialog.Builder(MobiSensRegister.this).create();
            	alertDialog.setTitle("Get Register Info Failed");
            	alertDialog.setMessage(getString(R.string.get_address_failed));
            	alertDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "OK", new DialogInterface.OnClickListener(){

					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						MobiSensRegister.this.setResult(RESULT_CANCELED);
						MobiSensRegister.this.finish();
						Log.i(TAG, "Get address failed.");
					}
            		
            	});
            	alertDialog.show();
            	break;
            case GET_PROFILE_SUCCEED:
            	setAllFieldsFromAddressObject(address);
            	MobiSensRegister.this.lyProgressPanel.setVisibility(View.GONE);
            	MobiSensRegister.this.lyAddressPanel.setVisibility(View.VISIBLE);
            	Log.i(TAG, "Get address completed.");
            	break;
            case UPDATE_PROFILE_FAILED:
            	// Show a warning if update failed.
            	AlertDialog uploadFailedDialog = new AlertDialog.Builder(MobiSensRegister.this).create();
            	uploadFailedDialog.setTitle("Upload Address Failed");
            	uploadFailedDialog.setMessage(getString(R.string.update_address_failed));
            	uploadFailedDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "OK", new DialogInterface.OnClickListener(){

					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						Log.i(TAG, "Upload address failed.");
					}
            		
            	});
            	uploadFailedDialog.show();
            	break;
            case UPDATE_PROFILE_SUCCEED:
            	MobiSensRegister.this.setResult(RESULT_OK);
            	MobiSensRegister.this.finish();
            	Log.i(TAG, "Upload address completed.");
            	break;
            }

        }

    };
	
	private Address address = new Address();

	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.register);
		
		initializeComponents();
	}
	
	protected void onResume(){
		super.onResume();
		
		this.lyAddressPanel.setVisibility(View.GONE);
		this.lyProgressPanel.setVisibility(View.VISIBLE);
		this.tvInfo.setText(R.string.get_info);
		
		Thread downloadThread = new Thread(){
	    	public void run(){
	    		super.run();
	    		
	    		Address addr = Address.download(SystemSensService.getDeviceID(MobiSensRegister.this));
	    		if(addr != null){
	    			address = addr;
	    			handler.sendEmptyMessage(GET_PROFILE_SUCCEED);
	    		}else{
	    			handler.sendEmptyMessage(GET_PROFILE_FAILED);
	    		}
	    	}
	    };
	    
	    downloadThread.start();
	}
	
	private void initializeComponents(){
		this.etxtAddress = (EditText)this.findViewById(R.id.etxtAddress);
		this.etxtCity = (EditText)this.findViewById(R.id.etxtCity);
		this.etxtState = (EditText)this.findViewById(R.id.etxtState);
		this.etxtZipcode = (EditText)this.findViewById(R.id.etxtZipcode);
		this.etxtEmail = (EditText)this.findViewById(R.id.etxtEmail);
		this.etxtCountry = (EditText)this.findViewById(R.id.etxtCountry);
		this.lyAddressPanel = (LinearLayout)this.findViewById(R.id.lyEditAddress);
		this.lyProgressPanel = (LinearLayout)this.findViewById(R.id.lyProcessing);
		this.tvInfo = (TextView)this.findViewById(R.id.tvProcessing);
	}
	
	private void setAllFieldsFromAddressObject(Address address){
		this.etxtAddress.setText(address.getStreet());
		this.etxtCity.setText(address.getCity());
		this.etxtState.setText(address.getState());
		this.etxtZipcode.setText(address.getZipcode());
		this.etxtEmail.setText(address.getEmail());
		this.etxtCountry.setText(address.getCountry());
	}
	
	private Address setValuesToAddressObject(Address targetAddress){
		targetAddress.setCity(this.etxtCity.getText().toString());
		targetAddress.setCountry(this.etxtCountry.getText().toString());
		targetAddress.setEmail(this.etxtEmail.getText().toString());
		targetAddress.setState(this.etxtState.getText().toString());
		targetAddress.setStreet(this.etxtAddress.getText().toString());
		targetAddress.setZipcode(this.etxtZipcode.getText().toString());
		
		return targetAddress;
	}
	
	public void onCancelButtonClick(View view){
		this.setResult(RESULT_CANCELED);
		this.finish();
	}
	
	public void onSubmitButtonClick(View view){
		
		this.lyProgressPanel.setVisibility(View.VISIBLE);
		this.lyAddressPanel.setVisibility(View.GONE);
		this.tvInfo.setText(R.string.upload_info);
		
		setValuesToAddressObject(address);
		
		Thread uploadThread = new Thread(){
	    	public void run(){
	    		super.run();
	    		
	    		boolean res = address.upload(SystemSensService.getDeviceID(MobiSensRegister.this));
	    		if(res){
	    			
	    			handler.sendEmptyMessage(UPDATE_PROFILE_SUCCEED);
	    		}else{
	    			handler.sendEmptyMessage(UPDATE_PROFILE_FAILED);
	    		}
	    	}
	    };
	    
	    uploadThread.start();
	}
	
}
