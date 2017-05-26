package implementations.test;

import java.util.Collections;
import java.util.List;

public class UserServices {
	
	//fibonatti
	public Integer execute(Integer n) throws Exception{
        System.out.println("Teste:"+n);
		if (n <= 1) return 1;
        else return execute(n-1) + execute(n-2);
    }
	
	//PA
	public static Integer execute(Integer a0, Integer an, Integer numElementos){
		System.out.println("Teste:");
		if(numElementos==null) numElementos = new Integer(1);
		Integer soma = (((a0 + an)*(numElementos))/2);
//		UserTeste t = new UserTeste();
//		System.out.println(t.teste("Envio de duas classes no mesmo Jars"));
		return soma;
	}
	
	//sorting
	public List<Integer> ordena(List<Integer> valores){
		Collections.sort(valores);
		return valores;
	}	

}
