package com.example.batsapp;

import android.util.Log;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.Objects;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


public class Security {
    private static final String TAG = "batsapp";

    //todo
    public static String getToken() {
        return "123456";
    }


    // authentication and store key
    public static String getAutokey(String userName, String password) {
        String url = MainActivity.REST_AUTH_URL + "?username=" + userName + "&password=" + password;
        final String[] result = {""};
        Request request = new Request.Builder()
                .url(url)
                .build();
        OkHttpClient client = new OkHttpClient();
        client.newCall(request).enqueue(new Callback() {

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                result[0] = Objects.requireNonNull(response.body()).string();
                Log.i(TAG, "get authKey from server -> " + result[0]);
            }

            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {

            }

        });
        return result[0];

    }
}
