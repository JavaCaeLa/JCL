package br.com.ufop.jars;

import implementations.dm_kernel.user.JCL_FacadeImpl;
import interfaces.kernel.JCL_facade;
import interfaces.kernel.JCL_result;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class Dijkstra {

	public void ComputePaths(String ini,int steps){
		JCL_facade jcl = JCL_FacadeImpl.getInstancePacu();		 
		System.out.println("nome a ser verificado 1 "+ini);
		if (!(jcl.containsTask("Dijkstra"))){
			File[] complexApplJars = {new File("../user_jars/Dijkstra.jar")};
			jcl.register(complexApplJars, "Dijkstra");
		}


		//VAR
		Vertex source = this.ByteToVertex((byte[])jcl.getValue(ini).getCorrectResult());	 
		List<Future<JCL_result>> ticket = new ArrayList<Future<JCL_result>>();
		Vertex dif;

		//End VAR

		Vertex u = source;

		// Visit each edge exiting u
		for (int j = 0; j < u.adjacencies.size();j++)
		{ 
			Edge e = u.adjacencies.get(j);
			Vertex v = this.ByteToVertex((byte[])jcl.getValue(e.target).getCorrectResult());
			double weight = e.weight;
			double distanceThroughU = u.minDistance + weight;
			//System.out.println("Valor Fora de j: "+j+" Valor da Distanvia: "+distanceThroughU+"<"+v.minDistance);
			if (distanceThroughU < v.minDistance) {
				System.out.println("Valor Dentro de j: "+j+" Valor da Distanvia: "+distanceThroughU+"<"+v.minDistance);
				dif = this.ByteToVertex((byte[]) jcl.getValueLocking(v.toString()).getCorrectResult());
				if (distanceThroughU<dif.minDistance){
					v.minDistance = distanceThroughU ;
					v.previous = u;
					jcl.setValueUnlocking(v.toString(), this.VertexToByte(v));

				} else{
					System.out.println("Problema!!!!"+"Min v:"+v.minDistance+" Min dif: "+dif.minDistance+" Novo valor: "+distanceThroughU);
					jcl.setValueUnlocking(v.toString(), this.VertexToByte(dif));
				}
				Object[] args1 = {v.toString(), steps, ini};	                	
				ticket.add(jcl.execute("Dijkstra", "ComputePaths3", args1));		                	
				System.out.println("tickets: "+ticket.get(ticket.size()-1)+" Valor de i: "+j+" Valor do v: "+v.toString());
			}
		} //End for

		try {
			for(int i = 0; i<ticket.size();i++){
				System.out.println("Valor de i: "+i+" de: "+ticket.size());
				ticket.get(i).get();		
				System.out.println("Valor Fim de i: "+i+" de: "+ticket.size());
			}
		} catch (InterruptedException | ExecutionException e) {
			System.err.println("ERROR ON RETRIVING RESULT");
			e.printStackTrace();
		}
		System.out.println("Fim verificado 1 "+ini);		
	}

	public void ComputePaths2(String ini,int steps, String son){

		//VAR
		JCL_facade jcl = JCL_FacadeImpl.getInstancePacu();
		Vertex source =this.ByteToVertex((byte[])jcl.getValue(ini).getCorrectResult());	
		Vertex dif;
		System.out.println("Nome ComputePaths2 INI: "+ini+" Filho de: "+son);  		 

		//End VAR
		PriorityQueue<Vertex> vertexQueue = new PriorityQueue<Vertex>();
		vertexQueue.add(source);

		while ((!vertexQueue.isEmpty())) {
			Vertex u = vertexQueue.poll();
			// Visit each edge exiting u
			for (int j = 0; j < u.adjacencies.size();j++)
			{ 

				Edge e = u.adjacencies.get(j);
				Vertex v = this.ByteToVertex((byte[])jcl.getValue(e.target).getCorrectResult());
				double weight = e.weight;
				double distanceThroughU = u.minDistance + weight;
				if (distanceThroughU < v.minDistance) {
					vertexQueue.remove(v);
					System.out.println("lock: "+v.toString());
					dif = this.ByteToVertex((byte[]) jcl.getValueLocking(v.toString()).getCorrectResult());
					if(distanceThroughU<dif.minDistance){
						v.minDistance = distanceThroughU ;
						v.previous = u;
						System.out.println("tUnlock:"+v.toString());
						jcl.setValueUnlocking(v.toString(), this.VertexToByte(v));
					} else{
						System.out.println("Problema!!!!"+"Min v:"+v.minDistance+" Min dif: "+dif.minDistance+" Novo valor: "+distanceThroughU);
						jcl.setValueUnlocking(v.toString(), this.VertexToByte(dif));
					}
					vertexQueue.add(v);
				}                
			} //End for
		}
	}

	public int ComputePaths3(String ini,int steps, String son){

		//VAR
		JCL_facade jcl = JCL_FacadeImpl.getInstancePacu();
		Vertex source = this.ByteToVertex((byte[])jcl.getValue(ini).getCorrectResult());	 
		List<Future<JCL_result>> ticket = new ArrayList<Future<JCL_result>>();
		Vertex dif;
		int cont = 0;
		//End VAR

		System.out.println("nome a ser verificado 3 "+ini+" Filho de:"+son);
		if (!(jcl.containsTask("Dijkstra"))){
			File[] complexApplJars = {new File("../user_jars/Dijkstra.jar")};
			jcl.register(complexApplJars, "Dijkstra");
		}

		PriorityQueue<Vertex> vertexQueue = new PriorityQueue<Vertex>();
		vertexQueue.add(source);
		while ((!vertexQueue.isEmpty())) {
			Vertex u = vertexQueue.poll();
			// Visit each edge exiting u
			for (int j = 0; j < u.adjacencies.size();j++)
			{ 
				Edge e = u.adjacencies.get(j);
				Vertex v = this.ByteToVertex((byte[])jcl.getValue(e.target).getCorrectResult());
				double weight = e.weight;
				double distanceThroughU = u.minDistance + weight;

				if (distanceThroughU < v.minDistance) {

					vertexQueue.remove(v);

					dif = this.ByteToVertex((byte[]) jcl.getValueLocking(v.toString()).getCorrectResult());
					if(distanceThroughU<dif.minDistance){
						v.minDistance = distanceThroughU ;
						v.previous = u;
						jcl.setValueUnlocking(v.toString(), this.VertexToByte(v));
					}else{
						System.out.println("Problema!!!!"+"Min v:"+v.minDistance+" Min dif: "+dif.minDistance+" Novo valor: "+distanceThroughU);
						jcl.setValueUnlocking(v.toString(), this.VertexToByte(dif));	
					}

					//	if (cont==steps){
					//    	Object[] args1 = {new String(v.toString()),steps,ini};				
					//   	ticket.add(jcl.execute("Dijkstra", "ComputePaths3", args1));
					//	}else{
					vertexQueue.add(v);
					//	}
				}
			} //End for

			cont++;
			if (cont==(steps+1))cont=0;
		}

		jcl.getAllResultBlocking(ticket);
		System.out.println("Fim verificado 3 "+ini);
		return cont;
	}

	public void ComputePaths4(String ini,int steps, String son){

		//VAR
		JCL_facade jcl = JCL_FacadeImpl.getInstancePacu();
		Vertex source =this.ByteToVertex((byte[])jcl.getValue(ini).getCorrectResult());	
		Vertex dif;
		System.out.println("Nome ComputePaths2 INI: "+ini+" Filho de: "+son);  		 

		//End VAR
		PriorityQueue<Vertex> vertexQueue = new PriorityQueue<Vertex>();
		vertexQueue.add(source);

		while ((!vertexQueue.isEmpty())) {
			Vertex u = vertexQueue.poll();
			// Visit each edge exiting u
			for (int j = 0; j < u.adjacencies.size();j++)
			{ 

				Edge e = u.adjacencies.get(j);
				Vertex v = this.ByteToVertex((byte[])jcl.getValue(e.target).getCorrectResult());
				double weight = e.weight;
				double distanceThroughU = u.minDistance + weight;
				if (distanceThroughU < v.minDistance) {
					vertexQueue.remove(v);
					System.out.println("lock: "+v.toString());
					dif = this.ByteToVertex((byte[]) jcl.getValueLocking(v.toString()).getCorrectResult());
					if(distanceThroughU<dif.minDistance){
						v.minDistance = distanceThroughU ;
						v.previous = u;
						System.out.println("tUnlock:"+v.toString());
						jcl.setValueUnlocking(v.toString(), this.VertexToByte(v));
					} else{
						System.out.println("Problema!!!!"+"Min v:"+v.minDistance+" Min dif: "+dif.minDistance+" Novo valor: "+distanceThroughU);
						jcl.setValueUnlocking(v.toString(), this.VertexToByte(dif));
					}
					vertexQueue.add(v);
				}                
			} //End for
		}
	}

	public Vertex ByteToVertex(byte[] serial){

		//VAR   
		ObjectInputStream obj_in = null;
		Vertex source = null;
		//End VAR

		ByteArrayInputStream bis = new ByteArrayInputStream(serial);
		try {
			obj_in = new ObjectInputStream (bis);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		try {
			source = (Vertex) obj_in.readObject();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return source;
	}

	public byte[] VertexToByte(Vertex serial){

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
