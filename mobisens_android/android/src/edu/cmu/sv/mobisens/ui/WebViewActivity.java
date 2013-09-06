package edu.cmu.sv.mobisens.ui;

import java.util.Calendar;


import edu.cmu.sv.mobisens.R;
import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.DatePicker;

public class WebViewActivity extends Activity {

	private WebView webView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);    	    	
		setContentView(R.layout.activity_web_view);
		prepareWebView();

	}

	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case 0:
			final Calendar c = Calendar.getInstance();
			int mYear = c.get(Calendar.YEAR);
			int mMonth = c.get(Calendar.MONTH);
			int mDay = c.get(Calendar.DAY_OF_MONTH);
			return new DatePickerDialog(this, mDateSetListener, mYear, mMonth,
					mDay);
		}
		return null;
	}

	public void loadScript(String script) {
		webView.loadUrl("javascript:(function() { " + script + "})()");
	}

	private DatePickerDialog.OnDateSetListener mDateSetListener = new DatePickerDialog.OnDateSetListener() {

		@Override
		public void onDateSet(DatePicker view, int year, int monthOfYear,
				int dayOfMonth) {

			String dateString = Integer.toString(monthOfYear) + "/"
					+ Integer.toString(dayOfMonth) + "/"
					+ Integer.toString(year);

			loadScript("document.getElementById('date').value = '" + dateString
					+ "';"); // ERROR
		}

	};

	private void prepareWebView() {
		webView = (WebView) findViewById(R.id.webView);

		WebSettings webSettings = webView.getSettings();
		webSettings.setJavaScriptEnabled(true);
		webSettings.setSupportMultipleWindows(true);
		webSettings.setJavaScriptCanOpenWindowsAutomatically(true);

		webView.setWebViewClient(new DateSetterWebViewClient());
		webView.setWebChromeClient(new CustomChromeClient());
		webView.addJavascriptInterface(new CustomJavaScriptInterface(),
				"DateInterface");

		String url = "http://mlt.sv.cmu.edu:3000/sessions/show_map_in_mobile/229?start_13=1362902400000&end_13=1362985200000";
//				"http://mlt.sv.cmu.edu:3000/sessions/show_map_in_mobile/229"; 
//				"http://mlt.sv.cmu.edu:3000/sessions/show_map_in_mobile/229";
		webView.loadUrl(url);
	}

	private class DateSetterWebViewClient extends WebViewClient {
		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			view.loadUrl(url);
			return false;
		}

		@Override
		public void onPageFinished(WebView view, String url) {
			loadScript("document.getElementById('date').onfocus = function(){this.blur();window.DateInterface.pickDate();};");
		}
	}

	private class CustomJavaScriptInterface {
		public void pickDate() {
			try {
				showDialog(0);
			} catch (Exception e) {

			}
		}
	}

	private class CustomChromeClient extends WebChromeClient {
		@Override
		public boolean onJsAlert(WebView view, String url, String message,
				final JsResult result) {
			// handle Alert event, here we are showing AlertDialog
			new AlertDialog.Builder(WebViewActivity.this)
			.setTitle("JavaScript Alert !")
			.setMessage(message)
			.setPositiveButton(android.R.string.ok,
					new AlertDialog.OnClickListener() {
				public void onClick(DialogInterface dialog,
						int which) {
					// do your stuff
					result.confirm();
				}
			}).setCancelable(false).create().show();
			return true;
		}
	}

}
