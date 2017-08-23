package implementations.dm_kernel;

import commom.Constants;
import interfaces.kernel.JCL_message_commons;
import io.protostuff.Tag;

public class MessageCommonsImpl implements JCL_message_commons{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 7000504857562969279L;	

	@Tag(1)
	private int type;
	@Tag(2)
	private String[] data;
    @Tag(3)
    private byte typeD;
	
	@Override
	public String[] getRegisterData() {
		return this.data;
	}
	@Override
	public void setRegisterData(String... data) {
		this.data = data;
	}
	
	@Override
	public int getMsgType() {
		// TODO Auto-generated method stub
		return Constants.Serialization.MSG_COMMONS;
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
	public byte getTypeDevice() {
		// TODO Auto-generated method stub
		return typeD;
	}

	@Override
	public void setTypeDevice(byte typeDevice) {
		typeD = typeDevice;		
	}
}
