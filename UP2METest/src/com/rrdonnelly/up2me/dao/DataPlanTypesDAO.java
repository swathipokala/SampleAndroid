package com.rrdonnelly.up2me.dao;

import android.app.Activity;
import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;

public class DataPlanTypesDAO {

	private static final String TABLE_DATA_PLAN_TYPES = "DataPlanTypes";

	public static void deleteDataPlanTypes(Activity activity) {
		DatabaseHandler databaseHandler = DatabaseHandler
				.getDatabaseHandlerInstance(activity);

		SQLiteDatabase db = databaseHandler.getWritableDatabase();
		db.delete(TABLE_DATA_PLAN_TYPES, null, null);
		// db.close();
	}

	// Statement Webservices Functionality

	public void addDataPlanType(String dataPlanType, Activity activity) {

		DatabaseHandler databaseHandler = DatabaseHandler
				.getDatabaseHandlerInstance(activity);

		SQLiteDatabase db = databaseHandler.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put("planType", dataPlanType);

		// Inserting Row
		long rowId = db.insert(TABLE_DATA_PLAN_TYPES, null, values);

		// db.close(); // Closing database connection
	}
}