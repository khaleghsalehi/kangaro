package com.example.batsapp;

import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class Network {
    private static final String TAG = "batsapp";

    public static void ping(OkHttpClient okHttpClient, String url) throws IOException {
        Log.d(TAG, "pinging server...");
        Request request = new Request.Builder()
                .url(url + "?uuid=" + MainActivity.authKey)
                .addHeader("authKey", MainActivity.authKey)
                .addHeader("custom-key", MainActivity.authKey)
                .build();

        try (Response response = okHttpClient.newCall(request).execute()) {

            if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
            String command = Objects.requireNonNull(response.body()).string();
            if (command.equals("start")) {
                MainActivity.COMMAND = "start";
            } else if (command.equals("stop")) {
                MainActivity.COMMAND = "stop";
            } else {
                MainActivity.COMMAND = "other";

            }

        } catch (Exception e) {
            Log.e(TAG, "exception in ping");
            MainActivity.COMMAND = "EXCEPTION";
        }
    }

    public static String uploadServer(OkHttpClient okHttpClient, String imagePath, String url) throws IOException {
        //todo send client token, authentication, etc.
        Log.d(TAG, "uploading to server " + imagePath);
        File file = new File(imagePath);
        RequestBody image = RequestBody.create(MediaType.parse("image/jpg"), file);
        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("file", file.getName(), image)
                .build();
        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .build();
        Response response = okHttpClient.newCall(request).execute();
        Log.d(TAG, "upload response code  " + response.code());
        return String.valueOf(response.code());

    }


}
