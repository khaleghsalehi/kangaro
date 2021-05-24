package com.example.batsapp;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Scanner;

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


        String ret = "empty";
        try {
            Log.d(TAG, "AUTH_KEY_FILE_NAME path " + AUTH_KEY_FILE_NAME);
            File authKeyFile = new File(AUTH_KEY_FILE_NAME);
            Scanner fileReader = new Scanner(authKeyFile);
            ret = Crypto.decrypt(fileReader.nextLine());
            Log.d(TAG, "read authKey from file " + ret);
        } catch (FileNotFoundException e) {
            Log.d(TAG, "Error, authKey not found");
        }
        return ret;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static void writeAuthKey(String data) {
        try {
            Log.d(TAG, "AUTH_KEY_FILE_NAME path " + AUTH_KEY_FILE_NAME);
            File file = new File(AUTH_KEY_FILE_NAME);
            FileWriter fileWriter = new FileWriter(AUTH_KEY_FILE_NAME);
            if (!file.exists()) {
                Log.d(TAG, "authKey created");
                if (file.createNewFile()) {
                    fileWriter.write(data);
                }
            } else {
                fileWriter.write(Crypto.encrypt(data));
            }
            Log.d(TAG, "authKey write");
            fileWriter.close();

        } catch (IOException e) {
            Log.d(TAG, "Oops! authKey write failed: ");
            e.printStackTrace();
        }
    }


    public static String getHash(String string) throws UnsupportedEncodingException, NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance("SHA-1");
        digest.reset();
        digest.update(string.getBytes("utf8"));
        return String.format("%040x", new BigInteger(1, digest.digest()));
    }
}
