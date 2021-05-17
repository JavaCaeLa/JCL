
package commom;

import java.io.IOException;

import org.apache.kafka.common.serialization.Deserializer;

import com.fasterxml.jackson.databind.ObjectMapper;

import interfaces.kernel.JCL_result;;

public class JCLResultDeserializer implements Deserializer<JCL_result> {

	@Override
	public JCL_result deserialize(String topic, byte[] data) {
	    ObjectMapper mapper = new ObjectMapper();
	    JCL_result result = null;
		
	    try {
			result = mapper.readValue(new String(data, "utf-8"), JCL_resultImpl.class);
		} catch (IOException e) {
			e.printStackTrace();
		}
	    
	    return result;
	}
}
