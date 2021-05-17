package implementations.util;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

import commom.Constants;

public class KafkaMapConfigProperties {
	private static KafkaMapConfigProperties instance;
	private static Properties properties;
	
	private KafkaMapConfigProperties() {
		properties = new Properties();
		
		try {
			properties.load(new FileInputStream(Constants.Environment.JCLKafkaMapConfig()));
		} catch (FileNotFoundException e){					
			System.err
				.println("File not found (" + Constants.Environment.JCLKafkaMapConfig() + ") !!!!!");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static KafkaMapConfigProperties getInstance() {
		if(instance == null) {
			instance = new KafkaMapConfigProperties();
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
