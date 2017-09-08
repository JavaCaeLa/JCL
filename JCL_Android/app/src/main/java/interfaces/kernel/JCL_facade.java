/**
 * To implement Java Ca&La we need just few interfaces
 */
package interfaces.kernel;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.Future;

/**
 * @author JCL Team
 * @version 1.0
 *
 * This class represents a JCL facade for HPC requirements. There are facades
 * for IoT demands and for capacity planning.
 *
 */

public interface JCL_facade{

	/**
	 * Registers a class on JCL, so it can be executed later.
	 *
	 * @param serviceClass - The class to be executed remotely
	 * @param nickName - The class name to be used by JCL
	 * @return true if it is registered, false otherwise
	 *
	 * @see #register(File[] jars, String classToBeExecuted)
	 */
	public abstract boolean register (Class<?> serviceClass, String nickName);

	/**
	 * Registers an array of JAR files. The first JAR file must contain
	 *  the class to be executed later with JCL. The remaining JAR files represent
	 *  the dependability, i.e. libraries or third part JARs
	 *
	 * @param jars - The array of jars files necessary to execute a class remotely. The first jar file is the user application. The remaining are dependencies.
	 * @param nickName - The class name to be used by JCL. It must be the same name of the class (without suffix .class or .java). The class package is not necessary in nickname.
	 * @return true if it is registered, false otherwise.
	 *
	 * @see #register(Class, String)
	 */
	public abstract boolean register (File[] jars, String nickName);

	/**
	 * Unregisters a class using its nickname. No mater if the class is registered
	 * via method register (Class<?> serviceClass, String nickName) or method
	 * register (File[] jars, String nickName)
	 *
	 * @param nickName - Class name defined by the developer in register phase. {@link #register(Class, String)} {@link #register(File[], String)}
	 * @return true if it is removed, false otherwise.
	 */
	public abstract boolean unRegister (String nickName);


	/**
	 * Executes the class method ("execute") from the class registered before. The method is executed remotely
	 * in JCL Pacu version or locally in Lambari version. It is an asynchronous call.
	 * IMPORTANT: the register method must be called before an execution
	 * The class to be executed must have the "execute" method inside
	 *
	 * @param nickName - Class name defined by the developer in register phase. {@link #register(Class, String)} {@link #register(File[], String)}
	 * @param args - The method parameters
	 * @return the task id, used to get the execution result asynchronously (Future.get() or future.get(time)).
	 *
	 *
	 * @see #execute(String, String, Object... args)
	 */
	public abstract Future<JCL_result> execute(String nickName, Object... args);

	/**
	 * Executes a specific method of a class registered before. The method is executed remotely
	 * in JCL Pacu version or locally in Lambari version. It is an asynchronous call.
	 *
	 * @param nickName - Class name defined by the developer in register phase. {@link #register(Class, String)} {@link #register(File[], String)}
	 * @param methodName - The class method name to be executed.
	 * @param args - The method parameters.
	 * @return The task id, used to get the result asynchronously (Future.get() or future.get(time)).
	 *
	 *
	 * @see #execute(String, Object...)
	 * @see #Java Future for a better understanding
	 */
	public abstract  Future<JCL_result> execute (String nickName, String methodName, Object...args);


	/**
	 * Gets a list of results (JCL_result) from a list of previous submitted tasks
	 * The caller blocks until all results are returned. If an exceptions occurs,
	 * JCL encapsulates it in JCL_result. In summary, all correct results or exceptions
	 * must be returned before continue.
	 *
	 * @param tickets - list of previous submitted tasks identifications
	 * @return a list of results, including execution exceptions.
	 *
	 * @see #JCL_result for a better understanding
	 *
	 */

	public abstract List<JCL_result> getAllResultBlocking(List<Future<JCL_result>> tickets);


	/**
	 * Gets a list of results (JCL_result) from a list of previous submitted tasks
	 * The caller is not blocked, so there is an option of NULLs in the result. If an exceptions occurs,
	 * JCL encapsulates it in JCL_result. In summary, all correct results or exceptions or NULLs
	 * must be returned before continue. It is a non-blocking call, very useful to check
	 * if a result is ready without blocking the caller.
	 *
	 * @param tickets - list of previous submitted tasks identifications
	 * @return a list of results, including execution exceptions and NULLs.
	 *
	 * @see #JCL_result for a better understanding
	 * @see #getAllResultBlocking(List<Future<JCL_result>>) for a blocking call
	 *
	 */

