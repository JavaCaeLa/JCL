package implementations.util;

import java.util.Properties;

import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;

public class KafkaProperties extends Properties{
	
	private static final long serialVersionUID = -6866333942719618294L;

	public Properties get(String host, String port, String objectNickname) {
		Properties properties = new Properties();
		
		properties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, host + ":" + port);
		properties.put(ProducerConfig.CLIENT_ID_CONFIG, objectNickname);
		properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
		properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
		
		return properties;
	}
}
