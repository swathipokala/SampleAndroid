package com.rrdonnelly.up2me;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.google.gson.Gson;
import com.rrdonnelly.up2me.dao.DatabaseHandler;
import com.rrdonnelly.up2me.dao.StateDAO;
import com.rrdonnelly.up2me.dao.UserDAO;
import com.rrdonnelly.up2me.json.State;
import com.rrdonnelly.up2me.security.SaltedMD5;
import com.rrdonnelly.up2me.services.StateService;
import com.rrdonnelly.up2me.services.UserService;
import com.rrdonnelly.up2me.valueobjects.User;
import com.testflightapp.lib.TestFlight;

/**
 * Activity which displays a login screen to the user, offering registration as
 * well.
 */
public class LoginActivity extends Activity {
	/**
	 * A dummy authentication store containing known user names and passwords.
	 * TODO: remove after connecting to a real authentication system.
	 */
	private static final String[] DUMMY_CREDENTIALS = new String[] {
			"foo@example.com:hello", "bar@example.com:world" };

	/**
	 * The default email to populate the email field with.
	 */
	public static final String EXTRA_EMAIL = "";

	/**
	 * Keep track of the login task to ensure we can cancel it if requested.
	 */
	private UserLoginTask mAuthTask = null;

	// Values for email and password at the time of the login attempt.
	private String mEmail;
	private String mPassword;

	// UI references.
	private EditText mEmailView;
	private EditText mPasswordView;
	private View mLoginFormView;
	private View mLoginStatusView;
	private TextView mLoginStatusMessageView;
	private TextView mLoginErrorMsg;
	
	ProgressDialog progressBar;
	boolean isSyncChecked = false;
	private Activity currentActivity = null;
	
