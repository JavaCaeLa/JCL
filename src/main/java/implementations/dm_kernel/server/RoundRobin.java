package implementations.dm_kernel.server;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;

public class RoundRobin {
	private static AtomicInteger current = new AtomicInteger(0);
	private static AtomicInteger currentGV = new AtomicInteger(0);
	private static Map<String, Map<String, String>> devices;
//	private static ConcurrentMap<String,String[]> slaves;
//	private static List<String> slavesIDs;
	private static Map<String, String> next;
	public static int core;
	
	public static void ini(Map<String, Map<String, String>> device){
//		slaves = slave;
//		slavesIDs = slavesID;
		devices = device;
		next = devices.get(devices.get(current.get() % devices.size()));		 
		core = Integer.parseInt(next.get("CORE(S)")); 
	}
	
	public static Map<String, String> getNext(){
		return next;
	}
	
	public static void next(){
		next = devices.get(devices.get((current.get()+1) % devices.size()));
		core = Integer.parseInt(next.get("CORE(S)")); 
	}	
	
	public static Map<String, String> getDevice(){				
		Map<String, String> result = devices.get(devices.get(current.incrementAndGet() % devices.size()));
		next();
		return result; 
	}
	
	public static Map<String, String> next(Map<String, Map<String, String>> device){		
		
		Map<String, String> result = device.get(device.get(current.incrementAndGet() % device.size()));
		next();
		return result; 
	}
	
	public static int core(List<String>slavesIDs, Map<String, String[]>slaves){		
		String[] result = slaves.get(slavesIDs.get(current.get() % slavesIDs.size()));
		return Integer.parseInt(result[3]); 
	}
	
	public static String[] nextGV(List<String>slavesIDs, Map<String, String[]>slaves){
		
		if(slavesIDs == null || slaves == null) return null;		
		String[] result = slaves.get(slavesIDs.get(currentGV.incrementAndGet() % slavesIDs.size()));
		//System.err.println("round robin... " + result[0] + result[1]);
		return result; 
	}

}
