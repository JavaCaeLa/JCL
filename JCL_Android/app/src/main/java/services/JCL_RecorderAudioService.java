package services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.hpc.jcl_android.JCL_ANDROID_Facade;

import org.eclipse.paho.client.mqttv3.MqttClient;

import commom.JCL_SensorImpl;
import implementations.collections.JCLHashMap;
import implementations.dm_kernel.MessageSensorImpl;
import implementations.dm_kernel.user.JCL_FacadeImpl;
import sensor.JCL_RecorderAudio2;
import sensor.JCL_Sensor;

public class JCL_RecorderAudioService extends Service implements Runnable, JCL_ANDROID_Facade.ServiceAudioHandler {
    private JCL_RecorderAudio2 jcl_RecorderAudio2;
    private Thread thread;
    private MqttClient mqttClient;
    public static boolean isWorking = false;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        thread = new Thread(this);
        thread.start();
        JCL_ANDROID_Facade.getInstance().setServiceAudioHandler(this);

        return START_STICKY;
    }


    @Override
    public void onDestroy() {
        //thread.stop();
        super.onDestroy();

        JCL_ANDROID_Facade.getInstance().wakeUp("Audio");
    }


    public void run() {
        JCL_ANDROID_Facade jcl = JCL_ANDROID_Facade.getInstance();
        jcl.wakeUp("Audio");
        isWorking = true;
        if (JCL_HostService.isWorking)
            jcl.waitTicket("Host");

        mqttClient = jcl.getMqttClient();
        try {
            jcl_RecorderAudio2 = new JCL_RecorderAudio2();
            JCL_FacadeImpl.getInstance().instantiateGlobalVar(JCL_ANDROID_Facade.getInstance().getDevice() + JCL_Sensor.TypeSensor.TYPE_AUDIO.id +"_NUMELEMENTS", "0");
            JCLHashMap<Integer, interfaces.kernel.JCL_Sensor> values = new JCLHashMap<Integer, interfaces.kernel.JCL_Sensor>(JCL_ANDROID_Facade.getInstance().getDevice() + JCL_Sensor.TypeSensor.TYPE_AUDIO.id + "_value");
            int min = 0, max = 0;
            while (isWorking) {
                if (!jcl.isStandBySen()) {
                    Integer d0 = jcl.getTimeRecorder() == 0 ? 1 : jcl.getTimeRecorder();
                    d0 *= 1000;
                    Log.e("au", "vai gravar");

                    byte[] audio = jcl_RecorderAudio2.recorderAudio(d0 + 1000);
                    //Log.e("au", "vai gravou");
                    //JCL_Sensor jcl_SensorFile = new JCL_Sensor(JCL_Sensor.TYPE_AUDIO, audio, "3gp");
                    //jcl.sendReceiveSync(jcl.getServerIp(), jcl.getServerPort(), jcl_SensorFile);
                    interfaces.kernel.JCL_Sensor s = new JCL_SensorImpl();
                    s.setObject(audio);
                    s.setTime(System.currentTimeMillis());
                    s.setDataType("3gp");
                    //s.showData();
                    //int key = values.keySet().str
                    if (!JCL_FacadeImpl.getInstance().containsGlobalVar(JCL_ANDROID_Facade.getInstance().getDevice() + JCL_Sensor.TypeSensor.TYPE_AUDIO.id +"_NUMELEMENTS")){
                        max = 0;
                        min = 0;
                        values = new JCLHashMap<Integer, interfaces.kernel.JCL_Sensor>(JCL_ANDROID_Facade.getInstance().getDevice() + JCL_Sensor.TypeSensor.TYPE_AUDIO.id + "_value");
                        JCL_FacadeImpl.getInstance().instantiateGlobalVar(JCL_ANDROID_Facade.getInstance().getDevice() + JCL_Sensor.TypeSensor.TYPE_AUDIO.id +"_NUMELEMENTS", "0");
                    }
                    if ((max-min)>jcl.getSize().get(JCL_Sensor.TypeSensor.TYPE_AUDIO.id)){
                        values.remove(min++);
                    }
                    values.put(max, s);
                    JCL_FacadeImpl.getInstance().setValueUnlocking(JCL_ANDROID_Facade.getInstance().getDevice() + JCL_Sensor.TypeSensor.TYPE_AUDIO.id +"_NUMELEMENTS", max++);


                    try {
                        Integer d = jcl.getDelayTime().get(JCL_Sensor.TypeSensor.TYPE_AUDIO.id) == 0 ? 1 : jcl.getDelayTime().get(JCL_Sensor.TypeSensor.TYPE_AUDIO.id);
                        //d *= 1000;
                        Thread.sleep(d);
                    } catch (InterruptedException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            }
            values.clear();
        } catch (Exception e) {

        }

    }


    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    @Override
    public MessageSensorImpl getSensorNow(long length) {
        JCL_ANDROID_Facade jcl = JCL_ANDROID_Facade.getInstance();
        long d0=0;
        if (length==0)
            d0= jcl.getTimeRecorder() == 0 ? 1 : jcl.getTimeRecorder();
        else
            d0=length;

        d0 *= 1000;
        Log.e("au","vai gravar");
        byte[] audio = jcl_RecorderAudio2.recorderAudio(d0 + 1000);
        Log.e("au","vai gravou");
        JCL_Sensor jcl_SensorFile = new JCL_Sensor(JCL_Sensor.TypeSensor.TYPE_AUDIO, audio, "3gp");
        return jcl_SensorFile.convertToMessage_sensor();
    }


}
