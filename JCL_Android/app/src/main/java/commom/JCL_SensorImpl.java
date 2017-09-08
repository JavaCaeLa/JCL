package commom;

import android.app.Activity;
import android.app.AlertDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaDataSource;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Environment;
import android.util.Log;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

//import javax.swing.JFrame;

//import com.sun.media.jfxmedia.MediaPlayer;

import implementations.dm_kernel.IoTuser.JCL_Context;
import implementations.util.ImageFrame;
import implementations.util.ImagePanel;
import implementations.util.JCL_ApplicationContext;
import interfaces.kernel.JCL_Sensor;
import io.protostuff.Tag;
//import sun.audio.AudioData;
//import sun.audio.AudioDataStream;
//import sun.audio.AudioPlayer;
//import javax.sound.sampled.AudioFormat;
//import javax.sound.sampled.AudioInputStream;
//import javax.sound.sampled.AudioSystem;
//import javax.sound.sampled.Clip;
//import javax.sound.sampled.LineUnavailableException;
//import javax.sound.sampled.UnsupportedAudioFileException;



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
		try {
			if (object instanceof byte[] && dataType != null && dataType.equals("jpeg")) {
//			ImageFrame image = new ImageFrame(new ImagePanel((byte[]) object));
//			image.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
//			//determina a resolucao
//			image.setSize( 1280, 960 );
//			//no centro
//			image.setLocationRelativeTo(null);
//			image.setVisible( true );
				try {
					ByteArrayInputStream inputStream = new ByteArrayInputStream((byte[]) object);
					Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
					ImageView picture = new ImageView(JCL_ApplicationContext.getContext());
					picture.setImageBitmap(bitmap);

					//LinearLayout linearLayout = new LinearLayout(JCL_ApplicationContext.getContext());
					final ImageView imageView = new ImageView(JCL_ApplicationContext.getContext());
					LinearLayout.LayoutParams vp =
							new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
									LinearLayout.LayoutParams.WRAP_CONTENT);
					imageView.setLayoutParams(vp);
					imageView.setImageBitmap(bitmap);
					//linearLayout.addView(imageView);


//			Toast toast = new Toast(JCL_ApplicationContext.getContext());
//			toast.setGravity(Gravity.BOTTOM, 0, 200);
//			toast.setDuration(Toast.LENGTH_LONG);
//			toast.setView(imageView);
//			toast.show();
					Activity activity = JCL_ApplicationContext.getActivity();
					activity.runOnUiThread(new Runnable() {
						@Override
						public void run() {
							try {
								AlertDialog.Builder builder = new AlertDialog.Builder(JCL_ApplicationContext.getContext());
								builder.setView(imageView).setNeutralButton("Close", null).show();
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					});
				} catch (Exception e) {
					Log.e("ShowData", e.getMessage());
				}


			} else if (object instanceof byte[] && dataType != null && dataType.equals("3gp")) {

				final String path = Environment.getExternalStorageDirectory().toString() + File.separatorChar + "jclAndroid"
						+ File.separatorChar + System.currentTimeMillis() + ".wav";
				MediaPlayer mPlayer = new MediaPlayer();
				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
					mPlayer.setDataSource(new ByteArrayMediaDataSource((byte[]) object));
				} else {

					try {
						FileOutputStream fout = new FileOutputStream(path);
						fout.write((byte[]) object);
						fout.flush();
						fout.close();
						mPlayer.setDataSource(path);
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				mPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

					@Override
					public void onCompletion(MediaPlayer mp) {
						File file = new File(path);
						if (file.exists())
							file.delete();
						mp.release();
					}
				});
				try {
					mPlayer.prepare();
				} catch (IOException e) {
					e.printStackTrace();
				}
				mPlayer.start();


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


			} else {

				Activity activity = JCL_ApplicationContext.getActivity();
				activity.runOnUiThread(new Runnable() {
					@Override
					public void run() {
						Toast.makeText(JCL_ApplicationContext.getContext(),JCL_SensorImpl.this.toString(),
								Toast.LENGTH_SHORT).show();
					}
				});

				//System.out.println(this.toString());
			}
		}catch (Exception e){
			Log.e("Show data", e.getMessage());
		}
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
