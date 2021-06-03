package ir.innovera.batsapp.Entites;

public class Config {
    private int screenShotDelay = 10_000;
    private int imageQuality = 30;
    private String command = "stop";

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

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }
}
