package edu.cmu.sv.lifelogger.helpers;
/**
 * Class to store all the global variables.
 * Mainly handling database connections for now
 * As a general practice, start moving shared state among actiities to this 
 * class. 
 * 
 * @author himanshu
 * 
 */
import java.util.Date;

import edu.cmu.sv.lifelogger.database.LocalDbAdapter;
import android.app.Application;
import android.content.Context;

public class App extends Application {
    public LocalDbAdapter db;
    // Store the latest activity shown in timeline here(Data Sharing)
    public Date lastTimelineActivityDate;

    @Override
    public void onCreate() {
        super.onCreate();
        Context ctx = getApplicationContext(); 
        this.db = new LocalDbAdapter(ctx);
        this.db.open();
    }

    @Override
    public void onTerminate() {
        this.db.close();
        super.onTerminate();
    }
}