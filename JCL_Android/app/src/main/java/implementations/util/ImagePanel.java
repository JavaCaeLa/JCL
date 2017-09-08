package implementations.util;

//import java.awt.Graphics;
//import java.awt.Graphics2D;
//import java.awt.Rectangle;
//import java.awt.TexturePaint;
//import java.awt.geom.Rectangle2D;
//import java.awt.image.BufferedImage;
//import java.io.ByteArrayInputStream;
//import java.io.IOException;
//
//import javax.imageio.ImageIO;
//import javax.swing.JPanel;

public class ImagePanel {//extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1561589406741950521L;
	
	
//	private Rectangle2D recttangle2D; //desenha o retangulo do fundo
//	private BufferedImage bufferedImage;//cria uma imagem que possa ser manipulada
//
//
//	public Rectangle2D getRecttangle2D() {
//		return recttangle2D;
//	}
//
//	public void setRecttangle2D(Rectangle2D recttangle2d) {
//		recttangle2D = recttangle2d;
//	}
//
//	public BufferedImage getBufferedImage() {
//		return bufferedImage;
//	}
//
//	public void setBufferedImage(BufferedImage bufferedImage) {
//		this.bufferedImage = bufferedImage;
//	}
//
//
//
//	public ImagePanel(byte[] bytes) {//construtor
//		BufferedImage img = null;
//		try {
//			img = ImageIO.read(new ByteArrayInputStream(bytes));
//		} catch (IOException e1) {
//			System.err.println("ImagePanel error in ImagePanel(byte[] bytes)");
//			e1.printStackTrace();
//		}
//		try {
//			setLayout(null);//seta o layout do jpanel para null
//			setBufferedImage(img);
//			//desenha um novo retangulo de acordo com a resoluco passada
//			setRecttangle2D(new Rectangle(0, 0, 808, 606));
//		} catch (Exception e) {
//			System.err.println("ImagePanel error in ImagePanel(byte[] bytes)");
//			e.printStackTrace();
//		}
//	}
//	@Override
//	public void paintComponent(Graphics g) {//desenha o componente na dela
//		//ciar um retangulo de acordo com o tamanho da tela
//		setRecttangle2D(new Rectangle(0,0,this.getWidth(),this.getHeight()));
//
//		//perenche uma forma com a textura especificada no BufferedImage
//		TexturePaint texturePaint = new TexturePaint(getBufferedImage(), getRecttangle2D());
//
//		//renderizaco da imagem
//		Graphics2D graphics2D = (Graphics2D) g;
//		graphics2D.setPaint(texturePaint);
//		graphics2D.fillRect(0, 0, this.getWidth(), this.getHeight());
//	}

}
