package org.javacaela;

import java.io.File;
import java.util.List;
import java.util.concurrent.Future;

import commom.Constants;
import implementations.dm_kernel.IoTuser.JCL_Expression;
import implementations.dm_kernel.IoTuser.JCL_IoTFacadeImpl;
import interfaces.kernel.JCL_IoTfacade;
import interfaces.kernel.JCL_result;
import interfaces.kernel.datatype.Device;
import interfaces.kernel.datatype.Sensor;

public class IoTDemo {
	public static void main(String[] args) throws Exception{
		JCL_IoTfacade iot = JCL_IoTFacadeImpl.getInstance(); // getting JCL instance
		
		int numRecords = 1000;  // number of records to store on the cluster
		int delay = 2000;	// the delay to collect the sensor data
		Object on[] = {0};	// command to send to an actuator
		
		// searching for the devices by their nicknames
		Device galileo = iot.<Device>getDeviceByName("galileo").get(0);
		Device raspberry = iot.<Device>getDeviceByName("raspberry").get(0);
		Device androidGPS = iot.<Device>getDeviceByName("android1").get(0);
		Device androidPhoto = iot.<Device>getDeviceByName("android2").get(0);
		
		// another way to get the Device
		List<Device> arduinoList = iot.getDeviceByName("arduino");  
		Device arduino = arduinoList.get(0);
		
		// configuring sensors on the devices
		iot.setSensorMetadata(galileo, "temperature", 16, numRecords, delay, Constants.IoT.INPUT, Constants.IoT.GENERIC);		
		iot.setSensorMetadata(arduino, "light", 54, numRecords, delay, Constants.IoT.INPUT, Constants.IoT.GENERIC);
		iot.setSensorMetadata(galileo, "potentiometer", 14, numRecords, delay, Constants.IoT.INPUT, Constants.IoT.GENERIC);
		iot.setSensorMetadata(androidGPS, "TYPE_GPS", Constants.IoT.TYPE_GPS, numRecords, delay, Constants.IoT.INPUT, Constants.IoT.GENERIC);
		iot.setSensorMetadata(androidPhoto, "TYPE_PHOTO", Constants.IoT.TYPE_PHOTO, numRecords, delay, Constants.IoT.INPUT, Constants.IoT.GENERIC);
		iot.setSensorMetadata(raspberry, "led", 7, numRecords, delay, Constants.IoT.OUTPUT, Constants.IoT.GENERIC);

		// getting the sensors by their nicknames
		Sensor led = iot.<Sensor>getSensorByName(raspberry, "led").get(0);
		Sensor gps = iot.<Sensor>getSensorByName(androidGPS, "TYPE_GPS").get(0);
		
		// registering a context
		String contextNickname = "gpsContext";
		iot.registerContext(androidGPS, gps, new JCL_Expression("S0<-20.396107; S1<51.0468"), contextNickname);
		
		// adding an action to the context to turn the led on
		iot.addContextAction(contextNickname, raspberry, led, on);
		
		// registering a class that has a method that take a picture and validates if there is no one in the room
		JCL_IoTFacadeImpl.PacuHPC.register(Methods.class, "validationClass");
		
		// adding a task to be executed when the context is reached
		Future<JCL_result> res = iot.addContextAction(contextNickname, false, "validationClass", "validate", null);
		
		// get the value of the method "validate" by creating a synchronization barrier 
		if (res.get().getCorrectResult().equals(true)){
			// registering the jars files
			File[] jarFiles = { new File("lib/emailSender.jar"), new File("lib/javax.mail-1.5.0.jar") };
			JCL_IoTFacadeImpl.PacuHPC.register(jarFiles, "EmailSender");
			
			// the parameters of the email 
			Object methodParameters[] = {"The person has just left the room", "A picture of the room is attached and the other values are:"};
			// executing the method
			JCL_IoTFacadeImpl.PacuHPC.execute("EmailSender", "sendEmail", methodParameters);
		}
		// cleaning the environment
		JCL_IoTfacade.PacuHPC.cleanEnvironment();
		JCL_IoTfacade.PacuHPC.destroy();
	}
}