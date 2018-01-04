package services;

import android.app.Service;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
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
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import commom.JCL_SensorImpl;
import implementations.collections.JCLHashMap;
import implementations.dm_kernel.IoTuser.JCL_Action;
import implementations.dm_kernel.IoTuser.JCL_Context;
import implementations.dm_kernel.IoTuser.JCL_Expression;
import implementations.dm_kernel.MessageSensorImpl;
import implementations.dm_kernel.user.JCL_FacadeImpl;
import implementations.util.android.ContextResource;
import sensor.JCL_Sensor;

//import jcl.connection.ConnectorImpl;
//import jcl.connection.JCL_message_metadata;
//import jcl.connection.MessageMetadataImpl;

public class JCL_NativeSensorService extends Service implements Runnable, SensorEventListener, sensor.JCL_Gps2.OnSendGps, JCL_ANDROID_Facade.ServiceNativeHandler {
    private SensorManager sensorManager = null;
    private String[] incompatibleSensor;

    public static boolean working = false;
    private String ip;
    private int port;
    //private static boolean ativo;
    private ConcurrentMap<Integer, Map<String, JCL_Context>> mapContext;
    private ConcurrentMap<String, Integer> mapNameContext;
    //private int contextNumber = 0;
    public Map<Integer, String> mapString;
    public Map<Integer, Integer> mapQuantMap;
    private Map<Integer, Integer> mapMaxValues;
    private Map<Integer, Integer> mapMinValues;
    private ConcurrentMap<Integer, Map<Integer, interfaces.kernel.JCL_Sensor>> jclMapSensors;
    private ConcurrentMap<Integer, JCL_Sensor.TypeSensor> convertMap;
    private ContextResource contextResource;
    private MqttClient mqttClient;
    //private boolean ter = false;

