package commom;

import java.time.Duration;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.errors.WakeupException;
import org.apache.kafka.common.serialization.StringDeserializer;

import implementations.util.KafkaMapConfigProperties;
import interfaces.kernel.JCL_result;

public class KafkaMapConsumerThread extends Thread {
	
	AtomicBoolean stop = new AtomicBoolean(false);
	KafkaConsumer<String, JCL_result> consumer;
	private static JCLResultResource localResourceMap;
	private static List<String> subscribedTopics;
	
	public KafkaMapConsumerThread(List<String> subscribedTopicsParam, JCLResultResource localResourceMapParam) {
		subscribedTopics = subscribedTopicsParam;
		localResourceMap = localResourceMapParam;
	}

	@Override
	public void run() {
		Properties consumerProperties = KafkaMapConfigProperties.getInstance().get();
		
		consumer =  new KafkaConsumer<>(
			consumerProperties,
			new StringDeserializer(),
			new JCLResultDeserializer()
		);
		
		consumer.subscribe(
			subscribedTopics
		);
		
		synchronized (this) {
			this.notify();
		}
		
		while(!stop.get()) {
			try {
				ConsumerRecords<String, JCL_result> records = consumer.poll(Duration.ofNanos(Long.MAX_VALUE));
				
				records.forEach(record -> {
//					System.out.println(record.key() + ":" + record);
					
					switch(record.key()) {
					case Constants.Environment.MAP_INIT:
						localResourceMap.create(record.topic(), record.value());
						break;
					
					case Constants.Environment.MAP_PUT:
						String mapPutgvNameKafka = record.topic().split(Constants.Environment.MAP_KEY_SUFFIX)[0];
						JCL_result mapPutHelper = null;
						
						try {
							if((localResourceMap.isFinished()==false) || (localResourceMap.getNumOfRegisters()!=0)){
								while ((mapPutHelper = localResourceMap.read(mapPutgvNameKafka)) == null);
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
						
						int mapPutSize = (int) mapPutHelper.getCorrectResult();
						mapPutSize = mapPutSize + 1;
						mapPutHelper.setCorrectResult(mapPutSize);
						
						localResourceMap.create(mapPutgvNameKafka, mapPutHelper);
						localResourceMap.create(record.topic(), record.value());
						break;
						
					case Constants.Environment.MAP_LOCK:
						JCL_result recordOffset = new JCL_resultImpl();
						recordOffset.setCorrectResult(record.offset());
						
						localResourceMap.create(
							record.topic() + ":" + Constants.Environment.LOCK_PREFIX + ":" + record.value().getCorrectResult(),
							recordOffset
						);
						break;
						
					case Constants.Environment.MAP_ACQUIRE:
						localResourceMap.create(
							record.topic() + ":" + Constants.Environment.MAP_ACQUIRE,
							record.value()
						);
						break;
					
					case Constants.Environment.MAP_RELEASE:
						try {
							JCL_result jclResultLockToken = localResourceMap.read(record.topic() + ":" + Constants.Environment.MAP_ACQUIRE);
							
							localResourceMap.delete(
								record.topic() + ":" + Constants.Environment.LOCK_PREFIX + ":" + jclResultLockToken.getCorrectResult()
							);
							
							localResourceMap.delete(
								record.topic() + ":" + Constants.Environment.MAP_ACQUIRE
							);
						} catch (Exception e1) {
							System.err
								.println("Problem in JCLKafkaConsumerThread case " + Constants.Environment.GLOBAL_VAR_RELEASE);
							e1.printStackTrace();
						}
						break;
						
					default:
						break;
					}
				});
				
				consumer.commitAsync();
				
			} catch (WakeupException wue) {
				consumer.subscribe(
					subscribedTopics
				);
			} catch (Exception e) {
				System.err
					.println("problem in KafkaConsumerRunner run()");
				e.printStackTrace();
			}
		}
		
		consumer.close();
	}
	
	public void shutdown() {
		stop.set(true);
	}
	
	public void wakeup() {
		consumer.wakeup();
	}
}
