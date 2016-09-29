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
 * @author Joubert
 * @version 1.0
 * 
 * - The developer API 
 * 
 */

//Andre Luis
public interface JCL_facade{
		
	/**
	 * Registers a class on JCL so it can be executed later.
	 * 
	 * @param serviceClass The class to be executed remotely
	 * @param nickName The class name to be used by JCL
	 * @return True if it is registered, false otherwise
	 * 
	 * @see #register(File[] jars, String classToBeExecuted)
	 */
	public abstract boolean register (Class<?> serviceClass, String nickName);
	
	/**
	 * Registers JAR files and it's class to be executed later on JCL.
	 * 
	 * @param jars The array of jars files necessary to execute a class remotely. The first jar file is the user application. The remaining are dependencies.
	 * @param classToBeExecuted The class name to be used by JCL. just the simple name. no packages. no .class extension.
	 * @return True if it is registered, false otherwise.
	 * 
	 * @see #register(Class, String)
	 */
	public abstract boolean register (File[] jars, String classToBeExecuted);	
	
	/**
	 * Unregisters a class or a JAR file by it's nickname.
	 * 
	 * @param nickName of the class, jar component to be removed by JCL. The nickName most be the same used in register method.
	 * @return True if it is removed, false otherwise.
	 */
	public abstract boolean unRegister (String nickName);
	
	
	/**
	 * Execute a class method("execute") from the class that was register in one core member of the cluster in distributed/parallel version or in one core of the local processor in parallel version. 
	 * To use this method the register Class most have a method named "execute".
	 * 
	 * @param objectNickname - Class name defined by the developer in register phase. {@link #register(Class, String)} 
	 * @param args - The method parameters values
	 * @return The task id, used to get the result asynchronously. The task id is used with {@link #getResultBlocking(String)} or {@link #getResultUnblocking(String)} 
	 * 
	 * @see #execute(String, String, Object... args)
	 */
	public abstract String execute(String objectNickname, Object... args);
	
	/**
	 * Execute a specific method from the object received using its arguments. 
	 * 
	 * @param className - The full class name, i.e., .class file name defined by the developer in register phase. {@link #register(Class, String)}
	 * @param methodName - The object method name to be executed.
	 * @param args - The method parameters values.
	 * @return The task id, used to get the result asynchronously. The task id is used with {@link #getResultBlocking(String)} or {@link #getResultUnblocking(String)}
	 * 
	 * @see #execute(String, Object...)
	 */
	public abstract String execute (String className, String methodName, Object...args);
				
	/**
	 * 
	 * Get a method result from JCL or a wait condition. If a wait condition is obtained, the caller is blocked. 
	 * until the result arrives or a timeout is achieved.
	 * @param ID - The method or task identification.
	 * @return The result OR an error.
	 * 
	 * @see #getResultUnblocking(String ID)
	 */
	
	public abstract JCL_result getResultBlocking(String ID);
	
	/**
	 * 
	 * Get all method result from JCL list ID or a wait condition. If a wait condition is obtained, the caller is blocked. 
	 * until the result arrives or a timeout is achieved.
	 * @param ID - List of task identification.
	 * @return The list of result OR an error.
	 * 
	 */
	
	public abstract List<JCL_result> getAllResultBlocking(List<String> ID);
	
	/**
	 * Get a method result or null. The caller is never blocked. Asynchronous get of results.
	 * 
	 * @param ID - The method or task identification.
	 * @return The result or error or null, the last indicating processing.
	 * 
	 * @see #getResultUnblocking(String ID)
	 */
	
	public abstract JCL_result getResultUnblocking(String ID);

	/**
	 * Get all method result or null. The caller is never blocked. Asynchronous get of results.
	 * 
	 * @param ID - List of task identification.
	 * @return The list of result or error or null. Null indicate processing.
	 * 
	 */
	
	public abstract List<JCL_result> getAllResultUnblocking(List<String> ID);

