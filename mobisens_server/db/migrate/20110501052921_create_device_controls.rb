class CreateDeviceControls < ActiveRecord::Migration
  def self.up
    create_table :device_controls do |t|
      t.string :device_id
      t.integer :profile_id

      t.timestamps
    end
  end

  def self.down
    drop_table :device_controls
  end
end
