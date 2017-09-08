package implementations.dm_kernel;

import interfaces.kernel.JCL_message_list_global_var;
import io.protostuff.Tag;

import java.util.HashMap;
import java.util.Map;

import commom.Constants;

public class MessageListGlobalVarImpl implements JCL_message_list_global_var{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -234826211022391890L;

	@Tag(1)
	private int type;
	@Tag(2)
	private Map<Object,Object> keyValue = new HashMap<Object,Object>();
    @Tag(3)
    private byte typeD;

		
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
		return Constants.Serialization.MSG_LISTGLOBALVARS;
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
