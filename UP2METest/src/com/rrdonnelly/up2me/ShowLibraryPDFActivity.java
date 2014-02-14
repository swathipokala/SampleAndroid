/*
package com.rrdonnelly.up2me;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;

public class ShowLibraryPDFActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_show_library_pdf);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.show_library_pd, menu);
		return true;
	}

}*/


package com.rrdonnelly.up2me;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.webkit.CookieSyncManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageButton;

import com.rrdonnelly.up2me.dao.StatementDAO;
import com.rrdonnelly.up2me.util.ControlApplication;
import com.testflightapp.lib.TestFlight;

public class ShowLibraryPDFActivity extends Activity{

	private ImageButton imgBtnHome;
	private ImageButton imgBack;
	private ImageButton imgNext;
//	PDFView pdf;
	private int pageNumber;
	private Object pdfName;
	
	private String userName = null;
	private String userToken = null;
	private String salt = null;
	private long loginUserId = 0l;
	private String fromScreen = null;
	private String parentScreen =null;
	private String pdfFileName = "";
	public File pdfFile = null;
	private long documentId;
	private String[] allStatmentsList = null;
	//WebView webView;
	Context context;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_show_library_pdf);
        context = this;
        
        userToken=getIntent().getStringExtra("usertoken");
        userName=getIntent().getStringExtra("userName");
        salt=getIntent().getStringExtra("salt");
        loginUserId=getIntent().getLongExtra("loginUserId", -1);
        fromScreen=getIntent().getStringExtra("fromScreen");
        parentScreen = getIntent().getStringExtra("parentScreen");
        pdfFileName = getIntent().getStringExtra("pdfName");
        allStatmentsList = getIntent().getStringArrayExtra("all_statements_list");
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        
        if("dashboardActivity".equalsIgnoreCase(fromScreen)) {
        		try{
					WebView webview = (WebView) findViewById(R.id.uh_provider_directorytr_cover_web_view);
					webview.setVisibility(View.VISIBLE);
					webview.setWebViewClient(new WebViewClient() {
			               @Override
			               public boolean shouldOverrideUrlLoading(WebView view, String url)
			               {
			                 view.loadUrl(url);
			                 return true;
			               }
			             });
					WebSettings webSettings = webview.getSettings();
					webSettings.setJavaScriptCanOpenWindowsAutomatically(false);
					webSettings.setJavaScriptEnabled(true);
			         webview.loadUrl(pdfFileName);
				}
				catch(Exception e){
					e.printStackTrace();
				}
        }
        		
