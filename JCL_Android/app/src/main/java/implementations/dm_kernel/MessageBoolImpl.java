package implementations.dm_kernel;

import commom.Constants;
import interfaces.kernel.JCL_message_bool;
import io.protostuff.Tag;

public class MessageBoolImpl implements JCL_message_bool{
	

	/**
	 * 
	 */
	private static final long serialVersionUID = 3535614339561931225L;
	@Tag(1)
	private int type;
	@Tag(2)
	private boolean[] data;
    @Tag(3)
    private byte typeD;
	
	@Override
	public boolean[] getRegisterData() {
		return this.data;
	}
	@Override
	public void setRegisterData(boolean... data) {
		this.data = data;
	}
	
	@Override
	public int getMsgType() {
		// TODO Auto-generated method stub
		return Constants.Serialization.MSG_BOOL;
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
	public byte getTypeDevice() {
		// TODO Auto-generated method stub
		return typeD;
	}

	@Override
	public void setTypeDevice(byte typeDevice) {
		typeD = typeDevice;		
	}
	
}
