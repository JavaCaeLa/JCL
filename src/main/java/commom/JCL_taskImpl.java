package commom;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import interfaces.kernel.JCL_task;

public class JCL_taskImpl implements JCL_task{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Long id;
	private int port;
	private String name;
	private String host;		
	private String method = "execute";
	private Object[] parameters;
	private boolean hostChange = true;
	private List<Long> time = Collections.synchronizedList( new ArrayList<Long>());
	private Class<?> userClass;
 
	public JCL_taskImpl(Long id, String name, String method, Object...parameters){
		this.id = id;
		this.name = name;
		this.method = method;		
		this.parameters = parameters;		
	}

	public JCL_taskImpl(Long id, String name, Object...parameters){
		this.id = id;
		this.name = name;
		this.parameters = parameters;		
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
