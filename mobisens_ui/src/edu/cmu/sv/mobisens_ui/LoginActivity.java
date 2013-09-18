package edu.cmu.sv.mobisens_ui;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Activity which displays a login screen to the user, offering registration as
 * well.
 */
public class LoginActivity extends FragmentActivity
{
  private LoginFragment loginFragment;

  // private static final String TAG = "MainFragment";

  @Override
  public void onCreate(Bundle savedInstanceState)
  {
    super.onCreate(savedInstanceState);

    setContentView(R.layout.activity_login);
   
    if (savedInstanceState == null)
    {
      // Add the fragment on initial activity setup
      loginFragment = new LoginFragment();
      getSupportFragmentManager().beginTransaction()
          .add(android.R.id.content, loginFragment)
          .commit();
    }
    else
    {
      // Or set the fragment from restored state info
      loginFragment = (LoginFragment) getSupportFragmentManager()
          .findFragmentById(android.R.id.content);
    }
  }

  

  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
  {
    View view = inflater.inflate(R.layout.activity_login, container, false);
    return view;
  }
}
