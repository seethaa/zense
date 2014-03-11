class AddIndexToUploadTable < ActiveRecord::Migration
  def self.up
    add_index :uploads, :file_type, :name => "fk_upload_file_type"
    add_index :uploads, :record_updated_at, :name => "fk_upload_record_updated_at"
    add_index :uploads, :upload_time, :name => "fk_upload_upload_time"
    add_index :uploads, :device_id, :name => "fk_upload_device_id"
  end

  def self.down

    remove_index :uploads, :name => "fk_upload_file_type"
    remove_index :uploads, :name => "fk_upload_record_updated_at"
    remove_index :uploads, :name => "fk_upload_upload_time"
    remove_index :uploads, :name => "fk_upload_device_id"

  end
end
