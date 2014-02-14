package com.rrdonnelly.up2me.dao;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.rrdonnelly.up2me.util.tags.AsyncTagSyncToBackend;
import com.rrdonnelly.up2me.valueobjects.User;
import com.rrdonnelly.up2me.valueobjects.UserRegistration;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class UserDAO {

	private static final String TABLE_USER = "users";

	public void saveUser(User user, Activity activity) {

		DatabaseHandler databaseHandler = DatabaseHandler
				.getDatabaseHandlerInstance(activity);

		SQLiteDatabase db = databaseHandler.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put("id", user.getId());
		values.put("username", user.getUserName());
		values.put("password", user.getPassword());
		// Inserting Row
		long rowId = db.insert(TABLE_USER, null, values);
	}

	public int updateUser(User user, Activity activity) {

		DatabaseHandler databaseHandler = DatabaseHandler
				.getDatabaseHandlerInstance(activity);
		SQLiteDatabase db = databaseHandler.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put("username", user.getUserName());
		values.put("password", user.getPassword());

		// updating row
		return db.update(TABLE_USER, values, "ID = ?",
				new String[] { String.valueOf(user.getId()) });
	}

	public int updateUserURLS(String URL, String column, Long userId,
			Activity activity) {

		DatabaseHandler databaseHandler = DatabaseHandler
				.getDatabaseHandlerInstance(activity);
		SQLiteDatabase db = databaseHandler.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(column, URL);

		// updating row
		return db.update(TABLE_USER, values, "ID = ?",
				new String[] { String.valueOf(userId) });
	}

	public static int updateUserDetails(UserRegistration user, Context activity) {
		DatabaseHandler databaseHandler = DatabaseHandler
				.getDatabaseHandlerInstance(activity);
		SQLiteDatabase db = databaseHandler.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put("username", user.getUserName());
		values.put("password", user.getPassword());
		values.put("firstName", user.getFirstname());
		values.put("lastName", user.getLastname());
		values.put("streeAddress1", user.getStreetaddress1());
		values.put("streeAddress2", user.getStreetaddress2());
		values.put("city", user.getCity());
		values.put("state", user.getState());
		values.put("zipcode", user.getZip());
		values.put("mobileno", user.getMobileno1());

		// updating row
		return db.update(TABLE_USER, values, "ID = ?",
				new String[] { String.valueOf(user.getUserId()) });
	}

	public static Boolean isUserExists(String id, Context activity) {
		DatabaseHandler databaseHandler = DatabaseHandler
				.getDatabaseHandlerInstance(activity);
		SQLiteDatabase db = databaseHandler.getReadableDatabase();
		String query = "SELECT * FROM " + TABLE_USER + " WHERE id=" + id;
		Cursor mCursor = db.rawQuery(query, null);

		if (mCursor.getCount() > 0) {
			return true;
		}
		return false;
	}

	public static User getUserByUserName(String email, Context activity) {
		DatabaseHandler databaseHandler = DatabaseHandler
				.getDatabaseHandlerInstance(activity);
		SQLiteDatabase db = databaseHandler.getReadableDatabase();
		String query = "SELECT id,username,password,zipcode,firstName,lastName,streeAddress1,streeAddress2,city,state,mobileno FROM "
				+ TABLE_USER + " WHERE username='" + email + "'";
		Cursor mCursor = db.rawQuery(query, null);
		User user = new User();
		if (mCursor.moveToFirst()) {
			do {
				user.setId(mCursor.getLong(0));
				user.setUserName(mCursor.getString(1));
				user.setPassword(mCursor.getString(2));
				user.setZipCode(mCursor.getString(3));
				user.setFirsName(mCursor.getString(4));
				user.setLastName(mCursor.getString(5));
				user.setStreeAddr1(mCursor.getString(6));
				user.setStreeAddr2(mCursor.getString(7));
				user.setCity(mCursor.getString(8));
				user.setState(mCursor.getString(9));
				user.setMobileNo(mCursor.getString(10));
			} while (mCursor.moveToNext());

		}
		mCursor.close();
		return user;
	}

	public String getUserURL(Long userID, String column, Activity activity) {
		DatabaseHandler databaseHandler = DatabaseHandler
				.getDatabaseHandlerInstance(activity);
		SQLiteDatabase db = databaseHandler.getReadableDatabase();
		String query = "SELECT " + column + " FROM " + TABLE_USER
				+ " WHERE ID='" + userID + "'";
		Cursor mCursor = db.rawQuery(query, null);
		String URL = "";
		if (mCursor.moveToFirst()) {
			do {
				URL = mCursor.getString(0);
			} while (mCursor.moveToNext());

		}
		mCursor.close();
		return URL;
	}

	public static void insertUserDetails(UserRegistration userRegistartion,
			Context activity, long userId) {
		DatabaseHandler databaseHandler = DatabaseHandler
				.getDatabaseHandlerInstance(activity);
		SQLiteDatabase db = databaseHandler.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put("id", userRegistartion.getUserId());
		values.put("username", userRegistartion.getUserName());
		values.put("password", userRegistartion.getPassword());
		values.put("firstName", userRegistartion.getFirstname());
		values.put("lastName", userRegistartion.getLastname());
		values.put("streeAddress1", userRegistartion.getStreetaddress1());
		values.put("streeAddress2", userRegistartion.getStreetaddress2());
		values.put("city", userRegistartion.getCity());
		values.put("state", userRegistartion.getState());
		values.put("zipcode", userRegistartion.getZip());
		values.put("mobileno", userRegistartion.getMobileno1());

		db.insert(TABLE_USER, null, values);
		
		UserDAO.createSystemTagsForUser(activity,userId);
		
		//new AsyncTagSyncToBackend(activity,true,false,false).execute(String.valueOf(userId));
	}

	public static void insertAllProvidersList(Context currentActivity,
			JSONArray preferencesList, long userId) throws JSONException {

		List<Integer> userCloudProvidersList = new ArrayList<Integer>();
		List<Integer> userDocumentProvidersList = new ArrayList<Integer>();
		List<Integer> userOfferProvidersList = new ArrayList<Integer>();

		for (int i = 0; i < preferencesList.length(); i++) {
			JSONObject preferenceObject = (JSONObject) preferencesList.get(i);

			String providerType = preferenceObject.getString("providerType");
			int providerId = preferenceObject.getInt("providerId");

			if (providerType.equalsIgnoreCase("Cloud")) {
				userCloudProvidersList.add(providerId);
			}

			if (providerType.equalsIgnoreCase("Account")) {
				userDocumentProvidersList.add(providerId);
			}

			if (providerType.equalsIgnoreCase("Offer")) {
				userOfferProvidersList.add(providerId);
			}
		}

		// Inserting the prepared providers list into local DB
		DocumentProvidersDAO.updateUserDocumentProviders(currentActivity,
				userDocumentProvidersList, userId);
		OfferProvidersDAO.updateUserOfferProvidersList(currentActivity,
				userOfferProvidersList, userId);
		CloudProviderDAO.updateUserCloudProvidersList(currentActivity, userCloudProvidersList, userId);
	}
	
	public static void createSystemTagsForUser(Context currentActivity, long userId){
		String tagName = "Important";
		String tagColorCode = "#EE1F25";
		new TagDAO().updateOrInsertTag(tagName, tagColorCode,currentActivity, userId);
	}


}
