package implementations.dm_kernel;

import interfaces.kernel.JCL_message_global_var_obj;

public class MessageGlobalVarObjImpl extends MessageImpl implements JCL_message_global_var_obj{

	
	/**
	 * 
	 */
	private static final long serialVersionUID = -3584036403579947694L;
	private String nickName;
	private Object key;
	private Object[] defaultValues;
	
	public MessageGlobalVarObjImpl(String nickName, Object key,
			Object[] defaultVarValue){
		
		try{
			this.key = key;
			this.nickName = nickName;
			this.defaultValues = defaultVarValue;

		}catch(Exception e){
			e.printStackTrace();
		}		
	}
		
	public MessageGlobalVarObjImpl() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public Object getVarKey() {
		return this.key;
	}

	@Override
	public Object[] getDefaultValues() {
		return this.defaultValues;
	}
	
	@Override
	public String getNickName() {
		return this.nickName;
	}

	@Override
	public int getMsgType() {
		// TODO Auto-generated method stub
		return MSG_GLOBALVARSOBJ;
	}

	@Override
	public Object getVarInstance() {
		// TODO Auto-generated method stub
		return null;
	}
	

}
