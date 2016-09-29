/**
 * 
 */
package interfaces.kernel;

import java.util.Map;

/**
 * @author Andre
 *
 */
public interface JCLMap<K, V> extends Map<K,V>{
	public abstract V getLock(Object key);
	public abstract V putUnLock(K key, V value);
}
