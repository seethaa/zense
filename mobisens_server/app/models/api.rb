require 'tempfile'
require 'net/http'
require 'cgi'

# To change this template, choose Tools | Templates
# and open the template in the editor.

class API

  # Get the data for specific timestamp, device and sensor type.
  def self.get_data(start_time_i, end_time_i, device_id, file_type)
    # Since the request data might be uploaded later,
    # we need a time margin to get as many files as possible.
    time_margin = 0
    current_time_i = DateTime.now.to_i * 1000

    search_start_time_i = start_time_i - time_margin
    search_end_time_i = end_time_i + time_margin

    if end_time_i < start_time_i
      return nil
    end

    if search_end_time_i > current_time_i
      search_end_time_i = current_time_i
    end

    files = Upload.find(:all, 
      :conditions => ["device_id = ? AND file_type = ? AND created_at >= ?",
        device_id, 
        file_type.to_i,
        Time.at(start_time_i / 1000)
      ])

    time_hash = {}
    tmp_file = Tempfile.new(Time.now.to_i.to_s)
    indicator_line = ''
    indicator_line_printed = false
    
    files.each do |file|
      csv_file = File.open(file.record.path)
      
      last_dataline = get_last_line(csv_file, 0)
      timestamp = get_timestamp(last_dataline, file_type.to_i)
      if timestamp < search_start_time_i
        csv_file.close
        puts "#{file.record.url} before start."
        next
      end

      data_line = csv_file.gets
      

      if file_type.to_i == 2 # Sensor files
        indicator_line = data_line
        data_line = csv_file.gets # skip the indicator line
        
        if !indicator_line_printed
          tmp_file.print(indicator_line, "")
          indicator_line_printed = true
        end
      end

      last_data_line = nil
      while data_line

        if file_type.to_i == 2
          if data_line.split(",").length != 15
            
            data_line = csv_file.gets
            next
          end

        end

        timestamp = get_timestamp(data_line, file_type.to_i)

        if timestamp == -1
          data_line = csv_file.gets
          next
        end
        
        if timestamp > search_end_time_i
          puts "#{file.record.url} after end."
          break
        end

        if timestamp >= search_start_time_i && timestamp <= search_end_time_i
          if !time_hash.has_key?(timestamp)
            time_hash[timestamp] = 1
            #tmp_file.print(data_line, "")

            if last_data_line != nil
              tmp_file.print(last_data_line, "") # ignore the last line, since it might be corrupted
            end
            last_data_line = data_line
          end
        end


        data_line = csv_file.gets

      end
      
      puts "#{file.record.url} just in the interval."
      csv_file.close
    end
    tmp_file.close
    
    return tmp_file
    
  end

  def self.get_total_hours(file_type)
 

    files = Upload.find(:all, :conditions => ["file_type = ?", file_type])

    total_time = 0
    total_file = files.length
    error_file = 0

    files.each do |file|
      csv_file = File.open(file.record.path)

      header_line = csv_file.gets # skip the indicator line

      data_line = csv_file.gets

      if data_line != nil
        start_timestamp = get_timestamp(data_line, file_type)
        last_dataline = get_last_line(csv_file, 1)
        end_timestamp = get_timestamp(last_dataline, file_type)

        

        if end_timestamp > start_timestamp &&
            end_timestamp.to_s.length == 13 &&
            start_timestamp.to_s.length == 13
          total_time += (end_timestamp - start_timestamp)
        else
          error_file += 1
        end
      else
        error_file += 1
      end
      
      csv_file.close
    end

    return [total_time / 1000 / 60 / 60, total_file, error_file]
  end

  def self.get_total_users(*params)
    file_type = 2 # for mobile user by default
    if params.length == 1
      file_type = params[0].to_i
    end
    addresses = Address.find(:all)
    user_count = 0

    addresses.each do |address|
      if Upload.find(:all, :conditions => ["device_id = ? AND file_type = ?", address.device_id, file_type]).length > 2
        user_count += 1
      end
    end

    return user_count
  end

  def self.gps_sampling_intervals


    files = Upload.find(:all,
      :conditions => ["device_id = ? AND file_type = ?", 'A0000022025624', 5],
      :order => "id desc", 
      :limit => 1000
    ).reverse

    intervals = []

    files.each do |file|
      csv_file = File.open(file.record.path)

      csv_file.gets # skip the indicator line

      data_line = csv_file.gets

      while data_line != nil
        if data_line.include? 'Current GPS interval'
          columns = data_line.split(' ')
          intervals << columns[5].to_i
          
        end
        data_line = csv_file.gets
      end

      csv_file.close

      if intervals.length >= 1000
        break
      end
    end

    return intervals
  end

  def self.anno_stat


    files = Upload.find(:all,
      :conditions => ["device_id <> ? AND device_id <> ? AND file_type = ?", 'A000002203410F', 'A0000022025624',3],
      :order => "id ASC"
    )

    intervals = []
    users = {}

    time_stamps = {}

    user_count_string = ""
    anno_count_string = ""
    
    last_time = nil
    machine_anno_cnt = 0
    human_anno_cnt = 0

    files.each do |file|

      if last_time != nil
        if file.created_at.to_i - last_time.to_i >= 24 * 60 * 60
          #intervals << [human_anno_cnt.to_f / users.length.to_f,
          #  (machine_anno_cnt + human_anno_cnt).to_f / users.length.to_f]
          

          n = file.created_at.to_i - last_time.to_i / 24 * 60 * 60
          if n <= 0
            n = 1
          end
          
          for day in 0..(n-1)
            user_count_string << "user: #{users.length}<br/>"
            anno_count_string << "anno: #{human_anno_cnt} #{machine_anno_cnt}<br/>"
          end
          
          last_time = file.created_at
          users = {}
          machine_anno_cnt = 0
          human_anno_cnt = 0
        end
      else
        last_time = file.created_at
      end

      #last_time = file.created_at
      users[file.device_id] = 1
      
      csv_file = File.open(file.record.path)
      data_line = csv_file.gets
      
      while data_line != nil
        columns = data_line.split(',')

        if time_stamps.has_key?(columns[0])
          data_line = csv_file.gets
          next
        else
          time_stamps[columns[0]] = 1
        end
        
        if columns[2] != 'Unknown Activity'
          if columns[3] == 'true'
            machine_anno_cnt += 1
          else
            human_anno_cnt += 1
          end
        end

        data_line = csv_file.gets
      end

      csv_file.close

      
    end

    return [user_count_string, anno_count_string]
  end

  def self.user_with_anno_count
    files = Upload.find(:all,
      :conditions => ["file_type = ?",3],
      :order => "id ASC"
    )

    users = {}

    files.each do |file|
      csv_file = File.open(file.record.path)
      data_line = csv_file.gets

      while data_line != nil
        columns = data_line.split(',')


        if columns[2] != 'Unknown Activity' && users.has_key?(file.device_id) == false
          users[file.device_id] = 1
          break
        end

        data_line = csv_file.gets
      end

      csv_file.close


    end

    return users.length
  end

  def self.user_install_time
    files = Upload.find(:all,
      :conditions => ["file_type = ?", 2],
      :order => "id ASC"
    )

    users = {}

    files.each do |file|
      if users.has_key?(file.device_id) == false
          users[file.device_id] = {:time => 0, :reg => file.created_at}
          
      end

      csv_file = File.open(file.record.path)
      csv_file.gets


      while csv_file.gets != nil
        users[file.device_id][:time] += 1
      end
      

      csv_file.close


    end

    return_string = ""
    users.each do |id, hash|
      return_string << "#{id},#{hash[:time]},#{hash[:reg]}<br/>"
    end
    
    return return_string
  end


  
  def self.get_devices
    devices = Address.find(:all)
    device_ids = []
    device_ids << devices.device_id
    return device_ids
  end

  def self.http_get(http_domain,path,params)
    param_path = "#{path}?".concat(params.collect { |k,v| "#{k}=#{CGI::escape(v.to_s)}" }.join('&'))

    url = URI.parse(http_domain)
    res = Net::HTTP.start(url.host, url.port) {|http|
      http.get(param_path)
    }
    result = res.body

    return result
  end


  private
  
  def self.get_timestamp(line, file_type)
    if file_type.to_i == 2  # sensor data
      columns = line.split(",")
      if columns.length < 3
        return -1
      else
        return columns[0].to_i
      end
    end

    if file_type.to_i == 1  # system data
      columns = line.split(",")
      if columns.length < 3
        return -1
      else
        return columns[2].to_i
      end
    end

    if file_type.to_i == 8 # PC Mouse track
      columns = line.split(",")
      if columns.length < 3
        return -1
      else
        return columns[2].to_i * 1000
      end
    end
  end

  def self.get_last_line(*params)
    if params.length == 0
      return
    end

    file = params[0]
    inverse_index = params.length == 2 ? params[1].to_i : 0
    
    last_line = ''
    index = inverse_index + 1
    current_position = file.tell

    # puts current_position

    pos = 1

    # puts file.size
    file.seek(-pos, IO::SEEK_END)

    while(c = file.getc)
      # puts "#{c.chr},#{file.tell}"

      if c.chr == "\n"
        if last_line.length > 0
          index -= 1
        end

        if index == 0
          file.seek(current_position, IO::SEEK_SET)

          # puts last_line
          return last_line
        else
          last_line = ''
        end
      else
        if c.chr != "\r"
          last_line = "#{c.chr}#{last_line}"
        end
      end

      pos += 1

      if pos <= file.size

        file.seek(-pos, IO::SEEK_END)
      else
        break
      end

    end

    return last_line
  end

  
end
