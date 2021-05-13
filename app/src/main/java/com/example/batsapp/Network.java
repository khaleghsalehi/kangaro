package com.example.batsapp;

import android.os.Build;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class Network {
    private static final String TAG = "batsapp";

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
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                if (Files.deleteIfExists(Paths.get(imagePath + fileName)))
                    Log.i(TAG, "uploaded file cleanup from device");
            } else {
                Log.i(TAG, "error while cleanup uploaded file ");
                String newName = imagePath + MainActivity.PREFIX_PROCESSED_FILE_NAME + fileName;
                if (srcFile.renameTo(new File(newName)))
                    Log.d(TAG, "uploaded file renamed success.");
                else
                    Log.e(TAG, "error while removing or renaming, == something is wrong ===");
            }
        }
        return String.valueOf(response.code());

    }


}
