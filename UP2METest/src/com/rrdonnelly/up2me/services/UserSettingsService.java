package com.rrdonnelly.up2me.services;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HTTP;
import org.json.JSONObject;

import android.app.Activity;
import android.os.StrictMode;

import com.rrdonnelly.up2me.R;
import com.rrdonnelly.up2me.valueobjects.UserRegistration;
import com.rrdonnelly.up2me.valueobjects.UserSettings;

public class UserSettingsService {

	public static String updateUserProfileDetails(Activity currentActivity,
			UserRegistration userProfileData) {

		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
				.permitAll().build();
		StrictMode.setThreadPolicy(policy);

		HttpClient client = new DefaultHttpClient();
		String webServiceUrl = currentActivity.getResources().getString(
				R.string.webservice_url);
		JSONObject json = new JSONObject();
		String returnMsg = "";
		try {

			HttpPost post = new HttpPost(webServiceUrl + "/UpdateUserDetails");
			json.put("userId", userProfileData.getUserId());
			json.put("email", userProfileData.getUserName());
			json.put("firstName", userProfileData.getFirstname());
			json.put("lastName", userProfileData.getLastname());
			json.put("street1", userProfileData.getStreetaddress1());
			json.put("street2", userProfileData.getStreetaddress2());
			json.put("city", userProfileData.getCity());
			json.put("state", userProfileData.getState());
			json.put("zip", userProfileData.getZip());
			json.put("mobileNo", userProfileData.getMobileno1());

			StringEntity se = new StringEntity(json.toString());
			se.setContentType(new BasicHeader(HTTP.CONTENT_TYPE,
					"application/json"));
			post.setEntity(se);
			HttpResponse response = client.execute(post);

			BufferedReader rd = new BufferedReader(new InputStreamReader(
					response.getEntity().getContent()));
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

	public static String updateUserSettings(Activity currentActivity,
			UserSettings userSettingsData) {

		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
				.permitAll().build();
		StrictMode.setThreadPolicy(policy);

		HttpClient client = new DefaultHttpClient();
		String webServiceUrl = currentActivity.getResources().getString(
				R.string.webservice_url);
		JSONObject json = new JSONObject();
		String returnMsg = "";
		try {
			HttpPost post = new HttpPost(webServiceUrl + "/UpdateUserSettings");

			json.put("userId", userSettingsData.getUserId());

			// json.put("userName", userSettingsData.getUserName());
			json.put("email", userSettingsData.getPrimaryEmail());
			json.put("password", userSettingsData.getPassword());

			json.put("showBadgesStatements",
					userSettingsData.isShowBadgesStatements());
			json.put("showBadgesBills", userSettingsData.isShowBadgesBills());
			json.put("showBadgesOffers", userSettingsData.isShowBadgesOffers());
			json.put("showBadgesLibrary",
					userSettingsData.isShowBadgesLibrary());

			json.put("showCalAlertsBills",
					userSettingsData.isCalendarAlertsBills());
			json.put("showCalAlertsOfers",
					userSettingsData.isCalendarAlertsOffers());

			json.put("showCloudSyncStatements",
					userSettingsData.isCloudSyncStatements());
			json.put("showCloudSyncBills", userSettingsData.isCloudSyncBills());
			json.put("showCloudSyncAll", userSettingsData.isCloudSyncAll());
			json.put("showCloudSyncAppData",
					userSettingsData.isCloudSyncAppData());

			json.put("timeout", userSettingsData.getSetTimeOut());

			StringEntity se = new StringEntity(json.toString());
			se.setContentType(new BasicHeader(HTTP.CONTENT_TYPE,
					"application/json"));
			post.setEntity(se);
			HttpResponse response = client.execute(post);

			BufferedReader rd = new BufferedReader(new InputStreamReader(
					response.getEntity().getContent()));
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
