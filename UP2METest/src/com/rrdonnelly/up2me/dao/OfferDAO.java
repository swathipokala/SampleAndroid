package com.rrdonnelly.up2me.dao;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.rrdonnelly.up2me.json.Offer;
import com.rrdonnelly.up2me.util.ImageUtil;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;
import android.util.Log;

public class OfferDAO {

	// All Static variables
	private static final String TABLE_OFFERS_SERVICE = "OffersService";
	private static final String TABLE_OFFERS_ZIP_CODES = "OffersZipCodes";

	public void refresh(Activity activity) {
		deleteOffersService(activity);
		deleteOffersZipCodes(activity);
	}

	public void deleteOffersService(Activity activity) {
		DatabaseHandler databaseHandler = DatabaseHandler
				.getDatabaseHandlerInstance(activity);

		SQLiteDatabase db = databaseHandler.getWritableDatabase();
		db.delete(TABLE_OFFERS_SERVICE, null, null);
		// db.close();
	}

	public void deleteOffersZipCodes(Activity activity) {
		DatabaseHandler databaseHandler = DatabaseHandler
				.getDatabaseHandlerInstance(activity);

		SQLiteDatabase db = databaseHandler.getWritableDatabase();
		db.delete(TABLE_OFFERS_ZIP_CODES, null, null);
		// db.close();
	}

	public void addOffer(Offer offer, long loginUserId, Context activity) {

		Long offerID = offer.getId();
		// List<String> validZipCodes;
		String name = offer.getName();
		String expiresOnDisplayDate = offer.getExpiresOnDisplayDate();
		boolean isRead = offer.isRead();
		String barcodeImagePath = offer.getBarcodeImagePath().getPath();
		String barcodeImagePathAltText = offer.getBarcodeImagePath()
				.getAltText();
		String offerImagePath = offer.getOfferImagePath().getPath();
		String offerImagePathAltText = offer.getOfferImagePath().getAltText();
		String teaserImagePath = offer.getTeaserImagePath().getPath();
		String teaserImagePathAltText = offer.getTeaserImagePath().getAltText();
		String displayImagePathSmall = offer.getDisplayImagePathSmall()
				.getPath();
		String displayImagePathSmallAltText = offer.getDisplayImagePathSmall()
				.getAltText();
		String offerText = offer.getOfferText();
		long createdDate = offer.getCreatedDate();
		// long userId = 7l;

		DatabaseHandler databaseHandler = DatabaseHandler
				.getDatabaseHandlerInstance(activity);

		SQLiteDatabase db = databaseHandler.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put("offerID", offerID);
		values.put("name", name);

		values.put("expiresOnDisplayDate", expiresOnDisplayDate);
		values.put("isRead", isRead);

		values.put("barcodeImagePath", barcodeImagePath);
		values.put("barcodeImagePathAltText", barcodeImagePathAltText);

		values.put("offerImagePath", offerImagePath);
		values.put("offerImagePathAltText", offerImagePathAltText);

		values.put("teaserImagePath", teaserImagePath);
		values.put("teaserImagePathAltText", teaserImagePathAltText);

		values.put("displayImagePathSmall", displayImagePathSmall);
		values.put("displayImagePathSmallAltText", displayImagePathSmallAltText);

		values.put("offerText", offerText);
		values.put("createdDate", createdDate);
		values.put("userID", loginUserId);
		// Inserting Row
		long rowId = db.insert(TABLE_OFFERS_SERVICE, null, values);

		// db.close(); // Closing database connection

		addOfferValidZipCodesList(offer.getValidZipCodes(), offer.getId(),
				loginUserId, activity);
	}

	private void addOfferValidZipCodesList(List<String> validZipCodesList,
			long offerId, long userId, Context activity) {
		for (String validZipCode : validZipCodesList) {
			addOfferValidZipCode(validZipCode, offerId, userId, activity);
		}
	}

