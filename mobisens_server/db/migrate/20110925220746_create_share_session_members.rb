class CreateShareSessionMembers < ActiveRecord::Migration
  def self.up
    create_table :share_session_members do |t|
      t.integer :share_session_id
      t.string :member_id

      t.timestamps
    end
  end

  def self.down
    drop_table :share_session_members
  end
end
