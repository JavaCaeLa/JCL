package br.com.ufop.exemplo;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import implementations.dm_kernel.user.JCL_FacadeImpl;
import interfaces.kernel.JCL_facade;
import interfaces.kernel.JCL_result;


public class MainKnapsack {

	private List<boolean[]> best = new ArrayList<boolean[]>();


	/**
	 * Create JCL interface and solve the problem
	 */
	public List<Item> exec(List<String> data,int capacity,int div) {
		//VAR
		System.out.println("capacity: "+capacity+" div: "+div);
		JCL_facade jcl = JCL_FacadeImpl.getInstance();
		JCL_result jclr = null;
		Item[] itemList;    
		int max = Integer.MIN_VALUE;
		int bestresult = 0;
		int size = (int) (Math.log(div)/Math.log(2));
		List<Future<JCL_result>> ticket = new ArrayList<Future<JCL_result>>();
		itemList = new Item[data.size()];
		//End VAR

		System.out.println("teste: "+((int) (Math.log(div)/Math.log(2))));

		//Load jar
		File[] complexApplJars = {new File("./lib/Knapsack.jar")};	

		//Register my class
		if (!(jcl.containsTask("Knapsack"))){			
			System.err.println(jcl.register(complexApplJars, "Knapsack"));
		}

		//Generate list
		//    Random rng = new Random();

		//    System.out.println(" Item Weight Value");
		//    for (int i = 0; i < n; i++) { 
		//      int w = 1 + rng.nextInt(capacity/2);
		//      int v = 1 + rng.nextInt(500);
		//      itemList[i] = new Item(w, v);
		//      System.out.printf("%4d %5d %5d\n", i, w, v);

		//    }// End generate

		//Load data    
		for(int i = 0; i< data.size(); i++){
			String[] tokens = data.get(i).replaceAll("\\s+", " ").trim().split(" ");
			int w = Integer.parseInt(tokens[1]);
			int v = Integer.parseInt(tokens[2]);
			itemList[i] = new Item(w, v);
			System.out.printf("%4d %5d %5d\n", i, w, v);			
		}
		//End Load data



		//Execute Knapsack brute force solve	
		for (int i = 0; i<div;i++){
			boolean[] mask = new boolean[size];
			int j = mask.length-1;
			while ((mask[j]) && (j > 0)){ 
				mask[j] = false; 
				j--;
			}
			if (i!=0) {mask[j] = true;}

			System.out.println("valor de i:"+i+"Valor do array: "+Arrays.toString(mask));
			Object[] args1 = {capacity,ItemToByte(itemList),mask};	
			ticket.add(jcl.execute("Knapsack", "solve", args1));
		}


		//Recover the result    
		try {
			for (int i = 0; i<div;i++){
				int inter;
				jclr = ticket.get(i).get();    	
				inter=this.printSolution(itemList, (boolean[]) jclr.getCorrectResult());
				if (inter>max){
					max =inter; 
					bestresult = i;
				}
			}
		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
		}
		return returnValue(itemList,best.get(bestresult));
	}

	/**
	 * Prints only the items whose entries in "solution" are marked "true"
	 */
	public int printSolution(Item[] itemList,boolean[] solution) {
		int bestSoFar = 0;
		best.add(solution);
		System.out.println(" Item Weight Value");
		for (int i = 0; i < itemList.length; i++) {
			if (solution[i]) {
				System.out.printf("%4d %5d %5d\n", i, itemList[i].weight(), 
						itemList[i].value());
				bestSoFar+=itemList[i].value();
			}
		}
		System.out.println("Best value: "+bestSoFar);
		return bestSoFar;
	}


	/**
	 * Create the final result
	 */
	public List<Item> returnValue(Item[] itemList,boolean[] solution){
		List<Item> result = new ArrayList<Item>();
		for (int i = 0; i < itemList.length; i++) {
			if (solution[i]) {
				result.add(itemList[i]);
			}

		}
		return result;
	}

	/**
	 * Convert object Item to Array of Byte
	 */
	public byte[] ItemToByte(Item[] serial){

		//VAR   
		ByteArrayOutputStream boss = null;
		//End VAR

		try {
			boss = new ByteArrayOutputStream();
			ObjectOutputStream obj_out = new ObjectOutputStream (boss);
			obj_out.writeObject (serial);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return boss.toByteArray();
	}

}