	/**
	 * Removes a result from the JCL.
	 * 
	 * @param ID The task name or the task id in asynchronous executions
	 * @return The removed result. null if no such result exists.
	 * 
	 */	
	public abstract JCL_result removeResult(String ID);
	
	/**
	 * Creates a global variable with a user defined key, user typed according to jar files 
	 * and a class name varName. The args array sets user variable initial values used in constructor of the class. 
	 * The class name varName must be equal the .class file in the jar file.
	 * @param key - The variable identifier.
	 * @param varName - The variable name. varName must be equal the .class file in the jar file.
	 * @param jars - jars files with varName class.
	 * @param defaultVarValue - The default variable value.
	 * @return Return the instance if the variable is created. null otherwise.
	 * 
	 * @see #instantiateGlobalVar(Object key, Object instance)
	 * @see #destroyGlobalVar(Object key)
	 */
	public abstract boolean instantiateGlobalVar(Object key,String varName, File[] jars, Object[] defaultVarValue);

	/**
	 * Creates a global variable a user defined key. JCL uses the user object 
	 * instance instead of creating an instance internally. It does not works with user types. 
	 * Only Java types.
	 * 
	 * @param key - The variable identifier.
	 * @param instance - A user variable value.
	 * @return Return true if the variable is registered. False otherwise.
	 * 
	 * @see #instantiateGlobalVar(Object key, String varName, File[] jars, Object[] defaultVarValue)
	 * @see #destroyGlobalVar(Object key)
	 */
	public abstract boolean instantiateGlobalVar(Object key, Object instance);

	/**
	 * Removes a user global variable with key identifier from JCL.
	 * If the variable is previously locked the destroy will return false.
	 * the user must unlock first and destroy after.
	 * 
	 * @param key - The variable identifier
	 * @return Return true if the variable is set null. False otherwise.
	 * 
	 * @see #instantiateGlobalVar(Object key, String varName, File[] jars, Object[] defaultVarValue)
	 * @see #instantiateGlobalVar(Object key, Object instance)
	 */
	public abstract boolean destroyGlobalVar(Object key);
	
	/**
	 * Updates the variable value. Unlock a variable if it is previously locked.  
	 * 
	 * @param key - The variable identifier
	 * @param value - Global variable value
	 * @return True if value is correctly updated or false otherwise
	 * 
	 * @see #setValueUnlocking(Object key, Object value)
	 */
	public abstract boolean setValueUnlocking (Object key, Object value);
	
	
	/**
	 * Gets a variable varName value 
	 * 
	 * @param key - The variable identifier
	 * @return The global variable value or an error or null if no variable exists
	 * 
	 * @see #getValue(Object key)
	 */
	public abstract JCL_result getValue(Object key);
	
	
	/**
	 * Gets a variable varName value and locks varName access. 
	 * This is a simple implementation of critical sections. 
	 * The variable is locked and writed by a single process until a {@link #setValueUnlocking(Object key, Object)} call. 
	 * Other processes can only read such a variable, using JCL {@link #getValue(Object key)} method
	 * @param key - The variable identifier
	 * @return The global variable value or an error or null if no variable exists. 
	 * If there is a correct value, the JCL_Result also stores a lock ticket,
	 * used to set a value or simple unlock the variable.
	 * the pair getValueLocking and setValueUnlocking implements critical sections in JCL.
	 * 
	 * @see #getValueLocking(Object key)
	 */
	public abstract JCL_result getValueLocking(Object key);
			
	/**
	 * destroy JCL_user class.
	 * Use in the end of JCL program.
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
	 * Verify if the global variable is lock {@link #getValueLocking(Object)}
	 * 
	 * @param key - The variable identifier.
	 * @return return true if global variable is lock or false if global variable is unlock. 
	 * 
	 *  boolean isLock(String varName).
	 */
	public abstract boolean isLock(Object key);
	
