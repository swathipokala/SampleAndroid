package com.rrdonnelly.up2me.services;

import java.io.BufferedReader;
import java.io.File;
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
import com.rrdonnelly.up2me.dao.DocumentProvidersDAO;
import com.rrdonnelly.up2me.json.DocumentProviders;
import com.rrdonnelly.up2me.util.ImageUtil;

public class DocumentProvidersService {

	private static void refresh(Context activity) {
		DocumentProvidersDAO.deleteDocumentProviders(activity);
	}

	public static void syncDocumentProviders(Context currentActivity,
			String userToken, String userName, String salt, long loginUserId,
			long lastSyncDateTime) throws ClientProtocolException, IOException,
			JSONException {

		/*
		 * HttpClient client = new DefaultHttpClient(); String webServiceUrl =
		 * currentActivity.getResources().getString(R.string.webservice_url);
		 * HttpGet request = new HttpGet(webServiceUrl +
		 * "/ContentTool/Provider/getAllProviders");
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
		 * List<DocumentProviders> documentProvidersList = new
		 * ArrayList<DocumentProviders>();
		 * 
		 * documentProvidersList =
		 * createDocumentProvidersList(responseJSONText.toString(),
		 * currentActivity);
		 */

		refresh(currentActivity);

		List<DocumentProviders> documentProvidersList = new ArrayList<DocumentProviders>();
		documentProvidersList = getDocumentProviders(currentActivity);

		Log.d("Insert: ", "Inserting Statements..");

		for (DocumentProviders documentProviders : documentProvidersList) {
			DocumentProvidersDAO.addDocumentProviders(documentProviders,
					currentActivity);
		}

		Log.d("Insert: ", "Inserting Statements Completed.");

		List<DocumentProviders> providers = DocumentProvidersDAO.getUserDocumentProviders(currentActivity, loginUserId+"");
		
		 for (DocumentProviders provider:providers){
			 String downloadUrl=provider.getImageUrlSmallStr();
			 String imagePathfileName = downloadUrl.substring(downloadUrl.lastIndexOf("/")+1);
			 File file = new File(currentActivity.getFilesDir(), imagePathfileName);
			/// if (!file.exists()){
				 ImageUtil.downloadImage(currentActivity, downloadUrl,imagePathfileName);
			// }
		 }
	}

	private static List<DocumentProviders> createDocumentProvidersList(
			String responseJSONText) throws JSONException {

		List<DocumentProviders> documentProvidersList = new ArrayList<DocumentProviders>();
		JSONArray documentProvidersArray = new JSONArray(responseJSONText);

		for (int i = 0; i < documentProvidersArray.length(); i++) {
			JSONObject documentProvidersJSON = documentProvidersArray
					.getJSONObject(i);
			DocumentProviders documentProviders = new DocumentProviders();

			if (documentProvidersJSON != null) {
				documentProviders.setProviderId(documentProvidersJSON
						.getLong("clientId"));
				documentProviders.setProviderName(documentProvidersJSON
						.getString("name"));
				documentProviders.setActive(true);
				documentProviders.setImageUrlSmallStr(documentProvidersJSON
						.getJSONObject("displayImagePath").getString("path"));
				documentProvidersList.add(documentProviders);
			}
		}// end if
		return documentProvidersList;
	}

	public static List<DocumentProviders> getDocumentProviders(
			Context currentActivity) throws ClientProtocolException,
			IOException, JSONException {

		HttpClient client = new DefaultHttpClient();
		String webServiceUrl = currentActivity.getResources().getString(
				R.string.webservice_url);
		HttpGet request = new HttpGet(webServiceUrl + "/Providers/Statement");

		HttpResponse response = client.execute(request);
		BufferedReader rd = new BufferedReader(new InputStreamReader(response
				.getEntity().getContent()));
		StringBuilder responseJSONText = new StringBuilder();

		String line = "";
		while ((line = rd.readLine()) != null) {
			responseJSONText.append(line);
		}

		List<DocumentProviders> documentProvidersList = new ArrayList<DocumentProviders>();

		documentProvidersList = createDocumentProvidersList(responseJSONText
				.toString());

		return documentProvidersList;
	}
	
	public static void syncDocumentProvidersListToBackend(Context currentActivity, long loginUserId) {
		List<Integer> documentProvidersList = DocumentProvidersDAO.getAllUserDocumentProviders(currentActivity, loginUserId);
		if(documentProvidersList.size() > 0) {
			updateUserDocumentProvidersListToBackEnd(currentActivity, documentProvidersList, loginUserId);
		}
	}
	
	public static String updateUserDocumentProvidersListToBackEnd(Context activity, List<Integer> documentProviders, long userId) {
		
		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
		StrictMode.setThreadPolicy(policy);
		
		HttpClient client = new DefaultHttpClient();
		String webServiceUrl = activity.getResources().getString(
				R.string.webservice_url);
		String returnMsg = "";
		try {
			HttpPost post = new HttpPost(webServiceUrl + "/user/UpdateAccountPrefs?userId=" + userId);
		
			JSONArray selectedDocumentProviders = new JSONArray(documentProviders);
		
			StringEntity se = new StringEntity(
					selectedDocumentProviders.toString());
			se.setContentType(new BasicHeader(HTTP.CONTENT_TYPE,
					"application/json"));
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
