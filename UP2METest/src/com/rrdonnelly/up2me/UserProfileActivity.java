package com.rrdonnelly.up2me;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import com.rrdonnelly.up2me.dao.StateDAO;
import com.rrdonnelly.up2me.dao.UserDAO;
import com.rrdonnelly.up2me.dao.UserSettingsDAO;
import com.rrdonnelly.up2me.json.State;
import com.rrdonnelly.up2me.services.UserSettingsService;
import com.rrdonnelly.up2me.util.ControlApplication;
import com.rrdonnelly.up2me.util.ValidationsUtil;
import com.rrdonnelly.up2me.valueobjects.User;
import com.rrdonnelly.up2me.valueobjects.UserRegistration;

import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.AdapterView.OnItemClickListener;

public class UserProfileActivity extends Activity {
	
	public String userName=null;
    public String userToken=null;
    public String salt=null;
    public long loginUserId = 0l;
    Activity currentActivity;

    String[] statesStrArray;  
	List<State> statesList;
	int selectedStateId;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_user_profile);
		
		currentActivity = this;
		userToken=getIntent().getStringExtra("usertoken");
		userName=getIntent().getStringExtra("userName");
        salt=getIntent().getStringExtra("salt");
        loginUserId=getIntent().getLongExtra("loginUserId", 0l);
        
        statesList = new StateDAO().getAllStatesByCountry(currentActivity, "");
        if(statesList != null && statesList.size() > 0) {
			statesStrArray = new String[statesList.size()];
			for(int i=0; i < statesList.size(); i++) {
				State state = statesList.get(i);
				statesStrArray[i] = state.getStateName();
			}
		}
        
		setTypeFace();
		
	}
	
	public void setTypeFace(){
		User user=getProfile();
		Typeface robotoBlack=Typeface.createFromAsset(getAssets(), getResources().getString(R.string.roboto_black));
		Typeface robotolight=Typeface.createFromAsset(getAssets(), getResources().getString(R.string.Roboto_Light));
		Button back=(Button)findViewById(R.id.back);
		back.setTypeface(robotolight);
		EditText firstName=(EditText)findViewById(R.id.firstName);
		firstName.setText(user.getFirsName());
		firstName.setTypeface(robotolight);
		EditText lastName=(EditText)findViewById(R.id.lastName);
		lastName.setTypeface(robotolight);
		lastName.setText(user.getLastName());
		EditText streeAddr1=(EditText)findViewById(R.id.streetAddress1);
		streeAddr1.setTypeface(robotolight);
		streeAddr1.setText(user.getStreeAddr1());
		EditText streetAddr2=(EditText)findViewById(R.id.streetAddress2);
		streetAddr2.setText(user.getStreeAddr2());
		streetAddr2.setTypeface(robotolight);
		EditText city=(EditText)findViewById(R.id.city);
		city.setTypeface(robotolight);
		city.setText(user.getCity());
		AutoCompleteTextView state=(AutoCompleteTextView)findViewById(R.id.state);
		state.setTypeface(robotolight);
		state.setText(user.getState());
		EditText zip=(EditText)findViewById(R.id.zip);
		zip.setTypeface(robotolight);
		zip.setText(user.getZipCode());
		zip.setText(user.getZipCode());
		EditText mobile=(EditText)findViewById(R.id.mobileNumber);
		mobile.setTypeface(robotolight);
	    mobile.setText(user.getMobileNo());
		Button save=(Button)findViewById(R.id.saveButtonId);
		save.setTypeface(robotoBlack);
		
		//Creating the instance of ArrayAdapter containing list of language names  
		if(statesStrArray != null) {
			ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.select_dialog_item, statesStrArray);  
		    //Getting the instance of AutoCompleteTextView  
		    state.setThreshold(1);//will start working from first character  
		    state.setAdapter(adapter);//setting the adapter data into the AutoCompleteTextView  
		    state.setOnItemClickListener(new OnItemClickListener(){
		    	@Override
		        public void onItemClick(AdapterView<?> parent, View view, int position, long rowId) {
		    		String selection = (String) parent.getItemAtPosition(position);
		            for (int i = 0; i <= statesList.size(); i++) {
		                State state = statesList.get(i);
		                if (state.getStateName().equalsIgnoreCase(selection)) {
		                	selectedStateId = i+1;
		                    break;
		                }
		            }
		    	}
		    });
		}
		
	}
	
	public void goBack(View view){
//        Intent settingsActivity = new Intent(view.getContext(), SettingsActivity.class);
//        settingsActivity.putExtra("usertoken", userToken);
//        settingsActivity.putExtra("userName", userName);
//        settingsActivity.putExtra("salt", salt);
//        settingsActivity.putExtra("loginUserId", loginUserId);
//        startActivityForResult(settingsActivity, 0);
		finish();
    }
	
	public User getProfile(){
		User user=null;
		UserDAO userDAO= new UserDAO();
		user = userDAO.getUserByUserName(userName, this);
		return user;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.user_profile, menu);
		return true;
	}
	
	public void saveSettings(View v){
		EditText firstName=(EditText)findViewById(R.id.firstName);
		EditText lastName=(EditText)findViewById(R.id.lastName);
		EditText streetAddr1=(EditText)findViewById(R.id.streetAddress1);
		EditText streetAddr2=(EditText)findViewById(R.id.streetAddress2);
		EditText city=(EditText)findViewById(R.id.city);
		EditText state=(EditText)findViewById(R.id.state);
		EditText zip=(EditText)findViewById(R.id.zip);
		EditText mobile=(EditText)findViewById(R.id.mobileNumber);
		UserRegistration user= new UserRegistration();
		user.setFirstname(firstName.getText().toString());
		user.setUserName(userName);
		user.setLastname(lastName.getText().toString());
		user.setStreetaddress1(streetAddr1.getText().toString());
		user.setStreetaddress2(streetAddr2.getText().toString());
		user.setCity(city.getText().toString());
		user.setState(state.getText().toString());
		user.setStateId(selectedStateId);
		user.setZip(zip.getText().toString());
		
		String mobileNumber = mobile.getText().toString();
		
		if(mobileNumber != null && mobileNumber.length() > 0){
			if(!ValidationsUtil.isValidPhoneNumber(mobileNumber)){
				mobile.setError(getString(R.string.error_invalid_mobileno));
				return;
			}
		}
		
		user.setMobileno1(mobileNumber);
		user.setUserId(loginUserId);
		UserDAO userDAO = new UserDAO();
		User userData = userDAO.getUserByUserName(userName, currentActivity);
		if(userData != null){
			user.setPassword(userData.getPassword());
		}
		
		String returnMsg = UserSettingsService.updateUserProfileDetails(currentActivity, user);
		userDAO.updateUserDetails(user, this);
		
		if(returnMsg != null && returnMsg.length() > 0){
			returnMsg = returnMsg;
		}else{
			returnMsg = "Profile not saved.";
		}
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage(returnMsg)
		       .setCancelable(false)
		       .setPositiveButton("OK", new DialogInterface.OnClickListener() {
		           public void onClick(DialogInterface dialog, int id) {
		               dialog.cancel();
		           }
		       });
		AlertDialog alert = builder.create();
		alert.show();
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
	    
	    @Override
	    protected void onDestroy() {
	    	// TODO Auto-generated method stub
	    	super.onDestroy();
	    	System.gc();
	    }


}
