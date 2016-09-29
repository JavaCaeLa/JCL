package implementations.dm_kernel;

import java.util.HashMap;
import java.util.Map;
import interfaces.kernel.JCL_message_list_task;
import interfaces.kernel.JCL_task;

public class MessageListTaskImpl extends MessageImpl implements JCL_message_list_task{


	/**
	 * 
	 */
	private static final long serialVersionUID = -4877761367663451523L;
	private  Map<String,JCL_task> task = new HashMap<String,JCL_task>();
	private String host;		
	private int port;		
		
	public void setHostPort(String host, int port){
		this.host = host;
		this.port = port;
	}
	
	@Override
	public void addTask(String key, JCL_task t) {
		this.task.put(key, t);	
	}	
		
	
	@Override
	public Map<String,JCL_task> getMapTask() {
		// TODO Auto-generated method stub
		return this.task;
	}
	
	@Override
	public int getMsgType() {
		// TODO Auto-generated method stub
		return MSG_LISTTASK;
	}

	@Override
	public int taskSize() {
		// TODO Auto-generated method stub
		return this.task.size();
	}
}
