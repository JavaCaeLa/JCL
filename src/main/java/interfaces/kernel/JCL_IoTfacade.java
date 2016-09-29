/**
 * To implement Java Ca&La we need just few interfaces
 */
package interfaces.kernel;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import implementations.dm_kernel.user.JCL_FacadeImpl;

//
//import java.io.File;
//import java.util.List;
//import java.util.Map;
//import java.util.concurrent.Future;
//
/**
 * @author Andre Almeida
 * @version 1.0
 * 
 * - The developer API for JCL IoT 
 * 
 */

public interface JCL_IoTfacade{
	
	/**
	 * JCL Pacu instance
	 */
	public static final JCL_facade Pacu = JCL_FacadeImpl.getInstancePacu();
	
	/**
	 * JCL Lambari instance
	 */
	public static final JCL_facade Lambari = JCL_FacadeImpl.getInstanceLambari();
	
	
	/** 
	 * gets a list with all sensing devices in the cluster
	 * @return a list with all sensing devices in the cluster
	 */
	public abstract List<Entry<String, String>> getSensingDevices();
	
	/** 
	 * gets a list with all "high-end" devices in the cluster. "High-end" devices are devices that can do storage, general purpose computing and others.
	 * @return a list with all "high-end" devices in the cluster
	 */
	public abstract List<Entry<String, String>> getDevices();
	
	/** 
	 * gets all sensors configured at a specific device
	 * @param device - The device to get the sensors
	 * @return a list with all sensors of a specific device 
	 * */	
	public abstract List<Entry<String, String>> getSensors(Entry<String, String> device);
	
	/**
	 * retrieves the last 10 collected data of a sensor
	 * @param device - The device which the sensor is configured 
	 * @param Sensor - The sensor to collect the sensing data
	 * @return a Map with the last 10 collected data of the sensor 
	 */  
	public abstract Map<Integer, JCL_Sensor> getsensingdata(Entry<String, String> device,Entry<String, String> Sensor);
	
	/**
	 * retrieves all collected data of a sensor
	 * @param device - The device which the sensor is configured 
	 * @param Sensor - The sensor to collect the sensing data
	 * @return a Map with all the sensing data of the sensor 
	 */
	public abstract Map<Integer, JCL_Sensor> getallsensingdata(Entry<String, String> device,Entry<String, String> Sensor);
	
	/**
	 * retrieves the last collected data of a sensor
	 * @param device - The device which the sensor is configured 
	 * @param Sensor - The sensor to collect the last sensing data
	 * @return the last sensing data of the sensor
	 */
	public abstract Entry<Integer, JCL_Sensor> getlastsensingdata(Entry<String, String> device,Entry<String, String> Sensor);
	
	/**
	 * allows active sensing, collecting the sensing data at the given moment
	 * @param device - The device where the active sensing will occurr
	 * @param Sensor - The sensor where the data will be retrieved
	 * @return a JCL_Sensor with the collected data
	 */
	public abstract JCL_Sensor getsensingdatanow(Entry<String, String> device,Entry<String, String> Sensor, Object... args);
	
	/**
	 * retrieves all information about the device configuration
	 * @param device - The device to collect the metadata
	 * @return a Map with all the metadata of the device
	 */
	public abstract Map<String, String> getMetadata(Entry<String, String> device);
	
	/**
	 * configures a device according to the metadata
	 * @param device - The device to set the metadata
	 * @param Metadata - The metadata to configure the device
	 * @return a boolean indicating if the metadata was configured at the device
	 * */
	public abstract boolean setMetadata(Entry<String, String> device,Map<String, String> Metadata);
	
	/**
	 * restarts JCL Host on the device  
	 * @param device - The device to restart
	 * @return a boolean indicating whether the restart was performed
	 */
	public abstract boolean restart(Entry<String, String> device);

	/**
	 * puts the device in an standby mode, stopping passive sensing. It remains on this state until {@link #turnOn(Entry)} is called
	 * @param device - The device to put on standby mode
	 * @return a boolean indicating whether the device was put on standby mode 
	 */
	public abstract boolean standBy(Entry<String, String> device);
	
	/**
	 * turn the device on, enabling passive sensing
	 * @param device - The device to turn on
	 * @return a boolean indicating whether the device was turned on 
	 */	
	public abstract boolean turnOn(Entry<String, String> device);
	
	/**
	 * allows the configuration of a input sensor on the device
	 * @param device - The device where the sensor will be configured
	 * @param sensor_alias - An alias for the sensor
	 * @param sensor_id - The pin where the sensor will be configured
	 * @param sensor_size - Indicates the maximum size in MB which will be used to store the collected sensor data 
	 * @param sensor_sampling - Indicates the interval to collect sensor data performed by the passive sensing
	 * @return a boolean indicating whether the sensor was configured
	 */
	public abstract boolean setSensor(Entry<String, String> device,String sensor_alias,int sensor_id, int sensor_size,int sensor_sampling);

	/**
	 * allows the configuration of a sensor on the device
	 * @param device - The device where the sensor will be configured
	 * @param sensor_alias - An alias for the sensor
	 * @param sensor_id - The pin where the sensor will be configured
	 * @param sensor_size - Indicates the maximum size in MB which will be used to store the collected sensor data 
	 * @param sensor_sampling - Indicates the interval to collect sensor data performed by the passive sensing
	 * @param inputOrOutput - An String indicating the direction of the sensor ("input" or "output") 
	 * @return a boolean indicating whether the sensor was configured
	 */
	public abstract boolean setSensor(Entry<String, String> device,String sensor_alias,int sensor_id, int sensor_size,int sensor_sampling, String inputOrOutput);
	
	/**
	 * configure a device according to an configuration file
	 * @param configTxt - A file with the configuration
	 * @return a boolean indicating if the configuration was loaded 
	 */
	public abstract boolean LoadConfig(File configTxt);
	
}
