import com.amd.aparapi.Kernel;

public class MatrixKernel extends Kernel{
	private int[] line;
	private int[] colum;
	private int[] element;
	
	public MatrixKernel(int [] line, int [] column){
		element = new int[line.length];
		this.line = line;
		this.colum = column;
	}
	
	public int getElement(){
		int sum = 0;
		for (int i=0;i<element.length;i++) sum += element[i];
		return sum;
	}

	@Override
	public void run() {
		int i = getGlobalId();
		int sum = 0;
		sum = line[i] * colum[i];
		element[i] = sum;
	}
}
