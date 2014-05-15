package edu.cmu.sv.mobisens;

import edu.cmu.sv.lifelogger.api.ActivityRecognitionRequestScheduler;
import edu.cmu.sv.mobisens.content.AnnotationWidget;
import edu.cmu.sv.mobisens.content.ForegroundUploadWidget;
import edu.cmu.sv.mobisens.content.MessageBoxWidget;
import edu.cmu.sv.mobisens.content.MiscDataDumpingWidget;
import edu.cmu.sv.mobisens.content.ModelWidget;
import edu.cmu.sv.mobisens.content.ProfileWidget;
import edu.cmu.sv.mobisens.io.DataMigration;
import edu.cmu.sv.mobisens.io.DataShrinker;
import edu.cmu.sv.mobisens.io.MobiSensLog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;

// Maintain all the notification in status bar.
public class NotificationService extends Service {

	
	
	public final static int DATA_SERVICE_RUNNING = 2;
	
	private final static String TAG = "NotifyService";

	private static NotificationService instance = null;
	
	
	private Boolean videoAnnotationNeedUpload = false;
	private boolean dataServiceIsRunning = false;
	
	private DataShrinker dataShrinker;
	
	private ForegroundUploadWidget uploadWidget = new ForegroundUploadWidget();
	private ProfileWidget profileWidget = new ProfileWidget();
	private MessageBoxWidget messageBoxWidget = new MessageBoxWidget();
	private AnnotationWidget annotationWidget = new AnnotationWidget();
	private ModelWidget modelWidget = new ModelWidget();
	
	private MiscDataDumpingWidget uploadControllerWidget = new MiscDataDumpingWidget();

	
	
	
	public static NotificationService getInstance(){
		return instance;
	}
	
	
	public static Notification makeDataServiceRunningNotification(){
		if(NotificationService.getInstance() != null){
			return NotificationService.getInstance().makeDataCollectionServiceRunningNotification();
		}
		
		return null;
	}
	
	public static void removeDataServiceRunningNotification(){
		if(NotificationService.getInstance() != null){
			NotificationService.getInstance().cancelDataCollectionServiceRunningNotification();
		}
	}
	
	
	
	public Notification makeDataCollectionServiceRunningNotification(){

		long when = System.currentTimeMillis();
		Notification notification = new Notification(R.drawable.monitoring, "MobiSens is running...", when);
		notification.flags = Notification.FLAG_ONGOING_EVENT;
		
		Context context = getApplicationContext();

		CharSequence contentTitle = getString(R.string.service_running_notify_ticker_text);
		CharSequence contentText = getString(R.string.service_running_notify_content_text);
		
		Intent launcherIntent = new Intent(this, MobiSensLauncher.class);
		launcherIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		
		PendingIntent contentIntent = PendingIntent.getActivity(this, 0, launcherIntent, 0);

		notification.setLatestEventInfo(context, contentTitle, contentText, contentIntent);
		//mNotificationManager.notify(DATA_SERVICE_RUNNING, notification);
		this.dataServiceIsRunning = true;
		
		return notification;
	}
	
	public void cancelDataCollectionServiceRunningNotification(){
		NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		mNotificationManager.cancel(DATA_SERVICE_RUNNING);
		dataServiceIsRunning = false;
	}

	
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}
	
	public void onCreate(){
		
		instance = this;

		this.dataShrinker = new DataShrinker(this);
		this.messageBoxWidget.register(this);
		this.profileWidget.register(this);
		this.uploadWidget.register(this);
		
		this.uploadControllerWidget.register(this);
		this.annotationWidget.register(this);
		// Since we disabled the machine annotation, we don't need the model widget.
		// this.modelWidget.register(this);   
		
		dataShrinker.start();
		System.out.println("Here Notification Service Created");
		DataMigration.migrate(this);
		ActivityRecognitionRequestScheduler.init(this);
		
		MobiSensLog.log("Notification service started.");
		
	}
	
	public void onDestroy(){
		
		dataShrinker.exit();
		
		this.modelWidget.unregister();
		this.annotationWidget.unregister();
		this.uploadControllerWidget.unregister();
		
		this.profileWidget.unregister();
		this.uploadWidget.unregister();
		

		this.messageBoxWidget.unregister();
		ActivityRecognitionRequestScheduler.close();

		NotificationService.instance = null;
	}
	


}
