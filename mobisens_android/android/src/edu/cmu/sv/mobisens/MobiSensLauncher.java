package edu.cmu.sv.mobisens;

import java.util.ArrayList;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;


import edu.cmu.sv.mobisens.content.AudioFeatureDumpingWidget;
import edu.cmu.sv.mobisens.content.ContextListener;
import edu.cmu.sv.mobisens.content.DataDumpingWidget;
import edu.cmu.sv.mobisens.content.MessageBoxWidget;
import edu.cmu.sv.mobisens.content.ProfileWidget;
import edu.cmu.sv.mobisens.content.SensorDataDumpingWidget;
import edu.cmu.sv.mobisens.content.SystemDataDumpingWidget;
import edu.cmu.sv.mobisens.content.UploadWidget;
import edu.cmu.sv.mobisens.io.Directory;
import edu.cmu.sv.mobisens.io.MobiSensLog;
import edu.cmu.sv.mobisens.net.Network;
import edu.cmu.sv.mobisens.net.URLs;
import edu.cmu.sv.mobisens.settings.LocalSettings;
import edu.cmu.sv.mobisens.settings.ServiceParameters;
import edu.cmu.sv.mobisens.ui.Eula;
import edu.cmu.sv.mobisens.ui.HorizontalGridView;
import edu.cmu.sv.mobisens.ui.ImageListViewAdapter;
import edu.cmu.sv.mobisens.ui.ImageListViewItem;
import edu.cmu.sv.mobisens.ui.ProfileListListViewItem;
import edu.cmu.sv.mobisens.ui.SwitcherActivity;
import edu.cmu.sv.mobisens.ui.UploadProgressRenderWidget;
import edu.cmu.sv.mobisens.util.About;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.app.ActionBar.OnNavigationListener;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.*;
import android.widget.*;
import android.widget.CompoundButton.OnCheckedChangeListener;

public class MobiSensLauncher extends ListActivity implements Eula.OnEulaAgreedTo {

	private static final String CLASS_PREFIX = MobiSensLauncher.class.getName();
	private static final String TAG = CLASS_PREFIX;
	private static final int LST_START_SERVICE_INDEX = 0;
	private static final int LST_VIEW_ACTIVITIES_INDEX = 1;
	private static final int LST_LIFE_STREAM_INDEX = 2;
	private static final int LST_MSGBOX_INDEX = 3;
	private static final int LST_PUSH_INDEX = 4;
	private static final String ACTION_LOAD_SPECIAL_PROFILE = CLASS_PREFIX + ".action_load_special_profile";
	
	private volatile boolean canShowProfileMessage = false;

	private ScrollView svDebugView = null;
	private RelativeLayout llProductView = null;

	private Button btnStartSensorService = null;
	private Button btnEndSensorService = null;
	private Button btnStartSystemService = null;
	private Button btnEndSystemService = null;
	private Button btnGlobalOperation = null;
	
	
	private CheckBox chkAccelerometer = null;
	private CheckBox chkCompass = null;
	private CheckBox chkOrientation = null;
	private CheckBox chkGyro = null;
	private CheckBox chkWIFI = null;
	private CheckBox chkBattery = null;
	private CheckBox chkGPS = null;
	private CheckBox chkPhone = null;

	private CheckBox chkDumpOnlyCharging = null;
	private CheckBox chkTemperature = null;
	private CheckBox chkLight = null;
	
	private EditText etxtSensors = null;
	private EditText etxtWIFI = null;
	private EditText etxtGPS = null;
	private EditText etxtBattery = null;

	private EditText etxtSystemInfoDumpInverval = null;
	private EditText etxtGetProfileInterval = null;
	
	private TextView tvDeviceID = null;
	
	private Intent systemSensServiceIntent = null;
	private Intent sensorServiceIntent = null;


	private Spinner spSelectGlobalOperation = null;
	private ListView lstActivities = null;
	private ArrayList<ImageListViewItem> listItems = null;
	
	//profiles
	private ServiceParameters[] specialProfiles;
	private UploadProgressRenderWidget progressRenderer = new UploadProgressRenderWidget();
	class ServiceOnCheckedChangeListener implements OnCheckedChangeListener{

		public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
			// TODO Auto-generated method stub
			ServiceParameters param = MobiSensService.getParameters();
			if(arg1){
				param.setServicesStatusEnabled(((Number)arg0.getTag()).longValue());
			}else{
				param.setServicesStatusDisabled(((Number)arg0.getTag()).longValue());
			}
			
