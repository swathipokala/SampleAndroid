package com.rrdonnelly.up2me.util;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.rrdonnelly.up2me.LoginActivity;

public class Waiter extends Thread
{
    private static final String TAG=Waiter.class.getName();
    private long lastUsed;
    private long period;
    private boolean stop;
    private Context context;

    public Waiter(long period,Context context)
    {
        this.period=period;
        this.context=context;
        stop=false;
    }

    public void run()
    {
        long idle=0;
        this.touch();
        do
        {
            idle=System.currentTimeMillis()-lastUsed;
            Log.d(TAG, "Application is idle for "+idle +" ms");
            try
            {
                Thread.sleep(5000); //check every 5 seconds
            }
            catch (InterruptedException e)
            {
                Log.d(TAG, "Waiter interrupted!");
            }
            if(idle > period)
            {
                idle=0;
                Intent loginIntent = new Intent(context, LoginActivity.class);
                loginIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                loginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                context.startActivity(loginIntent);
                stop = true;
            }
        }
        while(!stop);
        Log.d(TAG, "Finishing Waiter thread");
    }

    public synchronized void touch()
    {
        lastUsed=System.currentTimeMillis();
    }

    public synchronized void forceInterrupt()
    {
        this.interrupt();
    }

    //soft stopping of thread
//    public synchronized void stop()
//    {
//        stop=true;
//    }

    public synchronized void setPeriod(long period)
    {
        this.period=period;
    }
    
    public synchronized void stopThread(){
    	stop = true;
    }

}