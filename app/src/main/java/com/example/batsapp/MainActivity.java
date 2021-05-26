package com.example.batsapp;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.media.projection.MediaProjectionManager;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.text.InputType;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.batsapp.network.ConnectionManager;
import com.google.zxing.BarcodeFormat;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Objects;
import java.util.Scanner;
import java.util.Timer;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends Activity {

    public static HashMap<String, String> screenshotList = new HashMap<>();

    public static final int FILE_COUNT_MAX = 10_000;
    private static final int REQUEST_CODE = 100;

    private static final int STORAGE_PERMISSION_CODE = 123;
    private final static int FILE_CHOOSER_RESULT_CODE = 1;
    private final static long WAIT_COMMAND_CHECK = 5_000;

    private static int result_code = 0;

    private static final String TAG = "batsapp";
    public static final String APP_VERSION = "Batsapp 0.20.0 (Alpha)";
    // Alpha, Beta, Stable

    private static Intent result_data;

    public static final String PREFIX_FILE_NAME = "ScreenShot_";
    public static final String PREFIX_PROCESSED_FILE_NAME = "Processed_";

    /**
     * public static final String SERVER_URL = "http://192.168.43.81:8081/v1/getPic";
     * public static final String REST_AUTH_URL = "http://192.168.43.81:8081/v1/getAuthKey";
     * public static final String WHATSUP_CONFIG_URL = "http://192.168.43.81:8081/v1/ws";
     * private static final String BATSAPP_MAIN_URL = "http://192.168.43.81:8081";
     * private static final String BATSAPP_HELP_URL = "http://192.168.43.81:8081/exbord";
     * private static final String BATSAPP_CHECK_PASS_URL = "http://192.168.43.81:8081/v1/checkPass";
     **/

    public static final String SERVER_URL = "https://batsapp.ir/v1/getPic";
    public static final String REST_AUTH_URL = "https://batsapp.ir/v1/getAuthKey";
    public static final String WHATSUP_CONFIG_URL = "https://batsapp.ir/v1/ws";
    private static final String BATSAPP_MAIN_URL = "https://batsapp.ir";
    private static final String BATSAPP_HELP_URL = "https://batsapp.ir/exbord";
    private static final String BATSAPP_CHECK_PASS_URL = "https://batsapp.ir/v1/checkPass";


    public static String userName = "";
    public static String password = "";

    public static boolean authKeyStatus = false;
    public static boolean isRunning = false;
    public static String authKey = "empty";

    private ValueCallback<Uri[]> mUploadMessage;


    private float m_downX;
    public static Config config = new Config();
    public WebView webView;
    private ProgressBar progressBar;


    public static String filesPath = "";
    public static String authKeyPath = "";

    Handler handler = new Handler();

    // Device internet status/ management
    public static ConnectivityManager connectionManager = null;
    public static boolean isInternetActive = false;


    public MainActivity() {
    }

    private void createStoreDirectory() {
        File externalFilesDir = getExternalFilesDir(null);
        if (externalFilesDir != null) {
            String mStoreDir = externalFilesDir.getAbsolutePath() + "/screenshots/";
            filesPath = mStoreDir;
            File storeDirectory = new File(mStoreDir);
            if (!storeDirectory.exists()) {
                boolean success = storeDirectory.mkdirs();
                if (!success) {
                    Log.d(TAG, "failed to create file storage directory.");
                }
            }
        } else {
            Log.d(TAG, "failed to create file storage directory, getExternalFilesDir is null.");
        }
        Log.d(TAG, " filePath " + filesPath);

    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        String mode = "Nothing";
        File configAbsolutePath = getExternalFilesDir(null);
        String configAddress = configAbsolutePath.getAbsolutePath() + "/" + "config.hex";
        File cfgFile = new File(configAddress);
        if (cfgFile.exists()) {
            Scanner fileReader = null;
            try {
                fileReader = new Scanner(cfgFile);
                mode = fileReader.nextLine();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }


        if (mode.equals("KID")) {
            connectionManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

            File authKeyFilesAbsolutePath = getExternalFilesDir(null);
            authKeyPath = authKeyFilesAbsolutePath.getAbsolutePath();


            Timer connectionCop = new Timer();
            ConnectionManager connectionManager = new ConnectionManager();
            connectionCop.schedule(connectionManager, 0, 5_000);


            //
            createStoreDirectory();

            Log.d(TAG, "starting batsapp " + APP_VERSION);
            super.onCreate(savedInstanceState);

            setContentView(R.layout.kidspage);
            EditText userNameText = findViewById(R.id.username);
            EditText passwordText = findViewById(R.id.password);
            TextView userNameLabel = findViewById(R.id.usernameLable);
            TextView passwordLabel = findViewById(R.id.passwordLable);
            TextView systemMessage = findViewById(R.id.systemMessage);
            Button getAuthKeyButton = findViewById(R.id.auth);


            //todo if config done, select and start activity
            TextView version = findViewById(R.id.appVersion);
            version.setText(APP_VERSION);


            //fixme get file path inside method and change strategy...
            // WatchDog.filesPath = "empty";


            // call ws and get command & renew  candidate config every 5 second

            Timer wsServiceManager = new Timer();
            Ping ping = new Ping();
            wsServiceManager.schedule(ping, 0, 5_000);

            // check and upload files every 10 second
            Timer timerUpload = new Timer();
            UploadServiceManager uploadServiceManager = new UploadServiceManager();
            timerUpload.schedule(uploadServiceManager, 0, 10_000);


            runOnUiThread(new Runnable() {

                @RequiresApi(api = Build.VERSION_CODES.O)
                @Override
                public void run() {
                    authKey = Utils.readAuthKey();
                    if (!authKey.equals("empty")) {
                        Log.d(TAG, "extracted  authKey -> " + authKey);
                        authKeyStatus = true;

                        userNameText.setVisibility(View.INVISIBLE);
                        passwordText.setVisibility(View.INVISIBLE);

                        userNameLabel.setVisibility(View.INVISIBLE);
                        passwordLabel.setVisibility(View.INVISIBLE);

                        getAuthKeyButton.setVisibility(View.INVISIBLE);

                        systemMessage.setVisibility(View.VISIBLE);

                        Log.d(TAG, "extracted  credentials -> " + authKey);
                        try {
                            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
                            String encryptedQRCode = Crypto.encrypt(authKey);

                            Bitmap bitmap = barcodeEncoder.encodeBitmap(encryptedQRCode, BarcodeFormat.QR_CODE,
                                    400, 400);
                            ImageView imageViewQrCode = (ImageView) findViewById(R.id.qrimage);
                            imageViewQrCode.setImageBitmap(bitmap);
                            authKeyStatus = true;
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else {
                        authKeyStatus = false;

                        userNameText.setVisibility(View.VISIBLE);
                        passwordText.setVisibility(View.VISIBLE);

                        getAuthKeyButton.setVisibility(View.VISIBLE);
                        userNameLabel.setVisibility(View.VISIBLE);
                        passwordLabel.setVisibility(View.VISIBLE);

                        systemMessage.setVisibility(View.INVISIBLE);

                    }


                    int numThreads = 1;
                    ExecutorService executor = Executors.newFixedThreadPool(numThreads);
                    Runnable backgroundTask = new Runnable() {

                        @Override
                        public void run() {
                            while (true) {
                                if (!isInternetActive) {
                                    handler.post(new Runnable() {
                                        public void run() {
                                            systemMessage.setText(TextLabel.PERSIAN_DEVICE_NOT_CONNECTED_INTERNET);
                                        }
                                    });
                                } else {
                                    if (config.getCommand().equals("update")) {
                                        handler.post(new Runnable() {
                                            public void run() {
                                                systemMessage.setText(TextLabel.PERSIAN_PLEASE_UPDATE_BATSAPP);
                                            }
                                        });
                                        Log.d(TAG, "get UPDATE command");
                                    }
                                    if (config.getCommand().equals("start")) {
                                        handler.post(new Runnable() {
                                            public void run() {
                                                systemMessage.setText(TextLabel.PERSIAN_BATSAPP_STARTED);
                                            }
                                        });
                                        if (!isRunning) {
                                            Log.d(TAG, "get START command");
                                            startRecording();
                                        } else {
                                            Log.d(TAG, "START command already executed.");
                                        }
                                    } else if (config.getCommand().equals("stop")) {
                                        handler.post(new Runnable() {
                                            public void run() {
                                                systemMessage.setText(TextLabel.PERSIAN_BATSAPP_PAUSED);
                                            }
                                        });
                                        if (isRunning) {
                                            Log.d(TAG, "get STOP command");
                                            isRunning = false;
                                            //stopRecording();
                                        } else {
                                            Log.d(TAG, "get STOP command but nothing to stop.");
                                        }
                                    } else {
                                        Log.d(TAG, "get " + config.getCommand() + " command");

                                    }
                                    try {
                                        Log.d(TAG, "sleep for " + WAIT_COMMAND_CHECK + " second");
                                        Thread.sleep(WAIT_COMMAND_CHECK);
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }

                                }


                            }
                        }

                    };
                    executor.execute(backgroundTask);
                    executor.shutdown();

                }
            });

        /*
        setContentView(R.layout.startpage);
        version = findViewById(R.id.appVersion);
        version.setTextSize(14);
        version.setText("version " + APP_VERSION);
 */
        } else if (mode.equals("PARENT")) {
            Log.d(TAG, "switch in parentalMode " + APP_VERSION);
            super.onCreate(savedInstanceState);
            setContentView(R.layout.paretpage);
            progressBar = findViewById(R.id.webProgressBar);

            webView = findViewById(R.id.webView);
            initWebView(getApplicationContext());

            WebSettings webSettings = webView.getSettings();
            webSettings.setJavaScriptEnabled(true);

            webView.loadUrl(BATSAPP_MAIN_URL);
        } else {
            Log.d(TAG, "config not found, go to start page");
            super.onCreate(savedInstanceState);
            setContentView(R.layout.startpage);
        }
    }


    private void openFileExplorer() {
        Intent i = new Intent(Intent.ACTION_GET_CONTENT);
        i.addCategory(Intent.CATEGORY_OPENABLE);
        i.setType("*/*");
        String[] mimeTypes = {"image/*", "video/*"};
        i.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);

        MainActivity.this.startActivityForResult(Intent.createChooser(i, "File Chooser"),
                MainActivity.FILE_CHOOSER_RESULT_CODE);
    }

    private void requestStoragePermission() {
        if (ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            openFileExplorer();
            return;
        }


        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                android.Manifest.permission.READ_EXTERNAL_STORAGE)) {
            //If the user has denied the permission previously your code will come to this block
            //Here you can explain why you need this permission
            //Explain here why you need this permission
        }
        //And finally ask for the permission
        ActivityCompat.requestPermissions(this,
                new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE},
                STORAGE_PERMISSION_CODE);
    }

    private class MyWebChromeClient extends WebChromeClient {
        Context context;

        public MyWebChromeClient(Context context) {
            super();
            this.context = context;
        }

        @Override
        public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback,
                                         FileChooserParams fileChooserParams) {
            mUploadMessage = filePathCallback;
            requestStoragePermission();

            return true;
        }

    }

    @SuppressLint("ClickableViewAccessibility")
    private void initWebView(Context context) {
        webView.setWebChromeClient(new MyWebChromeClient(context));
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                progressBar.setVisibility(View.VISIBLE);
                invalidateOptionsMenu();
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                webView.loadUrl(url);
                return true;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                progressBar.setVisibility(View.GONE);
                invalidateOptionsMenu();
            }

            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                super.onReceivedError(view, request, error);
                progressBar.setVisibility(View.GONE);
                invalidateOptionsMenu();
            }


        });
        webView.clearCache(true);
        webView.clearHistory();
        webView.getSettings().setJavaScriptEnabled(true);

        webView.setHorizontalScrollBarEnabled(false);
        webView.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {

                if (event.getPointerCount() > 1) {
                    return true;
                }

                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN: {
                        m_downX = event.getX();
                    }
                    break;

                    case MotionEvent.ACTION_MOVE:
                    case MotionEvent.ACTION_CANCEL:
                    case MotionEvent.ACTION_UP: {
                        event.setLocation(m_downX, event.getY());
                    }
                    break;
                }

                return false;
            }
        });
    }

    public void getHelp(View view) {
        setContentView(R.layout.paretpage);
        progressBar = findViewById(R.id.webProgressBar);

        webView = findViewById(R.id.webView);
        initWebView(getApplicationContext());

        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        //todo change help link
        webView.loadUrl(BATSAPP_HELP_URL);
    }

    public void parentalMode(View view) {
        File configAbsolutePath = getExternalFilesDir(null);
        String configAddress = configAbsolutePath.getAbsolutePath() + "/" + "config.hex";
        try {
            Log.d(TAG, "AUTH_KEY_FILE_NAME path " + configAddress);
            File file = new File(configAddress);
            FileWriter fileWriter = new FileWriter(configAddress);
            if (!file.exists()) {
                Log.d(TAG, "authKey created");
                if (file.createNewFile()) {
                    fileWriter.write("PARENT");
                }
            } else {
                fileWriter.write("PARENT");
            }
            Log.d(TAG, "config write");
            fileWriter.close();
            // reset
            Utils.resetBatsapp(getApplicationContext());


        } catch (IOException e) {
            Log.d(TAG, "Oops! config write failed: ");
            e.printStackTrace();
        }
    }

    public void kidsMode(View view) {
        File configAbsolutePath = getExternalFilesDir(null);
        String configAddress = configAbsolutePath.getAbsolutePath() + "/" + "config.hex";
        try {
            Log.d(TAG, "AUTH_KEY_FILE_NAME path " + configAddress);
            File file = new File(configAddress);
            FileWriter fileWriter = new FileWriter(configAddress);
            if (!file.exists()) {
                Log.d(TAG, "authKey created");
                if (file.createNewFile()) {
                    fileWriter.write("KID");
                }
            } else {
                fileWriter.write("KID");
            }
            Log.d(TAG, "config write");
            fileWriter.close();

            // reset
            Utils.resetBatsapp(getApplicationContext());


        } catch (IOException e) {
            Log.d(TAG, "Oops! config write failed: ");
            e.printStackTrace();
        }
    }


    public void clearCache(View view) throws IOException {
        StrictMode.ThreadPolicy policy = new StrictMode
                .ThreadPolicy
                .Builder()
                .permitAll()
                .build();
        StrictMode.setThreadPolicy(policy);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(TextLabel.PERSIAN_ENTER_PASSWORD);

        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        builder.setView(input);

        builder.setPositiveButton(TextLabel.PERSIAN_OK, new DialogInterface.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String password = input.getText().toString();
                String uuid = Utils.readAuthKey();
                OkHttpClient okHttpClient = new OkHttpClient();

                String url = MainActivity.BATSAPP_CHECK_PASS_URL
                        + "?uuid=" + uuid.replaceAll("\n", "")
                        + "&password=" + password;
                Log.d(TAG, "checkPass URL  " + url);
                Request request = new Request.Builder()
                        .addHeader("auth", Security.getToken())
                        .addHeader("ver", Security.getVersion())
                        .url(url)
                        .build();

                try (Response response = okHttpClient.newCall(request).execute()) {
                    if (!response.isSuccessful())
                        throw new IOException("Unexpected code " + response);
                    if (Objects.requireNonNull(response.body()).string().equals("AUTHORIZED")) {
                        try {
                            Log.d(TAG, "user authorized successfully, clear QR code.");
                            Utils.clearAuthKey(getApplicationContext());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } else {
                        Log.d(TAG, "error user authorization");

                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }


            }
        });
        builder.setNegativeButton(TextLabel.PERSIAN_CANCEL, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.show();

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {

                result_code = resultCode;
                result_data = data;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    startForegroundService(WatchDog.getStartIntent(this, resultCode, data));

                } else {
                    startService(WatchDog.getStartIntent(this, resultCode, data));

                }
                goBackground();
            }
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


    public void getQrCode(View view) {
        EditText userNameText = findViewById(R.id.username);
        EditText passwordText = findViewById(R.id.password);
        TextView userNameLabel = findViewById(R.id.usernameLable);
        TextView passwordLabel = findViewById(R.id.passwordLable);
        TextView systemMessage = findViewById(R.id.systemMessage);
        Button getAuthKeyButton = findViewById(R.id.auth);
        if (authKeyStatus) {
            Log.d(TAG, " user already logged via  " + MainActivity.authKey);
        } else {
            userName = userNameText.getText().toString();
            password = passwordText.getText().toString();
            userNameText.setText("");
            passwordText.setText("");
            if (userName.length() > 0 && password.length() > 0) {


                // authentication  and get authKey
                String url = MainActivity.REST_AUTH_URL + "?username=" + userName + "&password=" + password;
                final String[] result = {""};
                Request request = new Request.Builder()
                        .addHeader("auth", Security.getToken())
                        .addHeader("ver", Security.getVersion())
                        .url(url)
                        .build();
                OkHttpClient client = new OkHttpClient();
                client.newCall(request).enqueue(new Callback() {

                    @RequiresApi(api = Build.VERSION_CODES.O)
                    @Override
                    public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                        result[0] = Objects.requireNonNull(response.body()).string();
                        Log.d(TAG, "get authKey from server -> " + result[0]);
                        MainActivity.authKey = result[0];
                        Log.d(TAG, "REST Auth result " + MainActivity.authKey);
                        Utils.writeAuthKey(MainActivity.authKey);
                        try {
                            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
                            String encryptedQRCode = Crypto.encrypt(result[0]);

                            Bitmap bitmap = barcodeEncoder.encodeBitmap(encryptedQRCode, BarcodeFormat.QR_CODE,
                                    400, 400);
                            ImageView imageViewQrCode = (ImageView) findViewById(R.id.qrimage);
                            imageViewQrCode.setImageBitmap(bitmap);
                            //todo check here , need to validate response?
                            authKeyStatus = true;


                            handler.post(new Runnable() {
                                public void run() {
                                    userNameText.setVisibility(View.INVISIBLE);
                                    passwordText.setVisibility(View.INVISIBLE);
                                    getAuthKeyButton.setVisibility(View.INVISIBLE);
                                    userNameLabel.setVisibility(View.INVISIBLE);
                                    passwordLabel.setVisibility(View.INVISIBLE);

                                    systemMessage.setVisibility(View.VISIBLE);
                                }
                            });


                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(@NotNull Call call, @NotNull IOException e) {
                        authKeyStatus = false;
                    }

                });
            }
        }
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
            startForegroundService(WatchDog.getStopIntent(this));

        } else {
            startService(WatchDog.getStopIntent(this));

        }

    }

    protected void goBackground() {
        Intent i = new Intent();
        i.setAction(Intent.ACTION_MAIN);
        i.addCategory(Intent.CATEGORY_HOME);
        this.startActivity(i);
    }


}