//        pdf=(PDFView)findViewById(R.id.pdfview);
        
        /*if("dashboardActivity".equalsIgnoreCase(fromScreen) || "libraryActivity".equalsIgnoreCase(fromScreen)) {
        	
	        //String webServiceUrl = getResources().getString(R.string.webservice_url_pdf) + "/" + getIntent().getLongExtra("document_id", 0)+".pdf";
	        long documentID=getIntent().getLongExtra("document_id", 0);
	
	        StatementDAO statementDao = new StatementDAO();
	        statementDao.updateRead(documentID, 1, this, 0);
			String URL = "http://mydigimag.rrd.com/publication/?i=172416";
	        
	        //String URL = "http://www.mydigitalpublication.com/1/";
	        //String URL = "http://209.11.252.135/MyPlans?userToken=" + userToken	+ "&userName=" + userName + "&salt=" + salt;
			
			//CookieSyncManager.createInstance(this);
			//CookieSyncManager.getInstance().startSync();
			
			
			
			if(pdfFileName.equalsIgnoreCase("uh_provider_directorytr_cover")){
				CookieSyncManager.createInstance(this);
				CookieSyncManager.getInstance().startSync();
				
				
				
				try{
					WebView webView = (WebView) findViewById(R.id.uh_provider_directorytr_cover_web_view);
					webView.setVisibility(View.VISIBLE);
					webView.setVerticalScrollBarEnabled(true);
					webView.setHorizontalScrollBarEnabled(true);
					WebSettings webSettings = webView.getSettings();
					//webSettings.setJavaScriptEnabled(true);
					webSettings.setLoadWithOverviewMode(true);
					webSettings.setUseWideViewPort(true);
					webView.loadUrl(URL);
				}catch(Exception e){
					e.printStackTrace();
				}
				
				
				try{
					WebView webview = (WebView) findViewById(R.id.uh_provider_directorytr_cover_web_view);
					webview.setVisibility(View.VISIBLE);
					webview.setWebViewClient(new WebViewClient() {
			               @Override
			               public boolean shouldOverrideUrlLoading(WebView view, 
			String url)
			               {
			                 view.loadUrl(url);
			                 return true;
			               }
			             });
					WebSettings webSettings = webview.getSettings();
					webSettings.setJavaScriptCanOpenWindowsAutomatically(false);
					webSettings.setJavaScriptEnabled(true);
			           //setContentView(webview);
			         webview.loadUrl(URL);
				}
				catch(Exception e){
					e.printStackTrace();
				}
				
				
				WebView webView = (WebView) findViewById(R.id.uh_provider_directorytr_cover_web_view);
				webView.setVerticalScrollBarEnabled(true);
				webView.setHorizontalScrollBarEnabled(true);
				WebSettings webSettings = webView.getSettings();
				webSettings.setJavaScriptEnabled(true);
				//webSettings.setLoadWithOverviewMode(true);
				//webSettings.setUseWideViewPort(true);
				webView.loadUrl(URL);
				CookieSyncManager.createInstance(this);
				CookieSyncManager.getInstance().startSync();
				CookieManager.getInstance().setAcceptCookie(true);
				webView.setVisibility(View.VISIBLE);
				CookieManager cookieManager= new CookieManager();
				cookieManager.setCookiePolicy(CookiePolicy.ACCEPT_ALL);
				CookieHandler.setDefault(cookieManager);
				
			}else{
		        AssetManager am = getAssets();
		        InputStream inputStream = null;
				try {
					inputStream = am.open("pdfs/"+pdfFileName+".pdf");
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		         File pdfFile = createFileFromInputStream(inputStream, getBaseContext().getFilesDir()+"/"+ pdfFileName+".pdf");
		        
		         if(pdfFile.exists()) {
		        	 showFile(pdfFile.getAbsolutePath());
				 } 
			}
        } */ /* else if ("billspage".equalsIgnoreCase(fromScreen) || "myplan".equalsIgnoreCase(fromScreen)) {
        	documentId = getIntent().getLongExtra("document_id", 0);
        	// Logic to showing the PDF using PDFView
    		String webServiceUrl = getResources().getString(R.string.webservice_url_pdf) + "/" + documentId + ".pdf";
    		
    		String extStorageDirectory = Environment.getExternalStorageDirectory().toString();

    		File folder = new File(extStorageDirectory, "Document");
    		if (!folder.exists()) {
    			folder.mkdir();
    		}

    		File pdfFile = new File(folder.getAbsolutePath(), String.valueOf(documentId) + ".pdf");

    		if (pdfFile.exists()) {
    			showFile(pdfFile.getAbsolutePath());
    		} else {
    			DownloadTask task = new DownloadTask(this, pdfFile.getAbsolutePath());
    			task.execute(webServiceUrl);
    		}
        }*/
		 
	}
	
	public void showBillsHome(View view){
		
//		   if("statements".equalsIgnoreCase(fromScreen)){
//			    Intent accountStatmentsIntent = new Intent(view.getContext(), AccountStatements.class);
//			    accountStatmentsIntent.putExtra("usertoken", userToken);
//			    accountStatmentsIntent.putExtra("userName", userName);
//			    accountStatmentsIntent.putExtra("salt", salt);
//			    accountStatmentsIntent.putExtra("loginUserId", loginUserId);
//			    if(parentScreen!=null){
//			    	accountStatmentsIntent.putExtra("fromScreen", parentScreen);
//			    }
//			    
//		        startActivityForResult(accountStatmentsIntent, 0);
//		   } else if("bills".equalsIgnoreCase(fromScreen)){
//			   Intent billsStatementIntent = new Intent(view.getContext(), BillsStatement.class);
//			   billsStatementIntent.putExtra("usertoken", userToken);
//			   billsStatementIntent.putExtra("userName", userName);
//			   billsStatementIntent.putExtra("salt", salt);
//			   billsStatementIntent.putExtra("loginUserId", loginUserId);
//			   if(parentScreen!=null){
//				   billsStatementIntent.putExtra("fromScreen", parentScreen);
//			    }
//		        startActivityForResult(billsStatementIntent, 0);
//		   } else if("mail".equalsIgnoreCase(fromScreen)){
//			   Intent mailActivityIntent = new Intent(view.getContext(), MailActivity.class);
//			   mailActivityIntent.putExtra("usertoken", userToken);
//			   mailActivityIntent.putExtra("userName", userName);
//			   mailActivityIntent.putExtra("salt", salt);
//			   mailActivityIntent.putExtra("loginUserId", loginUserId);
//		        startActivityForResult(mailActivityIntent, 0);
//		   } else {
//			   	Intent dashBoardIntent = new Intent(view.getContext(), DashBoardActivity.class);
//		        dashBoardIntent.putExtra("usertoken", userToken);
//		        dashBoardIntent.putExtra("userName", userName);
//		        dashBoardIntent.putExtra("salt", salt);
//		        dashBoardIntent.putExtra("loginUserId", loginUserId);
//		        startActivityForResult(dashBoardIntent, 0);   
//		   }
			
	        finish();
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.show_library_pd, menu);
		return true;
	}
	
	/*private class DownloadTask extends AsyncTask<String, Integer, String> {

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
			showStatmentPdfFile(file);
		}
	}
	*/
	public void showFile(String file) {
//		pdf.setVisibility(View.VISIBLE);
//		//file=Environment.getExternalStorageDirectory().toString()+"/"+"downloadedfile.pdf";
//		pdf.fromFile(new File(file))
//		   .defaultPage(1)
//		    .showMinimap(false)
//		    .enableSwipe(true)
//		    .load();
		
	}
	
