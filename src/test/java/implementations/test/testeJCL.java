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

		testeGeral();
//		testeArray();
//		testeMap();
//		testeIoT();
//		TesteLambari();
	}
	
	
	public void testeArray() throws InterruptedException, ExecutionException{
		
		JCL_facade jcl = JCL_FacadeImpl.getInstance();
		System.out.println(jcl.register(pacuSend.class, "pacuSend"));
		String[] ar = new String[]{"Andre","Luis","Barroso","Almeida"};
		Object[] arg = new Object[]{ar};
		Future<JCL_result> t = jcl.execute("pacuSend","teste2", arg);
		System.out.println(t.get().getCorrectResult());
		ar[2] = null;
		arg = new Object[]{ar};
		
		 t = jcl.execute("pacuSend","teste2", arg);
		System.out.println(t.get().getCorrectResult());
		
		System.out.println("FIM");
	}
	
	
	public void TesteLambari() throws InterruptedException, ExecutionException{
		JCL_facade jcl = JCL_FacadeImpl.getInstance();
//		System.out.println(jcl.register(pacuSend.class, "pacuSend"));
		Object[] arg = new Object[]{new Integer(10)};
		Future<JCL_result> t = jcl.execute("TestFo","test", arg);
		System.out.println("FIM EXEC");
		System.out.println(t.get().getCorrectResult());
		JCLHashMap<Integer,pacuSend> ttt = new JCLHashMap<Integer,pacuSend>("teste","pacuSend",pacuSend.class);
	//	t.get();
//		for(Future<JCL_result> ti:t){
//			System.out.println(ti.get().getCorrectResult());
//		}
		
		
		
//		Object[] arg = new Object[]{new Integer(10),new Integer(30)};
//		List<Future<JCL_result>> t = jcl.executeAllCores("pacuSend","teste1", arg);
//		System.out.println("FIM EXEC");
////		t.get();
//		for(Future<JCL_result> ti:t){
//			System.out.println(ti.get().getCorrectResult());
//		}
		
		
		System.out.println("FIM TUDO");
	}

	public void testeGeral() throws InterruptedException, ExecutionException {
		// TODO Auto-generated constructor stub
		JCL_facade jcl = JCL_FacadeImpl.getInstancePacu();
		System.out.println(jcl.register(pacuSend.class, "pacuSend"));
		Object[] arg = new Object[]{new Integer(10),new Integer(30)};
//		Future<JCL_result> t = jcl.execute("pacuSend","teste1", arg);

		List<Future<JCL_result>> t = jcl.executeAllCores("pacuSend","teste1", arg);
		System.out.println("FIM EXEC");
//		t.get();
		for(Future<JCL_result> ti:t){
			System.out.println(ti.get().getCorrectResult());
		}
		
		
		
		jcl.instantiateGlobalVar("Var1", new Integer(10));
		
		System.out.println(jcl.getValue("Var1").getCorrectResult());
		
		jcl.setValueUnlocking("Var1", new Integer(30));

		System.out.println(jcl.getValue("Var1").getCorrectResult());
		
		
		System.out.println("Fim");
		jcl.destroy();
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
