# This file is auto-generated from the current state of the database. Instead of editing this file, 
# please use the migrations feature of Active Record to incrementally modify your database, and
# then regenerate this schema definition.
#
# Note that this schema.rb definition is the authoritative source for your database schema. If you need
# to create the application database on another system, you should be using db:schema:load, not running
# all the migrations from scratch. The latter is a flawed and unsustainable approach (the more migrations
# you'll amass, the slower it'll run and the greater likelihood for issues).
#
# It's strongly recommended to check this file into your version control system.

ActiveRecord::Schema.define(:version => 20130226005618) do

  create_table "addresses", :force => true do |t|
    t.string   "device_id",  :null => false
    t.string   "street",     :null => false
    t.string   "city",       :null => false
    t.string   "state",      :null => false
    t.string   "country",    :null => false
    t.string   "zipcode",    :null => false
    t.string   "email"
    t.datetime "created_at"
    t.datetime "updated_at"
  end

  add_index "addresses", ["device_id"], :name => "fk_addresses_device_id"

  create_table "centroids", :force => true do |t|
    t.decimal  "avg_x",                                :precision => 22, :scale => 15
    t.decimal  "avg_y",                                :precision => 22, :scale => 15
    t.decimal  "avg_z",                                :precision => 22, :scale => 15
    t.decimal  "std_x",                                :precision => 22, :scale => 15
    t.decimal  "std_y",                                :precision => 22, :scale => 15
    t.decimal  "std_z",                                :precision => 22, :scale => 15
    t.integer  "life_logger_preprocessed_sessions_id",                                 :null => false
    t.string   "label"
    t.datetime "created_at"
    t.datetime "updated_at"
  end

  create_table "content_pages", :force => true do |t|
    t.string   "content",    :limit => 2000
    t.datetime "created_at"
    t.datetime "updated_at"
  end

  create_table "device_controls", :force => true do |t|
    t.string   "device_id"
    t.integer  "profile_id"
    t.datetime "created_at"
    t.datetime "updated_at"
  end

  create_table "export_queues", :force => true do |t|
    t.integer  "upload_id"
    t.boolean  "exported"
    t.integer  "generate_time"
    t.datetime "created_at"
    t.datetime "updated_at"
    t.integer  "file_type"
    t.integer  "try_count"
  end

  add_index "export_queues", ["exported", "file_type"], :name => "export_queues_index_on_exported_and_file_type"
  add_index "export_queues", ["exported"], :name => "fk_export_queues_exported"
  add_index "export_queues", ["file_type"], :name => "fk_export_queues_file_type"
  add_index "export_queues", ["generate_time", "exported", "file_type"], :name => "export_queues_index_on_generate_time_and_exported_and_file_type"
  add_index "export_queues", ["generate_time"], :name => "fk_export_queues_generate_time"
  add_index "export_queues", ["try_count"], :name => "fk_export_queues_try_count"
  add_index "export_queues", ["upload_id"], :name => "fk_export_queues_upload_id"

  create_table "life_logger_preprocessed_sessions", :force => true do |t|
    t.integer  "session_id", :null => false
    t.string   "device_id",  :null => false
    t.datetime "created_at"
    t.datetime "updated_at"
  end

  create_table "messages", :force => true do |t|
    t.string   "device_id"
    t.boolean  "read"
    t.string   "title"
    t.string   "url"
    t.datetime "created_at"
    t.datetime "updated_at"
  end

  create_table "profiles", :force => true do |t|
    t.string   "name"
    t.string   "config"
    t.datetime "created_at"
    t.datetime "updated_at"
    t.string   "photo_file_name"
    t.string   "photo_content_type"
    t.integer  "photo_file_size"
    t.datetime "photo_updated_at"
  end

  create_table "share_activities", :force => true do |t|
    t.string   "device_id"
    t.string   "activity"
    t.integer  "timestamp",  :limit => 8
    t.datetime "created_at"
    t.datetime "updated_at"
  end

  create_table "share_locations", :force => true do |t|
    t.string   "device_id"
    t.float    "latitude"
    t.float    "longitude"
    t.integer  "timestamp",  :limit => 8
    t.datetime "created_at"
    t.datetime "updated_at"
  end

  create_table "share_session_members", :force => true do |t|
    t.integer  "share_session_id"
    t.string   "member_id"
    t.datetime "created_at"
    t.datetime "updated_at"
  end

  create_table "share_sessions", :force => true do |t|
    t.string   "owner_device_id"
    t.datetime "created_at"
    t.datetime "updated_at"
  end

  create_table "special_profiles", :force => true do |t|
    t.integer  "profile_id"
    t.datetime "created_at"
    t.datetime "updated_at"
  end

  create_table "uploads", :force => true do |t|
    t.datetime "upload_time"
    t.datetime "created_at"
    t.datetime "updated_at"
    t.string   "record_file_name"
    t.string   "record_content_type"
    t.integer  "record_file_size"
    t.datetime "record_updated_at"
    t.string   "device_id"
    t.integer  "file_type",           :limit => 8
  end

  add_index "uploads", ["device_id"], :name => "fk_upload_device_id"
  add_index "uploads", ["file_type"], :name => "fk_upload_file_type"
  add_index "uploads", ["record_updated_at"], :name => "fk_upload_record_updated_at"
  add_index "uploads", ["upload_time"], :name => "fk_upload_upload_time"

  create_table "users", :force => true do |t|
    t.string   "login",             :null => false
    t.string   "crypted_password",  :null => false
    t.string   "password_salt",     :null => false
    t.string   "persistence_token", :null => false
    t.datetime "last_request_at"
    t.datetime "current_login_at"
    t.datetime "last_login_at"
    t.string   "current_login_ip"
    t.string   "last_login_ip"
    t.datetime "created_at"
    t.datetime "updated_at"
  end

  create_table "videos", :force => true do |t|
    t.integer  "timestamp",          :limit => 8
    t.string   "annotation"
    t.string   "device_id"
    t.string   "video_file_name"
    t.string   "video_content_type"
    t.integer  "video_file_size"
    t.datetime "video_updated_at"
    t.datetime "created_at"
    t.datetime "updated_at"
  end

end