	private void addOfferValidZipCode(String validZipCode, long offerId,
			long userId, Context activity) {

		DatabaseHandler databaseHandler = DatabaseHandler
				.getDatabaseHandlerInstance(activity);

		SQLiteDatabase db = databaseHandler.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put("zipCode", validZipCode);
		values.put("offerID", offerId);
		values.put("userID", userId);

		// Inserting Row
		db.insert(TABLE_OFFERS_ZIP_CODES, null, values);

		// db.close(); // Closing database connection
	}

	public List<Offer> getAllOffers(Context activity, long loginUserId,
			String sortBY) {
		List<Offer> offersList = new ArrayList<Offer>();

		DatabaseHandler databaseHandler = DatabaseHandler
				.getDatabaseHandlerInstance(activity);

		SQLiteDatabase db = databaseHandler.getReadableDatabase();

		/*
		 * String countQryForUserOfferProviders =
		 * "Select * from userOfferProviders where userid = " + loginUserId;
		 * Cursor countCursor = db.rawQuery(countQryForUserOfferProviders,
		 * null);
		 * 
		 * String query = ""; if(countCursor.getCount() > 0) {
		 */
		String query = "select offerId,name, displayImagePathSmall, offerImagePath, teaserImagePath,expiresOnDisplayDate,offerText,isread,tagId from offersservice os, offerproviders, userofferproviders "
				+ "where os.name = offerproviders.providername and offerproviders.providerid = userofferproviders.providerid and os.userID = userofferproviders.userId and os.userID = "
				+ loginUserId + " " + sortBY;
		/*
		 * } else { query =
		 * "select offerId,name, displayImagePathSmall, offerImagePath, teaserImagePath,expiresOnDisplayDate,offerText,isread,tagId from OffersService where userID="
		 * +loginUserId+" "+sortBY; }
		 */
		// String query =
		// "select offerId,name, displayImagePathSmall, offerImagePath, teaserImagePath,expiresOnDisplayDate,offerText,isread from OffersService where userID="+loginUserId+" "+sortBY;

		Cursor cursor = db.rawQuery(query, null);

		Offer offer = null;

		if (cursor.moveToFirst()) {
			do {
				offer = new Offer();

				offer.setId(Long.parseLong(cursor.getString(0)));
				offer.setName(cursor.getString(1));

				String displayImagePathSmall = cursor.getString(2);

				String displayImagePathSmallfileName = displayImagePathSmall
						.substring(displayImagePathSmall
								.lastIndexOf("/") + 1);
				File fdisplayImagePathSmall = new File(activity.getFilesDir(),
						displayImagePathSmallfileName);
				if (!fdisplayImagePathSmall.exists()) {
					Log.w("OffersService", "Calling Download Image for file :"
							+ displayImagePathSmallfileName);
					offer.setDisplayImagePathSmallLocal(ImageUtil
							.downloadImage(activity, displayImagePathSmall,
									displayImagePathSmallfileName));
				} else {
					offer.setDisplayImagePathSmallLocal(fdisplayImagePathSmall
							.getAbsolutePath());
				}

				String offerImagePath = cursor.getString(3);
				String offerImagePathfileName = offerImagePath
						.substring(offerImagePath.lastIndexOf("/") + 1);

				File fofferImagePath = new File(activity.getFilesDir(),
						offerImagePathfileName);
				if (!fofferImagePath.exists()) {
					Log.w("OffersService", "Calling Download Image for file :"
							+ offerImagePathfileName);
					offer.setOfferImagePathLocal(ImageUtil.downloadImage(
							activity, offerImagePath, offerImagePathfileName));
				} else {
					offer.setOfferImagePathLocal(fofferImagePath
							.getAbsolutePath());
				}

				String teaserImagePath = cursor.getString(4);
				String teaserImagePathfileName = teaserImagePath
						.substring(teaserImagePath.lastIndexOf("/") + 1);
				File fteaserImagePath = new File(activity.getFilesDir(),
						teaserImagePathfileName);
				if (!fteaserImagePath.exists()) {
					Log.w("OffersService", "Calling Download Image for file :"
							+ teaserImagePathfileName);
					offer.setTeaserImagePathLocal(ImageUtil.downloadImage(
							activity, teaserImagePath, teaserImagePathfileName));
				} else {
					offer.setTeaserImagePathLocal(fteaserImagePath
							.getAbsolutePath());
				}

				String offerText = cursor.getString(6);
				offer.setOfferText(offerText);
				offer.setExpiresOnDisplayDate(cursor.getString(5));
				offer.setRead(cursor.getString(7).equals("1"));
				offer.setTagId(cursor.getInt(8));
				offersList.add(offer);

			} while (cursor.moveToNext());
		}
		cursor.close();

		return offersList;
	}