	/**
	 * Clean all global variables and all result.
	 * 
	 * @return return true if all host and server was clean. 
	 * 
	 *  boolean cleanEnvironment().
	 */
	public abstract boolean cleanEnvironment();
	
	/**
	 * Execute the method named "execute" in all hosts from the object received using its arguments. 
	 * 
	 * @param objectNickname - The object nickname defined by the developer
	 * @param args - The method parameters values used in all task
	 * @return The task list id, used to get the result asynchronously. The task id is used with {@link #getResultBlocking(String)} or {@link #getResultUnblocking(String)} or {@link #getAllResultBlocking(List)} or {@link #getAllResultUnblocking(List)} 
	 * 
	 * @see #executeAll(String objectNickname, Object... args)
	 */
	public abstract List<String> executeAll(String objectNickname, Object... args);

	/**
	 * Execute the method named "execute" in all hosts with different arguments. 
	 * args[number of host][] one line by execute
	 * 
	 * @param objectNickname - The object nickname defined by the developer
	 * @param args - The method parameters values 
	 * @return The task list id, used to get the result asynchronously. The task id is used with {@link #getResultBlocking(String)} or {@link #getResultUnblocking(String)} or {@link #getAllResultBlocking(List)} or {@link #getAllResultUnblocking(List)} 
	 * 
	 * @see #executeAll(String objectNickname, Object[][] args)
	 */
	public abstract List<String> executeAll(String objectNickname, Object[][] args);

	/**
	 * Execute the method methodName in all hosts cores from the object received using its arguments. 
	 * 
	 * @param objectNickname - The object nickname defined by the developer
	 * @param methodName - The method name
	 * @param args - The method parameters values
	 * @return The task list id, used to get the result asynchronously. The task id is used with {@link #getResultBlocking(String)} or {@link #getResultUnblocking(String)} or {@link #getAllResultBlocking(List)} or {@link #getAllResultUnblocking(List)} 
	 * 
	 * @see #execute(String, String, Object...)
	 */
	public abstract List<String> executeAllCores (String objectNickname,String methodName, Object... args);

	/**
	 * Execute the method named "execute" in all hosts core from the object received using its arguments. 
	 * 
	 * @param objectNickname - The object nickname defined by the developer
	 * @param args - The method parameters values
	 * @return The task list id, used to get the result asynchronously. The task id is used with {@link #getResultBlocking(String)} or {@link #getResultUnblocking(String)} or {@link #getAllResultBlocking(List)} or {@link #getAllResultUnblocking(List)} 
	 * 
	 * @see #execute(String, String, Object...)
	 */
	public abstract List<String> executeAllCores (String objectNickname, Object... args);

	/**
	 * Execute the method named "execute" in all hosts cores from the object received using its arguments. 
	 * args[number of host][] one line by execute.
	 * 
	 * @param objectNickname - The object nickname defined by the developer
	 * @param args - The method parameters values
	 * @return The task id, used to get the result asynchronously. The task id is used with {@link #getResultBlocking(String)} or {@link #getResultUnblocking(String)} 
	 * 
	 * @see #execute(String, String, Object...)
	 */
	public abstract List<String> executeAllCores (String objectNickname, Object[][] args);

	/**
	 * Execute the method named "execute" in a specific host from the object received using its arguments. 
	 * 
	 * @param host - host address HOSTIDï¿½IPï¿½PORT. {@link #getHosts()}
	 * @param objectNickname - The object nickname defined by the developer
	 * @param args - The method parameters values
	 * @return The task id, used to get the result asynchronously. The task id is used with {@link #getResultBlocking(String)} or {@link #getResultUnblocking(String)} 
	 * 
	 * @see #executeOnHost(String, String, Object...)
	 */
	public abstract String executeOnHost (String host,String objectNickname, Object... args);
	
