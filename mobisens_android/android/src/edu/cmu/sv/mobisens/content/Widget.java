package edu.cmu.sv.mobisens.content;

import java.util.Iterator;

import edu.cmu.sv.mobisens.MobiSensService;
import edu.cmu.sv.mobisens.io.MobiSensLog;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;

public abstract class Widget extends BroadcastReceiver {
	private static final String TAG = "Widget";
	
	private ContextWrapper context = null;
	protected IntentFilter filter = null;
	protected String deviceID = null;
	
	protected abstract String[] getActions();
	protected void beforeRegistered(ContextWrapper context){
		
	}
	
	protected void setFilter(IntentFilter filter){
		if(this.filter == null)
			this.filter = filter;
		
		if(filter == null)
			return;
		
		if(filter != this.filter){
			Iterator<String> actions = filter.actionsIterator();

			while(actions.hasNext()){
				String action = actions.next();
				this.filter.addAction(action);
			}
		}
	}
	
	protected String getDeviceID(){
		return this.deviceID;
	}

	public void register(ContextWrapper contextWrapper){
		if(contextWrapper == null)
			return;
		
		if(this.getContext() != null)
			this.unregister();
		this.setContext(contextWrapper);
		
		if(this.filter == null)
			this.filter = new IntentFilter();
		
		this.beforeRegistered(contextWrapper);
		
		String[] actions = this.getActions();
		for(String action:actions){
			this.filter.addAction(action);
		}
		
		this.getContext().registerReceiver(this, filter);
		this.deviceID = MobiSensService.getDeviceID(getContext());
		
		Log.i(TAG, "Widget: " + this.getClass().getName() + " registered.");
		MobiSensLog.log("Widget: " + this.getClass().getName() + " registered.");
		
	}
	
	public void unregister(){
		if(this.getContext() == null)
			return;

		this.getContext().unregisterReceiver(this);
		
		this.setContext(null);
		
		this.filter = null;
		
		Log.i(TAG, "Widget: " + this.getClass().getName() + " unregistered.");
		MobiSensLog.log("Widget: " + this.getClass().getName() + " unregistered.");
	}
	
	public boolean hasRegistered(){
		return this.getContext() != null;
	}
	
	
	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub

	}

	protected void setContext(ContextWrapper context) {
		this.context = context;
	}

	public ContextWrapper getContext() {
		return context;
	}
}
