package com.example.batsapp;

public class Config {
    private int screenShotDelay = 10_000;
    private int imageQuality = 50;

    public int getScreenShotDelay() {
        return screenShotDelay;
    }

    public void setScreenShotDelay(int screenShotDelay) {
        this.screenShotDelay = screenShotDelay;
    }

    public int getImageQuality() {
        return imageQuality;
    }

    public void setImageQuality(int imageQuality) {
        this.imageQuality = imageQuality;
    }
}
