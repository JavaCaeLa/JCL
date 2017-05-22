package implementations.dm_kernel.IoTuser;

import implementations.util.IoT.ReadXML;

public class MQTTSubscriber {

	public static void main(String[] args) {
		try{
			ReadXML app = new ReadXML();
			app.readFile();
			app.start();
		}catch(Exception e){
			e.printStackTrace();
		}
	}
}
