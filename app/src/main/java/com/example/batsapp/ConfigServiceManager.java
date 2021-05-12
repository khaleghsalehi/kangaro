package com.example.batsapp;

import android.util.Log;

import org.json.JSONObject;

import java.io.IOException;
import java.util.Objects;
import java.util.TimerTask;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class ConfigServiceManager extends TimerTask {
    private static final String TAG = "batsapp";

    @Override
    public void run() {
        Log.i(TAG, "try to get new config...");
        MainActivity.config = getConfig();

    }

    private Config getConfig() {
        Config cfg = new Config();
        String configJson = null;
        OkHttpClient okHttpClient = new OkHttpClient();
        Request request = new Request.Builder()
                .url(MainActivity.GET_CONFIG_URL + "?uuid=" + MainActivity.authKey)
                .build();

        try (Response response = okHttpClient.newCall(request).execute()) {
            if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
            configJson = Objects.requireNonNull(response.body()).string();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            if (configJson != null) {
                JSONObject json = new JSONObject(configJson);

                int imageQuality = Integer.parseInt(json.getString("imageQuality"));
                int delay = Integer.parseInt(json.getString("screenShotDelay"));
                Log.d(TAG, " get config from server imageQuality " + imageQuality + " delay time " + delay*1000);
                cfg.setImageQuality(imageQuality);
                cfg.setScreenShotDelay(delay*1000);
                return cfg;
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "Ooopsss! exception while setting config, return defualt");
        }
        return cfg;
    }

}
