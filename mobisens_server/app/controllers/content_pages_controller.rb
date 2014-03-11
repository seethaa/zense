require 'will_paginate'

class ContentPagesController < ApplicationController

  layout :choose_layout

  before_filter :require_user, :only => [:index, :new, :create, :edit, :show, :update, :destroy]
  
  # GET /content_pages
  # GET /content_pages.xml
  def index
    page = 1
    if params.has_key?(:page)
      page = params[:page]
    end

    @content_pages = ContentPage.display_all_by_page(page)

    respond_to do |format|
      format.html # index.html.erb
      format.xml  { render :xml => @content_pages }
    end
  end

  # GET /content_pages/1
  # GET /content_pages/1.xml
  def show
    @content_page = ContentPage.find(params[:id])

    respond_to do |format|
      format.html # show.html.erb
      format.xml  { render :xml => @content_page }
    end
  end

  def device_show
    begin
      @content_page = ContentPage.find(params[:id])
      @message_title = Message.find(params[:message_id]).title
      @device_id = params[:device_id]
    rescue
      
    end
    

    respond_to do |format|
      format.html # show.html.erb
      format.xml  { render :xml => @content_page }
    end
  end

  # GET /content_pages/new
  # GET /content_pages/new.xml
  def new
    @content_page = ContentPage.new

    respond_to do |format|
      format.html # new.html.erb
      format.xml  { render :xml => @content_page }
    end
  end

  # GET /content_pages/1/edit
  def edit
    @content_page = ContentPage.find(params[:id])
  end

  # POST /content_pages
  # POST /content_pages.xml
  def create
    @content_page = ContentPage.new(params[:content_page])

    respond_to do |format|
      if @content_page.save
        format.html { redirect_to(@content_page, :notice => 'ContentPage was successfully created.') }
        format.xml  { render :xml => @content_page, :status => :created, :location => @content_page }
      else
        format.html { render :action => "new" }
        format.xml  { render :xml => @content_page.errors, :status => :unprocessable_entity }
      end
    end
  end

  # PUT /content_pages/1
  # PUT /content_pages/1.xml
  def update
    @content_page = ContentPage.find(params[:id])

    respond_to do |format|
      if @content_page.update_attributes(params[:content_page])
        format.html { redirect_to(@content_page, :notice => 'ContentPage was successfully updated.') }
        format.xml  { head :ok }
      else
        format.html { render :action => "edit" }
        format.xml  { render :xml => @content_page.errors, :status => :unprocessable_entity }
      end
    end
  end

  # DELETE /content_pages/1
  # DELETE /content_pages/1.xml
  def destroy
    @content_page = ContentPage.find(params[:id])
    @content_page.destroy

    respond_to do |format|
      format.html { redirect_to(content_pages_url) }
      format.xml  { head :ok }
    end
  end

  def choose_layout
    if ['device_show'].include? action_name
      'messages.html'
    else
      'application.html'
    end
  end
end
