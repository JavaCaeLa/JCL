package implementations.dm_kernel;

import java.util.HashMap;
import java.util.Map;

import commom.Constants;
import interfaces.kernel.JCL_message_list_task;
import interfaces.kernel.JCL_task;
import io.protostuff.Tag;

public class MessageListTaskImpl implements JCL_message_list_task{


	/**
	 * 
	 */
	private static final long serialVersionUID = -4877761367663451523L;
	@Tag(1)
	private int type;
	@Tag(2)
	private  Map<Long,JCL_task> task = new HashMap<Long,JCL_task>();
	@Tag(3)
	private String host;
	@Tag(4)
	private int port;	
    @Tag(5)
    private byte typeD;
		
	public void setHostPort(String host, int port){
		this.host = host;
		this.port = port;
	}
	
	@Override
	public void addTask(Long key, JCL_task t) {
		this.task.put(key, t);	
	}	
		
	
	@Override
	public Map<Long,JCL_task> getMapTask() {
		// TODO Auto-generated method stub
		return this.task;
	}
	
	@Override
	public int getMsgType() {
		// TODO Auto-generated method stub
		return Constants.Serialization.MSG_LISTTASK;
	}

	@Override
	public int taskSize() {
		// TODO Auto-generated method stub
		return this.task.size();
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
