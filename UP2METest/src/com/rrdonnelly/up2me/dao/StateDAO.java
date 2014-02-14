package com.rrdonnelly.up2me.dao;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.rrdonnelly.up2me.json.State;

public class StateDAO {

	private static final String TABLE_STATE = "State";

	public static void deleteState(Activity activity) {
		DatabaseHandler databaseHandler = DatabaseHandler
				.getDatabaseHandlerInstance(activity);

		SQLiteDatabase db = databaseHandler.getWritableDatabase();
		db.delete(TABLE_STATE, null, null);
	}

	public static void addState(State state, Activity activity) {

		int stateId = state.getStateId();
		String stateCode = state.getStateCode();
		String stateName = state.getStateName();
		int countryId = state.getCountryId();
		String countryName = state.getCountryName();
		String countryCode = state.getCountryCode();
		boolean isActive = state.isActive();

		DatabaseHandler databaseHandler = DatabaseHandler
				.getDatabaseHandlerInstance(activity);
		SQLiteDatabase db = databaseHandler.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put("stateId", stateId);
		values.put("stateCode", stateCode);
		values.put("stateName", stateName);
		values.put("countryId", countryId);
		values.put("countryName", countryName);
		values.put("countryCode", countryCode);
		values.put("isActive", isActive);

		db.insert(TABLE_STATE, null, values);
	}

	public List<State> getAllStatesByCountry(Activity activity, String country) {

		List<State> statesList = new ArrayList<State>();
		DatabaseHandler databaseHandler = DatabaseHandler
				.getDatabaseHandlerInstance(activity);
		SQLiteDatabase db = databaseHandler.getReadableDatabase();

		String query = "select stateId, stateCode, stateName, countryId, countryName, countryCode, isActive from State where isActive = 1 ";
		if (country != "" && country.length() > 0) {
			query = query + " and countryCode = " + country;
		}
		Cursor cursor = db.rawQuery(query, null);
		State state = null;
		if (cursor.moveToFirst()) {
			do {
				state = new State();

				state.setStateId(cursor.getInt(0));
				state.setStateCode(cursor.getString(1));
				state.setStateName(cursor.getString(2));
				state.setCountryId(cursor.getInt(3));
				state.setCountryName(cursor.getString(4));
				state.setCountryCode(cursor.getString(5));
				state.setActive(Boolean
						.valueOf(cursor.getString(6).equals("1")));

				statesList.add(state);
			} while (cursor.moveToNext());
		}
		cursor.close();

		return statesList;
	}

	public static String getStateNameByStateId(Context activity, String stateId) {
		DatabaseHandler databaseHandler = DatabaseHandler
				.getDatabaseHandlerInstance(activity);
		SQLiteDatabase db = databaseHandler.getReadableDatabase();
		String stateName = "";

		String query = "select stateId, stateCode, stateName from State where stateId = "
				+ stateId;

		Cursor cursor = db.rawQuery(query, null);
		if (cursor.moveToFirst()) {
			stateName = cursor.getString(1);
		}
		cursor.close();

		return stateName;
	}
}
