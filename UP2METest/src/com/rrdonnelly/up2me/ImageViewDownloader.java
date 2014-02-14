package com.rrdonnelly.up2me;

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

/**
 * Created by rahul.k on 12/2/13.
 */
public class ImageViewDownloader extends AsyncTask<String, Void, String> {
    private final WeakReference<ImageView> imageViewReference;
    private Context context;
    public ImageViewDownloader(ImageView image, Context context) {
        imageViewReference = new WeakReference<ImageView>(image);
        this.context=context;
    }

    @Override
    // Actual download method, run in the task thread
    protected String doInBackground(String... params) {
        Log.v("ImageViewDownloader", "doInBackground :" + params[0]);
        // params comes from the execute() call: params[0] is the url.
        
        String downloadUrl = params[0];
        String fileName = params[1];
        return ImageUtil.downloadImage(context, downloadUrl, fileName);
        
        //return downloadBitmap(params[0],params[1]);
    }

    @Override
    // Once the image is downloaded, associates it to the imageView
    protected void onPostExecute(String imagePath) {
        if (isCancelled()) {
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

        }
    }

    /*protected String downloadBitmap(String DownloadUrl,String fileName) {
    	 String filePath=null;
         try {
             //Log.d("DownloadImage" , "download url:" +DownloadUrl);
             //Log.d("DownloadImage" , "download file name:" + fileName);


      	   if(fileName.indexOf("imageName=")!=-1){
      		   fileName = fileName
      		           .substring(fileName.lastIndexOf("imageName=") + 1);
   
      	   }
      	   
             URL url = new URL(DownloadUrl);
             File file = new File(context.getFilesDir(), fileName);

             long startTime = System.currentTimeMillis();


             URLConnection uconn = url.openConnection();
             uconn.connect();

             InputStream is = uconn.getInputStream();
             
             FileOutputStream fos = new FileOutputStream( file);
             byte data[] = new byte[1024];
  		   long total = 0;
  		   int count = 0;
  		   while ((count = is.read(data)) != -1) {
  			   total += count;
  			   fos.write(data, 0, count);
  		   }
             
  		   fos.flush();
             fos.close();
             //Log.d("DownloadImage" , "download ready in" + ((System.currentTimeMillis() - startTime)/1000) + "sec");
             int dotindex = fileName.lastIndexOf('.');
             if(dotindex>=0){
                 fileName = fileName.substring(0,dotindex);

             }
             filePath=file.getAbsolutePath();
             //Log.d("DownloadImage" , "downloaded file path :"+filePath);
             uconn=null;
             //filePath=null;
             fos=null;
             is.close();
             is=null;
             file=null;
             url=null;
          		   
             } catch(Exception e) {
                 //Log.d("DownloadImage" , "Error:" + e);
             }
          	   
             return filePath;
        
    }*/

}
