package edu.cmu.sv.mobisens.ui;

import java.util.ArrayList;


import edu.cmu.sv.mobisens.content.ProfileWidget;
import edu.cmu.sv.mobisens.io.MobiSensLog;
import edu.cmu.sv.mobisens.settings.LocalSettings;
import edu.cmu.sv.mobisens.settings.ServiceParameters;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.GridView;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.TextView;

public class HorizontalGridView extends LinearLayout {

	public HorizontalGridView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}
	private ArrayList<View> subViews = new ArrayList<View>();
	private LinearLayout container = null;
	private ListAdapter adapter = null;
	private LinearLayout layout = null;
	/*
	private final BroadcastReceiver receiver = new BroadcastReceiver(){

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			String action = intent.getAction();
			if(ProfileWidget.ACTION_SERVICE_PARAMETER_UPDATED.equals(action)){
				ServiceParameters selectedProfile = ServiceParameters.fromProfileString(intent.getStringExtra(ProfileWidget.EXTRA_NEW_PROFILE));
				selectedProfile.setName(intent.getStringExtra(ProfileWidget.EXTRA_PROFILE_NAME));
				LocalSettings.setCurrentProfile(context, selectedProfile.getId());   //HorizontalGridView.this is important
				
				//Is there a better way to get parent of parent??
				View parent = (View)layout.getParent();
				View profilesView = (View)parent.getParent();
				((TextView)profilesView.findViewById(R.id.tvSelectedMode)).setText("Current Sensing Mode: " + selectedProfile.getName());
			}
		}
		
	};
	
	protected void onWindowVisibilityChanged(int visibility){
		if(visibility == View.VISIBLE){
			ServiceParameters selectedProfile = LocalSettings.getCurrentProfile(getContext());
			if(selectedProfile != null){
				View parent = (View)layout.getParent();
				View profilesView = (View)parent.getParent();
				((TextView)profilesView.findViewById(R.id.tvSelectedMode)).setText("Current Sensing Mode: " + selectedProfile.getName());
			}
			
			this.getContext().registerReceiver(receiver, new IntentFilter(ProfileWidget.ACTION_SERVICE_PARAMETER_UPDATED));
		}else{
			try{
				this.getContext().unregisterReceiver(receiver);
			}catch(Exception ex){
				ex.printStackTrace();
				MobiSensLog.log(ex);
			}
		}
	}
	
	public HorizontalGridView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		
		
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
		this.layout  = (LinearLayout)inflater.inflate(R.layout.special_profile_list, this);
    	this.container = (LinearLayout) this.layout.findViewById(R.id.profile_container);
    	
	}

	public void setAdapter(ListAdapter adapter) {
		this.container.removeAllViews();
		this.subViews.clear();
		this.adapter = adapter;
		
		// TODO Auto-generated method stub
		for(int i = 0; i < adapter.getCount(); i++){
			View itemView = adapter.getView(i, null, this);
			if(itemView.getParent() != this){
				this.container.addView(itemView);
				this.subViews.add(itemView);
				itemView.setOnClickListener(specialProfileButtonClicked);
			}
		}
		
	}
	
	public void invalidate(){
		super.invalidate();
//		added null check to avoid exceptions if there is no net connection
		if(subViews != null){
			for(int i = 0; i < subViews.size(); i++){
				this.adapter.getView(i, this.subViews.get(i), this);
			}
		}
		

	}

	
	
	private OnClickListener specialProfileButtonClicked = new OnClickListener() {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			int position = (Integer) v.getTag();
			ServiceParameters profile = (ServiceParameters) adapter.getItem(position);
			AlertDialog dialog = new AlertDialog.Builder(v.getContext()).create();
			dialog.setTitle(R.string.message_dialog_title);
			dialog.setMessage(profile.getServiceParameterString(ServiceParameters.SWITCH_MESSAGE));
			dialog.setButton("OK", (DialogInterface.OnClickListener)null);
			dialog.setIcon(R.drawable.message_icon);
			
			dialog.show();
			
			// Tell the registered ProfileWidgets to update the profiles.
			Intent setProfileIntent = new Intent(ProfileWidget.ACTION_SET_SENSING_PROFILE);
			setProfileIntent.putExtra(ProfileWidget.EXTRA_SENSING_PROFILE, profile.toString());
			setProfileIntent.putExtra(ProfileWidget.EXTRA_PROFILE_NAME, profile.getName());
			v.getContext().sendBroadcast(setProfileIntent);
			
		} 
		
	};
	
	*/
}
