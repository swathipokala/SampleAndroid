package com.rrdonnelly.up2me.dao;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.rrdonnelly.up2me.json.Offer;
import com.rrdonnelly.up2me.json.Statement;
import com.rrdonnelly.up2me.json.UserLibrary;
import com.rrdonnelly.up2me.util.ImageUtil;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class UserLibraryDAO {
	private static final String TABLE_USER_LIBRARY = "UserLibrary";
	
	public static void refresh(Context activity) {
		deleteUserLibrary(activity);
	}
	
	public static void deleteUserLibrary(Context activity) {
		DatabaseHandler databaseHandler = DatabaseHandler
				.getDatabaseHandlerInstance(activity);

		SQLiteDatabase db = databaseHandler.getWritableDatabase();
		db.delete(TABLE_USER_LIBRARY, null, null);
		// db.close();
	}
	
	
	public static void addUserLibrary(UserLibrary userLibrary, long loginUserId, Context activity) {

		
		Long userId = loginUserId;
		Long libraryId = userLibrary.getLibraryId();
		String file = userLibrary.getFile();
		String cover = userLibrary.getCover();
		String type = userLibrary.getType();
		String createdDate = userLibrary.getCreatedDate();
		String modifiedDate = userLibrary.getModifiedDate();
		Boolean isActive = userLibrary.getIsActive();
		String url = userLibrary.getUrl();
		boolean useUrl = userLibrary.getUseUrl();
		boolean isRead = userLibrary.getIsRead();
		
		DatabaseHandler databaseHandler = DatabaseHandler
				.getDatabaseHandlerInstance(activity);

		SQLiteDatabase db = databaseHandler.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put("userId", loginUserId);
		values.put("libraryId", libraryId);

		values.put("file", file);
		values.put("cover", cover);

		values.put("type", type);
		values.put("createdDate", createdDate);

		values.put("modifiedDate", modifiedDate);
		values.put("isActive", isActive);
		
		values.put("url", url);
		values.put("useUrl", useUrl);
		values.put("isRead", isRead);
		
		// Inserting Row
		long rowId = db.insert(TABLE_USER_LIBRARY, null, values);

		// db.close(); // Closing database connection
	}
	
	public List<UserLibrary> getAllUserLibraryList(Activity activity, long userId, String type) {
		ArrayList<UserLibrary> userLibraryList = new ArrayList<UserLibrary>();

		DatabaseHandler databaseHandler = DatabaseHandler.getDatabaseHandlerInstance(activity);

		SQLiteDatabase db = databaseHandler.getReadableDatabase();

		String query = "SELECT * FROM UserLibrary where userId = " + userId + " and isActive = 1 ";
		
		if(type != "") {
			query = query + " and type = '" + type + "'";
		}
		
		Cursor cursor = db.rawQuery(query, null);
		
		if (cursor.moveToFirst()) {
			do {
				UserLibrary userLibrary = new UserLibrary();
				
				userLibrary.setLibraryId(cursor.getLong(1));
				userLibrary.setFile(cursor.getString(2));
				String displayImagePathSmall = cursor.getString(3);
				
				String displayImagePathSmallfileName = displayImagePathSmall.substring(displayImagePathSmall.lastIndexOf("/") + 1);
				File fdisplayImagePathSmall = new File(activity.getBaseContext().getFilesDir(), displayImagePathSmallfileName);
				if (!fdisplayImagePathSmall.exists()) {
					ImageUtil.downloadImage(activity, displayImagePathSmall, displayImagePathSmallfileName);
				} 
				userLibrary.setCover(displayImagePathSmall);
				
				userLibrary.setCreatedDate(cursor.getString(5));
				userLibrary.setModifiedDate(cursor.getString(6));
				
				userLibrary.setUrl(cursor.getString(8));
				userLibrary.setUseUrl(cursor.getString(9).equals("1"));
				
				userLibrary.setIsRead(cursor.getString(10).equals("1"));
				
				userLibraryList.add(userLibrary);
				
			} while (cursor.moveToNext());

		}
		cursor.close();
		
		return userLibraryList;
	}
	
	public int updateRead(long id, int read, Activity activity, int isDirty) {
		DatabaseHandler databaseHandler = DatabaseHandler
				.getDatabaseHandlerInstance(activity);
		SQLiteDatabase db = databaseHandler.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put("isRead", read);
		values.put("isDirty", isDirty);

		// updating row
		return db.update(TABLE_USER_LIBRARY, values, "libraryId = ?",
				new String[] { String.valueOf(id) });
	}
	
	public int getUnReadLibraryCount(Activity activity, long userId) {
		int count = 0;
		DatabaseHandler databaseHandler = DatabaseHandler.getDatabaseHandlerInstance(activity);

		SQLiteDatabase db = databaseHandler.getReadableDatabase();

		String query = "SELECT * FROM UserLibrary where userId = " + userId + " and isActive = 1  and isRead = 0";
		
		Cursor cursor = db.rawQuery(query, null);
		
		if (cursor.getCount() > 0) {
			count = cursor.getCount();
		}
		cursor.close();
		
		return count;
	}
}
