package implementations.test;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import implementations.collections.JCLHashMap;
import implementations.dm_kernel.IoTuser.JCL_IoTFacadeImpl;
import implementations.dm_kernel.user.JCL_FacadeImpl;
import implementations.util.Entry;
import interfaces.kernel.JCL_IoTfacade;
import interfaces.kernel.JCL_facade;
import interfaces.kernel.JCL_result;

public class testeJCL {
	
	public testeJCL() throws InterruptedException, ExecutionException{

//		testeGeral();
//		testeMap();
		testeIoT();
		
	}

	public void testeGeral() throws InterruptedException, ExecutionException {
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
	
	public void testeMap(){
		Map<Integer, Integer> teste = new JCLHashMap<Integer, Integer>("Teste");
		teste.put(0, 1);
		teste.put(1, 10);
		System.out.println(teste.get(0));
		System.out.println(teste.get(1));
		
		for(java.util.Map.Entry<Integer, Integer> v:teste.entrySet()){
			System.out.println("key:"+v.getKey()+" value:"+v.getValue());
		}
		System.out.println("Fim");
	}

	public void testeIoT(){
		
		JCL_IoTfacade iot = JCL_IoTFacadeImpl.getInstance();
		System.out.println("Fim");
	}
}
