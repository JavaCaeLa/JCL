package implementations.dm_kernel;

import commom.Constants;
import interfaces.kernel.JCL_message_generic;
import io.protostuff.Tag;

public class MessageGenericImpl implements JCL_message_generic {

    /**
     *
     */
    private static final long serialVersionUID = -8052143086240494591L;
    @Tag(1)
    private int type;
    @Tag(2)
    private Object data;
    @Tag(3)
    private byte typeD;

    @Override
    public Object getRegisterData() {
        return this.data;
    }

    @Override
    public void setRegisterData(Object data) {
        this.data = data;
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
    public int getMsgType() {
        // TODO Auto-generated method stub
        return Constants.Serialization.MSG_GENERIC;
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
