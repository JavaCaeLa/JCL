package sensor;

import android.content.Context;
import android.graphics.ImageFormat;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.PictureCallback;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import implementations.util.JCL_ApplicationContext;

@SuppressWarnings("deprecation")
public class JCL_Camera{
    public static final int MEDIA_TYPE_IMAGE = 1;
    public static final int MEDIA_TYPE_VIDEO = 2;
    private Camera mCamera;
    private OnSendPhoto onSendPhoto;
    private static String mPhotoFile;
    private int width, height;
    private static JCL_Camera jcl_camera = null;
    private SurfaceTexture f;



    public interface OnSendPhoto {
        public void sendPhoto(byte[] photo);
    }

    public static JCL_Camera getInstance(OnSendPhoto onSendPhoto){
        if (jcl_camera==null)
            jcl_camera = new JCL_Camera(onSendPhoto);
        return  jcl_camera;
    }



    private JCL_Camera(OnSendPhoto onSendPhoto) {
        this.onSendPhoto = onSendPhoto;
        Object[] resol = getMaxResolution();
        this.width = (int) resol[0];
        this.height = (int) resol[1];
        mCamera = getCameraInstance();
        //prepare();
    }

    private PictureCallback mPicture = new PictureCallback() {
        public synchronized void onPictureTaken(byte[] data, Camera camera) {
            try {
                File pictureFile = getOutputMediaFile(MEDIA_TYPE_IMAGE);
                if (pictureFile == null) {
                    Log.d("JCL_Camera",
                            "Error creating media file, check storage permissions: ");
                    wakeUp();
                    mCamera.stopPreview();
                    if (f!=null)
                        f.release();
                    //mCamera.unlock();
                    return;
                }
                Log.e("Photo", "Taken");
                onSendPhoto.sendPhoto(data);
                //mCamera.unlock();
                mCamera.stopPreview();
                if (f!=null)
                    f.release();
                wakeUp();
            } catch (Exception e) {
                wakeUp();
                e.printStackTrace();
            }

        }
    };

    public synchronized void takePicture() {
        try {
            //if (prepare()) {
            prepare();
            preparePhoto();
                mCamera.takePicture(null, null, mPicture);
                wait();
            //}
        } catch (Exception e) {
            mCamera.stopPreview();
            if (f!=null)
                f.release();
            //restart();
//            mCamera.setPreviewCallback(null);
//            mCamera.stopPreview();
//            mCamera.release();
            wakeUp();
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public synchronized void preparePhoto(){
        mCamera.startPreview();
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * Create a file Uri for saving an image or video
     */
    private static Uri getOutputMediaFileUri(int type) {
        return Uri.fromFile(getOutputMediaFile(type));
    }

    /**
     * Create a File for saving an image or video
     */
    private static File getOutputMediaFile(int type) {
        // To be safe, you should check that the SDCard is mounted
        // using Environment.getExternalStorageState() before doing this.
        File sdCard = Environment.getExternalStorageDirectory();

        File mediaStorageDir = new File(sdCard.getAbsolutePath().toString()
                + "/jclAndroid/");
        // This location works best if you want the created images to be shared
        // between applications and persist after your app has been uninstalled.

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d("MyCameraApp", "failed to create directory");
                return null;
            }
        }

        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss")
                .format(new Date());
        mPhotoFile = timeStamp;
        File mediaFile;
        if (type == MEDIA_TYPE_IMAGE) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator
                    + "IMG_" + timeStamp + ".jpg");
            mPhotoFile += ".jpg";
        } else if (type == MEDIA_TYPE_VIDEO) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator
                    + "VID_" + timeStamp + ".mp4");
        } else {
            return null;
        }

        return mediaFile;
    }

    public Camera getCamera() {
        return mCamera;
    }

    public synchronized static Camera getCameraInstance() {
        Camera c = null;
        try {
            c = Camera.open(); // attempt to get a JCL_Camera instance
        } catch (Exception e) {
            // JCL_Camera is not available (in use or does not exist)
        }
        return c; // returns null if camera is unavailable
    }

//    public synchronized void stop(){
//        mCamera.release();
//    }

    public boolean prepare() {
        try {
            f = new SurfaceTexture(0);

//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1) {
//                f.setDefaultBufferSize(10000, 10000);
//            }
           // SurfaceHolder h = new SurfaceView(JCL_ApplicationContext.getContext()).getHolder();
            //mCamera.setPreviewDisplay(h);
            mCamera.setPreviewTexture(f);
        } catch (IOException e1) {
            Log.e("JCLCAMERA", e1.getMessage());
            return false;
        }

        Parameters params = mCamera.getParameters();
        params.setPreviewSize(width, height);
        params.setPictureSize(width, height);

        params.setJpegQuality(50);
        params.setFlashMode(Parameters.FLASH_MODE_OFF);
        params.setPictureFormat(ImageFormat.JPEG);
        mCamera.setParameters(params);
        return true;
    }

    public synchronized void wakeUp() {
        notifyAll();
    }

//    public void restart() {
//        mCamera.setPreviewCallback(null);
//        mCamera.stopPreview();
//        mCamera.release();
//        mCamera = getCameraInstance();
//        jcl_camera = null;
//        //prepare();
//    }

    private static Object[] getMaxResolutionOnCamera() {
        Camera mCamera = getCameraInstance();
        Camera.Parameters param;
        param = mCamera.getParameters();

        Camera.Size bestSize = null;
        List<Camera.Size> sizeList = mCamera.getParameters().getSupportedPreviewSizes();
        bestSize = sizeList.get(0);
        for (int i = 1; i < sizeList.size(); i++) {
            if ((sizeList.get(i).width * sizeList.get(i).height) > (bestSize.width * bestSize.height)) {
                bestSize = sizeList.get(i);
            }
        }

        List<Integer> supportedPreviewFormats = param.getSupportedPreviewFormats();
        Iterator<Integer> supportedPreviewFormatsIterator = supportedPreviewFormats.iterator();
        while (supportedPreviewFormatsIterator.hasNext()) {
            Integer previewFormat = supportedPreviewFormatsIterator.next();
            if (previewFormat == ImageFormat.YV12) {
                param.setPreviewFormat(previewFormat);
            }
        }

        mCamera.release();
        return  new Object[]{bestSize.width, bestSize.height};
    }
    public static Object[] getMaxResolution(){
        Object[] res = getMaxResolutionFile(JCL_ApplicationContext.getContext());
        if (res!=null)
            return res;
        else{
            res = getMaxResolutionOnCamera();
            setMaxResolutionFile(res, JCL_ApplicationContext.getContext());
            return res;
        }

    }
    private static Object[] getMaxResolutionFile(Context context) {
        try {

            FileInputStream file = context.openFileInput("resolution.jcl");

            ObjectInputStream ois = new ObjectInputStream(file);
            Object[] resolution = (Object[]) ois.readObject();
            file.close();
            ois.close();
            return resolution;

        } catch (Exception e) {
            e.printStackTrace();
            return null;

        }

    }
    private static void setMaxResolutionFile(Object[] resolution, Context context) {
        try {

            FileOutputStream file = context.openFileOutput("resolution.jcl",Context.MODE_PRIVATE);
            ObjectOutputStream oos = new ObjectOutputStream(file);
            oos.writeObject(resolution);
            oos.close();
            file.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public synchronized void release() {
        if (mCamera!=null) {
            mCamera.release();
            mCamera = null;
        }
        jcl_camera = null;
    }

    public void setOnSendPhoto(OnSendPhoto onSendPhoto){
        this.onSendPhoto = onSendPhoto;
    }

}
