package interfaces.kernel;

public interface JCL_IoT_Sensing_Model {
	public static String GALILEO_GEN_2 = "Intel Galileo Gen 2";
	public static String RASPBERRY_PI_BPLUS_REV_1 = "Raspberry Pi Model B+ Rev 1";
	public static String RASPBERRY_PI_2_B = "Raspberry Pi 2 Model B Rev 1";
	public static String BEAGLEBONE_BLACK_REV_B = "Beaglebone Black Rev. B";
	public final int INVALID_PIN = -1;
	
	public int getGPIO(int port);
	public boolean isPortDigital(int port);
	public boolean isPortAnalog(int port);
	public boolean validPin(int port);
	public boolean specialPin(int port);
}
