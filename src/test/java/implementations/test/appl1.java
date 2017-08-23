package implementations.test;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import implementations.dm_kernel.CPuser.JCL_CPFacadeImpl;
import implementations.dm_kernel.user.JCL_FacadeImpl;
import interfaces.kernel.JCL_CPfacade;
import interfaces.kernel.JCL_facade;
import interfaces.kernel.JCL_result;


//a developer application just to test JCL 
public class appl1 {

	public static void main(String[] args) {
		new appl1();
	}
	
//	public static void main(String[] args) {
//		// TODO Auto-generated method stub
//		new appl1(); 
//	}
	
	public appl1(){
		
		JCL_facade jcl = JCL_FacadeImpl.getInstance();
		Boolean b = jcl.register(UserServices.class, "UserServices");
		System.err.println(b);
		
		// the useful result of JavaCa&La
		JCL_result jclr =null;
		
			
		//correct and elegant way: another simultaneously or concurrent execution ....
		Object[] args1 ={new Integer(1), new Integer(100), new Integer(10)};
		Future<JCL_result> ticket = jcl.execute("UserServices", args1);
		
		
		List<Integer> l = new LinkedList<Integer>();
		l.add(new Integer("10"));
		l.add(new Integer("1"));
		l.add(new Integer("14"));
		l.add(new Integer("100"));
		l.add(new Integer("56"));
		l.add(new Integer("12"));
		l.add(new Integer("4"));
		l.add(new Integer("103"));
		l.add(new Integer("11"));
		l.add(new Integer("44"));
		
		Object[] args2 ={l};
		
		//nth execution ....and as many as you want !!
		Future<JCL_result> ticket1 = jcl.execute("UserServices", "ordena", args2);
		
		try {
			jclr = ticket1.get();
		} catch (InterruptedException | ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(jclr.getCorrectResult()!=null) System.err.println("sorted is: " + jclr.getCorrectResult().toString());
		else jclr.getErrorResult().printStackTrace();
		
				
		//eu uso como quero o resultado acima
		
		try {
			
			System.out.println("Aki");
			jclr = ticket.get();
			System.out.println("Aki2");
			
			JCL_CPfacade jclCP = JCL_CPFacadeImpl.getInstance(); 
	
			
	
			System.out.println(jclCP.getExecutionTime(ticket1));
			System.out.println(jclCP.getHostTime(ticket1));
			
			jcl.removeResult(ticket);
			jcl.removeResult(ticket1);
//			ticket.get();
			
		} catch (InterruptedException | ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		if(jclr.getCorrectResult()!=null)
			System.err.println("pa is: " + jclr.getCorrectResult().toString());
		else jclr.getErrorResult().printStackTrace();
		
		
		
		jcl.destroy();
		
	}

}
