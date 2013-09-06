class CreateExportQueues < ActiveRecord::Migration
  def self.up
    create_table :export_queues do |t|
      t.integer :upload_id
      t.boolean :exported
      t.integer :generate_time

      t.timestamps
    end
  end

  def self.down
    drop_table :export_queues
  end
end
