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
import com.rrdonnelly.up2me.dao.DataPlanDAO;
import com.rrdonnelly.up2me.json.DataPlan;

public class DataPlanService {

	private static void refresh(Activity activity) {
		DataPlanDAO.deleteDataPlans(activity);
		DataPlanDAO.deleteDataPlanLines(activity);
	}

	public static void syncDataPlans(Activity currentActivity,
			String userToken, String userName, String salt, long loginUserId)
			throws ClientProtocolException, IOException, JSONException {

		HttpClient client = new DefaultHttpClient();
		String webServiceUrl = currentActivity.getResources().getString(
				R.string.webservice_url);
		HttpGet request = new HttpGet(webServiceUrl + "/DataPlans");
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

		List<DataPlan> dataPlanList = new ArrayList<DataPlan>();

		dataPlanList = createDataPlanList(responseJSONText.toString());

		Log.d("Insert: ", "Inserting Statements..");
		DataPlanDAO dataPlanDAO = new DataPlanDAO();

		refresh(currentActivity);

		for (DataPlan dataPlan : dataPlanList) {
			dataPlanDAO.addDataPlan(dataPlan, currentActivity);
		}

		Log.d("Insert: ", "Inserting Statements Completed.");

		Log.d("Insert: ", "Inserting Statements Completed.");

	}

	private static List<DataPlan> createDataPlanList(String responseJSONText)
			throws JSONException {

		List<DataPlan> dataPlanList = new ArrayList<DataPlan>();
		JSONArray dataPlanArray = new JSONArray(responseJSONText);
		for (int i = 0; i < dataPlanArray.length(); i++) {
			JSONObject dataPlanJSON = dataPlanArray.getJSONObject(i);
			DataPlan dataPlan = new DataPlan();

			if (dataPlanJSON != null) {

				dataPlan.setProviderName(dataPlanJSON.getString("providerName"));
				dataPlan.setDataValue(dataPlanJSON.getString("dataValue"));
				dataPlan.setDataUnit(dataPlanJSON.getString("dataUnit"));
				dataPlan.setPlanType(dataPlanJSON.getString("planType"));
				dataPlan.setMinutesValue(dataPlanJSON.getString("minutesValue"));
				dataPlan.setTextMessageValue(dataPlanJSON
						.getString("textMessageValue"));

				List<Double> linesList = new ArrayList<Double>();

				JSONArray linesJSONArray = null;

				if (dataPlanJSON.get("lines") != JSONObject.NULL) {
					linesJSONArray = dataPlanJSON.getJSONArray("lines");
				}

				if (linesJSONArray != null) {
					for (int k = 0; k < linesJSONArray.length(); k++) {
						if (linesJSONArray.getString(k).length() != 0) {
							linesList.add(linesJSONArray.getDouble(k));
						}
					}
					dataPlan.setLines(linesList);
				}// end if

				dataPlanList.add(dataPlan);
			}// end if
		}

		return dataPlanList;
	}

}
