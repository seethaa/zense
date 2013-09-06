package edu.cmu.sv.mobisens.io;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class MobiSensSQLiteHelper extends SQLiteOpenHelper {
	private static final String TAG = "MobiSensSQLiteHelper";
	
	// database-related constants
	private static final String DATABASE_NAME = "mobisens.db";
	private static final int DATABASE_VERSION = 2;	
	
	// table-related constants
	public static final String TABLE_MOBISENSDATA = "mobi_sens_data";
	public static final String COLUMN_ID = "_id";
	public static final String COLUMN_START_TIME = "start_time";
	public static final String COLUMN_END_TIME = "end_time";
	public static final String COLUMN_ANNOTATION = "annotation";
	public static final String COLUMN_TYPE = "annotator_type";
		
	private static final String TABLE_CREATE_MOBISENSDATA = 
		String.format("CREATE TABLE %s (%s INTEGER PRIMARY KEY, %s INTEGER, %s INTEGER, %s TEXT, %s INTEGER);",
				TABLE_MOBISENSDATA, COLUMN_ID, COLUMN_START_TIME, COLUMN_END_TIME, COLUMN_ANNOTATION, COLUMN_TYPE);
	private static final String TABLE_DROP_MOBISENSDATA = "DROP TABLE IF EXISTS " + TABLE_MOBISENSDATA;
	
	private Context appContext;
	
	public MobiSensSQLiteHelper(Context context) {
	    super(context, DATABASE_NAME, null, DATABASE_VERSION);
	    appContext = context;
	}
	
	@Override
	public void onCreate(SQLiteDatabase db) {
		Log.v(TAG, TABLE_CREATE_MOBISENSDATA);
		db.execSQL(TABLE_CREATE_MOBISENSDATA);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Log.v(TAG, TABLE_DROP_MOBISENSDATA);
		db.execSQL(TABLE_DROP_MOBISENSDATA);
	    onCreate(db);
	}
}