	/**
	 * Execute a specific method in all hosts from the object received using its arguments. 
	 * 
	 * @param className - The full class name, i.e., .class file name.
	 * @param methodName - The object method name to be executed.
	 * @param args - The method parameters values.
	 * @return The task list id, used to get the result asynchronously. The task id is used with {@link #getResultBlocking(String)} or {@link #getResultUnblocking(String)} or {@link #getAllResultBlocking(List)} or {@link #getAllResultUnblocking(List)}
	 * 
	 * @see #execute(String, Object...)
	 */
	public abstract List<String> executeAll (String className, String methodName, Object...args);

	/**
	 * Execute a specific method in all hosts cores from the object received using its arguments. 
	 * args[number of host][] one line by execute.
	 * 
	 * @param objectNickname - The full class name, i.e., .class file name.
	 * @param methodName - The object method name to be executed.
	 * @param args - The method parameters values.
	 * @return The task id, used to get the result asynchronously. The task id is used with {@link #getResultBlocking(String)} or {@link #getResultUnblocking(String)}
	 * 
	 * @see #execute(String, Object...)
	 */
	public abstract List<String> executeAllCores (String objectNickname, String methodName, Object[][] args);

	/**
	 * Execute a specific method in all hosts from the object received using its arguments. 
	 * args[number of host][] one line by execute.
	 * 
	 * @param className - The full class name, i.e., .class file name.
	 * @param methodName - The object method name to be executed.
	 * @param args - The method parameters values.
	 * @return The task list id, used to get the result asynchronously. The task id is used with {@link #getResultBlocking(String)} or {@link #getResultUnblocking(String)}
	 * 
	 * @see #execute(String, Object...)
	 */
	public abstract List<String> executeAll (String className, String methodName, Object[][] args);
	
	/**
	 * Execute a specific method in a specific host from the object received using its arguments. 
	 * 
	 * @param host - host address HOSTIDï¿½IPï¿½PORT.
	 * @param className - The full class name, i.e., .class file name.
	 * @param methodName - The object method name to be executed.
	 * @param args - The method parameters values.
	 * @return The task id, used to get the result asynchronously. The task id is used with {@link #getResultBlocking(String)} or {@link #getResultUnblocking(String)}
	 * 
	 * @see #execute(String objectNickname, Object... args)
	 */
	public abstract String executeOnHost (String host, String className, String methodName, Object...args);
	
	/**
	 * Get list of registered Hosts.
	 * Format: HOSTIDï¿½IPï¿½PORT
	 * 
	 * @return The list of hosts or error or null.
	 * 
	 * @see #getHosts()
	 */
	public abstract List<String> getHosts();

	/**
	 * The number of core of the specified host.
	 * 
	 * @param HostID - the specified host Format: HOSTIDï¿½IPï¿½PORT.
	 * @return  the number of core.
	 * 
	 * @see #getHosts()
	 */
	public abstract int getHostCore(String HostID);

	/**
	 * Get list of registered Hosts with the number of cores.
	 * Format: "HOSTID¬IP¬PORT", "number of cores"
	 * 
	 * @return The Map with host as key and number of cores as value.
	 *   
	 * @see #getHosts()
	 */
	public abstract Map<String, Integer> getAllHostCores();

	/**
	 * Get the number of cores in the cluster.
	 * 
	 * @return number of cores in the cluster.
	 * 
	 * @see #getAllHostCores()
	 */
	public abstract int getClusterCores();

	/**
	 * Creates a global variable in a specific host with name equals nickName, user typed according to jar files 
	 * and a class name varName. The args array sets user variable initial values. 
	 * The class name varName must be equal the .class file in the jar file.
	 * 
	 * @param host - host address HOSTIDï¿½IPï¿½PORT.
	 * @param nickname - The user nickname.  varName must be equal the .class file in the jar file.
	 * @param key - The variable key.
	 * @param jars - Jars file with nickname class.
	 * @param defaultVarValue - The default variable value.
	 * @return Return the instance if the variable is created. null otherwise.
	 * 
	 * @see #instantiateGlobalVar(Object key, Object instance)
	 * @see #destroyGlobalVar(Object key)
	 */
	public abstract Object instantiateGlobalVarOnHost(String host, String nickname, Object key, File[] jars, Object[] defaultVarValue);

