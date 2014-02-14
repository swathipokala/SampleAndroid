package com.rrdonnelly.up2me.services;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;
import com.rrdonnelly.up2me.R;
import com.rrdonnelly.up2me.dao.DocumentProvidersDAO;
import com.rrdonnelly.up2me.dao.OfferDAO;
import com.rrdonnelly.up2me.dao.OfferProvidersDAO;
import com.rrdonnelly.up2me.json.Image;
import com.rrdonnelly.up2me.json.Offer;
import com.rrdonnelly.up2me.json.OfferProviders;
import com.rrdonnelly.up2me.util.ImageUtil;
import com.rrdonnelly.up2me.valueobjects.MarkReadOrUnRead;
import com.rrdonnelly.up2me.valueobjects.UserFavorite;

public class OfferService {

	public static void syncOffers(Context currentActivity, String userToken,
			String userName, String salt, long loginUserId,
			long lastSyncDateTime) throws ClientProtocolException, IOException,
			JSONException {

		HttpClient client = new DefaultHttpClient();
		String webServiceUrl = currentActivity.getResources().getString(
				R.string.webservice_url);

		String offerWebServiceUrl = webServiceUrl + "/user/Offers?userId="
				+ loginUserId;
		// System.out.println("LastSyncDateTime:"+lastSyncDateTime);
		if (lastSyncDateTime != 0l) {
			offerWebServiceUrl += "&date=" + lastSyncDateTime;
		}

		HttpGet request = new HttpGet(offerWebServiceUrl);
		request.addHeader("userName", userName);
		request.addHeader("userToken", userToken);
		request.addHeader("salt", salt);

		HttpResponse response = client.execute(request);
		BufferedReader rd = new BufferedReader(new InputStreamReader(response
				.getEntity().getContent()));
		StringBuilder responseJSONText = new StringBuilder();

		String line = "";
		while ((line = rd.readLine()) != null) {
			responseJSONText.append(line);
		}

		List<Offer> offersList = new ArrayList<Offer>();

		offersList = createOffersList(responseJSONText.toString(),
				currentActivity);

		String offerIds = "";
		for (Offer offer : offersList) {
			offerIds = offerIds + offer.getId() + ",";
		}

		if (offerIds.length() > 0) {
			offerIds = offerIds.substring(0, offerIds.lastIndexOf(","));
		}

		Log.d("Insert: ", "Inserting Statements..");
		OfferDAO offersDAO = new OfferDAO();

		// offersDAO.refresh(currentActivity);

		OfferDAO.deleteOffers(offerIds, currentActivity, loginUserId);

		for (Offer offer : offersList) {
			offersDAO.addOffer(offer, loginUserId, currentActivity);
		}

		Log.d("Insert: ", "Inserting Statements Completed.");

	}

