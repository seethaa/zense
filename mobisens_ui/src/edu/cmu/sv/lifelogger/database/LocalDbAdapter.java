package edu.cmu.sv.lifelogger.database;

import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.android.gms.maps.model.LatLng;

import android.content.ContentValues;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;
import android.util.Log;
import au.com.bytecode.opencsv.CSVWriter;
import edu.cmu.sv.lifelogger.entities.Activity;
import edu.cmu.sv.lifelogger.entities.ActivityItem;
import edu.cmu.sv.lifelogger.entities.TimelineItem;
import edu.cmu.sv.lifelogger.helpers.Coordinates;

/**
 * Simple database access helper class. Defines the basic 
 */
public class LocalDbAdapter {



	private DatabaseHelper mDbHelper;
	private SQLiteDatabase mDb;

	/**
	 * Database creation sql statement
	 */

	private Context mCtx;
	/* @TODO another decision to add  */
	private static String imagesTableCreate = "create table Image (imageName text,  location text, activityID integer)";
	private static String IMAGE_TABLE_NAME = "Image";
	private static String activityTableCreate = "create table ActivityTable "
			+ "(activityID integer, activityName text,  description text, "
			+ "activityType text,startLocation text," +
			"endLocation text, startTime text, endTime text, startLocationLat text, startLocationLng text, "
			+ "endLocationLat text, endLocationLng text)";
	private static String ACTIVITY_TABLE_NAME = "ActivityTable";
	private static String userTableCreate = "create table Users( userID integer, userName text,  email text, about text, profilePictureLocation text)";
	private static String USERS_TABLE_NAME = "Users";

	/*
	 * Huge locations table to store all the locations associated with all the ID's
	 * It is a weak entity totally depending on activityid
	 * */	
	private static String locationsTableCreate = "create table locations( activityID integer, latitude text , longitude text ,timestamp text )";
	private static String LOCATIONS_TABLE_NAME = "Locations";

	private static String taggedLocationsTableCreate = "create table TaggedLocations( activityID integer, latitude text , longitude text, title text)";
	private static String TAGGED_LOCATIONS_TABLE_NAME = "TaggedLocations";

	private static String dashboardSummaryTable = "create table DashboardSummary( activityType text, percentage real)";
	private static String DASHBOARD_SUMMARY_TABLE_NAME = "DashboardSummary";

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
			db.execSQL(locationsTableCreate);
			db.execSQL(taggedLocationsTableCreate);
			db.execSQL(dashboardSummaryTable);

			/* Also seed data for default values */
			seedData(db);

