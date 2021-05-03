package com.example.myapplication;

import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.util.TimerTask;

import okhttp3.OkHttpClient;

public class UploadServiceManager extends TimerTask {
    private final static OkHttpClient uploadAgent = new OkHttpClient();
    private final static String UPLOAD_URL = MainActivity.SERVER_URL;
    private static final String TAG = "kangaro";

    @Override
    public void run() {
        if (!BackgroundService.filesPath.equals("empty")) { // yup upload it
            Log.e(TAG, "checking and uploading new files.");
            String rootPath = BackgroundService.filesPath;
            int counter = 0;
            String[] pathnames;
            File f = new File(rootPath);
            pathnames = f.list();
            for (String fileName : pathnames != null ? pathnames : new String[0]) {
                if (fileName.startsWith(MainActivity.PREFIX_FILE_NAME) && fileName.contains(".jpg")) {
                    counter++;
                    Log.e(TAG, "file in root path -> " + fileName);
                    try {
                        String res = Network.uploadServer(uploadAgent,
                                rootPath + fileName,
                                UPLOAD_URL);
                        if (res.equals("200")) {
                            File srcFile = new File(rootPath + fileName);
                            if (srcFile.renameTo(new File(rootPath + MainActivity.PREFIX_PROCESSED_FILE_NAME + fileName)))
                                Log.e(TAG, "rename done,");
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            if (counter > 0) {
                Log.e(TAG, counter + " file successfully send to server");
            } else {
                Log.e(TAG, " everything is updated");
            }
        } else { // Ooopssss, files path not found...
            Log.e(TAG, "file path not fond");
        }
    }
}
