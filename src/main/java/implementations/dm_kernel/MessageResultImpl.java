package implementations.dm_kernel;

import interfaces.kernel.JCL_message_result;
import interfaces.kernel.JCL_result;

public class MessageResultImpl extends MessageImpl implements JCL_message_result{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1805440475172003438L;
	private JCL_result result;

	@Override
	public JCL_result getResult() {
		// TODO Auto-generated method stub
		return this.result;
	}

	@Override
	public void setResult(JCL_result result) {
		this.result = result;
		
	}
	
	@Override
	public int getMsgType() {
		// TODO Auto-generated method stub
		return MSG_RESULT;
	}
}
