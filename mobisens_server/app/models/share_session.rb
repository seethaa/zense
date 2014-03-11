class ShareSession < ActiveRecord::Base
  has_many :share_session_members, :dependent => :delete_all

end
