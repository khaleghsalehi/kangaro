package ir.innovera.batsapp.network;

import android.util.Log;

import ir.innovera.batsapp.MainActivity;

import java.util.TimerTask;


public class ConnectionManager extends TimerTask {
    private static final String TAG = "batsapp";


    @Override
    public void run() {
        if (Connection.isConnected(MainActivity.connectionManager)) {
            MainActivity.isInternetActive = true;
            Log.d(TAG, "= device internet active =");
        } else {
            MainActivity.isInternetActive = false;
            Log.d(TAG, "= device internet is off =");
        }

    }
}
