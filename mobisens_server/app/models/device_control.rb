class DeviceControl < ActiveRecord::Base
  belongs_to :profile

  before_create :check_for_device_id

  def check_for_device_id
    if DeviceControl.find(:first, :conditions => ["device_id = ?", self.device_id]) != nil
      return false
    end

    return true
  end
  
end
