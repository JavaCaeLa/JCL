package implementations.dm_kernel;

import interfaces.kernel.JCL_message_list_global_var;
import java.util.HashMap;
import java.util.Map;

public class MessageListGlobalVarImpl extends MessageImpl implements JCL_message_list_global_var{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -234826211022391890L;
	
	private Map<Object,Object> keyValue = new HashMap<Object,Object>();

		
	public MessageListGlobalVarImpl(Object key, Object instance){
		getKeyValue().put(key, instance);
	}
	
	public MessageListGlobalVarImpl() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public Object putVarKeyInstance(Object key, Object instance) {
		return getKeyValue().put(key, instance);
	}
	
	/**
	 * @return the keyValue
	 */
	public Map<Object,Object> getKeyValue(){
		return keyValue;
	}
	
	@Override
	public int getMsgType() {
		// TODO Auto-generated method stub
		return MSG_LISTGLOBALVARS;
	}

}
