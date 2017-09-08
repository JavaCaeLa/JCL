package com.hpc.jcl_android;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.LocationManager;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;
import android.util.Log;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;
import java.net.NetworkInterface;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import implementations.collections.JCLHashMap;
import implementations.dm_kernel.ConnectorImpl;
import implementations.dm_kernel.IoTuser.JCL_Context;
import implementations.dm_kernel.MessageMetadataImpl;
import implementations.dm_kernel.MessageSensorImpl;
import implementations.dm_kernel.user.JCL_FacadeImpl;
import implementations.util.JCL_ApplicationContext;
import implementations.util.android.AndroidSensor;
import interfaces.kernel.JCL_message_bool;
import jcl.connection.ConnectionAsync;
import jcl.connection.ConnectionAsync2;
import jcl.connection.Synchronous;
import sensor.JCL_Gps2;
import sensor.JCL_Sensor;
import services.JCL_HostService;
import services.JCL_RecorderAudioService;
import services.JCL_RecorderPhotoService;


public class JCL_ANDROID_Facade {
    private List<JCL_Sensor.TypeSensor> sensors;
//    private Vector<Integer> sensorsCode;
//    private Vector<Integer> jclSensorsCode;
    //private Vector<Integer> indexSensorsCode;
    private Vector<Integer> time;
    private Vector<Boolean> participation;
    private Vector<Boolean> compatibleSensorVec;
    private Vector<Integer> size;
    private static JCL_ANDROID_Facade jcl_ANDROID_Facade = new JCL_ANDROID_Facade();
    private Map<Integer, Sensor> JCL_Map_Sensor;
    //    private JCL_Camera jclCamera;
    private JCL_Gps2 jcl_Gps;
    //    private JCL_RecorderAudio jcl_RecorderAudio;
    private String ip;
    private int serverPort;
    private int myPort;

    private Integer timeRecorder;
    private String device;
    private Map<String, Synchronous> synchronousMap;
    private Map<String, String> metadata;
    private ServiceHandler serviceHandler;
    private ServiceNativeHandler serviceNativeHandler;
    private ServicePhotoHandler servicePhotoHandler;
    private ServiceAudioHandler serviceAudioHandler;
    private boolean changingMetadata = false;
    private boolean standBySen = false;
    private ConcurrentMap<Integer, JCL_Sensor> mapValueSensor;
    private String terminal;
    private MqttClient mqttClient;
    private String deviceName;

    public static JCL_ANDROID_Facade getInstance() {
        return jcl_ANDROID_Facade;
    }

    private JCL_ANDROID_Facade() {
        sensors = new ArrayList<>();
//        sensorsCode = new Vector<Integer>();
//        jclSensorsCode = new Vector<Integer>();
//        indexSensorsCode = new Vector<Integer>();
        time = new Vector<Integer>();
        participation = new Vector<>();
        size = new Vector<>();
        synchronousMap = new HashMap<>();
        compatibleSensorVec = new Vector<>();
        mapValueSensor = new ConcurrentHashMap<>();
        setTerminal("");

        sensors.add(JCL_Sensor.TypeSensor.TYPE_ACCELEROMETER);
        sensors.add(JCL_Sensor.TypeSensor.TYPE_AMBIENT_TEMPERATURE);
        sensors.add(JCL_Sensor.TypeSensor.TYPE_GRAVITY);
        sensors.add(JCL_Sensor.TypeSensor.TYPE_GYROSCOPE);
        sensors.add(JCL_Sensor.TypeSensor.TYPE_LIGHT);
        sensors.add(JCL_Sensor.TypeSensor.TYPE_LINEAR_ACCELERATION);
        sensors.add(JCL_Sensor.TypeSensor.TYPE_MAGNETIC_FIELD);
        sensors.add(JCL_Sensor.TypeSensor.TYPE_PRESSURE);
        sensors.add(JCL_Sensor.TypeSensor.TYPE_PROXIMITY);
        sensors.add(JCL_Sensor.TypeSensor.TYPE_RELATIVE_HUMIDITY);
        sensors.add(JCL_Sensor.TypeSensor.TYPE_ROTATION_VECTOR);
        sensors.add(JCL_Sensor.TypeSensor.TYPE_GPS);
        sensors.add(JCL_Sensor.TypeSensor.TYPE_AUDIO);
        sensors.add(JCL_Sensor.TypeSensor.TYPE_PHOTO);

        //Classe com o nome dos sensores
//        sensors.add("TYPE_ACCELEROMETER");
//        sensors.add("TYPE_AMBIENT_TEMPERATURE");
//        sensors.add("TYPE_GRAVITY");
//        sensors.add("TYPE_GYROSCOPE");
//        sensors.add("TYPE_LIGHT");
//        sensors.add("TYPE_LINEAR_ACCELERATION");
//        sensors.add("TYPE_MAGNETIC_FIELD");
//        sensors.add("TYPE_PRESSURE");
//        sensors.add("TYPE_PROXIMITY");
//        sensors.add("TYPE_RELATIVE_HUMIDITY");
//        sensors.add("TYPE_ROTATION_VECTOR");
//
//        sensors.add("TYPE_GPS");
//        sensors.add("TYPE_AUDIO");
//        sensors.add("TYPE_PHOTO");
//
//        //classe com o codigo dos JCL_sensores
//        jclSensorsCode.add(JCL_Sensor.TypeSensor.TYPE_ACCELEROMETER.id);
//        jclSensorsCode.add(JCL_Sensor.TypeSensor.TYPE_AMBIENT_TEMPERATURE.id);
//        jclSensorsCode.add(JCL_Sensor.TypeSensor.TYPE_GRAVITY.id);
//        jclSensorsCode.add(JCL_Sensor.TypeSensor.TYPE_GYROSCOPE.id);
//        jclSensorsCode.add(JCL_Sensor.TypeSensor.TYPE_LIGHT.id);
//        jclSensorsCode.add(JCL_Sensor.TypeSensor.TYPE_LINEAR_ACCELERATION.id);
//        jclSensorsCode.add(JCL_Sensor.TypeSensor.TYPE_MAGNETIC_FIELD.id);
//        jclSensorsCode.add(JCL_Sensor.TypeSensor.TYPE_PRESSURE.id);
//        jclSensorsCode.add(JCL_Sensor.TypeSensor.TYPE_PROXIMITY.id);
//        jclSensorsCode.add(JCL_Sensor.TypeSensor.TYPE_RELATIVE_HUMIDITY.id);
//        jclSensorsCode.add(JCL_Sensor.TypeSensor.TYPE_ROTATION_VECTOR.id);
//
//        jclSensorsCode.add(JCL_Sensor.TypeSensor.TYPE_GPS.id);
//        jclSensorsCode.add(JCL_Sensor.TypeSensor.TYPE_AUDIO.id);
//        jclSensorsCode.add(JCL_Sensor.TypeSensor.TYPE_PHOTO.id);
//
//        sensorsCode.add(Sensor.TYPE_ACCELEROMETER);
//        sensorsCode.add(Sensor.TYPE_AMBIENT_TEMPERATURE);
//        sensorsCode.add(Sensor.TYPE_GRAVITY);
//        sensorsCode.add(Sensor.TYPE_GYROSCOPE);
//        sensorsCode.add(Sensor.TYPE_LIGHT);
//        sensorsCode.add(Sensor.TYPE_LINEAR_ACCELERATION);
//        sensorsCode.add(Sensor.TYPE_MAGNETIC_FIELD);
//        sensorsCode.add(Sensor.TYPE_PRESSURE);
//        sensorsCode.add(Sensor.TYPE_PROXIMITY);
//        sensorsCode.add(Sensor.TYPE_RELATIVE_HUMIDITY);
//        sensorsCode.add(Sensor.TYPE_ROTATION_VECTOR);
//
//        sensorsCode.add(JCL_Sensor.TypeSensor.TYPE_GPS.id);
//        sensorsCode.add(JCL_Sensor.TypeSensor.TYPE_AUDIO.id);
//        sensorsCode.add(JCL_Sensor.TypeSensor.TYPE_PHOTO.id);

    }

