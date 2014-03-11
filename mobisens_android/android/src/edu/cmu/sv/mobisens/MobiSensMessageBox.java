package edu.cmu.sv.mobisens;

import java.util.ArrayList;


import edu.cmu.sv.mobisens.content.MessageBoxWidget;
import edu.cmu.sv.mobisens.io.MobiSensLog;
import edu.cmu.sv.mobisens.net.Network;
import edu.cmu.sv.mobisens.net.URLs;
import edu.cmu.sv.mobisens.ui.HeaderListViewItem;
import edu.cmu.sv.mobisens.ui.ImageListViewAdapter;
import edu.cmu.sv.mobisens.ui.ImageListViewItem;
import edu.cmu.sv.mobisens.ui.MapAnnotationActivity;
import edu.cmu.sv.mobisens.util.Annotation;
import edu.cmu.sv.mobisens.util.ServerMessage;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ListView;

public class MobiSensMessageBox extends ListActivity {
	private final static String CLASS_PREFIX = MobiSensMessageBox.class.getName();
	private final static String TAG = "MobiSensMessageBox";
	private final static String ACTION_LOAD_MSG_COMPLETE = CLASS_PREFIX + ".action_load_message_done";
	
	private ServerMessage[] messages;
	private void loadMessagesAsync(){
		
		AsyncTask<Void, Void, ServerMessage[]> task = new AsyncTask<Void, Void, ServerMessage[]>(){

			@Override
			protected ServerMessage[] doInBackground(Void... params) {
				// TODO Auto-generated method stub
				return ServerMessage.getMessagesFromServer(MobiSensService.getDeviceID(MobiSensMessageBox.this));
			}
			
			@Override
			protected void onPostExecute(ServerMessage[] messages){
				if(messages == null)
					return;
				MobiSensMessageBox.this.messages = messages;
				Intent removeNotificationIntent = new Intent(MessageBoxWidget.ACTION_REMOVE_MESSAGEBOX_NOTIFY);
				MobiSensMessageBox.this.sendBroadcast(removeNotificationIntent);
				
				ArrayList<ImageListViewItem> listItems = new ArrayList<ImageListViewItem>();
		        
				for(ServerMessage message:messages){
			        listItems.add(new ImageListViewItem(message.getTitle(),
			        		message.isRead() ? message.getCreatedDate().toLocaleString() : "Unread Message", 
			        		message.isRead() ? R.drawable.sensec_green : R.drawable.sensec_red));
				}
				
				ImageListViewAdapter adapter = new ImageListViewAdapter(MobiSensMessageBox.this, 
		        		R.layout.message_listview_item,
		        		listItems);
				MobiSensMessageBox.this.setListAdapter(adapter);
			}
			
		};
		
		task.execute(new Void[0]);
	}
	
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.list_activity);
		
		ActionBar actionBar = getActionBar();
		if(actionBar != null){
			actionBar.setDisplayHomeAsUpEnabled(true);
			actionBar.setDisplayShowHomeEnabled(true);
		}
		
	}
	
	protected void onDestroy(){
		super.onDestroy();
		
	}
	
	protected void onResume(){
		super.onResume();
		
		AsyncTask<String, Void, Boolean> task = new AsyncTask<String, Void, Boolean>(){

			@Override
			protected Boolean doInBackground(String... params) {
				// TODO Auto-generated method stub
				return Network.canConnectToServer(URLs.MESSAGEBOX_CONNECTION_URL);
			}
			
			protected void onPostExecute(Boolean result) {
		        if(result){
		        	loadMessagesAsync();
		        }else{
		        	AlertDialog alertDialog = new AlertDialog.Builder(MobiSensMessageBox.this).create();
			    	alertDialog.setTitle(getString(R.string.message_dialog_title));
			    	alertDialog.setMessage(getString(R.string.messagebox_network_error));
		        	alertDialog.setButton(DialogInterface.BUTTON_NEUTRAL, "OK", new DialogInterface.OnClickListener() {
		    			public void onClick(DialogInterface dialog, int which) {
		    				MobiSensMessageBox.this.finish();
		    				return;
		    			}
		        	});
		        	alertDialog.setIcon(R.drawable.abort_dlg);
		        	
		        	alertDialog.show();
		        }
			}

			
		};
		
		task.execute(URLs.MESSAGEBOX_CONNECTION_URL);
		
		
	}
	
	protected void onPause(){
		Intent refreshNotificationIntent = new Intent(MessageBoxWidget.ACTION_REFRESH_MESSAGEBOX_NOTIFY);
		this.sendBroadcast(refreshNotificationIntent);
		
		super.onPause();
		
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    switch (item.getItemId()) {
	        case android.R.id.home:
	            // app icon in action bar clicked; go home
	            Intent intent = new Intent(this, MobiSensLauncher.class);
	            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
	            startActivity(intent);
	            return true;
	        default:
	            return super.onOptionsItemSelected(item);
	    }
	}
	
	protected void onListItemClick(ListView l, View v, int position, long id){
		//ImageListViewItem item = (ImageListViewItem)this.getListAdapter().getItem(position);
		
		ServerMessage message = this.messages[position];
		String url = message.getMessageURL();
		if(url.indexOf("/") == 0){  // A URL relative to the MobiSens gateway.
			url = URLs.MOBISENS_HOST_URL + url;
		}
		
		final ServerMessage.AsyncCallbackHandler callBackHandler = new ServerMessage.AsyncCallbackHandler() {
			
			@Override
			public void onComplete(ServerMessage sender) {
				// TODO Auto-generated method stub
				loadMessagesAsync(); // Reload the crap
			}
		};
		
		message.setAsReadAsync(callBackHandler);
		
		
		try{
			Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
			startActivity(browserIntent); // open the browser
			
		}catch(Exception ex){
			ex.printStackTrace();
			MobiSensLog.log(ex);
		}
		
	}
}
