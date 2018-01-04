
import implementations.collections.JCLHashMap;

public class JCLMatrixBroker {
	
	public void multiply(int [] line, int [] colum, String ij){
		
		JCLHashMap<String,Integer> hostResults = new JCLHashMap<String,Integer>("results");
		
		MatrixKernel mk = new MatrixKernel(line, colum);
		
		mk.execute(line.length);
		
		int result = mk.getElement();
		
		hostResults.put(ij, result);
	}
}
