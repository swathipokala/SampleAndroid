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

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;
import com.rrdonnelly.up2me.R;
import com.rrdonnelly.up2me.dao.OfferDAO;
import com.rrdonnelly.up2me.dao.StatementDAO;
import com.rrdonnelly.up2me.dao.Tag;
import com.rrdonnelly.up2me.dao.TagDAO;
import com.rrdonnelly.up2me.json.Offer;
import com.rrdonnelly.up2me.json.Statement;
import com.rrdonnelly.up2me.json.UserDocHeaderTagJson;
import com.rrdonnelly.up2me.json.UserOfferTagJson;
import com.rrdonnelly.up2me.json.UserTagJson;
import com.rrdonnelly.up2me.util.ColorCodeUtil;

public class TagService {

	private static void refresh(Context activity, long userId) {
		TagDAO.deleteTags(activity, userId);
		TagDAO.deleteAllOffersAndStatementTags(activity, userId);
	}

	
	public static void syncTags(Context currentActivity, long loginUserId)
			throws ClientProtocolException, IOException, JSONException {

		HttpClient client = new DefaultHttpClient();
		String webServiceUrl = currentActivity.getResources().getString(
				R.string.webservice_url);

		String tagsWebServiceUrl = webServiceUrl + "/user/GetTags?userId="
				+ loginUserId;

		HttpGet request = new HttpGet(tagsWebServiceUrl);
		/*
		 * request.addHeader("userName",userName);
		 * request.addHeader("userToken",userToken);
		 * request.addHeader("salt",salt);
		 */

		HttpResponse response = client.execute(request);
		BufferedReader rd = new BufferedReader(new InputStreamReader(response
				.getEntity().getContent()));
		StringBuilder responseJSONText = new StringBuilder();

		String line = "";
		while ((line = rd.readLine()) != null) {
			responseJSONText.append(line);
		}

		List<UserTagJson> tagsList = new ArrayList<UserTagJson>();

		tagsList = createTagsList(responseJSONText.toString(), currentActivity);

		Log.d("Insert: ", "Inserting Tags..");
		refresh(currentActivity, loginUserId);

		for (UserTagJson tag : tagsList) {
			TagDAO.addTag(tag, loginUserId, currentActivity);
		}

		Log.d("Insert: ", "Inserting Tags Completed.");

	}

	private static List<UserTagJson> createTagsList(String responseJSONText,
			Context activity) throws JSONException {

		List<UserTagJson> tagsList = new ArrayList<UserTagJson>();
		JSONArray offersArray = new JSONArray(responseJSONText);
		for (int i = 0; i < offersArray.length(); i++) {
			JSONObject tagJSON = offersArray.getJSONObject(i);
			UserTagJson tag = new UserTagJson();

			if (tagJSON != null) {

				tag.setUserId(tagJSON.getLong("userId"));
				tag.setTagName(tagJSON.getString("tagName"));
				tag.setColour(tagJSON.getString("colour"));
				//tag.setSequenceId(tagJSON.getInt("sequenceId"));
				tag.setTagId(tagJSON.getLong("tagId"));

				tagsList.add(tag);
			}// end if
		}

		return tagsList;
	}


	
	public static void syncUserOfferTags(Context currentActivity, long loginUserId)
			throws ClientProtocolException, IOException, JSONException {

		HttpClient client = new DefaultHttpClient();
		String webServiceUrl = currentActivity.getResources().getString(
				R.string.webservice_url);

		String tagsWebServiceUrl = webServiceUrl + "/user/GetOfferTags?userId="
				+ loginUserId;

		HttpGet request = new HttpGet(tagsWebServiceUrl);
		/*
		 * request.addHeader("userName",userName);
		 * request.addHeader("userToken",userToken);
		 * request.addHeader("salt",salt);
		 */

		HttpResponse response = client.execute(request);
		BufferedReader rd = new BufferedReader(new InputStreamReader(response
				.getEntity().getContent()));
		StringBuilder responseJSONText = new StringBuilder();

		String line = "";
		while ((line = rd.readLine()) != null) {
			responseJSONText.append(line);
		}

		List<UserOfferTagJson> userOfferTagsList = new ArrayList<UserOfferTagJson>();

		userOfferTagsList = createUserOfferTagsList(responseJSONText.toString(), currentActivity);

		Log.d("Insert: ", "Inserting User Offer Tags..");
		

		for (UserOfferTagJson userOfferTag : userOfferTagsList) {
			TagDAO.addUserOfferTag(userOfferTag, loginUserId, currentActivity);
		}

		Log.d("Insert: ", "Inserting User Offer Tags Completed.");

	}
	
