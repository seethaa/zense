require 'socket'

task :cron_runner => :environment do |t, args|

  host = Socket.gethostname
  is_gateway = false
  if host == 'beware.sv.cmu.edu'
    puts "Run as gateway MobiSens."
    is_gateway = true
  else
    puts "Run as storage MobiSens."
  end

  log_file = File.open("#{Rails.root}/log/cron_runner.log", "w+")
  
  second = 0
  minute = 0
  hour = 0

  last_hour = hour
  last_minute = minute

  hour_changed = false

  while(true)

    begin
      time_string = Time.now.strftime('%Y-%m-%d %H:%M:%S %z').split(' ')[1]
      # log_file.puts time_string
      # log_file.flush
      
      time_parts = time_string.split(':')
      hour = time_parts[0].to_i
      minute = time_parts[1].to_i
      second = time_parts[2].to_i

      # puts minute

      if minute == 0 && hour_changed
        if !is_gateway
          back_track_time = 8 * 24 * 60 * 60
          `rake cron_export[#{back_track_time}] > #{Rails.root}/log/export.log`

        else

          `rake distribute_data`
          `rake gateway_reupload`
        end

        hour_changed = false
      end



      sleep(1)

      if last_hour != hour
        last_hour = hour
        hour_changed = true
      end

      if last_minute != minute
        last_minute = minute
      end

    rescue => exception
      log_file.puts "#{Time.now}: cron runner error: #{exception.backtrace}"
      log_file.flush
    end

  end
end