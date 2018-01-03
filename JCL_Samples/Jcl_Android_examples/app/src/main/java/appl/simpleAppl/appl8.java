package appl.simpleAppl;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import implementations.dm_kernel.user.JCL_FacadeImpl;
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
		
		//JCL_CPfacade jclCP = JCL_CPFacadeImpl.getInstance();
		//Long time = jclCP.getExecutionTime(ticket);
	//	Long storage = jclCP.getMemory(ticket);
//		Long storage = 1L;
		//System.out.println("Execution Time (nano): " + time.longValue() + " Memory: " + storage.longValue());
		//List<Long> times = jclCP.getTaskTimes(ticket);
		
		jcl.removeResult(ticket);
		jcl.destroy();		
	}	

}
