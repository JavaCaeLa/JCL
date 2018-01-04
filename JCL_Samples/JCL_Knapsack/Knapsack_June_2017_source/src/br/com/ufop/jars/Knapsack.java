package br.com.ufop.jars;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.Arrays;

import br.com.ufop.exemplo.Item;;

public class Knapsack {

	/**
	   *  
	   * Solve a knapsack problem
	   */
	  public boolean[] solve(int capacity, byte[] serial,boolean[] mask) {
		  
		  System.out.println("Running Solve...");
		  Item[] itemList = this.ByteToItem(serial);
		  System.out.println("Item:"+itemList.length);
		  System.out.println("Max:"+mask.length);
		  boolean[] solution = new boolean[itemList.length];		  		  
		  boolean[] current = new boolean[itemList.length];
		  int bestSoFar = Integer.MIN_VALUE;
		  
		  for(int i =0; i < mask.length;i++){
			  current[i]=mask[i];
		  }

		  for(int i=0;i< (Math.pow(2,(itemList.length)-mask.length));i++){

			  int j = itemList.length-1;  
			  int wt = 0;
			  int val = 0;
	      
			  while ((current[j]) && (j > 0)){ 
				  current[j] = false; 
				  j--;
			  }
			  if (i!=0) {current[j] = true;}
			  
	      
	      for (int k = 0; k < itemList.length; k++) {

	        if (current[k]) {
	          wt += itemList[k].weight();
	          val += itemList[k].value();
	        }
	      }

	      // Check to see if we've got a better solution: 
	      if (wt <= capacity && val > bestSoFar) {
	    	  bestSoFar = val;
	        solution = current.clone();
	      }
		  }
		  System.out.println("Melhor ate agora:"+bestSoFar);
		  return solution;		  
	  }

	  /**
	   * Convert Array of Byte in to a Item object
	   */
	public Item[] ByteToItem(byte[] serial){
			
			//VAR   
			ObjectInputStream obj_in = null;
			Item[] source = null;
			//End VAR
			
			   ByteArrayInputStream bis = new ByteArrayInputStream(serial);
			   try {
				obj_in = new ObjectInputStream (bis);
			   } catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			   }
			   		   		   
				try {
					source = (Item[]) obj_in.readObject();
				} catch (ClassNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		return source;
		}
	
	}