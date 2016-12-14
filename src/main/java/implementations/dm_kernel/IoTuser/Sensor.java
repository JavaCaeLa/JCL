package implementations.dm_kernel.IoTuser;

public class Sensor {
	private int pin;
	private String alias;
	private int delay;
	private int size;
	private char dir;
	private float lastValue;
	private long lastExecuted;
	private int type;
	
	public Sensor() {
		lastExecuted = 0;
	}

	public String getAlias() {
		return alias;
	}

	public void setAlias(String alias) {
		this.alias = alias;
	}

	public int getDelay() {
		return delay;
	}

	public void setDelay(int delay) {
		this.delay = delay;
	}

	public char getDir() {
		return dir;
	}

	public void setDir(char dir) {
		this.dir = dir;
	}

	public float getLastValue() {
		return lastValue;
	}

	public void setLastValue(float lastValue) {
		this.lastValue = lastValue;
	}

	public long getLastExecuted() {
		return lastExecuted;
	}

	public void setLastExecuted(long lastExecuted) {
		this.lastExecuted = lastExecuted;
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
	
	
}
