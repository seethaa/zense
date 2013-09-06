require 'will_paginate'

class Upload < ActiveRecord::Base
  
  validate :validate_upload_password

  has_attached_file :record

  cattr_reader :per_page
  has_many :export_queues, :dependent => :delete_all

  @@per_page = 30

  def self.FILE_TYPE
    [{:id => 1, :name => 'System Sense File'},
    {:id => 2, :name => 'Sensor Data File'},
    {:id => 3, :name => 'Annotation File'},
    {:id => 5, :name => 'Log'},
    {:id => 6, :name => 'PC System Data'},
    {:id => 7, :name => 'Audio Feature'},
    {:id => 8, :name => 'PC MouseTrack'},
    {:id => 9, :name => 'PCAP WIFI Records'}
    ]
  end

  def upload_password=(value)
    @upload_password = value
  end

  def upload_password
    return @upload_password
  end

  def file_type_s
    Upload.FILE_TYPE.each do |type|
      if type[:id] == self.file_type
        return type[:name]
      end
    end

    return 'Unknown'
  end

  def self.display_by_device_id_and_filetype_by_page(page, device_id, file_type)
    
    conditions_hash = {}
    if device_id != '-1'
      conditions_hash['device_id'] = device_id
    end
    
    if file_type != -1
      conditions_hash['file_type'] = file_type.to_i
    end
    
    conditions = [conditions_hash.keys.map{|k| k + ' = ?'}.join(' AND ')] + conditions_hash.values
    
    return paginate(:page => page, :conditions => conditions, :order => "upload_time DESC")
  end


  def self.display_all_by_page(page)
    return paginate(:page => page, :order => "upload_time DESC")
  end

  private
  @upload_password

  def validate_upload_password
    if self.upload_password != 'pang.wu@sv.cmu.edu'
      errors.add(:upload_password, "Upload password incorrect.")
    end

  end
end
