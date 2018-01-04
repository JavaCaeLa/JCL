package appl.simpleAppl;

import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import implementations.dm_kernel.user.JCL_FacadeImpl;
import interfaces.kernel.JCL_facade;
import interfaces.kernel.JCL_result;
import interfaces.kernel.datatype.Device;


public class appl6 {
	public static void main(String[] args) {
		appl6 app = new appl6();
	}
	
	public appl6() {
		
		// TODO Auto-generated constructor stub
		JCL_facade javaCaLa = JCL_FacadeImpl.getInstance();
		
		//the name of the class to be executed in the Jar file		
		javaCaLa.register(manipulateFile.class, "manipulateFile");
		
		// Get all hosts
		List<Device> hosts = javaCaLa.getDevices();
		
		// random choice one host
		Random gerador = new Random();
        int number = gerador.nextInt(hosts.size());        
        Device host = hosts.get(number);
		
		//no args execute in choice host (Create file on host)
        Future<JCL_result> ticket = javaCaLa.executeOnDevice(host,"manipulateFile", "create");
		
		//wait
        try{
        	ticket.get();
        } catch (InterruptedException | ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		//no args execute in chosen host (Print file content on host)
        Future<JCL_result> ticketPrint = javaCaLa.executeOnDevice(host,"manipulateFile", "printOnHost");
		
		//wait
        try{
        	ticketPrint.get();
        } catch (InterruptedException | ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		//Print chosen host
		System.out.println("Host: "+host);
		
		//close JCL
		javaCaLa.destroy();
		
	}

}
