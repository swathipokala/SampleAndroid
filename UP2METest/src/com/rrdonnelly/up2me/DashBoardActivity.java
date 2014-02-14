package com.rrdonnelly.up2me;

import android.app.Activity;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.Surface;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.artifex.mupdfdemo.MuPDFActivity;
import com.rrdonnelly.up2me.adapter.ItemListBaseAdapter;
import com.rrdonnelly.up2me.dao.DatabaseHandler;
import com.rrdonnelly.up2me.dao.OfferDAO;
import com.rrdonnelly.up2me.dao.StatementDAO;
import com.rrdonnelly.up2me.dao.UserLibraryDAO;
import com.rrdonnelly.up2me.dao.UserSettingsDAO;
import com.rrdonnelly.up2me.json.Offer;
import com.rrdonnelly.up2me.json.Statement;
import com.rrdonnelly.up2me.json.UserLibrary;
import com.rrdonnelly.up2me.services.AlarmService;
import com.rrdonnelly.up2me.services.UserLibraryService;
import com.rrdonnelly.up2me.util.ControlApplication;
import com.rrdonnelly.up2me.util.DownloadImages;
import com.rrdonnelly.up2me.valueobjects.UserSettings;
import com.testflightapp.lib.TestFlight;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

public class DashBoardActivity extends Activity {

    public String userName=null;
    public String userToken=null;
    public String salt=null;
    public long loginUserId = 0l;
    
    String[] offersNamesList;
    private Activity currentActivity = null;
    
    Button catalogsflyerPlusBtn;
    Button referencePlusBtn;
    
    String[] allStatementsList;
    
    private AlarmService alarm;
    
    Context context;
    SharedPreferences prefsServiceAlarm = null;
	SharedPreferences.Editor prefsServiceAlarmEditor = null;

	private TextView unReadtext;
    private TextView billtext;
    private TextView txtOffersCount;
    private TextView txtLibraryCount;
    UserSettings userSetting;
    
