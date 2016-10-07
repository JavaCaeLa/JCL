/**
 * To implement Java Ca&La we need just few interfaces
 */
package interfaces.kernel;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.Future;

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
	
	/**
	 * JCL Pacu instance
	 */
	public static final JCL_facade Pacu = JCL_FacadeImpl.getInstancePacu();
	
	/**
	 * JCL Lambari instance
	 */
	public static final JCL_facade Lambari = JCL_FacadeImpl.getInstanceLambari();
	
	/**
	 * 
	 * Get a list of times os the task.1- Request; 2- Arrive on Host; 3- Start exec or Send to new Host; 4- End exec or arrive o new host
	 * 5 - leave the Host or Start exec; 6 - Arrive result on client or or End exec; 7 - leave new the Host; 8 -Arrive result.      
	 * @param ticket - task identification.
	 * @return A list of times(nanoseconds).
	 * 
	 */
	public abstract List<Long> getTaskTimes(Future<JCL_result> ticket);
	
	/**
	 * 
	 * Get  the total time : timeline(8) - timeline(1).      
	 * @param ticket - task identification.
	 * @return times in nanoseconds.
	 * 
	 */
	public abstract Long getTotaltime(Future<JCL_result> ticket);
	
	/**
	 * 
	 * Get  the Queue time : (timeline(3) - timeline(2)) + (timeline(5) - timeline(4)).      
	 * @param ticket - task identification.
	 * @return times in nanoseconds.
	 * 
	 */
	public abstract Long getQueuetime(Future<JCL_result> ticket);
	
	/**
	 * 
	 * Get  the Execution time : timeline(6) - timeline(5).      
	 * @param ticket - task identification.
	 * @return times in nanoseconds.
	 * 
	 */
	public abstract Long getExecutiontime(Future<JCL_result> ticket);
	
	/**
	 * 
	 * Get  the Result retrieval time : timeline(7) - timeline(6).      
	 * @param ticket - task identification.
	 * @return times in nanoseconds.
	 * 
	 */
	public abstract Long getResultretrievaltime(Future<JCL_result> ticket);
	
	/**
	 * 
	 * Get  the Host time : timeline(3) - timeline(2).      
	 * @param ticket - task identification.
	 * @return times in nanoseconds.
	 * 
	 */
	public abstract Long getHosttime(Future<JCL_result> ticket);
	
	/**
	 * 
	 * Get  the Network time : ((timeline(6) - timeline(1)) - (timeline(5) - timeline(2))).      
	 * @param ticket - task identification.
	 * @return times in nanoseconds.
	 * 
	 */
	public abstract Long getNetworktime(Future<JCL_result> ticket);
	
	
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
	 * @return the All Host JVM memory.
	 * 
	 */
	public abstract List<Entry<Entry<String,String>, Long>> getAllDevicesMemory();

	/**
	 * 
	 * Get Host computer memory.      
	 * @param device - id do Host.
	 * @return the Host computer JVM memory.
	 * 
	 */
	public abstract Long getHostMemory(Entry<String,String> device);
	
	/**
	 * 
	 * Get Host computer cpu usage.      
	 * @param device - id do Host.
	 * @return the Host computer cpu usage.
	 * 
	 */
	public abstract Long getDeviceCpuUsage(Entry<String,String> device);
	
	/**
	 * 
	 * Get All Host computer cpu usage.      
	 * @return the All Host computer cpu usage.
	 * 
	 */
	public abstract List<Entry<Entry<String,String>, Long>> getAllDeviceCpuUsage();
	
	/**
	 * 
	 * Get local computer cpu usage.      
	 * @return the All Host computer cpu usage.
	 * 
	 * @see #getLocalcpuUsage()
	 */
	public abstract Long getLocalcpuUsage();
}
