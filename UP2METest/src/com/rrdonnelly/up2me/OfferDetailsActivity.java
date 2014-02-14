package com.rrdonnelly.up2me;

import java.io.IOException;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.TimeZone;

import android.accounts.AccountManager;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.DialogInterface.OnKeyListener;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.ParseException;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Debug;
import android.provider.CalendarContract.Events;
import android.provider.CalendarContract.Reminders;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.dropbox.sync.android.DbxAccountManager;
import com.dropbox.sync.android.DbxException;
import com.dropbox.sync.android.DbxException.Unauthorized;
import com.dropbox.sync.android.DbxFile;
import com.dropbox.sync.android.DbxFileSystem;
import com.dropbox.sync.android.DbxPath;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;
import com.google.api.client.http.FileContent;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.drive.model.File;
import com.rrdonnelly.up2me.R.color;
import com.rrdonnelly.up2me.adapter.ImageListViewAdapter;
import com.rrdonnelly.up2me.dao.OfferDAO;
import com.rrdonnelly.up2me.dao.OfferProvidersDAO;
import com.rrdonnelly.up2me.dao.Tag;
import com.rrdonnelly.up2me.dao.TagDAO;
import com.rrdonnelly.up2me.json.OfferProviders;
import com.rrdonnelly.up2me.services.OfferService;
import com.rrdonnelly.up2me.util.ControlApplication;
import com.rrdonnelly.up2me.util.GPSTracker;
import com.rrdonnelly.up2me.util.tags.AsyncTagSyncToBackend;
import com.rrdonnelly.up2me.valueobjects.Offer;
import com.rrdonnelly.up2me.valueobjects.SavedState;
import com.rrdonnelly.up2me.widget.menu.ActionItem;
import com.rrdonnelly.up2me.widget.menu.QuickAction;
import com.testflightapp.lib.TestFlight;


public class OfferDetailsActivity extends Activity {

	QuickAction quickAction; 
	public String userID=null;
    public String userToken=null;
    public String salt=null;
    public String offerScreen=null;
    public long loginUserId;
    
    
	private String userName = null;
	private String fromScreen = null;
	private String parentScreen = null;

    
	private static final int ID_UP     = 1;
	private static final int ID_DOWN   = 2;
	private static final int ID_SEARCH = 3;
	private static final int ID_INFO   = 4;
	private static final int ID_ERASE  = 5;	
	private static final int ID_OK     = 6;
	private static final String TAG_DEFAULT_COLOR = "#CACBDA";
    
    List<String> groupList;
    //Map<String, ArrayList<Offer>>  offersListToExpand;
    ArrayList<Offer> offersList;
    ArrayList<Offer> offersNearByMeList;
    
    String[] offersNamesList;
    String currentOfferName;
    String nextOfferName;
    String previousOfferName;
    String postalCode="";
    
    ListView lv1;
    ListView lv2;
    
    ImageView fullOfferImageDisplay;
    int fullOfferImageDisplayHeight;
    
    static Activity currentActivity;
    
    ImageButton OffersNearByMeToggleBtn;
    ImageButton AllOffersToggleBtn;
    
    int nextOfferPostion = 0;
    int previousOfferPostion = 0;
    int currentOfferPostion = 0;
    ListView currentListView;
    String currentOfferId = "0";
    
    String offerText = null;
    ImageButton actionButton;
    String expiresOnDisplayDate;
    String offerName;
    ProgressDialog progressBar;
    String offerDescription =null;
    
    /**
	 * Google Drive
	 */
	  static final int REQUEST_ACCOUNT_PICKER = 1;
	  static final int REQUEST_AUTHORIZATION = 2;
	  static final int CAPTURE_IMAGE = 3;

	  private static Uri fileUri;
	  private static Drive service;
	  private GoogleAccountCredential credential;
	  
	  String strImagePath;
    
    ToggleButton checkIsFavorite;
	OfferProvidersDAO offerProvidersDAO = new OfferProvidersDAO();
	int providerId = 0;
	boolean isFavourite = false;
	
	private DbxAccountManager mDbxAcctMgr;
	private static final int REQUEST_LINK_TO_DBX = 0;
    
