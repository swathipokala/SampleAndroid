package com.rrdonnelly.up2me;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import org.apache.http.client.ClientProtocolException;
import org.json.JSONException;

import android.accounts.AccountManager;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnKeyListener;
import android.content.Intent;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.media.ThumbnailUtils;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Debug;
import android.os.Environment;
import android.os.Handler;
import android.os.StrictMode;
import android.provider.CalendarContract.Events;
import android.provider.CalendarContract.Reminders;
import android.provider.MediaStore;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.PopupMenu;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;
import android.widget.VideoView;

import com.artifex.mupdfdemo.MuPDFActivity;
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
import com.rrdonnelly.up2me.dao.DatabaseHandler;
import com.rrdonnelly.up2me.dao.DocumentProvidersDAO;
import com.rrdonnelly.up2me.dao.StatementDAO;
import com.rrdonnelly.up2me.dao.Tag;
import com.rrdonnelly.up2me.dao.TagDAO;
import com.rrdonnelly.up2me.json.DocumentProviders;
import com.rrdonnelly.up2me.services.StatementsService;
import com.rrdonnelly.up2me.services.TagService;
import com.rrdonnelly.up2me.util.AsyncImageDownloader;
import com.rrdonnelly.up2me.util.ControlApplication;
import com.rrdonnelly.up2me.util.DateFormatUtil;
import com.rrdonnelly.up2me.util.ImageUtil;
import com.rrdonnelly.up2me.util.NumberFormatUtil;
import com.rrdonnelly.up2me.util.tags.AsyncTagSyncToBackend;
import com.rrdonnelly.up2me.valueobjects.SavedState;
import com.rrdonnelly.up2me.widget.menu.QuickAction;
import com.rrdonnelly.up2me.widget.menu.StatementActionItem;
import com.rrdonnelly.up2me.widget.menu.StatementQuickAction;
import com.testflightapp.lib.TestFlight;


public class BillDetailsActivity extends Activity{

	StatementQuickAction statementQuickAction;
//	PDFView pdf;
	private int pageNumber;
	private Object pdfName;

	private String userName = null;
	private String userToken = null;
	private String salt = null;
	private long loginUserId = 0l;
	private String fromScreen = null;
	private String parentScreen = null;
	public File pdfFile = null;
	public ImageButton actionButton = null;

	public String userID = null;
	long currentDocumentID;
	long nextDocumentId;
	long previousDocumentId;

	Activity currentActivity;

	private String[] allStatmentsList = null;

	VideoView mVideoView;
	ImageView imgVideoThumbNail;
	ToggleButton unpaidCheckBox;
	boolean isPaid = false;
	boolean isPaidCheck = false;
	boolean isBill;
	String stmtText;
	private DbxAccountManager mDbxAcctMgr;
	private static final int REQUEST_LINK_TO_DBX = 0;
	private static final int ID_UP     = 1;
	private static final int ID_DOWN   = 2;
	private static final int ID_SEARCH = 3;
	private static final int ID_INFO   = 4;
	private static final int ID_ERASE  = 5;	
	private static final int ID_OK     = 6;
	private static final String TAG_DEFAULT_COLOR = "#CACBDA";

	static Context context;
    //String currentOfferId = "0";
	TextView textViewBillLable, textViewWithDate;
	String statmentName = null;
	String dueDate = null;
    ToggleButton checkIsFavorite;
	DocumentProvidersDAO documentProvidersDAO = new DocumentProvidersDAO();
	int providerId = 0;
	boolean isFavourite = false;
	String coverPagePdffileName;
	String documentCoverPath;
	
	String currentPdfFileName;
	String currentPdfDownloadURL;
	
	TextView stmtText1Right;
	/**
	 * Google Drive
	 */
	  static final int REQUEST_ACCOUNT_PICKER = 1;
	  static final int REQUEST_AUTHORIZATION = 2;
	  static final int CAPTURE_IMAGE = 3;

	  private static Uri fileUri;
	  private static Drive service;
	  private GoogleAccountCredential credential;
	  
	  ProgressDialog progressBar;
	  
	  ImageView imgPdfCoverPage;
	  Button taptoView;
	  TextView statementdeleteInformation;
	  Button revertBtnToDownloadpdf;
	  String strGoogleDrive = "";
	  
