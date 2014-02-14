package com.rrdonnelly.up2me;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.rrdonnelly.up2me.dao.CloudProviderDAO;
import com.rrdonnelly.up2me.dao.DocumentProvidersDAO;
import com.rrdonnelly.up2me.dao.OfferProvidersDAO;
import com.rrdonnelly.up2me.dao.TagDAO;
import com.rrdonnelly.up2me.dao.UserDAO;
import com.rrdonnelly.up2me.dao.UserSettingsDAO;
import com.rrdonnelly.up2me.json.OfferProviders;
import com.rrdonnelly.up2me.services.OfferProvidersService;
import com.rrdonnelly.up2me.services.TagService;
import com.rrdonnelly.up2me.util.ControlApplication;
import com.rrdonnelly.up2me.util.DownloadImages;
import com.rrdonnelly.up2me.util.tags.AsyncTagSyncToBackend;
import com.rrdonnelly.up2me.valueobjects.SavedState;
import com.rrdonnelly.up2me.valueobjects.UserRegistration;

public class ManageOfferProvidersActivity extends Activity {

	Activity currentActivity;
	public String userName=null;
	public String userToken=null;
	public String salt=null;
	public long loginUserId = 0l;
	
	public String pageFlow = null;
	List<Integer> userOfferProvidersListFromDB = new ArrayList<Integer>();
	List<OfferProviders> offerProvidersListFromDB = null;
	
