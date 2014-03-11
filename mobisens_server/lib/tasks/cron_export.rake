require 'net/http'
require 'cgi'
require 'multipart'

task :cron_export, [:back_track_time_sec] => :environment do |t, args|

  begin
    log_file = File.open("#{Rails.configuration.root_path}/log/export_queue_#{Time.now.localtime.to_formatted_s(:number)}.log", "w+")
    # Import the annotation first
    export_mobisens_anno_to_lifelogger(false)

    back_track_time = args[:back_track_time_sec].to_i
    if back_track_time == 0
      back_track_time = 48 * 60 * 60
    end

    
    current_time = Time.now.utc.to_i
    time_boundary = current_time -  back_track_time

    # Only retrieve and export the accelerometer sensor data for the latest 48 hours.
    # Or there might be too much data for lifelogger to process.
    sensor_queueing_uploads = ExportQueue.find(:all,
      :conditions => ["generate_time >= ? AND exported = ? AND file_type = ?",
        time_boundary, false, 2])

    # For the location data, we export all data disregarding their upload time.
    sys_queueing_uploads = ExportQueue.find(:all,
      :conditions => ["exported = ? AND file_type = ?",
        false, 1])

    pc_sys_data_uploads = ExportQueue.find(:all, :conditions => ["exported = ? AND file_type = ?", false, 6])

    pc_mouse_data_uploads = ExportQueue.find(:all, :conditions => ["exported = ? AND file_type = ?", false, 8])

    queueing_uploads = []
    queueing_uploads = queueing_uploads.concat(sensor_queueing_uploads).concat(sys_queueing_uploads).
        concat(pc_sys_data_uploads).concat(pc_mouse_data_uploads)

    log_file.puts "Total upload: #{queueing_uploads.length}, Sensor uploads: #{sensor_queueing_uploads.length}, System uploads: #{sys_queueing_uploads.length}, PC System uploads: #{pc_sys_data_uploads.length}, PC Mouse Track uploads: #{pc_mouse_data_uploads.length}"

    log_file.puts "#{Time.now.to_formatted_s(:db)}: start to export.."

    log_file.puts "Export queue size: #{queueing_uploads.length}"
    log_file.flush

    lifelogger_base_url = 'http://lifelogger.sv.cmu.edu:3000'
    import_key = 'pang.wu@sv.cmu.edu'
    export_count = 0

    ignore_devices_list = []

    # We need to skip the device using fast sampling from Le's MobiSens.
    ignore_file = File.open("#{Rails.root}/ignore_devices.txt", "r")
    while line = ignore_file.gets
      ignore_devices_list << line.strip
    end
    ignore_file.close


    queueing_uploads.each do |item|

      if item.exported == true
        next
      end
      
      begin
        log_file.puts "#{Time.now.to_formatted_s(:db)}: Exporting item id: #{item.upload.id}, file: #{item.upload.record.path}, upload_at: #{Time.at(item.generate_time).to_formatted_s(:db)}"
        device_id = item.upload.device_id

        if ignore_devices_list.include?(device_id)
          next
        end
        # Step 1: Create User
        http_params = {:key => import_key, :device_id => device_id}
        user_id = API.http_get("#{lifelogger_base_url}", '/import/import_user_by_device_id', http_params)

        if user_id == 'false'
          log_file.puts "Create user for device #{device_id} failed."
          export_count += 1
          next
        else
          log_file.puts "Create user for device #{device_id} done."
        end
        log_file.flush

        file_type = item.upload.file_type
        import_data_url = "#{lifelogger_base_url}"

        case file_type
        when 1 # GPS data
          import_data_url += '/import/import_system_data_no_merge'
          device_type = 1
        when 2 # Sensor data
          import_data_url += '/import/import_sensor_data_no_merge'
          device_type = 1
        when 6
          import_data_url += '/import/import_pc_env_data'
          device_type = 2
        when 8
          import_data_url += '/import/import_pc_hid_data'
          device_type = 2
        else
          export_count += 1
          next # Just skip the annotation and log file
        end


        # Step 2: Create Session.
        date_start = DateTime.now.beginning_of_day.to_time.to_i
        date_end = DateTime.now.end_of_day.to_time.to_i

        http_params = {:key => import_key,
          :device_id => device_id,
          :date_start => date_start,
          :date_end => date_end,
          :device_type => device_type
        }
        session_id = API.http_get("#{lifelogger_base_url}", '/import/create_session_by_date', http_params)

        if session_id == 'false'
          log_file.puts "Create session started at #{date_start} for device #{device_id} failed.<br/>"
          export_count += 1
          next
        else
          log_file.puts "Create session started at #{date_start} for device #{device_id} done, session_id: #{session_id}.<br/>"
        end
        log_file.flush



        f = File.new(item.upload.record.path, "rb") # The data_file was closed before returned, so we need to new another file

        import_params = Hash.new


        # set the params to meaningful values
        import_params[:file] = f
        import_params[:key] = import_key
        import_params[:session_id] = session_id
        import_params[:device_id] = device_id

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

        result_string = ""
        is_export_success = false

        # res holds the response to the POST
        case status_code
        when -1
          result_string << "Import data failed, exception #{result[:exception]}, file_type: #{file_type}, session_id: #{session_id}, device_id: #{device_id}. \n"
        when Net::HTTPSuccess
          is_export_success = true
          result_string << "Import data completed, file_type: #{file_type}, session_id: #{session_id}, device_id: #{device_id}. \n"
        when Net::HTTPInternalServerError
          result_string << "Import data failed, file_type: #{file_type}, session_id: #{session_id}, device_id: #{device_id}. \n"
        else
          result_string << "Import data failed, file_type: #{file_type}, session_id: #{session_id}, device_id: #{device_id}. Unknown error #{status_code}: #{status_code.inspect} \n"
        end

        result_string << "file: #{item.upload.record.path} \n"
        result_string << "Return content: #{result[:content]} \n"
        log_file.puts result_string
        if is_export_success
          export_count += 1
        end

        log_file.puts "Total item #{queueing_uploads.length}, exported #{export_count}"
        log_file.flush
        item.exported = is_export_success

        if item.try_count == nil
          item.try_count = 1
        else
          item.try_count += 1

          if item.try_count > 20
            item.exported = true
          end
        end

        item.save!
      rescue => loop_exception
        log_file.puts "error while uplading item ##{item.id}, #{$!}"
        log_file.puts loop_exception.backtrace
      end
    end

    log_file.puts "All export done."
    
  rescue => exception
    log_file.puts "error:"
    log_file.puts exception.backtrace
  end
  log_file.close
