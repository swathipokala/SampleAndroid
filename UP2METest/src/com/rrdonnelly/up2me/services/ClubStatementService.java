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
import com.rrdonnelly.up2me.dao.ClubStatementDAO;
import com.rrdonnelly.up2me.json.ClubStatement;

public class ClubStatementService {

	public void syncClubStatements(Activity currentActivity, String userToken,
			String userName, String salt, long loginUserId)
			throws ClientProtocolException, IOException, JSONException {

		HttpClient client = new DefaultHttpClient();
		String webServiceUrl = currentActivity.getResources().getString(
				R.string.webservice_url);
		HttpGet request = new HttpGet(webServiceUrl + "/Providers/Statement");
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

		/*
		 * Map<String,List<ClubStatement>> clubStatementsMap = new
		 * HashMap<String,List<ClubStatement>>();
		 */

		/*
		 * List<ClubStatement> billClubStatements = new
		 * ArrayList<ClubStatement>(); List<ClubStatement> accountClubStatements
		 * = new ArrayList<ClubStatement>();
		 */

		List<ClubStatement> clubStatementsList = new ArrayList<ClubStatement>();

		clubStatementsList = createClubStatementList(responseJSONText
				.toString());

		Log.d("Insert: ", "Inserting Statements..");
		ClubStatementDAO clubStatementDAO = new ClubStatementDAO();

		refresh(currentActivity);

		for (ClubStatement clubStatement : clubStatementsList) {
			clubStatementDAO.addClubStatement(clubStatement, currentActivity);
		}

		Log.d("Insert: ", "Inserting Statements Completed.");

	}

	private static List<ClubStatement> createClubStatementList(
			String responseJSONText) throws JSONException {

		List<ClubStatement> clubStatementsList = new ArrayList<ClubStatement>();
		JSONArray clubStatementArray = new JSONArray(responseJSONText);
		for (int i = 0; i < clubStatementArray.length(); i++) {
			JSONObject clubStatementJSON = clubStatementArray.getJSONObject(i);
			ClubStatement clubStatement = new ClubStatement();

			if (clubStatementJSON != null) {

				clubStatement.setId(clubStatementJSON.getLong("clientId"));
				clubStatement.setName(clubStatementJSON.getString("name"));

				clubStatementsList.add(clubStatement);
			}// end if
		}

		return clubStatementsList;
	}

	private static void refresh(Activity activity) {
		ClubStatementDAO.deleteClubStatements(activity);
	}

}
