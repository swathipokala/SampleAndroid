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
import com.rrdonnelly.up2me.dao.DataPlanProviderNamesDAO;

public class DataPlanProviderNamesService {

	private static void refresh(Activity activity) {
		DataPlanProviderNamesDAO.deleteDataPlanProviderNames(activity);
	}

	public static void syncDataPlanProviderNames(Activity currentActivity,
			String userToken, String userName, String salt, long loginUserId)
			throws ClientProtocolException, IOException, JSONException {

		HttpClient client = new DefaultHttpClient();
		String webServiceUrl = currentActivity.getResources().getString(
				R.string.webservice_url);
		HttpGet request = new HttpGet(webServiceUrl + "/DataPlanProviderNames");
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

		List<String> dataPlanProviderNameList = new ArrayList<String>();

		dataPlanProviderNameList = createDataPlanProviderNameList(responseJSONText
				.toString());

		Log.d("Insert: ", "Inserting Statements..");
		DataPlanProviderNamesDAO dataPlanProviderNamesDAO = new DataPlanProviderNamesDAO();

		refresh(currentActivity);

		for (String dataPlanProviderName : dataPlanProviderNameList) {
			dataPlanProviderNamesDAO.addDataPlanProviderName(
					dataPlanProviderName, currentActivity);
		}

		Log.d("Insert: ", "Inserting Statements Completed.");

		Log.d("Insert: ", "Inserting Statements Completed.");

	}

	private static List<String> createDataPlanProviderNameList(
			String responseJSONText) throws JSONException {

		List<String> dataPlanProviderNameList = new ArrayList<String>();

		JSONArray dataPlanProviderNameArray = new JSONArray(responseJSONText);

		if (dataPlanProviderNameArray != JSONObject.NULL) {
			for (int i = 0; i < dataPlanProviderNameArray.length(); i++) {
				String dataPlanProviderName = dataPlanProviderNameArray
						.getString(i);

				dataPlanProviderNameList.add(dataPlanProviderName);
			}
		}

		return dataPlanProviderNameList;
	}

}
