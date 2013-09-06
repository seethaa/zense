require 'will_paginate'

class AddressController < ApplicationController
  before_filter :require_user, :only =>[:list, :device_list_json]
  protect_from_forgery :only => [:update, :delete, :create, :list, :device_list_json]

  def list

    page = 1
    if params.has_key?(:page)
      page = params[:page]
    end

    @addresses = Address.display_all_by_page(page)
    
    respond_to do |format|
      format.html # list.html.erb
      format.xml  { render :xml => @addresses }
    end
  end

  def device_list_json
    addresses = Address.find(:all)
    device_ids = []
    addresses.each do |address|
      device_ids << address.device_id
    end

    render :json => device_ids
  end

  def clear
    empty_addresses = []
    addresses = Address.find(:all)

    addresses.each do |address|
      if Upload.find(:all, :conditions => ["device_id = ? AND (file_type = 1 OR file_type = 2)", address.device_id]).length == 0
        empty_addresses << address
      end
    end

    empty_addresses.each do |empty_address|
      empty_address.delete
    end
    
    render :text => "Done, #{empty_addresses.length} removed."
  end
  
  def get_address
    error_message = nil
    address = Address.new(:street => '',
      :city => '',
      :state => '',
      :country => '',
      :zipcode => '',
      :email => ''
    )
    begin
      upload_key = params[:upload_key]
      device_id = params[:device_id]

      if upload_key == 'pang.wu@sv.cmu.edu'
        address_on_device = Address.find(:first, :conditions => ["device_id = ?", device_id])
        if address_on_device != nil
          address = address_on_device
          if address.email == nil
            address.email = ''
          end
        end
      else
        error_message = "Inavlid parameters."
      end
      
    rescue
      error_message = "Inavlid parameters."
    end

    if error_message != nil
      render :text => error_message
    else
      render :text => address.to_json.to_s
      
    end
  end

  def upload_address
    error_message = nil
    begin
      upload_key = params[:upload_key]
      device_id = params[:device_id]

      if upload_key == 'pang.wu@sv.cmu.edu'
        address = Address.find(:first, :conditions => ["device_id = ?", device_id])

        if address == nil
          address = Address.create(:device_id => device_id,
            :street => params[:street],
            :city => params[:city],
            :state => params[:state],
            :country => params[:country],
            :zipcode => params[:zipcode],
            :email => params[:email]
          )
        else
          address.street = params[:street]
          address.city = params[:city]
          address.state = params[:state]
          address.country = params[:country]
          address.zipcode = params[:zipcode]
          address.email = params[:email]

          address.save!
        end
      end
    rescue
      error_message = "error"
    end

    if error_message == nil
      render :text => ''
    else
      render :text => error_message
    end
  end
end
