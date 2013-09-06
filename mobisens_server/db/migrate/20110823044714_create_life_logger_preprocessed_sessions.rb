class CreateLifeLoggerPreprocessedSessions < ActiveRecord::Migration
  def self.up
    create_table :life_logger_preprocessed_sessions do |t|
      t.integer :session_id, :null => false
      t.string :device_id, :null => false

      t.timestamps
    end
  end

  def self.down
    drop_table :life_logger_preprocessed_sessions
  end
end
