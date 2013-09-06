require 'test_helper'

class DeviceControlsControllerTest < ActionController::TestCase
  test "should get index" do
    get :index
    assert_response :success
    assert_not_nil assigns(:device_controls)
  end

  test "should get new" do
    get :new
    assert_response :success
  end

  test "should create device_control" do
    assert_difference('DeviceControl.count') do
      post :create, :device_control => { }
    end

    assert_redirected_to device_control_path(assigns(:device_control))
  end

  test "should show device_control" do
    get :show, :id => device_controls(:one).to_param
    assert_response :success
  end

  test "should get edit" do
    get :edit, :id => device_controls(:one).to_param
    assert_response :success
  end

  test "should update device_control" do
    put :update, :id => device_controls(:one).to_param, :device_control => { }
    assert_redirected_to device_control_path(assigns(:device_control))
  end

  test "should destroy device_control" do
    assert_difference('DeviceControl.count', -1) do
      delete :destroy, :id => device_controls(:one).to_param
    end

    assert_redirected_to device_controls_path
  end
end
