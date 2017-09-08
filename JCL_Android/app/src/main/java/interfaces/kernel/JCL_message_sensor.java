package interfaces.kernel;


/**
 * @author Joubert
 * @version 1.0
 * 
 * enables task messages in JCL
 */

public interface JCL_message_sensor extends JCL_message{
		
	/**
	 * Returns the device id.
	 * @return The device id.
	 */	
	public abstract String getDevice();

	/**
	 * Sets device id.
	 * @param device - device id.
	 */
	public abstract void setDevice(String device);
	
	/**
	 * Returns the Sensor id.
	 * @return The Sensor id.
	 */
	public abstract int getSensor();
	
	/**
	 * Sets Sensor id.
	 * @param sensor - Sensor id.
	 */
	public abstract void setSensor(int sensor);
	
	/**
	 * Returns the Value of the sensor.
	 * @return The Value of the sensor.
	 */
	public abstract Object getValue();
	
	/**
	 * Sets the value of the sensor.
	 * @param value - Value of the sensor.
	 */
	public abstract void setValue(Object value);

	/**
	 * Get the Msg type of the class.
	 */
	public abstract int getMsgType();	
	
	/**
	 * Returns the time.
	 * @return The time of the sensor.
	 */	
	public abstract long getTime();

	/**
	 * Sets the value of the sensor.
	 * @param time - time of the sensor.
	 */
	public abstract void setTime(long time);

	void setDataType(String dataType);

	String getDataType();

}
