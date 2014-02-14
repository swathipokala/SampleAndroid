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

import com.rrdonnelly.up2me.R;
import com.rrdonnelly.up2me.json.Offer;
import com.rrdonnelly.up2me.json.OfferProviders;
import com.rrdonnelly.up2me.services.OfferProvidersService;
import com.rrdonnelly.up2me.services.OfferService;
import com.rrdonnelly.up2me.util.ImageUtil;
import com.rrdonnelly.up2me.valueobjects.UserFavorite;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.os.StrictMode;

public class OfferProvidersDAO {

	private static final String TABLE_OFFER_PROVIDERS = "OfferProviders";
	private static final String TABLE_USER_OFFER_PROVIDERS = "UserOfferProviders";
	private static final String TABLE_USER_OFFER_PROVIDERS_FAVOURITE = "UserOfferProvidersFavorite";
	private static final String USER_ID = "userId";

	public static void deleteOfferProviders(Context activity) {
		DatabaseHandler databaseHandler = DatabaseHandler
				.getDatabaseHandlerInstance(activity);

		SQLiteDatabase db = databaseHandler.getWritableDatabase();
		db.delete(TABLE_OFFER_PROVIDERS, null, null);
	}

	public static void addOfferProviders(OfferProviders offerProviders,
			Context activity) {

		Long providerId = offerProviders.getProviderId();
		String providerName = offerProviders.getProviderName();
		boolean isActive = offerProviders.isActive();
		String imageUrlSmallStr = offerProviders.getImageUrlSmallStr();

		DatabaseHandler databaseHandler = DatabaseHandler
				.getDatabaseHandlerInstance(activity);
		SQLiteDatabase db = databaseHandler.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put("providerId", providerId);
		values.put("providerName", providerName);
		values.put("isActive", isActive);
		values.put("imageUrlSmallStr", imageUrlSmallStr);

		db.insert(TABLE_OFFER_PROVIDERS, null, values);
	}

	public List<OfferProviders> getAllOfferProviders(Activity activity) {

		List<OfferProviders> offerProvidersList = new ArrayList<OfferProviders>();

		DatabaseHandler databaseHandler = DatabaseHandler
				.getDatabaseHandlerInstance(activity);

		SQLiteDatabase db = databaseHandler.getReadableDatabase();

		String query = "select providerId, providerName, isActive, imageUrlSmallStr from OfferProviders where isActive = 1 order by providerName";
		Cursor cursor = db.rawQuery(query, null);
		String webServiceUrl = activity.getResources().getString(
				R.string.webservice_url);
		OfferProviders offerProviders = null;
		if (cursor.moveToFirst()) {
			do {
				offerProviders = new OfferProviders();

				offerProviders
						.setProviderId(Long.parseLong(cursor.getString(0)));
				offerProviders.setProviderName(cursor.getString(1));

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
				 * offerProviders.setImageUrlSmallStr
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
				offerProviders.setImageUrlSmallStr(displayImagePathSmall);
				// offerProviders.setImageUrlSmallStr(cursor.getString(3));
				offerProvidersList.add(offerProviders);
			} while (cursor.moveToNext());
		}
		cursor.close();

		return offerProvidersList;
	}

	public static List<Integer> getAllUserOfferProviders(Context activity,
			long loginUserId) {

		List<Integer> userOfferProvidersList = new ArrayList<Integer>();
		DatabaseHandler databaseHandler = DatabaseHandler
				.getDatabaseHandlerInstance(activity);

		SQLiteDatabase db = databaseHandler.getReadableDatabase();

		String query = "select providerId, userId from UserOfferProviders where UserId = "
				+ loginUserId;
		Cursor cursor = db.rawQuery(query, null);
		if (cursor.moveToFirst()) {
			do {
				userOfferProvidersList.add(cursor.getInt(0));
			} while (cursor.moveToNext());
		}
		cursor.close();

		return userOfferProvidersList;
	}

	public static void deleteUserOfferProviders(Context activity, long userId) {
		DatabaseHandler databaseHandler = DatabaseHandler
				.getDatabaseHandlerInstance(activity);

		SQLiteDatabase db = databaseHandler.getWritableDatabase();
		db.delete(TABLE_USER_OFFER_PROVIDERS, USER_ID + " = ?",
				new String[] { String.valueOf(userId) });
	}

	public String InsertUserOfferProviders(Activity activity,
			List<Integer> offerProviders, long userId) {

		String returnMsg = "Successfully updated Offer Providers.";
		
		ConnectivityManager cm = (ConnectivityManager) activity.getSystemService(Activity.CONNECTIVITY_SERVICE);
		if(cm != null && cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected()){
			returnMsg = OfferProvidersService.updateUserOfferProvidersListToBackEnd(activity, offerProviders, userId);
		} 

		updateUserOfferProvidersList(activity, offerProviders, userId);

		return returnMsg;
	}

	public static void updateUserOfferProvidersList(Context activity,
			List<Integer> offerProviders, long userId) {
		// First Delete all the offerProviders for that user then insert
		// selected offer providers.
		deleteUserOfferProviders(activity, userId);

		DatabaseHandler databaseHandler = DatabaseHandler
				.getDatabaseHandlerInstance(activity);
		SQLiteDatabase db = databaseHandler.getWritableDatabase();
		for (int i = 0; i < offerProviders.size(); i++) {
			ContentValues values = new ContentValues();
			values.put("userId", userId);
			values.put("providerId", offerProviders.get(i));

			db.insert(TABLE_USER_OFFER_PROVIDERS, null, values);
		}
	}
	

