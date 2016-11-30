package implementations.dm_kernel.CPuser;

import interfaces.kernel.JCL_CPfacade;
import interfaces.kernel.JCL_result;
import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.Future;

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

	@Override
	public List<Long> getTaskTimes(Future<JCL_result> ticket) {
		// TODO Auto-generated method stub
		return null;
	}

	//Get All times of a Task
//	@Override
//	public List<Long> getTaskTimes(String ID){	
//		try {
//			Future<JCL_result> t = jcl.execute("JCL_FacadeImplLamb", "getTaskTimes", null);					
//			return ((ConcurrentHashMap<Long, List<Long>>)(t.get()).getCorrectResult()).get(Long.parseLong(ID));
//			
//		} catch (Exception e) {
//			// TODO: handle exception
//			e.printStackTrace();
//			System.err.println("problem in JCL facade ConcurrentHashMap<Long, List<Long>> getTaskTimes()");
//			return null;
//		}
//	}


	@Override
	public Long getTotalTime(Future<JCL_result> ticket) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Long getQueueTime(Future<JCL_result> ticket) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Long getExecutionTime(Future<JCL_result> ticket) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Long getResultRetrievalTime(Future<JCL_result> ticket) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Long getHostTime(Future<JCL_result> ticket) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Long getNetworkTime(Future<JCL_result> ticket) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Long getMemory(Future<JCL_result> ticket) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getVersion() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Long getServerTime() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Long getDeviceTime(Entry<String, String> device) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Long getSuperPeerTime(Entry<String, String> device) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Long getServerMemory() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Long getDeviceMemory(Entry<String, String> device) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Long getSuperPeerMemory(Entry<String, String> device) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Long getServerCpuUsage() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Long getDeviceCpuUsage(Entry<String, String> device) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Long getSuperPeerCpuUsage(Entry<String, String> device) {
		// TODO Auto-generated method stub
		return null;
	}
	
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
