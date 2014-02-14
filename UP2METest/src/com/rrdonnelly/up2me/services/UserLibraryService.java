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
import com.rrdonnelly.up2me.dao.OfferDAO;
import com.rrdonnelly.up2me.dao.StatementDAO;
import com.rrdonnelly.up2me.dao.UserLibraryDAO;
import com.rrdonnelly.up2me.json.Image;
import com.rrdonnelly.up2me.json.Offer;
import com.rrdonnelly.up2me.json.Statement;
import com.rrdonnelly.up2me.json.SubAccount;
import com.rrdonnelly.up2me.json.TelephoneStatement;
import com.rrdonnelly.up2me.json.UserLibrary;
import com.rrdonnelly.up2me.util.AsyncImageDownloader;
import com.rrdonnelly.up2me.util.ImageUtil;
import com.rrdonnelly.up2me.valueobjects.MarkReadOrUnRead;

public class UserLibraryService {

	public static void synUserLibraries(Context currentActivity, String userToken,
			String userName, String salt, long loginUserId,
			long lastSyncDateTime) throws ClientProtocolException, IOException,
			JSONException {

		HttpClient client = new DefaultHttpClient();
		String webServiceUrl = currentActivity.getResources().getString(
				R.string.webservice_url);

		String userLibraryWebServiceUrl = webServiceUrl + "/user/UserLibraries?userToken="+ userToken+"&userName="+userName+"&salt="+salt;
		// System.out.println("LastSyncDateTime:"+lastSyncDateTime);
		/*if (lastSyncDateTime != 0l) {
			offerWebServiceUrl += "&date=" + lastSyncDateTime;
		}*/

		HttpGet request = new HttpGet(userLibraryWebServiceUrl);
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


		
		Map<String, List<UserLibrary>> userLibraryMap = new HashMap<String, List<UserLibrary>>();

		List<UserLibrary> flyerLibraries = new ArrayList<UserLibrary>();
		List<UserLibrary> referenceLibraries = new ArrayList<UserLibrary>();

		JSONObject json = new JSONObject(responseJSONText.toString());

		flyerLibraries = createUserLibrariesList("Flyer", json, currentActivity, loginUserId);
		referenceLibraries = createUserLibrariesList("Reference", json,
				currentActivity, loginUserId);

		/*String statementIds = "";
		for (Statement billStatement : billStatements) {
			statementIds = statementIds + billStatement.getId() + ",";
		}

		for (Statement accountStatement : accountStatements) {
			statementIds = statementIds + accountStatement.getId() + ",";
		}

		if (statementIds.length() > 0) {
			statementIds = statementIds.substring(0,
					statementIds.lastIndexOf(","));
		}*/

		// System.out.println(statementIds);

		userLibraryMap.put("Flyer", flyerLibraries);
		userLibraryMap.put("Reference", referenceLibraries);

		Log.d("Insert: ", "Inserting UserLibraries..");
		UserLibraryDAO.refresh(currentActivity);
		/*StatementDAO.deleteStatements(statementIds, currentActivity,
				loginUserId);*/

		for (UserLibrary flyerLibrary : flyerLibraries) {
			UserLibraryDAO.addUserLibrary(flyerLibrary, loginUserId, currentActivity);
		}

		for (UserLibrary referenceLibrary : referenceLibraries) {
			UserLibraryDAO.addUserLibrary(referenceLibrary, loginUserId, currentActivity);
		}

	}

	/*private static List<UserLibrary> createUserLibrariesList(String responseJSONText,
			Context activity, long userId) throws JSONException {

		List<UserLibrary> userLibraryList = new ArrayList<UserLibrary>();
		JSONArray userLibraryArray = new JSONArray(responseJSONText);
		for (int i = 0; i < userLibraryArray.length(); i++) {
			JSONObject userLibraryJSON = userLibraryArray.getJSONObject(i);
			UserLibrary userLibrary = new UserLibrary();

			if (userLibraryJSON != null) {

				userLibrary.setUserId(userId);
				userLibrary.setLibraryId(userLibraryJSON.getLong("libraryId"));
				userLibrary.setFile(userLibraryJSON.getString("file"));
				userLibrary.setCover(userLibraryJSON.getString("cover"));
				userLibrary.setType(userLibraryJSON.getString("type"));
				userLibrary.setCreatedDate(userLibraryJSON.getLong("createdDate"));
				userLibrary.setModifiedDate(userLibraryJSON.getLong("modifiedDate"));
				userLibrary.setIsActive(userLibraryJSON.getBoolean("isActive"));
				
				userLibraryList.add(userLibrary);
			}// end if
		}

		return userLibraryList;
	}*/

	public static List<UserLibrary> createUserLibrariesList(String type,
			JSONObject json, Context activity, long userId) throws JSONException {

		List<UserLibrary> userLibraryList = new ArrayList<UserLibrary>();
		
		JSONArray userLibraryArray = null;
		try{
			userLibraryArray = (JSONArray) json.get(type);
		}
		catch(JSONException jsone){
			//jsone.printStackTrace();
			return userLibraryList;
		}
		
		
		for (int i = 0; i < userLibraryArray.length(); i++) {
			JSONObject userLibraryJSON = userLibraryArray.getJSONObject(i);

			UserLibrary userLibrary = new UserLibrary();

			if (userLibraryJSON != null) {
				// Level two statements (Bill -> Statement*)

				userLibrary.setUserId(userId);
				userLibrary.setLibraryId(userLibraryJSON.getLong("libraryId"));
				userLibrary.setFile(userLibraryJSON.getString("file"));
				userLibrary.setCover(userLibraryJSON.getString("cover"));
				userLibrary.setType(type);
				//userLibrary.setCreatedDate(userLibraryJSON.getString("createdDate"));
				//userLibrary.setModifiedDate(userLibraryJSON.getString("modifiedDate"));
				userLibrary.setIsActive(userLibraryJSON.getBoolean("isActive"));
				userLibrary.setUrl(userLibraryJSON.getString("url"));
				userLibrary.setUseUrl(userLibraryJSON.getBoolean("useUrl"));
				userLibrary.setIsRead(userLibraryJSON.getBoolean("isRead"));
				
				userLibraryList.add(userLibrary);
			}// if(bill!=null)

		} // For i
		return userLibraryList;
	}
	
	public static void readUnreadLibrary(Activity currentActivity,
			long loginUserId, long libraryId, boolean isRead)
			throws Exception {
		try {
			HttpClient client = new DefaultHttpClient();
			String webServiceUrl = currentActivity.getResources().getString(
					R.string.webservice_url);

			String statementWebServiceUrl = webServiceUrl
					+ "/user/MarkLibraryReadOrUnread?userId=" + loginUserId;
			HttpPost post = new HttpPost(statementWebServiceUrl);

			String returnMsg = "";
			ArrayList<MarkReadOrUnRead> markReadOrUnReadList = new ArrayList<MarkReadOrUnRead>();
			MarkReadOrUnRead markReadOrUnRead = new MarkReadOrUnRead();
			markReadOrUnRead.setIsRead(isRead);
			markReadOrUnRead.setId(libraryId);

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

}
