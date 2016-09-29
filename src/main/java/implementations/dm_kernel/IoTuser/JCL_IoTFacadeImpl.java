package implementations.dm_kernel.IoTuser;

import implementations.collections.JCLHashMap;
import implementations.dm_kernel.ConnectorImpl;
import implementations.dm_kernel.MessageControlImpl;
import implementations.dm_kernel.MessageGenericImpl;
import implementations.dm_kernel.MessageImpl;
import implementations.dm_kernel.MessageMetadataImpl;
import interfaces.kernel.JCL_IoTfacade;
import interfaces.kernel.JCL_Sensor;
import interfaces.kernel.JCL_connector;
import interfaces.kernel.JCL_message;
import interfaces.kernel.JCL_message_bool;
import interfaces.kernel.JCL_message_control;
import interfaces.kernel.JCL_message_generic;
import interfaces.kernel.JCL_message_metadata;
import interfaces.kernel.JCL_message_sensor;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import commom.JCL_SensorImpl;

/*
 * 
 *  
 1 public boolean register(File[] f, String classToBeExecuted) {
 2 public boolean unRegister(String nickName) {
 3 public void executeSimple(String objectNickname, Object... args) {
 4 public String execute(String objectNickname, Object... args) {
 5 public String execute(String className, String methodName, Object... args) {
 6 public JCL_result getResultBlocking(String ID) {
 7 public JCL_result getResultUnblocking(String ID) {
 8 public JCL_result removeResult(String ID) {
 9 public Object instantiateGlobalVar(String varName, Class<?> varType,
 10 public boolean instantiateGlobalVar(String varName, Object instance) {
 11 public boolean destroyGlobalVar(String varName) {
 12 public boolean setValue(String varName, Object value) {
 13 public boolean setValueUnlocking(String varName, Object value) {
 14 public JCL_result getValue(String varName) {
 15 public JCL_result getValueLocking(String varName) {
 16 public void destroy() {
 17 public boolean containsGlobalVar(String ninckName){
 18 public boolean containsTask(String ninckName){
 19 public List<String> getHosts() {

 METHOD DEPRECATED in JCL distributed version: public boolean register(Class<?> object, String nickName) {

 */

public class JCL_IoTFacadeImpl implements JCL_IoTfacade{

//	private static ConcurrentMap<String,List<String>> jarsSlaves;
//	private static ConcurrentMap<String,String[]> slaves;	
//	private static List<String> slavesIDs;
	private ConcurrentMap<String, Map<String, String>> devices;
	private static JCL_IoTfacade instanceIoT;
//	public static JCL_facade Pacu;
//	private static JCL_facade Lamb;
//	private String serverAdd;	
//	private int serverPort;
			
	protected JCL_IoTFacadeImpl(){		
		
		try {
			//single pattern
			if (instanceIoT == null){
				instanceIoT = this;
			}
			Pacu.version();						
			//ini variables			
//			jarsSlaves = new ConcurrentHashMap<String,List<String>>();			
//			jcl = super.getInstance();
							
			
			//ini jcl lambari 
//			jclLamb.register(JCL_IoTFacadeImplLamb.class, "JCL_IoTFacadeImplLamb");
			
			//getHosts using lambari
			int type = 3;
			Object[] argsLam = {Holder.serverIP(), Holder.serverPort(),type};
			String t = Lambari.execute("JCL_FacadeImplLamb", "getSlaveIds", argsLam);
			JCL_message_generic mgh = (JCL_message_generic) Lambari.getResultBlocking(t).getCorrectResult();
			
			devices = (ConcurrentMap<String, Map<String, String>>) mgh.getRegisterData();
			
			if (devices==null){
				devices = new ConcurrentHashMap<String, Map<String,String>>();	
			}
			
			//finish
			System.out.println("client JCL IoT is OK");
			
		} catch (Exception e) {
			System.err.println("JCL facade constructor error");
			e.printStackTrace();			
		}
	}

//	@Override
//	public void destroy() {
//		try {
//						
//			Pacu.destroy();
//
//		} catch (Exception e) {
//			System.err.println("problem in JCL facade destroy()");
//			e.printStackTrace();
//		}
//
//	}
	
//	//Get HashMap
//	public static <K, V> Map<K, V> GetHashMap(String gvName){
//		return new JCLHashMap<K, V>(gvName);
//	}
//	
//	//Get HashMap
//	public static <K, V> Map<K, V> GetHashMap(String gvName,String ClassName,File[] f){
//		return new JCLHashMap<K, V>(gvName,ClassName,f);
//	}
	@Override
	public List<Entry<String, String>> getSensingDevices() {
		try {
			List<Entry<String, String>> list = new ArrayList<>();
			for (String mac_port : devices.keySet()) {
				list.add(new implementations.util.Entry(mac_port, devices.get(mac_port).get("DEVICE_ID")));
			}
			return list;
		} catch (Exception e) {
			System.err.println("problem in JCL facade getSensingDevices()");
			e.printStackTrace();
			return null;
		}

	}

