class AddDeviceIdAndFileTypeToUpload < ActiveRecord::Migration
  def self.up
    add_column :uploads, :device_id, :string, :limit => 255
    add_column :uploads, :file_type, :integer, :limit => 8
  end

  def self.down
    remove_column :uploads, :device_id
    remove_column :uploads, :file_type
  end
end
