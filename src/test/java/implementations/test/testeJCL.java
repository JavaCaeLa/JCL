package implementations.test;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import implementations.dm_kernel.user.JCL_FacadeImpl;
import interfaces.kernel.JCL_facade;
import interfaces.kernel.JCL_result;

public class testeJCL {

	public testeJCL() throws InterruptedException, ExecutionException {
		// TODO Auto-generated constructor stub
		JCL_facade jcl = JCL_FacadeImpl.getInstancePacu();
		jcl.register(pacuSend.class, "pacuSend");
		Object[] arg = new Object[]{new Integer(10),new Integer(30)};
		List<Future<JCL_result>> t = jcl.executeAllCores("pacuSend","teste1", arg);
		
//		t.get();
		for(Future<JCL_result> ti:t){
			System.out.println(ti.get().getCorrectResult());
			
		}
		
		
		
		jcl.instantiateGlobalVar("Var1", new Integer(10));
		
		System.out.println(jcl.getValue("Var1").getCorrectResult());
		
		jcl.setValueUnlocking("Var1", new Integer(30));

		System.out.println(jcl.getValue("Var1").getCorrectResult());
		
		
		System.out.println("Fim");
	}
	

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		try {
			new testeJCL();
		} catch (InterruptedException | ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
