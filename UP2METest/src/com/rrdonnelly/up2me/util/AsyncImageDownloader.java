package com.rrdonnelly.up2me.util;

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
import com.rrdonnelly.up2me.util.ImageUtil;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by rahul.k on 12/2/13.
 */
public class AsyncImageDownloader extends AsyncTask<ArrayList<String>, Void, String> {
    //private final WeakReference<ImageView> imageViewReference;
    private Context context;
    public AsyncImageDownloader(Context context) {
        //imageViewReference = new WeakReference<ImageView>(image);
        this.context=context;
    }

    @Override
    // Actual download method, run in the task thread
    protected String doInBackground(ArrayList<String>... params) {
        Log.v("AsyncImageDownloader", "doInBackground :" + params[0]);
        // params comes from the execute() call: params[0] is the url.
        List<String> downloadURLs=params[0];
       for(int i = 0; i < downloadURLs.size(); i++){
    	   String downloadUrl = downloadURLs.get(i);
    	   System.out.println("Downloaded Image URL started: "+ downloadUrl);
           //ImageUtil.downloadImageIfNotExists(context, downloadUrl);
           
           String imagePathfileName = downloadUrl.substring(downloadUrl.lastIndexOf("/")+1);
           ImageUtil.downloadImage(context, downloadUrl,
   				imagePathfileName);
           
           System.out.println("Downloaded Image URL ended: "+ downloadUrl);
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