	public List<Offer> getAllDirtyOffers(Context activity, long loginUserId,
			String sortBY) {
		List<Offer> offersList = new ArrayList<Offer>();

		DatabaseHandler databaseHandler = DatabaseHandler
				.getDatabaseHandlerInstance(activity);

		SQLiteDatabase db = databaseHandler.getReadableDatabase();

		String countQryForUserOfferProviders = "Select * from userOfferProviders where userid = "
				+ loginUserId;
		Cursor countCursor = db.rawQuery(countQryForUserOfferProviders, null);

		String query = "";
		if (countCursor.getCount() > 0) {
			query = "select offerId,name, displayImagePathSmall, offerImagePath, teaserImagePath,expiresOnDisplayDate,offerText,isread,tagId from offersservice os, offerproviders, userofferproviders "
					+ "where os.name = offerproviders.providername and offerproviders.providerid = userofferproviders.providerid and os.userID = userofferproviders.userId and os.userID = "
					+ loginUserId + " and os.isDirty=1" + sortBY;
		} else {
			query = "select offerId,name, displayImagePathSmall, offerImagePath, teaserImagePath,expiresOnDisplayDate,offerText,isread,tagId from OffersService where userID="
					+ loginUserId + " and isDirty=1" + sortBY;
		}
		// String query =
		// "select offerId,name, displayImagePathSmall, offerImagePath, teaserImagePath,expiresOnDisplayDate,offerText,isread from OffersService where userID="+loginUserId+" "+sortBY;

		Cursor cursor = db.rawQuery(query, null);

		Offer offer = null;

		if (cursor.moveToFirst()) {
			do {
				offer = new Offer();

				offer.setId(Long.parseLong(cursor.getString(0)));
				offer.setName(cursor.getString(1));

				/*
				 * String displayImagePathSmall = cursor.getString(2);
				 * 
				 * String displayImagePathSmallfileName = displayImagePathSmall
				 * .substring(displayImagePathSmall.lastIndexOf("imageName=") +
				 * 10); File fdisplayImagePathSmall = new
				 * File(activity.getFilesDir(), displayImagePathSmallfileName);
				 * if (!fdisplayImagePathSmall.exists()) {
				 * Log.w("OffersService", "Calling Download Image for file :" +
				 * displayImagePathSmallfileName);
				 * offer.setDisplayImagePathSmallLocal
				 * (ImageUtil.downloadImage(activity, displayImagePathSmall,
				 * displayImagePathSmallfileName)); } else {
				 * offer.setDisplayImagePathSmallLocal
				 * (fdisplayImagePathSmall.getAbsolutePath()); }
				 * 
				 * String offerImagePath = cursor.getString(3); String
				 * offerImagePathfileName = offerImagePath
				 * .substring(offerImagePath.lastIndexOf("imageName=") + 10);
				 * 
				 * File fofferImagePath = new File(activity.getFilesDir(),
				 * offerImagePathfileName); if (!fofferImagePath.exists()) {
				 * Log.w("OffersService", "Calling Download Image for file :" +
				 * offerImagePathfileName);
				 * offer.setOfferImagePathLocal(ImageUtil
				 * .downloadImage(activity, offerImagePath,
				 * offerImagePathfileName)); } else {
				 * offer.setOfferImagePathLocal
				 * (fofferImagePath.getAbsolutePath()); }
				 * 
				 * String teaserImagePath = cursor.getString(4); String
				 * teaserImagePathfileName = teaserImagePath
				 * .substring(teaserImagePath.lastIndexOf("imageName=") + 10);
				 * File fteaserImagePath = new File( activity.getFilesDir(),
				 * teaserImagePathfileName); if (!fteaserImagePath.exists()) {
				 * Log.w("OffersService", "Calling Download Image for file :" +
				 * teaserImagePathfileName);
				 * offer.setTeaserImagePathLocal(ImageUtil
				 * .downloadImage(activity, teaserImagePath,
				 * teaserImagePathfileName)); } else {
				 * offer.setTeaserImagePathLocal
				 * (fteaserImagePath.getAbsolutePath()); }
				 * 
				 * String offerText = cursor.getString(6);
				 * offer.setOfferText(offerText);
				 * offer.setExpiresOnDisplayDate(cursor.getString(5));
				 */
				offer.setRead(cursor.getString(7).equals("1"));
				offer.setTagId(cursor.getInt(8));
				offersList.add(offer);

			} while (cursor.moveToNext());
		}
		cursor.close();

		return offersList;
	}

