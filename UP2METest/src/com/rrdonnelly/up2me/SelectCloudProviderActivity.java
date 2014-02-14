package com.rrdonnelly.up2me;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import com.rrdonnelly.up2me.dao.CloudProviderDAO;
import com.rrdonnelly.up2me.json.CloudProvider;
import com.rrdonnelly.up2me.services.CloudProviderService;
import com.rrdonnelly.up2me.services.JsonDataCallback;
import com.rrdonnelly.up2me.util.ControlApplication;
import com.rrdonnelly.up2me.valueobjects.SavedState;
import com.rrdonnelly.up2me.valueobjects.UserRegistration;
import com.testflightapp.lib.TestFlight;

public class SelectCloudProviderActivity extends Activity {

	
	public String userID = null;
    public String userName=null;
    public String userToken=null;
    public String salt=null;
    public long loginUserId = 0l;

    public String pageFlow = null;
    
    UserRegistration userRegistrationData;
    CloudAdapter cloudAdapter;
    Context context;
    GridView gridViewCloud;
    ArrayList<CloudData> list;
    List<CloudProvider> cloudProvidersListFromDB = null;
    
	private String mEmail;
	private String mPassword;
	
	List<Integer> selectedCloudItems = new ArrayList<Integer>();
	List<CloudProvider> cloudProviderList = new ArrayList<CloudProvider>();


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_select_cloud_provider);
		context = this;
		
		gridViewCloud = (GridView) findViewById(R.id.gridViewCloud);
		
		pageFlow = getIntent().getStringExtra("pageflow");
		userRegistrationData = (UserRegistration) getIntent().getSerializableExtra("UserRegistrationData");
		
		Button continueButtonId = (Button) findViewById(R.id.saveButtonId);
		
		SavedState data=(SavedState)getLastNonConfigurationInstance();
        if (data != null){
        	selectedCloudItems = data.getSelectedCloudListBuild();
        } 

		
		list = new ArrayList<CloudData>();
		cloudAdapter = new CloudAdapter(context, R.layout.cloud_grid_row, list);
		gridViewCloud.setAdapter(cloudAdapter);
		
		gridViewCloud.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> a, View v, int position, long id) {
				 Object o = gridViewCloud.getItemAtPosition(position);
	             CloudData cloud = (CloudData)o;
	             //Toast.makeText(context, cloud.getStrImageText(), Toast.LENGTH_SHORT).show();
	             System.out.println(cloud.getStrImageText());
//	             userRegistrationData.setSelectedCloudProviders(new ArrayList<Integer>(checkedItems));
	             
	             int i=0;
	             boolean ret = false;
	             if(selectedCloudItems.size() > 0){
		             for (i = 0; i < selectedCloudItems.size(); i++) {
		     			if (selectedCloudItems.get(i) == cloud.getClientId()) {
		     				ret = true;
		     				break;
		     			}
		             }
	             }
	             
	             if(ret) {
	            	 v.setBackgroundResource(R.color.white);
	            	 selectedCloudItems.remove(i);
	             } else {
	            	 v.setBackgroundResource(R.drawable.cloud_selected_background);
	            	 selectedCloudItems.add(Integer.parseInt(String.valueOf(cloud.getClientId())));
	             }
	             
			}
		});
		
		
		if("newuser".equalsIgnoreCase(pageFlow)) {
			continueButtonId.setText(R.string.continuebtn);
				CloudProviderDAO cloudProviderDAO = new CloudProviderDAO();
				cloudProvidersListFromDB = cloudProviderDAO.getAllCloudProviders(this);
				if(cloudProvidersListFromDB.size() > 0){
					cloudProviders();
				}else{
					cloudProviderList();
				}
		} else {
			continueButtonId.setText(R.string.save);
			userName=getIntent().getStringExtra("userName");
			userToken=getIntent().getStringExtra("usertoken");
  	        salt=getIntent().getStringExtra("salt");
		    loginUserId=getIntent().getLongExtra("loginUserId", -1);
		    if (data == null) {
		    	selectedCloudItems = new CloudProviderDAO().getAllUserCloudProviders(this, loginUserId);
		    }
		    cloudProviders();
		}
		
		if(userRegistrationData != null) {
			EditText userName = (EditText) findViewById(R.id.username);
			userName.setText(userRegistrationData.getCloudProviderUserName());
			
			EditText password = (EditText) findViewById(R.id.password);
			password.setText(userRegistrationData.getCloudProviderPassword());
		}
		
		
	}
	

	@Override
	    public Object onRetainNonConfigurationInstance() {
	    	final SavedState data = new SavedState();
	        data.setSelectedCloudListBuild(selectedCloudItems);
	    	return data;
	    }


	private void cloudProviders() {
	    CloudProviderDAO cloudProviderDAO = new CloudProviderDAO();
	    try {
			
	    	//CloudProviderService.syncCloudProviders(SelectCloudProviderActivity.this, userToken, userName, salt, loginUserId);
	    	cloudAdapter.notifyDataSetChanged();
			cloudProvidersListFromDB = cloudProviderDAO.getAllCloudProviders(this);
			if(cloudProvidersListFromDB != null) {
		        for (int i = 0; i < cloudProvidersListFromDB.size(); i++) {
		        	CloudProvider cloudProvider = cloudProvidersListFromDB.get(i);
		        	list.add(new CloudData(cloudProvider.getClientId(), cloudProvider.getStrImagePath(), cloudProvider.getImageText(), false, selectedCloudItems));
		        	cloudAdapter.notifyDataSetChanged();
		        }
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void cloudProviderList() {
		String webServiceUrl = SelectCloudProviderActivity.this.getResources().getString(R.string.webservice_url);
		JSONObject serverInputs=new JSONObject();
		JsonDataCallback callback = new JsonDataCallback(SelectCloudProviderActivity.this, serverInputs) {
			@Override
			public void receiveData(Object object) {
					String strObject = (String) object;
					try {
						getCloudProviderData(strObject);	
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			};
		callback.execute(webServiceUrl + "/Providers/Cloud", null, null);
	}

	protected void getCloudProviderData(String strObject) throws JSONException {
		JSONArray cloudProviderArray = new JSONArray(strObject);
		
		for(int i=0; i<cloudProviderArray.length(); i++){
        	JSONObject cloudProviderJSON = cloudProviderArray.getJSONObject(i);
        	
        	if(cloudProviderJSON!=null){
        		
        		long l = cloudProviderJSON.getLong("clientId");
        		String strImageText = cloudProviderJSON.getString("providerName");
	        	if(cloudProviderJSON.getJSONObject("displayImagePath")!=null){
	        		String strImagePath = cloudProviderJSON.getJSONObject("displayImagePath").getString("path");
	        		
	        		list.add(new CloudData(l, strImagePath, strImageText, false,selectedCloudItems));
	        	}
        	}
        	cloudAdapter.notifyDataSetChanged();
        	
        	CloudProviderDAO.deleteCloudProviders(context);

    		cloudProviderList = CloudProviderService.createCloudProviderList(strObject.toString(), context);
    		
    		for (CloudProvider cloudProvider : cloudProviderList) {
    			CloudProviderDAO.addCloudProvider(cloudProvider, context);
    		}
		}
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.select_cloud, menu);
		return true;
	}
	
	
	public void showSettingsPage(View view){
//		if("newuser".equalsIgnoreCase(pageFlow)) {
//			Intent termsandConditionsActivityIntent = new Intent(view.getContext(), UserTermsConditionsActivity.class);
//			termsandConditionsActivityIntent.putExtra("pageflow", "newuser");
//			termsandConditionsActivityIntent.putExtra("UserRegistrationData", userRegistrationData);
//			startActivityForResult(termsandConditionsActivityIntent, 0);
//			
//		} else {
//		    Intent settingsActivityIntent = new Intent(view.getContext(), SettingsActivity.class);
//		    
//		    settingsActivityIntent.putExtra("usertoken", userToken);
//	        settingsActivityIntent.putExtra("userName",userName);
//	        settingsActivityIntent.putExtra("salt",salt);
//	        settingsActivityIntent.putExtra("loginUserId",loginUserId);
//	
//		    startActivityForResult(settingsActivityIntent, 0);
//		}
		finish();
	}
	
	public void saveCloudProviders(View view) {
		EditText userName = (EditText) findViewById(R.id.username);
		EditText password = (EditText) findViewById(R.id.password);
		TextView resultMsg = (TextView) findViewById(R.id.resultMsg);
		resultMsg.setVisibility(View.VISIBLE);
		resultMsg.setText("");
		
		mEmail = userName.getText().toString();
		mPassword = password.getText().toString();
		
		boolean cancel = false;
		View focusView = null;

		// Check for a valid password.
		if (TextUtils.isEmpty(mPassword)) {
			password.setError(getString(R.string.error_field_required));
			TestFlight.log("Password is required" + getString(R.string.error_field_required));
			focusView = password;
			cancel = true;
		} 

		// Check for a valid email address.
		if (TextUtils.isEmpty(mEmail)) {
			userName.setError(getString(R.string.error_field_required));
			focusView = userName;
			cancel = true;
		} else if (!mEmail.contains("@")) {
			userName.setError(getString(R.string.error_invalid_email));
			focusView = userName;
			cancel = true;
		}
		
		if (cancel) {
			// There was an error; don't attempt login and focus the first
			// form field with an error.
			focusView.requestFocus();
		} else {
			if(userRegistrationData != null) {
				userRegistrationData.setCloudProviderUserName(userName.getText().toString());
				userRegistrationData.setCloudProviderPassword(password.getText().toString());
			}
			
			if("newuser".equalsIgnoreCase(pageFlow)){
				
				if(selectedCloudItems.size() > 0){
					userRegistrationData.setSelectedCloudProviders(new ArrayList<Integer>(selectedCloudItems));
					
					Intent manageDocProviders = new Intent(view.getContext(), ManageDocumentProvidersActivity.class);
					manageDocProviders.putExtra("pageflow", "newuser");
					manageDocProviders.putExtra("UserRegistrationData", userRegistrationData);
					startActivityForResult(manageDocProviders, 0);
				}else{
					Toast.makeText(context, "Please select atleast once cloud provider", Toast.LENGTH_LONG).show();
				}
			}else{
				if(selectedCloudItems.size() > 0){
					String resultMsgText = new CloudProviderDAO().InsertUserCloudProviders(this, selectedCloudItems, loginUserId);
					resultMsg.setText(resultMsgText);
				}else{
					Toast.makeText(context, "Please select atleast once cloud provider", Toast.LENGTH_LONG).show();
				}
			}
		}
	}
	
	 public ControlApplication getApp()
	    {
	        return (ControlApplication )this.getApplication();
	    }

	    @Override
	    public void onUserInteraction()
	    {
	        super.onUserInteraction();
	        if(!("newuser".equalsIgnoreCase(pageFlow))){
	        getApp().touch();
	        String TAG= "";
	        Log.d(TAG, "User interaction to "+this.toString());
	        }
	    }	
	    
	    @Override
	    protected void onDestroy() {
	    	// TODO Auto-generated method stub
	    	super.onDestroy();
	    	System.gc();
	    }

	
}
