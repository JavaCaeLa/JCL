package implementations.dm_kernel.CPuser;

import interfaces.kernel.JCL_CPfacade;
import interfaces.kernel.JCL_result;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Future;
import implementations.collections.JCLPFuture;
import interfaces.kernel.JCL_message_long;

/*
 * 
 *  
 1 public boolean register(File[] f, String classToBeExecuted) {
 2 public boolean unRegister(String nickName) {
 3 public void executeSimple(String objectNickname, Object... args) {
 4 public String execute(String objectNickname, Object... args) {
 5 public String execute(String className, String methodName, Object... args) {
 6 public JCL_result getResultBlocking(String ID) {
 7 public JCL_result getResultUnblocking(String ID) {
 8 public JCL_result removeResult(String ID) {
 9 public Object instantiateGlobalVar(String varName, Class<?> varType,
 10 public boolean instantiateGlobalVar(String varName, Object instance) {
 11 public boolean destroyGlobalVar(String varName) {
 12 public boolean setValue(String varName, Object value) {
 13 public boolean setValueUnlocking(String varName, Object value) {
 14 public JCL_result getValue(String varName) {
 15 public JCL_result getValueLocking(String varName) {
 16 public void destroy() {
 17 public boolean containsGlobalVar(String ninckName){
 18 public boolean containsTask(String ninckName){
 19 public List<String> getHosts() {

 METHOD DEPRECATED in JCL distributed version: public boolean register(Class<?> object, String nickName) {

 */

public class JCL_CPFacadeImpl implements JCL_CPfacade{

//	private ConcurrentMap<String, Map<String, String>> devices;
	private static JCL_CPfacade instanceCP;

//	@Override
//	public List<Long> getTaskTimes(Future<JCL_result> ticket) {
//		// TODO Auto-generated method stub
//		return null;
//	}

	//Get All times of a Task
	@Override
	public List<Long> getTaskTimes(Future<JCL_result> ticket){	
		try {
			Long tLamb = ((JCLPFuture)ticket).getTicket();
			Future<JCL_result> t = LambariHPC.execute("JCL_FacadeImplLamb", "getTaskTimes", null);					
			return ((ConcurrentHashMap<Long, List<Long>>)(t.get()).getCorrectResult()).get(tLamb);
			
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			System.err.println("problem in JCL facade ConcurrentHashMap<Long, List<Long>> getTaskTimes()");
			return null;
		}
	}


	@Override
	public Long getTotalTime(Future<JCL_result> ticket) {
		// TODO Auto-generated method stub
		List<Long> times = this.getTaskTimes(ticket);
		if (times.size()==6){
			return (times.get(5)-times.get(0));
		}else{
			return (times.get(7)-times.get(0));			
		}
	}

	@Override
	public Long getQueueTime(Future<JCL_result> ticket) {
		// TODO Auto-generated method stub
		List<Long> times = this.getTaskTimes(ticket);
		if (times.size()==6){
			return (times.get(2)-times.get(1));
		}else{
			return ((times.get(2)-times.get(1))+(times.get(4)-times.get(3)));	
		}
	}

	@Override
	public Long getExecutionTime(Future<JCL_result> ticket){
		List<Long> times = this.getTaskTimes(ticket);
		if (times.size()==6){
			return (times.get(3)-times.get(2));
		}else{
			return (times.get(5)-times.get(4));			
		}
	}

	@Override
	public Long getResultRetrievalTime(Future<JCL_result> ticket) {
		// TODO Auto-generated method stub
		List<Long> times = this.getTaskTimes(ticket);
		if (times.size()==6){
			return (times.get(4)-times.get(3));
		}else{
			return (times.get(6)-times.get(5));	
		}
		
	}

	@Override
	public Long getHostTime(Future<JCL_result> ticket) {
		List<Long> times = this.getTaskTimes(ticket);
		if (times.size()==6){
			return (times.get(4)-times.get(1));
		}else{
			return ((times.get(2)-times.get(1))+(times.get(6)-times.get(3)));	
		}
	}

	@Override
	public Long getNetworkTime(Future<JCL_result> ticket) {
		// TODO Auto-generated method stub
		List<Long> times = this.getTaskTimes(ticket);
		if (times.size()==6){
			return ((times.get(5)-times.get(0))-(times.get(4)-times.get(1)));	
		}else{
			return ((times.get(7)-times.get(0))-(times.get(2)-times.get(1)) - (times.get(6)-times.get(3)));	
		}
	}

	@Override
	public Long getMemory(Future<JCL_result> ticket) {
		try {
			Long tLamb = ((JCLPFuture)ticket).getTicket();
			Future<JCL_result> t = LambariHPC.execute("JCL_FacadeImplLamb", "getTaskMemory", null);					
			return ((ConcurrentHashMap<Long, Long>)(t.get()).getCorrectResult()).get(tLamb);
			
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			System.err.println("problem in JCL facade getMemory(Future<JCL_result> ticket)");
			return null;
		}	}

//	@Override
//	public String getVersion() {
//		// TODO Auto-generated method stub
//		return null;
//	}

	@Override
	public Long getServerTime() {
		try {
		//exec lamb
		Object[] argsLam = {PacuHPC.serverAdd,PacuHPC.serverPort,new Integer(26)};
		Future<JCL_result> t = LambariHPC.execute("JCL_FacadeImplLamb", "getServerTime", argsLam);
		JCL_message_long mst = (JCL_message_long) (t.get()).getCorrectResult();
		return mst.getRegisterData()[0];
	} catch (Exception e) {
		System.err
				.println("JCL facade Lambari problem in getServerTime()");
		return null;
	}
	}

