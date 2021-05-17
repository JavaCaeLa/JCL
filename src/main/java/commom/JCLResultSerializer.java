
package commom;

import java.io.IOException;

import org.apache.kafka.common.serialization.Serializer;

import com.fasterxml.jackson.databind.ObjectMapper;

import interfaces.kernel.JCL_result;

public class JCLResultSerializer implements Serializer<JCL_result> {

	@Override
	public byte[] serialize(final String topic, final JCL_result data) {
		byte[] retVal = null;
		
		ObjectMapper objectMapper = new ObjectMapper();

	    try {
	    	retVal = objectMapper.writeValueAsString(data).getBytes();
		} catch (IOException e) {
			System.err.println("Problem on JCLResultResourceSerializer");
			e.printStackTrace();
		}
	    
	    return retVal;
	}
}
