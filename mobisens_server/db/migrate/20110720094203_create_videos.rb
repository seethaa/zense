class CreateVideos < ActiveRecord::Migration
  def self.up
    create_table :videos do |t|
      t.integer :timestamp, :limit => 8
      t.string :annotation
      t.string :device_id
      t.string :video_file_name
      t.string :video_content_type
      t.integer :video_file_size
      t.datetime :video_updated_at

      t.timestamps
    end
  end

  def self.down
    drop_table :videos
  end
end
