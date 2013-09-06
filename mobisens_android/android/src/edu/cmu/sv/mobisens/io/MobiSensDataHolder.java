package edu.cmu.sv.mobisens.io;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.Locale;

import edu.cmu.sv.mobisens.util.Annotation;
import edu.cmu.sv.mobisens.util.MachineAnnotation;

import android.content.Context;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class MobiSensDataHolder {
	private static final String TAG = "MobiSensDataHolder";
	
    private SQLiteDatabase db;
    private MobiSensSQLiteHelper dbHelper;
	
    public MobiSensDataHolder (Context context) {
    	dbHelper = new MobiSensSQLiteHelper(context);
    }

    public void open() throws SQLException {
    	Log.v(TAG, "open");
    	MobiSensLog.log("DB: open");
    	
    	db = dbHelper.getWritableDatabase();
    }
    
    public void close() {
    	Log.v(TAG, "close");
    	MobiSensLog.log("DB: close");
    	
        dbHelper.close();
    }
    
    public boolean add(long startTime, long endTime, String annotation) {
    	Log.v(TAG, "add");
    	MobiSensLog.log("DB: add");
    	
    	if (!db.isOpen()) {
    		Log.e(TAG, "db is not open!");
    		return false;
    	}
    	
    	Annotation anno = Annotation.fromString(annotation);
    	int type = (anno instanceof MachineAnnotation) ? 1 : 0;
    	
    	if (!MobiSensData.isValid(startTime, endTime, annotation, type)) {
    		Log.e(TAG, "data are invalid!");
    		return false;
    	}	
    	
    	// Remove the id from annotation string
    	
    	anno.setDBId(-1);
    	annotation = anno.toString();
    	
    	ContentValues values = new ContentValues();
    	values.put(MobiSensSQLiteHelper.COLUMN_START_TIME, startTime);
    	values.put(MobiSensSQLiteHelper.COLUMN_END_TIME, endTime);
    	values.put(MobiSensSQLiteHelper.COLUMN_ANNOTATION, annotation);
    	values.put(MobiSensSQLiteHelper.COLUMN_TYPE, type);
    	return (db.insert(MobiSensSQLiteHelper.TABLE_MOBISENSDATA, null, values) >= 0 ? true : false);
    }	
    
    public int batchRemove(long reserveTimeInMS) {
    	Log.v(TAG, "batchRemove");
    	MobiSensLog.log("DB: batchRemove");
    	
    	if (!db.isOpen()) {
    		Log.e(TAG, "db is not open!");
    		return 0;
    	}
    	
    	MobiSensData lastData = this.getLast();
    	if(lastData == null)
    		return 0;
    	
    	long endTime = lastData.getEndTime() - reserveTimeInMS;
    	int rowsRemoved = db.delete(MobiSensSQLiteHelper.TABLE_MOBISENSDATA, MobiSensSQLiteHelper.COLUMN_END_TIME+"<"+String.valueOf(endTime), null);
    	MobiSensLog.log("End time: " + endTime + ", " + rowsRemoved + " records removed.");
    	return rowsRemoved;
    }
    
    public String[] search(long s, long e) {
    	Log.v(TAG, "search");
    	MobiSensLog.log("DB: search");
    	
    	if (!db.isOpen()) {
    		Log.e(TAG, "db is not open!");
    		return null;
    	}    	
    	String[] queryColumns = {MobiSensSQLiteHelper.COLUMN_ID, MobiSensSQLiteHelper.COLUMN_ANNOTATION};
    	String selection = String.format(Locale.US, "%s >= %d and %s <= %d", MobiSensSQLiteHelper.COLUMN_START_TIME, s, MobiSensSQLiteHelper.COLUMN_START_TIME, e);
    	String orderBy = MobiSensSQLiteHelper.COLUMN_START_TIME + " ASC";
    	
    	Cursor cursor = db.query(MobiSensSQLiteHelper.TABLE_MOBISENSDATA, queryColumns, selection, null, null, null, orderBy);
    	int count = cursor.getCount();
    	Log.v(TAG, "found " + count + " records.");
    	if (count <= 0 ) {
    		return new String[]{};
    	}
    	
    	String[] results = new String[count];
    	cursor.moveToFirst();
    	for (int i=0; i<count; i++){
    		results[i] = cursor.getString(1) + "\t_id:" + cursor.getLong(0);
    		cursor.moveToNext();
    	}
    	cursor.close();
    	MobiSensLog.log("DB: search ended");
    	return results;
    }
    
    public boolean dumpAllToFile(File file){
    	Log.v(TAG, "dumpAllToFile");
    	MobiSensLog.log("DB: dumpAllToFile");
    	
    	if (!db.isOpen()) {
    		Log.e(TAG, "db is not open!");
    		return  false;
    	}    	
    	String[] queryColumns = {MobiSensSQLiteHelper.COLUMN_ANNOTATION, MobiSensSQLiteHelper.COLUMN_START_TIME};
    	String orderBy = MobiSensSQLiteHelper.COLUMN_START_TIME + " ASC";
    	Cursor cursor = db.query(MobiSensSQLiteHelper.TABLE_MOBISENSDATA, queryColumns, null, null, null, null, orderBy);
    	int count = cursor.getCount();
    	Log.v(TAG, "found " + count + " records.");
    	if (count <= 0 ) {
    		return true;
    	}
    	
    	try{
        	cursor.moveToFirst();
        	
			FileWriter fileWritter = new FileWriter(file, true);
	        BufferedWriter bufferWritter = new BufferedWriter(fileWritter, 100 * 1024);
	        
	        for (int i=0; i<count; i++){
	    		String annoString = cursor.getString(0);
	    		bufferWritter.write(annoString + "\r\n");
	    		cursor.moveToNext();
	    	}  	
	        
	        
	        bufferWritter.close();
		}catch(Exception ex){
			MobiSensLog.log(ex);
			return false;
		}
		
    	cursor.close();
    	
    	return true;
    }
    
    
    public String[] searchByType(int type) {
    	Log.v(TAG, "searchByType"); 
    	MobiSensLog.log("DB: searchByType");
    	
    	if (!db.isOpen()) {
    		Log.e(TAG, "db is not open!");
    		return null;
    	}    	
    	String[] queryColumns = { MobiSensSQLiteHelper.COLUMN_ID, 
    			MobiSensSQLiteHelper.COLUMN_ANNOTATION, 
    			MobiSensSQLiteHelper.COLUMN_TYPE, 
    			MobiSensSQLiteHelper.COLUMN_START_TIME };
    	String selection = String.format(Locale.US, "%s = %d", MobiSensSQLiteHelper.COLUMN_TYPE, type);
    	String orderBy = MobiSensSQLiteHelper.COLUMN_START_TIME + " ASC";
    	Cursor cursor = db.query(MobiSensSQLiteHelper.TABLE_MOBISENSDATA, queryColumns, selection, null, null, null, orderBy);
    	int count = cursor.getCount();
    	Log.v(TAG, "found " + count + " records.");
    	if (count <= 0 ) {
    		return new String[]{};
    	}
    	
    	String[] results = new String[count];
    	cursor.moveToFirst();
    	for (int i=0; i<count; i++){
    		results[i] = cursor.getString(1) + "\t_id:" + cursor.getLong(0);
    		cursor.moveToNext();
    	}  	
    	cursor.close();
    	MobiSensLog.log("DB: searchByType ended");
    	
    	return results;
    }
    
    public boolean set(long id, long startTime, long endTime, String annotation) {
    	Log.v(TAG, "set");
    	MobiSensLog.log("DB: set");
    	
    	if (!db.isOpen()) {
    		Log.e(TAG, "db is not open!");
    		return false;
    	}
    	
    	Annotation anno = Annotation.fromString(annotation);
    	int type = (anno instanceof MachineAnnotation) ? 1 : 0;
    	
    	if (!MobiSensData.isValid(startTime, endTime, annotation, type)) {
    		Log.e(TAG, "data are invalid!");
    		return false;
    	}
    	
    	// Remove the id from annotation string
    	anno.setDBId(-1);
    	annotation = anno.toString();
    	
    	ContentValues values = new ContentValues();
    	values.put(MobiSensSQLiteHelper.COLUMN_ANNOTATION, annotation);
    	values.put(MobiSensSQLiteHelper.COLUMN_TYPE, type);
    	values.put(MobiSensSQLiteHelper.COLUMN_END_TIME, endTime);
    	String selection = String.format(Locale.US, "%s = %d", MobiSensSQLiteHelper.COLUMN_ID, id);
    	long rowAffected = db.update(MobiSensSQLiteHelper.TABLE_MOBISENSDATA, values, selection, null);
    	
    	Log.i(TAG, "Row affected: " + String.valueOf(rowAffected));
    	// Kate: This seems doesn't works.
    	if (0 == rowAffected) {
    		Log.e(TAG, "db update " + selection + " failed!");
    		return add(startTime, endTime, annotation);
    	}else{
    		// Debug here
    		
    		/*
    		String[] updated = this.search(startTime, endTime);
    		
    		for(String anno:updated){
    			Log.i(TAG, anno);
    		}
    		*/
    	}
    	return true;
    }	
    
    public int clear() {
    	Log.v(TAG, "clear");
    	MobiSensLog.log("DB: clear");
    	
    	if (!db.isOpen()) {
    		Log.e(TAG, "db is not open!");
    		return 0;
    	}
    	return db.delete(MobiSensSQLiteHelper.TABLE_MOBISENSDATA, null, null);
    }

    public MobiSensData getFirst() {
    	Log.v(TAG, "getFirst");
    	MobiSensLog.log("DB: getFirst");
    	
    	if (!db.isOpen()) {
    		Log.e(TAG, "db is not open!");
    		return null;
    	}    	
    	
    	String[] queryColumns = {MobiSensSQLiteHelper.COLUMN_START_TIME, MobiSensSQLiteHelper.COLUMN_END_TIME, 
    			MobiSensSQLiteHelper.COLUMN_ANNOTATION, 
    			MobiSensSQLiteHelper.COLUMN_TYPE,
    			MobiSensSQLiteHelper.COLUMN_ID};
    	String orderBy = MobiSensSQLiteHelper.COLUMN_START_TIME + " ASC";
    	Cursor cursor = db.query(MobiSensSQLiteHelper.TABLE_MOBISENSDATA, queryColumns, null, null, null, null, orderBy, "1");
    	if (cursor.getCount() <= 0) {
    		Log.v(TAG, "table is empty");
    		return null;
    	}
    	cursor.moveToFirst();
    	MobiSensData data = new MobiSensData(cursor.getLong(0), cursor.getLong(1), 
    			cursor.getString(2) + "\t_id:" + cursor.getLong(4), 
    			cursor.getInt(3));
    	cursor.close();
    	return data;
    }
    
    public MobiSensData getLast() {
    	Log.v(TAG, "getLast");
    	MobiSensLog.log("DB: getLast");
    	
    	if (!db.isOpen()) {
    		Log.e(TAG, "db is not open!");
    		return null;
    	}    	
    	
    	String[] queryColumns = { MobiSensSQLiteHelper.COLUMN_START_TIME, 
    			MobiSensSQLiteHelper.COLUMN_END_TIME, 
    			MobiSensSQLiteHelper.COLUMN_ANNOTATION, 
    			MobiSensSQLiteHelper.COLUMN_TYPE,
    			MobiSensSQLiteHelper.COLUMN_ID
    			};
    	String orderBy = MobiSensSQLiteHelper.COLUMN_END_TIME + " DESC";
    	Cursor cursor = db.query(MobiSensSQLiteHelper.TABLE_MOBISENSDATA, queryColumns, null, null, null, null, orderBy, "1");
    	if (cursor.getCount() <= 0) {
    		Log.v(TAG, "table is empty");
    		return null;
    	}
    	cursor.moveToLast();
    	
    	MobiSensData data = new MobiSensData(cursor.getLong(0), cursor.getLong(1), 
    			cursor.getString(2) + "\t_id:" + cursor.getLong(4), 
    			cursor.getInt(3)); 
    	cursor.close();
    	return data;
    }
}
