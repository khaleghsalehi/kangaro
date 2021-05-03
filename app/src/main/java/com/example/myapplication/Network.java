package com.example.myapplication;

import android.util.Log;

import java.io.File;
import java.io.IOException;

import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class Network {
    private static final String TAG = "kangaro";

    public static void ping(OkHttpClient okHttpClient, String url) throws IOException {

        Log.i(TAG, "pinging server...");
        Request request = new Request.Builder()
                .url(url)
                .addHeader("custom-key", "1234567890")  // add request headers
                .build();

        try (Response response = okHttpClient.newCall(request).execute()) {

            if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);

            Headers responseHeaders = response.headers();
            for (int i = 0; i < responseHeaders.size(); i++) {
             //   Log.e(TAG, responseHeaders.name(i) + ": " + responseHeaders.value(i));
            }
            // if can not ping server then do something... e.g store file in local storage...

        }
    }

    public static String uploadServer(OkHttpClient okHttpClient, String imagePath, String url) throws IOException {
        //todo send client token, authentication, etc.
        Log.i(TAG, "uploading to server " + imagePath);
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
        Log.e(TAG, "upload result:  " + response.code());
        return String.valueOf(response.code());

    }


}
