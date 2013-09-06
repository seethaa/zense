# This controller is almost the same with uploads_controller

class VideosController < ApplicationController
  layout :choose_layout
  before_filter :require_user, :only => [:index, :new, :show, :edit, :update, :delete_all]
  
  # GET /videos
  # GET /videos.xml
  def index
    @devices = Address.find(:all)
    if params.has_key?(:device_id)
      @videos = Video.find(:all,
        :conditions =>["device_id = ?", params[:device_id]],
        :order => "timestamp DESC")
    else
      @videos = Video.find(:all, :order => "timestamp DESC")
    end


    respond_to do |format|
      format.html # index.html.erb
      format.xml  { render :xml => @videos }
    end
  end


  def calendar

    timestamps = []
    jump_to_specified_date = false

    if params.has_key?(:ts)
      timestamps << params[:ts].to_i
      jump_to_specified_date = true
    end

    @view = 'agendaWeek'
    if params.has_key?(:view)
      @view = params[:view]
    end
    
    @devices = Address.find(:all)
    @selected_device = Address.find(:first,
      :conditions =>["device_id = ?", params[:id]])

    @videos = Video.find(:all,
      :conditions =>["device_id = ?", params[:id]],
      :order => "timestamp DESC")

    @videos_json = []
    @videos.each do |video|
      @videos_json << {:id => video.id,
        :url => video.video.url,
        :title => video.annotation == nil ? '(No title)' : video.annotation,
        :description => video.annotation == nil ? '(No description)' : video.annotation,
        :start => "#{video.timestamp / 1000}",
        :end => "#{video.timestamp / 1000 + 2}",
        :allDay => false,
        :recurring => false}

      if !jump_to_specified_date
        timestamps << video.timestamp / 1000
      end
      
    end
    

    if timestamps.size > 0
      @ts = Time.at(timestamps.max)
    else
      @ts = Time.current
    end

    respond_to do |format|
      format.html # index.html.erb
      format.xml  { render :xml => @videos }
    end
  end

  # GET /videos/1
  # GET /videos/1.xml
  def show
    @video = Video.find(params[:id])

    respond_to do |format|
      format.html # show.html.erb
      format.xml  { render :xml => @video }
    end
  end

  # GET /videos/new
  # GET /videos/new.xml
  def new
    @video = Video.new

    respond_to do |format|
      format.html # new.html.erb
      format.xml  { render :xml => @video }
    end
  end

  # GET /videos/1/edit
  def edit
    @video = Video.find(params[:id])
  end

  # POST /videos
  # POST /videos.xml
  def create
    flash[:notice] = ''

    if params.has_key?(:file)
      register_device params[:device_id]

      @video = Video.new(:video => params[:file],
        :upload_password => params[:upload_password],
        :device_id => params[:device_id],
        :timestamp => params[:timestamp],
        :annotation => params[:annotation]
      )
    else
      @video = Video.new(params[:upload])
      @video.timestamp = DateTime.now.to_i
    end

    respond_to do |format|
      if @video.save
        format.html { redirect_to :action => 'index' }
        format.xml  { render :xml => @video, :status => :created, :location => @video }
      else
        format.html do
          flash[:notice] = 'Upload Error.'
          redirect_to :action => "index"
        end
        format.xml  { render :xml => @video.errors, :status => :unprocessable_entity }
      end
    end
  end

  # PUT /videos/1
  # PUT /videos/1.xml
  def update
    @video = Video.find(params[:id])

    respond_to do |format|
      if @video.update_attributes(params[:video])
        format.html { redirect_to(@video, :notice => 'Video was successfully updated.') }
        format.xml  { head :ok }
      else
        format.html { render :action => "edit" }
        format.xml  { render :xml => @video.errors, :status => :unprocessable_entity }
      end
    end
  end

  # DELETE /videos/1
  # DELETE /videos/1.xml
  def destroy
    @video = Video.find(params[:id])
    @video.destroy

    respond_to do |format|
      format.html { redirect_to(videos_url) }
      format.xml  { head :ok }
    end
  end

  def delete_all
    Video.destroy_all()

    respond_to do |format|
      format.html { redirect_to(videos_url) }
      format.xml  { head :ok }
    end
  end

  private
  def choose_layout
    if ['calendar'].include? action_name
      'calendar.html'
    else
      'application.html'
    end
  end
  

end