	private static List<Offer> createOffersList(String responseJSONText,
			Context activity) throws JSONException {

		List<Offer> offersList = new ArrayList<Offer>();
		JSONArray offersArray = new JSONArray(responseJSONText);
		for (int i = 0; i < offersArray.length(); i++) {
			JSONObject offerJSON = offersArray.getJSONObject(i);
			Offer offer = new Offer();

			if (offerJSON != null) {

				offer.setId(offerJSON.getLong("id"));
				offer.setName(offerJSON.getString("name"));
				offer.setExpiresOnDisplayDate(offerJSON
						.getString("expiresOnDisplayDate"));
				offer.setOfferText(offerJSON.getString("offerText"));
				offer.setCreatedDate(offerJSON.getLong("createdDate"));
				offer.setRead(offerJSON.getBoolean("read"));

				// Level two statements (Offer -> Image)

				Image barcodeImagePath = new Image();
				Image offerImagePath = new Image();
				Image teaserImagePath = new Image();
				Image displayImagePathSmall = new Image();

				if (offerJSON.getJSONObject("barcodeImagePath") != null) {
					barcodeImagePath.setPath(offerJSON.getJSONObject(
							"barcodeImagePath").getString("path"));
					barcodeImagePath.setAltText(offerJSON.getJSONObject(
							"barcodeImagePath").getString("altText"));
					offer.setBarcodeImagePath(barcodeImagePath);
					//Commented since barcode images are not using anywhere
					/*ImageUtil.downloadImageIfNotExists(activity,
							barcodeImagePath.getPath());*/
				}

				if (offerJSON.getJSONObject("offerImagePath") != null) {
					offerImagePath.setPath(offerJSON.getJSONObject(
							"offerImagePath").getString("path"));
					offerImagePath.setAltText(offerJSON.getJSONObject(
							"offerImagePath").getString("altText"));
					offer.setOfferImagePath(offerImagePath);
					ImageUtil.downloadImageIfNotExists(activity,
							offerImagePath.getPath());
				}

				if (offerJSON.getJSONObject("teaserImagePath") != null) {
					teaserImagePath.setPath(offerJSON.getJSONObject(
							"teaserImagePath").getString("path"));
					teaserImagePath.setAltText(offerJSON.getJSONObject(
							"teaserImagePath").getString("altText"));
					offer.setTeaserImagePath(teaserImagePath);
					ImageUtil.downloadImageIfNotExists(activity,
							teaserImagePath.getPath());

				}

				if (offerJSON.getJSONObject("displayImagePathSmall") != null) {
					displayImagePathSmall.setPath(offerJSON.getJSONObject(
							"displayImagePathSmall").getString("path"));
					displayImagePathSmall.setAltText(offerJSON.getJSONObject(
							"displayImagePathSmall").getString("altText"));
					offer.setDisplayImagePathSmall(displayImagePathSmall);
					ImageUtil.downloadImageIfNotExists(activity,
							displayImagePathSmall.getPath());
				}

				List<String> validZipCodesList = new ArrayList<String>();

				JSONArray offerValidZipCodesJSONArray = null;

				if (offerJSON.get("validZipCodes") != JSONObject.NULL) {
					offerValidZipCodesJSONArray = offerJSON
							.getJSONArray("validZipCodes");
				}

				if (offerValidZipCodesJSONArray != null) {
					for (int k = 0; k < offerValidZipCodesJSONArray.length(); k++) {
						if (offerValidZipCodesJSONArray.getString(k).length() != 0) {
							validZipCodesList.add(offerValidZipCodesJSONArray
									.getString(k));
						}
					}
					offer.setValidZipCodes(validZipCodesList);
				}// end if

				offersList.add(offer);
			}// end if
		}

		return offersList;
	}

