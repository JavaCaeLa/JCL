import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.Future;

import implementations.collections.JCLHashMap;
import implementations.dm_kernel.user.JCL_FacadeImpl;
import interfaces.kernel.JCL_facade;
import interfaces.kernel.JCL_result;

public class SplitMatrices {
	
	private JCL_facade jcl = JCL_FacadeImpl.getInstance();
	private List<Future<JCL_result>> tickets = new ArrayList<Future<JCL_result>>();
	private Matrix A;
	private Matrix B;
	
	public SplitMatrices(Matrix A, Matrix B){
		JCL_facade jcl = JCL_FacadeImpl.getInstance();
		jcl.register(JCLMatrixBroker.class, "JCLMatrixBroker");

		if(A.getNColumns() != B.getNLines()){
			System.err.println("Matrizes incompatíveis");
			System.exit(0);
		}
		
		this.A = A;
		this.B = B;
		
		for(int i=0;i<A.getNLines();i++){
			int [] line = A.getValueLine(i);
			for(int j=0;j<B.getNColumns();j++){
				int [] column = B.getValueColumn(j);
				String ij = i+":"+j;
				sendToJCL(line, column, ij);
			}
		}
		
		jcl.getAllResultBlocking(tickets);
		
	}
	
	public Matrix getResult(){
		JCLHashMap<String,Integer> hostResults = new JCLHashMap<String,Integer>("results");
		
		int [][] r = new int[A.getNLines()][];
		for(int i=0;i<A.getNLines();i++) r[i] = new int[B.getNColumns()]; 
		
		for(Entry<String, Integer> e : hostResults.entrySet()){
			String [] pos =  e.getKey().split(":");
			
			int x = Integer.parseInt(pos[0]);
			int y = Integer.parseInt(pos[1]);
			
			r[x][y] = Integer.parseInt(e.getValue().toString());
		}
		return new Matrix(r, A.getNLines(), B.getNColumns());
	}
	
	private void sendToJCL(int [] line, int [] colum, String ij){
		Object [] args = {line, colum, ij};

		Future<JCL_result> ticket = jcl.execute("JCLMatrixBroker", "multiply", args);
		tickets.add(ticket);
	}
}
