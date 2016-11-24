package commom;

import java.util.ArrayList;
import java.util.List;

import interfaces.kernel.JCL_task;
import io.protostuff.Tag;

public class JCL_taskImpl implements JCL_task{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@Tag(1)
	private Long id;
	@Tag(2)
	private int port;
	@Tag(3)
	private String name;
	@Tag(4)
	private String host;
	@Tag(5)
	private String method = "execute";
	@Tag(6)
	private Object[] parameters;
	@Tag(7)
	private boolean hostChange = true;
	@Tag(8)
	private List<Long> time = new ArrayList<Long>();
	@Tag(9)
	private Class<?> userClass;

	public JCL_taskImpl(){

	}
 
	public JCL_taskImpl(Long id, String name, String method, Object parameters){
		this.id = id;
		this.name = name;
		this.method = method;
		this.parameters = (Object[]) parameters;
	}

	public JCL_taskImpl(Long id, String name, Object parameters){
		this.id = id;
		this.name = name;
		this.parameters = (Object[]) parameters;
	}
	
	@Override
	public long getTaskID() {
		// TODO Auto-generated method stub
		return this.id;
	}
	
	@Override
	public void setTaskID(long id) {
		// TODO Auto-generated method stub
		this.id = id;
	}

	@Override
	public String getObjectName() {
		// TODO Auto-generated method stub
		return this.name;
	}

	@Override
	public String getObjectMethod() {
		// TODO Auto-generated method stub
		return this.method;
	}
	
	@Override
	public Object[] getMethodParameters() {
		// TODO Auto-generated method stub
		return this.parameters;
	}
	
	@Override
	public void setTaskTime(Long time) {
		this.time.add(time);		
	}

	@Override
	public List<Long> getTaskTime() {
		// TODO Auto-generated method stub
		return this.time;
	}

	@Override
	public Class<?> getObjectClass() {
		return this.userClass;
	}
	
	@Override
	public void setObjectClass(Class<?> userClass) {
		this.userClass = userClass;		
	}	

	@Override
	public void setObjectName(String name) {
		this.name = name;		
	}

	@Override
	public void setHost(String host) {
		// TODO Auto-generated method stub
		this.host = host;
	}

	@Override
	public void setPort(int port) {
		// TODO Auto-generated method stub
		this.port = port;
		
	}

	@Override
	public String getHost() {
		// TODO Auto-generated method stub
		return this.host;
	}

	@Override
	public int getPort() {
		// TODO Auto-generated method stub
		return this.port;
	}

	@Override
	public boolean getHostChange() {
		return hostChange;
	}

	@Override
	public void setHostChange(boolean hostChange) {
		this.hostChange = hostChange;
	}

}
