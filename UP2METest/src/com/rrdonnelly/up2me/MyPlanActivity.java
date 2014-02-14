package com.rrdonnelly.up2me;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;

import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.artifex.mupdfdemo.MuPDFActivity;
import com.rrdonnelly.up2me.R;
import com.rrdonnelly.up2me.dao.UserDAO;
import com.rrdonnelly.up2me.util.ControlApplication;
import com.testflightapp.lib.TestFlight;

public class MyPlanActivity extends Activity {

	public String userName = null;
	public String userToken = null;
	public String salt = null;
	public long loginUserId = 0l;
	private String fromScreen = null;
	// WebView webView=null;
	public Activity activity;
	String URL;
	String header;
	public File pdfFile = null;
    public boolean loadPDf=false;
	protected FrameLayout webViewPlaceholder;
	protected WebView webView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_my_plan);
		
		TextView headerTxt = (TextView) findViewById(R.id.up2me_txt);
		header = getIntent().getStringExtra("header");
		headerTxt.setText(header);
		userToken = getIntent().getStringExtra("usertoken");
		userName = getIntent().getStringExtra("userName");
		salt = getIntent().getStringExtra("salt");
		loginUserId = getIntent().getLongExtra("loginUserId", -1);
		fromScreen = getIntent().getStringExtra("fromScreen");
		URL = getIntent().getStringExtra("URL");

		activity = this;

		initUI();

	}

	protected void initUI() {
		// Retrieve UI elements
		webViewPlaceholder = ((FrameLayout) findViewById(R.id.webViewPlaceholder));

		// Initialize the WebView if necessary
		if (webView == null) {

			// webView = (WebView) findViewById(R.id.myPlanData);

			
			
			webView = new WebView(this);
			webView.setVerticalScrollBarEnabled(false);
			webView.setHorizontalScrollBarEnabled(false);
			WebSettings webSettings = webView.getSettings();
			webSettings.setAppCachePath(getCacheDir().getAbsolutePath());
			webSettings.setAllowFileAccess(true);
			webSettings.setAppCacheEnabled(true);
			webSettings.setLoadsImagesAutomatically(true);
			webSettings.setAppCacheMaxSize(1024 * 1024 * 8);
			webSettings.setJavaScriptEnabled(true);
			webView.requestFocusFromTouch();
			webSettings.setLoadWithOverviewMode(false);
//			webSettings.setUseWideViewPort(true);
			webSettings.setDomStorageEnabled(true);

			System.out.println((getBaseContext().getCacheDir())
					.getAbsolutePath());

			ConnectivityManager cm = (ConnectivityManager) this
					.getSystemService(Activity.CONNECTIVITY_SERVICE);
			if (cm != null && cm.getActiveNetworkInfo() != null
					&& cm.getActiveNetworkInfo().isConnected()) {
				webSettings.setCacheMode(WebSettings.LOAD_DEFAULT);
				webView.loadUrl(URL);
				webView.setWebViewClient(new WebViewClient() {

					@Override
					public void onPageFinished(WebView view, String url) {
						setUserURL(url);
					}
 
					@Override
					public boolean shouldOverrideUrlLoading(WebView view,
							String url) {
						if (url.startsWith("openpdf")) {
							if(!loadPDf){
							 loadPDf=true;
							 loadPDF(view,url);
							return false;
							}
						}
						return false;
					}
				});

				//webView.setWebChromeClient(new WebChromeClient());
			} else {
				webView.setWebViewClient(new WebViewClient());
				UserDAO userDAO = new UserDAO();
				if ("MY PLAN".equalsIgnoreCase(header)) {
					URL = userDAO.getUserURL(loginUserId, "myplanurl", this);
				} else {
					URL = userDAO.getUserURL(loginUserId, "mycreditcard", this);
				}
				webSettings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
				webView.loadUrl(URL);
			}

			
		}
		
		
		
		// Attach the WebView to its placeholder
		webViewPlaceholder.addView(webView);

		Button btnHomeorAssistance = (Button) findViewById(R.id.homeorBackbtn);
		if ("assistance".equalsIgnoreCase(fromScreen)) {
			btnHomeorAssistance.setText("< Assistants");
			btnHomeorAssistance.setBackgroundResource(17170445);
		} else {
			btnHomeorAssistance.setText("< Home");
			btnHomeorAssistance.setBackgroundResource(17170445);
		}

	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		if (webView != null) {
			// Remove the WebView from the old placeholder
			webViewPlaceholder.removeView(webView);
		}

		super.onConfigurationChanged(newConfig);

		// Load the layout resource for the new configuration
		setContentView(R.layout.activity_my_plan);
		TextView headerTxt = (TextView) findViewById(R.id.up2me_txt);
		header = getIntent().getStringExtra("header");
		headerTxt.setText(header);
		// Reinitialize the UI
		initUI();
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);

		// Save the state of the WebView
		webView.saveState(outState);
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);

		// Restore the state of the WebView
		webView.restoreState(savedInstanceState);
	}

	public void setUserURL(String url) {
		UserDAO userDAO = new UserDAO();
		if (url.indexOf("MyPlans") > -1) {
			userDAO.updateUserURLS(url, "myplanurl", loginUserId, activity);
		} else {
			userDAO.updateUserURLS(url, "mycreditcard", loginUserId, activity);
		}
	}

	public void loadPDF(WebView view, String url) {

		
		String decodedURL = "";
		try {
			decodedURL = URLDecoder.decode(url.replace("openpdf:", ""), "UTF-8");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String documentName = decodedURL.substring(decodedURL.lastIndexOf("/") + 1);
		
		String extStorageDirectory = Environment.getExternalStorageDirectory().toString();

		File folder = new File(extStorageDirectory, "Document");
		if (!folder.exists()) {
			folder.mkdir();
		}

		File pdfFile = new File(folder.getAbsolutePath(), documentName);

		if (pdfFile.exists()) {
			loadPDf=false;
			openMuPDF(pdfFile.getAbsoluteFile().toString());
		} else {
			DownloadTaskPDf taskPdf = new DownloadTaskPDf(this, pdfFile.getAbsolutePath());
			taskPdf.execute(decodedURL);
		}
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		/* getMenuInflater().inflate(R.menu.my_plan, menu); */
		return true;
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
			loadPDf=false;
		}
	}
	
	public void showStatmentPdfFile(String file) {
//		pdf.setVisibility(View.VISIBLE);
		// logHeap("Bill Details Activity - DownloadTask inner class - showFile method starting ");
		pdfFile = new File(file);
		// file=Environment.getExternalStorageDirectory().toString()+"/"+"downloadedfile.pdf";
		try {
			if(pdfFile.exists()) {
//				pdf.fromFile(pdfFile).defaultPage(1).showMinimap(false).enableSwipe(true).load();
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
        Intent intent = new Intent(this, MuPDFActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        intent.setAction(Intent.ACTION_VIEW);
        intent.setData(uri);
        this.startActivity(intent);
        loadPDf=false;
	}
//	@Override
//	protected void onDestroy() {
//		// Clear the cache (this clears the WebViews cache for the entire
//		// application)
//		// webView.clearCache(false);
//
//		super.onDestroy();
//	}

	public void showDashboardHome(View view) {

			finish();

	}

	public ControlApplication getApp() {
		return (ControlApplication) this.getApplication();
	}

	@Override
	public void onUserInteraction() {
		super.onUserInteraction();

		getApp().touch();
		String TAG = "";
		Log.d(TAG, "User interaction to " + this.toString());
	}
	
	 @Override
	    protected void onDestroy() {
	    	// TODO Auto-generated method stub
	    	super.onDestroy();
	    	System.gc();
	    }

	  @Override
	protected void onResume() {
		super.onResume();
	
	}

}
