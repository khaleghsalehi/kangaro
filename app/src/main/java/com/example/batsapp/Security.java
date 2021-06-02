package com.example.batsapp;

import android.util.Log;

import java.util.Random;

public class Security {
    private static final Random r = new Random();
    private static final String TAG = "batsapp";

    static {
        System.loadLibrary("batsapp");
    }

    private static native String getk();

    private static native String getAESKey();

    private static native String getSalt();

    public static String getTokenFromNative() {

        return getk();
    }

    public static String getAESFromNative() {
        return getAESKey();
    }

    public static String getSaltFromNative() {
        return getSalt();
    }


    public static String getToken() {
        String tk = getTokenFromNative();
        Log.d(TAG, "tk =" + tk);
        return tk + System.currentTimeMillis();
    }


    public static String getVersion() {
        return MainActivity.APP_VERSION;
    }
}
