package implementations.dm_kernel;


import interfaces.kernel.JCL_message_long;

public class MessageLongImpl extends MessageImpl implements JCL_message_long{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 7000504857562969279L;
	private Long[] data;
	
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
		return MSG_LONG;
	}
	
}
