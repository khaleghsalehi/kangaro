package ir.innovera.batsapp.Entites;

public class Config {
    private int screenShotDelay = 10_000;
    private int imageQuality = 30;
    private String command="4e554c4c5f434f4e464947";

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
