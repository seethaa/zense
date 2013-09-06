require 'will_paginate'

class MessagesController < ApplicationController

  layout :choose_layout

  before_filter :require_user, :only => [:index, :new, :create, :edit, :show, :update, :destroy]
  
  # GET /messages
  # GET /messages.xml
  def index
    page = 1
    if params.has_key?(:page)
      page = params[:page]
    end

    @messages = Message.display_all_by_page(page)

    respond_to do |format|
      format.html # index.html.erb
      format.xml  { render :xml => @messages }
    end
  end

  def tutorial
    @device_id = 'none'
    begin
      @device_id = params[:device_id]
    rescue

    end
    respond_to do |format|
      format.html # index.html.erb
    end
  end

  def device_index
    
    @error_message = nil
    begin
      @device_id = params[:device_id]
      register_device @device_id
      @messages = Message.find(:all, :conditions => ["device_id = ?", @device_id], :order => "created_at DESC")
    rescue
      @error_message = 'Error'
    end

    respond_to do |format|
      format.html
    end
  end

  def check_connection
    render :text => "It works!"
  end

  def show_new
    @error_message = nil

    begin
      @messages = Message.find(:all,
        :conditions => ["device_id = ? AND read = ?", params[:device_id], false],
        :order => "created_at DESC")
      @device_id = params[:device_id]
    rescue
      @error_message = 'An error occured while retrieving messages.'
    end
    
    respond_to do |format|
      format.html
    end
  end

  def get_unread
    count = 0
    begin
      device_id = params[:device_id]

      register_device device_id
      
      upload_key = params[:upload_key]

      if upload_key == 'pang.wu@sv.cmu.edu'
        count = Message.find(:all, :conditions => ["device_id = ? AND read = ?", device_id, false]).length
      
      end
      
    rescue
    end

    render :text => count.to_s
  end
  
  
  def read_message
    begin
      message_id = params[:id]
      message = Message.find(message_id)
      message.read = true
      message.save!
      redirect_to message.url.gsub('{device_id}', message.device_id).gsub('{message_id}', message_id)
    rescue
    end
  end

  def show_read
    @error_message = nil

    begin
      @messages = Message.find(:all,
        :conditions => ["device_id = ? AND read = ?", params[:device_id], true],
        :order => "created_at DESC")
      @device_id = params[:device_id]
    rescue
      @error_message = 'An error occured while retrieving messages.'
    end

    respond_to do |format|
      format.html
    end
  end

  # GET /messages/1
  # GET /messages/1.xml
  def show
    @message = Message.find(params[:id])
    @message.read = true
    @message.save!

    respond_to do |format|
      format.html # show.html.erb
      format.xml  { render :xml => @message }
    end
  end

  def show_json
    
    device_id = params[:device_id]
    messages = Message.find(:all, :conditions => ["device_id = ?", device_id])
    
    json_messages = []
    messages.each do |message|
      json_messages << {
        :id => message.id,
        :url => "#{message.url}".gsub('{device_id}', device_id).gsub('{message_id}', message.id.to_s),
        :read => message.read,
        :title => message.title,
        :created_at => message.created_at
      }
    end

    render :json => json_messages
  end

  # GET /messages/new
  # GET /messages/new.xml
  def new
    @message = Message.new
    @devices = []
    
    Address.find(:all).each do |address|
      @devices << {:id => address.device_id, :name => address.device_id}
    end
    @devices << {:id => '0', :name => 'All'}

    respond_to do |format|
      format.html # new.html.erb
      format.xml  { render :xml => @message }
    end
  end

  # GET /messages/1/edit
  def edit
    @devices = []

    Address.find(:all).each do |address|
      @devices << {:id => address.device_id, :name => address.device_id}
    end
    @devices << {:id => '0', :name => 'All'}
    
    @message = Message.find(params[:id])
  end

  # POST /messages
  # POST /messages.xml
  def create


    @message = Message.create(params[:message])
    if params[:device_id_text] && params[:device_id_text] != ""
      @message.device_id = params[:device_id_text]
    end
    
    if @message.device_id.to_s == '0'
      Address.find(:all).each do |address|
        # new_message = Message.create(params[:message])
        Message.create(
          :title => params[:message][:title],
          :read => params[:message][:read],
          :device_id => address.device_id,
          :url => params[:message][:url]
        )

      end

    end

    
    
    respond_to do |format|
      if @message.save
        format.html { redirect_to(@message, :notice => 'Message was successfully created.') }
        format.xml  { render :xml => @message, :status => :created, :location => @message }
      else
        format.html { render :action => "new" }
        format.xml  { render :xml => @message.errors, :status => :unprocessable_entity }
      end
    end
  end

  # PUT /messages/1
  # PUT /messages/1.xml
  def update
    @message = Message.find(params[:id])

    respond_to do |format|
      if @message.update_attributes(params[:message])
        format.html { redirect_to(@message, :notice => 'Message was successfully updated.') }
        format.xml  { head :ok }
      else
        format.html { render :action => "edit" }
        format.xml  { render :xml => @message.errors, :status => :unprocessable_entity }
      end
    end
  end

  # DELETE /messages/1
  # DELETE /messages/1.xml
  def destroy
    @message = Message.find(params[:id])
    @message.destroy

    respond_to do |format|
      format.html { redirect_to(messages_url) }
      format.xml  { head :ok }
    end
  end

  def delete_by_title
    title = 'Common Sharing Circle ID'
    Message.delete_all(["title = ?", title])

    render :text => 'done'
  end

  private
  def register_device(device_id)

    if device_id == nil || device_id == ''
      return nil
    end
    
    begin
      if Address.find(:first, :conditions => ["device_id = ?", device_id]) == nil
        # register the new device
        address = Address.create(:device_id => device_id,
          :street => 'N/A',
          :city => 'N/A',
          :state => 'N/A',
          :country => 'N/A',
          :zipcode => 'N/A',
          :email => 'N/A'
        )

        #create a default message for the newly joined device
        message = Message.create(
          :device_id => device_id,
          :read => false,
          :title => 'Welcome to Carnegie Mellon MobiSens!',
          :url => '/messages/tutorial?device_id={device_id}'
        )

        # copy all 'to all' messages under the new device id
        messages_to_all = Message.find(:all, :conditions => ["device_id = ?", 0])
        messages_to_all.each do |message_to_all|
          Message.create(
            :device_id => device_id,
            :title => message_to_all.title,
            :url => message_to_all.url,
            :read => false
          )
        end

        return [address, message]
      end
    rescue

    end
    return nil
  end

  def choose_layout
    if ['device_index', 'tutorial', 'show_new', 'show_read'].include? action_name
      'messages.html'
    elsif ['get_unread_count'].include? action_name
        nil
    else
      'application.html'
    end
  end
end
