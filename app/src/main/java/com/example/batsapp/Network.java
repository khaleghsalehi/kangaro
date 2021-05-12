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

    public static String uploadServer(String imagePath, String fileName, String url) throws IOException {
        String rootPath = imagePath + "/" + fileName;
        Log.d(TAG, "uploading to server " + rootPath);
        File file = new File(rootPath);
        RequestBody image = RequestBody.create(MediaType.parse("image/jpg"), file);
        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("file", file.getName(), image)
                .build();
        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .build();
        OkHttpClient client = new OkHttpClient();
        Response response = client.newCall(request).execute();
        Log.d(TAG, "upload response code  " + response.code());
        if (response.code() == 200) {
            File srcFile = new File(rootPath);
            //todo file already uploaded to server, delete processed file
            if (srcFile.renameTo(new File(imagePath + MainActivity.PREFIX_PROCESSED_FILE_NAME + fileName)))
                Log.d(TAG, "rename success, change file status.");
        }
        return String.valueOf(response.code());

    }


}
