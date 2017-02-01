package implementations.dm_kernel.IoTuser;

import java.io.File;
import java.lang.management.ManagementFactory;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import implementations.dm_kernel.ConnectorImpl;
import implementations.dm_kernel.MessageMetadataImpl;
import implementations.dm_kernel.MessageSensorImpl;
import interfaces.kernel.JCL_IoT_Sensing_Model;
import interfaces.kernel.JCL_message_bool;
import mraa.Aio;
import mraa.Dir;
import mraa.Gpio;
import mraa.Pwm;

public class Device implements Runnable{
	
	private static final char INPUT_CHAR = 'I';
	private static final char OUTPUT_CHAR = 'O';
	
	private static String deviceAlias;
	private static String boardIP;
	private static String port;
	private static String serverIP;
	private static String serverPort;
	private static String mac;
	private static String core;
	private static String deviceType;
	private static List<Sensor> enabledSensors = new ArrayList<>();
	private static JCL_IoT_Sensing_Model sensingModel;
	private static boolean standBy;
	
	@Override
	public void run() {		
		Device.makeSensing();
	}

	public static void makeSensing(){
		float value;
		while (true){
			if ( !isStandBy() ){
				List<Sensor> clone = new ArrayList<>();
				clone.addAll(enabledSensors);				
				ListIterator<Sensor> it = clone.listIterator();
				while (it.hasNext()){
					Sensor s = it.next();
					if  ( System.currentTimeMillis() - s.getLastExecuted() >= s.getDelay() * 1000 ){
						/*if ( s.getDir() == OUTPUT_CHAR )
							continue;		*/				
						value = sensing(s);
						s.setLastValue(value);
						s.setLastExecuted(System.currentTimeMillis());
						sendSensingMessage(s);
					}
				}
			}
		}
	}
	
	public static float sensing(Sensor s){
		float value;
		if ( sensingModel.isPortDigital(s.getPin()) ){
			Gpio gpio = new Gpio(sensingModel.getGPIO(s.getPin()), true);
			value = gpio.read() ;							
			gpio.delete();
		}else{
			Aio aio = new Aio( sensingModel.getGPIO(s.getPin()) );
			value =  aio.read();
			aio.delete();
		}
		return value;
	}
	
	public static MessageSensorImpl sensorNow(Object arg){
		System.out.println("** SensorNow **");
		Object[] args = (Object[]) arg;		
		MessageSensorImpl msg = new MessageSensorImpl();		
		msg.setDevice(getMac()+ getPort());
		msg.setSensor(Integer.parseInt(args[0].toString()));
		msg.setType(27);	//mensagem de sensor		
		
		Integer port = Integer.parseInt(args[0].toString());
		Sensor s = new Sensor();
		s.setPin(port);
		msg.setValue(sensing(s));
		return msg;
	}	
	
	public static boolean setMetadata(Map<String, String> metadados){
		System.out.println("** SetMetadata **");
		
		ArrayList<Sensor> newSensors = new ArrayList<>();
		String[] enableSensors = metadados.get("ENABLE_SENSOR").split(";");
		for (int i=0; i<enableSensors.length; i++){
			Sensor s = new Sensor();
			s.setPin(Integer.parseInt(enableSensors[i]));
			if (sensingModel != null && !sensingModel.validPin(s.getPin()) )
				return false;		// Para impedir tentativa de habilitar pinos não existentes
			
			// Valores default caso as demais configurações não sejam enviadas
			s.setAlias("sensor_" + s.getPin());
			s.setSize(1);
			s.setDelay(10);
			s.setDir(INPUT_CHAR);
			s.setLastExecuted(0);
			s.setType(0);
			
			if ( metadados.get("SENSOR_ALIAS_" + enableSensors[i]) != null)
				s.setAlias(metadados.get("SENSOR_ALIAS_" + enableSensors[i]));			

			if ( metadados.get("SENSOR_SIZE_" + enableSensors[i]) != null)
				s.setSize(Integer.parseInt(metadados.get("SENSOR_SIZE_" + enableSensors[i])));
			
			if ( metadados.get("SENSOR_SAMPLING_" + enableSensors[i]) != null)
				s.setDelay(Integer.parseInt(metadados.get("SENSOR_SAMPLING_" + enableSensors[i])));

			if ( metadados.get("SENSOR_DIR_" + enableSensors[i]) != null){
				s.setDir(metadados.get("SENSOR_DIR_" + enableSensors[i]).toUpperCase().charAt(0));
				if ( s.getDir() != INPUT_CHAR && s.getDir() != OUTPUT_CHAR )
					s.setDir(INPUT_CHAR);
			}
			
			if ( metadados.get("SENSOR_TYPE_" + enableSensors[i]) != null)
				s.setType(Integer.parseInt(metadados.get("SENSOR_TYPE_" + enableSensors[i])));			
			
			newSensors.add(s);
		}
		
		System.out.println("Novos sensores");
		for (int i=0; i<newSensors.size(); i++){
			System.out.println("port: " + newSensors.get(i).getPin());
			System.out.println("alias: " + newSensors.get(i).getAlias());
			System.out.println("delay: " + newSensors.get(i).getDelay());
			System.out.println("size: " + newSensors.get(i).getSize());
		}
		
		if (metadados.get("DEVICE_ID") != null)
			setDeviceAlias(metadados.get("DEVICE_ID"));
		
		ListIterator<Sensor> it = enabledSensors.listIterator();
		while (it.hasNext()){
			Sensor s1 = it.next();
			it.remove();			
		}
		for (Sensor s: newSensors){
			it.add(s);
		}

		sendMetadata();
		return true;
	}
	
