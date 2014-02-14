package com.rrdonnelly.up2me.dao;

import java.util.List;

import com.rrdonnelly.up2me.json.ContractTerminationFee;

import android.app.Activity;
import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class ContractTerminationFeeDAO {

	private static final String TABLE_CONTRACT_TERMINATION_FEE = "ContractTerminationFee";
	private static final String TABLE_CONTRACT_TERMINATION_FEES_LIST = "ContractTerminationFeesList";

	/*
	 * public String userID=null; public String userToken=null; public String
	 * salt=null;
	 */

	public static void deleteContractTerminationFees(Activity activity) {
		DatabaseHandler databaseHandler = DatabaseHandler
				.getDatabaseHandlerInstance(activity);

		SQLiteDatabase db = databaseHandler.getWritableDatabase();
		db.delete(TABLE_CONTRACT_TERMINATION_FEE, null, null);
		// db.close();
	}

	public static void deleteContractTerminationFeesList(Activity activity) {
		DatabaseHandler databaseHandler = DatabaseHandler
				.getDatabaseHandlerInstance(activity);

		SQLiteDatabase db = databaseHandler.getWritableDatabase();
		db.delete(TABLE_CONTRACT_TERMINATION_FEES_LIST, null, null);
		// db.close();
	}

	public void addContractTerminationFee(
			ContractTerminationFee contractTerminationFee, Activity activity) {

		Long providerId = contractTerminationFee.getProviderId();
		String providerName = contractTerminationFee.getProviderName();

		List<Double> fees = contractTerminationFee.getFees();

		DatabaseHandler databaseHandler = DatabaseHandler
				.getDatabaseHandlerInstance(activity);

		SQLiteDatabase db = databaseHandler.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put("providerId", providerId);
		values.put("providerName", providerName);

		// Inserting Row
		long rowId = db.insert(TABLE_CONTRACT_TERMINATION_FEE, null, values);

		addContractTerminationFeeList(contractTerminationFee.getFees(),
				providerId, activity);

		// db.close(); // Closing database connection
	}

	private void addContractTerminationFeeList(List<Double> feesList,
			long providerId, Activity activity) {
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
		db.insert(TABLE_CONTRACT_TERMINATION_FEES_LIST, null, values);

		// db.close(); // Closing database connection
	}

}