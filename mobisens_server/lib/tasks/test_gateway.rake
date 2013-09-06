task :test_gateway => :environment do |t|
  uploads = Upload.find(:all, :order => "id desc", :limit => 20)
  uploads.each do |upload|
    param_hash = {}
    param_hash[:device_id] = upload.device_id
    param_hash[:upload_time] = upload.upload_time
    param_hash[:file_type] = upload.file_type
    param_hash[:try_count] = 0
    param_hash[:upload_password] = "pang.wu@sv.cmu.edu"
    csv_file = File.open(upload.record.path)
    param_hash[:file] = csv_file

    result = Gateway.distribute(param_hash)
    puts result
    csv_file.close
    
  end

  puts "Test done."
end


task :disable_all_pending_exports => :environment do |t|
  pending_exports = ExportQueue.find(:all, :conditions => ["exported = ?", false])
  pending_exports.each do |pending_export|
    pending_export.exported = true
    pending_export.save!
  end
end