package implementations.test;

import java.util.Map.Entry;
import implementations.dm_kernel.IoTuser.JCL_IoTFacadeImpl;
import interfaces.kernel.JCL_IoTfacade;
import interfaces.kernel.JCL_Sensor;

public class JCL_IOS {
	
	public static void main(String[] args){
		JCL_IoTfacade iot = JCL_IoTFacadeImpl.getInstance();

		// número máximo de registros a serem salvos  
		int numMaxRegistro = 9999999;
		// intervalo de coleta do sensor em ms
		int delay = 1000;
		
		// pegando a referência do dispositivo pelo nickname 
	//	Entry<String, String> android = iot.getDeviceByName("Moto X").get(0);
		
		// configurando o sensor de GPS
	//	iot.setSensorMetadata(android, "nickname do sensor", Constants.IoT.TYPE_GPS, numMaxRegistro, delay, Constants.IoT.INPUT, Constants.IoT.GENERIC);
		
		// pegando a referência do sensor de GPS que foi configurado
	//	Entry<String, String> gps = iot.getSensorByName(android, "nickname do sensor").get(0);
		
		// listando todos os dispositivos IoT
	//	for (Entry<String, String> device:iot.getIoTDevices()){
	//		System.out.println(device);
	//	}
		
		// listando todos os sensores dos dispositivos IoT
		for (Entry<String, String> device : iot.getIoTDevices()){
	//		System.out.println(device);
			for (Entry<String, String> sensor : iot.getSensors(device)){
	//			System.out.println(sensor);
				//iot.getSensingData(device,sensor);
				
				for (Entry<Integer,JCL_Sensor> s:iot.getSensingData(device,sensor).entrySet()){
		//			s.getValue().showData();
				}
				
		//		iot.getLastSensingData(device,sensor).getValue().showData();
				
				System.out.println("Sensor now!!!");
				iot.getSensingDataNow(device, sensor).showData();
			}
		}
		
		// colocando o dispositivo em standby
	//	iot.standBy(android);
		
		// tirando o dispositivo do modo standby
	//	iot.turnOn(android);
		
		// pegando o dado de sensoriamento ativamente 
	//	iot.getSensingDataNow(android, gps);
		
		// removendo sensor
	//	iot.removeSensor(android, gps);
		
		JCL_IoTfacade.PacuHPC.destroy();
	}
}