	public static boolean setSensor(String[] args){
		System.out.println("** Set Sensor **");		
		Sensor s = new Sensor();
		s.setAlias(args[0]);
		s.setPin(Integer.parseInt(args[1].toString()));
		s.setSize(Integer.parseInt(args[2].toString()));
		s.setDelay(Integer.parseInt(args[3].toString()));
		if ( args[5] == null )
			s.setType(0);
		else
			s.setType(Integer.parseInt(args[5].toString()));
		s.setLastExecuted(0);
		if (args.length < 5)
			s.setDir(INPUT_CHAR);
		else{
			s.setDir(args[4].toUpperCase().toString().charAt(0));
			if ( s.getDir() != INPUT_CHAR && s.getDir() != OUTPUT_CHAR )
				s.setDir(INPUT_CHAR);
		}
		if ( !getSensingModel().validPin(s.getPin()) )
			return false;		// Para impedir tentativa de habilitar pinos nÃ£o existentes
		
		ListIterator<Sensor> it = enabledSensors.listIterator();
		while (it.hasNext()){
			Sensor s1 = it.next();
			if ( s1.getPin() == s.getPin() )
				it.remove();	// Caso já exista um sensor configurado naquele pino, o mesmo é descartado para depois adicionar o novo
		}
		it.add(s);		
		sendMetadata();
		return true;
	}
	
	public static boolean acting(Object arg){
		System.out.println("** Acting **");
		Object[] args = (Object[]) arg;				
		
		Integer port = Integer.parseInt(args[0].toString());
		float value = Float.parseFloat(args[1].toString());
		Integer iValue = (int) value;
		System.out.println("port; " + port);
		System.out.println("value: " + value);		
		if ( getSensingModel().validPin(port) ){
			for ( Sensor s : enabledSensors ){
System.out.println("type: " + s.getType());				
				if ( s.getPin() == port ){
					if ( s.getDir() != OUTPUT_CHAR )
						return false;
					// Servo
					if ( s.getType() == 1 ){
System.out.println("pwm");						
						Pwm servo = new Pwm(port, true);
						servo.period_ms(20);
						servo.enable(true);						
						servo.pulsewidth_ms((int)value);
					//	servo.delete();
					}
					else if ( getSensingModel().isPortDigital(port) ){
						if ( iValue != 0 && iValue != 1 )
							return false;
						Gpio gpio = new Gpio( getSensingModel().getGPIO(port), true );
						gpio.dir(Dir.DIR_OUT);
						gpio.write(iValue);
						gpio.delete();
					}else{
						if ( iValue < 0 || iValue > 255 )
							return false;
						Aio aio = new Aio(getSensingModel().getGPIO(port));
						aio.setBit(iValue);
						aio.delete();
					}
					return true;
				}
			}
		}
		return false;
	}
	
	public static boolean removeSensor(Object arg){
		System.out.println("** RemoveSensor **");
		Object[] args = (Object[]) arg;
		Integer port = Integer.parseInt(args[0].toString());
		if ( getSensingModel().validPin(port) ){
			ListIterator<Sensor> it = enabledSensors.listIterator();
			while (it.hasNext()){
				Sensor s1 = it.next();
				if ( s1.getPin() == port ){
					it.remove();
					sendMetadata();
					return true;
				}
			}
		}
		return false;
	}	
	
