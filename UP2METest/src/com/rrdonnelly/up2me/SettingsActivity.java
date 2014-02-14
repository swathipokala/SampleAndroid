package com.rrdonnelly.up2me;

import com.rrdonnelly.up2me.dao.DatabaseHandler;
import com.rrdonnelly.up2me.dao.UserDAO;
import com.rrdonnelly.up2me.dao.UserSettingsDAO;
import com.rrdonnelly.up2me.services.UserSettingsService;
import com.rrdonnelly.up2me.util.ControlApplication;
import com.rrdonnelly.up2me.valueobjects.User;
import com.rrdonnelly.up2me.valueobjects.UserSettings;
import com.testflightapp.lib.TestFlight;

import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.Switch;
import android.widget.TextView;

public class SettingsActivity extends Activity {
	
	public String userID = null;
    public String userName=null;
    public String userToken=null;
    public String salt=null;
    public long loginUserId = 0l;
    
	private TextView mLoginErrorMsg;
	private View mSettingsFormView;
	private EditText mEmailView;
	private EditText mPasswordView;
	private EditText mConfirmPasswordView;
	private String mEmail;
	private String mPassword;
	private String mConfirmPassword;
	
	private Switch showBadgesStatementsView;
	private Switch showBadgesBillsView;	
	private Switch showBadgesOffersView;
	private Switch showBadgesLibraryView;
	private Switch calendarAlertsBillsView;
	private Switch calendarAlertsOffersView;
	private Switch cloudSyncStatementsView;
	private Switch cloudSyncBillsView;
	private Switch cloudSyncAllView;
	private Switch cloudSyncAppDataView;
    private Activity currentActivity;
	private boolean showBadgesStatements;
	private boolean showBadgesBills;	
	private boolean showBadgesOffers;
	private boolean showBadgesLibrary;
	private boolean calendarAlertsBills;
	private boolean calendarAlertsOffers;
	private boolean cloudSyncStatements;
	private boolean cloudSyncBills;
	private boolean cloudSyncAll;
	private boolean cloudSyncAppData;
	