	/**
	 * Creates a global variable in a specific host with name equals varName. JCL uses the user object 
	 * instance instead of creating an instance internally. It does not works with user types. 
	 * Only Java types.
	 * @param host - host address HOSTIDï¿½IPï¿½PORT.
	 * @param key - The variable key.
	 * @param instance - A user variable value.
	 * @return Return true if the variable is registered. False otherwise.
	 * 
	 * @see #instantiateGlobalVar(Object key, String varName, File[] jars, Object[] defaultVarValue)
	 * @see #destroyGlobalVar(Object key)
	 */
	public abstract boolean instantiateGlobalVarOnHost(String host, Object key, Object instance);	
	
	/**
	 * Insert a Host in the cluster.
	 * 
	 * @param mac - Mac of the host.
	 * @param ip - IP of the host.
	 * @param port - port of the host.
	 * @return True if the host is inserted in the cluster, false otherwise.
	 * 
	 * @see #removeHost(String, String, String)
	 */
	public abstract boolean insertHost(String mac, String ip, String port);
	
	/**
	 * Remove a Host in the cluster.
	 * 
	 * @param mac - Mac of the host.
	 * @param ip - IP of the host.
	 * @param port - port of the host.
	 * @return True if the host is removed from the cluster, false otherwise.
	 * 
	 * @see #insertHost(String, String, String)
	 */
	public abstract boolean  removeHost(String mac, String ip, String port);
		
	/**
	 * Creates a global variable with name equals key. JCL uses the user object 
	 * instance instead of creating an instance internally. It does not works with user types. 
	 * Only Java types.Return Future &gt; Boolean &lt;, so this method is asynchronous, to lock use Future.get().  
	 * 
	 * @param key - The variable identifier.
	 * @param instance - A user variable object.
	 * @return Return Future &gt; Boolean &lt; true if the variable is registered. False otherwise.
	 * 
	 * @see #instantiateGlobalVar(Object key, String varName, File[] jars, Object[] defaultVarValue)
	 * @see #destroyGlobalVar(Object key)
	 */
	public abstract Future<Boolean> instantiateGlobalVarAsy(Object key, Object instance);
	
	/**
	 * Creates a global variable with name equals key, user typed according to jar files 
	 * and a class name varName. The args array sets user variable initial values. 
	 * The class name varName must be equal the .class file in the jar file.
	 * Return Future Boolean, so this method is asynchronous to lock use Future.get().  
	 * @param key - The variable identifier.
	 * @param varName - The variable name. varName must be equal the .class file in the jar file.
	 * @param jars - jars files with varName class.
	 * @param defaultVarValue - The default variable value.
	 * @return Return Future interface.
	 * 
	 * @see #instantiateGlobalVar(Object key, Object instance)
	 * @see #destroyGlobalVar(Object key)
	 */
	public abstract Future<Boolean> instantiateGlobalVarAsy(Object key,String varName, File[] jars, Object[] defaultVarValue);

	/**
	 * Creates a global variable with name equals key. JCL uses the user object 
	 * instance instead of creating an instance internally. 
	 * 
	 * @param key - The variable identifier.
	 * @param instance - A user variable value.
	 * @param classVar - User type previously registered.
	 * @param Registers - true try to register.
	 * @return Return true if the variable is registered. False otherwise.
	 * 
	 * @see #instantiateGlobalVar(Object key, String varName, File[] jars, Object[] defaultVarValue)
	 * @see #destroyGlobalVar(Object key)
	 */
	public abstract boolean instantiateGlobalVar(Object key, Object instance,String classVar,boolean  Registers);

	/**
	 * Get JCL type, Lambari version or Pacu version.
	 * 
	 * @return The JCL version, Lambari or Pacu.
	 * 
	 * @see #getHosts()
	 */
	public abstract String version();
	
