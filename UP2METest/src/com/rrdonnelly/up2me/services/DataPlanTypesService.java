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
import com.rrdonnelly.up2me.dao.DataPlanTypesDAO;

public class DataPlanTypesService {

	private static void refresh(Activity activity) {
		DataPlanTypesDAO.deleteDataPlanTypes(activity);
	}

	public static void syncDataPlanTypes(Activity currentActivity,
			String userToken, String userName, String salt, long loginUserId)
			throws ClientProtocolException, IOException, JSONException {

		/*
		 * userID=userName; userToken=userToken; salt=salt;
		 */

		HttpClient client = new DefaultHttpClient();
		String webServiceUrl = currentActivity.getResources().getString(
				R.string.webservice_url);
		HttpGet request = new HttpGet(webServiceUrl + "/DataPlanTypes");
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

		List<String> dataPlanTypesList = new ArrayList<String>();

		dataPlanTypesList = createDataPlanTypesList(responseJSONText.toString());

		Log.d("Insert: ", "Inserting Statements..");
		DataPlanTypesDAO dataPlanTypesDAO = new DataPlanTypesDAO();

		refresh(currentActivity);

		for (String dataPlanType : dataPlanTypesList) {
			dataPlanTypesDAO.addDataPlanType(dataPlanType, currentActivity);
		}

		Log.d("Insert: ", "Inserting Statements Completed.");

		Log.d("Insert: ", "Inserting Statements Completed.");

	}

	private static List<String> createDataPlanTypesList(String responseJSONText)
			throws JSONException {

		List<String> dataPlanTypesList = new ArrayList<String>();

		JSONArray dataPlanTypesArray = new JSONArray(responseJSONText);

		if (dataPlanTypesArray != JSONObject.NULL) {
			for (int i = 0; i < dataPlanTypesArray.length(); i++) {
				String dataPlanType = dataPlanTypesArray.getString(i);

				dataPlanTypesList.add(dataPlanType);
			}
		}

		return dataPlanTypesList;
	}

}
