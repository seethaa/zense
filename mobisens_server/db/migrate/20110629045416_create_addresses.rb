class CreateAddresses < ActiveRecord::Migration
  def self.up
    create_table :addresses do |t|
      t.string :device_id, :null => false
      t.string :street, :null => false
      t.string :city, :null => false
      t.string :state, :null => false
      t.string :country, :null => false
      t.string :zipcode, :null => false
      t.string :email
      t.timestamps
    end
  end

  def self.down
    drop_table :addresses
  end
end
