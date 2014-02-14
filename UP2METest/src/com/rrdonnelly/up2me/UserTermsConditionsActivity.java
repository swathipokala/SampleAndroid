package com.rrdonnelly.up2me;

import com.rrdonnelly.up2me.valueobjects.UserRegistration;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.view.Window;

public class UserTermsConditionsActivity extends Activity {

	UserRegistration userRegistrationData;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_user_terms_conditions);
		
		userRegistrationData = (UserRegistration) getIntent().getSerializableExtra("UserRegistrationData");
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.user_terms_conditions, menu);
		return true;
	}

	public void onClickDoNotAccept(View view) {
		Intent loginAct = new Intent(view.getContext(), LoginActivity.class);
		startActivity(loginAct);
		finish();
	}
	
	public void onClickAcceptBtn(View view) {
		Intent selectCloudPage = new Intent(view.getContext(), SelectCloudProviderActivity.class);
		selectCloudPage.putExtra("pageflow", "newuser");
		selectCloudPage.putExtra("UserRegistrationData", userRegistrationData);
		startActivityForResult(selectCloudPage, 0);
	}
	
	public void onClickBackBtn(View view) {
		finish();
//		Intent userRegistrationAct = new Intent(view.getContext(), UserRegistrationActivity.class);
//		userRegistrationAct.putExtra("pageflow", "newuser");
//		userRegistrationAct.putExtra("UserRegistrationData", userRegistrationData);
//		startActivityForResult(userRegistrationAct, 0);
	}
	
	public void onClickSaveBtn(View view) {
		
	}
}
