package interfaces.kernel;

import java.util.Map;


/**
 * @author Joubert
 * @version 1.0
 * 
 * enables task messages in JCL
 */

public interface JCL_message_metadata extends JCL_message{
		
	
	/**
	 * Returns the metadata of the device.
	 * @return The metadata of the device.
	 */
	public Map<String, String> getMetadados();

	/**
	 * Sets the metadata of the device.
	 * @param metadados - metadata of the device.
	 */
	public void setMetadados(Map<String, String> metadados);	

	
	/**
	 * Get the Msg type of the class.
	 */
	public abstract int getMsgType();	
}
