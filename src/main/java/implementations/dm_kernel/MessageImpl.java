package implementations.dm_kernel;

import interfaces.kernel.JCL_message;

public class MessageImpl implements JCL_message{

	
	private static final long serialVersionUID = 5450456847644209521L;

	private int type;

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
		return MSG;
	}	
}
