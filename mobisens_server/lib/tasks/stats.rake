
task :get_hours => :environment do |t|
  log_file = File.open("#{Rails.root}/log/total_hours.log", "w")
  stats = API.get_total_hours(2)
  log_file.puts "total hours: #{stats[0].to_i}, error: #{stats[2].to_i}, total files: #{stats[1].to_i}, file_type: 2"
  log_file.flush
  
  stats = API.get_total_hours(8)
  log_file.puts "total hours: #{stats[0].to_i}, error: #{stats[2].to_i}, total files: #{stats[1].to_i}, file_type: 8"

  log_file.close
  
end