require 'socket'

task :gateway_remove_backup => :environment do |t, args|
  host = Socket.gethostname
  if host != "beware.sv.cmu.edu"
    puts "This is not the gateway machine!"
    return
  end

  # Remove all records older than one week in the Gateway machine.
  # Since the file deletion crap is written in :before_destroy filer of a paperclip model
  # don't use delete_all or paperclip won't delete the attached files!
  Upload.destroy_all#(["upload_time < ?", Time.at(Time.now.to_i - 7 * 24 * 60 * 60)])
  ExportQueue.destroy_all
  
end


task :gateway_reupload => :environment do |t, args|
  host = Socket.gethostname
  if host != "beware.sv.cmu.edu"
    puts "This is not the gateway machine!"
    return
  end

  log_file = File.open("#{Rails.configuration.root_path}/log/redistriubte.log", "w+")
  export_items = ExportQueue.find(:all, :conditions => ["exported = ?", false])
  export_items.each do |export_item|
    upload = export_item.upload
    if upload == nil
      next
    end

    file = File.open(upload.record.path, "r")
    request_params = {:device_id => upload.device_id,
      :file => file,
      :upload_password => "pang.wu@sv.cmu.edu",
      :file_type => upload.file_type,
      :upload_time => upload.upload_time,
      :try_count => 0,

    }

    result = Gateway.distribute(request_params)

    case result[:status]
    when Net::HTTPSuccess, Net::HTTPRedirection
      # Successfully distributed to one of the storages.
      log_file.puts "#{Time.now}, upload done: #{upload.record.path}, file_type: #{upload.file_type}"
      upload.destroy
      export_item.exported = true
      export_item.save!

    else
      log_file.puts "#{Time.now}, upload failed: #{upload.record.path}, file_type: #{upload.file_type}"
      export_item.try_count += 1
      export_item.save!
    end

    file.close
  end


  log_file.close
end


task :distribute_data => :environment do |t, args|
  host = Socket.gethostname
  if host != "beware.sv.cmu.edu"
    puts "This is not the gateway machine!"
    return
  end

  log_file = File.open("#{Rails.configuration.root_path}/log/distriubte.log", "w+")
  export_items = ExportQueue.find(:all, :conditions => ["exported = ?", false])
  export_items.each do |export_item|
    upload = export_item.upload
    if upload == nil
      next
    end

    file = File.open(upload.record.path, "r")
    request_params = {:device_id => upload.device_id,
      :file => file,
      :upload_password => "pang.wu@sv.cmu.edu",
      :file_type => upload.file_type,
      :upload_time => upload.upload_time,
      :try_count => 0,

    }

    result = Gateway.distribute(request_params)

    case result[:status]
    when Net::HTTPSuccess, Net::HTTPRedirection
      # Successfully distributed to one of the storages.
      log_file.puts "#{Time.now}, upload done: #{upload.record.path}, file_type: #{upload.file_type}"
      upload.destroy
      export_item.destroy

    else
      log_file.puts "#{Time.now}, upload failed: #{upload.record.path}, file_type: #{upload.file_type}"
      export_item.try_count += 1
      export_item.save!
    end

    file.close
  end


  log_file.close
end