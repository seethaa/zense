require 'multipart'

class ApiController < ApplicationController
  before_filter :require_user, :only => [:export]

  def data
    if params.has_key?(:start) && params.has_key?(:end) && params.has_key?(:device_id) &&
        params.has_key?(:file_type) && params.has_key?(:download_key)

      if params[:download_key] == 'pang.wu@sv.cmu.edu'
        data_file = API.get_data(params[:start].to_i, params[:end].to_i, params[:device_id], params[:file_type])
        file_path = data_file.path
        send_file file_path

      end
    else
      render :text => ""
    end
  end


  def device_list
    if params.has_key?(:download_key)
      device_ids = []
      if params[:download_key] == 'pang.wu@sv.cmu.edu'
        device_ids = API.get_devices
      end
      render :text => device_ids.to_json
    else
      render :text => ""
    end
  end

  def export
    @devices = []

    Address.find(:all).each do |address|
      @devices << {:id => address.device_id, :name => address.device_id}
    end
    @devices << {:id => '0', :name => 'All'}

    
  end

  def do_export

    render :text => "Not avaibale anymore."

    return

    
    device_id = params[:device_id]
    device_ids = []
    if device_id == '0'
      Address.find(:all).each do |address|
        device_ids << address.device_id
      end
    else
      device_ids << device_id
    end


    lifelogger_base_url = 'http://mlt.sv.cmu.edu:3000'
    #lifelogger_base_url = 'http://localhost:3000'
    import_key = 'pang.wu@sv.cmu.edu'
    result_string = ""
    
    device_ids.each do |id|

      # Step 1: Create user
      http_params = {:key => import_key, :device_id => id}
      user_id = API.http_get(lifelogger_base_url, '/import/import_user_by_device_id', http_params)

      if user_id == 'false'
        result_string << "Import user #{id} failed.<br/>"
        next
      else
        result_string << "Import user #{id} done, user_id: #{user_id}.<br/>"
      end

      # Step 2: Create session.
      http_params = {:key => import_key,
        :device_id => id,
        :start_timestamp => params[:start_timestamp],
        :end_timestamp => params[:end_timestamp]
        }
      session_id = API.http_get(lifelogger_base_url, '/import/create_session_by_device_id', http_params)

      if session_id == 'false'
        result_string << "Create session #{id} failed.<br/>"
        next
      else
        result_string << "Create session #{id} done, session_id: #{session_id}.<br/>"
      end

      # Step 3: Import data
      file_types = [1,2]
      file_types.each do |file_type|

        data_file = API.get_data(params[:start_timestamp].to_i, 
          params[:end_timestamp].to_i, 
          params[:device_id], file_type)

        import_data_url = lifelogger_base_url
        
        if file_type == 1 # GPS data
          import_data_url += '/import/import_system_data'

        end

        if file_type == 2 # Sensor data
          import_data_url += '/import/import_sensor_data'

        end

        f = File.new(data_file.path, "rb") # The data_file was closed before returned, so we need to new another file

        import_params = Hash.new


        # set the params to meaningful values
        import_params[:file] = f
        import_params[:key] = import_key
        import_params[:session_id] = session_id

        # make a MultipartPost
        mp = Multipart::MultipartPost.new

        # Get both the headers and the query ready,
        # given the new MultipartPost and the params
        # Hash
        query, headers = mp.prepare_query(import_params)

        # done with file now
        f.close

        # Make sure the URL is useable
        url = URI.parse(import_data_url)

        # Do the actual POST, given the right inputs
        result = Multipart::post_form(url, query, headers)
        status_code = result[:status]

        # res holds the response to the POST
        case status_code
        when -1
          result_string << "Import data failed, file_type: #{file_type}, session_id: #{session_id}, device_id: #{id}. <br/>"
        when Net::HTTPSuccess
          result_string << "Import data completed, file_type: #{file_type}, session_id: #{session_id}, device_id: #{id}. <br/>"
        when Net::HTTPInternalServerError
          result_string << "Import data failed, file_type: #{file_type}, session_id: #{session_id}, device_id: #{id}. <br/>"
        else
          result_string << "Import data failed, file_type: #{file_type}, session_id: #{session_id}, device_id: #{id}. Unknown error #{status_code}: #{status_code.inspect} <br/>"
        end

        #File.delete f.path
        result_string << "Timestamps, start: #{params[:start_timestamp]}, end: #{params[:start_timestamp]} <br/>"
        result_string << "Temp file: #{f.path} <br/>"
        result_string << "Return content: #{result[:content]} <br/>"
        result_string << "--------------------<br/>"
        puts f.path
          
      end
      
    end

    @result = result_string
  end

  def total_hours
    if params[:key] != 'pang.wu@sv.cmu.edu'
      render :text => 'error', :status => 500
      return
    end

    stats = API.get_total_hours(2)
    render :text => "total hours: #{stats[0].to_i}, error: #{stats[2].to_i}, total files: #{stats[1].to_i}"
    
  end

  def total_users
    if params[:key] != 'pang.wu@sv.cmu.edu'
      render :text => 'error', :status => 500
      return
    end

    stats = API.get_total_users
    render :text => "users who had provided data: #{stats}"

  end

  def gps_intervals
    if params[:key] != 'pang.wu@sv.cmu.edu'
      render :text => 'error', :status => 500
      return
    end

    intervals = API.gps_sampling_intervals
    out_string = ''
    intervals.each do |interval|
      out_string << "#{interval}<br/>"
    end

    render :text => out_string
  end

  def anno_stat
    if params[:key] != 'pang.wu@sv.cmu.edu'
      render :text => 'error', :status => 500
      return
    end

    strings = API.anno_stat


    render :text => "#{strings[0]}<br/>#{strings[1]}"
  end


  def get_user_with_anno_count
    if params[:key] != 'pang.wu@sv.cmu.edu'
      render :text => 'error', :status => 500
      return
    end

    render :text => API.user_with_anno_count
  end


  def get_user_install_time
    if params[:key] != 'pang.wu@sv.cmu.edu'
      render :text => 'error', :status => 500
      return
    end

    render :text => API.user_install_time
  end


  def export_queues
    if params[:key] != 'pang.wu@sv.cmu.edu'
      render :text => 'error', :status => 500
      return
    end

    result_string = ""

    time_boundary = Time.now.utc.to_i - 7 * 24 * 60 * 60
    queueing_uploads = ExportQueue.find(:all, :conditions => ["generate_time > ?", time_boundary])
    queueing_uploads.each do |queueing_upload|
      if queueing_upload.file_type == nil
        queueing_upload.file_type = queueing_upload.upload.file_type
        queueing_upload.save!
      end
      result_string << "id: #{queueing_upload.upload.id}, #{queueing_upload.exported}, #{queueing_upload.try_count}, #{Time.at(queueing_upload.generate_time)}<br/>"
    end


    render :text => result_string
  end

  private


  
end
