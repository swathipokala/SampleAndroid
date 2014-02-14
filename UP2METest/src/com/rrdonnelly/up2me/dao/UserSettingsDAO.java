package com.rrdonnelly.up2me.dao;

import java.io.BufferedReader;
import java.io.InputStreamReader;

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
import com.rrdonnelly.up2me.valueobjects.UserRegistration;
import com.rrdonnelly.up2me.valueobjects.UserSettings;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.StrictMode;

public class UserSettingsDAO {

	private static final String TABLE_USERSETTINGS = "UserSettings";

	public Boolean isUserExists(String id, Activity activity) {
		DatabaseHandler databaseHandler = DatabaseHandler
				.getDatabaseHandlerInstance(activity);
		SQLiteDatabase db = databaseHandler.getReadableDatabase();
		String query = "SELECT * FROM " + TABLE_USERSETTINGS + " WHERE id="
				+ id;
		Cursor mCursor = db.rawQuery(query, null);

		if (mCursor.getCount() > 0) {
			return true;
		}
		return false;
	}

	public static void saveUserToggles(UserSettings userSettings,
			Context activity) {

		DatabaseHandler databaseHandler = DatabaseHandler
				.getDatabaseHandlerInstance(activity);

		SQLiteDatabase db = databaseHandler.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put("id", userSettings.getUserId());
		values.put("showBadgesStatements",
				userSettings.isShowBadgesStatements());
		values.put("showBadgesBills", userSettings.isShowBadgesBills());
		values.put("showBadgesOffers", userSettings.isShowBadgesOffers());
		values.put("showBadgesLibrary", userSettings.isShowBadgesLibrary());
		values.put("calendarAlertsBills", userSettings.isCalendarAlertsBills());
		values.put("calendarAlertsOffers",
				userSettings.isCalendarAlertsOffers());
		values.put("cloudSyncStatements", userSettings.isCloudSyncStatements());
		values.put("cloudSyncBills", userSettings.isCloudSyncBills());
		values.put("cloudSyncAll", userSettings.isCloudSyncAll());
		values.put("cloudSyncAppData", userSettings.isCloudSyncAppData());
		values.put("passwordChecked", userSettings.isPasswordChecked());
		values.put("setTimeOut", userSettings.getSetTimeOut());

		// Inserting Row
		long rowId = db.insert(TABLE_USERSETTINGS, null, values);

	}

	public static int updateUserToggles(UserSettings userSettings,
			Context activity) {
		DatabaseHandler databaseHandler = DatabaseHandler
				.getDatabaseHandlerInstance(activity);
		SQLiteDatabase db = databaseHandler.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put("id", userSettings.getUserId());
		values.put("showBadgesStatements",
				userSettings.isShowBadgesStatements());
		values.put("showBadgesBills", userSettings.isShowBadgesBills());
		values.put("showBadgesOffers", userSettings.isShowBadgesOffers());
		values.put("showBadgesLibrary", userSettings.isShowBadgesLibrary());
		values.put("calendarAlertsBills", userSettings.isCalendarAlertsBills());
		values.put("calendarAlertsOffers",
				userSettings.isCalendarAlertsOffers());
		values.put("cloudSyncStatements", userSettings.isCloudSyncStatements());
		values.put("cloudSyncBills", userSettings.isCloudSyncBills());
		values.put("cloudSyncAll", userSettings.isCloudSyncAll());
		values.put("cloudSyncAppData", userSettings.isCloudSyncAppData());
		values.put("passwordChecked", userSettings.isPasswordChecked());
		values.put("setTimeOut", userSettings.getSetTimeOut());

		// updating row
		return db.update(TABLE_USERSETTINGS, values, "ID = ?",
				new String[] { String.valueOf(userSettings.getUserId()) });
	}

