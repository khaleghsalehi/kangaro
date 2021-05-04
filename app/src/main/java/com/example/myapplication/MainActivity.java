package com.example.myapplication;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.projection.MediaProjectionManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.google.zxing.BarcodeFormat;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Timer;

import static com.example.myapplication.Utils.getRandomString;

public class MainActivity extends Activity {
    private static final int REQUEST_CODE = 100;
    private static final String TAG = "kangaro";
    private static int result_code = 0;
    private static Intent result_data;
    public static final String PREFIX_FILE_NAME = "Screen_";
    public static final String PREFIX_PROCESSED_FILE_NAME = "Processed_";
    public static final String SERVER_URL = "http://192.168.43.81:9000";
    public static String userName = "";
    public static String password = "";
    public static boolean authKeyStatus = false;

    public void clearCache(View view) throws IOException {
        Utils.clearAuthKey(getApplicationContext());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.e(TAG, "starting kangaro...");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //fixme get file path inside method and change strategy...
        BackgroundService.filesPath = "empty";

        Timer timerPing = new Timer();
        PingServiceManager serviceMaster = new PingServiceManager();
        timerPing.schedule(serviceMaster, 0, 5000);

        Timer timerUpload = new Timer();
        UploadServiceManager uploadServiceManager = new UploadServiceManager();
        timerUpload.schedule(uploadServiceManager, 0, 10_000);

        runOnUiThread(new Runnable() {

            @Override
            public void run() {
                String tempAuthKey = Utils.readAuthKey(getApplicationContext());
                Log.e(TAG, "extracted  authKey -> " + tempAuthKey);

                if (tempAuthKey != null) {
                    List<String> creditList = Arrays.asList(tempAuthKey.split("\\|"));

                    if (creditList.size() > 2) {
                        authKeyStatus = true;
                        userName = creditList.get(0);
                        password = creditList.get(1);
                        Log.e(TAG, "extracted  credentials -> " + userName + " == " + password);
                        getQrCode(getWindow().getDecorView().findViewById(android.R.id.content));
                    }
                }

            }
        });

    }


    public void getQrCode(View view) {
        String authKey;
        String delimiter = "|";
        if (authKeyStatus) {
            Log.e(TAG, " user already logged via " + userName + " == " + password);
        } else {
            Log.e(TAG, " user not logged in");
            EditText u = findViewById(R.id.username);
            EditText p = findViewById(R.id.password);
            userName = u.getText().toString();
            password = p.getText().toString();
            u.setText("");
            p.setText("");
            authKey = userName + delimiter + password + delimiter + getRandomString(15); // server side all string - last 15 char
            Utils.writeAuthKey(authKey, getApplicationContext());
        }

        if (userName.length() > 0 && password.length() > 0) {
            // authentication
            // if authentication, then store authkey in phone
            authKeyStatus = true;

            authKey = userName + delimiter + password + delimiter + getRandomString(15); // server side all string - last 15 char

            try {
                BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
                Bitmap bitmap = barcodeEncoder.encodeBitmap(authKey, BarcodeFormat.QR_CODE, 400, 400);
                ImageView imageViewQrCode = (ImageView) findViewById(R.id.qrimage);
                imageViewQrCode.setImageBitmap(bitmap);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            Log.e(TAG, "Please enter username and password");
        }


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {

                result_code = resultCode;
                result_data = data;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    startForegroundService(com.example.myapplication.BackgroundService.getStartIntent(this, resultCode, data));

                } else {
                    startService(com.example.myapplication.BackgroundService.getStartIntent(this, resultCode, data));

                }


                // send app to background after run app

                Intent i = new Intent();
                i.setAction(Intent.ACTION_MAIN);
                i.addCategory(Intent.CATEGORY_HOME);
                this.startActivity(i);


            }
        }
    }

    public void startProjection(View v) {
        MediaProjectionManager mProjectionManager =
                (MediaProjectionManager) getSystemService(Context.MEDIA_PROJECTION_SERVICE);
        startActivityForResult(mProjectionManager.createScreenCaptureIntent(), REQUEST_CODE);


    }


    public void stopProjection(View v) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(com.example.myapplication.BackgroundService.getStopIntent(this));

        } else {
            startService(com.example.myapplication.BackgroundService.getStopIntent(this));

        }
    }


    @Override
    protected void onDestroy() {
        //stopService(mServiceIntent);


//        Intent broadcastIntent = new Intent();
//        broadcastIntent.putExtra("resultCode", result_code);
//        broadcastIntent.putExtras(result_data);
//        broadcastIntent.setAction("restartservice");
//        broadcastIntent.setClass(this, RestartService.class);
//        this.sendBroadcast(broadcastIntent);
        super.onDestroy();
    }


}