end

task :cron_export_mobisens_anno => :environment do |t|
  export_mobisens_anno_to_lifelogger(true)
end

def export_mobisens_anno_to_lifelogger(is_test)
  log_file = File.open("#{Rails.configuration.root_path}/log/anno_queue_#{Time.now.localtime.to_formatted_s(:number)}.log", "w+")

  current_time = Time.now.utc.to_i
  time_boundary = current_time - 7 * 24 * 60 * 60

  anno_queueing_uploads = []
  
  if is_test == false
    anno_queueing_uploads = ExportQueue.find(:all,
      :conditions => ["generate_time >= ? AND exported = ? AND file_type = ?",
        time_boundary, false, 3])
  else

    anno_queueing_uploads = ExportQueue.find(:all,
      :conditions => ["generate_time >= ? AND file_type = ?",
        time_boundary, 3])
  end
  
  log_file.puts "Total upload: #{anno_queueing_uploads.length}"

  log_file.puts "#{Time.now.to_formatted_s(:db)}: start to export.."

  log_file.puts "Export queue size: #{anno_queueing_uploads.length}"
  log_file.flush

  lifelogger_base_url = 'http://mlt.sv.cmu.edu:3000'
  import_data_url = "#{lifelogger_base_url}/import/import_mobisens_annotations_by_device_id"
  import_key = 'pang.wu@sv.cmu.edu'
  export_count = 0

  user_anno = {}
  queue_indexed_by_device = {}

  anno_queueing_uploads.each do |item|

    if !is_test
      if item.exported == true
        next
      end
    end

    log_file.puts "#{Time.now.to_formatted_s(:db)}: Exporting item id: #{item.upload.id}, file: #{item.upload.record.path}, upload_at: #{Time.at(item.generate_time).to_formatted_s(:db)}"
    device_id = item.upload.device_id

    if user_anno.has_key?(device_id) == false
      user_anno[device_id] = []
      queue_indexed_by_device[device_id] = []
    end

    queue_indexed_by_device[device_id] << item
    f = File.open(item.upload.record.path, "rb")

    line = ""
    while((line = f.gets("\r\n")))
      line = line.gsub("\r\n", '')
      data_parts = line.split("\t")
      if data_parts.length < 2
        next
      end
      
      anno_part = data_parts[0]
      columns = anno_part.split(',')
      if columns.length == 3
        user_anno[device_id] << anno_part
      end

      if columns.length > 3
        #if columns[3] == 'false'
        user_anno[device_id] << anno_part # export the human anno.
        #end
      end
    end

    f.close
  end

  user_anno.each do |device_id, annos|
    log_file.puts "#{Time.now.to_formatted_s(:db)}: Exporting annotation for device: #{device_id}"
    log_file.flush

    content = annos.join("\r\n")

    log_file.puts "#{content}\r\n"
    log_file.flush

    import_params = Hash.new


    # set the params to meaningful values
    import_params[:annotations] = content
    import_params[:key] = import_key
    import_params[:device_id] = device_id

    # make a MultipartPost
    mp = Multipart::MultipartPost.new

    # Get both the headers and the query ready,
    # given the new MultipartPost and the params
    # Hash
    query, headers = mp.prepare_query(import_params)


    # Make sure the URL is useable
    url = URI.parse(import_data_url)

    # Do the actual POST, given the right inputs
    result = Multipart::post_form(url, query, headers)
    status_code = result[:status]

    result_string = ""
    is_export_success = false

    # res holds the response to the POST
    case status_code
    when -1
      result_string << "Import annotation failed, exception #{result[:exception]}, device_id: #{device_id}. \n"
    when Net::HTTPSuccess
      is_export_success = true
      result_string << "Import annotation completed, device_id: #{device_id}. \n"
    when Net::HTTPInternalServerError
      result_string << "Import annotation failed, device_id: #{device_id}. \n"
    else
      result_string << "Import data failed, device_id: #{device_id}. Unknown error #{status_code}: #{status_code.inspect} \n"
    end

    result_string << "Return content: #{result[:content]} \n"
    log_file.puts result_string
    log_file.flush

    queue_indexed_by_device[device_id].each do |item|
      item.exported = is_export_success

      if item.try_count == nil
        item.try_count = 1
      else
        item.try_count += 1

        if item.try_count > 4
          item.exported = true
        end
      end

      item.save!
    end
    
  end

  log_file.puts "All annotation export done."
  log_file.close
