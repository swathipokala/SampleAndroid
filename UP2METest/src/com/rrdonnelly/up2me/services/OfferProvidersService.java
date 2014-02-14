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
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HTTP;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.os.StrictMode;
import android.util.Log;

import com.rrdonnelly.up2me.R;
import com.rrdonnelly.up2me.dao.OfferProvidersDAO;
import com.rrdonnelly.up2me.json.OfferProviders;

public class OfferProvidersService {

	private static void refresh(Context activity) {
		OfferProvidersDAO.deleteOfferProviders(activity);
	}

	public static void syncOfferProviders(Context currentActivity,
			String userToken, String userName, String salt, long loginUserId,
			long lastSyncDateTime) throws ClientProtocolException, IOException,
			JSONException {

		/*
		 * HttpClient client = new DefaultHttpClient(); String webServiceUrl =
		 * currentActivity.getResources().getString(R.string.webservice_url);
		 * HttpGet request = new HttpGet(webServiceUrl +
		 * "/ContentTool/Provider/getOfferProviders");
		 * request.addHeader("userName",userName);
		 * request.addHeader("userToken",userToken);
		 * request.addHeader("salt",salt);
		 * 
		 * HttpResponse response = client.execute(request); BufferedReader rd =
		 * new BufferedReader(new
		 * InputStreamReader(response.getEntity().getContent())); StringBuilder
		 * responseJSONText = new StringBuilder();
		 * 
		 * String line = ""; while ((line = rd.readLine()) != null) {
		 * responseJSONText.append(line); }
		 * 
		 * List<OfferProviders> offerProvidersList = new
		 * ArrayList<OfferProviders>();
		 * 
		 * offerProvidersList =
		 * createOfferProvidersList(responseJSONText.toString(),
		 * currentActivity);
		 */

		List<OfferProviders> offerProvidersList = new ArrayList<OfferProviders>();

		offerProvidersList = getOfferProviders(currentActivity);

		Log.d("Insert: ", "Inserting Statements..");

		refresh(currentActivity);

		for (OfferProviders offerProviders : offerProvidersList) {
			OfferProvidersDAO
					.addOfferProviders(offerProviders, currentActivity);
		}

		Log.d("Insert: ", "Inserting Statements Completed.");

	}

	private static List<OfferProviders> createOfferProvidersList(
			String responseJSONText, Context activity) throws JSONException {

		List<OfferProviders> offerProvidersList = new ArrayList<OfferProviders>();

		JSONArray offerProvidersArray = new JSONArray(responseJSONText);

		for (int i = 0; i < offerProvidersArray.length(); i++) {
			JSONObject offerProvidersJSON = offerProvidersArray
					.getJSONObject(i);
			OfferProviders offerProviders = new OfferProviders();

			if (offerProvidersJSON != null) {
				offerProviders.setProviderId(offerProvidersJSON
						.getLong("clientId"));
				offerProviders.setProviderName(offerProvidersJSON
						.getString("providerName"));
				offerProviders.setActive(true);
				offerProviders.setImageUrlSmallStr(offerProvidersJSON
						.getJSONObject("displayImagePath").getString("path"));
			}

			offerProvidersList.add(offerProviders);
		}
		return offerProvidersList;
	}

	public static List<OfferProviders> getOfferProviders(Context currentActivity)
			throws ClientProtocolException, IOException, JSONException {
		HttpClient client = new DefaultHttpClient();
		String webServiceUrl = currentActivity.getResources().getString(
				R.string.webservice_url);
		HttpGet request = new HttpGet(webServiceUrl + "/Providers/Offer");

		HttpResponse response = client.execute(request);
		BufferedReader rd = new BufferedReader(new InputStreamReader(response
				.getEntity().getContent()));
		StringBuilder responseJSONText = new StringBuilder();

		String line = "";
		while ((line = rd.readLine()) != null) {
			responseJSONText.append(line);
		}

		List<OfferProviders> offerProvidersList = new ArrayList<OfferProviders>();

		offerProvidersList = createOfferProvidersList(
				responseJSONText.toString(), currentActivity);

		return offerProvidersList;
	}
	
	public static void syncOfferProvidersListToBackend(Context currentActivity, long loginUserId) {
		List<Integer> offerProvidersList = OfferProvidersDAO.getAllUserOfferProviders(currentActivity, loginUserId);
		if(offerProvidersList.size() > 0) {
			updateUserOfferProvidersListToBackEnd(currentActivity, offerProvidersList, loginUserId);
		}
	}
	
	public static String updateUserOfferProvidersListToBackEnd(Context activity, List<Integer> offerProviders, long userId) {

		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
		StrictMode.setThreadPolicy(policy);

		HttpClient client = new DefaultHttpClient();
		String webServiceUrl = activity.getResources().getString(R.string.webservice_url);
		String returnMsg = "";
		try {
			HttpPost post = new HttpPost(webServiceUrl + "/user/UpdateOfferPrefs?userId=" + userId);

			JSONArray selectedOfferProviders = new JSONArray(offerProviders);

			StringEntity se = new StringEntity(selectedOfferProviders.toString());
			se.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
			post.setEntity(se);

			HttpResponse response = client.execute(post);

			BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
			StringBuilder sb = new StringBuilder();

			String line = "";
			while ((line = rd.readLine()) != null) {
				sb.append(line);
			}

			JSONObject jsonReturnMsg = new JSONObject(sb.toString());
			returnMsg = jsonReturnMsg.get("message").toString();

		} catch (Exception ex) {
			ex.printStackTrace();
		}
		
		return returnMsg;
	}

}
