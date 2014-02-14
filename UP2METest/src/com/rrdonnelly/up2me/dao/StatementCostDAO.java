package com.rrdonnelly.up2me.dao;

import com.rrdonnelly.up2me.json.StatementCost;
import android.app.Activity;
import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;

public class StatementCostDAO {

	// All Static variables
	private static final String TABLE_STATEMENTS_COST = "StatementsCost";

	public static void deleteStatementsCost(Activity activity) {
		DatabaseHandler databaseHandler = DatabaseHandler
				.getDatabaseHandlerInstance(activity);

		SQLiteDatabase db = databaseHandler.getWritableDatabase();
		db.delete(TABLE_STATEMENTS_COST, null, null);
		// db.close();
	}

	public static void addStatementCost(StatementCost statementCost,
			long loginUserId, Activity activity) {
		int year = statementCost.getYear();
		String month = statementCost.getMonth();
		Double dataCost = statementCost.getDataCost();
		Long textUsage = statementCost.getTextUsage();
		Double totalCost = statementCost.getTotalCost();
		Double minutesUsed = statementCost.getMinutesUsed();
		int lineCount = statementCost.getLineCount();
		String contractStartDate = statementCost.getContractStartDate();
		String contractEndDate = statementCost.getContractEndDate();
		String providerName = statementCost.getProviderName();
		String planCost = statementCost.getPlanCost();
		Double accessDiscount = statementCost.getAccessDiscount();
		Double dataOverageCost = statementCost.getDataOverageCost();
		Double minutesOverageCost = statementCost.getMinutesOverageCost();
		Double textOverageCost = statementCost.getTextOverageCost();
		// long userId = 7l;

		DatabaseHandler databaseHandler = DatabaseHandler
				.getDatabaseHandlerInstance(activity);

		SQLiteDatabase db = databaseHandler.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put("year", year);
		values.put("month", month);
		values.put("dataCost", dataCost);
		values.put("textUsage", textUsage);
		values.put("totalCost", totalCost);
		values.put("minutesUsed", minutesUsed);
		values.put("lineCount", lineCount);
		values.put("contractStartDate", contractStartDate);
		values.put("contractEndDate", contractEndDate);
		values.put("providerName", providerName);
		values.put("planCost", planCost);
		values.put("accessDiscount", accessDiscount);
		values.put("dataOverageCost", dataOverageCost);
		values.put("minutesOverageCost", minutesOverageCost);
		values.put("textOverageCost", textOverageCost);
		values.put("userID", loginUserId);
		// Inserting Row
		long rowId = db.insert(TABLE_STATEMENTS_COST, null, values);

		// db.close(); // Closing database connection
	}

}