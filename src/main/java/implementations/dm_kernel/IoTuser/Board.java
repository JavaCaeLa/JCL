package implementations.dm_kernel.IoTuser;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.lang.management.ManagementFactory;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import commom.JCL_SensorImpl;
import implementations.collections.JCLHashMap;
import implementations.dm_kernel.ConnectorImpl;
import implementations.dm_kernel.MessageMetadataImpl;
import implementations.dm_kernel.MessageSensorImpl;
import implementations.dm_kernel.host.MainHost;
import implementations.dm_kernel.host.SensorAcq;
import implementations.dm_kernel.user.JCL_FacadeImpl;
import implementations.util.CoresAutodetect;
import interfaces.kernel.JCL_IoT_Sensing_Model;
import interfaces.kernel.JCL_Sensor;
import interfaces.kernel.JCL_facade;
import mraa.Aio;
import mraa.Dir;
import mraa.Gpio;
import mraa.Pwm;

public class Board implements Runnable{
	
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
	private static String platform;
	private static ConcurrentMap<Integer, Map<String, JCL_Context>> mapContext = new ConcurrentHashMap<>();
	private static ConcurrentMap<String, Integer> mapNameContext = new ConcurrentHashMap<>();
	private static List<SensorAcq> enabledSensors = new ArrayList<>();
	private static JCL_IoT_Sensing_Model sensingModel;
	private static boolean standBy;
	private static String brokerIP;
	private static String brokerPort;
	static MqttClient mqttClient;
	private static ScheduledThreadPoolExecutor scheduler = (ScheduledThreadPoolExecutor) Executors.newScheduledThreadPool(CoresAutodetect.cores);
	private static boolean allowUser = true;
	
	@Override
	public void run() {		
		Board.checkingContexts();
	}

	public static void connectToBroker(){
        try {
        	String broker = "tcp://" + brokerIP + ":" + brokerPort;
        	MemoryPersistence persistence = new MemoryPersistence();
            mqttClient = new MqttClient(broker, getMac()+getPort(), persistence);
            MqttConnectOptions connOpts = new MqttConnectOptions();
            connOpts.setCleanSession(true);
            mqttClient.connect(connOpts);
        }catch(MqttException e){
            //e.printStackTrace();
        	System.out.println("Could not connect to MQTT Broker");
        }
	}
	
	public static void restore(){
	/*	String separator = "~";
		String contextNickname = "";
		try {
			System.out.println("**Restoring**");
			BufferedReader input =  new BufferedReader(new FileReader("device.ser"));
			String line = null;
			while (( line = input.readLine()) != null){
				String[] split = line.split(separator);
				if (split[0].charAt(0) == '+' ){
					Sensor s = new Sensor();
					s.setPin(Integer.valueOf(split[1]));
					s.setAlias(split[2]);
					s.setDelay(Long.valueOf(split[3]));
					s.setSize(Integer.valueOf(split[4]));
					s.setDir(split[5].charAt(0));
					s.setType(Integer.valueOf(split[6]));
					ListIterator<Sensor> it = enabledSensors.listIterator();
					it.add(s);					
				}else if (split[0].charAt(0) == '='){
					int pin = Integer.valueOf(split[1]);
					contextNickname = split[2];
					JCL_Context ctx = new JCL_Context(new JCL_Expression(split[3]), contextNickname);
					
					Map<String, JCL_Context> contexts = null;
					if ( mapContext.containsKey(pin) ){
						contexts = mapContext.get(pin);
					}else
						contexts = new HashMap<>();
					contexts.put(contextNickname, ctx);
					
					mapContext.put(pin, contexts);
					mapNameContext.put(contextNickname, pin);	
				}else if (split[0].charAt(0) == '^'){

					JCL_Context ctx = mapContext.get(mapNameContext.get(contextNickname)).get(contextNickname);
					
					Entry<String, String> deviceNickname = new implementations.util.Entry(split[1], split[2]);										
					Entry<String, String> actuatorNickname = new implementations.util.Entry(split[3], split[4]);
					
					int paramSize = Integer.valueOf(split[5]);
					Object param[] = new Object[paramSize];
					
					for (int i=6; i<6+paramSize; i++){
						param[i-6] = split[i];
					}
					
					JCL_Action action = new JCL_Action(deviceNickname, actuatorNickname, param);
					ctx.addAction(action);					
				}else if (split[0].charAt(0) == '>'){
					JCL_Context ctx = mapContext.get(mapNameContext.get(contextNickname)).get(contextNickname);	
					
					String hostTicketIP = split[2], 
							hostTicketPort = split[4], 
							hostTicketMac = split[3];
					String superPeerPort;
					if (split[5].equals("null"))
						superPeerPort = null;
					else
						superPeerPort = split[5];
					Long ticket = Long.valueOf(split[1]);
					boolean b = Boolean.valueOf(split[8]);
					String className = split[6],
							methodName = split[7];					
					int paramSize = Integer.valueOf(split[9]);
					Object param[] = new Object[paramSize];
					
					for (int i=10; i<10+paramSize; i++){
						param[i-10] = split[i];
					}
					JCL_Action action = new JCL_Action(b, ticket, hostTicketIP, hostTicketPort, hostTicketMac, superPeerPort, className, methodName, param);
					ctx.addAction(action);
				}
			}
			sendMetadata();
			input.close();
		}catch (IOException e){
			e.printStackTrace();
		}*/
	}
	
