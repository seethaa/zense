package edu.cmu.sv.lifelogger.database;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.location.Location;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

/**
 * Simple ERC database access helper class. Defines the basic 
 */
public class LocalDbAdapter {



	private DatabaseHelper mDbHelper;
	private SQLiteDatabase mDb;

	/**
	 * Database creation sql statement
	 */

	private Context mCtx;

	private static String imagesTableCreate = "create table Image (imageName text,  location text, activityID integer)";
	// 6 states supported, with type as 1= question, 2 = inform. 

	private static final String TAG = "DBHelper";
	private static final String DATABASE_NAME = "MobisensDB";
	private static final String DATABASE_TABLE1 = "Image";

	private static final int DATABASE_VERSION = 2;

	private static class DatabaseHelper extends SQLiteOpenHelper {

		private     Context context;

		DatabaseHelper(Context context) {


			super(context, DATABASE_NAME, null, DATABASE_VERSION);
			this.context = context;
			System.out.println("In DatabaseHelper constructor");

		}

		@Override
		public void onCreate(SQLiteDatabase db) {


			System.out.println("In onCreate()");

			db.execSQL(imagesTableCreate);


			/* Also seed data for default values */
			seedData(db);
			
			/*Toast.makeText(context, "4 table created", Toast.LENGTH_LONG).show();*/

		}

		public void seedData(SQLiteDatabase db){
			//@ToDo add Seed data here
			createImageRow(db, "test1.jpg",  Environment.getExternalStorageDirectory().getPath() +"/DCIM/Camera", 1);
			createImageRow(db, "test2.jpg",  Environment.getExternalStorageDirectory().getPath() +"/DCIM/Camera", 1);
			
		}

		
		public long createImageRow(SQLiteDatabase db,String imageName, String location, Integer activityID)
		{
			ContentValues initialValues = new ContentValues();
			initialValues.put("imageName", imageName);
			initialValues.put("location", location);
			initialValues.put("activityID", activityID);
			
			System.out.println("HIMZ: creating values");
			return db.insert("Image", null, initialValues);
		}
		
		

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
					+ newVersion + ", which will destroy all old data");
			db.execSQL("DROP TABLE IF EXISTS TripIt");
			onCreate(db);
		}
	}

	/**
	 * Constructor - takes the context to allow the database to be
	 * opened/created
	 * 
	 * @param ctx the Context within which to work
	 */
	public LocalDbAdapter(Context ctx) {
		this.mCtx = ctx;
	}

	/**
	 * Open the notes database. If it cannot be opened, try to create a new
	 * instance of the database. If it cannot be created, throw an exception to
	 * signal the failure
	 * 
	 * @return this (self reference, allowing this to be chained in an
	 *         initialization call)
	 * @throws SQLException if the database could be neither opened or created
	 */
	public LocalDbAdapter open() throws SQLException {

		System.out.println("In open()");

		mDbHelper = new DatabaseHelper(mCtx);
		mDb = mDbHelper.getWritableDatabase();

		return this;
	}
	/**
	 * @brief Close the adapter
	 */
	public void close() {
		mDbHelper.close();
	}






	
	public List<String>  getImagesForActivity(Integer activityID){
		Cursor c = null;
		int grpNo=0;
		List<String> labels = new ArrayList<String>();			
		c = mDb.rawQuery("select imageName,location from Image where activityID = " + "\"" + activityID +"\"", null);
		
		try{


			if (c.moveToFirst()) {
				do {
					   	
					labels.add(c.getString(1) + "/" + c.getString(0));
					
				} while (c.moveToNext());
			}

			// closing connection
			c.close();
		}
		catch(Exception e){
			System.out.println("asdf");
		}


		return labels ;
	}
	
	
	
	



	public static ArrayList<String> convertStringToArray(String str)
	{
		List<String> items = new ArrayList<String>(Arrays.asList(str.split("\\s*,\\s*")));
		System.out.println("arraylist=" + items);
		return (ArrayList<String>) items;
	}



}