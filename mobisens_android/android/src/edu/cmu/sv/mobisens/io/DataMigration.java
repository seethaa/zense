package edu.cmu.sv.mobisens.io;

import java.io.File;

import edu.cmu.sv.mobisens.settings.LocalSettings;
import android.content.ContextWrapper;

public class DataMigration {
	
	public static void migrate(final ContextWrapper context){
		Thread migrationThread = new Thread(){
			public void run(){
				migrateToV4(context);
				migrateToV5(context);
			}
		};
		
		migrationThread.start();
	}
	
	
	public static void migrateToV4(final ContextWrapper context){
		if(LocalSettings.isV4Migrated(context)){
			return;
		}
		
		MobiSensLog.log("Migrating to v4 storage..");
		
		removeV3GeoFiles();
		
		
		LocalSettings.setV4Migrated(context, true);
		MobiSensLog.log("V4 migration done..");
		
	}
	
	public static void migrateToV5(final ContextWrapper context){
		if(LocalSettings.isV5Migrated(context)){
			return;
		}
		
		MobiSensLog.log("Migrating to v5 storage..");
		
		removeV4ModelFiles();
		
		
		LocalSettings.setV5Migrated(context, true);
		MobiSensLog.log("V5 migration done..");
		
	}
	
	private static void removeV4ModelFiles(){
		String modelIndexFileName = Directory.MODEL_INDEX_V1_FILENAME;
		try {
			File indexFile = Directory.openFile(Directory.MOBISENS_ROOT, modelIndexFileName);
			String csvInput = FileOperation.readFileAsString(indexFile);
			if(csvInput.equals(""))
				return;
			String[] lines = csvInput.split("\r\n");
			if(lines.length == 0){
				return;
			}
			
			for(String line:lines){
				String[] columns = line.split(",");
				String dataFilePath = columns[3];
				
				try{
					File dataFile = new File(dataFilePath);
					dataFile.delete();
				}catch(Exception ex){
					ex.printStackTrace();
					MobiSensLog.log(ex);
				}
				
			}
			
			indexFile.delete();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			MobiSensLog.log(e);
		}
		
	}
	
	private static void removeV3GeoFiles(){
		File geoDataFolder = new File(Directory.GEO_DATA_FOLDER);
		if(!geoDataFolder.exists())
			return;
		String[] files = geoDataFolder.list();
		if(files == null)
			return;
		for(String file:files){
			try{
				if(file.substring(file.length() - ".geo".length() - 1).equals(".geo")){
					FileOperation.deleteFile(file);
				}
				
				if(file.substring(file.length() - ".geo.csv".length() - 1).equals(".geo.csv")){
					FileOperation.deleteFile(file);
				}
			}catch(Exception ex){
				MobiSensLog.log(ex);
			}
			
		}
	}
}
