/**
 * 
 */
package interfaces.kernel;

import java.io.Serializable;
import java.util.List;

/**
 * A task is composed by an object name + object nickname + a method to 
 * be remotely invoked + the method args + the class to be executed and the task execution time
 * 
 * @author Joubert
 * @version 1.0
 *
 */
public interface JCL_task extends Serializable{
	
	/**
	 * Returns the task ID.
	 * @return The task ID.
	 */
	public abstract long getTaskID();
	
	/**
	 * Returns the object name.
	 * @return The object name.
	 */
	public abstract String getObjectName();
	
	/**
	 * Sets the object name.
	 * @param name - The object name to be set.
	 */
	public abstract void setObjectName(String name);
	
	/**
	 * Sets the Host.
	 * @param host - ip from host.
	 */
	public abstract void setHost(String host);

	/**
	 * Sets the port.
	 * @param port - open port on host.
	 */
	public abstract void setPort(int port);

	/**
	 * Returns Host ip.
	 * @return host ip.
	 */
	public abstract String getHost();

	/**
	 * Returns Host open port.
	 * @return Host open port.
	 */
	public abstract int getPort();

	/**
	 * Returns the class of the object.
	 * @return The class of the object.
	 */
	public abstract Class<?> getObjectClass();
	
	/**
	 * Sets the class of the object.
	 * @param userClass - The class to be set.
	 */
	public abstract void setObjectClass(Class<?> userClass);
	
	/**
	 * Returns the object method's name.
	 * @return The object method's name.
	 */
	public abstract String getObjectMethod();
		
	/**
	 * Returns the method parameters of the task.
	 * @return The method parameters of the task.
	 */
	public abstract Object[] getMethodParameters();
	
	/**
	 * Sets the task time.
	 * @param time - The task time to be set.
	 */
	public abstract void setTaskTime(Long time);
	
	/**
	 * Returns the task times.
	 * @return The task time.
	 */
	public abstract List<Long> getTaskTime();
	
	/**
	 * Set Task ID.
	 * @param id - Set task id.
	 */	
	public abstract void setTaskID(long id);

	/**
	 * Returns true if this task can run in other host.
	 * @return true: This task can run in other host; false: This task can not run in other host.
	 */
	public abstract boolean getHostChange();
	
	/**
	 * Set hostChange: true: can run in other host. false: cann't run in other host.
	 * @param hostChange - Set hostChange.
	 */	
	public abstract void setHostChange(boolean hostChange);		
}
