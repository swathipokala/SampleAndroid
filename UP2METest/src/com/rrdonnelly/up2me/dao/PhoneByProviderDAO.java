package com.rrdonnelly.up2me.dao;

import java.util.List;

import com.rrdonnelly.up2me.json.Phone;
import com.rrdonnelly.up2me.json.PhoneByProvider;

import android.app.Activity;
import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;

public class PhoneByProviderDAO {

	private static final String TABLE_PHONES_SERVICE = "PhonesService";

	public static void deletePhonesService(Activity activity) {
		DatabaseHandler databaseHandler = DatabaseHandler
				.getDatabaseHandlerInstance(activity);

		SQLiteDatabase db = databaseHandler.getWritableDatabase();
		db.delete(TABLE_PHONES_SERVICE, null, null);
		// db.close();
	}

	public void addPhoneByProvider(PhoneByProvider phoneByProvider,
			Activity activity) {

		Long providerId = phoneByProvider.getProviderId();
		String providerName = phoneByProvider.getProviderName();

		/*
		 * DatabaseHandler databaseHandler =
		 * DatabaseHandler.getDatabaseHandlerInstance();
		 * 
		 * SQLiteDatabase db = databaseHandler.getWritableDatabase();
		 * 
		 * ContentValues values = new ContentValues(); values.put("providerId",
		 * providerId); values.put("providerName", providerName);
		 * 
		 * // Inserting Row long rowId = db.insert(TABLE_PHONES_SERVICE, null,
		 * values);
		 */

		addPhonesList(phoneByProvider.getPhones(), providerId, providerName,
				activity);

		// db.close(); // Closing database connection
	}

	private void addPhonesList(List<Phone> phonesList, long providerId,
			String providerName, Activity activity) {
		for (Phone phone : phonesList) {
			addPhone(phone, providerId, providerName, activity);
		}
	}

	private void addPhone(Phone phone, long providerId, String providerName,
			Activity activity) {

		DatabaseHandler databaseHandler = DatabaseHandler
				.getDatabaseHandlerInstance(activity);

		SQLiteDatabase db = databaseHandler.getWritableDatabase();

		ContentValues values = new ContentValues();

		values.put("providerID", providerId);
		values.put("providerName", providerName);

		values.put("phoneID", phone.getId());
		values.put("manufacturer", phone.getManufacturer());
		values.put("name", phone.getName());
		values.put("color", phone.getColor());
		values.put("dimensionsInInches", phone.getDimensionsInInches());
		values.put("operatingsystem", phone.getOperatingsystem());
		values.put("processor", phone.getProcessor());
		values.put("connectionspeed", phone.getConnectionspeed());
		values.put("displayResolution", phone.getDisplayResolution());
		values.put("storageMemoryInGB", phone.getStorageMemoryInGB());
		values.put("cameraMegapixels", phone.getCameraMegapixels());
		values.put("batteryLifeInHours", phone.getBatteryLifeInHours());
		values.put("priceWithContract", phone.getPriceWithContract());
		values.put("weightInOunces", phone.getWeightInOunces());
		values.put("availableDate", phone.getAvailableDate());
		values.put("displaySizeInInches", phone.getDisplaySizeInInches());

		// Inserting Row
		db.insert(TABLE_PHONES_SERVICE, null, values);

		// db.close(); // Closing database connection
	}

}