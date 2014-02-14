package com.rrdonnelly.up2me;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.rrdonnelly.up2me.R;
import com.rrdonnelly.up2me.dao.StateDAO;
import com.rrdonnelly.up2me.json.DocumentProviders;
import com.rrdonnelly.up2me.json.State;
import com.rrdonnelly.up2me.services.StateService;
import com.rrdonnelly.up2me.util.ValidationsUtil;
import com.rrdonnelly.up2me.valueobjects.UserRegistration;

import android.os.Bundle;
import android.os.StrictMode;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.text.TextUtils;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ToggleButton;

public class UserRegistrationActivity extends Activity {

	UserRegistration userRegistrationData;
	String[] statesStrArray;  
	Activity currentActivity;
	List<State> statesList;
	int selectedStateId;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_user_registration);
		currentActivity = this;
		
		statesList = new StateDAO().getAllStatesByCountry(currentActivity, "");
		try {
			if(statesList != null && statesList.size() == 0 ) {
				StateDAO stateDao = new StateDAO();
				StateService.syncState(currentActivity);
				statesList = stateDao.getAllStatesByCountry(currentActivity, "");
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		
		if(statesList != null && statesList.size() > 0) {
			statesStrArray = new String[statesList.size()];
			for(int i=0; i < statesList.size(); i++) {
				State state = statesList.get(i);
				statesStrArray[i] = state.getStateName();
			}
		}
		
		EditText firstname = (EditText) findViewById(R.id.firstname);
		EditText lastname = (EditText) findViewById(R.id.lastname);
		EditText streetaddress1 = (EditText) findViewById(R.id.streetaddress1);
		EditText streetaddress2 = (EditText) findViewById(R.id.streetaddress2);
		EditText city = (EditText) findViewById(R.id.city);
		AutoCompleteTextView state = (AutoCompleteTextView) findViewById(R.id.state);
		EditText zip = (EditText) findViewById(R.id.zip);
		EditText mobileno1 = (EditText) findViewById(R.id.mobileno1);
		EditText email = (EditText) findViewById(R.id.email);
		EditText pwd = (EditText) findViewById(R.id.pwd);
		EditText confirmpwd = (EditText) findViewById(R.id.confirmpwd);
		
		userRegistrationData = (UserRegistration) getIntent().getSerializableExtra("UserRegistrationData");
		
		if(userRegistrationData != null){
			firstname.setText(userRegistrationData.getFirstname().toString());
			lastname.setText(userRegistrationData.getLastname().toString());
			streetaddress1.setText(userRegistrationData.getStreetaddress1().toString());
			streetaddress2.setText(userRegistrationData.getStreetaddress2().toString());
			city.setText(userRegistrationData.getCity().toString());
			state.setText(userRegistrationData.getState().toString());
			zip.setText(userRegistrationData.getZip().toString());
			mobileno1.setText(userRegistrationData.getMobileno1().toString());
			email.setText(userRegistrationData.getUserName().toString());
			pwd.setText(userRegistrationData.getPassword().toString());
			confirmpwd.setText(userRegistrationData.getConfirmpwd().toString());
		}
		
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

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.user_registration, menu);
		return true;
	}

	public void onClickRegisterBtn(View view) {
		
		EditText firstname = (EditText) findViewById(R.id.firstname);
		EditText lastname = (EditText) findViewById(R.id.lastname);
		EditText streetaddress1 = (EditText) findViewById(R.id.streetaddress1);
		EditText streetaddress2 = (EditText) findViewById(R.id.streetaddress2);
		EditText city = (EditText) findViewById(R.id.city);
		AutoCompleteTextView state = (AutoCompleteTextView) findViewById(R.id.state);
		EditText zip = (EditText) findViewById(R.id.zip);
		EditText mobileno1 = (EditText) findViewById(R.id.mobileno1);
		EditText email = (EditText) findViewById(R.id.email);
		EditText pwd = (EditText) findViewById(R.id.pwd);
		EditText confirmpwd = (EditText) findViewById(R.id.confirmpwd);
		
		/*LinearLayout layout = (LinearLayout) findViewById(R.id.llregisterUserForm);
		HashMap<String, String> al = new HashMap<String, String>();
		for(int i=0; i<layout.getChildCount(); i++) {
			View viewl = layout.getChildAt(i);
			if(viewl instanceof LinearLayout) {
				LinearLayout layoutSub = (LinearLayout) viewl;
				for(int j=0; j < layoutSub.getChildCount(); j++) {
					final View mChild = layoutSub.getChildAt(j);
					if(mChild instanceof EditText) {
						EditText edText = (EditText) mChild;
						al.put(edText.getId()+"", edText.getText().toString());
					}
				}
			}
		}*/
		
		String firstnameVal = firstname.getText().toString();
		String lastnameVal = lastname.getText().toString();
		String streetaddress1Val = streetaddress1.getText().toString();
		String streetaddress2Val = streetaddress2.getText().toString();
		String cityVal = city.getText().toString();
		String stateVal = state.getText().toString();
		String zipVal = zip.getText().toString();
		String mobileno1Val = mobileno1.getText().toString();
		String emailVal = email.getText().toString();
		String pwdVal = pwd.getText().toString();
		String confirmpwdVal = confirmpwd.getText().toString();
		
		View focusView = null;
		boolean cancel = false;
		
		if (TextUtils.isEmpty(firstnameVal)) {
			firstname.setError(getString(R.string.error_field_required));
			focusView = firstname;
			cancel = true;
		}
		
		if (TextUtils.isEmpty(lastnameVal)) {
			lastname.setError(getString(R.string.error_field_required));
			focusView = lastname;
			cancel = true;
		}
		
		if (TextUtils.isEmpty(streetaddress1Val)) {
			streetaddress1.setError(getString(R.string.error_field_required));
			focusView = streetaddress1;
			cancel = true;
		}
		
		if (TextUtils.isEmpty(cityVal)) {
			city.setError(getString(R.string.error_field_required));
			focusView = city;
			cancel = true;
		}
		
		if (TextUtils.isEmpty(stateVal)) {
			state.setError(getString(R.string.error_field_required));
			focusView = state;
			cancel = true;
		}
		
		if (TextUtils.isEmpty(zipVal)) {
			zip.setError(getString(R.string.error_field_required));
			focusView = zip;
			cancel = true;
		}
		
		if (TextUtils.isEmpty(mobileno1Val)) {
			mobileno1.setError(getString(R.string.error_field_required));
			focusView = mobileno1;
			cancel = true;
		} else if(!ValidationsUtil.isValidPhoneNumber(mobileno1Val)) {
			mobileno1.setError(getString(R.string.error_invalid_mobileno));
			focusView = mobileno1;
			cancel = true;
		}
		
		if (TextUtils.isEmpty(emailVal)) {
			email.setError(getString(R.string.error_field_required));
			focusView = email;
			cancel = true;
		} else if (!ValidationsUtil.isValidEmail(emailVal)) {
			email.setError(getString(R.string.error_invalid_email));
			focusView = email;
			cancel = true;
		}
		
		if (TextUtils.isEmpty(pwdVal)) {
			pwd.setError(getString(R.string.error_field_required));
			focusView = pwd;
			cancel = true;
		}
		
		if (TextUtils.isEmpty(confirmpwdVal)) {
			confirmpwd.setError(getString(R.string.error_field_required));
			focusView = confirmpwd;
			cancel = true;
		}
		
		if(pwdVal != null && confirmpwdVal != null && pwdVal.length() > 0 && confirmpwdVal.length() > 0){
			if(!pwdVal.equals(confirmpwdVal)){
				pwd.setError(getString(R.string.passwords_do_not_match));
				confirmpwd.setError(getString(R.string.passwords_do_not_match));
				focusView = confirmpwd;
				cancel = true;
			}
		}
		
		if (cancel) {
			focusView.requestFocus();
		} else {
			
			UserRegistration userRegistrationProcess = new UserRegistration();
			userRegistrationProcess.setFirstname(firstnameVal);
			userRegistrationProcess.setLastname(lastnameVal);
			userRegistrationProcess.setStreetaddress1(streetaddress1Val);
			userRegistrationProcess.setStreetaddress2(streetaddress2Val);
			userRegistrationProcess.setCity(cityVal);
			userRegistrationProcess.setState(stateVal);
			userRegistrationProcess.setZip(zipVal);
			userRegistrationProcess.setMobileno1(mobileno1Val);
			userRegistrationProcess.setUserName(emailVal);
			userRegistrationProcess.setPassword(pwdVal);
			userRegistrationProcess.setConfirmpwd(confirmpwdVal);
			userRegistrationProcess.setStateId(selectedStateId);
			
			boolean isUserExisted = false;
			try {
				
				isUserExisted = checkUserExisted(emailVal);
				
				if(!isUserExisted) {
					Intent userTermsandCondtionsPage = new Intent(view.getContext(), UserTermsConditionsActivity.class);
					userTermsandCondtionsPage.putExtra("pageflow", "newuser");
					userTermsandCondtionsPage.putExtra("UserRegistrationData", userRegistrationProcess);
					startActivityForResult(userTermsandCondtionsPage, 0);
				} else {
					/*email.setError(getString(R.string.userexisted));
					focusView = email;
					focusView.requestFocus();*/
					AlertDialog.Builder builder = new AlertDialog.Builder(this);
					builder.setMessage(R.string.userexisted)
					       .setCancelable(false)
					       .setPositiveButton("OK", new DialogInterface.OnClickListener() {
					           public void onClick(DialogInterface dialog, int id) {
					               dialog.cancel();
					           }
					       });
					AlertDialog alert = builder.create();
					alert.show();
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	}
	
	public void onClickCancelBtn(View view) {
		Intent loginPage = new Intent(view.getContext(), LoginActivity.class);
		startActivity(loginPage);
		finish();
	}
	
	public boolean checkUserExisted(String userName) throws Exception {
		boolean isExistedUser = false;
		
		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
		StrictMode.setThreadPolicy(policy);
		
		HttpClient client = new DefaultHttpClient();
		String webServiceUrl = getResources().getString(R.string.webservice_url);
        HttpPost post = new HttpPost( webServiceUrl + "/CheckUser");
        post.setEntity(new StringEntity(userName));
        
        HttpResponse response = client.execute(post);
        BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
        StringBuilder sb = new StringBuilder();
        
        String line = "";
        while ((line = rd.readLine()) != null) {
        	sb.append(line);
        }
		
        isExistedUser = Boolean.parseBoolean(sb.toString());
                
		return isExistedUser;
	}
}
