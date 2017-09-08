package implementations.collections;

import commom.Constants;
import implementations.dm_kernel.user.JCL_FacadeImpl.Holder;
import interfaces.kernel.JCL_facade;
import interfaces.kernel.JCL_map;
import interfaces.kernel.JCL_message_generic;
import interfaces.kernel.JCL_result;
import java.io.*;
import java.util.AbstractCollection;
import java.util.AbstractSet;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;



public class JCLHashMapPacu<K,V>
        extends Holder
        implements JCL_map<K,V>, Cloneable, Serializable
{

    /**
     *
     */
    private static final long serialVersionUID = -4532275712761435044L;

    /**
     * Default JCL pacu instance.
     */
    private static JCL_facade DEFAULT_JCL;

    /**
     * The number of key-value mappings contained in this map.
     */
    private Map<String, String> Localize;

    /**
     * The number of key-value mappings contained in this map.
     */
    private int idLocalize;

    /**
     * The HashMap name in the cluster.
     */
    private String gvName;

    /**
     *Class name.
     */
    private String clName="";

    /**
     *Register Class.
     */
    private boolean regClass = false;



    /**
     * Constructs with HashMap name.
     */
    public JCLHashMapPacu(String gvName){
        this.gvName = gvName;

        //Get Pacu
        String a = new String();
        Properties properties = new Properties();
        try {
            properties.load(new FileInputStream(Constants.Environment.JCLConfig()));
        } catch (IOException e) {
            e.printStackTrace();
        }

        DEFAULT_JCL = super.getInstancePacu(properties);
        init();
    }

    /**
     * Constructs with HashMap name.
     */
    public JCLHashMapPacu(String gvName,String ClassName,File[] f){
        this.gvName = gvName;
        this.clName = ClassName;
        this.regClass = true;

        //Get Pacu
        Properties properties = new Properties();
        try {
            properties.load(new FileInputStream(Constants.Environment.JCLConfig()));
        } catch (IOException e) {
            e.printStackTrace();
        }

        DEFAULT_JCL = super.getInstancePacu(properties);
        DEFAULT_JCL.register(f, ClassName);
        init();
    }

    /**
     * Constructs with HashMap name.
     */
    public JCLHashMapPacu(String gvName,String ClassName,Class<?> f){
        this.gvName = gvName;
        this.clName = ClassName;
        this.regClass = true;

        //Get Pacu
        Properties properties = new Properties();
        try {
            properties.load(new FileInputStream(Constants.Environment.JCLConfig()));
        } catch (IOException e) {
            e.printStackTrace();
        }

        DEFAULT_JCL = super.getInstancePacu(properties);
        DEFAULT_JCL.register(f, ClassName);
        init();
    }


    // internal utilities
    void init(){

        List<java.util.Map.Entry<String, Map<String, String>>> hosts = super.getDeviceS();
        idLocalize = (Math.abs(gvName.hashCode())%hosts.size());

        if(!DEFAULT_JCL.containsGlobalVar(gvName)){
            Map<String, String> hostIp = hosts.get(idLocalize).getValue();
            super.createhashKey(gvName,clName, regClass,idLocalize);
            DEFAULT_JCL.instantiateGlobalVar(gvName, hostIp);
            Localize = hostIp;

        }else{

            Localize =  (Map<String, String>) DEFAULT_JCL.getValue(gvName).getCorrectResult();
        }
    }

    /**
     * Returns the number of key-value mappings in this map.
     *
     * @return the number of key-value mappings in this map
     */
    public int size(){
        return super.hashSize(gvName,idLocalize);
    }

    /**
     * Returns <tt>true</tt> if this map contains no key-value mappings.
     *
     * @return <tt>true</tt> if this map contains no key-value mappings
     */
    public boolean isEmpty(){
        if (super.hashSize(gvName,idLocalize) == 0){
            return true;
        }else{
            return false;
        }
    }

    /**
     * Returns the value to which the specified key is mapped.
     */
    public V get(Object key){
        V oldValue = null;
        if (key != null){
            oldValue = (V) DEFAULT_JCL.getValue(key.toString()+"¬Map¬"+gvName).getCorrectResult();
        }else{
            System.out.println("Can't get<K,V> with null key!");
        }
        return (oldValue == null ? null : oldValue);
    }

    /**
     * Returns and lock the value to which the specified key is mapped.
     */
    public V getLock(Object key){
        V oldValue = null;
        if (key != null){
            oldValue = (V) DEFAULT_JCL.getValueLocking(key.toString()+"¬Map¬"+gvName).getCorrectResult();
        }else{
            System.out.println("Can't get<K,V> with null key!");
        }
        return (oldValue == null ? null : oldValue);
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
        return super.containsKey(gvName,key,idLocalize);
    }

    final Entry<K,V> getEntry(Object key) {
        V value = (V) DEFAULT_JCL.getValue(key.toString()+"¬Map¬"+gvName).getCorrectResult();
        return new implementations.util.Entry(key,value);
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
        Object oldValue = null;
        if ((key != null) && ((oldValue = super.hashPut((key.toString()+"¬Map¬"+gvName), value,this.clName, this.regClass))!=null)){
            super.hashAdd(gvName,key,idLocalize);
        }else{
            System.out.println("Null key or fault in put<K,V> on cluster!");
        }

        return (V)oldValue;
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
        V oldValue = null;
        if (key != null){
            if(DEFAULT_JCL.containsGlobalVar(key.toString()+"¬Map¬"+gvName)){
                oldValue = (V) DEFAULT_JCL.getValue(key.toString()+"¬Map¬"+gvName).getCorrectResult();
                DEFAULT_JCL.setValueUnlocking((key.toString()+"¬Map¬"+gvName), value);
            }else if (DEFAULT_JCL.instantiateGlobalVar((key.toString()+"¬Map¬"+gvName), value)){
                super.hashAdd(gvName,key,idLocalize);
            }
        }else{
            System.out.println("Can't put<K,V> with null key!");
        }
        return (oldValue == null ? null : oldValue);
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
        int numKeysToBeAdded = m.size();

        if (numKeysToBeAdded == 0)
            return;
        super.instantiateBin((Object)m.entrySet(),this.clName, this.gvName, this.regClass);

        List<Object> obj =  new ArrayList<Object>();
        for(K key:m.keySet()){
            obj.add(key);
        }
        super.hashAdd(gvName, obj,idLocalize);
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
        V oldValue = null;

        if (key != null){
            if(DEFAULT_JCL.containsGlobalVar(key.toString()+"¬Map¬"+gvName)){
                oldValue = (V) DEFAULT_JCL.getValue(key.toString()+"¬Map¬"+gvName).getCorrectResult();
            }
            if (DEFAULT_JCL.deleteGlobalVar(key.toString()+"¬Map¬"+gvName)){
                super.hashRemove(gvName,key,idLocalize);
            }
        }else{
            System.out.println("Can't remove null key!");
        }
        return (oldValue == null ? null : oldValue);
    }

    protected V removeInt(Object key) {
        V oldValue = null;

        if (key != null){
            if(DEFAULT_JCL.containsGlobalVar(key.toString()+"¬Map¬"+gvName)){
                oldValue = (V) DEFAULT_JCL.getValue(key.toString()+"¬Map¬"+gvName).getCorrectResult();
            }
            if (DEFAULT_JCL.deleteGlobalVar(key.toString()+"¬Map¬"+gvName)){
                super.hashRemove(gvName,key,idLocalize);
            }
        }else{
            System.out.println("Can't remove null key!");
        }
        return (oldValue == null ? null : oldValue);
    }

    /**
     * Removes all of the mappings from this map.
     * The map will be empty after this call returns.
     */
    public void clear() {
        Set<K> table = super.hashClean(gvName,idLocalize);
        for(K key:table){
            if (DEFAULT_JCL.deleteGlobalVar(key.toString()+"¬Map¬"+gvName)){
                table.remove(key);
            }
        }
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
        Set<K> table = super.getHashSet(gvName,idLocalize);
        for(K key:table){
            Object valueGV = DEFAULT_JCL.getValue(key.toString()+"¬Map¬"+gvName).getCorrectResult();
            if(value.equals(valueGV)){
                return true;
            }
        }
        return false;
    }

    protected Set<K> getHashSet(String gvName){
        return super.getHashSet(gvName,idLocalize);
    }

    private abstract class HashIterator<E> implements Iterator<E> {

        Entry<K,V> current;     // current entry
        Iterator<java.util.Map.Entry<Integer, JCL_message_generic>> intGvList;
        Queue<Entry<K,V>> queue = new ConcurrentLinkedQueue();
        Queue<Future<JCL_result>> ticket = new LinkedList<>();

        Map<Integer,JCL_message_generic> gvList;
        int length = 0;
        double size = 0;

        HashIterator(){

            Set key = getHashSet(gvName);
            length = key.size();
            gvList = JCLHashMapPacu.super.getHashQueue(queue,key,gvName);
            intGvList = gvList.entrySet().iterator();

        }

        public final boolean hasNext() {
            try {

                if(queue.isEmpty()){
                    if (size==length){
                        return false;
                    }else{
                        if(ticket.isEmpty()){

                            System.err.println("FAULT: Can't retrive all datas!!!");
                        } else{

//        				JCLHashMapPacu.super.getResultBlocking(ticket.poll());

                            ticket.poll().get();

                            while(queue.isEmpty() && (!ticket.isEmpty())){

                                //       					JCLHashMapPacu.super.getResultBlocking(ticket.poll());
                                ticket.poll().get();
                            }
                        }
                        return true;
                    }
                } else{
                    return true;
                }
            } catch (InterruptedException | ExecutionException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                return false;
            }
        }

        final Entry<K,V> nextEntry() {
            current = queue.poll();

            double pag = size/(queue.size()+size);
            if(((pag>0.4) || (gvList.size()==1)) && (intGvList.hasNext())){
                java.util.Map.Entry<Integer, JCL_message_generic> entHost = intGvList.next();
                ticket.add(JCLHashMapPacu.super.getHashValues(queue, entHost.getValue(), entHost.getKey()));
            }


            size++;
            return current;
        }

        public void remove() {
            JCLHashMapPacu.this.remove(current.getKey());
        }

    }

    private final class ValueIterator extends HashIterator<V> {
        public V next() {
            return nextEntry().getValue();
        }
    }

    private final class KeyIterator extends HashIterator<K> {
        public K next() {
            return nextEntry().getKey();
        }
    }

    private final class EntryIterator extends HashIterator<Map.Entry<K,V>> {
        public Map.Entry<K,V> next() {
            return nextEntry();
        }
    }

    // Subclass overrides these to alter behavior of views' iterator() method
    Iterator<K> newKeyIterator(){
        return new KeyIterator();
    }
    Iterator<V> newValueIterator()   {
        return new ValueIterator();
    }
    Iterator<Map.Entry<K,V>> newEntryIterator(){
        return new EntryIterator();
    }


    // Views

    private transient Set<Map.Entry<K,V>> entrySet = null;

    /**
     * Returns a {@link Set} view of the keys contained in this map.
     * The set is backed by the map, so changes to the map are
     * reflected in the set, and vice-versa.  If the map is modified
     * while an iteration over the set is in progress (except through
     * the iterator's own <tt>remove</tt> operation), the results of
     * the iteration are undefined.  The set supports element removal,
     * which removes the corresponding mapping from the map, via the
     * <tt>Iterator.remove</tt>, <tt>Set.remove</tt>,
     * <tt>removeAll</tt>, <tt>retainAll</tt>, and <tt>clear</tt>
     * operations.  It does not support the <tt>add</tt> or <tt>addAll</tt>
     * operations.
     */
    public Set<K> keySet(){
        Set<K> ks = super.getHashSet(gvName,idLocalize);
        return (ks != null ? ks : (ks = new HashSet<K>()));
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
        Collection<V> vs = new Values();
        return vs;
    }

    private final class Values extends AbstractCollection<V> {
        public Iterator<V> iterator() {
            return newValueIterator();
        }
        public int size(){
            return JCLHashMapPacu.this.size();
        }
        public boolean contains(Object o) {
            return containsValue(o);
        }
        public void clear() {
            JCLHashMapPacu.this.clear();
        }
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
        return entrySet0();
    }

    private Set<Map.Entry<K,V>> entrySet0() {
        Set<Map.Entry<K,V>> es = entrySet;
        return es != null ? es : (entrySet = new EntrySet());
    }

    private final class EntrySet extends AbstractSet<Map.Entry<K,V>> {
        public Iterator<Map.Entry<K,V>> iterator() {
            return newEntryIterator();
        }
        public boolean contains(Object o) {
            if (!(o instanceof Map.Entry))
                return false;
            Map.Entry<K,V> e = (Map.Entry<K,V>) o;
            Entry<K,V> candidate = getEntry(e.getKey());
            return candidate != null && candidate.equals(e);
        }
        public boolean remove(Object o) {
            return (JCLHashMapPacu.this.remove(o)!=null);
        }

        public int size() {
            return JCLHashMapPacu.this.size();
        }

        public void clear() {
            JCLHashMapPacu.this.clear();
        }
    }

    public static void destroy(){
        if(DEFAULT_JCL!=null){
            DEFAULT_JCL.destroy();
        }else{
            System.exit(0);
        }
    }
}
