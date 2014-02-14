package com.rrdonnelly.up2me.services;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Handler;
import android.widget.Toast;

/**
 * 
 * @author Team Android
 *
 */

public abstract class JsonDataCallback extends AsyncTask<String, String, String> implements CallbackReciever {
Handler handler;
Runnable callback;
Activity activity;
JSONObject dto;
String url="";
/**
 * Constructor to initialize
 * @param activity
 * @param dto
 */
public JsonDataCallback(Activity activity,JSONObject dto) 
{
	this.activity=activity;
	 SharedPreferences pref =activity.getSharedPreferences("Details", 0);
	 url = pref.getString("weburl", null);
     this.dto=dto;
}

/**
 * abstract implementation of receivedData of callback interface
 */
public abstract void receiveData(Object object);
@Override
protected void onPreExecute() {
	super.onPreExecute();
}

@Override
protected String doInBackground(String... aurl) {
	String results="";
    try {
    	if(isOnline()){
    	
             HttpClient httpClient = new DefaultHttpClient();
             if(aurl[0]!=null && aurl[0].contains("http"))
             {
            	url=aurl[0];
             }
             else
             {
            	 url=url+aurl[0];
             }
             HttpGet post = new HttpGet(url);
             post.setHeader("content-type", "text/json");
//                 StringEntity entity = new StringEntity(dto.toString());
//                 post.setEntity(entity);
                 HttpResponse resp = httpClient.execute(post);
                 String line = "";
                 StringBuilder total = new StringBuilder();

                 // Wrap a BufferedReader around the InputStream
                 BufferedReader rd = new BufferedReader(new
                              InputStreamReader(resp.getEntity().getContent()));

                 // Read response 
                 while ((line = rd.readLine()) != null) { 
                     total.append(line); 
                 }
             results=StringEscapeUtils.unescapeJava(total.toString());
             
                 if(results!=null && results.length()!=0)
                 {
                 if(results.startsWith("\""))
                 {
                	 results=results.substring(1,results.length());
                 }
                 if(results.endsWith("\""))
                 {
                	 results=results.substring(0,results.length()-1); 
                 }
                 }
    	}
    	else
    	{
    		return "No connection Available";
    	}
    } catch (Exception e) {
    	e.printStackTrace();
    }
    return results;
}

@Override
protected void onPostExecute(String jsondata) {
	 if(jsondata!=null && jsondata.equalsIgnoreCase("No connection Available"))
	 {
		 Toast.makeText(activity, "Internet connection Not Available", Toast.LENGTH_LONG).show();
	 }
	 if(jsondata!=null && !jsondata.equalsIgnoreCase("No connection Available"))
	 {
		 receiveData(jsondata);
	 }
}
public boolean isOnline() {
    ConnectivityManager connMgr = (ConnectivityManager) activity.getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
    NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
    return (networkInfo != null && networkInfo.isConnected());
}  
}