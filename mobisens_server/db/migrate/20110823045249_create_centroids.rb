class CreateCentroids < ActiveRecord::Migration
  def self.up
    create_table :centroids do |t|
      t.decimal :avg_x, :precision => 22, :scale => 15
      t.decimal :avg_y, :precision => 22, :scale => 15
      t.decimal :avg_z, :precision => 22, :scale => 15
      t.decimal :std_x, :precision => 22, :scale => 15
      t.decimal :std_y, :precision => 22, :scale => 15
      t.decimal :std_z, :precision => 22, :scale => 15
      t.integer :life_logger_preprocessed_sessions_id, :null => false
      t.string :label

      t.timestamps
    end
  end

  def self.down
    drop_table :centroids
  end
end