	int userSelectedOfferListViewForRetain = 1;
	ListView lvAllOffersTeasers;
	ListView lvOffersNearBymeTeasers;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_offer_details);
		
		currentActivity = this;
		
		actionButton = (ImageButton) findViewById(R.id.pageoffer_subnav_icon4);
		actionButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				showActionPopup(v);
			}
		});
		
	
        GPSTracker gpsTracker = new GPSTracker(this);

        if (gpsTracker.canGetLocation())
        {
            //TextView textview = null;
        	String stringLatitude = String.valueOf(gpsTracker.latitude);
            postalCode = gpsTracker.getPostalCode(this);
        }
        else
        {
            gpsTracker.showSettingsAlert();
        }
	
        
		userToken = getIntent().getStringExtra("usertoken");
		userName = getIntent().getStringExtra("userName");
		salt = getIntent().getStringExtra("salt");
		loginUserId = getIntent().getLongExtra("loginUserId", -1);
		fromScreen = getIntent().getStringExtra("fromScreen");
		parentScreen = getIntent().getStringExtra("parentScreen");

		String offerId = getIntent().getExtras().getString("offer_id");
        loginUserId=getIntent().getExtras().getLong("loginUserId");
        offerScreen=getIntent().getExtras().getString("fromScreen");
        currentOfferPostion = getIntent().getExtras().getInt("clickposition");
		offerDescription = getIntent().getExtras().getString("offer_description");
		lvAllOffersTeasers = (ListView) findViewById(R.id.offersTeaserImagesList);
        lvOffersNearBymeTeasers = (ListView) findViewById(R.id.offersTeaserImagesNearMeList);
        
        AllOffersToggleBtn = (ImageButton) findViewById(R.id.AllOffersToggleBtn);
        OffersNearByMeToggleBtn = (ImageButton) findViewById(R.id.OffersNearByMeToggleBtn);
        
        
        
        lv1 = (ListView) findViewById(R.id.offersTeaserImagesList);
        
        currentListView = lv1;
        
        lv2 = (ListView) findViewById(R.id.offersTeaserImagesNearMeList);
        
		
		
		currentOfferId = offerId;
		   SavedState data=(SavedState)getLastNonConfigurationInstance();
	        if (data != null){
	        	currentOfferId = data.getSelectedID()+"";
	        	Offer offer;
				if (currentOfferId != null) {
					offer = new OfferDAO().getOfferByDescription(currentActivity, currentOfferId, null, loginUserId);
				} else {
					offer = new OfferDAO().getOfferByDescription(currentActivity, null, offerDescription, loginUserId);
				}
				createAllOffersCollection(offer.getName());
		        createNearByMeOffersCollection(offer.getName(),postalCode);
	        	
	        	if(data.getSelectedListView() == 1) {
	        		currentListView = lv1;
	        		lvOffersNearBymeTeasers.setVisibility(View.GONE);
					lvAllOffersTeasers.setVisibility(View.VISIBLE);
					userSelectedOfferListViewForRetain = 1;
	        	} else {
	        		currentListView = lv2;
	        		lvOffersNearBymeTeasers.setVisibility(View.VISIBLE);
					lvAllOffersTeasers.setVisibility(View.GONE);
					userSelectedOfferListViewForRetain = 2;
	        	}
	        	showOfferImage(offer);
	        	//sideMenuUpandDownArrows();
	        } else {
	        	ExpandandCollapse("alloffers");
	        	new OfferBackgroundTask().execute("");
	        }
	}
	
	public void showOfferImage(Offer offer){
		try {
			ImageView imgHomeBack = (ImageView) findViewById(R.id.mailIcon);
			Button btnOfferBack = (Button) findViewById(R.id.offersBackbtn);
			if(offerScreen.equalsIgnoreCase("offerScreen")) {
				imgHomeBack.setVisibility(View.INVISIBLE);
				btnOfferBack.setVisibility(View.VISIBLE);
			} else {
				imgHomeBack.setVisibility(View.VISIBLE);
				btnOfferBack.setVisibility(View.INVISIBLE);
			}
			
		
		
		offerName = offer.getName();
        String offerImagePath = offer.getOfferImagePath();
        expiresOnDisplayDate = offer.getExpiresOnDisplayDate();
        offerText = offer.getStatementText();
        
        ImageView offerImage = (ImageView) findViewById(R.id.offerImage);
        java.io.File imgFile = new java.io.File(offerImagePath);
        if(imgFile.exists()){
            Log.w("ItemListBaseAdapter", "setting image :"+imgFile.getAbsolutePath());
           // Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
            //offerImage.setImageBitmap(myBitmap);
            offerImage.setImageURI(Uri.fromFile(imgFile));
        }
       
        Button allOffersText = (Button) findViewById(R.id.alloffersText);
        allOffersText.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				ExpandandCollapse("alloffers");
			}
		});
        
        Button offersNearMeText = (Button) findViewById(R.id.offersNearMeText);
        offersNearMeText.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				ExpandandCollapse("offersnearbyme");
			}
		});
        
        btnOfferBack.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				showOfferDetailsHome(v);
			}
		});
        
        
         int i =0;
         
         Offer offerObj= null;
        if(offersList != null && offersList.size() > 0){
        	Iterator<Offer> itr = offersList.iterator();
        	while(itr.hasNext()){
        		 offerObj = itr.next();
        		if(currentOfferId != null && currentOfferId.equalsIgnoreCase(offerObj.getId())){
        			currentOfferPostion = i;
        			break;
        		}
        		i++;
        	}
        }
        
        if(offerObj != null){
        	offersList.remove(offerObj);
        	offersList.add(0, offerObj);
        	currentOfferPostion = 0; 
        }
        
        lv1.setAdapter(new ImageListViewAdapter(this, offersList));
        lv2.setAdapter(new ImageListViewAdapter(this, offersNearByMeList));
        
        // To show first Image as a Default Image from List View.
        if(currentListView.getAdapter().getCount() > 0) {
        	showOfferImage(currentListView, currentOfferPostion);
        } else {
        	if(lv1.getAdapter().getCount() > 0) {
        		showOfferImage(lv1, currentOfferPostion);
        	}
        }
        
    	lv1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> a, View v, int position, long id) {
            	userSelectedOfferListViewForRetain = 1;
            	currentListView = lv1;
            	createAllOffersCollection(offerName);
        		Object o = lv1.getItemAtPosition(position);
                Offer offer = (Offer) o;
                currentOfferId = offer.getId();
                
                Offer offerObj= null;   
                if(offersList != null && offersList.size() > 0){
                	Iterator<Offer> itr = offersList.iterator();
                	while(itr.hasNext()){
                		 offerObj = itr.next();
                		if(currentOfferId != null && currentOfferId.equalsIgnoreCase(offerObj.getId())){
                			break;
                		}
                	}
                }
                
                if(offerObj != null){
                	offersList.remove(offerObj);
                	offersList.add(0, offerObj);
                	currentOfferPostion = 0; 
                }
                
                lv1.setAdapter(new ImageListViewAdapter(currentActivity, offersList));
            	
                    showOfferImage(currentListView, currentOfferPostion);
                }
        });
        
        lv2.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> a, View v, int position, long id) {
            	currentOfferPostion = position;
            	currentListView = lv2;
            	userSelectedOfferListViewForRetain = 2;
            	/*if(offersNearByMeList.size() > 0) {
					showOfferImage(lv2, currentOfferPostion);
				}*/
            	
	        	createNearByMeOffersCollection(offerName, postalCode);
	        	
	    		Object o = lv2.getItemAtPosition(position);
	            Offer offer = (Offer) o;
	            currentOfferId = offer.getId();
	            
	            Offer offerObj= null;   
	            if(offersNearByMeList != null && offersNearByMeList.size() > 0){
	            	Iterator<Offer> itr = offersNearByMeList.iterator();
	            	while(itr.hasNext()){
	            		 offerObj = itr.next();
	            		if(currentOfferId != null && currentOfferId.equalsIgnoreCase(offerObj.getId())){
	            			break;
	            		}
	            	}
	            }
	            
	            if(offerObj != null){
	            	offersNearByMeList.remove(offerObj);
	            	offersNearByMeList.add(0, offerObj);
	            	currentOfferPostion = 0; 
	            }
	            
	            lv2.setAdapter(new ImageListViewAdapter(currentActivity, offersNearByMeList));
	        	
	                showOfferImage(currentListView, currentOfferPostion);
	            }
        });
        
        AllOffersToggleBtn.setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						try{
							ExpandandCollapse("alloffers");
						}
						catch(Exception e){
							e.printStackTrace();
						}
					}
				});
        
        // We are setting the Offers Near by me List Value height as 0 at first time page load.
        //ListView lvOffersNearBymeTeasers = (ListView) findViewById(R.id.offersTeaserImagesNearMeList);
		/*ViewGroup.LayoutParams OffersNearBymeTeasersparams = lv2.getLayoutParams();
		OffersNearBymeTeasersparams.height = 0;
		lv2.setLayoutParams(OffersNearBymeTeasersparams);*/
		if(userSelectedOfferListViewForRetain == 1) {
			OffersNearByMeToggleBtn.setImageResource(R.drawable.dropdown_arrowup);
		} else {
			OffersNearByMeToggleBtn.setImageResource(R.drawable.dropdown_arrowdown);
		}
        OffersNearByMeToggleBtn.setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						try{
							ExpandandCollapse("offersnearbyme");
						}
						catch(Exception e){
							e.printStackTrace();
						}
					}
				});
		
		// Next and Back buttons functionality 
		currentOfferName = offerName;
		offersNamesList = getIntent().getStringArrayExtra("offers_names_list");
		
		}catch (Exception ex) {
			ex.printStackTrace();
		}
		
		
		displayMenu(currentOfferId, loginUserId);
		//logHeap("Offer Details Activity - OnCreate method ending ");
		

		checkIsFavorite = (ToggleButton) findViewById(R.id.pageoffer_subnav_icon2);
		
		providerId = offerProvidersDAO.getProviderId(offerName, currentActivity);
        
		isFavourite = offerProvidersDAO.isFavourit(loginUserId, providerId, currentActivity);
		
        if(isFavourite) {
        	checkIsFavorite.setBackgroundResource(R.drawable.pageoffer_subnav_icon2_yellowstar);
        	checkIsFavorite.setChecked(true);
        } else {
        	checkIsFavorite.setBackgroundResource(R.drawable.pageoffer_subnav_icon2);
        	checkIsFavorite.setChecked(false);
        }
        
        checkIsFavorite.setOnClickListener(new ImageView.OnClickListener() {
            public void onClick(View v) {
            	Boolean isChecked=  ((ToggleButton) v).isChecked();
				if(isChecked) {
					v.setBackgroundResource(R.drawable.pageoffer_subnav_icon2_yellowstar);
					try {
            			ConnectivityManager cm = (ConnectivityManager) currentActivity.getSystemService(Activity.CONNECTIVITY_SERVICE);
            			if(cm != null && cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected()){
            				OfferService.updateFavourite(currentActivity, loginUserId, providerId, true);
            				saveFavorite(true,false);
            			} else{
            				saveFavorite(true,true);
            			}
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				} else {
					v.setBackgroundResource(R.drawable.pageoffer_subnav_icon2);
					try {
            			ConnectivityManager cm = (ConnectivityManager) currentActivity.getSystemService(Activity.CONNECTIVITY_SERVICE);
            			if(cm != null && cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected()){
            				OfferService.updateFavourite(currentActivity, loginUserId, providerId, false);
            				saveFavorite(false,false);
            			} else{
            				saveFavorite(false,true);
            			}
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		});
        
     
        	currentListView = lv1;
        	
       

	}
	
	public void onClickPageOfferNextLink(View view){
        System.out.println("PageOffer Next button clicked");
        try{
        	showOfferImage(currentListView, nextOfferPostion);
		}
		catch(Exception e){
			e.printStackTrace();
		}
    }
	
	 public void onClickPageOfferBackLink(View view){
	        System.out.println("PageOffer Previous button clicked");
	        try{
        		showOfferImage(currentListView, previousOfferPostion);
			}
			catch(Exception e){
				e.printStackTrace();
			}
	    }
	 
    public void showOfferDetailsHome(View view){
        System.out.println("showofferDetailsHome button clicked");
//		if ("offerScreen".equals(offerScreen)) {
//			Intent offersListActivity = new Intent(view.getContext(), OfferListActivity.class);
//			offersListActivity.putExtra("usertoken", userToken);
//			offersListActivity.putExtra("userName", userName);
//			offersListActivity.putExtra("salt", salt);
//			offersListActivity.putExtra("loginUserId", loginUserId);
//			startActivityForResult(offersListActivity, 0);
//		} else {
//            Intent dashBoardIntent = new Intent(view.getContext(), DashBoardActivity.class);
//            dashBoardIntent.putExtra("usertoken", userToken);
//            dashBoardIntent.putExtra("userName", userName);
//            dashBoardIntent.putExtra("salt", salt);
//            dashBoardIntent.putExtra("loginUserId", loginUserId);
//            startActivityForResult(dashBoardIntent, 0);
//        }
        finish();
    }
    
    @Override
    public Object onRetainNonConfigurationInstance() {
    	
    	final SavedState data = new SavedState();
    	data.setSelectedID(Integer.parseInt(currentOfferId));
    	data.setSelectedListView(userSelectedOfferListViewForRetain);
        return data;
    }

	public void showOfferImage(ListView lv1, int position) {
		currentOfferPostion = position;
		currentListView = lv1;
		navigationBtnDisplay(lv1);
		Object o = lv1.getItemAtPosition(position);
        Offer offer = (Offer) o;
        
        expiresOnDisplayDate= offer.getExpiresOnDisplayDate();
        
//        DateFormatUtil date = new DateFormatUtil();
//        String strDate = date.offersPageDateFormat("MM/dd/yyyy", "MMM dd", expiresOnDisplayDate).toString().trim();
        
        TextView textViewOfferLable = (TextView) findViewById(R.id.Offerdescription);
        textViewOfferLable.setText("Offer");

       TextView textViewWithDate = (TextView) findViewById(R.id.OfferdescriptionWithdate);
       textViewWithDate.setText(" " + " • " + expiresOnDisplayDate);
        
        TextView textOfferDescription = (TextView) findViewById(R.id.offerName);
        textOfferDescription.setText(offerName);
        
        currentOfferId = offer.getId();
        
        displayMenu(currentOfferId, loginUserId);
        if(quickAction!=null){
        	quickAction.setOfferId(Long.parseLong(offer.getId()));	
        }
        offerText = offer.getStatementText();
        
        OfferDAO offerDAO = new OfferDAO();
        long offerId = Long.parseLong(offer.getId());
        try {
	        ConnectivityManager cm = (ConnectivityManager) this.getSystemService(Activity.CONNECTIVITY_SERVICE);
			if(cm != null && cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected()){
				OfferService.readUnreadOffer(currentActivity, loginUserId, offerId, true);
				offerDAO.updateRead(offerId,1, currentActivity, 0);
			} else {
				offerDAO.updateRead(offerId,1, currentActivity, 1);
			}
        } catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
        int tagId = 0;
        tagId = offerDAO.getTagIdFromOffer(offer.getId(), currentActivity, loginUserId);
        
        ImageView tagButton1 = (ImageView) (findViewById(R.id.pageoffer_subnav_icon1)); 
        TagDAO tagDAO = new TagDAO();
        String tagColorCode = tagDAO.getColorCode(tagId, currentActivity, loginUserId);
        tagButton1.setBackgroundColor(Color.parseColor(tagColorCode));
        
        
        ImageView imageView = (ImageView) findViewById(R.id.fullOfferImageDisplay);
        String imageURL = offer.getOfferImagePath();
        java.io.File imgFile = new java.io.File(imageURL);
        if(imgFile.exists()){
            Log.w("Offer Details Activity: showOfferImage method", "setting image :"+imgFile.getAbsolutePath());
            strImagePath = imgFile.getAbsolutePath();
            //Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
            //Bitmap myBitmap;
            try {
	            //myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
	            //imageView.setImageBitmap(myBitmap);
	            imageView.setImageURI(Uri.fromFile(imgFile));
            } catch (OutOfMemoryError ome){
            	Log.w("Offer Details Activity: showOfferImage method", "setting image size is too large :"+imgFile.getAbsolutePath());
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inDither = false;
                options.inPurgeable = true;
                options.outHeight = 100;
                options.outWidth = 100;
                options.inSampleSize = 2;
                options.inTempStorage=new byte[32 * 1024]; 

                //myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath(), options);
                //imageView.setImageBitmap(myBitmap);
                //myBitmap.recycle();
                imageView.setImageURI(Uri.fromFile(imgFile));
            } finally {
            	//myBitmap = null;
            	System.gc();
            }
        }
    }
	
	private void ExpandandCollapse(String toggleBtnText) {
		
		lvAllOffersTeasers.setBackgroundColor(color.offer_color2);
		
		lvOffersNearBymeTeasers.setBackgroundColor(color.offer_color2);
		
		try {
			if (toggleBtnText.equalsIgnoreCase("alloffers")) { 
				if(lvAllOffersTeasers.getVisibility() == View.VISIBLE) {
					lvAllOffersTeasers.setVisibility(View.GONE);
					lvOffersNearBymeTeasers.setVisibility(View.VISIBLE);
				} else {
					lvAllOffersTeasers.setVisibility(View.VISIBLE);
					lvOffersNearBymeTeasers.setVisibility(View.GONE);
				}
				if(offersList != null && offersList.size() > 0){
					showOfferImage(lv1, currentOfferPostion);
				}
			} else if("offersnearbyme".equalsIgnoreCase(toggleBtnText)) {
				if(lvOffersNearBymeTeasers.getVisibility() == View.VISIBLE) {
					lvAllOffersTeasers.setVisibility(View.VISIBLE);
					lvOffersNearBymeTeasers.setVisibility(View.GONE);
				} else {
					lvOffersNearBymeTeasers.setVisibility(View.VISIBLE);
					lvAllOffersTeasers.setVisibility(View.GONE);
				}
				if(offersNearByMeList != null && offersNearByMeList.size() > 0) {
					showOfferImage(lv2, currentOfferPostion);
				}
			}
			
			sideMenuUpandDownArrows();

		}catch(Exception e) {
			e.printStackTrace();
		} 
	}
	
	private void sideMenuUpandDownArrows() {
		if(lvAllOffersTeasers.getVisibility() == View.VISIBLE) {
			AllOffersToggleBtn.setImageResource(R.drawable.dropdown_arrowdown);
			currentListView = lv1; 
			userSelectedOfferListViewForRetain = 1;
		} else {
			AllOffersToggleBtn.setImageResource(R.drawable.dropdown_arrowup);
			currentListView = lv2;
			userSelectedOfferListViewForRetain = 2;
		}
		
		if(lvOffersNearBymeTeasers.getVisibility() == View.VISIBLE) {
			OffersNearByMeToggleBtn.setImageResource(R.drawable.dropdown_arrowdown);
			currentListView = lv2;
			userSelectedOfferListViewForRetain = 2;
		} else {
			OffersNearByMeToggleBtn.setImageResource(R.drawable.dropdown_arrowup);
			currentListView = lv1;
			userSelectedOfferListViewForRetain = 1;
		}
	}

	private void createAllOffersCollection(String offerDescription) {
		offersList = new OfferDAO().getUserAllOffers(currentActivity, offerDescription,loginUserId, " expiresOnDisplayDate asc");
	}
	
	private void createNearByMeOffersCollection(String offerDescription, String postalCode) {
        offersNearByMeList = new OfferDAO().getUserAllOffersNearByMe(currentActivity, offerDescription, postalCode,loginUserId);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.offer_details, menu);
		return true;
	}
		
	@Override
    public void onDestroy() {
        super.onDestroy();
        System.gc();
	}
	
	public void navigationBtnDisplay(ListView lv) {
		int size = lv.getCount();
		ImageView imgNext = (ImageView) findViewById(R.id.pageoffer_next_link);
		ImageView imgPrevious = (ImageView) findViewById(R.id.pageoffer_back_link);
		
		for (int i = 0; i < size; i++) {
			if (i== currentOfferPostion) {
				if (i == size - 1) {
					nextOfferPostion = 0;
					imgNext.setVisibility(View.INVISIBLE);
				} else {
					nextOfferPostion = i + 1;
					imgNext.setVisibility(View.VISIBLE);
				}

				if (i == 0) {
					previousOfferPostion = size- 1;
					imgPrevious.setVisibility(View.INVISIBLE);
				} else {
					previousOfferPostion = i - 1;
					imgPrevious.setVisibility(View.VISIBLE);
				}
			}
		}
	}
	
	public static void logHeap(String logMethodLocation) {
        Double allocated = Double.valueOf(Debug.getNativeHeapAllocatedSize())/Double.valueOf((1048576));
        Double available = Double.valueOf(Debug.getNativeHeapSize())/1048576.0;
        Double free = Double.valueOf(Debug.getNativeHeapFreeSize())/1048576.0;
        DecimalFormat df = new DecimalFormat();
        df.setMaximumFractionDigits(2);
        df.setMinimumFractionDigits(2);

        TestFlight.passCheckpoint(logMethodLocation + " debug.heap native: allocated " + df.format(allocated) + "MB of " + df.format(available) + "MB (" + df.format(free) + "MB free)");
        Log.w("Memory Sizes: ",logMethodLocation + " debug.heap native: allocated " + df.format(allocated) + "MB of " + df.format(available) + "MB (" + df.format(free) + "MB free)");
        TestFlight.passCheckpoint(logMethodLocation + " debug.memory: allocated: " + df.format(Double.valueOf(Runtime.getRuntime().totalMemory()/1048576)) + "MB of " + df.format(Double.valueOf(Runtime.getRuntime().maxMemory()/1048576))+ "MB (" + df.format(Double.valueOf(Runtime.getRuntime().freeMemory()/1048576)) +"MB free)");
        Log.w("Memory Sizes: ",logMethodLocation + " debug.memory: allocated: " + df.format(Double.valueOf(Runtime.getRuntime().totalMemory()/1048576)) + "MB of " + df.format(Double.valueOf(Runtime.getRuntime().maxMemory()/1048576))+ "MB (" + df.format(Double.valueOf(Runtime.getRuntime().freeMemory()/1048576)) +"MB free)");
    }
	
	private void displayMenu(String offerId, long userId){
		
		List<Tag> tagsListFromDB = new TagDAO().getAllTags(currentActivity, loginUserId);
		int i = 0;
		
		OfferDAO offerDAO = new OfferDAO();
		int tagId = offerDAO.getTagId(currentActivity, userId, Long.parseLong(offerId));
		
		//final QuickAction quickAction = new QuickAction(this, QuickAction.ANIM_GROW_FROM_CENTER);
		quickAction = new QuickAction(this, QuickAction.ANIM_GROW_FROM_CENTER);
		
		ImageView tagButton1 = (ImageView) (findViewById(R.id.pageoffer_subnav_icon1)); 
		
		quickAction.setMainPageTagImageView(tagButton1);
		
		quickAction.setCurrentActivity(currentActivity);
		quickAction.setUserId(userId);
		quickAction.setOfferId(Long.parseLong(offerId));
		ActionItem actionItem = null;
		
		for(Tag tag : tagsListFromDB){
			boolean isTagged = false;
			if(tag.getTagId() == tagId){
				isTagged = true;
			}
			if(tag.getName().length()!=0){
				actionItem = new ActionItem(tag.getTagId(), tag.getName(), getResources().getDrawable(R.drawable.tabbutton),isTagged,Long.valueOf(offerId), userId,tag.getColorCode());
				quickAction.addActionItem(actionItem);
				i++;
			}
		}
		tagsListFromDB = null;
		
		actionItem = new ActionItem(0, "Add Tag", getResources().getDrawable(R.drawable.tabbutton),false,Long.valueOf(offerId), userId,TAG_DEFAULT_COLOR);
		quickAction.addActionItem(actionItem);
		
		/*ActionItem nextItem 	= new ActionItem(ID_DOWN, "Next", getResources().getDrawable(R.drawable.tabbutton),true);
		ActionItem prevItem 	= new ActionItem(ID_UP, "Prev", getResources().getDrawable(R.drawable.tabbutton),true);
        ActionItem searchItem 	= new ActionItem(ID_SEARCH, "Find", getResources().getDrawable(R.drawable.tabbutton),false);
        ActionItem infoItem 	= new ActionItem(ID_INFO, "Info", getResources().getDrawable(R.drawable.tabbutton),false);
        ActionItem eraseItem 	= new ActionItem(ID_ERASE, "Clear", getResources().getDrawable(R.drawable.tabbutton),false);
        ActionItem okItem 		= new ActionItem(ID_OK, "OK", getResources().getDrawable(R.drawable.tabbutton), false);
		
		
		
        //use setSticky(true) to disable QuickAction dialog being dismissed after an item is clicked
        //prevItem.setSticky(true);
        //nextItem.setSticky(true);
		
		//create QuickAction. Use QuickAction.VERTICAL or QuickAction.HORIZONTAL param to define layout 
        //orientation
		//final QuickAction quickAction = new QuickAction(this, QuickAction.ANIM_GROW_FROM_CENTER);
		
		//add action items into QuickAction
        /*quickAction.addActionItem(nextItem);
		quickAction.addActionItem(prevItem);
        quickAction.addActionItem(searchItem);
        quickAction.addActionItem(infoItem);
        quickAction.addActionItem(eraseItem);
        quickAction.addActionItem(okItem);*/
        
        //Set listener for action item clicked
		quickAction.setOnActionItemClickListener(new QuickAction.OnActionItemClickListener() {			
			@Override
			public void onItemClick(QuickAction source, int pos, int actionId) {
				if(actionId==0){
					showPopup(source,actionId);
				}else{
					
					//Code block to handle isClickOnSameItem - START
					boolean isClickOnSameItem = false;
					OfferDAO offerDAO = new OfferDAO();
					TagDAO tagDAO = new TagDAO();
					ActionItem action = source.getActionItem(pos);
					
					Tag tag = new Tag();
					tag = offerDAO.getTagWithUserIdAndOfferId(loginUserId, Long.parseLong(currentOfferId),currentActivity);
					long prevSelectedTagId = 0l;
					if(tag!=null){
						prevSelectedTagId = tag.getTagId();
					}
					
					
					int currentSelectedTagId = 0;
					
					currentSelectedTagId = tagDAO.getTagIdWithColorCode(action.getTagColorCode(), currentActivity, loginUserId);
					
					
					if(prevSelectedTagId==currentSelectedTagId){
						isClickOnSameItem = true;
					} else {
						isClickOnSameItem = false;
					}
					////Code block to handle isClickOnSameItem - END.
					
					
					tagDAO.updateOrInsertTag(action.getTitle(), action.getTagColorCode(), currentActivity, loginUserId);
					//Toast.makeText(getApplicationContext(), "Tag saved", Toast.LENGTH_SHORT).show();
					
					
					int tagId = tagDAO.getTagIdWithColorCode(action.getTagColorCode(), currentActivity, loginUserId);
					
					final ImageView tagButton = (ImageView) findViewById(R.id.pageoffer_subnav_icon1);
					
					boolean isSlected = offerDAO.tagExistsUserIdAndOfferId(loginUserId, Long.parseLong(currentOfferId),currentActivity);
					
					
					if(!isSlected){
						offerDAO.resetTagToOffer(Long.parseLong(currentOfferId), loginUserId, currentActivity);
						offerDAO.setTagToOffer(Long.parseLong(currentOfferId), loginUserId, tagId, currentActivity);
						tagButton.setBackgroundColor(Color.parseColor(action.getTagColorCode()));
					}else{
						if(isClickOnSameItem){
							offerDAO.resetTagToOffer(Long.parseLong(currentOfferId), loginUserId, currentActivity);
							tagButton.setBackgroundColor(Color.parseColor("#FFFFFF"));
						} else {
							offerDAO.resetTagToOffer(Long.parseLong(currentOfferId), loginUserId, currentActivity);
							offerDAO.setTagToOffer(Long.parseLong(currentOfferId), loginUserId, tagId, currentActivity);
							tagButton.setBackgroundColor(Color.parseColor(action.getTagColorCode()));
						}
					}
					
					new AsyncTagSyncToBackend(currentActivity,true,false,true)
					.execute(String.valueOf(loginUserId));
					
					/*try {
						TagService.syncTagsToBackend(currentActivity, loginUserId);
						TagService.syncUserOfferTagsToBackend(currentActivity, loginUserId);
					} catch (ClientProtocolException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}*/

					displayMenu(currentOfferId, loginUserId);
					
					
				}

				/*ActionItem actionItem = quickAction.getActionItem(pos);
				actionItem.setSelected(true);*/
				
				//                 
//				//here we can filter which action item was clicked with pos or actionId parameter
//				if (actionId == ID_SEARCH) {
//					Toast.makeText(getApplicationContext(), "Let's do some search action", Toast.LENGTH_SHORT).show();
//				} else if (actionId == ID_INFO) {
//					Toast.makeText(getApplicationContext(), "I have no info this time", Toast.LENGTH_SHORT).show();
//				} else {
//					Toast.makeText(getApplicationContext(), actionItem.getTitle() + " selected", Toast.LENGTH_SHORT).show();
//				}
			}
		});
		
		//set listnener for on dismiss event, this listener will be called only if QuickAction dialog was dismissed
		//by clicking the area outside the dialog.
		quickAction.setOnDismissListener(new QuickAction.OnDismissListener() {			
			@Override
			public void onDismiss() {
//				Toast.makeText(getApplicationContext(), "Dismissed", Toast.LENGTH_SHORT).show();
			}
		});
		
		//show on btn1
		ImageView tagButton = (ImageView) this.findViewById(R.id.pageoffer_subnav_icon1);
		tagButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				quickAction.show(v);
			}
		});

	}
	
	
    public void showTagsMenu(View view){

    }
    
    protected void showPopup(QuickAction source, int actionId) {
		
    	try {
			final Dialog dialog = new Dialog(this, R.style.Theme_CustomAlertDialog);
			dialog.setContentView(R.layout.tag_popup);
			dialog.setOnKeyListener(new OnKeyListener() {

				public boolean onKey(DialogInterface dialog1, int keyCode, KeyEvent event) {
					if (keyCode == KeyEvent.KEYCODE_BACK) {
						dialog.show();
					}
					return true;
				}
			});

			final TextView tvPopupHeader = (TextView) dialog.findViewById(R.id.tvHeading);
			final Button btnButton = (Button) dialog.findViewById(R.id.btnCancel);
			btnButton.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					dialog.dismiss();
				}
			});
			
			
			final Button okButton = (Button) dialog.findViewById(R.id.btnOk);
			final ImageView tagButton = (ImageView) this.findViewById(R.id.pageoffer_subnav_icon1);
			
			okButton.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					EditText etTagName = (EditText) dialog.findViewById(R.id.etTagName); 
					String tagName = etTagName.getText().toString();
					TextView txtView =  (TextView) dialog.findViewById(R.id.tagColor);
					//String tagColor ="#FFFFFF";
					String tagColor ="";
					if(txtView.getText().toString().length()!=0){
						tagColor = txtView.getText().toString();
					}
					
					TagDAO tagDAO = new TagDAO();
					int tagIdWitColorCode = tagDAO.getTagIdWithColorCode(tagColor, currentActivity, loginUserId);
					int tagIdWitName = tagDAO.getTagIdWithTagName(tagName, currentActivity, loginUserId);
					boolean tagExistsWithColor = false;
					boolean tagExistWithSameName = false;
					
					if(tagIdWitColorCode>0){
						tagExistsWithColor = true;
					}
					
					if(tagIdWitName>0){
						tagExistWithSameName = true;
					}
					
					
					if(tagName.trim().length()==0){
						Builder myQuittingDialogBox = new AlertDialog.Builder(OfferDetailsActivity.this); 
						myQuittingDialogBox.setTitle("UP2ME");
						myQuittingDialogBox.setMessage("Please enter tag title."); 
					        	myQuittingDialogBox.setPositiveButton("Ok", new DialogInterface.OnClickListener() { 
					        		public void onClick(DialogInterface dialog, int whichButton) { 
					            	 
					        		}              
					        	});

//					        	myQuittingDialogBox.setNegativeButton("Cancel", new DialogInterface.OnClickListener() { 
//					        		public void onClick(DialogInterface dialog, int whichButton) { 
//					            	
//					             } 
//					        	});
					        	
					        	myQuittingDialogBox.create();
					        	myQuittingDialogBox.show();
					} else if(tagColor.trim().length()==0){
						Builder myQuittingDialogBox = new AlertDialog.Builder(OfferDetailsActivity.this); 
						myQuittingDialogBox.setTitle("UP2ME");
						myQuittingDialogBox.setMessage("Please select color."); 
					        	myQuittingDialogBox.setPositiveButton("Ok", new DialogInterface.OnClickListener() { 
					        		public void onClick(DialogInterface dialog, int whichButton) { 
					            	 
					        		}              
					        	});

//					        	myQuittingDialogBox.setNegativeButton("Cancel", new DialogInterface.OnClickListener() { 
//					        		public void onClick(DialogInterface dialog, int whichButton) { 
//					            	
//					             } 
//					        	});
					        	
					        	myQuittingDialogBox.create();
					        	myQuittingDialogBox.show();
					} else if(tagExistWithSameName){
						Builder myQuittingDialogBox = new AlertDialog.Builder(OfferDetailsActivity.this); 
						myQuittingDialogBox.setTitle("UP2ME");
						myQuittingDialogBox.setMessage("Tag already exists with this name.  Please enter a different tag name."); 
					        	myQuittingDialogBox.setPositiveButton("Ok", new DialogInterface.OnClickListener() { 
					        		public void onClick(DialogInterface dialog, int whichButton) { 
					            	 
					        		}              
					        	});

//					        	myQuittingDialogBox.setNegativeButton("Cancel", new DialogInterface.OnClickListener() { 
//					        		public void onClick(DialogInterface dialog, int whichButton) { 
//					            	
//					             } 
//					        	});
					        	
					        	myQuittingDialogBox.create();
					        	myQuittingDialogBox.show();
					} else if(tagExistsWithColor){
						Builder myQuittingDialogBox = new AlertDialog.Builder(OfferDetailsActivity.this); 
						myQuittingDialogBox.setTitle("UP2ME");
						myQuittingDialogBox.setMessage("Please select a different color. This color is already selected."); 
					        	myQuittingDialogBox.setPositiveButton("Ok", new DialogInterface.OnClickListener() { 
					        		public void onClick(DialogInterface dialog, int whichButton) { 
					            	 
					        		}              
					        	});

//					        	myQuittingDialogBox.setNegativeButton("Cancel", new DialogInterface.OnClickListener() { 
//					        		public void onClick(DialogInterface dialog, int whichButton) { 
//					            	
//					             } 
//					        	});
					        	
					        	myQuittingDialogBox.create();
					        	myQuittingDialogBox.show();
					}else{
						
						if(tagName!=null && (tagName.trim()).length()>0){
							tagDAO.updateOrInsertTag(tagName, tagColor, currentActivity, loginUserId);
							//Toast.makeText(getApplicationContext(), "Tag saved", Toast.LENGTH_SHORT).show();
							
							
							int tagId = tagDAO.getTagIdWithColorCode(tagColor, currentActivity, loginUserId);
							
							OfferDAO offerDAO = new OfferDAO();
							offerDAO.resetTagToOffer(Long.parseLong(currentOfferId), loginUserId, currentActivity);
							offerDAO.setTagToOffer(Long.parseLong(currentOfferId), loginUserId, tagId, currentActivity);
							
							new AsyncTagSyncToBackend(currentActivity,true,false,true)
							.execute(String.valueOf(loginUserId));
							
							/*try {
								TagService.syncTagsToBackend(currentActivity, loginUserId);
								TagService.syncUserOfferTagsToBackend(currentActivity, loginUserId);
							} catch (ClientProtocolException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							} catch (JSONException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}*/
			
							
							tagButton.setBackgroundColor(Color.parseColor(tagColor));
							
							displayMenu(currentOfferId, loginUserId);
							
							dialog.dismiss();
						}else{
							Toast.makeText(getApplicationContext(), "Tag name should not be empty.", Toast.LENGTH_SHORT).show();
						}
					}
					
				}
			});
			
			
			//TODO - All these listeners are duplicate code.  Need to write a seperate inner class and use it.
			final TextView textView1= (TextView) dialog.findViewById(R.id.tag1ColorBox);
			textView1.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					TextView txtView =  (TextView) dialog.findViewById(R.id.tagColor);
					TextView clickedColor =  (TextView) v;
					String tagColor = (clickedColor.getTag()).toString();
					txtView.setText(tagColor);
					//tagButton.setBackgroundColor(Color.parseColor(tagColor));
					
					//Toast.makeText(getApplicationContext(), "Color is selected", Toast.LENGTH_SHORT).show();
				}
			});
			

			final TextView textView2= (TextView) dialog.findViewById(R.id.tag2ColorBox);
			textView2.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					TextView txtView =  (TextView) dialog.findViewById(R.id.tagColor);
					TextView clickedColor =  (TextView) v;
					String tagColor = (clickedColor.getTag()).toString();
					txtView.setText(tagColor);
					//tagButton.setBackgroundColor(Color.parseColor(tagColor));
					//Toast.makeText(getApplicationContext(), "Color is selected", Toast.LENGTH_SHORT).show();
				}
			});

			
			final TextView textView3= (TextView) dialog.findViewById(R.id.tag3ColorBox);
			textView3.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					TextView txtView =  (TextView) dialog.findViewById(R.id.tagColor);
					TextView clickedColor =  (TextView) v;
					String tagColor = (clickedColor.getTag()).toString();
					txtView.setText(tagColor);
					//tagButton.setBackgroundColor(Color.parseColor(tagColor));
					//Toast.makeText(getApplicationContext(), "Color is selected", Toast.LENGTH_SHORT).show();
				}
			});

			
			final TextView textView4= (TextView) dialog.findViewById(R.id.tag4ColorBox);
			textView4.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					TextView txtView =  (TextView) dialog.findViewById(R.id.tagColor);
					TextView clickedColor =  (TextView) v;
					String tagColor = (clickedColor.getTag()).toString();
					txtView.setText(tagColor);
					//tagButton.setBackgroundColor(Color.parseColor(tagColor));
					//Toast.makeText(getApplicationContext(), "Color is selected", Toast.LENGTH_SHORT).show();
				}
			});

			
			final TextView textView5= (TextView) dialog.findViewById(R.id.tag5ColorBox);
			textView5.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					TextView txtView =  (TextView) dialog.findViewById(R.id.tagColor);
					TextView clickedColor =  (TextView) v;
					String tagColor = (clickedColor.getTag()).toString();
					txtView.setText(tagColor);
					//tagButton.setBackgroundColor(Color.parseColor(tagColor));
					//Toast.makeText(getApplicationContext(), "Color is selected", Toast.LENGTH_SHORT).show();
				}
			});

			
			
			final TextView textView6= (TextView) dialog.findViewById(R.id.tag6ColorBox);
			textView6.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					TextView txtView =  (TextView) dialog.findViewById(R.id.tagColor);
					TextView clickedColor =  (TextView) v;
					String tagColor = (clickedColor.getTag()).toString();
					txtView.setText(tagColor);
					//tagButton.setBackgroundColor(Color.parseColor(tagColor));
					//Toast.makeText(getApplicationContext(), "Color is selected", Toast.LENGTH_SHORT).show();
				}
			});

			
			final TextView textView7= (TextView) dialog.findViewById(R.id.tag7ColorBox);
			textView7.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					TextView txtView =  (TextView) dialog.findViewById(R.id.tagColor);
					TextView clickedColor =  (TextView) v;
					String tagColor = (clickedColor.getTag()).toString();
					txtView.setText(tagColor);
					//tagButton.setBackgroundColor(Color.parseColor(tagColor));
					//Toast.makeText(getApplicationContext(), "Color is selected", Toast.LENGTH_SHORT).show();
				}
			});

			
			final TextView textView8= (TextView) dialog.findViewById(R.id.tag8ColorBox);
			textView8.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					TextView txtView =  (TextView) dialog.findViewById(R.id.tagColor);
					TextView clickedColor =  (TextView) v;
					String tagColor = (clickedColor.getTag()).toString();
					txtView.setText(tagColor);
					//tagButton.setBackgroundColor(Color.parseColor(tagColor));
					//Toast.makeText(getApplicationContext(), "Color is selected", Toast.LENGTH_SHORT).show();
				}
			});

			dialog.setCancelable(true);
			dialog.show();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
    
    public void setImageColor(View view){
        System.out.println("showofferDetailsHome button clicked");
    }
    
    public void showActionPopup(View v) {
		// Creating the instance of PopupMenu
		PopupMenu popup = new PopupMenu(OfferDetailsActivity.this, actionButton);
		// Inflating the Popup using xml file
		popup.getMenuInflater().inflate(R.menu.bill_details, popup.getMenu());
		popup.getMenu().getItem(2).setVisible(false);
		popup.getMenu().getItem(3).setVisible(false);
		// registering popup with OnMenuItemClickListener
		popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
			public boolean onMenuItemClick(MenuItem item) {
				switch (item.getItemId()) {
				case R.id.print:
					Toast.makeText(getApplicationContext(), "Print functionality will be implemented in future release", Toast.LENGTH_SHORT).show();
					break;
				case R.id.openInMenu:
					openIn();
					break;
				case R.id.addToDropBoxMenu:
					addToDropBox();
					break;
				case R.id.addToCalenderMenu:
					 addToCalender();
					break;
				case R.id.addToGoogleDrive:
					addToGoogleDrive();
					break;
				case R.id.deleteBillMenu:
					deleteOffer(currentActivity,loginUserId, Long.valueOf(currentOfferId));
					break;
				}

				return true;
			}
		});

		popup.show();// showing popup menu
	}
    
	public void openIn() {
		
		java.io.File fileContent = new java.io.File(strImagePath);
		Intent target = new Intent(Intent.ACTION_VIEW);
		target.setDataAndType(Uri.fromFile(fileContent), "image/*");
		target.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);

		Intent intent = Intent.createChooser(target, "Open In");
		try {
			startActivity(intent);
		} catch (ActivityNotFoundException e) {
			e.printStackTrace();
		}

	}
    
    /**
     * Add to Drop Box
     */
    
    public void addToDropBox() {
		String appKey = "17xvui7szluvwi1";
		String appSecret = "3be8sdjcq74c8at";
		java.io.File fileContent = new java.io.File(strImagePath);
		mDbxAcctMgr = DbxAccountManager.getInstance(getApplicationContext(), appKey, appSecret);
		DbxPath testPath = new DbxPath(DbxPath.ROOT, fileContent.getName());
		DbxFile testFile = null;
		// Create DbxFileSystem for synchronized file access.
		try {
			if (mDbxAcctMgr.hasLinkedAccount()) {
				DbxFileSystem dbxFs = DbxFileSystem.forAccount(mDbxAcctMgr.getLinkedAccount());
				testFile = dbxFs.create(testPath);
				testFile.writeFromExistingFile(fileContent, false);
			} else {
				mDbxAcctMgr.startLink((Activity) this, REQUEST_LINK_TO_DBX);
			}

		} catch (Unauthorized e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (DbxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if (testFile != null)
				testFile.close();
		}
	}
    
    @Override
	protected void onResume() {
		super.onResume();
		if (mDbxAcctMgr != null) {
			if (mDbxAcctMgr.hasLinkedAccount()) {

			}
		}
	}
    
    
    /**
  	 * Add to Calendar
  	 */
  	
  	protected void addToCalender() {
  		try {
  			  Date startDate = null, endDate = null;
  			    String oldFormat = "MM/dd/yyyy";
  			    String newFormat = "MMM dd, yyyy, h:mma";
  
  			    SimpleDateFormat sdf1 = new SimpleDateFormat(oldFormat);
  			    SimpleDateFormat sdf2 = new SimpleDateFormat(newFormat);
  			    try {
  			    	DateFormat formatter = new SimpleDateFormat("MMMM dd, yyyy, h:mma");
  					startDate = formatter.parse(sdf2.format(sdf1.parse(expiresOnDisplayDate)));
  					endDate = formatter.parse(sdf2.format(sdf1.parse(expiresOnDisplayDate)));
  			    } catch (ParseException e) {
  			        e.printStackTrace();
  			    }
  			
  			
  			
  		    Uri EVENTS_URI = Uri.parse(getCalendarUriBase() + "events");
  		    ContentResolver cr = this.getContentResolver();
  		    // event insert
  		    ContentValues values = new ContentValues();
  		    
  		    values.put(Events.EVENT_TIMEZONE, TimeZone.getDefault().getID());
  		    values.put("calendar_id", 1);
  		    values.put("title", offerName);
  		    values.put("allDay", 0);
  		    values.put("dtstart", startDate.getTime());
  		    values.put("dtend", endDate.getTime());
  		    values.put("description", "Offer : "+offerText.toString().trim());
//  		    values.put("visibility", 1);
  		  values.put("hasAlarm", 1);// now
		    Uri event = cr.insert(EVENTS_URI, values);
		        // reminder insert
		        Uri REMINDERS_URI = Uri.parse(getCalendarUriBase() + "reminders");
		        values = new ContentValues();
		        values.put("event_id", Long.parseLong(event.getLastPathSegment()));
		        values.put("method", Reminders.METHOD_ALERT);
		        values.put("minutes", 24 * 60);
		        cr.insert(REMINDERS_URI, values);
		        
		        Toast.makeText(currentActivity, "Offer is added to Calendar Successfully", Toast.LENGTH_LONG).show();

  		} catch (Exception e) {
  			e.printStackTrace();
  		}
  	}
  	
  	
  	public static String getCalendarUriBase() {

  	    String calendarUriBase = null;
  	    Uri calendars = Uri.parse("content://calendar/calendars");
  	    Cursor managedCursor = null;
  		try {
  	        managedCursor = ((Activity) currentActivity).managedQuery(calendars, null, null, null, null);
  	    } catch (Exception e) {
  	        e.printStackTrace();
  	    }
  	    if (managedCursor != null) {
  	        calendarUriBase = "content://calendar/";
  	    } else {
  	        calendars = Uri.parse("content://com.android.calendar/calendars");
  	        try {
  	            managedCursor = ((Activity) currentActivity).managedQuery(calendars, null, null, null, null);
  	        } catch (Exception e) {
  	            e.printStackTrace();
  	        }
  	        if (managedCursor != null) {
  	            calendarUriBase = "content://com.android.calendar/";
  	        }
  	    }
  	    // //Log.d("Calendar", "" + calendarUriBase);
  	    return calendarUriBase;
  	}
  	
	/**
	 * Upload Google Drive
	 */
	
	protected void addToGoogleDrive() {
		try {
			 credential = GoogleAccountCredential.usingOAuth2(this, Arrays.asList(DriveScopes.DRIVE));
			  startActivityForResult(credential.newChooseAccountIntent(), REQUEST_ACCOUNT_PICKER);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	 private Drive getDriveService(GoogleAccountCredential credential) {
		    return new Drive.Builder(AndroidHttp.newCompatibleTransport(), new GsonFactory(), credential)
		        .build();
		  }

	 private void saveFileToDrive() {
		  /*  Thread t = new Thread(new Runnable() {
		      @Override
		      public void run() {
		        try {
		          // File's binary content
		          java.io.File fileContent = new java.io.File(strImagePath);
		          FileContent mediaContent = new FileContent("images/*", fileContent);

		          // File's metadata.
		          File body = new File();
		          body.setTitle(fileContent.getName());
		          body.setMimeType("images/*");

		          File file = service.files().insert(body, mediaContent).execute();
		          if (file != null) {
		            showToast("File uploaded: " + file.getTitle());
		          }
		        } catch (UserRecoverableAuthIOException e) {
		          startActivityForResult(e.getIntent(), REQUEST_AUTHORIZATION);
		        } catch (IOException e) {
		          e.printStackTrace();
		        }
		      }
		    });
		    t.start();*/
		 startProgress();
		 
		  }
	 
	 public void startProgress() {

		 progressBar = new ProgressDialog(currentActivity);
	     progressBar.setCancelable(true);
	     progressBar.setMessage("Please Wait ...");
//	     progressBar.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
//	     progressBar.setProgress(0);
//	     progressBar.setMax(100);
	     progressBar.show();
	     
			new Thread(new Task()).start();
		}
	 
	class Task implements Runnable {
		@Override
		public void run() {
			try {
				// File's binary content
				java.io.File fileContent = new java.io.File(strImagePath);
				FileContent mediaContent = new FileContent("images/*", fileContent);

				// File's metadata.
				File body = new File();
				body.setTitle(fileContent.getName());
				body.setMimeType("images/*");

				File file = service.files().insert(body, mediaContent).execute();
				if (file != null) {
					showToast("File uploaded: " + file.getTitle());
					if (progressBar.isShowing()) {
						progressBar.dismiss();
					}
				}
			} catch (UserRecoverableAuthIOException e) {
				if (progressBar.isShowing()) {
					progressBar.dismiss();
				}
				showToast("Authorization Error Please Try Again");
				startActivityForResult(e.getIntent(), REQUEST_AUTHORIZATION);
			} catch (IOException e) {
				e.printStackTrace();
				if (progressBar.isShowing()) {
					progressBar.dismiss();
				}
				showToast("Network Error Please Try Again");
			}
		}

	}

	 public void showToast(final String toast) {
			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					Toast.makeText(getApplicationContext(), toast, Toast.LENGTH_SHORT).show();
				}
			});
		}
	 
	 @Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		 switch (requestCode) {
			case REQUEST_LINK_TO_DBX:
				if (requestCode == REQUEST_LINK_TO_DBX) {
					if (resultCode == Activity.RESULT_OK) {
						java.io.File fileContent = new java.io.File(strImagePath);
						if (mDbxAcctMgr.hasLinkedAccount()) {
							DbxPath testPath = new DbxPath(DbxPath.ROOT, fileContent.getName());
							DbxFile testFile = null;
							try {
								DbxFileSystem dbxFs = DbxFileSystem.forAccount(mDbxAcctMgr.getLinkedAccount());
								testFile = dbxFs.create(testPath);
								testFile.writeFromExistingFile(fileContent, false);
							} catch (Exception e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							} finally {
								if (testFile != null)
									testFile.close();
							}
						} else {

						}
					} else {

					}
				} else {
					super.onActivityResult(requestCode, resultCode, data);
				}
				break;
			 case REQUEST_ACCOUNT_PICKER:
			      if (resultCode == RESULT_OK && data != null && data.getExtras() != null) {
			        String accountName = data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
			        if (accountName != null) {
			          credential.setSelectedAccountName(accountName);
			          service = getDriveService(credential);
			          saveFileToDrive();
			        }
			      }
			      break;
			    case REQUEST_AUTHORIZATION:
			      if (resultCode == Activity.RESULT_OK) {
			        saveFileToDrive();
			      } else {
			        startActivityForResult(credential.newChooseAccountIntent(), REQUEST_ACCOUNT_PICKER);
			      }
			      break;
			
			}

		super.onActivityResult(requestCode, resultCode, data);
	}
	
	private void saveFavorite(boolean isFavourite, boolean isDirty){

        boolean isUserProviderExists = offerProvidersDAO.isUserProviderExists(String.valueOf(loginUserId), String.valueOf(providerId), currentActivity);

        OfferProviders offerProviders = new OfferProviders();
		
        if(isUserProviderExists){
        	offerProviders.setUserId(loginUserId);
        	offerProviders.setProviderId(providerId);
        	offerProviders.setFavorite(isFavourite);
        	offerProviders.setDirty(isDirty);
        	offerProvidersDAO.addUpdateUserProviders(isUserProviderExists,offerProviders, currentActivity);
        } else{
        	offerProviders.setUserId(loginUserId);
        	offerProviders.setProviderId(providerId);
        	offerProviders.setFavorite(isFavourite);
        	offerProviders.setDirty(isDirty);
        	offerProvidersDAO.addUpdateUserProviders(isUserProviderExists,offerProviders, currentActivity);
        }
	}
	
	public void deleteOffer(Activity currentActivity, long userId, long offerId) {
		
		OfferDAO offerDAO = new OfferDAO();
		
		try {
			ConnectivityManager cm = (ConnectivityManager) this.getSystemService(Activity.CONNECTIVITY_SERVICE);
			if(cm != null && cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected()){
				OfferService.offerDelete(currentActivity, userId, offerId);
				offerDAO.deleteOffer(currentActivity, offerId, userId);
			} else {
				offerDAO.updateIsDirty(offerId, currentActivity, 1);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		if(userSelectedOfferListViewForRetain == 1) {
			offersList = new OfferDAO().getUserAllOffers(currentActivity, offerName,userId, null);
			if(offersList != null && offersList.size() > 0){
		        lv1.setAdapter(new ImageListViewAdapter(this, offersList));
		        showOfferImage(lv1, 0);
			} else{
				Toast.makeText(getApplicationContext(), "No more files to Delete", Toast.LENGTH_LONG).show();
			}
		} else {
			offersNearByMeList = new OfferDAO().getUserAllOffersNearByMe(currentActivity, offerName, postalCode,loginUserId);
			if(offersNearByMeList != null && offersNearByMeList.size() > 0){
		        lv2.setAdapter(new ImageListViewAdapter(this, offersNearByMeList));
		        showOfferImage(lv2, 0);
			} else{
				Toast.makeText(getApplicationContext(), "No more files to Delete", Toast.LENGTH_LONG).show();
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
	        
	        getApp().touch();
	        String TAG= "";
	        Log.d(TAG, "User interaction to "+this.toString());
	    }
	    
	    
	    public class OfferBackgroundTask extends AsyncTask<String, Integer, Offer> {
			private ProgressDialog mProgressDialog;
			int progress;
			int n = 0;
			
			public OfferBackgroundTask() {
				mProgressDialog = new ProgressDialog(currentActivity);
				mProgressDialog.setMax(100);
				mProgressDialog.setProgress(0);
				mProgressDialog.setCancelable(false);
				publishProgress(0);
			}

			@Override
			protected void onPreExecute() {
				mProgressDialog.show();
				super.onPreExecute();
			}

			@Override
			protected void onProgressUpdate(Integer... values) {
				//setProgress(values[0]);
				n = values[0];
				mProgressDialog.setMessage("Loading.. " + n + "%");
			}

			@Override
			protected Offer doInBackground(String... params) {
				Offer offer;
				if (currentOfferId != null) {
					offer = new OfferDAO().getOfferByDescription(currentActivity, currentOfferId, null, loginUserId);
				} else {
					offer = new OfferDAO().getOfferByDescription(currentActivity, null, offerDescription, loginUserId);
				}
				publishProgress(30);
				createAllOffersCollection(offer.getName());
				publishProgress(60);
		        createNearByMeOffersCollection(offer.getName(),postalCode);
		        publishProgress(100);
				return offer;
			}

			@Override
			protected void onPostExecute(Offer offer) {

			  showOfferImage(offer);
				mProgressDialog.dismiss();
			}
		}
	    
	    
}
