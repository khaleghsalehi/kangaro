package com.example.batsapp;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

public class RestartService extends BroadcastReceiver {
    private static final String TAG = "batsapp";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "Service tried to stop");


        int resultCode = intent.getIntExtra("resultCode", Activity.RESULT_CANCELED);
        Intent result_data = intent.getParcelableExtra("DATA");

        Log.d(TAG, String.valueOf(resultCode));


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(WatchDog.getStartIntent(context, resultCode, result_data));

        } else {
            context.startService(WatchDog.getStartIntent(context, resultCode, result_data));
        }
    }


}