	public int getTagId(Activity activity, long loginUserId, long offerId) {
		int tagId = 0;
		DatabaseHandler databaseHandler = DatabaseHandler
				.getDatabaseHandlerInstance(activity);
		SQLiteDatabase db = databaseHandler.getReadableDatabase();

		String query = "select tagId from OffersService where userID="
				+ loginUserId + " and offerId = " + offerId;

		Cursor cursor = db.rawQuery(query, null);

		if (cursor.moveToFirst()) {
			tagId = cursor.getInt(0);
		}
		cursor.close();

		return tagId;
	}

	public int updateRead(long id, int read, Activity activity, int isDirty) {
		DatabaseHandler databaseHandler = DatabaseHandler
				.getDatabaseHandlerInstance(activity);
		SQLiteDatabase db = databaseHandler.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put("isRead", read);
		values.put("isDirty", isDirty);

		// updating row
		return db.update(TABLE_OFFERS_SERVICE, values, "offerID = ?",
				new String[] { String.valueOf(id) });
	}

	public com.rrdonnelly.up2me.valueobjects.Offer getOfferByDescription(
			Activity currentActivity, String offerId, String offerDescription,
			long loginUserId) {

		DatabaseHandler databaseHandler = DatabaseHandler
				.getDatabaseHandlerInstance(currentActivity);

		SQLiteDatabase db = databaseHandler.getReadableDatabase();
		Cursor cursor;
		if (offerId != null) {
			cursor = db.query("OffersService", new String[] { "offerId",
					"name", "displayImagePathSmall", "offerImagePath",
					"expiresOnDisplayDate", "offerText" },
					"offerID=? and userId=?",
					new String[] { offerId, String.valueOf(loginUserId) },
					null, null, "offerId", null);
		} else {
			cursor = db.query("OffersService", new String[] { "offerId",
					"name", "displayImagePathSmall", "offerImagePath",
					"expiresOnDisplayDate", "offerText" },
					"name=? and userId=?", new String[] { offerDescription,
							String.valueOf(loginUserId) }, null, null,
					"offerId", null);
		}

		com.rrdonnelly.up2me.valueobjects.Offer offer = new com.rrdonnelly.up2me.valueobjects.Offer();
		if (cursor != null) {
			cursor.moveToFirst();
			offer.setName(cursor.getString(1));

			String file = cursor.getString(2);
			String fileName = file
					.substring(file.lastIndexOf("/") + 1);

			String extStorageDirectory = Environment
					.getExternalStorageDirectory().toString();

			File folder = new File(extStorageDirectory, "Document");
			if (!folder.exists()) {
				folder.mkdir();
			}

			File f = new File(folder.getAbsolutePath(), fileName);
			if (!f.exists()) {
				offer.setOfferImagePath(ImageUtil.downloadImagetoExternalPath(
						currentActivity, file, fileName));
			} else {
				offer.setOfferImagePath(f.getAbsolutePath());
			}
			// offer.set = cursor.getString(3);
			offer.setExpiresOnDisplayDate(cursor.getString(4));
			offer.setStatementText(cursor.getString(5));
		}
		cursor.close();

		return offer;
	}

