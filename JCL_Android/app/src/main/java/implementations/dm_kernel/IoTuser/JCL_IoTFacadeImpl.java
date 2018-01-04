package implementations.dm_kernel.IoTuser;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Future;

import commom.Constants;
import commom.JCL_SensorImpl;
import implementations.collections.JCLHashMap;
import implementations.collections.JCLPFuture;
import implementations.dm_kernel.ConnectorImpl;
import implementations.dm_kernel.MessageControlImpl;
import implementations.dm_kernel.MessageGenericImpl;
import implementations.dm_kernel.MessageImpl;
import implementations.dm_kernel.MessageMetadataImpl;
import implementations.dm_kernel.user.JCL_FacadeImpl;
import interfaces.kernel.JCL_IoTfacade;
import interfaces.kernel.JCL_Sensor;
import interfaces.kernel.JCL_connector;
import interfaces.kernel.JCL_message;
import interfaces.kernel.JCL_message_bool;
import interfaces.kernel.JCL_message_control;
import interfaces.kernel.JCL_message_generic;
import interfaces.kernel.JCL_message_metadata;
import interfaces.kernel.JCL_message_sensor;
import interfaces.kernel.JCL_result;
import interfaces.kernel.datatype.Device;

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
    private static Map<String, Map<String, String>> devices;
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
//			PacuHPC.version();
            //ini variables
//			jarsSlaves = new ConcurrentHashMap<String,List<String>>();
//			jcl = super.getInstance();


            //ini jcl lambari
//			jclLamb.register(JCL_IoTFacadeImplLamb.class, "JCL_IoTFacadeImplLamb");

            //getHosts using lambari
//			int type = 3;
//			Object[] argsLam = {Holder.serverIP(), Holder.serverPort(),type};
//			Future<JCL_result> t = LambariHPC.execute("JCL_FacadeImplLamb", "getSlaveIds", argsLam);
//			JCL_message_generic mgh = (JCL_message_generic) (t.get()).getCorrectResult();

