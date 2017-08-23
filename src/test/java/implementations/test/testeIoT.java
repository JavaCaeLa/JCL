package implementations.test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import commom.Constants;
import implementations.dm_kernel.IoTuser.JCL_Expression;
import implementations.dm_kernel.IoTuser.JCL_IoTFacadeImpl;
//import implementations.test.iot.Methods;
import interfaces.kernel.JCL_IoTfacade;
import interfaces.kernel.JCL_Sensor;
import interfaces.kernel.JCL_result;
import interfaces.kernel.datatype.Device;
import interfaces.kernel.datatype.Sensor;

public class testeIoT {
	static JCL_IoTfacade iot = JCL_IoTFacadeImpl.getInstance();
	int maxRecords = 999999999;
	int time=500;

	public static void main(String[] args) {		
		testeIoT t = new testeIoT();

		
		t.testeClean();
		
//		iot.PacuHPC.execute("myClass", new Object[]{4});
//		System.out.println(iot.PacuHPC.getValue("var").getCorrectResult());
		
//		t.mqtt();
		
	/*	iot.PacuHPC.register(Methods.class, "cl");
		iot.PacuHPC.executeAllCores("cl", "teste");
		iot.PacuHPC.executeAllCores("cl", "teste");*/
		
//		for (iot.getIoTDevices())
			
//t.getSensingDelay();
		
/*		iot.setSensorMetadata(iot.getIoTDevices().get(0), "cam", 41, 1000, 30000, Constants.IoT.INPUT, Constants.IoT.GENERIC);
		
		Entry<String, String> rasp = iot.getIoTDevices().get(0);
		Entry<String, String> cam = iot.getSensors(rasp).get(0);
		iot.getSensingDataNow(rasp, cam).showData();*/
		

//		t.android();
		
//		t.testeBeagle();
//		t.testeRasp();
//		t.arduino();
//		t.tresArduino();
//		t.testeGalileo();
//		t.tresGalileo();
//		t.testeNovaAPI();
//		t.topic();
//		t.sensores3();
//		t.arduinoERasp();
//		t.arduino1();
//		t.t();
		
//		Entry<String, String> raspberry = iot.getDeviceByName("Raspberry").get(0);
//		iot.setSensorMetadata(raspberry, "amarelo", 5, 100, 100, JCL_IoTFacadeImpl.INPUT, JCL_IoTFacadeImpl.GENERIC);		
	
//		t.getSensingData();
		
		JCL_IoTfacade.PacuHPC.destroy();
		
//		t.cast();
	}
	
