package com.example.myapplication;

import java.io.IOException;
import java.util.TimerTask;

import okhttp3.OkHttpClient;

public class PingServiceManager extends TimerTask {
    private final static OkHttpClient pingAgent = new OkHttpClient();
    private final static String PING_URL = MainActivity.SERVER_URL;

    @Override
    public void run() {
        try {
            Network.ping(pingAgent, PING_URL);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