    LinearLayout llBarFlyer;
	LinearLayout llBarReference;
	public File pdfFile = null;
    UserSettingsDAO userSettingsDAO = new UserSettingsDAO();
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_dash_board);
        UserSettingsDAO usersettings = new UserSettingsDAO();
       
        //OfferDetailsActivity.logHeap("Dashboard Activity - onCreate method starting ");
        /*int rotation = getWindowManager().getDefaultDisplay().getRotation();
        switch (rotation) {
            case Surface.ROTATION_0:
            case Surface.ROTATION_180:
                setContentView(R.layout.activity_dash_board);
                break;
            case Surface.ROTATION_270:
            case Surface.ROTATION_90:
                setContentView(R.layout.activity_dash_board_l);
                break;
        }*/
        userToken=getIntent().getStringExtra("usertoken");
        userName=getIntent().getStringExtra("userName");
        salt=getIntent().getStringExtra("salt");
        loginUserId=getIntent().getLongExtra("loginUserId", -1);
        long timeout = (usersettings.getUserSettingsByUserId(loginUserId, this)).getSetTimeOut();
        getApp().start(timeout);
       String AlarmTask=getIntent().getStringExtra("AlarmTask");
        
        //final ListView lv1 = (ListView) findViewById(R.id.offersList);
        String webServiceUrl = getResources().getString(R.string.webservice_url);
        currentActivity = this;
        context = this;
        //new OffersService().execute(webServiceUrl + "/user/Offers");
        
         //userSetting = userSettingsDAO.getUserSettingsByUserId(loginUserId, currentActivity);

    	List<UserLibrary> userLibraryFlyer = new UserLibraryDAO().getAllUserLibraryList(currentActivity, loginUserId, "Flyer");
    	List<UserLibrary> UserLibraryReference = new UserLibraryDAO().getAllUserLibraryList(currentActivity, loginUserId, "Reference");
    	
    	txtLibraryCount =(TextView) findViewById(R.id.txtLibraryCount);
    	
    	llBarFlyer = (LinearLayout) findViewById(R.id.BarFlyer);
    	llBarReference = (LinearLayout) findViewById(R.id.BarReference);
    	
    	if(userLibraryFlyer.size() > 0) {
    		buildUserLibrarySection(userLibraryFlyer, llBarFlyer);
    	}
    	
    	if(UserLibraryReference.size() > 0) {
    		buildUserLibrarySection(UserLibraryReference, llBarReference);
    	}
    	
        //new StatementService().execute(webServiceUrl + "/user/Statements");
    	

        catalogsflyerPlusBtn = (Button) findViewById(R.id.catalogsflyerPlusBtn);
        catalogsflyerPlusBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				ExpandandCollapse("catalog");
			}
		});
        
        referencePlusBtn = (Button) findViewById(R.id.referencePlusBtn);
        referencePlusBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				ExpandandCollapse("reference");
			}
		});
        //OfferDetailsActivity.logHeap("Dashboard Activity - onCreate method ending ");
        
        if(AlarmTask != null)
        {
        	BackgroundTask task = new BackgroundTask();
			task.execute("");
        }	
        
        
    }
    
	public void setUnreadLibraryCount(boolean isLibraryChecked) {
		int readLibcount = 0;
		readLibcount = new UserLibraryDAO().getUnReadLibraryCount(currentActivity, loginUserId);
        if(readLibcount > 0 && isLibraryChecked){
        	txtLibraryCount.setVisibility(View.VISIBLE);
        	txtLibraryCount.setText(readLibcount+"");
        } else {
        	txtLibraryCount.setVisibility(View.INVISIBLE);
        	txtLibraryCount.setText(readLibcount+"");
        }
	}
    
   public void buildUserLibrarySection(List<UserLibrary> userLibraryList, LinearLayout llUserLibraryCover) {
		
        final LayoutInflater  inflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        
        for (int i=0; i < userLibraryList.size(); i++){
        	UserLibrary userLibrary = userLibraryList.get(i);
        	
        	LinearLayout lv= (LinearLayout) inflater.inflate(R.layout.user_library_list_view, null);
        	ImageView imageView=(ImageView) lv.findViewById(R.id.userlibraryCoverPageImage);
        	imageView.setId(Integer.parseInt(String.valueOf(userLibrary.getLibraryId())));
        	imageView.setTag(userLibrary.getUseUrl());
        	if(userLibrary.getUseUrl()) {
        		imageView.setContentDescription(userLibrary.getUrl());
        	} else {
        		imageView.setContentDescription(userLibrary.getFile());
        	}
        	imageView.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {

                	Boolean useUrl = Boolean.parseBoolean(v.getTag().toString());
                	String pdfURL = v.getContentDescription().toString();
                	
                	if(useUrl) {
                		Intent libraryActivityIntent = new Intent(v.getContext(), ShowLibraryPDFActivity.class);
                        libraryActivityIntent.putExtra("usertoken", userToken);
                        libraryActivityIntent.putExtra("userName",userName);
                        libraryActivityIntent.putExtra("salt",salt);
                        libraryActivityIntent.putExtra("loginUserId",loginUserId);
                        libraryActivityIntent.putExtra("fromScreen", "dashboardActivity");
                        libraryActivityIntent.putExtra("pdfName", pdfURL);
                        startActivityForResult(libraryActivityIntent, 0);
                	} else {
	                	
						String extStorageDirectory = Environment.getExternalStorageDirectory().toString();
				
						File folder = new File(extStorageDirectory, "Document");
						if (!folder.exists()) {
							folder.mkdir();
						}
				
	    				String displayPdfPathfileName = pdfURL.substring(pdfURL.lastIndexOf("/") + 1);
						File pdfFile = new File(folder.getAbsolutePath(), displayPdfPathfileName);
				
						if (pdfFile.exists()) {
							openMuPDF(pdfFile.getAbsoluteFile().toString());
						} else {
							DownloadTaskPDf taskPdf = new DownloadTaskPDf(DashBoardActivity.this, pdfFile.getAbsolutePath());
							taskPdf.execute(pdfURL);
						}
                	}
                	
                	UserLibraryDAO userLibraryDAO = new UserLibraryDAO();
	               	long libraryId = v.getId();
	               	try {
	        			ConnectivityManager cm = (ConnectivityManager) currentActivity.getSystemService(Activity.CONNECTIVITY_SERVICE);
	        			if(cm != null && cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected()){
	        				UserLibraryService.readUnreadLibrary(currentActivity, loginUserId, libraryId, true);
	        				userLibraryDAO.updateRead(libraryId, 1, currentActivity , 0);
	        			} else {
	        				userLibraryDAO.updateRead(libraryId, 1, currentActivity , 1);
	        			}
	        		} catch (Exception e) {
	        			// TODO Auto-generated catch block
	        			e.printStackTrace();
	        		}
	               	
                }
            });
        	
        	String imageURL = userLibrary.getCover();
        	if(imageURL != null) {
            	String displayImagePathSmall = imageURL;
				String displayImagePathSmallfileName = displayImagePathSmall.substring(displayImagePathSmall.lastIndexOf("/") + 1);
				if(displayImagePathSmallfileName != null && displayImagePathSmallfileName != "") {
	            	File imgFile = new File(currentActivity.getBaseContext().getFilesDir(), displayImagePathSmallfileName);
		            if(!imgFile.exists()){
		            	new DownloadImages(DashBoardActivity.this).execute(imageURL, imageView);
		            } else {
		            	imageView.setImageURI(Uri.fromFile(imgFile));
		            }
				}
            }    
        	
        	llUserLibraryCover.addView(lv);
        }
	}


	public class BackgroundTask extends AsyncTask<String, Integer, Void> {
//		private ProgressDialog mProgressDialog;
		int progress;

		public BackgroundTask() {
//			mProgressDialog = new ProgressDialog(context);
//			mProgressDialog.setMax(100);
//			mProgressDialog.setProgress(0);
		}

		@Override
		protected void onPreExecute() {
//			mProgressDialog = ProgressDialog.show(context, "", "Please Wait...", true, false);
			super.onPreExecute();
		}

		@Override
		protected void onProgressUpdate(Integer... values) {
			setProgress(values[0]);
		}

		@Override
		protected Void doInBackground(String... params) {
			DateFormat df = new SimpleDateFormat("h:mm a");
	        String date = df.format(Calendar.getInstance().getTime());
	        alarm = new AlarmService();
	        
	        prefsServiceAlarm = context.getSharedPreferences("ServiceAlarm", 0);
	        prefsServiceAlarmEditor = prefsServiceAlarm.edit();
	        prefsServiceAlarmEditor.putString("userToken", userToken);
	        prefsServiceAlarmEditor.putString("userName", userName);
	        prefsServiceAlarmEditor.putString("salt", salt);
	        prefsServiceAlarmEditor.putLong("loginUserId", loginUserId);
	        prefsServiceAlarmEditor.putBoolean("starting", true);
	        prefsServiceAlarmEditor.commit();

	        alarm.SetAlarm(context, date, "5", userToken, userName, salt, loginUserId);
			while (progress < 100) {
				progress++;
				publishProgress(progress);
				SystemClock.sleep(100);
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
//			mProgressDialog.dismiss();
			super.onPostExecute(result);
		}
	}


    private void ExpandandCollapse(String toggleBtnText) {
    	HorizontalScrollView hrScViewFlyer = (HorizontalScrollView) findViewById(R.id.horizontalScrollViewFlyer);
    	HorizontalScrollView hrScViewReference = (HorizontalScrollView) findViewById(R.id.horizontalScrollViewReference);
    	
    	Button catalogsflyerBtn = (Button) findViewById(R.id.catalogsflyerBtn);
    	Button referenceBtn = (Button) findViewById(R.id.referenceBtn);
    	
		try{
			if(toggleBtnText.equalsIgnoreCase("catalog")) {
				if(hrScViewFlyer.getVisibility() == View.VISIBLE) {
					hrScViewFlyer.setVisibility(View.GONE);
					catalogsflyerPlusBtn.setText("+");
					catalogsflyerPlusBtn.setBackgroundColor(getResources().getColor(R.color.dashboardRefCOlor));
					catalogsflyerBtn.setBackgroundColor(getResources().getColor(R.color.dashboardRefCOlor));
				} else {
					hrScViewFlyer.setVisibility(View.VISIBLE);
					catalogsflyerPlusBtn.setText("—");
					catalogsflyerPlusBtn.setBackgroundColor(getResources().getColor(R.color.selectedCatalog));
					catalogsflyerBtn.setBackgroundColor(getResources().getColor(R.color.selectedCatalog));
				}
			} else {
				if(hrScViewReference.getVisibility() == View.VISIBLE) {
					hrScViewReference.setVisibility(View.GONE);
					referencePlusBtn.setText("+");
					referencePlusBtn.setBackgroundColor(getResources().getColor(R.color.dashboardRefCOlor));
					referenceBtn.setBackgroundColor(getResources().getColor(R.color.dashboardRefCOlor));
				} else {
					hrScViewReference.setVisibility(View.VISIBLE);
					referencePlusBtn.setText("—");
					referencePlusBtn.setBackgroundColor(getResources().getColor(R.color.selectedCatalog));
					referenceBtn.setBackgroundColor(getResources().getColor(R.color.selectedCatalog));
				}
			}
			
		}catch(Exception e) {
			e.printStackTrace();
		}
	}

    
    public void showOfferActivity(View view){
        Intent offersListActivity = new Intent(view.getContext(), OfferListActivity.class);
        offersListActivity.putExtra("usertoken", userToken);
        offersListActivity.putExtra("userName", userName);
        offersListActivity.putExtra("salt", salt);
        offersListActivity.putExtra("loginUserId", loginUserId);
        startActivityForResult(offersListActivity, 0);
    }
    
    public void showAssistantsActivity(View view){
        Intent assistantsActivity = new Intent(view.getContext(), AssistantsActivity.class);
        assistantsActivity.putExtra("usertoken", userToken);
        assistantsActivity.putExtra("userName", userName);
        assistantsActivity.putExtra("salt", salt);
        assistantsActivity.putExtra("loginUserId", loginUserId);
        startActivityForResult(assistantsActivity, 0);
    }
    
    
    public void showLibraryActivity(View view){
        Intent libraryActivity = new Intent(view.getContext(), LibraryActivity.class);
        libraryActivity.putExtra("usertoken", userToken);
        libraryActivity.putExtra("userName", userName);
        libraryActivity.putExtra("salt", salt);
        libraryActivity.putExtra("loginUserId", loginUserId);
        libraryActivity.putExtra("fromScreen", "home");
        startActivityForResult(libraryActivity, 0);
    }
    
    
    public void showSettingsActivity(View view){
        Intent settingsActivity = new Intent(view.getContext(), SettingsActivity.class);
        settingsActivity.putExtra("usertoken", userToken);
        settingsActivity.putExtra("userName", userName);
        settingsActivity.putExtra("salt", salt);
        settingsActivity.putExtra("loginUserId", loginUserId);
        startActivityForResult(settingsActivity, 0);
    }

    public void setListAdapter(List<Offer> offersList, boolean isOfferChecked) {
        final ListView lv1 = (ListView) findViewById(R.id.offersList);
    	txtOffersCount=(TextView) findViewById(R.id.txtOfferCount);
        int count=0;
        for(Offer offer:offersList){
            if(!offer.isRead()){
                count++;
            }
        }
        if(count > 0 && isOfferChecked){
            txtOffersCount.setVisibility(View.VISIBLE);
            txtOffersCount.setText(count+"");
        }else{
            txtOffersCount.setVisibility(View.INVISIBLE);
            txtOffersCount.setText(count+"");
        }

        //DatabaseHandler databaseHandler = new DatabaseHandler(this);
        DatabaseHandler databaseHandler = DatabaseHandler.getDatabaseHandlerInstance(this);
        SQLiteDatabase db = databaseHandler.getReadableDatabase();
        
        String selectQuery = "SELECT distinct name  FROM offersservice";
 
        Cursor cursor = db.rawQuery(selectQuery, null);
        offersNamesList = new String[cursor.getCount()];
        int i = 0;
        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
           /* for(int i = 0; i < cursor.getCount(); i++) {
            	offersNamesList[i] = cursor.getString(0) + "|,|" + cursor.getString(1);
            	cursor.moveToNext();
            } */
        	
        	do {
        		offersNamesList[i] = cursor.getString(0);
        		i++;
        	} while (cursor.moveToNext());
        }
        cursor.close();
        
        lv1.setAdapter(new ItemListBaseAdapter(this, offersList));
        lv1.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> a, View v, int position, long id) {
                Object o = lv1.getItemAtPosition(position);
                Offer offer = (Offer) o;
                //    Toast.makeText(DashBoardActivity.this, "You have chosen : " + " " + offer.getName() + " : " + offer.getDescription(), Toast.LENGTH_LONG).show();
                OfferDAO offerDAO=new OfferDAO();
                offerDAO.updateRead(offer.getId(),1, currentActivity, 0);
                Intent offerDetails = new Intent(DashBoardActivity.this, OfferDetailsActivity.class);
                offerDetails.putExtra("usertoken", userToken);
                offerDetails.putExtra("userName",userName);
                offerDetails.putExtra("salt",salt);
                offerDetails.putExtra("offer_id", offer.getId()+"");
                offerDetails.putExtra("loginUserId", loginUserId);
                offerDetails.putExtra("offer_description", offer.getName());
                offerDetails.putExtra("offers_names_list", offersNamesList);
                offerDetails.putExtra("fromScreen", "home");
                offerDetails.putExtra("clickposition", position);
                startActivityForResult(offerDetails, 0);
            }
        });
    }

    public void showStatments(View view) {
        Intent accountStatement = new Intent(view.getContext(), AccountStatements.class);
        accountStatement.putExtra("usertoken", userToken);
        accountStatement.putExtra("userName",userName);
        accountStatement.putExtra("salt",salt);
        accountStatement.putExtra("loginUserId",loginUserId);
        accountStatement.putExtra("fromScreen", "home");
        startActivityForResult(accountStatement, 0);
    }

    public void showBills(View view) {
        Intent billStatement = new Intent(view.getContext(), BillsStatement.class);
        billStatement.putExtra("usertoken", userToken);
        billStatement.putExtra("userName",userName);
        billStatement.putExtra("salt",salt);
        billStatement.putExtra("loginUserId",loginUserId);
        billStatement.putExtra("fromScreen", "home");
        startActivityForResult(billStatement, 0);
    }
    
    
    public void showMyPlan(View view) {
        Intent myPlanActivity = new Intent(view.getContext(), MyPlanActivity.class);
        myPlanActivity.putExtra("usertoken", userToken);
        myPlanActivity.putExtra("userName",userName);
        myPlanActivity.putExtra("salt",salt);
        myPlanActivity.putExtra("loginUserId",loginUserId);
        myPlanActivity.putExtra("fromScreen","home");
        myPlanActivity.putExtra("header","MY PLAN");
        String URL =" http://209.11.252.135/UP2MEPlugIns/MyPlan?userToken=" + userToken	+ "&userName=" + userName + "&salt=" + salt;
        myPlanActivity.putExtra("URL",URL);
        startActivityForResult(myPlanActivity, 0);
    }
    
    public void showMyCreditCard(View view) {
        Intent myPlanActivity = new Intent(view.getContext(), MyPlanActivity.class);
        myPlanActivity.putExtra("usertoken", userToken);
        myPlanActivity.putExtra("userName",userName);
        myPlanActivity.putExtra("salt",salt);
        myPlanActivity.putExtra("loginUserId",loginUserId);
        myPlanActivity.putExtra("fromScreen","home");
        myPlanActivity.putExtra("header","CREDIT CARD");
        String URL ="http://209.11.252.135/UP2MEPlugIns/CCAssistant?userToken=" + userToken	+ "&userName=" + userName + "&salt=" + salt;
        myPlanActivity.putExtra("URL",URL);
        startActivityForResult(myPlanActivity, 0);
    }

    public void showCashFlow(View view) {
        Intent cashFlowActivity = new Intent(view.getContext(), CashFlowActivity.class);
        cashFlowActivity.putExtra("usertoken", userToken);
        cashFlowActivity.putExtra("userName",userName);
        cashFlowActivity.putExtra("salt",salt);
        cashFlowActivity.putExtra("loginUserId",loginUserId);
        cashFlowActivity.putExtra("fromScreen","home");
        startActivityForResult(cashFlowActivity, 0);
    }

    public void showMailActivity(View view) {
        Intent mailActivity = new Intent(view.getContext(), MailActivity.class);
        mailActivity.putExtra("usertoken", userToken);
        mailActivity.putExtra("userName",userName);
        mailActivity.putExtra("salt",salt);
        mailActivity.putExtra("loginUserId",loginUserId);
        startActivityForResult(mailActivity, 0);
    }

    public void showUnpaid(View view) {
        Intent unpaidActivity = new Intent(view.getContext(), UnpaidActivity.class);
        unpaidActivity.putExtra("usertoken", userToken);
        unpaidActivity.putExtra("userName",userName);
        unpaidActivity.putExtra("salt",salt);
        unpaidActivity.putExtra("loginUserId",loginUserId);
        unpaidActivity.putExtra("fromScreen","home");
        startActivityForResult(unpaidActivity, 0);
    }
    

    public void displayStatment(List<Statement> statementsList) {
       
        LinearLayout lh1 = (LinearLayout) findViewById(R.id.topStatementBar);
        lh1.removeAllViews();
        ImageView imageViewStart=new ImageView(this);
        imageViewStart.setAdjustViewBounds(true);
        imageViewStart.setImageResource(R.drawable.ui_bg_glass_100_f6f6f6_1_400);
        lh1.addView(imageViewStart);
        allStatementsList = new String[statementsList.size()];
        final LayoutInflater  inflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        for (int i=0;i<statementsList.size();i++){
            //LinearLayout.LayoutParams paramsExample = new LinearLayout.LayoutParams(lay);
            Statement statement= statementsList.get(i);
            LinearLayout lv= (LinearLayout) inflater.inflate(R.layout.statements_list_view, null);
            ImageView imageView=(ImageView) lv.findViewById(R.id.statementThumbNails);
            imageView.setId(Integer.parseInt(String.valueOf(statement.getId())));
            allStatementsList[i] = String.valueOf(statement.getId());
            //imageView.setAdjustViewBounds(true);
            //imageView.setX(0);
            //imageView.setY(0);
            //imageView.setMaxWidth(97);
            //imageView.setMaxHeight(82);
            imageView.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    v.getId();
                    Log.v("ImageButton click id", v.getId()+"");

                    Intent billDetailsActivity = new Intent(v.getContext(), BillDetailsActivity.class);
                    billDetailsActivity.putExtra("document_id",(long) v.getId());
                    billDetailsActivity.putExtra("usertoken", userToken);
                    billDetailsActivity.putExtra("userName",userName);
                    billDetailsActivity.putExtra("salt",salt);
                    billDetailsActivity.putExtra("loginUserId",loginUserId);
                    billDetailsActivity.putExtra("fromScreen","home");
                    billDetailsActivity.putExtra("all_statements_list", allStatementsList);
                    startActivityForResult(billDetailsActivity, 0);
                }
            });
            String imageURL = statement.getDisplayImagePathSmall().getPath();
            if(imageURL!=null && imageURL.length()>0){
                File imgFile = new File(imageURL);
                if(imgFile.exists()){
                    //Log.w("ItemListBaseAdapter", "setting image :"+imgFile.getAbsolutePath());
                    Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                    imageView.setImageBitmap(myBitmap);
                }
            }
            //lv.addView(imageView);
           // new ImageViewDownloader(imageView).execute(statement.getImageURL());
            TextView tv=(TextView)lv.findViewById(R.id.statementType);
            if (statement.isBill()){
            	tv.setText("Bill");
            } else {
            	tv.setText("Statement");
            }
           // tv.setTextColor(0xFFFFFFFF);
          //  tv.setEllipsize(TextUtils.TruncateAt.END);
           // tv.setMaxLines(1);
           // tv.setMaxWidth(82);
        //    tv.setTextSize(13.0f);
           // tv.setGravity(Gravity.CENTER_HORIZONTAL);
           // lv.addView(tv);
            lh1.addView(lv);
            ImageView imageViewLn=new ImageView(this);
            imageViewLn.setImageResource(R.drawable.ui_bg_glass_100_f6f6f6_1_400);
            lh1.addView(imageViewLn);
           

        }

       

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.dash_board, menu);
        return true;
    }

    @Override
    public void onBackPressed() {
        //this.finish();

     	Intent myIntent = new Intent(DashBoardActivity.this, LoginActivity.class);

        myIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(myIntent);
        finish();
        

        
        return;
    }
    
    
    public void showLibraryPDFActivity(View view) {
        /*int imageViewId = view.getId();
        String pdfName = "";
        
        if(imageViewId == R.id.universalflyer03_13cover){
        	pdfName = "bestyoyflyer08_13";
        	openMuPDF(pdfName);
        }else if(imageViewId == R.id.homedepotflyercover){
        	pdfName = "homedepotflyerlo";
        	openMuPDF(pdfName);
        }if(imageViewId == R.id.homedepotspringstylecover ){
        	pdfName = "homedepotspringstylelo";
        	openMuPDF(pdfName);
        }if(imageViewId == R.id.targetflyercover){
        	pdfName = "targetadlo";
        	openMuPDF(pdfName);
        }if(imageViewId == R.id.uh_provider_directorytr_cover){
        	pdfName = "uh_provider_directorytr_cover";
          
        	Intent libraryActivityIntent = new Intent(view.getContext(), ShowLibraryPDFActivity.class);
           libraryActivityIntent.putExtra("usertoken", userToken);
           libraryActivityIntent.putExtra("userName",userName);
           libraryActivityIntent.putExtra("salt",salt);
           libraryActivityIntent.putExtra("loginUserId",loginUserId);
           libraryActivityIntent.putExtra("fromScreen", "dashboardActivity");
           libraryActivityIntent.putExtra("pdfName", pdfName);
           startActivityForResult(libraryActivityIntent, 0);
        }*/
    }    
    
	private void openMuPDF(String pdfName) {
		/*InputStream inputStream = null;
        AssetManager am = getAssets();
        try {
			inputStream = am.open("pdfs/"+pdfName+".pdf");
		} catch (IOException e) {
			e.printStackTrace();
		}
        File pdfFile = createFileFromInputStream(inputStream, getBaseContext().getFilesDir()+"/"+ pdfName+".pdf");
        
        Uri uri = Uri.parse(pdfFile.getAbsolutePath());
        Intent intent = new Intent(context, MuPDFActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        intent.setAction(Intent.ACTION_VIEW);
        intent.setData(uri);
        context.startActivity(intent);*/
		
		 Uri uri = Uri.parse(pdfName);
		 Intent intent = new Intent(context, MuPDFActivity.class);
	     intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
         intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
	     intent.setAction(Intent.ACTION_VIEW);
	     intent.setData(uri);
	     context.startActivity(intent);
	}


	private File createFileFromInputStream(InputStream inputStream, String outputFilePath) {

		   try{
		      File f = new File(outputFilePath);
		      OutputStream outputStream = new FileOutputStream(f);
		      byte buffer[] = new byte[1024];
		      int length = 0;

		      while((length=inputStream.read(buffer)) > 0) {
		        outputStream.write(buffer,0,length);
		      }

		      outputStream.flush();
		      outputStream.close();
		      inputStream.close();

		      return f;
		   }catch (IOException e) {
		         //Logging exception
			   e.printStackTrace();
		   }

		return null;
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
	    
	 
	    private class DownloadTaskPDf extends AsyncTask<String, Integer, String> {

			private Context context;
			String file;
			ProgressDialog dialog;
			int n = 0;

			public DownloadTaskPDf(Context context, String file) {
				this.context = context;
				this.file = file;
				dialog = new ProgressDialog(context);
				dialog.setMessage("Loading.. " + n + "%");
			}

			@Override
			protected void onPreExecute() {
				super.onPreExecute();
				dialog.show();
			}

			@Override
			protected void onProgressUpdate(Integer... values) {
				super.onProgressUpdate(values);
				n = values[0];
				dialog.setMessage("Loading.. " + n + "%");
			}

			@Override
			protected String doInBackground(String... sUrl) {
				try {
					InputStream input = null;
					OutputStream output = null;
					HttpURLConnection connection = null;
					try {
						URL url = new URL(sUrl[0].replace(" ", "%20").replace("_", "%5F"));
						connection = (HttpURLConnection) url.openConnection();
						connection.connect();

						// expect HTTP 200 OK, so we don't mistakenly save error
						// report
						// instead of the file
						if (connection.getResponseCode() != HttpURLConnection.HTTP_OK)
							return "Server returned HTTP " + connection.getResponseCode() + " " + connection.getResponseMessage();

						// this will be useful to display download percentage
						// might be -1: server did not report the length
						int fileLength = connection.getContentLength();

						// download the file
						input = connection.getInputStream();
						output = new FileOutputStream(file);

						byte data[] = new byte[4096];
						long total = 0;
						int count;
						while ((count = input.read(data)) != -1) {
							// allow canceling with back button
							if (isCancelled())
								return null;
							total += count;
							// publishing the progress....
							if (fileLength > 0) // only if total length is known
								publishProgress((int) (total * 100 / fileLength));
							output.write(data, 0, count);
						}
					} catch (Exception e) {
						return e.toString();
					} finally {
						try {
							if (output != null)
								output.close();
							if (input != null)
								input.close();
						} catch (IOException ignored) {
						}

						if (connection != null)
							connection.disconnect();
					}
				} finally {
					// wl.release();
				}
				return null;
			}

			@Override
			protected void onPostExecute(String result) {
				dialog.dismiss();
				showStatmentPdfFile(file);
			}
		}
	    
	    public void showStatmentPdfFile(String file) {
//			pdf.setVisibility(View.VISIBLE);
			// logHeap("Bill Details Activity - DownloadTask inner class - showFile method starting ");
			pdfFile = new File(file);
			// file=Environment.getExternalStorageDirectory().toString()+"/"+"downloadedfile.pdf";
			try {
				if(pdfFile.exists()) {
//					pdf.fromFile(pdfFile).defaultPage(1).showMinimap(false).enableSwipe(true).load();
					openMuPDF(pdfFile.getAbsolutePath().toString());
				}
			} catch (Exception ex) {
				TestFlight.log("There might be a problem with downloading pdf file." + ex.getMessage());
				File pdfFileToDelete = new File(file);
				if (pdfFileToDelete.exists()) {
					pdfFileToDelete.delete();
				}
			}
			// logHeap("Bill Details Activity - DownloadTask inner class - showFile method ending ");
		}
	    
	    @Override
	    protected void onResume() {
	    	// TODO Auto-generated method stub
	    	super.onResume();
	    	
	    	userSetting = userSettingsDAO.getUserSettingsByUserId(loginUserId, currentActivity);
	    	List<Statement> statementsListFromDB = new StatementDAO().getAllStatements(currentActivity,loginUserId);
	    	
	    	displayStatment(statementsListFromDB);	    	
	    	
	    	setUnreadLibraryCount(userSetting.isShowBadgesLibrary());
	    	
	    	List<Statement> statementsList = new StatementDAO().getAllStatementsForCount(currentActivity,loginUserId);
	    	
	    	  int unRead=0;
	          int bill=0;
	          int unPaidBills=0;
	    	 for (int i=0;i<statementsList.size();i++){
	             //LinearLayout.LayoutParams paramsExample = new LinearLayout.LayoutParams(lay);
	             Statement statement= statementsList.get(i);
	             if (statement.isBill() && !statement.isRead()){
	                 bill++;
	             }
	             if (!statement.isPaid() && statement.isBill()){
	                 unPaidBills++;
	             }

	             if(!statement.isBill() && !statement.isRead()){
	                 unRead++;
	             }
	    	 }
	         if(unPaidBills>0 && userSetting != null && userSetting.isShowBadgesBills()){
	             billtext=(TextView)findViewById(R.id.bills_text);
	             billtext.setVisibility(View.VISIBLE);
	             billtext.setText(unPaidBills+"");
	         }else{
	             billtext=(TextView)findViewById(R.id.bills_text);
	             billtext.setVisibility(View.INVISIBLE);
	             billtext.setText(unPaidBills+"");
	         }
	         /*
	         if(unRead>0 && userSetting != null && userSetting.isShowBadgesStatements()){
	             unReadtext=(TextView)findViewById(R.id.statements_text);
	             unReadtext.setVisibility(View.VISIBLE);
	             unReadtext.setText(unRead+"");
	         }else{
	             unReadtext=(TextView)findViewById(R.id.statements_text);
	             unReadtext.setVisibility(View.INVISIBLE);
	             unReadtext.setText(unRead+"");
	         }*/
	         if (bill>0 || unRead>0){
	           TextView mailCount=(TextView)findViewById(R.id.mailCount);
	           mailCount.setVisibility(View.VISIBLE);
	           mailCount.setText(bill+unRead+"");
	         }else{
	           TextView mailCount=(TextView)findViewById(R.id.mailCount);
	           mailCount.setVisibility(View.INVISIBLE);
	           mailCount.setText(bill+unRead+"");
	         }
	         
	         List<Offer> offersListFromDB = new OfferDAO().getAllOffers(currentActivity,loginUserId," order by expiresOnDisplayDate asc");
	         if(userSetting != null ){
	         	setListAdapter(offersListFromDB, userSetting.isShowBadgesOffers());
	         }

	    }
}