	private CheckBox checkPasswordRequired;
	private boolean isPasswordChecked;
	private RadioButton doNotTimeOut;
	private RadioButton setTimeOutTo;
	private RadioGroup radioSetTimeOutTo;
	private int setMinutes = R.id.doNotTimeOut;
    private EditText setTimeOutToValue;
    private int setTimeOut = 0;
    private TextView setTimeOutError;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		currentActivity = this;
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_settings);
		 userToken=getIntent().getStringExtra("usertoken");
	     userName=getIntent().getStringExtra("userName");
	     salt=getIntent().getStringExtra("salt");
	     loginUserId=getIntent().getLongExtra("loginUserId", -1);
	     
	     boolean defaultValue = true;
	     
	     mSettingsFormView = findViewById(R.id.settings_form);
		 mLoginErrorMsg = (TextView) findViewById(R.id.loginErroMsg);
		 setTimeOutError = (TextView) findViewById(R.id.setTimeOutError);
		 
		showBadgesStatementsView = (Switch) findViewById(R.id.showBadgesStatementsValue);
 		showBadgesBillsView = (Switch) findViewById(R.id.showBadgesBillsValue);
 		showBadgesOffersView = (Switch) findViewById(R.id.showBadgesOffersValue);
 		showBadgesLibraryView = (Switch) findViewById(R.id.showBadgesLibraryValue);
 		calendarAlertsBillsView = (Switch) findViewById(R.id.calendarAlertsBillsValue);
 		calendarAlertsOffersView = (Switch) findViewById(R.id.calendarAlertsOffersValue);
 		cloudSyncStatementsView = (Switch) findViewById(R.id.CloudSyncStatementsValue);
 		cloudSyncBillsView = (Switch) findViewById(R.id.CloudSyncBillsValue);
 		cloudSyncAllView = (Switch) findViewById(R.id.CloudSyncAllValue);
 		cloudSyncAppDataView = (Switch) findViewById(R.id.CloudSyncAppDataValue);	
 		
		mEmailView = (EditText) findViewById(R.id.loginEmail);
		mPasswordView = (EditText) findViewById(R.id.passwordValue);
		mConfirmPasswordView = (EditText) findViewById(R.id.confirmPasswordValue);
		
		checkPasswordRequired = (CheckBox) findViewById(R.id.chekRequirePassword);
		doNotTimeOut = (RadioButton) findViewById(R.id.doNotTimeOut);
		setTimeOutTo = (RadioButton) findViewById(R.id.setTimeOutTo);
		radioSetTimeOutTo = (RadioGroup) findViewById(R.id.radioSetTimeOutTo);
		setTimeOutToValue = (EditText) findViewById(R.id.setTimeOutToValue);
 		
		UserDAO userDAO = new UserDAO();
		User user = userDAO.getUserByUserName(userName, currentActivity);
		
		if(user != null){
			mEmailView.setText(userName);
			mEmailView.setEnabled(false);
			mPasswordView.setText(user.getPassword());
			mConfirmPasswordView.setText(user.getPassword());
			checkPasswordRequired.setClickable(defaultValue);
			doNotTimeOut.setChecked(defaultValue);
		}
		
		radioSetTimeOutTo.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				// TODO Auto-generated method stub
					setMinutes = checkedId;
					if(checkedId == R.id.doNotTimeOut){
						setTimeOutToValue.setText("");
						setTimeOutToValue.setEnabled(false);
					}else{
						setTimeOutToValue.setEnabled(true);
					}
			}
		});	 
		
		//By default create a record if the userId does not exists in UserSettings table and set all toggle values to true.
		UserSettingsDAO userSettingsDao = new UserSettingsDAO();
        boolean userExits = userSettingsDao.isUserExists(String.valueOf(loginUserId), currentActivity);
        
        if (!userExits){
        	UserSettings userSettings = new UserSettings();
        	userSettings.setUserId(loginUserId);
        	userSettings.setShowBadgesStatements(defaultValue);
        	userSettings.setShowBadgesBills(defaultValue);
        	userSettings.setShowBadgesOffers(defaultValue);
        	userSettings.setShowBadgesLibrary(defaultValue);
        	userSettings.setCalendarAlertsBills(defaultValue);
        	userSettings.setCalendarAlertsOffers(defaultValue);
        	userSettings.setCloudSyncStatements(defaultValue);
        	userSettings.setCloudSyncBills(defaultValue);
        	userSettings.setCloudSyncAll(defaultValue);
        	userSettings.setCloudSyncAppData(defaultValue);
        	userSettings.setSetTimeOut(0);
        	userSettingsDao.saveUserToggles(userSettings, this);
        }
        
        UserSettings userSetting = userSettingsDao.getUserSettingsByUserId(loginUserId, currentActivity);
        if(userSetting != null){
    		
    		
    		showBadgesStatementsView.setChecked(userSetting.isShowBadgesStatements());
    		showBadgesBillsView.setChecked(userSetting.isShowBadgesBills());
    		showBadgesOffersView.setChecked(userSetting.isShowBadgesOffers());
    		showBadgesLibraryView.setChecked(userSetting.isShowBadgesLibrary());
    		calendarAlertsBillsView.setChecked(userSetting.isCalendarAlertsBills());
    		calendarAlertsOffersView.setChecked(userSetting.isCalendarAlertsOffers());
    		cloudSyncStatementsView.setChecked(userSetting.isCloudSyncStatements());
    		cloudSyncBillsView.setChecked(userSetting.isCloudSyncBills());
    		cloudSyncAllView.setChecked(userSetting.isCloudSyncAll());
    		cloudSyncAppDataView.setChecked(userSetting.isCloudSyncAppData());
    		checkPasswordRequired.setChecked(userSetting.isPasswordChecked());
    		
    		if(userSetting.getSetTimeOut() > 0){
    			setTimeOutToValue.setText(String.valueOf(userSetting.getSetTimeOut()));
    			setTimeOutTo.setChecked(defaultValue);
    		}else if(userSetting.getSetTimeOut() == 0){
    			setTimeOutToValue.setText("");
    			doNotTimeOut.setChecked(defaultValue);
    		}
        }
		

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.settings, menu);
		return true;
	}
	
    public void goBack(View view){
    	finish();
//        Intent dashBoardIntent = new Intent(view.getContext(), DashBoardActivity.class);
//        dashBoardIntent.putExtra("usertoken", userToken);
//        dashBoardIntent.putExtra("userName", userName);
//        dashBoardIntent.putExtra("salt", salt);
//        dashBoardIntent.putExtra("loginUserId", loginUserId);
//        startActivityForResult(dashBoardIntent, 0);
    }
    
    public void saveSettings(View view){
    	
			boolean cancel = checkValidData(view);
			if(cancel){
				return;
			}else{
				mLoginErrorMsg.setText("");
				setTimeOutError.setText("");
			}
			
			saveSettingsData(view);
    }	
    
    public void revertSettings(View view){
    	finish();
//        Intent dashBoardIntent = new Intent(view.getContext(), DashBoardActivity.class);
//        dashBoardIntent.putExtra("usertoken", userToken);
//        dashBoardIntent.putExtra("userName", userName);
//        dashBoardIntent.putExtra("salt", salt);
//        dashBoardIntent.putExtra("loginUserId", loginUserId);
//        startActivityForResult(dashBoardIntent, 0);
    }
    
    public void showUserProfile(View view){
        Intent userProfileIntent = new Intent(view.getContext(), UserProfileActivity.class);
        userProfileIntent.putExtra("usertoken", userToken);
        userProfileIntent.putExtra("userName", userName);
        userProfileIntent.putExtra("salt", salt);
        userProfileIntent.putExtra("loginUserId", loginUserId);
        startActivityForResult(userProfileIntent, 0);
    }
    
    
    public void showManageTags(View view){
        Intent manageTagsActivityIntent = new Intent(view.getContext(), ManageTagsActivity.class);
        manageTagsActivityIntent.putExtra("usertoken", userToken);
        manageTagsActivityIntent.putExtra("userName", userName);
        manageTagsActivityIntent.putExtra("salt", salt);
        manageTagsActivityIntent.putExtra("loginUserId", loginUserId);
        startActivityForResult(manageTagsActivityIntent, 0);
    }
    
	public boolean checkValidData(View view) {

		mEmail = mEmailView.getText().toString();
		mPassword = mPasswordView.getText().toString();
		mConfirmPassword = mConfirmPasswordView.getText().toString();
		
		boolean cancel = false;
		View focusView = null;
		
		// Check for a valid password.
		if (TextUtils.isEmpty(mPassword)) {
			mPasswordView.setError(getString(R.string.error_field_required));
			TestFlight.log("Password is required" + getString(R.string.error_field_required));
			focusView = mPasswordView;
			cancel = true;
		} 
		
		// Check for a valid confirm password.
		if (TextUtils.isEmpty(mConfirmPassword)) {
			mConfirmPasswordView.setError(getString(R.string.error_field_required));
			TestFlight.log("Confirm Password is required" + getString(R.string.error_field_required));
			focusView = mConfirmPasswordView;
			cancel = true;
		} 

		// Check for a valid email address.
		if (TextUtils.isEmpty(mEmail)) {
			mEmailView.setError(getString(R.string.error_field_required));
			focusView = mEmailView;
			cancel = true;
		} else if (!mEmail.contains("@")) {
			mEmailView.setError(getString(R.string.error_invalid_email));
			focusView = mEmailView;
			cancel = true;
		}
		
		if(mPassword != null && mConfirmPassword != null && mPassword.length() > 0 && mConfirmPassword.length() > 0){
			if(!mPassword.equals(mConfirmPassword)){
				mLoginErrorMsg.setText("Passwords Do Not Match");
				cancel = true;
			}
		}
		
    	if(setMinutes == R.id.setTimeOutTo){
    		if(setTimeOutToValue.getText().toString() != null && setTimeOutToValue.getText().toString().length() > 0){
    			setTimeOut = Integer.parseInt(setTimeOutToValue.getText().toString());
    			if(setTimeOut > 10080 || setTimeOut <= 0){
    				setTimeOutError.setText("Time period should be between 1 and 10,080 minutes.");
    				cancel = true;
    				focusView = setTimeOutToValue;
    			}    			
    		} else{
    			setTimeOutError.setText("Time period should not be empty.");
    			cancel = true;
    			focusView = setTimeOutToValue;
    		}
    	} else if (setMinutes == R.id.doNotTimeOut){
    		setTimeOutToValue.setText("");
    		setTimeOut = 0;
    	}
		
		return cancel;
	}
	
	public void saveSettingsData(View view) {
		
		showBadgesStatements = showBadgesStatementsView.isChecked();
		showBadgesBills = showBadgesBillsView.isChecked();
		showBadgesOffers = showBadgesOffersView.isChecked();
		showBadgesLibrary = showBadgesLibraryView.isChecked();
		calendarAlertsBills = calendarAlertsBillsView.isChecked();
		calendarAlertsOffers = calendarAlertsOffersView.isChecked();
		cloudSyncStatements = cloudSyncStatementsView.isChecked();
		cloudSyncBills = cloudSyncBillsView.isChecked();
		cloudSyncAll = cloudSyncAllView.isChecked();
		cloudSyncAppData = cloudSyncAppDataView.isChecked();
		
		isPasswordChecked = checkPasswordRequired.isChecked();
		
		mEmail = mEmailView.getText().toString();
		mPassword = mPasswordView.getText().toString();
		
		UserSettingsDAO userSettingsDao = new UserSettingsDAO();
        boolean userExits = userSettingsDao.isUserExists(String.valueOf(loginUserId), currentActivity);
        
    	UserSettings userSettings = new UserSettings();
    	userSettings.setUserName(mEmail);
    	userSettings.setPrimaryEmail(mEmail);
    	userSettings.setPassword(mPassword);
    	userSettings.setUserId(loginUserId);
    	userSettings.setShowBadgesStatements(showBadgesStatements);
    	userSettings.setShowBadgesBills(showBadgesBills);
    	userSettings.setShowBadgesOffers(showBadgesOffers);
    	userSettings.setShowBadgesLibrary(showBadgesLibrary);
    	userSettings.setCalendarAlertsBills(calendarAlertsBills);
    	userSettings.setCalendarAlertsOffers(calendarAlertsOffers);
    	userSettings.setCloudSyncStatements(cloudSyncStatements);
    	userSettings.setCloudSyncBills(cloudSyncBills);
    	userSettings.setCloudSyncAll(cloudSyncAll);
    	userSettings.setCloudSyncAppData(cloudSyncAppData);
    	userSettings.setPasswordChecked(isPasswordChecked);
    	
    	userSettings.setSetTimeOut(setTimeOut);
        if (userExits){
        	String resultMsg = UserSettingsService.updateUserSettings(currentActivity, userSettings);
        	userSettingsDao.updateUserToggles(userSettings, this);
        	if(resultMsg != null && resultMsg.length() > 0){
        		resultMsg = resultMsg;
        	}else{
        		resultMsg = "User settings not saved.";
        	}
        	AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setMessage(resultMsg)
			       .setCancelable(false)
			       .setPositiveButton("OK", new DialogInterface.OnClickListener() {
			           public void onClick(DialogInterface dialog, int id) {
			               dialog.cancel();
			           }
			       });
			AlertDialog alert = builder.create();
			alert.show();
			
			getApp().setPeriod(setTimeOut);
			
        } else {
        	userSettingsDao.saveUserToggles(userSettings, this);
        }
	}
	
	public void showOfferProviders(View view) {
    	Intent manageOfferProivders = new Intent(view.getContext(), ManageOfferProvidersActivity.class);
    	manageOfferProivders.putExtra("usertoken", userToken);
    	manageOfferProivders.putExtra("userName", userName);
    	manageOfferProivders.putExtra("salt", salt);
    	manageOfferProivders.putExtra("loginUserId", loginUserId);
    	manageOfferProivders.putExtra("pageflow", "edituser");
        startActivityForResult(manageOfferProivders, 0);
    }
	
	
    public void showSelectCloudProvider(View view){
        Intent selectCloudProviderIntent = new Intent(view.getContext(), SelectCloudProviderActivity.class);
        selectCloudProviderIntent.putExtra("usertoken", userToken);
        selectCloudProviderIntent.putExtra("userName", userName);
        selectCloudProviderIntent.putExtra("salt", salt);
        selectCloudProviderIntent.putExtra("loginUserId", loginUserId);
        selectCloudProviderIntent.putExtra("pageflow", "edituser");
        startActivityForResult(selectCloudProviderIntent, 0);
    }
    
    public void showDocumentProviders(View view){
    	Intent manageDocumentProivders = new Intent(view.getContext(), ManageDocumentProvidersActivity.class);
    	manageDocumentProivders.putExtra("usertoken", userToken);
    	manageDocumentProivders.putExtra("userName", userName);
    	manageDocumentProivders.putExtra("salt", salt);
    	manageDocumentProivders.putExtra("loginUserId", loginUserId);
    	manageDocumentProivders.putExtra("pageflow", "edituser");
        startActivityForResult(manageDocumentProivders, 0);
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
