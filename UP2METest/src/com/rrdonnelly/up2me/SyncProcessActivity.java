package com.rrdonnelly.up2me;

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

import com.rrdonnelly.up2me.R;
import com.rrdonnelly.up2me.R.id;
import com.rrdonnelly.up2me.R.layout;
import com.rrdonnelly.up2me.R.menu;
import com.rrdonnelly.up2me.dao.CloudProviderDAO;
import com.rrdonnelly.up2me.dao.ClubOfferDAO;
import com.rrdonnelly.up2me.dao.ClubStatementDAO;
import com.rrdonnelly.up2me.dao.ContractTerminationFeeDAO;
import com.rrdonnelly.up2me.dao.DataPlanDAO;
import com.rrdonnelly.up2me.dao.DataPlanProviderNamesDAO;
import com.rrdonnelly.up2me.dao.DataPlanTypesDAO;
import com.rrdonnelly.up2me.dao.DocumentProvidersDAO;
import com.rrdonnelly.up2me.dao.OfferDAO;
import com.rrdonnelly.up2me.dao.PhoneByProviderDAO;
import com.rrdonnelly.up2me.dao.OfferProvidersDAO;
import com.rrdonnelly.up2me.dao.SettingsDAO;
import com.rrdonnelly.up2me.dao.StatementCostDAO;
import com.rrdonnelly.up2me.dao.StatementDAO;
import com.rrdonnelly.up2me.dao.TagDAO;
import com.rrdonnelly.up2me.dao.UserDAO;
import com.rrdonnelly.up2me.dao.UserSubscriptionOfferDAO;
import com.rrdonnelly.up2me.dao.UserSubscriptionStatementDAO;
import com.rrdonnelly.up2me.json.DocumentProviders;
import com.rrdonnelly.up2me.services.CloudProviderService;
import com.rrdonnelly.up2me.services.DocumentProvidersService;
import com.rrdonnelly.up2me.services.OfferProvidersService;
import com.rrdonnelly.up2me.services.OfferService;
import com.rrdonnelly.up2me.services.StatementsService;
import com.rrdonnelly.up2me.services.TagService;
import com.rrdonnelly.up2me.services.UserLibraryService;
import com.rrdonnelly.up2me.util.ControlApplication;
import com.rrdonnelly.up2me.valueobjects.User;
import com.rrdonnelly.up2me.valueobjects.UserFavorite;
import com.rrdonnelly.up2me.valueobjects.UserRegistration;

import android.os.Bundle;
import android.os.Handler;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.TextureView;
import android.view.Window;
import android.widget.ProgressBar;
import android.widget.TextView;

public class SyncProcessActivity extends Activity {

	public String userName=null;
    public String userToken=null;
    public String salt=null;
    public long loginUserId = 0l;
	
