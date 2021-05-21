package com.example.batsapp.network;

import android.util.Log;

import com.example.batsapp.MainActivity;

import java.util.TimerTask;


public class ConnectionManager extends TimerTask {
    private static final String TAG = "batsapp";


    @Override
    public void run() {
        if (Connection.isConnected(MainActivity.conMgr)) {
            MainActivity.isInternetActive = true;
            Log.d(TAG, "= device internet active =");
        } else {
            MainActivity.isInternetActive = false;
            Log.d(TAG, "= device internet is off =");
        }

    }
}