	public static void readUnreadOffers(Activity currentActivity,
			long loginUserId, long offerId, boolean isRead) throws Exception {
		try {
			HttpClient client = new DefaultHttpClient();
			String webServiceUrl = currentActivity.getResources().getString(
					R.string.webservice_url);

			String offerWebServiceUrl = webServiceUrl
					+ "/user/MarkUserOfferReadOrUnread?userId=" + loginUserId;
			HttpPost post = new HttpPost(offerWebServiceUrl);

			String returnMsg = "";
			ArrayList<MarkReadOrUnRead> markReadOrUnReadList = new ArrayList<MarkReadOrUnRead>();
			MarkReadOrUnRead markReadOrUnRead = new MarkReadOrUnRead();
			markReadOrUnRead.setIsRead(isRead);
			markReadOrUnRead.setId(offerId);

			markReadOrUnReadList.add(markReadOrUnRead);

			Gson gson = new Gson();

			StringEntity input = new StringEntity(
					gson.toJson(markReadOrUnReadList));
			input.setContentType("application/json");

			post.setEntity(input);

			HttpResponse response = client.execute(post);

			BufferedReader rd = new BufferedReader(new InputStreamReader(
					response.getEntity().getContent()));
			StringBuilder sb = new StringBuilder();
			String line = "";
			while ((line = rd.readLine()) != null) {
				sb.append(line);
			}

			returnMsg = sb.toString();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void offerDelete(Activity currentActivity, long loginUserId,
			long offerId) throws Exception {
		try {
			HttpClient client = new DefaultHttpClient();
			String webServiceUrl = currentActivity.getResources().getString(
					R.string.webservice_url);

			String doucumentWebServiceUrl = webServiceUrl
					+ "/user/DeleteUserOffer?userId=" + loginUserId;
			HttpPost post = new HttpPost(doucumentWebServiceUrl);

			String returnMsg = "";
			ArrayList<Long> offerIdList = new ArrayList<Long>();

			offerIdList.add(offerId);

			Gson gson = new Gson();

			StringEntity input = new StringEntity(gson.toJson(offerIdList));
			input.setContentType("application/json");

			post.setEntity(input);

			HttpResponse response = client.execute(post);

			BufferedReader rd = new BufferedReader(new InputStreamReader(
					response.getEntity().getContent()));
			StringBuilder sb = new StringBuilder();
			String line = "";
			while ((line = rd.readLine()) != null) {
				sb.append(line);
			}

			returnMsg = sb.toString();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void syncOffersToBackend(Context currentActivity,
			long loginUserId) throws Exception {
		List<Offer> offersListFromDB = new OfferDAO().getAllDirtyOffers(
				currentActivity, loginUserId, " order by name");

		List<MarkReadOrUnRead> markReadOrUnReadList = new ArrayList<MarkReadOrUnRead>();

		String offerIds = "";
		for (Offer offer : offersListFromDB) {
			MarkReadOrUnRead markReadOrUnRead = new MarkReadOrUnRead();
			markReadOrUnRead.setIsRead(offer.isRead());
			markReadOrUnRead.setId(offer.getId());

			markReadOrUnReadList.add(markReadOrUnRead);
			markReadOrUnRead = null;

			offerIds = offerIds + offer.getId() + ",";
		}

		if (offerIds.length() > 0) {
			offerIds = offerIds.substring(0, offerIds.lastIndexOf(","));
		}
		updateMarkUserOfferReadOrUnreadToBackend(offersListFromDB,
				currentActivity, loginUserId);
		updateUserOfferDeleteToBackend(offersListFromDB, currentActivity,
				loginUserId);
		OfferDAO.resetDirtyFlagToFalse(offerIds, currentActivity, loginUserId);
	}

	private static void updateMarkUserOfferReadOrUnreadToBackend(
			List<Offer> offersListFromDB, Context currentActivity,
			long loginUserId) throws Exception {

		try {
			// List<Offer> offersListFromDB = new
			// OfferDAO().getAllDirtyOffers(currentActivity,loginUserId," order by name");

			List<MarkReadOrUnRead> markReadOrUnReadList = new ArrayList<MarkReadOrUnRead>();

			String offerIds = "";
			for (Offer offer : offersListFromDB) {
				MarkReadOrUnRead markReadOrUnRead = new MarkReadOrUnRead();
				markReadOrUnRead.setIsRead(offer.isRead());
				markReadOrUnRead.setId(offer.getId());

				markReadOrUnReadList.add(markReadOrUnRead);
				markReadOrUnRead = null;

				// offerIds=offerIds+offer.getId()+",";
			}

			/*
			 * if(offerIds.length()>0){
			 * offerIds=offerIds.substring(0,offerIds.lastIndexOf(",")); }
			 */

			HttpClient client = new DefaultHttpClient();
			String webServiceUrl = currentActivity.getResources().getString(
					R.string.webservice_url);

			String offerWebServiceUrl = webServiceUrl
					+ "/user/MarkUserOfferReadOrUnread?userId=" + loginUserId;
			HttpPost post = new HttpPost(offerWebServiceUrl);

			String returnMsg = "";

			Gson gson = new Gson();

			StringEntity input = new StringEntity(
					gson.toJson(markReadOrUnReadList));
			input.setContentType("application/json");

			post.setEntity(input);

			HttpResponse response = client.execute(post);

			BufferedReader rd = new BufferedReader(new InputStreamReader(
					response.getEntity().getContent()));
			StringBuilder sb = new StringBuilder();
			String line = "";
			while ((line = rd.readLine()) != null) {
				sb.append(line);
			}

			returnMsg = sb.toString();

			// This will revert isDirty=0 for the backed updated offers.
			// resetDirtyFlagToFalse(offerIds, currentActivity, loginUserId);

			return;

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static void updateUserOfferDeleteToBackend(
			List<Offer> offersListFromDB, Context currentActivity,
			long loginUserId) throws Exception {

		try {
			/*
			 * List<Offer> offersListFromDB = new
			 * OfferDAO().getAllDirtyOffers(currentActivity
			 * ,loginUserId," order by name");
			 * 
			 * List<MarkReadOrUnRead> markReadOrUnReadList = new
			 * ArrayList<MarkReadOrUnRead>();
			 * 
			 * String offerIds = ""; for(Offer offer: offersListFromDB){
			 * MarkReadOrUnRead markReadOrUnRead = new MarkReadOrUnRead();
			 * markReadOrUnRead.setIsRead(offer.isRead());
			 * markReadOrUnRead.setId(offer.getId());
			 * 
			 * markReadOrUnReadList.add(markReadOrUnRead); markReadOrUnRead =
			 * null;
			 * 
			 * offerIds=offerIds+offer.getId()+","; }
			 * 
			 * if(offerIds.length()>0){
			 * offerIds=offerIds.substring(0,offerIds.lastIndexOf(",")); }
			 */

			ArrayList<Long> offerIdList = new ArrayList<Long>();
			for (Offer offer : offersListFromDB) {
				offerIdList.add(offer.getId());
			}

			HttpClient client = new DefaultHttpClient();
			String webServiceUrl = currentActivity.getResources().getString(
					R.string.webservice_url);

			String offerWebServiceUrl = webServiceUrl
					+ "/user/DeleteUserOffer?userId=" + loginUserId;
			HttpPost post = new HttpPost(offerWebServiceUrl);

			String returnMsg = "";

			Gson gson = new Gson();

			StringEntity input = new StringEntity(gson.toJson(offerIdList));
			input.setContentType("application/json");

			post.setEntity(input);

			HttpResponse response = client.execute(post);

			BufferedReader rd = new BufferedReader(new InputStreamReader(
					response.getEntity().getContent()));
			StringBuilder sb = new StringBuilder();
			String line = "";
			while ((line = rd.readLine()) != null) {
				sb.append(line);
			}

			returnMsg = sb.toString();

			// This will revert isDirty=0 for the backed updated offers.
			// resetDirtyFlagToFalse(offerIds, currentActivity, loginUserId);

			return;

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void readUnreadOffer(Activity currentActivity,
			long loginUserId, long offerId, boolean isRead)
			throws Exception {
		try {
			HttpClient client = new DefaultHttpClient();
			String webServiceUrl = currentActivity.getResources().getString(
					R.string.webservice_url);

			String statementWebServiceUrl = webServiceUrl
					+ "/user/MarkUserOfferReadOrUnread?userId=" + loginUserId;
			HttpPost post = new HttpPost(statementWebServiceUrl);

			String returnMsg = "";
			ArrayList<MarkReadOrUnRead> markReadOrUnReadList = new ArrayList<MarkReadOrUnRead>();
			MarkReadOrUnRead markReadOrUnRead = new MarkReadOrUnRead();
			markReadOrUnRead.setIsRead(isRead);
			markReadOrUnRead.setId(offerId);

			markReadOrUnReadList.add(markReadOrUnRead);

			Gson gson = new Gson();

			StringEntity input = new StringEntity(
					gson.toJson(markReadOrUnReadList));
			input.setContentType("application/json");

			post.setEntity(input);

			HttpResponse response = client.execute(post);

			BufferedReader rd = new BufferedReader(new InputStreamReader(
					response.getEntity().getContent()));
			StringBuilder sb = new StringBuilder();
			String line = "";
			while ((line = rd.readLine()) != null) {
				sb.append(line);
			}

			returnMsg = sb.toString();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void updateFavourite(Context currentActivity,long loginUserId,long providerId,boolean isFavourite) throws Exception {
		
		try {
			HttpClient client = new DefaultHttpClient();
			String webServiceUrl = currentActivity.getResources().getString(
					R.string.webservice_url);

			String offerWebServiceUrl = webServiceUrl
					+ "/user/updateUserFavorites?userId=" + loginUserId;
			HttpPost post = new HttpPost(offerWebServiceUrl);

			Boolean isSuccess = false;
			ArrayList<UserFavorite> userFavouriteList = new ArrayList<UserFavorite>();
			UserFavorite userFavorite = new UserFavorite();
			userFavorite.setProviderId(providerId);
			userFavorite.setFavorite(true);
			userFavorite.setIsOffer(isFavourite);
			DocumentProvidersDAO documentProvidersDAO = new DocumentProvidersDAO();
			boolean isDocument = documentProvidersDAO.isFavourit(loginUserId, (int)providerId, currentActivity);
			userFavorite.setIsDocument(isDocument);
			userFavouriteList.add(userFavorite);

			Gson gson = new Gson();

			StringEntity input = new StringEntity(gson.toJson(userFavouriteList));
			input.setContentType("application/json");

			post.setEntity(input);

			HttpResponse response = client.execute(post);

			BufferedReader rd = new BufferedReader(new InputStreamReader(
					response.getEntity().getContent()));
			StringBuilder sb = new StringBuilder();
			String line = "";
			while ((line = rd.readLine()) != null) {
				sb.append(line);
			}

			isSuccess = Boolean.valueOf(sb.toString());

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void syncFavoritesToBackend(Context currentActivity,long loginUserId) throws Exception {
		List<OfferProviders> offerProvidersList = new OfferProvidersDAO().getAllDirtyFavorites(currentActivity, loginUserId);

		List<UserFavorite> userFavoriteList = new ArrayList<UserFavorite>();
		
		String providerIds = "";

		for (OfferProviders offerProviders : offerProvidersList) {
			UserFavorite userFavorite = new UserFavorite();
			userFavorite.setProviderId(offerProviders.getProviderId());
			userFavorite.setFavorite(offerProviders.isFavorite());

			userFavoriteList.add(userFavorite);
			userFavorite = null;

			providerIds = providerIds + offerProviders.getProviderId() + ",";
		}

		if (providerIds.length() > 0) {
			providerIds = providerIds.substring(0, providerIds.lastIndexOf(","));
			
			updateOfferFavorites(offerProvidersList,currentActivity, loginUserId);
			OfferProvidersDAO.resetDirtyFavoritesToFalse(providerIds, currentActivity, loginUserId);
		}
	}
	
	public static void updateOfferFavorites(List<OfferProviders> offerProvidersList, Context currentActivity,long loginUserId) throws Exception {
		try {
			HttpClient client = new DefaultHttpClient();
			String webServiceUrl = currentActivity.getResources().getString(
					R.string.webservice_url);

			String offerWebServiceUrl = webServiceUrl
					+ "/user/updateUserFavorites?userId=" + loginUserId;
			HttpPost post = new HttpPost(offerWebServiceUrl);

			Boolean isSuccess = false;
			ArrayList<UserFavorite> userFavoriteList = new ArrayList<UserFavorite>();
			
			DocumentProvidersDAO documentProvidersDAO = new DocumentProvidersDAO();
			for (OfferProviders offerProviders : offerProvidersList) {
				UserFavorite userFavorite = new UserFavorite();
				userFavorite.setProviderId(offerProviders.getProviderId());
				userFavorite.setFavorite(true);
				userFavorite.setIsOffer(offerProviders.isFavorite());
				boolean isDocument = documentProvidersDAO.isFavourit(loginUserId, (int)offerProviders.getProviderId(), currentActivity);
				userFavorite.setIsDocument(isDocument);
				userFavoriteList.add(userFavorite);
				userFavorite = null;
			}

			Gson gson = new Gson();

			StringEntity input = new StringEntity(gson.toJson(userFavoriteList));
			input.setContentType("application/json");

			post.setEntity(input);

			HttpResponse response = client.execute(post);

			BufferedReader rd = new BufferedReader(new InputStreamReader(
					response.getEntity().getContent()));
			StringBuilder sb = new StringBuilder();
			String line = "";
			while ((line = rd.readLine()) != null) {
				sb.append(line);
			}

			isSuccess = Boolean.valueOf(sb.toString());

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void syncFavorites(Context currentActivity,long loginUserId, String userToken,String userName, String salt, long lastSyncDateTime)throws ClientProtocolException, IOException,JSONException {
		try{
			HttpClient client = new DefaultHttpClient();
			String webServiceUrl = currentActivity.getResources().getString(
					R.string.webservice_url);
	
			HttpGet request = new HttpGet(webServiceUrl + "/user/getAllFavorites" + "?userId=" + loginUserId);
			request.addHeader("userName", userName);
			request.addHeader("userToken", userToken);
			request.addHeader("salt", salt);
	
			HttpResponse response = client.execute(request);
			BufferedReader rd = new BufferedReader(new InputStreamReader(response
					.getEntity().getContent()));
			StringBuilder responseJSONText = new StringBuilder();
	
			String line = "";
			while ((line = rd.readLine()) != null) {
				responseJSONText.append(line);
			}
	
			List<UserFavorite> userFavoriteList = new ArrayList<UserFavorite>();
	
			userFavoriteList = userFavoriteList(responseJSONText.toString());
			
			DocumentProvidersDAO documentProvidersDAO = new DocumentProvidersDAO();
			OfferProvidersDAO offerProvidersDAO = new OfferProvidersDAO();
			
			offerProvidersDAO.refreshUserOfferFavourite(currentActivity, loginUserId);
			documentProvidersDAO.refreshUserDocumentFavourite(currentActivity, loginUserId);
			
			for (UserFavorite userFavorite : userFavoriteList) {
				if(userFavorite.getIsOffer()){
					offerProvidersDAO.addUserFavourite(userFavorite,currentActivity);
				}
				if(userFavorite.getIsDocument()){
					documentProvidersDAO.addUserFavourite(userFavorite,currentActivity);
				}
			}
			
			
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	private static List<UserFavorite> userFavoriteList(
			String responseJSONText) throws JSONException {

		List<UserFavorite> userFavoriteList = new ArrayList<UserFavorite>();
		JSONArray userFavoriteArray = new JSONArray(responseJSONText);

		for (int i = 0; i < userFavoriteArray.length(); i++) {
			JSONObject userFavoriteJSON = userFavoriteArray.getJSONObject(i);
			UserFavorite userFavorite = new UserFavorite();

			if (userFavoriteJSON != null) {
				userFavorite.setUserId(userFavoriteJSON.getLong("userId"));
				userFavorite.setProviderId(userFavoriteJSON.getLong("providerId"));
				userFavorite.setProviderName(userFavoriteJSON.getString("providerName"));
				userFavorite.setProviderIconPath(userFavoriteJSON.getString("providerIconPath"));
				userFavorite.setIsOffer(userFavoriteJSON.getBoolean("isOffer"));
				userFavorite.setIsDocument(userFavoriteJSON.getBoolean("isDocument"));
				userFavoriteList.add(userFavorite);
			}
		}
		return userFavoriteList;
	}
	
}
