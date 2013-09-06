package edu.cmu.sv.mobisens.content;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.util.Log;
import edu.cmu.sv.mobisens.MobiSensService;
import edu.cmu.sv.mobisens.io.MobiSensLog;
import edu.cmu.sv.mobisens.net.HttpRequestForCMUSVProjects;
import edu.cmu.sv.mobisens.net.Network;
import edu.cmu.sv.mobisens.net.URLs;
import edu.cmu.sv.mobisens.settings.LocalSettings;
import edu.cmu.sv.mobisens.settings.ServiceParameters;

public class ProfileWidget extends PeriodicNetworkRequestWidget {
	
	
	private static final String CLASS_PREFIX = ProfileWidget.class.getName();
	private static final String TAG = CLASS_PREFIX;
	
	public final static String ACTION_SERVICE_PARAMETER_UPDATED = CLASS_PREFIX + ".action_service_params_updated";
	public final static String ACTION_LOAD_DEFAULT_PROFILE = CLASS_PREFIX + ".action_load_default_profile";
	public final static String ACTION_SET_SENSING_PROFILE = CLASS_PREFIX + ".action_set_profile";
	public final static String ACTION_PROFILE_EXPIRED = CLASS_PREFIX + ".action_profile_expired";
	
	public final static String EXTRA_SENSING_PROFILE = CLASS_PREFIX + ".extra_sensing_profile";
	public final static String EXTRA_PROFILE_NAME = CLASS_PREFIX + ".extra_sensing_profile_name";
	public final static String EXTRA_NEW_PROFILE = CLASS_PREFIX + ".extra_new_profile";
	public final static String EXTRA_OLD_PROFILE = CLASS_PREFIX + ".extra_old_profile";
	
	public final static String EXTRA_EXPIRED_PROFILE = CLASS_PREFIX + ".extra_expired_profile";
	//public final static String EXTRA_DEFAULT_PROFILE = CLASS_PREFIX + ".extra_default_profile";
	
	private boolean shouldSleep = false;
	
	private long timePassed = 0;
	private long lastWakeUpTimestamp = 0;
	
	private ServiceParameters defaultProfile = null;
	private ServiceParameters currentProfile = null;
	
	
	private long lastScreenOn;
	
	public ProfileWidget(){
		super(MobiSensService.getParameters()
				.getServiceParameter(
						ServiceParameters.GET_PROFILE_INTERVAL));
		this.lastWakeUpTimestamp = System.currentTimeMillis();
	}
	
	@Override
	protected void onChecked(long sleepTime){
		long expireTime = -1;
		
		if(this.currentProfile != null){
			this.timePassed += (System.currentTimeMillis() - this.lastWakeUpTimestamp);  // This doesn't work, if the shit crashed.
			this.lastWakeUpTimestamp = System.currentTimeMillis();
			expireTime = this.currentProfile.getServiceParameter(ServiceParameters.PROFILE_TIMEOUT);
			long effectiveTime = LocalSettings.getCurrentProfileEffectiveTime(getContext());
			
			// Check whether the current profile should expire.
			if(System.currentTimeMillis() - effectiveTime >= expireTime && expireTime > 0){
				// Tell everyone the current profile expired!
				Intent expireIntent = new Intent(ProfileWidget.ACTION_PROFILE_EXPIRED);
				
				expireIntent.putExtra(ProfileWidget.EXTRA_EXPIRED_PROFILE, this.currentProfile.toString());
				this.getContext().sendBroadcast(expireIntent);
				
				// reset the current profile to default profile.
				if(this.defaultProfile != null){
					this.setAndBroadcastNewProfile(this.defaultProfile, this.currentProfile);
				}else{
					this.loadAndBroadcastDefaultProfile();
				}
				
			}
		}else{
		
			loadAndBroadcastDefaultProfile();
		}
	}
	
	
	private void loadAndBroadcastDefaultProfile(){
		
		AsyncTask<String, Void, ServiceParameters> task = new AsyncTask<String, Void, ServiceParameters>(){

			@Override
			protected ServiceParameters doInBackground(String... params) {
				if(params.length > 0 && Network.isNetworkConnected(getContext())){
					// TODO Auto-generated method stub
					return getDefaultProfileFromServer(params[0]);
				}else{
					ServiceParameters profile = ServiceParameters.fromProfileString(ServiceParameters.DEFAULT_PROFILE_STRING);
					profile.setName(ServiceParameters.DEFAULT_PROFILE_NAME);
					return profile;
				}
			}
			
			@Override
			protected void onPostExecute(ServiceParameters result){
				if(result == null){
					Log.i(TAG, "Get default profile failed, nothing returned.");
					MobiSensLog.log("Get default profile failed, nothing returned.");
					return;
				}
				
				result.setName(ServiceParameters.DEFAULT_PROFILE_NAME);
				
				defaultProfile = result;
				setAndBroadcastNewProfile(result, MobiSensService.getParameters());
				
				Log.i(TAG, "Get default profile done.");
				MobiSensLog.log("Get default profile done.");
			}
			
		};
		
		task.execute(new String[]{ getDeviceID() });
	}
	
