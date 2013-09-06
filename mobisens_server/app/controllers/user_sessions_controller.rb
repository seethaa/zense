class UserSessionsController < ApplicationController

  # Create new user session.
  def new
    @user_session = UserSession.new
  end

  # Create a new session for user login.
  def create
    flash[:notice] = ''
    
    @user_session = UserSession.new(params[:user_session])
    if @user_session.save
      #flash[:notice] = "Successfully logged in."
      redirect_to :controller => "uploads", :action => "index"
    else
      flash[:notice] = "Login failed, wrong username or password."
      redirect_to :action => "new"
    end
  end

  # Destroy the session for a specific user.
  # Usually called when the user logs out.
  def destroy
    @user_session = UserSession.find(params[:id])
    if @user_session
      @user_session.destroy
    end
    #flash[:notice] = "Successfully logged off."
    redirect_to :action => "new"
  end
  
end
