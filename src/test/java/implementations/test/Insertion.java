package implementations.test;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import implementations.collections.JCLHashMap;

public class Insertion {
	
	public void insertionMap(String name){		
		JCLHashMap<String, Set<String>> map = new JCLHashMap<String, Set<String>>(name);
//		if(map.size() != 0){	
//			Map<String ,Set<String>> mapLocal = new HashMap<String ,Set<String>>();
//			
//			String key;
//			String va = "abcd";
//			for(int i = 0; i < 10; i++){
//					 key = Integer.toString(i);	         
//					Set<String> value = map.getLock(key);
//	                value.add(va);                
//		            mapLocal.put(key, value);               
//	               	value = null;
//	               	key = null;
//				}
//	
//			map.putAll(mapLocal);
//		}else{
			Map<String ,Set<String>> mapLocal = new HashMap<String ,Set<String>>();
			
			String key;
			String va = "abcd";
			for(int i = 0; i < 100; i++){
					 key = Integer.toString(i);	         
					Set<String> value = new TreeSet<String>();
	                value.add(va);                
		            mapLocal.put(key, value);               
	               	value = null;
	               	key = null;
				}
	
			map.putAll(mapLocal);
			System.out.println("local"+mapLocal.size());
	//		System.out.println("putall"+map.size());		
	//	}
		
		System.out.println("putall"+map.size());		
	}
	
	public void insertionSemColisao(){
		JCLHashMap<String, Set<String>> map = new JCLHashMap<String, Set<String>>("myHashMap");
		
		for(int i = 100; i < 200; i++){
			String key = Integer.toString(i);
			String va = "efg";
			
			Set<String> value = new TreeSet<String>();
            value.add(va);
            
            map.putUnlock(key, value);
           
            value = null;
           	key = null;
		}	
		//System.out.println("put"+map.size());	
	}

	public void insertionComColisao(){
		JCLHashMap<String, Set<String>> map = new JCLHashMap<String, Set<String>>("myHashMap1");
		//System.out.println("Aqui");
		if(map.size() != 0){
			for(int i = 0; i < 100; i++){
				String key = Integer.toString(i);
				String va = "efg";
				
				Set<String> value = map.getLock(key);
	            value.add(va);
	            
	            map.putUnlock(key, value);
	           
	            value = null;
	           	key = null;
			}
		}else{
			for(int i = 0; i < 100; i++){
				String key = Integer.toString(i);
				String va = "efg";
				
				Set<String> value = new TreeSet<String>();
	            value.add(va);
	            
	            map.put(key, value);
	           
	            value = null;
	           	key = null;
			}
		}
		//System.out.println("put"+map.size());	
	}
}
