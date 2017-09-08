package implementations.dm_kernel;


import commom.Constants;
import interfaces.kernel.JCL_message_long;
import io.protostuff.Tag;

public class MessageLongImpl implements JCL_message_long{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 7000504857562969279L;

	@Tag(1)
	private int type;
	@Tag(2)
	private Long[] data;
    @Tag(3)
    private byte typeD;
	
	@Override
	public Long[] getRegisterData() {
		return this.data;
	}

	@Override
	public void setRegisterData(Long... data) {
		this.data = data;
	}
		
	@Override
	public int getMsgType() {
		// TODO Auto-generated method stub
		return Constants.Serialization.MSG_LONG;
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
