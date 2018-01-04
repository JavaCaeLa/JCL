package interfaces.kernel;

/**
 * @author Joubert
 * @version 1.0
 * 
 * enables task messages in JCL
 */

public interface JCL_message_task extends JCL_message{
		
	/**
	 * Returns the task in the message.
	 * @return The task in the message.
	 */
	public abstract JCL_task getTask();
	
	/**
	 * Sets a task in the message.
	 * @param t - Task to be set in the message.
	 */
	public abstract void setTask(JCL_task t);
	
	public abstract void setMessageRegister(JCL_message_register userRegister);

	public abstract JCL_message_register getMessageRegister();
	
	/**
	 * Get the Msg type of the class.
	 */
	public abstract int getMsgType();	

}
