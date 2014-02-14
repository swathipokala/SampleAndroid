package com.rrdonnelly.up2me;

import com.rrdonnelly.up2me.util.ControlApplication;

import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class BillPayActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_bill_pay);
		String URL = getIntent().getStringExtra("url");
		WebView webView=(WebView) findViewById(R.id.billPayWebView);
		

		try{
			webView.setVisibility(View.VISIBLE);
			webView.setWebViewClient(new WebViewClient() {
	               @Override
	               public boolean shouldOverrideUrlLoading(WebView view, 
	String url)
	               {
	                 view.loadUrl(url);
	                 return true;
	               }
	             });
			WebSettings webSettings = webView.getSettings();
			webSettings.setJavaScriptCanOpenWindowsAutomatically(false);
			webSettings.setJavaScriptEnabled(true);
	           //setContentView(webview);
			webView.loadUrl(URL);
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.bill_pay, menu);
		return true;
	}
	
	public void doneView(View v){
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

}
