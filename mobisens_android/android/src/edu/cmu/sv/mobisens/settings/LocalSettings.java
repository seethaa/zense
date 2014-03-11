package edu.cmu.sv.mobisens.settings;

import edu.cmu.sv.mobisens.ui.SwitcherActivity;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class LocalSettings {
	private final static String SETTING_SHARING = "sharing";
	private final static String IS_PUSHING = "is_pushing";
	private final static String CURRENT_PROFILE = "current_profile_id";
	private final static String ACTIVITY_VIEW = "view";
	private final static String V4_MIGRATED = "v4_migrated";
	private final static String V5_MIGRATED = "v5_migrated";
	private static final String SELECTED_MAP_TYPE = "selected_map_type";
	private static final String PROFILE_EFFECTIVE_TIME = "profile_effective_time";
	
	public static boolean isPushing(Context caller){
		SharedPreferences settings = caller.getSharedPreferences(LocalSettings.class.getName(), 0);
		return settings.getBoolean(LocalSettings.IS_PUSHING, false);
	}
	
	public static void setPushing(Context caller, boolean pushing){
		SharedPreferences settings = caller.getSharedPreferences(LocalSettings.class.getName(), 0);

		Editor edit = settings.edit();
		edit.putBoolean(LocalSettings.IS_PUSHING, pushing);
		edit.commit();
	}
	
	public static boolean isSharing(Context caller){
		SharedPreferences settings = caller.getSharedPreferences(LocalSettings.class.getName(), 0);
		return settings.getBoolean(SETTING_SHARING, false);
	}
	
	public static void setSharing(Context caller, boolean sharing){
		SharedPreferences settings = caller.getSharedPreferences(LocalSettings.class.getName(), 0);

		Editor edit = settings.edit();
		edit.putBoolean(SETTING_SHARING, sharing);
		edit.commit();
	}
	
	public static String getView(Context caller){
		SharedPreferences settings = caller.getSharedPreferences(LocalSettings.class.getName(), 0);
		return settings.getString(ACTIVITY_VIEW, SwitcherActivity.MAP_VIEW);
	}
	
	public static void setView(Context caller, String view){
		SharedPreferences settings = caller.getSharedPreferences(LocalSettings.class.getName(), 0);

		Editor edit = settings.edit();
		edit.putString(ACTIVITY_VIEW, view);
		edit.commit();
	}
	
	public static boolean isV4Migrated(Context caller){
		SharedPreferences settings = caller.getSharedPreferences(LocalSettings.class.getName(), 0);
		return settings.getBoolean(V4_MIGRATED, false);
	}
	
	public static void setV4Migrated(Context caller, boolean sharing){
		SharedPreferences settings = caller.getSharedPreferences(LocalSettings.class.getName(), 0);

		Editor edit = settings.edit();
		edit.putBoolean(V4_MIGRATED, sharing);
		edit.commit();
	}
	
	public static boolean isV5Migrated(Context caller){
		SharedPreferences settings = caller.getSharedPreferences(LocalSettings.class.getName(), 0);
		return settings.getBoolean(V5_MIGRATED, false);
	}
	
	public static void setV5Migrated(Context caller, boolean sharing){
		SharedPreferences settings = caller.getSharedPreferences(LocalSettings.class.getName(), 0);

		Editor edit = settings.edit();
		edit.putBoolean(V5_MIGRATED, sharing);
		edit.commit();
	}
	
	public static long getCurrentProfileEffectiveTime(Context caller){
		SharedPreferences settings = caller.getSharedPreferences(LocalSettings.class.getName(), 0);
		long effectiveTime = settings.getLong(PROFILE_EFFECTIVE_TIME, -1);
		
		return effectiveTime;
	}
	
	public static void setCurrentProfileEffectiveTime(Context caller, long effectiveTime){
		SharedPreferences settings = caller.getSharedPreferences(LocalSettings.class.getName(), 0);
		Editor edit = settings.edit();
		edit.putLong(PROFILE_EFFECTIVE_TIME, effectiveTime);
		
		edit.commit();
	}
	
	public static long getCurrentProfile(Context caller){
		SharedPreferences settings = caller.getSharedPreferences(LocalSettings.class.getName(), 0);

		long profileId = settings.getLong(CURRENT_PROFILE, 0);
		return profileId;
	}
	
	public static void setCurrentProfile(Context caller, long id){
		SharedPreferences settings = caller.getSharedPreferences(LocalSettings.class.getName(), 0);

		Editor edit = settings.edit();
		edit.putLong(LocalSettings.CURRENT_PROFILE, id);
		
		edit.commit();
	}
	
	public static int getSelectedMapType(Context caller){
		SharedPreferences settings = caller.getSharedPreferences(LocalSettings.class.getName(), 0);

		int profileId = settings.getInt(SELECTED_MAP_TYPE, 0);
		return profileId;
	}
	
	public static void setSelectedMapType(Context caller, int id){
		SharedPreferences settings = caller.getSharedPreferences(LocalSettings.class.getName(), 0);

		Editor edit = settings.edit();
		edit.putInt(LocalSettings.SELECTED_MAP_TYPE, id);
		
		edit.commit();
	}
}
