package ir.innovera.batsapp.task;

import android.util.Log;

import org.json.JSONObject;

import java.io.IOException;
import java.util.Objects;
import java.util.TimerTask;

import ir.innovera.batsapp.MainActivity;
import ir.innovera.batsapp.security.Defender;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class Ping extends TimerTask {
    private static final String TAG = "batsapp";

    @Override
    public void run() {
        if (MainActivity.authKey.equals("")) {
            Log.d(TAG, "authKey not found or empty");
            return;
        }
        if (!MainActivity.isInternetActive) {
            Log.e(TAG, "ping (wa) ignored, no internet connection");
        } else {
            Log.d(TAG, "call whatsup API and get response");
            getConfig();
        }
    }

    private void getConfig() {
        String resultJson = null;
        OkHttpClient okHttpClient = new OkHttpClient();
        Request request = new Request.Builder()
                .addHeader("auth", Defender.getToken())
                .addHeader("ver", Defender.getVersion())
                .url(MainActivity.WHATSUP_CONFIG_URL + "?uuid=" + MainActivity.authKey)
                .build();

        try (Response response = okHttpClient.newCall(request).execute()) {
            if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
            resultJson = Objects.requireNonNull(response.body()).string();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            if (resultJson != null) {
                JSONObject json = new JSONObject(resultJson);

                int imageQuality = Integer.parseInt(json.getString("imageQuality"));
                int delay = Integer.parseInt(json.getString("screenShotDelay"));
                String command = json.getString("command");

                Log.d(TAG, "parsing json, imageQuality: " + imageQuality +
                        " screenShotDelay: " + delay * 1000 +
                        " command: " + command);
                //todo check values before assignment?
                MainActivity.config.setImageQuality(imageQuality);
                MainActivity.config.setScreenShotDelay(delay * 1000);
                MainActivity.config.setCommand(command);
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.d(TAG, "Oops! exception while setting config, return defualt");
        }
        okHttpClient.connectionPool().evictAll();
    }

}
