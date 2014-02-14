package com.rrdonnelly.up2me.dao;

import com.rrdonnelly.up2me.json.ClubProvider;
import com.rrdonnelly.up2me.json.Image;

import android.app.Activity;
import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;

public class UserSubscriptionOfferDAO {

	// All Static variables
	private static final String TABLE_SUBSCRIPTION_OFFER = "SubscriptionOffer";

	public static void deleteClubProviders(Activity activity) {
		DatabaseHandler databaseHandler = DatabaseHandler
				.getDatabaseHandlerInstance(activity);

		SQLiteDatabase db = databaseHandler.getWritableDatabase();
		db.delete(TABLE_SUBSCRIPTION_OFFER, null, null);
		// db.close();
	}

	public static void addClubProvider(ClubProvider clubProvider,
			long loginUserId, Activity activity) {

		Long Id = clubProvider.getId();
		String name = clubProvider.getName();
		Image displayImagePath = clubProvider.getDisplayImagePath();
		boolean subscribed = clubProvider.isSubscribed();
		// long userId = 7l;

		DatabaseHandler databaseHandler = DatabaseHandler
				.getDatabaseHandlerInstance(activity);

		SQLiteDatabase db = databaseHandler.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put("soID", Id);
		values.put("name", name);
		values.put("subscribed", subscribed);
		values.put("isOffer", true);
		values.put("isBill", false);
		values.put("isAccount", false);
		values.put("userID", loginUserId);

		if (displayImagePath != null) {
			values.put("displayImagePath", displayImagePath.getPath());
			values.put("displayImagePathAltText", displayImagePath.getAltText());
		}
		// Inserting Row
		long rowId = db.insert(TABLE_SUBSCRIPTION_OFFER, null, values);

		// db.close(); // Closing database connection
	}

}
