require 'will_paginate'

class ContentPage < ActiveRecord::Base
  cattr_reader :per_page
  @@per_page = 30



  def get_relative_url
    return "/content_pages/#{self.id}"
  end

  def get_absolute_url
    return "#{Rails.configuration.root_path}" + get_relative_url
  end

  def self.display_all_by_page(page)
    return paginate(:page => page, :order => "created_at DESC")
  end
  
end
