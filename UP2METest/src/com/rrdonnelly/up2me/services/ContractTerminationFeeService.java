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
import com.rrdonnelly.up2me.dao.ContractTerminationFeeDAO;
import com.rrdonnelly.up2me.json.ContractTerminationFee;

public class ContractTerminationFeeService {

	public void syncContractTerminationFee(Activity currentActivity,
			String userToken, String userName, String salt, long loginUserId)
			throws ClientProtocolException, IOException, JSONException {

		HttpClient client = new DefaultHttpClient();
		String webServiceUrl = currentActivity.getResources().getString(
				R.string.webservice_url);
		HttpGet request = new HttpGet(webServiceUrl + "/ContractTerminationFee");
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

		// Gson gson = new GsonBuilder().serializeNulls().create();
		// Object value = new Gson().fromJson(responseJSONText.toString(), new
		// TypeToken<List<Offer>>() {}.getType());
		/*
		 * Map<String,List<Statement>> statements = new
		 * Gson().fromJson(responseJSONText.toString(), new
		 * TypeToken<Map<String,List<Statement>>>() {}.getType());
		 * 
		 * List<Statement> billStatements = statements.get("Bill");
		 * List<Statement> accountStatements = statements.get("Account");
		 * 
		 * Log.d("Insert: ", "Inserting Statements.."); StatementDAO
		 * statementDAO = new StatementDAO();
		 * 
		 * statementDAO.refresh();
		 * 
		 * for(Statement billStatement: billStatements){
		 * statementDAO.addStatement(billStatement); }
		 * 
		 * for(Statement accountStatement: accountStatements){
		 * statementDAO.addStatement(accountStatement); }
		 * 
		 * Log.d("Insert: ", "Inserting Statements Completed.");
		 */

		List<ContractTerminationFee> contractTerminationFeeList = new ArrayList<ContractTerminationFee>();

		contractTerminationFeeList = createContractTerminationFeeList(responseJSONText
				.toString());

		Log.d("Insert: ", "Inserting Statements..");
		ContractTerminationFeeDAO contractTerminationFeeDAO = new ContractTerminationFeeDAO();

		refresh(currentActivity);

		for (ContractTerminationFee contractTerminationFee : contractTerminationFeeList) {
			contractTerminationFeeDAO.addContractTerminationFee(
					contractTerminationFee, currentActivity);
		}

		Log.d("Insert: ", "Inserting Statements Completed.");

		Log.d("Insert: ", "Inserting Statements Completed.");

	}

	private static List<ContractTerminationFee> createContractTerminationFeeList(
			String responseJSONText) throws JSONException {

		List<ContractTerminationFee> contractTerminationFeeList = new ArrayList<ContractTerminationFee>();
		JSONArray contractTerminationFeeArray = new JSONArray(responseJSONText);
		for (int i = 0; i < contractTerminationFeeArray.length(); i++) {
			JSONObject contractTerminationFeeJSON = contractTerminationFeeArray
					.getJSONObject(i);
			ContractTerminationFee contractTerminationFee = new ContractTerminationFee();

			if (contractTerminationFeeJSON != null) {

				contractTerminationFee.setProviderId(contractTerminationFeeJSON
						.getLong("providerId"));
				contractTerminationFee
						.setProviderName(contractTerminationFeeJSON
								.getString("providerName"));

				List<Double> feesList = new ArrayList<Double>();

				JSONArray feesJSONArray = null;

				if (contractTerminationFeeJSON.get("fees") != JSONObject.NULL) {
					feesJSONArray = contractTerminationFeeJSON
							.getJSONArray("fees");
				}

				if (feesJSONArray != null) {
					for (int k = 0; k < feesJSONArray.length(); k++) {
						if (feesJSONArray.getString(k).length() != 0) {
							feesList.add(feesJSONArray.getDouble(k));
						}
					}
					contractTerminationFee.setFees(feesList);
				}// end if

				contractTerminationFeeList.add(contractTerminationFee);
			}// end if
		}

		return contractTerminationFeeList;
	}

	public void refresh(Activity activity) {
		ContractTerminationFeeDAO.deleteContractTerminationFees(activity);
		ContractTerminationFeeDAO.deleteContractTerminationFeesList(activity);
	}

}
