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

import com.google.gson.Gson;
import com.rrdonnelly.up2me.R;
import com.rrdonnelly.up2me.dao.CloudProviderDAO;
import com.rrdonnelly.up2me.json.CloudProvider;
import com.rrdonnelly.up2me.json.DocumentProviders;
import com.rrdonnelly.up2me.json.Image;
import com.rrdonnelly.up2me.util.ImageUtil;

public class CloudProviderService {

	public static void syncCloudProviders(Context currentActivity,
			String userToken, String userName, String salt, long loginUserId)
			throws ClientProtocolException, IOException, JSONException {

		refresh(currentActivity);
		List<CloudProvider> cloudProviderList = new ArrayList<CloudProvider>();

		cloudProviderList = getCloudProviders(currentActivity);

		Log.d("Insert: ", "Inserting Cloud..");

		refresh(currentActivity);

		for (CloudProvider cloudProvider : cloudProviderList) {
			CloudProviderDAO.addCloudProvider(cloudProvider, currentActivity);
		}

		Log.d("Insert: ", "Inserting Cloud Completed.");

	}
	
	public static List<CloudProvider> getCloudProviders(
			Context currentActivity) throws ClientProtocolException,
			IOException, JSONException {


		HttpClient client = new DefaultHttpClient();
		String webServiceUrl = currentActivity.getResources().getString(R.string.webservice_url);
		HttpGet request = new HttpGet(webServiceUrl + "/Providers/Cloud");

		HttpResponse response = client.execute(request);
		BufferedReader rd = new BufferedReader(new InputStreamReader(response
				.getEntity().getContent()));
		StringBuilder responseJSONText = new StringBuilder();

		String line = "";
		while ((line = rd.readLine()) != null) {
			responseJSONText.append(line);
		}

		List<CloudProvider> cloudProviderList = new ArrayList<CloudProvider>();

		cloudProviderList = createCloudProviderList(
				responseJSONText.toString(), currentActivity);

		return cloudProviderList;
	}
	

	private static void refresh(Context activity) {
		CloudProviderDAO.deleteCloudProviders(activity);
	}

	public static List<CloudProvider> createCloudProviderList(
			String responseJSONText, Context activity) throws JSONException {

		List<CloudProvider> cloudProviderList = new ArrayList<CloudProvider>();

		JSONArray cloudProviderArray = new JSONArray(responseJSONText);

		for (int i = 0; i < cloudProviderArray.length(); i++) {
			JSONObject cloudProviderJSON = cloudProviderArray.getJSONObject(i);
			CloudProvider cloudProvider = new CloudProvider();

			if (cloudProviderJSON != null) {

				cloudProvider
						.setClientId(cloudProviderJSON.getLong("clientId"));
				cloudProvider.setProviderName(cloudProviderJSON
						.getString("providerName"));
				cloudProvider.setImageText(cloudProviderJSON
						.getString("providerName"));

				Image displayImagePath = new Image();

				if (cloudProviderJSON.getJSONObject("displayImagePath") != null) {
					displayImagePath.setPath(cloudProviderJSON.getJSONObject(
							"displayImagePath").getString("path"));
					displayImagePath.setAltText(cloudProviderJSON
							.getJSONObject("displayImagePath").getString(
									"altText"));
					cloudProvider.setDisplayImagePath(displayImagePath);

					 ImageUtil.downloadImageIfNotExists(activity, displayImagePath.getPath());
				}

				cloudProviderList.add(cloudProvider);
			}// end if
		}
		return cloudProviderList;
	}
	
	public static String updateUserCloudProvidersListToBackEnd(Context activity, List<Integer> cloudProviders, long userId) {

		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
		StrictMode.setThreadPolicy(policy);

		HttpClient client = new DefaultHttpClient();
		String webServiceUrl = activity.getResources().getString(R.string.webservice_url);
		String returnMsg = "";
		try {
			HttpPost post = new HttpPost(webServiceUrl + "/user/UpdateCloudPrefs?userId=" + userId);

			JSONArray selectedCloudProviders = new JSONArray(cloudProviders);

			StringEntity se = new StringEntity(selectedCloudProviders.toString());
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
