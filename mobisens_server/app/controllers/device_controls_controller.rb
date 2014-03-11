class DeviceControlsController < ApplicationController
  before_filter :require_user, :only => [:index, :new, :create, :show, :edit, :update, :destroy, :delete_all]
  
  # GET /device_controls
  # GET /device_controls.xml
  def index
    @device_controls = DeviceControl.all

    respond_to do |format|
      format.html # index.html.erb
      format.xml  { render :xml => @device_controls }
    end
  end

  def get_config
    config_text = Profile.default_profile
    if params.has_key?('device_id')
      device_control = DeviceControl.find(:first, :conditions => ["device_id = ?", params['device_id']])
      if device_control == nil
        device_control = DeviceControl.find(:first, :conditions => ["device_id = ?", 0])
      end
      
      if device_control != nil
        config_text = device_control.profile.config
      end
    end

    render :text => config_text.gsub("\t", '').gsub("\n", '').gsub("\r",'')
  end

  # GET /device_controls/1
  # GET /device_controls/1.xml
  def show
    @device_control = DeviceControl.find(params[:id])

    respond_to do |format|
      format.html # show.html.erb
      format.xml  { render :xml => @device_control }
    end
  end

  # GET /device_controls/new
  # GET /device_controls/new.xml
  def new
    @device_control = DeviceControl.new

    respond_to do |format|
      format.html # new.html.erb
      format.xml  { render :xml => @device_control }
    end
  end

  # GET /device_controls/1/edit
  def edit
    @device_control = DeviceControl.find(params[:id])
  end

  # POST /device_controls
  # POST /device_controls.xml
  def create
    @device_control = DeviceControl.new(params[:device_control])

    respond_to do |format|
      if @device_control.save
        format.html { redirect_to(@device_control, :notice => 'DeviceControl was successfully created.') }
        format.xml  { render :xml => @device_control, :status => :created, :location => @device_control }
      else
        format.html do
          flash[:notice] = "Cannot create the association, " +
            "a configuration associates with Device ID '#{@device_control.device_id}'" +
            " might already exists.<br/>" +
            "Please edit that association instead of creating a new one.<br/>"
          render :action => "new" 
        end
        format.xml  { render :xml => @device_control.errors, :status => :unprocessable_entity }
      end
    end
  end

  # PUT /device_controls/1
  # PUT /device_controls/1.xml
  def update
    @device_control = DeviceControl.find(params[:id])

    respond_to do |format|
      if @device_control.update_attributes(params[:device_control])
        format.html { redirect_to(@device_control, :notice => 'DeviceControl was successfully updated.') }
        format.xml  { head :ok }
      else
        format.html { render :action => "edit" }
        format.xml  { render :xml => @device_control.errors, :status => :unprocessable_entity }
      end
    end
  end

  # DELETE /device_controls/1
  # DELETE /device_controls/1.xml
  def destroy
    @device_control = DeviceControl.find(params[:id])
    @device_control.destroy

    respond_to do |format|
      format.html { redirect_to(device_controls_url) }
      format.xml  { head :ok }
    end
  end

  def delete_all
    DeviceControl.destroy_all

    respond_to do |format|
      format.html { redirect_to(device_controls_url) }
      format.xml  { head :ok }
    end
  end
  
end
