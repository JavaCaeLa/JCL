package implementations.dm_kernel;

import java.util.Map;
import interfaces.kernel.JCL_message_metadata;
import io.protostuff.Tag;

//public class MessageMetaImpl implements JCL_message{
public class MessageMetadataImpl implements JCL_message_metadata{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -5106525308167974692L;
	
	@Tag(1)
	private int type;
	@Tag(2)
	private Map<String, String> Metadados;
		

	@Override
	public Map<String, String> getMetadados() {
		return Metadados;
	}
	@Override
	public void setMetadados(Map<String, String> metadados) {
		Metadados = metadados;
	}	
	
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

	@Override
	public int getMsgType() {
		// TODO Auto-generated method stub
		return MSG_METADATA;
	}
}
