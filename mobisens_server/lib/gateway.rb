# To change this template, choose Tools | Templates
# and open the template in the editor.

class Gateway
  def initialize
    
  end


  def self.distribute(request_params)
    device_id = request_params[:device_id]
    file_type = request_params[:file_type]

    # if necessary, do something with the device id then distribute.
    storage_server_domain = "10.0.13.152"
    save_data_url = "http://#{storage_server_domain}:3000/uploads/save_data"
    return_obj = {}
    log_file = File.open("#{Rails.configuration.root_path}/log/gateway.log", "a")

    begin
      
      # make a MultipartPost
      mp = Multipart::MultipartPost.new

      # Get both the headers and the query ready,
      # given the new MultipartPost and the params
      # Hash
      query, headers = mp.prepare_query(request_params)

      # Make sure the URL is useable
      url = URI.parse(save_data_url)

      # Do the actual POST, given the right inputs
      result = Multipart::post_form(url, query, headers)
      status_code = result[:status]

      if result[:exception] == nil
        return_obj = {:message => result[:content], :status => status_code}
      else
        return_obj = {:message => "#{return_obj[:content]} #{result[:content].backtrace}", :status => -1}
      end

    rescue => exception
      return_obj = {:message => exception.backtrace, :status => -1}
    end

    log_file.puts "#{DateTime.now}, #{device_id}, file_type #{file_type}: #{return_obj[:message]}, status: #{return_obj[:status]}."
    log_file.close

    return return_obj
    
  end
end
