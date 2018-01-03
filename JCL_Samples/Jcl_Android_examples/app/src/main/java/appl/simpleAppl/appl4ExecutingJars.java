package appl.simpleAppl;

import android.os.Environment;
import android.widget.RadioButton;

import implementations.dm_kernel.user.JCL_FacadeImpl;
import interfaces.kernel.JCL_facade;
import interfaces.kernel.JCL_result;

import java.io.File;
import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class appl4ExecutingJars {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		new appl4ExecutingJars();
	}
	

	public appl4ExecutingJars(){
		try {
			Long ini = System.currentTimeMillis();			
			//application with two dependencies
			String rootPath = Environment.getExternalStorageDirectory().toString();
			File[] complexApplJars = {new File(rootPath + "/jcl_useful_jars/sorting.jar")};
						
			JCL_facade javaCaLa = JCL_FacadeImpl.getInstance();
			
			//the name of the class to be executed in the Jar file
			javaCaLa.register(complexApplJars, "Merge_Sort");

			Integer[] array = new Integer[100];
			Random r = new Random(System.currentTimeMillis());
			for (int i=0; i<array.length; i++){
				array[i] = r.nextInt();
			}
						
			//no args
			Future<JCL_result> ticket3 = javaCaLa.execute("Merge_Sort", "sort", array);

			
			
			JCL_result jclr = null;
			try{
				jclr = ticket3.get();
			} catch (InterruptedException | ExecutionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if(jclr.getErrorResult()!=null)
				jclr.getErrorResult().printStackTrace();
			else{
				Integer[] array2 =(Integer[])jclr.getCorrectResult();
				for (Integer i: array2)
					System.err.print(i+", ");
			}
			
			javaCaLa.removeResult(ticket3);

			javaCaLa.destroy();
			System.out.println(System.currentTimeMillis()-ini);
						
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
	}

}
