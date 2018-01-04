package services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.hpc.jcl_android.JCL_ANDROID_Facade;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.MqttPersistenceException;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

import commom.JCL_SensorImpl;
import implementations.collections.JCLHashMap;
import implementations.dm_kernel.MessageSensorImpl;
import implementations.dm_kernel.user.JCL_FacadeImpl;
import interfaces.kernel.JCL_facade;
import sensor.JCL_Camera;
import sensor.JCL_Sensor;

public class JCL_RecorderPhotoService extends Service implements Runnable,
        sensor.JCL_Camera.OnSendPhoto, JCL_ANDROID_Facade.ServicePhotoHandler {
    private JCL_Camera jcl_Camera;
    public static boolean isWorking = false;
    private byte[] data;
    String print = "";
    int quant = 0;

    //private PowerManager.WakeLock wakeLock;
    private Thread thread;
    private MqttClient mqttClient;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
//        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
//        wakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK
//                | PowerManager.ACQUIRE_CAUSES_WAKEUP, "whatever2");
//        if (!wakeLock.isHeld())
//            wakeLock.acquire();


        Log.e("Camera", "Starting");
        thread = new Thread(this);
        thread.start();
        JCL_ANDROID_Facade.getInstance().setServicePhotoHandler(this);

        return START_STICKY;

    }


    @Override
    public void onDestroy() {
        //thread.stop();
        super.onDestroy();
        JCL_ANDROID_Facade.getInstance().recorderTimeTest(print, "android-500"+"-"+JCL_Sensor.TypeSensor.TYPE_PHOTO.id+"_"+"4");
        //JCL_Camera.getInstance(this).release();

        JCL_ANDROID_Facade.getInstance().wakeUp("Photo");
    }


    public void run() {
        JCL_ANDROID_Facade jcl = JCL_ANDROID_Facade.getInstance();
        Log.e("Camera", "wating");
        jcl.wakeUp("camera");
        isWorking = true;
        if (JCL_HostService.isWorking)
            jcl.waitTicket("Host");
        Log.e("Camera", "Starting now");

        mqttClient = jcl.getMqttClient();
        try {
            JCL_FacadeImpl.getInstance().instantiateGlobalVar(JCL_ANDROID_Facade.getInstance().getDevice() + JCL_Sensor.TypeSensor.TYPE_PHOTO.id +"_NUMELEMENTS", 0);
            JCLHashMap<Integer, interfaces.kernel.JCL_Sensor> values = new JCLHashMap<Integer, interfaces.kernel.JCL_Sensor>(JCL_ANDROID_Facade.getInstance().getDevice() + JCL_Sensor.TypeSensor.TYPE_PHOTO.id + "_value");
            int min = 0, max = 0;
            JCL_facade f = JCL_FacadeImpl.getInstance();
            jcl_Camera = JCL_Camera.getInstance(this);
            //jcl_Camera.prepare();
            quant++;
            while (isWorking) {
                try {
                    if (!jcl.isStandBySen()) {
                        Log.e("Camera", quant+"");
                        byte[] da = takePicture();
                        if (da != null) {
                            interfaces.kernel.JCL_Sensor s = new JCL_SensorImpl();
                            s.setObject(da);
                            s.setTime(System.currentTimeMillis());
                            s.setDataType("jpeg");
                            //s.showData();
                            //int key = values.keySet().str
                            if (!JCL_FacadeImpl.getInstance().containsGlobalVar(JCL_ANDROID_Facade.getInstance().getDevice() + JCL_Sensor.TypeSensor.TYPE_PHOTO.id +"_NUMELEMENTS")){
                                max = 0;
                                min = 0;
                                values = new JCLHashMap<Integer, interfaces.kernel.JCL_Sensor>(JCL_ANDROID_Facade.getInstance().getDevice() + JCL_Sensor.TypeSensor.TYPE_PHOTO.id + "_value");
                                JCL_FacadeImpl.getInstance().instantiateGlobalVar(JCL_ANDROID_Facade.getInstance().getDevice() + JCL_Sensor.TypeSensor.TYPE_PHOTO.id +"_NUMELEMENTS", "0");
                            }
                            if ((max-min)>jcl.getSize().get(JCL_Sensor.TypeSensor.TYPE_PHOTO.id)){
                                values.remove(min++);
                            }
//                            if (quant < 240) {
//                                long ini = System.nanoTime();
                                values.put(max, s);
                                sendMqtt(s.getObject(), jcl.getDevice()+"/"+ "TYPE_PHOTO");
                            JCL_FacadeImpl.getInstance().setValueUnlocking(JCL_ANDROID_Facade.getInstance().getDevice() + JCL_Sensor.TypeSensor.TYPE_PHOTO.id +"_NUMELEMENTS",max++);
//                                print += (TimeUnit.NANOSECONDS.toMillis(System.nanoTime()-ini))+"|"+da.length+" B\n";
//                                quant++;
//                            }else{
//                                System.out.println("Acabou foto");
//                                values.put(max++, s);
//                            }


//                            if (total){
//                                values.remove(min++);
//                            }
                            //if (values.)

                            //JCL_Sensor jcl_Sensor = new JCL_Sensor(JCL_Sensor.TYPE_PHOTO, da, "jpeg");


                            //jcl.sendReceiveSync(jcl.getServerIp(), jcl.getServerPort(), jcl_Sensor);
                        }
                        try {
                            Integer d = jcl.getDelayTime().get(JCL_Sensor.TypeSensor.TYPE_PHOTO.id) == 0 ? 1 : jcl.getDelayTime().get(JCL_Sensor.TypeSensor.TYPE_PHOTO.id);
                            Thread.sleep(d);
                        } catch (InterruptedException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            jcl_Camera.release();
            jcl_Camera = null;
            values.clear();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    public void sendPhoto(byte[] photo) {
        data = photo;
    }

    @Override
    public MessageSensorImpl getSensorNow() {
        byte[] d = takePicture();
        if (d != null) {
            JCL_Sensor jcl_Sensor = new JCL_Sensor(JCL_Sensor.TypeSensor.TYPE_PHOTO, d, "jpeg");
            return jcl_Sensor.convertToMessage_sensor();
        } else {
            MessageSensorImpl m = new MessageSensorImpl();
            m.setType(27);
            m.setDataType("Error");
            return m;
        }
    }

    public synchronized byte[] takePicture() {
//        if (jcl_Camera==null)
//            jcl_Camera = JCL_Camera.getInstance(this);
        jcl_Camera.setOnSendPhoto(this);
        jcl_Camera.takePicture();
        if (data != null) {
            Log.e("size photo", ((data.length) / 1024) + "");
            return data;
        }
        return null;

    }

    public void sendMqtt(Object value, String sensorAlias) {
        try {
            if (mqttClient == null)
                return;
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            ObjectOutputStream os = new ObjectOutputStream(out);

            os.writeObject(value);


            MqttMessage message = new MqttMessage((out.toByteArray()));
            message.setQos(2);

            if (mqttClient.isConnected())
                mqttClient.publish(sensorAlias, message);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (MqttPersistenceException e) {
        } catch (MqttException e) {
            e.printStackTrace();
        }

    }
}
