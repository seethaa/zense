class ShareSessionsController < ApplicationController
  
  # POST /share_sessions
  # POST /share_sessions.xml
  def create
    if params[:key] != 'pang.wu@sv.cmu.edu'
      render :text => 'error', :status => 500
      return
    end
    
    device_id = params[:device_id]
    register_device device_id

    share_session = ShareSession.create(:owner_device_id => device_id)
    ShareSessionMember.create(:share_session_id => share_session.id, :member_id => device_id)
    render :text => "Circle #{share_session.id} was created, please mark down this session id for inviting other people to join the Circle."
    
  end

  def join
    if params[:key] != 'pang.wu@sv.cmu.edu'
      render :text => 'error', :status => 500
      return
    end

    device_id = params[:device_id]
    share_session_id = params[:session_id]
    
    register_device device_id

    
    joined_share_session = ShareSessionMember.find(:first, :conditions => ["member_id = ? AND share_session_id = ?", device_id, share_session_id])

    if joined_share_session != nil
      render :text => "You are already in Circle ##{share_session_id}"
    else
      share_session = ShareSession.find(:first, :conditions => ["id = ?", share_session_id])
      if share_session == nil
        render :text => "Circle ##{share_session_id} doesn't exist."
      else
        ShareSessionMember.create(:share_session_id => share_session_id, :member_id => device_id)
        render :text => "Join Circle ##{share_session_id} completed, please a moment for map refreshing..."
      end
    end

    
    
  end

  def left
    if params[:key] != 'pang.wu@sv.cmu.edu'
      render :text => 'error', :status => 500
      return
    end

    device_id = params[:device_id]
    share_session_id = params[:session_id]

    register_device device_id

    share_session = ShareSession.find(:first, :conditions => ["id = ?", share_session_id])

    if share_session == nil
      render :text => "Circle ##{share_session_id} doesn't exist."
    else
      ShareSessionMember.delete_all(["share_session_id = ? AND member_id = ?", share_session_id, device_id])
      render :text => "You have left Circle ##{share_session_id}."
    end
  end

  def get_my_circles
    if params[:key] != 'pang.wu@sv.cmu.edu'
      render :text => 'error', :status => 500
      return
    end
    device_id = params[:device_id]

    memberships_sessions = ShareSessionMember.find(:all, :conditions => ["member_id = ?", device_id])

    result_string = "-1"

    memberships_sessions.each do |share_session|
      result_string << ",#{share_session.share_session_id}"
    end

    render :text => result_string
    
  end

end
