class ShareLocationsController < ApplicationController
  skip_before_filter :verify_authenticity_token
  
  def push
    if params[:key] != 'pang.wu@sv.cmu.edu'
      render :text => 'error', :status => 500
      return
    end

    device_id = params[:device_id]
    register_device device_id

    ShareLocation.create(
      :device_id => device_id,
      :latitude => params[:lat].to_f,
      :longitude => params[:lng].to_f,
      :timestamp => params[:timestamp].to_i
    )

    ShareLocation.destroy_all(["device_id = ? AND timestamp < ?", params[:device_id], params[:timestamp].to_i - 24 * 60 * 60 * 1000])
    render :text => 'done'
  end
end
