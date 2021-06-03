package ir.innovera.batsapp.device;

public interface Status {

    /**
     *  if standby then let server know, don't report false positive in parental dashboard
     * @return standby status
     */

    public boolean isSandeMode();



    /**
     *  if battery percentage less than N, then do actions. e.g call ws every 60 second
     * @return battery percentage
     */

    public float getBatteryPercentage();

    public int getDeviceTemperate();
}
