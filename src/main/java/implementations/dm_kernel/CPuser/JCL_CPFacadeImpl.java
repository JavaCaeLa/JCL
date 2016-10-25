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



	@Override
	public Long getTotaltime(Future<JCL_result> ticket) {
		// TODO Auto-generated method stub
		return null;
	}



	@Override
	public Long getQueuetime(Future<JCL_result> ticket) {
		// TODO Auto-generated method stub
		return null;
	}



	@Override
	public Long getExecutiontime(Future<JCL_result> ticket) {
		// TODO Auto-generated method stub
		return null;
	}



	@Override
	public Long getResultretrievaltime(Future<JCL_result> ticket) {
		// TODO Auto-generated method stub
		return null;
	}



	@Override
	public Long getHosttime(Future<JCL_result> ticket) {
		// TODO Auto-generated method stub
		return null;
	}



	@Override
	public Long getNetworktime(Future<JCL_result> ticket) {
		// TODO Auto-generated method stub
		return null;
	}



	@Override
	public Long getLocalmemory() {
		// TODO Auto-generated method stub
		return null;
	}



	@Override
	public List<Entry<Entry<String, String>, Long>> getAllDevicesMemory() {
		// TODO Auto-generated method stub
		return null;
	}



	@Override
	public Long getHostMemory(Entry<String, String> device) {
		// TODO Auto-generated method stub
		return null;
	}



	@Override
	public Long getDeviceCpuUsage(Entry<String, String> device) {
		// TODO Auto-generated method stub
		return null;
	}



	@Override
	public List<Entry<Entry<String, String>, Long>> getAllDeviceCpuUsage() {
		// TODO Auto-generated method stub
		return null;
	}



	@Override
	public Long getLocalcpuUsage() {
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
