package interfaces.kernel;


/**
 * @author Joubert
 * @version 1.0
 * 
 * Enables any message coded as an array of Long. such messages are used to call servers and slaves
 */
public interface JCL_message_class extends JCL_message{
	
	/**
	 * Returns any type of information coded as Long[].
	 * @return the coded information 
	 */
	public abstract byte[] getRegisterData();
	/**
	 * Set the message content.
	 * @param data the content coded as a Long[]
	 */
	public abstract void setRegisterData(byte[] data);
	/**
	 * Returns the name of the class.
	 * @return The name of the class.
	 */
	public abstract String getClassName();
	
	/**
	 * Sets the name of the class.
	 * @param className - The name of the class.
	 */
	public abstract void setClassName(String className);
	
	/**
	 * Get the Msg type of the class.
	 */
	public abstract int getMsgType();

}
