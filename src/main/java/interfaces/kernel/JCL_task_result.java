/**
 * 
 */
package interfaces.kernel;

import java.util.Comparator;

/**
 * @author Joubert
 * @version 1.0
 * It stores the results of the tasks started by the developer. JCL
 * stores these results in the caller machine
 */


public interface JCL_task_result<T> {
	
	/**
	 * @param key - The task key, executed remotely.
	 * @param result - The result of the executed task.
	 */
	public abstract void putResult(String key, T result);
	
	/**
	 * @param key - the task key, executed remotely.
	 * @return any possible result. Null indicates no available result.
	 */
	public abstract T getResult(String key);
	
	/**
	 * @param key - The task key, executed remotely.
	 */
	public abstract void removeResult(String key);
	
	/**
	 * @param comparator - The comparator of some specific results. 
	 * Java wrapper classes (Integer, Float, etc.) do not need comparator.
	 */
	public abstract void setComparator (Comparator<T> comparator);

}
