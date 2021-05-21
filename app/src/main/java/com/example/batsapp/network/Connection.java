package com.example.batsapp.network;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class Connection {
    public static boolean isConnected(ConnectivityManager conMgr) {
        NetworkInfo netInfo = conMgr.getActiveNetworkInfo();
            return netInfo != null;
    }
}
