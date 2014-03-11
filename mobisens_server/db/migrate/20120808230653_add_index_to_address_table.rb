class AddIndexToAddressTable < ActiveRecord::Migration
  def self.up
    add_index :addresses, :device_id, :name => "fk_addresses_device_id"
  end

  def self.down
    remove_index :addresses, :name => "fk_addresses_device_id"
  end
end