	private static List<UserOfferTagJson> createUserOfferTagsList(String responseJSONText,
			Context activity) throws JSONException {

		List<UserOfferTagJson> userOfferTagsList = new ArrayList<UserOfferTagJson>();
		JSONArray userOfferTagsArray = new JSONArray(responseJSONText);
		for (int i = 0; i < userOfferTagsArray.length(); i++) {
			JSONObject userOfferTagJSON = userOfferTagsArray.getJSONObject(i);
			UserOfferTagJson userOfferTag = new UserOfferTagJson();

			if (userOfferTagJSON != null) {

				userOfferTag.setUserId(userOfferTagJSON.getLong("userId"));
				userOfferTag.setTagId(userOfferTagJSON.getLong("tagId"));
				userOfferTag.setOfferId(userOfferTagJSON.getLong("offerId"));
				userOfferTagsList.add(userOfferTag);
				
			}// end if
		}

		return userOfferTagsList;
	}
	

	public static void syncUserStatementTags(Context currentActivity, long loginUserId)
			throws ClientProtocolException, IOException, JSONException {

		HttpClient client = new DefaultHttpClient();
		String webServiceUrl = currentActivity.getResources().getString(
				R.string.webservice_url);

		String tagsWebServiceUrl = webServiceUrl + "/user/GetStatementTags?userId="
				+ loginUserId;

		HttpGet request = new HttpGet(tagsWebServiceUrl);
		/*
		 * request.addHeader("userName",userName);
		 * request.addHeader("userToken",userToken);
		 * request.addHeader("salt",salt);
		 */

		HttpResponse response = client.execute(request);
		BufferedReader rd = new BufferedReader(new InputStreamReader(response
				.getEntity().getContent()));
		StringBuilder responseJSONText = new StringBuilder();

		String line = "";
		while ((line = rd.readLine()) != null) {
			responseJSONText.append(line);
		}

		List<UserDocHeaderTagJson> userStatementTagsList = new ArrayList<UserDocHeaderTagJson>();

		userStatementTagsList = createUserStatementTagsList(responseJSONText.toString(), currentActivity);

		Log.d("Insert: ", "Inserting User Offer Tags..");
		
		for (UserDocHeaderTagJson userStatementTag : userStatementTagsList) {
			TagDAO.addUserStatementTag(userStatementTag, loginUserId, currentActivity);
		}

		Log.d("Insert: ", "Inserting User Offer Tags Completed.");

	}
	
	private static List<UserDocHeaderTagJson> createUserStatementTagsList(String responseJSONText,
			Context activity) throws JSONException {

		List<UserDocHeaderTagJson> userStatementTagsList = new ArrayList<UserDocHeaderTagJson>();
		JSONArray userStatementTagsArray = new JSONArray(responseJSONText);
		for (int i = 0; i < userStatementTagsArray.length(); i++) {
			JSONObject userStatementTagJSON = userStatementTagsArray.getJSONObject(i);
			UserDocHeaderTagJson userStatementTag = new UserDocHeaderTagJson();

			if (userStatementTagJSON != null) {

				userStatementTag.setUserId(userStatementTagJSON.getLong("userId"));
				userStatementTag.setDocHeaderId(userStatementTagJSON.getLong("docHeaderId"));
				userStatementTag.setTagId(userStatementTagJSON.getLong("tagId"));
				userStatementTagsList.add(userStatementTag);
				
			}// end if
		}

		return userStatementTagsList;
	}
	
	
	
	public static void syncTagsToBackend(Context currentActivity,
			long loginUserId) throws ClientProtocolException, IOException,
			JSONException {

		Log.d("Update Backend: ", "Inserting Tags..");

		List<Tag> allTagsFromLocalDB = TagDAO.getAllTags(currentActivity,
				loginUserId);

		List<UserTagJson> tagsListToUpdateBackend = new ArrayList<UserTagJson>();
		for (Tag tag : allTagsFromLocalDB) {
			UserTagJson userTagJson = new UserTagJson();
			userTagJson.setUserId(loginUserId);
			userTagJson.setColour(ColorCodeUtil.hexToRgb.get(tag.getColorCode()));
			//userTagJson.setSequenceId(tag.getSequenceId());
			userTagJson.setTagName(tag.getName());
			userTagJson.setTagId(tag.getTagId());
			tagsListToUpdateBackend.add(userTagJson);
			userTagJson = null;
		}

		HttpClient client = new DefaultHttpClient();
		String webServiceUrl = currentActivity.getResources().getString(
				R.string.webservice_url);
		String updateTagsToBackendWebServiceUrl = webServiceUrl
				+ "/user/UpdateTags?userId=" + loginUserId;

		HttpPost post = new HttpPost(updateTagsToBackendWebServiceUrl);

		Gson gson = new Gson();

		StringEntity input = new StringEntity(
				gson.toJson(tagsListToUpdateBackend));
		input.setContentType("application/json");

		post.setEntity(input);

		HttpResponse response = client.execute(post);
		BufferedReader rd = new BufferedReader(new InputStreamReader(response
				.getEntity().getContent()));
		StringBuilder responseJSONText = new StringBuilder();

		String line = "";
		while ((line = rd.readLine()) != null) {
			responseJSONText.append(line);
		}

		String jsonText = responseJSONText.toString();

		Log.d("Update Backend: ", "Inserting Tags Completed.");

		/*
		 * JSONObject json = new JSONObject(jsonText); String statusCode =
		 * json.getJSONObject("Status").getString("StatusCode"); String
		 * loginUserId = json.getJSONObject("Status").getString("userId");
		 */

		System.out.println(jsonText);
	}

