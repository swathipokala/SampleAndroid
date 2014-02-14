package com.rrdonnelly.up2me.services;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;

import android.app.Activity;

import com.rrdonnelly.up2me.R;

public class AdminService {
	public static long getServerTime(Activity currentActivity) {
		long serverTime = 0l;
		try {
			HttpClient client = new DefaultHttpClient();
			String webServiceUrl = currentActivity.getResources().getString(
					R.string.webservice_url);

			String serverTimeUrl = webServiceUrl + "/ServerTime";
			HttpGet request = new HttpGet(serverTimeUrl);

			HttpResponse response = client.execute(request);
			BufferedReader rd = new BufferedReader(new InputStreamReader(
					response.getEntity().getContent()));
			StringBuilder responseJSONText = new StringBuilder();
			String line = "";
			while ((line = rd.readLine()) != null) {
				responseJSONText.append(line);
			}
			JSONObject json = new JSONObject(responseJSONText.toString());
			serverTime = Long.parseLong(String.valueOf(json.get("ServerTime")));
			responseJSONText = null;

			line = null;
			rd = null;
			webServiceUrl = null;
			serverTimeUrl = null;
			request = null;
			response = null;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return serverTime;
	}
}
