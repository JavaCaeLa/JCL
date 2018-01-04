package appl.simpleAppl;

import java.util.List;
import java.util.concurrent.Future;

import implementations.dm_kernel.user.JCL_FacadeImpl;
import interfaces.kernel.JCL_facade;
import interfaces.kernel.JCL_result;

public class appl5 {
	public static void main(String[] args) {
		appl5 app = new appl5();
	}
	
	public appl5() {
		
		// TODO Auto-generated constructor stub
		JCL_facade javaCaLa = JCL_FacadeImpl.getInstance();
		
		//the name of the class to be executed in the Jar file		
		javaCaLa.register(HostName.class, "HostName");
		
		//no args execute in all host
		List<Future<JCL_result>> ticket = javaCaLa.executeAll("HostName", "exec");
		
		// wait the results
		List<JCL_result> result = javaCaLa.getAllResultBlocking(ticket);
		
		for (int cont = 0; cont < result.size(); cont++){			
			System.out.println("CP name:"+result.get(cont).getCorrectResult());
		}
	
		//close JCL
		javaCaLa.destroy();
	}

}