	public ArrayList<com.rrdonnelly.up2me.valueobjects.Offer> getUserAllOffers(
			Activity currentActivity, String offerDescription, long userID, String sortBY) {
		DatabaseHandler databaseHandler = DatabaseHandler
				.getDatabaseHandlerInstance(currentActivity);
		SQLiteDatabase db = databaseHandler.getReadableDatabase();
		Cursor cursor = db.query("OffersService", new String[] { "offerId",
				"name", "displayImagePathSmall", "offerImagePath",
				"teaserImagePath", "expiresOnDisplayDate", "offerText" },
				"name" + "=?  and userID=?  ", new String[] { offerDescription,userID+"" }, null, null,
				sortBY, null);
		com.rrdonnelly.up2me.valueobjects.Offer offer = null;
		// offersListToExpand = new LinkedHashMap<String, ArrayList<Offer>>();
		ArrayList<com.rrdonnelly.up2me.valueobjects.Offer> offersList = new ArrayList<com.rrdonnelly.up2me.valueobjects.Offer>();

		if (cursor.moveToFirst()) {
			do {
				offer = new com.rrdonnelly.up2me.valueobjects.Offer();

				offer.setId(cursor.getString(0));
				offer.setName(cursor.getString(1));

				String displayImagePathSmall = cursor.getString(2);
				String displayImagePathSmallfileName = displayImagePathSmall
						.substring(displayImagePathSmall
								.lastIndexOf("/") + 1);
				File fdisplayImagePathSmall = new File(currentActivity
						.getBaseContext().getFilesDir(),
						displayImagePathSmallfileName);
				if (!fdisplayImagePathSmall.exists()) {
					Log.w("OffersService", "Calling Download Image for file :"
							+ displayImagePathSmallfileName);
					offer.setImageURL(ImageUtil.downloadImage(currentActivity,
							displayImagePathSmall,
							displayImagePathSmallfileName));
				} else {
					offer.setImageURL(fdisplayImagePathSmall.getAbsolutePath());
				}

				String offerImagePath = cursor.getString(3);
				String offerImagePathfileName = offerImagePath
						.substring(offerImagePath.lastIndexOf("/") + 1);
				String extStorageDirectory = Environment
						.getExternalStorageDirectory().toString();
				File folder = new File(extStorageDirectory, "Document");
				if (!folder.exists()) {
					folder.mkdir();
				}
				File fofferImagePath = new File(folder.getAbsolutePath(),
						offerImagePathfileName);
				if (!fofferImagePath.exists()) {
					Log.w("OffersService", "Calling Download Image for file :"
							+ offerImagePathfileName);
					offer.setOfferImagePath(ImageUtil
							.downloadImagetoExternalPath(currentActivity,
									offerImagePath, offerImagePathfileName));
				} else {
					offer.setOfferImagePath(fofferImagePath.getAbsolutePath());
				}

				String teaserImagePath = cursor.getString(4);
				String teaserImagePathfileName = teaserImagePath
						.substring(teaserImagePath.lastIndexOf("/") + 1);
				File fteaserImagePath = new File(currentActivity
						.getBaseContext().getFilesDir(),
						teaserImagePathfileName);
				if (!fteaserImagePath.exists()) {
					Log.w("OffersService", "Calling Download Image for file :"
							+ teaserImagePathfileName);
					offer.setTeaserImagePath(ImageUtil.downloadImage(
							currentActivity, teaserImagePath,
							teaserImagePathfileName));
				} else {
					offer.setTeaserImagePath(fteaserImagePath.getAbsolutePath());
				}
				offer.setExpiresOnDisplayDate(cursor.getString(5));
				offer.setStatementText(cursor.getString(6));

				offersList.add(offer);

			} while (cursor.moveToNext());
		}
		cursor.close();

		return offersList;
	}