    //private int control = 0;



    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        working = true;
        JCL_ANDROID_Facade.getInstance().setServiceNativeHandler(this);
        mapContext = new ConcurrentHashMap<>();
        mapNameContext = new ConcurrentHashMap<>();
        convertMap = createConvertMap();
        contextResource = new ContextResource();
        //convertMap =
        new Thread(this).start();
        new Thread(new ContextConsumer()).start();
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        try {
            Log.e("Service", "Finished");
            JCL_ANDROID_Facade jcl = JCL_ANDROID_Facade.getInstance();


            contextResource.setFinished(true);
            sensorManager.unregisterListener(this);
            if (jcl.getParticipation().get(JCL_Sensor.TypeSensor.TYPE_GPS.id))
                jcl.stopGpsListener(this);
            if (jcl.getParticipation().get(JCL_Sensor.TypeSensor.TYPE_AUDIO.id))
                jcl.stopRecorder(this);
            if (jcl.getParticipation().get(JCL_Sensor.TypeSensor.TYPE_PHOTO.id))
                jcl.stopTakePicture(this);

//        if (JCL_RecorderAudioService.isWorking())
//            jcl.waitTicket("Audio");
//        if (JCL_RecorderPhotoService.isWorking())
//            jcl.waitTicket("Photo");

            JCL_ANDROID_Facade.getInstance().destroyAllVar();
            working = false;
        }catch (Exception e){
            e.printStackTrace();
        }
        super.onDestroy();
    }


    public void run() {
        try {
            sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
            JCL_ANDROID_Facade jcl = JCL_ANDROID_Facade.getInstance();
            incompatibleSensor = jcl.getCompatibleSensor(sensorManager);//createMapSensor(this,sensorManager);
            String[] ipPort = jcl.getIpPort(this);
            ip = ipPort[0];
            port = Integer.parseInt(ipPort[1]);


            Log.e("Ip:Port", ip + ":" + port);
            jcl.createMapSensor(this, sensorManager, this);
            jcl.registerSensors(sensorManager, this);
            jcl.wakeUp("Sensors");
            if (JCL_HostService.isWorking)
                jcl.waitTicket("Host");
            else
                JCL_HostService.isWorking = true;

            if (jcl.isChangingMetadata()) {
                jcl.createMapMetadata(this);
                jcl.setChangingMetadata(false);
            }
            jcl.wakeUp("Meta");
            mqttClient = jcl.getMqttClient();


            //JCL_ANDROID_Facade jcl = JCL_ANDROID_Facade.getInstance();
            Vector<Integer> time = new Vector<Integer>();
            mapMaxValues = new HashMap<>();
            mapMinValues = new HashMap<>();
            jclMapSensors = new ConcurrentHashMap<>();
            mapString = new HashMap<>();
            mapQuantMap = new HashMap<>();
            for (int i = JCL_Sensor.TypeSensor.TYPE_ACCELEROMETER.id; i <= JCL_Sensor.TypeSensor.TYPE_GPS.id; i++) {
                time.add(jcl.getDelayTime().get(i));
            }
            for (int i = JCL_Sensor.TypeSensor.TYPE_ACCELEROMETER.id; i <= JCL_Sensor.TypeSensor.TYPE_GPS.id; i++) {
                if (jcl.getParticipation().get(i)) {
                    mapMaxValues.put(i, 0);
                    mapMinValues.put(i, 0);
                    mapString.put(i,"");
                    mapQuantMap.put(i, 0);
                    jclMapSensors.put(i, new JCLHashMap<Integer, interfaces.kernel.JCL_Sensor>(JCL_ANDROID_Facade.getInstance().getDevice() + i + "_value"));
                    JCL_FacadeImpl.getInstance().instantiateGlobalVar(JCL_ANDROID_Facade.getInstance().getDevice() + i +"_NUMELEMENTS", "0");
                }
            }

            while (working) {
                try {
                    Thread.sleep(1);//min divisor
                    if (!jcl.isStandBySen()) {
                        ConcurrentMap<Integer, JCL_Sensor> mapS = jcl.getMapValueSensor();

                        for (int i = JCL_Sensor.TypeSensor.TYPE_ACCELEROMETER.id; i <= JCL_Sensor.TypeSensor.TYPE_GPS.id; i++) {


//                            if (mapContext.containsKey(i)){
//                                float[] values = (float[]) mapS.get(i).getValue();
//                                for (JCL_Context con: mapContext.get(i).values()){
//                                    con.check(values);
//                                }
//                            }

                            if (jcl.getDelayTime().get(i) != 0) {
                                if (jcl.getParticipation().get(i) && (time.get(i) % jcl.getDelayTime().get(i)) == 0
                                        && mapS.containsKey(i)) {
                                    Log.e("Send", mapS.get(i).getNameSensor());
                                    JCL_Sensor se = mapS.get(i);
                                    interfaces.kernel.JCL_Sensor s = new JCL_SensorImpl();
                                    s.setObject(se.getValue());
                                    s.setTime(System.currentTimeMillis());
                                    s.setDataType(se.getDataType());
                                    //int key = values.keySet().str
                                    if (!JCL_FacadeImpl.getInstance().containsGlobalVar(JCL_ANDROID_Facade.getInstance().getDevice() + i +"_NUMELEMENTS")){
                                        mapMaxValues.put(i, 0);
                                        mapMinValues.put(i, 0);
                                        jclMapSensors.put(i, new JCLHashMap<Integer, interfaces.kernel.JCL_Sensor>(JCL_ANDROID_Facade.getInstance().getDevice() + i + "_value"));
                                        JCL_FacadeImpl.getInstance().instantiateGlobalVar(JCL_ANDROID_Facade.getInstance().getDevice() + i +"_NUMELEMENTS", "0");
                                    }
                                    if ((mapMaxValues.get(i) - mapMinValues.get(i)) > jcl.getSize().get(i)) {
                                        int min = mapMinValues.get(i);
                                        jclMapSensors.get(i).remove(min);
                                        mapMinValues.put(i, ++min);
                                    }
                                    int max = mapMaxValues.get(i);

//                                    if (mapQuantMap.get(i) < 240 && control > 20) {
//                                        Log.e("Vai enviar", "1");
//                                        long inicial = System.currentTimeMillis();
                                    jclMapSensors.get(i).put(max, s);
                                    JCL_FacadeImpl.getInstance().setValueUnlocking(JCL_ANDROID_Facade.getInstance().getDevice() + i +"_NUMELEMENTS", max+"");
                                    sendMqtt(s.getObject(), jcl.getDeviceName()+"/"+se.getNameSensor());
                                    mapMaxValues.put(i, ++max);
                                        //mapString.put(i, mapString.get(i)+(System.currentTimeMillis()-inicial)+"\n");
                                        //Log.e("Enviou", "1");
                                        //mapQuantMap.put(i, mapQuantMap.get(i)+1);
//                                    }else{
//                                        if (control > 20)
//                                            mapQuantMap.put(i, mapQuantMap.get(i)+1);
//                                        control++;
//                                        if (mapQuantMap.get(i) < 240)
//                                            //jclMapSensors.get(i).put(max++, s);
//                                            Log.e("Vai enviar", "0");
//                                        sendMqtt(s.getObject(), se.getType()+""+se.getType());
//                                        Log.e("Enviou", "0");
//                                        //ConnectorImpl
//                                    }
//                                    mapMaxValues.put(i, max);
                                    //jcl.sendReceiveSync(ip, port, mapS.get(i));
                                }
                                time.set(i, (time.get(i) + 1) % jcl.getDelayTime().get(i));
                            } else {
                                jcl.getDelayTime().add(i, 1);
                                //Log.e("Finalizou", mapString.get(i));
                            }
                        }
//                        boolean f = true;
//                        for (Map.Entry<Integer, Integer> en: mapQuantMap.entrySet()){
//                            if (en.getValue()<240 || en.getValue()>244) {
//                                f = false;
//                                break;
//                            }
//                        }
//                        if (f && !ter){
//                            ter = true;
//                            for (Map.Entry<Integer, String> times: mapString.entrySet()){
//                                jcl.recorderTimeTest(times.getValue(), "android-"+jcl.getDelayTime().get(0)+"-"+times.getKey()+"_"+mapString.size());
//                            }
//                            System.out.println("terminou");
//                        }

                    }
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            for (int i = JCL_Sensor.TypeSensor.TYPE_ACCELEROMETER.id; i <= JCL_Sensor.TypeSensor.TYPE_GPS.id; i++) {
                if (jcl.getParticipation().get(i)) {
                    jclMapSensors.get(i).clear();
                    JCL_FacadeImpl.getInstance().deleteGlobalVar(JCL_ANDROID_Facade.getInstance().getDevice() + i + "_NUMELEMENTS");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    public void onSensorChanged(SensorEvent event) {
        Sensor sensor = event.sensor;
        JCL_ANDROID_Facade jcl = JCL_ANDROID_Facade.getInstance();
        ConcurrentMap<Integer, JCL_Sensor> mapS = jcl.getMapValueSensor();

        //Log.e("TYPE_ACCELEROMETER", Arrays.toString(event.values));
        JCL_Sensor.TypeSensor type = convertMap.get(sensor.getType());
        JCL_Sensor jcl_Sensor = new JCL_Sensor(type, event.values);
        mapS.put(type.id, jcl_Sensor);
        jcl.wakeUp(type.id + "");
        if (mapContext != null && mapContext.containsKey(type.id))
            contextResource.putElement(new Object[]{type.id, event.values});
    }

    public void sendLocation(Location location) {
        ConcurrentMap<Integer, JCL_Sensor> mapS = JCL_ANDROID_Facade.getInstance().getMapValueSensor();
        float[] data = {(float) location.getLatitude(), (float) location.getLongitude(), 0};
        JCL_ANDROID_Facade jcl = JCL_ANDROID_Facade.getInstance();
        JCL_Sensor jcl_Sensor = new JCL_Sensor(JCL_Sensor.TypeSensor.TYPE_GPS, data);
        mapS.put(JCL_Sensor.TypeSensor.TYPE_GPS.id, jcl_Sensor);
        jcl.wakeUp(JCL_Sensor.TypeSensor.TYPE_GPS.id + "");
        Log.e("GPS", Arrays.toString(data));

        if (mapContext != null && mapContext.containsKey(JCL_Sensor.TypeSensor.TYPE_GPS.id))
            contextResource.putElement(new Object[]{JCL_Sensor.TypeSensor.TYPE_GPS.id, data});
    }

    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // TODO Auto-generated method stub

    }

    public MessageSensorImpl getSensorNow(int type) {
        ConcurrentMap<Integer, JCL_Sensor> mapS = JCL_ANDROID_Facade.getInstance().getMapValueSensor();
        if (!mapS.containsKey(type))
            JCL_ANDROID_Facade.getInstance().waitTicket(type + "");
        return mapS.get(type).convertToMessage_sensor();
    }

    public boolean createNewTopic(Object obj) {
        JCL_ANDROID_Facade jcl = JCL_ANDROID_Facade.getInstance();
        System.out.println("** creating MQTT Context **");
        Object[] args = (Object[]) obj;
        JCL_Expression exp;
        String expression = String.valueOf(args[0]);
        Integer sensorPin = Integer.parseInt(args[1] + "");
        exp = new JCL_Expression(expression);
        String topicName = String.valueOf(args[2]);


        if (!jcl.getParticipation().get(sensorPin))
            return false;

        JCL_Context topic = new JCL_Context(exp, topicName, true);
        ;

        Map<String, JCL_Context> contexts = null;
        if (mapContext.containsKey(sensorPin)) {
            contexts = mapContext.get(sensorPin);
        } else
            contexts = new HashMap<>();
        contexts.put(topicName, topic);

        mapContext.put(sensorPin, contexts);
        mapNameContext.put(topicName, sensorPin);
        System.out.println(topicName);

        return true;
    }

    public boolean setContext(Object obj) {
        JCL_ANDROID_Facade jcl = JCL_ANDROID_Facade.getInstance();
        System.out.println("** Registering Context **");
        Object[] args = (Object[]) obj;
        JCL_Expression exp;
        String expression = String.valueOf(args[0]);
        Integer sensorPin = Integer.parseInt(args[1] + "");
        exp = new JCL_Expression(expression);
        String nickname = String.valueOf(args[2]);

        if (!jcl.getParticipation().get(sensorPin))
            return false;

        JCL_Context ctx = new JCL_Context(exp, nickname);

        Map<String, JCL_Context> contexts = null;
        if (mapContext.containsKey(sensorPin)) {
            contexts = mapContext.get(sensorPin);
        } else
            contexts = new HashMap<>();
        contexts.put(nickname, ctx);

        mapContext.put(sensorPin, contexts);
        mapNameContext.put(nickname, sensorPin);

        return true;
    }

    public boolean addTaskOnContext(Object obj) {
        System.out.println("** Adding Task On Context **");
        Object[] args = (Object[]) obj;
        String contextNickname = String.valueOf(args[0]);

        JCL_Context ctx = null;

        if (!mapNameContext.containsKey(contextNickname))
            return false;

        ctx = mapContext.get(mapNameContext.get(contextNickname)).get(contextNickname);

        String hostTicketIP = String.valueOf(args[1]),
                hostTicketPort = String.valueOf(args[2]),
                hostTicketMac = String.valueOf(args[3]);
        String superPeerPort = args[4] + "";
        Long ticket = Long.valueOf(args[5] + "");
        boolean b = Boolean.valueOf("" + args[6]);
        String className = String.valueOf(args[7]),
                methodName = String.valueOf(args[8]);
        Object param[] = (Object[]) args[9];

        JCL_Action action = new JCL_Action(b, ticket, hostTicketIP, hostTicketPort, hostTicketMac, superPeerPort, className, methodName, param);
        ctx.addAction(action);
        return true;
    }

    public boolean addActingOnContext(Object obj) {
        System.out.println("** Adding Task On Context **");
        Object[] args = (Object[]) obj;
        String contextNickname = String.valueOf(args[0]);

        JCL_Context ctx = null;

        if (!mapNameContext.containsKey(contextNickname))
            return false;

        ctx = mapContext.get(mapNameContext.get(contextNickname)).get(contextNickname);

        Map.Entry<String, String> deviceNickname = (Map.Entry<String, String>) args[4],
                actuatorNickname = (Map.Entry<String, String>) args[5];
        Object[] commands = (Object[]) args[6];

        JCL_Action action = new JCL_Action(deviceNickname, actuatorNickname, commands);
        ctx.addAction(action);
        return true;
    }

    @Override
    public void removeSensorAction(int id) {
        try {
            for (String name : mapContext.get(id).keySet())
                mapNameContext.remove(name);
            mapContext.remove(id);
        }catch (Exception e){
            Log.e("Removing Sensor", e.getMessage());
        }
    }

    public ConcurrentHashMap<Integer, JCL_Sensor.TypeSensor> createConvertMap() {
        ConcurrentHashMap<Integer, JCL_Sensor.TypeSensor> map = new ConcurrentHashMap<>();
        map.put(Sensor.TYPE_ACCELEROMETER, JCL_Sensor.TypeSensor.TYPE_ACCELEROMETER);
        map.put(Sensor.TYPE_AMBIENT_TEMPERATURE, JCL_Sensor.TypeSensor.TYPE_AMBIENT_TEMPERATURE);
        map.put(Sensor.TYPE_GRAVITY, JCL_Sensor.TypeSensor.TYPE_GRAVITY);
        map.put(Sensor.TYPE_GYROSCOPE, JCL_Sensor.TypeSensor.TYPE_GYROSCOPE);
        map.put(Sensor.TYPE_LIGHT, JCL_Sensor.TypeSensor.TYPE_LIGHT);
        map.put(Sensor.TYPE_LINEAR_ACCELERATION, JCL_Sensor.TypeSensor.TYPE_LINEAR_ACCELERATION);
        map.put(Sensor.TYPE_MAGNETIC_FIELD, JCL_Sensor.TypeSensor.TYPE_MAGNETIC_FIELD);
        map.put(Sensor.TYPE_PRESSURE, JCL_Sensor.TypeSensor.TYPE_PRESSURE);
        map.put(Sensor.TYPE_PROXIMITY, JCL_Sensor.TypeSensor.TYPE_PROXIMITY);
        map.put(Sensor.TYPE_RELATIVE_HUMIDITY, JCL_Sensor.TypeSensor.TYPE_RELATIVE_HUMIDITY);
        map.put(Sensor.TYPE_ROTATION_VECTOR, JCL_Sensor.TypeSensor.TYPE_ROTATION_VECTOR);
        return map;
        //TYPE_GPS = 11;
        //TYPE_AUDIO = 12;
        //TYPE_PHOTO = 13;
    }

    private class ContextConsumer implements Runnable {

        @Override
        public void run() {
            while (!contextResource.isFinished()) {
                Object[] objects = contextResource.getElement();
                if (objects != null && mapContext != null) {
                    //new Object[]{type, event.values
                    try {
                        int sensor = (int) objects[0];
                        float[] values = (float[]) objects[1];
                        for (JCL_Context con : mapContext.get(sensor).values()) {
                            con.check(values);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
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

    public boolean unregisterContext(Object obj) {
        System.out.println("** Unregistering Context **");
        Object[] args = (Object[]) obj;

        String nickname = String.valueOf(args[0]);

        if (mapNameContext.containsKey(nickname)) {
            mapContext.remove(mapNameContext.get(nickname));
            mapNameContext.remove(nickname);
        } else
            return false;

        return true;
    }

    public boolean removeActingOnContext(Object obj) {
        System.out.println("** Removing Acting On Context **");
        Object[] args = (Object[]) obj;
        String contextNickname = String.valueOf(args[0]);

        JCL_Context ctx = null;

        if (!mapNameContext.containsKey(contextNickname))
            return false;

        ctx = mapContext.get(mapNameContext.get(contextNickname)).get(contextNickname);

        Map.Entry<String, String> deviceNickname = (Map.Entry<String, String>) args[4],
                actuatorNickname = (Map.Entry<String, String>) args[5];
        Object[] commands = (Object[]) args[6];
        boolean exists = false;
        Iterator<JCL_Action> it = ctx.getActionList().iterator();
        while (it.hasNext()) {
            JCL_Action act = it.next();
            if (act.isActing() && act.getDeviceNickname().equals(deviceNickname) && act.getActuatorNickname().equals(actuatorNickname) && Arrays.equals(act.getParam(), commands)) {
                it.remove();
                exists = true;
            }
        }

        if (!exists)
            return false;

        return true;
    }

    public boolean removeTaskOnContext(Object obj) {
        System.out.println("** Removing Task On Context **");
        Object[] args = (Object[]) obj;
        String contextNickname = String.valueOf(args[0]);

        JCL_Context ctx = null;

        if (!mapNameContext.containsKey(contextNickname))
            return false;

        ctx = mapContext.get(mapNameContext.get(contextNickname)).get(contextNickname);

        boolean useSensorValue = Boolean.valueOf("" + args[1]);
        String classNickname = (String) args[2];
        String methodName = (String) args[3];
        Object[] commands = (Object[]) args[4];

        boolean exists = false;
        Iterator<JCL_Action> it = ctx.getActionList().iterator();
        while (it.hasNext()) {
            JCL_Action act = it.next();
            System.out.println(act.getClassName().equals(classNickname) + "  " + act.getMethodName().equals(methodName) + "  " + Arrays.equals(act.getParam(), commands) + "  " + (useSensorValue == act.isUseSensorValue()));
            if (act.getClassName().equals(classNickname) && act.getMethodName().equals(methodName) && Arrays.equals(act.getParam(), commands) && useSensorValue == act.isUseSensorValue()) {
                it.remove();
                exists = true;
            }
        }

        if (!exists)
            return false;

        return true;
    }

    public void enableSensor(int JCL_sensorType) {
        JCL_ANDROID_Facade jcl = JCL_ANDROID_Facade.getInstance();
        jcl.enableSensor(JCL_sensorType, sensorManager, this, this, this);
        if (JCL_sensorType <= JCL_Sensor.TypeSensor.TYPE_GPS.id) {
            mapMaxValues.put(JCL_sensorType, 0);
            mapMinValues.put(JCL_sensorType, 0);
            jclMapSensors.put(JCL_sensorType, new JCLHashMap<Integer, interfaces.kernel.JCL_Sensor>(JCL_ANDROID_Facade.getInstance().getDevice() + JCL_sensorType + "_value"));
            JCL_FacadeImpl.getInstance().instantiateGlobalVar(JCL_ANDROID_Facade.getInstance().getDevice() + JCL_sensorType +"_NUMELEMENTS", "0");
        }else{
            jcl.wakeUp("Host");
        }
    }
    public void disableSensor(int JCL_sensorType){
        JCL_ANDROID_Facade jcl = JCL_ANDROID_Facade.getInstance();

        if (JCL_sensorType == JCL_Sensor.TypeSensor.TYPE_PHOTO.id)
            jcl.stopTakePicture(this);
        else if (JCL_sensorType == JCL_Sensor.TypeSensor.TYPE_AUDIO.id)
            jcl.stopRecorder(this);
        else {
            if (JCL_sensorType == JCL_Sensor.TypeSensor.TYPE_GPS.id) {
                jcl.stopGpsListener(this);
            }else {
                sensorManager.unregisterListener(this, jcl.getJCL_Map_Sensor().get(JCL_sensorType));
            }

            jclMapSensors.get(JCL_sensorType).clear();
        }
        JCL_FacadeImpl.getInstance().deleteGlobalVar(JCL_ANDROID_Facade.getInstance().getDevice() + JCL_sensorType + "_NUMELEMENTS");
        jcl.getJCL_Map_Sensor().remove(JCL_sensorType);
    }


}
