/**
 * 
 */
package interfaces.kernel;

import java.io.Serializable;

/**
 * @author Joubert
 * @version 1.0
 * 
 * JCL stores huge amount of statistics and maps of resources and tasks.
 * JCL_state is used to such a requirement 
 * to recover from a crash JCL uses such states
 *
 */
public interface JCL_state extends Serializable {
	

}