	public ArrayList<com.rrdonnelly.up2me.valueobjects.Offer> getUserAllOffersNearByMe(
			Activity currentActivity, String offerDescription, String postalCode, long userId) {
		DatabaseHandler databaseHandler = DatabaseHandler
				.getDatabaseHandlerInstance(currentActivity);
		SQLiteDatabase db = databaseHandler.getReadableDatabase();

		String query = "select OffersService.offerId,name, displayImagePathSmall, offerImagePath, teaserImagePath,expiresOnDisplayDate, offerText from OffersService, OffersZipCodes "
				+ " where OffersService.offerId =  OffersZipCodes.offerId  and name = '"
				+ offerDescription
				+ "' and offersZipCodes.zipCode='"
				+ postalCode + "'  and OffersService.userID="+userId ;

		Cursor cursor = db.rawQuery(query, null);

		com.rrdonnelly.up2me.valueobjects.Offer offer = null;
		ArrayList<com.rrdonnelly.up2me.valueobjects.Offer> offersNearByMeList = new ArrayList<com.rrdonnelly.up2me.valueobjects.Offer>();

		if (cursor.moveToFirst()) {
			do {
				offer = new com.rrdonnelly.up2me.valueobjects.Offer();

				offer.setId(cursor.getString(0));
				offer.setName(cursor.getString(1));

				String displayImagePathSmall = cursor.getString(2);
				String displayImagePathSmallfileName = displayImagePathSmall
						.substring(displayImagePathSmall
								.lastIndexOf("/") + 1);
				File fdisplayImagePathSmall = new File(currentActivity
						.getBaseContext().getFilesDir(),
						displayImagePathSmallfileName);
				if (!fdisplayImagePathSmall.exists()) {
					Log.w("OffersService", "Calling Download Image for file :"
							+ displayImagePathSmallfileName);
					offer.setImageURL(ImageUtil.downloadImage(currentActivity,
							displayImagePathSmall,
							displayImagePathSmallfileName));
				} else {
					offer.setImageURL(fdisplayImagePathSmall.getAbsolutePath());
				}

				String offerImagePath = cursor.getString(3);
				String offerImagePathfileName = offerImagePath
						.substring(offerImagePath.lastIndexOf("/") + 1);
				String extStorageDirectory = Environment
						.getExternalStorageDirectory().toString();

				File folder = new File(extStorageDirectory, "Document");
				if (!folder.exists()) {
					folder.mkdir();
				}
				File fofferImagePath = new File(folder.getAbsolutePath(),
						offerImagePathfileName);
				if (!fofferImagePath.exists()) {
					Log.w("OffersService", "Calling Download Image for file :"
							+ offerImagePathfileName);
					offer.setOfferImagePath(ImageUtil
							.downloadImagetoExternalPath(currentActivity,
									offerImagePath, offerImagePathfileName));
				} else {
					offer.setOfferImagePath(fofferImagePath.getAbsolutePath());
				}

				String teaserImagePath = cursor.getString(4);
				String teaserImagePathfileName = teaserImagePath
						.substring(teaserImagePath.lastIndexOf("/") + 1);
				File fteaserImagePath = new File(currentActivity
						.getBaseContext().getFilesDir(),
						teaserImagePathfileName);
				if (!fteaserImagePath.exists()) {
					Log.w("OffersService", "Calling Download Image for file :"
							+ teaserImagePathfileName);
					offer.setTeaserImagePath(ImageUtil.downloadImage(
							currentActivity, teaserImagePath,
							teaserImagePathfileName));
				} else {
					offer.setTeaserImagePath(fteaserImagePath.getAbsolutePath());
				}
				offer.setExpiresOnDisplayDate(cursor.getString(5));
				offer.setStatementText(cursor.getString(6));

				offersNearByMeList.add(offer);

			} while (cursor.moveToNext());
		}
		cursor.close();

		return offersNearByMeList;
	}

