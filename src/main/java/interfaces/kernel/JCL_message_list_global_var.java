package interfaces.kernel;

import java.util.Map;

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
public interface JCL_message_list_global_var extends JCL_message{
	
	//Comentar
	public abstract Object putVarKeyInstance(Object key, Object instance);	
	//Comentar
	public abstract Map<Object,Object> getKeyValue();
	
	/**
	 * Get the Msg type of the class.
	 */
	public abstract int getMsgType();
	
}
