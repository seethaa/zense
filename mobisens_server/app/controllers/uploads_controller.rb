require 'will_paginate'
require 'tempfile'
require 'zlib'

class UploadsController < ApplicationController
  before_filter :require_user, :only => [:index, :new, :show, :edit, :update, :delete_all]
  skip_before_filter :verify_authenticity_token, :only => [:save_data]
  
  # GET /uploads
  # GET /uploads.xml
  def index

    @devices = Address.find(:all)
    
    
    @selected_device_id = '-1'
    @selected_file_type = -1
    
    if params[:device_id_text] && params[:device_id_text] != ""
      @selected_device_id = params[:device_id_text]
    elsif params[:device_id]
      @selected_device_id = params[:device_id]
    end


    if params[:file_type]
      @selected_file_type = params[:file_type].to_i
    end

    
    if params[:page]
      page = params[:page]
    end


    #if @selected_device_id != ''
      @uploads = Upload.display_by_device_id_and_filetype_by_page(page, @selected_device_id, @selected_file_type)
    #else
    #  @uploads = Upload.display_all_by_page(page)
    #end


    respond_to do |format|
      format.html # index.html.erb
      format.xml  { render :xml => @uploads }
    end
  end

  # GET /uploads/1
  # GET /uploads/1.xml
  def show
    @upload = Upload.find(params[:id])

    respond_to do |format|
      format.html # show.html.erb
      format.xml  { render :xml => @upload }
    end
  end

  # GET /uploads/new
  # GET /uploads/new.xml
  def new
    @upload = Upload.new

    respond_to do |format|
      format.html # new.html.erb
      format.xml  { render :xml => @upload }
    end
  end

  # GET /uploads/1/edit
  def edit
    @upload = Upload.find(params[:id])
  end

  # POST /uploads
  # POST /uploads.xml
  # This is the gateway method
  def create
    
    params[:upload_time] = DateTime.now

    ## Some error happens, save the file locally.
    upload = nil

    if params.has_key?(:file)
      register_device params[:device_id]

      file = params[:file]
      decompressed_file = nil
      
      if params[:compressed] == 'true'
        compressed_stream = Zlib::GzipReader.new(file)
        decompressed_file = Tempfile.new(File.basename(file.path))

        # puts decompressed_file.path
        IO.copy_stream(compressed_stream, decompressed_file)
        compressed_stream.close
        
      end
      
      upload = Upload.new(:record => decompressed_file.nil? ? params[:file] : decompressed_file,
        :upload_password => params[:upload_password],
        :device_id => params[:device_id],
        :file_type => params[:file_type],
        :upload_time => params[:upload_time])
    else
      upload = Upload.new(params[:upload])
      upload.upload_time = params[:upload_time]
    end

    if upload.save && ExportQueue.create(
          :upload_id => upload.id,
          :generate_time => upload.upload_time.utc.to_i,
          :exported => false,
          :try_count => 0,
          :file_type => params[:file_type]
        )

      if !decompressed_file.nil?
        decompressed_file.close
        decompressed_file.unlink
      end

      # Fail to distribute to other storages (VM power down?),
      # but managed to save locally.
      render :text => true.to_s

    else

      # Fail to distribute to other storages (VM power down?),
      # failed to save locally, must be re-uploaded.

      render :text => false.to_s

    end
    
  end

  def save_data
    upload = nil
    device_id = params[:device_id]

    log_file = File.open("#{Rails.root}/log/save_data.log", "a")

    begin
      if device_id != nil && device_id != ""
        if params.has_key?(:file)
          register_device device_id

          file = params[:file]
          decompressed_file = nil

          if params[:compressed] == 'true'
            compressed_stream = Zlib::GzipReader.new(file)
            decompressed_file = Tempfile.new(File.basename(file.path))
            IO.copy_stream(compressed_stream, decompressed_file)
            compressed_stream.close
            # decompressed_file.close
          end

          upload = Upload.new(:record => decompressed_file.nil? ? params[:file] : decompressed_file,
            :upload_password => params[:upload_password],
            :device_id => params[:device_id],
            :file_type => params[:file_type],
            :upload_time => params[:upload_time])

        else
          upload = Upload.new(params[:upload])
          upload.upload_time = params[:upload_time]
        end

        if upload.save

          export_queue_item = ExportQueue.new(
            :upload_id => upload.id,
            :generate_time => upload.upload_time.utc.to_i,
            :exported => false,
            :try_count => params[:try_count],
            :file_type => params[:file_type]
          )

          export_queue_item.save!

          if !decompressed_file.nil?
            decompressed_file.close
            decompressed_file.unlink
          end
          log_file.puts "#{Time.now.to_formatted_s(:db)}: save file #{params[:file].path} done."
          log_file.close
          
          render :text => true.to_s
          return
        end
      end
    rescue => exception
      log_file.puts "#{Time.now.to_formatted_s(:db)}: #{exception.backtrace}"
    end

    log_file.close
    
    render :text => false.to_s, :status => 500

  end

  # PUT /uploads/1
  # PUT /uploads/1.xml
  def update
    @upload = Upload.find(params[:id])

    respond_to do |format|
      if @upload.update_attributes(params[:upload])
        format.html { redirect_to(@upload, :notice => 'Upload was successfully updated.') }
        format.xml  { head :ok }
      else
        format.html { render :action => "edit" }
        format.xml  { render :xml => @upload.errors, :status => :unprocessable_entity }
      end
    end
  end

  # DELETE /uploads/1
  # DELETE /uploads/1.xml
  def destroy
    @upload = Upload.find(params[:id])
    @upload.destroy

    respond_to do |format|
      format.html { redirect_to(uploads_url) }
      format.xml  { head :ok }
    end
  end

  def delete_all

    # Deprecated
    render :text => "error", :status => 500
    return


    if params[:key] != 'pang.wu@sv.cmu.edu'
      render :text => "error", :status => 500
      return
    end
    
    Upload.destroy_all()

    respond_to do |format|
      format.html { redirect_to(uploads_url) }
      format.xml  { head :ok }
    end
  end
end
