package sensor;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.media.MediaRecorder;
import android.os.Environment;
import android.util.Log;

public class JCL_RecorderAudio {
	private static final String LOG_TAG = "AudioRecordTest";
    private static String mFileName = null;
    private String mAudioName = null;
    private MediaRecorder mRecorder = null;
    private boolean recording = false;

    private void onRecord() {
        if (!recording) {
        	recording = true;
            startRecording();
        } else {
        	recording = false;
            stopRecording();
        }
    }
    
    private void startRecording() {
        mRecorder = new MediaRecorder();
        mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.AAC_ADTS);
        mRecorder.setOutputFile(mFileName+"/"+mAudioName);
        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);

        try {
            mRecorder.prepare();
        } catch (IOException e) {
        	e.printStackTrace();
            Log.e(LOG_TAG, "prepare() failed");
        }

        mRecorder.start();
    }

    private void stopRecording() {
        mRecorder.stop();
        mRecorder.release();
        mRecorder = null;
    }

    public JCL_RecorderAudio(){
    	File sdCard = Environment.getExternalStorageDirectory();
        File mediaStorageDir = new File(sdCard.getAbsolutePath().toString()
				+ "/jclAndroid/audio/");
		if (!mediaStorageDir.exists()) {
			if (!mediaStorageDir.mkdirs()) {
				Log.d("MyCameraApp", "failed to create directory");
				mFileName = mediaStorageDir.getAbsolutePath().toString();
				Log.e("File", mFileName);
			}
		}else
			mFileName = mediaStorageDir.getAbsolutePath().toString();
    }

    
    public void release() {
        if (mRecorder != null) {
            mRecorder.release();
            mRecorder = null;
        }
    }
    
    public byte[] getAudio(){
    	File f = new File(mFileName+"/"+mAudioName);
    	try {
			FileInputStream fi = new FileInputStream(f);
			int length = (int) f.length();
			byte[] file = new byte[length];
			fi.read(file);
			fi.close();
			return file;
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
    	
    }
    public byte[] recorderAudio(long time){
    	mAudioName = new SimpleDateFormat("yyyyMMdd_HHmmss")
		.format(new Date());
    	mAudioName+=".aac";
    	onRecord();
    	try {
			Thread.sleep(time);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	onRecord();
    	return getAudio();
    }
    
    public String getNameFile(){
    	return mAudioName;
    }
    
}
