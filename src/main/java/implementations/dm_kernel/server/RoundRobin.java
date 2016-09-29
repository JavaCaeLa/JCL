package implementations.dm_kernel.server;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;

public class RoundRobin {
	private static AtomicInteger current = new AtomicInteger(0);
	private static AtomicInteger currentGV = new AtomicInteger(0);
	private static ConcurrentMap<String,String[]> slaves;
	private static List<String> slavesIDs;
	private static String[] next;
	public static int core;
	
	public static void ini(ConcurrentMap<String,String[]> slave, List<String> slavesID){
		slaves = slave;
		slavesIDs = slavesID;
		next = slaves.get(slavesIDs.get(current.get() % slavesIDs.size()));		 
		core = Integer.parseInt(next[3]); 
	}
	
	public static String[] getNext(){
		return next;
	}
	
	public static void next(){
		next = slaves.get(slavesIDs.get((current.get()+1) % slavesIDs.size()));
		core = Integer.parseInt(next[3]); 
	}	
	
	public static String[] next(List<String>slavesIDs, Map<String, String[]>slaves){		
		String[] result = slaves.get(slavesIDs.get(current.incrementAndGet() % slavesIDs.size()));
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
