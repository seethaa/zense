require "base64"

class SpecialProfilesController < ApplicationController
  before_filter :require_user, :only => [:index, :delete]
  
  def index
    

    if params[:profile_id] != nil
      new_special_profile = SpecialProfiles.create(:profile_id => params[:profile_id].to_i)
      if new_special_profile == nil
        flash[:notice] = 'Create Special Profile Failed.'
      end
    end

    special_profiles = SpecialProfiles.all
    @profiles = []
    special_profiles.each do |special_profile|
     @profiles <<  {:profile => Profile.find(:first, :conditions => ["id = ?", special_profile.profile_id]),
       :special_profile => special_profile
     }
    end
    
  end

  def delete
    item_to_delete = SpecialProfiles.find(params[:id].to_i)
    item_to_delete.destroy
    
    redirect_to :action => :index
  end

  # Get all special profiles and render them in JSON.
  def get_all
    special_profiles = SpecialProfiles.all
    json_profiles = []
    special_profiles.each do |special_profile|
      profile = Profile.find(:first, :conditions => ["id = ?", special_profile.profile_id])
      base64_image = ""

      begin
        if File.exist?(profile.photo.path)
          image_file = File.open(profile.photo.path, "rb")
          base64_image = ActiveSupport::Base64.encode64(image_file.read())
          image_file.close
        end
      rescue
        # something goes wrong here.
      end

      hash_object = {:profile =>
          {:config => profile.config,
            :id => profile.id,
            :photo => base64_image,
            :photo_file_name => profile.photo_file_name,
            :photo_file_size => profile.photo_file_size,
            :photo_url => profile.photo.url,
            :name => profile.name
          }
        }
      json_profiles << hash_object
    end

    render :json => json_profiles
  end
end
