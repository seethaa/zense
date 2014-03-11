require 'will_paginate'

class Address < ActiveRecord::Base
  cattr_reader :per_page
  @@per_page = 30

  def self.display_all_by_page(page)
    return paginate(:page => page, :order => "created_at DESC")
  end
end
