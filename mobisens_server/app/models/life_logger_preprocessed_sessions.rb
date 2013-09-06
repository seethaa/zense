class LifeLoggerPreprocessedSessions < ActiveRecord::Base
  has_many :centroids, :dependent => :delete_all
end