/*	public void showStatmentPdfFile(String file) {
//		pdf.setVisibility(View.VISIBLE);
		// logHeap("Bill Details Activity - DownloadTask inner class - showFile method starting ");
		pdfFile = new File(file);
		// file=Environment.getExternalStorageDirectory().toString()+"/"+"downloadedfile.pdf";
		try {
			if(pdfFile.exists()) {
//				pdf.fromFile(pdfFile).defaultPage(1).showMinimap(false).enableSwipe(true).load();
			}
		} catch (Exception ex) {
			TestFlight.log("There might be a problem with downloading pdf file." + ex.getMessage());
			File pdfFileToDelete = new File(file);
			if (pdfFileToDelete.exists()) {
				pdfFileToDelete.delete();
			}
		}
		// logHeap("Bill Details Activity - DownloadTask inner class - showFile method ending ");
	}*/

//	@Override
//	public void onPageChanged(int page, int pageCount) {
//		pageNumber = page;
//        setTitle(String.format("%s %s / %s", pdfName, page, pageCount));
//		
//	}
//	
	
	/*private File createFileFromInputStream(InputStream inputStream, String outputFilePath) {

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
	}*/
	
	
	public void doneView(View view){
		/*
		   if("libraryActivity".equalsIgnoreCase(fromScreen)){
			    Intent accountStatmentsIntent = new Intent(view.getContext(), LibraryActivity.class);
			    accountStatmentsIntent.putExtra("usertoken", userToken);
			    accountStatmentsIntent.putExtra("userName", userName);
			    accountStatmentsIntent.putExtra("salt", salt);
			    accountStatmentsIntent.putExtra("loginUserId", loginUserId);
			    if(parentScreen!=null){
			    	accountStatmentsIntent.putExtra("fromScreen", parentScreen);
			    }
			    startActivityForResult(accountStatmentsIntent, 0);
		   } else if("myplan".equalsIgnoreCase(fromScreen)){
			   Intent myPlanActivity = new Intent(view.getContext(), MyPlanActivity.class);
		        myPlanActivity.putExtra("usertoken", userToken);
		        myPlanActivity.putExtra("userName",userName);
		        myPlanActivity.putExtra("salt",salt);
		        myPlanActivity.putExtra("loginUserId",loginUserId);
		        myPlanActivity.putExtra("fromScreen","home");
		        myPlanActivity.putExtra("header","CREDIT CARD");
		        String URL ="http://209.11.252.135/CreditCardAssist?userToken=" + userToken	+ "&userName=" + userName + "&salt=" + salt;
		        myPlanActivity.putExtra("URL",URL);
		        startActivityForResult(myPlanActivity, 0);
		   } else if ("dashboardActivity".equalsIgnoreCase(fromScreen) ) {
			   	Intent dashBoardIntent = new Intent(view.getContext(), DashBoardActivity.class);
		        dashBoardIntent.putExtra("usertoken", userToken);
		        dashBoardIntent.putExtra("userName", userName);
		        dashBoardIntent.putExtra("salt", salt);
		        dashBoardIntent.putExtra("loginUserId", loginUserId);
		        startActivityForResult(dashBoardIntent, 0);   
		   } else if("billspage".equalsIgnoreCase(fromScreen)) {
			   Intent billDetailsPage = new Intent(view.getContext(), BillDetailsActivity.class);
			   billDetailsPage.putExtra("usertoken", userToken);
			   billDetailsPage.putExtra("userName", userName);
			   billDetailsPage.putExtra("salt", salt);
			   billDetailsPage.putExtra("loginUserId", loginUserId);
			   billDetailsPage.putExtra("document_id", documentId);
			   billDetailsPage.putExtra("fromscreen", "home");
			   billDetailsPage.putExtra("all_statements_list", allStatmentsList);
		       startActivityForResult(billDetailsPage, 0);   
		   }*/
			
		   /*else if("bills".equalsIgnoreCase(fromScreen)){
		   Intent billsStatementIntent = new Intent(view.getContext(), BillsStatement.class);
		   billsStatementIntent.putExtra("usertoken", userToken);
		   billsStatementIntent.putExtra("userName", userName);
		   billsStatementIntent.putExtra("salt", salt);
		   billsStatementIntent.putExtra("loginUserId", loginUserId);
		   if(parentScreen!=null){
			   billsStatementIntent.putExtra("fromScreen", parentScreen);
		    }
	        startActivityForResult(billsStatementIntent, 0);
	   }*/
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
