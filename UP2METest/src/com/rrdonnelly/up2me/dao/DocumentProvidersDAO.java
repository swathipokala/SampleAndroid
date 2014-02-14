package com.rrdonnelly.up2me.dao;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HTTP;
import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.os.StrictMode;

import com.rrdonnelly.up2me.R;
import com.rrdonnelly.up2me.json.DocumentProviders;
import com.rrdonnelly.up2me.json.OfferProviders;
import com.rrdonnelly.up2me.services.DocumentProvidersService;
import com.rrdonnelly.up2me.util.ImageUtil;
import com.rrdonnelly.up2me.valueobjects.UserFavorite;

public class DocumentProvidersDAO {

	private static final String TABLE_DOCUMENT_PROVIDERS = "DocumentProviders";
	private static final String TABLE_USER_DOCUMENT_PROVIDERS = "UserDocumentProviders";
	private static final String TABLE_USER_DOCUMENT_PROVIDERS_FAVORITE = "UserDocumentProvidersFavorite";
	private static final String USER_ID = "userId";

	public static void deleteDocumentProviders(Context activity) {
		DatabaseHandler databaseHandler = DatabaseHandler
				.getDatabaseHandlerInstance(activity);

		SQLiteDatabase db = databaseHandler.getWritableDatabase();
		db.delete(TABLE_DOCUMENT_PROVIDERS, null, null);
	}

	public static void addDocumentProviders(
			DocumentProviders documentProviders, Context activity) {

		Long providerId = documentProviders.getProviderId();
		String providerName = documentProviders.getProviderName();
		boolean isActive = documentProviders.isActive();
		String imageUrlSmallStr = documentProviders.getImageUrlSmallStr();

		DatabaseHandler databaseHandler = DatabaseHandler
				.getDatabaseHandlerInstance(activity);
		SQLiteDatabase db = databaseHandler.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put("providerId", providerId);
		values.put("providerName", providerName);
		values.put("isActive", isActive);
		values.put("imageUrlSmallStr", imageUrlSmallStr);

		db.insert(TABLE_DOCUMENT_PROVIDERS, null, values);
	}

	public List<DocumentProviders> getAllDocumentProviders(Activity activity) {

		List<DocumentProviders> documentProvidersList = new ArrayList<DocumentProviders>();

		DatabaseHandler databaseHandler = DatabaseHandler
				.getDatabaseHandlerInstance(activity);

		SQLiteDatabase db = databaseHandler.getReadableDatabase();

		String query = "select providerId, providerName, isActive, imageUrlSmallStr from DocumentProviders where isActive = 1 order by providerName";
		Cursor cursor = db.rawQuery(query, null);
		String webServiceUrl = activity.getResources().getString(
				R.string.webservice_url);
		DocumentProviders documentProviders = null;
		if (cursor.moveToFirst()) {
			do {
				documentProviders = new DocumentProviders();

				documentProviders.setProviderId(Long.parseLong(cursor
						.getString(0)));
				documentProviders.setProviderName(cursor.getString(1));

				/*
				 * String displayImagePathSmall = webServiceUrl + "/Images/" +
				 * cursor.getString(3); String displayImagePathSmallfileName =
				 * displayImagePathSmall
				 * .substring(displayImagePathSmall.lastIndexOf("imageName=") +
				 * 10); File fdisplayImagePathSmall = new
				 * File(activity.getBaseContext().getFilesDir(),
				 * displayImagePathSmallfileName); if
				 * (!fdisplayImagePathSmall.exists()) {
				 * ImageUtil.downloadImage(activity, displayImagePathSmall,
				 * displayImagePathSmallfileName); }
				 * documentProviders.setImageUrlSmallStr
				 * (displayImagePathSmallfileName);
				 */

				String displayImagePathSmall = cursor.getString(3);
				String displayImagePathSmallfileName = displayImagePathSmall
						.substring(displayImagePathSmall
								.lastIndexOf("/") + 1);
				File fdisplayImagePathSmall = new File(activity
						.getBaseContext().getFilesDir(),
						displayImagePathSmallfileName);
				if (!fdisplayImagePathSmall.exists()) {
					ImageUtil.downloadImage(activity, displayImagePathSmall,
							displayImagePathSmallfileName);
				}
				documentProviders.setImageUrlSmallStr(displayImagePathSmall);

				documentProvidersList.add(documentProviders);
			} while (cursor.moveToNext());
		}
		cursor.close();

		return documentProvidersList;
	}

