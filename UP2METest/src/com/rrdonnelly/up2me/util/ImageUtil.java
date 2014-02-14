package com.rrdonnelly.up2me.util;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.net.URL;
import java.net.URLConnection;

import org.apache.http.util.ByteArrayBuffer;

import android.app.Activity;
import android.content.Context;
import android.os.Environment;
import android.util.Log;

public class ImageUtil {
	
	public static void downloadImageIfNotExists(Context activity, String imageURLPath){
		
		long startTime = System.currentTimeMillis();
		String imagePathfileName = "";
		
		
		if(imageURLPath.indexOf("/")!=-1){
			imagePathfileName = imageURLPath
					.substring(imageURLPath.lastIndexOf("/") + 1);
		}
		
		if(imageURLPath.indexOf("/")!=-1){
			imagePathfileName = imageURLPath.substring(imageURLPath.lastIndexOf("/")+1);
		}
			
			
		
		File fileImagePath = new File(activity.getFilesDir(),
				imagePathfileName);
		
		if (!fileImagePath.exists()) {
			//Log.w("OffersService", "Calling Download Image for file :"
					//+ imagePathfileName);
			downloadImage(activity, imageURLPath,
					imagePathfileName);
			
		}
		
		fileImagePath = null;
		imagePathfileName = null;
	}
	
	
	public static boolean isFileExists(Context activity, String imageURLPath){
		
		String imagePathfileName = "";
		
		
		if(imageURLPath.indexOf("/")!=-1){
			imagePathfileName = imageURLPath
					.substring(imageURLPath.lastIndexOf("/") + 1);
		}
		
		File fileImagePath = new File(activity.getFilesDir(),
				imagePathfileName);
		
		if (fileImagePath.exists()) {
			return true;
		} else{
			return false;
		}
	}
	
	
    public static String downloadImage(Context activity, String DownloadUrl, String fileName) {
        String filePath=null;
       try {
           //Log.d("DownloadImage" , "download url:" +DownloadUrl);
           //Log.d("DownloadImage" , "download file name:" + fileName);


    	   if(fileName.indexOf("/")!=-1){
    		   fileName = fileName
    		           .substring(fileName.lastIndexOf("/") + 1);
 
    	   }
    	   
           URL url = new URL(DownloadUrl.replaceAll(" ", "%20"));
           File file = new File(activity.getFilesDir(), fileName);

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
               Log.d("DownloadImage" , "Error  :" + e);
           }
        	   
           return filePath;
      }
    
    public static String downloadImagetoExternalPath(Activity activity, String DownloadUrl, String fileName) {
        String filePath=null;
       try {
           //Log.d("DownloadImage" , "download url:" +DownloadUrl);
           //Log.d("DownloadImage" , "download file name:" + fileName);
    	   String extStorageDirectory = Environment.getExternalStorageDirectory().toString();

   		File folder = new File(extStorageDirectory, "Document");
   		if (!folder.exists()) {
   			folder.mkdir();
   		}

    	   if(fileName.indexOf("/")!=-1){
    		   fileName = fileName
    		           .substring(fileName.lastIndexOf("/") + 1);
 
    	   }
    	   
           URL url = new URL(DownloadUrl);
           File file = new File(folder.getAbsolutePath(), fileName);

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
      }


}
