package implementations.dm_kernel;

import interfaces.kernel.JCL_message_get_host;
import io.protostuff.Tag;

import java.util.List;
import java.util.concurrent.ConcurrentMap;

import commom.Constants;

public class MessageGetHostImpl implements JCL_message_get_host{

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 5507780546752665469L;
	
	@Tag(1)
	private int type;
	@Tag(2)
	private List<String> slavesIDs;
	@Tag(3)
	private ConcurrentMap<String,String[]> slaves;
    @Tag(4)
    private byte typeD;
	@Tag(5)
	private String MAC;


	@Override
	public List<String> getSlavesIDs() {
		// TODO Auto-generated method stub
		return this.slavesIDs;
	}

	@Override
	public void setSlavesIDs(List<String> slavesIDs) {
		this.slavesIDs = slavesIDs;		
	}
	
	@Override
	public ConcurrentMap<String, String[]> getSlaves() {
		// TODO Auto-generated method stub
		return this.slaves;
	}

	@Override
	public void setSlaves(ConcurrentMap<String,String[]> slaves) {
		this.slaves = slaves;		
	}

	@Override
	public int getMsgType() {
		// TODO Auto-generated method stub
		return Constants.Serialization.MSG_GETHOST;
	}

	@Override
	public int getType() {
		// TODO Auto-generated method stub
		return type;
	}

	@Override
	public void setType(int type) {
		// TODO Auto-generated method stub
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
	@Override
	public String getMAC() {
		return MAC;
	}

	@Override
	public void setMAC(String mAC) {
		MAC = mAC;
	}
	
}
