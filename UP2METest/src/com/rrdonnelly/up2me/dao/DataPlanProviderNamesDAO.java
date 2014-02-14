package com.rrdonnelly.up2me.dao;

import android.app.Activity;
import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;

public class DataPlanProviderNamesDAO {

	private static final String TABLE_DATA_PLAN_PROVIDER_NAMES = "DataPlanProviderNames";

	public static void deleteDataPlanProviderNames(Activity activity) {
		DatabaseHandler databaseHandler = DatabaseHandler
				.getDatabaseHandlerInstance(activity);

		SQLiteDatabase db = databaseHandler.getWritableDatabase();
		db.delete(TABLE_DATA_PLAN_PROVIDER_NAMES, null, null);
		// db.close();
	}

	public void addDataPlanProviderName(String dataPlanProviderName,
			Activity activity) {

		DatabaseHandler databaseHandler = DatabaseHandler
				.getDatabaseHandlerInstance(activity);

		SQLiteDatabase db = databaseHandler.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put("dataPlanProviderName", dataPlanProviderName);

		// Inserting Row
		long rowId = db.insert(TABLE_DATA_PLAN_PROVIDER_NAMES, null, values);

		// db.close(); // Closing database connection
	}
}