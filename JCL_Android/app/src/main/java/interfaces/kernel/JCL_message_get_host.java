package interfaces.kernel;

import java.util.List;
import java.util.concurrent.ConcurrentMap;


/**
 * @author Joubert
 * @version 1.0
 * 
 * Enables task result messages in JCL.
 */

public interface JCL_message_get_host extends JCL_message{
	
	/**
	 * Returns slave ID in the message.
	 * @return slave ID in the message.
	 */
	public List<String> getSlavesIDs();

	/**
	 * Sets slave ID in the message.
	 * @param slavesIDs - A slave IDs list to be set in the message.
	 */
	public void setSlavesIDs(List<String> slavesIDs);
	
	/**
	 * Returns Slave map in the message.
	 * @return The slave map in the message.
	 */
	public ConcurrentMap<String, String[]> getSlaves();

	/**
	 * Sets Slaves map in the message.
	 * @param slaves - A Slaves map to be set in the message.
	 */
	public void setSlaves(ConcurrentMap<String, String[]> slaves);
	
	/**
	 * Get the Msg type of the class.
	 */
	public abstract int getMsgType();

	public abstract String getMAC();

	public abstract void setMAC(String mAC);
}