	public static List<Integer> getAllUserDocumentProviders(Context activity,
			long loginUserId) {

		List<Integer> userDocumentProvidersList = new ArrayList<Integer>();
		DatabaseHandler databaseHandler = DatabaseHandler
				.getDatabaseHandlerInstance(activity);

		SQLiteDatabase db = databaseHandler.getReadableDatabase();

		String query = "select providerId, userId from UserDocumentProviders where UserId = "
				+ loginUserId;
		Cursor cursor = db.rawQuery(query, null);
		if (cursor.moveToFirst()) {
			do {
				userDocumentProvidersList.add(cursor.getInt(0));
			} while (cursor.moveToNext());
		}
		cursor.close();

		return userDocumentProvidersList;
	}

	public static void deleteUserDocumentProviders(Context activity,
			long userId) {
		DatabaseHandler databaseHandler = DatabaseHandler
				.getDatabaseHandlerInstance(activity);

		SQLiteDatabase db = databaseHandler.getWritableDatabase();
		db.delete(TABLE_USER_DOCUMENT_PROVIDERS, USER_ID + " = ?",
				new String[] { String.valueOf(userId) });
	}

	public String InsertUserDocumentProviders(Activity activity,
			List<Integer> documentProviders, long userId) {
		
		String returnMsg = "Successfully updated Document providers.";
		
		ConnectivityManager cm = (ConnectivityManager) activity.getSystemService(Activity.CONNECTIVITY_SERVICE);
		if(cm != null && cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected()){
			returnMsg = DocumentProvidersService.updateUserDocumentProvidersListToBackEnd(activity, documentProviders, userId);
		} 
		
		updateUserDocumentProviders(activity, documentProviders, userId);

		return returnMsg;
	}

	
	public Boolean isUserProviderExists(String id, String providerId,
			Context activity) {
		DatabaseHandler databaseHandler = DatabaseHandler
				.getDatabaseHandlerInstance(activity);
		SQLiteDatabase db = databaseHandler.getReadableDatabase();
		String query = "SELECT * FROM "
				+ TABLE_USER_DOCUMENT_PROVIDERS_FAVORITE + " WHERE userId="
				+ id + " " + "and " + "providerId = " + providerId + "";
		Cursor mCursor = db.rawQuery(query, null);

		if (mCursor.getCount() > 0) {
			return true;
		}
		return false;
	}

	public void addUpdateUserProviders(boolean isUserProviderExists,
			DocumentProviders documentProviders, Activity activity) {
		DatabaseHandler databaseHandler = DatabaseHandler
				.getDatabaseHandlerInstance(activity);

		SQLiteDatabase db = databaseHandler.getWritableDatabase();

		ContentValues values = new ContentValues();

		if (isUserProviderExists) {
			values.put("isFavorite", documentProviders.isFavorite() ? 1 : 0);
			values.put("isDirty", documentProviders.isDirty() ? 1 : 0);

			// Updating Row
			db.update(
					TABLE_USER_DOCUMENT_PROVIDERS_FAVORITE,
					values,
					"providerId = ? and userId = ?",
					new String[] {
							String.valueOf(documentProviders.getProviderId()),
							String.valueOf(documentProviders.getUserId()) });
		} else {
			values.put("userId", documentProviders.getUserId());
			values.put("providerId", documentProviders.getProviderId());
			values.put("isFavorite", documentProviders.isFavorite());
			values.put("isDirty", documentProviders.isDirty() ? 1 : 0);
			// Inserting Row
			long rowId = db.insert(TABLE_USER_DOCUMENT_PROVIDERS_FAVORITE,
					null, values);
		}
	}

