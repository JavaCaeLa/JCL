package interfaces.kernel;


/**
 * @author Joubert
 * @version 1.0
 * 
 * enables any control message used to manage Java Ca&amp;La
 */
public interface JCL_message_control extends JCL_message{
	
	/**
	 * Returns any type of information coded as char[]
	 * @return the coded information 
	 */
	public abstract String[] getRegisterData();
	
	/**
	 * Set the message content
	 * @param data the content coded as a char[]
	 */
	public abstract void setRegisterData(String...data);
	
	
	/**
	 * Get the Msg type of the class.
	 */
	public abstract int getMsgType();
}
