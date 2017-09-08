/**
 * 
 */
package interfaces.kernel;

import java.util.Map;

/**
 * @author JCL Team
 * @version 1.0
 * 
 * This class extends the Java Map Interface, enabling thread-safe locks
 * The developer can acquire a pair <key, value> with a getLock call, locking its access to
 * other threads. To unlock, the developer calls putUnlock
 * 
 * JCL Map implementation is portable for multicore and multicomputer architectures. 
 * It is not sorted. s
 *
 */

public interface JCL_map<K, V> extends Map<K,V>{
	/**
	 * This method returns a pair <key, value> and locks such a map entry. Subsequence 
	 * calls to getLock the same entry will be queued and blocked until a putUnlock call
	 * occurs...
	 * @param <K> indicates the key of the map entry
	 * @return <V> returns the value stored in the map
	 */
	public abstract V getLock(K key);
	
	/**
	 * This method puts a pair <key, value> in a map and unlocks such a map entry
	 * @param <K> indicates the key of the map entry
	 * @param <V> indicates the value of the map entry.
	 */
	public abstract V putUnlock(K key, V value);
}
