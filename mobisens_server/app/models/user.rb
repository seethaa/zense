class User < ActiveRecord::Base
  AUTH_KEY = 'mobisens@cmusv'
  
  acts_as_authentic do |c|
    
  end # the configuration block is optional
end
