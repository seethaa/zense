task :export_data_from, [:device_id, :file_type,:start_time_ms] => :environment do |t, args|
  export_by_type(args[:device_id], args[:file_type].to_i, args[:start_time_ms].to_i, -1)
end


task :export_data_in, [:device_id, :file_type,:start_time_ms,:end_time_ms] => :environment do |t, args|
  export_by_type(args[:device_id], args[:file_type].to_i, args[:start_time_ms].to_i, args[:end_time_ms].to_i)
end

task :extract_features => :environment do |t|
  # export_by_type 1
  # export_by_type 2
  # export_by_type 3
  extract_features 12
end

task :bulk_export => :environment do |t|
  user_list_file = File.open("#{Rails.root}/export_data/user_list.txt", "r")
  devices = []
  
  while device_id = user_list_file.gets
    devices << device_id.strip
  end

  user_list_file.close
  file_types = [1, 2]
  export_root_path = "#{Rails.root}/export_data"
  
  file_types.each do |file_type|
    devices.each do |device_id|
      
      out_file = File.open("#{export_root_path}/#{device_id}_#{file_type}.csv", "w+")
      uploads = Upload.find(:all, :conditions => ["device_id = ? AND file_type = ?",
          device_id,
          file_type
        ])

      uploads.each do |upload|
        current_file = File.open(upload.record.path, "r")

        while(line = current_file.gets)
          out_file.puts line

        end
        current_file.close
      end

      out_file.close

    end
  end
  
end



task :export_unlocin => :environment do |t|
  device_id = "355031041215563"
  export_root_path = "#{Rails.root}/export_data_le"
  out_file = File.open("#{export_root_path}/#{device_id}_system_file.csv", "w+")
  
  start_ts = Time.parse("2011-10-01 00:00:00")
  end_ts = Time.parse("2012-11-19 00:00:00")

  uploads = Upload.find(:all, :conditions => ["device_id = ? AND upload_time > ? AND upload_time < ? AND file_type = ?",
      device_id,
      Time.at(start_ts).utc,
      Time.at(end_ts).utc,
      1
    ])

  uploads.each do |upload|
    current_file = File.open(upload.record.path, "r")
    
    while(line = current_file.gets)
      columns = line.split(",")
      if columns.length > 0
        if columns[0] == "location" || columns[0] == "wifi_accesspoint_info"
          out_file.puts line
        end
      end
      
    end
  end

  out_file.close
  
end


task :export_joy => :environment do |t|
  device_id = "A00000024109D8"
  export_root_path = "#{Rails.root}/export_data"
  out_file = File.open("#{export_root_path}/gps.csv", "w+")
  
  start_ts = Time.parse("2012-06-16 00:00:00")
  end_ts = Time.parse("2012-06-18 00:00:00")

  uploads = Upload.find(:all, :conditions => ["device_id = ? AND upload_time > ? AND upload_time < ? AND file_type = ?",
      device_id,
      Time.at(start_ts).utc,
      Time.at(end_ts).utc,
      1
    ])

  uploads.each do |upload|
    current_file = File.open(upload.record.path, "r")
    
    while(line = current_file.gets)
      columns = line.split(",")
      if columns.length > 0
        if columns[0] == "location"
          out_file.puts line
        end
      end
      
    end
  end

  out_file.close
  
end


task :export_annotation => :environment do |t|
  user_list_file = File.open("/mobisens/export_data/user_list.txt", "r")
  devices = []

  while device_id = user_list_file.gets
    devices << device_id.strip
  end

  user_list_file.close

  
  export_root_path = "#{Rails.root}/export_data"
  log_file = File.open("#{export_root_path}/log.log", "w+")

  log_file.puts "#{Time.now.to_formatted_s(:db)}: start to export.."
  
  devices.each do |device_id|
    uploads = Upload.find(:all,
      :conditions => ["device_id = ? AND file_type = ?", device_id, 3],
      :order => "upload_time ASC"
    )


    file_to_write = File.open("#{export_root_path}/#{device_id}_annos.csv", "w")
    unique_activities = {}
    
    
    uploads.each do |upload|
      
      current_file = File.open(upload.record.path, "r")
      while line = current_file.gets
        line = line.strip
        if unique_activities.has_key?(line) == false
          file_to_write.puts line
          unique_activities[line] = true
        end
        
      end

      current_file.close
    end

    file_to_write.close

  end
  log_file.puts "#{Time.now.to_formatted_s(:db)}: export done."
  log_file.close
