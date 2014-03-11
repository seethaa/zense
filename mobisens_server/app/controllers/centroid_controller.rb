class CentroidController < ApplicationController
  before_filter :require_user, :only =>[:delete_all]
  skip_before_filter :verify_authenticity_token, :only => [:import]

  def pick_preprocessed_session_by_random
    picked_id = -1

    if params[:key] != 'pang.wu@sv.cmu.edu'
      render :text => 'Invalid request key.', :status => 500
      return
    end

    device_id = params[:device_id]

    sessions = LifeLoggerPreprocessedSessions.find(:all, :conditions => ["device_id = ?", device_id])
    
    if sessions.length == 0
      sessions = LifeLoggerPreprocessedSessions.find(:all)
      
    end
    
    preprocessed_sessions = []
    
    sessions.each do |session|
      preprocessed_sessions << session.id
    end

    if preprocessed_sessions.size == 0
      render :text => picked_id.to_s
      return
    end

    rand_index = 0
    if preprocessed_sessions.size > 1
      rand_index = rand(preprocessed_sessions.size)
    end

    render :text => preprocessed_sessions[rand_index].to_s #picked_id.to_s
  end
  
  def get_acc_centroids_by_session_id

    if params[:key] != 'pang.wu@sv.cmu.edu'
      render :text => 'Invalid request key.', :status => 500
      return
    end
    
    result = ''
    #centroids = Centroid.find(:all, :conditions => ["life_logger_preprocessed_sessions_id = ?", params[:id]])
    centroids = Centroid.find(:all, :conditions => ["life_logger_preprocessed_sessions_id = ?", params[:id]])
    
    centroids.each do |centroid|
      if result != ''
        result << ",6,#{centroid.avg_x},#{centroid.avg_y},#{centroid.avg_z},#{centroid.std_x},#{centroid.std_y},#{centroid.std_z}"
      else
        result << "6,#{centroid.avg_x},#{centroid.avg_y},#{centroid.avg_z},#{centroid.std_x},#{centroid.std_y},#{centroid.std_z}"
      end
    end

    render :text => result
  end

  def import
    key = params[:key]
    if key != 'pang.wu@sv.cmu.edu'
      render :text => 'Invalid upload key', :status => 500
      return
    end


    session_id = params[:session_id]
    device_id = params[:login]
    centroid_file = params[:file]
    data_line = centroid_file.gets

    if LifeLoggerPreprocessedSessions.find(:first, :conditions => ["session_id = ? AND device_id = ?", session_id, device_id])
      render :text => 'Session already imported', :status => 500
      return
    end

    preprocessed_session = LifeLoggerPreprocessedSessions.create(:session_id => session_id,
          :device_id => device_id)

    centroids = []
    while data_line && preprocessed_session

      columns = data_line.split(",")
      centroids << Centroid.new(
        :avg_x => columns[0],
        :avg_y => columns[1],
        :avg_z => columns[2],
        :std_x => columns[3],
        :std_y => columns[4],
        :std_z => columns[5],
        :label => columns[6],
        :life_logger_preprocessed_sessions_id => preprocessed_session.id
      )
      
      data_line = centroid_file.gets

    end

    centroid_file.close
    Centroid.import centroids
    
    render :text => "done"
  end
  
  def import_fast
    key = params[:key]
    if key != 'pang.wu@sv.cmu.edu'
      render :text => 'Invalid upload key', :status => 500
      return
    end


    session_id = params[:session_id]
    device_id = params[:login]
    centroid_file = params[:file]
    data_line = centroid_file.gets

    if LifeLoggerPreprocessedSessions.find(:first, :conditions => ["session_id = ? AND device_id = ?", session_id, device_id])
      render :text => 'Session already imported', :status => 500
      return
    end

    preprocessed_session = LifeLoggerPreprocessedSessions.create(:session_id => session_id,
          :device_id => device_id)

    centroid_sql_string_header = "INSERT INTO #{Centroid.table_name} " +
                      "(avg_x,avg_y,avg_z,std_x,std_y,std_z,life_logger_preprocessed_sessions_id,label,created_at,updated_at)"+
                      "VALUES "
    centroid_sql_string = "#{centroid_sql_string_header}"
    current_time = Time.now.utc.to_formatted_s(:db)


    sql_data_string = ""
    first_line = true

    max_query_size = 900 * 1024 # The max size of a sql query, define by /etc/my.conf
    query_size = centroid_sql_string.length
    line_count = 0

    #================== SQLite DOESNOT support multi-line insert===================
        
    while data_line && preprocessed_session

      columns = data_line.split(",")

      if first_line
        first_line = false
        sql_data_string = "(#{columns[0]},#{columns[1]},#{columns[2]},#{columns[3]}," << 
          "#{columns[4]},#{columns[5]},#{preprocessed_session.id}," <<
          "'#{columns[6].strip}','#{current_time}','#{current_time}');"

      else
        sql_data_string = ",(#{columns[0]},#{columns[1]},#{columns[2]},#{columns[3]}," <<
          "#{columns[4]},#{columns[5]},#{preprocessed_session.id}," <<
          "'#{columns[6].strip}','#{current_time}','#{current_time}');"
      end

      
      centroid_sql_string << sql_data_string
      query_size += sql_data_string.length


      if query_size > max_query_size
        Centroid.connection.execute centroid_sql_string
        centroid_sql_string = "#{centroid_sql_string_header}"
        first_line = true
        query_size = centroid_sql_string.length
      end

      data_line = centroid_file.gets
      line_count += 1
    end

    if !first_line
      Centroid.connection.execute centroid_sql_string
    end
    centroid_file.close
    
    render :text => "done"
  end

  def delete_all
    LifeLoggerPreprocessedSessions.destroy_all()
    render :text => "done"
  end
end
