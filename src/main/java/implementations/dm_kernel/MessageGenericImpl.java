package implementations.dm_kernel;

import interfaces.kernel.JCL_message_generic;
import io.protostuff.Tag;

public class MessageGenericImpl implements JCL_message_generic{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -8052143086240494591L;
	
	@Tag(1)
	private int type;
	@Tag(2)
	private Object data;
	
	@Override
	public Object getRegisterData() {
		return this.data;
	}

	@Override
	public void setRegisterData(Object data) {
		this.data = data;
	}
	
	@Override
	public int getMsgType() {
		// TODO Auto-generated method stub
		return MSG_GENERIC;
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
	
}
