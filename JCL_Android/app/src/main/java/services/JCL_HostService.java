package services;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Icon;
import android.os.Build;
import android.os.Environment;
import android.os.IBinder;
import android.os.PowerManager;
import android.util.Log;
import android.widget.Toast;

import com.hpc.jcl_android.JCL_ANDROID_Facade;
import com.hpc.jcl_android.MainActivity;
import com.hpc.jcl_android.R;
//import com.hpc.jcl_android.SuperContext;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import implementations.dm_kernel.ConnectorImpl;
import implementations.dm_kernel.MessageMetadataImpl;
import implementations.dm_kernel.host.MainHost;
import implementations.util.IoT.CryptographyUtils;
import implementations.util.ServerDiscovery;
import interfaces.kernel.JCL_message_get_host;
import sensor.JCL_Sensor;

//import jcl.connection.ConnectorImpl;
//import jcl.connection.JCL_message_metadata;
//import jcl.connection.MessageMetadataImpl;

public class JCL_HostService extends Service implements Runnable, JCL_ANDROID_Facade.ServiceHandler {

    public static boolean isWorking = false;
    private String ip;
    private int port;
    private String myIp;
    private PowerManager.WakeLock wakeLock;
    //public static boolean isWorking;
    private static JCL_message_get_host message_host;


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        isWorking = true;
        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        wakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK
                | PowerManager.ACQUIRE_CAUSES_WAKEUP, "whateverHost");
        if (!wakeLock.isHeld())
            wakeLock.acquire();

        //getData();
        new Thread(this).start();
        startServiceForeground(intent, flags, startId);
        JCL_ANDROID_Facade.getInstance().setServiceHandler(this);
        Log.e("Info", "Starting");
        return START_STICKY;
    }


    public int startServiceForeground(Intent intent, int flags, int startId) {
        //showNotification("Tocando agora:", "",false,false);
        Intent notificationIntent = new Intent(this, MainActivity.class);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
                notificationIntent, 0);
        Notification n = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            Intent intentN = new Intent("NOTIFICATION");

            PendingIntent pShutOff = PendingIntent.getBroadcast(this, 1, intentN, PendingIntent.FLAG_UPDATE_CURRENT);


            Notification.Builder b = new Notification.Builder(this)
                    .setContentTitle("JCL_IOT")
                    .setContentText("Sensing")
                    .setContentIntent(pendingIntent)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setContentIntent(pendingIntent).setAutoCancel(true)
                    .setOngoing(true);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                Notification.Action action = new Notification.Action.Builder(
                        Icon.createWithResource(this, R.drawable.icon_close),
                        "Close",
                        pShutOff).build();
                b.addAction(action);
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT_WATCH)
                b.addAction(new Notification.Action(R.drawable.icon_close, "Close", pShutOff));
            else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                b.addAction(R.drawable.icon_close, "Close", pShutOff);
            }
            n = b.build();
        } else {
            new Notification.Builder(this).setContentTitle("JCL_IOT")
                    .setContentText("Sensing")
                    .setContentIntent(pendingIntent)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setContentIntent(pendingIntent).setAutoCancel(true).getNotification();
        }
        startForeground(300, n);

        return START_STICKY;
    }


    public void onCreate() {


    }

    @Override
    public void onDestroy() {
        isWorking = false;
        JCL_NativeSensorService.working = false;
        stopService(new Intent(this, JCL_NativeSensorService.class));
        String[] info = {myIp, ip, port + ""};
        stopForeground(true);
        Log.e("Info", "Closing");
        super.onDestroy();
    }


    public void run() {
        try {
            JCL_ANDROID_Facade jcl = JCL_ANDROID_Facade.getInstance();
            myIp = jcl.getMyIp(this);
            String[] ipPort = jcl.getIpPort(this);
            ip = ipPort[0];
            port = Integer.parseInt(ipPort[1]);
            startService(new Intent(this, JCL_NativeSensorService.class));
            jcl.waitTicket("Sensors");
            register();
            jcl.wakeUp("Host");
            if (getMessage_host() != null && message_host.getSlaves() != null) {
                JCL_NativeSensorService.working = true;
                MainHost.main(null, this, getMessage_host());
            } else
                return;
        } catch (Exception e) {
            //e.printStackTrace();
            Log.e("Error in register", e.getMessage());
        }
    }


    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    public static JCL_message_get_host getMessage_host() {
        return message_host;
    }

    public static void setMessage_host(JCL_message_get_host message_host) {
        JCL_HostService.message_host = message_host;
    }


    @Override
    public void restart(final long time) {
        new Thread() {
            public void run() {

                JCL_ANDROID_Facade jcl = JCL_ANDROID_Facade.getInstance();

//                stopService(new Intent(JCL_HostService.this, JCL_HostService.class));
//                JCL_HostService.isWorking = false;

                JCL_NativeSensorService.working = false;
                stopService(new Intent(JCL_HostService.this, JCL_NativeSensorService.class));
                try {
                    jcl.waitTicket("Native");
                } catch (Exception e) {
                    e.printStackTrace();
                }

                String[] ipPort = new String[0];
                try {
                    ipPort = jcl.getIpPort(JCL_HostService.this);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                String[] inf = {jcl.getMyIp(JCL_HostService.this), ipPort[0], ipPort[1]};
                MainHost.unRegisterSync(inf);


                try {
                    Thread.sleep(time * 1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                new Thread(JCL_HostService.this).start();
                //startService(new Intent(JCL_HostService.this, JCL_HostService.class));
                //JCL_HostService.isWorking = true;


            }
        }.start();
    }

    @Override
    public boolean changeMetadata(Map<String, String> meta, List<JCL_Sensor.TypeSensor> sensors, String ipServer, int portServer) {
        try {
            JCL_ANDROID_Facade jcl = JCL_ANDROID_Facade.getInstance();


            Properties delayProperties = jcl.getDelayProperties(this);
            Properties sizeProperties = jcl.getSizeProperties(this);

            //Map<String, String> metaAux = jcl.getMetadata(this);

            String particip = "# Participation Properties:\n\n";
            String delay = "# Delay Properties:\n\n"
                    + "# For default values use 1\n"
                    + "# Values em seconds\n\n";
            String size = "# Size Properties:\n\n"
                    + "# Minimum value = 1\n"
                    + "# Values em MB\n\n";

//            String enableSensor = ";";
//            enableSensor += meta.get("ENABLE_SENSOR") + ";";

            List<String> enableSensor = new ArrayList<>(Arrays.asList(meta.get("ENABLE_SENSOR").split(";")));
            for (int i = 0; i < sensors.size(); i++) {
                if (i == JCL_Sensor.TypeSensor.TYPE_AUDIO.id + 1)
                    delay += ("TIME_AUDIO" + " = " + delayProperties.getProperty("TIME_AUDIO") + "\n");
                if (enableSensor.contains(i+"") && i >= 0 && i <= 13 && (!jcl.getCompatibleSensorVec().isEmpty() && jcl.getCompatibleSensorVec().get(i))) {
                    if (!jcl.getParticipation().get(i)) {
                        jcl.getParticipation().set(i, true);
                        jcl.enableSensor(i);

                    }
                    particip += (sensors.get(i) + " = true \n");
                    if (meta.containsKey("SENSOR_SAMPLING_" + i))
                        delay += (sensors.get(i) + " = " + meta.get("SENSOR_SAMPLING_" + i) + " \n");
                    else
                        delay += (sensors.get(i) + " = " + delayProperties.getProperty(sensors.get(i).name) + " \n");
                    if (meta.containsKey("SENSOR_SIZE_" + i))
                        size += (sensors.get(i) + " = " + meta.get("SENSOR_SIZE_" + i) + " \n");
                    else
                        size += (sensors.get(i) + " = " + sizeProperties.getProperty(sensors.get(i).name) + " \n");
                } else {
                    if (jcl.getParticipation().get(i)) {
                        jcl.getParticipation().set(i, false);
                        jcl.disableSensor(i);
                        if (jcl.getMapValueSensor().containsKey(i)) {
                            jcl.getMapValueSensor().remove(i);
                        }
                    }
                    particip += (sensors.get(i) + " = false\n");
                    delay += (sensors.get(i) + " = " + delayProperties.getProperty(sensors.get(i).name)+"\n");
                    size += (sensors.get(i) + " = "+ sizeProperties.getProperty(sensors.get(i).name)+"\n");
                }
            }
            meta.put("TOTAL_SENSOR", enableSensor.size() + "");

            jcl.recorderProperties(this, particip, "participation");
            jcl.recorderProperties(this, delay, "delay");
            jcl.recorderProperties(this, size, "size");
            //jcl.setChangingMetadata(true);
            //jcl.standBy();
            //jcl.createMapMetadata(this);
            //jcl.turnOn();
            //jcl.waitTicket("Meta");

            MessageMetadataImpl m = new MessageMetadataImpl();

            m.setType(40);
            Map<String, String> aux = new HashMap<>();
            aux.putAll(jcl.getMetadata(this));
            aux.remove("IP");
            m.setMetadados(aux);
            //m.getMetadados().remove("IP");

            ConnectorImpl co = new ConnectorImpl();
            co.connect(ipServer, portServer, null);

            co.sendReceiveG(m, null);

            co.disconnect();
            //System.out.println(jcl.getMetadata(this));
            Log.d("Meta: ", jcl.getMetadata(this).toString());
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        //restart(0);
    }

    @Override
    public void standBy() {
        isWorking = true;
        JCL_NativeSensorService.working = false;
        Log.e("Sensor", "Stand by");
        stopService(new Intent(JCL_HostService.this, JCL_NativeSensorService.class));
        JCL_ANDROID_Facade.getInstance().waitTicket("Finish");
    }

    @Override
    public void turnOn() {
        isWorking = false;
        startService(new Intent(this, JCL_NativeSensorService.class));
    }


    public void register() {

        JCL_ANDROID_Facade jcl = JCL_ANDROID_Facade.getInstance();

        MessageMetadataImpl m = new MessageMetadataImpl();

        m.setType(-1);
        m.setMetadados(jcl.createMapMetadata(this));


        ConnectorImpl.encryption = false;
        boolean activateEncryption = Boolean.parseBoolean(m.getMetadados().get("ENCRYPTION"));


        ConnectorImpl co = new ConnectorImpl();



        boolean connected = co.connect(ip, port, null);
        if (!connected){
            String serverData[] = ServerDiscovery.discoverServer();
            if (serverData != null){
                ip = serverData[0];
                port = Integer.parseInt(serverData[1]);
                co.connect(ip, port, null);
                Properties properties = jcl.getConfigurationProperties(this);
                properties.setProperty("SERVERPORT", port+"");
                properties.setProperty("SERVERIP", ip);

                String rootPath = Environment.getExternalStorageDirectory().toString() + "/jclAndroid/";
                FileOutputStream fileOut = null;
                try {
                    fileOut = new FileOutputStream(rootPath + "/jcl.configuration.properties");
                    properties.store(fileOut, "");
                    fileOut.close();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                jcl.writeHPCProperties(properties);
            }

        }




        JCL_message_get_host msgr = (JCL_message_get_host) co.sendReceiveG(m, null);

        if (activateEncryption)
            ConnectorImpl.encryption = true;


        if ((msgr.getSlaves() != null)) {
            CryptographyUtils.setClusterPassword(msgr.getMAC());
            Log.e("Mac", msgr.getMAC());
            JCL_HostService.setMessage_host(msgr);
            jcl.wakeUp("Sensors");
            System.out.println("IOT JCL is OK");
        } else {
            System.err.println("IOT JCL NOT REGISTERED");
            jcl.wakeUp("Sensors");
            //msgr.setSlaves(new );
            ConcurrentMap<Integer, String> c = new ConcurrentHashMap<>();
            c.put(0, jcl.getMetadata(this).get("MAC") + jcl.getMetadata(this).get("PORT"));
            JCL_HostService.setMessage_host(msgr);
        }


        //System.out.println();
        Log.d("Meta2: ", m.getMetadados().toString());

        //ShutDownHook();
        co.disconnect();
    }

    public void getData(){
        new Thread(new Runnable(){
            public void run(){
                ArrayList<String> list = new ArrayList<>();
                try {
                    // -m 10, how many entries you want, -d 1, delay by how much, -n 1,
                    // number of iterations
                    //Process p = Runtime.getRuntime().exec("shell dumpsys batterystats");
                    Process p = Runtime.getRuntime().exec("top -m 1 -d 1 -n 240");
                    //Process p = Runtime.getRuntime().exec("top -m 10 | grep com.hpc.jcl_android");

                    BufferedReader reader = new BufferedReader(new InputStreamReader(
                            p.getInputStream()));
                    int i = 0;
                    String line = reader.readLine();
                    float mediaPer = 0;
                    float mediaMem = 0;
                    while (line != null && i<60) {
                        Pattern timePattern = Pattern.compile("com.hpc.jcl_android");
                        Pattern timePattern2 = Pattern.compile("[0-9]+?%");
                        Pattern timePattern3 = Pattern.compile("[0-9]+K");
                        Matcher m = timePattern.matcher(line);
                        if (m.find()) {
                            m = timePattern2.matcher(line);
                            m.find();
                            String match = m.group(0).replace("%", "");
                            System.out.print(match+"%");
                            mediaPer += Integer.parseInt(match);

                            m = timePattern3.matcher(line);
                            m.find();
                            match = m.group(0).replace("K", "");
                            System.out.print(match+"K");
                            mediaMem += Integer.parseInt(match);
                            //if (line.contains("com.hpc.jcl_android")) {
                            Log.e("Output " + i, line);
                            list.add(line);
                            i++;
                        }
                        line = reader.readLine();
                        //}
                    }
                    Log.e("media", "perc: "+ (mediaPer/i));
                    Log.e("media", "mem: "+ (mediaMem/i));
                    p.waitFor();

                    //Toast.makeText(getBaseContext(), "Got update",Toast.LENGTH_SHORT)
                            //.show();

                } catch (Exception e) {
                    e.printStackTrace();
//                    Toast.makeText(getBaseContext(), "Caught", Toast.LENGTH_SHORT)
//                            .show();
                }
            }
        }
        ).start();
    }


}
