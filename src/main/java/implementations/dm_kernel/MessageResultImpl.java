package implementations.dm_kernel;

import commom.Constants;
import interfaces.kernel.JCL_message_result;
import interfaces.kernel.JCL_result;
import io.protostuff.Tag;

public class MessageResultImpl  implements JCL_message_result{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1805440475172003438L;

	@Tag(1)
	private int type;
	@Tag(2)
	private JCL_result result;
    @Tag(3)
    private byte typeD;

	@Override
	public JCL_result getResult() {
		// TODO Auto-generated method stub
		return this.result;
	}

	@Override
	public void setResult(JCL_result result) {
		this.result = result;
		
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
		return Constants.Serialization.MSG_RESULT;
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