	public abstract List<JCL_result> getAllResultUnblocking(List< Future<JCL_result>> tickets);

	/**
	 * Removes a result from the JCL. Must be called to avoid garbage in JCL.
	 *
	 * @param ticket - The task name or the task id in asynchronous executions
	 * @return the removed result (encapsulated in JCL_result) or NULL if there is no result.
	 *
	 */
	public abstract JCL_result removeResult(Future<JCL_result> ticket);

	/**
	 * Creates a global variable with a user defined key. The class adopted to instantiate
	 * the global variable must be informed (className). All JAR files, including the dependencies,
	 * must be added to enable correct instantiations. Finally, the arguments adopted to
	 * instantiate the global variable (args). Note that, args can be NULL.
	 * IMPORTANT: It is a synchronous call. JCL also enables asynchronous instantiations.
	 * IMPORTANT: The className must be equal to the .class file in the first JAR file of "jars" argument.
	 *
	 * @param key - The variable identifier.
	 * @param className - the name of the global variable class. It must be equal to the .class file in the first JAR file of "jars" argument.
	 * @param jars - JARs files used to instantiate the global variable. The first JAR file includes the global variable class file. The remaining JAR files are dependencies.
	 * @param args - the arguments adopted by the constructor of the global variable. It can be NULL.
	 * @return Return true if the variable is created. false otherwise.
	 *
	 * @see #instantiateGlobalVar(Object key, Object instance)
	 * @see #deleteGlobalVar(Object key)
	 */
	public abstract boolean instantiateGlobalVar(Object key, String className, File[] jars, Object[] args);

	/**
	 * Stores a previous instantiated object (instance) in JCL with a user defined nickname (key)
	 * This method works only with Java types, since there is no user type to be registered.
	 * IMPORTANT: It is a synchronous call. JCL also enables asynchronous instantiations.
	 *
	 * @param key - The variable identifier.
	 * @param instance - An instance of the variable
	 * @return Return true if the variable is registered. False otherwise.
	 *
	 * @see #instantiateGlobalVar(Object key, String varName, File[] jars, Object[] args)
	 * @see #deleteGlobalVar(Object key)
	 */
	public abstract boolean instantiateGlobalVar(Object key, Object instance);

	/**
	 * Removes a user global variable from JCL.
	 * If the variable is previously locked, the delete will return false.
	 * The user must unlock first and then delete the global variable.
	 *
	 * @param key - The variable identifier
	 * @return Return true if the variable is deleted. False otherwise.
	 *
	 * @see #instantiateGlobalVar(Object key, String varName, File[] jars, Object[] defaultVarValue)
	 * @see #instantiateGlobalVar(Object key, Object instance)
	 */
	public abstract boolean deleteGlobalVar(Object key);

	/**
	 * Updates a global variable with a new value. It also unlocks the variable access
	 * if it is previously locked.
	 *
	 * @param key - The variable identifier
	 * @param value - The global variable new value
	 * @return True if value is correctly updated and unlocked or false otherwise
	 *
	 */
	public abstract boolean setValueUnlocking(Object key, Object value);


	/**
	 * Gets a variable value from JCL. It does not lock de global variable
	 *
	 * @param key - The variable identifier
	 * @return The global variable value or an error or null if no variable exists
	 *
	 * @see #getValueLocking(Object key) for a locked call
	 */
	public abstract JCL_result getValue(Object key);


	/**
	 * Gets a variable varName value and locks its access.
	 * This is a simple implementation of critical sections.
	 * The variable is locked by a single process until a {@link #setValueUnlocking(Object key, Object)} call occurs.
	 * Other processes can only read such a variable value, using JCL {@link #getValue(Object key)} method
	 *
	 * @param key - The variable identifier
	 * @return The global variable value or an error or null if no variable exists.
	 *
	 * The pair getValueLocking and setValueUnlocking implements critical sections in JCL.
	 *
	 * @see #getValue(Object key) for an unlocked call
	 */
	public abstract JCL_result getValueLocking(Object key);

	/**
	 * destroy JCL_User classes.
	 * Adopted at the end of a JCL program.
	 */
	public abstract void destroy();

