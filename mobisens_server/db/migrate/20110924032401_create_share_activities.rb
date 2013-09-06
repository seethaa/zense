class CreateShareActivities < ActiveRecord::Migration
  def self.up
    create_table :share_activities do |t|
      t.string :device_id
      t.string :activity
      t.integer :timestamp, :limit => 8

      t.timestamps
    end
  end

  def self.down
    drop_table :share_activities
  end
end
