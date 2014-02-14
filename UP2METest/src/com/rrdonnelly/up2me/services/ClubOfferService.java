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

import com.rrdonnelly.up2me.R;
import com.rrdonnelly.up2me.dao.ClubOfferDAO;
import com.rrdonnelly.up2me.json.ClubOffer;
import com.rrdonnelly.up2me.json.Image;

public class ClubOfferService {

	public void syncClubOffers(Activity currentActivity, String userToken,
			String userName, String salt, long loginUserId)
			throws ClientProtocolException, IOException, JSONException {

		HttpClient client = new DefaultHttpClient();
		String webServiceUrl = currentActivity.getResources().getString(
				R.string.webservice_url);
		HttpGet request = new HttpGet(webServiceUrl + "/Providers/Offer");
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

		List<ClubOffer> clubOfferList = new ArrayList<ClubOffer>();

		clubOfferList = createClubOfferList(responseJSONText.toString(),
				currentActivity);

		Log.d("Insert: ", "Inserting Statements..");

		refresh(currentActivity);

		for (ClubOffer clubOffer : clubOfferList) {
			ClubOfferDAO.addClubOffer(clubOffer, currentActivity);
		}

		Log.d("Insert: ", "Inserting Statements Completed.");

	}

	private static List<ClubOffer> createClubOfferList(String responseJSONText,
			Activity activity) throws JSONException {

		List<ClubOffer> clubOfferList = new ArrayList<ClubOffer>();

		JSONArray clubOfferArray = new JSONArray(responseJSONText);

		for (int i = 0; i < clubOfferArray.length(); i++) {
			JSONObject clubOfferJSON = clubOfferArray.getJSONObject(i);
			ClubOffer clubOffer = new ClubOffer();

			if (clubOfferJSON != null) {

				clubOffer.setClientId(clubOfferJSON.getLong("clientId"));
				clubOffer.setId(clubOfferJSON.getLong("id"));

				Image displayImagePath = new Image();

				if (clubOfferJSON.getJSONObject("displayImagePath") != null) {
					displayImagePath.setPath(clubOfferJSON.getJSONObject(
							"displayImagePath").getString("path"));
					displayImagePath.setAltText(clubOfferJSON.getJSONObject(
							"displayImagePath").getString("altText"));
					clubOffer.setDisplayImagePath(displayImagePath);
					// ImageUtil.downloadImageIfNotExists(activity,
					// displayImagePath.getPath());
				}

				clubOfferList.add(clubOffer);
			}// end if
		}
		return clubOfferList;
	}

	public void refresh(Activity activity) {
		ClubOfferDAO.deleteClubOffers(activity);
	}

}
