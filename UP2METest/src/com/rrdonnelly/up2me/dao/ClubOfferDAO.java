package com.rrdonnelly.up2me.dao;


import com.rrdonnelly.up2me.json.ClubOffer;
import com.rrdonnelly.up2me.json.Image;

import android.app.Activity;
import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;

public class ClubOfferDAO {

	// All Static variables
	private static final String TABLE_CLUB_OFFER = "ClubOffer";


	public static void deleteClubOffers(Activity activity) {
		DatabaseHandler databaseHandler = DatabaseHandler
				.getDatabaseHandlerInstance(activity);

		SQLiteDatabase db = databaseHandler.getWritableDatabase();
		db.delete(TABLE_CLUB_OFFER, null, null);
		// db.close();
	}

	public static void addClubOffer(ClubOffer clubOffer, Activity activity) {

		Long id = clubOffer.getId();
		Long clientId = clubOffer.getClientId();
		Image displayImagePath = clubOffer.getDisplayImagePath();

		DatabaseHandler databaseHandler = DatabaseHandler
				.getDatabaseHandlerInstance(activity);

		SQLiteDatabase db = databaseHandler.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put("id", id);
		values.put("clientId", clientId);

		if (displayImagePath != null) {
			values.put("displayImagePath", displayImagePath.getPath());
			values.put("displayImagePathAltText", displayImagePath.getAltText());
		}
		// Inserting Row
		long rowId = db.insert(TABLE_CLUB_OFFER, null, values);

		// db.close(); // Closing database connection
	}

}