	public static ServiceParameters getDefaultProfileFromServer(String deviceId){
		HttpRequestForCMUSVProjects httpRequest = 
	    	new HttpRequestForCMUSVProjects(
	    			URLs.DEFAULT_GET_PROFILE_URL,
	    			URLs.DEFAULT_UPLOAD_URL, 
	    			HttpRequestForCMUSVProjects.DEFAULT_UPLOADKEY);
		
		String profileString = httpRequest.getDeviceProfile(deviceId);
		
		if(!profileString.equals("")){
			ServiceParameters profile = ServiceParameters.fromProfileString(profileString);
			profile.setName(ServiceParameters.DEFAULT_PROFILE_NAME);
			
			return profile;
		}
		
		return null;
	}
	
	/*
	 * Update the new profile to all the sensor services.
	 * Please DONOT set the profile ID here, there are two reasons:
	 * 1) It should be set at the UI activity, since it is the UI activity who get these crap.
	 * 2) The id will lost in the broadcast serialization/deserialization process. You can't get the correct
	 *    id here, and I have no plan to fix it. Id is not something should belongs to the profile content.
	 *    
	 * - by Pang, if you have questions, please email them to pang.wu@hotmail.com
	 * 
	 * Oh shit, I changed my mine, losing the id in serialization is a bad idea. It makes the
	 * MobiSensLauncher and ProfileWidget tightly coupled. You have the id now and every 'set'
	 * action has to be finished here.
	 */
	private void setAndBroadcastNewProfile(ServiceParameters newParams, ServiceParameters oldParams){
		String newProfileString = newParams.toString();
		String oldProfileString = oldParams.toString();
		
		long newWakupInterval = newParams.getServiceParameter(ServiceParameters.GET_PROFILE_INTERVAL);
		this.setWakeupInterval(newWakupInterval);
		
		MobiSensService.setParameters(newParams);
		MobiSensLog.log(newParams.toString());
		Log.i(TAG, newParams.toString());
		
		this.currentProfile = newParams;  // Yes, this is ugly...
		
		// Set all the info here, then the UI will read them once it is activated.
		LocalSettings.setCurrentProfile(getContext(), newParams.getId());
		Log.i(TAG, "Profile with id " + newParams.getId() + " set.");
		MobiSensLog.log("Profile with id " + newParams.getId() + " set.");
		
		LocalSettings.setCurrentProfileEffectiveTime(getContext(), System.currentTimeMillis());
		
		
		Intent serviceParamsUpdatedIntent = new Intent(ACTION_SERVICE_PARAMETER_UPDATED);
		serviceParamsUpdatedIntent.putExtra(EXTRA_NEW_PROFILE, newProfileString);
		serviceParamsUpdatedIntent.putExtra(EXTRA_OLD_PROFILE, oldProfileString);
		serviceParamsUpdatedIntent.putExtra(EXTRA_PROFILE_NAME, newParams.getName());
		
		this.getContext().sendBroadcast(serviceParamsUpdatedIntent);
	}
	
	public void register(ContextWrapper contextWrapper){
		IntentFilter filter = new IntentFilter(ProfileWidget.ACTION_LOAD_DEFAULT_PROFILE);
		filter.addAction(Intent.ACTION_SCREEN_ON);
		filter.addAction(Intent.ACTION_SCREEN_OFF);
		filter.addAction(ProfileWidget.ACTION_SET_SENSING_PROFILE);
		
		this.setFilter(filter);
		
		super.register(contextWrapper);
	}
	
	public void onReceive(Context context, Intent intent) {
		super.onReceive(context, intent);
		// TODO Auto-generated method stub
		
		String action = intent.getAction();
		
		if(action.equals(ACTION_LOAD_DEFAULT_PROFILE)){
			loadAndBroadcastDefaultProfile();
		}
		
		if(Intent.ACTION_SCREEN_OFF.equals(action)){
			this.setShouldSleep(true);
		}
		
		if(Intent.ACTION_SCREEN_ON.equals(action)){
			this.setShouldSleep(false);
			
			/*
			if(System.currentTimeMillis() - lastScreenOn > 60 * 60 * 1000){
				this.setForceWakeUp(true);
				MobiSensLog.log("Set force get profile.");
			}
			
			this.lastScreenOn = System.currentTimeMillis();
			*/
		}
		
		
		// Use selected a special profile on the action bar..
		if(ProfileWidget.ACTION_SET_SENSING_PROFILE.equals(action)){
			String newProfileString = intent.getStringExtra(EXTRA_SENSING_PROFILE);
			ServiceParameters newParams = ServiceParameters.fromProfileString(newProfileString);
			newParams.setName(intent.getStringExtra(EXTRA_PROFILE_NAME));
			
			this.setAndBroadcastNewProfile(newParams, MobiSensService.getParameters());
		}

	}
	
	private void setShouldSleep(boolean shouldSleep) {
		this.shouldSleep = shouldSleep;
	}

	private boolean isShouldSleep() {
		return shouldSleep;
	}
}