	/**
	 * Verify if there is a task registered in JCL.
	 *
	 * @param nickName the task identifier.
	 * @return true if JCL contains such a task, false otherwise.
	 *
	 * @see #containsGlobalVar(Object key)
	 */
	public abstract boolean containsTask(String nickName);

	/**
	 * Verify if there is a global variable registered in JCL
	 *
	 * @param key - The variable identifier.
	 * @return True if JCL contains such a global variable, false otherwise.
	 *
	 * @see #containsTask(String)
	 */
	public abstract boolean containsGlobalVar(Object key);

	/**
	 * Verify if the global variable is locked, without
	 * locking it. It is different from a {@link #getValueLocking(Object)} call,
	 * which locks a variable access until a {@link #setValueLocking(Object, Object)} call occurs.
	 *
	 * @param key - The variable identifier.
	 * @return return true if global variable is locked or false otherwise.
	 *
	 */
	public abstract boolean isLock(Object key);

	/**
	 * Clean all global variables and all results from submitted tasks from JCL.
	 *
	 * @return return true if all Host and Server components were cleaned or false otherwise.
	 *
	 */
	public abstract boolean cleanEnvironment();

	/**
	 * Execute the method named "execute" in all Hosts of a JCL. The same arguments args
	 * are adopted in each Host. Just one task per Host in this method
	 *
	 * @param nickName - The object nickname
	 * @param args - The method parameters values used in all Host calls
	 * @return A list of Future objects to get all the results asynchronously.
	 *
	 * @see #executeAllCores(String, Object...) for an execution per core of JCL
	 */
	public abstract List<Future<JCL_result>> executeAll(String nickName, Object... args);

	/**
	 * Execute the method named "execute" in all Hosts of a JCL. Different arguments args
	 * are adopted in each Host. Just one task per Host in this method
	 *
	 * @param nickName - The object nickname
	 * @param args - The method parameters values used in all Host calls
	 * @return A list of Future objects to get all the results asynchronously.
	 *
	 * @see #executeAllCores(String, Object[][]) for an execution per core of JCL
	 */
	public abstract List<Future<JCL_result>> executeAll(String nickName, Object[][] args);

	/**
	 * Execute a specific method in all cores of all Hosts of a JCL. The same arguments args
	 * are adopted in each core of each Host.
	 *
	 * @param nickName - The object nickname
	 * @param methodName - The method name
	 * @param args - The method parameters values used in all calls
	 * @return A list of Future objects to get all the results asynchronously.
	 *
	 * @see #executeAll(String, Object...) for an execution per Host of JCL
	 */

	public abstract List<Future<JCL_result>> executeAllCores (String nickName,String methodName, Object... args);

	/**
	 * Execute the method named "execute" in all cores of all Hosts of a JCL. The same arguments args
	 * are adopted in each Host. Just one task per Host core in this method
	 *
	 * @param nickName - The object nickname
	 * @param args - The method parameters values used in all calls
	 * @return A list of Future objects to get all the results asynchronously.
	 *
	 * @see #executeAll(String, Object...) for an execution per Host of JCL
	 */
	public abstract List<Future<JCL_result>> executeAllCores (String nickName, Object... args);

	/**
	 * Execute the method named "execute" in all cores of all Hosts of a JCL. A different argument args
	 * is adopted in each core of each Host. Just one task per core in this method
	 *
	 * @param nickName - The object nickname
	 * @param args - The method parameters values used in all calls
	 * @return A list of Future objects to get all the results asynchronously.
	 *
	 * @see #executeAll(String, Object[][]) for an execution per Host of JCL
	 */
	public abstract List<Future<JCL_result>> executeAllCores (String nickName, Object[][] args);


	/**
	 * Execute a specific method in all Hosts of a JCL. The same arguments args
	 * are adopted in each Host.
	 *
	 * @param nickName - The object nickname
	 * @param methodName - The method name
	 * @param args - The method parameters values used in all Host calls
	 * @return A list of Future objects to get all the results asynchronously.
	 *
	 * @see #executeAllCores(String,String, Object...) for an execution per core of JCL
	 */
	public abstract List<Future<JCL_result>> executeAll (String nickName, String methodName, Object...args);