//			devices = (ConcurrentMap<String, Map<String, String>>) mgh.getRegisterData();
            int type[] = {4,5,6,7};
            devices = ((JCL_FacadeImpl)PacuHPC).getDevicesMetadados(type);

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

    public void destroy() {
        try {

            PacuHPC.destroy();

        } catch (Exception e) {
            System.err.println("problem in JCL facade destroy()");
            e.printStackTrace();
        }

    }

    protected static void update(){
        try {
            ((JCL_FacadeImpl)PacuHPC).update();
            int type[] = {4,5,6,7};
            devices = ((JCL_FacadeImpl)PacuHPC).getDevicesMetadados(type);

//		int type = 3;
//		Object[] argsLam = {Holder.serverIP(), Holder.serverPort(),type};
//		Future<JCL_result> t = LambariHPC.execute("JCL_FacadeImplLamb", "getSlaveIds", argsLam);
//		JCL_message_generic mgh;
//
//		mgh = (JCL_message_generic) (t.get()).getCorrectResult();
//
//		devices = (ConcurrentMap<String, Map<String, String>>) mgh.getRegisterData();
//
//		if (devices==null){
//			devices = new ConcurrentHashMap<String, Map<String,String>>();
//		}

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

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
    public <T extends java.util.Map.Entry<String, String>> List<T> getIoTDevices() {
        try {
            List<T> list = new ArrayList<>();
            for (String mac_port : devices.keySet()) {
                list.add((T) new Device(mac_port, devices.get(mac_port).get("DEVICE_ID")));
            }

            return list;
        } catch (Exception e) {
            System.err.println("problem in JCL facade getIoTDevices()");
            e.printStackTrace();
            return null;
        }

    }

    @Override
    public <T extends java.util.Map.Entry<String, String>> List<T> getSensors(java.util.Map.Entry<String, String> deviceNickname) {
        List<T> sensors = new ArrayList<>();
        try {
            Map<String, String> meta = devices.get(deviceNickname.getKey());
            if ( meta.get("ENABLE_SENSOR") == null )
                return sensors;
            String[] enableSensors = meta.get("ENABLE_SENSOR").split(";");
            for (int i = 0; i < enableSensors.length; i++) {
                sensors.add((T) new interfaces.kernel.datatype.Sensor(meta.get("SENSOR_ALIAS_" + enableSensors[i]),
                        enableSensors[i] + ""));
            }
        } catch (Exception e) {
            System.err.println("problem in JCL facade getSensors(Entry<String, String> deviceNickname)");
            e.printStackTrace();
            return null;
        }
        return sensors;
    }

    @Override
    public Map<Integer, JCL_Sensor> getSensingData(java.util.Map.Entry<String, String> deviceNickname, java.util.Map.Entry<String, String> sensorNickname) {
        try {

            Map<Integer, JCL_Sensor> jcl_hashMap = new JCLHashMap<>(deviceNickname.getKey() + sensorNickname.getValue()+"_value");
            Map<Integer, JCL_Sensor> sensors = new HashMap<>();
            int size = Integer.valueOf(PacuHPC.getValue(deviceNickname.getKey() + sensorNickname.getValue()+"_NUMELEMENTS").getCorrectResult().toString());
            for (int i = 1; i <= Math.min(size, 10); i++) {
                int pos = size - i;
                sensors.put(pos, jcl_hashMap.get(pos));
            }
            return sensors;
        } catch (Exception e) {
            System.err.println(
                    "problem in JCL facade getSensingData(Entry<String, String> deviceNickname, Entry<String, String> sensorNickname)");
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public  Map<Integer, JCL_Sensor> getAllSensingData(java.util.Map.Entry<String, String> deviceNickname, java.util.Map.Entry<String, String> sensorNickname) {
        // TODO Auto-generated method stub
        try {
            return new JCLHashMap<>(deviceNickname.getKey() + sensorNickname.getValue()+"_value");
        } catch (Exception e) {
            System.err.println(
                    "problem in JCL facade getAllSensingData(Entry<String, String> deviceNickname, Entry<String, String> sensorNickname)");
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public Entry<Integer, JCL_Sensor> getLastSensingData(java.util.Map.Entry<String, String> deviceNickname, java.util.Map.Entry<String, String> sensorNickname) {
        try {
            Map<Integer, JCL_Sensor> jcl_hashMap = new JCLHashMap<>(deviceNickname.getKey() + sensorNickname.getValue()+"_value");
            int size = Integer.valueOf(PacuHPC.getValue(deviceNickname.getKey() + sensorNickname.getValue()+"_NUMELEMENTS").getCorrectResult().toString());
            if(size>0){
                return new implementations.util.Entry(size - 1, jcl_hashMap.get(size - 1));
            }else{
                return null;
            }

        } catch (Exception e) {
            System.err.println(
                    "problem in JCL facade getLastSensingData(Entry<String, String> deviceNickname, Entry<String, String> sensorNickname)");
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public JCL_Sensor getSensingDataNow(java.util.Map.Entry<String, String> deviceNickname, java.util.Map.Entry<String, String> sensorNickname) {
        try {

            String IP = devices.get(deviceNickname.getKey()).get("IP");
            String port = devices.get(deviceNickname.getKey()).get("PORT");
            String mac =  devices.get(deviceNickname.getKey()).get("MAC");
            String portS =  devices.get(deviceNickname.getKey()).get("PORT_SUPER_PEER");

            JCL_message_generic msg = new MessageGenericImpl();
            msg.setType(44);
            Object[] arg = {sensorNickname.getValue()};
            msg.setRegisterData(arg);

            JCL_connector controlConnector = new ConnectorImpl(false);
            controlConnector.connect(IP,Integer.parseInt(port),mac);
            JCL_message_sensor msgR = (JCL_message_sensor) controlConnector.sendReceiveG(msg, portS);

            if (msgR != null){
                JCL_Sensor sensor = new JCL_SensorImpl();
                sensor.setDataType(msgR.getDataType());
                sensor.setTime(System.currentTimeMillis());
                sensor.setObject(msgR.getValue());

                return sensor;
            }

        } catch (Exception e){
            // TODO: handle exception
            System.err.println("problem in JCL IoTfacade getSensingDataNow(Entry<String, String> deviceNickname, Entry<String, String> sensorNickname)");
            e.printStackTrace();
        }
        return null;
    }

    public Map<String, String> getIoTDeviceMetadata(java.util.Map.Entry<String, String> deviceNickname) {
        // TODO Auto-generated method stub
        try {

//			JCL_message_control msg = new MessageControlImpl();
//			msg.setType(41);
            return devices.get(deviceNickname.getKey());
//			msg.setRegisterData(type,device.getKey());
//
//			JCL_connector controlConnector = new ConnectorImpl(false);
//			controlConnector.connect(Holder.serverIP(), Holder.serverPort(),null);
//			JCL_message_metadata jclR = (JCL_message_metadata) controlConnector.sendReceiveG(msg, null);
//
//			return jclR.getMetadados();

        } catch (Exception e){
            // TODO: handle exception
            System.err.println("problem in JCL IoTfacade getIoTDeviceMetadata(Entry<String, String> deviceNickname)");
            e.printStackTrace();
            return null;
        }
    }

    public boolean setIoTDeviceMetadata(java.util.Map.Entry<String, String> deviceNickname, Map<String, String> metadata){
        // TODO Auto-generated method stub
        try {

            String IP = devices.get(deviceNickname.getKey()).get("IP");
            String port = devices.get(deviceNickname.getKey()).get("PORT");
            String mac =  devices.get(deviceNickname.getKey()).get("MAC");
            String portS =  devices.get(deviceNickname.getKey()).get("PORT_SUPER_PEER");

            JCL_message_metadata msg = new MessageMetadataImpl();
            msg.setType(47);
            msg.setMetadados(metadata);

            JCL_connector controlConnector = new ConnectorImpl(false);
            controlConnector.connect(IP, Integer.parseInt(port), mac);
            JCL_message_bool jclR = (JCL_message_bool) controlConnector.sendReceiveG(msg, portS);


            if(jclR.getRegisterData()[0]){
                JCL_connector conn = new ConnectorImpl(false);
                //	conn.connect(Holder.serverIP(), Holder.serverPort(),null);
                //	JCL_message_bool jclRe = (JCL_message_bool) controlConnector.sendReceiveG(msg, null);
                update();
                //return jclRe.getRegisterData()[0];
                return jclR.getRegisterData()[0];
            } else{
                return false;
            }

            //			controlConnector.connect(Holder.serverIP(), Holder.serverPort(),null);

        } catch (Exception e){
            // TODO: handle exception
            System.err.println("problem in JCL IoTfacade setIoTDeviceMetadata(Entry<String, String> deviceNickname, Map<String, String> metadata)");
            e.printStackTrace();
            return false;
        }

    }

    @Override
    public boolean turnOn(java.util.Map.Entry<String, String> deviceNickname) {
        try{
            String IP = devices.get(deviceNickname.getKey()).get("IP");
            String port = devices.get(deviceNickname.getKey()).get("PORT");
            String mac =  devices.get(deviceNickname.getKey()).get("MAC");
            String portS =  devices.get(deviceNickname.getKey()).get("PORT_SUPER_PEER");

            JCL_message msg = new MessageImpl();
            msg.setType(45);

            JCL_connector controlConnector = new ConnectorImpl(false);
            controlConnector.connect(IP,Integer.parseInt(port),mac);
            JCL_message msgR = (JCL_message) controlConnector.sendReceiveG(msg, portS);
            update();
            return (msgR.getType()==101);
        }catch(Exception e){
            System.err.println("problem in JCL IoTfacade turnOn(Entry<String, String> deviceNickname");
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean standBy(java.util.Map.Entry<String, String> deviceNickname) {
        try{
            String IP = devices.get(deviceNickname.getKey()).get("IP");
            String port = devices.get(deviceNickname.getKey()).get("PORT");
            String mac =  devices.get(deviceNickname.getKey()).get("MAC");
            String portS =  devices.get(deviceNickname.getKey()).get("PORT_SUPER_PEER");

            JCL_message msg = new MessageImpl();
            msg.setType(46);

            JCL_connector controlConnector = new ConnectorImpl(false);
            controlConnector.connect(IP,Integer.parseInt(port),mac);
            JCL_message msgR = (JCL_message) controlConnector.sendReceiveG(msg,portS);

            update();
            return (msgR.getType()==102);
        }catch(Exception e){
            System.err.println("problem in JCL IoTfacade standBy(Entry<String, String> deviceNickname");
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean setSensorMetadata(java.util.Map.Entry<String, String> deviceNickname, String sensorAlias, int sensorId, int sensorSize,
                                     int sensorSampling, String inputOrOutput, int type) {
        // TODO Auto-generated method stub
        try{
            String IP = devices.get(deviceNickname.getKey()).get("IP");
            String port = devices.get(deviceNickname.getKey()).get("PORT");
            String mac =  devices.get(deviceNickname.getKey()).get("MAC");
            String portS =  devices.get(deviceNickname.getKey()).get("PORT_SUPER_PEER");

            JCL_message_control msg = new MessageControlImpl();
            msg.setType(49);
            msg.setRegisterData(sensorAlias,String.valueOf(sensorId),String.valueOf(sensorSize),String.valueOf(sensorSampling), String.valueOf(inputOrOutput), Integer.toString(type));

            JCL_connector controlConnector = new ConnectorImpl(false);
            controlConnector.connect(IP,Integer.parseInt(port),mac);
            JCL_message_bool msgR = (JCL_message_bool) controlConnector.sendReceiveG(msg, portS);
            update();
            return msgR.getRegisterData()[0];
        }catch(Exception e){
            System.err.println("problem in JCL IoTfacade setSensorMetadata(Entry<String, String> deviceNickname, String sensorAlias, int sensorId, int sensorSize, int sensorSampling, String inputOrOutput, int type)");
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean removeSensor(java.util.Map.Entry<String, String> deviceNickname, java.util.Map.Entry<String, String> sensorNickname){
        try {

            String IP = devices.get(deviceNickname.getKey()).get("IP");
            String port = devices.get(deviceNickname.getKey()).get("PORT");
            String mac =  devices.get(deviceNickname.getKey()).get("MAC");
            String portS =  devices.get(deviceNickname.getKey()).get("PORT_SUPER_PEER");

            JCL_message_generic msg = new MessageGenericImpl();
            msg.setType(50);
            Object[] arg = {sensorNickname.getValue()};
            msg.setRegisterData(arg);

            JCL_connector controlConnector = new ConnectorImpl(false);
            controlConnector.connect(IP,Integer.parseInt(port),mac);

            JCL_message_bool msgR = (JCL_message_bool) controlConnector.sendReceiveG(msg, portS);
            update();
            return msgR.getRegisterData()[0];

        } catch (Exception e){
            // TODO: handle exception
            System.err.println("problem in JCL IoTfacade removeSensor(Entry<String, String> deviceNickname, Entry<String, String> sensorNickname)");
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean isDeviceInStandBy(java.util.Map.Entry<String, String> deviceNickname) {
        try{
            String standBy = getIoTDeviceMetadata(deviceNickname).get("STANDBY");
            if (standBy == null)
                return false;
            return Boolean.valueOf(standBy);
        }catch(Exception e){
            System.err.println("problem in JCL IoTfacade isDeviceInStandBy(Entry<String, String> deviceNickname)");
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public int getIoTDeviceCores(java.util.Map.Entry<String, String> deviceNickname) {
        try{
            String core = devices.get(deviceNickname.getKey()).get("CORE(S)");
            if (core != null)
                return Integer.parseInt(core);
        }catch(Exception e){
            System.err.println("problem in JCL IoTfacade getIoTDeviceCores(Entry<String, String> deviceNickname)");
            e.printStackTrace();
        }
        return 1;
    }

    @Override
    public <T extends java.util.Map.Entry<String, String>> Map<T, Integer> getAllIoTDeviceCores() {
        try{
            Map<T, Integer> result = new HashMap<>();
            for (String s:devices.keySet() ){
                String core = devices.get(s).get("CORE(S)");
                result.put((T) new Device(s, devices.get(s).get("DEVICE_ID")), core!=null?Integer.parseInt(core):1);
            }
            return result;
        }catch(Exception e){
            System.err.println("problem in JCL IoTfacade getAllIoTDeviceCores()");
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Map<String, String> getSensorMetadata(java.util.Map.Entry<String, String> deviceNickname, java.util.Map.Entry<String, String> sensorNickname) {
        try{
            Map<String, String> sensorMetadata = new HashMap<>();
            Map<String, String> deviceMetadata = getIoTDeviceMetadata(deviceNickname);
            for (String key: deviceMetadata.keySet()){
                if ( key.startsWith("SENSOR_") && key.endsWith("_" + sensorNickname.getValue()) ){
                    sensorMetadata.put(key, deviceMetadata.get(key));
                }
            }
            return sensorMetadata;
        }catch(Exception e){
            System.err.println("problem in JCL IoTfacade getSensorMetadata(Entry<String, String> deviceNickname, Entry<String, String> sensorNickname)");
            e.printStackTrace();
        }
        return null;
    }

    public boolean setEncryption(java.util.Map.Entry<String, String> deviceNickname, boolean encryption){
        try{
            String IP = devices.get(deviceNickname.getKey()).get("IP");
            String port = devices.get(deviceNickname.getKey()).get("PORT");
            String mac =  devices.get(deviceNickname.getKey()).get("MAC");
            String portS =  devices.get(deviceNickname.getKey()).get("PORT_SUPER_PEER");

            JCL_message_control msg = new MessageControlImpl();
            msg.setType(53);
            msg.setRegisterData(String.valueOf(encryption));

            JCL_connector controlConnector = new ConnectorImpl(false);
            controlConnector.connect(IP,Integer.parseInt(port),mac);
            JCL_message_bool msgR = (JCL_message_bool) controlConnector.sendReceiveG(msg,portS);
            update();
            return msgR.getRegisterData()[0];
        }catch(Exception e){
            System.err.println("problem in JCL IoTfacade setEncryption(Entry<String, String> deviceNickname, boolean encryption)");
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean registerContext(java.util.Map.Entry<String, String> deviceNickname, java.util.Map.Entry<String, String> sensorNickname, JCL_Expression expression, String contextNickname) {
        try {
            JCL_IoTFacadeImpl.PacuHPC.instantiateGlobalVar(contextNickname + "_CONTEXT", deviceNickname.getKey().toString());
            String IP = devices.get(deviceNickname.getKey()).get("IP");
            String port = devices.get(deviceNickname.getKey()).get("PORT");
            String mac =  devices.get(deviceNickname.getKey()).get("MAC");
            String portS =  devices.get(deviceNickname.getKey()).get("PORT_SUPER_PEER");

            JCL_message_generic msg = new MessageGenericImpl();
            msg.setType(54);
            Object[] arg = {expression.toString(), sensorNickname.getValue(), contextNickname, ""+false};
            msg.setRegisterData(arg);

            JCL_connector controlConnector = new ConnectorImpl(false);
            controlConnector.connect(IP,Integer.parseInt(port),mac);

            JCL_message_bool msgR = (JCL_message_bool) controlConnector.sendReceiveG(msg, portS);
            update();
            return msgR.getRegisterData()[0];

        } catch (Exception e){
            // TODO: handle exception
            System.err.println("problem in JCL IoTfacade registerContext(Entry<String, String> deviceNickname, Entry<String, String> sensorNickname, JCL_Expression expression, String contextNickname)");
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean addContextAction(String contextNickname, java.util.Map.Entry<String, String> deviceNickname, java.util.Map.Entry<String, String> actuatorNickname, Object[] commands) {
        try{
            String deviceKey = ""+JCL_IoTfacade.PacuHPC.getValue(contextNickname + "_CONTEXT").getCorrectResult();
            String IP = devices.get(deviceKey).get("IP");
            String port = devices.get(deviceKey).get("PORT");
            String mac =  devices.get(deviceKey).get("MAC");
            String portS =  devices.get(deviceKey).get("PORT_SUPER_PEER");

            String hostIP = devices.get(deviceNickname.getKey()).get("IP");
            String hostport = devices.get(deviceNickname.getKey()).get("PORT");

            JCL_message_generic msg = new MessageGenericImpl();
            msg.setType(56);
            String[] stringCommand = new String[commands.length];
            for (int i=0; i<commands.length; i++)
                stringCommand[i] = commands[i]+"";
            Object[] arg = {contextNickname, hostIP, hostport, portS+"", deviceNickname, actuatorNickname, stringCommand};
            msg.setRegisterData(arg);

            JCL_connector controlConnector = new ConnectorImpl(false);
            controlConnector.connect(IP,Integer.parseInt(port),mac);

            JCL_message_bool msgR = (JCL_message_bool) controlConnector.sendReceiveG(msg, portS);
            update();
            return msgR.getRegisterData()[0];
        }catch(Exception e){
            System.err.println("problem in JCL IoTfacade addContextAction(String contextNickname, Entry<String, String> deviceNickname,Entry<String, String> actuatorNickname, Object[] commands)");
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public Future<JCL_result> addContextAction(String contextNickname, boolean useSensorValue, String classNickname, String methodName, Object... args) {
        try {

            Properties properties = new Properties();
            try {
                properties.load(new FileInputStream(Constants.Environment.JCLConfig()));
            } catch (IOException e) {
                e.printStackTrace();
            }
            String serverAdd = properties.getProperty("serverMainAdd"),
                    serverPort = properties.getProperty("serverMainPort");

            Map<String, String> hostPort;
            String ticketHost;
            String ticketPort;
            String mac;

            Object[] argsLam = {serverAdd, serverPort,null,null,classNickname};
            Future<JCL_result> ticket = LambariHPC.execute("JCL_FacadeImplLamb", "registerByServer", argsLam);

            hostPort = (Map<String, String>) ticket.get().getCorrectResult();


            ticketHost = hostPort.get("IP");
            ticketPort = hostPort.get("PORT");
            mac = hostPort.get("MAC");
            String portS =  hostPort.get("PORT_SUPER_PEER");

            List<String> js = new ArrayList<String>();
            js.add(ticketHost+ticketPort+mac);

            JCL_message_generic msg1 = new MessageGenericImpl();
            msg1.setType(57);
            JCL_connector controlConnector1 = new ConnectorImpl(false);
            controlConnector1.connect(ticketHost,Integer.parseInt(ticketPort),mac);
            JCL_message_generic msgR1 = (JCL_message_generic) controlConnector1.sendReceiveG(msg1, portS);

            String deviceKey = ""+JCL_IoTfacade.PacuHPC.getValue(contextNickname + "_CONTEXT").getCorrectResult();
            String IP = devices.get(deviceKey).get("IP");
            String port = devices.get(deviceKey).get("PORT");
            String mac2 =  devices.get(deviceKey).get("MAC");
            String portS2 =  devices.get(deviceKey).get("PORT_SUPER_PEER");

            JCL_message_generic msg = new MessageGenericImpl();
            msg.setType(55);
            Object[] arg = {contextNickname, ticketHost, ticketPort, mac, portS+"", msgR1.getRegisterData()+"", ""+useSensorValue, classNickname, methodName, args};
            msg.setRegisterData(arg);

            JCL_connector controlConnector = new ConnectorImpl(false);
            controlConnector.connect(IP,Integer.parseInt(port),mac2);

            JCL_message_bool msgR = (JCL_message_bool) controlConnector.sendReceiveG(msg, portS2);
            update();
            Long ticketPacu = (Long) msgR1.getRegisterData();
            Long ticketLamb = implementations.sm_kernel.JCL_FacadeImpl.createTicket();

            implementations.sm_kernel.JCL_FacadeImpl.updateTicket(ticketLamb, new Object[]{ticketPacu, ticketHost, ticketPort, mac, portS});
            if (msgR.getRegisterData()[0])
                return new JCLPFuture<JCL_result>(ticketLamb);
            else
                return null;

        } catch (Exception e){
            // TODO: handle exception
            System.err.println("problem in JCL IoTfacade addContextAction(String contextNickname, String classNickname, String methodName, Object... args)");
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public boolean removeContextResult(String contextNickname, Future<JCL_result> ticket) {
        try{
            if (JCL_IoTfacade.PacuHPC.getValue(contextNickname + "_CONTEXT") == null)
                return false;

            Object[] args = { ((JCLPFuture<JCL_result>)ticket).getTicket() };
            Future<JCL_result> res = LambariHPC.execute("JCL_FacadeImplLamb", "getTicketData", args);
            Object[] answer = (Object[]) res.get().getCorrectResult();
            Long ticketPacu = (Long) answer[0];

            Object obj[] = { contextNickname, ticketPacu };
            JCL_message_generic msg = new MessageGenericImpl();
            msg.setType(59);
            msg.setRegisterData(obj);

            String ticketIP = answer[1]+"", ticketPort = answer[2]+"",
                    ticketMac = answer[3]+"", ticketPortSuperPeer = answer[4]+"";
            JCL_connector controlConnector = new ConnectorImpl(false);
            controlConnector.connect(ticketIP,Integer.parseInt(ticketPort),ticketMac);

            JCL_message_bool msgR = (JCL_message_bool) controlConnector.sendReceiveG(msg, ticketPortSuperPeer);
            return msgR.getRegisterData()[0];
        }catch(Exception e){
            System.err.println("problem in JCL IoTfacade removeContextResult(String contextNickname, Long ticket)");
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean acting(java.util.Map.Entry<String, String> deviceNickname, java.util.Map.Entry<String, String> actuatorNickname, Object[] commands) {
        try {

            String IP = devices.get(deviceNickname.getKey()).get("IP");
            String port = devices.get(deviceNickname.getKey()).get("PORT");
            String mac =  devices.get(deviceNickname.getKey()).get("MAC");
            String portS =  devices.get(deviceNickname.getKey()).get("PORT_SUPER_PEER");

            JCL_message_generic msg = new MessageGenericImpl();
            msg.setType(51);
            String[] stringCommand = new String[commands.length];
            for (int i=0; i<commands.length; i++)
                stringCommand[i] = commands[i]+"";
            Object[] arg = {actuatorNickname.getValue(), stringCommand};
            msg.setRegisterData(arg);

            JCL_connector controlConnector = new ConnectorImpl(false);
            controlConnector.connect(IP,Integer.parseInt(port),mac);

            JCL_message_bool msgR = (JCL_message_bool) controlConnector.sendReceiveG(msg, portS);

            return msgR.getRegisterData()[0];
        } catch (Exception e){
            // TODO: handle exception
            System.err.println("problem in JCL IoTfacade acting(Entry<String, String> deviceNickname, Entry<String, String> actuatorNickname, float value)");
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public <T extends java.util.Map.Entry<String, String>> List<T> getDeviceByName(String deviceNickname){
        List<T> deviceList = new ArrayList<>();
        for (T device : this.<T>getIoTDevices() ){
            if (device.getValue().contains(deviceNickname))
                deviceList.add(device);
        }
        return deviceList;
    }

    @Override
    public <T extends java.util.Map.Entry<String, String>> List<T> getSensorByName(java.util.Map.Entry<String, String> deviceNickname, String sensorNickname){
        List<T> sensorList = new ArrayList<T>();
        for (interfaces.kernel.datatype.Sensor sensor: this.<interfaces.kernel.datatype.Sensor>getSensors(deviceNickname) ){
            if (sensor.getKey().contains(sensorNickname))
                sensorList.add((T)sensor);
        }
        return sensorList;
    }

    @Override
    public boolean registerMQTTContext(java.util.Map.Entry<String, String> deviceNickname, java.util.Map.Entry<String, String> sensorNickname, JCL_Expression expression, String topicName) {
        try {
            JCL_IoTFacadeImpl.PacuHPC.instantiateGlobalVar(topicName + "_MQTTCONTEXT", deviceNickname.getKey().toString());

            String IP = devices.get(deviceNickname.getKey()).get("IP");
            String port = devices.get(deviceNickname.getKey()).get("PORT");
            String mac =  devices.get(deviceNickname.getKey()).get("MAC");
            String portS =  devices.get(deviceNickname.getKey()).get("PORT_SUPER_PEER");

            JCL_message_generic msg = new MessageGenericImpl();
            msg.setType(61);
            Object[] arg = {expression.toString(), sensorNickname.getValue(), topicName};
            msg.setRegisterData(arg);

            JCL_connector controlConnector = new ConnectorImpl(false);
            controlConnector.connect(IP,Integer.parseInt(port),mac);

            JCL_message_bool msgR = (JCL_message_bool) controlConnector.sendReceiveG(msg, portS);
            update();
            return msgR.getRegisterData()[0];

        } catch (Exception e){
            System.err.println("problem in JCL IoTfacade createNewTopic(Entry<String, String> deviceNickname, Entry<String, String> sensorNickname, JCL_Expression expression, String topicName)");
            e.printStackTrace();
        }
        return false;

    }

    @Override
    public boolean unregisterContext(String contextNickname) {

        try{
            if (JCL_IoTfacade.PacuHPC.getValue(contextNickname + "_CONTEXT") == null)
                return false;

            String deviceKey = ""+JCL_IoTfacade.PacuHPC.getValue(contextNickname + "_CONTEXT").getCorrectResult();
            String IP = devices.get(deviceKey).get("IP");
            String port = devices.get(deviceKey).get("PORT");
            String mac =  devices.get(deviceKey).get("MAC");
            String portS =  devices.get(deviceKey).get("PORT_SUPER_PEER");


            JCL_message_generic msg = new MessageGenericImpl();
            msg.setType(62);
            Object[] arg = {contextNickname};
            msg.setRegisterData(arg);

            JCL_connector controlConnector = new ConnectorImpl(false);
            controlConnector.connect(IP,Integer.parseInt(port),mac);

            JCL_message_bool msgR = (JCL_message_bool) controlConnector.sendReceiveG(msg, portS);
            update();
            return msgR.getRegisterData()[0];
        }catch(Exception e){
            System.err.println("problem in JCL IoTfacade unregisterContext(String contextNickname)");
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean unregisterMQTTContext(String contextNickname) {

        try{
            if (JCL_IoTfacade.PacuHPC.getValue(contextNickname + "_MQTTCONTEXT") == null)
                return false;

            String deviceKey = ""+JCL_IoTfacade.PacuHPC.getValue(contextNickname + "_MQTTCONTEXT").getCorrectResult();
            String IP = devices.get(deviceKey).get("IP");
            String port = devices.get(deviceKey).get("PORT");
            String mac =  devices.get(deviceKey).get("MAC");
            String portS =  devices.get(deviceKey).get("PORT_SUPER_PEER");


            JCL_message_generic msg = new MessageGenericImpl();
            msg.setType(63);
            Object[] arg = {contextNickname};
            msg.setRegisterData(arg);

            JCL_connector controlConnector = new ConnectorImpl(false);
            controlConnector.connect(IP,Integer.parseInt(port),mac);

            JCL_message_bool msgR = (JCL_message_bool) controlConnector.sendReceiveG(msg, portS);
            update();
            return msgR.getRegisterData()[0];
        }catch(Exception e){
            System.err.println("problem in JCL IoTfacade unregisterContext(String contextNickname)");
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean removeContextAction(String contextNickname, java.util.Map.Entry<String, String> deviceNickname, java.util.Map.Entry<String, String> actuatorNickname, Object[] commands) {
        try{
            if (JCL_IoTfacade.PacuHPC.getValue(contextNickname + "_CONTEXT") == null)
                return false;

            String deviceKey = ""+JCL_IoTfacade.PacuHPC.getValue(contextNickname + "_CONTEXT").getCorrectResult();
            String IP = devices.get(deviceKey).get("IP");
            String port = devices.get(deviceKey).get("PORT");
            String mac =  devices.get(deviceKey).get("MAC");
            String portS =  devices.get(deviceKey).get("PORT_SUPER_PEER");

            String hostIP = devices.get(deviceNickname.getKey()).get("IP");
            String hostport = devices.get(deviceNickname.getKey()).get("PORT");

            JCL_message_generic msg = new MessageGenericImpl();
            msg.setType(64);

            String[] stringCommand = new String[commands.length];
            for (int i=0; i<commands.length; i++)
                stringCommand[i] = commands[i]+"";

            Object[] arg = {contextNickname, hostIP, hostport, portS+"", deviceNickname, actuatorNickname, stringCommand};
            msg.setRegisterData(arg);

            JCL_connector controlConnector = new ConnectorImpl(false);
            controlConnector.connect(IP,Integer.parseInt(port),mac);

            JCL_message_bool msgR = (JCL_message_bool) controlConnector.sendReceiveG(msg, portS);
            update();
            return msgR.getRegisterData()[0];
        }catch(Exception e){
            System.err.println("problem in JCL IoTfacade removeContextAction(String contextNickname, Entry<String, String> deviceNickname, Entry<String, String> actuatorNickname) ");
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean removeContextAction(String contextNickname, boolean useSensorValue, String classNickname, String methodName, Object... args) {
        try {
            if (JCL_IoTfacade.PacuHPC.getValue(contextNickname + "_CONTEXT") == null)
                return false;

            String deviceKey = ""+JCL_IoTfacade.PacuHPC.getValue(contextNickname + "_CONTEXT").getCorrectResult();
            String IP = devices.get(deviceKey).get("IP");
            String port = devices.get(deviceKey).get("PORT");
            String mac2 =  devices.get(deviceKey).get("MAC");
            String portS2 =  devices.get(deviceKey).get("PORT_SUPER_PEER");

            JCL_message_generic msg = new MessageGenericImpl();
            msg.setType(65);
            Object[] arg = {contextNickname, ""+useSensorValue, classNickname, methodName, args};
            msg.setRegisterData(arg);

            JCL_connector controlConnector = new ConnectorImpl(false);
            controlConnector.connect(IP,Integer.parseInt(port),mac2);

            JCL_message_bool msgR = (JCL_message_bool) controlConnector.sendReceiveG(msg, portS2);
            update();

            return msgR.getRegisterData()[0];

        } catch (Exception e){
            System.err.println("problem in JCL IoTfacade removeContextAction(String contextNickname, String classNickname, String methodName, Object... args)");
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean setConfig(JCL_Configuration configuration) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public JCL_Configuration getConfig(java.util.Map.Entry<String, String> deviceNickname) {
        // TODO Auto-generated method stub
        return null;
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
                properties.load(new FileInputStream(Constants.Environment.JCLConfig()));
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

            update();

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
}