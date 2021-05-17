package commom;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.errors.WakeupException;
import org.apache.kafka.common.serialization.StringDeserializer;

import implementations.util.KafkaConfigProperties;
import interfaces.kernel.JCL_result;

public class KafkaConsumerThread extends Thread {
	
	AtomicBoolean stop = new AtomicBoolean(false);
	KafkaConsumer<String, JCL_result> consumer;
	
	private static JCLResultResource localResourceGlobalVar;
	private static JCLResultResource localResourceExecute;
	private static List<String> subscribedTopics;
	public String topicGranularity;
	
	public KafkaConsumerThread(List<String> subscribedTopicsParam, JCLResultResource localResourceGlobalVarParam, JCLResultResource localResourceExecuteParam) {
		subscribedTopics = subscribedTopicsParam;
		localResourceGlobalVar = localResourceGlobalVarParam;
		localResourceExecute = localResourceExecuteParam;
	}

	@Override
	public void run() {
		Properties consumerProperties = KafkaConfigProperties.getInstance().get();
		
		this.topicGranularity = consumerProperties.getProperty(
			Constants.Environment.GRANULARITY_CONFIG_KEY, 
			Constants.Environment.HIGH_GRANULARITY_CONFIG_VALUE
		);
		
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
		
		if(this.topicGranularity == Constants.Environment.HIGH_GRANULARITY_CONFIG_VALUE) {
			this.processHighGranularityTopicis();
		} else {
			this.processLowGranularityTopicis();
		}
		
		consumer.close();
	}
	
	public void shutdown() {
//		System.out.println(Thread.currentThread().getId() + ":shutdown()");
		stop.set(true);
	}
	
	public void wakeup() {
//		System.out.println(Thread.currentThread().getId() + ":wakeup()");
		consumer.wakeup();
	}
	
	private void processHighGranularityTopicis() {
		while(!stop.get()) {
			try {
				ConsumerRecords<String, JCL_result> records = consumer.poll(Duration.ofNanos(Long.MAX_VALUE));
				
				records.forEach(record -> {
//					System.out.println(record.key() + ":" + record);
					
					switch(record.key()) {
					case Constants.Environment.EXECUTE_KEY:
						localResourceExecute.create(
							record.topic(),
							record.value()
						);
						break;
						
					case Constants.Environment.GLOBAL_VAR_KEY:
						localResourceGlobalVar.create(
							record.topic(),
							record.value()
						);
						break;
					
					case Constants.Environment.GLOBAL_VAR_LOCK_KEY:
						JCL_result value = new JCL_resultImpl();
						
						value.setCorrectResult(record.offset());
						
						localResourceGlobalVar.create(
							record.topic() + ":" + Constants.Environment.LOCK_PREFIX + ":" + record.value().getCorrectResult(),
							value
						);
						break;
					
					case Constants.Environment.GLOBAL_VAR_ACQUIRE:
						localResourceGlobalVar.create(
							record.topic() + ":" + Constants.Environment.GLOBAL_VAR_ACQUIRE,
							record.value()
						);
						break;
					
					case Constants.Environment.GLOBAL_VAR_RELEASE:
						try {
							JCL_result jclResultLockToken = localResourceGlobalVar.read(record.topic() + ":" + Constants.Environment.GLOBAL_VAR_ACQUIRE);
							
							localResourceGlobalVar.delete(
								record.topic() + ":" + Constants.Environment.LOCK_PREFIX + ":" + jclResultLockToken.getCorrectResult()
							);
							
							localResourceGlobalVar.delete(
								record.topic() + ":" + Constants.Environment.GLOBAL_VAR_ACQUIRE
							);
						} catch (Exception e1) {
							System.err
								.println("Problem in JCLKafkaConsumerThread case " + Constants.Environment.GLOBAL_VAR_RELEASE);
							e1.printStackTrace();
						}
						break;
					
					case Constants.Environment.GLOBAL_VAR_DEL:
						localResourceGlobalVar.delete(
							record.topic() 
						);
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
	}

	private void processLowGranularityTopicis() {
		while(!stop.get()) {
			try {
//				System.out.println(subscribedTopics);
				ConsumerRecords<String, JCL_result> records = consumer.poll(Duration.ofNanos(Long.MAX_VALUE));
				
				for(ConsumerRecord<String, JCL_result> record : records) {
//					System.out.println(record.key() + ":" + record);
					
					String jclAction = new String(record.headers().lastHeader("jcl-action").value(), StandardCharsets.UTF_8);
					
					switch(jclAction) {
						case Constants.Environment.EXECUTE_KEY:
							localResourceExecute.create(
								record.key(),
								record.value()
							);
							break;
							
						case Constants.Environment.GLOBAL_VAR_KEY:
							localResourceGlobalVar.create(
								record.key(),
								record.value()
							);
							break;
						
						case Constants.Environment.GLOBAL_VAR_LOCK_KEY:
							JCL_result value = new JCL_resultImpl();
							
							value.setCorrectResult(record.offset());
							
							localResourceGlobalVar.create(
								record.key() + ":" + Constants.Environment.LOCK_PREFIX + ":" + record.value().getCorrectResult(),
								value
							);
							break;
						
						case Constants.Environment.GLOBAL_VAR_ACQUIRE:
							localResourceGlobalVar.create(
								record.key() + ":" + Constants.Environment.GLOBAL_VAR_ACQUIRE,
								record.value()
							);
							break;
						
						case Constants.Environment.GLOBAL_VAR_RELEASE:
							try {
								JCL_result jclResultLockToken = localResourceGlobalVar.read(record.key() + ":" + Constants.Environment.GLOBAL_VAR_ACQUIRE);
								
								localResourceGlobalVar.delete(
									record.key() + ":" + Constants.Environment.LOCK_PREFIX + ":" + jclResultLockToken.getCorrectResult()
								);
								
								localResourceGlobalVar.delete(
									record.key() + ":" + Constants.Environment.GLOBAL_VAR_ACQUIRE
								);
							} catch (Exception e1) {
								System.err
									.println("Problem in JCLKafkaConsumerThread case " + Constants.Environment.GLOBAL_VAR_RELEASE);
								e1.printStackTrace();
							}
							break;
						
						case Constants.Environment.GLOBAL_VAR_DEL:
							localResourceGlobalVar.delete(
								record.key() 
							);
							break;
							
						default:
							break;
					}
				}
				
//				consumer.commitAsync();
				
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
	}
}
