package interfaces.kernel;


/**
 * @author Joubert
 * @version 1.0
 * 
 * Enables any message coded as an array of Long. such messages are used to call servers and slaves
 */
public interface JCL_message_long extends JCL_message{
	
	/**
	 * Returns any type of information coded as Long[].
	 * @return the coded information 
	 */
	public abstract Long[] getRegisterData();
	/**
	 * Set the message content.
	 * @param data the content coded as a Long[]
	 */
	public abstract void setRegisterData(Long...data);
	
	
	/**
	 * Get the Msg type of the class.
	 */
	public abstract int getMsgType();

}