	@Override
	public List<Entry<String, String>> getDevices() {
		// TODO Auto-generated method stub
		try{
		int type = 5;
		Object[] argsLam = {Holder.serverIP(), Holder.serverPort(),type};
		String t = Lambari.execute("JCL_FacadeImplLamb", "getSlaveIds", argsLam);
		JCL_message_generic mgh = (JCL_message_generic) Lambari.getResultBlocking(t).getCorrectResult();
		
		ConcurrentMap<String, Map<String, String>> devicesL = (ConcurrentMap<String, Map<String, String>>) mgh.getRegisterData();
		
		List<Entry<String, String>> list = new ArrayList<>();
		for (String mac_port : devicesL.keySet()) {
			list.add(new implementations.util.Entry(mac_port, devicesL.get(mac_port).get("DEVICE_ID")));
		}
		return list;
		
	} catch (Exception e) {
		System.err.println("problem in JCL facade getSensors(Entry<String, String> device)");
		e.printStackTrace();
		return null;
	}
				
	}

	@Override
	public List<Entry<String, String>> getSensors(Entry<String, String> device) {
		try {
			List<Entry<String, String>> sensors = new ArrayList<>();
			Map<String, String> meta = devices.get(device.getKey());
			String[] enableSensors = meta.get("ENABLE_SENSOR").split(";");
			for (int i = 0; i < enableSensors.length; i++) {
				sensors.add(new implementations.util.Entry(meta.get("SENSOR_ALIAS_" + enableSensors[i]),
						enableSensors[i] + ""));
			}
			return sensors;
		} catch (Exception e) {
			System.err.println("problem in JCL facade getSensors(Entry<String, String> device)");
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public Map<Integer, JCL_Sensor> getsensingdata(Entry<String, String> device, Entry<String, String> Sensor) {
		try {

			Map<Integer, JCL_Sensor> jcl_hashMap = new JCLHashMap<>(device.getKey() + Sensor.getValue()+"_value");
			Map<Integer, JCL_Sensor> sensors = new HashMap<>();
			int size = jcl_hashMap.size();
			for (int i = 0; i < Integer.min(size, 10); i++) {
				sensors.put(size - i, jcl_hashMap.get(size - i));
			}
			return sensors;
		} catch (Exception e) {
			System.err.println(
					"problem in JCL facade getsensingdata(Entry<String, String> device, Entry<String, String> Sensor)");
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public Map<Integer, JCL_Sensor> getallsensingdata(Entry<String, String> device, Entry<String, String> Sensor) {
		// TODO Auto-generated method stub
		try {
			return new JCLHashMap<>(device.getKey() + Sensor.getValue()+"_value");
		} catch (Exception e) {
			System.err.println(
					"problem in JCL facade getallsensingdata(Entry<String, String> device, Entry<String, String> Sensor)");
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public Entry<Integer, JCL_Sensor> getlastsensingdata(Entry<String, String> device, Entry<String, String> Sensor) {
		try {
			Map<Integer, JCL_Sensor> jcl_hashMap = new JCLHashMap<>(device.getKey() + Sensor.getValue()+"_value");
			int size = jcl_hashMap.size();
			if(size>0){
			return new implementations.util.Entry(jcl_hashMap.size(), jcl_hashMap.get(size));
			}else{
				return null;
			}
			
			} catch (Exception e) {
			System.err.println(
					"problem in JCL facade getlastsensingdata(Entry<String, String> device, Entry<String, String> Sensor)");
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public JCL_Sensor getsensingdatanow(Entry<String, String> device, Entry<String, String> Sensor, Object... args) {
		try {			

			String IP = devices.get(device.getKey()).get("IP");
			String port = devices.get(device.getKey()).get("PORT");

			
			JCL_message_generic msg = new MessageGenericImpl();
			msg.setType(44);
			Object[] arg = {Sensor.getValue(),args};
			msg.setRegisterData(arg);
				
			JCL_connector controlConnector = new ConnectorImpl(false);
			controlConnector.connect(IP,Integer.parseInt(port),null);		
			JCL_message_sensor msgR = (JCL_message_sensor) controlConnector.sendReceiveG(msg, null);
				
			JCL_Sensor sensor = new JCL_SensorImpl();
			sensor.setDataType(msgR.getDataType());
			sensor.setTime(System.currentTimeMillis());
			sensor.setObject(msgR.getValue());
			
			
			return sensor; 		 
				
		} catch (Exception e){
			// TODO: handle exception
			System.err.println("problem in JCL IoTfacade getsensingdatanow(Entry<String, String> device, Entry<String, String> Sensor, Object... args)");
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public Map<String, String> getMetadata(Entry<String, String> device) {
		// TODO Auto-generated method stub
		try {
			
//			JCL_message_control msg = new MessageControlImpl();
//			msg.setType(41);
			return devices.get(device.getKey());
//			msg.setRegisterData(type,device.getKey());
//				
//			JCL_connector controlConnector = new ConnectorImpl(false);
//			controlConnector.connect(Holder.serverIP(), Holder.serverPort(),null);		
//			JCL_message_metadata jclR = (JCL_message_metadata) controlConnector.sendReceiveG(msg, null);
//				
//			return jclR.getMetadados(); 		 
				
		} catch (Exception e){
			// TODO: handle exception
			System.err.println("problem in JCL IoTfacade setMetadata(Entry<String, String> device, Map<String, String> Metadata)");
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public boolean setMetadata(Entry<String, String> device, Map<String, String> Metadata){
		// TODO Auto-generated method stub
		try {
			
			String IP = devices.get(device.getKey()).get("IP");
			String port = devices.get(device.getKey()).get("PORT");			
			
			JCL_message_metadata msg = new MessageMetadataImpl();
			msg.setType(47);				
			msg.setMetadados(Metadata);
		
			JCL_connector controlConnector = new ConnectorImpl(false);
			controlConnector.connect(IP, Integer.parseInt(port),null);		
			JCL_message_bool jclR = (JCL_message_bool) controlConnector.sendReceiveG(msg, null);


			if(jclR.getRegisterData()[0]){
				JCL_connector conn = new ConnectorImpl(false);
				conn.connect(Holder.serverIP(), Holder.serverPort(),null);
				JCL_message_bool jclRe = (JCL_message_bool) controlConnector.sendReceiveG(msg, null);			
				return jclRe.getRegisterData()[0];
			} else{
				return false;
			}
			
			//			controlConnector.connect(Holder.serverIP(), Holder.serverPort(),null);		
		
		} catch (Exception e){
			// TODO: handle exception
			System.err.println("problem in JCL IoTfacade setMetadata(Entry<String, String> device, Map<String, String> Metadata)");
			e.printStackTrace();
			return false;
		}

	}

	@Override
	public boolean restart(Entry<String, String> device) {
		// TODO Auto-generated method stub
		
		String IP = devices.get(device.getKey()).get("IP");
		String port = devices.get(device.getKey()).get("PORT");

		JCL_message msg = new MessageImpl();
		msg.setType(43);
			
		JCL_connector controlConnector = new ConnectorImpl(false);
		controlConnector.connect(IP,Integer.parseInt(port),null);		
		JCL_message msgR = (JCL_message) controlConnector.sendReceiveG(msg, null);
					
		return (msgR.getType()==100); 
	}

	@Override
	public boolean turnOn(Entry<String, String> device) {
		
		String IP = devices.get(device.getKey()).get("IP");
		String port = devices.get(device.getKey()).get("PORT");

		JCL_message msg = new MessageImpl();
		msg.setType(45);
			
		JCL_connector controlConnector = new ConnectorImpl(false);
		controlConnector.connect(IP,Integer.parseInt(port),null);		
		JCL_message msgR = (JCL_message) controlConnector.sendReceiveG(msg, null);
					
		return (msgR.getType()==101); 
	}

	@Override
	public boolean standBy(Entry<String, String> device) {

		String IP = devices.get(device.getKey()).get("IP");
		String port = devices.get(device.getKey()).get("PORT");

		JCL_message msg = new MessageImpl();
		msg.setType(46);
			
		JCL_connector controlConnector = new ConnectorImpl(false);
		controlConnector.connect(IP,Integer.parseInt(port),null);		
		JCL_message msgR = (JCL_message) controlConnector.sendReceiveG(msg, null);
					
		return (msgR.getType()==102); 
	}

	@Override
	public boolean setSensor(Entry<String, String> device, String sensor_alias, int sensor_id, int sensor_size,
			int sensor_sampling) {
		// TODO Auto-generated method stub
		String IP = devices.get(device.getKey()).get("IP");
		String port = devices.get(device.getKey()).get("PORT");
		
		JCL_message_control msg = new MessageControlImpl();
		msg.setType(49);
		msg.setRegisterData(sensor_alias,String.valueOf(sensor_id),String.valueOf(sensor_size),String.valueOf(sensor_sampling));
		
		JCL_connector controlConnector = new ConnectorImpl(false);
		controlConnector.connect(IP,Integer.parseInt(port),null);		
		JCL_message_bool msgR = (JCL_message_bool) controlConnector.sendReceiveG(msg, null);
					
		return msgR.getRegisterData()[0]; 
	}

	@Override
	public boolean LoadConfig(File configTxt) {
		// TODO Auto-generated method stub
		return false;
	}
	
	public static JCL_IoTfacade getInstance() {
		return Holder.getIoTInstance();
	}
	
//	public static JCL_facade getInstancePacu(){
//		Properties properties = new Properties();
//		try {
//			properties.load(new FileInputStream("../jcl_conf/config.properties"));
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//		
//		return Holder.getInstancePacu(properties);
//	}
	
//	public static JCL_facade getInstanceLambari() {
//		return Holder.getInstanceLambari();
//	}
	
	public static class Holder extends implementations.dm_kernel.user.JCL_FacadeImpl.Holder{
		
		protected static String serverIP() {			
			 return implementations.dm_kernel.user.JCL_FacadeImpl.Holder.ServerIP();
		}

		protected static int serverPort() {			
			return implementations.dm_kernel.user.JCL_FacadeImpl.Holder.ServerPort();			
		}
		
		protected synchronized static JCL_IoTfacade getIoTInstance(){
			
			Properties properties = new Properties();
			try {
				properties.load(new FileInputStream("../jcl_conf/config.properties"));
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			//get type of Instance 
			if (instanceIoT == null){
				instanceIoT = new JCL_IoTFacadeImpl();
			}
			
//			if (Pacu == null){
//				Pacu = implementations.dm_kernel.user.JCL_FacadeImpl.Holder.getInstancePacu(properties);
//			}
//
//			if (jclLamb == null){
//				jclLamb = implementations.dm_kernel.user.JCL_FacadeImpl.Holder.getInstanceLambari();
//			}
			
			return instanceIoT;
		}

//		protected synchronized static JCL_facade getInstancePacu(Properties properties){
//
//			//Pacu type
//			
//			if (jclPacu == null){
//				jclPacu = implementations.dm_kernel.user.JCL_FacadeImpl.Holder.getInstancePacu(properties);
//			}
//			
//			return jclPacu;
//		}
		
//		protected synchronized static JCL_facade getInstanceLambari(){
//			
//			//Lambari type
//			
//			if (jclLamb == null){
//				jclLamb = implementations.dm_kernel.user.JCL_FacadeImpl.Holder.getInstanceLambari();
//			}
//			
//			return jclLamb;
//		}		
	}

	@Override
	public boolean setSensor(Entry<String, String> device, String sensor_alias, int sensor_id, int sensor_size,
			int sensor_sampling, String inputOrOutput) {
		// TODO Auto-generated method stub
		return false;
	}
	
}
