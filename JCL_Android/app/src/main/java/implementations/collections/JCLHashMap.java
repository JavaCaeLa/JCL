package implementations.collections;

import implementations.dm_kernel.user.JCL_FacadeImpl.Holder;
import interfaces.kernel.JCL_facade;
import interfaces.kernel.JCL_map;

import java.io.*;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;



public class JCLHashMap<K,V>
        extends Holder
        implements JCL_map<K,V>, Cloneable, Serializable
{

    private static final long serialVersionUID = -3476453350396829516L;
    private Map<K,V> currentMap;
    private final JCL_facade DEFAULT_JCL = super.getInstance();
    private final static Map<String,Map> colletionMap = new HashMap<String,Map>();

    /**
     * Constructs with HashMap name.
     */
    public JCLHashMap(String gvName){

        //Started JCL Map on Lambari version
        if (DEFAULT_JCL instanceof implementations.sm_kernel.JCL_FacadeImpl){
            if (colletionMap.containsKey(gvName)){
                currentMap = colletionMap.get(gvName);
            }else{
                currentMap = new HashMap<K,V>();
                colletionMap.put(gvName, currentMap);
            }
            //Started JCL Map on Pacu version
        }else{
            currentMap = new JCLHashMapPacu<K, V>(gvName);
        }
    }

    /**
     * Constructs with HashMap name.
     */
    public JCLHashMap(String gvName,String ClassName,File[] f){

        //Started JCL Map on Lambari version
        if (DEFAULT_JCL instanceof implementations.sm_kernel.JCL_FacadeImpl){
            if (colletionMap.containsKey(gvName)){
                currentMap = colletionMap.get(gvName);
            }else{
                currentMap = new HashMap<K,V>();
                colletionMap.put(gvName, currentMap);
            }
            //Started JCL Map on Pacu version
        }else{
            currentMap = new JCLHashMapPacu<K, V>(gvName,ClassName,f);
        }
    }

    /**
     * Constructs with HashMap name.
     */
    public JCLHashMap(String gvName,String ClassName,Class<?> f){

        //Started JCL Map on Lambari version
        if (DEFAULT_JCL instanceof implementations.sm_kernel.JCL_FacadeImpl){
            if (colletionMap.containsKey(gvName)){
                currentMap = colletionMap.get(gvName);
            }else{
                currentMap = new HashMap<K,V>();
                colletionMap.put(gvName, currentMap);
            }
            //Started JCL Map on Pacu version
        }else{
            currentMap = new JCLHashMapPacu<K, V>(gvName,ClassName,f);
        }
    }


    /**
     * Returns the number of key-value mappings in this map.
     *
     * @return the number of key-value mappings in this map
     */
    public int size(){
        return currentMap.size();
    }

    /**
     * Returns <tt>true</tt> if this map contains no key-value mappings.
     *
     * @return <tt>true</tt> if this map contains no key-value mappings
     */
    public boolean isEmpty(){
        return currentMap.isEmpty();
    }

    /**
     * Returns the value to which the specified key is mapped.
     */
    public V get(Object key){
        return currentMap.get(key);
    }

    /**
     * Returns and lock the value to which the specified key is mapped.
     */
    public V getLock(Object key){

        if (DEFAULT_JCL instanceof implementations.sm_kernel.JCL_FacadeImpl){
            return currentMap.get(key);
        }else{
            return (V)((JCL_map)currentMap).getLock(key);
        }
    }

    /**
     * Returns <tt>true</tt> if this map contains a mapping for the
     * specified key.
     *
     * @param   key   The key whose presence in this map is to be tested
     * @return <tt>true</tt> if this map contains a mapping for the specified
     * key.
     */
    public boolean containsKey(Object key){
        return currentMap.containsKey(key);
    }

    /**
     * Associates the specified value with the specified key in this map.
     * If the map previously contained a mapping for the key, the old
     * value is replaced.
     *
     * @param key key with which the specified value is to be associated
     * @param value value to be associated with the specified key
     * @return the previous value associated with <tt>key</tt>, or
     *         <tt>null</tt> if there was no mapping for <tt>key</tt>.
     */
    public V put(K key, V value){
        return currentMap.put(key, value);
    }


    /**
     * Associates the specified value with the specified key in this map.
     * If the map previously contained a mapping for the key, the old
     * value is replaced.
     *
     * @param key key with which the specified value is to be associated
     * @param value value to be associated with the specified key
     * @return the previous value associated with <tt>key</tt>, or
     *         <tt>null</tt> if there was no mapping for <tt>key</tt>.
     */

    public V putUnlock(K key, V value){
        if (DEFAULT_JCL instanceof implementations.sm_kernel.JCL_FacadeImpl){
            return currentMap.put(key,value);
        }else{
            return (V)((JCL_map)currentMap).putUnlock(key, value);
        }

    }

    /**
     * Copies all of the mappings from the specified map to this map.
     * These mappings will replace any mappings that this map had for
     * any of the keys currently in the specified map.
     *
     * @param m mappings to be stored in this map
     * @throws NullPointerException if the specified map is null
     */
    public void putAll(Map<? extends K, ? extends V> m) {
        currentMap.putAll(m);
    }

    /**
     * Removes the mapping for the specified key from this map if present.
     *
     * @param  key key whose mapping is to be removed from the map
     * @return the previous value associated with <tt>key</tt>, or
     *         <tt>null</tt> if there was no mapping for <tt>key</tt>.
     *         (A <tt>null</tt> return can also indicate that the map
     *         previously associated <tt>null</tt> with <tt>key</tt>.)
     */
    public V remove(Object key) {
        return currentMap.remove(key);
    }

    /**
     * Removes all of the mappings from this map.
     * The map will be empty after this call returns.
     */
    public void clear() {
        currentMap.clear();
    }

    /**
     * Returns <tt>true</tt> if this map maps one or more keys to the
     * specified value.
     *
     * @param value value whose presence in this map is to be tested
     * @return <tt>true</tt> if this map maps one or more keys to the
     *         specified value
     */
    public boolean containsValue(Object value) {
        return currentMap.containsValue(value);
    }

    public Set<K> keySet(){
        return currentMap.keySet();
    }

    /**
     * Returns a {@link Collection} view of the values contained in this map.
     * The collection is backed by the map, so changes to the map are
     * reflected in the collection, and vice-versa.  If the map is
     * modified while an iteration over the collection is in progress
     * (except through the iterator's own <tt>remove</tt> operation),
     * the results of the iteration are undefined.  The collection
     * supports element removal, which removes the corresponding
     * mapping from the map, via the <tt>Iterator.remove</tt>,
     * <tt>Collection.remove</tt>, <tt>removeAll</tt>,
     * <tt>retainAll</tt> and <tt>clear</tt> operations.  It does not
     * support the <tt>add</tt> or <tt>addAll</tt> operations.
     */
    public Collection<V> values() {
        return currentMap.values();
    }

    /**
     * Returns a {@link Set} view of the mappings contained in this map.
     * The set is backed by the map, so changes to the map are
     * reflected in the set, and vice-versa.  If the map is modified
     * while an iteration over the set is in progress (except through
     * the iterator's own <tt>remove</tt> operation, or through the
     * <tt>setValue</tt> operation on a map entry returned by the
     * iterator) the results of the iteration are undefined.  The set
     * supports element removal, which removes the corresponding
     * mapping from the map, via the <tt>Iterator.remove</tt>,
     * <tt>Set.remove</tt>, <tt>removeAll</tt>, <tt>retainAll</tt> and
     * <tt>clear</tt> operations.  It does not support the
     * <tt>add</tt> or <tt>addAll</tt> operations.
     *
     * @return a set view of the mappings contained in this map
     */
    public Set<Map.Entry<K,V>> entrySet() {
        return currentMap.entrySet();
    }

    public void destroy(){
        DEFAULT_JCL.destroy();
    }
}