	public static void storeChanges(){
/*		try{
		    PrintWriter writer = new PrintWriter("device.ser", "UTF-8");
		    char separator = '~';
			for (SensorAcq s:enabledSensors){
				writer.write("+");
				writer.write(separator);
				writer.write(""+s.getPin());
				writer.write(separator);
				writer.write(s.getAlias());
				writer.write(separator);
				writer.write(String.valueOf(s.getDelay()));
				writer.write(separator);
				writer.write(""+s.getSize());
				writer.write(separator);
				writer.write(s.getDir());
				writer.write(separator);
				writer.write(""+s.getType());
				writer.write("\n");
			}
			
			Iterator<Entry<Integer, Map<String, JCL_Context>>> it = mapContext.entrySet().iterator();
			while (it.hasNext()) {
				Entry<Integer, Map<String, JCL_Context>> contextIt = it.next();
				for (Entry<String, JCL_Context> ent:contextIt.getValue().entrySet()){
					JCL_Context ctx = ent.getValue();
					writer.write("=");
					writer.write(separator);
					writer.write(""+contextIt.getKey());
					writer.write(separator);
					writer.write(ctx.getContextNickname());
					writer.write(separator);
					writer.write(ctx.getExpression().getExpression());
					writer.write("\n");
					for (JCL_Action act: ctx.getActionList()){						
						if (act.isActing()){
							writer.write("^");
							writer.write(separator);
							writer.write(act.getDeviceNickname().getKey());
							writer.write(separator);
							writer.write(act.getDeviceNickname().getValue());
							writer.write(separator);
							writer.write(act.getActuatorNickname().getKey());
							writer.write(separator);
							writer.write(act.getActuatorNickname().getValue());
							writer.write(separator);
						}else{
							writer.write(">");
							writer.write(separator);						
							writer.write(""+act.getTicket());
							writer.write(separator);
							writer.write(act.getHostTicketIP());
							writer.write(separator);
							writer.write(act.getHostTicketMac());
							writer.write(separator);
							writer.write(act.getHostTicketPort());
							writer.write(separator);
							writer.write(act.getHostTicketPortSuperPeer());
							writer.write(separator);
							writer.write(act.getClassName());
							writer.write(separator);
							writer.write(act.getMethodName());
							writer.write(separator);
							writer.write(String.valueOf(act.isUseSensorValue()));
							writer.write(separator);
						}
						writer.write(""+act.getParam().length);
						writer.write(separator);
						for (int i=0; i<act.getParam().length;i++){
							writer.write(act.getParam()[i].toString());
							writer.write(separator);							
						}
						writer.write("\n");
					}
				}
			}
		    writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}*/
	}
	
