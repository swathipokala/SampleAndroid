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
import com.rrdonnelly.up2me.json.CloudProvider;
import com.rrdonnelly.up2me.json.Image;
import com.rrdonnelly.up2me.services.CloudProviderService;
import com.rrdonnelly.up2me.services.OfferProvidersService;
import com.rrdonnelly.up2me.util.ImageUtil;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.os.StrictMode;

public class CloudProviderDAO {

	// All Static variables
	private static final String TABLE_CLOUD_PROVIDER = "CloudProvider";
	private static final String TABLE_USER_CLOUD_PROVIDERS = "UserCloudProviders";
	private static final String USER_ID = "userId";

	public static void deleteCloudProviders(Context activity) {
		DatabaseHandler databaseHandler = DatabaseHandler
				.getDatabaseHandlerInstance(activity);

		SQLiteDatabase db = databaseHandler.getWritableDatabase();
		db.delete(TABLE_CLOUD_PROVIDER, null, null);
		// db.close();
	}

	public static void addCloudProvider(CloudProvider cloudProvider,
			Context activity) {

		Long clientId = cloudProvider.getClientId();
		Image displayImagePath = cloudProvider.getDisplayImagePath();
		String providerName = cloudProvider.getProviderName();

		DatabaseHandler databaseHandler = DatabaseHandler
				.getDatabaseHandlerInstance(activity);

		SQLiteDatabase db = databaseHandler.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put("clientId", clientId);
		values.put("providerName", providerName);
		values.put("displayImagePathAltText", cloudProvider.getImageText());
		if (displayImagePath != null) {
			values.put("displayImagePath", displayImagePath.getPath());

		}
		// Inserting Row
		long rowId = db.insert(TABLE_CLOUD_PROVIDER, null, values);

		// db.close(); // Closing database connection
	}
	
	

	public String InsertUserCloudProviders(Activity activity,
			List<Integer> cloudProviders, long userId) {
		
		String returnMsg = "Successfully updated Cloud Providers.";
		
		ConnectivityManager cm = (ConnectivityManager) activity.getSystemService(Activity.CONNECTIVITY_SERVICE);
		if(cm != null && cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected()){
			returnMsg = CloudProviderService.updateUserCloudProvidersListToBackEnd(activity, cloudProviders, userId);
		} 

		updateUserCloudProvidersList(activity, cloudProviders, userId);
		
		return returnMsg;
	}

	public static void updateUserCloudProvidersList(Context activity,
			List<Integer> cloudProviders, long userId) {
		// First Delete all the offerProviders for that user then insert
		// selected offer providers.
		deleteUserOfferProviders(activity, userId);

		DatabaseHandler databaseHandler = DatabaseHandler
				.getDatabaseHandlerInstance(activity);
		SQLiteDatabase db = databaseHandler.getWritableDatabase();
		for (int i = 0; i < cloudProviders.size(); i++) {
			ContentValues values = new ContentValues();
			values.put("userId", userId);
			values.put("providerId", cloudProviders.get(i));

			db.insert(TABLE_USER_CLOUD_PROVIDERS, null, values);
		}
	}

	public static void deleteUserOfferProviders(Context activity, long userId) {
		DatabaseHandler databaseHandler = DatabaseHandler
				.getDatabaseHandlerInstance(activity);

		SQLiteDatabase db = databaseHandler.getWritableDatabase();
		db.delete(TABLE_USER_CLOUD_PROVIDERS, USER_ID + " = ?",
				new String[] { String.valueOf(userId) });
	}

	public List<CloudProvider> getAllCloudProviders(Activity activity) {

		List<CloudProvider> cloudProvidersList = new ArrayList<CloudProvider>();

		DatabaseHandler databaseHandler = DatabaseHandler
				.getDatabaseHandlerInstance(activity);

		SQLiteDatabase db = databaseHandler.getReadableDatabase();

		String query = "select clientId, displayImagePath, displayImagePathAltText, providerName from CloudProvider";
		Cursor cursor = db.rawQuery(query, null);
		CloudProvider cloudProviders = null;
		if (cursor.moveToFirst()) {
			do {
				cloudProviders = new CloudProvider();

				cloudProviders.setClientId(Long.parseLong(cursor.getString(0)));

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

				String displayImagePathSmall = cursor.getString(1);
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
				cloudProviders.setStrImagePath(displayImagePathSmall);

				cloudProviders.setImageText(cursor.getString(2));
				cloudProviders.setProviderName(cursor.getString(3));
				// offerProviders.setImageUrlSmallStr(cursor.getString(3));
				cloudProvidersList.add(cloudProviders);
			} while (cursor.moveToNext());
		}
		cursor.close();

		return cloudProvidersList;
	}
	
	public static List<Integer> getAllUserCloudProviders(Context activity,
			long loginUserId) {

		List<Integer> userDocumentProvidersList = new ArrayList<Integer>();
		DatabaseHandler databaseHandler = DatabaseHandler
				.getDatabaseHandlerInstance(activity);

		SQLiteDatabase db = databaseHandler.getReadableDatabase();

		String query = "select providerId, userId from UserCloudProviders where UserId = "
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

}
