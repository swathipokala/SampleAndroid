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
import android.os.StrictMode;

import com.rrdonnelly.up2me.R;
import com.rrdonnelly.up2me.dao.StateDAO;
import com.rrdonnelly.up2me.json.State;

public class StateService {

	private static void refresh(Activity activity) {
		StateDAO.deleteState(activity);
	}

	public static void syncState(Activity currentActivity)
			throws ClientProtocolException, IOException, JSONException {

		List<State> stateList = new ArrayList<State>();
		stateList = createStatesList(currentActivity);

		refresh(currentActivity);

		for (State state : stateList) {
			StateDAO.addState(state, currentActivity);
		}

	}

	private static List<State> createStatesList(Activity currentActivity) {
		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
				.permitAll().build();
		StrictMode.setThreadPolicy(policy);

		HttpClient client = new DefaultHttpClient();
		String webServiceUrl = currentActivity.getResources().getString(
				R.string.webservice_url);
		HttpGet request = new HttpGet(webServiceUrl + "/getStatesList");

		HttpResponse response;
		List<State> stateList = new ArrayList<State>();
		try {
			response = client.execute(request);
			BufferedReader rd = new BufferedReader(new InputStreamReader(
					response.getEntity().getContent()));
			StringBuilder responseJSONText = new StringBuilder();

			String line = "";
			while ((line = rd.readLine()) != null) {
				responseJSONText.append(line);
			}
			stateList = prepareStatesList(responseJSONText.toString());
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return stateList;
	}

	private static List<State> prepareStatesList(String responseJSONText)
			throws JSONException {

		List<State> statesList = new ArrayList<State>();

		JSONArray stateListArray = new JSONArray(responseJSONText);

		for (int i = 0; i < stateListArray.length(); i++) {
			JSONObject stateJSON = stateListArray.getJSONObject(i);
			State state = new State();

			if (stateJSON != null) {
				state.setStateId(stateJSON.getInt("stateId"));
				state.setStateCode(stateJSON.getString("stateCode"));
				state.setStateName(stateJSON.getString("stateName"));
				state.setCountryId(stateJSON.getInt("countryId"));
				state.setCountryName(stateJSON.getString("countryName"));
				state.setCountryCode(stateJSON.getString("countryCode"));
				state.setActive(stateJSON.getBoolean("isActive"));
			}

			statesList.add(state);
		}

		return statesList;
	}

}