end

task :fix_export => :environment do |t|

  log_file = File.open("#{Rails.root}/log/export_queue_#{Time.now.localtime.to_formatted_s(:number)}.log", "w+")

  current_time = Time.now.utc.to_i
  time_boundary = current_time - 7 * 24 * 60 * 60
 
  queueing_uploads = ExportQueue.find(:all,
    :conditions => ["exported = ? AND created_at > ? AND created_at < ? AND file_type = ?",
      true,
      Time.at(0),
      Time.parse("2013-4-27"),
      1
    ])

  # queueing_uploads.delete_if { |item| item.upload.device_id != "357605040312238" }
  log_file.puts "Total upload: #{queueing_uploads.length}, Sensor uploads: #{queueing_uploads.length - queueing_uploads.length}, System uploads: #{queueing_uploads.length}"

  log_file.puts "#{Time.now.to_formatted_s(:db)}: start to export.."

  log_file.puts "Export queue size: #{queueing_uploads.length}"
  log_file.flush

  lifelogger_base_url = 'http://mlt.sv.cmu.edu:3000'
  import_key = 'pang.wu@sv.cmu.edu'
  export_count = 0


  queueing_uploads.each do |item|

    log_file.puts "#{Time.now.to_formatted_s(:db)}: Exporting item id: #{item.upload.id}, file: #{item.upload.record.path}, upload_at: #{Time.at(item.generate_time).to_formatted_s(:db)}"
    device_id = item.upload.device_id
    if device_id != '99000209841366'
      next
    end

    # Step 1: Create User
    http_params = {:key => import_key, :device_id => device_id}
    user_id = API.http_get("#{lifelogger_base_url}", '/import/import_user_by_device_id', http_params)

    if user_id == 'false'
      log_file.puts "Create user for device #{device_id} failed."
      export_count += 1
      next
    else
      log_file.puts "Create user for device #{device_id} done."
    end

    file_type = item.upload.file_type
    import_data_url = "#{lifelogger_base_url}"

    case file_type
    when 1 # GPS data
      import_data_url += '/import/import_system_data_no_merge'
    when 2 # Sensor data
      #import_data_url += '/import/import_sensor_data_no_merge'
    else
      export_count += 1
      next # Just skip the annotation and log file
    end


    # Step 2: Create Session.
    date_start = DateTime.now.beginning_of_day.to_time.to_i
    date_end = DateTime.now.end_of_day.to_time.to_i

    http_params = {:key => import_key,
      :device_id => device_id,
      :date_start => date_start,
      :date_end => date_end
      }

    session_id = API.http_get("#{lifelogger_base_url}", '/import/create_session_by_date', http_params)

    if session_id == 'false'
      log_file.puts "Create session started at #{date_start} for device #{device_id} failed.<br/>"
      export_count += 1
      next
    else
      log_file.puts "Create session started at #{date_start} for device #{device_id} done, session_id: #{session_id}.<br/>"
    end



    f = File.new(item.upload.record.path, "rb") # The data_file was closed before returned, so we need to new another file

    import_params = Hash.new


    # set the params to meaningful values
    import_params[:file] = f
    import_params[:key] = import_key
    import_params[:session_id] = session_id
    import_params[:device_id] = device_id

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

    result_string = ""
    is_export_success = false

    # res holds the response to the POST
    case status_code
    when -1
      result_string << "Import data failed, exception #{result[:exception]}, file_type: #{file_type}, session_id: #{session_id}, device_id: #{device_id}. \n"
    when Net::HTTPSuccess
      is_export_success = true
      result_string << "Import data completed, file_type: #{file_type}, session_id: #{session_id}, device_id: #{device_id}. \n"
    when Net::HTTPInternalServerError
      result_string << "Import data failed, file_type: #{file_type}, session_id: #{session_id}, device_id: #{device_id}. \n"
    else
      result_string << "Import data failed, file_type: #{file_type}, session_id: #{session_id}, device_id: #{device_id}. Unknown error #{status_code}: #{status_code.inspect} \n"
    end

    result_string << "file: #{item.upload.record.path} \n"
    result_string << "Return content: #{result[:content]} \n"
    log_file.puts result_string
    if is_export_success
      export_count += 1
    end

    log_file.puts "Total item #{queueing_uploads.length}, exported #{export_count}"
    log_file.flush

    # The fix export should not save anything
    #item.exported = is_export_success
    #item.save!
  end

  log_file.puts "All export done."
  log_file.close

end