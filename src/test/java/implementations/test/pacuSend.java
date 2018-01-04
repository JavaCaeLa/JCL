package implementations.test;

import java.util.Arrays;


public class pacuSend{
    public String name;

	public pacuSend(){
	}

	public pacuSend(int type, String b){
//	this.type = type;
	this.name = b;
		System.out.println("Constructor!!!!");
	}
    
	public String teste1(int a, int b){
	System.out.println("Teste Agora!!!");
	return "TESTE FINAL !!!!";
	}
	
	public String[] teste2(String[] as){
	System.out.println(Arrays.toString(as));
	return as;
	}
	
	public String toString(){
		return name;	
	}
}
