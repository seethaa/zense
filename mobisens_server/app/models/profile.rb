class Profile < ActiveRecord::Base
  has_many :device_controls, :dependent => :delete_all
  before_destroy :check_special_profiles

  has_attached_file :photo
  
  def self.default_profile
    return "accelerometer:on;\n" +
    "compass:on;\n" +
    "battery_status:on;\n" +
    "call_monitor:on;\n" +
    "gps:on;\n" +
    "gyro:on;\n" +
    "light:on;\n" +
    "orientation:on;\n" +
    "temperature:on;\n" +
    "wifi_scan:on;\n" +
    "battery_status_scan_interval:120000;\n" +
    "gps_dump_interval:120000;\n" +
    "sensor_sampling_interval:200;\n" +
    "wifi_scan_interval:120000;\n" +
    "wifi_outdoor_scan_interval:1200000;\n" +
    "upload_interval:3600000;\n" +
    "get_profile_interval:120000;\n" +
    "annotation_request_interval:1800000;\n" +
    "gps_slowstart_threshold:600000;\n" +
    "gps_samelocation_distance:20;\n" +
    "wifi_samelocation_intersect_percentage:50;\n" +
    "upload_when_charging_only:false;\n" +
    "activity_merge_threshold:60000;\n" +
    "similar_activity_threshold:51;\n" +
    "collection_data_size:600;\n" +
    "lifelogger_window_size:26;\n" +
    "lifelogger_step_size:13;\n" +
    "ngram_max_n:4;\n" +
    "cos_similarity_threshold:80;\n" +
    "similar_activity_threshold_2:40;\n" +
    "edge_distance:3;\n" +
    "max_sample_length:5;\n" +
    "proc_scan_interval:60000;\n" +
    "gps_open_window:15000;\n" +
    "phone_wakeup_duration:20000;\n" +
    "max_gps_cycle:20;\n" +
    "profile_timeout:7200000;\n" +
    "debug_mode:false;"
  end


  def check_special_profiles
    SpecialProfiles.destroy_all(:profile_id => self.id)
  end
end
