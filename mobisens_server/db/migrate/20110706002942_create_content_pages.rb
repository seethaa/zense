class CreateContentPages < ActiveRecord::Migration
  def self.up
    create_table :content_pages do |t|
      t.string :content, :limit => 2000

      t.timestamps
    end
  end

  def self.down
    drop_table :content_pages
  end
end