	public void testeClean(){
		try{
			Device device = iot.<Device>getIoTDevices().get(0);
			iot.setSensorMetadata(device, "alias", 16, maxRecords, 1000, "in", 0);
			//		
			iot.PacuHPC.cleanEnvironment();

			Sensor s = iot.<Sensor>getSensors(device).get(0);
			for (Entry<Integer, JCL_Sensor> data: iot.getSensingData(device, s).entrySet()){
				System.out.println(data);
			}
	/*		iot.PacuHPC.register(UserServices.class, "class");
			iot.registerContext(device, s, new JCL_Expression("S0>400"), "ctx");
			iot.addContextAction("ctx", false, "class", "execute", new Object[]{5});*/
			
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public void testeNovaAPI(){
//      Entry<String, String> rasp = iot.getDeviceByName("raspberry").get(0);
      Device rasp = iot.<Device>getIoTDevices().get(0);
      int time=500;
      
      
      iot.setSensorMetadata(rasp, "push", 7, maxRecords, time, Constants.IoT.INPUT, Constants.IoT.GENERIC);
      iot.setSensorMetadata(rasp, "led", 11, maxRecords, time, Constants.IoT.OUTPUT, Constants.IoT.GENERIC);
      
      Sensor push = iot.<Sensor>getSensorByName(rasp, "push").get(0), 
              led = iot.<Sensor>getSensorByName(rasp, "led").get(0);
      
      System.out.println(iot.registerContext(rasp, push, new JCL_Expression("S0=0"), "ctx"));
      System.out.println(iot.registerContext(rasp, push, new JCL_Expression("S0=1"), "ctx2"));
      
      System.out.println(iot.addContextAction("ctx", rasp, led, new Object[]{1}));
      System.out.println(iot.addContextAction("ctx2", rasp, led, new Object[]{0}));
      
      List<Device> devicesList = iot.getIoTDevices();
      
      try {
		Thread.sleep(1000);
	} catch (InterruptedException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
      
      for (Device device:devicesList){
          List<Sensor> sensorList = iot.getSensors(device);
          System.out.println(device);
          for (Sensor sensor:sensorList){                
              Map<Integer, JCL_Sensor> sensingData = iot.getSensingData(device, sensor);  
              System.out.println(sensor);
          }
      }
		
	}
	
	public void getSensingData(){
		Entry<String, String> rasp = iot.getIoTDevices().get(0);
		int time=50;
		
		iot.setSensorMetadata(rasp, "push", 7, maxRecords, time, Constants.IoT.INPUT, Constants.IoT.GENERIC);
		
		Entry<String, String> push = iot.getSensorByName(rasp, "push").get(0);
	
		try {
			Thread.sleep(600);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		for ( Entry<Integer, JCL_Sensor> s : iot.getSensingData(rasp, push).entrySet() ){
			System.out.println(s.getValue().toString());
		}
		
	}
	
	public void cast(){
		int i = 10;
		Object obj = i;
		
		System.out.println(obj.toString());
		
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		ObjectOutputStream os;
		try {
			os = new ObjectOutputStream(out);
			os.writeObject(obj);
			
		    ByteArrayInputStream in = new ByteArrayInputStream(out.toByteArray());
		    ObjectInputStream is = new ObjectInputStream(in);
		    System.out.println(Double.parseDouble(""+is.readObject()));		
		    } catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void teste(){
		JCL_IoTfacade iot = JCL_IoTFacadeImpl.getInstance();

		List<Entry<String, String>> devicesList = iot.getIoTDevices();
		for (Entry<String, String> device:devicesList){
			List<Entry<String, String>> sensorList = iot.getSensors(device);
			for (Entry<String, String> sensor:sensorList){				
				Map<Integer, JCL_Sensor> sensingData = iot.getSensingData(device, sensor);		
			}
		}
		iot.PacuHPC.destroy();
	}

	public void mqtt(){
		int time = 20;
		Entry<String, String> device = iot.getIoTDevices().get(0);
		
		
//		iot.setSensorMetadata(device, "qualquer", 17, maxRecords, 999999999, "input", 0);
		iot.setSensorMetadata(device, "sensor", 17, maxRecords, time, "input", 0);
		System.out.println(iot.getAllSensingData(device, iot.getSensors(device).get(0)).size());
		
//		Thread t = new Thread(new SensorListener());
//		t.start();
//		try{
//			Thread.sleep(1000);
//		}catch(Exception e){
//			
//		}

		iot.setSensorMetadata(device, "sensor", 17, maxRecords, time, "input", 0);
	}
	
	public void tresGalileo(){
		Entry<String, String> g1 = iot.getIoTDevices().get(0);
		Entry<String, String> g2 = iot.getIoTDevices().get(1);
		//Entry<String, String> g3 = iot.getIoTDevices().get(2);
		
		int time = 500;
		
		iot.setSensorMetadata(g1, "gal-" + time + "-photo", 15, maxRecords, time, "input", 0);
		iot.setSensorMetadata(g1, "gal-" + time + "-ntc", 16, maxRecords, time, "input", 0);
		iot.setSensorMetadata(g1, "gal-" + time + "-potentiometer", 17, maxRecords, time, "input", 0);

		iot.setSensorMetadata(g2, "gal-" + time + "-push1", 15, maxRecords, time, "input", 0);
		iot.setSensorMetadata(g2, "gal-" + time + "-push2", 16, maxRecords, time, "input", 0);
		iot.setSensorMetadata(g2, "gal-" + time + "-push3", 17, maxRecords, time, "input", 0);		
		
	/*	iot.setSensorMetadata(g3, "gal-" + time + "-push1", 15, maxRecords, time, "input", 0);
		iot.setSensorMetadata(g3, "gal-" + time + "-push2", 16, maxRecords, time, "input", 0);
		iot.setSensorMetadata(g3, "gal-" + time + "-push3", 17, maxRecords, time, "input", 0);*/
		
		
	}
	
	public void arduino(){
//		Entry<String, String> rasp = iot.getDeviceByName("arduino").get(0);
		Entry<String, String> ard = iot.getIoTDevices().get(0);
		System.out.println(iot.getIoTDeviceMetadata(ard));
		
		int time = 500;
//iot.turnOn(rasp);
		boolean b = iot.setSensorMetadata(ard, "led", 3, 99999, time, Constants.IoT.INPUT, Constants.IoT.GENERIC);
System.out.println(b);
b = iot.setSensorMetadata(ard, "temp1", 58, 99999, time, "inp", Constants.IoT.GENERIC);
//System.out.println(b);
iot.setSensorMetadata(ard, "temp2", 57, 99999, time, "inp", Constants.IoT.GENERIC);
try{	
/*Thread.sleep(150000);
iot.standBy(rasp);*/
}catch(Exception e){
	e.printStackTrace();
}
/*iot.setSensorMetadata(rasp, "hpclab/temp", 56, 500, 10000, "inp", Constants.IoT.GENERIC);
iot.setSensorMetadata(rasp, "hpclab/temp3", 57, 500, 10000, "inp", Constants.IoT.GENERIC);
iot.setSensorMetadata(rasp, "another sensor", 58, 500, 10000, "inp", Constants.IoT.GENERIC);
iot.setSensorMetadata(rasp, "hpclab/temp", 59, 500, 10000, "inp", Constants.IoT.GENERIC);*/
	}
	
	public void tresArduino(){
//		Entry<String, String> rasp = iot.getDeviceByName("arduino").get(0);
		Entry<String, String> ard = iot.getIoTDevices().get(0);
		Entry<String, String> ard1 = iot.getIoTDevices().get(1);
		System.out.println(iot.getIoTDeviceMetadata(ard));
		
		int time = 500;
//iot.turnOn(rasp);
		boolean b = iot.setSensorMetadata(ard, "led", 3, 99999, time, Constants.IoT.INPUT, Constants.IoT.GENERIC);
		b = iot.setSensorMetadata(ard1, "led", 3, 99999, time, Constants.IoT.INPUT, Constants.IoT.GENERIC);
System.out.println(b);
b = iot.setSensorMetadata(ard, "temp1", 58, 99999, time, "inp", Constants.IoT.GENERIC);
b = iot.setSensorMetadata(ard1, "temp1", 58, 99999, time, "inp", Constants.IoT.GENERIC);
//System.out.println(b);
iot.setSensorMetadata(ard, "temp2", 57, 99999, time, "inp", Constants.IoT.GENERIC);
iot.setSensorMetadata(ard1, "temp2", 57, 99999, time, "inp", Constants.IoT.GENERIC);
try{	
/*Thread.sleep(150000);
iot.standBy(rasp);*/
}catch(Exception e){
	e.printStackTrace();
}
/*iot.setSensorMetadata(rasp, "hpclab/temp", 56, 500, 10000, "inp", Constants.IoT.GENERIC);
iot.setSensorMetadata(rasp, "hpclab/temp3", 57, 500, 10000, "inp", Constants.IoT.GENERIC);
iot.setSensorMetadata(rasp, "another sensor", 58, 500, 10000, "inp", Constants.IoT.GENERIC);
iot.setSensorMetadata(rasp, "hpclab/temp", 59, 500, 10000, "inp", Constants.IoT.GENERIC);*/
	}
	
	public void getSensingDelay(){
		Entry<String, String> beagle = iot.getIoTDevices().get(0);
		
		Map<Integer, JCL_Sensor> m = iot.getAllSensingData(beagle, iot.getSensors(beagle).get(0));
		for (Integer i:m.keySet()){
			System.out.println(i+": " + m.get(i).getTime());
		}
		System.out.println(m.size());
	}
	
	public void android(){
		Entry<String, String> android = iot.getIoTDevices().get(0);
		Entry<String, String> sensor = iot.getSensors(android).get(0);
		
		iot.registerMQTTContext(android, sensor, new JCL_Expression("S0<50"), "pot");
	}
	
	
	public void testeBeagle(){
		Entry<String, String> beagle = iot.getIoTDevices().get(0);

		int time=50;
		
		boolean b = iot.setSensorMetadata(beagle, "beagle-" + time + "-push1", 8, maxRecords, time, Constants.IoT.INPUT, Constants.IoT.GENERIC);
		b = iot.setSensorMetadata(beagle, "beagle-" + time + "-push2", 10, maxRecords, time, Constants.IoT.INPUT, Constants.IoT.GENERIC);
		b = iot.setSensorMetadata(beagle, "beagle-" + time + "-push3", 12, maxRecords, time, Constants.IoT.INPUT, Constants.IoT.GENERIC);
		/*b = iot.setSensorMetadata(beagle, "", 14, maxRecords, time, Constants.IoT.INPUT, Constants.IoT.GENERIC);
		b = iot.setSensorMetadata(beagle, "", 16, maxRecords, time, Constants.IoT.INPUT, Constants.IoT.GENERIC);
		b = iot.setSensorMetadata(beagle, "", 18, maxRecords, time, Constants.IoT.INPUT, Constants.IoT.GENERIC);
		System.out.println(b);*/
//		System.out.println(b);*/
	}	
	
	public void testeRasp(){
		
		List<Entry<String, String>> devicesList1 = iot.getIoTDevices();
		List<Device> devicesList2 = iot.getIoTDevices();
		
		System.out.println(devicesList1);
		System.out.println(devicesList2);

		Device d1 = devicesList2.get(0);
		Entry<String, String> d11 = devicesList2.get(0); 
		
		List<Device> listRasp = iot.getDeviceByName("Motorola XT1650");
		Device rasp = listRasp.get(0);
		Entry<String, String> rasp2 = listRasp.get(0);
		
		Device d2 = iot.<Device>getIoTDevices().get(0);
		Device rasp5 = iot.<Device>getDeviceByName("Motorola XT1650").get(0);
		
		
		iot.setSensorMetadata(rasp, "push", 7, maxRecords, time, Constants.IoT.INPUT, Constants.IoT.GENERIC);
		
		List<Sensor> sensorList1 = iot.getSensors(rasp);
		
		Sensor s = iot.<Sensor>getSensors(rasp).get(0);
		
	//	iot.getSensingData(d1, s);
		List<Sensor> sensorList = iot.getSensorByName(rasp, "push");
		
		System.out.println(sensorList);
		
		Sensor sensorList2 = iot.<Sensor>getSensorByName(rasp, "push").get(0);
		Entry<String, String> sensorList3 = iot.getSensorByName(rasp, "push").get(0);

		
/*//		iot.setSensorMetadata(rasp2, "rasp-" + time + "-push1", 7, maxRecords, time, Constants.IoT.INPUT, Constants.IoT.GENERIC);
//		iot.setSensorMetadata(rasp3, "rasp-" + time + "-push1", 7, maxRecords, time, Constants.IoT.INPUT, Constants.IoT.GENERIC);
		iot.setSensorMetadata(rasp, "rasp-" + time + "-push2", 8, maxRecords, time, Constants.IoT.INPUT, Constants.IoT.GENERIC);
//		iot.setSensorMetadata(rasp2, "rasp-" + time + "-push2", 8, maxRecords, time, Constants.IoT.INPUT, Constants.IoT.GENERIC);
//		iot.setSensorMetadata(rasp3, "rasp-" + time + "-push2", 8, maxRecords, time, Constants.IoT.INPUT, Constants.IoT.GENERIC);
		iot.setSensorMetadata(rasp, "rasp-" + time + "-push3", 10, maxRecords, time, Constants.IoT.INPUT, Constants.IoT.GENERIC);
//		iot.setSensorMetadata(rasp2, "rasp-" + time + "-push3", 10, maxRecords, time, Constants.IoT.INPUT, Constants.IoT.GENERIC);
//		iot.setSensorMetadata(rasp3, "rasp-" + time + "-push3", 10, maxRecords, time, Constants.IoT.INPUT, Constants.IoT.GENERIC);
				
//		iot.setSensorMetadata(rasp, "rasp-" + time + "-cam", 41, maxRecords, time, Constants.IoT.INPUT, Constants.IoT.GENERIC);
		
//		iot.setSensorMetadata(rasp, "", 11, maxRecords, time, Constants.IoT.INPUT, Constants.IoT.GENERIC);
//		iot.setSensorMetadata(rasp, "", 13, maxRecords, time, Constants.IoT.INPUT, Constants.IoT.GENERIC);
//		iot.setSensorMetadata(rasp, "", 15, maxRecords, time, Constants.IoT.INPUT, Constants.IoT.GENERIC);
//		System.out.println(b);*/
		
	/*	iot.setSensorMetadata(rasp, "push", 7, maxRecords, time, Constants.IoT.INPUT, Constants.IoT.GENERIC);
		iot.setSensorMetadata(rasp, "led", 11, maxRecords, time, Constants.IoT.OUTPUT, Constants.IoT.GENERIC);
		
		Entry<String, String> push = iot.getSensorByName(rasp, "push").get(0), 
				led = iot.getSensorByName(rasp, "led").get(0);
		
		System.out.println(iot.registerContext(rasp, push, new JCL_Expression("S0=0"), "ctx"));
		System.out.println(iot.registerContext(rasp, push, new JCL_Expression("S0=1"), "ctx2"));
		
		System.out.println(iot.addContextAction("ctx", rasp, led, new Object[]{1}));
		System.out.println(iot.addContextAction("ctx2", rasp, led, new Object[]{0}));
		*/
		
	}
	
}