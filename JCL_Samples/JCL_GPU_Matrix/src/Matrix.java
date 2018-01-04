
public class Matrix{
	private int [][] values;
	private int n_lines;
	private int n_columns;
	
	public Matrix(int [][] values, int n_lines, int n_columns){
		this.values = values;
		this.n_lines = n_lines;
		this.n_columns = n_columns;
	}
	
	
	public int[][] getValues() {
		return values;
	}
	public void setValues(int[][] values) {
		this.values = values;
	}
	
	public int getValueAt(int i, int j){
		return values[i][j];
	}
	public void setValueAt(int i, int j, int value){
		values[i][j] = value;
	}
	
	public int getNLines() {
		return n_lines;
	}
	public void setNLines(int lines) {
		this.n_lines = lines;
	}
	
	public int getNColumns() {
		return n_columns;
	}
	public void setNColums(int colums) {
		this.n_columns = colums;
	}
	
	public int [] getValueLine(int i){
		return values[i];
	}
	
	public int [] getValueColumn(int j){
		int [] colum = new int[n_lines];
		
		for(int i=0;i<n_lines;i++){
			colum[i] = values[i][j];
		}
		
		return colum;
	}
	
	@Override
	public String toString(){
		String print="\n";
		
		for(int i=0;i<n_lines;i++){
			for(int j=0;j<n_columns;j++){
				print+="" + values[i][j] + " ";
			}
			print+="\n";
		}
		return print;
	}
}
