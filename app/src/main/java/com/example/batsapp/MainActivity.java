package com.example.batsapp;

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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static com.example.batsapp.Utils.getRandomString;

public class MainActivity extends Activity {
    private static final int REQUEST_CODE = 100;
    private static final String TAG = "batsapp";
    private static int result_code = 0;
    private static Intent result_data;
    public static final String PREFIX_FILE_NAME = "Screen_";
    public static final String PREFIX_PROCESSED_FILE_NAME = "Processed_";
    public static final String SERVER_URL = "http://192.168.43.81:9000";
    public static final String PING_URL = "http://192.168.43.81:9000/v1/cmd";
    public static String userName = "";
    public static String password = "";
    public static boolean authKeyStatus = false;
    public static boolean isRunning = false;
    public static String authKey = "";
    public static String COMMAND = "init";


    public void clearCache(View view) throws IOException {
        Utils.clearAuthKey(getApplicationContext());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.e(TAG, "starting batsapp...");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //fixme get file path inside method and change strategy...
        BackgroundService.filesPath = "empty";

        // server pinger
        Timer timerPing = new Timer();
        PingServiceManager serviceMaster = new PingServiceManager();
        timerPing.schedule(serviceMaster, 0, 5_000);

        // check and upload
        Timer timerUpload = new Timer();
        UploadServiceManager uploadServiceManager = new UploadServiceManager();
        timerUpload.schedule(uploadServiceManager, 0, 10_000);


        runOnUiThread(new Runnable() {

            @Override
            public void run() {
                String authKey = Utils.readAuthKey(getApplicationContext());
                Log.d(TAG, "extracted  authKey  " + authKey);

                if (authKey != null) {
                    List<String> creditList = Arrays.asList(authKey.split("\\|"));

                    if (creditList.size() > 2) {
                        authKeyStatus = true;
                        userName = creditList.get(0);
                        password = creditList.get(1);
                        Log.d(TAG, "extracted  credentials -> " + userName + " == " + password);
                        getQrCode(getWindow().getDecorView().findViewById(android.R.id.content));
                    }
                }


                int numThreads = 1;
                ExecutorService executor = Executors.newFixedThreadPool(numThreads);

                Runnable backgroundTask = new Runnable() {

                    @Override
                    public void run() {
                        while (true) {
                            if (COMMAND.equals("start")) {
                                if (!isRunning) {
                                    Log.d(TAG, "get START command");
                                    startRecording();
                                } else {
                                    Log.d(TAG, "START command already executed.");
                                }
                            } else if (COMMAND.equals("stop")) {
                                if (isRunning) {
                                    Log.d(TAG, "get STOP command");
                                    stopRecording();
                                } else {
                                    Log.d(TAG, "get STOP command but nothing to stop.");
                                }
                            } else {
                                Log.d(TAG, "get " + COMMAND + " command");

                            }
                            try {
                                Log.d(TAG, "sleep for 60 second");
                                Thread.sleep(60_000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }

                        }
                    }

                };
                executor.execute(backgroundTask);
                executor.shutdown();


            }
        });
    }


    public void getQrCode(View view) {
        String authKey;
        String delimiter = "|";
        if (authKeyStatus) {
            Log.d(TAG, " user already logged via  username: " + userName + " password: " + password);
        } else {
            Log.d(TAG, " user not loggedIn.");
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
            Log.e(TAG, "invalid or empty username and password");
        }


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {

                result_code = resultCode;
                result_data = data;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    startForegroundService(com.example.batsapp.BackgroundService.getStartIntent(this, resultCode, data));

                } else {
                    startService(com.example.batsapp.BackgroundService.getStartIntent(this, resultCode, data));

                }
                goBackground();
            }
        }
    }


    public void startProjection(View v) {
        if (isRunning) {
            Log.d(TAG, "service already recording screen...");
            return;
        }
        isRunning = true;
        MediaProjectionManager mProjectionManager =
                (MediaProjectionManager) getSystemService(Context.MEDIA_PROJECTION_SERVICE);
        startActivityForResult(mProjectionManager.createScreenCaptureIntent(), REQUEST_CODE);


    }

    public void startRecording() {
        if (isRunning) {
            Log.d(TAG, "service already recording screen...");
        } else {
            isRunning = true;
            Log.d(TAG, "start recording inside other method.....");
            MediaProjectionManager mProjectionManager =
                    (MediaProjectionManager) getSystemService(Context.MEDIA_PROJECTION_SERVICE);
            startActivityForResult(mProjectionManager.createScreenCaptureIntent(), REQUEST_CODE);
        }


    }

    public void stopRecording() {
        if (!isRunning) {
            Log.d(TAG, "return , there is not active running");
            return;
        }
        isRunning = false;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(com.example.batsapp.BackgroundService.getStopIntent(this));

        } else {
            startService(com.example.batsapp.BackgroundService.getStopIntent(this));

        }

    }

    public void stopProjection(View v) {
        if (!isRunning) {
            Log.d(TAG, "return , there is not active running");
            return;
        }
        isRunning = false;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(com.example.batsapp.BackgroundService.getStopIntent(this));

        } else {
            startService(com.example.batsapp.BackgroundService.getStopIntent(this));

        }
    }


    @Override
    protected void onDestroy() {
        //stopService(mServiceIntent);

//fixme why I disabled below line and it's working well?


//        Intent broadcastIntent = new Intent();
//        broadcastIntent.putExtra("resultCode", result_code);
//        broadcastIntent.putExtras(result_data);
//        broadcastIntent.setAction("restartservice");
//        broadcastIntent.setClass(this, RestartService.class);
//        this.sendBroadcast(broadcastIntent);
        super.onDestroy();
    }

    protected void goBackground() {
        Intent i = new Intent();
        i.setAction(Intent.ACTION_MAIN);
        i.addCategory(Intent.CATEGORY_HOME);
        this.startActivity(i);
    }


}