/**
 * To implement Java Ca&La we need just few interfaces
 */
package interfaces.kernel;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import implementations.dm_kernel.user.JCL_FacadeImpl;

//
//import java.io.File;
//import java.util.List;
//import java.util.Map;
//import java.util.concurrent.Future;
//

/**
 * @author Andre Almeida
 * @version 1.0
 * 
 * - The developer API for JCL IoT 
 * 
 */

public interface JCL_CPfacade{
	
	public static final JCL_facade Pacu = JCL_FacadeImpl.getInstancePacu();
	public static final JCL_facade Lambari = JCL_FacadeImpl.getInstanceLambari();
	
	/**
	 * 
	 * Get a list of times os the task.1- Request; 2- Arrive on Host; 3- Start exec or Send to new Host; 4- End exec or arrive o new host
	 * 5 - leave the Host or Start exec; 6 - Arrive result on client or or End exec; 7 - leave new the Host; 8 -Arrive result.      
	 * @param ID - task identification.
	 * @return A list of times(nanoseconds).
	 * 
	 * @see #getTotaltime(String ID)
	 */
	public abstract List<Long> getTaskTimes(String ID);
	
	/**
	 * 
	 * Get  the total time : timeline(8) - timeline(1).      
	 * @param ID - task identification.
	 * @return times in nanoseconds.
	 * 
	 * @see #getTotaltime(String ID)
	 */
	public abstract Long getTotaltime(String ID);
	
	/**
	 * 
	 * Get  the Queue time : (timeline(3) - timeline(2)) + (timeline(5) - timeline(4)).      
	 * @param ID - task identification.
	 * @return times in nanoseconds.
	 * 
	 * @see #getQueuetime(String ID)
	 */
	public abstract Long getQueuetime(String ID);
	
	/**
	 * 
	 * Get  the Execution time : timeline(6) - timeline(5).      
	 * @param ID - task identification.
	 * @return times in nanoseconds.
	 * 
	 * @see #getExecutiontime(String ID)
	 */
	public abstract Long getExecutiontime(String ID);
	
	/**
	 * 
	 * Get  the Result retrieval time : timeline(7) - timeline(6).      
	 * @param ID - task identification.
	 * @return times in nanoseconds.
	 * 
	 * @see #getResultretrievaltime(String ID)
	 */
	public abstract Long getResultretrievaltime(String ID);
	
	/**
	 * 
	 * Get  the Host time : timeline(3) - timeline(2).      
	 * @param ID - task identification.
	 * @return times in nanoseconds.
	 * 
	 * @see #getHosttime(String ID)
	 */
	public abstract Long getHosttime(String ID);
	
	/**
	 * 
	 * Get  the Network time : ((timeline(6) - timeline(1)) - (timeline(5) - timeline(2))).      
	 * @param ID - task identification.
	 * @return times in nanoseconds.
	 * 
	 * @see #getNetworktime(String ID)
	 */
	public abstract Long getNetworktime(String ID);
	
	
	/**
	 * 
	 * Get Local computer memory.       
	 * @return the JVM memory.
	 * 
	 * @see #getLocalmemory()
	 */
	public abstract Long getLocalmemory();
	
	/**
	 * 
	 * Get All Host computer memory.      
	 * @param ID - id do Host.
	 * @return the All Host JVM memory.
	 * 
	 * @see #getAllHostmemory(String ID)
	 */
	public abstract Long getAllHostmemory(String ID);

	/**
	 * 
	 * Get Host computer memory.      
	 * @param ID - id do Host.
	 * @return the Host computer JVM memory.
	 * 
	 * @see #getHostmemory(String ID)
	 */
	public abstract Long getHostmemory(String ID);
	
	/**
	 * 
	 * Get Host computer cpu usage.      
	 * @param ID - id do Host.
	 * @return the Host computer cpu usage.
	 * 
	 * @see #getHostcpuUsage(String ID)
	 */
	public abstract Long getHostcpuUsage(String ID);
	
	/**
	 * 
	 * Get All Host computer cpu usage.      
	 * @param ID - id do Host.
	 * @return the All Host computer cpu usage.
	 * 
	 * @see #getAllHostcpuUsage(String ID)
	 */
	public abstract Long getAllHostcpuUsage(String ID);
	
	/**
	 * 
	 * Get local computer cpu usage.      
	 * @return the All Host computer cpu usage.
	 * 
	 * @see #getLocalcpuUsage()
	 */
	public abstract Long getLocalcpuUsage();
}
