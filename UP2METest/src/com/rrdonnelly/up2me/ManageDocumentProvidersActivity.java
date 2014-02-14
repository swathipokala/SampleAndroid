package com.rrdonnelly.up2me;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.rrdonnelly.up2me.dao.DocumentProvidersDAO;
import com.rrdonnelly.up2me.json.DocumentProviders;
import com.rrdonnelly.up2me.services.DocumentProvidersService;
import com.rrdonnelly.up2me.util.ControlApplication;
import com.rrdonnelly.up2me.util.DownloadImages;
import com.rrdonnelly.up2me.util.ImageUtil;
import com.rrdonnelly.up2me.valueobjects.SavedState;
import com.rrdonnelly.up2me.valueobjects.UserRegistration;

import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
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

public class ManageDocumentProvidersActivity extends Activity {

	Activity currentActivity;
	public String userName=null;
	public String userToken=null;
	public String salt=null;
	public long loginUserId = 0l;
	
	public String pageFlow = null;
	List<Integer> userDocumentProvidersListFromDB = new ArrayList<Integer>();
	List<DocumentProviders> documentProvidersListFromDB = null;
	
	UserRegistration userRegistrationData;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_manage_document_providers);
		
		currentActivity = this;
		pageFlow = getIntent().getStringExtra("pageflow");
		userRegistrationData = (UserRegistration) getIntent().getSerializableExtra("UserRegistrationData");
		
		Button saveButtonId = (Button) findViewById(R.id.saveButtonId);
		
		//documentProvidersListFromDB = new DocumentProvidersDAO().getAllDocumentProviders(currentActivity);
		
		SavedState data=(SavedState)getLastNonConfigurationInstance();
        if (data != null){
        	userDocumentProvidersListFromDB = data.getSelectedDocumentProviderList();
        	
        } 
		
		if("newuser".equalsIgnoreCase(pageFlow)) {
			saveButtonId.setText(R.string.continuebtn);
			if(userRegistrationData != null && userRegistrationData.getSelectedDocumentProviders() != null && userRegistrationData.getSelectedDocumentProviders().size() > 0) {
				userDocumentProvidersListFromDB = new ArrayList<Integer>(userRegistrationData.getSelectedDocumentProviders());
			}
			
			try {
				StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
				StrictMode.setThreadPolicy(policy);
				
				documentProvidersListFromDB = DocumentProvidersService.getDocumentProviders(currentActivity);
				
			}catch (Exception ex) {
				ex.printStackTrace();
			}
		} else {
			saveButtonId.setText(R.string.save);
			
			userToken=getIntent().getStringExtra("usertoken");
	        userName=getIntent().getStringExtra("userName");
	        salt=getIntent().getStringExtra("salt");
	        loginUserId=getIntent().getLongExtra("loginUserId", -1);
	        documentProvidersListFromDB = new DocumentProvidersDAO().getAllDocumentProviders(currentActivity);
	        if(data == null){
	        	userDocumentProvidersListFromDB = new DocumentProvidersDAO().getAllUserDocumentProviders(currentActivity, loginUserId);
	        }
		}
        
        LinearLayout layout = (LinearLayout) findViewById(R.id.rlDocumentProvidersList);
        final LayoutInflater  inflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        RelativeLayout llChild;
        ToggleButton onOffImage;
        ImageView imgOfferProvider;
        TextView txtOfferProviderName;
        
        if(documentProvidersListFromDB != null) {
        for (int i = 0; i < documentProvidersListFromDB.size(); i++) {
        	DocumentProviders documentProvider = documentProvidersListFromDB.get(i);
        	
        	llChild = (RelativeLayout) inflater.inflate(R.layout.manage_offer_providers_adapter, null);
        	
        	onOffImage = (ToggleButton) llChild.findViewById(R.id.onOffImage);
        	imgOfferProvider = (ImageView) llChild.findViewById(R.id.offerProvderImage);
        	txtOfferProviderName = (TextView) llChild.findViewById(R.id.providersName);
        	
        	txtOfferProviderName.setText(documentProvider.getProviderName());
            String imageURL = documentProvider.getImageUrlSmallStr();
            if(imageURL != null) {
            	String displayImagePathSmall = imageURL;
				String displayImagePathSmallfileName = displayImagePathSmall.substring(displayImagePathSmall.lastIndexOf("/") + 1);
				if(displayImagePathSmallfileName != null && displayImagePathSmallfileName != "") {
	            	File imgFile = new File(currentActivity.getBaseContext().getFilesDir(), displayImagePathSmallfileName);
		            if(!imgFile.exists()){
		            	/*String webServiceUrl = currentActivity.getResources().getString(R.string.webservice_url);
		            	String displayImagePathSmall = webServiceUrl + "/Images/" + imageURL;*/
						/*String displayImagePathSmallfileName = displayImagePathSmall.substring(displayImagePathSmall.lastIndexOf("/") + 1);
						File fdisplayImagePathSmall = new File(currentActivity.getBaseContext().getFilesDir(), displayImagePathSmallfileName);*/
		            	new DownloadImages(ManageDocumentProvidersActivity.this).execute(imageURL, imgOfferProvider);
		            } else {
		            	imgOfferProvider.setImageURI(Uri.fromFile(imgFile));
		            }
				}
            } 
            
            boolean mappedWithUser = false;
            if(userDocumentProvidersListFromDB != null) {
	            for(int k = 0; k < userDocumentProvidersListFromDB.size(); k++) {
	            	if(userDocumentProvidersListFromDB.get(k) == documentProvider.getProviderId()) {
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
            
            onOffImage.setId(Integer.parseInt(documentProvider.getProviderId()+""));
            onOffImage.setOnClickListener(new ImageView.OnClickListener() {
                public void onClick(View v) {
                	Boolean isChecked=  ((ToggleButton) v).isChecked();
    				if(isChecked) {
    					v.setBackgroundResource(R.drawable.onbtn);
    					userDocumentProvidersListFromDB.add(v.getId());
    				} else {
    					v.setBackgroundResource(R.drawable.offbtn);
    					
							int i = 0;
							boolean ret = false;
							if (userDocumentProvidersListFromDB.size() > 0) {
								for (i = 0; i < userDocumentProvidersListFromDB.size(); i++) {
									if (userDocumentProvidersListFromDB.get(i) == v.getId()) {
										ret = true;
										break;
									}
								}
							}
							if (ret) {
								userDocumentProvidersListFromDB.remove(i);
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
        data.setSelectedDocumentProviderList(userDocumentProvidersListFromDB);
    	return data;
    }


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.manage_document_providers, menu);
		return true;
	}
	
	@Override
    public void onDestroy() {
        super.onDestroy();
        System.gc();
	}

	public void saveUserDocumentProviders(View view) {
		LinearLayout layout = (LinearLayout) findViewById(R.id.rlDocumentProvidersList);
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
			userRegistrationData.setSelectedDocumentProviders(new ArrayList<Integer>(checkedItems));
		}
		
		TextView resultMsg = (TextView) findViewById(R.id.resultMsg);
		resultMsg.setVisibility(View.VISIBLE);
		try {
			if("newuser".equalsIgnoreCase(pageFlow)) {
				Intent manageOfferProviderAct = new Intent(view.getContext(), ManageOfferProvidersActivity.class);
				manageOfferProviderAct.putExtra("pageflow", "newuser");
				manageOfferProviderAct.putExtra("UserRegistrationData", userRegistrationData);
				startActivityForResult(manageOfferProviderAct, 0);
			} else {
				String resultMsgText = new DocumentProvidersDAO().InsertUserDocumentProviders(currentActivity, checkedItems, loginUserId);
				resultMsg.setText(resultMsgText);
			}
		}catch (Exception ex) {
			resultMsg.setText(R.string.documentproviderssavefailedmsg);
		}
	}
	
	public void onClickBackBtn(View view) {
//		if("newuser".equalsIgnoreCase(pageFlow)) {
//			Intent coludProvidersActivity = new Intent(view.getContext(), SelectCloudProviderActivity.class);
//			coludProvidersActivity.putExtra("pageflow", "newuser");
//			coludProvidersActivity.putExtra("UserRegistrationData", userRegistrationData);
//			startActivity(coludProvidersActivity);
//			finish();
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
