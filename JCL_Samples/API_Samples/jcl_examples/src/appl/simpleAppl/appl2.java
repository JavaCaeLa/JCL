package appl.simpleAppl;

import implementations.dm_kernel.user.JCL_FacadeImpl;
import interfaces.kernel.JCL_facade;
import interfaces.kernel.JCL_result;

import java.io.File;
import java.util.List;
import java.util.concurrent.Future;


//a developer application just to test JCL 
public class appl2 {

	/**
	 * @param args
	 */
	@SuppressWarnings("unused")
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		new appl2(); 
	}
	
	public appl2(){
		
		JCL_facade jcl = JCL_FacadeImpl.getInstance();
		
		Boolean b = jcl.register(UserServices.class, "UserServices");
		System.err.println(b);
		
		// the useful result of JavaCa&La
		List<JCL_result> jclrL;
		
			
		// Get cluster sizes
		int clusterSize = jcl.getDevices().size();
		int ClusterCoreSize = jcl.getClusterCores();
		
		// Create matrix of object
		Object[][] argsExec = new Object[clusterSize][3];
		Object[][] argsExecC = new Object[ClusterCoreSize][3];

		// Create different arg to executeAll 
		for(int x = 0;x < clusterSize;x++){
			Object[] args ={new Integer("1"), new Integer(x*20), new Integer(10)};
			argsExec[x] = args;
		}
		
		// Create different arg to executeAllCores
		for(int x = 0;x < ClusterCoreSize;x++){
			Object[] args ={new Integer("1"), new Integer(x*20), new Integer(10)};
			argsExecC[x] = args;
		}
		
		// Same arg to all execute
		Object[] args1 ={new Integer("1"), new Integer("100"), new Integer(10)};
		
		
		//Send task to cluster
		List<Future<JCL_result>> ticket0 = jcl.executeAll("UserServices", argsExec);
		List<Future<JCL_result>> ticket1 = jcl.executeAll("UserServices", args1);
		List<Future<JCL_result>> ticket2 = jcl.executeAllCores("UserServices", argsExecC);
		List<Future<JCL_result>> ticket3 = jcl.executeAllCores("UserServices", args1);
		
				
		//Get result executeAll
		jclrL = null;
		jclrL = jcl.getAllResultBlocking(ticket0);
		System.out.println("ExecuteAll different arg.");
		for(JCL_result jclr:jclrL){	
			if(jclr.getCorrectResult()!=null)
				System.out.println("pa is: " + jclr.getCorrectResult().toString());
			else jclr.getErrorResult().printStackTrace();		
		}
	
		//Get result executeAll
		jclrL = null;
		jclrL = jcl.getAllResultBlocking(ticket1);
		System.out.println("ExecuteAll same arg.");
		for(JCL_result jclr:jclrL){	
			if(jclr.getCorrectResult()!=null)
				System.out.println("pa is: " + jclr.getCorrectResult().toString());
			else jclr.getErrorResult().printStackTrace();		
		}
	
		//Get result executeAllCore
		jclrL = null;
		jclrL = jcl.getAllResultBlocking(ticket2);
		System.out.println("ExecuteAllCore different arg.");
		for(JCL_result jclr:jclrL){	
			if(jclr.getCorrectResult()!=null)
				System.out.println("pa is: " + jclr.getCorrectResult().toString());
			else jclr.getErrorResult().printStackTrace();		
		}

		//Get result executeAllCore
		jclrL = null;
		jclrL = jcl.getAllResultBlocking(ticket3);
		System.out.println("ExecuteAllCore same arg.");
		for(JCL_result jclr:jclrL){	
			if(jclr.getCorrectResult()!=null)
				System.out.println("pa is: " + jclr.getCorrectResult().toString());
			else jclr.getErrorResult().printStackTrace();		
		}
	
	
		jcl.destroy();
		
	}

}
