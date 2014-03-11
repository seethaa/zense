package edu.cmu.sv.mobisens.net;

public class URLs {
	public static final String MOBISENS_HOST_URL = "http://beware.sv.cmu.edu:3000";
	public static final String LIFELOGGER_HOST_URL = "http://mlt.sv.cmu.edu:3000";
	
	//public static final String HOST_URL = "http://192.168.1.66:3000";
	public final static String GET_UNREAD_MESSAGECOUNT_URL = MOBISENS_HOST_URL + "/messages/get_unread";
	//public final static String MESSAGEBOX_BASE_URL = MOBISENS_HOST_URL + "/messages/device_index";
	public final static String MESSAGEBOX_CONNECTION_URL = MOBISENS_HOST_URL + "/messages/check_connection";
	public final static String ADDRESS_UPLOAD_URL = MOBISENS_HOST_URL + "/address/upload_address/";
	public final static String ADDRESS_DOWNLOAD_URL = MOBISENS_HOST_URL + "/address/get_address";
	public final static String GET_SHARED_ACTIVITIES_URL = MOBISENS_HOST_URL + "/share_activities/get_all";
	public final static String SHARE_LOCATION_URL = MOBISENS_HOST_URL + "/share_locations/push";
	public final static String SHARE_ACTIVITY_URL = MOBISENS_HOST_URL + "/share_activities/push";
	public final static String BUNDLE_SHARE_URL = MOBISENS_HOST_URL + "/share_activities/bundle_push";
	
	public final static String CREATE_SHARING_SESSION_URL = MOBISENS_HOST_URL + "/share_sessions/create";
	public final static String JOIN_SHARING_SESSION_URL = MOBISENS_HOST_URL + "/share_sessions/join";
	public final static String LEFT_SHARING_SESSION_URL = MOBISENS_HOST_URL + "/share_sessions/left";
	public final static String GET_SHARING_SESSIONsS_URL = MOBISENS_HOST_URL + "/share_sessions/get_my_circles";
	
	public final static String GET_SERVER_MESSAGES_URL = MOBISENS_HOST_URL + "/messages/show_json";
	public final static String SET_MESSAGES_AS_READ_URL = MOBISENS_HOST_URL + "/messages/read_message";
	
	// I don't get the ip out since this URL can be different from the upload url
	public static final String DEFAULT_GET_PROFILE_URL = MOBISENS_HOST_URL + "/device_controls/get_config";
	public static final String GET_SPECIAL_PROFILE_URL = MOBISENS_HOST_URL + "/special_profiles/get_all";
	
	public static final String DEFAULT_UPLOAD_URL = MOBISENS_HOST_URL + "/uploads/create";
	public static final String DEFAULT_VIDEO_UPLOAD_URL = MOBISENS_HOST_URL + "/videos/create";
	
	
	//public static final String LIFELOGGER_HOST_URL = "http://192.168.1.65:3000";
	public static final String GET_RANDOM_PREPROCESSED_SESSION_URL = MOBISENS_HOST_URL + "/centroid/pick_preprocessed_session_by_random";
	public static final String GET_CENTROIDS_URL = MOBISENS_HOST_URL + "/centroid/get_acc_centroids_by_session_id";
	
	// Use model URL for SenSec
	public static final String SENSEC_USER_MODEL_URL = MOBISENS_HOST_URL + "/sen_sec_model/get_model";
	
	// Lifelogger API for location based activity recognition
	public static final String LOCATION_BASED_RECOGNITION_URL = LIFELOGGER_HOST_URL + "/api/classify_by_location_from_device";
	
	
}
