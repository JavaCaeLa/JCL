package br.com.ufop.exemplo;

import implementations.dm_kernel.user.JCL_FacadeImpl;
import interfaces.kernel.JCL_facade;
import interfaces.kernel.JCL_result;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Future;

import br.com.ufop.jars.Edge;
import br.com.ufop.jars.Vertex;

public class MainDijkstra {

	public List<Vertex> exec(List<String> data, int from, int to){	
		//VAR
		List<Vertex> vert = new ArrayList<Vertex>();
		ByteArrayOutputStream boss = null;
		byte[] encod = null;
		long inicio=0;
		long fim = 0;
		JCL_facade jcl = JCL_FacadeImpl.getInstance();
		//End VAR

		//Load jar
		File[] complexApplJars = {new File("./lib/Dijkstra.jar")};		


		//Register my class
		if (!(jcl.containsTask("Dijkstra"))){			
			System.err.println(jcl.register(complexApplJars, "Dijkstra"));
		}

		//Load graph
		// try{
		//	    FileInputStream fstream = new FileInputStream("./lib/teste4.txt");
		//	          DataInputStream in = new DataInputStream(fstream);
		//	          BufferedReader br = new BufferedReader(new InputStreamReader(in));
		//	          String strLine;
		//	          while ((strLine = br.readLine().replaceAll("\\s+", " ").trim()) != null) {
		//	        	  String[] tokens = strLine.split(" ");
		//	        	  if (tokens.length==1){
		//	        		  for (int i = 0; i<Integer.parseInt(tokens[0]);i++){
		//	        			  vert.add(new Vertex("v"+i));				   
		//	        		  }			   
		//	        	  }else{				   
		//	        		  vert.get(Integer.parseInt(tokens[0])).adjacencies.add(new Edge("v"+tokens[1],Double.parseDouble(tokens[2]))); 
		//	        	  }

		//          }
		//	          in.close();
		//	   	}catch (Exception e){
		//	   	}

		for (int i = 0;i<Integer.parseInt(data.get(0));i++){
			vert.add(new Vertex("v"+i));
		}
		for(int i = 1; i< data.size(); i++){
			String[] tokens = data.get(i).replaceAll("\\s+", " ").trim().split(" ");
			vert.get(Integer.parseInt(tokens[0])).adjacencies.add(new Edge("v"+tokens[1],Double.parseDouble(tokens[2])));			
		}

		//end load graph

		//Create Global VAR


		inicio = System.currentTimeMillis();
		vert.get(from).minDistance = 0.;
		System.out.println(vert.get(0).adjacencies.size());		 
		for(int i=0;i<vert.size();i++){
			try {
				boss = new ByteArrayOutputStream();
				ObjectOutputStream obj_out = new ObjectOutputStream (boss);
				obj_out.writeObject (vert.get(i));
			} catch (IOException er) {
				er.printStackTrace();
			}			  
			encod = boss.toByteArray();		
			if (!jcl.containsGlobalVar("v"+i)){
				jcl.instantiateGlobalVar(("v"+i), encod);
			}else{
				jcl.getValueLocking(("v"+i));
				jcl.setValueUnlocking(("v"+i), encod);	

			}
		}
		fim = System.currentTimeMillis();
		System.out.println("Tempo decorrido var1: "+(fim-inicio));

		//End Create Global VAR


		//Just on one piece
		/*
		  inicio = System.currentTimeMillis();
		  Object[] args1 = {new String("v0"),new Integer(500)};				
		  String ticket = jcl.execute("Dijkstra", "ComputePaths", args1);
		  System.out.println(ticket);
		  jcl.getResultBlocking(ticket);
		  fim = System.currentTimeMillis();
		  System.out.println("Tempo decorrido mapear: "+(fim-inicio));
		 */


		//	/*	
		//VAR
		Vertex source = this.ByteToVertex((byte[])jcl.getValue("v"+from).getCorrectResult());	 
		List<Future<JCL_result>> ticket = new ArrayList<Future<JCL_result>>();

		//End VAR

		Vertex u = source;
		inicio = System.currentTimeMillis();
		// Visit each edge exiting u
		for (int j = 0; j < u.adjacencies.size();j++)
		{ 
			Edge e = u.adjacencies.get(j);
			Vertex v = this.ByteToVertex((byte[])jcl.getValueLocking(e.target).getCorrectResult());
			double weight = e.weight;
			double distanceThroughU = u.minDistance + weight;
			if (distanceThroughU < v.minDistance) {
				v.minDistance = distanceThroughU ;
				v.previous = u;
				jcl.setValueUnlocking(v.toString(), this.VertexToByte(v));
				Object[] args1 = {v.toString(), new Integer(500), new String("v"+from)};	                	
				ticket.add(jcl.execute("Dijkstra", "ComputePaths2", args1));		                	
			} else jcl.setValueUnlocking(v.toString(), this.VertexToByte(v));
		} //End for

		jcl.getAllResultBlocking(ticket);

		fim = System.currentTimeMillis();
		System.out.println("Tempo decorrido mapear: "+(fim-inicio));

		//*/		  

		//		  System.out.println("Distancia: " + path.get(path.size()-1).minDistance);
		//		  System.out.println("Path: " + path);

		return this.getShortestPathTo("v"+to);


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

	public List<Vertex> getShortestPathTo(String encoded)
	{
		//VAR
		Vertex target = null;
		List<Vertex> path = new ArrayList<Vertex>();
		JCL_facade jcl = JCL_FacadeImpl.getInstance();
		//VAR

		target = this.ByteToVertex((byte[])jcl.getValue(encoded).getCorrectResult());

		for (Vertex vertex = target; vertex != null; vertex = vertex.previous){
			path.add(vertex);
		}
		Collections.reverse(path);
		return path;
	}
}
