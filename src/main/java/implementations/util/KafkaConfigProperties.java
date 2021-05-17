package implementations.util;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

import commom.Constants;

public class KafkaConfigProperties {
	private static KafkaConfigProperties instance;
	private static Properties properties;
	
	private KafkaConfigProperties() {
		properties = new Properties();
		
		try {
			properties.load(new FileInputStream(Constants.Environment.JCLKafkaConfig()));
		} catch (FileNotFoundException e){					
			System.err
				.println("File not found (" + Constants.Environment.JCLKafkaConfig() + ") !!!!!");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static KafkaConfigProperties getInstance() {
		if(instance == null) {
			instance = new KafkaConfigProperties();
		}
		
		return instance;
	}
	
	public Properties get() {
		return properties;		
	}
	
	public Properties get(String path) {
		return properties;		
	}
}
