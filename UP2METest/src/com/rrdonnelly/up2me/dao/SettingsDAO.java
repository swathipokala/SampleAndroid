package com.rrdonnelly.up2me.dao;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.rrdonnelly.up2me.services.AdminService;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class SettingsDAO {
	 
    private static final String TABLE_SETTINGS= "Settings";
    public static SimpleDateFormat sdfDateTime = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
    
    public void addSyncDateTime(Activity activity,Long userId) {
    	
    	deleteSettings(activity,userId);
    	
    	long lastSyncDateTime = 0l;
    	lastSyncDateTime = AdminService.getServerTime(activity);//(new Date()).getTime();
    	
    	System.out.println("Server time : "+ lastSyncDateTime);
    	
    	DatabaseHandler databaseHandler = DatabaseHandler.getDatabaseHandlerInstance(activity);
    	SQLiteDatabase db = databaseHandler.getWritableDatabase();
 
        ContentValues values = new ContentValues(); 
        
        values.put("lastSyncDateTime", lastSyncDateTime);
        values.put("lastSyncDateTimeStr", sdfDateTime.format(lastSyncDateTime));
        values.put("userId",userId);
        // Inserting Row
        long rowId = db.insert(TABLE_SETTINGS, null, values);
        
        //db.close(); // Closing database connection
        
    }
    
    public long getLastSyncDateTime(Context activity, Long userId){
    	DatabaseHandler databaseHandler = DatabaseHandler.getDatabaseHandlerInstance(activity);
        SQLiteDatabase db = databaseHandler.getReadableDatabase();
        long lastSyncDateTime = 0l;
        
        String query = "SELECT  lastSyncDateTime FROM " + TABLE_SETTINGS+ " where userId="+userId;
        Cursor cursor = db.rawQuery(query, null);
        
        if (cursor != null){
            if(cursor.moveToFirst()){
            	lastSyncDateTime = cursor.getLong(0);
            }
        }
 
        return lastSyncDateTime;
    }
    
    public void deleteSettings(Activity activity,Long userId){
    	DatabaseHandler databaseHandler = DatabaseHandler.getDatabaseHandlerInstance(activity);

    	SQLiteDatabase db = databaseHandler.getWritableDatabase();
    	
    	String deleteQuery = "DELETE FROM  settings where userID="+userId;
    	
    	db.execSQL(deleteQuery);
    }
}