	public static void syncUserStatementTagsToBackend(Context currentActivity,
			long loginUserId) throws ClientProtocolException, IOException,
			JSONException {

		Log.d("Update Backend: ", "Updating User Statement Tags..");

		List<Statement> statementsListFromDB = new StatementDAO()
				.getAllStatements(currentActivity, loginUserId);

		List<UserDocHeaderTagJson> userDocHeaderTagsListToUpdateBackend = new ArrayList<UserDocHeaderTagJson>();

		for (Statement statement : statementsListFromDB) {
			UserDocHeaderTagJson userDocHeaderTagJson = new UserDocHeaderTagJson();
			userDocHeaderTagJson.setUserId(loginUserId);
			userDocHeaderTagJson.setDocHeaderId(statement.getId());
			userDocHeaderTagJson.setTagId(Long.valueOf(String.valueOf(statement
					.getTagId())));
			userDocHeaderTagsListToUpdateBackend.add(userDocHeaderTagJson);
			userDocHeaderTagJson = null;
		}

		HttpClient client = new DefaultHttpClient();
		String webServiceUrl = currentActivity.getResources().getString(
				R.string.webservice_url);
		String updateOfferTagsToBackendWebServiceUrl = webServiceUrl
				+ "/user/UpdateStatementTags?userId=" + loginUserId;

		HttpPost post = new HttpPost(updateOfferTagsToBackendWebServiceUrl);

		Gson gson = new Gson();

		StringEntity input = new StringEntity(
				gson.toJson(userDocHeaderTagsListToUpdateBackend));
		input.setContentType("application/json");

		post.setEntity(input);

		HttpResponse response = client.execute(post);
		BufferedReader rd = new BufferedReader(new InputStreamReader(response
				.getEntity().getContent()));
		StringBuilder responseJSONText = new StringBuilder();

		String line = "";
		while ((line = rd.readLine()) != null) {
			responseJSONText.append(line);
		}

		String jsonText = responseJSONText.toString();

		Log.d("Update Backend: ", "Updating User Statement Tags Completed.");

		/*
		 * JSONObject json = new JSONObject(jsonText); String statusCode =
		 * json.getJSONObject("Status").getString("StatusCode"); String
		 * loginUserId = json.getJSONObject("Status").getString("userId");
		 */

		System.out.println(jsonText);
	}

	public static void syncUserOfferTagsToBackend(Context currentActivity,
			long loginUserId) throws ClientProtocolException, IOException,
			JSONException {

		Log.d("Update Backend: ", "Inserting User Offer Tags..");

		List<Offer> offersListFromDB = new OfferDAO().getAllOffers(
				currentActivity, loginUserId, " order by name");
		List<UserOfferTagJson> userOfferTagsListToUpdateBackend = new ArrayList<UserOfferTagJson>();

		for (Offer offer : offersListFromDB) {
			UserOfferTagJson userOfferTagJson = new UserOfferTagJson();
			userOfferTagJson.setUserId(loginUserId);
			userOfferTagJson.setOfferId(offer.getId());
			userOfferTagJson.setTagId(Long.valueOf(String.valueOf(offer
					.getTagId())));
			userOfferTagsListToUpdateBackend.add(userOfferTagJson);
			userOfferTagJson = null;
		}

		HttpClient client = new DefaultHttpClient();
		String webServiceUrl = currentActivity.getResources().getString(
				R.string.webservice_url);
		String updateOfferTagsToBackendWebServiceUrl = webServiceUrl
				+ "/user/UpdateOfferTags?userId=" + loginUserId;

		HttpPost post = new HttpPost(updateOfferTagsToBackendWebServiceUrl);

		Gson gson = new Gson();

		StringEntity input = new StringEntity(
				gson.toJson(userOfferTagsListToUpdateBackend));
		input.setContentType("application/json");

		post.setEntity(input);

		HttpResponse response = client.execute(post);
		BufferedReader rd = new BufferedReader(new InputStreamReader(response
				.getEntity().getContent()));
		StringBuilder responseJSONText = new StringBuilder();

		String line = "";
		while ((line = rd.readLine()) != null) {
			responseJSONText.append(line);
		}

		String jsonText = responseJSONText.toString();

		Log.d("Update Backend: ", "Inserting User Offer Tags Completed.");

		/*
		 * JSONObject json = new JSONObject(jsonText); String statusCode =
		 * json.getJSONObject("Status").getString("StatusCode"); String
		 * loginUserId = json.getJSONObject("Status").getString("userId");
		 */

		// System.out.println(jsonText);
	}

}