			/*Toast.makeText(context, "4 table created", Toast.LENGTH_LONG).show();*/

		}

		public void seedData(SQLiteDatabase db){
			//@ToDo add Seed data here


			// Create User
			createUserRow(db,1, "Frank Hsueh", "abc@gmail.com", "I am super man", "");
			// Seed the activity table first for first activity id
			//@TODO removed this seed data to do new database changes 
			/*createActivityRow(db,1, "Driving", "I was driving from home to work", "Driving", "Santa Clara", "Palo Alto","9:00 AM", "9:30 AM");
			createActivityRow(db, 2, "Working", "Had a bad day at work", "Working", "University Ave, Palo Alto", "University Ave, Palo Alto","9:30 AM", "11:30 AM");
			createActivityRow(db, 3, "Dining", "Having a sip of coffee at my favorite place", "Dining", "Starbucks, Palo Alto", "Starbucks, Palo Alto","12:00 PM", "1:00 PM");
			createActivityRow(db, 4, "Walking", "Beautiful landscape here", "Walking", "University Ave, Palo Alto", "University Ave, Palo Alto","1:00 PM", "1:30 PM");
			createActivityRow(db, 5, "Meeting", "Made some important decisions", "Meeting","Moffett Field, Mountain View", "Moffett Field, Mountain View","9:30 PM", "11:30 PM");
			 */
			/* Not a good idea to seed activities with random image files
			 * @TODO find good image files, place them in a folder, and then 
			 * seed db with them associating with a particular activity
			 */			
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
			return db.insert(USERS_TABLE_NAME, null, initialValues);

		}

		private long createActivityRow(SQLiteDatabase db, int activityID, String activityName,
				String description, String activityType, String startLocation, String endLocation,
				String startTime, String endTime) {

			ContentValues initialValues = new ContentValues();
			initialValues.put("activityID", activityID);
			initialValues.put("activityName", activityName);
			initialValues.put("description", description);
			initialValues.put("activityType", activityType);
			initialValues.put("startLocation", startLocation);
			initialValues.put("endLocation", endLocation);
			initialValues.put("startTime", startTime);
			initialValues.put("endTime", endTime);

			System.out.println("HIMZ: creating values");
			return db.insert(ACTIVITY_TABLE_NAME, null, initialValues);

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
			e.printStackTrace();
		}


		return labels ;
	}

	
	public void doDashboardSummaryAnalysis(){
		// Get All the rows from activityTable
		// Create hashmap for each activityType and update the total time taken there.
		HashMap<String, Long> map = new HashMap();
		Cursor c = null;
		c = mDb.rawQuery("select  activityType, startTime, endTime from ActivityTable" , null);
		String activityType = "";String strStartTime = ""; String strEndTime = "";
		SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
		Date startDate, endDate;
		long diffTime;
		try{
			if (c.moveToFirst()) {
				do {
					startDate = format.parse(c.getString(1));
					endDate = format.parse(c.getString(2));
					diffTime = endDate.getTime() -startDate.getTime();
					//Change minimum precision to seconds from microseconds
					diffTime = diffTime/1000;
					activityType = c.getString(0);
					if(map.containsKey(activityType)){
						// Edit the key
						long prevTime = (Long) map.get(activityType);
						map.put(activityType,  prevTime + diffTime);
					} else {
						// Add the time difference to the map
						map.put(activityType, diffTime);
					}
				} while (c.moveToNext());
			}
			// closing connection
			c.close();
		}
		catch(Exception e){
			e.printStackTrace();
		}
		/* Create the percentages and store them */

		// Find the total time taken in seconds
		Long totalTime = 0L;
		for (Map.Entry entry : map.entrySet()) {
			totalTime = totalTime + (Long) entry.getValue();
		}
		
		
		// Clear the existing DashboardSummaryTable.
		deleteAllRowsFromTable(DASHBOARD_SUMMARY_TABLE_NAME);
		
		//Create percentage and store in the db
		HashMap<String, Long> hm = new HashMap();
		Long tempValue;
		Double percentage = 0.0;
		for (Map.Entry entry : map.entrySet()) {
			tempValue = (Long)entry.getValue();
			percentage = (double) (( (double) tempValue/ (double)totalTime) * 100);
			// Round off the value of the percentage
			percentage = Double.valueOf(new DecimalFormat("#.##").format(percentage)); 
			createDashboardSummaryRow((String) entry.getKey(), percentage);
		}
	}


	/**
	 * Remove all users and groups from database.
	 */
	public void deleteAllRowsFromTable(String tableName)
	{
		// db.delete(String tableName, String whereClause, String[] whereArgs);
		// If whereClause is null, it will delete all rows.
		/*SQLiteDatabase mDb = this.getWritableDatabase(); // helper is object extends SQLiteOpenHelper */
		mDb.delete(tableName, null, null);
	}

	public long createDashboardSummaryRow(String activityType, Double percentage ) {
		ContentValues initialValues = new ContentValues();
		initialValues.put("activityType", activityType);
		initialValues.put("percentage", percentage);
		return mDb.insert(DASHBOARD_SUMMARY_TABLE_NAME, null, initialValues);
	}

	/**
	 * Get dashboard summary data
	 * 
	 * @return
	 */
	public ArrayList<ActivityItem> getAllDashboardSummary(){
		ArrayList<ActivityItem> data1 = new ArrayList<ActivityItem>();
		Cursor c = null;
		c = mDb.rawQuery("select  * from "+ DASHBOARD_SUMMARY_TABLE_NAME , null);
		try{
			if (c.moveToFirst()) {
				do {
					ActivityItem t1 = new ActivityItem(c.getString(0), c.getDouble(1));
					data1.add(t1);
				} while (c.moveToNext());
			}
			// closing connection
			c.close();
		}
		catch(Exception e){
			e.printStackTrace();
		}
		return data1;
	}


	/**
	 * Function to update the description of the activity
	 * @param description
	 * @param activityID
	 * @return true, if updated and false if not updated
	 */
	public boolean updateDescriptionOfActivity(String description, int activityID){


		String strSQL = "UPDATE ActivityTable SET description = " + "\"" + description +"\"" + "WHERE activityID = " + "\"" + activityID +"\"";


		if(strSQL != null){
			try {
				mDb.execSQL(strSQL);
				return true;
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return false;
			}
		}
		return false; 

	}
	/**
	 * Function to update the activity data. 
	 * @param activityID - activityID for the activity
	 * @param activityName - new activity name
	 * @param startLocation -  new start location
	 * @param endLocation - new end location
	 * @return true, if updated, else false
	 */
	public boolean updateActivity(int activityID, String activityName,
			String startLocation, String endLocation) {

		String strSQL = "UPDATE ActivityTable SET activityName = " + "\""
				+ activityName + "\"" + ", startLocation =" + "\""
				+ startLocation + "\"" + ", endLocation =" + "\"" + endLocation
				+ "\"" + "WHERE activityID = " + "\"" + activityID + "\"";

		if (strSQL != null) {
			try {
				mDb.execSQL(strSQL);
				return true;
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return false;
			}
		}
		return false;

	}



	/**
	 * Function to update the activity data. 
	 * Adding this overloaded function for updating activityType also, as we 
	 * are supporting it as of now
	 * @param activityID - activityID for the activity
	 * @param activityName - new activity name
	 * @param startLocation -  new start location
	 * @param endLocation - new end location
	 * @return true, if updated, else false
	 */
	public boolean updateActivity(int activityID, String activityName, 
			String activityType, String startLocation, String endLocation) {

		String strSQL = "UPDATE ActivityTable SET activityName = " + "\""
				+ activityName  + "\"" + ", activityType =" + "\""
				+ activityType + "\"" + ", startLocation =" + "\""
				+ startLocation + "\"" + ", endLocation =" + "\"" + endLocation
				+ "\"" + "WHERE activityID = " + "\"" + activityID + "\"";

		if (strSQL != null) {
			try {
				mDb.execSQL(strSQL);
				return true;
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return false;
			}
		}
		return false;

	}

	/**
	 * API to fetch all the images from the local DB for the given activity ID
	 * @param position
	 * @param activityID
	 * @return 
	 */
	public String getImageAtPositionForActivity(Integer position, Integer activityID){
		Cursor c = null;
		String imageLocation = null;		
		c = mDb.rawQuery("select imageName,location from Image where activityID = " + "\"" + activityID +"\"", null);
		try{			
			if(c.moveToPosition(position)){
				imageLocation = c.getString(1) + "/" + c.getString(0);
			}
			// closing connection
			c.close();
		}
		catch(Exception e){
			e.printStackTrace();
		}


		return imageLocation ;
	}

	/**
	 * Function to return the last activityID from the table
	 * @return 
	 */
	public int fetchRowCountActivityTable() {
		// TODO Auto-generated method stub
		Cursor c = null;
		c = mDb.rawQuery("select count(*) from ActivityTable", null);
		c.moveToFirst();
		int rowCount = c.getInt(0);
		return rowCount;
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
			e.printStackTrace();
		}


		return nameAndDescription ;
	}


	/**
	 * Function to return all timeline activities, sorted by date
	 * @return Arraylist<TimelineItem>
	 */
	public ArrayList<TimelineItem> getAllTimelineActivity(){
		ArrayList<TimelineItem> data1 = new ArrayList<TimelineItem>();

		Cursor c = null;
		c = mDb.rawQuery("select  * from ActivityTable order by endTime desc" , null);

		try{


			if (c.moveToFirst()) {
				do {

					TimelineItem t1 = new TimelineItem((int)Integer.parseInt(c.getString(0)),c.getString(1),c.getString(2),c.getString(3),
							c.getString(6),c.getString(7),c.getString(4),c.getString(5));
					data1.add(t1);
				} while (c.moveToNext());
			}

			// closing connection
			c.close();
		}
		catch(Exception e){
			e.printStackTrace();
		}


		return data1;
	}




	public TimelineItem getTimelineActivityItem(int activityID){
		TimelineItem t1 = null;

		Cursor c = null;
		c = mDb.rawQuery("select  * from ActivityTable where activityID = " + "\"" + activityID +"\"" , null);

		try{


			if (c.moveToFirst()) {
				do {

					t1 = new TimelineItem((int)Integer.parseInt(c.getString(0)),c.getString(1),c.getString(2),c.getString(3),
							c.getString(6),c.getString(7),c.getString(4),c.getString(5));

				} while (c.moveToNext());
			}

			// closing connection
			c.close();
		}
		catch(Exception e){
			e.printStackTrace();
		}


		return t1;
	}

	public Activity getActivity(int activityID){
		Activity t1 = null;

		Cursor c = null;
		c = mDb.rawQuery("select  * from ActivityTable where activityID = " + "\"" + activityID +"\"" , null);

		try{


			if (c.moveToFirst()) {
				do {

					t1 = new Activity((int)Integer.parseInt(c.getString(0)),c.getString(1),c.getString(2),c.getString(3),
							c.getString(4),c.getString(5),c.getString(6),c.getString(7),c.getString(8),c.getString(9),c.getString(10),c.getString(11));

				} while (c.moveToNext());
			}

			// closing connection
			c.close();
		}
		catch(Exception e){
			e.printStackTrace();
		}


		return t1;
	}




	public ArrayList<LatLng> getAllLocationsForActivityID(int activityID){
		ArrayList<LatLng> data1 = new ArrayList<LatLng>();

		Cursor c = null;
		c = mDb.rawQuery("select  * from " + LOCATIONS_TABLE_NAME + " where activityID = " + "\"" + activityID +"\""  , null);

		try{


			if (c.moveToFirst()) {
				do {
					int ativityIDTemp = c.getInt(0);
					String timeStampTemp = c.getString(3);
					LatLng cc = new LatLng(c.getDouble(1), c.getDouble(2));
					data1.add(cc);
				} while (c.moveToNext());
			}

			// closing connection
			c.close();
		}
		catch(Exception e){
			e.printStackTrace();
		}


		return data1;
	}

	public ArrayList<Place> getAllTaggedLocationsForActivityID(int activityID){
		ArrayList<Place> data1 = new ArrayList<Place>();

		Cursor c = null;
		c = mDb.rawQuery("select  * from " + TAGGED_LOCATIONS_TABLE_NAME + " where activityID = " + "\"" + activityID +"\""  , null);

		try{


			if (c.moveToFirst()) {
				LatLng newPoint;
				Place place = new Place();
				do {
					String title = c.getString(3);
					newPoint = new LatLng( c.getDouble(1), c.getDouble(2));
					place.setPoint(newPoint);
					place.setName(title);
					data1.add(place);
				} while (c.moveToNext());
			}

			// closing connection
			c.close();
		}
		catch(Exception e){
			e.printStackTrace();
		}


		return data1;
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
			e.printStackTrace();
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
			e.printStackTrace();
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
			e.printStackTrace();
		}
		return about ;
	}
	/**
	 * Overloaded function - should never be used, apart from testing purposes
	 * @return
	 */
	private boolean backupDB(){
		String appDirectory = "/data/data/edu.cmu.sv.mobisens_ui/files/";
		return backupDB(appDirectory);
	}
	
	/**
	 * Backup all the database in the directory name provided here. 
	 * @param directoryName
	 * @return
	 */
	public boolean backupDB(String directoryName){
		boolean result = false;
		// Loop through all the tables and send it to the csv creating function
		 ArrayList<String[]> values = new ArrayList<String[]>();
		Cursor c = null;
		//1 ActivityTable
		try{
			String tableName = ACTIVITY_TABLE_NAME;
			c = mDb.rawQuery("select  * from " + tableName , null);
			if (c.moveToFirst()) {
				do {
					ArrayList<String> temp =new ArrayList<String>();
					temp.add(c.getString(0));temp.add(c.getString(1));temp.add(c.getString(2));temp.add(c.getString(3));temp.add(c.getString(4));temp.add(c.getString(5));temp.add(c.getString(6));temp.add(c.getString(7));temp.add(c.getString(8));temp.add(c.getString(9));temp.add(c.getString(10));temp.add(c.getString(11));
					String[] stringArray = temp.toArray(new String[temp.size()]);
					values.add(stringArray);
				} while (c.moveToNext());
			}
			createCSV(values, directoryName, tableName);
			
			//2 DashboardSummary
			values = new ArrayList<String[]>();
			tableName = DASHBOARD_SUMMARY_TABLE_NAME;
			c = mDb.rawQuery("select  * from " + tableName , null);
			if (c.moveToFirst()) {
				do {
					ArrayList<String> temp =new ArrayList<String>();
					temp.add(c.getString(0)); temp.add(c.getString(1));
					String[] stringArray = temp.toArray(new String[temp.size()]);
					values.add(stringArray);
				} while (c.moveToNext());
			}
			createCSV(values, directoryName, tableName);
			
			//3 Image
			values = new ArrayList<String[]>();
			tableName = IMAGE_TABLE_NAME;
			c = mDb.rawQuery("select  * from " + tableName , null);
			if (c.moveToFirst()) {
				do {
					ArrayList<String> temp =new ArrayList<String>();
					temp.add(c.getString(0));temp.add(c.getString(1));temp.add(c.getString(2));
					String[] stringArray = temp.toArray(new String[temp.size()]);
					values.add(stringArray);
				} while (c.moveToNext());
			}
			createCSV(values, directoryName, tableName);
			
			//4 TaggedLocations
			values = new ArrayList<String[]>();
			tableName = TAGGED_LOCATIONS_TABLE_NAME;
			c = mDb.rawQuery("select  * from " + tableName , null);
			if (c.moveToFirst()) {
				do {
					ArrayList<String> temp =new ArrayList<String>();
					temp.add(c.getString(0));temp.add(c.getString(1));temp.add(c.getString(2));temp.add(c.getString(3));
					String[] stringArray = temp.toArray(new String[temp.size()]);
					values.add(stringArray);
				} while (c.moveToNext());
			}
			createCSV(values, directoryName, tableName);
			
			//5 Users
			values = new ArrayList<String[]>();
			tableName = USERS_TABLE_NAME;
			c = mDb.rawQuery("select  * from " + tableName , null);
			if (c.moveToFirst()) {
				do {
					ArrayList<String> temp =new ArrayList<String>();
					temp.add(c.getString(0));temp.add(c.getString(1));temp.add(c.getString(2));temp.add(c.getString(3));temp.add(c.getString(4));
					String[] stringArray = temp.toArray(new String[temp.size()]);
					values.add(stringArray);
				} while (c.moveToNext());
			}
			createCSV(values, directoryName, tableName);
			
			//6 locations
			values = new ArrayList<String[]>();
			tableName = LOCATIONS_TABLE_NAME;
			c = mDb.rawQuery("select  * from " + tableName , null);
			if (c.moveToFirst()) {
				do {
					ArrayList<String> temp =new ArrayList<String>();
					temp.add(c.getString(0));temp.add(c.getString(1));temp.add(c.getString(2));temp.add(c.getString(3));
					String[] stringArray = temp.toArray(new String[temp.size()]);
					values.add(stringArray);
				} while (c.moveToNext());
			}
			createCSV(values, directoryName, tableName);
			
			System.out.println("LocalDbAdapter: All the tables backedup");
			result = true;
			// closing connection
			c.close();
		}
		catch(Exception e){
			result = false;
			System.out.println("LocalDbAdapter: There was an error backing up the tables");
		}
		return result;
	}
	
	public void createCSV(ArrayList<String[]> values, String directoryName ,String tableName){
		CSVWriter writer = null;
		// Iterate through the cursor
		try 
		{	
			writer = new CSVWriter(new FileWriter(directoryName +  tableName + ".csv"), ',');
			for(String[] entries : values) {
				writer.writeNext(entries); 
			}
			writer.close();
		} 
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}


	public String getActivityID(String activityType,String startLocation, String endLocation, String startTime, 
			String endTime ){
		Cursor c = null;
		String activityIDStr= new String();			
		c = mDb.rawQuery("select activityID from ActivityTable where activityType = " + "\"" + activityType +"\""
				+ "and startLocation="  + "\"" + startLocation +"\"" 
				+ "and endLocation="  + "\"" + endLocation +"\""
				+ "and startTime="  + "\"" + startTime +"\""
				+ "and endTime="  + "\"" + endTime +"\"", null);

		try{
			if (c.moveToFirst()) {
				do {

					activityIDStr = c.getString(0);

				} while (c.moveToNext());
			}

			// closing connection
			c.close();
		}
		catch(Exception e){
			e.printStackTrace();
		}
		return activityIDStr ;
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


	public long storeTaggedLocation(int activityID, String latitude, String longitude, String title)
	{
		ContentValues initialValues = new ContentValues();

		initialValues.put("activityID", activityID);
		initialValues.put("latitude", latitude);
		initialValues.put("longitude", longitude);
		initialValues.put("title", title);
		System.out.println("HIMZ: creating tagged locations rows");
		return mDb.insert(TAGGED_LOCATIONS_TABLE_NAME, null, initialValues);
	}


	public long storeLocation(int activityID, String latitude, String longitude)
	{
		ContentValues initialValues = new ContentValues();

		initialValues.put("activityID", activityID);
		initialValues.put("latitude", latitude);
		initialValues.put("longitude", longitude);
		initialValues.put("timestamp", "0");
		System.out.println("HIMZ: creating locations rows");
		return mDb.insert(LOCATIONS_TABLE_NAME, null, initialValues);
	}


	public long storeLocation(int activityID, String latitude, String longitude, String timestamp)
	{
		ContentValues initialValues = new ContentValues();

		initialValues.put("activityID", activityID);
		initialValues.put("latitude", latitude);
		initialValues.put("longitude", longitude);
		initialValues.put("timestamp", "0");
		System.out.println("HIMZ: creating locations rows");
		return mDb.insert(LOCATIONS_TABLE_NAME, null, initialValues);
	}

	public void storeLocation(int activityID,ArrayList<Coordinates> coordinates)
	{
		ContentValues initialValues;

		for (Coordinates coordinates2 : coordinates) {
			if(coordinates2.timestamp == 0){
				storeLocation(activityID,Double.toString(coordinates2.latitude),
						Double.toString(coordinates2.longitude));
			} else {
				storeLocation(activityID,Double.toString(coordinates2.latitude),
						Double.toString(coordinates2.longitude),Long.toString(coordinates2.timestamp));
			}
		}

		return ;
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
			String description, String activityType, String startLocation, String endLocation,
			String startTime, String endTime, String startLocationLat, 
			String startLocationLng, String endLocationLat, 
			String endLocationLng) {

		ContentValues initialValues = new ContentValues();
		initialValues.put("activityID", activityID);
		initialValues.put("activityName", activityName);
		initialValues.put("description", description);
		initialValues.put("activityType", activityType);
		initialValues.put("startLocation", startLocation);
		initialValues.put("endLocation", endLocation);
		initialValues.put("startTime", startTime);
		initialValues.put("endTime", endTime);
		initialValues.put("startLocationLat", startLocationLat);
		initialValues.put("startLocationLng", startLocationLng);
		initialValues.put("endLocationLat", endLocationLat);
		initialValues.put("endLocationLng", endLocationLng);

		System.out.println("HIMZ: creating values");
		return mDb.insert(ACTIVITY_TABLE_NAME, null, initialValues);

	}
	/**
	 * Wrapper funtion to create an activity row, just from the TimelineItem
	 * @param activity
	 */
	/*public void createActivityRow(TimelineItem activity) {
		// TODO Auto-generated method stub
		createActivityRow(activity.getmActivity_id(), activity.getmActivity_name(),
				activity.getmDescription(),  activity.getmActivityType(), 
				activity.getmStart_location(), activity.getmEnd_location(),
				activity.getmStart_time(), activity.getmEnd_time());
	}
	 */
	/**
	 * Wrapper function to create an activity row, just from the Activity
	 * @param activity
	 */
	public void createActivityRow(Activity activity) {
		/* 
		 * Take care of converting all non-string values to string for now. 
		 * Have to change the activity table later to match activity.java exactly
		 * */

		DateFormat df = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
		String startTime = df.format(activity.getmStart_time());
		String endTime = df.format(activity.getmEnd_time());

		String startLatitude = Double.toString(activity.getStartCoordinates().latitude);
		String startLongitude = Double.toString(activity.getStartCoordinates().longitude);
		String endLatitude = Double.toString(activity.getEndCoordinates().latitude);
		String endLongitude = Double.toString(activity.getEndCoordinates().longitude);


		createActivityRow(activity.getmActivity_id(), activity.getmActivity_name(),
				activity.getmDescription(),  activity.getmActivityType(), 
				activity.getmStart_location(), activity.getmEnd_location(),
				startTime, endTime, startLatitude, startLongitude, endLatitude, endLongitude);
	}


	
	
	private static ArrayList<String> convertStringToArray(String str)
	{
		List<String> items = new ArrayList<String>(Arrays.asList(str.split("\\s*,\\s*")));
		System.out.println("arraylist=" + items);
		return (ArrayList<String>) items;
	}

	
	
	public int isUserExist(String userEmail) {
		// TODO Auto-generated method stub

		Cursor c = null;
		int count = 0;		
		c = mDb.rawQuery("select userID from Users where email = " + "\"" + userEmail +"\"", null);

		try{
			if (c.moveToFirst()) {
				do {

					count++;

				} while (c.moveToNext());
			}

			// closing connection
			c.close();
		}
		catch(Exception e){
			e.printStackTrace();
		}
		return count ;	
	}




}