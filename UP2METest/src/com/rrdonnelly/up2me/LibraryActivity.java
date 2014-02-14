package com.rrdonnelly.up2me;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import com.artifex.mupdfdemo.MuPDFActivity;
import com.rrdonnelly.up2me.dao.UserLibraryDAO;
import com.rrdonnelly.up2me.json.Offer;
import com.rrdonnelly.up2me.json.UserLibrary;
import com.rrdonnelly.up2me.services.StatementsService;
import com.rrdonnelly.up2me.services.UserLibraryService;
import com.rrdonnelly.up2me.util.ControlApplication;
import com.rrdonnelly.up2me.util.DownloadImages;
import com.testflightapp.lib.TestFlight;

import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.TextView;

public class LibraryActivity extends Activity {

	public String userName=null;
	public String userToken=null;
	public String salt=null;
	private Activity currentActivity = null;
	long loginUserId = 0l;
	public String fromScreen = null;
	public String parentScreen = null;
	Button catalogsflyerPlusBtn;
    Button referencePlusBtn;
    Context context;
    
    LinearLayout llBarFlyer;
	LinearLayout llBarReference;
    public File pdfFile = null;
    int readLibcount=0;
    TextView textView;
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_library);
		context = this;

		currentActivity = this;
        userToken=getIntent().getStringExtra("usertoken");
        userName=getIntent().getStringExtra("userName");
        salt=getIntent().getStringExtra("salt");
        loginUserId=getIntent().getLongExtra("loginUserId", 0l);
        fromScreen=getIntent().getStringExtra("fromScreen");
		
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
        
        
        List<UserLibrary> userLibraryFlyer = new UserLibraryDAO().getAllUserLibraryList(currentActivity, loginUserId, "Flyer");
    	List<UserLibrary> UserLibraryReference = new UserLibraryDAO().getAllUserLibraryList(currentActivity, loginUserId, "Reference");
        
    	llBarFlyer = (LinearLayout) findViewById(R.id.BarFlyer);
    	llBarReference = (LinearLayout) findViewById(R.id.BarReference);
    	
    	buildUserLibrarySection(userLibraryFlyer, llBarFlyer);
    	buildUserLibrarySection(UserLibraryReference, llBarReference);
	}
	
	public void setUnreadLibraryCount() {
		textView=(TextView)findViewById(R.id.unReadMessages); 
		int readLibcount = new UserLibraryDAO().getUnReadLibraryCount(currentActivity, loginUserId);
        textView.setText(readLibcount +" "+ "Unread");
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.library, menu);
		return true;
	}
	
	
    public void showHideSearchView(View view) {
   	 SearchView editSearchText = (SearchView) findViewById(R.id.searchviewText);
		if(editSearchText.getVisibility() != View.VISIBLE) {
			editSearchText.setVisibility(View.VISIBLE);
			editSearchText.setFocusable(true);
		} else {
			editSearchText.setVisibility(View.INVISIBLE);
			editSearchText.clearFocus();
			editSearchText.setFocusable(false);
		}
   }

   public void showHome(View view){
//   	if ("home".equalsIgnoreCase(fromScreen) || fromScreen==null){
   		finish();
//	        Intent dashBoardIntent = new Intent(view.getContext(), DashBoardActivity.class);
//	        dashBoardIntent.putExtra("usertoken", userToken);
//	        dashBoardIntent.putExtra("userName", userName);
//	        dashBoardIntent.putExtra("salt", salt);
//	        dashBoardIntent.putExtra("loginUserId", loginUserId);
//	        startActivityForResult(dashBoardIntent, 0);
//   	}else if("assistance".equalsIgnoreCase(fromScreen)){
//	        Intent dashBoardIntent = new Intent(view.getContext(), AssistantsActivity.class);
//	        dashBoardIntent.putExtra("usertoken", userToken);
//	        dashBoardIntent.putExtra("userName", userName);
//	        dashBoardIntent.putExtra("salt", salt);
//	        dashBoardIntent.putExtra("loginUserId", loginUserId);
//	        startActivityForResult(dashBoardIntent, 0);
//   	}
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
							DownloadTaskPDf taskPdf = new DownloadTaskPDf(LibraryActivity.this, pdfFile.getAbsolutePath());
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
			            	new DownloadImages(LibraryActivity.this).execute(imageURL, imageView);
			            } else {
			            	imageView.setImageURI(Uri.fromFile(imgFile));
			            }
					}
	           }    
	       	
	       	llUserLibraryCover.addView(lv);
	       }
	}
   @Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		
		setUnreadLibraryCount();
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
            libraryActivityIntent.putExtra("fromScreen", "libraryActivity");
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
	    protected void onDestroy() {
	    	// TODO Auto-generated method stub
	    	super.onDestroy();
	    	System.gc();
	    }

}
