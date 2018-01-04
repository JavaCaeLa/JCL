package appl.simpleAppl;

import implementations.dm_kernel.user.JCL_FacadeImpl;
import interfaces.kernel.JCL_facade;
import interfaces.kernel.JCL_result;

import java.io.File;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class appl4ExecutingJars {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		appl4ExecutingJars appl1 = new appl4ExecutingJars();
	}
	
	@SuppressWarnings("unchecked")
	public appl4ExecutingJars(){
		try {
			Long ini = System.currentTimeMillis();			
			//application with two dependencies
			File[] complexApplJars = {new File("../user_jars/myChartAppl.jar"), new File("../user_jars/jcommon-1.0.17.jar"), new File("../user_jars/jfreechart-1.0.14.jar")};
						
			JCL_facade javaCaLa = JCL_FacadeImpl.getInstance();
			
			//the name of the class to be executed in the Jar file
			javaCaLa.register(complexApplJars, "BarExample2");
						
			//no args
			Future<JCL_result> ticket3 = javaCaLa.execute("BarExample2", null);
			Future<JCL_result> ticket4 = javaCaLa.execute("BarExample2", null);
//			List<Future<JCL_result>> ticket5 = javaCaLa.executeAll("BarExample2", null);
			
			
			JCL_result jclr = null;
			try{
				jclr = ticket4.get();
			} catch (InterruptedException | ExecutionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if(jclr.getErrorResult()!=null)
				jclr.getErrorResult().printStackTrace();
			else System.err.println(jclr.getCorrectResult().toString());
			
			javaCaLa.removeResult(ticket4);
			
						
			jclr = null;
			try{
				jclr = ticket3.get();
			} catch (InterruptedException | ExecutionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if(jclr.getErrorResult()!=null)
				jclr.getErrorResult().printStackTrace();
			else System.err.println(jclr.getCorrectResult().toString());
			
			javaCaLa.removeResult(ticket3);
			javaCaLa.destroy();
			System.out.println(System.currentTimeMillis()-ini);
						
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
	}
	
	public appl4ExecutingJars(boolean f){
		try {
			Long ini = System.currentTimeMillis();			
			//application with two dependencies
			System.out.println("teste1");
			JCL_facade javaCaLa = JCL_FacadeImpl.getInstance();
			System.out.println("teste2");
			
			//the name of the class to be executed in the Jar file
			System.out.println(javaCaLa.register(BarExample2.class, "BarExample2"));			
			//no args
			Future<JCL_result> ticket3 = javaCaLa.execute("BarExample2", null);
			Future<JCL_result> ticket4 = javaCaLa.execute("BarExample2", null);
			
			
			JCL_result jclr = null;
			try{
				jclr = ticket4.get();
			} catch (InterruptedException | ExecutionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if(jclr.getErrorResult()!=null)
				jclr.getErrorResult().printStackTrace();
			else System.err.println(jclr.getCorrectResult().toString());
			
			javaCaLa.removeResult(ticket4);
			
						
			jclr = null;
			try{
				jclr = ticket3.get();
			} catch (InterruptedException | ExecutionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if(jclr.getErrorResult()!=null)
				jclr.getErrorResult().printStackTrace();
			else System.err.println(jclr.getCorrectResult().toString());
			
			javaCaLa.removeResult(ticket3);
			javaCaLa.destroy();
			System.out.println(System.currentTimeMillis()-ini);
						
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
	}

}
