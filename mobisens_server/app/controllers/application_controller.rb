# Filters added to this controller apply to all controllers in the application.
# Likewise, all the methods added will be available for all controllers.
require 'authlogic'

class ApplicationController < ActionController::Base
  helper :all # include all helpers, all the time
  protect_from_forgery :except => :create # See ActionController::RequestForgeryProtection for details

  # Scrub sensitive parameters from your log
  # filter_parameter_logging :password
  #Paperclip::Railtie.insert

  filter_parameter_logging :password, :password_confirmation
  helper_method :current_user_session, :current_user
  before_filter :activate_authlogic

private
    def current_user_session
      return @current_user_session if defined?(@current_user_session)
      @current_user_session = UserSession.find
    end

    def current_user
      return @current_user if defined?(@current_user)
      @current_user = current_user_session && current_user_session.record
    end

    def require_user
      unless current_user
        store_location
        flash[:notice] = "You must be logged in to access this page"
        redirect_to new_user_session_url
        return false
      end
    end

    def require_no_user
      if current_user
        current_user_session.destroy
        
        return false
      end
    end

    def store_location
      session[:return_to] = request.request_uri
    end

    def redirect_back_or_default(default)
      redirect_to(session[:return_to] || default)
      session[:return_to] = nil
    end

    def register_device(device_id)

      if device_id == nil || device_id == ''
        return nil
      end

      begin
        address = Address.find(:first, :conditions => ["device_id = ?", device_id])

        if address == nil
          # register the new device
          address = Address.create(:device_id => device_id,
            :street => 'N/A',
            :city => 'N/A',
            :state => 'N/A',
            :country => 'N/A',
            :zipcode => 'N/A',
            :email => 'N/A'
          )


        end

        return address
      rescue
        
      end

      return nil
    end
end
