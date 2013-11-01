package edu.cmu.sv.mobisens.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;

public class OS {
	public static boolean isLowerthanAPILevel8(){
		return Build.VERSION.SDK_INT <= 7;
	}
	
	public static boolean isLowerthanAPILevel9(){
		return Build.VERSION.SDK_INT <= 8;
	}
	
	
	
	public static String getRealPathFromURI(Activity activity, Uri contentUri) {
        String[] proj = { MediaStore.Images.Media.DATA };
        Cursor cursor = activity.managedQuery(contentUri, proj, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        String path = cursor.getString(column_index);
        cursor.close();
        return path;
    }
}