	private static boolean wifiConnected = false;
    // Whether there is a mobile connection.
    private static boolean mobileConnected = false;
 

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		checkNetworkConnection();
		//Initialize TestFlight with your app token.
        TestFlight.takeOff(this.getApplication(), "09959785-ec62-4cf2-8363-8d7336a16d16");
		
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_login);
		
		currentActivity = this;
		DatabaseHandler databaseHandler = DatabaseHandler.getDatabaseHandlerInstance(currentActivity);
		SQLiteDatabase db = databaseHandler.getReadableDatabase();
		
		List<State> statesList = new StateDAO().getAllStatesByCountry(currentActivity, "");
		try {
			if(statesList != null && statesList.size() == 0 ) {
				StateService.syncState(currentActivity);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		
		mLoginFormView = findViewById(R.id.login_form);
		mLoginStatusView = findViewById(R.id.login_status);
		mLoginStatusMessageView = (TextView) findViewById(R.id.login_status_message);
		mLoginErrorMsg = (TextView) findViewById(R.id.loginErroMsg);
		
		findViewById(R.id.signIn).setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						try{
							attemptLogin(view);
						}
						catch(Exception e){
							e.printStackTrace();
						}
					}
				});
		
		findViewById(R.id.clear).setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						
						mEmailView = (EditText) findViewById(R.id.email);
						mPasswordView = (EditText) findViewById(R.id.password);
						
						mEmailView.setText("");
						mPasswordView.setText("");
					}
				});
		
		findViewById(R.id.register).setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						
						Intent registerPage = new Intent(view.getContext(), UserRegistrationActivity.class);
						startActivityForResult(registerPage, 0);
					}
				});
		
		
		ToggleButton toggle = (ToggleButton) findViewById(R.id.toggle);
		isSyncChecked = toggle.isChecked();
		toggle.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				isSyncChecked = isChecked;
			}
		});
		
		//currentActivity = this;
	}
	
				
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		getMenuInflater().inflate(R.menu.login, menu);
		return true;
	}
	
    private void checkNetworkConnection() {
        // BEGIN_INCLUDE(connect)
        ConnectivityManager connMgr =
            (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeInfo = connMgr.getActiveNetworkInfo();
        if (activeInfo != null && activeInfo.isConnected()) {
            wifiConnected = activeInfo.getType() == ConnectivityManager.TYPE_WIFI;
            mobileConnected = activeInfo.getType() == ConnectivityManager.TYPE_MOBILE;
            if(wifiConnected) {
                //Log.i(TAG, getString(R.string.wifi_connection));
            } else if (mobileConnected){
                //Log.i(TAG, getString(R.string.mobile_connection));
            }
        } else {
            //Log.i(TAG, getString(R.string.no_wifi_or_mobile));
        }
        // END_INCLUDE(connect)
      }
	
	/**
	 * Attempts to sign in or register the account specified by the login form.
	 * If there are form errors (invalid email, missing fields, etc.), the
	 * errors are presented and no actual login attempt is made.
	 */
	public void attemptLogin(View view) throws Exception {
		if (mAuthTask != null) {
			return;
		}
		
		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
		StrictMode.setThreadPolicy(policy);
		
		mEmailView = (EditText) findViewById(R.id.email);
		mPasswordView = (EditText) findViewById(R.id.password);
		
		mEmail = mEmailView.getText().toString();
		mPassword = mPasswordView.getText().toString();
		
        String salt = SaltedMD5.getSalt();
         
        String userToken = SaltedMD5.getUserToken(mPassword, salt);
        //System.out.println(userToken); //Prints 83ee5baeea20b6c21635e4ea67847f66
		
		// Reset errors.
		mEmailView.setError(null);
		mPasswordView.setError(null);

		// Store values at the time of the login attempt.
		mEmail = mEmailView.getText().toString();
		mPassword = mPasswordView.getText().toString();

		boolean cancel = false;
		View focusView = null;

		// Check for a valid password.
		if (TextUtils.isEmpty(mPassword)) {
			mPasswordView.setError(getString(R.string.error_field_required));
			TestFlight.log("Password is required" + getString(R.string.error_field_required));
			focusView = mPasswordView;
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
		
		if (!wifiConnected && !mobileConnected){
			UserDAO userDAO = new UserDAO();
			User user=userDAO.getUserByUserName(mEmail, this);
			if (mEmail.equals(user.getUserName()) && mPassword.equals(user.getPassword())){
				Intent dashBoardIntent = new Intent(view.getContext(), DashBoardActivity.class);
	            dashBoardIntent.putExtra("usertoken", userToken);
	            dashBoardIntent.putExtra("userName", mEmail);
	            dashBoardIntent.putExtra("salt", salt);
                dashBoardIntent.putExtra("loginUserId", user.getId());
                dashBoardIntent.putExtra("AlarmTask", "true");
	            startActivityForResult(dashBoardIntent, 0);
			} else {
				mLoginErrorMsg.setText(R.string.error_field_username_password);
			}
			
		} else {
  
		if (cancel) {
			// There was an error; don't attempt login and focus the first
			// form field with an error.
			focusView.requestFocus();
		} else {
			//HTTPClient code

			HttpClient client = new DefaultHttpClient();
			String webServiceUrl = getResources().getString(R.string.webservice_url);
	        HttpPost post = new HttpPost(webServiceUrl + "/Login");
	        
	        AuthenticationInfo authInfo = new AuthenticationInfo(userToken, mEmail, salt);
	        Gson gson = new Gson(); 
	        
	        StringEntity input = new StringEntity(gson.toJson(authInfo)); 
	        input.setContentType("application/json");
	        
	        post.setEntity(input);
	        
	        HttpResponse response = client.execute(post);
	        BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
	        StringBuilder sb = new StringBuilder();
	        
	        String line = "";
	        while ((line = rd.readLine()) != null) {
	        	sb.append(line);
	        }
			
	        String jsonText = sb.toString();
	        JSONObject json = new JSONObject(jsonText);
	        String statusCode = json.getJSONObject("Status").getString("StatusCode");
	        String loginUserId = null;
	        
	        if(statusCode.equalsIgnoreCase("200")) {
	        	
	        	 loginUserId = json.getJSONObject("Status").getString("userId");
                
	        	DatabaseHandler db = DatabaseHandler.getDatabaseHandlerInstance(currentActivity);
	        	//db.close();
	        	
	        	TestFlight.passCheckpoint("LOGIN_SUCCESS_CHECKPOINT");
	        	
	        	// Show a progress spinner, and kick off a background task to
                // perform the user login attempt.
                mLoginStatusMessageView.setText(R.string.login_progress_signing_in);
                showProgress(true);
                UserDAO userDao=new UserDAO();
                /*boolean userExits=userDao.isUserExists(loginUserId, this);
                
                if (userExits){
                	User user= new User();
                	user.setId(Long.valueOf(loginUserId));
                	user.setUserName(mEmail);
                	user.setPassword(mPassword);
                	userDao.updateUser(user, this);
                } else {
                	  User user= new User();
                      user.setId(Long.valueOf(loginUserId));
                      user.setUserName(mEmail);
                      user.setPassword(mPassword);
                      userDao.saveUser(user, this);
                }*/
                
                UserService.getUserInformation(currentActivity, Long.valueOf(loginUserId),  mPassword);
                
				if (isSyncChecked) {
					Intent syncProcess = new Intent(view.getContext(),
							SyncProcessActivity.class);

					syncProcess.putExtra("usertoken", authInfo.getUserToken());
					syncProcess.putExtra("userName", authInfo.getUserName());
					syncProcess.putExtra("salt", authInfo.getSalt());
					if(loginUserId!=null && loginUserId.length()>0){
						syncProcess.putExtra("loginUserId", Long.parseLong(loginUserId));
					}
					

					startActivityForResult(syncProcess, 0);
					
				} else {
                	Intent dashBoardIntent = new Intent(view.getContext(), DashBoardActivity.class);
    	            
    	            dashBoardIntent.putExtra("usertoken", authInfo.getUserToken());
    	            dashBoardIntent.putExtra("userName", authInfo.getUserName());
    	            dashBoardIntent.putExtra("salt", authInfo.getSalt());
    	            dashBoardIntent.putExtra("AlarmTask", "true");
    	            if(loginUserId!=null && loginUserId.length()>0){
    	            	dashBoardIntent.putExtra("loginUserId", Long.parseLong(loginUserId));
    	            }
    	            
    	            startActivityForResult(dashBoardIntent, 0);
                }
                
                
	        } else {
	        	mLoginErrorMsg.setText(R.string.error_field_username_password);
	        }
			//End of HTTPClient code
		}
		
	} //end of connection check..
		
	}

	@Override
	public void onBackPressed() {
		this.finish();
	}
	
	/**
	 * Shows the progress UI and hides the login form.
	 */
	@TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
	private void showProgress(final boolean show) {
		// On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
		// for very easy animations. If available, use these APIs to fade-in
		// the progress spinner.
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
			int shortAnimTime = getResources().getInteger(
					android.R.integer.config_shortAnimTime);

			mLoginStatusView.setVisibility(View.VISIBLE);
			mLoginStatusView.animate().setDuration(shortAnimTime)
					.alpha(show ? 1 : 0)
					.setListener(new AnimatorListenerAdapter() {
						@Override
						public void onAnimationEnd(Animator animation) {
							mLoginStatusView.setVisibility(show ? View.VISIBLE
									: View.VISIBLE);
						}
					});

			mLoginFormView.setVisibility(View.VISIBLE);
			mLoginFormView.animate().setDuration(shortAnimTime)
					.alpha(show ? 0 : 1)
					.setListener(new AnimatorListenerAdapter() {
						@Override
						public void onAnimationEnd(Animator animation) {
							mLoginFormView.setVisibility(show ? View.VISIBLE
									: View.VISIBLE);
						}
					});
		} else {
			// The ViewPropertyAnimator APIs are not available, so simply show
			// and hide the relevant UI components.
			mLoginStatusView.setVisibility(show ? View.VISIBLE : View.GONE);
			mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
		}
	}

	/**
	 * Represents an asynchronous login/registration task used to authenticate
	 * the user.
	 */
	public class UserLoginTask extends AsyncTask<Void, Void, Boolean> {
		@Override
		protected Boolean doInBackground(Void... params) {
			// TODO: attempt authentication against a network service.

			try {
				// Simulate network access.
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				return false;
			}

			for (String credential : DUMMY_CREDENTIALS) {
				String[] pieces = credential.split(":");
				if (pieces[0].equals(mEmail)) {
					// Account exists, return true if the password matches.
					return pieces[1].equals(mPassword);
				}
			}

			// TODO: register the new account here.
			return true;
		}

		@Override
		protected void onPostExecute(final Boolean success) {
			mAuthTask = null;
			showProgress(false);

			if (success) {
				finish();
			} else {
				mPasswordView
						.setError(getString(R.string.error_incorrect_password));
				mPasswordView.requestFocus();
			}
		}

		@Override
		protected void onCancelled() {
			mAuthTask = null;
			showProgress(false);
		}
	}
	
}
