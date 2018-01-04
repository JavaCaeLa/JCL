package appl.simpleAppl;

import java.util.Map.Entry;

import commom.Constants;
import implementations.dm_kernel.IoTuser.JCL_IoTFacadeImpl;
import interfaces.kernel.JCL_IoTfacade;
import interfaces.kernel.datatype.Device;
import interfaces.kernel.datatype.Sensor;

public class appl10 {
	public static void main(String[] args){
		new appl10();
	}
	
	public appl10(){
		JCL_IoTfacade iot = JCL_IoTFacadeImpl.getInstance();

		// n�mero m�ximo de registros a serem salvos  
		int numMaxRegistro = 1000;
		// intervalo de coleta do sensor em ms
		int delay = 1000;
		
		// pegando a refer�ncia do dispositivo pelo nickname 
		Device android = iot.<Device>getDeviceByName("").get(0);
		
		// configurando o sensor de GPS
		iot.setSensorMetadata(android, "Moto X GPS", Constants.IoT.TYPE_GPS, numMaxRegistro, delay, Constants.IoT.INPUT, Constants.IoT.GENERIC);
		
		// pegando a refer�ncia do sensor de GPS que foi configurado
		Sensor gps = iot.<Sensor>getSensorByName(android, "Moto X GPS").get(0);
		
		// listando todos os dispositivos IoT do cluster
		for (Entry<String, String> device:iot.getIoTDevices()){
			System.out.println(device);
		}
		
		// listando todos os sensores dos dispositivos IoT do cluster
		for (Entry<String, String> device : iot.getIoTDevices()){
			System.out.println(device);
			for (Entry<String, String> sensor : iot.getSensors(device))
				System.out.println(sensor);
		}
		
		// colocando o dispositivo em standby. Enquanto estiver nesse estado, 
		//os dados de sensoriamento n�o s�o coletados e contextos n�o s�o ativados
		iot.standBy(android);
		
		// tirando o dispositivo do modo standby
		iot.turnOn(android);
		
		// pegando o dado de sensoriamento ativamente 
		iot.getSensingDataNow(android, gps).showData();
		
		// removendo sensor
		iot.removeSensor(android, gps);
		
		JCL_IoTfacade.PacuHPC.destroy();
	}
}
