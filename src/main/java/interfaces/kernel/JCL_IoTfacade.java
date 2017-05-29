/**
 * To implement Java Ca&La we need just few interfaces
 */
package interfaces.kernel;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.Future;
import implementations.dm_kernel.IoTuser.JCL_Configuration;
import implementations.dm_kernel.IoTuser.JCL_Expression;
import implementations.dm_kernel.user.JCL_FacadeImpl;
import interfaces.kernel.datatype.Device;
import interfaces.kernel.datatype.Sensor;


/**
 * @author JCL team
 * @version 1.0
 * 
 * This class represents a JCL facade for IoT requirements. There are facades
 * for HPC demands and for capacity planning. For JCL an IoT device indicates a sensing device.
 * 
 * IMPORTANT: JCL requires the configuration of each Host in a cluster or grid. The Host
 * can be a HPC device (laptops, desktops, workstations and servers) or an IoT device (arduino)
 * or a hybrid device (smartphones and Linux based boards with sensing and HPC features,
 *  like multiple cores and gpu cards). See JCL installation guide and programming guide
 *  for more details.  
 */

public interface JCL_IoTfacade{

	/**
	 * JCL Pacu instance
	 */
	public static final JCL_facade PacuHPC = JCL_FacadeImpl.getInstancePacu();
	
	/**
	 * JCL Lambari instance
	 */
	public static final JCL_facade LambariHPC = JCL_FacadeImpl.getInstanceLambari();
	
	/**
	 * checks if the device is in standBy mode
	 * @param deviceNickname The device to get the status
	 * @return a boolean indicating whether the device is in standby mode  
	 * */
	public abstract boolean isDeviceInStandBy(java.util.Map.Entry<String, String> deviceNickname);
	
	/**
	 * the number of cores on the specified IoT device. 
	 * @param deviceNickname The device to get the number of cores
	 * @return the number of cores on the IoT device
	 */
	public abstract  int getIoTDeviceCores(java.util.Map.Entry<String, String> deviceNickname);

	/**
	 * get a list of registered IoT Devices with the number of cores. Each entry of the map
	 * corresponds to an IoT device with its number of cores
	 * @return a Map with host as key and number of cores as value
	 * */
	public abstract<T extends java.util.Map.Entry<String, String>> Map<T, Integer> getAllIoTDeviceCores();
	
	/** 
	 * gets a list with all IoT devices in the cluster
	 * @return a list with all sensing devices in the cluster
	 */
	public abstract <T extends java.util.Map.Entry<String, String>> List<T> getIoTDevices();
	
	/** 
	 * gets all sensors configured at a specific device
	 * @param deviceNickname The device to get the sensors
	 * @return a list with all sensors of a specific device 
	 */
	public abstract <T extends java.util.Map.Entry<String, String>> List<T> getSensors(java.util.Map.Entry<String, String> deviceNickname);
	
	/**
	 * retrieves the last 10 collected data of a sensor, regardless the data are obtained from
	 * a context event or periodically
	 * @param deviceNickname The device which the sensor is configured 
	 * @param sensorNickname The sensor to collect the sensing data
	 * @return a Map with the last 10 collected data of the sensor 
	 */  
	public abstract Map<Integer, JCL_Sensor> getSensingData(java.util.Map.Entry<String, String> deviceNickname,java.util.Map.Entry<String, String> sensorNickname);
	
	/**
	 * retrieves all collected data of a sensor. Note that, each sensor in JCL has a limit
	 * support to retrieve data. Each sensor has a limit in MBytes. JCL cleans automatically the old data to avoid garbage.
	 * @param deviceNickname The device which the sensor is configured 
	 * @param sensorNickname The sensor to collect the sensing data
	 * @return a Map with all the sensing data of all the sensors of an IoT device 
	 */
	public abstract Map<Integer, JCL_Sensor> getAllSensingData(java.util.Map.Entry<String, String> deviceNickname,java.util.Map.Entry<String, String> sensorNickname);
		
	/**
	 * retrieves the last collected data of a specific sensor
	 * @param deviceNickname The device which the sensor is configured 
	 * @param sensorNickname The sensor to collect the last sensing data
	 * @return the last sensing data of the sensor
	 */
	public abstract Entry<Integer, JCL_Sensor> getLastSensingData(java.util.Map.Entry<String, String> deviceNickname,java.util.Map.Entry<String, String> sensorNickname);	
	
	/**
	 * allows active sensing, collecting the sensing data at the given moment
	 * @param deviceNickname The device where the active sensing will occur
	 * @param sensorNickname The sensor where the data will be retrieved
	 * @return a JCL_Sensor with the collected data
	 * 
	 * @see #JCL_Sensor for more details
	 */
	public abstract JCL_Sensor getSensingDataNow(java.util.Map.Entry<String, String> deviceNickname,java.util.Map.Entry<String, String> sensorNickname);
	

