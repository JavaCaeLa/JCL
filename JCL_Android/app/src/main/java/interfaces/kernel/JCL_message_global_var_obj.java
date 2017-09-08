package interfaces.kernel;

/**
 * @author Joubert
 * @version 1.0
 * 
 * enables global var message used by Java Ca&La to store Java or user typed objects everywhere
 */
/**
 * @author UFOP
 *
 */
public interface JCL_message_global_var_obj extends JCL_message_global_var{
	
	/**
	 * Returns the name of the variable.
	 * @return The name of the variable.
	 */	
//	public abstract Object getVarKey();
	/**
	 * Return the jars of the variable.
	 * @return A set of jar files contents.
	 */
//	public abstract byte[][] getJars();
	/**
	 * @return The global variable constructor values.
	 */
	public abstract Object[] getDefaultValues();
	
	/**
	 * @return The variable object.
	 */
//	public abstract Object getVarInstance();
	
	/**
	 * @return The names of jar files.
	 */
//	public abstract String[] getJarsNames();
	/**
	 * @return The user nickname of the global var.
	 */
	public abstract String getNickName();
	
	
	/**
	 * Get the Msg type of the class.
	 */
	public abstract int getMsgType();

	
}
