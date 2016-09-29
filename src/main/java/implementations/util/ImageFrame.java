package implementations.util;

import javax.swing.JFrame;
import javax.swing.JPanel;



public class ImageFrame extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1236208868682146596L;
	
	public ImageFrame(JPanel jPanel){
		super("JCL"); 
		setLayout(null); //define o layout como nulo
		setResizable(true); //define que nao eh possivel redefinir o tamanho
		setFocusable(true);//define que o paninel eh focavel 
		setContentPane(jPanel);//define o fundo de acordo com o objeto criado
	}

}
