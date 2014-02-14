package com.rrdonnelly.up2me.dao;

import java.util.ArrayList;
import java.util.List;

import com.rrdonnelly.up2me.json.UserDocHeaderTagJson;
import com.rrdonnelly.up2me.json.UserOfferTagJson;
import com.rrdonnelly.up2me.json.UserTagJson;
import com.rrdonnelly.up2me.util.ColorCodeUtil;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class TagDAO {

	// All Static variables
	private static final String TABLE_TAG = "Tags";

	public static void deleteTags(Context activity, long userId) {
		DatabaseHandler databaseHandler = DatabaseHandler
				.getDatabaseHandlerInstance(activity);

		SQLiteDatabase db = databaseHandler.getWritableDatabase();

		String deleteTagsQuery = "delete from " + TABLE_TAG + " where userId="+userId;

		db.execSQL(deleteTagsQuery);
	}

	public static void addTag(UserTagJson tag, long loginUserId,
			Context activity) {

		Long userId = tag.getUserId();
		String tagName = tag.getTagName();
		
		
		String colourInRGB = tag.getColour();
		String colourInHexa = "";
		if(colourInRGB!=null && colourInRGB.indexOf("#")==-1){
			colourInHexa = ColorCodeUtil.rgbToHex.get(colourInRGB);
		}else{
			colourInHexa = colourInRGB;
		}
		
		String colour = (colourInHexa!=null) ? colourInHexa.toUpperCase():"";
		//int sequenceId = tag.getSequenceId();
		long tagId = tag.getTagId();

		DatabaseHandler databaseHandler = DatabaseHandler
				.getDatabaseHandlerInstance(activity);

		SQLiteDatabase db = databaseHandler.getWritableDatabase();

		ContentValues values = new ContentValues();

		values.put("userId", userId);
		values.put("name", tagName);
		values.put("colorCode", colour);
		//values.put("sequenceId", sequenceId);
		values.put("tagId", tagId);

		long rowId = db.insert(TABLE_TAG, null, values);

	}
	
	
	public static void addUserOfferTag(UserOfferTagJson userOfferTag, long userId, Context activity){
		
		
		//Long userId = userOfferTag.getUserId();
		Long offerId = userOfferTag.getOfferId();
		Long tagId = userOfferTag.getTagId();
		
		DatabaseHandler databaseHandler = DatabaseHandler
				.getDatabaseHandlerInstance(activity);

		SQLiteDatabase db = databaseHandler.getWritableDatabase();

		String updateOffersServiceQuery = "update offersservice set tagId = "+tagId+" where userID=" + userId+" and offerID="+offerId;
		
		db.execSQL(updateOffersServiceQuery);
	}

	public static void addUserStatementTag(UserDocHeaderTagJson userStatementTag, long userId, Context activity){
		
		Long docHeaderId = userStatementTag.getDocHeaderId();
		Long tagId = userStatementTag.getTagId();
		
		DatabaseHandler databaseHandler = DatabaseHandler
				.getDatabaseHandlerInstance(activity);

		SQLiteDatabase db = databaseHandler.getWritableDatabase();

		String updateStatementsQuery = "update statements set tagId = "+ tagId+" where userId=" + userId+" and Id="+docHeaderId;
		
		db.execSQL(updateStatementsQuery);
	}
	
	//Not used any where.
	/*public void addTag(Tag tag, Activity activity) {

		String colorCode = tag.getColorCode();
		String name = tag.getName();
		//int sequenceId = tag.getSequenceId();
		long userId = tag.getUserId();

		DatabaseHandler databaseHandler = DatabaseHandler
				.getDatabaseHandlerInstance(activity);

		SQLiteDatabase db = databaseHandler.getWritableDatabase();

		ContentValues values = new ContentValues();

		values.put("colorCode", colorCode);
		values.put("name", name);
		//values.put("sequenceId", sequenceId);
		values.put("userId", userId);
		// Inserting Row
		long rowId = db.insert(TABLE_TAG, null, values);

		// db.close(); // Closing database connection
	}*/

	public void deleteTag(Activity activity, long tagId, long userId) {
		DatabaseHandler databaseHandler = DatabaseHandler
				.getDatabaseHandlerInstance(activity);

		SQLiteDatabase db = databaseHandler.getWritableDatabase();

		String deleteQuery = "DELETE FROM  tags where tagId = "+tagId+" and userId="+userId;

		db.execSQL(deleteQuery);
	}

	public void deleteAllTags(Activity activity, long userId) {
		DatabaseHandler databaseHandler = DatabaseHandler
				.getDatabaseHandlerInstance(activity);

		SQLiteDatabase db = databaseHandler.getWritableDatabase();

		String deleteQuery = "DELETE FROM  tags where userId=" + userId;

		db.execSQL(deleteQuery);
	}

	/*public void updateTag(String sequenceId, String tagName, Activity activity) {
		DatabaseHandler databaseHandler = DatabaseHandler
				.getDatabaseHandlerInstance(activity);
		SQLiteDatabase db = databaseHandler.getWritableDatabase();

		ContentValues values = new ContentValues();
		// values.put("colorCode", tag.getColorCode());
		values.put("name", tagName);

		// updating row
		db.update(TABLE_TAG, values, "sequenceId = ?",
				new String[] { String.valueOf(sequenceId) });
	}*/

	public static List<Tag> getAllTags(Context activity, long userId) {
		List<Tag> tagsList = new ArrayList<Tag>();

		DatabaseHandler databaseHandler = DatabaseHandler
				.getDatabaseHandlerInstance(activity);

		SQLiteDatabase db = databaseHandler.getReadableDatabase();

		/*String query = "select tagId, colorCode, name, sequenceId from Tags where userId="
				+ userId + " order by sequenceId";*/
		
		String query = "select t.tagId, t.colorCode, t.name from Tags t, TagColorSequence tcs  where t.userId="
				+ userId + " and t.colorCode=tcs.colorCode order by tcs.sequenceId";
		
		
		Cursor cursor = db.rawQuery(query, null);

		Tag tag = null;
		// offersListToExpand = new LinkedHashMap<String, ArrayList<Offer>>();

		if (cursor.moveToFirst()) {
			do {
				tag = new Tag();

				tag.setTagId(cursor.getInt(0));
				tag.setColorCode(cursor.getString(1));
				tag.setName(cursor.getString(2));
				//tag.setSequenceId(cursor.getInt(3));

				tagsList.add(tag);

			} while (cursor.moveToNext());
		}
		cursor.close();

		return tagsList;
		// offersListToExpand.put("ALL OFFERS", offersList);
	}

	public void updateOrInsertTag(String tagName, String colorCode,
			Context activity, long userId) {

		DatabaseHandler databaseHandler = DatabaseHandler
				.getDatabaseHandlerInstance(activity);

		SQLiteDatabase db = databaseHandler.getWritableDatabase();

		if (isTagExistsWithColorCode(colorCode, activity, userId)) {
			String updateQuery = "update " + TABLE_TAG + " set name = '"
					+ tagName + "' where colorCode='" + colorCode
					+ "' and userId=" + userId;
			db.execSQL(updateQuery);
		} else {
			//int nextSequenceId = getNextSequenceId(activity, userId);
			int nextTagId = getNextTagId(activity, userId);
			
			ContentValues values = new ContentValues();
			
			values.put("tagId", nextTagId);
			values.put("colorCode", colorCode);
			values.put("name", tagName);
			//values.put("sequenceId", nextSequenceId);
			values.put("userId", userId);

			// Inserting Row
			long rowId = db.insert(TABLE_TAG, null, values);
		}

	}

	/*public int getNextSequenceId(Activity activity, long userId) {
		DatabaseHandler databaseHandler = DatabaseHandler
				.getDatabaseHandlerInstance(activity);

		SQLiteDatabase db = databaseHandler.getReadableDatabase();

		String query = "select max(sequenceId) from Tags where userId="
				+ userId;
		Cursor cursor = db.rawQuery(query, null);

		int maxSequenceId = 0;

		if (cursor.moveToFirst()) {
			maxSequenceId = cursor.getInt(0);
		}
		cursor.close();

		return maxSequenceId + 1;
	}*/

	
	public int getNextTagId(Context activity, long userId) {
		DatabaseHandler databaseHandler = DatabaseHandler
				.getDatabaseHandlerInstance(activity);

		SQLiteDatabase db = databaseHandler.getReadableDatabase();

		String query = "select max(tagId) from Tags where userId="
				+ userId;
		Cursor cursor = db.rawQuery(query, null);

		int maxTagId = 0;

		if (cursor.moveToFirst()) {
			maxTagId = cursor.getInt(0);
		}
		cursor.close();

		return maxTagId + 1;
	}
	
	
	public boolean isTagExistsWithColorCode(String colorCode,
			Context activity, long userId) {
		DatabaseHandler databaseHandler = DatabaseHandler
				.getDatabaseHandlerInstance(activity);

		SQLiteDatabase db = databaseHandler.getReadableDatabase();

		boolean tagExists = false;
		String query = "select tagId from Tags where name<>'' and colorCode = '"
				+ colorCode + "' and userId=" + userId;
		Cursor cursor = db.rawQuery(query, null);

		if (cursor.moveToFirst()) {
			tagExists = true;
		}
		cursor.close();

		return tagExists;
	}

	public int getTagIdWithColorCode(String colorCode, Activity activity,
			long userId) {

		DatabaseHandler databaseHandler = DatabaseHandler
				.getDatabaseHandlerInstance(activity);

		SQLiteDatabase db = databaseHandler.getReadableDatabase();

		String query = "select tagId from Tags where name<>'' and colorCode = '"
				+ colorCode + "' and userId=" + userId;
		Cursor cursor = db.rawQuery(query, null);

		int tagId = 0;

		if (cursor.moveToFirst()) {
			tagId = cursor.getInt(0);
		}
		cursor.close();

		return tagId;
	}


	public int getTagIdWithTagName(String tagName, Activity activity,
			long userId) {

		DatabaseHandler databaseHandler = DatabaseHandler
				.getDatabaseHandlerInstance(activity);

		SQLiteDatabase db = databaseHandler.getReadableDatabase();

		String query = "select tagId from Tags where name='"+tagName +"' and userId=" + userId;
		Cursor cursor = db.rawQuery(query, null);

		int tagId = 0;

		if (cursor.moveToFirst()) {
			tagId = cursor.getInt(0);
		}
		cursor.close();

		return tagId;
	}
	
	
	
	public String getColorCode(int tagId, Activity activity, long userId) {
		List<Tag> tagsList = new ArrayList<Tag>();

		DatabaseHandler databaseHandler = DatabaseHandler
				.getDatabaseHandlerInstance(activity);

		SQLiteDatabase db = databaseHandler.getReadableDatabase();

		String colorCode = "";
		String query = "select colorCode from Tags where tagId = '" + tagId
				+ "' and name<>'' and userId="+userId;
		Cursor cursor = db.rawQuery(query, null);

		if (cursor.moveToFirst()) {
			colorCode = cursor.getString(0);
		} else {
			colorCode = "#FFFFFF";
		}
		cursor.close();

		return colorCode;
	}

	public static void deleteOffersAndStatementTags(long tagId, Activity activity,
			long userId) {
		DatabaseHandler databaseHandler = DatabaseHandler
				.getDatabaseHandlerInstance(activity);

		SQLiteDatabase db = databaseHandler.getWritableDatabase();

		String updateStatementsQuery = "update statements set tagId = 0 where tagId="
				+ tagId + " and userId=" + userId;
		String updateOffersServiceQuery = "update offersservice set tagId = 0 where tagId="
				+ tagId + " and userID=" + userId;
		db.execSQL(updateStatementsQuery);
		db.execSQL(updateOffersServiceQuery);
	}
	
	
	public static void deleteAllOffersAndStatementTags(Context activity,
			long userId) {
		DatabaseHandler databaseHandler = DatabaseHandler
				.getDatabaseHandlerInstance(activity);

		SQLiteDatabase db = databaseHandler.getWritableDatabase();

		String updateStatementsQuery = "update statements set tagId = 0 where userId=" + userId;
		String updateOffersServiceQuery = "update offersservice set tagId = 0 where userID=" + userId;
		db.execSQL(updateStatementsQuery);
		db.execSQL(updateOffersServiceQuery);
	}

	

	/*public long getTagIdWithSequenceId(String sequenceId, Activity activity,
			long userId) {

		DatabaseHandler databaseHandler = DatabaseHandler
				.getDatabaseHandlerInstance(activity);

		SQLiteDatabase db = databaseHandler.getReadableDatabase();

		String query = "select tagId from Tags where sequenceId = '"
				+ sequenceId + "' and userId=" + userId;
		Cursor cursor = db.rawQuery(query, null);

		long tagId = 0;

		if (cursor.moveToFirst()) {
			tagId = cursor.getLong(0);
		}
		cursor.close();

		return tagId;
	}*/

}
