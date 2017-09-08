package implementations.dm_kernel;

import commom.Constants;
import interfaces.kernel.JCL_message_control;
import io.protostuff.Tag;

public class MessageControlImpl implements JCL_message_control{

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 2451196379433239868L;

	@Tag(1)
	private int type;
	@Tag(2)
	private String[] data;
    @Tag(3)
    private byte typeD;
	

	@Override
	public String[] getRegisterData() {
		// TODO Auto-generated method stub
		return this.data;
	}

	@Override
	public void setRegisterData(String... data) {
		// TODO Auto-generated method stub
		this.data = data;
	}

	@Override
	public int getType() {
		return type;
	}

	@Override
	public void setType(int type) {
		this.type = type;
	}

	@Override
	public int getMsgType() {
		// TODO Auto-generated method stub
		return Constants.Serialization.MSG_CONTROL;
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
