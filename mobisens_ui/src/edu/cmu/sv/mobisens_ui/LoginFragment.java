package edu.cmu.sv.mobisens_ui;
import java.util.Arrays;

import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.widget.LoginButton;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
//import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class LoginFragment extends Fragment
{
  private UiLifecycleHelper uiHelper;

  private Session.StatusCallback callback = new Session.StatusCallback()
  {
    @Override
    public void call(Session session, SessionState state, Exception exception)
    {
      onSessionStateChange(session, state, exception);
    }
  };

  @Override
  public void onCreate(Bundle savedInstanceState)
  {
    super.onCreate(savedInstanceState);
    uiHelper = new UiLifecycleHelper(getActivity(), callback);
    uiHelper.onCreate(savedInstanceState);
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
  {
    View view = inflater.inflate(R.layout.activity_login, container, false);
    LoginButton authButton = (LoginButton) view.findViewById(R.id.authButton);
    authButton.setFragment(this);
    authButton.setReadPermissions(Arrays.asList("xmpp_login"));
 
    //TextView titleTextView = (TextView) view.findViewById(R.id.app_title);
    //Typeface myTypeface = Typeface.createFromAsset(titleTextView.getContext().getAssets(), "fonts/Champagne & Limousines Bold.ttf");
    //titleTextView.setTypeface(myTypeface);
    //UpdateFont.updateFont((TextView)view.findViewById(R.id.app_title), UpdateFont.CONVENE_FONT);
    return view;
  }

  private void onSessionStateChange(Session session, SessionState state, Exception exception)
  {
    if (state.isOpened())
    {
      Intent intent = new Intent(getActivity(), MainActivity.class); 
      startActivity(intent);
    }
  }

  @Override
  public void onResume()
  {
    super.onResume();
    // uiHelper.onResume();
    // For scenarios where the main activity is launched and user
    // session is not null, the session state change notification
    // may not be triggered. Trigger it if it's open/closed.
    Session session = Session.getActiveSession();
    if (session != null && (session.isOpened() || session.isClosed()))
    {
      onSessionStateChange(session, session.getState(), null);
    }

    uiHelper.onResume();
  }

  @Override
  public void onActivityResult(int requestCode, int resultCode, Intent data)
  {
    super.onActivityResult(requestCode, resultCode, data);
    uiHelper.onActivityResult(requestCode, resultCode, data);
  }

  @Override
  public void onPause()
  {
    super.onPause();
    uiHelper.onPause();
  }

  @Override
  public void onDestroy()
  {
    super.onDestroy();
    uiHelper.onDestroy();
  }

  @Override
  public void onSaveInstanceState(Bundle outState)
  {
    super.onSaveInstanceState(outState);
    uiHelper.onSaveInstanceState(outState);
  }
}