	public void setTagToOffer(long offerId, long userId, int tagId,
			Activity activity) {
		/*
		 * DatabaseHandler databaseHandler =
		 * DatabaseHandler.getDatabaseHandlerInstance(activity); SQLiteDatabase
		 * db = databaseHandler.getWritableDatabase();
		 * 
		 * ContentValues values = new ContentValues(); values.put("tagId",
		 * tagId);
		 * 
		 * // updating row return db.update(TABLE_OFFERS_SERVICE, values,
		 * "offerID = ?", new String[] { String.valueOf(id) });
		 */

		DatabaseHandler databaseHandler = DatabaseHandler
				.getDatabaseHandlerInstance(activity);

		SQLiteDatabase db = databaseHandler.getWritableDatabase();

		String updateQuery = "update " + TABLE_OFFERS_SERVICE + " set tagId = "
				+ tagId + " where offerId=" + offerId + " and userID=" + userId;

		db.execSQL(updateQuery);

	}

	public void resetTagToOffer(long offerId, long userId, Activity activity) {

		DatabaseHandler databaseHandler = DatabaseHandler
				.getDatabaseHandlerInstance(activity);

		SQLiteDatabase db = databaseHandler.getWritableDatabase();

		String updateQuery = "update " + TABLE_OFFERS_SERVICE
				+ " set tagId = 0 where offerId=" + offerId + " and userID="
				+ userId;

		db.execSQL(updateQuery);

	}

	public boolean tagExistsUserIdAndOfferId(long userId, long offerId,
			Activity activity) {

		DatabaseHandler databaseHandler = DatabaseHandler
				.getDatabaseHandlerInstance(activity);
		SQLiteDatabase db = databaseHandler.getReadableDatabase();

		String countQuery = "SELECT  tagId FROM " + TABLE_OFFERS_SERVICE
				+ " WHERE userId=" + userId + " AND offerID=" + offerId;
		// Cursor cursor = db.rawQuery(countQuery, null);
		boolean isTagExists = false;

		Cursor cursor = db.rawQuery(countQuery, null);
		int tagId = 0;
		if (cursor.moveToFirst()) {
			tagId = cursor.getInt(0);
			if (tagId != 0) {
				isTagExists = true;
			}
		}

		cursor.close();

		return isTagExists;
	}

	public Tag getTagWithUserIdAndOfferId(long userId, long offerId,
			Activity activity) {

		DatabaseHandler databaseHandler = DatabaseHandler
				.getDatabaseHandlerInstance(activity);
		SQLiteDatabase db = databaseHandler.getReadableDatabase();

		/*String query = "select t.tagId, t.colorCode,t.name,t.sequenceId,t.userId from tags t, OffersService os where t.tagId = os.tagid and t.userId=os.userId and os.userId="
				+ userId + " and os.offerID=" + offerId;
*/

		
		String query = "select t.tagId, t.colorCode,t.name,t.userId from tags t, OffersService os where t.tagId = os.tagid and t.userId=os.userId and os.userId="
				+ userId + " and os.offerID=" + offerId;

		
		Cursor cursor = db.rawQuery(query, null);

		long tagId = 0;
		String colorCode = "";
		String name = "";
		//int sequenceId = 0;

		if (cursor.moveToFirst()) {
			tagId = cursor.getLong(0);
			colorCode = cursor.getString(0);
			name = cursor.getString(2);
			//sequenceId = cursor.getInt(3);
		}

		cursor.close();

		Tag tag = new Tag();
		tag.setTagId(tagId);
		tag.setColorCode(colorCode);
		tag.setName(name);
		//tag.setSequenceId(sequenceId);

		return tag;
	}

