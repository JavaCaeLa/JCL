package implementations.dm_kernel;

import commom.Constants;
import interfaces.kernel.JCL_message_register;
import interfaces.kernel.JCL_message_task;
import interfaces.kernel.JCL_task;
import io.protostuff.Tag;

public class MessageTaskImpl implements JCL_message_task {


    /**
     *
     */
    private static final long serialVersionUID = 7931886566254801531L;

    @Tag(1)
    private int type;
    @Tag(2)
    private JCL_message_register JarFile;
    @Tag(3)
    private JCL_task task;
    @Tag(4)
    private byte typeD;

//	public MessageTaskImpl(){}
//	
//	public MessageTaskImpl(String host,int port) {
//		// TODO Auto-generated constructor stub
//		this.host = host;
//		this.port = port;
//	}

    @Override
    public JCL_task getTask() {
        // TODO Auto-generated method stub
        return this.task;
    }


    @Override
    public void setTask(JCL_task t) {
        this.task = t;
    }

    @Override
    public JCL_message_register getMessageRegister() {
        return this.JarFile;
    }

    @Override
    public void setMessageRegister(JCL_message_register userRegister) {
        this.JarFile = userRegister;
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
        return Constants.Serialization.MSG_TASK;
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
