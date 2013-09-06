class CreateSpecialProfiles < ActiveRecord::Migration
  def self.up
    create_table :special_profiles do |t|
      t.integer :profile_id

      t.timestamps
    end
  end

  def self.down
    drop_table :special_profiles
  end
end
