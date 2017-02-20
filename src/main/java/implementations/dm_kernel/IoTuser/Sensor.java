package implementations.dm_kernel.IoTuser;

public class Sensor {
	private int pin;
	private String alias;
	private long delay;
	private int size;
	private char dir;
	private float lastValue;
	private long lastExecuted;
	private int type;
	private int min;
	private int max;
	
	public Sensor() {
		lastExecuted = 0;
		min = 0;
		max = 0;
		size = 0;
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
}
