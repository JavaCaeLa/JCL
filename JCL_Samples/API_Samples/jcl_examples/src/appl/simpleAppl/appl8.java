package appl.simpleAppl;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import implementations.dm_kernel.CPuser.JCL_CPFacadeImpl;
import implementations.dm_kernel.user.JCL_FacadeImpl;
import interfaces.kernel.JCL_CPfacade;
import interfaces.kernel.JCL_facade;
import interfaces.kernel.JCL_result;

public class appl8 {
	
	public static void main(String[] args) {
		new appl8();
	}
	public appl8(){
		//portable way to instantiate JCL
		JCL_facade jcl = JCL_FacadeImpl.getInstance();
		
		Boolean b = jcl.register(UserServices.class, "UserServices");
		System.err.println(b);
		
		// the useful result of JavaCa&La
		JCL_result jclr =null;
		
		Object[] args1 ={new Integer("1"), new Integer("100"), new Integer(10)};
		Future<JCL_result> ticket = jcl.execute("UserServices", args1);
		
		try {
			jclr = ticket.get();			
		} catch (InterruptedException | ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(jclr.getCorrectResult()!=null) System.err.println("PA is: " + jclr.getCorrectResult().toString());
		else jclr.getErrorResult().printStackTrace();
		
		JCL_CPfacade jclCP = JCL_CPFacadeImpl.getInstance();
		Long time = jclCP.getExecutionTime(ticket);
		Long storage = jclCP.getMemory(ticket);
		System.out.println("Execution Time (nano): " + time.longValue() + " Memory: " + storage.longValue());
		List<Long> times = jclCP.getTaskTimes(ticket);
		System.out.println("All Times collected by JCL (nano): " + times);
		Long networkTime = jclCP.getNetworkTime(ticket);
		System.out.println("Network Time (nano): " + networkTime);
		Long queueTime = jclCP.getQueueTime(ticket);
		System.out.println("Queue Time (nano): " + queueTime);		
		
		jcl.removeResult(ticket);
		jcl.destroy();		
	}	

}