	public UserSettings getUserSettingsByUserId(long userId, Activity activity) {
		DatabaseHandler databaseHandler = DatabaseHandler
				.getDatabaseHandlerInstance(activity);
		SQLiteDatabase db = databaseHandler.getReadableDatabase();
		String query = "SELECT * FROM " + TABLE_USERSETTINGS + " WHERE id="
				+ userId;
		Cursor mCursor = db.rawQuery(query, null);
		UserSettings userSettings = new UserSettings();

		if (mCursor.moveToFirst()) {
			do {
				userSettings.setUserId(mCursor.getLong(0));
				userSettings.setShowBadgesStatements("1".equals(mCursor
						.getString(1)));
				userSettings
						.setShowBadgesBills("1".equals(mCursor.getString(2)));
				userSettings.setShowBadgesOffers("1".equals(mCursor
						.getString(3)));
				userSettings.setShowBadgesLibrary("1".equals(mCursor
						.getString(4)));
				userSettings.setCalendarAlertsBills("1".equals(mCursor
						.getString(5)));
				userSettings.setCalendarAlertsOffers("1".equals(mCursor
						.getString(6)));
				userSettings.setCloudSyncStatements("1".equals(mCursor
						.getString(7)));
				userSettings
						.setCloudSyncBills("1".equals(mCursor.getString(8)));
				userSettings.setCloudSyncAll("1".equals(mCursor.getString(9)));
				userSettings.setCloudSyncAppData("1".equals(mCursor
						.getString(10)));
				userSettings.setPasswordChecked("1".equals(mCursor
						.getString(11)));
				userSettings.setSetTimeOut(mCursor.getInt(12));
			} while (mCursor.moveToNext());

		}
		mCursor.close();
		return userSettings;
	}

	public static long createUserProfile(Activity currentActivity,
			UserRegistration userRegistrationData) {

		long userId = 0l;

		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
				.permitAll().build();
		StrictMode.setThreadPolicy(policy);

		HttpClient client = new DefaultHttpClient();
		String webServiceUrl = currentActivity.getResources().getString(
				R.string.webservice_url);
		JSONObject json = new JSONObject();
		try {
			HttpPost post = new HttpPost(webServiceUrl + "/RegisterUser");

			json.put("firstName", userRegistrationData.getFirstname());
			json.put("lastName", userRegistrationData.getLastname());
			json.put("streetAddress1", userRegistrationData.getStreetaddress1());
			json.put("streetAddress2", userRegistrationData.getStreetaddress2());
			json.put("city", userRegistrationData.getCity());
			json.put("stateId", userRegistrationData.getStateId());
			json.put("zip", userRegistrationData.getZip());
			json.put("mobileno1", userRegistrationData.getMobileno1());
			json.put("email", userRegistrationData.getUserName());
			json.put("pwd", userRegistrationData.getPassword());
			json.put("cloudProviderId",
					userRegistrationData.getCloudProviderId());
			json.put("cloudProviderUserName",
					userRegistrationData.getCloudProviderUserName());
			json.put("cloudProviderPassword",
					userRegistrationData.getCloudProviderPassword());

			JSONArray selectedDocProviders = new JSONArray(
					userRegistrationData.getSelectedDocumentProviders());
			json.put("selectedDocumentProviders", selectedDocProviders);

			JSONArray selectedOfferProviders = new JSONArray(
					userRegistrationData.getSelectedOfferProviders());
			json.put("selectedOfferProviders", selectedOfferProviders);

			StringEntity se = new StringEntity(json.toString());
			se.setContentType(new BasicHeader(HTTP.CONTENT_TYPE,
					"application/json"));
			post.setEntity(se);
			HttpResponse response = client.execute(post);

			BufferedReader rd = new BufferedReader(new InputStreamReader(
					response.getEntity().getContent()));
			StringBuilder sb = new StringBuilder();

			String line = "";
			while ((line = rd.readLine()) != null) {
				sb.append(line);
			}

			userId = Long.parseLong(sb.toString());

		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return userId;

	}

}
