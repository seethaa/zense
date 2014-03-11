class AddTryCountToExportQueue < ActiveRecord::Migration
  def self.up
    add_column :export_queues, :try_count, :integer
  end

  def self.down
    remove_column :export_queues, :try_count
  end
end