			//MobiSensService.setParameters(param);
			Intent setProfileIntent = new Intent(ProfileWidget.ACTION_SET_SENSING_PROFILE);
			setProfileIntent.putExtra(ProfileWidget.EXTRA_SENSING_PROFILE, param.toString());
			setProfileIntent.putExtra(ProfileWidget.EXTRA_PROFILE_NAME, param.getName());
			MobiSensLauncher.this.sendBroadcast(setProfileIntent);
		}
		
	};
	
	class StartServiceConextListener extends ContextListener{
		protected void onServiceParametersSaved(){}
		
		protected void onEditParameterPannelDisabled(){}
		
		protected void onStartServiceCompleted(){}
		protected void onStartServiceFailed(Exception ex){}
		
		public StartServiceConextListener(Context context, Intent intent) {
			super(context, intent);
			// TODO Auto-generated constructor stub
			intent.putExtra(MobiSensService.EXTRA_KEY_START_BY, MobiSensService.EXTRA_VALUE_START_BY_ACTIVITY);
		}

		@Override
		public void onClick(View v){
			try
	        {
				onEditParameterPannelDisabled();
				
				saveServiceParametersFromControlValue();
				onServiceParametersSaved();
				
	        	this.getContext().startService(getIntent());
	        	v.setEnabled(false);
	        	
	        	onStartServiceCompleted();
	        	
	        	Log.i(TAG, "Service launch completed.");
	        	

	        }catch(Exception ex)
	        {
	        	Log.e(TAG, "Exception in starting the service", ex);
	        	onStartServiceFailed(ex);
	        }
		}
	}
	
	class EndServiceConextListener extends ContextListener{
		
		protected void onEndServiceCompleted(){}
		protected void onEndServiceFailed(Exception ex){}
		
		public EndServiceConextListener(Context context, Intent intent) {
			super(context, intent);
			// TODO Auto-generated constructor stub
		}

		@Override
		public void onClick(View v){
			
			int runningServiceCount = 0;
        	if(SensorService.getServiceContext() != null)++runningServiceCount;
			if(SystemSensService.getServiceContext() != null)++runningServiceCount;

			
			try {
	        	this.getContext().stopService(getIntent());
	        	v.setEnabled(false);

	        	onEndServiceCompleted();
	        	
	        } catch(Exception ex) {
	        	Log.e(TAG, "Exception in ending the service", ex);
	        	onEndServiceFailed(ex);
	        }
		}
	}
	
	
	private void initializeComponent(){
		setContentView(R.layout.main);
		
		//Layouts
		this.svDebugView = (ScrollView)this.findViewById(R.id.svDebugView);
		this.llProductView = (RelativeLayout)this.findViewById(R.id.llProductionView);
		
		//Buttons
        this.btnStartSensorService = (Button)this.findViewById(R.id.btnStartSensorService);
        this.btnEndSensorService = (Button)this.findViewById(R.id.btnStopSensorService);
        this.btnStartSystemService = (Button)this.findViewById(R.id.btnStartSystemService);
        this.btnEndSystemService = (Button)this.findViewById(R.id.btnStopSystemService);

        this.btnGlobalOperation = (Button)this.findViewById(R.id.btnApplyGlobalOperation);

        //CheckBoxes
        this.chkAccelerometer = (CheckBox)this.findViewById(R.id.chkAcc);

        this.chkBattery = (CheckBox)this.findViewById(R.id.chkBattery);
        this.chkCompass = (CheckBox)this.findViewById(R.id.chkCompass);
        this.chkGPS = (CheckBox)this.findViewById(R.id.chkGPS);
        this.chkGyro = (CheckBox)this.findViewById(R.id.chkGyro);
        this.chkOrientation = (CheckBox)this.findViewById(R.id.chkOrientation);
        this.chkPhone = (CheckBox)this.findViewById(R.id.chkPhone);

        this.chkWIFI = (CheckBox)this.findViewById(R.id.chkWIFI);
        this.chkDumpOnlyCharging = (CheckBox)this.findViewById(R.id.chkDumpOnlyCharging);
        this.chkTemperature = (CheckBox)this.findViewById(R.id.chkTemperature);
        this.chkLight = (CheckBox)this.findViewById(R.id.chkLight);

        this.chkAccelerometer.setTag(ServiceParameters.ACCELEROMETER);
        this.chkBattery.setTag(ServiceParameters.BATTERY_STATUS);
        this.chkCompass.setTag(ServiceParameters.COMPASS);
        this.chkGPS.setTag(ServiceParameters.GPS);
        this.chkGyro.setTag(ServiceParameters.GYRO);
        this.chkOrientation.setTag(ServiceParameters.ORIENTATION);
        this.chkPhone.setTag(0);

        this.chkWIFI.setTag(ServiceParameters.WIFI_SCAN);
        this.chkTemperature.setTag(ServiceParameters.TEMPERATURE);
        this.chkLight.setTag(ServiceParameters.LIGHT);

        this.etxtBattery = (EditText)this.findViewById(R.id.extBatterySamplingRate);
        this.etxtGPS = (EditText)this.findViewById(R.id.extGPSSamplingRate);
        this.etxtSensors = (EditText)this.findViewById(R.id.etxtSensorSamplingRate);

        this.etxtWIFI = (EditText)this.findViewById(R.id.etxtWIFISamplingRate);
        this.etxtSystemInfoDumpInverval = (EditText)this.findViewById(R.id.etxtSystemInfoDumpingInterval);
        this.etxtGetProfileInterval = (EditText)this.findViewById(R.id.etxtGetProfileInterval);
        
        //Miscs.
        
        this.tvDeviceID = (TextView)this.findViewById(R.id.tvDeviceID);
        this.spSelectGlobalOperation = (Spinner)this.findViewById(R.id.spOperation);
        //this.gvProfileList = (GridView)this.findViewById(R.id.profileList);
        
        // ListView
        this.lstActivities = this.getListView();

        listItems = new ArrayList<ImageListViewItem>();
        
        // 0
        listItems.add(new ImageListViewItem(getString(R.string.start_all_services),
        		getString(R.string.activity_start_service_description), 
        		R.drawable.icon));
        
        // 1
        listItems.add(new ImageListViewItem(getString(R.string.activity_annotation_title),
        		getString(R.string.activity_annotation_description), 
        		R.drawable.tag_large));
        
        // 2
        listItems.add(new ImageListViewItem(getString(R.string.activity_life_stream),
        		getString(R.string.activity_life_stream_description), 
        		R.drawable.lifeloggerweb));
        
        // 3
        listItems.add(new ImageListViewItem(getString(R.string.activity_messagebox_title),
        		getString(R.string.activity_messagebox_description), 
        		R.drawable.messagebox_large));
        
        // 4
        if(!LocalSettings.isPushing(this)){
        	listItems.add(new ImageListViewItem(getString(R.string.activity_go_pushing),
            		getString(R.string.activity_go_pushing_description), 
            		R.drawable.push_cloud));
        }else{
        	listItems.add(new ImageListViewItem(getString(R.string.activity_abort_pushing),
            		getString(R.string.activity_abort_pushing_description), 
            		R.drawable.abort_push_cloud));
        }
        
        ImageListViewAdapter adapter = new ImageListViewAdapter(this, 
        		R.layout.image_listview_item,
        		listItems);
        this.setListAdapter(adapter);
        
        
		
	}
	
	private void buildHorizontalScrollView(){
		 if(this.specialProfiles == null || this.specialProfiles.length == 0)
			 return;
		 
		 String[] profileNames = new String[this.specialProfiles.length + 1];
		 
		 profileNames[0] = "Default Mode";
		 long selectedId = LocalSettings.getCurrentProfile(this);
		 int selectedIndex = 0;
		 
		 for(int i = 1; i < profileNames.length; i++){
			 profileNames[i] = this.specialProfiles[i - 1].getName();
			 if(selectedId == this.specialProfiles[i - 1].getId()){
				 selectedIndex = i;
			 }
		 }
		 
		 
		 ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, 
				 R.layout.profile_selector_spinner_item, 
				 android.R.id.text1, profileNames);
		 adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		 
		 getActionBar().setListNavigationCallbacks(adapter, navigationListener);
		 
		 if(adapter.getCount() > selectedIndex){
			 getActionBar().setSelectedNavigationItem(selectedIndex);
		 }
		 
		 getActionBar().setDisplayShowTitleEnabled(false);
		 
	}
	
	private volatile long selectedProfileId = 0;
	private OnNavigationListener navigationListener = new OnNavigationListener() {
    	
        @Override
        public boolean onNavigationItemSelected(int position, long id) {

        	// Holyshit....
        	if(position > 0){
        		ServiceParameters profile = specialProfiles[position - 1];
	        	
	        	// Avoid the message box pop up twice. This seems to be a bug of Android?
        		// wasted me a full day to debug..
	        	if(selectedProfileId == profile.getId()){
	        		Log.i(TAG, "Duplicated selection. A bug in Android or I am wrong?");
	        		
	        	}else{
	        		selectedProfileId = profile.getId();
	        		
	        		if(canShowProfileMessage){
		        		// If canShowProfileMessage is false, that means the app is not visible(usually before UI initialized)
		        		// This happens when the app is first loaded into memory.
		        		// We don't want to see the message box, so filter this case out.
						AlertDialog dialog = new AlertDialog.Builder(MobiSensLauncher.this).create();
						dialog.setTitle(R.string.message_dialog_title);
						dialog.setMessage(profile.getServiceParameterString(ServiceParameters.SWITCH_MESSAGE));
						dialog.setButton("OK", (DialogInterface.OnClickListener)null);
						dialog.setIcon(R.drawable.message_icon);
					
						dialog.show();
		        	}
		        	
		        	
					
					// Tell the registered ProfileWidgets to update the profiles.
					Intent setProfileIntent = new Intent(ProfileWidget.ACTION_SET_SENSING_PROFILE);
					setProfileIntent.putExtra(ProfileWidget.EXTRA_SENSING_PROFILE, profile.toString());
					setProfileIntent.putExtra(ProfileWidget.EXTRA_PROFILE_NAME, profile.getName());
					sendBroadcast(setProfileIntent);
					
	        	}
	        	
	        	
				
        	}else{
        		if(LocalSettings.getCurrentProfile(getApplicationContext()) != 0){
	        		Intent requestDefaultProfileIntent = new Intent(ProfileWidget.ACTION_LOAD_DEFAULT_PROFILE);
	        		MobiSensLauncher.this.sendBroadcast(requestDefaultProfileIntent);
        		}
        	}
        	
        	canShowProfileMessage = true; // Ugly.
			
			
            return false;
        }
    };
    
	//Initialize all control value by service parameters.
	private ServiceParameters loadControlValueFromParams(ServiceParameters params){
		
		//ServiceParameters.getSpecialProfiles();  // OK this is just for testing.
		
		boolean isDebugMode = params.getServiceParameterBoolean(ServiceParameters.DEBUG_MODE);
		this.svDebugView.setVisibility(isDebugMode ? View.VISIBLE : View.GONE);
		this.llProductView.setVisibility(isDebugMode ? View.GONE : View.VISIBLE);

		Log.i(TAG, "Is debug_mode: " + isDebugMode);
		
        this.etxtBattery.setText(params.getServiceParameterString(
						ServiceParameters.BATTERY_STATUS));
        
        this.etxtGPS.setText(params.getServiceParameterString(
						ServiceParameters.GPS));
        
        this.etxtSensors.setText(params.getServiceParameterString(
						ServiceParameters.ACCELEROMETER));
        
        this.etxtWIFI.setText(params.getServiceParameterString(
						ServiceParameters.WIFI_SCAN));

        this.etxtSystemInfoDumpInverval.setText(params.getServiceParameterString(
				ServiceParameters.SYSTEM_DUMP_INTERVAL));
        
        this.etxtGetProfileInterval.setText(params.getServiceParameterString(
        		ServiceParameters.GET_PROFILE_INTERVAL));
        
        this.chkAccelerometer.setChecked(
        		params.getServicesStatus(ServiceParameters.ACCELEROMETER)
        );
        
        this.chkBattery.setChecked(
        		params.getServicesStatus(ServiceParameters.BATTERY_STATUS)
        );
        
        this.chkCompass.setChecked(
        		params.getServicesStatus(ServiceParameters.COMPASS)
        );
        
        this.chkGPS.setChecked(
        		params.getServicesStatus(ServiceParameters.GPS)
        );
        
        this.chkGyro.setChecked(
        		params.getServicesStatus(ServiceParameters.GYRO)
        );
        
        this.chkOrientation.setChecked(
        		params.getServicesStatus(ServiceParameters.ORIENTATION)
        );
        
        this.chkPhone.setChecked(
        		params.getServicesStatus(ServiceParameters.CALL_MONITOR)
        );
        
        this.chkWIFI.setChecked(
        		params.getServicesStatus(ServiceParameters.WIFI_SCAN)
        );
        
        this.chkDumpOnlyCharging.setChecked(
        		!params.getServiceParameterBoolean(ServiceParameters.ENABLE_SYSTEM_DUMP_BY_INTERVAL)
        );
        
        this.chkTemperature.setChecked(
        		params.getServicesStatus(ServiceParameters.TEMPERATURE)
        );
        
        this.chkLight.setChecked(
        		params.getServicesStatus(ServiceParameters.LIGHT)
        );
        
        if(SystemSensService.getInstance() != null){
        	this.tvDeviceID.setText(getString(R.string.device_id) + SystemSensService.getInstance().getDeviceID());
        }

        return params;
	}
	
	
	private void setSensorPanelEnabled(boolean e){
		this.chkAccelerometer.setEnabled(e);
		this.chkGyro.setEnabled(e);
		this.chkOrientation.setEnabled(e);
		this.chkCompass.setEnabled(e);
		this.chkLight.setEnabled(e);
		this.chkTemperature.setEnabled(e);
		
		this.etxtSensors.setEnabled(e);
		//this.btnStartSensorService.setEnabled(e);
		//this.btnEndSensorService.setEnabled(!e);
	}
	
	private void setSystemServicePanelEnabled(boolean e){
		this.chkBattery.setEnabled(e);
		this.chkGPS.setEnabled(e);
		this.chkPhone.setEnabled(e);
		this.chkWIFI.setEnabled(e);
		this.chkDumpOnlyCharging.setEnabled(e);
		
		this.etxtBattery.setEnabled(e);
		this.etxtGPS.setEnabled(e);
		this.etxtWIFI.setEnabled(e);
		this.etxtSystemInfoDumpInverval.setEnabled(e);
		
		//this.btnStartSystemService.setEnabled(e);
		//this.btnEndSystemService.setEnabled(!e);
	}
	
	private void setSystemWiseSettingsPanelEnable(boolean e){
		this.chkDumpOnlyCharging.setEnabled(e);
		this.etxtSystemInfoDumpInverval.setEnabled(e);
		this.etxtGetProfileInterval.setEnabled(e);
	}
	
	private void attachEventHandler(){
		
		btnStartSensorService.setOnClickListener(
        		new StartServiceConextListener(this, sensorServiceIntent){
        			@Override
					protected void onServiceParametersSaved(){
        				super.onServiceParametersSaved();
        				SensorService.setServiceContext(MobiSensLauncher.this);
        			}
        			
        			@Override
					protected void onStartServiceCompleted(){
        				super.onStartServiceCompleted();
        				btnStartSensorService.setEnabled(false);
        				btnEndSensorService.setEnabled(true);
        			}
        		});
        
        btnEndSensorService.setOnClickListener(
        		new EndServiceConextListener(this, sensorServiceIntent){
        			@Override
					protected void onEndServiceCompleted(){
        				super.onEndServiceCompleted();
        				btnStartSensorService.setEnabled(true);
        				btnEndSensorService.setEnabled(false);
        				SensorService.setServiceContext(null);
        			}
        		});
        
        btnStartSystemService.setOnClickListener(
        		new StartServiceConextListener(this, systemSensServiceIntent){
        			@Override
					protected void onServiceParametersSaved(){
        				super.onServiceParametersSaved();
        				SystemSensService.setServiceContext(MobiSensLauncher.this);
        			}
        			
        			@Override
					protected void onStartServiceCompleted(){
        				super.onStartServiceCompleted();
        				btnStartSystemService.setEnabled(false);
        				btnEndSystemService.setEnabled(true);
        			}
        		});
        
        btnEndSystemService.setOnClickListener(
        		new EndServiceConextListener(this, systemSensServiceIntent){
        			@Override
					protected void onEndServiceCompleted(){
        				super.onEndServiceCompleted();
        				btnStartSystemService.setEnabled(true);
        				btnEndSystemService.setEnabled(false);
        				SystemSensService.setServiceContext(null);
        			}
        		});

        /*this.btnStartAllServices.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(btnStartAllServices.getText().equals(getString(R.string.start_all_services))){
					btnEndSystemService.performClick();
					btnEndSensorService.performClick();
					
					btnStartSystemService.performClick();
					btnStartSensorService.performClick();
					btnStartAllServices.setText(R.string.end_all_services);
					
				}else{
					btnEndSystemService.performClick();
					btnEndSensorService.performClick();
					btnStartAllServices.setText(R.string.start_all_services);
				}
			}
		});*/

        this.btnGlobalOperation.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				// TODO Auto-generated method stub
				int selectIndex = spSelectGlobalOperation.getSelectedItemPosition();
				boolean needStopAllServices = true;
				if(selectIndex == 3 || selectIndex == 5)
					needStopAllServices = false;
				
				if( needStopAllServices && 
					(SensorService.getServiceContext()!= null ||
					SystemSensService.getServiceContext() != null)){
					Toast.makeText(MobiSensLauncher.this, "Please stop all running services first.", Toast.LENGTH_LONG).show();
				}else{
					try{
						
						switch(selectIndex){
						case 0:
							return;
						case 1:
							MobiSensService.clearDataFiles();
							break;
						case 2:
							btnStartSensorService.performClick();
							btnStartSystemService.performClick();
							break;
						case 4:
							ServiceParameters params = MobiSensService.getParameters();
							params.resetAllParameters();
							loadControlValueFromParams(params);
							Intent setProfileIntent = new Intent(ProfileWidget.ACTION_SET_SENSING_PROFILE);
							setProfileIntent.putExtra(ProfileWidget.EXTRA_SENSING_PROFILE, params.toString());
							setProfileIntent.putExtra(ProfileWidget.EXTRA_PROFILE_NAME, params.getName());
							break;
						case 3:
							if(btnEndSensorService.isEnabled())
								btnEndSensorService.performClick();
							if(btnEndSystemService.isEnabled())
								btnEndSystemService.performClick();
							break;
						case 5:
							break;
						}
						Toast.makeText(MobiSensLauncher.this, "Done", 1).show();
					}catch(Exception ex){
						Log.e(TAG, "Exception", ex);
					}
					
				}
			}
		});
        
        
	}
	
	//Save all control value back to service parameters.
	private void saveServiceParametersFromControlValue(){
		ServiceParameters params = MobiSensService.getParameters();

		//Params for SystemSens services
		params.setServiceParameters(ServiceParameters.WIFI_SCAN, 
				Integer.parseInt(etxtWIFI.getText().toString()));
		params.setServiceParameters(ServiceParameters.BATTERY_STATUS, 
				Integer.parseInt(etxtBattery.getText().toString()));
		params.setServiceParameters(ServiceParameters.GPS, 
				Integer.parseInt(etxtGPS.getText().toString()));
		params.setServiceParameters(ServiceParameters.ENABLE_SYSTEM_DUMP_BY_INTERVAL, 
				!this.chkDumpOnlyCharging.isChecked());
		params.setServiceParameters(ServiceParameters.SYSTEM_DUMP_INTERVAL, 
				Integer.parseInt(this.etxtSystemInfoDumpInverval.getText().toString()));
		params.setServiceParameters(ServiceParameters.GET_PROFILE_INTERVAL, 
				Integer.parseInt(this.etxtGetProfileInterval.getText().toString()));
		
		//MobiSensService.setParameters(params);
		
		//Params for sensor services.
		params.setServiceParameters(ServiceParameters.ACCELEROMETER |
				ServiceParameters.COMPASS |
				ServiceParameters.GYRO |
				ServiceParameters.ORIENTATION |
				ServiceParameters.TEMPERATURE |
				ServiceParameters.LIGHT, 
				Integer.parseInt(etxtSensors.getText().toString()));
		
		//MobiSensService.setParameters(params);
		
		Intent setProfileIntent = new Intent(ProfileWidget.ACTION_SET_SENSING_PROFILE);
		setProfileIntent.putExtra(ProfileWidget.EXTRA_SENSING_PROFILE, params.toString());
		setProfileIntent.putExtra(ProfileWidget.EXTRA_PROFILE_NAME, params.getName());
		sendBroadcast(setProfileIntent);
	}
	
	public void onListItemClick(ListView l, View v, int position, long id){
		if(position == MobiSensLauncher.LST_START_SERVICE_INDEX){
			if(this.listItems.get(position).getTitle().equals(getString(R.string.start_all_services))){
				this.btnEndSystemService.performClick();
				this.btnEndSensorService.performClick();
				
				this.btnStartSensorService.performClick();
				this.btnStartSystemService.performClick();
				
				this.listItems.get(position).setTitle(getString(R.string.end_all_services));
				this.listItems.get(position).setDescription(getString(R.string.activity_end_service_description));
			}else{
				this.btnEndSystemService.performClick();
				this.btnEndSensorService.performClick();
				
				this.listItems.get(position).setTitle(getString(R.string.start_all_services));
				this.listItems.get(position).setDescription(getString(R.string.activity_start_service_description));
			}
		}
		
		if(position == MobiSensLauncher.LST_VIEW_ACTIVITIES_INDEX){
			Intent annotationIntent = new Intent(this, SwitcherActivity.class);
			annotationIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
			this.startActivity(annotationIntent);
		}
		
		if(position == MobiSensLauncher.LST_LIFE_STREAM_INDEX){
			Intent launchLifeStreamIntent = new Intent(this, LifeStreamActivity.class);
			launchLifeStreamIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
			this.startActivity(launchLifeStreamIntent);
		}
		
		if(position == MobiSensLauncher.LST_MSGBOX_INDEX){
			AsyncTask<String, Void, Boolean> task = new AsyncTask<String, Void, Boolean>(){

				@Override
				protected Boolean doInBackground(String... params) {
					// TODO Auto-generated method stub
					return Network.canConnectToServer(URLs.MESSAGEBOX_CONNECTION_URL);
				}
				
				protected void onPostExecute(Boolean result) {
			         if(result){
			        	Intent messageboxIntent = new Intent(MobiSensLauncher.this, MobiSensMessageBox.class);
			        	messageboxIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
			        	startActivity(messageboxIntent);
			         }else{
			        	AlertDialog alertDialog = new AlertDialog.Builder(MobiSensLauncher.this).create();
						alertDialog.setTitle(getString(R.string.message_dialog_title));
						alertDialog.setMessage(getString(R.string.messagebox_network_error));
						alertDialog.setButton(DialogInterface.BUTTON_NEUTRAL, "OK", new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int which) {
								return;
							}
						});
						alertDialog.setIcon(R.drawable.abort_dlg);
						
						alertDialog.show();
			         }
			     }

				
			};

			task.execute(URLs.MESSAGEBOX_CONNECTION_URL);
			
		}
		
		if(position == MobiSensLauncher.LST_PUSH_INDEX){
			v.setEnabled(false);
			if(LocalSettings.isPushing(this)){
				
				Intent endPushIntent = new Intent(UploadWidget.ACTION_USER_END_UPLOAD);
				this.sendBroadcast(endPushIntent);
			}else{
				this.startPush();
			}
			
			v.setEnabled(true);
		}
		
		l.invalidateViews();
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		if(!this.requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS)){
			Log.e(TAG, "Window not support progressbar!");
			MobiSensLog.log("Window not support progressbar!");
		}
		
		setContentView(R.layout.main);
		getActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
		
		Eula.show(this);
		
		initializeComponent();
	
		this.systemSensServiceIntent = new Intent(this, SystemSensService.class);
		this.sensorServiceIntent = new Intent(this, SensorService.class);
		
		if(Eula.hasAccepted(this)){
			Intent notifyServiceIntent = new Intent(this, NotificationService.class);
			this.startService(notifyServiceIntent);
		}
		
		attachEventHandler();
		
		this.progressRenderer.register(this);
	
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.main_option_menu, menu);
	    return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    // Handle item selection
		Intent intent = new Intent(this, MobiSensInformation.class);
	    switch (item.getItemId()) {
	    case R.id.about_menu:
	        //newGame();
	    	intent.putExtra(MobiSensInformation.EXTRA_TYPE, MobiSensInformation.TYPE_ABOUT);
	    	this.startActivity(intent);
	        return true;
	    case R.id.eula_menu:
	        //showHelp();
	    	intent.putExtra(MobiSensInformation.EXTRA_TYPE, MobiSensInformation.TYPE_EULA);
	    	
	    	this.startActivity(intent);
	        return true;
	    default:
	        return super.onOptionsItemSelected(item);
	    }
	}
	
	@Override
	protected void onStart(){
		super.onStart();
		IntentFilter filter = new IntentFilter();
		filter.addAction(MessageBoxWidget.ACTION_UPDATE_UNREAD_MESSAGE_COUNT);
		filter.addAction(ProfileWidget.ACTION_SERVICE_PARAMETER_UPDATED);
		filter.addAction(UploadWidget.ACTION_USER_UPLOAD_END);
		filter.addAction(ProfileWidget.ACTION_PROFILE_EXPIRED);
		
		this.registerReceiver(receiver, filter);
	}
	
	@Override
	protected void onDestroy(){
		super.onDestroy();
		this.unregisterReceiver(receiver);
		this.progressRenderer.unregister();
	}
	
	@Override
	protected void onPause(){
		super.onPause();
		canShowProfileMessage = false;
		saveServiceParametersFromControlValue();
	}
	
	@Override
	protected void onResume()
	{	
		super.onResume();

        this.setSystemServicePanelEnabled(false);
        this.setSensorPanelEnabled(false);
        this.setSystemWiseSettingsPanelEnable(false);
        
        this.loadControlValueFromParams(MobiSensService.getParameters());
        this.setServiceButtonStateByServiceStatus();
        
        // Use a separate thread instead of the UI thread to get the profile,
        // so the UI won't be blocked.
        
        AsyncTask<Void, Void, ServiceParameters[]> task = new AsyncTask<Void, Void, ServiceParameters[]>(){

        	protected void onPreExecute(){
        		
        		specialProfiles = ServiceParameters.getCachedSpecialProfiles();
        		buildHorizontalScrollView();
        	}
        	
			@Override
			protected ServiceParameters[] doInBackground(Void... arg0) {
				
				// TODO Auto-generated method stub
				return ServiceParameters.getSpecialProfiles();
			}
			
			protected void onPostExecute(ServiceParameters[] results){
				if(results.length > 0){
					specialProfiles = results;
				
					buildHorizontalScrollView();
				}
			}
        	
        };
        
        task.execute(new Void[0]);
		
		if(GooglePlayServicesUtil.isGooglePlayServicesAvailable(this) != ConnectionResult.SUCCESS){
			AlertDialog alertDialog = new AlertDialog.Builder(MobiSensLauncher.this).create();
			alertDialog.setTitle(getString(R.string.message_dialog_title));
			alertDialog.setMessage(getString(R.string.old_play_store));
			alertDialog.setButton(DialogInterface.BUTTON_NEUTRAL, "OK", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					return;
				}
			});
			alertDialog.setIcon(R.drawable.abort_dlg);
			
			alertDialog.show();
		}
	}
	
	public void btnMessageBoxClick(View view){
		Intent messageboxIntent = new Intent(this, MobiSensMessageBox.class);
		this.startActivity(messageboxIntent);
	}
	
	public void btnStartAnnotationClicked(View view){
		Intent annotationIntent = new Intent(this, SwitcherActivity.class);
		this.startActivity(annotationIntent);
	}
	
	private void setServiceButtonStateByServiceStatus(){
		if(SensorService.getInstance() != null){
			this.btnStartSensorService.setEnabled(false);
			this.btnEndSensorService.setEnabled(true);
		}else{
			this.btnStartSensorService.setEnabled(true);
			this.btnEndSensorService.setEnabled(false);
		}
		
		if(SystemSensService.getInstance() != null){
			this.btnStartSystemService.setEnabled(false);
			this.btnEndSystemService.setEnabled(true);
		}else{
			this.btnStartSystemService.setEnabled(true);
			this.btnEndSystemService.setEnabled(false);
		}
		
		if(SensorService.getInstance() != null && SystemSensService.getInstance() != null){
        	//this.btnStartAllServices.setText(R.string.end_all_services);
			this.listItems.get(0).setTitle(getString(R.string.end_all_services));
			this.listItems.get(0).setDescription(getString(R.string.activity_end_service_description));
        }else{
        	//this.btnStartAllServices.setText(R.string.start_all_services);
        	this.listItems.get(0).setTitle(getString(R.string.start_all_services));
        	this.listItems.get(0).setDescription(getString(R.string.activity_start_service_description));
        }
		this.lstActivities.invalidateViews();
		
		
	}

    private final BroadcastReceiver receiver = new BroadcastReceiver(){

		@Override
		public void onReceive(Context context, Intent intent) {
			
			String action = intent.getAction();
			
			// TODO Auto-generated method stub
			if(action.equals(MessageBoxWidget.ACTION_UPDATE_UNREAD_MESSAGE_COUNT)){
				int unreadCount = intent.getIntExtra(MessageBoxWidget.EXTRA_UNREAD_MESSAGE_COUNT, 0);
				listItems.get(LST_MSGBOX_INDEX).setDescription(getString(R.string.unread_message_format).replace("{0}", String.valueOf(unreadCount)));
				lstActivities.invalidateViews();
			}
			
			if(action.equals(ProfileWidget.ACTION_SERVICE_PARAMETER_UPDATED)){
				loadControlValueFromParams(ServiceParameters.fromProfileString(intent.getStringExtra(ProfileWidget.EXTRA_NEW_PROFILE)));
			}
			
			if(UploadWidget.ACTION_USER_UPLOAD_END.equals(action)){
				pushEnded();
			}
			
			if(ProfileWidget.ACTION_PROFILE_EXPIRED.equals(action)){
				canShowProfileMessage = false;
				MobiSensLauncher.this.getActionBar().setSelectedNavigationItem(0);  // Reselect the default profile
				
			}
			
		}
    	
    };
    
    private void pushEnded(){
    	listItems.get(LST_PUSH_INDEX).setTitle(getString(R.string.activity_go_pushing));
    	listItems.get(LST_PUSH_INDEX).setDescription(getString(R.string.activity_go_pushing_description));
    	listItems.get(LST_PUSH_INDEX).setIconResourceId(R.drawable.push_cloud);
    	
    	this.getListView().invalidateViews();
    	
    	Log.i(TAG, "User upload ended!");
    }
    
    private void startPush(){
    	if(LocalSettings.isPushing(this))
    		return;
    	
    	Intent systemDataUploadRequestIntent = new Intent(SystemDataDumpingWidget.ACTION_PREPARE_UPLOAD);
		Intent sensorDataUploadRequestIntent = new Intent(SensorDataDumpingWidget.ACTION_PREPARE_UPLOAD);
		Intent audioDataUploadRequestIntent = new Intent(AudioFeatureDumpingWidget.ACTION_PREPARE_UPLOAD);
		
    	if(SensorService.getInstance() != null && SystemSensService.getInstance() != null){
    		// The data collection is on. Do this to avoid breaking/switching the 
    		// current data file.
    		this.sendBroadcast(systemDataUploadRequestIntent);
    		this.sendBroadcast(sensorDataUploadRequestIntent);
    		this.sendBroadcast(audioDataUploadRequestIntent);
    		
    	}else{
    		SystemDataDumpingWidget.broadcastUploadFiles(systemDataUploadRequestIntent, this);
    		SensorDataDumpingWidget.broadcastUploadFiles(sensorDataUploadRequestIntent, this);
    		AudioFeatureDumpingWidget.broadcastUploadFiles(audioDataUploadRequestIntent, this);
    	}
    	
    	// And upload the annotation file.
    	DataDumpingWidget.boradcastFileList(
        		new String[]{ Directory.ANNOTATION_DEFAULT_DATA_FOLDER + MobiSensService.getDeviceID(this) + "_" + Directory.RECORDER_TYPE_ANNOTATION + ".csv" }, 
        		String.valueOf(Directory.FILE_TYPE_ANNOTATION), 
        		false,
        		this);
    	
    	// Upload the log file as well.
    	DataDumpingWidget.boradcastFileList(
        		new String[]{ MobiSensLog.LOG_FILE_PATH }, 
        		String.valueOf(Directory.FILE_TYPE_LOG), 
        		true,
        		this);
    	
    	// Change the text and description of list item.
    	this.listItems.get(LST_PUSH_INDEX).setTitle(getString(R.string.activity_abort_pushing));
    	this.listItems.get(LST_PUSH_INDEX).setDescription(getString(R.string.activity_abort_pushing_description));
    	this.listItems.get(LST_PUSH_INDEX).setIconResourceId(R.drawable.abort_push_cloud);
    	this.getListView().invalidateViews();
    	
    	
    	Thread waitThread = new Thread(){
    		public void run(){
    			try {
    				// I know this is ugly....
    				// We should wait for all the ACK after broadcasting
    				// those files..
    				// But this is fast to implement..
					Thread.sleep(5 * 1000); 
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} 
    			// Tell the uploader everything is ready to be upload.
    	    	// The uploader widget is registered with NotificationService.
    	    	Intent pushIntent = new Intent(UploadWidget.ACTION_USER_FORCE_UPLOAD);
    	    	sendBroadcast(pushIntent);
    		}
    	};
    	waitThread.start();
    	
    }

	public void onEulaAgreedTo() {
		// TODO Auto-generated method stub
		Intent notifyServiceIntent = new Intent(this, NotificationService.class);
		this.startService(notifyServiceIntent);
	}
}
