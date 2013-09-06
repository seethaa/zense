class SenSecModelController < ApplicationController


  def get_model
    if !params.has_key?(:key)
      render :text => '', :status => 500
      return
    end

    if params[:key] != 'pang.wu@sv.cmu.edu'
      render :text => '', :status => 500
      return
    end

    model_string = ''
    begin
      device_id = params[:device_id]
      file_path = "#{Rails.configuration.root_path}/public/sensec_models/#{device_id}.model.csv"

      if File.exist?(file_path)
        model_file = File.open(file_path, 'rb')
        model_string = model_file.read
        model_file.close
      end
      
    rescue

    end

    render :text => model_string
    
  end
end
