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
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.util.Log;

import com.google.gson.Gson;
import com.rrdonnelly.up2me.R;
import com.rrdonnelly.up2me.dao.UserSubscriptionOfferDAO;
import com.rrdonnelly.up2me.json.ClubProvider;
import com.rrdonnelly.up2me.json.Image;

public class UserSubscriptionOfferService {

	private static void refresh(Activity activity) {
		UserSubscriptionOfferDAO.deleteClubProviders(activity);
	}

	public static void syncClubProviders(Activity currentActivity,
			String userToken, String userName, String salt, long loginUserId)
			throws ClientProtocolException, IOException, JSONException {

		HttpClient client = new DefaultHttpClient();
		String webServiceUrl = currentActivity.getResources().getString(
				R.string.webservice_url);
		HttpGet request = new HttpGet(webServiceUrl
				+ "/user/Subscriptions/Offer");

		HttpResponse response = client.execute(request);
		BufferedReader rd = new BufferedReader(new InputStreamReader(response
				.getEntity().getContent()));
		StringBuilder responseJSONText = new StringBuilder();
		request.addHeader("userName", userName);
		request.addHeader("userToken", userToken);
		request.addHeader("salt", salt);

		String line = "";
		while ((line = rd.readLine()) != null) {
			responseJSONText.append(line);
		}

		Gson gson = new Gson();

		List<ClubProvider> clubProviderList = new ArrayList<ClubProvider>();

		clubProviderList = createCludProviderList(responseJSONText.toString(),
				currentActivity);

		Log.d("Insert: ", "Inserting Statements..");

		refresh(currentActivity);

		for (ClubProvider clubProvider : clubProviderList) {
			UserSubscriptionOfferDAO.addClubProvider(clubProvider, loginUserId,
					currentActivity);
		}

		Log.d("Insert: ", "Inserting Statements Completed.");

	}

	private static List<ClubProvider> createCludProviderList(
			String responseJSONText, Activity activity) throws JSONException {

		List<ClubProvider> clubProviderList = new ArrayList<ClubProvider>();

		JSONArray clubProviderArray = new JSONArray(responseJSONText);

		for (int i = 0; i < clubProviderArray.length(); i++) {
			JSONObject clubProviderJSON = clubProviderArray.getJSONObject(i);
			ClubProvider clubProvider = new ClubProvider();

			if (clubProviderJSON != null) {

				clubProvider.setId(clubProviderJSON.getLong("id"));
				clubProvider.setName(clubProviderJSON.getString("name"));
				clubProvider.setSubscribed(clubProviderJSON
						.getBoolean("subscribed"));

				Image displayImagePath = new Image();

				if (clubProviderJSON.getJSONObject("displayImagePath") != null) {
					displayImagePath.setPath(clubProviderJSON.getJSONObject(
							"displayImagePath").getString("path"));
					displayImagePath.setAltText(clubProviderJSON.getJSONObject(
							"displayImagePath").getString("altText"));
					clubProvider.setDisplayImagePath(displayImagePath);
					// ImageUtil.downloadImageIfNotExists(activity,
					// displayImagePath.getPath());
				}

				clubProviderList.add(clubProvider);
			}// end if
		}
		return clubProviderList;
	}

}
