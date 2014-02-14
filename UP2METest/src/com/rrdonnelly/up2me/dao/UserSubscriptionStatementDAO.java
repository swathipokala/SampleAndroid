package com.rrdonnelly.up2me.dao;

import com.rrdonnelly.up2me.json.ClubProvider;
import com.rrdonnelly.up2me.json.Image;

import android.app.Activity;
import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;

public class UserSubscriptionStatementDAO {

	// All Static variables
	private static final String TABLE_SUBSCRIPTION_OFFER = "SubscriptionOffer";

	public void addClubProvider(ClubProvider clubProvider, String type,
			Activity activity) {

		Long id = clubProvider.getId();
		String name = clubProvider.getName();
		Image displayImagePath = clubProvider.getDisplayImagePath();
		boolean subscribed = clubProvider.isSubscribed();

		DatabaseHandler databaseHandler = DatabaseHandler
				.getDatabaseHandlerInstance(activity);

		SQLiteDatabase db = databaseHandler.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put("soID", id);
		values.put("name", name);
		values.put("subscribed", subscribed);
		values.put("isBill", type.equals("Bill"));
		values.put("isAccount", type.equals("Account"));
		values.put("isOffer", false);

		if (displayImagePath != null) {
			values.put("displayImagePath", displayImagePath.getPath());
			values.put("displayImagePathAltText", displayImagePath.getAltText());
		}

		// Inserting Row
		long rowId = db.insert(TABLE_SUBSCRIPTION_OFFER, null, values);

		// db.close(); // Closing database connection
	}

	public static void deleteClubStatements(Activity activity) {
		DatabaseHandler databaseHandler = DatabaseHandler
				.getDatabaseHandlerInstance(activity);

		SQLiteDatabase db = databaseHandler.getWritableDatabase();
		db.delete(TABLE_SUBSCRIPTION_OFFER, null, null);
		// db.close();
	}
}