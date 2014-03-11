task :user_install_time => :environment do |t|
  html_string = API.user_install_time
  html_file = File.open("/mobisens/export_data/install_time.html", "w+")
  html_file.puts html_string
  html_file.close
end