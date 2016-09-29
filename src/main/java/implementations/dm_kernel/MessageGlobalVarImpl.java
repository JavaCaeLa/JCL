package implementations.dm_kernel;

import interfaces.kernel.JCL_message_global_var;

public class MessageGlobalVarImpl extends MessageImpl implements JCL_message_global_var{

	/**
	 * 
	 */
	private static final long serialVersionUID = 7445623646784876528L;
	private Object key;
	private Object instance;
		
	public MessageGlobalVarImpl(Object key, Object instance){
		this.key = key;
		this.instance = instance;
	}

	public MessageGlobalVarImpl(){
	}
	
	@Override
	public Object getVarKey() {
		return this.key;
	}
	
	@Override
	public Object getVarInstance() {
		return this.instance;
	}
		
	@Override
	public int getMsgType() {
		return MSG_GLOBALVARS;
	}	
}
