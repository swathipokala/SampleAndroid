package com.rrdonnelly.up2me.services;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.util.Log;

import com.rrdonnelly.up2me.R;
import com.rrdonnelly.up2me.dao.UserSubscriptionStatementDAO;
import com.rrdonnelly.up2me.json.ClubProvider;
import com.rrdonnelly.up2me.json.Image;

public class UserSubscriptionStatementService {

	private static void refresh(Activity activity) {
		UserSubscriptionStatementDAO.deleteClubStatements(activity);
	}

	public static void syncClubProvider(Activity currentActivity,
			String userToken, String userName, String salt, long loginUserId)
			throws ClientProtocolException, IOException, JSONException {

		HttpClient client = new DefaultHttpClient();
		String webServiceUrl = currentActivity.getResources().getString(
				R.string.webservice_url);
		HttpGet request = new HttpGet(webServiceUrl
				+ "/user/Subscriptions/Statement");
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

		Map<String, List<ClubProvider>> clubStatementsMap = new HashMap<String, List<ClubProvider>>();

		List<ClubProvider> billClubProviders = new ArrayList<ClubProvider>();
		List<ClubProvider> accountClubProviders = new ArrayList<ClubProvider>();

		JSONObject json = new JSONObject(responseJSONText.toString());

		billClubProviders = createClubProviderList("Bill", json,
				currentActivity);
		accountClubProviders = createClubProviderList("Account", json,
				currentActivity);

		clubStatementsMap.put("Bill", billClubProviders);
		clubStatementsMap.put("Account", accountClubProviders);

		Log.d("Insert: ", "Inserting Statements..");
		UserSubscriptionStatementDAO UserSubscriptionStatementDAO = new UserSubscriptionStatementDAO();

		refresh(currentActivity);

		for (ClubProvider billClubProvider : billClubProviders) {
			UserSubscriptionStatementDAO.addClubProvider(billClubProvider,
					"Bill", currentActivity);
		}

		for (ClubProvider accountClubProvider : accountClubProviders) {
			UserSubscriptionStatementDAO.addClubProvider(accountClubProvider,
					"Account", currentActivity);
		}

		Log.d("Insert: ", "Inserting Statements Completed.");


	}

	private static List<ClubProvider> createClubProviderList(String type,
			JSONObject json, Activity activity) throws JSONException {

		JSONArray clubProviderArray = (JSONArray) json.get(type);
		List<ClubProvider> clubProvidersList = new ArrayList<ClubProvider>();

		for (int i = 0; i < clubProviderArray.length(); i++) {
			JSONObject clubProviderJSON = clubProviderArray.getJSONObject(i);

			ClubProvider clubProviderObj = new ClubProvider();

			if (clubProviderJSON != null) {
				// Level two statements (Bill -> Statement*)

				clubProviderObj.setId(clubProviderJSON.getLong("id"));
				clubProviderObj.setName(clubProviderJSON.getString("name"));
				clubProviderObj.setSubscribed(clubProviderJSON
						.getBoolean("subscribed"));

				Image displayImagePath = new Image();

				if (clubProviderJSON.getJSONObject("displayImagePath") != null) {
					displayImagePath.setPath(clubProviderJSON.getJSONObject(
							"displayImagePath").getString("path"));
					displayImagePath.setAltText(clubProviderJSON.getJSONObject(
							"displayImagePath").getString("altText"));
					clubProviderObj.setDisplayImagePath(displayImagePath);
					// ImageUtil.downloadImageIfNotExists(activity,
					// displayImagePath.getPath());
				}

				clubProvidersList.add(clubProviderObj);
			}// if(bill!=null)

		} // For i
		return clubProvidersList;
	}

}