	UserRegistration userRegistrationData;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_manage_offer_providers);
		
		currentActivity = this;
		pageFlow = getIntent().getStringExtra("pageflow");
		userRegistrationData = (UserRegistration) getIntent().getSerializableExtra("UserRegistrationData");
		
		Button btnsaveButtonId = (Button) findViewById(R.id.saveButtonId);
		//offerProvidersListFromDB = new OfferProvidersDAO().getAllOfferProviders(currentActivity);
		
		SavedState data=(SavedState)getLastNonConfigurationInstance();
        if (data != null){
        	userOfferProvidersListFromDB = data.getSelectedOfferProviderList();
        	
        } 
		
		if("newuser".equalsIgnoreCase(pageFlow)) {
			try {
			btnsaveButtonId.setText(R.string.finish);
			if(userRegistrationData != null && userRegistrationData.getSelectedOfferProviders() != null && userRegistrationData.getSelectedOfferProviders().size() > 0) {
				userOfferProvidersListFromDB = new ArrayList<Integer>(userRegistrationData.getSelectedOfferProviders());
			}
			
			StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
			StrictMode.setThreadPolicy(policy);

			offerProvidersListFromDB = OfferProvidersService.getOfferProviders(currentActivity);
				
			}catch (Exception ex) {
				ex.printStackTrace();
				if(offerProvidersListFromDB != null) {
					offerProvidersListFromDB = null;
				}
			}
			
		} else {
			btnsaveButtonId.setText(R.string.save);
			
			userToken=getIntent().getStringExtra("usertoken");
	        userName=getIntent().getStringExtra("userName");
	        salt=getIntent().getStringExtra("salt");
	        loginUserId=getIntent().getLongExtra("loginUserId", -1);
	        offerProvidersListFromDB = new OfferProvidersDAO().getAllOfferProviders(currentActivity);
	        if(data == null){
	        	userOfferProvidersListFromDB = new OfferProvidersDAO().getAllUserOfferProviders(currentActivity, loginUserId);
	        }
		}
        
        LinearLayout layout = (LinearLayout) findViewById(R.id.rlofferProvidersList);
        final LayoutInflater  inflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        RelativeLayout llChild;
        ToggleButton onOffImage;
        ImageView imgOfferProvider;
        TextView txtOfferProviderName;
        
        if(offerProvidersListFromDB != null) {
        for (int i = 0; i < offerProvidersListFromDB.size(); i++) {
        	OfferProviders offerProvider = offerProvidersListFromDB.get(i);
        	
        	llChild = (RelativeLayout) inflater.inflate(R.layout.manage_offer_providers_adapter, null);
        	
        	onOffImage = (ToggleButton) llChild.findViewById(R.id.onOffImage);
        	imgOfferProvider = (ImageView) llChild.findViewById(R.id.offerProvderImage);
        	txtOfferProviderName = (TextView) llChild.findViewById(R.id.providersName);
        	
        	txtOfferProviderName.setText(offerProvider.getProviderName());
        	String imageURL = offerProvider.getImageUrlSmallStr();
            if(imageURL != null) {
            	String displayImagePathSmall = imageURL;
				String displayImagePathSmallfileName = displayImagePathSmall.substring(displayImagePathSmall.lastIndexOf("/") + 1);
				if(displayImagePathSmallfileName != null && displayImagePathSmallfileName != "") {
	            	File imgFile = new File(currentActivity.getBaseContext().getFilesDir(), displayImagePathSmallfileName);
		            if(!imgFile.exists()){
		            	//String webServiceUrl = currentActivity.getResources().getString(R.string.webservice_url);
		            	//String displayImagePathSmall = webServiceUrl + "/Images/" + imageURL;
		            	new DownloadImages(ManageOfferProvidersActivity.this).execute(imageURL, imgOfferProvider);
		            } else {
		            	imgOfferProvider.setImageURI(Uri.fromFile(imgFile));
		            }
				}
            }           
            boolean mappedWithUser = false;
	        if(userOfferProvidersListFromDB != null) {
	            for(int k = 0; k < userOfferProvidersListFromDB.size(); k++) {
	            	if(userOfferProvidersListFromDB.get(k)== offerProvider.getProviderId()) {
	            		mappedWithUser = true;
	            	}
	            }
            }
            if(mappedWithUser) {
            	onOffImage.setBackgroundResource(R.drawable.onbtn);
            	onOffImage.setChecked(true);
            } else {
            	onOffImage.setBackgroundResource(R.drawable.offbtn);
            	onOffImage.setChecked(false);
            }
            
            onOffImage.setId(Integer.parseInt(offerProvider.getProviderId()+""));
            onOffImage.setOnClickListener(new ImageView.OnClickListener() {
                public void onClick(View v) {
                	Boolean isChecked=  ((ToggleButton) v).isChecked();
    				if(isChecked) {
    					v.setBackgroundResource(R.drawable.onbtn);
    					userOfferProvidersListFromDB.add(v.getId());
    				} else {
    					v.setBackgroundResource(R.drawable.offbtn);
    					
    					int i=0;
    		             boolean ret = false;
    		             if(userOfferProvidersListFromDB.size() > 0){
    			             for (i = 0; i < userOfferProvidersListFromDB.size(); i++) {
    			     			if (userOfferProvidersListFromDB.get(i) == v.getId()) {
    			     				ret = true;
    			     				break;
    			     			}
    			             }
    		             }
    		             if(ret) {
    		            	 userOfferProvidersListFromDB.remove(i);
    		             }
    				}
    				
    			}
    		});
        	
        	layout.addView(llChild);
        }
        }
	}
	

	@Override
	    public Object onRetainNonConfigurationInstance() {
	    	final SavedState data = new SavedState();
	        data.setSelectedOfferProviderList(userOfferProvidersListFromDB);
	    	return data;
	    }


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.manage_offer_providers, menu);
		return true;
	}
	
	@Override
    public void onDestroy() {
        super.onDestroy();
        System.gc();
	}
	
	public void saveUserOfferProviders(View view) {
		LinearLayout layout = (LinearLayout) findViewById(R.id.rlofferProvidersList);
		List<Integer> checkedItems = new ArrayList<Integer>();
		for(int i=0; i<layout.getChildCount(); i++) {
			View viewl = layout.getChildAt(i);
			if(viewl instanceof RelativeLayout) {
				LinearLayout layoutSub = (LinearLayout) viewl.findViewById(R.id.llofferproviderMain);
				for(int j=0; j < layoutSub.getChildCount(); j++) {
					final View mChild = layoutSub.getChildAt(j);
					if(mChild instanceof ToggleButton) {
						ToggleButton onOffImage = (ToggleButton) mChild;
						if(onOffImage.isChecked()) {
							checkedItems.add(onOffImage.getId());
						}
					}
				}
			}
		}
		
		if(userRegistrationData != null) {
			userRegistrationData.setSelectedOfferProviders(new ArrayList<Integer>(checkedItems));
		}
		TextView resultMsg = (TextView) findViewById(R.id.resultMsg);
		resultMsg.setVisibility(View.VISIBLE);
		try {
			if("newuser".equalsIgnoreCase(pageFlow)) {
				
				long userId = UserSettingsDAO.createUserProfile(currentActivity, userRegistrationData);
				if(userId > 0) {
					userRegistrationData.setUserId(userId);
					//Insert User Information
					UserDAO.insertUserDetails(userRegistrationData, currentActivity, userId);
					
					//TagService.syncTagsToBackend(currentActivity, userId);
					//Save Document providers list
					//new DocumentProvidersDAO().deleteUserDocumentProviders(currentActivity, userId);
					new DocumentProvidersDAO().InsertUserDocumentProviders(currentActivity, userRegistrationData.getSelectedDocumentProviders(), userId);
					
					// Save Offers Providers list
					//new OfferProvidersDAO().deleteUserOfferProviders(currentActivity, userId);
					new OfferProvidersDAO().InsertUserOfferProviders(currentActivity, userRegistrationData.getSelectedOfferProviders(), userId);
					
					//save CloudProvicers
					new CloudProviderDAO().InsertUserCloudProviders(currentActivity, userRegistrationData.getSelectedCloudProviders(), userId);
					
					Intent loginActivity = new Intent(view.getContext(), LoginActivity.class);
					startActivity(loginActivity);
					finish();
				} else {
					resultMsg.setText(R.string.new_user_registration_failed);
				}
				
			} else {
				//new OfferProvidersDAO().deleteUserOfferProviders(currentActivity, loginUserId);
				String returnMsg = new OfferProvidersDAO().InsertUserOfferProviders(currentActivity, checkedItems, loginUserId);
				resultMsg.setText(returnMsg);
			}
		} catch (Exception ex) {
			resultMsg.setText(R.string.offerproviderssavefailedmsg);
		}
	}
	
	
	public void onClickBackBtn(View view) {
//		if("newuser".equalsIgnoreCase(pageFlow)) {
//			Intent docProvidersActivity = new Intent(view.getContext(), ManageDocumentProvidersActivity.class);
//			docProvidersActivity.putExtra("pageflow", "newuser");
//			docProvidersActivity.putExtra("UserRegistrationData", userRegistrationData);
//			startActivity(docProvidersActivity);
//		} else {
//			Intent settingsActivity = new Intent(view.getContext(), SettingsActivity.class);
//			settingsActivity.putExtra("usertoken", userToken);
//			settingsActivity.putExtra("userName", userName);
//			settingsActivity.putExtra("salt", salt);
//			settingsActivity.putExtra("loginUserId", loginUserId);
//	        startActivity(settingsActivity);
//	        finish();
//		}
		finish();
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
	    
	    
}
