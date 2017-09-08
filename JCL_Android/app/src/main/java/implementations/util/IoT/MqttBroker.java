package implementations.util.IoT;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import implementations.dm_kernel.user.JCL_FacadeImpl;
import interfaces.kernel.JCL_facade;

public class MqttBroker implements MqttCallback{
	private String ip;
	private String port;
	private String clientID;
	private MqttClient client;
	Map<String, String> mapTopicsThreshold = new HashMap<>();
	List<Subscribe> list;

	public void connect(){
		String broker       = "tcp://" + getIp() + ":" + getPort();
		String clientId     = getClientID();
		try {
			client = new MqttClient(broker, clientId);
			client.connect();
			client.setCallback(this);
		} catch(MqttException e) {
			e.printStackTrace();
		}
	}
	
	public void subscribeToTopics(List<Subscribe> list){
		try{
			for (Subscribe sub:list){
				client.subscribe(sub.getTopic());
				mapTopicsThreshold.put(sub.getTopic(), sub.getThreshold());				
			}
			this.list = list;
		}catch(MqttException me) {
			me.printStackTrace();
		}
	}
	
	@Override
	public void connectionLost(Throwable arg0) {
		System.out.println("The connection with the broker was lost. Trying to establish connection...");
		try{
			Thread.sleep(10000);
		}catch(Exception e){}
		connect();
		if (!client.isConnected())
			System.out.println("Could not establish the connection");
		else
			System.out.println("Connection established");
	}

	@Override
	public void deliveryComplete(IMqttDeliveryToken arg0) {
	}

	@Override
	public void messageArrived(String topic, MqttMessage message){
		try{
			System.out.println("*** Message arrived ***");
			System.out.println("| Topic:" + topic);
			System.out.println("| Message: " + new String(message.getPayload()));
			System.out.println();

			Subscribe sub = null;

			if ( mapTopicsThreshold.containsKey(topic)){
				for (Subscribe s:list){
					if (s.getTopic().equals(topic)){
						sub = s;
						break;
					}
				}
				if ( checkContext(message, sub) ){
					if(!sub.isTriggered()){
						sub.setTriggered(true);
						JCL_facade hpc = JCL_FacadeImpl.getInstance();

						if ( sub.getClassName().endsWith(".jar") ){
							String[] jarFiles = sub.getClassName().split(",");
							File f[] = new File[jarFiles.length];
							for (int i=0; i<jarFiles.length; i++)
								f[i] = new File("../applications/" + jarFiles[i]);
							hpc.register(f, sub.getClassNickname());
						}else{
							/*String pathToClassFile = "../applications/" + sub.getClassName();
							
							ProcessBuilder pb = new ProcessBuilder("javap",pathToClassFile);
							Process p = pb.start();
							String classname = null;
							try(BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()))) {
								String line;
								while(null != (line = br.readLine())) {
									if(line.startsWith("public class")) {
										classname = line.split(" ")[2];
										break;
									}
								}
							}
							
							URLClassLoader loader = new URLClassLoader(new URL[]{new File("../applications").toURI().toURL()});
							Class<?> myClass = loader.loadClass(classname);							
							nickName = sub.getClassName();*/
							URLClassLoader loader = new URLClassLoader(new URL[]{new File("../applications").toURI().toURL()});
							System.out.println(sub.getClassName());
							Class<?> myClass = loader.loadClass(sub.getClassName());
							hpc.register(myClass, sub.getClassNickname());
							loader.close();
						}
						
						Object[] obj = {message.getPayload()};
						hpc.execute(sub.getClassNickname(), obj);
					}
					if (new String(message.getPayload()).equals("done"))
						sub.setTriggered(false);
				}else 
					sub.setTriggered(false);
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	public boolean checkContext(MqttMessage message, Subscribe sub){
		if (sub.getThreshold() == null)
			return true;
		double value = Double.valueOf(new String(message.getPayload()));
		double threshold = Double.valueOf(sub.getThreshold());
		if ( sub.getOperator().equals("=") ){
			if (value == threshold)
				return true;
		}else if ( sub.getOperator().equals(">") ){
			if (value > threshold)
				return true;			
		}else if ( sub.getOperator().equals(">=") ){
			if (value >= threshold)
				return true;			
		}else if ( sub.getOperator().equals("<") ){
			if (value < threshold)
				return true;			
		}else if ( sub.getOperator().equals("<=") ){
			if (value <= threshold)
				return true;			
		}
		return false;
	}
	
	public String getIp() {
		return ip;
	}
	
	public void setIp(String ip) {
		this.ip = ip;
	}
	
	public String getPort() {
		return port;
	}
	
	public void setPort(String port) {
		this.port = port;
	}

	public MqttClient getClient() {
		return client;
	}

	public String getClientID() {
		return clientID;
	}

	public void setClientID(String clientID) {
		this.clientID = clientID;
	}
}
