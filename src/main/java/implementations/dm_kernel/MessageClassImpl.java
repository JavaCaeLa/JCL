package implementations.dm_kernel;
import commom.Constants;
import interfaces.kernel.JCL_message_class;
import io.protostuff.Tag;


public class MessageClassImpl implements JCL_message_class {
	

	/**
	 * 
	 */
	private static final long serialVersionUID = 253490456886534768L;

	@Tag(1)
	private int type;
	@Tag(2)
	private byte[] data;
	@Tag(3)
	private String className;
    @Tag(4)
    private byte typeD;
	
	@Override
	public byte[] getRegisterData() {
		return this.data;
	}
	@Override
	public void setRegisterData(byte[] data) {
		this.data = data;
	}	
	@Override
	public int getMsgType() {
		// TODO Auto-generated method stub
		return Constants.Serialization.MSG_CLASS;
	}
	
	@Override
	public String getClassName() {
		// TODO Auto-generated method stub
		return this.className;
	}
	
	@Override
	public void setClassName(String className) {
		// TODO Auto-generated method stub
		this.className = className;
		
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
