package implementations.dm_kernel;

import interfaces.kernel.JCL_message_commons;

public class MessageCommonsImpl extends MessageImpl implements JCL_message_commons{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 7000504857562969279L;	

	private String[] data;
	
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
		return MSG_COMMONS;
	}
	
}
