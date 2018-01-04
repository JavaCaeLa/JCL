package main;

import implementations.collections.JCLHashMap;
import implementations.dm_kernel.user.JCL_FacadeImpl;
import interfaces.kernel.JCL_facade;
import interfaces.kernel.JCL_result;
import it.unimi.dsi.fastutil.ints.Int2LongMap;
import it.unimi.dsi.fastutil.ints.Int2LongOpenHashMap;
import it.unimi.dsi.fastutil.ints.IntAVLTreeSet;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;

import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;

public class Main {	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		new Main();
	}	
	public Main(){
		long time = System.nanoTime();
		MersenneTwisterFast r = new MersenneTwisterFast();
		IntSet sementes = new IntOpenHashSet();
		while(sementes.size()<1000000)			
			sementes.add(r.nextInt());				
		JCL_facade jcl = JCL_FacadeImpl.getInstance();
		
		int numJCLclusterCores = jcl.getClusterCores();
		//registering
		jcl.register(Random_Number.class, "Random_Number");
		
		//builds the input data, partitioned over JCL cluster		
		Object[][] args = new Object[numJCLclusterCores][];
		for(int i=0;i<numJCLclusterCores;i++) {
			Object[] oneArg = {sementes, "output"+i};
			args[i]= oneArg;
		}
		List<Future<JCL_result>> tickets = jcl.executeAllCores("Random_Number", "Create1GB", args);
		jcl.getAllResultBlocking(tickets);
		for(Future<JCL_result> aTicket:tickets) jcl.removeResult(aTicket);
		tickets.clear();
		tickets=null;
		System.err.println("Time to create input (sec): " + (System.nanoTime()-time)/1000000000);
		//sementes.clear();
		//sementes = null;		
		
		//done!! all inputs built
		//reads the input to calculate cardinality of the input as well as 
		//the frequency of each item in the input
		//each JCL thread will handle a task
		
		time=System.nanoTime();
		jcl.register(Sorting.class, "Sorting");
		Object[][] args1 = new Object[numJCLclusterCores][];
		
		
		//mount each argument for each method call
		for(int i=0;i<numJCLclusterCores;i++){
			Object[] oneMethodArgs = {i,"output"+i, numJCLclusterCores};
			args1[i]=oneMethodArgs;
		}
		//executes a method on each JCL cluster thread
		List<Future<JCL_result>>  tickets1= jcl.executeAllCores("Sorting", "phase1", args1);
		//synchronization barrier in JCL
		//obtains all results from previous executions
		List<JCL_result> results = jcl.getAllResultBlocking(tickets1);
		for(Future<JCL_result> oneR:tickets1)jcl.removeResult(oneR);
		String schema = buildInputDataPartitionSchema(results, numJCLclusterCores);
		System.err.println("Time to phase1 (sec): " + (System.nanoTime()-time)/1000000000);
				
		//mount arguments to phase 2
		time = System.nanoTime();
		
		for(int i=0; i<numJCLclusterCores;i++){
			Object[] oneMethodArgument ={i, numJCLclusterCores, schema};
			args[i] = oneMethodArgument;
		}	
		
		
		//create global maps, one for each JCL thread
		for(int i=0;i<numJCLclusterCores;i++)
			new JCLHashMap<Integer, Map<Integer, Long>>(String.valueOf(i));
				
		
		//submits the schema to all threads to start partition their inputs
		tickets1= jcl.executeAllCores("Sorting", "phase2", args);
		results = jcl.getAllResultBlocking(tickets1);
		
		for(Future<JCL_result> oneR:tickets1) jcl.removeResult(oneR);
		System.err.println("Time to phase 2 + jcl global maps creation (sec): " + (System.nanoTime()-time)/1000000000);
				
		//starts phase 3 - the final pipe
		time= System.nanoTime();
		
		for(int i=0; i<numJCLclusterCores;i++){
			Object[] oneMethodArgument ={i};
			args[i] = oneMethodArgument;
		}	
		tickets1= jcl.executeAllCores("Sorting", "phase3", args);
		results = jcl.getAllResultBlocking(tickets1);
		
		for(Future<JCL_result> oneR:tickets1)jcl.removeResult(oneR);		
		System.err.println("Time to phase 3 (sec): " + (System.nanoTime()-time)/1000000000);
				
		//validation per JCL Host, not per JCL thread
		time= System.nanoTime();
		int numJCLMachines = jcl.getDevices().size();
		
		for(int i=0; i<numJCLMachines;i++){
		
			Object[] oneMethodArgument ={"output", numJCLclusterCores};;
			args[i] = oneMethodArgument;
		
		}	
		
		tickets1= jcl.executeAll("Sorting", "phase4", args);
		results = jcl.getAllResultBlocking(tickets1);
		
		
		for(Future<JCL_result> oneR:tickets1)jcl.removeResult(oneR);			
		for(JCL_result rr:results)
			if(((int)rr.getCorrectResult())>0) System.err.println("ERROR IN VALIDATING!!" + (int)rr.getCorrectResult());
		System.err.println("Time to phase 4 (sec): " + (System.nanoTime()-time)/1000000000);
		
		tickets1.clear();
		tickets1=null;
		jcl.cleanEnvironment();
		jcl.destroy();
	}
	
	private String buildInputDataPartitionSchema(List<JCL_result> r, int numOfJCLThreads){
		
		IntSet sorted = new IntAVLTreeSet();
		long totalF=0;
		Int2LongMap map = new Int2LongOpenHashMap();
		
		for(JCL_result oneR:r){
			
			try{
				@SuppressWarnings("unchecked")
				List<String> l = (List<String>) oneR.getCorrectResult();
				
				for(String s : l){
					String[] args = s.split(":");
					int key = Integer.parseInt(args[0]); long freq = Long.parseLong(args[1]);
					sorted.add(key);
			
					if(map.containsKey(key)){
						freq+=map.get(key);	
						totalF+=map.get(key);
					} else totalF+=freq;
					
					map.put(key, freq);
				}
				
			}catch(Exception e){}
		}
		
		long load=0; int b; String result = "";
		for(int ac:sorted){
			load += map.get(ac);
			if(load > (totalF/(numOfJCLThreads))){					
				b=ac;
				result += b + ":";				
				load=0;
			}	
		}		
		
		return result;
	}

}