	/**
	 * retrieves all information about the sensor configuration, i.e. its name, id, type and many more
	 * @param deviceNickname The device where the sensor is configured
	 * @param sensorNickname The sensor to get the metadata
	 * @return a Map with all the metadata of the sensor
	 */
	public abstract Map<String, String> getSensorMetadata(java.util.Map.Entry<String, String> deviceNickname, java.util.Map.Entry<String, String> sensorNickname);

	/**
	 * allows the configuration of the IoT device metadata 
	 * @param deviceNickname The device to configure the metadata
	 * @param metadata a Map with all the metadata of all sensors
	 * @return a boolean indicating whether ther device was configured
	 */ 
	public abstract boolean setIoTDeviceMetadata(java.util.Map.Entry<String, String> deviceNickname, Map<String, String> metadata);
	
	/** 
	 * get all metadata from a IoT device
	 * @param deviceNickname The device to get the metadata
	 * @return a Map with all the metadata of the device
	 * */
	public abstract Map<String, String> getIoTDeviceMetadata(java.util.Map.Entry<String, String> deviceNickname);	
	
	/**
	 * the device is turned on. When the device is turned off, it is a logical
	 * operation only, so the device still receives commands. Sensing and processing
	 * and storage are prohibitive during the off state. The developer cannot obtain sensing
	 * data from off devices. He must turn them on first. 
	 * @param deviceNickname The device to turn on
	 * @return a boolean indicating whether the device was turned on or not
	 */
	public abstract boolean turnOn(java.util.Map.Entry<String, String> deviceNickname);
	
	/**
	 * puts the device in an standby mode. In this mode the device stops sensing, processing
	 * and storage. Control commands, like turn on, are accepted 
	 * @param deviceNickname The device to put on standby mode
	 * @return a boolean indicating whether the device was put on standby mode 
	 */
	public abstract boolean standBy(java.util.Map.Entry<String, String> deviceNickname);
	
	/**
	 * allows the configuration of a sensor of device and programmatically
	 * @param deviceNickname The device where the sensor will be configured
	 * @param sensorAlias An alias for the sensor
	 * @param sensorId The pin where the sensor will be configured
	 * @param sensorSize Indicates the maximum size in MB, which will be used to store the collected sensor data 
	 * @param sensorSampling Indicates the interval to collect sensor data, performing the passive sensing periodically
	 * @param inputOrOutput An String indicating the type of the sensor ("input" for sensing and "output" for acting)
	 * @param type A Integer representing the sensor/actuator type (0 - Generic, 1 - Servo) 
	 * @return a boolean indicating whether the sensor was configured
	 */
	public abstract boolean setSensorMetadata(java.util.Map.Entry<String, String> deviceNickname,String sensorAlias,int sensorId, int sensorSize,int sensorSampling, String inputOrOutput, int type);
	
	
	/**
	 * removes a previously configured sensor from a device. No more commands are allowed to
	 * such a device. JCL removes it from its cluster or grid.
	 * @param deviceNickname The device where the sensor is configured
	 * @param sensorNickname The sensor to remove
	 * @return a boolean indicating if the sensor was removed
	 * */
	public abstract boolean removeSensor(java.util.Map.Entry<String, String> deviceNickname, java.util.Map.Entry<String, String> sensorNickname);

	/**
	 * executes a sequence of commands on an actuator
	 * @param deviceNickname The device where the actuator is configured
	 * @param actuatorNickname The actuator where the commands will be executed
	 * @param commands a sequence of commands
	 * @return a boolean indicating whether the action was done
	 */
	public abstract boolean acting(java.util.Map.Entry<String, String> deviceNickname, java.util.Map.Entry<String, String> actuatorNickname, Object[] commands);
	
	/**
	 * configure a device according to an configuration instance. The configuration instance
	 * is the in memory representation of the configuration file used for each JCL component (User, Server,
	 * Super-peer and Host)
	 * @param deviceNickname the Device to set the configuration
	 * @param configuration the configuration to be deployed in the device
	 * @return a boolean indicating if the configuration was loaded 
	 */
	public abstract boolean setConfig(java.util.Map.Entry<String, String> deviceNickname, JCL_Configuration configuration);
	
	/**
	 * gets the configuration of an device
	 * @param deviceNickname the device to get the configuration
	 * @return the configuration of the device
	 * */
	public abstract JCL_Configuration getConfig(java.util.Map.Entry<String, String> deviceNickname);	
	
	/**
	 * enables or disables the encryption on a device
	 * @param deviceNickname The device to define the encryption
	 * @param encryption Indicates if the encryption will be enabled or disabled
	 * @return a boolean indicating whether the encryption was set
	 * */
	public abstract boolean setEncryption(java.util.Map.Entry<String, String> deviceNickname, boolean encryption);

