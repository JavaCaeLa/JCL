package implementations.dm_kernel;

import commom.Constants;
import interfaces.kernel.JCL_message_global_var_obj;
import io.protostuff.Tag;

public class MessageGlobalVarObjImpl implements JCL_message_global_var_obj{

	
	/**
	 * 
	 */
	private static final long serialVersionUID = -3584036403579947694L;
	@Tag(1)
	private int type;
	@Tag(2)
	private String nickName;
	@Tag(3)
	private Object key;
	@Tag(4)
	private Object[] defaultValues;
    @Tag(5)
    private byte typeD;
	
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
		return Constants.Serialization.MSG_GLOBALVARSOBJ;
	}

	@Override
	public Object getVarInstance() {
		// TODO Auto-generated method stub
		return null;
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
