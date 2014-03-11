class AddIndexToExportQueue < ActiveRecord::Migration
  def self.up
    add_index :export_queues, :file_type, :name => "fk_export_queues_file_type"
    add_index :export_queues, :try_count, :name => "fk_export_queues_try_count"
    add_index :export_queues, :generate_time, :name => "fk_export_queues_generate_time"
    add_index :export_queues, :upload_id, :name => "fk_export_queues_upload_id"
    add_index :export_queues, :exported, :name => "fk_export_queues_exported"
    add_index :export_queues, [:exported, :file_type], :name => "export_queues_index_on_exported_and_file_type"
    add_index :export_queues, [:generate_time, :exported, :file_type], :name => "export_queues_index_on_generate_time_and_exported_and_file_type"
  end

  def self.down
    remove_index :export_queues, :name => "fk_export_queues_file_type"
    remove_index :export_queues, :name => "fk_export_queues_try_count"
    remove_index :export_queues, :name => "fk_export_queues_generate_time"
    remove_index :export_queues, :name => "fk_export_queues_upload_id"
    remove_index :export_queues, :name => "fk_export_queues_exported"
    remove_index :export_queues, :name => "export_queues_index_on_exported_and_file_type"
    remove_index :export_queues, :name => "export_queues_index_on_generate_time_and_exported_and_file_type"
  end
end
