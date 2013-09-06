class UsersController < ApplicationController
  before_filter :require_no_user, :only => [:new, :create]
  before_filter :require_user, :only => [:index, :show, :edit, :update]

  def index
    @users = User.all
  end
  
  def new
    @user = User.new
  end

  def create
    auth_key = params[:auth_key]
    @user = User.new(params[:user])
    
    if auth_key != User::AUTH_KEY
      flash[:notice] = "Incorrect Authentication Key."
      render :action => 'new'
    else
      
      if @user.save
        flash[:notice] = "Account registered!"
        redirect_to :controller => 'user_sessions', :action => 'new'
      else
        flash[:notice] = "Account registration failed!"
        render :action => 'new'
      end
    end
    
    
  end

  def show
    @user = @current_user
  end

  def edit
    @user = @current_user
  end

  def update
    @user = @current_user # makes our views "cleaner" and more consistent
    if @user.update_attributes(params[:user])
      flash[:notice] = "Account updated!"
      redirect_to account_url
    else
      render :action => :edit
    end
  end
end
