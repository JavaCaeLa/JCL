package interfaces.kernel;

import java.io.File;

/**
 * @author Joubert
 * @version 1.0
 * 
 * enables JCL register a JAVA component (jars). After a registration an execution is possible in JCL.
 */
public interface JCL_message_register extends JCL_message{
		
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
	 * Returns all the JAR files in bytes
	 * @return The JAR files.
	 */
	public abstract byte[][] getJars();
	
	/**
	 * Sets the JAR files.
	 * @param files - Array of files to be set.
	 */
	public abstract void setJars(File[] files);
	
	/**
	 * Sets the JAR files.
	 * @param files - Array of bytes.
	 */
	public abstract void setJars(byte[][] files);
	
	/**
	 * Gets all the names of the registered JAR files.
	 * @return A String array that contains the names of the JAR files.
	 */
	public abstract String[] getJarsNames();

	/**
	 * Sets all the names of the registered JAR files.
	 * @param files - Array of files to have their names set to the message.
	 */
	public abstract void setJarsNames(File[] files);
	
	/**
	 * Sets all the names of the registered JAR files.
	 * @param files - Array of files to have their names set to the message.
	 */
	public abstract void setJarsNames(String[] files);
	
	/**
	 * Get the Msg type of the class.
	 */
	public abstract int getMsgType();
	
}