	public int getTagIdFromOffer(String offerId, Activity activity, long userId) {
		List<Tag> tagsList = new ArrayList<Tag>();

		DatabaseHandler databaseHandler = DatabaseHandler
				.getDatabaseHandlerInstance(activity);

		SQLiteDatabase db = databaseHandler.getReadableDatabase();

		int tagId = 0;
		String query = "select tagId from OffersService where offerId = '"
				+ offerId + "' and userID=" + userId;
		Cursor cursor = db.rawQuery(query, null);

		if (cursor.moveToFirst()) {
			tagId = cursor.getInt(0);
		}

		cursor.close();

		return tagId;
	}

	public Offer getOfferByID(Activity activity, long offerId) {

		DatabaseHandler databaseHandler = DatabaseHandler
				.getDatabaseHandlerInstance(activity);
		SQLiteDatabase db = databaseHandler.getReadableDatabase();

		Cursor cursor = db.rawQuery("SELECT offerID,isRead FROM "
				+ TABLE_OFFERS_SERVICE + " where offerID = " + offerId, null);
		Offer offer = null;
		if (cursor.moveToFirst()) {
			do {

				Long id = Long.parseLong(cursor.getString(0));
				offer = new Offer();
				offer.setId(offerId);
				offer.setRead(Boolean.valueOf(cursor.getString(1).equals("1")));
			} while (cursor.moveToNext());
		}
		cursor.close();

		return offer;
	}

	public void deleteOffer(Activity activity, long offerId, long userId) {

		if (offerId > 0 && userId > 0) {
			DatabaseHandler databaseHandler = DatabaseHandler
					.getDatabaseHandlerInstance(activity);

			SQLiteDatabase db = databaseHandler.getWritableDatabase();

			String deleteOffersZipCodesQuery = "delete from "
					+ TABLE_OFFERS_ZIP_CODES + " where offerID = " + offerId
					+ " and userID=" + userId;
			String deleteOffersServiceQuery = "delete from "
					+ TABLE_OFFERS_SERVICE + " where offerID = " + offerId
					+ " and userID=" + userId;

			db.execSQL(deleteOffersZipCodesQuery);
			db.execSQL(deleteOffersServiceQuery);
		}
	}

	public int updateIsDirty(long id, Activity activity, int isDirty) {
		DatabaseHandler databaseHandler = DatabaseHandler
				.getDatabaseHandlerInstance(activity);
		SQLiteDatabase db = databaseHandler.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put("isDirty", isDirty);

		// updating row
		return db.update(TABLE_OFFERS_SERVICE, values, "offerID = ?",
				new String[] { String.valueOf(id) });
	}

	public static void deleteOffers(String offerIds, Context activity,
			long userId) {
		if (offerIds != null && offerIds.length() > 0) {
			DatabaseHandler databaseHandler = DatabaseHandler
					.getDatabaseHandlerInstance(activity);

			SQLiteDatabase db = databaseHandler.getWritableDatabase();

			String deleteOffersZipCodesQuery = "delete from "
					+ TABLE_OFFERS_ZIP_CODES + " where offerID in(" + offerIds
					+ ") and userID=" + userId;
			String deleteOffersServiceQuery = "delete from "
					+ TABLE_OFFERS_SERVICE + " where offerID in(" + offerIds
					+ ") and userID=" + userId;

			db.execSQL(deleteOffersZipCodesQuery);
			db.execSQL(deleteOffersServiceQuery);
		}
	}

	public static void resetDirtyFlagToFalse(String offerIds,
			Context activity, long userId) {
		if (offerIds != null && offerIds.length() > 0) {
			DatabaseHandler databaseHandler = DatabaseHandler
					.getDatabaseHandlerInstance(activity);

			SQLiteDatabase db = databaseHandler.getWritableDatabase();

			String updateDirtyFlagOffersQuery = "update offersservice set isDirty = 0 where offerID in("
					+ offerIds + ") and userID=" + userId;
			db.execSQL(updateDirtyFlagOffersQuery);
		}
	}


}
