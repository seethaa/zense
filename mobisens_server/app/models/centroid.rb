require 'ar-extensions'

class Centroid < ActiveRecord::Base
  belongs_to :life_logger_preprocessed_session
end
