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

import com.google.gson.Gson;
import com.rrdonnelly.up2me.R;
import com.rrdonnelly.up2me.dao.StatementCostDAO;
import com.rrdonnelly.up2me.json.StatementCost;

public class StatementCostService {

	private static void refresh(Activity activity) {
		StatementCostDAO.deleteStatementsCost(activity);
	}

	public static void syncStatementsCost(Activity currentActivity,
			String userToken, String userName, String salt, long loginUserId)
			throws ClientProtocolException, IOException, JSONException {

		HttpClient client = new DefaultHttpClient();
		String webServiceUrl = currentActivity.getResources().getString(
				R.string.webservice_url);
		HttpGet request = new HttpGet(webServiceUrl + "/user/StatementsCost");
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

		Gson gson = new Gson();

		List<StatementCost> statementCostList = new ArrayList<StatementCost>();

		statementCostList = createStatementCostList(responseJSONText.toString());

		Log.d("Insert: ", "Inserting Statements..");

		refresh(currentActivity);

		for (StatementCost statementCost : statementCostList) {
			StatementCostDAO.addStatementCost(statementCost, loginUserId,
					currentActivity);
		}

		Log.d("Insert: ", "Inserting Statements Completed.");

	}

	private static List<StatementCost> createStatementCostList(
			String responseJSONText) throws JSONException {

		List<StatementCost> statementCostList = new ArrayList<StatementCost>();

		JSONArray statementCostArray = new JSONArray(responseJSONText);

		for (int i = 0; i < statementCostArray.length(); i++) {
			JSONObject statementCostJSON = statementCostArray.getJSONObject(i);
			StatementCost statementCost = new StatementCost();

			if (statementCostJSON != null) {
				statementCost.setYear(statementCostJSON.getInt("year"));
				statementCost.setMonth(statementCostJSON.getString("month"));
				statementCost.setDataCost(statementCostJSON
						.getDouble("dataCost"));
				statementCost.setTextUsage(statementCostJSON
						.getLong("textUsage"));
				statementCost.setTotalCost(statementCostJSON
						.getDouble("totalCost"));
				statementCost.setMinutesUsed(statementCostJSON
						.getDouble("minutesUsed"));
				statementCost.setLineCount(statementCostJSON
						.getInt("lineCount"));
				statementCost.setContractStartDate(statementCostJSON
						.getString("contractStartDate"));
				statementCost.setContractEndDate(statementCostJSON
						.getString("contractEndDate"));
				statementCost.setProviderName(statementCostJSON
						.getString("providerName"));
				statementCost.setPlanCost(statementCostJSON
						.getString("planCost"));
				statementCost.setAccessDiscount(statementCostJSON
						.getDouble("accessDiscount"));
				statementCost.setDataOverageCost(statementCostJSON
						.getDouble("dataOverageCost"));
				statementCost.setMinutesOverageCost(statementCostJSON
						.getDouble("minutesOverageCost"));
				statementCost.setTextOverageCost(statementCostJSON
						.getDouble("textOverageCost"));

				statementCostList.add(statementCost);
			}// end if
		}
		return statementCostList;
	}

}
