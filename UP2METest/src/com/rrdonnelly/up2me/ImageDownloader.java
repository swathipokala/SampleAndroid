package com.rrdonnelly.up2me;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.http.AndroidHttpClient;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageButton;

import com.rrdonnelly.up2me.R;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;

import java.io.InputStream;
import java.lang.ref.WeakReference;

public class ImageDownloader extends AsyncTask<String, Void, Bitmap> {
    private final WeakReference imageViewReference;
    
    public ImageDownloader(Context context, ImageButton image) {
        imageViewReference = new WeakReference(image);
    }
    
    @Override
    // Actual download method, run in the task thread
    protected Bitmap doInBackground(String... params) {
        Log.v("ImageDownloader", "doInBackground :" + params[0]);
        // params comes from the execute() call: params[0] is the url.
        return downloadBitmap(params[0]);
    }

    @Override
    // Once the image is downloaded, associates it to the imageView
    protected void onPostExecute(Bitmap bitmap) {
        if (isCancelled()) {
            bitmap = null;
        }

        if (imageViewReference != null) {
            ImageButton image = (ImageButton) imageViewReference.get();
            if (image != null) {

                if (bitmap != null) {
                    image.setImageBitmap(bitmap);
                } else {
                    image.setImageDrawable(image.getContext().getResources().getDrawable(R.drawable.app_img1));
                }
            }

        }
    }

    static Bitmap downloadBitmap(String url) {
        Log.v("ImageDownloader", "downloadBitmap :" + url);
        final AndroidHttpClient client = AndroidHttpClient.newInstance("Android");
        final HttpGet getRequest = new HttpGet(url);
        try {
            HttpResponse response = client.execute(getRequest);
            final int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode != HttpStatus.SC_OK) {
                Log.w("ImageDownloader", "Error " + statusCode
                        + " while retrieving bitmap from " + url);
                return null;
            }

            final HttpEntity entity = response.getEntity();
            if (entity != null) {
                InputStream inputStream = null;
                try {
                    inputStream = entity.getContent();
                    final Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                    return bitmap;
                } finally {
                    if (inputStream != null) {
                        inputStream.close();
                    }
                    entity.consumeContent();
                }
            }
        } catch (Exception e) {
            // Could provide a more explicit error message for IOException or
            // IllegalStateException
            e.printStackTrace();
            getRequest.abort();
            Log.w("ImageDownloader", "Error while retrieving bitmap from " + url);
        } finally {
            if (client != null) {
                client.close();
            }
        }
        return null;
    }

}