    private ProgressBar progressBar;
    private Activity currentActivity;
    private TextView sync_status_message;
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_sync_process);
		
		currentActivity = this;
		
		userToken=getIntent().getStringExtra("usertoken");
		userName=getIntent().getStringExtra("userName");
        salt=getIntent().getStringExtra("salt");
        loginUserId=getIntent().getLongExtra("loginUserId", 0l);

        progressBar = (ProgressBar) findViewById(R.id.progessbarSync);
        sync_status_message = (TextView) findViewById(R.id.sync_status_message);
		progressBar.setProgress(0);
		
		setProgressBarVisibility(true);
		
		SyncProcessActivity prevActivity = (SyncProcessActivity)getLastNonConfigurationInstance();
		   if(prevActivity!= null) { 
		       // So the orientation did change
		       // Restore some fields
			   this.userToken=getIntent().getStringExtra("usertoken");
			   this.userName=getIntent().getStringExtra("userName");
			   this.salt=getIntent().getStringExtra("salt");
			   this.loginUserId=getIntent().getLongExtra("loginUserId", 0l);
			   currentActivity = this;
		   } else {
		   //getProfile();
		 // Start lengthy operation in a background thread
        new Thread(new Runnable() {
			public void run() {
				//sync_status_message.setText("Synchronizing Offer Details...");
				
				long lastSyncDateTime = 0l;
				SettingsDAO settingsDAO = new SettingsDAO();
				lastSyncDateTime = settingsDAO.getLastSyncDateTime(currentActivity,loginUserId);
				
				runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                    	sync_status_message.setText("Synchronizing Statements Details...");        
                    }
                });
				
				syncStatementsProcess(currentActivity, userToken, userName, salt, loginUserId, lastSyncDateTime);
				
				runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
        				sync_status_message.setText("Synchronizing Offer Details...");        
                    }
                });
				

				syncOffersProcess(currentActivity, userToken, userName, salt, loginUserId, lastSyncDateTime);

				runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
        				sync_status_message.setText("Synchronizing Cloud Providers Details...");        
                    }
                });
				
				syncCloudProvidersProcess(currentActivity, userToken, userName, salt, loginUserId, lastSyncDateTime);

				
				runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
        				sync_status_message.setText("Synchronizing Offer Providers Details...");        
                    }
                });

				syncOfferProvidersProcess(currentActivity, userToken, userName, salt, loginUserId, lastSyncDateTime);


				runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
        				sync_status_message.setText("Synchronizing Tag Details...");        
                    }
                });
				
				
				syncTagsProcess(currentActivity, userToken, userName, salt, loginUserId, lastSyncDateTime);

				
				runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
        				sync_status_message.setText("Synchronizing Document Providers Details...");        
                    }
                });
				
				syncDocumentProvidersProcess(currentActivity, userToken, userName, salt, loginUserId, lastSyncDateTime);
				
				runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
        				sync_status_message.setText("Synchronizing User Favorites...");        
                    }
                });

				syncUserFavorite(currentActivity,loginUserId,userToken,userName,salt, lastSyncDateTime);
				
				runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
        				sync_status_message.setText("Synchronizing User Library Details...");        
                    }
                });
				
				synUserLibrariesProcess(currentActivity, userToken, userName, salt, loginUserId, lastSyncDateTime);
				
				runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
        				sync_status_message.setText("Synchronizing Bills and CashFlow Categories Details...");        
                    }
                });
				
				synStatementCategoryProcess(currentActivity, userToken, userName, salt, loginUserId, lastSyncDateTime);
				
				
				settingsDAO.addSyncDateTime(currentActivity,loginUserId);
				
				setProgressBarVisibility(false);
		        
		        Intent dashBoardIntent = new Intent(SyncProcessActivity.this, DashBoardActivity.class);
		        dashBoardIntent.putExtra("usertoken", userToken);
		        dashBoardIntent.putExtra("userName", userName);
		        dashBoardIntent.putExtra("salt", salt);
		        dashBoardIntent.putExtra("loginUserId", loginUserId);
		        dashBoardIntent.putExtra("AlarmTask", "true");
		        startActivityForResult(dashBoardIntent, 0);
		        
				/*// Update the progress bar
				progressBarbHandler.post(new Runnable() {
					public void run() {
						mProgress.setProgress(100);
					}
				});*/
			}
        }).start();
		   }
		//syncProcess(this, userToken, userID, salt);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.sync_process, menu);
		return true;
	}
	
	/*private void syncProcess(Activity activity, String userToken, String userName, String salt, long loginUserId){
		try{
			long lastSyncDateTime = 0l;
			SettingsDAO settingsDAO = new SettingsDAO();
			lastSyncDateTime = settingsDAO.getLastSyncDateTime(activity);
			
			new StatementDAO().syncStatements(activity, userToken, userName, salt, loginUserId, lastSyncDateTime);
			new OfferDAO().syncOffers(activity, userToken, userName, salt, loginUserId, lastSyncDateTime);
			//new StatementCostDAO().syncStatementsCost(activity, userToken, userName, salt, loginUserId);
			//new ClubOfferDAO().syncClubOffers(activity, userToken, userName, salt, loginUserId);
			//new ClubStatementDAO().syncClubStatements(activity, userToken, userName, salt, loginUserId);
			//new CloudProviderDAO().syncCloudProviders(activity, userToken, userName, salt, loginUserId);
			new OfferProvidersDAO().syncOfferProviders(activity, userToken, userName, salt, loginUserId, lastSyncDateTime);
			new DocumentProvidersDAO().syncDocumentProviders(activity, userToken, userName, salt, loginUserId, lastSyncDateTime);
			
			//Commented below since we are non synching these right now.
			//new ContractTerminationFeeDAO().syncContractTerminationFee(activity, userToken, userName, salt, loginUserId);
			//new DataPlanDAO().syncDataPlans(activity, userToken, userName, salt, loginUserId);
			//new DataPlanTypesDAO().syncDataPlanTypes(activity, userToken, userName, salt, loginUserId);
			//new DataPlanProviderNamesDAO().syncDataPlanProviderNames(activity, userToken, userName, salt, loginUserId);
			//new PhoneByProviderDAO().syncPhones(activity, userToken, userName, salt, loginUserId);
			//new UserSubscriptionOfferDAO().syncClubProviders(activity, userToken, userName, salt, loginUserId);
			//new UserSubscriptionStatementDAO().syncClubProvider(activity, userToken, userName, salt, loginUserId);
			settingsDAO.addSyncDateTime(activity);
		}
		catch(ClientProtocolException cpe){
			cpe.printStackTrace();
		}
		catch(IOException ioe){
			ioe.printStackTrace();
		}
		catch(JSONException jsone){
			jsone.printStackTrace();
		}
		
	}*/
	
	
	
	public void syncStatementsProcess(Context activity, String userToken, String userName, String salt, long loginUserId, long lastSyncDateTime){
		try{
			StatementsService.syncStatementsToBackend(activity, loginUserId);
			StatementsService.syncStatements(activity, userToken, userName, salt, loginUserId, lastSyncDateTime);
		}
		catch(ClientProtocolException cpe){
			cpe.printStackTrace();
		}
		catch(IOException ioe){
			ioe.printStackTrace();
		}
		catch(JSONException jsone){
			jsone.printStackTrace();
		}
		catch(Exception e){
			e.printStackTrace();
		}
		
	}

	public void syncOffersProcess(Context activity, String userToken, String userName, String salt, long loginUserId, long lastSyncDateTime){
		try{
			OfferService.syncOffersToBackend(activity, loginUserId);
			OfferService.syncOffers(activity, userToken, userName, salt, loginUserId, lastSyncDateTime);
		}
		catch(ClientProtocolException cpe){
			cpe.printStackTrace();
		}
		catch(IOException ioe){
			ioe.printStackTrace();
		}
		catch(JSONException jsone){
			jsone.printStackTrace();
		}
		catch(Exception e){
			e.printStackTrace();
		}
		
	}
	
	public void syncCloudProvidersProcess(Context activity, String userToken, String userName, String salt, long loginUserId, long lastSyncDateTime){
		try{
			//DocumentProvidersService.syncCloudProvidersListToBackend(activity, loginUserId);
			CloudProviderService.syncCloudProviders(activity, userToken, userName, salt, loginUserId);
		}
		catch(ClientProtocolException cpe){
			cpe.printStackTrace();
		}
		catch(IOException ioe){
			ioe.printStackTrace();
		}
		catch(JSONException jsone){
			jsone.printStackTrace();
		}
		
	}
	
	public void syncOfferProvidersProcess(Context context, String userToken, String userName, String salt, long loginUserId, long lastSyncDateTime){
		try{
			OfferProvidersService.syncOfferProvidersListToBackend(context, loginUserId);
			OfferProvidersService.syncOfferProviders(context, userToken, userName, salt, loginUserId, lastSyncDateTime);
		}
		catch(ClientProtocolException cpe){
			cpe.printStackTrace();
		}
		catch(IOException ioe){
			ioe.printStackTrace();
		}
		catch(JSONException jsone){
			jsone.printStackTrace();
		}
		
	}

	public void syncDocumentProvidersProcess(Context activity, String userToken, String userName, String salt, long loginUserId, long lastSyncDateTime){
		try{
			DocumentProvidersService.syncDocumentProvidersListToBackend(activity, loginUserId);
			DocumentProvidersService.syncDocumentProviders(activity, userToken, userName, salt, loginUserId, lastSyncDateTime);
		}
		catch(ClientProtocolException cpe){
			cpe.printStackTrace();
		}
		catch(IOException ioe){
			ioe.printStackTrace();
		}
		catch(JSONException jsone){
			jsone.printStackTrace();
		}
		
	}


	
	public void synUserLibrariesProcess(Context activity, String userToken, String userName, String salt, long loginUserId, long lastSyncDateTime){
		try{
			UserLibraryService.synUserLibraries(activity, userToken, userName, salt, loginUserId, lastSyncDateTime);
		}
		catch(ClientProtocolException cpe){
			cpe.printStackTrace();
		}
		catch(IOException ioe){
			ioe.printStackTrace();
		}
		catch(JSONException jsone){
			jsone.printStackTrace();
		}
		
	}
	
	public void synStatementCategoryProcess(Context activity, String userToken, String userName, String salt, long loginUserId, long lastSyncDateTime){
		try{
			StatementsService.synStatementCategory(activity);
		}
		catch(Exception jsone){
			jsone.printStackTrace();
		}
		
	}
	
	
	public void getProfile(){
		HttpClient client = new DefaultHttpClient();
		String webServiceUrl = getResources().getString(R.string.webservice_url);
		HttpGet request = new HttpGet(webServiceUrl + "/users/getUserProfile?userId="+loginUserId);
		
		String firstName =""; 
		String lastName =""; 
		String streeAddr1 =""; 
		String streeAddr2 =""; 
		String city =""; 
		String state=""; 
		String zip=""; 
		String mobile=""; 
		
		HttpResponse response;
		try {
			response = client.execute(request);
		
        BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
        StringBuilder responseJSONText = new StringBuilder();
        
        String line = "";
        while ((line = rd.readLine()) != null) {
        	responseJSONText.append(line);
        }
        
        UserRegistration user = new UserRegistration();
        user.setUserId(loginUserId);
        JSONObject json = new JSONObject(responseJSONText.toString());
		user.setFirstname(json.getString("firstName"));
		user.setLastname(json.getString("lastName")); 
		user.setStreetaddress1(json.getJSONObject("mailingAddress").getString("street1")); 
		user.setStreetaddress2(json.getJSONObject("mailingAddress").getString("street2")); 
		user.setCity(json.getJSONObject("mailingAddress").getString("city"));
		user.setState(json.getJSONObject("mailingAddress").getString("state")); 
		user.setZip(json.getJSONObject("mailingAddress").getString("zip")); 
		//user.setMobileno1(json.getJSONObject("mailingAddress").getString("phoneNumber1"));
		user.setMobileno1(json.getString("mobileNo"));
        
		UserDAO userDao= new UserDAO();
		userDao.updateUserDetails(user, this);
		
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void syncTagsProcess(Context activity, String userToken, String userName, String salt, long loginUserId, long lastSyncDateTime){
		try{
			/*TagService.syncTagsToBackend(activity, loginUserId);
			TagService.syncUserOfferTagsToBackend(activity, loginUserId);
			TagService.syncUserStatementTagsToBackend(activity, loginUserId);*/
			
			TagService.syncTags(activity, loginUserId);
			TagService.syncUserOfferTags(activity, loginUserId);
			TagService.syncUserStatementTags(activity, loginUserId);
			UserDAO.createSystemTagsForUser(currentActivity,loginUserId);
		}
		catch(ClientProtocolException cpe){
			cpe.printStackTrace();
		}
		catch(IOException ioe){
			ioe.printStackTrace();
		}
		catch(JSONException jsone){
			jsone.printStackTrace();
		}
		
	}

	public void syncUserFavorite(Context currentActivity,long loginUserId, String userToken,String userName, String salt, long lastSyncDateTime){
		try{
			OfferService.syncFavoritesToBackend(currentActivity, loginUserId);
			StatementsService.syncFavoritesToBackend(currentActivity, loginUserId);
			OfferService.syncFavorites(currentActivity, loginUserId, userToken, userName, salt, lastSyncDateTime);
		}
		catch(ClientProtocolException cpe){
			cpe.printStackTrace();
		}
		catch(IOException ioe){
			ioe.printStackTrace();
		}
		catch(JSONException jsone){
			jsone.printStackTrace();
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}
	
	@Override
	public Object onRetainNonConfigurationInstance() {
		   return this;
		}
	
	 public ControlApplication getApp()
	    {
	        return (ControlApplication )this.getApplication();
	    }

	    @Override
	    public void onUserInteraction()
	    {
	        super.onUserInteraction();
	        
	        getApp().touch();
	        String TAG= "";
	        Log.d(TAG, "User interaction to "+this.toString());
	    }	

}
