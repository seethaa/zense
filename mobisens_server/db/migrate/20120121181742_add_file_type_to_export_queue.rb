class AddFileTypeToExportQueue < ActiveRecord::Migration
  def self.up
    add_column :export_queues, :file_type, :integer
  end

  def self.down
    remove_column :export_queues, :file_type
  end
end
