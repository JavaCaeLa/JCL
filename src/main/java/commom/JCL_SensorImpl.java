package commom;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Arrays;

import javax.swing.JFrame;

//import com.sun.media.jfxmedia.MediaPlayer;

import implementations.util.ImageFrame;
import implementations.util.ImagePanel;
import interfaces.kernel.JCL_Sensor;
import io.protostuff.Tag;
//import sun.audio.AudioData;
//import sun.audio.AudioDataStream;
//import sun.audio.AudioPlayer;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;



public class JCL_SensorImpl implements JCL_Sensor {
	/**
	 * 
	 */
	private static final long serialVersionUID = 4539013136634398910L;
	
	@Tag(1)
	private Object object;

	@Tag(2)
	private long time; 
	
	@Tag(3)
	private String dataType;

	@Override
	public Object getObject() {
		return object;
	}

	@Override
	public void setObject(Object object) {
		// TODO Auto-generated method stub
		this.object = object;
	}

	@Override
	public String getType() {
		if (object instanceof float[])
			return "float array";
		if (object instanceof byte[])
			return "image or audio";
		return object.getClass().getName();
	}
	@Override
	public String toString(){
		if (object instanceof float[])
			return Arrays.toString((float[]) object);
		if (object instanceof byte[] && dataType!=null && dataType.equals("3gp"))
			return "audio";
		else if (object instanceof byte[] && dataType!=null && dataType.equals("jpeg"))
			return "image";
		return object.toString();
	}

	@Override
	public void showData() {
		if (object instanceof byte[] && dataType!=null && dataType.equals("jpeg")){
			ImageFrame image = new ImageFrame(new ImagePanel((byte[]) object));
			image.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
			//determina a resolucao
			image.setSize( 1280, 960 ); 
			//no centro
			image.setLocationRelativeTo(null);  
			image.setVisible( true );
		}else if (object instanceof byte[] && dataType!=null && dataType.equals("3gp")){

//			AudioData audiodata = new AudioData((byte[]) object);
//			AudioDataStream audioStream = new AudioDataStream(audiodata);
//			// Play the sound
//			AudioPlayer.player.start(audioStream);
			
					
//			try {
//			//	audioIn.read((byte[]) object, 0, ((byte[]) object).length);
//				AudioInputStream audioIn = AudioSystem.getAudioInputStream(new ByteArrayInputStream((byte[]) object));
//				Clip clip = AudioSystem.getClip();
//				clip.open(audioIn);
//				clip.start();	
//			} catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
			
			
		}else
			System.out.println(this.toString());
	}

	@Override
	public long getTime() {
		return time;
	}

	@Override
	public void setTime(long time) {
		this.time = time;
		
	}

	@Override
	public void setDataType(String dataType) {
		this.dataType = dataType;
	}

}
