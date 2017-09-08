/**
 * 
 */
package interfaces.kernel;

import java.nio.ByteBuffer;

/**
 * @author Joubert
 * @version 1.0
 * 
 *  creates a RMI, TCP socket or UDP datagram connetions
 *
 */
public interface JCL_connector{
	
	/**
	 * Method to connect to a socket server with a port and an ip.
	 * 
	 * @param host - the machine host.
	 * @param port - the machine port.
	 * @param mac - the machine mac.
	 * @return true connection OK, false connection NOK.
	 * 
	 * @see #disconnect()
	 */
	public abstract boolean connect(String host, int port, String mac);
	
	/**
	 * Method to disconnect a socket connection.
	 * @return if connection has closed or not.
	 * 
	 * @see #connect(String host, int port, String mac)
	 */
	public abstract boolean disconnect();
	
	/**
	 * Enables synchronous communication. JCL is asynchronous. It uses
	 * threads and synchronous communication to provide asynchronous communications.
	 * 
	 * @param msg - the message to be sent
	 * @param idHost - id of the destination Host.
	 * @return a new message or null if nothing is received in timeout millisecs.
	 * 
	 * @see #sendReceive(JCL_message_control msg, Short idHost)
	 */
	public abstract JCL_message_result sendReceive(JCL_message msg, String idHost);

	

	/**
	 * Enables synchronous communication. JCL is asynchronous. It uses
	 * threads and synchronous communication to provide asynchronous communications.
	 * 
	 * @param msg - the message to be sent
	 * @param idHost - id of the destination Host.
	 * @return a new message or null if nothing is received in timeout millisecs.
	 * 
	 * @see #sendReceive(JCL_message msg, Short idHost)
	 */
	public abstract JCL_message_control sendReceive(JCL_message_control msg, String idHost);

	/**
	 * Enables synchronous communication. JCL is asynchronous. It uses
	 * threads and synchronous communication to provide asynchronous communications.
	 * 
	 * @param msg - the message to be sent.
	 * @param idHost - id of the destination Host.
	 * @return a new message or null if nothing is received in timeout millisecs.
	 * 
	 * @see #sendReceive(JCL_message msg, Short idHost)
	 */
	public abstract JCL_message sendReceiveG(JCL_message msg, String idHost);
		
	/**
	 * Receive a JCL_menssage
	 * 
	 * @return the message receive.
	 * 
	 * @see #receive()
	 */

	public abstract JCL_message receive();
	
	/**
	 * Send a JCL_menssage
	 * 
	 * @param msg - the message to be sent
	 * @param idHost - id of the destination Host.
	 * 
	 * @return true - send message OK, false send message NOK.
	 * 
	 * @see #receive()
	 */
	
	public abstract boolean send(JCL_message msg, String idHost);

	/**
	 * Send a JCL_menssage
	 * 
	 * @param msg - the message to be sent
	 * 
	 * @return true - send message OK, false send message NOK.
	 * 
	 * @see #send(JCL_message msg, Short idHost)
	 */
	public abstract ByteBuffer sendReceiveB(ByteBuffer msg);
}
