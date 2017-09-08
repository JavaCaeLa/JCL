/**
 * To implement Java Ca&La we need just few interfaces
 */
package interfaces.kernel;

import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.Future;

import implementations.dm_kernel.user.JCL_FacadeImpl;

/**
 * @author JCL team
 * @version 1.0
 *
 * The developer API for capacity planning
 *
 *  JCL adopts the concept of task cost, so the developer can obtain how a certain
 *  task costs in terms of runtime and memory consumption
 *
 */

public interface JCL_CPfacade{

	/**
	 * JCL Pacu instance
	 */
	public static final JCL_FacadeImpl PacuHPC = (JCL_FacadeImpl)JCL_FacadeImpl.getInstancePacu();

	/**
	 * JCL Lambari instance
	 */
	public static final JCL_facade LambariHPC = JCL_FacadeImpl.getInstanceLambari();



	/**
	 *
	 * Get a list of times for task, representing its cost. The times are: 1- Request; 2- handle by Host; 3- Start executing or Send to a new Host; 4- Finishes the execution or handle by a new host
	 * 5 - leave the Host or Start execution; 6 - Arrive the result at client or or End execution; 7 - leave the new the Host; 8 - Arrive the result at client.
	 * @param ticket - task identification.
	 * @return A list of times in nanoseconds.
	 *
	 */
	public abstract List<Long> getTaskTimes(Future<JCL_result> ticket);

	/**
	 *
	 * Get  the total time : timeline(8) - timeline(1).
	 * @param ticket - task identification.
	 * @return a time in nanoseconds.
	 *
	 */
	public abstract Long getTotalTime(Future<JCL_result> ticket);

	/**
	 *
	 * Get  the Queue time : (timeline(3) - timeline(2)) + (timeline(5) - timeline(4)).
	 * @param ticket - task identification.
	 * @return a time in nanoseconds.
	 *
	 */
	public abstract Long getQueueTime(Future<JCL_result> ticket);

	/**
	 *
	 * Get  the Execution time : timeline(6) - timeline(5).
	 * @param ticket - task identification.
	 * @return a time in nanoseconds.
	 *
	 */
	public abstract Long getExecutionTime(Future<JCL_result> ticket);

	/**
	 *
	 * Get  the Result retrieval time : timeline(7) - timeline(6).
	 * @param ticket - task identification.
	 * @return a time in nanoseconds.
	 *
	 */
	public abstract Long getResultRetrievalTime(Future<JCL_result> ticket);

	/**
	 *
	 * Get  the Host time : timeline(3) - timeline(2).
	 * @param ticket - task identification.
	 * @return a time in nanoseconds.
	 *
	 */
	public abstract Long getHostTime(Future<JCL_result> ticket);

	/**
	 *
	 * Get  the Network time : ((timeline(6) - timeline(1)) - (timeline(5) - timeline(2))).
	 * @param ticket - task identification.
	 * @return a time in nanoseconds.
	 *
	 */
	public abstract Long getNetworkTime(Future<JCL_result> ticket);


	/**
	 * Get  the memory consumption of a specific task.
	 * @param ticket - task identification.
	 * @return the memory consumption in bytes
	 *
	 */
	public abstract Long getMemory(Future<JCL_result> ticket);

//	/**
//	 * Gets JCL version, i.e. Lambari version or Pacu version.
//	 *
//	 * @return The JCL version, Lambari or Pacu.
//	 *
//	 * @see #getDevices()
//	 */
//	public abstract String getVersion();

	/**
	 * Gets the Server internal time.
	 *
	 * @return Server time.
	 *
	 * @see #getVersion()
	 */
	public abstract Long getServerTime();

	/**
	 * Gets the Device internal time.
	 *
	 * @param device is a specific Host
	 * @return Device time.
	 *
	 * @see #getServerTime()
	 */
	public abstract Long getDeviceTime(Entry<String, String> device);

//	/**
//	 * Gets the Super-peer internal time.
//	 *
//	 * @param device is a specific super-peer
//	 * @return Super-peer time.
//	 *
//	 * @see #getDeviceTime()
//	 */
//	public abstract Long getSuperPeerTime(Entry<String, String> device);

	/**
	 * Gets the Server available memory.
	 *
	 * @return Server available memory.
	 *
	 */
	public abstract Long getServerMemory();

	/**
	 * Gets the Device available memory.
	 *
	 * @param device is a specific Host
	 * @return Device available memory.
	 *
	 */
	public abstract Long getDeviceMemory(Entry<String, String> device);

//	/**
//	 * Gets the Super-peer available memory.
//	 *
//	 * @param device is a specific super-peer
//	 * @return Super-peer available memory.
//	 *
//	 */
//	public abstract Long getSuperPeerMemory(Entry<String, String> device);

	/**
	 * Gets the Server CPU usage.
	 *
	 * @return Server CPU usage.
	 *
	 */
	public abstract Long getServerCpuUsage();

	/**
	 * Gets the Device CPU usage.
	 *
	 * @param device is a specific Host
	 * @return Device available memory.
	 *
	 */
	public abstract Long getDeviceCpuUsage(Entry<String, String> device);

//	/**
//	 * Gets the Super-peer CPU usage.
//	 *
//	 * @param device is a specific super-peer
//	 * @return Super-peer CPU usage.
//	 *
//	 */
//	public abstract Long getSuperPeerCpuUsage(Entry<String, String> device);


}
