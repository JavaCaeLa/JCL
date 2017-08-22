package implementations.dm_kernel.host;

import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;
import java.util.concurrent.ScheduledFuture;

import javax.imageio.ImageIO;

import org.eclipse.paho.client.mqttv3.MqttMessage;

import com.hopding.jrpicam.RPiCamera;

import implementations.collections.JCLHashMap;
import implementations.dm_kernel.IoTuser.Board;
import implementations.dm_kernel.user.JCL_FacadeImpl;
import interfaces.kernel.JCL_IoT_Sensing_Model;
import interfaces.kernel.JCL_Sensor;
import interfaces.kernel.JCL_facade;
import mraa.Aio;
import mraa.Gpio;

public class SensorAcq implements Runnable{
	private int pin;
	private String alias;
	private long delay;
	private int size;
	private char dir;
	private Object lastValue;
	private int type;
	private int min;
	private int max;
	private String dataType;
	private JCLHashMap<Integer, JCL_Sensor> values;
	private ScheduledFuture<SensorAcq> future;
	
	public SensorAcq() {
		min = 0;
		max = 0;
		size = 0;
	}

	@Override
	public void run() {
		try{
			if (Board.isStandBy())
				return;
			Object value = sensing();
			if (value == null)
				return;
			if (pin == 41 && Board.getPlatform().equals(JCL_IoT_Sensing_Model.RASPBERRY_PI_2_B))
				dataType = "jpeg";			

			setLastValue(value);
			if ( Board.getMqttClient() != null && Board.getMqttClient().isConnected() ){
				ByteArrayOutputStream out = new ByteArrayOutputStream();
				ObjectOutputStream os = new ObjectOutputStream(out);
				os.writeObject(value);

				MqttMessage message = new MqttMessage(out.toByteArray());
				message.setQos(2);

				if (Board.getMqttClient().isConnected()){
					Board.getMqttClient().publish(Board.getDeviceAlias() + "/" + this.alias, message);
				}
			}
			
			Board.saveAsGV(this, dataType);
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public Object sensing(){
		Object value = null;
		try{
			if ( pin == 41 && Board.getPlatform().equals(JCL_IoT_Sensing_Model.RASPBERRY_PI_2_B) ) {
				RPiCamera piCamera = new RPiCamera("/home/pi/Pictures");
				piCamera.setWidth(2592);   // Set width property of RPiCamera
				piCamera.setHeight(1944); // Set height property of RPiCamera
				piCamera.setTimeout(200);
				
				while (value == null){
					ByteArrayOutputStream baos = new ByteArrayOutputStream();
					ImageIO.write( piCamera.takeBufferedStill(), "jpg", baos );
					baos.flush();
					byte[] imageInByte = baos.toByteArray();
					baos.close();
					value = imageInByte;
				}
			}
			else if ( Board.getSensingModel().isPortDigital(getPin()) ){
				Gpio gpio = new Gpio(Board.getSensingModel().getGPIO(pin), true);			
				value = gpio.read();
				gpio.delete();
			}else{
				Aio aio = new Aio( Board.getSensingModel().getGPIO(pin) );
				value =  aio.read();
				aio.delete();
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return value;
	}
	
	public void removeFuture(int pin){
		if (future != null)
			future.cancel(false);
		if (Board.isAllowUser()){
			if (values!= null)
				values.clear();
			JCL_facade jcl = JCL_FacadeImpl.getInstancePacu();
			jcl.deleteGlobalVar(Board.getMac() + Board.getPort() + pin +"_NUMELEMENTS");
		}
	}
	
	public String getAlias() {
		return alias;
	}

	public void setAlias(String alias) {
		this.alias = alias;
	}

	public long getDelay() {
		return delay;
	}

	public void setDelay(long delay) {
		this.delay = delay;
	}

	public char getDir() {
		return dir;
	}

	public void setDir(char dir) {
		this.dir = dir;
	}

	public Object getLastValue() {
		return lastValue;
	}

	public void setLastValue(Object lastValue) {
		this.lastValue = lastValue;
	}
	
	public int getPin() {
		return pin;
	}
	
	public void setPin(int port) {
		this.pin = port;
	}

	public int getSize() {
		return size;
	}

	public void setSize(int size) {
		this.size = size;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public int getMax() {
		return max;
	}

	public void setMax(int max) {
		this.max = max;
	}

	public int getMin() {
		return min;
	}

	public void setMin(int min) {
		this.min = min;
	}
	
	public int getMinAndIncrement(){
		return this.min++;
	}
	
	
	public int getMaxAndIncrement(){
		return this.max++;
	}

	public ScheduledFuture<SensorAcq> getFuture() {
		return future;
	}

	public void setFuture(ScheduledFuture<SensorAcq> future) {
		this.future = future;
	}

	public String getDataType() {
		return dataType;
	}

	public JCLHashMap<Integer, JCL_Sensor> getValues() {
		return values;
	}

	public void setValues(JCLHashMap<Integer, JCL_Sensor> values) {
		this.values = values;
	}
	
}