	@Override
	public Long getDeviceTime(Entry<String, String> device) {
  		 try {
   			 
				Map<String, String> hostPort = PacuHPC.getDeviceMetadata(device);
				
				String host = hostPort.get("IP");
	   		  	String port = hostPort.get("PORT");
	   		  	String mac = hostPort.get("MAC");
	   		  	String portS = hostPort.get("PORT_SUPER_PEER");
	   		  	
  			//exec lamb
  			Object[] argsLam = {host,Integer.parseInt(port),new Integer(26)};
  			Future<JCL_result> t = LambariHPC.execute("JCL_FacadeImplLamb", "getServerTime", argsLam);
  			JCL_message_long mst = (JCL_message_long) (t.get()).getCorrectResult();
  			return mst.getRegisterData()[0];
  		} catch (Exception e) {
  			System.err
  					.println("JCL facade Lambari problem in getServerTime()");
  			return null;
  		}
	}

//	@Override
//	public Long getSuperPeerTime(Entry<String, String> device) {
//		// TODO Auto-generated method stub
//		return null;
//	}

	@Override
	public Long getServerMemory() {
		try {
		//exec lamb
		Object[] argsLam = {PacuHPC.serverAdd,PacuHPC.serverPort,new Integer(80)};
		Future<JCL_result> t = LambariHPC.execute("JCL_FacadeImplLamb", "getServerTime", argsLam);
		JCL_message_long mst = (JCL_message_long) (t.get()).getCorrectResult();
		return mst.getRegisterData()[0];
	} catch (Exception e) {
		System.err
				.println("JCL facade Lambari problem in getServerTime()");
		return null;
	}
	}

	@Override
	public Long getDeviceMemory(Entry<String, String> device) {
		   		  	
		   		 try {
		   			 
						Map<String, String> hostPort = PacuHPC.getDeviceMetadata(device);
						
						String host = hostPort.get("IP");
			   		  	String port = hostPort.get("PORT");
			   		  	String mac = hostPort.get("MAC");
			   		  	String portS = hostPort.get("PORT_SUPER_PEER");
			   		  	
		   			//exec lamb
		   			Object[] argsLam = {host,Integer.parseInt(port),new Integer(80)};
		   			Future<JCL_result> t = LambariHPC.execute("JCL_FacadeImplLamb", "getServerTime", argsLam);
		   			JCL_message_long mst = (JCL_message_long) (t.get()).getCorrectResult();
		   			return mst.getRegisterData()[0];
		   		} catch (Exception e) {
		   			System.err
		   					.println("JCL facade Lambari problem in getServerTime()");
		   			return null;
		   		}
	}

//	@Override
//	public Long getSuperPeerMemory(Entry<String, String> device) {
//		// TODO Auto-generated method stub
//		return null;
//	}

	@Override
	public Long getServerCpuUsage() {
		try {
		//exec lamb
		Object[] argsLam = {PacuHPC.serverAdd,PacuHPC.serverPort,new Integer(81)};
		Future<JCL_result> t = LambariHPC.execute("JCL_FacadeImplLamb", "getServerTime", argsLam);
		JCL_message_long mst = (JCL_message_long) (t.get()).getCorrectResult();
		return mst.getRegisterData()[0];
	} catch (Exception e) {
		System.err
				.println("JCL facade Lambari problem in getServerTime()");
		return null;
	}
	}

	@Override
	public Long getDeviceCpuUsage(Entry<String, String> device) {
  		 try {
   			 
				Map<String, String> hostPort = PacuHPC.getDeviceMetadata(device);
				
				String host = hostPort.get("IP");
	   		  	String port = hostPort.get("PORT");
	   		  	String mac = hostPort.get("MAC");
	   		  	String portS = hostPort.get("PORT_SUPER_PEER");
	   		  	
  			//exec lamb
  			Object[] argsLam = {host,Integer.parseInt(port),new Integer(81)};
  			Future<JCL_result> t = LambariHPC.execute("JCL_FacadeImplLamb", "getServerTime", argsLam);
  			JCL_message_long mst = (JCL_message_long) (t.get()).getCorrectResult();
  			return mst.getRegisterData()[0];
  		} catch (Exception e) {
  			System.err
  					.println("JCL facade Lambari problem in getServerTime()");
  			return null;
  		}
	}

	public static JCL_CPfacade getInstance() {
		return Holder.getCPInstance();
	}

//	@Override
//	public Long getSuperPeerCpuUsage(Entry<String, String> device) {
//		// TODO Auto-generated method stub
//		return null;
//	}
	
	public static class Holder extends implementations.dm_kernel.user.JCL_FacadeImpl.Holder{
		
		protected static String serverIP() {			
			 return implementations.dm_kernel.user.JCL_FacadeImpl.Holder.ServerIP();
		}

		protected static int serverPort() {			
			return implementations.dm_kernel.user.JCL_FacadeImpl.Holder.ServerPort();			
		}
		
		protected synchronized static JCL_CPfacade getCPInstance(){
						
			//get type of Instance 
			if (instanceCP == null){
				instanceCP = new JCL_CPFacadeImpl();
			}
						
			return instanceCP;
		}
		
	}
}
