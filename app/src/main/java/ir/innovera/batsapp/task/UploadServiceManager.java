package ir.innovera.batsapp.task;

import android.os.Build;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.TimerTask;

import ir.innovera.batsapp.MainActivity;
import ir.innovera.batsapp.Utilities;

public class UploadServiceManager extends TimerTask {
    private final static String UPLOAD_URL = MainActivity.SERVER_URL;
    private static final String TAG = "batsapp";

    @Override
    public void run() {
        if (MainActivity.config.getCommand().equals(MainActivity.batsAppStartCode())) {
            if (!MainActivity.filesPath.equals("empty")) { // yup upload it
                Log.d(TAG, "checking and uploading new files.");
                String rootPath = MainActivity.filesPath;
                int counter = 0;
                String[] pathnames;
                File f = new File(rootPath);
                pathnames = f.list();
                for (String fileName : pathnames != null ? pathnames : new String[0]) {
                    if (fileName.startsWith(MainActivity.PREFIX_FILE_NAME) && fileName.contains(".jpg")) {
                        counter++;
                        Log.d(TAG, "file in root path  " + fileName);
                        try {
                            if (MainActivity.authKey != null && MainActivity.authKey != "empty") {

                                String AUTH_INFO = "?uuid=" + MainActivity.authKey;

                                File file = new File(rootPath + fileName);
                                MessageDigest md5Digest = MessageDigest.getInstance("MD5");
                                String checksum = Utilities.getFileChecksum(md5Digest, file);
                                if (!MainActivity.screenshotList.containsKey(checksum)) {
                                    String response = Network.uploadServer(rootPath, fileName, UPLOAD_URL + AUTH_INFO);
                                    if (response.contains("200")) {
                                        int mapSize = MainActivity.screenshotList.size();
                                        if (mapSize > MainActivity.FILE_COUNT_MAX) {
                                            // pure device safety, reset hashmap and init it
                                            Log.d(TAG, " hashmap reached and reset for size of " + mapSize);
                                            MainActivity.screenshotList.clear();
                                        }
                                        MainActivity.screenshotList.put(checksum, rootPath + fileName);
                                        Log.d(TAG, "file added to hashmap, upload result " + response);
                                    }
                                } else {
                                    Log.d(TAG, "duplicated screenshot," + rootPath + fileName + "checksum " + checksum);
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                        boolean result = Files.deleteIfExists(Paths.get(rootPath + fileName));
                                        if (result)
                                            Log.d(TAG, "duplicated removed from device");
                                        else
                                            Log.d(TAG, "removed duplicated file error!");

                                    }
                                }
                            } else {
                                Log.d(TAG, "authKey is null or empty!");
                            }
                        } catch (IOException | NoSuchAlgorithmException e) {
                            e.printStackTrace();
                        }
                    }
                }
                if (counter > 0) {
                    Log.d(TAG, counter + " file successfully send to server");
                } else {
                    Log.d(TAG, " everything is updated");
                }
            } else { // Ooopssss, files path not found...
                Log.d(TAG, "file path not fond");
            }
        } else {
            Log.d(TAG, "stop command detected.");
        }

    }
}