	public Boolean isFavourit(long userId, int providerId, Context activity) {
		DatabaseHandler databaseHandler = DatabaseHandler
				.getDatabaseHandlerInstance(activity);
		SQLiteDatabase db = databaseHandler.getReadableDatabase();
		String query = "SELECT isFavorite FROM "
				+ TABLE_USER_DOCUMENT_PROVIDERS_FAVORITE + " WHERE userId="
				+ userId + " " + "and " + "providerId = " + providerId + "";
		Cursor mCursor = db.rawQuery(query, null);

		boolean isFavourite = false;
		if (mCursor != null && mCursor.getCount() > 0) {
			mCursor.moveToFirst();
			isFavourite = Boolean.valueOf("1".equals(mCursor.getString(0)));
		}
		return isFavourite;
	}

	public int getProviderId(String providerName, Activity activity) {
		DatabaseHandler databaseHandler = DatabaseHandler
				.getDatabaseHandlerInstance(activity);
		SQLiteDatabase db = databaseHandler.getReadableDatabase();
		String query = "SELECT providerId FROM " + TABLE_DOCUMENT_PROVIDERS
				+ " WHERE providerName='" + providerName + "'";
		Cursor cursor = db.rawQuery(query, null);
		String strProviderId;
		int providerId = 0;
		if (cursor != null && cursor.getCount() > 0) {
			cursor.moveToFirst();
			strProviderId = cursor.getString(0);
			if (strProviderId != null) {
				providerId = Integer.parseInt(strProviderId);
			}
		}
		return providerId;
	}

	public static void updateUserDocumentProviders(Context activity,
			List<Integer> documentProviders, long userId) {
		// First Delete all the offerProviders for that user then insert
		// selected offer providers.
		deleteUserDocumentProviders(activity, userId);

		DatabaseHandler databaseHandler = DatabaseHandler
				.getDatabaseHandlerInstance(activity);

		SQLiteDatabase db = databaseHandler.getWritableDatabase();
		for (int i = 0; i < documentProviders.size(); i++) {
			ContentValues values = new ContentValues();
			values.put("userId", userId);
			values.put("providerId", documentProviders.get(i));

			db.insert(TABLE_USER_DOCUMENT_PROVIDERS, null, values);
		}
	}
	
	public void refreshUserDocumentFavourite(Context activity,long userId) {
		DatabaseHandler databaseHandler = DatabaseHandler
				.getDatabaseHandlerInstance(activity);
		SQLiteDatabase db = databaseHandler.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put("isFavorite", 0);

		db.update(TABLE_USER_DOCUMENT_PROVIDERS_FAVORITE, values, "userId = ?",
				new String[] { String.valueOf(userId) });
	}	
	
	public void addUserFavourite(UserFavorite userFavorite,Context currentActivity) {
		long providerId = userFavorite.getProviderId();
		long userId = userFavorite.getUserId();
		boolean isFavourite = true;
		
		boolean isUserProviderExists = isUserProviderExists(String.valueOf(userId), String.valueOf(providerId), currentActivity);
		
		DatabaseHandler databaseHandler = DatabaseHandler.getDatabaseHandlerInstance(currentActivity);
		
		SQLiteDatabase db = databaseHandler.getWritableDatabase();
		
		ContentValues values = new ContentValues();
		if (isUserProviderExists) {
			values.put("userId", userId);
			values.put("providerId",providerId);
			values.put("isFavorite",isFavourite);
			db.update(
					TABLE_USER_DOCUMENT_PROVIDERS_FAVORITE,
					values,
					"providerId = ? and userId = ?",
					new String[] {
							String.valueOf(providerId),
							String.valueOf(userId) });
		} else{
			values.put("userId", userId);
			values.put("providerId",providerId);
			values.put("isFavorite",isFavourite);
			db.insert(TABLE_USER_DOCUMENT_PROVIDERS_FAVORITE, null, values);
		}
	}
	
