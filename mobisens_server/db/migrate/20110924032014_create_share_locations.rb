class CreateShareLocations < ActiveRecord::Migration
  def self.up
    create_table :share_locations do |t|
      t.string :device_id
      t.float :latitude
      t.float :longitude
      t.integer :timestamp, :limit => 8

      t.timestamps
    end
  end

  def self.down
    drop_table :share_locations
  end
end
