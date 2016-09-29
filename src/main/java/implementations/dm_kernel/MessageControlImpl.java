package implementations.dm_kernel;

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
		return MSG_CONTROL;
	}
}