	public List<DocumentProviders> getAllDirtyFavorites(Context activity,long userId) {
		DatabaseHandler databaseHandler = DatabaseHandler
				.getDatabaseHandlerInstance(activity);
		SQLiteDatabase db = databaseHandler.getReadableDatabase();
		
		List<DocumentProviders> documentProvidersList = new ArrayList<DocumentProviders>();
		
		String userDocumentProvidersFavoriteCount = "Select * from UserDocumentProvidersFavorite where userId = "+ userId + " and isDirty=1";
		Cursor countCursor = db.rawQuery(userDocumentProvidersFavoriteCount, null);
		String query = "";
		if (countCursor.getCount() > 0) {
			query = "select providerId,isFavorite from UserDocumentProvidersFavorite where userId="+ userId + " and isDirty=1";
		
			Cursor cursor = db.rawQuery(query, null);
			DocumentProviders documentProviders = null;
			if (cursor != null && cursor.moveToFirst()) {
				documentProviders = new DocumentProviders();
				do {
					
					documentProviders.setProviderId(Long.parseLong(cursor.getString(0)));
					documentProviders.setFavorite(cursor.getString(1).equals("1"));
					documentProvidersList.add(documentProviders);
					
				} while (cursor.moveToNext());
			}
			cursor.close();
		}
		
		return documentProvidersList;
	}
	
	public static void resetDirtyFavoritesToFalse(String providerIds,
			Context activity, long userId) {
		if (providerIds != null && providerIds.length() > 0) {
			DatabaseHandler databaseHandler = DatabaseHandler.getDatabaseHandlerInstance(activity);

			SQLiteDatabase db = databaseHandler.getWritableDatabase();

			String updateDirtyFlagOffersFavoriteQuery = "update UserDocumentProvidersFavorite set isDirty = 0 where providerId in("+ providerIds + ") and userId=" + userId;
			db.execSQL(updateDirtyFlagOffersFavoriteQuery);
		}
	}	
	
	public static List<DocumentProviders> getUserDocumentProviders(Context context,String userID){
		DatabaseHandler databaseHandler = DatabaseHandler
				.getDatabaseHandlerInstance(context);
		SQLiteDatabase db = databaseHandler.getReadableDatabase();
		String sql="SELECT dp.providerid, dp.providername, dp.imageURLsmallstr FROM userDocumentproviders udp, documentproviders dp where dp.providerid=udp.providerid and udp.userid ="+userID +" order by dp.providername";
		List<DocumentProviders> documentProvidersList = new ArrayList<DocumentProviders>();
		Cursor cursor = db.rawQuery(sql, null);
		DocumentProviders documentProviders = null;
		if (cursor != null && cursor.moveToFirst()) {
		
			do {
				documentProviders = new DocumentProviders();
				documentProviders.setProviderId(Long.parseLong(cursor.getString(0)));
				documentProviders.setProviderName(cursor.getString(1));
				documentProviders.setImageUrlSmallStr(cursor.getString(2));
				documentProviders.setItemSelected(true);
				documentProvidersList.add(documentProviders);
				
			} while (cursor.moveToNext());
			cursor.close();
		}
		

		return documentProvidersList;
	}
	
	public static List<DocumentProviders> getUserDocumentProviders(Context context,String userID,String providerName){
		DatabaseHandler databaseHandler = DatabaseHandler
				.getDatabaseHandlerInstance(context);
		SQLiteDatabase db = databaseHandler.getReadableDatabase();
		String sql="SELECT dp.providerid, dp.providername, dp.imageURLsmallstr FROM userDocumentproviders udp, documentproviders dp where dp.providerid=udp.providerid and udp.userid ="+userID +" and dp.providername in ("+providerName+") order by dp.providername";
		List<DocumentProviders> documentProvidersList = new ArrayList<DocumentProviders>();
		Cursor cursor = db.rawQuery(sql, null);
		DocumentProviders documentProviders = null;
		if (cursor != null && cursor.moveToFirst()) {
		
			do {
				documentProviders = new DocumentProviders();
				documentProviders.setProviderId(Long.parseLong(cursor.getString(0)));
				documentProviders.setProviderName(cursor.getString(1));
				documentProviders.setImageUrlSmallStr(cursor.getString(2));
				documentProviders.setItemSelected(true);
				documentProvidersList.add(documentProviders);
				
			} while (cursor.moveToNext());
			cursor.close();
		}
		

		return documentProvidersList;
	}
}
