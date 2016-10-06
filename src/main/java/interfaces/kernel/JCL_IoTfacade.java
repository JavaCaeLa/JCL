/**
 * To implement Java Ca&La we need just few interfaces
 */
package interfaces.kernel;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import commom.JCL_Configuration;
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
	
	public static final JCL_facade Pacu = JCL_FacadeImpl.getInstancePacu();
	public static final JCL_facade Lambari = JCL_FacadeImpl.getInstanceLambari();
	
	/**
	 * checks if the device is in standBy mode
	 * @param deviceNickname The device to get the status
	 * @return a boolean indicating whether the device is in standby mode  
	 * */
	public abstract boolean isDeviceInStandBy(Entry<String, String> deviceNickname);
	
	/**
	 * the number of cores on the specified IoT device
	 * @param deviceNickname The device to get the number of cores
	 * @return the number of cores on the IoT device
	 */
	public abstract int getIoTDeviceCores(Entry<String, String> deviceNickname);
	
	/**
	 * get a list of registered IoT Devices with the number of cores
	 * @return a Map with host as key and number of cores as value
	 * */
	public abstract Map<Entry<String,String>, Integer> getAllIoTDeviceCores();
	
	/** 
	 * gets a list with all IoT devices in the cluster
	 * @return a list with all sensing devices in the cluster
	 */
	public abstract List<Entry<String, String>> getIoTDevices();
	
	/** 
	 * gets all sensors configured at a specific device
	 * @param deviceNickname The device to get the sensors
	 * @return a list with all sensors of a specific device 
	 */
	public abstract List<Entry<String, String>> getSensors(Entry<String, String> deviceNickname);
	
	/**
	 * retrieves the last 10 collected data of a sensor
	 * @param deviceNickname The device which the sensor is configured 
	 * @param sensorNickname The sensor to collect the sensing data
	 * @return a Map with the last 10 collected data of the sensor 
	 */  
	public abstract Map<Integer, JCL_Sensor> getSensingData(Entry<String, String> deviceNickname,Entry<String, String> sensorNickname);
	
	/**
	 * retrieves all collected data of a sensor
	 * @param deviceNickname The device which the sensor is configured 
	 * @param sensorNickname The sensor to collect the sensing data
	 * @return a Map with all the sensing data of the sensor 
	 */
	public abstract Map<Integer, JCL_Sensor> getAllSensingData(Entry<String, String> deviceNickname,Entry<String, String> sensorNickname);
	
	/**
	 * retrieves the last collected data of a sensor
	 * @param deviceNickname The device which the sensor is configured 
	 * @param sensorNickname The sensor to collect the last sensing data
	 * @return the last sensing data of the sensor
	 */
	public abstract Entry<Integer, JCL_Sensor> getLastSensingData(Entry<String, String> deviceNickname,Entry<String, String> sensorNickname);
	
	
	/**
	 * allows active sensing, collecting the sensing data at the given moment
	 * @param deviceNickname The device where the active sensing will occurr
	 * @param sensorNickname The sensor where the data will be retrieved
	 * @return a JCL_Sensor with the collected data
	 */
	public abstract JCL_Sensor getSensingDataNow(Entry<String, String> deviceNickname,Entry<String, String> sensorNickname);
	
	/**
	 * retrieves all information about the device configuration
	 * @param deviceNickname The device to collect the metadata
	 * @return a Map with all the metadata of the device
	 */
	public abstract Map<String, String> getIoTDeviceMetadata(Entry<String, String> deviceNickname);

	/**
	 * retrieves all information about the sensor configuration
	 * @param deviceNickname The device where the sensor is configured
	 * @param sensorNickname The sensor to get the metadata
	 * @return a Map with all the metadata of the sensor
	 */
	public abstract Map<String, String> getSensorMetadata(Entry<String, String> deviceNickname, Entry<String, String> sensorNickname);
	
	/**
	 * configures a device according to the metadata
	 * @param deviceNickname The device to set the metadata
	 * @param metadata The metadata to configure the device
	 * @return a boolean indicating if the metadata was configured at the device
	 */
	public abstract boolean setIoTDeviceMetadata(Entry<String, String> deviceNickname,Map<String, String> metadata);
	
	/**
	 * turn the device on, enabling passive sensing and allowing receiving commands
	 * @param deviceNickname The device to turn on
	 * @return a boolean indicating whether the device was turned on 
	 */
	public abstract boolean turnOn(Entry<String, String> deviceNickname);
	
	/**
	 * puts the device in an standby mode. In this mode the device stops passive sensing and stops receiving commands. 
	 * @param deviceNickname The device to put on standby mode
	 * @return a boolean indicating whether the device was put on standby mode 
	 */
	public abstract boolean standBy(Entry<String, String> deviceNickname);
	
	/**
	 * allows the configuration of a sensor on the device
	 * @param deviceNickname The device where the sensor will be configured
	 * @param sensorAlias An alias for the sensor
	 * @param sensorId The pin where the sensor will be configured
	 * @param sensorSize Indicates the maximum size in MB which will be used to store the collected sensor data 
	 * @param sensorSampling Indicates the interval to collect sensor data performed by the passive sensing
	 * @param inputOrOutput An String indicating the direction of the sensor ("input" or "output")
	 * @param actuatorType indicates the type of the actuator 
	 * @return a boolean indicating whether the sensor was configured
	 */
	public abstract boolean setSensorMetadata(Entry<String, String> deviceNickname,String sensorAlias,int sensorId, int sensorSize,int sensorSampling, String inputOrOutput, int actuatorType);
	
	/**
	 * removes a previously configured sensor from a device
	 * @param deviceNickname The device where the sensor is configured
	 * @param sensorNickname The sensor to remove
	 * @return a boolean indicating whter the sensor was removed
	 * */
	public abstract boolean removeSensor(Entry<String, String> deviceNickname, Entry<String, String> sensorNickname);
	
	/**
	 * executes a sequence of commands on the actuator
	 * @param deviceNickname The device where the actuator is configured
	 * @param actuatorNickname The actuator where the commands will be executed
	 * @param value the command to send to the sensor
	 * @return a boolean indicating whether the action was done
	 */
	public abstract boolean acting(Entry<String, String> deviceNickname, Entry<String, String> actuatorNickname, float value);
	
	/**
	 * configure a device according to an configuration instance
	 * @param configuration the configuration to be deployed on the device
	 * @return a boolean indicating if the configuration was loaded 
	 */
	public abstract boolean setConfig(JCL_Configuration configuration);
	
	/**
	 * gets the configuration of an device
	 * @param deviceNickname the device to get the configuration
	 * @return the configuration of the device
	 * */
	public abstract JCL_Configuration getConfig(Entry<String, String> deviceNickname);
	

}
