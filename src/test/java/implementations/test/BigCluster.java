package implementations.test;

import commom.Constants;
import implementations.dm_kernel.IoTuser.JCL_Expression;
import implementations.dm_kernel.IoTuser.JCL_IoTFacadeImpl;
import interfaces.kernel.JCL_IoTfacade;
import interfaces.kernel.JCL_facade;
import interfaces.kernel.datatype.Device;
import interfaces.kernel.datatype.Sensor;

public class BigCluster {

	static JCL_IoTfacade iot;
	
	static Device galileo1, galileo2, galileo3, galileo4;	
	static Device rasp1, rasp2;
	static Device arduino;
	static Device lenovo;
	
	public static void main(String[] args) {	
		iot = JCL_IoTFacadeImpl.getInstance();
		setup();
		System.out.println("saiu");
		for (int i=0; i< 100; i++){
			for (Device d:iot.<Device>getIoTDevices()){
//				if (!d.getKey().equals(arduino.getKey()))
					for (Sensor s: iot.<Sensor>getSensors(d)){
						System.out.println(d + "  " + s);
						try{
						iot.getSensingDataNow(d, s).showData();
						}catch(Exception e){
							e.printStackTrace();
						}
					}
			}
		}
		
		System.out.println("saiu 2");
		for (Device d:iot.<Device>getIoTDevices()){
			for (Sensor s: iot.<Sensor>getSensors(d)){
				System.out.println(d + "  " + s);
				try{
					System.out.println(iot.getSensingData(d, s).toString());
				}catch(Exception e){
					e.printStackTrace();
				}
			}
		}
		
		
		
		
		JCL_IoTfacade.PacuHPC.destroy();
	}
	
	public static void setup(){
		int sensorSampling = 500,
			sensorSize = 999999;
		Object on[] = new Object[]{1}, off[] = new Object[] {0};
		
		galileo1 = iot.<Device>getDeviceByName("gal").get(0); 
		galileo2 = iot.<Device>getDeviceByName("galileo2").get(0); 
		galileo3 = iot.<Device>getDeviceByName("galileo3").get(0);
		galileo4 = iot.<Device>getDeviceByName("galileo4").get(0);
		
		rasp1 = iot.<Device>getDeviceByName("raspberry1").get(0);		
		rasp2 = iot.<Device>getDeviceByName("raspberry2").get(0);
		
		arduino = iot.<Device>getDeviceByName("arduino").get(0);
		
		lenovo = iot.<Device>getDeviceByName("lenovo").get(0);
		
		iot.setSensorMetadata(galileo1, "touch-sensor", 7, sensorSize, sensorSampling, Constants.IoT.INPUT, Constants.IoT.GENERIC);
		
		iot.setSensorMetadata(galileo2, "vibration-sensor", 7, sensorSize, sensorSampling, Constants.IoT.INPUT, Constants.IoT.GENERIC);
		
		iot.setSensorMetadata(galileo3, "vibration-sensor", 7, sensorSize, sensorSampling, Constants.IoT.INPUT, Constants.IoT.GENERIC);
		
		 iot.setSensorMetadata(galileo4, "touch-sensor", 7, sensorSize, sensorSampling, Constants.IoT.INPUT, Constants.IoT.GENERIC);
		
		iot.setSensorMetadata(rasp1, "camera", 31, sensorSize, 8000, Constants.IoT.INPUT, Constants.IoT.GENERIC);
		iot.setSensorMetadata(rasp2, "led", 8, sensorSize, 8000, Constants.IoT.OUTPUT, Constants.IoT.GENERIC);
		
		iot.setSensorMetadata(arduino, "led", 54, sensorSize, sensorSampling, Constants.IoT.INPUT, Constants.IoT.GENERIC);
		
		iot.setSensorMetadata(lenovo, "TYPE_LIGHT", Constants.IoT.TYPE_LIGHT, sensorSize, sensorSampling, Constants.IoT.OUTPUT, Constants.IoT.GENERIC);
		
		iot.registerContext(lenovo, iot.<Sensor>getSensors(lenovo).get(0), new JCL_Expression("S0<100"), "ctx");
		
		iot.PacuHPC.register(UserServices.class, "class");
		
		iot.addContextAction("ctx", false, "class", "execute", new Object[]{8} );	
		
		try{
			Thread.sleep(5000);
		}catch (Exception e){
			
		}
	}
}