	/**
	 * Execute a specific method in all cores of all Hosts of a JCL. A different argument of args
	 * is adopted in each core of each Host.
	 *
	 * @param nickName - The object nickname
	 * @param methodName - The method name
	 * @param args - The method parameters values used in all calls
	 * @return A list of Future objects to get all the results asynchronously.
	 *
	 * @see #executeAll(String, String, Object[][]) for an execution per Host of JCL
	 */
	public abstract List<Future<JCL_result>> executeAllCores (String nickName, String methodName, Object[][] args);

	/**
	 * Execute a specific method in all Hosts of a JCL. A different argument of args
	 * is adopted in each Host.
	 *
	 * @param nickName - The object nickname
	 * @param methodName - The method name
	 * @param args - The method parameters values used in all calls
	 * @return A list of Future objects to get all the results asynchronously.
	 *
	 * @see #executeAllCores(String, String, Object[][]) for an execution per core of JCL
	 */
	public abstract List<Future<JCL_result>> executeAll (String nickName, String methodName, Object[][] args);


	/**
	 * Returns a list with all "high-end" devices in the JCL cluster. "High-end" devices are devices that can do storage and general purpose computing.
	 * Sensing activities are classified in JCL by IoT devices
	 *
	 * @return a list with all "high-end" devices in the cluster
	 *
	 * @see #getIoTDevices() to get devices that handle sensing activities
	 */
	public abstract <T extends java.util.Map.Entry<String, String>> List<T> getDevices();

	/**
	 * The number of cores of a specified Host or High-end device, i.e. devices that do no sensing.
	 *
	 * @param device - the specified device Format: Entry of "alias name , HOSTID¬IP¬PORT".
	 * @return  the number of cores.
	 *
	 * @see #getDevices()
	 */
	public abstract int getDeviceCore(Entry<String, String> device);

	/**
	 * Get a list of registered "high-end" devices or Hosts number of cores.
	 * Format:  Entry of "alias name , HOSTID¬IP¬PORT", "number of cores"
	 *
	 * @return The Map with Host as key and number of cores as value.
	 *
	 * @see #getDevices()
	 */
	public abstract <T extends java.util.Map.Entry<String, String>> Map<T, Integer> getAllDevicesCores();

	/**
	 * Get the number of cores in the JCL cluster. In Lambari version, it returns the
	 * number of cores of the local machine
	 *
	 * @return number of cores in the cluster.
	 *
	 */
	public abstract int getClusterCores();

	/**
	 * Creates a global variable with a user defined key in a specific Host. The class adopted to instantiate
	 * the global variable must be informed (className). All JAR files, including the dependencies,
	 * must be added to enable correct instantiations. Finally, the arguments adopted to
	 * instantiate the global variable (args). Note that, args can be NULL.
	 * IMPORTANT: It is a synchronous call. JCL also enables asynchronous instantiations.
	 * IMPORTANT: The className must be equal to the .class file in the first JAR file of "jars" argument.
	 *
	 * @param device - Entry of "alias name , HOSTID¬IP¬PORT".
	 * @param key - The variable identifier.
	 * @param className - the name of the global variable class. It must be equal to the .class file in the first JAR file of "jars" argument.
	 * @param jars - JARs files used to instantiate the global variable. The first JAR file includes the global variable class file. The remaining JAR files are dependencies.
	 * @param args - the arguments adopted by the constructor of the global variable. It can be NULL.
	 * @return Return true if the variable is created. false otherwise.
	 *
	 * @see #instantiateGlobalVar(Object, Object)
	 * @see #deleteGlobalVar(Object)
	 */

	public abstract boolean instantiateGlobalVarOnDevice(Entry<String, String> device, Object key, String className, File[] jars, Object[] args);

	/**
	 * Stores a previous instantiated object (instance) in JCL with a user defined nickname (key) and in a specific Host
	 * This method works only with Java types, since there is no user type to be registered.
	 * IMPORTANT: It is a synchronous call. JCL also enables asynchronous instantiations.
	 *
	 * @param device - Entry of "alias name , HOSTID¬IP¬PORT".
	 * @param key - The variable identifier.
	 * @param instance - An instance of the variable
	 * @return Return true if the variable is registered. False otherwise.
	 *
	 * @see #instantiateGlobalVar(Object, String, File[], Object[])
	 * @see #deleteGlobalVar(Object)
	 */
	public abstract boolean instantiateGlobalVarOnDevice(Entry<String, String> device, Object key, Object instance);