end

def export_by_type(device_id, file_type, start_time_ms, end_time_ms)
  start_check_time = start_time_ms / 1000
  end_check_time = end_time_ms / 1000
  
  export_root_path = "#{Rails.root}/export_data"
  log_file = File.open("#{export_root_path}/log.log", "w+")

  log_file.puts "#{Time.now.to_formatted_s(:db)}: start to export.."
  
  a_lot_number = 20
  uploads = []

  if end_check_time > 0
    uploads = Upload.find(:all,
      :conditions => ["device_id = ? AND upload_time >= ? AND upload_time <= ? AND file_type = ?",
        device_id,
        Time.at(start_check_time),
        Time.at(end_check_time),
        file_type],
      :order => "upload_time ASC"
    )
  else
    uploads = Upload.find(:all,
      :conditions => ["device_id = ? AND upload_time >= ? AND file_type = ?",
        device_id,
        Time.at(start_check_time),
        file_type],
      :order => "upload_time ASC"
    )
  end
  
  files = {}
  uploads.each do |upload|
    if !files.has_key?(upload.device_id)
      files[upload.device_id] = File.open("#{export_root_path}/#{upload.device_id}_#{file_type}.csv", "w+")
    end

    file_to_write = files[upload.device_id]
    current_file = File.open(upload.record.path, "r")

    line = current_file.gets
    while(line != nil)
      file_to_write.puts line
      line = current_file.gets
    end

    current_file.close

  end

  files.each do |device_id, file|
    file.close
  end

  log_file.puts "#{Time.now.to_formatted_s(:db)}: export done."
  log_file.close
end

def extract_features(window_size)
  exported_file_path = "#{Rails.root}/export_data/out_2.csv"
  features_file_path = "#{Rails.root}/export_data/features.csv"

  in_file = File.open(exported_file_path, 'r')
  feature_file = File.open(features_file_path, 'w')
  
  index = 0

  buffers = []
  
  while((line = in_file.gets) != nil)
    if line == ''
      next
    end

    columns = line.split(',')
    if columns.length <= 4
      next
    else
      buffers << columns
    end

    if (index + 1) % window_size == 0
      avg_x = 0
      avg_y = 0
      avg_z = 0
      std_x = 0
      std_y = 0
      std_z = 0
      
      buffers.each do |row|
        avg_x += row[1].to_f
        avg_y += row[2].to_f
        avg_z += row[3].to_f
      end

      avg_x /= buffers.length.to_f
      avg_y /= buffers.length.to_f
      avg_z /= buffers.length.to_f

      buffers.each do |row|
        std_x += (row[1].to_f - avg_x) * (row[1].to_f - avg_x)
        std_y += (row[2].to_f - avg_y) * (row[2].to_f - avg_y)
        std_z += (row[3].to_f - avg_z) * (row[3].to_f - avg_z)
      end

      std_x = Math.sqrt(std_x / buffers.length.to_f)
      std_y = Math.sqrt(std_y / buffers.length.to_f)
      std_z = Math.sqrt(std_z / buffers.length.to_f)

      feature_file.puts "#{avg_x},#{avg_y},#{avg_z},#{std_x},#{std_y},#{std_z}"
      buffers = []
    end
    
    index += 1
  end
end


def export_by_type_to_one_file(file_type, start_time_ms, end_time_ms)
  start_check_time = start_time_ms / 1000
  device_id = "357605040312238"
  export_root_path = "#{Rails.root}/export_data"
  log_file = File.open("#{export_root_path}/log.log", "w+")

  log_file.puts "#{Time.now.to_formatted_s(:db)}: start to export.."

  uploads = Upload.find(:all,
    :conditions => ["device_id = ? AND upload_time >= ? AND file_type = ?", device_id, Time.at(start_check_time), file_type],
    :order => "upload_time ASC"
  )
  
  out_file = File.open("#{export_root_path}/out_#{file_type}_#{device_id}.csv", "w+")
  uploads.each do |upload|

    current_file = File.open(upload.record.path, "r")

    if file_type == 2
      line = current_file.gets
    end
    line = current_file.gets
    while(line != nil)
      out_file.puts line
      line = current_file.gets
    end

    current_file.close
    out_file.puts ''

  end

  out_file.close

  log_file.puts "#{Time.now.to_formatted_s(:db)}: export done."
  log_file.close
end
