package appl.simpleAppl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import commom.Constants;

public class manipulateFile {
	
	public boolean create(){
		try {
			/*to access the correct path on both java_Host or android_Host, we need to use "Constants.Environment.JCLRoot()"
				witch returns "../" for java and the correct path on android */
			String rootPath = Constants.Environment.JCLRoot();
			File mediaStorageDir = new File(rootPath + "/jcl_temp/");
			if (!mediaStorageDir.exists()) {
				if (!mediaStorageDir.mkdirs()) {
					return false;
				}
			}
			FileWriter writer = new FileWriter(new File(rootPath+"/jcl_temp/saida.txt"));
			PrintWriter saida = new PrintWriter(writer); 
			for (int cont = 0 ; cont < 100; cont++){
				saida.println("Saida: "+cont);
			}
			saida.close();
			writer.close();
			return true;
		} catch (IOException e){
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}  				
	}
	
	
	public void printOnHost(){
		
		try {
			String rootPath = Constants.Environment.JCLRoot();
			File mediaStorageDir = new File(rootPath + "/jcl_temp/");
			if (!mediaStorageDir.exists()) {
				if (!mediaStorageDir.mkdirs()) {
					return;
				}
			}
			FileReader reader = new FileReader(rootPath+"/jcl_temp/saida.txt");
			BufferedReader leitor = new BufferedReader(reader); 
			String linha = null;  
			while((linha = leitor.readLine()) != null) {  
			    System.out.println("Line: " + linha);  
			}
			
			leitor.close();  
			reader.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}  
	
	}
}
