package interfaces.kernel;

import java.io.File;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import javassist.CtClass;

/**
 * ORB is the main component of JCL. It locates the object,
 * solving location and name transparencies, and calls a stub 
 * to proceed the rpc. Communication Fault tolerance are implemented in orbs.
 * @author Joubert Lima
 * @version 1.0
 */
public interface JCL_orb <R extends JCL_result>{
	
	
	/**
	 * @param mainComponentClass The class to be executed remotely.
	 * @param nickName The class name to be used by JCL.
	 * @return True if it has successfully registered, false otherwise.
	 */
	public abstract boolean register(String mainComponentClass, String nickName);
	/**
	 * @param mainComponentClass The class to be executed remotely.
	 * @param nickName The class name to be used by JCL.
	 * @return True if it has successfully registered, false otherwise.
	 */
	public abstract boolean register(Class<?> mainComponentClass, String nickName);
	
	//public abstract boolean register(CtClass cc, String nickName);

	/**
	 * @param jars - The jar file (first file) and its dependencies
	 * (the remaining files) registered.
	 * @param classToBeExecuted - The class name to be used by JCL. No class packages. 
	 * @return True if it has successfully registered, false otherwise.
	 */
	public boolean register(File[] jars, String classToBeExecuted);
	
		
	/**
	 * @param task - The task to be executed.
	 * @param results - Map of result.
	 * 
	 */
	public abstract void execute(JCL_task task);
	
	/**
	 * @param key - The variable key.
	 * @param className - The class to be executed to instantiate the global var.
	 * @param jars - Jar list with className class.
	 * @param defaultVarValue - The constructor variable arguments.
	 * @return The instance if the variable is created. null otherwise.
	 */
	public abstract boolean instantiateGlobalVar(Object key, String className, File[] jars, Object[] defaultVarValue);
	
	public abstract boolean instantiateGlobalVar(Object key, String nickName, Object[] defaultVarValue);


	/**
	 * @param key - The variable key.
	 * @param instance - A user variable value.
	 * @return True if the variable is registered. False otherwise.
	 */
	public abstract boolean instantiateGlobalVar(Object key, Object instance);

	/**
	 * @param key - The variable key.
	 * @return True if the variable is set null. False otherwise.
	 * If the variable is previously locked the destroy will return false.
	 * The user must unlock first and destroy after.
	 */
	public abstract boolean destroyGlobalVar(Object key);

	
	/**
	 * Updates the variable value. The setValue is not performed if a variable is previously locked.  
	 * 
	 * @param key - Global variable key.
	 * @param value - Global variable value.
	 * @return True if value is correctly updated or false otherwise.
	 */
	public abstract boolean setValue(Object key, Object value);
	
	/**
	 * Updates the variable value. Unlock a variable if it is previously locked.  
	 * 
	 * @param key - Global variable key.
	 * @param value - Global variable value.
	 * @return True if value is correctly updated or false otherwise.
	 */
	public abstract boolean setValueUnlocking(Object key, Object value);
	
	
	/**
	 * @param key - Global variable key.
	 * @return The global variable value or an error or null if no variable exists.
	 */
	public abstract JCL_result getValue(Object key);
	
	
	/**
	 * @param key - Global variable ID.
	 * @return return true if global variable is lock or false if global variable is unlock. 
	 * 
	 *  boolean isLock(Object key).
	 */
	public abstract boolean isLock(Object key);

	/**
	 * @param key - Global variable ID.
	 * @return The global variable value or an error or null if no variable exists. 
	 * 
	 * The pair getValueLocking and setValueUnlocking implements critical sections in JCL.
	 */
	public abstract JCL_result getValueLocking(Object key);
	

	/**
	 * @param nickName The class name to be removed by JCL.
	 * @return True if it is removed, false otherwise.
	 */
	public abstract boolean unRegister(String nickName);
	
	/**
	 * Verify if there is a task registered in JCL.
	 * 
	 * @param nickName The task name.
	 * @return True if JCL contains such a task, false otherwise.
	 */
	public abstract boolean containsTask(String nickName);
	
	/**
	 * Verify if there is a global variable registered in JCL.
	 * 
	 * @param key -  global var ID.
	 * @return True if JCL contains such a global variable, false otherwise.
	 */
	public abstract boolean containsGlobalVar(Object key);
	
	/**
	 * @return return true if all host and server was clean. 
	 * 
	 *  boolean cleanEnvironment().
	 */
	public abstract boolean cleanEnvironment();
	
	public abstract Set<Entry<Object, Object>> getGlobalVarEntrySet();
	public abstract boolean unLockGlobalVar(Object key);
	public abstract boolean lockGlobalVar(Object key);
	public abstract Map<Long, R> getResults();
	public abstract void setResults(Map<Long, R> results);
	public abstract boolean registerGV(Class<?> gvClass, String nickName);
}
