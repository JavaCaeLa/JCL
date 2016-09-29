package implementations.dm_kernel;
import interfaces.kernel.JCL_message_class;


public class MessageClassImpl extends MessageImpl implements JCL_message_class {
	

	/**
	 * 
	 */
	private static final long serialVersionUID = 253490456886534768L;
	private byte[] data;
	private String className;
	
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
		return MSG_CLASS;
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
}