	public Boolean isUserProviderExists(String id, String providerId,
			Context activity) {
		DatabaseHandler databaseHandler = DatabaseHandler
				.getDatabaseHandlerInstance(activity);
		SQLiteDatabase db = databaseHandler.getReadableDatabase();
		String query = "SELECT * FROM " + TABLE_USER_OFFER_PROVIDERS_FAVOURITE
				+ " WHERE userId=" + id + " " + "and " + "providerId = "
				+ providerId + "";
		Cursor mCursor = db.rawQuery(query, null);

		if (mCursor.getCount() > 0) {
			return true;
		}
		return false;
	}

	public void addUpdateUserProviders(boolean isUserProviderExists,
			OfferProviders offerProviders, Activity activity) {
		DatabaseHandler databaseHandler = DatabaseHandler
				.getDatabaseHandlerInstance(activity);

		SQLiteDatabase db = databaseHandler.getWritableDatabase();

		ContentValues values = new ContentValues();

		if (isUserProviderExists) {
			values.put("isFavorite", offerProviders.isFavorite() ? 1 : 0);
			values.put("isDirty", offerProviders.isDirty() ? 1 : 0);

			// Updating Row
			db.update(
					TABLE_USER_OFFER_PROVIDERS_FAVOURITE,
					values,
					"providerId = ? and userId = ?",
					new String[] {
							String.valueOf(offerProviders.getProviderId()),
							String.valueOf(offerProviders.getUserId()) });
		} else {
			values.put("userId", offerProviders.getUserId());
			values.put("providerId", offerProviders.getProviderId());
			values.put("isFavorite", offerProviders.isFavorite());
			values.put("isDirty", offerProviders.isDirty() ? 1 : 0);			
			// Inserting Row
			long rowId = db.insert(TABLE_USER_OFFER_PROVIDERS_FAVOURITE, null,
					values);
		}
	}

	public Boolean isFavourit(long userId, int providerId, Context activity) {
		DatabaseHandler databaseHandler = DatabaseHandler
				.getDatabaseHandlerInstance(activity);
		SQLiteDatabase db = databaseHandler.getReadableDatabase();
		String query = "SELECT isFavorite FROM "
				+ TABLE_USER_OFFER_PROVIDERS_FAVOURITE + " WHERE userId="
				+ userId + " " + "and " + "providerId = " + providerId + "";
		Cursor mCursor = db.rawQuery(query, null);

		boolean isFavourite = false;
		if (mCursor != null && mCursor.getCount() > 0) {
			mCursor.moveToFirst();
			isFavourite = Boolean.valueOf("1".equals(mCursor.getString(0)));
		}
		return isFavourite;
	}

	public int getProviderId(String offerName, Activity activity) {
		DatabaseHandler databaseHandler = DatabaseHandler
				.getDatabaseHandlerInstance(activity);
		SQLiteDatabase db = databaseHandler.getReadableDatabase();
		String query = "SELECT providerId FROM " + TABLE_OFFER_PROVIDERS
				+ " WHERE providerName='" + offerName + "'";
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
	
	public void refreshUserOfferFavourite(Context activity,long userId) {
		DatabaseHandler databaseHandler = DatabaseHandler
				.getDatabaseHandlerInstance(activity);
		SQLiteDatabase db = databaseHandler.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put("isFavorite", 0);

		db.update(TABLE_USER_OFFER_PROVIDERS_FAVOURITE, values, "userId = ?",
				new String[] { String.valueOf(userId) });
	}	
	
	public void addUserFavourite(UserFavorite userFavorite,Context currentActivity){
		
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
					TABLE_USER_OFFER_PROVIDERS_FAVOURITE,
					values,
					"providerId = ? and userId = ?",
					new String[] {
							String.valueOf(providerId),
							String.valueOf(userId) });
		} else{
			values.put("userId", userId);
			values.put("providerId",providerId);
			values.put("isFavorite",isFavourite);
			db.insert(TABLE_USER_OFFER_PROVIDERS_FAVOURITE, null, values);
		}
	}
	
	public List<OfferProviders> getAllDirtyFavorites(Context activity,long userId) {
		DatabaseHandler databaseHandler = DatabaseHandler
				.getDatabaseHandlerInstance(activity);
		SQLiteDatabase db = databaseHandler.getReadableDatabase();
		
		List<OfferProviders> offerProvidersList = new ArrayList<OfferProviders>();
		
		String userOfferProvidersFavoriteCount = "Select * from UserOfferProvidersFavorite where userId = "+ userId + " and isDirty=1";
		Cursor countCursor = db.rawQuery(userOfferProvidersFavoriteCount, null);
		String query = "";
		if (countCursor.getCount() > 0) {
			query = "select providerId,isFavorite from UserOfferProvidersFavorite where userId="+ userId + " and isDirty=1";
		
			Cursor cursor = db.rawQuery(query, null);
			OfferProviders offerProviders = null;
			if (cursor != null && cursor.moveToFirst()) {
				offerProviders = new OfferProviders();
				do {
					
					offerProviders.setProviderId(Long.parseLong(cursor.getString(0)));
					offerProviders.setFavorite(cursor.getString(1).equals("1"));
					offerProvidersList.add(offerProviders);
					
				} while (cursor.moveToNext());
			}
			cursor.close();
		}
		
		return offerProvidersList;
	}
	
	public static void resetDirtyFavoritesToFalse(String providerIds,
			Context activity, long userId) {
		if (providerIds != null && providerIds.length() > 0) {
			DatabaseHandler databaseHandler = DatabaseHandler.getDatabaseHandlerInstance(activity);

			SQLiteDatabase db = databaseHandler.getWritableDatabase();

			String updateDirtyFlagOffersFavoriteQuery = "update UserOfferProvidersFavorite set isDirty = 0 where providerId in("+ providerIds + ") and userId=" + userId;
			db.execSQL(updateDirtyFlagOffersFavoriteQuery);
		}
	}	
}
