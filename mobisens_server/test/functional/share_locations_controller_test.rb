require 'test_helper'

class ShareLocationsControllerTest < ActionController::TestCase
  test "should get index" do
    get :index
    assert_response :success
    assert_not_nil assigns(:share_locations)
  end

  test "should get new" do
    get :new
    assert_response :success
  end

  test "should create share_location" do
    assert_difference('ShareLocation.count') do
      post :create, :share_location => { }
    end

    assert_redirected_to share_location_path(assigns(:share_location))
  end

  test "should show share_location" do
    get :show, :id => share_locations(:one).to_param
    assert_response :success
  end

  test "should get edit" do
    get :edit, :id => share_locations(:one).to_param
    assert_response :success
  end

  test "should update share_location" do
    put :update, :id => share_locations(:one).to_param, :share_location => { }
    assert_redirected_to share_location_path(assigns(:share_location))
  end

  test "should destroy share_location" do
    assert_difference('ShareLocation.count', -1) do
      delete :destroy, :id => share_locations(:one).to_param
    end

    assert_redirected_to share_locations_path
  end
end
