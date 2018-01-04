package org.javacaela;

import java.util.Map.Entry;

import implementations.dm_kernel.IoTuser.JCL_IoTFacadeImpl;
import interfaces.kernel.JCL_IoTfacade;
import interfaces.kernel.JCL_Sensor;

public class Methods {
	
	public boolean validate(){
    	JCL_IoTfacade iot = JCL_IoTFacadeImpl.getInstance();
    	
		Entry<String, String> androidPhoto = iot.getDeviceByName("android2").get(0);
		Entry<String, String> photo = iot.getSensorByName(androidPhoto, "TYPE_PHOTO").get(0);
		
		JCL_Sensor sensor = iot.getSensingDataNow(androidPhoto, photo);
		
		return recognizeFace(sensor.getObject());
	}
	
	
	public boolean recognizeFace(Object photo){	
		return true;
	}
}
