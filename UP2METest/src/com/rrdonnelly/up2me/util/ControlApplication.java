package com.rrdonnelly.up2me.util;

import com.rrdonnelly.up2me.dao.DatabaseHandler;
import com.rrdonnelly.up2me.dao.UserSettingsDAO;

import android.app.Application;
import android.util.Log;

public class ControlApplication extends Application
{
    private static final String TAG=ControlApplication.class.getName();
    private Waiter waiter;  //Thread which controls idle time

    // only lazy initializations here!
    @Override
    public void onCreate()
    {
        super.onCreate();
        
       
    }

    public void touch()
    {
    	if(waiter != null){
        waiter.touch();
    	}
    }
    
    public void start(long timeout){
    	 Log.d(TAG, "Starting application"+this.toString());
    	 if(timeout > 0){
    		 
         waiter=new Waiter(timeout*60*1000,getApplicationContext()); //15 mins
         waiter.start();
    	 }
    }
    
    public void setPeriod(long timeout){
    	if(timeout > 0){
    	if(waiter != null && waiter.isAlive()){
    	waiter.setPeriod(timeout*60*1000);
    	}else{
    		 waiter=new Waiter(timeout*60*1000,getApplicationContext()); //15 mins
             waiter.start();
    	}
    	}else{
    		if(waiter != null && waiter.isAlive()){
    	    	waiter.stopThread();
    	    	}
    	}
    }
    
    
}