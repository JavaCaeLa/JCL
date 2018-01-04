public class Main {
	public static void main(String [] args){
		int [][] matA = {{3 , 2},
						 {3 , 3},
						 {1 , 2}
						};

		int [][] matB = {{3 , 2 , 6},
						 {1 , 2 , 5},
						};

		Matrix A = new Matrix(matA, 3, 2);
		Matrix B = new Matrix(matB, 2, 3);

		SplitMatrices sm = new SplitMatrices(A, B);
		
		System.out.println(sm.getResult());
	}
}