	  MediaController mcon;
	  boolean isVideoPlayed = false;
	  
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_bill_details);
		context = this;

		// logHeap("Bill Details Activity - OnCreate method starting ");
		currentActivity = this;
		userToken = getIntent().getStringExtra("usertoken");
		userName = getIntent().getStringExtra("userName");
		salt = getIntent().getStringExtra("salt");
		loginUserId = getIntent().getLongExtra("loginUserId", -1);
		fromScreen = getIntent().getStringExtra("fromScreen");
		parentScreen = getIntent().getStringExtra("parentScreen");
		currentDocumentID = getIntent().getLongExtra("document_id", 0);
		allStatmentsList = getIntent().getStringArrayExtra("all_statements_list");

		imgPdfCoverPage = (ImageView) findViewById(R.id.pdfPreviewImage);
		taptoView = (Button) findViewById(R.id.taptoViewBtn);
		statementdeleteInformation = (TextView) findViewById(R.id.statementdeleteInformation);
		revertBtnToDownloadpdf = (Button) findViewById(R.id.revertBtnToDownloadpdf);
		
		SavedState data=(SavedState)getLastNonConfigurationInstance();
        if (data != null){
        	currentDocumentID = data.getSelectedID();
        }
		// Setting home and back buttons
		buildBackorHome(fromScreen);

		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
		StrictMode.setThreadPolicy(policy);

		buildBillDetailsPage(currentDocumentID);

		// logHeap("Bill Details Activity - OnCreate method ending ");
		displayMenu(String.valueOf(currentDocumentID), loginUserId);
        
        checkIsFavorite.setOnClickListener(new ImageView.OnClickListener() {
            public void onClick(View v) {
            	Boolean isChecked=  ((ToggleButton) v).isChecked();
				if(isChecked) {
					v.setBackgroundResource(R.drawable.pageoffer_subnav_icon2_yellowstar);
					try {
            			ConnectivityManager cm = (ConnectivityManager) currentActivity.getSystemService(Activity.CONNECTIVITY_SERVICE);
            			if(cm != null && cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected()){
            				StatementsService.updateFavourite(currentActivity, loginUserId, providerId, true);
            				saveFavourite(true,false);
            			} else{
            				saveFavourite(true,true);
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
            				StatementsService.updateFavourite(currentActivity, loginUserId, providerId, false);
            				saveFavourite(false,false);
            			} else{
            				saveFavourite(false,true);
            			}
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		});
	}
	
	private void buildBillDetailsPage(Long currentDocID) {
		currentDocumentID = currentDocID;
		
		ImageButton imageButtonPayBy=(ImageButton)(findViewById(R.id.payBy));
		
		
        displayMenu(String.valueOf(currentDocumentID), loginUserId);
        if(statementQuickAction!=null){
        	statementQuickAction.setCurrentDocumentId(currentDocID);	
        }
        
        StatementDAO statementDAO = new StatementDAO();
        int tagId = 0;
        tagId = statementDAO.getTagIdFromStatement(String.valueOf(currentDocumentID), currentActivity, loginUserId);
        
        ImageView tagButton1 = (ImageView) (findViewById(R.id.pageoffer_subnav_icon1)); 
        TagDAO tagDAO = new TagDAO();
        String tagColorCode = tagDAO.getColorCode(tagId, currentActivity, loginUserId);
        tagButton1.setBackgroundColor(Color.parseColor(tagColorCode));		
		
		DatabaseHandler databaseHandler = DatabaseHandler.getDatabaseHandlerInstance(currentActivity);

		SQLiteDatabase db = databaseHandler.getReadableDatabase();
		String query = "SELECT id,providerName,displayImagePathSmall,read,paid,bill,periodEndDate,"
				+ "(substr(statementDisplayDate , 1, 2)  || '/' ||    substr(statementDisplayDate , 4, 2) || '/' || substr(statementDisplayDate , 7, 4) ) as statementDate,   balance, minAmountDue, dueDate, " + "messageType, message, tagId, documentCoverPath, isPDFDeleted,showInCashFlow,showInStatements,showInBill,showInUnpaid, documentPath FROM statements where "
				+ "id = " + currentDocumentID + " and userId = " + loginUserId + " and bill = 1 group by displayImagePathBigAltText " 
				+ " union" 
				+ " SELECT id,providerName,displayImagePathSmall,read,paid,bill,periodEndDate,"
				+ " (substr(statementDisplayDate , 1, 2)  || '/' ||    substr(statementDisplayDate , 4, 2) || '/' || substr(statementDisplayDate , 7, 4) ) as statementDate,  balance, minAmountDue, dueDate, " + "messageType, message, tagId, documentCoverPath, isPDFDeleted,showInCashFlow,showInStatements,showInBill,showInUnpaid, documentPath FROM statements where "
				+ " id = " + currentDocumentID + " and userId = " + loginUserId + " and bill = 0 group by displayImagePathBigAltText";

		Cursor cursor = db.rawQuery(query, null);
		String displayImagePathSmall = null;
		String statementDate = null;
		stmtText = "";
		String statemetId = null;
		isBill = false;
		String messageType = null;
		String messagePath = null;
		String[] stmtTextLeft = null;
		String[] stmtTextResultRight = null;
		boolean isPDFDeleted = false;
		//int tagId = 0;
		if (cursor != null) {
			cursor.moveToFirst();
			
			boolean isShowInCashFlow = "1".equals(cursor.getString(16));
			boolean isShowInStatements =  "1".equals(cursor.getString(17));
			boolean isShowInBill = "1".equals(cursor.getString(18));
			boolean isShowInUnpaid = "1".equals(cursor.getString(19));

			statmentName = cursor.getString(1);
			displayImagePathSmall = cursor.getString(2);
			if (cursor.getString(4).equalsIgnoreCase("1")) {
				isPaid = true;
			}
			if (cursor.getString(5).equalsIgnoreCase("1")) {
				isBill = true;
			}
			//LinearLayout linearLayoutCF=(LinearLayout)findViewById(R.id.cashFlowlayout);
			LinearLayout linearLayoutSTSep=(LinearLayout)findViewById(R.id.StatementsLayoutSeparator1);
			LinearLayout linearLayoutST=(LinearLayout)findViewById(R.id.statementlayout);
			//LinearLayout linearLayoutSep=(LinearLayout)findViewById(R.id.cashFlowLayoutSeparator1);
			LinearLayout linearLayoutBill=(LinearLayout)findViewById(R.id.billLayout);
			//LinearLayout linearLayoutUnPaidSep=(LinearLayout)findViewById(R.id.unPaidLayoutSep);
			//LinearLayout linearLayoutUnPaid=(LinearLayout)findViewById(R.id.unpaidLayout);
			
			if (isBill) {
				if(!isPaid){
					imageButtonPayBy.setVisibility(View.VISIBLE);
				} else  {
					imageButtonPayBy.setVisibility(View.GONE);
				}
				
				//linearLayoutSep.setVisibility(View.GONE);
				//linearLayoutCF.setVisibility(View.GONE);
				linearLayoutSTSep.setVisibility(View.GONE);
				linearLayoutST.setVisibility(View.GONE);
				linearLayoutBill.setVisibility(View.VISIBLE);
				//linearLayoutUnPaidSep.setVisibility(View.VISIBLE);
				//linearLayoutUnPaid.setVisibility(View.VISIBLE);
				stmtText = "Bill";
				statementDate = cursor.getString(7);
				stmtTextLeft = new String[3];
				stmtTextResultRight = new String[3];
				stmtTextLeft[0] = "New Balance ";
				stmtTextLeft[1] = "Min Payment ";
				stmtTextLeft[2] = "Due Date";
				dueDate = cursor.getString(10);

				stmtTextResultRight[0] = cursor.getString(8);
				stmtTextResultRight[1] = cursor.getString(9);
				stmtTextResultRight[2] = dueDate;
				
				if (isShowInBill){
					ToggleButton toggleButton = (ToggleButton)findViewById(R.id.billsCheckBox);
					toggleButton.setChecked(isShowInBill);
					toggleButton.setBackgroundResource(R.drawable.checkbox_ticked);
				} else {
					ToggleButton toggleButton = (ToggleButton)findViewById(R.id.billsCheckBox);
					toggleButton.setChecked(isShowInBill);
					toggleButton.setBackgroundResource(R.drawable.checkbox_un_ticked);
				}
				
				/*if(isShowInUnpaid){
					ToggleButton toggleButton = (ToggleButton)findViewById(R.id.unpaidCheckBox);
					toggleButton.setChecked(isShowInStatements);
					toggleButton.setBackgroundResource(R.drawable.checkbox_ticked);
				} else {
					ToggleButton toggleButton = (ToggleButton)findViewById(R.id.unpaidCheckBox);
					toggleButton.setChecked(isShowInStatements);
					toggleButton.setBackgroundResource(R.drawable.checkbox_un_ticked);
				}*/
				
			} else {
				imageButtonPayBy.setVisibility(View.GONE);
				//linearLayoutCF.setVisibility(View.VISIBLE);
				linearLayoutSTSep.setVisibility(View.VISIBLE);
				linearLayoutST.setVisibility(View.VISIBLE);
				//linearLayoutSep.setVisibility(View.GONE);
				linearLayoutBill.setVisibility(View.GONE);
				//linearLayoutUnPaidSep.setVisibility(View.GONE);
				//linearLayoutUnPaid.setVisibility(View.GONE);
				stmtText = "Statement";
				statementDate = cursor.getString(6);
				statemetId = cursor.getString(0);
				
				/*if (isShowInCashFlow){
					ToggleButton toggleButton = (ToggleButton)findViewById(R.id.cashflowCheckBox);
					toggleButton.setChecked(isShowInCashFlow);
					toggleButton.setBackgroundResource(R.drawable.checkbox_ticked);
				} else {
					ToggleButton toggleButton = (ToggleButton)findViewById(R.id.cashflowCheckBox);
					toggleButton.setChecked(isShowInCashFlow);
					toggleButton.setBackgroundResource(R.drawable.checkbox_un_ticked);
				}*/
				
				if(isShowInStatements){
					ToggleButton toggleButton = (ToggleButton)findViewById(R.id.statementCheckBox);
					toggleButton.setChecked(isShowInStatements);
					toggleButton.setBackgroundResource(R.drawable.checkbox_ticked);
				} else {
					ToggleButton toggleButton = (ToggleButton)findViewById(R.id.statementCheckBox);
					toggleButton.setChecked(isShowInStatements);
					toggleButton.setBackgroundResource(R.drawable.checkbox_un_ticked);
				}
			}

			messageType = cursor.getString(11);
			messagePath = cursor.getString(12);
			tagId = cursor.getInt(13);
			documentCoverPath = cursor.getString(14);
			if (cursor.getString(15).equalsIgnoreCase("1")) {
				isPDFDeleted = true;
			}
			currentPdfDownloadURL = cursor.getString(20);
			currentPdfFileName = currentPdfDownloadURL.substring(currentPdfDownloadURL.lastIndexOf("/") + 1);
		}
		cursor.close();

		/*unpaidCheckBox = (ToggleButton) findViewById(R.id.unpaidCheckBox);
		unpaidCheckBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if (isChecked) {
					unpaidCheckBox.setBackgroundResource(R.drawable.checkbox_ticked);
					StatementDAO statementDAO = new StatementDAO();
					statementDAO.updatePaid(currentDocumentID, 0, null);
				} else {
					unpaidCheckBox.setBackgroundResource(R.drawable.checkbox_un_ticked);
					StatementDAO statementDAO = new StatementDAO();
					statementDAO.updatePaid(currentDocumentID, 1, null);
				}
			}
		});*/
		

		// For StatementsSubAccount details
		if (statemetId != null) {
			stmtTextLeft = null;
			stmtTextResultRight = null;
			String queryStmtSubAcc = "SELECT StatementId, subaccountType, endBalance FROM StatementsSubAccounts where statementid = " + statemetId;
			Cursor cursorStmtSubAcc = db.rawQuery(queryStmtSubAcc, null);
			stmtTextLeft = new String[cursorStmtSubAcc.getCount()];
			stmtTextResultRight = new String[cursorStmtSubAcc.getCount()];
			int i = 0;
			if (cursorStmtSubAcc.moveToFirst()) {
				do {
					stmtTextLeft[i] = cursorStmtSubAcc.getString(1);
					stmtTextResultRight[i] = String.valueOf(cursorStmtSubAcc.getDouble(2));
					i++;
				} while (cursorStmtSubAcc.moveToNext());
			}
			cursorStmtSubAcc.close();
		}

		TextView stmtText1Left = (TextView) findViewById(R.id.statmentText1_Left);
		stmtText1Left.setText("");
		TextView stmtText2Left = (TextView) findViewById(R.id.statmentText2_Left);
		stmtText2Left.setText("");
		TextView stmtText3Left = (TextView) findViewById(R.id.statmentText3_Left);
		stmtText3Left.setText("");
		
		stmtText1Right = (TextView) findViewById(R.id.statmentText1_right);
		stmtText1Right.setText("");
		TextView stmtText2Right = (TextView) findViewById(R.id.statmentText2_right);
		stmtText2Right.setText("");
		TextView stmtText3Right = (TextView) findViewById(R.id.statmentText3_right);
		stmtText3Right.setText("");
		
		for (int i = 0; i < stmtTextLeft.length; i++) {
			if (i == 0) {
				stmtText1Left.setText(stmtTextLeft[0]);
				stmtText1Right.setText("$ " + NumberFormatUtil.insertCommas(stmtTextResultRight[0]));
			}

			if (i == 1) {
				stmtText2Left.setText(stmtTextLeft[1]);
				stmtText2Right.setText("$ " + NumberFormatUtil.insertCommas(stmtTextResultRight[1]));
			}
			if (i == 2) {
				stmtText3Left.setText(stmtTextLeft[2]);
				if (statemetId != null) {
					stmtText3Right.setText("$ " + NumberFormatUtil.insertCommas(stmtTextResultRight[2]));
				} else {
					stmtText3Right.setText(stmtTextResultRight[2]);
				}
			}
		}

		TextView statementslbl = (TextView) findViewById(R.id.statementslbl);
		statementslbl.setText(stmtText);

		ImageView statementImage = (ImageView) findViewById(R.id.statmentsImage);

		String displayImagePathSmallfileName = displayImagePathSmall.substring(displayImagePathSmall.lastIndexOf("/") + 1);
		File fdisplayImagePathSmall = new File(getBaseContext().getFilesDir(), displayImagePathSmallfileName);
		if (fdisplayImagePathSmall.exists()) {
			Log.w("Bill Details", "setting image :" + fdisplayImagePathSmall.getAbsolutePath());
			Bitmap myBitmap = BitmapFactory.decodeFile(fdisplayImagePathSmall.getAbsolutePath());

			statementImage.setImageBitmap(myBitmap);
		}

		TextView textOfferDescription = (TextView) findViewById(R.id.statementName);
		textOfferDescription.setText(statmentName);

		textViewBillLable = (TextView) findViewById(R.id.statementDescription);
		textViewBillLable.setText(stmtText);

		textViewWithDate = (TextView) findViewById(R.id.statementDescriptionWithdate);
		textViewWithDate.setText(" " + " • " + statementDate);

		TextView documentHistoryResultText = (TextView) findViewById(R.id.documentHistoryResultText);
		documentHistoryResultText.setText("• Received date:" + statementDate);

		// Logic to display Image or Video in side menu bar
		ImageView imgMessageView = (ImageView) findViewById(R.id.messageImageView);
		imgVideoThumbNail = (ImageView) findViewById(R.id.videoImageThumb);
		mVideoView = (VideoView) findViewById(R.id.messageVideoView);
		mcon = new MediaController(this);
		
		if (messageType.equalsIgnoreCase("Video")) {
			imgMessageView.setVisibility(View.GONE);
			imgVideoThumbNail.setVisibility(View.VISIBLE);
			mVideoView.setVisibility(View.VISIBLE);

			String stmtVideofileName = messagePath.substring(messagePath.lastIndexOf("/") + 1);
			File stmtVideoPathSmall = new File(getBaseContext().getFilesDir(), stmtVideofileName);
			try {
				if (!stmtVideoPathSmall.exists()) {
					saveVideoToSDcard(messagePath);
				}
				messagePath = stmtVideoPathSmall.getAbsolutePath();
				// Here we created the thumb-nail for the video
				Bitmap thumbNail = createThumbNailVideo(messagePath);
				BitmapDrawable bitmapDrawable = new BitmapDrawable(thumbNail);
				imgVideoThumbNail.setBackgroundDrawable(bitmapDrawable);

				mcon.setMediaPlayer(mVideoView);
				mcon.setAnchorView(mVideoView);
				mcon.setEnabled(true); 
				mVideoView.setMediaController(mcon);
				mVideoView.setVideoURI(Uri.parse(messagePath));
				/* mcon.show(); */
				imgVideoThumbNail.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						
						mVideoView.start();
						isVideoPlayed = true;
						mcon.setEnabled(true); 
						mcon.show();
						if (isVideoPlayed) {
							imgVideoThumbNail.setVisibility(ImageView.GONE);
							mVideoView.setVisibility(View.VISIBLE);
						} else {
							imgVideoThumbNail.setVisibility(ImageView.VISIBLE);
							mVideoView.setVisibility(View.GONE);
						}

					}
				});
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		} 

		if (messageType.equalsIgnoreCase("Image")) {
			mVideoView.setVisibility(View.GONE);
			imgMessageView.setVisibility(View.VISIBLE);

			if (messagePath != null && messagePath.lastIndexOf("/") > -1){
				String stmtImagefileName = messagePath.substring(messagePath.lastIndexOf("/") + 1);
				File stmtImagePathSmall = new File(getBaseContext().getFilesDir(), stmtImagefileName);
				if (stmtImagePathSmall.exists()) {
					Log.w("Bill Details", "setting image :" + stmtImagePathSmall.getAbsolutePath());
					Bitmap myBitmap = BitmapFactory.decodeFile(stmtImagePathSmall.getAbsolutePath());
					imgMessageView.setImageBitmap(myBitmap);
				} else {
					String stmtImagefileName1 = messagePath.substring(messagePath.lastIndexOf("/") + 1);
					/*String imagePath = ImageUtil.downloadImage(currentActivity, messagePath, stmtImagefileName1);
					Bitmap myBitmap = BitmapFactory.decodeFile(imagePath);
					imgMessageView.setImageBitmap(myBitmap);*/
					
					new ImageViewDownloader(imgMessageView,this).execute(messagePath,stmtImagefileName1);
				}
			}
		}
		
		coverPagePdffileName = documentCoverPath.substring(documentCoverPath.lastIndexOf("/") + 1);
		if(isPDFDeleted) {
			statementdeleteInformation.setVisibility(View.VISIBLE);
			revertBtnToDownloadpdf.setVisibility(View.VISIBLE);
			imgPdfCoverPage.setVisibility(View.GONE);
			taptoView.setVisibility(View.GONE);
		} else {
			statementdeleteInformation.setVisibility(View.GONE);
			revertBtnToDownloadpdf.setVisibility(View.GONE);
			imgPdfCoverPage.setVisibility(View.VISIBLE);
			taptoView.setVisibility(View.VISIBLE);
			
			if (documentCoverPath != "") {
				File pdfCoverPage = new File(getBaseContext().getFilesDir(), coverPagePdffileName);
				if (pdfCoverPage.exists()) {
					try{
						imgPdfCoverPage.setImageURI(Uri.fromFile(pdfCoverPage));
					} catch (OutOfMemoryError ome){
		            	Log.w("Offer Details Activity: showOfferImage method", "setting image size is too large :"+pdfCoverPage.getAbsolutePath());
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
		                imgPdfCoverPage.setImageURI(Uri.fromFile(pdfCoverPage));
		            } finally {
		            	//myBitmap = null;
		            	System.gc();
		            }
					
				} else {
					/*String imagePath = ImageUtil.downloadImage(currentActivity, documentCoverPath, coverPagePdffileName);
					File pdfCoverPageDownloadPath = new File(imagePath);
					imgPdfCoverPage.setImageURI(Uri.fromFile(pdfCoverPageDownloadPath));*/
					ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Activity.CONNECTIVITY_SERVICE);

					if (cm != null && cm.getActiveNetworkInfo() != null	&& cm.getActiveNetworkInfo().isConnected()) {
						new ImageViewDownloader(imgPdfCoverPage,this).execute(documentCoverPath,coverPagePdffileName);
						imgPdfCoverPage.setVisibility(View.VISIBLE);
						taptoView.setVisibility(View.VISIBLE);
						statementdeleteInformation.setVisibility(View.GONE);
						revertBtnToDownloadpdf.setVisibility(View.GONE);
					}else{
						imgPdfCoverPage.setVisibility(View.GONE);
						taptoView.setVisibility(View.GONE);
						statementdeleteInformation.setVisibility(View.VISIBLE);
						revertBtnToDownloadpdf.setVisibility(View.VISIBLE);
					}
				}
				
			}
		}

		StatementDAO statementDao = new StatementDAO();
		try {
			ConnectivityManager cm = (ConnectivityManager) this.getSystemService(Activity.CONNECTIVITY_SERVICE);
			if(cm != null && cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected()){
				StatementsService.readUnreadStatements(currentActivity, loginUserId, currentDocumentID, true);
				statementDao.updateRead(currentDocumentID, 1, this,0);
			} else {
				statementDAO.updateRead(currentDocumentID, 0, this,1);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		/*pdf = (PDFView) findViewById(R.id.pdfview);
		pdf.recycle();*/

		// Determine the Next Bill and Previous Bill
		ImageView imgNextBtn = (ImageView) findViewById(R.id.pageoffer_next_link);
		ImageView imgBackBtn = (ImageView) findViewById(R.id.pageoffer_back_link);
		for (int i = 0; i < allStatmentsList.length; i++) {
			if (allStatmentsList[i].equals(currentDocumentID + "")) {
				if (i == allStatmentsList.length - 1) {
					nextDocumentId = Long.parseLong(allStatmentsList[0]);
					imgNextBtn.setVisibility(View.INVISIBLE);
				} else {
					nextDocumentId = Long.parseLong(allStatmentsList[i + 1]);
					imgNextBtn.setVisibility(View.VISIBLE);
				}

				if (i == 0) {
					previousDocumentId = Long.parseLong(allStatmentsList[allStatmentsList.length - 1]);
					imgBackBtn.setVisibility(View.INVISIBLE);
				} else {
					previousDocumentId = Long.parseLong(allStatmentsList[i - 1]);
					imgBackBtn.setVisibility(View.VISIBLE);
				}
			}
		}
		
		checkIsFavorite = (ToggleButton) findViewById(R.id.pageoffer_subnav_icon2);
		
		providerId = documentProvidersDAO.getProviderId(statmentName, currentActivity);
        
		isFavourite = documentProvidersDAO.isFavourit(loginUserId, providerId, currentActivity);
		
        if(isFavourite) {
        	checkIsFavorite.setBackgroundResource(R.drawable.pageoffer_subnav_icon2_yellowstar);
        	checkIsFavorite.setChecked(true);
        } else {
        	checkIsFavorite.setBackgroundResource(R.drawable.pageoffer_subnav_icon2);
        	checkIsFavorite.setChecked(false);
        }
	}
	
	@Override
    public Object onRetainNonConfigurationInstance() {
    	
    	final SavedState data = new SavedState();
        data.setSelectedID(Integer.parseInt(currentDocumentID+""));
        
        return data;
    }
	
	public void showStatementPDF(View view) {
    	// Logic to showing the PDF using PDFView
		//String webServiceUrl = getResources().getString(R.string.webservice_url) + "/" + currentPdfFileName;
		String webServiceUrl = currentPdfDownloadURL;
		String extStorageDirectory = Environment.getExternalStorageDirectory().toString();

		File folder = new File(extStorageDirectory, "Document");
		if (!folder.exists()) {
			folder.mkdir();
		}

		File pdfFile = new File(folder.getAbsolutePath(), currentPdfFileName);

		if (pdfFile.exists()) {
			openMuPDF(pdfFile.getAbsoluteFile().toString());
//			showFile(pdfFile.getAbsolutePath());
		} else {
			ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Activity.CONNECTIVITY_SERVICE);

			if (cm != null && cm.getActiveNetworkInfo() != null	&& cm.getActiveNetworkInfo().isConnected()) {
				DownloadTaskPDf taskPdf = new DownloadTaskPDf(this, pdfFile.getAbsolutePath());
				taskPdf.execute(webServiceUrl);
			}else{
				Toast.makeText(context, "Internet connection Not Available", Toast.LENGTH_LONG).show();
			}
		}
		
//		Intent libraryActivityIntent = new Intent(view.getContext(), ShowLibraryPDFActivity.class);
//        libraryActivityIntent.putExtra("usertoken", userToken);
//        libraryActivityIntent.putExtra("userName",userName);
//        libraryActivityIntent.putExtra("salt",salt);
//        libraryActivityIntent.putExtra("loginUserId",loginUserId);
//        libraryActivityIntent.putExtra("fromScreen", "billspage");
//        libraryActivityIntent.putExtra("document_id", currentDocumentID);
//        libraryActivityIntent.putExtra("all_statements_list", allStatmentsList);
//        startActivityForResult(libraryActivityIntent, 0);
	}

	public void buildBackorHome(String fromScreen) {
		// TODO Auto-generated method stub
		ImageView imgHomeBack = (ImageView) findViewById(R.id.home_page_link);
		Button statmentsBackbtn = (Button) findViewById(R.id.statmentsBackbtn);
		actionButton = (ImageButton) findViewById(R.id.pageoffer_subnav_icon4);
		if ("statements".equalsIgnoreCase(fromScreen)) {
			imgHomeBack.setVisibility(View.GONE);
			statmentsBackbtn.setText("< Statements");
		} else if ("bills".equalsIgnoreCase(fromScreen)) {
			imgHomeBack.setVisibility(View.GONE);
			statmentsBackbtn.setText("< Bills");
		} else if ("mail".equalsIgnoreCase(fromScreen)) {
			imgHomeBack.setVisibility(View.GONE);
			statmentsBackbtn.setText("< Mail");
		} else if ("home".equalsIgnoreCase(fromScreen)) {
			imgHomeBack.setVisibility(View.VISIBLE);
			statmentsBackbtn.setText("");
			statmentsBackbtn.setVisibility(View.GONE);
		} else if ("unpaid".equalsIgnoreCase(fromScreen)) {
			imgHomeBack.setVisibility(View.GONE);
			statmentsBackbtn.setText("< Unpaid");
			statmentsBackbtn.setVisibility(View.VISIBLE);
		} else {
			imgHomeBack.setVisibility(View.VISIBLE);
			statmentsBackbtn.setText("");
			statmentsBackbtn.setVisibility(View.GONE);
		}
	}

	public Bitmap createThumbNailVideo(String path) {
		String mMediaPath = path;
		Bitmap bitmap = ThumbnailUtils.createVideoThumbnail(mMediaPath, MediaStore.Video.Thumbnails.MINI_KIND);
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
		return bitmap;
	}

	public void openIn() {
		
			Intent target = new Intent(Intent.ACTION_VIEW);
			target.setDataAndType(Uri.fromFile(pdfFile), "application/pdf");
			target.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);

			Intent intent = Intent.createChooser(target, "Open In");
			try {
				startActivity(intent);
			} catch (ActivityNotFoundException e) {
				// Instruct the user to install a PDF reader here, or something
			}
		

	}

	private void saveVideoToSDcard(String video) throws IOException {
		// logHeap("Bill Details Activity - saveVideoToSDcard method starting ");
		String name = "";
		try {
			URL url = new URL(video);
			URLConnection ucon;
			name = video.substring(video.lastIndexOf("/") + 1);
			System.out.println("video download beginning: " + name);

			// Open a connection to that URL
			ucon = url.openConnection();

			// this timeout affects how long it takes for the app to realize
			// there's
			// a connection problem
			ucon.setReadTimeout(9000);
			ucon.setConnectTimeout(9000);

			// Define InputStreams to read from the URLConnection.
			// uses 3KB download buffer
			InputStream is = ucon.getInputStream();
			BufferedInputStream inStream = new BufferedInputStream(is, 1024 * 5);
			FileOutputStream outStream = new FileOutputStream(getBaseContext().getFilesDir() + "/" + name);
			byte[] buff = new byte[5 * 1024];

			// Read bytes (and store them) until there is nothing more to
			// read(-1)
			int len;
			while ((len = inStream.read(buff)) != -1) {
				outStream.write(buff, 0, len);
			}

			// clean up
			outStream.flush();
			outStream.close();
			inStream.close();

		} catch (Exception ex) {
			Toast.makeText(BillDetailsActivity.this, "There might be a problem with downloading video file : " + name, Toast.LENGTH_LONG).show();
			File pdfFileToDelete = new File(getBaseContext().getFilesDir(), name);
			if (pdfFileToDelete.exists() && pdfFileToDelete.length() == 0) {
				// pdfFileToDelete.delete();
			}
		}
		// logHeap("Bill Details Activity - saveVideoToSDcard method ending ");
	}

	public void sideMenuShowHide(View view) {
		ScrollView scrView = (ScrollView) findViewById(R.id.sidebarScrollView);
		ViewGroup.LayoutParams paramsWidth = scrView.getLayoutParams();

		ImageView sideMenuPanelimg = (ImageView) findViewById(R.id.panelHideShowBtn);
		if (scrView.getVisibility() == View.GONE && sideMenuPanelimg.getPaddingLeft() == 0) {
			sideMenuPanelimg.setPadding(paramsWidth.width, 0, 0, 0);
			scrView.setVisibility(View.VISIBLE);
		} else {
			sideMenuPanelimg.setPadding(0, 0, 0, 0);
			scrView.setVisibility(View.GONE);
			mVideoView.stopPlayback();
			mcon.setEnabled(true);
			isVideoPlayed = false;
			imgVideoThumbNail.setVisibility(ImageView.VISIBLE);
			mVideoView.setVisibility(View.INVISIBLE);
		}
	}

	public void showHideDocHistory(View view) {
		LinearLayout llDocHistory = (LinearLayout) findViewById(R.id.LayoutDocHistory17);
		if (llDocHistory.getVisibility() == View.VISIBLE) {
			llDocHistory.setVisibility(View.GONE);
		} else {
			llDocHistory.setVisibility(View.VISIBLE);
		}
	}

	public void nextBtnClicked(View view) {
		try {
			buildBillDetailsPage(nextDocumentId);
		} catch (Exception e) {
			e.printStackTrace();
			TestFlight.log("Error in Next button click in Bills page Flow" + e.getMessage());
		}
	}

	public void backBtnClicked(View view) {

		System.out.println("PageOffer Previous button clicked");
		try {
			buildBillDetailsPage(previousDocumentId);
		} catch (Exception e) {
			e.printStackTrace();
			TestFlight.log("Error in Previous button click in offers page Flow" + e.getMessage());
		}
	}

	public void showBillsHome(View view) {

//		if ("statements".equalsIgnoreCase(fromScreen)) {
//			Intent accountStatmentsIntent = new Intent(view.getContext(), AccountStatements.class);
//			accountStatmentsIntent.putExtra("usertoken", userToken);
//			accountStatmentsIntent.putExtra("userName", userName);
//			accountStatmentsIntent.putExtra("salt", salt);
//			accountStatmentsIntent.putExtra("loginUserId", loginUserId);
//			if (parentScreen != null) {
//				accountStatmentsIntent.putExtra("fromScreen", parentScreen);
//			}
//
//			startActivityForResult(accountStatmentsIntent, 0);
//		} else if ("bills".equalsIgnoreCase(fromScreen)) {
//			Intent billsStatementIntent = new Intent(view.getContext(), BillsStatement.class);
//			billsStatementIntent.putExtra("usertoken", userToken);
//			billsStatementIntent.putExtra("userName", userName);
//			billsStatementIntent.putExtra("salt", salt);
//			billsStatementIntent.putExtra("loginUserId", loginUserId);
//			if (parentScreen != null) {
//				billsStatementIntent.putExtra("fromScreen", parentScreen);
//			}
//			startActivityForResult(billsStatementIntent, 0);
//		} else if ("mail".equalsIgnoreCase(fromScreen)) {
//			Intent mailActivityIntent = new Intent(view.getContext(), MailActivity.class);
//			mailActivityIntent.putExtra("usertoken", userToken);
//			mailActivityIntent.putExtra("userName", userName);
//			mailActivityIntent.putExtra("salt", salt);
//			mailActivityIntent.putExtra("loginUserId", loginUserId);
//			startActivityForResult(mailActivityIntent, 0);
//		} else {
			finish();
//			Intent dashBoardIntent = new Intent(view.getContext(), DashBoardActivity.class);
//			dashBoardIntent.putExtra("usertoken", userToken);
//			dashBoardIntent.putExtra("userName", userName);
//			dashBoardIntent.putExtra("salt", salt);
//			dashBoardIntent.putExtra("loginUserId", loginUserId);
//			startActivityForResult(dashBoardIntent, 0);
//		}

//		finish();

	}

	/*@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		/*getMenuInflater().inflate(R.menu.bill_details, menu);
		return true;
	}*/
	
	@Override
    public void onDestroy() {
        super.onDestroy();
        System.gc();
	}
	
	public static void logHeap(String logMethodLocation) {
		Double allocated = Double.valueOf(Debug.getNativeHeapAllocatedSize()) / Double.valueOf((1048576));
		Double available = Double.valueOf(Debug.getNativeHeapSize()) / 1048576.0;
		Double free = Double.valueOf(Debug.getNativeHeapFreeSize()) / 1048576.0;
		DecimalFormat df = new DecimalFormat();
		df.setMaximumFractionDigits(2);
		df.setMinimumFractionDigits(2);

		TestFlight.passCheckpoint(logMethodLocation + " debug.heap native: allocated " + df.format(allocated) + "MB of " + df.format(available) + "MB (" + df.format(free) + "MB free)");
		Log.w("Memory Sizes: ", logMethodLocation + " debug.heap native: allocated " + df.format(allocated) + "MB of " + df.format(available) + "MB (" + df.format(free) + "MB free)");
		TestFlight.passCheckpoint(logMethodLocation + " debug.memory: allocated: " + df.format(Double.valueOf(Runtime.getRuntime().totalMemory() / 1048576)) + "MB of " + df.format(Double.valueOf(Runtime.getRuntime().maxMemory() / 1048576)) + "MB ("
				+ df.format(Double.valueOf(Runtime.getRuntime().freeMemory() / 1048576)) + "MB free)");
		Log.w("Memory Sizes: ",
				logMethodLocation + " debug.memory: allocated: " + df.format(Double.valueOf(Runtime.getRuntime().totalMemory() / 1048576)) + "MB of " + df.format(Double.valueOf(Runtime.getRuntime().maxMemory() / 1048576)) + "MB ("
						+ df.format(Double.valueOf(Runtime.getRuntime().freeMemory() / 1048576)) + "MB free)");
	}

	

	public void showActionPopup(View v) {
		// Creating the instance of PopupMenu
		PopupMenu popup = new PopupMenu(BillDetailsActivity.this, actionButton);
		// Inflating the Popup using xml file
		popup.getMenuInflater().inflate(R.menu.bill_details, popup.getMenu());
		if(!isBill){
		popup.getMenu().getItem(4).setVisible(false);
		}
		// registering popup with OnMenuItemClickListener
		popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
			public boolean onMenuItemClick(MenuItem item) {
				switch (item.getItemId()) {
				
				case R.id.print:
					Toast.makeText(getApplicationContext(), "Print functionality will be implemented in future release", Toast.LENGTH_SHORT).show();
					break;
				case R.id.openInMenu:
					strGoogleDrive = "openIn";
					if(pdfFile != null){
						openIn();	
					}else{
						ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Activity.CONNECTIVITY_SERVICE);

						if (cm != null && cm.getActiveNetworkInfo() != null	&& cm.getActiveNetworkInfo().isConnected()) {
							downloadPDF();
						}else{
							Toast.makeText(context, "Internet connection Not Available", Toast.LENGTH_LONG).show();
						}
						
					}
					
					break;
				case R.id.addToDropBoxMenu:
					strGoogleDrive = "DropBox";
					if(pdfFile != null){
					addToDropBox();
					}else{
						ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Activity.CONNECTIVITY_SERVICE);

						if (cm != null && cm.getActiveNetworkInfo() != null	&& cm.getActiveNetworkInfo().isConnected()) {
							downloadPDF();
						}else{
							Toast.makeText(context, "Internet connection Not Available", Toast.LENGTH_LONG).show();
						}
					}
					break;
				case R.id.addToCalenderMenu:
					 addToCalender();
					break;
				case R.id.addToGoogleDrive:
					strGoogleDrive = "GoogleDrive";
					if(pdfFile != null){
						addToGoogleDrive();
					}else{
						ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Activity.CONNECTIVITY_SERVICE);

						if (cm != null && cm.getActiveNetworkInfo() != null	&& cm.getActiveNetworkInfo().isConnected()) {
							downloadPDF();
						}else{
							Toast.makeText(context, "Internet connection Not Available", Toast.LENGTH_LONG).show();
						}
					}
					break;
				case R.id.deleteBillMenu:
					strGoogleDrive = "";
					deleteStatementPDF(currentDocumentID, coverPagePdffileName, currentPdfFileName);
					break;
				}

				return true;
			}
		});

		popup.show();// showing popup menu
	}
	
	
	public void payBy(View view) {
		// Creating the instance of PopupMenu
		PopupMenu popup = new PopupMenu(BillDetailsActivity.this, view);
		// Inflating the Popup using xml file
		popup.getMenuInflater().inflate(R.menu.payby, popup.getMenu());

		// registering popup with OnMenuItemClickListener
		popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
			public boolean onMenuItemClick(MenuItem item) {
				Intent billPayIntent =null;
				switch (item.getItemId()) {
					case R.id.googleWallet:
						billPayIntent = new Intent(currentActivity.getBaseContext(), BillPayActivity.class);
						billPayIntent.putExtra("usertoken", userToken);
						billPayIntent.putExtra("userName", userName);
						billPayIntent.putExtra("salt", salt);
						billPayIntent.putExtra("loginUserId", loginUserId);
						billPayIntent.putExtra("url", "http://www.google.com/wallet/");
						startActivityForResult(billPayIntent, 0);
						break;
				
					case R.id.payPal:
						billPayIntent = new Intent(currentActivity.getBaseContext(), BillPayActivity.class);
						billPayIntent.putExtra("usertoken", userToken);
						billPayIntent.putExtra("userName", userName);
						billPayIntent.putExtra("salt", salt);
						billPayIntent.putExtra("loginUserId", loginUserId);
						billPayIntent.putExtra("url", "Https://www.paypal.com");
						startActivityForResult(billPayIntent, 0);
						break;
					case R.id.online:
						billPayIntent = new Intent(currentActivity.getBaseContext(), BillPayActivity.class);
						billPayIntent.putExtra("usertoken", userToken);
						billPayIntent.putExtra("userName", userName);
						billPayIntent.putExtra("salt", salt);
						billPayIntent.putExtra("loginUserId", loginUserId);
						billPayIntent.putExtra("url", "Https://www.rrdonnelley.com/m-ebpp/index.html");
						startActivityForResult(billPayIntent, 0);
						break;
						
				}

				return true;
			}
		});

		popup.show();// showing popup menu
	}


	public void deleteStatementPDF(long currentDocumentID, String coverPagePdffileName, String currentPdfFileName) {
		String extStorageDirectory = Environment.getExternalStorageDirectory().toString();
		File folder = new File(extStorageDirectory, "Document");
		File pdfFileToDelete = new File(folder.getAbsolutePath(), currentPdfFileName);
		if (pdfFileToDelete.exists()) {
			pdfFileToDelete.delete();
		}
		
		File pdfCoverPage = new File(getBaseContext().getFilesDir(), coverPagePdffileName);
		if (pdfCoverPage.exists()) {
			pdfCoverPage.delete();
		}
		
		StatementDAO statementDao = new StatementDAO();
		
		try {
			ConnectivityManager cm = (ConnectivityManager) this.getSystemService(Activity.CONNECTIVITY_SERVICE);
			if(cm != null && cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected()){
				StatementsService.documentDelete(currentActivity, currentDocumentID, true, loginUserId);
				statementDao.updateIsPdfDeleted(currentDocumentID, 1, currentActivity, 0);
			} else {
				statementDao.updateIsPdfDeleted(currentDocumentID, 1, currentActivity, 1);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		imgPdfCoverPage.setVisibility(View.GONE);
		taptoView.setVisibility(View.GONE);
		
		statementdeleteInformation.setVisibility(View.VISIBLE);
		revertBtnToDownloadpdf.setVisibility(View.VISIBLE);
	}

	
	public void downloadStatementPDF(View view) {

		StatementDAO statementDao = new StatementDAO();
		
		try {
			ConnectivityManager cm = (ConnectivityManager) this.getSystemService(Activity.CONNECTIVITY_SERVICE);
			if(cm != null && cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected()){
				StatementsService.documentDelete(currentActivity, currentDocumentID, false, loginUserId);
				statementDao.updateIsPdfDeleted(currentDocumentID, 0, currentActivity, 0);
			} else {
				statementDao.updateIsPdfDeleted(currentDocumentID, 0, currentActivity, 1);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		ConnectivityManager cm = (ConnectivityManager) this.getSystemService(Activity.CONNECTIVITY_SERVICE);
		if(cm != null && cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected()){
			
		}else{
			Toast.makeText(context, "Internet connection Not Available", Toast.LENGTH_LONG).show();
			return;
		}
		File pdfCoverPage = new File(getBaseContext().getFilesDir(), coverPagePdffileName);
		if (!pdfCoverPage.exists()) {
			String imagePath = ImageUtil.downloadImage(currentActivity, documentCoverPath, coverPagePdffileName);
			File pdfCoverPageDownloadPath = new File(imagePath);
			imgPdfCoverPage.setImageURI(Uri.fromFile(pdfCoverPageDownloadPath));
		}
		imgPdfCoverPage.setVisibility(View.VISIBLE);
		taptoView.setVisibility(View.VISIBLE);
		
		statementdeleteInformation.setVisibility(View.GONE);
		revertBtnToDownloadpdf.setVisibility(View.GONE);
		
		//String webServiceUrl = getResources().getString(R.string.webservice_url_pdf) + "/" + currentDocumentID + ".pdf";
		String webServiceUrl = currentPdfDownloadURL;
		String extStorageDirectory = Environment.getExternalStorageDirectory().toString();

		File folder = new File(extStorageDirectory, "Document");
		if (!folder.exists()) {
			folder.mkdir();
		}

		File pdfFile = new File(folder.getAbsolutePath(), currentPdfFileName);

		if (!pdfFile.exists()) {
				DownloadTask task = new DownloadTask(this, pdfFile.getAbsolutePath());
				task.execute(webServiceUrl);
		}
		
		
	}
	
	
	
	
	public void addToDropBox() {
		String appKey = "17xvui7szluvwi1";
		String appSecret = "3be8sdjcq74c8at";
		mDbxAcctMgr = DbxAccountManager.getInstance(getApplicationContext(), appKey, appSecret);
		DbxPath testPath = new DbxPath(DbxPath.ROOT, pdfFile.getName());
		DbxFile testFile = null;
		// Create DbxFileSystem for synchronized file access.
		try {
			if (mDbxAcctMgr.hasLinkedAccount()) {
				DbxFileSystem dbxFs = DbxFileSystem.forAccount(mDbxAcctMgr.getLinkedAccount());
				testFile = dbxFs.create(testPath);
				testFile.writeFromExistingFile(pdfFile, false);
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
		
		buildBillDetailsPage(currentDocumentID);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case REQUEST_LINK_TO_DBX:
			if (requestCode == REQUEST_LINK_TO_DBX) {
				if (resultCode == Activity.RESULT_OK) {

					if (mDbxAcctMgr.hasLinkedAccount()) {
						DbxPath testPath = new DbxPath(DbxPath.ROOT, pdfFile.getName());
						DbxFile testFile = null;
						try {
							DbxFileSystem dbxFs = DbxFileSystem.forAccount(mDbxAcctMgr.getLinkedAccount());
							testFile = dbxFs.create(testPath);
							testFile.writeFromExistingFile(pdfFile, false);
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

	}


	private void displayMenu(String currentDocumentId, long userId){
		
		List<Tag> tagsListFromDB = new TagDAO().getAllTags(currentActivity, userId);
		int i = 0;
		
		StatementDAO statementDAO = new StatementDAO();
		int tagId = statementDAO.getTagId(currentActivity, userId, Long.parseLong(currentDocumentId));
		
		//final QuickAction quickAction = new QuickAction(this, QuickAction.ANIM_GROW_FROM_CENTER);
		statementQuickAction = new StatementQuickAction(this, QuickAction.ANIM_GROW_FROM_CENTER);
		
		ImageView tagButton1 = (ImageView) (findViewById(R.id.pageoffer_subnav_icon1)); 
		
		statementQuickAction.setMainPageTagImageView(tagButton1);
		
		statementQuickAction.setCurrentActivity(currentActivity);
		statementQuickAction.setUserId(userId);
		//quickAction.setOfferId(Long.parseLong(offerId));
		statementQuickAction.setCurrentDocumentId(Long.parseLong(currentDocumentId));
		StatementActionItem statementActionItem = null;
		
		for(Tag tag : tagsListFromDB){
			boolean isTagged = false;
			if(tag.getTagId() == tagId){
				isTagged = true;
			}
			if(tag.getName().length()!=0){
				statementActionItem = new StatementActionItem(tag.getTagId(), tag.getName(), getResources().getDrawable(R.drawable.tabbutton),isTagged,Long.valueOf(currentDocumentId), userId,tag.getColorCode());
				statementQuickAction.addActionItem(statementActionItem);
				i++;
			}
		}
		tagsListFromDB = null;
		
		statementActionItem = new StatementActionItem(0, "Add Tag", getResources().getDrawable(R.drawable.tabbutton),false,Long.valueOf(currentDocumentId), userId,TAG_DEFAULT_COLOR);
		statementQuickAction.addActionItem(statementActionItem);
		
		/*ActionItem nextItem 	= new ActionItem(ID_DOWN, "Next", getResources().getDrawable(R.drawable.tabbutton),true);
		ActionItem prevItem 	= new ActionItem(ID_UP, "Prev", getResources().getDrawable(R.drawable.tabbutton),true);
        ActionItem searchItem 	= new ActionItem(ID_SEARCH, "Find", getResources().getDrawable(R.drawable.tabbutton),false);
        ActionItem infoItem 	= new ActionItem(ID_INFO, "Info", getResources().getDrawable(R.drawable.tabbutton),false);
        ActionItem eraseItem 	= new ActionItem(ID_ERASE, "Clear", getResources().getDrawable(R.drawable.tabbutton),false);
        ActionItem okItem 		= new ActionItem(ID_OK, "OK", getResources().getDrawable(R.drawable.tabbutton), false);*/
		
		
		
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
		statementQuickAction.setOnActionItemClickListener(new StatementQuickAction.OnActionItemClickListener() {			
			@Override
			public void onItemClick(StatementQuickAction source, int pos, int actionId) {
				if(actionId==0){
					showPopup(source,actionId);
				}else{

					//Code block to handle isClickOnSameItem - START
					boolean isClickOnSameItem = false;
					StatementDAO statementDAO = new StatementDAO();
					TagDAO tagDAO = new TagDAO();
					StatementActionItem action = source.getActionItem(pos);
					
					Tag tag = new Tag();
					tag = statementDAO.getTagWithUserIdAndDocumentId(loginUserId, currentDocumentID,currentActivity);
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
					
					
					
					/*StatementActionItem action = source.getActionItem(pos);
					action.getTagColorCode();*/
					
					//TagDAO tagDAO = new TagDAO();
					tagDAO.updateOrInsertTag(action.getTitle(), action.getTagColorCode(), currentActivity,loginUserId);
					//Toast.makeText(getApplicationContext(), "Tag saved", Toast.LENGTH_SHORT).show();
					
					
					int tagId = tagDAO.getTagIdWithColorCode(action.getTagColorCode(), currentActivity, loginUserId);
					
					ImageView tagButton = (ImageView) findViewById(R.id.pageoffer_subnav_icon1);
					
					//StatementDAO statementDAO = new StatementDAO();
					boolean isSlected = statementDAO.tagExistsUserIdAndDocumentId(loginUserId, currentDocumentID,currentActivity);
					
					if(!isSlected){
						statementDAO.resetTagToStatement(currentDocumentID, loginUserId, currentActivity);
						statementDAO.setTagToStatement(currentDocumentID, loginUserId, tagId, currentActivity);
						tagButton.setBackgroundColor(Color.parseColor(action.getTagColorCode()));
					}else{
						if(isClickOnSameItem){
							statementDAO.resetTagToStatement(currentDocumentID, loginUserId, currentActivity);
							tagButton.setBackgroundColor(Color.parseColor("#FFFFFF"));	
						} else {
							statementDAO.resetTagToStatement(currentDocumentID, loginUserId, currentActivity);
							statementDAO.setTagToStatement(currentDocumentID, loginUserId, tagId, currentActivity);
							tagButton.setBackgroundColor(Color.parseColor(action.getTagColorCode()));
						}
					}
					
					
					new AsyncTagSyncToBackend(currentActivity,true,true,false)
					.execute(String.valueOf(loginUserId));
					
					/*try {
						TagService.syncTagsToBackend(currentActivity, loginUserId);
						TagService.syncUserStatementTagsToBackend(currentActivity, loginUserId);
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
					
					displayMenu(currentDocumentID+"", loginUserId);
					
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
		statementQuickAction.setOnDismissListener(new StatementQuickAction.OnDismissListener() {			
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
				statementQuickAction.show(v);
			}
		});

	}
	
	
    public void showTagsMenu(View view){

    }
    
    protected void showPopup(StatementQuickAction source, int actionId) {
		
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
						Builder myQuittingDialogBox = new AlertDialog.Builder(BillDetailsActivity.this); 
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
						Builder myQuittingDialogBox = new AlertDialog.Builder(BillDetailsActivity.this); 
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
						Builder myQuittingDialogBox = new AlertDialog.Builder(BillDetailsActivity.this); 
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
						Builder myQuittingDialogBox = new AlertDialog.Builder(BillDetailsActivity.this); 
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
							
							StatementDAO statementDAO = new StatementDAO();
							statementDAO.resetTagToStatement(currentDocumentID, loginUserId, currentActivity);
							statementDAO.setTagToStatement(currentDocumentID, loginUserId, tagId, currentActivity);
							
							
							new AsyncTagSyncToBackend(currentActivity,true,true,false)
							.execute(String.valueOf(loginUserId));
							
							/*try {
								TagService.syncTagsToBackend(currentActivity, loginUserId);
								TagService.syncUserStatementTagsToBackend(currentActivity, loginUserId);
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
							
							displayMenu(String.valueOf(currentDocumentID), loginUserId);
							
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



	public void showToast(final String toast) {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				Toast.makeText(getApplicationContext(), toast, Toast.LENGTH_SHORT).show();
			}
		});
	}
	
	/**
	 * Add to Calendar
	 */
	
	protected void addToCalender() {
		try {
		
			DateFormatUtil dateformat = new DateFormatUtil();
			Date outputDate = dateformat.dateFormater("MM/dd/yyyy", "MMM dd, yyyy, h:mma", dueDate);

		    Uri EVENTS_URI = Uri.parse(getCalendarUriBase() + "events");
		    ContentResolver cr = this.getContentResolver();
		    // event insert
		    ContentValues values = new ContentValues();
		    
		    values.put(Events.EVENT_TIMEZONE, TimeZone.getDefault().getID());
		    values.put("calendar_id", 1);
		    values.put("title", statmentName);
		    values.put("allDay", 0);
		    values.put("dtstart", outputDate.getTime());
		    values.put("dtend", outputDate.getTime());
		    values.put("description", textViewBillLable.getText().toString().trim().toUpperCase()+" - Min Amount Due : "+stmtText1Right.getText().toString().trim());
//		    values.put("visibility", 0);
		    values.put("hasAlarm", 1);// now
		    Uri event = cr.insert(EVENTS_URI, values);
		        // reminder insert
		        Uri REMINDERS_URI = Uri.parse(getCalendarUriBase() + "reminders");
		        values = new ContentValues();
		        values.put("event_id", Long.parseLong(event.getLastPathSegment()));
		        values.put("method", Reminders.METHOD_ALERT);
		        values.put("minutes", 48 * 60);
		        cr.insert(REMINDERS_URI, values);
		        
		        Toast.makeText(context, "Bill is added to Calendar Successfully", Toast.LENGTH_LONG).show();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	public static String getCalendarUriBase() {

	    String calendarUriBase = null;
	    Uri calendars = Uri.parse("content://calendar/calendars");
	    Cursor managedCursor = null;
		try {
	        managedCursor = ((Activity) context).managedQuery(calendars, null, null, null, null);
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	    if (managedCursor != null) {
	        calendarUriBase = "content://calendar/";
	    } else {
	        calendars = Uri.parse("content://com.android.calendar/calendars");
	        try {
	            managedCursor = ((Activity) context).managedQuery(calendars, null, null, null, null);
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

	private void saveFavourite(boolean isFavourite, boolean isDirty){

        boolean isUserProviderExists = documentProvidersDAO.isUserProviderExists(String.valueOf(loginUserId), String.valueOf(providerId), currentActivity);

        DocumentProviders documentProviders = new DocumentProviders();
		
        if(isUserProviderExists){
        	documentProviders.setUserId(loginUserId);
        	documentProviders.setProviderId(providerId);
        	documentProviders.setFavorite(isFavourite);
        	documentProviders.setDirty(isDirty);
        	documentProvidersDAO.addUpdateUserProviders(isUserProviderExists,documentProviders, currentActivity);
        } else{
        	documentProviders.setUserId(loginUserId);
        	documentProviders.setProviderId(providerId);
        	documentProviders.setFavorite(isFavourite);
        	documentProviders.setDirty(isDirty);
        	documentProvidersDAO.addUpdateUserProviders(isUserProviderExists,documentProviders, currentActivity);
        }
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
		   
		 	startProgress();
		  }
	
	 
	 public void startProgress() {

		 progressBar = new ProgressDialog(context);
	     progressBar.setCancelable(true);
	     progressBar.setMessage("Please Wait ...");
	     progressBar.show();
	     
			new Thread(new Task()).start();
		}

		class Task implements Runnable {
			@Override
			public void run() {
		        try {
		          // File's binary content
		          java.io.File fileContent = new java.io.File(pdfFile.getPath());
		          FileContent mediaContent = new FileContent("application/pdf", fileContent);

		          // File's metadata.
		          com.google.api.services.drive.model.File body = new com.google.api.services.drive.model.File();
		          body.setTitle(fileContent.getName());
		          body.setMimeType("application/pdf");

		          com.google.api.services.drive.model.File file = service.files().insert(body, mediaContent).execute();
		          if (file != null) {
		            showToast("File uploaded: " + file.getTitle());
		            if(progressBar.isShowing()){
		            	progressBar.dismiss();
		            }
		          }
		        } catch (UserRecoverableAuthIOException e) {
		        	if(progressBar.isShowing()){
		            	progressBar.dismiss();
		            }
		        	showToast("Authorization Error Please Try Again");
		          startActivityForResult(e.getIntent(), REQUEST_AUTHORIZATION);
		        } catch (IOException e) {
		          e.printStackTrace();
		          if(progressBar.isShowing()){
		            	progressBar.dismiss();
		           }
		          showToast("Network Error Please Try Again");
		        }
			}

		}
		
		private class DownloadTask extends AsyncTask<String, Integer, String> {

			private Context context;
			String file;
			ProgressDialog dialog;
			int n = 0;

			public DownloadTask(Context context, String file) {
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
				if(strGoogleDrive.equalsIgnoreCase("GoogleDrive")){
				addToGoogleDrive();
				}else if(strGoogleDrive.equalsIgnoreCase("DropBox")){
				addToDropBox();
				}else if(strGoogleDrive.equalsIgnoreCase("openIn")){
					openIn();
				}
			}
		}
		
		
		public void downloadPDF() {

			StatementDAO statementDao = new StatementDAO();
			statementDao.updateIsPdfDeleted(currentDocumentID, 0, currentActivity, 0);
			
			File pdfCoverPage = new File(getBaseContext().getFilesDir(), coverPagePdffileName);
			if (!pdfCoverPage.exists()) {
				String imagePath = ImageUtil.downloadImage(currentActivity, documentCoverPath, coverPagePdffileName);
				File pdfCoverPageDownloadPath = new File(imagePath);
				imgPdfCoverPage.setImageURI(Uri.fromFile(pdfCoverPageDownloadPath));
			}
			imgPdfCoverPage.setVisibility(View.VISIBLE);
			taptoView.setVisibility(View.VISIBLE);
			
			statementdeleteInformation.setVisibility(View.GONE);
			revertBtnToDownloadpdf.setVisibility(View.GONE);
			
			//String webServiceUrl = getResources().getString(R.string.webservice_url_pdf) + "/" + currentDocumentID + ".pdf";
			String webServiceUrl = currentPdfDownloadURL;
			String extStorageDirectory = Environment.getExternalStorageDirectory().toString();

			File folder = new File(extStorageDirectory, "Document");
			if (!folder.exists()) {
				folder.mkdir();
			}

			pdfFile = new File(folder.getAbsolutePath(), currentPdfFileName);

			if (!pdfFile.exists()) {
				DownloadTask task = new DownloadTask(this, pdfFile.getAbsolutePath());
				task.execute(webServiceUrl);
			}
			
		}
		
		public void onToggleClicked(View view) {    
			ToggleButton toggleButton= (ToggleButton) view;
			boolean on = toggleButton.isChecked();
			String columnName="";
			int value=1;
			String url="";
			String sendColumntoBackend = "";
			StatementDAO statementDao = new StatementDAO();
			int tagID=toggleButton.getId();
			if (tagID==R.id.billsCheckBox){
				columnName = "showInBill";
				sendColumntoBackend = "docInBill";
				url = "/user/updateDocIntoBill";
			} /*else if(tagID==R.id.cashflowCheckBox) {
				columnName = "showInCashFlow";
				sendColumntoBackend = "docInCashflow";
				url = "/user/updateDocIntoCashflow";
			}*/ else if (tagID==R.id.statementCheckBox){
				columnName = "showInStatements";
				sendColumntoBackend = "docInStatement";
				url = "/user/updateDocIntoStatement";
			} /*else if (tagID==R.id.unpaidCheckBox){
				columnName = "showInUnpaid";
				sendColumntoBackend = "docInUnPaid";
				url = "/user/updateDocIntoUnPaid";
			}*/
				
				if (on) {
					value=1	;
					toggleButton.setBackgroundResource(R.drawable.checkbox_ticked);
				} else {
					value=0;
					toggleButton.setBackgroundResource(R.drawable.checkbox_un_ticked);
				}
				
				try {
					StatementsService.updateBillorStatementAssistanceInfo(url, currentActivity, loginUserId, currentDocumentID, on, sendColumntoBackend);
					statementDao.updateShowInAssistance(currentDocumentID, value,columnName, currentActivity);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
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
		
		private void openMuPDF(String pdfName) {
			
	        Uri uri = Uri.parse(pdfName);
	        Intent intent = new Intent(context, MuPDFActivity.class);
	        intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
            intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
	        intent.setAction(Intent.ACTION_VIEW);
	        intent.setData(uri);
	        context.startActivity(intent);
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
		    
		    
		
	
}