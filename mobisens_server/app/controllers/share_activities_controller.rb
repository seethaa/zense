class ShareActivitiesController < ApplicationController
  skip_before_filter :verify_authenticity_token

  @@back_track_offset = 12 * 60 * 60 * 1000
  
  def push
    if params[:key] != 'pang.wu@sv.cmu.edu'
      render :text => 'error', :status => 500
      return
    end

    device_id = params[:device_id]

    register_device device_id

    ShareActivity.create(
      :device_id => device_id,
      :activity => params[:activity],
      :timestamp => params[:timestamp].to_i
    )

    ShareActivity.destroy_all(["device_id = ? AND timestamp < ?", params[:device_id], params[:timestamp].to_i - 24 * 60 * 60 * 1000])
    render :text => 'done'
  end


  def bundle_push
    if params[:key] != 'pang.wu@sv.cmu.edu'
      render :text => 'error', :status => 500
      return
    end


    device_id = params[:device_id]
    location_string = params[:location]
    activity = params[:activity]
    start_time = params[:start_time].to_i
    end_time = params[:end_time].to_i

    register_device device_id

    locations = location_string.split(",")

    index = 0
    time_step = ((end_time - start_time) / locations.length * 2).to_i
    time_span = 0
    
    while(index < locations.length - 1)
      ShareLocation.create(
        :device_id => device_id,
        :latitude => locations[index].to_f,
        :longitude => locations[index + 1].to_f,
        :timestamp => start_time + time_span
      )

      time_span += time_step
      index += 2
    end

    old_activity = ShareActivity.find(:first, :conditions => ["device_id = ? AND timestamp = ?", device_id, end_time])
    
    if old_activity != nil
      old_activity.activity = activity
      old_activity.save!
    else
      ShareActivity.create(
        :device_id => device_id,
        :activity => activity,
        :timestamp => end_time
      )
    end


    render :text => 'done'
  end


  def get_all
    if params[:key] != 'pang.wu@sv.cmu.edu'
      render :text => 'error', :status => 500
      return
    end

    result_string = ""

    device_id = params[:device_id]
    register_device device_id
    membership_sessions = ShareSessionMember.find(:all, :conditions => ["member_id = ?", device_id])
    sharing_users = {}

    membership_sessions.each do |membership_session|
      friends = ShareSessionMember.find(:all, :conditions => ["share_session_id = ?", membership_session.share_session_id])
      friends.each do |friend|

        if !sharing_users.has_key?(friend.member_id)
          sharing_users[friend.member_id] = 1
        end
        
      end
    end

    sharing_users.keys.each do |friend_device_id|
      recent_activities = ShareActivity.find(:all,
          :conditions => ["device_id = ? AND timestamp > ?", friend_device_id, params[:current_time].to_i - @@back_track_offset],
          :order => "`timestamp` DESC")

      recent_locations = ShareLocation.find(:all,
        :conditions => ["device_id = ? AND timestamp > ?", friend_device_id, params[:current_time].to_i - @@back_track_offset],
        :order => "`timestamp` ASC"
      )

      if recent_activities.length > 0
        result_string << "User ##{friend_device_id},#{recent_activities[0].activity},#{recent_locations.length}"

        recent_locations.each do |location|
          result_string << ",#{location.latitude},#{location.longitude}"
        end

        result_string << ";"
      end
      
    end
    

    render :text => result_string
  end
end
