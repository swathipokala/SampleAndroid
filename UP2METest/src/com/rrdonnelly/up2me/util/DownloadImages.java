package com.rrdonnelly.up2me.util;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import org.apache.http.util.ByteArrayBuffer;
import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.widget.ImageView;

public class DownloadImages extends AsyncTask<Object, Object, Object>  {

	Context context;
	public DownloadImages(Activity currentActivity) {
		// TODO Auto-generated constructor stub
		context = currentActivity;
	}

	@Override
	protected Object doInBackground(Object... params) {
		// TODO Auto-generated method stub
		
		String imageUrl = params[0].toString();
		ImageView imgView = (ImageView) params[1];
		
		download(imageUrl, imgView);
		
		return null;
	}

	/**
	 * @param imageUrl
	 * @param imgView
	 * @return
	 */
	private String download(String imageUrl, ImageView imgView) {
		
		String filePath=null;
        try {

        	String displayImagePathSmallfileName = imageUrl.substring(imageUrl.lastIndexOf("/") + 1);
        	/*String webServiceUrl = context.getResources().getString(R.string.webservice_url);
        	String displayImagePathSmall = webServiceUrl + "/Images/" + imageUrl;*/
        	
            URL url = new URL(imageUrl);
            File file = new File(context.getApplicationContext().getFilesDir(), displayImagePathSmallfileName);

            URLConnection uconn = url.openConnection();

            InputStream is = uconn.getInputStream();
            BufferedInputStream bufferinstream = new BufferedInputStream(is);

            ByteArrayBuffer baf = new ByteArrayBuffer(5000);
            int current = 0;
            while((current = bufferinstream.read()) != -1){
                baf.append((byte) current);
            }

            FileOutputStream fos = new FileOutputStream( file);
            fos.write(baf.toByteArray());
            fos.flush();
            fos.close();
            
            filePath=file.getAbsolutePath();
            
            imgView.setImageURI(Uri.fromFile(file));

            } catch(Exception e) {
            	e.printStackTrace();
            }
            return filePath;
        }
}
