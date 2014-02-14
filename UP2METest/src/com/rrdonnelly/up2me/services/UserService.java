package com.rrdonnelly.up2me.services;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.os.StrictMode;

import com.rrdonnelly.up2me.R;
import com.rrdonnelly.up2me.dao.StateDAO;
import com.rrdonnelly.up2me.dao.UserDAO;
import com.rrdonnelly.up2me.dao.UserSettingsDAO;
import com.rrdonnelly.up2me.valueobjects.UserRegistration;
import com.rrdonnelly.up2me.valueobjects.UserSettings;

public class UserService {

	public static void getUserInformation(Context currentActivity,
			long loginId, String passWord) {

		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
				.permitAll().build();
		StrictMode.setThreadPolicy(policy);

		HttpClient client = new DefaultHttpClient();
		String webServiceUrl = currentActivity.getResources().getString(
				R.string.webservice_url);
		HttpGet request = new HttpGet(webServiceUrl
				+ "/user/getUserProfile?userId=" + loginId);

		HttpResponse response;
		try {
			response = client.execute(request);
			BufferedReader rd = new BufferedReader(new InputStreamReader(
					response.getEntity().getContent()));
			StringBuilder responseJSONText = new StringBuilder();

			String line = "";
			while ((line = rd.readLine()) != null) {
				responseJSONText.append(line);
			}

			insertUserInformation(currentActivity, responseJSONText.toString(),
					passWord);

		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public static void insertUserInformation(Context currentActivity,
			String responseJSONText, String passWord) throws JSONException {

		JSONObject json = new JSONObject(responseJSONText.toString());

		long userId = json.getInt("userId");

		// JSONArray userinfo = (JSONArray) json.get("userinfo");
		JSONObject userinfoObject = (JSONObject) json.get("userinfo");

		// JSONArray billingAddress = (JSONArray) json.get("billingAddress");
		JSONObject billingAddressObject = (JSONObject) json
				.get("billingAddress");

		// JSONArray mailingAddress = (JSONArray) json.get("mailingAddress");
		JSONObject mailingAddressObject = (JSONObject) json
				.get("mailingAddress");

		JSONArray preferencesList = (JSONArray) json.get("preferences");

		boolean userExits = UserDAO.isUserExists(String.valueOf(userId),
				currentActivity);

		UserRegistration userRegistration = new UserRegistration();
		// get User Login information
		userRegistration.setUserId(userinfoObject.getLong("userId"));
		userRegistration.setUserName(userinfoObject.getString("userName"));
		userRegistration.setUserName(json.get("email").toString());
		userRegistration.setPassword(passWord);
		userRegistration.setFirstname(userinfoObject.getString("firstName"));
		userRegistration.setLastname(userinfoObject.getString("lastName"));

		UserSettings userSettings = new UserSettings();

		userSettings.setUserId(userId);

		String showBadgesStatements = json.getString("showBadgesStatements");
		String showBadgesBills = json.getString("showBadgesBills");
		String showBadgesOffers = json.getString("showBadgesOffers");
		String showBadgesLibrary = json.getString("showBadgesLibrary");
		String showCalAlertsBills = json.getString("showCalAlertsBills");
		String showCalAlertsOfers = json.getString("showCalAlertsOfers");
		String showCloudSyncStatements = json
				.getString("showCloudSyncStatements");
		String showCloudSyncBills = json.getString("showCloudSyncBills");
		String showCloudSyncAll = json.getString("showCloudSyncAll");
		String showCloudSyncAppData = json.getString("showCloudSyncAppData");
		String timeout = json.getString("timeout");

		if (showBadgesStatements != null && showBadgesStatements != ""
				&& !"null".equalsIgnoreCase(showBadgesStatements)) {
			userSettings.setShowBadgesStatements(json
					.getBoolean("showBadgesStatements"));
		} else {
			userSettings.setShowBadgesStatements(false);
		}
		if (showBadgesBills != null && showBadgesBills != ""
				&& !"null".equalsIgnoreCase(showBadgesBills)) {
			userSettings.setShowBadgesBills(json.getBoolean("showBadgesBills"));
		} else {
			userSettings.setShowBadgesBills(false);
		}
		if (showBadgesOffers != null && showBadgesOffers != ""
				&& !"null".equalsIgnoreCase(showBadgesOffers)) {
			userSettings.setShowBadgesOffers(json
					.getBoolean("showBadgesOffers"));
		} else {
			userSettings.setShowBadgesOffers(false);
		}
		if (showBadgesLibrary != null && showBadgesLibrary != ""
				&& !"null".equalsIgnoreCase(showBadgesLibrary)) {
			userSettings.setShowBadgesLibrary(json
					.getBoolean("showBadgesLibrary"));
		} else {
			userSettings.setShowBadgesLibrary(false);
		}
		if (showCalAlertsBills != null && showCalAlertsBills != ""
				&& !"null".equalsIgnoreCase(showCalAlertsBills)) {
			userSettings.setCalendarAlertsBills(json
					.getBoolean("showCalAlertsBills"));
		} else {
			userSettings.setCalendarAlertsBills(false);
		}
		if (showCalAlertsOfers != null && showCalAlertsOfers != ""
				&& !"null".equalsIgnoreCase(showCalAlertsOfers)) {
			userSettings.setCalendarAlertsOffers(json
					.getBoolean("showCalAlertsOfers"));
		} else {
			userSettings.setCalendarAlertsOffers(false);
		}
		if (showCloudSyncStatements != null && showCloudSyncStatements != ""
				&& !"null".equalsIgnoreCase(showCloudSyncStatements)) {
			userSettings.setCloudSyncStatements(json
					.getBoolean("showCloudSyncStatements"));
		} else {
			userSettings.setCloudSyncStatements(false);
		}
		if (showCloudSyncBills != null && showCloudSyncBills != ""
				&& !"null".equalsIgnoreCase(showCloudSyncBills)) {
			userSettings.setCloudSyncBills(json
					.getBoolean("showCloudSyncBills"));
		} else {
			userSettings.setCloudSyncBills(false);
		}
		if (showCloudSyncAll != null && showCloudSyncAll != ""
				&& !"null".equalsIgnoreCase(showCloudSyncAll)) {
			userSettings.setCloudSyncAll(json.getBoolean("showCloudSyncAll"));
		} else {
			userSettings.setCloudSyncAll(false);
		}
		if (showCloudSyncAppData != null && showCloudSyncAppData != ""
				&& !"null".equalsIgnoreCase(showCloudSyncAppData)) {
			userSettings.setCloudSyncAppData(json
					.getBoolean("showCloudSyncAppData"));
		} else {
			userSettings.setCloudSyncAppData(false);
		}
		if (timeout != null && timeout != ""
				&& !"null".equalsIgnoreCase(timeout)) {
			userSettings.setSetTimeOut(json.getInt("timeout"));
		} else {
			userSettings.setSetTimeOut(0);
		}

		// get User Address Information
		userRegistration.setStreetaddress1(mailingAddressObject
				.getString("street1"));
		userRegistration.setStreetaddress2(mailingAddressObject
				.getString("street2"));
		userRegistration.setCity(mailingAddressObject.getString("city"));
		userRegistration.setZip(mailingAddressObject.getString("zipCode"));
		String stateName = StateDAO.getStateNameByStateId(currentActivity,
				mailingAddressObject.getString("stateId"));
		userRegistration.setState(stateName);
		userRegistration.setMobileno1(json.getString("mobileNo"));

		if (userExits) {
			UserDAO.updateUserDetails(userRegistration, currentActivity);
			UserSettingsDAO.updateUserToggles(userSettings, currentActivity);
			UserDAO.createSystemTagsForUser(currentActivity,userId);
		} else {
			UserDAO.insertUserDetails(userRegistration, currentActivity, userId);
			UserSettingsDAO.saveUserToggles(userSettings, currentActivity);
		}

		UserDAO.insertAllProvidersList(currentActivity, preferencesList, userId);
	}

}
