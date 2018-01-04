package main;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.Random;
import java.util.Set;

public final class Random_Number {	
	
	public static void Create1GB(Set<Integer> sementes, String name){
		try{
			//file 1GB = 1.000MB = 1.000.000KB = 1.000.000.000B = 250 milhoes de ints de 32 bits
				
			//numero com intervalo de -1bi a +1bi
			Random r = new Random();
			File f = new File("../"+name+"/"+name+".bin");
			f.getParentFile().mkdirs(); 
			f.createNewFile();
			@SuppressWarnings("resource")
			FileChannel fc = new FileOutputStream(f).getChannel();
						
			Integer[] sementesF = new Integer[1000000];
			sementesF = sementes.toArray(sementesF);			
			int ateQDO= sementesF.length/2 + r.nextInt(sementesF.length/2);			
			System.err.println("gerou" + sementesF.length);
			sementes.clear();
			//gerando somente 100MB
			//para gerar 1GB coloque k<1000
			for(int k=0; k<10; k++){
				ByteBuffer buf = ByteBuffer.allocate(1000000);
				System.err.println("faltam " + (1000-k));
				for(int i=0; i<1000000; i+=4){
					int seed= r.nextInt(ateQDO);
					ByteBuffer b = ByteBuffer.allocate(4).putInt(sementesF[seed].intValue());
					buf.put(b.array());					
				}
				
				buf.flip();
				fc.write(buf);
				fc.force(true);
				buf=null;
			}
			
			fc.close();
		}catch (Exception e){
			e.printStackTrace();
		}
	}
}
