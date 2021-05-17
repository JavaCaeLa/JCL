package implementations.collections;

import implementations.dm_kernel.JCLTopicAdmin;
import implementations.dm_kernel.user.JCL_FacadeImpl.Holder;
import implementations.util.ObjectWrap;
import interfaces.kernel.JCL_facade;
import interfaces.kernel.JCL_map;
import interfaces.kernel.JCL_message_generic;
import interfaces.kernel.JCL_result;
import io.protostuff.LinkedBuffer;
import io.protostuff.ProtobufIOUtil;
import io.protostuff.Schema;
import io.protostuff.runtime.RuntimeSchema;
import java.io.*;
import java.net.InetAddress;

import implementations.util.ByteBuffer;
import implementations.util.KafkaMapConfigProperties;

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
import java.util.UUID;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;

import commom.Constants;
import commom.KafkaMapConsumerThread;
import commom.JCLResultResource;
import commom.JCLResultResourceContainer;
import commom.JCLResultSerializer;
import commom.JCL_resultImpl;

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

    private int size;
    
    /** begin 3.0 **/
    private Producer<String, JCL_result> kafkaProducer;
    private static JCLResultResourceContainer localResourceMapContainer;
    private String gvNameKafka;
    
    private static JCLTopicAdmin jclTopicAdmin;
    
    private static List<String> subscribedTopics;
    private static KafkaMapConsumerThread kafkaMapConsumerThread;
    private static JCLResultResource localResourceMap;
    private static KafkaMapConfigProperties kafkaMapConfigProperties;
    /** end 3.0 **/
    
    /**
     * Constructs with HashMap name.
     */
    public JCLHashMapPacu(String gvName){
    	this.gvName = gvName;

    	//Get Pacu
    	Properties properties = new Properties();
		try {
			properties.load(new FileInputStream(Constants.Environment.JCLConfig()));
		} catch (IOException e) {
			e.printStackTrace();
		}
    	
		DEFAULT_JCL = super.getInstancePacu(properties);
		
		this.initKafka(gvName);
    }
    
    // internal utilities
    private void initKafka(String gvName){
    	this.gvNameKafka = getMapNameMappedToKafka(gvName);
    	Properties kafkaProperties = new Properties();
    	
    	try {
			kafkaProperties.load(new FileInputStream(Constants.Environment.JCLKafkaMapConfig()));
		} catch (IOException e1) {
			e1.printStackTrace();
		}
    	
    	if(localResourceMap == null) {
    		try {
				String groupId = "jcl-" + InetAddress.getLocalHost().getHostAddress() + "-" + UUID.randomUUID().toString();
				kafkaProperties.put("group.id", groupId);
				
				kafkaProperties.store(new FileOutputStream(Constants.Environment.JCLKafkaMapConfig()), null);
			} catch (IOException e) {
				e.printStackTrace();
			}
    		
			localResourceMap = new JCLResultResource();
			
			subscribedTopics = new CopyOnWriteArrayList<String>();
			subscribedTopics.add(gvNameKafka);

			kafkaMapConsumerThread = new KafkaMapConsumerThread(
				subscribedTopics, 
				localResourceMap
			);
			
			try {
				kafkaMapConsumerThread.start();
				synchronized(kafkaMapConsumerThread) {
					kafkaMapConsumerThread.wait();
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
    	
    	kafkaProducer = new KafkaProducer<>(
			kafkaProperties,
			new StringSerializer(),
			new JCLResultSerializer()
		);
    	
    	jclTopicAdmin = JCLTopicAdmin.getInstance();
		
    	kafkaProperties.put("topic.name", gvNameKafka);
		boolean existsMap = jclTopicAdmin.exists(kafkaProperties);
		
		if(!existsMap) {
			JCL_result jclResultHeader = new JCL_resultImpl();
			ProducerRecord<String, JCL_result> producedRecord;
			int size = 0;
			
			jclResultHeader.setCorrectResult(size);
			
			producedRecord = new ProducerRecord<>(
				gvNameKafka,
				Constants.Environment.MAP_INIT,
				jclResultHeader
			);
			
			kafkaProducer.send(
				new ProducerRecord<>(
					gvNameKafka,
					Constants.Environment.MAP_INIT,
					jclResultHeader
				)
			);
		}
    }

    private String getMapNameMappedToKafka(Object key) {
    	return Constants.Environment.MAP_PREFIX + key;
    }
    
    private String getMapKeyMappedToKafka(Object key) {
    	return gvNameKafka + Constants.Environment.MAP_KEY_SUFFIX + key;	
    }
    
    /**
     * Returns the number of key-value mappings in this map.
     *
     * @return the number of key-value mappings in this map
     */
    public int size(){
    	JCLResultResource selfMapResource = null;
    	JCL_result jclResult = null;
    	int size = -1;
    	
		try {
			if((localResourceMapContainer.isFinished() == false) || (localResourceMapContainer.getNumOfRegisters() != 0)){
				while ((selfMapResource = localResourceMapContainer.read(gvNameKafka)) == null);
			}
			
			if((selfMapResource.isFinished() == false) || (selfMapResource.getNumOfRegisters() != 0)){
				while ((jclResult = selfMapResource.read(Constants.Environment.MAP_HEADER_SIZE)) == null);
			}
			
			size = (int) jclResult.getCorrectResult();
		} catch (Exception e) {
			System.err
				.println("problem in JCL_HashMapPacu V get(" + gvNameKafka + ")");
			e.printStackTrace();
		}
    	
        return size;
    }        

    /**
     * Returns <tt>true</tt> if this map contains no key-value mappings.
     *
     * @return <tt>true</tt> if this map contains no key-value mappings
     */
    public boolean isEmpty(){
    	if (size() == 0){
    		return true;
    	}
    	
    	return false;
    }

    /**
     * Returns the value to which the specified key is mapped.
     */
    public V get(Object key){
    	JCL_result jclResult = new JCL_resultImpl();
    	String keyMappedToKafka = getMapKeyMappedToKafka(key);
    	AtomicBoolean checkedIfExistsOnServer = new AtomicBoolean();
		checkedIfExistsOnServer.set(false);
		
    	try {
    		if((localResourceMap.isFinished() == false) || (localResourceMap.getNumOfRegisters() != 0)){
    			while((jclResult = localResourceMap.read(keyMappedToKafka)) == null) {
    				if(!subscribedTopics.contains(keyMappedToKafka)) {
						subscribedTopics.add(keyMappedToKafka);
						kafkaMapConsumerThread.wakeup();
					} else if(!checkedIfExistsOnServer.get()) { 
						checkedIfExistsOnServer.set(true);
						
						if(!containsKey(key)) {
							return null;
						}
					}
    			};
    		}
		} catch (Exception e){
			jclResult.setCorrectResult("no result");
			
			System.err
				.println("problem in JCL_HashMapPacu V get(" + key + ")");
			e.printStackTrace();
		}
    	
    	V value = (V) jclResult.getCorrectResult();
    	
    	return value;
    }
        
    /**
     * Returns and lock the value to which the specified key is mapped.
     */
    public V getLock(Object key){
    	V oldValue = null;
    	
    	if(true) {
    		JCL_result jclResult = new JCL_resultImpl();
    		JCL_result jclResultLockToken = new JCL_resultImpl();
    		String lockToken = UUID.randomUUID().toString();
    		String topicName = getMapKeyMappedToKafka(key);
    		AtomicBoolean checkedIfExistsOnServer = new AtomicBoolean();
    		
    		checkedIfExistsOnServer.set(false);
    		jclResultLockToken.setCorrectResult(lockToken);

    		kafkaProducer.send(
    			new ProducerRecord<>(
    				topicName,
    				Constants.Environment.MAP_LOCK,
    				jclResultLockToken
    			)
    		);
    		
    		try {
    			if((localResourceMap.isFinished()==false) || (localResourceMap.getNumOfRegisters()!=0)){
    				while ((jclResult = localResourceMap.read(topicName + ":" + Constants.Environment.LOCK_PREFIX + ":" + lockToken)) == null) {
    					if(!subscribedTopics.contains(topicName)) {
    						subscribedTopics.add(topicName);
    						kafkaMapConsumerThread.wakeup();
    						
    					} else if(!checkedIfExistsOnServer.get()) {
    						checkedIfExistsOnServer.set(true);
    						
    						if(!containsKey(key)) {
    							return null;
    						}
    					}
    				};
    			}

    			while(!canAcquireMapKey(topicName, lockToken));			

    			kafkaProducer.send(
    				new ProducerRecord<>(
						topicName,
    					Constants.Environment.MAP_ACQUIRE,
    					jclResultLockToken
    				)
    			);

    			if((localResourceMap.isFinished()==false) || (localResourceMap.getNumOfRegisters()!=0)){
    				while ((jclResult = localResourceMap.read(topicName)) == null) {
    					if(!subscribedTopics.contains(topicName)) {
    						subscribedTopics.add(topicName);
    						kafkaMapConsumerThread.wakeup();
    						
    					} else if(!checkedIfExistsOnServer.get()) {
    						checkedIfExistsOnServer.set(true);
    						
    						if(!containsKey(key)) {
    							return null;
    						}
    					}
    				}
    			}
    			
    			return (V) jclResult.getCorrectResult();
    		} catch (Exception e){
    			System.err
    				.println("problem in JCL facade getValueLocking(Object " + key + ")");
    			e.printStackTrace();
    			
    			jclResult.setErrorResult(e);
    			
    			return null;
    		}
    	}
        
        return (oldValue == null ? null : oldValue);
    }
    
    private boolean canAcquireMapKey (Object key, String lockToken) {
		Entry<String, JCL_result> minEntry = null;
		String prefix = key + ":" + Constants.Environment.LOCK_PREFIX + ":";
		
		try {
			for (Entry<String, JCL_result> entry : localResourceMap.entrySet()) {
				if(entry.getKey().startsWith(prefix)) {
					if (minEntry == null || Long.parseLong(entry.getValue().getCorrectResult().toString()) < Long.parseLong(minEntry.getValue().getCorrectResult().toString())) {
						minEntry = entry;
					}
				}
			}
			
			if(minEntry != null && minEntry.getKey().toString().contains(lockToken)) {
				return true;
			}
		} catch (Exception e) {
			System.err
				.println("Problem in JCLHashMapPacu canAcquireMap(" + key + ")");
			e.printStackTrace();
		}
		
		return false;
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
    	key = getMapKeyMappedToKafka(key);
    	Properties properties = KafkaMapConfigProperties.getInstance().get();
		properties.put("topic.name", key);
		
		boolean contains = false;
		
		contains = jclTopicAdmin.exists(properties);
		
		return contains;
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
    	String topicName = getMapKeyMappedToKafka(key);
    	JCL_result jclResultInstance = new JCL_resultImpl();
    	
    	subscribedTopics.add(topicName);
    	kafkaMapConsumerThread.wakeup();
		
		jclResultInstance.setCorrectResult(value);
		
		try {
			kafkaProducer.send(
				new ProducerRecord<>(
					topicName,
					Constants.Environment.MAP_PUT,
					jclResultInstance
				)
			);
		} catch (Exception e) {
			System.err
				.println("problem in JCL_HashMapPacu V put(" + key + ", " + value + ")");
			kafkaProducer.abortTransaction();
		}
        
        return value;
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
    	JCLResultResource selfMap = null;
        JCL_result jclResult = new JCL_resultImpl();
        String topicName = getMapKeyMappedToKafka(key);
        Object[] pair = {
			key,
			value
		};

		try {
			jclResult.setCorrectResult(pair);
			
			kafkaProducer.send(
				new ProducerRecord<>(
					topicName,
					Constants.Environment.MAP_PUT,
					jclResult
				)
			);
			
			kafkaProducer.send(
				new ProducerRecord<>(
					topicName,
					Constants.Environment.MAP_RELEASE,
					new JCL_resultImpl()
				)
			);
			
			if((localResourceMap.isFinished()==false) || (localResourceMap.getNumOfRegisters()!=0)){
				while (localResourceMap.read(topicName + ":" + Constants.Environment.MAP_ACQUIRE) != null);
			}
			
			return value;
			
		} catch (Exception e) {
			System.err.println("problem in JCL facade setValueUnlocking(Object " + key + ", Object " + value + ")");
			
			e.printStackTrace();
			
			return null;
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
        int numKeysToBeAdded = m.size();
                
        if (numKeysToBeAdded == 0)
            return;
        super.instantiateBin((Object)m.entrySet(), this.gvName);
                
        List<Object> obj =  new ArrayList<Object>();
		LinkedBuffer buffer = LinkedBuffer.allocate(1048576);

        for(K key:m.keySet()){
        	
			// ################ Serialization key ########################
			buffer.clear();
        	ObjectWrap objW = new ObjectWrap(key);	
			Schema scow = RuntimeSchema.getSchema(ObjectWrap.class);
			byte[] k = ProtobufIOUtil.toByteArray(objW,scow, buffer);			
			// ################ Serialization key ########################

        	obj.add(ByteBuffer.wrap(k));
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
    	Set table = super.hashClean(gvName,idLocalize);
       for(Object key:table){    	   
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
    	Set table = super.getHashSet(gvName,idLocalize);
    	for(Object k:table){
    		
			Schema<ObjectWrap> scow = RuntimeSchema.getSchema(ObjectWrap.class);
			ObjectWrap obj = scow.newMessage();
			ProtobufIOUtil.mergeFrom(((ByteBuffer)k).getArray(), obj, scow);    		
    		K key = (K)obj.getobj();
    		
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
        	        	
			Schema<ObjectWrap> scow = RuntimeSchema.getSchema(ObjectWrap.class);
			ObjectWrap obj = scow.newMessage();
			ProtobufIOUtil.mergeFrom(((ByteBuffer)current.getKey()).getArray(), obj, scow);    		
    		K key = (K)obj.getobj();
 
			ProtobufIOUtil.mergeFrom((byte[])current.getValue(), obj, scow);    		
    		V value = (V)obj.getobj();
        	
        	return new implementations.util.Entry<K, V>(key,value);
//            return current;
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
        Set ks = super.getHashSet(gvName,idLocalize);
        
        Set<K> retSet = new HashSet<K>();
		Schema<ObjectWrap> scow = RuntimeSchema.getSchema(ObjectWrap.class);        
		ObjectWrap obj = scow.newMessage();

		for(Object key:ks){
			ProtobufIOUtil.mergeFrom(((ByteBuffer)key).getArray(), obj, scow);    		
            retSet.add((K)obj.getobj());        	
        }
        
        return retSet;
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

