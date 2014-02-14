package com.rrdonnelly.up2me.dao;


import java.util.List;

import com.rrdonnelly.up2me.json.DataPlan;

import android.app.Activity;
import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;

public class DataPlanDAO {

	private static final String TABLE_DATA_PLANS = "DataPlans";
	private static final String TABLE_DATA_PLAN_LINES = "DataPlanLines";

	public static void deleteDataPlans(Activity activity) {
		DatabaseHandler databaseHandler = DatabaseHandler
				.getDatabaseHandlerInstance(activity);

		SQLiteDatabase db = databaseHandler.getWritableDatabase();
		db.delete(TABLE_DATA_PLANS, null, null);
		// db.close();
	}

	public static void deleteDataPlanLines(Activity activity) {
		DatabaseHandler databaseHandler = DatabaseHandler
				.getDatabaseHandlerInstance(activity);

		SQLiteDatabase db = databaseHandler.getWritableDatabase();
		db.delete(TABLE_DATA_PLAN_LINES, null, null);
		// db.close();
	}

	// Statement Webservices Functionality

	public void addDataPlan(DataPlan dataPlan, Activity activity) {

		String providerName = dataPlan.getProviderName();
		String dataValue = dataPlan.getDataValue();
		String dataUnit = dataPlan.getDataUnit();
		String planType = dataPlan.getPlanType();

		String minutesValue = dataPlan.getMinutesValue();
		String textMessageValue = dataPlan.getTextMessageValue();

		List<Double> lines = dataPlan.getLines();

		DatabaseHandler databaseHandler = DatabaseHandler
				.getDatabaseHandlerInstance(activity);

		SQLiteDatabase db = databaseHandler.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put("providerName", providerName);
		values.put("dataValue", dataValue);

		values.put("dataUnit", providerName);
		values.put("planType", dataValue);

		values.put("minutesValue", providerName);
		values.put("textMessageValue", dataValue);

		// Inserting Row
		long rowId = db.insert(TABLE_DATA_PLANS, null, values);

		// addDataPlanLinesList(dataPlan.getLines(), providerId);

		// db.close(); // Closing database connection
	}

	private void addDataPlanLinesList(List<Double> feesList, long providerId,
			Activity activity) {
		for (double fee : feesList) {
			addFees(fee, providerId, activity);
		}
	}

	private void addFees(double fees, long providerId, Activity activity) {

		DatabaseHandler databaseHandler = DatabaseHandler
				.getDatabaseHandlerInstance(activity);

		SQLiteDatabase db = databaseHandler.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put("providerId", providerId);
		values.put("fees", fees);

		// Inserting Row
		// db.insert(TABLE_CONTRACT_TERMINATION_FEES_LIST, null, values);

		// db.close(); // Closing database connection
	}

}