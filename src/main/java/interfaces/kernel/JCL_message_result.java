package interfaces.kernel;


/**
 * @author Joubert
 * @version 1.0
 * 
 * Enables task result messages in JCL.
 */

public interface JCL_message_result extends JCL_message{
		
	/**
	 * Returns the result in the message.
	 * @return The result in the message.
	 */
	public abstract JCL_result getResult();
	
	/**
	 * Sets a result in the message.
	 * @param result - A result to be set in the message.
	 */
	public abstract void setResult(JCL_result result);
	
	
	/**
	 * Get the Msg type of the class.
	 */
	public abstract int getMsgType();

}
