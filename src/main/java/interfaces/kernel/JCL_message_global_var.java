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
public interface JCL_message_global_var extends JCL_message{
	
	/**
	 * Returns the key of the variable.
	 * @return The key of the variable.
	 */	
	public abstract Object getVarKey();
	
	/**
	 * @return The variable object.
	 */
	public abstract Object getVarInstance();
		
	/**
	 * Get the Msg type of the class.
	 */
	public abstract int getMsgType();

	
}
