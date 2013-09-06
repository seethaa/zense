require 'test_helper'

class ShareSessionMembersControllerTest < ActionController::TestCase
  test "should get index" do
    get :index
    assert_response :success
    assert_not_nil assigns(:share_session_members)
  end

  test "should get new" do
    get :new
    assert_response :success
  end

  test "should create share_session_member" do
    assert_difference('ShareSessionMember.count') do
      post :create, :share_session_member => { }
    end

    assert_redirected_to share_session_member_path(assigns(:share_session_member))
  end

  test "should show share_session_member" do
    get :show, :id => share_session_members(:one).to_param
    assert_response :success
  end

  test "should get edit" do
    get :edit, :id => share_session_members(:one).to_param
    assert_response :success
  end

  test "should update share_session_member" do
    put :update, :id => share_session_members(:one).to_param, :share_session_member => { }
    assert_redirected_to share_session_member_path(assigns(:share_session_member))
  end

  test "should destroy share_session_member" do
    assert_difference('ShareSessionMember.count', -1) do
      delete :destroy, :id => share_session_members(:one).to_param
    end

    assert_redirected_to share_session_members_path
  end
end
