package interfaces.kernel;

import java.util.Map;

/**
 * @author Joubert
 * @version 1.0
 * 
 * enables task messages in JCL
 */

public interface JCL_message_list_task extends JCL_message{
			
	/**
	 * Returns the Queue of task in the message.
	 * @return The Queue of task in the message.
	 */
	public abstract Map<Long,JCL_task> getMapTask();
	
	/**
	 * Add a task in the message.
	 * @param key - Task key.
	 * @param t - Task to be set in the message.
	 */
	public abstract void addTask(Long key, JCL_task t);
	
	public abstract int taskSize();
	
	
	/**
	 * Get the Msg type of the class.
	 */
	public abstract int getMsgType();

	/**
	 * Set host and Port of the request user.
	 * @param host - Host of the user.
	 * @param port - Open port on the user.
	 */	
	public abstract void setHostPort(String host, int port);
}