	public static void checkingContexts(){
		try{
			while (true){
				if ( !isStandBy() ){
					List<SensorAcq> clone = new ArrayList<>();
					clone.addAll(enabledSensors);
					ListIterator<SensorAcq> it = clone.listIterator();
					while (it.hasNext()){
						SensorAcq s = it.next();
						if (sensingModel.specialPin(s.getPin()))
							continue;
						/* checking contexts */
						Object sensingValue = s.sensing();
						Object lastValue = s.getLastValue();
						if (lastValue == null)
							continue;
						if (sensingValue == null)
							continue;
						float f[] = new float[]{Float.valueOf(sensingValue.toString())};
						if (mapContext.containsKey(s.getPin())){
							for (JCL_Context ctx:mapContext.get(s.getPin()).values()){								
								if (lastValue != null)
									ctx.check(f, new float[]{Float.valueOf(lastValue.toString())});
							}
						}
					}
				}
				Thread.sleep(500);
			}
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}	
	

	
	public static MessageSensorImpl sensorNow(Object arg){
		System.out.println("** SensorNow **");
		if (standBy)
			return null;
		Object[] args = (Object[]) arg;		
		Integer port = Integer.parseInt(args[0].toString());
		
		ListIterator<SensorAcq> it = enabledSensors.listIterator();
		while (it.hasNext()){
			SensorAcq s1 = it.next();
			if ( s1.getPin() == port ){
				MessageSensorImpl msg = new MessageSensorImpl();		
				msg.setDevice(getMac()+ getPort());
				msg.setSensor(Integer.parseInt(args[0].toString()));
				msg.setType(27);	//mensagem de sensor		
				
				msg.setDataType(s1.getDataType());
				msg.setValue(s1.sensing());
				return msg;
			}
		}
		return null;
	}	
	
	public static boolean setMetadata(Map<String, String> metadados){
		if (standBy)
			return false;
		System.out.println("** SetMetadata **");

		if (metadados == null)
			return false;
		
		ArrayList<SensorAcq> newSensors = new ArrayList<>();
		if (metadados.get("ENABLE_SENSOR") != null){
			String[] enableSensors = metadados.get("ENABLE_SENSOR").split(";");
			for (int i=0; i<enableSensors.length; i++){
				SensorAcq s = new SensorAcq();
				s.setPin(Integer.parseInt(enableSensors[i]));
				if (sensingModel != null && !sensingModel.validPin(s.getPin()) )
					return false;		// Para impedir tentativa de habilitar pinos não existentes

				// Valores default caso as demais configurações não sejam enviadas
				s.setAlias("sensor_" + s.getPin());
				s.setSize(1000);
				s.setDelay(10000);
				s.setDir(INPUT_CHAR);
				s.setType(0);

				if ( metadados.get("SENSOR_ALIAS_" + enableSensors[i]) != null)
					s.setAlias(metadados.get("SENSOR_ALIAS_" + enableSensors[i]));
				else
					return false;

				if ( metadados.get("SENSOR_SIZE_" + enableSensors[i]) != null)
					s.setSize(Integer.parseInt(metadados.get("SENSOR_SIZE_" + enableSensors[i])));
				else
					return false;
				
				if ( metadados.get("SENSOR_SAMPLING_" + enableSensors[i]) != null)
					s.setDelay(Integer.parseInt(metadados.get("SENSOR_SAMPLING_" + enableSensors[i])));
				else
					return false;
				
				if ( metadados.get("SENSOR_DIR_" + enableSensors[i]) != null){
					s.setDir(metadados.get("SENSOR_DIR_" + enableSensors[i]).toUpperCase().charAt(0));
					
					if ( s.getDir() != INPUT_CHAR && s.getDir() != OUTPUT_CHAR )
						s.setDir(INPUT_CHAR);
				}
				else
					return false;
				
				if ( metadados.get("SENSOR_TYPE_" + enableSensors[i]) != null)
					s.setType(Integer.parseInt(metadados.get("SENSOR_TYPE_" + enableSensors[i])));			
				else
					return false;
				if ( s.getDir() == OUTPUT_CHAR && getSensingModel().isPortDigital(s.getPin()) ){
					Gpio g = new Gpio(s.getPin(), true);
					g.dir(Dir.DIR_OUT_HIGH);
					g.write(0);
				}
				newSensors.add(s);
			}
		}
		
		if (metadados.get("DEVICE_ID") != null)
			setDeviceAlias(metadados.get("DEVICE_ID"));
		
		ListIterator<SensorAcq> it = enabledSensors.listIterator();
		while (it.hasNext()){			
			SensorAcq sensor = it.next();
			sensor.removeFuture(sensor.getPin());
			it.remove();
		}
		for (SensorAcq s: newSensors){
			it.add(s);
			putInScheduler(s);
		}

		sendMetadata();
		storeChanges();
		return true;
	}
	
	public static boolean setSensor(String[] args){
		if (standBy)
			return false;
		System.out.println("** Set Sensor **");		
		SensorAcq s = new SensorAcq();
		s.setAlias(args[0]);
		s.setPin(Integer.parseInt(args[1].toString()));
		s.setSize(Integer.parseInt(args[2].toString()));
		s.setDelay(Integer.parseInt(args[3].toString()));
		if ( args[5] == null )
			s.setType(0);
		else
			s.setType(Integer.parseInt(args[5].toString()));
		if (args.length < 5)
			s.setDir(INPUT_CHAR);
		else{
			s.setDir(args[4].toUpperCase().toString().charAt(0));
			if ( s.getDir() != INPUT_CHAR && s.getDir() != OUTPUT_CHAR )
				s.setDir(INPUT_CHAR);
		}
		if ( !getSensingModel().validPin(s.getPin()) )
			return false;		// Para impedir tentativa de habilitar pinos nÃ£o existentes
		
		
		if ( s.getDir() == OUTPUT_CHAR && getSensingModel().isPortDigital(s.getPin()) ){
			Gpio g = new Gpio(s.getPin(), true);
			g.dir(Dir.DIR_OUT);
			g.write(1);
//			g.write();	
		}

		ListIterator<SensorAcq> it = enabledSensors.listIterator();
		while (it.hasNext()){
			SensorAcq s1 = it.next();
			if ( s1.getPin() == s.getPin() ){
				s1.removeFuture(s.getPin());
				it.remove();	// Caso já exista um sensor configurado naquele pino, o mesmo é descartado para depois adicionar o novo
			}
		}
		it.add(s);
		sendMetadata();
		storeChanges();
		
		putInScheduler(s);
		
		return true;
	}
	
	private static void putInScheduler(SensorAcq s){
//		if (s.getDir() == OUTPUT_CHAR)
//			return;
		
		ScheduledFuture<SensorAcq> future = (ScheduledFuture<SensorAcq>)scheduler.scheduleWithFixedDelay(s, 0, s.getDelay(), TimeUnit.MILLISECONDS);
		s.setFuture(future);
	}
	
	
	public static boolean acting(Object arg){
		if (standBy)
			return false;
		System.out.println("** Acting **");
		Object[] args = (Object[]) arg;				

		Integer port = Integer.parseInt(args[0].toString());
		String[] commands = (String[]) args[1];
		for (String c:commands){
			float value = Float.parseFloat(c);
			Integer iValue = (int) value;
			if ( getSensingModel().validPin(port) ){
				for ( SensorAcq s : enabledSensors ){
					if ( s.getPin() == port ){
						if ( s.getDir() != OUTPUT_CHAR )
							return false;
						// Servo
						if ( s.getType() == 1 ){
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
							//gpio.delete();
						}else{
							if ( iValue < 0 || iValue > 255 )
								return false;
							Aio aio = new Aio(getSensingModel().getGPIO(port));
							aio.setBit(iValue);
							// aio.delete();
						}
					}
				}
			}
		}
		return true;
	}
	
	public static boolean removeSensor(Object arg){
		if (standBy)
			return false;
		System.out.println("** RemoveSensor **");
		boolean removed = false;
		Object[] args = (Object[]) arg;
		Integer pin = Integer.parseInt(args[0].toString());
		if ( getSensingModel().validPin(pin) ){
			ListIterator<SensorAcq> it = enabledSensors.listIterator();
			while (it.hasNext()){
				SensorAcq s1 = it.next();
				if ( s1.getPin() == pin ){
					s1.removeFuture(pin);
					it.remove();
					sendMetadata();
					removed = true;
				}
			}
			
			if (mapContext.get(pin)!= null){
				for (String name:mapContext.get(pin).keySet())
					mapNameContext.remove(name);
				mapContext.remove(pin);
			}

		}
		if (removed)
			storeChanges();
		return removed;
	}	
	
	public static void restart(){
		try {
			if (standBy)
				return;
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
		    cmd.append(MainHost.class.getName()).append(" ");
		    Runtime.getRuntime().exec(cmd.toString());
		    System.exit(0);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private static void sendMetadata(){		
		Map<String,String> metaMap = new HashMap<String, String>();		
/*		metaMap.put("IP", getBoardIP());
		metaMap.put("CORE(S)", getCore());
		 
		metaMap.put("DEVICE_PLATFORM", mraa.mraa.getPlatformName());
		metaMap.put("DEVICE_TYPE", getDeviceType());*/
		
		metaMap.put("MAC", getMac()); 
		metaMap.put("PORT", getPort());
		metaMap.put("DEVICE_TYPE", getDeviceType());
		metaMap.put("NUMBER_SENSORS", String.valueOf(enabledSensors.size()));
		metaMap.put("DEVICE_ID", getDeviceAlias());
		metaMap.put("STANDBY", String.valueOf(isStandBy()));
		
		HashMap<String,String> sensores = new HashMap<>();
		String stringSensor = new String();		
		for(int i=0;i< enabledSensors.size();i++){
			SensorAcq s = new SensorAcq();
			s = enabledSensors.get(i);
			sensores.put("SENSOR_ALIAS_" + s.getPin(), s.getAlias());
			sensores.put("SENSOR_SAMPLING_"+s.getPin(), ""+s.getDelay());
			sensores.put("SENSOR_SIZE_"+s.getPin(),""+s.getSize());
			sensores.put("SENSOR_DIR_" + s.getPin(), "" + s.getDir());
			if(i==enabledSensors.size()-1)	stringSensor += s.getPin();
			else stringSensor += s.getPin()+";";
		}
		
		if (enabledSensors.size() > 0)
			sensores.put("ENABLE_SENSOR", stringSensor);
		else
			sensores.put("ENABLE_SENSOR", ";");
			
		
		metaMap.putAll(sensores);
		
		MessageMetadataImpl msg = new MessageMetadataImpl();
		msg.setMetadados(metaMap);
		msg.setType(40); //mensagem de atualização
		ConnectorImpl c = new ConnectorImpl(false);
		c.connect(getServerIP(), Integer.parseInt(getServerPort()), null);
		c.sendReceiveG(msg, null);
	}
	
	public static int createMapAndGV(SensorAcq s){
		JCL_facade jcl = JCL_FacadeImpl.getInstancePacu();
		s.setMin(0);
		s.setMax(0);
		int pos = s.getMaxAndIncrement();
		jcl.instantiateGlobalVar(Board.getMac() + Board.getPort() + s.getPin()+"_NUMELEMENTS", pos);		
		s.setValues(new JCLHashMap<Integer,JCL_Sensor>(Board.getMac() + Board.getPort() + s.getPin()+"_value"));
		return pos;
	}
	
	public static void saveAsGV(SensorAcq s, String dataType){
		int pos = s.getMaxAndIncrement();
		JCL_Sensor sensor = new JCL_SensorImpl();	
		sensor.setTime(System.currentTimeMillis());
		sensor.setDataType(dataType);
		sensor.setObject(s.getLastValue());
		if (allowUser){
			JCL_facade jcl = JCL_FacadeImpl.getInstancePacu();
			if (!jcl.containsGlobalVar(Board.getMac() + Board.getPort() + s.getPin()+"_NUMELEMENTS"))
				pos = createMapAndGV(s);

			s.getValues().put((pos),sensor);
			if (pos - s.getMin() >= s.getSize())
				s.getValues().remove(s.getMinAndIncrement());
			
			jcl.setValueUnlocking(Board.getMac() + Board.getPort() + s.getPin()+"_NUMELEMENTS", pos);
		}else
		{
			MessageSensorImpl message = new MessageSensorImpl();
			message.setType(27);
			String device = Board.getMac()+ Board.getPort();
			message.setDevice(device);
			message.setValue(sensor);
			message.setSensor(s.getPin());
			message.setDataType(dataType);
			long time = System.nanoTime();
			message.setTime(time);
			ConnectorImpl con = new ConnectorImpl();
			con.connect(getServerIP(), Integer.valueOf(getServerPort()), null);
			con.sendReceiveG(message, null);
		}
	}
	
	public static Long getTime()  {
		try{
			Runtime rt = Runtime.getRuntime();
			String[] cmd = { "/bin/sh", "-c", "date +%s%N | cut -b1-13" };
			Process proc = rt.exec(cmd);
			BufferedReader is = new BufferedReader(new InputStreamReader(proc.getInputStream()));
			String line;
			if ((line = is.readLine()) != null) {
				return Long.valueOf(line);
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return null;
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
	
	public static boolean createNewTopic(Object obj){
		if (standBy)
			return false;
		System.out.println("** creating MQTT Context **");
		Object[] args = (Object[]) obj;
		JCL_Expression exp;
		String expression = String.valueOf(args[0]), sensorPin = String.valueOf(args[1]);		
		exp = new JCL_Expression(expression);
		String topicName = String.valueOf(args[2]);
		
		SensorAcq sensor = null;
		
		for (SensorAcq s: getEnabledSensors()){
			if (s.getPin() == Integer.valueOf(sensorPin)){
				sensor = s;
				break;
			}
		}
		if (sensor == null)
			return false;
		
		JCL_Context topic = new JCL_Context(exp, topicName, true);;
		
		Map<String, JCL_Context> contexts = null;
		if ( mapContext.containsKey(sensor.getPin()) ){
			contexts = mapContext.get(sensor.getPin());
		}else
			contexts = new HashMap<>();
		contexts.put(topicName, topic);
		
		mapContext.put(sensor.getPin(), contexts);
		mapNameContext.put(topicName, sensor.getPin());
		System.out.println(topicName);
		
		storeChanges();
		return true;
	}	
	
	public static boolean setContext(Object obj){
		if (standBy)
			return false;
		System.out.println("** Registering Context **");
		Object[] args = (Object[]) obj;
		JCL_Expression exp;
		String expression = String.valueOf(args[0]), sensorPin = String.valueOf(args[1]);		
		exp = new JCL_Expression(expression);
		String nickname = String.valueOf(args[2]);
		
		SensorAcq sensor = null;
		
		for (SensorAcq s: getEnabledSensors()){
			if (s.getPin() == Integer.valueOf(sensorPin)){
				sensor = s;
				break;
			}
		}
		if (sensor == null)
			return false;
		JCL_Context ctx = new JCL_Context(exp, nickname, false);
		
		Map<String, JCL_Context> contexts = null;
		if ( mapContext.containsKey(sensor.getPin()) ){
			contexts = mapContext.get(sensor.getPin());
		}else
			contexts = new HashMap<>();
		contexts.put(nickname, ctx);
		
		mapContext.put(sensor.getPin(), contexts);
		mapNameContext.put(nickname, sensor.getPin());
		
		storeChanges();
		return true;
	}
	
	public static boolean unregisterContext(Object obj){
		if (standBy)
			return false;
		
		System.out.println("** Unregistering Context **");
		Object[] args = (Object[]) obj;

		String nickname = String.valueOf(args[0]);
		
		if (mapNameContext.containsKey(nickname)){
			mapContext.remove(mapNameContext.get(nickname));
			mapNameContext.remove(nickname);
		}else
			return false;
		
		storeChanges();

		return true;
	}
	
	public static boolean unregisterMQTTContext(Object obj){
		if (standBy)
			return false;
		
		System.out.println("** Unregistering MQTT Context **");
		Object[] args = (Object[]) obj;

		String nickname = String.valueOf(args[0]);

		System.out.println(nickname);
		System.out.println(mapNameContext.containsKey(nickname));
		
		if (mapNameContext.containsKey(nickname)){
			mapContext.remove(mapNameContext.get(nickname));
			mapNameContext.remove(nickname);
		}else
			return false;
		
		storeChanges();

		return true;		
	}

	public static boolean removeActingOnContext(Object obj){
		if (standBy)
			return false;
		System.out.println("** Removing Acting On Context **");
		Object[] args = (Object[]) obj;
		String contextNickname = String.valueOf(args[0]);
		
		JCL_Context ctx = null;
		
		if (!mapNameContext.containsKey(contextNickname))
			return false;
		
		ctx = mapContext.get(mapNameContext.get(contextNickname)).get(contextNickname);
		
		Entry<String, String> deviceNickname= (Entry<String, String>) args[4],
				actuatorNickname= (Entry<String, String>) args[5];
		Object[] commands = (Object[]) args[6];
		boolean exists = false;
		Iterator<JCL_Action > it = ctx.getActionList().iterator();
		while (it.hasNext()){
			JCL_Action act = it.next();
			if (act.isActing() && act.getDeviceNickname().equals(deviceNickname) && act.getActuatorNickname().equals(actuatorNickname) && Arrays.equals(act.getParam(), commands)){
				it.remove();
				exists = true;
			}
		}
	
		if (!exists)
			return false;
		
		storeChanges();
		return true;
	}
	
	public static boolean removeTaskOnContext(Object obj){
		if (standBy)
			return false;
		System.out.println("** Removing Task On Context **");
		Object[] args = (Object[]) obj;
		String contextNickname = String.valueOf(args[0]);
		
		JCL_Context ctx = null;
		
		if (!mapNameContext.containsKey(contextNickname))
			return false;
		
		ctx = mapContext.get(mapNameContext.get(contextNickname)).get(contextNickname);

		boolean useSensorValue = Boolean.valueOf(""+args[1]);
		String classNickname =  (String)args[2];
		String methodName = (String) args[3];
		Object[] commands = (Object[]) args[4];
		
		boolean exists = false;
		Iterator<JCL_Action > it = ctx.getActionList().iterator();
		while (it.hasNext()){
			JCL_Action act = it.next();
			System.out.println(act.getClassName().equals(classNickname)  + "  " + act.getMethodName().equals(methodName)  + "  " +Arrays.equals(act.getParam(), commands) + "  " + (useSensorValue == act.isUseSensorValue()));
			if (act.getClassName().equals(classNickname) && act.getMethodName().equals(methodName) && Arrays.equals(act.getParam(), commands) && useSensorValue == act.isUseSensorValue()){
				it.remove();
				exists = true;
			}
		}
	
		if (!exists)
			return false;
		
		storeChanges();
		return true;
	}
	
	public static boolean addTaskOnContext(Object obj){
		if (standBy)
			return false;
		System.out.println("** Adding Task On Context **");
		Object[] args = (Object[]) obj;
		String contextNickname = String.valueOf(args[0]);
		
		JCL_Context ctx = null;
		
		if (!mapNameContext.containsKey(contextNickname))
			return false;
		
		ctx = mapContext.get(mapNameContext.get(contextNickname)).get(contextNickname);	
		
		String hostTicketIP = String.valueOf(args[1]), 
				hostTicketPort = String.valueOf(args[2]), 
				hostTicketMac = String.valueOf(args[3]);
		String superPeerPort = args[4]+"";
		Long ticket = Long.valueOf(args[5]+"");
		boolean b = Boolean.valueOf(""+args[6]);
		String className = String.valueOf(args[7]),
				methodName= String.valueOf(args[8]);
		Object param[] = (Object[]) args[9];
		
		JCL_Action action = new JCL_Action(b, ticket, hostTicketIP, hostTicketPort, hostTicketMac, superPeerPort, className, methodName, param);
		ctx.addAction(action);
		
		storeChanges();
		return true;
	}
	
	public static boolean addActingOnContext(Object obj){
		if (standBy)
			return false;
		System.out.println("** Adding Task On Context **");
		Object[] args = (Object[]) obj;
		String contextNickname = String.valueOf(args[0]);
		
		JCL_Context ctx = null;
		
		if (!mapNameContext.containsKey(contextNickname))
			return false;
		
		ctx = mapContext.get(mapNameContext.get(contextNickname)).get(contextNickname);
		
		Entry<String, String> deviceNickname= (Entry<String, String>) args[4],
				actuatorNickname= (Entry<String, String>) args[5];
		Object[] commands = (Object[]) args[6];
		
		JCL_Action action = new JCL_Action(deviceNickname, actuatorNickname, commands);
		ctx.addAction(action);
		
		storeChanges();
		return true;
	}
	
	public static String getDeviceAlias() {
		return deviceAlias;
	}
	
	public static void setDeviceAlias(String deviceAlias) {
		Board.deviceAlias = deviceAlias;
	}
	
	public static String getBoardIP() {
		return boardIP;
	}

	public static void setBoardIP(String boardIP) {
		Board.boardIP = boardIP;
	}

	public static String getServerIP() {
		return serverIP;
	}

	public static void setServerIP(String serverIP) {
		Board.serverIP = serverIP;
	}

	public static String getPort() {
		return port;
	}

	public static void setPort(String port) {
		Board.port = port;
	}

	public static String getMac() {
		return mac;
	}

	public static void setMac(String mac) {
		Board.mac = mac;
	}

	public static List<SensorAcq> getEnabledSensors() {
		return enabledSensors;
	}

	public static void setEnabledSensors(List<SensorAcq> enabledSensors) {
		Board.enabledSensors = enabledSensors;
	}

	public static JCL_IoT_Sensing_Model getSensingModel() {
		return sensingModel;
	}

	public static void setSensingModel(JCL_IoT_Sensing_Model sensingModel) {
		Board.sensingModel = sensingModel;
	}

	public static String getDeviceType() {
		return deviceType;
	}

	public static void setDeviceType(String deviceType) {
		Board.deviceType = deviceType;
	}

	public static String getCore() {
		return core;
	}

	public static void setCore(String core) {
		Board.core = core;
	}

	public static String getServerPort() {
		return serverPort;
	}

	public static void setServerPort(String serverPort) {
		Board.serverPort = serverPort;
	}

	public static boolean isStandBy() {
		return standBy;
	}

	public static void setStandBy(boolean standBy) {
		Board.standBy = standBy;
	}

	public static String getBrokerIP() {
		return brokerIP;
	}

	public static void setBrokerIP(String brokerIP) {
		Board.brokerIP = brokerIP;
	}

	public static String getBrokerPort() {
		return brokerPort;
	}

	public static void setBrokerPort(String brokerPort) {
		Board.brokerPort = brokerPort;
	}

	public static MqttClient getMqttClient() {
		return mqttClient;
	}

	public static boolean isAllowUser() {
		return allowUser;
	}

	public static void setAllowUser(Boolean allowUser) {
		Board.allowUser = allowUser;
	}

	public static String getPlatform() {
		return platform;
	}

	public static void setPlatform(String platform) {
		Board.platform = platform;
	}
	
}