	/**
	 * Get Server time.
	 * 
	 * @return Server time.
	 * 
	 * @see #getServerTime()
	 */
	public abstract Long getServerTime();

	/**
	 * Execute a class method from the class that was register in one core member of the cluster in distributed/parallel version or in one core of the local processor in parallel version. 
	 * 
	 * @param task - JCL_task that contain all task info. {@link #register(Class, String)} 
	 * @return The task id, used to get the result asynchronously. The task id is used with {@link #getResultBlocking(String)} or {@link #getResultUnblocking(String)} 
	 * 
	 * @see #execute(String, String, Object... args)
	 */
	public abstract String execute(JCL_task task);

	/**
	 * Get a method result or null. The caller is never blocked. Asynchronous get of results.
	 * 
	 * @param ID - The method or task identification.
	 * @return The result or error or null, the last indicating processing.
	 * 
	 * @see #getResultUnblocking(String ID)
	 */
	public abstract JCL_result getResultUnblocking(Long ID);
	
	/**
	 * Removes a result from the JCL.
	 * 
	 * @param ID The task name or the task id in asynchronous executions
	 * @return The removed result. null if no such result exists.
	 * 
	 */	
	public abstract JCL_result removeResult(Long ID);
	
	/**
	 * 
	 * Get a method result from JCL or a wait condition. If a wait condition is obtained, the caller is blocked. 
	 * until the result arrives or a timeout is achieved.
	 * @param ID - The method or task identification.
	 * @return The result OR an error.
	 * 
	 * @see #getResultUnblocking(String ID)
	 */
	public abstract JCL_result getResultBlocking(Long ID);

	/**
	 * 
	 * Get a list of times os the task.1- Request; 2- Arrive on Host; 3- Start exec or Send to new Host; 4- End exec or arrive o new host
	 * 5 - leave the Host or Start exec; 6 - Arrive result on client or or End exec; 7 - leave new the Host; 8 -Arrive result.      
	 * @param ID - task identification.
	 * @return A list of times(nanoseconds).
	 * 
	 */
	public abstract List<Long> getTaskTimes(String ID);
	
	/**
	 * Execute the method named "execute" in a specific host from the object received using its arguments. 
	 * 
	 * @param device - host address HOSTID¬IP¬PORT. {@link #getHosts()}
	 * @param objectNickname - The object nickname defined by the developer
	 * @param args - The method parameters values
	 * @return The task id, used to get the result asynchronously. The task id is used with {@link #getResultBlocking(String)} or {@link #getResultUnblocking(String)} 
	 * 
	 */
	public abstract String executeOnHost(Entry<String, String> device, String objectNickname, String methodName, Object[] args);

	
	/**
	 * Execute the method named "execute" in a specific host from the object received using its arguments. 
	 * 
	 * @param device - host address HOSTIDï¿½IPï¿½PORT. {@link #getHosts()}
	 * @param objectNickname - The object nickname defined by the developer
	 * @param args - The method parameters values
	 * @return The task id, used to get the result asynchronously. The task id is used with {@link #getResultBlocking(String)} or {@link #getResultUnblocking(String)} 
	 * 
	 */
	public abstract String executeOnHost(Entry<String, String> device, String objectNickname, Object[] args);

	
	/**
	 * Creates a global variable with name equals key. JCL uses the user object 
	 * instance instead of creating an instance internally. It does not works with user types. 
	 * Only Java types.Return Future &gt; Boolean &lt;, so this method is asynchronous, to lock use Future.get().  
	 * 
	 * @param key - The variable identifier.
	 * @param instance - A user variable object.
	 * @return Return Future &gt; Boolean &lt; true if the variable is registered. False otherwise.
	 * 
	 * @see #instantiateGlobalVarAsy(Object key, Object instance, String classVar, boolean Registers)
	 * @see #destroyGlobalVar(Object key)
	 */
	public abstract Future<Boolean> instantiateGlobalVarAsy(Object key, Object instance, String classVar, boolean Registers);

}
