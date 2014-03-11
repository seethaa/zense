package edu.cmu.sv.mobisens.content;


import edu.cmu.sv.mobisens.MobiSensLauncher;
import edu.cmu.sv.mobisens.R;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;

public class ForegroundUploadWidget extends UploadWidget {
	
	
	public void register(ContextWrapper contextWrapper){
		super.register(contextWrapper);
		
		if(this.getContext() instanceof Service){
			((Service)this.getContext()).startForeground(0, this.createNotification());
		}
	}
	
	public void unregister(){
		if(this.getContext() instanceof Service){
			((Service)this.getContext()).stopForeground(true);
		}
		
		super.unregister();
	}
	
	public Notification createNotification(){

		if(this.getContext() == null){
			return null;
			
		}
		
		long when = System.currentTimeMillis();
		Notification notification = new Notification(R.drawable.monitoring, "MobiSens is running...", when);
		notification.flags = Notification.FLAG_ONGOING_EVENT;

		CharSequence contentTitle = this.getContext().getString(R.string.service_running_notify_ticker_text);
		CharSequence contentText = this.getContext().getString(R.string.service_running_notify_content_text);
		
		Intent launcherIntent = new Intent(this.getContext(), MobiSensLauncher.class);
		launcherIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		
		PendingIntent contentIntent = PendingIntent.getActivity(this.getContext().getApplicationContext(), 0, launcherIntent, 0);

		notification.setLatestEventInfo(this.getContext(), contentTitle, contentText, contentIntent);

		return notification;
	}
	
}