	/**
	 * register a context on a device
	 * @param deviceNickname The device where the context will be registered
	 * @param sensorNickname The sensor which value will be used to trigger the context
	 * @param expression An {@link JCL_Expression} to indicate the conditions to trigger the context 
	 * @param contextNickname A nickname for the context 
	 * @return a boolean indicating whether the context was set
	 * */
	public abstract boolean registerContext(java.util.Map.Entry<String, String> deviceNickname, java.util.Map.Entry<String, String> sensorNickname, JCL_Expression expression, String contextNickname);
	
	/**
	 * adds a task to be executed when a certain context is reached
	 * @param contextNickname The context nickname
	 * @param useSensorValue Indicates if the sensor value will be used in the task
	 * @param classNickname Class name defined by the developer in register phase. {@link #register(Class, String)} {@link #register(File[], String)}
	 * @param methodName The class method name to be executed.
	 * @param args The method parameters
	 * @return a Long indicating a ticket to get the result of the task
	 * */
	public abstract Future<JCL_result> addContextAction(String contextNickname, boolean useSensorValue, String classNickname, String methodName, Object... args);
	
	/**
	 * adds a acting command to an actuator when a certain context is reached
	 * @param contextNickname The context nickname
	 * @param deviceNickname The device where the actuator is configured
	 * @param actuatorNickname The actuator where the commands will be executed
	 * @param commands a sequence of commands
	 * @return a boolean indicating whether the action was done
	 * */
	public abstract boolean addContextAction(String contextNickname, java.util.Map.Entry<String, String> deviceNickname, java.util.Map.Entry<String, String> actuatorNickname, Object[] commands);
	
	/**
	 * remove the result of the task if the context already was reached.  
	 * @param contextNickname The context nickname
	 * @param ticket the ID of the task
	 * @return a boolean indicating whether the result was removed
	 * */
	public abstract boolean removeContextResult(String contextNickname, Future<JCL_result> ticket);
	
	/**
	 * returns a list of IoT devices with the specified nickname
	 * @param deviceNickname the name of the device to look for
	 * @return a list with all the IoT devices connected with that name 
	 * */
	public abstract <T extends java.util.Map.Entry<String, String>> List<T> getDeviceByName(String deviceNickname);
	
	/**
	 * returns a list of sensors configured with the specified nickname
	 * @param deviceNickname the device to look for the sensors
	 * @param sensorNickname the nickname of the sensor
	 * @return a list with all sensor on a device configured with the given name
	 * */
	public abstract <T extends java.util.Map.Entry<String, String>> List<T> getSensorByName(java.util.Map.Entry<String, String> deviceNickname, String sensorNickname);
	
		
	/**
	 * remove a previously configured context action
	 * @param contextNickname The context where the action is associated
	 * @param deviceNickname The device where the action is associated
	 * @param actuatorNickname The actuator where the action is associated
	 * @param commands The command of the action
	 * @return a boolean indicating whether the action was removed 
	 * */
	public abstract boolean removeContextAction(String contextNickname, boolean useSensorValue, String classNickname, String methodName, Object... args);	

	/** unregister a previously registered context
	 * @param contextNickname The nickname of the context that will be unregistered
	 * @return a boolean indicating whether the context was unregistered
	 * */
	public abstract boolean unregisterContext(String contextNickname);
	
	/**
	 * register a new MQTT Context using the name specified as the topic name. Data is published on this topic only when the specified expression is true
	 * @param deviceNickname The device where the context will be registered
	 * @param sensorNickname The sensor which value will be used to trigger the context
	 * @param expression An {@link JCL_Expression} to indicate the conditions to trigger the context 
	 * @param topicName the topic nickname 
	 * @return a boolean indicating whether the context was set
	 * */
	public abstract boolean registerMQTTContext(java.util.Map.Entry<String, String> deviceNickname, java.util.Map.Entry<String, String> sensorNickname, JCL_Expression expression, String topicName);
		/** unregister a previously registered MQTT context
	 * @param topicName The nickname of the topic that will be unregistered
	 * @return a boolean indicating whether the context was unregistered
	 * */
	public abstract boolean unregisterMQTTContext(String topicName);
	
	/**        
     * remove a previously configured context action        
     * @param contextNickname The context where the action is associated        
     * @param deviceNickname The device where the action is associated        
     * @param actuatorNickname The actuator where the action is associated        
     * @param commands The command of the action        
     * @return a boolean indicating whether the action was removed         
     * */
	public abstract boolean removeContextAction(String contextNickname, java.util.Map.Entry<String, String> deviceNickname, java.util.Map.Entry<String, String> actuatorNickname, Object[] commands);
  
}