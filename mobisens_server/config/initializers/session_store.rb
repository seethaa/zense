# Be sure to restart your server when you modify this file.

# Your secret key for verifying cookie session data integrity.
# If you change this key, all old sessions will become invalid!
# Make sure the secret is at least 30 characters and all random, 
# no regular words or you'll be exposed to dictionary attacks.
ActionController::Base.session = {
  :key         => '_StrategicReading_session',
  :secret      => '931ef6b1c90d46a9ec9cf6d82889831738d0f66961bacfa3c251696508c143dc4bcaa65168a1233da99a84b53540ec1337cd924403cd156c364e1e189b5447c8'
}

# Use the database for sessions instead of the cookie-based default,
# which shouldn't be used to store highly confidential information
# (create the session table with "rake db:sessions:create")
# ActionController::Base.session_store = :active_record_store
