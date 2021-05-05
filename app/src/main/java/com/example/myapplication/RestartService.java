package com.example.myapplication;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

public class RestartService extends BroadcastReceiver {
    private static final String TAG ="kangaro" ;

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i(TAG, "Service tried to stop");


        int resultCode = intent.getIntExtra("resultCode", Activity.RESULT_CANCELED);
        Intent result_data = intent.getParcelableExtra("DATA");

        Log.e(TAG, String.valueOf(resultCode));


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(com.example.myapplication.BackgroundService.getStartIntent(context, resultCode, result_data));

        } else {
            context.startService(com.example.myapplication.BackgroundService.getStartIntent(context, resultCode, result_data));
        }
    }


}