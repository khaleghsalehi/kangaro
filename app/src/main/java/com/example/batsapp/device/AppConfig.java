package com.example.batsapp.device;

import java.io.Serializable;

public class AppConfig implements Serializable {
    private String authKey="";
    private String mode="";

    public String getAuthKey() {
        return authKey;
    }

    public void setAuthKey(String authKey) {
        this.authKey = authKey;
    }

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    @Override
    public String toString() {
        return "AppConfig{" +
                "authKey='" + authKey + '\'' +
                ", mode='" + mode + '\'' +
                '}';
    }
}