	/**
	 * Stores a previous instantiated object (instance) in JCL with a user defined nickname (key)
	 * This method works only with Java types, since there is no user type to be registered.
	 * IMPORTANT: It is an synchronous call. JCL also enables synchronous instantiations.
	 *
	 * @param key - The variable identifier.
	 * @param instance - An instance of the variable
	 * @return Return true if the variable is registered. False otherwise.
	 *
	 * @see #instantiateGlobalVarAsy(Object, String, File[], Object[])
	 * @see #deleteGlobalVar(Object)
	 */
	public abstract Future<Boolean> instantiateGlobalVarAsy(Object key, Object instance);

	/**
	 * Creates a global variable with a user defined key. The class adopted to instantiate
	 * the global variable must be informed (className). All JAR files, including the dependencies,
	 * must be added to enable correct instantiations. Finally, the arguments adopted to
	 * instantiate the global variable (args). Note that, args can be NULL.
	 * IMPORTANT: It is an asynchronous call. JCL also enables synchronous instantiations.
	 * IMPORTANT: The className must be equal to the .class file in the first JAR file of "jars" argument.
	 *
	 * @param key - The variable identifier.
	 * @param className - the name of the global variable class. It must be equal to the .class file in the first JAR file of "jars" argument.
	 * @param jars - JARs files used to instantiate the global variable. The first JAR file includes the global variable class file. The remaining JAR files are dependencies.
	 * @param args - the arguments adopted by the constructor of the global variable. It can be NULL.
	 * @return Return true if the variable is created. false otherwise.
	 *
	 * @see #instantiateGlobalVarAsy(Object, Object)
	 * @see #deleteGlobalVar(Object)
	 */
	public abstract Future<Boolean> instantiateGlobalVarAsy(Object key, String className, File[] jars, Object[] args);


	/**
	 * Executes a method named "methodName" in a specific device or Host. The method arguments are
	 * represented by args
	 *
	 * @param device - Entry of "alias name , HOSTID¬IP¬PORT". {@link #getDevices()}
	 * @param key - The object nickname defined by the developer in register
	 * @param methodName - The name of the method to be executed
	 * @param args - The method parameters values
	 * @return a Future object to get the result asynchronously.
	 *
	 * @see JCL_result for more details about how JCL encapsulates error and correct results
	 */
	public abstract Future<JCL_result> executeOnDevice(Entry<String, String> device, String registerClass, String methodName, Object... args);


	/**
	 * Executes a method named execute in a specific device or Host. The method arguments are
	 * represented by args
	 *
	 * @param device - Entry of "alias name , HOSTID¬IP¬PORT". {@link #getDevices()}
	 * @param registerClass - The object nickname defined by the developer in register
	 * @param args - The method parameters values
	 * @return a Future object to get the result asynchronously.
	 *
	 * @see JCL_result for more details about how JCL encapsulates error and correct results
	 */
	public abstract Future<JCL_result> executeOnDevice(Entry<String, String> device, String registerClass, Object... args);


	/**
	 * Stores a global variable with name equals key. The developer
	 * instance is adopted. It does not works with user types.
	 * Only Java types.Return Future &gt; Boolean &lt;, so this method is asynchronous, to lock use Future.get().
	 *
	 * @param key - The variable identifier.
	 * @param instance - A user variable object.
	 * @return Return Future &gt; Boolean &lt; true if the variable is registered. False otherwise.
	 *
	 * @see #instantiateGlobalVarAsy(Object key, Object instance, String classVar, boolean Registers)
	 * @see #deleteGlobalVar(Object key)
	 */
	//public abstract Future<Boolean> instantiateGlobalVarAsy(Object key, Object instance, String className, boolean Registers);

	/**
	 * retrieves all information about a device configuration
	 *
	 * @param device - The device to collect the metadata
	 * @return a Map with all the metadata of the device
	 */
	public abstract Map<String, String> getDeviceMetadata(Entry<String, String> device);

	/**
	 * Configures a device according to a metadata
	 *
	 * @param device - The device to set the metadata
	 * @param metadata - The metadata to configure the device
	 * @return a boolean indicating if the metadata was configured at the device or not
	 */
	public abstract boolean setDeviceMetadata(Entry<String, String> device, Map<String, String> metadata);

	Map<String, String> getDeviceConfig(Entry<String, String> deviceNickname);

	boolean setDeviceConfig(Entry<String, String> deviceNickname, Map<String, String> metadata);

}
