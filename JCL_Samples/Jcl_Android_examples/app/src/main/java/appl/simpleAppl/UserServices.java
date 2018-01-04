package appl.simpleAppl;

import java.util.Collections;
import java.util.List;

public class UserServices {
	
	//fibonatti
	public Integer execute(Integer n) throws Exception{
        if (n <= 1) return 1;
        else return execute(n-1) + execute(n-2);
    }
	
	//PA
	public static Integer execute(Integer a0, Integer an, Integer numElementos){
		if(numElementos==null) numElementos = new Integer(1);
		Integer soma = (((a0 + an)*(numElementos))/2);
		
		return soma;
	}
	
	//sorting
	public List<Integer> ordena(List<Integer> valores){
		Collections.sort(valores);
		return valores;
	}
	
	public void IoTActionTask(){
		// do any task in Java
		System.out.println("after the context is reached a task is excuted !!");
	}

}