	public static void restart(){
		try {
			System.out.println("** Restart **");
		//	sendMessage_unregister(meta);
/*			Sensor.sensors.clear();
			System.exit(0);*/			
		//	Process p = Runtime.getRuntime().exec("sudo shutdown -r now");

		    StringBuilder cmd = new StringBuilder();
		    cmd.append(System.getProperty("java.home") + File.separator + "bin" + File.separator + "java ");
		    for (String jvmArg : ManagementFactory.getRuntimeMXBean().getInputArguments()) {
		        cmd.append(jvmArg + " ");
		    }
		    cmd.append("-cp ").append(ManagementFactory.getRuntimeMXBean().getClassPath()).append(" ");
		    cmd.append(MainIoTHost.class.getName()).append(" ");
		    Runtime.getRuntime().exec(cmd.toString());
		    System.exit(0);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private static void sendMetadata(){		
		Map<String,String> metaMap = new HashMap<String, String>();
		
		metaMap.put("IP", getBoardIP());
		metaMap.put("CORE(S)", getCore());
		metaMap.put("PORT", getPort()); 
		metaMap.put("MAC", getMac()); 
		metaMap.put("DEVICE_TYPE", getDeviceType());
		metaMap.put("CONNECTED_SENSOR", String.valueOf(enabledSensors.size()));
		metaMap.put("DEVICE_ID", getDeviceAlias());
		metaMap.put("STANDBY", String.valueOf(isStandBy()));
		
		HashMap<String,String> sensores = new HashMap<>();
		String stringSensor = new String();		
		for(int i=0;i< enabledSensors.size();i++){
			Sensor s = new Sensor();
			s = enabledSensors.get(i);
			sensores.put("SENSOR_ALIAS_" + s.getPin(), s.getAlias());
			sensores.put("SENSOR_SAMPLING_"+s.getPin(), ""+s.getDelay());
			sensores.put("SENSOR_SIZE_"+s.getPin(),""+s.getSize());
			sensores.put("SENSOR_DIR_" + s.getPin(), "" + s.getDir());
			if(i==enabledSensors.size()-1)	stringSensor += s.getPin();
			else stringSensor += s.getPin()+";";
		}

		sensores.put("ENABLE_SENSOR", stringSensor);
		
		metaMap.putAll(sensores);
		
		MessageMetadataImpl msg = new MessageMetadataImpl();
		msg.setMetadados(metaMap);
		msg.setType(40); //mensagem de atualizaÃ§Ã£o
		ConnectorImpl c = new ConnectorImpl();
		c.connect(getServerIP(), Integer.parseInt(getServerPort()), null);
		JCL_message_bool anwser = (JCL_message_bool) c.sendReceiveG(msg, null);
		System.out.println(anwser.getRegisterData()[0]);
	}
	
	private static void sendSensingMessage(Sensor s){
		MessageSensorImpl msg = new MessageSensorImpl();
		msg.setDevice(getMac()+ getPort());
		msg.setSensor(s.getPin());
		msg.setType(27);	//mensagem de sensor
		msg.setValue(s.getLastValue());
		
		ConnectorImpl c = new ConnectorImpl();
		c.connect(getServerIP(), Integer.parseInt(getServerPort()),null);
		@SuppressWarnings("unused")
		Object recive = c.sendReceiveG(msg,null);
		c.disconnect();		
	}
	
	public static boolean standBy(){
		System.out.println("** Entering in standBy Mode **");
		setStandBy(true);
		sendMetadata();
		return isStandBy();
	}
	
	public static boolean turnOn(){
		System.out.println("** Turning the device on **");
		setStandBy(false);
		sendMetadata();
		return !isStandBy();
	}
	
	public static String getDeviceAlias() {
		return deviceAlias;
	}
	
	public static void setDeviceAlias(String deviceAlias) {
		Device.deviceAlias = deviceAlias;
	}
	
	public static String getBoardIP() {
		return boardIP;
	}

	public static void setBoardIP(String boardIP) {
		Device.boardIP = boardIP;
	}

	public static String getServerIP() {
		return serverIP;
	}

	public static void setServerIP(String serverIP) {
		Device.serverIP = serverIP;
	}

	public static String getPort() {
		return port;
	}

	public static void setPort(String port) {
		Device.port = port;
	}

	public static String getMac() {
		return mac;
	}

	public static void setMac(String mac) {
		Device.mac = mac;
	}

	public static List<Sensor> getEnabledSensors() {
		return enabledSensors;
	}

	public static void setEnabledSensors(List<Sensor> enabledSensors) {
		Device.enabledSensors = enabledSensors;
	}

	public static JCL_IoT_Sensing_Model getSensingModel() {
		return sensingModel;
	}

	public static void setSensingModel(JCL_IoT_Sensing_Model sensingModel) {
		Device.sensingModel = sensingModel;
	}

	public static String getDeviceType() {
		return deviceType;
	}

	public static void setDeviceType(String deviceType) {
		Device.deviceType = deviceType;
	}

	public static String getCore() {
		return core;
	}

	public static void setCore(String core) {
		Device.core = core;
	}

	public static String getServerPort() {
		return serverPort;
	}

	public static void setServerPort(String serverPort) {
		Device.serverPort = serverPort;
	}

	public static boolean isStandBy() {
		return standBy;
	}

	public static void setStandBy(boolean standBy) {
		Device.standBy = standBy;
	}
	
}
