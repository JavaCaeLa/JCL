package implementations.test;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.Future;
import implementations.collections.JCLHashMap;
import implementations.collections.JCLHashMapPacu;
import implementations.dm_kernel.user.JCL_FacadeImpl;
import interfaces.kernel.JCL_facade;
import interfaces.kernel.JCL_result;

public class MainTesteHashMap {
	public static void main(String[] args) throws IOException {
		new MainTesteHashMap();
	}
	
	public MainTesteHashMap(){		
		testeSemColisao();
	//	testeComColisao();
	//	JCLHashMapPacu.destroy();	
	}

	public void testeSemColisao(){
		Long inicio = System.nanoTime();
			JCLHashMap<String, Set<String>> map = new JCLHashMap<String, Set<String>>("myHashMap");			
			JCL_facade jcl = JCL_FacadeImpl.getInstance();
			List<Future<JCL_result>> tickets = new LinkedList<Future<JCL_result>>();
			jcl.register(Insertion.class, "Insertion");
			String name = "myHashMap";
			Object[] args = {name};
			Future<JCL_result> ticket = jcl.execute("Insertion", "insertionMap", args);
			tickets.add(ticket);
//			Future<JCL_result> ticket1 = jcl.execute("Insertion", "insertionSemColisao");
//			tickets.add(ticket1);
			jcl.getAllResultBlocking(tickets);
			
			int cont = 0;
			for(Entry<String, Set<String>> E:map.entrySet()){
				System.out.println("key:"+E.getKey()+" value:"+E.getValue());
				cont++;
			}
			
			if(cont != 200){
				System.err.println("ERROR: Insercao sem colisao retornou tamaho da HashMap errado" + " " + map.size());
			}

			if(map.size() != 200){
				System.err.println("ERROR: Insercao sem colisao retornou tamaho da HashMap errado" + " " + map.size());
			}
			//JCLHashMapPacu.destroy();	
			
			System.out.println(System.nanoTime()-inicio);
	}
	
	public void testeComColisao(){
		JCLHashMap<String, Set<String>> map = new JCLHashMap<String, Set<String>>("myHashMap1");
		
		JCL_facade jcl = JCL_FacadeImpl.getInstance();
		List<Future<JCL_result>> tickets = new LinkedList<Future<JCL_result>>();
		jcl.register(Insertion.class, "Insertion");
		String name = "myHashMap1";
		Object[] args = {name};
		Future<JCL_result> ticket = jcl.execute("Insertion", "insertionMap", args);
		tickets.add(ticket);
		Future<JCL_result> ticket1 = jcl.execute("Insertion", "insertionComColisao");
		tickets.add(ticket1);
		jcl.getAllResultBlocking(tickets);
		int cont = 0;
		System.out.println("############################### TESTE MAP 2 #################################");

		for(Entry<String, Set<String>> E:map.entrySet()){
			System.out.println("key:"+E.getKey()+" value:"+E.getValue());
			cont++;
		}
		
		if(cont != 100){
			System.err.println("ERROR: Insercao sem colisao retornou tamaho da HashMap errado" + " " + map.size());
		}
		
		if(map.size() != 100){
			System.err.println("ERROR: Insercao com colisao retornou tamaho da HashMap errado"+ " " + map.size());
		}
		//JCLHashMapPacu.destroy();
	}
}

