class CreateShareSessions < ActiveRecord::Migration
  def self.up
    create_table :share_sessions do |t|
      t.string :owner_device_id

      t.timestamps
    end
  end

  def self.down
    drop_table :share_sessions
  end
end
