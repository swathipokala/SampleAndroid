package com.rrdonnelly.up2me.dao;

import com.rrdonnelly.up2me.json.ClubStatement;

import android.app.Activity;
import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;

public class ClubStatementDAO {

	private static final String TABLE_CLUB_STATEMENT = "clubstatement";

	/*
	 * public String userID=null; public String userToken=null; public String
	 * salt=null;
	 */

	/**
	 * All CRUD(Create, Read, Update, Delete) Operations
	 */

	public void addClubStatement(ClubStatement clubStatement, Activity activity) {

		Long id = clubStatement.getId();
		String name = clubStatement.getName();

		DatabaseHandler databaseHandler = DatabaseHandler
				.getDatabaseHandlerInstance(activity);

		SQLiteDatabase db = databaseHandler.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put("id", id);
		values.put("name", name);
		// values.put("type", type);

		// Inserting Row
		long rowId = db.insert(TABLE_CLUB_STATEMENT, null, values);

		// db.close(); // Closing database connection
	}

	public static void deleteClubStatements(Activity activity) {
		DatabaseHandler databaseHandler = DatabaseHandler
				.getDatabaseHandlerInstance(activity);

		SQLiteDatabase db = databaseHandler.getWritableDatabase();
		db.delete(TABLE_CLUB_STATEMENT, null, null);
		// db.close();
	}

}