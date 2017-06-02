package junit.iot;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;


import implementations.dm_kernel.IoTuser.JCL_IoTFacadeImpl;
import implementations.dm_kernel.IoTuser.JCL_Expression;
import implementations.dm_kernel.user.JCL_FacadeImpl;
import implementations.test.UserServices;
import implementations.dm_kernel.IoTuser.JCL_Configuration;
import interfaces.kernel.JCL_facade;
import interfaces.kernel.JCL_IoTfacade;
import interfaces.kernel.JCL_Sensor;
import interfaces.kernel.JCL_result;
import commom.Constants;
import commom.Constants.IoT;

public class IoTuniTests1 {
	private JCL_IoTfacade jclIoT;

	private List<Entry<String,String>> devices;
	private List<Entry<String,String>> sensors;
	private Entry<String,String> device;
	private Entry<String,String> sensor;

	private JCL_facade jcl;
	private Entry<String,String> actuator;
	private Map<Entry<String,String>, Integer> allIotDeviceCores;
	private Map<String,String> sensorMetadata;
	private JCL_Expression expression;
	private Future<JCL_result> ticket;
	private JCL_Configuration configuration;
	private List<Integer> numbers;


	public IoTuniTests1(){
		jclIoT = JCL_IoTFacadeImpl.getInstance();
		jcl = JCL_FacadeImpl.getInstance();

		devices = jclIoT.getIoTDevices();
		device = jclIoT.getIoTDevices().get(0);

		// Verificar os pinos para testar em cada placa 
		jclIoT.setSensorMetadata(device, "sensor", 7, 1000, 1000, Constants.IoT.INPUT, Constants.IoT.GENERIC);
		jclIoT.setSensorMetadata(device, "atuador", 11, 50000, 5000, IoT.OUTPUT, IoT.GENERIC);

		
		try{
			Thread.sleep(1000);
		}catch(Exception e){
			
		}
		//System.out.println(jclIoT.getSensors(device).size());
		sensors = jclIoT.getSensors(device);
		sensor = jclIoT.getSensors(device).get(0);
		actuator = jclIoT.getSensors(device).get(1);


		allIotDeviceCores = new HashMap<Entry<String,String>, Integer>();
		allIotDeviceCores = jclIoT.getAllIoTDeviceCores();
		sensorMetadata = jclIoT.getSensorMetadata(device, sensor);
		expression = new JCL_Expression("S0<30");
		jcl.register(UserServices.class, "userServices");
		configuration = jclIoT.getConfig(device);
		numbers = new ArrayList<>();
		for(int i=100; i>0; i--){
			int n = i;
			numbers.add(n);
		}
	}


	public boolean testGetIoTDevices() {
		return true == devices.equals(jclIoT.getIoTDevices());
	}


	public boolean testGetSensors() {


		return true == (sensors.equals(jclIoT.getSensors(device)));
	}


	public boolean testGetSensingData() {
		Object result = null;
		for(Entry<Integer,JCL_Sensor> sensing : jclIoT.getSensingData(device, sensor).entrySet()){
			result = sensing.getValue();
			break;
		}
		return true == (result != null);
	}


	public boolean testGetAllSensingData() {
		Object result = null;

		for(Entry<Integer,JCL_Sensor> sensing : jclIoT.getAllSensingData(device, sensor).entrySet()){
			result = sensing.getValue();
			break;
		}
		return true == (result != null);
	}


	public boolean testGetLastSensingData() {
		Object result = jclIoT.getLastSensingData(device, sensor);
		return true == (result != null);
	}


	public boolean testGetSensingDataNow() {
		Object result = jclIoT.getSensingDataNow(device, sensor);
		//System.out.println(result);
		return true == (result != null);
	}


	public boolean testGetIoTDeviceMetadata() {
		//System.out.println(jclIoT.getIoTDeviceMetadata(device));
		return "5151".equals(jclIoT.getIoTDeviceMetadata(device).get("PORT"));
	}


	public boolean testSetIoTDeviceMetadata() {
		Map<String,String> metadados = new HashMap<String, String>();
		metadados.put("verbose", "false");
		metadados.put("ENABLE_SENSOR", ";");
		/*metadados.put("SENSOR_ALIAS_8", "sensor");
		metadados.put("SENSOR_SIZE_8", "1000");
		metadados.put("SENSOR_SAMPLING_8", "1000");
		metadados.put("SENSOR_DIR_8", "I");
		metadados.put("SENSOR_TYPE_8", "0");*/
		return true == (jclIoT.setIoTDeviceMetadata(device, metadados));
	}


	public boolean testTurnOn() {
		return true == (jclIoT.turnOn(device));
	}


	public boolean testStandBy() {
		boolean result;
		result = true == (jclIoT.standBy(device));
		jclIoT.turnOn(device);
		return result;
	}

	public boolean testSetSensorMetadata() {
		return true == (jclIoT.setSensorMetadata(device, "sensor", 7, 1000, 1000, Constants.IoT.INPUT, 1));
	}


	public boolean testRemoveSensor() {
		//sensors = jclIoT.getSensors(device);
		return true == (jclIoT.removeSensor(device, sensor));
	}

	public boolean testIsDeviceInStandBy() {
		jclIoT.standBy(device);
		boolean result = true == (jclIoT.isDeviceInStandBy(device)); 
		jclIoT.turnOn(device);
		return result;
	}

