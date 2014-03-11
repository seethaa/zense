require 'test_helper'

class ShareSessionsControllerTest < ActionController::TestCase
  test "should get index" do
    get :index
    assert_response :success
    assert_not_nil assigns(:share_sessions)
  end

  test "should get new" do
    get :new
    assert_response :success
  end

  test "should create share_session" do
    assert_difference('ShareSession.count') do
      post :create, :share_session => { }
    end

    assert_redirected_to share_session_path(assigns(:share_session))
  end

  test "should show share_session" do
    get :show, :id => share_sessions(:one).to_param
    assert_response :success
  end

  test "should get edit" do
    get :edit, :id => share_sessions(:one).to_param
    assert_response :success
  end

  test "should update share_session" do
    put :update, :id => share_sessions(:one).to_param, :share_session => { }
    assert_redirected_to share_session_path(assigns(:share_session))
  end

  test "should destroy share_session" do
    assert_difference('ShareSession.count', -1) do
      delete :destroy, :id => share_sessions(:one).to_param
    end

    assert_redirected_to share_sessions_path
  end
end
