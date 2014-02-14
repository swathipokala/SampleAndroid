package com.rrdonnelly.up2me.util.tags;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.net.http.AndroidHttpClient;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.rrdonnelly.up2me.R;
import com.rrdonnelly.up2me.services.TagService;
import com.rrdonnelly.up2me.util.AsyncImageDownloader;
import com.rrdonnelly.up2me.util.ImageUtil;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.json.JSONException;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.net.URL;
import java.net.URLConnection;

/**
 * Created by rahul.k on 12/2/13.
 */
public class AsyncTagSyncToBackend extends AsyncTask<String, Void, String> {
    //private final WeakReference<ImageView> imageViewReference;
    private Context activity;
    private boolean syncUserTags = false;
    private boolean syncUserStatementTags = false;
    private boolean syncUserOfferTags = false;
    
    public AsyncTagSyncToBackend(Context activity, boolean syncUserTags, boolean syncUserStatementTags, boolean syncUserOfferTags) {
        //imageViewReference = new WeakReference<ImageView>(image);
        this.activity=activity;
        this.syncUserTags = syncUserTags;
        this.syncUserStatementTags = syncUserStatementTags;
        this.syncUserOfferTags = syncUserOfferTags;
    }

    @Override
    // Actual download method, run in the task thread
    protected String doInBackground(String... params) {
        Log.v("AsyncTagSyncToBackend", "doInBackground :" + params[0]);
        // params comes from the execute() call: params[0] is the url.
        
        String loginUserId = params[0];
        System.out.println("AsyncTagSyncToBackend started: "+ loginUserId);
        
        try {
			if(syncUserTags){
				System.out.println("SyncTagsToBackend started: "+ loginUserId);
				TagService.syncTagsToBackend(activity, Long.parseLong(loginUserId));
				System.out.println("SyncTagsToBackend ended: "+ loginUserId);
			}
			
			if(syncUserStatementTags){
				System.out.println("SyncUserStatementTags started: "+ loginUserId);
				TagService.syncUserStatementTagsToBackend(activity, Long.parseLong(loginUserId));
				System.out.println("SyncUserStatementTags ended: "+ loginUserId);
			}
			
			if(syncUserOfferTags){
				System.out.println("SyncUserOfferTags started: "+ loginUserId);
				TagService.syncUserOfferTagsToBackend(activity, Long.parseLong(loginUserId));
				System.out.println("SyncUserOfferTags started: "+ loginUserId);
			}
        	
			System.out.println("AsyncTagSyncToBackend ended: "+ loginUserId);
			
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
        return null;
        //return ImageUtil.downloadImage(context, downloadUrl, fileName);
        
        //return downloadBitmap(params[0],params[1]);
    }

    @Override
    // Once the image is downloaded, associates it to the imageView
    protected void onPostExecute(String imagePath) {
        /*if (isCancelled()) {
        	imagePath = null;
        }

        if (imageViewReference != null) {
            ImageView image = imageViewReference.get();
            if (image != null) {
                if (imagePath != null) {
                	File pdfCoverPageDownloadPath = new File(imagePath);
                	image.setImageURI(Uri.fromFile(pdfCoverPageDownloadPath));  
                } else {
                    image.setImageDrawable(image.getContext().getResources().getDrawable(R.drawable.list_placeholder));
                }
            }

        }*/
    }
}
