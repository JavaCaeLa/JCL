package implementations.test;

import java.util.HashMap;
import java.util.Map;

import implementations.collections.JCLHashMap;

public class PutAllconc {
	
	public void execconc(Integer v){
		
		Map<Integer, Integer> Nmap = new HashMap<Integer, Integer>();
		
		Map<Integer, Integer> teste = new JCLHashMap<Integer, Integer>("testeMap");
		
		for(int cont = 1;cont<=15;cont++){
			Nmap.put((cont+v), (cont+v));
		}
		
		teste.putAll(Nmap);
//		System.out.println("Fim valor:"+v);
	}

}
