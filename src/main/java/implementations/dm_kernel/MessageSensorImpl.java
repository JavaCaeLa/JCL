package implementations.dm_kernel;

import commom.Constants;
import interfaces.kernel.JCL_message_sensor;
import io.protostuff.Tag;

//public class MessageSensorImpl implements JCL_message{
public class MessageSensorImpl implements JCL_message_sensor{
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 8101397026813525678L;
	@Tag(1)
	private int type;
	@Tag(2)
	private String device;
	@Tag(3)
	private int sensor;
	@Tag(4)
	private Object value;
	@Tag(5)
	private long time;
	@Tag(6)
	private String dataType;
    @Tag(7)
    private byte typeD;
	
	
	@Override
	public String getDataType() {
		return dataType;
	}
	@Override
	public void setDataType(String dataType) {
		this.dataType = dataType;
	}
	@Override
	public long getTime() {
		return time;
	}
	@Override
	public void setTime(long time) {
		this.time = time;
	}

	@Override
	public String getDevice() {
		return device;
	}
	
	@Override
	public void setDevice(String device) {
		this.device = device;
	}
	
	@Override
	public int getSensor() {
		return sensor;
	}
	
	@Override
	public void setSensor(int sensor) {
		this.sensor = sensor;
	}
	
	@Override
	public Object getValue() {
		return value;
	}
	
	@Override	
	public void setValue(Object value) {
		this.value = value;
	}
	
	@Override
	public int getType() {
		// TODO Auto-generated method stub
		return this.type;
	}

	@Override
	public void setType(int type) {
		// TODO Auto-generated method stub
		this.type = type;
	}

	@Override
	public int getMsgType() {
		// TODO Auto-generated method stub
		return Constants.Serialization.MSG_SENSOR;
	}
	@Override
	public byte getTypeDevice() {
		// TODO Auto-generated method stub
		return typeD;
	}

	@Override
	public void setTypeDevice(byte typeDevice) {
		typeD = typeDevice;		
	}
}