    public void recorderProperties(Context context, String properties, String type) {
        try {

            File sdCard = Environment.getExternalStorageDirectory();

            File mediaStorageDir = new File(sdCard.getAbsolutePath().toString()
                    + "/jclAndroid/");
            // This location works best if you want the created images to be shared
            // between applications and persist after your app has been uninstalled.

            // Create the storage directory if it does not exist
            if (!mediaStorageDir.exists()) {
                if (!mediaStorageDir.mkdirs()) {
                    Log.d("MyCameraApp", "failed to create directory");
                    return;
                }
            }
            PrintWriter writer = new PrintWriter(mediaStorageDir.getPath() + "/jcl." + type + ".properties", "UTF-8");
            writer.print(properties);
            writer.close();
        } catch (Exception e) {
            Log.e("saveToInternalStorage()", e.getMessage());

        }

    }

    public String recoverStringProperties(String type) {

        BufferedReader br = null;
        try {
            String rootPath = Environment.getExternalStorageDirectory().toString()+"/jclAndroid";
            String sCurrentLine, properties = "";

            br = new BufferedReader(new FileReader(rootPath + "/jcl." + type + ".properties"));

            while ((sCurrentLine = br.readLine()) != null) {
                properties += sCurrentLine + "\n";
            }
            br.close();
            return properties;

        } catch (Exception e) {
            return null;
        } finally {
            try {
                if (br != null)
                    br.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }

    }

    private Properties recoverProperties(Context context, String type) {

        BufferedReader br = null;
        try {


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

            if (new File(mediaStorageDir.getPath() + "/jcl." + type + ".properties").exists()) {
                FileInputStream in = new FileInputStream(mediaStorageDir.getPath() + "/jcl." + type + ".properties");
                Properties properties = new Properties();
                properties.load(in);
                in.close();
                return properties;
            } else
                return null;
        } catch (Exception e) {
            return null;
        } finally {
            try {
                if (br != null)
                    br.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }

    }

    public Properties getParticipationProperties(Context context) {
        Properties result = recoverProperties(context, "participation");
        if (result != null)
            return result;
        else {
            String pro = "#Participation Properties:\n\n";
            for (JCL_Sensor.TypeSensor sensor: sensors) {
                String name = sensor.name;
                pro += (name + " = false\n");
            }
            recorderProperties(context, pro, "participation");
            return recoverProperties(context, "participation");
        }
    }

    public Properties getDelayProperties(Context context) {
        Properties result = recoverProperties(context, "delay");
        if (result != null)
            return result;
        else {
            String pro = "# Delay Properties:\n\n"
                    + "# For default values use 1000\n"
                    + "# Values in milliseconds\n\n";

            int i = JCL_Sensor.TypeSensor.TYPE_ACCELEROMETER.id;
            for (JCL_Sensor.TypeSensor sensor: sensors) {
                String name = sensor.name;
                if (i == JCL_Sensor.TypeSensor.TYPE_AUDIO.id + 1)
                    pro += ("TIME_AUDIO" + " = 10\n");
                pro += (name + " = 1000\n");
                i++;
            }
            recorderProperties(context, pro, "delay");
            return recoverProperties(context, "delay");
        }
    }

    public Properties getSizeProperties(Context context) {
        Properties result = recoverProperties(context, "size");
        if (result != null)
            return result;
        else {
            String pro = "# Size Properties:\n\n"
                    + "# Minimum value = 1\n\n";
                    //+ "# Values in quantities\n\n";

            //int i = JCL_Sensor.TYPE_ACCELEROMETER;
            for (JCL_Sensor.TypeSensor sensor: sensors) {
                String name = sensor.name;
                pro += (name + " = 10000\n");
                //i++;
            }
            recorderProperties(context, pro, "size");
            return recoverProperties(context, "size");
        }
    }

    public Properties getConfigurationProperties(Context context) {
        Properties result = recoverProperties(context, "configuration");
        if (result != null)
            return result;
        else {
            String manufacturer = Build.MANUFACTURER;
            String model = Build.MODEL;
            String deviceName;
            if (model.startsWith(manufacturer)) {
                deviceName = capitalize(model);
            } else {
                deviceName = capitalize(manufacturer) + " " + model;
            }
            StatFs stat = new StatFs(Environment.getExternalStorageDirectory().getPath());
            long bytesAvailable = (long) stat.getBlockSize() * (long) stat.getBlockCount();
            double megAvailable = (double) bytesAvailable / 1048576;
            String storageCapacity = String.format("%.2f", megAvailable / 1024);

            String pro = "# Configuration Properties:\n\n" + "SERVERIP = localhost\n"
                    //+ "MyIp = localhost \n
                    + "PORT = 5151\n"
                    + "SERVERPORT = 6969\n"
                    //+ "mac = " + getMacAdress(context) + " \n"
                    + "DEVICE_TYPE = " + 7 + "\n"
                    + "DEVICE_OS = " + "Android " + Build.VERSION.RELEASE + "\n"
                    + "BROKERAD = \n"
                    + "BROKERPORT = 1883\n"
                    + "DEVICE_ID = " + deviceName + "\n"
                    + "DEVICE_RAM = " + getTotalRAM() + "\n"
                    //+ "Device_Category = 3 \n"
                    + "CORE(S) = " + Runtime.getRuntime().availableProcessors() + "\n"
                    + "DEVICE_STORAGE_CAPACITY = " + storageCapacity + " GB\n"
                    + "STANDBY = false\n"
                    + "DEVICE_PLATFORM = Android\n"
                    + "ENCRYPTION = false\n";

            recorderProperties(context, pro, "configuration");
            return recoverProperties(context, "configuration");
        }
    }

    public Map<String, String> getConfiguration(Properties properties, Context context) {
        //String[] propertiesVector = properties.split("\n");
        Map<String, String> map = new HashMap<>();
        Vector<String> vecConfiguration = new Vector<>();
        vecConfiguration.add("PORT");
        vecConfiguration.add("DEVICE_TYPE");
        vecConfiguration.add("DEVICE_OS");
        vecConfiguration.add("DEVICE_ID");
        vecConfiguration.add("DEVICE_RAM");
        vecConfiguration.add("CORE(S)");
        vecConfiguration.add("DEVICE_STORAGE_CAPACITY");
        vecConfiguration.add("STANDBY");
        vecConfiguration.add("DEVICE_PLATFORM");
        vecConfiguration.add("ENCRYPTION");
        vecConfiguration.add("ENCRYPTION");
        vecConfiguration.add("ENCRYPTION");
        vecConfiguration.add("BROKERAD");
        vecConfiguration.add("BROKERPORT");
        map.put("MAC", getMacAdress(context));
        map.put("IP", getMyIp(context));
        setDevice(getMacAdress(context));


        for (String key : vecConfiguration) {
            map.put(key, properties.getProperty(key).trim());
        }
        setDeviceName(map.get("DEVICE_ID"));
        setDevice(getDevice() + map.get("PORT"));
        setMyPort(Integer.parseInt(map.get("PORT")));
        return map;
    }

    public String[] getIpPort(Context context) throws IOException {
        Properties p = getConfigurationProperties(context);
        String[] result = {p.getProperty("SERVERIP").trim(), p.getProperty("SERVERPORT").trim()};
        return result;
    }

    public void getTimeDelayOrSize(Properties properties, Vector<Integer> vec) {
        for (JCL_Sensor.TypeSensor sensor: sensors) {
            String key = sensor.name;
            Integer timeS = Integer.valueOf(properties.getProperty(key).trim());
            vec.add(timeS);
        }
    }




    public List<JCL_Sensor.TypeSensor> getTypeSensor(Properties properties, Context context) {
        List<JCL_Sensor.TypeSensor> result = new ArrayList<>();
        for (JCL_Sensor.TypeSensor sensor: sensors) {
            String key = sensor.name;
            if (properties.getProperty(key).trim().equals("true") && compatibleSensorVec.get(sensor.id) && (sensor.id != JCL_Sensor.TypeSensor.TYPE_GPS.id || ((LocationManager) context.getSystemService(Context.LOCATION_SERVICE)).isProviderEnabled(LocationManager.GPS_PROVIDER))) {
                participation.add(true);
                result.add(sensor);
//                indexSensorsCode.add(sensor.id);
            } else {
                participation.add(false);
            }
        }
        //for (Integer h : indexSensorsCode)
            //Log.e("Sensor", sensors.get(h));
        return result;
    }

    public String[] getCompatibleSensor(SensorManager sensorManager) {
        compatibleSensorVec.clear();
        String[] compatibleSen = {"", "", "", ""};
        for (int i=0; i<11; i++) {
            JCL_Sensor.TypeSensor sensor =  sensors.get(i);
            compatibleSensor(sensor.androidId, sensor.name, compatibleSen,
                    sensorManager);
        }
        compatibleSensorVec.add(true);
        compatibleSensorVec.add(true);
        compatibleSensorVec.add(true);
        Log.e("Compatible", compatibleSensorVec.toString());
        return compatibleSen;
    }

    public boolean changeMetadata(Map<String, String> meta) {
        metadata.clear();
        metadata.putAll(meta);
        return getServiceHandler().changeMetadata(metadata, sensors, getServerIp(), getServerPort());
    }

    public boolean setSensor(String sensor_alias, int sensor_id, int sensor_size, int sensor_sampling, boolean setByGUI) {
        if (sensor_id < 0 || sensor_id > 13 || (!compatibleSensorVec.isEmpty() && !compatibleSensorVec.get(sensor_id)))
            return false;

        getDelayTime().set(sensor_id, sensor_sampling);
        getSize().set(sensor_id, sensor_size);

        metadata.put("SENSOR_SAMPLING_" + sensor_id, sensor_sampling + "");
        metadata.put("SENSOR_SIZE_" + sensor_id, sensor_size + "");
        metadata.put("SENSOR_ALIAS_" + sensor_id, sensor_alias + "");
        String enableSensor0 = metadata.get("ENABLE_SENSOR");
        if (!getParticipation().get(sensor_id)) {
            enableSensor0 = enableSensor0.equals(";")? sensor_id+"": enableSensor0 +";" + sensor_id;
            metadata.put("ENABLE_SENSOR", enableSensor0);
        }
        if (!setByGUI){
            updateGUI(sensor_id,sensor_sampling+"", sensor_size+"", "true");
        }

        return getServiceHandler().changeMetadata(metadata, sensors, getServerIp(), getServerPort());

    }
    public void enableSensor(int JCL_sensorType) {
        getServiceNativeHandler().enableSensor(JCL_sensorType);
    }
    public void disableSensor(int JCL_sensorType){
        getServiceNativeHandler().disableSensor(JCL_sensorType);
    }

    public boolean removeSensor(int sensor_id, boolean removedByGUI) {
        if (sensor_id < 0 || sensor_id > 13 || (!compatibleSensorVec.isEmpty() && !compatibleSensorVec.get(sensor_id)) || !participation.get(sensor_id))
            return false;

        List<String> enableSensorList = new ArrayList<>(Arrays.asList(metadata.get("ENABLE_SENSOR").split(";")));
        enableSensorList.remove(sensor_id+"");
        String enableSensor = "";
        for (String sensor: enableSensorList) {
            enableSensor+= sensor+";";
        }
        if (!enableSensor.equals(""))
            enableSensor = enableSensor.substring(0, enableSensor.length() - 1);
        enableSensor = enableSensor.equals("")? ";": enableSensor;

//        String enableSensor = ";";
//        enableSensor += enableSensor0 + ";";
//        //if (!enableSensor.contains(";"+sensor_id+";"))
//        //enableSensor.replaceFirst(";"+sensor_id+";", ";")
//        //enableSensor.replaceAll(";" + sensor_id + ";", ";");
//        enableSensor = enableSensor.replace(";" + sensor_id + ";", ";");
//        if (enableSensor.endsWith(";"))
//            enableSensor = enableSensor.substring(0, enableSensor.length() - 1);
//        if (enableSensor.startsWith(";"))
//            enableSensor = enableSensor.substring(1, enableSensor.length());
        metadata.put("ENABLE_SENSOR", enableSensor);
        //}
        //metadata.put("SENSOR_SAMPLING_" + sensor_id, sensor_sampling+"");
        metadata.remove("SENSOR_SAMPLING_" + sensor_id);
        //metadata.put("SENSOR_SIZE_" + sensor_id, sensor_size+"");
        metadata.remove("SENSOR_SIZE_" + sensor_id);
        //metadata.put("SENSOR_ALIAS_" + sensor_id, sensor_alias+"");
        metadata.remove("SENSOR_ALIAS_" + sensor_id);
        getServiceNativeHandler().removeSensorAction(sensor_id);

        if (!removedByGUI){
            updateGUI(sensor_id, null, null, "false");
        }
        return getServiceHandler().changeMetadata(metadata, sensors, getServerIp(), getServerPort());

    }

    public Map<String, String> createMapMetadata(Context context) {
        Map<String, String> metadata = new HashMap<>();
        String enableSensor = "";
        int totalSensors = 0;
        for (int i = 0; i < sensors.size(); i++) {
            if (participation.get(i)) {
                metadata.put("SENSOR_SAMPLING_" + i, getDelayTime().get(i) + "");
                metadata.put("SENSOR_SIZE_" + i, getSize().get(i) + "");
                metadata.put("SENSOR_ALIAS_" + i, sensors.get(i).name);
                enableSensor += i + ";";
                totalSensors++;
            }
        }
        if (!enableSensor.equals(""))
            enableSensor = enableSensor.substring(0, enableSensor.length() - 1);
        enableSensor = enableSensor.equals("")? ";": enableSensor;
        metadata.put("ENABLE_SENSOR", enableSensor);
        metadata.put("TOTAL_SENSOR", totalSensors + "");

        Properties properties = getConfigurationProperties(context);

        metadata.putAll(getConfiguration(properties, context));

        writeHPCProperties(properties);

        setMetadata(metadata);
        return metadata;
    }


    public void createMapSensor(Context context, SensorManager sensorManager, JCL_Gps2.OnSendGps onSendGps) {
        JCL_Map_Sensor = new HashMap<Integer, Sensor>();
        Properties properties = getParticipationProperties(context);
        Properties delayProperties = getDelayProperties(context);
        Properties sizeProperties = getSizeProperties(context);

        setTimeRecorder(Integer.valueOf(delayProperties.getProperty("TIME_AUDIO").trim()));
        getTimeDelayOrSize(delayProperties, getDelayTime());
        getTimeDelayOrSize(sizeProperties, getSize());
        List<JCL_Sensor.TypeSensor> sensorsChoose = getTypeSensor(properties, context);

        for (JCL_Sensor.TypeSensor sensor : sensorsChoose) {
            int code = sensor.id;
            if (code == JCL_Sensor.TypeSensor.TYPE_GPS.id)
                getGpsInfo(context, onSendGps);
            else if (code == JCL_Sensor.TypeSensor.TYPE_PHOTO.id)
                takePicture(context);
            else if (code == JCL_Sensor.TypeSensor.TYPE_AUDIO.id)
                recorderAudio(context);
            else
                putSensor(sensor.id, sensor.androidId, sensorManager);
        }
    }

    public void enableSensor(int JCL_sensorType, SensorManager sensorManager, Context context, JCL_Gps2.OnSendGps onSendGps, SensorEventListener sensorEvent){

        if (JCL_sensorType == JCL_Sensor.TypeSensor.TYPE_GPS.id)
            getGpsInfo(context, onSendGps);
        else if (JCL_sensorType == JCL_Sensor.TypeSensor.TYPE_PHOTO.id) {
            JCL_FacadeImpl.getInstance().instantiateGlobalVar(JCL_ANDROID_Facade.getInstance().getDevice() + JCL_Sensor.TypeSensor.TYPE_PHOTO.id +"_NUMELEMENTS", 0);
            JCLHashMap<Integer, interfaces.kernel.JCL_Sensor> values = new JCLHashMap<Integer, interfaces.kernel.JCL_Sensor>(JCL_ANDROID_Facade.getInstance().getDevice() + JCL_Sensor.TypeSensor.TYPE_PHOTO.id + "_value");
            Log.e("Photo", "set");
            takePicture(context);
        }
        else if (JCL_sensorType == JCL_Sensor.TypeSensor.TYPE_AUDIO.id) {
            Log.e("Audio", "set");
            JCL_FacadeImpl.getInstance().instantiateGlobalVar(JCL_ANDROID_Facade.getInstance().getDevice() + JCL_Sensor.TypeSensor.TYPE_AUDIO.id +"_NUMELEMENTS", "0");
            JCLHashMap<Integer, interfaces.kernel.JCL_Sensor> values = new JCLHashMap<Integer, interfaces.kernel.JCL_Sensor>(JCL_ANDROID_Facade.getInstance().getDevice() + JCL_Sensor.TypeSensor.TYPE_AUDIO.id + "_value");
            recorderAudio(context);
        }
        else
            putSensor(JCL_sensorType, sensors.get(JCL_sensorType).androidId, sensorManager);

        sensorManager.registerListener(sensorEvent, JCL_Map_Sensor.get(JCL_sensorType),
                SensorManager.SENSOR_DELAY_NORMAL);
    }

    private void compatibleSensor(int sensorType, String nameSensor,
                                  String[] result, SensorManager sensorManager) {
        if (sensorManager.getDefaultSensor(sensorType) == null) {
            result[0] += nameSensor + "\n";
            compatibleSensorVec.add(false);
        } else {
            result[1] += nameSensor + "\n";
            compatibleSensorVec.add(true);
        }
    }

    private void putSensor(int JCL_sensorType, int sensorType,
                           SensorManager sensorManager) {
        JCL_Map_Sensor.put(JCL_sensorType,
                sensorManager.getDefaultSensor(sensorType));
    }

    public void registerSensors(SensorManager sensorManager,
                                SensorEventListener sensorEvent) {
        for (Sensor s : JCL_Map_Sensor.values())
            sensorManager.registerListener(sensorEvent, s,
                    SensorManager.SENSOR_DELAY_NORMAL);
    }

    public void sendReceiveSync(String ip, int port, JCL_Sensor jcl_sensor) {
        Log.e("sending", jcl_sensor.getNameSensor());
        ConnectorImpl co = new ConnectorImpl();
        co.connect(ip, port, null);
        co.sendReceiveG(jcl_sensor.convertToMessage_sensor(), null);
        co.disconnect();
    }

    public void sendReceiveAsync(String ip, int port, JCL_Sensor jcl_sensor) {
        Log.e("sending", jcl_sensor.getNameSensor());
        ConnectionAsync connectionAsync = new ConnectionAsync(ip, port, jcl_sensor.convertToMessage_sensor());
        connectionAsync.execute(null, null);
    }

    public void sendReceive(String ip, int port, JCL_Sensor obj) {
        ConnectionAsync2 connectionAsync2 = new ConnectionAsync2(ip, port, obj.convertToMessage_sensor());
        connectionAsync2.execute(null, null);
    }

    public void sendReceive2(String ip, int port, JCL_Sensor obj) {
        ConnectionAsync2 connectionAsync2 = new ConnectionAsync2(ip, port, obj.convertToMessage_sensor());
        connectionAsync2.sendReceive();
    }

    public void takePicture(Context context) {
        Intent intent = new Intent(context, JCL_RecorderPhotoService.class);
        context.startService(intent);
        if (!JCL_RecorderPhotoService.isWorking)
            waitTicket("camera");
    }

    public void stopTakePicture(Context context) {
        JCL_RecorderPhotoService.isWorking = false;
        Intent intent = new Intent(context, JCL_RecorderPhotoService.class);
        context.stopService(intent);
    }

    public void getGpsInfo(Context context, JCL_Gps2.OnSendGps onSendGps) {
        if (jcl_Gps == null)
            jcl_Gps = new JCL_Gps2(onSendGps, context);
        jcl_Gps.startListener(context);
    }

    public void stopGpsListener(Context context) {
        if (jcl_Gps != null)
            jcl_Gps.stopListener(context);

    }

    public void recorderAudio(Context context) {
        Intent intent = new Intent(context, JCL_RecorderAudioService.class);
        context.startService(intent);
        if (!JCL_RecorderAudioService.isWorking)
            waitTicket("Audio");
    }

    public void stopRecorder(Context context) {
        JCL_RecorderAudioService.isWorking = false;
        context.stopService(new Intent(context, JCL_RecorderAudioService.class));
    }

//    public long getDelay() {
//        return delay;
//    }
//
//    public void setDelay(long delay) {
//        this.delay = delay;
//    }

    public String getServerIp() {
        return ip;
    }


    //ServerIp
    public void setServerIp(String ip) {
        this.ip = ip;
    }

    public int getServerPort() {
        return serverPort;
    }

    public void setServerPort(int port) {
        this.serverPort = port;
    }

    public Integer getTimeRecorder() {
        return timeRecorder;
    }

    public void setTimeRecorder(Integer timeRecorder) {
        this.timeRecorder = timeRecorder;
    }

    public Vector<Integer> getDelayTime() {
        return time;
    }

    public String getTotalRAM() {
        RandomAccessFile reader = null;
        String load = null;
        DecimalFormat twoDecimalForm = new DecimalFormat("#.##");
        double totRam = 0;
        String lastValue = "";
        try {
            reader = new RandomAccessFile("/proc/meminfo", "r");
            load = reader.readLine();

            // Get the Number value from the string
            Pattern p = Pattern.compile("(\\d+)");
            Matcher m = p.matcher(load);
            String value = "";
            while (m.find()) {
                value = m.group(1);
                // System.out.println("Ram : " + value);
            }
            reader.close();

            totRam = Double.parseDouble(value);
            // totRam = totRam / 1024;

            double mb = totRam / 1024.0;
            double gb = totRam / 1048576.0;
            double tb = totRam / 1073741824.0;

            if (tb > 1) {
                lastValue = twoDecimalForm.format(tb).concat(" TB");
            } else if (gb > 1) {
                lastValue = twoDecimalForm.format(gb).concat(" GB");
            } else if (mb > 1) {
                lastValue = twoDecimalForm.format(mb).concat(" MB");
            } else {
                lastValue = twoDecimalForm.format(totRam).concat(" KB");
            }


        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            // Streams.close(reader);
        }

        return lastValue;
    }

    private String capitalize(String s) {
        if (s == null || s.length() == 0) {
            return "";
        }
        char first = s.charAt(0);
        if (Character.isUpperCase(first)) {
            return s;
        } else {
            return Character.toUpperCase(first) + s.substring(1);
        }
    }

    public Vector<Integer> getSize() {
        return size;
    }

    public void setSize(Vector<Integer> size) {
        this.size = size;
    }

    public void destroyAllVar() {
//        indexSensorsCode.clear();
        time.clear();
        participation.clear();
        size.clear();
        compatibleSensorVec.clear();
        for (Synchronous s : synchronousMap.values()) {
            s.wakeUp();
        }
        synchronousMap.clear();
        mapValueSensor.clear();
    }
    
    public String getDeviceName(){
        return  deviceName;
    }
    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public String getDevice() {
        return device;
    }

    public void setDevice(String device) {
        this.device = device;

    }

///   static final ThreadLocal<LinkedBuffer> localBuffer = new ThreadLocal<LinkedBuffer>() {
//        public LinkedBuffer initialValue() {
//            return LinkedBuffer.allocate(2048);
//        }
//    };

//    public static LinkedBuffer getApplicationBuffer() {
//        return localBuffer.get();
//    }

    public String getMacAdress(Context context) {
        try {
            int currentapiVersion = android.os.Build.VERSION.SDK_INT;
            if (currentapiVersion >= Build.VERSION_CODES.M)
                return getMacAdressM().toUpperCase();
            WifiManager manager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
            WifiInfo info = manager.getConnectionInfo();
            String address = info.getMacAddress();
            address = address.toUpperCase();
            address = address.replace(':', '-');
            return address;
        } catch (Exception e) {
            return getMyIp(context);
        }

    }

    public String getMyIp(Context context) {
        WifiManager manager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInfo = manager.getConnectionInfo();
        int ip =
                wifiInfo.getIpAddress();
        String strIP = String.format("%d.%d.%d.%d",
                (ip & 0xff), (ip >> 8 & 0xff), (ip >> 16 & 0xff), (ip >> 24 & 0xff));

        return strIP;
    }

    public String getMacAdressM() {
        try {
            String interfaceName = "wlan0";
            List<NetworkInterface> interfaces = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface intf : interfaces) {
                if (!intf.getName().equalsIgnoreCase(interfaceName)) {
                    continue;
                }

                byte[] mac = intf.getHardwareAddress();
                if (mac == null) {
                    return "";
                }

                StringBuilder buf = new StringBuilder();
                for (byte aMac : mac) {
                    buf.append(String.format("%02X:", aMac));
                }
                if (buf.length() > 0) {
                    buf.deleteCharAt(buf.length() - 1);
                }
                String macRet = buf.toString().replace(":", "-");
                return macRet;
            }
        } catch (Exception ex) {
        } // for now eat exceptions
        return "";
    }

    public void
    waitTicket(String ticket) {
        Log.e("Esperando", ticket);
        if (!synchronousMap.containsKey(ticket))
            synchronousMap.put(ticket, new Synchronous());
        Synchronous s = synchronousMap.get(ticket);
        s.waitNow();
//        try{
//            wait();
//        }catch (Exception e){}
    }

    public synchronized void wakeUp(String ticket) {
        if (synchronousMap.containsKey(ticket)) {
            Synchronous s = synchronousMap.get(ticket);
            s.wakeUp();
        }
    }

    public int getMyPort() {
        return myPort;
    }

    public void setMyPort(int myPort) {
        this.myPort = myPort;
    }


    public Map<String, String> getMetadata(Context context) {
        if (metadata != null)
            return metadata;
        else {
            setMetadata(createMapMetadata(context));
            return metadata;
        }

    }

    public void restart(long time) {
        try {
            if (getServiceHandler() != null)
                getServiceHandler().restart(time);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public MessageSensorImpl getSensorNow(Object args) {
        //Entry<String,String> entry = (Entry) args[0];
        Object[] arg = (Object[]) args;
        Integer sensor = Integer.parseInt((String) arg[0]);
        if (!participation.get(sensor))
            return null;
        else if (sensor <= JCL_Sensor.TypeSensor.TYPE_GPS.id && serviceNativeHandler != null) {
            return serviceNativeHandler.getSensorNow(sensor);
        } else if (sensor == JCL_Sensor.TypeSensor.TYPE_PHOTO.id && servicePhotoHandler != null)
            return servicePhotoHandler.getSensorNow();
        else if (sensor == JCL_Sensor.TypeSensor.TYPE_AUDIO.id && serviceAudioHandler != null) {
            Object[] arg1 = null;
            if (arg.length > 1 && (arg1 = (Object[]) arg[1]).length > 0) {
                return serviceAudioHandler.getSensorNow((long) arg1[0]);
            } else
                return serviceAudioHandler.getSensorNow(0);
        } else {
            MessageSensorImpl m = new MessageSensorImpl();
            m.setType(27);
            m.setDataType("Error");
            return m;
        }
    }

    public void standByNativeSensor() {
        setStandBySen(true);
    }

    public void turnOnNativeSensor() {
        setStandBySen(false);
    }

    public void standBy() {
        try {
            if (getServiceHandler() != null)
                getServiceHandler().standBy();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void turnOn() {
        try {
            if (getServiceHandler() != null)
                getServiceHandler().turnOn();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setMetadata(Map<String, String> metadata) {
        this.metadata = metadata;
    }

    public ServiceHandler getServiceHandler() {
        return serviceHandler;
    }

    public void setServiceHandler(ServiceHandler serviceHandler) {
        this.serviceHandler = serviceHandler;
        try {
            String[] ipPort = getIpPort(JCL_ApplicationContext.getContext());
            setServerIp(ipPort[0]);
            setServerPort(Integer.parseInt(ipPort[1]));
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public ServiceNativeHandler getServiceNativeHandler() {
        return serviceNativeHandler;
    }

    public void setServiceNativeHandler(ServiceNativeHandler serviceNativeHandler) {
        this.serviceNativeHandler = serviceNativeHandler;
    }

    public ServicePhotoHandler getServicePhotoHandler() {
        return servicePhotoHandler;
    }

    public void setServicePhotoHandler(ServicePhotoHandler servicePhotoHandler) {
        this.servicePhotoHandler = servicePhotoHandler;
    }

    public ServiceAudioHandler getServiceAudioHandler() {
        return serviceAudioHandler;
    }

    public void setServiceAudioHandler(ServiceAudioHandler serviceAudioHandler) {
        this.serviceAudioHandler = serviceAudioHandler;
    }

    public boolean isChangingMetadata() {
        return changingMetadata;
    }

    public void setChangingMetadata(boolean changingMetadata) {
        this.changingMetadata = changingMetadata;
    }

    public Vector<Boolean> getCompatibleSensorVec() {
        return compatibleSensorVec;
    }

    public void setCompatibleSensorVec(Vector<Boolean> compatibleSensorVec) {
        this.compatibleSensorVec = compatibleSensorVec;
    }

    public boolean isStandBySen() {
        return standBySen;
    }

    public void setStandBySen(boolean standBySen) {
        this.standBySen = standBySen;
        MessageMetadataImpl m = new MessageMetadataImpl();
        getMetadata(null).put("STANDBY", String.valueOf(standBySen));
        m.setType(40);
        m.setMetadados(getMetadata(null));
        ConnectorImpl co = new ConnectorImpl();
        co.connect(getServerIp(), getServerPort(), null);
        JCL_message_bool b = (JCL_message_bool) co.sendReceiveG(m, null);
        co.disconnect();
        System.out.println(b.getRegisterData()[0]);
        Log.e("Meta", getMetadata(null).toString());
    }

    public ConcurrentMap<Integer, JCL_Sensor> getMapValueSensor() {
        return mapValueSensor;
    }

    public void setMapValueSensor(ConcurrentMap<Integer, JCL_Sensor> mapValueSensor) {
        this.mapValueSensor = mapValueSensor;
    }

    public String getTerminal() {
        return terminal;
    }

    public void setTerminal(String terminal) {
        this.terminal = terminal;
    }

    public interface ServiceHandler {

        public void restart(long time);

        public void standBy();

        public void turnOn();

        public boolean changeMetadata(Map<String, String> meta, List<JCL_Sensor.TypeSensor> sensors, String ipServer, int portServer);

    }

    public interface ServiceNativeHandler {

        public MessageSensorImpl getSensorNow(int type);
        public boolean setContext(Object object);
        public boolean addTaskOnContext(Object obj);
        public boolean addActingOnContext(Object obj);
        public void removeSensorAction(int id);
        public boolean createNewTopic(Object obj);
        public boolean unregisterContext(Object obj);
        public boolean removeActingOnContext(Object obj);
        public boolean removeTaskOnContext(Object obj);
        public void enableSensor(int jcl_sensorType);
        public void disableSensor(int jcl_sensorType);
    }

    public interface ServiceAudioHandler {

        public MessageSensorImpl getSensorNow(long length);
    }

    public interface ServicePhotoHandler {

        public MessageSensorImpl getSensorNow();
    }

    public void writeHPCProperties(Properties configProperties) {
        try {

            String rootPath = Environment.getExternalStorageDirectory().toString();

            Properties properties = new Properties();
            File file = new File(rootPath + "/jcl_conf/config.properties");

            //caso exista properties, da um load
            if (file.exists())
                properties.load(new FileInputStream(rootPath + "/jcl_conf/config.properties"));

            //caso não exita, cria um com os campos padrão
            else {
                File mediaStorageDir = new File(rootPath + "/jcl_conf/");
                if (!mediaStorageDir.exists()) {
                    if (!mediaStorageDir.mkdirs()) {
                        Log.d("MyCameraApp", "failed to create directory");
                        return;
                    }
                }
                String pro = "###################################################\n" +
                        "#               JCL config file                   #\n" +
                        "###################################################\n" +
                        "# Config JCL type\n" +
                        "# true => Pacu version\n" +
                        "# false => Lambari version\n" +
                        "distOrParell = true\n" +
                        "serverMainPort = 6969\n" +
                        "superPeerMainPort = 6868\n" +
                        "routerMainPort = 7070\n" +
                        "serverMainAdd = localhost\n" +
                        "hostPort = 5151\n" +
                        "nic = \n" +
                        "simpleServerPort = 4949\n" +
                        "timeOut = 5000\n" +
                        "byteBuffer = 10000000\n" +
                        "routerLink = 5\n" +
                        "enablePBA = false\n" +
                        "PBAsize=50\n" +
                        "delta=0\n" +
                        "PGTerm = 10\n" +
                        "twoStep = false\n" +
                        "useCore=100\n" +
                        "deviceID = Host1\n" +
                        "enableDinamicUp = false\n" +
                        "findServerTimeOut = 1000\n" +
                        "findHostTimeOut = 1000\n" +
                        "enableFaultTolerance = false\n" +
                        "verbose = true\n"+
                        "encryption = false\n"+
                        "deviceType = 3\n";
                PrintWriter writer = new PrintWriter(rootPath + "/jcl_conf/config.properties", "UTF-8");
                writer.print(pro);
                writer.close();
                properties.load(new FileInputStream(rootPath + "/jcl_conf/config.properties"));

            }

            //seta as configurações setadas
            properties.setProperty("brokerAdd", configProperties.getProperty("BROKERAD").trim());
            properties.setProperty("brokerPort", configProperties.getProperty("BROKERPORT").trim());
            properties.setProperty("serverMainPort", configProperties.getProperty("SERVERPORT").trim());
            properties.setProperty("serverMainAdd", configProperties.getProperty("SERVERIP").trim());
            properties.setProperty("hostPort", configProperties.getProperty("PORT").trim());
            properties.setProperty("encryption", configProperties.getProperty("ENCRYPTION").trim());
            properties.setProperty("deviceType", configProperties.getProperty("DEVICE_TYPE").trim());

            FileOutputStream fileOut = new FileOutputStream(file);
            properties.store(fileOut, "");
            fileOut.close();


        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public boolean setContext(Object object){
        if (isStandBySen())
            return false;
        return serviceNativeHandler.setContext(object);
    }
    public boolean createNewTopic(Object obj){
        if (isStandBySen())
            return false;
        return serviceNativeHandler.createNewTopic(obj);
    }
    public boolean unregisterContext(Object obj){
        if (isStandBySen())
            return false;
        return serviceNativeHandler.unregisterContext(obj);
    }
    public boolean removeActingOnContext(Object obj){
        if (isStandBySen())
            return false;
        return serviceNativeHandler.removeActingOnContext(obj);
    }
    public boolean removeTaskOnContext(Object obj){
        if (isStandBySen())
            return false;
        return serviceNativeHandler.removeTaskOnContext(obj);
    }
    public Vector<Boolean> getParticipation(){
        return participation;
    }
    public boolean addTaskOnContext(Object obj){
        if (isStandBySen())
            return false;
        return  serviceNativeHandler.addTaskOnContext(obj);
    }
    public boolean addActingOnContext(Object obj){
        if (isStandBySen())
            return false;
        return serviceNativeHandler.addActingOnContext(obj);
    }
    public List<JCL_Sensor.TypeSensor> getSensors() {
        return sensors;
    }

    public void setSensors(List<JCL_Sensor.TypeSensor> sensors) {
        this.sensors = sensors;
    }

    public synchronized MqttClient getMqttClient() {
        if (mqttClient == null){
            try {
                String brokerIP = metadata.get("BROKERAD");
                if (brokerIP.equals(""))
                    return null;
                String brokerPort = metadata.get("BROKERPORT");
                //String deviceAlias = getDevice();
                mqttClient = connectToBroker(brokerIP, brokerPort, getDevice());
            }catch (Exception e){
                Log.e("Erro MQtt", e.getMessage());
                return null;
            }
        }

        return mqttClient;
    }

    public MqttClient connectToBroker(String brokerIP, String brokerPort, String deviceAlias){
        try {
            MqttClient mqttClient;
            String broker = "tcp://" + brokerIP + ":" + brokerPort;
            MemoryPersistence persistence = new MemoryPersistence();
            mqttClient = new MqttClient(broker, deviceAlias, persistence);
            MqttConnectOptions connOpts = new MqttConnectOptions();
            connOpts.setCleanSession(true);
            mqttClient.connect(connOpts);
            return  mqttClient;
        }catch(MqttException e){
            e.printStackTrace();
            return mqttClient;
        }
    }

    public Map<Integer, Sensor> getJCL_Map_Sensor() {
        return JCL_Map_Sensor;
    }





    public void recorderTimeTest(String properties, String sensor) {
        try {

            File sdCard = Environment.getExternalStorageDirectory();

            File mediaStorageDir = new File(sdCard.getAbsolutePath().toString()
                    + "/jclAndroid/");
            // This location works best if you want the created images to be shared
            // between applications and persist after your app has been uninstalled.

            // Create the storage directory if it does not exist
            if (!mediaStorageDir.exists()) {
                if (!mediaStorageDir.mkdirs()) {
                    Log.d("MyCameraApp", "failed to create directory");
                    return;
                }
            }
            PrintWriter writer = new PrintWriter(mediaStorageDir.getPath() + "/"+sensor+".txt", "UTF-8");
            writer.print(properties);
            writer.close();
        } catch (Exception e) {
            Log.e("saveToInternalStorage()", e.getMessage());

        }

    }

    public void deleteRecursive(File fileOrDirectory) {
        try {
            if (fileOrDirectory.isDirectory())
                for (File child : fileOrDirectory.listFiles())
                    deleteRecursive(child);
            fileOrDirectory.delete();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updateGUI(final int sensorID, final String delay, final String size, final String participation){
        try {
            final Activity activity = (Activity) JCL_ApplicationContext.getContext();
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {
                        ListView list = (ListView) activity.findViewById(R.id.lvSensors);
                        ListViewSensorAdapter adapter = (ListViewSensorAdapter) list.getAdapter();
                        AndroidSensor a = adapter.getItemById(sensorID);

                        a.setParticipation(participation);
                        if (delay != null) {
                            a.setDelay(delay);
                        }
                        if (size != null) {
                            a.setSize(size);
                        }

                        adapter.notifyDataSetChanged();
                    }catch (Exception e){
                        Log.e("updating scren", e.getMessage());
                    }
                }
            });
        }catch (Exception e){
            Log.e("updating scren", e.getMessage());
        }
    }



}
