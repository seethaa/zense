require 'test_helper'

class ShareActivitiesControllerTest < ActionController::TestCase
  test "should get index" do
    get :index
    assert_response :success
    assert_not_nil assigns(:share_activities)
  end

  test "should get new" do
    get :new
    assert_response :success
  end

  test "should create share_activity" do
    assert_difference('ShareActivity.count') do
      post :create, :share_activity => { }
    end

    assert_redirected_to share_activity_path(assigns(:share_activity))
  end

  test "should show share_activity" do
    get :show, :id => share_activities(:one).to_param
    assert_response :success
  end

  test "should get edit" do
    get :edit, :id => share_activities(:one).to_param
    assert_response :success
  end

  test "should update share_activity" do
    put :update, :id => share_activities(:one).to_param, :share_activity => { }
    assert_redirected_to share_activity_path(assigns(:share_activity))
  end

  test "should destroy share_activity" do
    assert_difference('ShareActivity.count', -1) do
      delete :destroy, :id => share_activities(:one).to_param
    end

    assert_redirected_to share_activities_path
  end
end
