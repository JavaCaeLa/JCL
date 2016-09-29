package implementations.dm_kernel;

import interfaces.kernel.JCL_message_register;
import interfaces.kernel.JCL_message_task;
import interfaces.kernel.JCL_task;

public class MessageTaskImpl extends MessageImpl implements JCL_message_task{

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 7931886566254801531L;
	private JCL_message_register JarFile;
	private JCL_task task;
	
//	public MessageTaskImpl(){}
//	
//	public MessageTaskImpl(String host,int port) {
//		// TODO Auto-generated constructor stub
//		this.host = host;
//		this.port = port;
//	}
	
	@Override
	public JCL_task getTask() {
		// TODO Auto-generated method stub
		return this.task;
	}

	
	@Override
	public void setTask(JCL_task t) {
		this.task=t;		
	}
	
	@Override
	public JCL_message_register getMessageRegister() {
			return this.JarFile;		
	}
		
	@Override
	public void setMessageRegister(JCL_message_register userRegister){               		
			this.JarFile = userRegister;
	}
	
	@Override
	public int getMsgType() {
		// TODO Auto-generated method stub
		return MSG_TASK;
	}
}
