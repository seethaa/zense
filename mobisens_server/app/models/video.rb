class Video < ActiveRecord::Base
  validate :validate_upload_password
  has_attached_file :video


  def upload_password=(value)
    @upload_password = value
  end

  def upload_password
    return @upload_password
  end

  private
  @upload_password

  def validate_upload_password
    if self.upload_password != 'pang.wu@sv.cmu.edu'
      errors.add(:upload_password, "Upload password incorrect.")
    end

  end
end
