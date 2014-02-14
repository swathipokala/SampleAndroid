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
import com.rrdonnelly.up2me.dao.PhoneByProviderDAO;
import com.rrdonnelly.up2me.json.Phone;
import com.rrdonnelly.up2me.json.PhoneByProvider;

public class PhoneByProviderService {

	private static void refresh(Activity activity) {
		PhoneByProviderDAO.deletePhonesService(activity);
	}

	public static void syncPhones(Activity currentActivity, String userToken,
			String userName, String salt, long loginUserId)
			throws ClientProtocolException, IOException, JSONException {

		HttpClient client = new DefaultHttpClient();
		String webServiceUrl = currentActivity.getResources().getString(
				R.string.webservice_url);
		HttpGet request = new HttpGet(webServiceUrl + "/Phones");
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

		List<PhoneByProvider> phoneByProviderList = new ArrayList<PhoneByProvider>();

		phoneByProviderList = createPhoneByProviderList(responseJSONText
				.toString());

		Log.d("Insert: ", "Inserting Statements..");
		PhoneByProviderDAO phoneByProviderDAO = new PhoneByProviderDAO();

		refresh(currentActivity);

		for (PhoneByProvider phoneByProvider : phoneByProviderList) {
			phoneByProviderDAO.addPhoneByProvider(phoneByProvider,
					currentActivity);
		}

		Log.d("Insert: ", "Inserting Statements Completed.");

		/*
		 * Map<String,List<Statement>> statementsMap = new
		 * HashMap<String,List<Statement>>();
		 * 
		 * List<Statement> billStatements = new ArrayList<Statement>();
		 * List<Statement> accountStatements = new ArrayList<Statement>();
		 * 
		 * JSONObject json = new JSONObject(responseJSONText.toString());
		 * 
		 * billStatements = createStatementList("Bill", json); accountStatements
		 * = createStatementList("Account", json);
		 * 
		 * statementsMap.put("Bill", billStatements);
		 * statementsMap.put("Account", accountStatements);
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
		 */

		Log.d("Insert: ", "Inserting Statements Completed.");

	}

	private static List<PhoneByProvider> createPhoneByProviderList(
			String responseJSONText) throws JSONException {

		List<PhoneByProvider> phoneByProviderList = new ArrayList<PhoneByProvider>();
		JSONArray phoneByProviderArray = new JSONArray(responseJSONText);
		for (int i = 0; i < phoneByProviderArray.length(); i++) {
			JSONObject phoneByProviderJSON = phoneByProviderArray
					.getJSONObject(i);
			PhoneByProvider phoneByProvider = new PhoneByProvider();

			if (phoneByProviderJSON != null) {

				phoneByProvider.setProviderId(phoneByProviderJSON
						.getLong("providerId"));
				phoneByProvider.setProviderName(phoneByProviderJSON
						.getString("providerName"));

				Phone phone = new Phone();
				List<Phone> phonesList = new ArrayList<Phone>();

				JSONArray phonesJSONArray = null;

				if (phoneByProviderJSON.get("phones") != JSONObject.NULL) {
					phonesJSONArray = phoneByProviderJSON
							.getJSONArray("phones");
				}

				if (phonesJSONArray != null) {
					for (int j = 0; j < phonesJSONArray.length(); j++) {
						JSONObject phoneJSON = phonesJSONArray.getJSONObject(j);
						if (phoneJSON != JSONObject.NULL) {
							phone.setId(phoneJSON.getLong("id"));
							phone.setManufacturer(phoneJSON
									.getString("manufacturer"));
							phone.setName(phoneJSON.getString("name"));
							phone.setColor(phoneJSON.getString("color"));
							phone.setDimensionsInInches(phoneJSON
									.getString("dimensionsInInches"));
							phone.setOperatingsystem(phoneJSON
									.getString("operatingsystem"));
							phone.setProcessor(phoneJSON.getString("processor"));
							phone.setConnectionspeed(phoneJSON
									.getString("connectionspeed"));
							phone.setDisplayResolution(phoneJSON
									.getString("displayResolution"));
							phone.setStorageMemoryInGB(phoneJSON
									.getDouble("storageMemoryInGB"));
							phone.setCameraMegapixels(phoneJSON
									.getDouble("cameraMegapixels"));
							phone.setBatteryLifeInHours(phoneJSON
									.getDouble("batteryLifeInHours"));
							phone.setPriceWithContract(phoneJSON
									.getDouble("priceWithContract"));
							phone.setWeightInOunces(phoneJSON
									.getDouble("weightInOunces"));
							phone.setAvailableDate(phoneJSON
									.getLong("availableDate"));
							phone.setDisplaySizeInInches(phoneJSON
									.getDouble("displaySizeInInches"));

							phonesList.add(phone);
						}
					}
					phoneByProvider.setPhones(phonesList);
				}// end if

				phoneByProviderList.add(phoneByProvider);
			}// end if
		}

		return phoneByProviderList;
	}

}
