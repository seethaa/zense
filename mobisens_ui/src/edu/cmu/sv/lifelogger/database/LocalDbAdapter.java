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
	private static String activityTableCreate = "create table ActivityTable ( activityID integer, activityName text,  description text, activityType text)";
	private static String userTableCreate = "create table Users( userID integer, userName text,  email text, about text, profilePictureLocation text)";
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
			db.execSQL(activityTableCreate);
			db.execSQL(userTableCreate);
			/* Also seed data for default values */
			seedData(db);
			
			/*Toast.makeText(context, "4 table created", Toast.LENGTH_LONG).show();*/

		}

		public void seedData(SQLiteDatabase db){
			//@ToDo add Seed data here
			

			// Create User
			createUserRow(db,1, "Frank Hsueh", "abc@gmail.com", "I am super man", "");
			// Seed the activity table first for first activity id
			createActivityRow(db,1, "Driving", "I was driving from home to work", "Driving");
			createImageRow(db, "test1.jpg",  Environment.getExternalStorageDirectory().getPath() +"/DCIM/Camera", 1);
			createImageRow(db, "test2.jpg",  Environment.getExternalStorageDirectory().getPath() +"/DCIM/Camera", 1);
			createImageRow(db, "1384037586887.jpg",  Environment.getExternalStorageDirectory().getPath() +"/DCIM/Camera", 2);
			createImageRow(db, "1384026465395.jpg",  Environment.getExternalStorageDirectory().getPath() +"/DCIM/Camera", 2);
			createActivityRow(db, 2, "Dining", "I am eating my laptop", "Dining");
		}

		/**
		 * Function to create user in the system. Profile picture must be loaded
		 *  in to the system
		 * Assumption now, fetch from facebook all the time
		 * -- This is only seeding function. NO api should be provided to 
		 * create users, rather api to fetch user from the backend should be 
		 * implemented when possible
		 * @param db
		 * @param userID
		 * @param userName
		 * @param email
		 * @param about
		 * @param profilePictureLocation
		 */
		private long createUserRow(SQLiteDatabase db, int userID, String userName,
				String email, String about, String profilePictureLocation) {
			// TODO Auto-generated method stub
			ContentValues initialValues = new ContentValues();
			initialValues.put("userID", userID);
			initialValues.put("userName", userName);
			initialValues.put("email", email);
			initialValues.put("about", about);
			initialValues.put("profilePictureLocation", profilePictureLocation);
			
			System.out.println("HIMZ: creating values");
			return db.insert("ActivityTable", null, initialValues);
			
		}

		private long createActivityRow(SQLiteDatabase db, int activityID, String activityName,
				String description, String activityType) {

			ContentValues initialValues = new ContentValues();
			initialValues.put("activityID", activityID);
			initialValues.put("activityName", activityName);
			initialValues.put("description", description);
			initialValues.put("activityType", activityType);
			
			System.out.println("HIMZ: creating values");
			return db.insert("ActivityTable", null, initialValues);
			
		}

		private long createImageRow(SQLiteDatabase db,String imageName, String location, Integer activityID)
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






	/**
	 * API to fetch all the images from the local DB for the given activity ID
	 * @param activityID
	 * @return
	 */
	public List<String> getImagesForActivity(Integer activityID){
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
	
	public String getNameAndDescriptionForActivity(Integer activityID){
		Cursor c = null;
		int grpNo=0;
		String nameAndDescription= new String();			
		c = mDb.rawQuery("select activityName,description from ActivityTable where activityID = " + "\"" + activityID +"\"", null);
		
		try{


			if (c.moveToFirst()) {
				do {
					   	
					nameAndDescription = c.getString(0) + ": " + c.getString(1);
					
				} while (c.moveToNext());
			}

			// closing connection
			c.close();
		}
		catch(Exception e){
			System.out.println("asdf");
		}


		return nameAndDescription ;
	}

	

	public String getUserName(Integer userID){
		Cursor c = null;
		String name= new String();			
		c = mDb.rawQuery("select userName from Users where userID = " + "\"" + userID +"\"", null);
		
		try{


			if (c.moveToFirst()) {
				do {
					   	
					name = c.getString(0) + ": " + c.getString(1);
					
				} while (c.moveToNext());
			}

			// closing connection
			c.close();
		}
		catch(Exception e){
			System.out.println("asdf");
		}


		return name ;
	}
	
	
	public String getUserEmail(Integer userID){
		Cursor c = null;
		String email= new String();			
		c = mDb.rawQuery("select email from Users where userID = " + "\"" + userID +"\"", null);
		
		try{


			if (c.moveToFirst()) {
				do {
					   	
					email = c.getString(0) + ": " + c.getString(1);
					
				} while (c.moveToNext());
			}

			// closing connection
			c.close();
		}
		catch(Exception e){
			System.out.println("asdf");
		}


		return email ;
	}

	/**
	 * 
	 * @param userID
	 * @return
	 */
	public String getAboutUser(Integer userID){
		Cursor c = null;
		String about= new String();			
		c = mDb.rawQuery("select about from Users where userID = " + "\"" + userID +"\"", null);
		
		try{
			if (c.moveToFirst()) {
				do {
					   	
					about = c.getString(0) + ": " + c.getString(1);
					
				} while (c.moveToNext());
			}

			// closing connection
			c.close();
		}
		catch(Exception e){
			System.out.println("asdf");
		}
		return about ;
	}
	
	/**
	 * API to store images location in the local DB
	 * @param imageName
	 * @param location
	 * @param activityID
	 * @return
	 */
	public long createImageRow(String imageName, String location, Integer activityID)
	{
		ContentValues initialValues = new ContentValues();
		initialValues.put("imageName", imageName);
		initialValues.put("location", location);
		initialValues.put("activityID", activityID);
		
		System.out.println("HIMZ: creating values");
		return mDb.insert("Image", null, initialValues);
	}

	
	/**
	 * API to store activity information in our Database
	 * @param activityID
	 * @param activityName
	 * @param description
	 * @param activityType
	 * @return
	 */
	public long createActivityRow(int activityID, String activityName,
			String description, String activityType) {

		ContentValues initialValues = new ContentValues();
		initialValues.put("activityID", activityID);
		initialValues.put("activityName", activityName);
		initialValues.put("description", description);
		initialValues.put("activityType", activityType);
		
		System.out.println("HIMZ: creating values");
		return mDb.insert("ActivityTable", null, initialValues);
		
	}
	



	private static ArrayList<String> convertStringToArray(String str)
	{
		List<String> items = new ArrayList<String>(Arrays.asList(str.split("\\s*,\\s*")));
		System.out.println("arraylist=" + items);
		return (ArrayList<String>) items;
	}



}