	public boolean testGetIoTDeviceCores() {
		int deviceCores = jclIoT.getIoTDeviceCores(device);
		return true == (deviceCores>0);
	}

	//------------------------



	public boolean testGetAllIoTDeviceCores() {
		return (allIotDeviceCores.equals(jclIoT.getAllIoTDeviceCores()));
	}


	public boolean testGetSensorMetadata() {
		return (sensorMetadata.equals(jclIoT.getSensorMetadata(device, sensor)));
	}


	public boolean testSetEncryption() {
		return (true == jclIoT.setEncryption(device, true));
	}


	public boolean testRegisterContext() {
		return (true == jclIoT.registerContext(device, sensor, expression, "test context"));
	}


	public boolean testAddContextAction1() {
		System.out.println(device + "\n" + actuator);
		return (true == jclIoT.addContextAction("test context", device, actuator, new Object[]{1}));
	}


	public boolean testAddContextAction2() {
		Object result  = jclIoT.addContextAction("test context", true, "userServices", "ordena", numbers); 
		return (result != null);
	}


	public boolean testRemoveContextResult() {
		try {
			jclIoT.registerContext(device, sensor, new JCL_Expression("S0<1"), "test context2");
			ticket = jclIoT.addContextAction("test context2", false, "userServices", "ordena", numbers);
//			ticket.get();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return (true == jclIoT.removeContextResult("test context", ticket));
	}


	public boolean testActing() {
		return (true == jclIoT.acting(device, actuator, new Object[]{1}));
	}


	public boolean testGetDeviceByName() {
		return (jclIoT.getDeviceByName("raspberry") != null);
	}


	public boolean testGetSensorByName() {
		return (jclIoT.getSensorByName(device, "fsdaf") != null);
	}


	public boolean testRegisterMQTTContext() {
		return (true == jclIoT.registerMQTTContext(device, sensor, expression, "name"));
	}


	public boolean testUnregisterContext() {
		return (true == jclIoT.unregisterContext("test context"));
	}


	public boolean testUnregisterMQTTContext() {
		return (true == jclIoT.unregisterMQTTContext("name"));
	}


	public boolean testRemoveContextAction1() {
		actuator = jclIoT.getSensors(device).get(1);
		return (true == jclIoT.removeContextAction("test context", device, actuator, new Object[]{1}));
	}


	public boolean testRemoveContextAction2() {
		return (true == jclIoT.removeContextAction("test context", true, "userServices", "ordena", numbers));
	}


	public boolean testSetConfig() {
		return (true == jclIoT.setConfig(device, configuration));
	}


	public boolean testGetConfig() {
		return (configuration.metadados.equals(jclIoT.getConfig(device).metadados));
	}


	public static void main (String [] args){
		IoTuniTests1 uniTests = new IoTuniTests1();

        System.out.println("Acting " + uniTests.testActing());
        
        System.out.println("RegisterContext " + uniTests.testRegisterContext());
        
        System.out.println("AddContextActionSt " + uniTests.testAddContextAction1());
        
        System.out.println("AddContextActionSt " + uniTests.testAddContextAction2());
        
        System.out.println("GetAllIoTDeviceCores " + uniTests.testGetAllIoTDeviceCores());
        
        System.out.println("GetConfig " + uniTests.testGetConfig());
        
        System.out.println("GetDeviceByName " + uniTests.testGetDeviceByName());
        
        System.out.println("GetSensorByName " + uniTests.testGetSensorByName());
        
        System.out.println("GetSensorMetadata " + uniTests.testGetSensorMetadata());
        
        System.out.println("RegisterMQTTContext " + uniTests.testRegisterMQTTContext());
        
        System.out.println("RemoveContextActio " + uniTests.testRemoveContextAction1());
        
        System.out.println("RemoveContextActio " + uniTests.testRemoveContextAction2());
        
        System.out.println("RemoveContextResult " + uniTests.testRemoveContextResult());
        
        System.out.println("SetEncryption " + uniTests.testSetEncryption());
        
        System.out.println("UnregisterContext " + uniTests.testUnregisterContext());
        
        System.out.println("UnregisterMQTTContext " + uniTests.testUnregisterMQTTContext());
		
		System.out.println("GetAllSensingData " + uniTests.testGetAllSensingData());

		System.out.println("GetIoTDeviceCores " + uniTests.testGetIoTDeviceCores());

		System.out.println("GetIoTDeviceMetadata " + uniTests.testGetIoTDeviceMetadata());

		System.out.println("GetIoTDevices " + uniTests.testGetIoTDevices());

		System.out.println("GetLastSensingData " + uniTests.testGetLastSensingData());

		System.out.println("GetSensingData " + uniTests.testGetSensingData());

		System.out.println("GetSensingDataNow " + uniTests.testGetSensingDataNow());

		System.out.println("GetSensors " + uniTests.testGetSensors());

		System.out.println("IsDeviceInStandBy " + uniTests.testIsDeviceInStandBy());

		System.out.println("SetSensorMetadata " + uniTests.testSetSensorMetadata());

		System.out.println("RemoveSensor " + uniTests.testRemoveSensor());

		System.out.println("SetIoTDeviceMetadata " + uniTests.testSetIoTDeviceMetadata());

		System.out.println("StandBy " + uniTests.testStandBy());

		System.out.println("TurnOn " + uniTests.testTurnOn());
		
		System.out.println("SetConfig " + uniTests.testSetConfig());
		
        JCL_IoTfacade.PacuHPC.destroy();
	}
}