package implementations.dm_kernel;

import commom.Constants;
import interfaces.kernel.JCL_message;
import interfaces.kernel.JCL_message_global_var;
import io.protostuff.Tag;

public class MessageGlobalVarImpl  implements JCL_message_global_var, JCL_message{

	/**
	 * 
	 */
	private static final long serialVersionUID = 7445623646784876528L;
	@Tag(1)
	private int type;
	@Tag(2)
	private Object key;
	@Tag(3)
	private Object instance;
    @Tag(4)
    private byte typeD;



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
		return Constants.Serialization.MSG_GLOBALVARS;
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
