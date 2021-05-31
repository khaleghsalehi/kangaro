package com.example.batsapp;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.example.batsapp.device.AppConfig;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Utils {
    private static final String AUTH_KEY_FILE_NAME = MainActivity.authKeyPath + "/" + "authKey.data";
    private static final String TAG = "batsapp";

    public static String getFileChecksum(MessageDigest digest, File file) throws IOException {
        FileInputStream fis = new FileInputStream(file);
        byte[] byteArray = new byte[1024];
        int bytesCount = 0;
        while ((bytesCount = fis.read(byteArray)) != -1) {
            digest.update(byteArray, 0, bytesCount);
        }
        fis.close();
        byte[] bytes = digest.digest();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < bytes.length; i++) {
            sb.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
        }
        return sb.toString();
    }

    public static void clearAuthKey(Context context) throws IOException {
        Log.d(TAG, "AUTH_KEY_FILE_NAME path " + AUTH_KEY_FILE_NAME);
        File file = new File(AUTH_KEY_FILE_NAME);
        if (file.delete()) {
            Log.d(TAG, "clear authKey and reload app");
            Intent mStartActivity = new Intent(context, MainActivity.class);
            int mPendingIntentId = 123456;
            PendingIntent mPendingIntent = PendingIntent.getActivity(context, mPendingIntentId, mStartActivity, PendingIntent.FLAG_CANCEL_CURRENT);
            AlarmManager mgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 100, mPendingIntent);
            System.exit(0);

        } else {
            Log.d(TAG, "clear authKey error.");

        }

    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    public static String readAuthKey() {
        AppConfig appConfig = readAppConfig();
        return Crypto.decrypt(appConfig.getAuthKey());
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static void writeAuthKey(String data) {
        AppConfig appconfig = readAppConfig();
        appconfig.setMode(appconfig.getMode());
        appconfig.setAuthKey(Crypto.encrypt(data));
        writeAppConfig(appconfig);
    }


    public static String getHash(String string) throws UnsupportedEncodingException, NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance("SHA-1");
        digest.reset();
        digest.update(string.getBytes("utf8"));
        return String.format("%040x", new BigInteger(1, digest.digest()));
    }

    public static void resetBatsapp(Context context) {
        Log.d(TAG, "reload batsapp...");
        Intent mStartActivity = new Intent(context, MainActivity.class);
        int mPendingIntentId = 123456;
        PendingIntent mPendingIntent = PendingIntent.getActivity(context, mPendingIntentId, mStartActivity, PendingIntent.FLAG_CANCEL_CURRENT);
        AlarmManager mgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 100, mPendingIntent);
        System.exit(0);
    }


    public static AppConfig readAppConfig() {
        String configAddress = MainActivity.authKeyPath + "/" + "app.cfg";
        try {
            File file = new File(configAddress);
            if (!file.exists())
                if (file.createNewFile())
                    Log.d(TAG, "appConfig not found, initialized...");
            FileInputStream f = new FileInputStream(file);
            ObjectInputStream oi = new ObjectInputStream(f);

            try {
                return (AppConfig) oi.readObject();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }

        } catch (FileNotFoundException e) {
            Log.d(TAG, "File not found");


        } catch (IOException e) {
            e.printStackTrace();
        }
        return new AppConfig();
    }

    public static void writeAppConfig(AppConfig appConfig) {
        String configAddress = MainActivity.authKeyPath + "/" + "app.cfg";
        try {
            File file = new File(configAddress);
            if (!file.exists())
                if (file.createNewFile())
                    Log.d(TAG, "config file initialized.");
            FileOutputStream f = new FileOutputStream(file);
            ObjectOutputStream o = new ObjectOutputStream(f);

            // Write objects to file
            o.writeObject(appConfig);

            o.close();
            f.close();

        } catch (FileNotFoundException e) {
            Log.d(TAG, "File not found");
        } catch (IOException e) {
            Log.d(TAG, "Error initializing stream");
        }
    }


}
