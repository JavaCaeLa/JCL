package com.jcl.jcl_androidapplication;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import fragments.FragmentConfiguration;
import fragments.FragmentItems;
import implementations.dm_kernel.IoTuser.JCL_IoTFacadeImpl;
import implementations.util.JCL_ApplicationContext;
import interfaces.kernel.JCL_IoTfacade;
import interfaces.kernel.JCL_Sensor;
import interfaces.kernel.datatype.Device;
import interfaces.kernel.datatype.Sensor;

public class MainActivity extends AppCompatActivity {

    private static final int permissionStorage = 1000;
    private List<Object> listDevices;
    private Map<String, List<Object>> mapSensorsNames;
    private Map<String, List<Object>> maptSensorValues;
    private String mode="";
    private String nameSensor;
    private String device;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FragmentConfiguration fragmentConfiguration = new FragmentConfiguration();
        getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_content, fragmentConfiguration);
        ft.commit();


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        JCL_ApplicationContext.setContext(this);
        getPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, permissionStorage);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_update) {
            try {
                getSensors();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void getSensors() throws Exception {
        new AsyncTask<Void, Void, Void>() {
            ProgressDialog progressDialog;
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                progressDialog = new ProgressDialog(MainActivity.this);
                progressDialog.setMessage("Getting sensor data");
                progressDialog.show();
            }

            @Override
            protected Void doInBackground(Void[] objects) {
                listDevices = new ArrayList<>();
                mapSensorsNames = new HashMap<>();
                maptSensorValues = new HashMap<>();

                JCL_IoTfacade jclIot = JCL_IoTFacadeImpl.getInstance();

                List<Device> devices = jclIot.getIoTDevices();
                for (Device device: devices){
                    listDevices.add(device.getValue()+"::"+device.getKey());

                    List<Sensor> sensors = jclIot.getSensors(device);
                    List<Object> listSensorsNames = new ArrayList<>();


                    for (Sensor sensor : sensors){
                        List<Object> listSensorValues = new ArrayList<>();
                        Map<Integer, JCL_Sensor> sensorsData = jclIot.getSensingData(device, sensor);
                        if (sensorsData !=null) {
                            listSensorValues.addAll(sensorsData.values());
                        }
                        listSensorsNames.add(sensor.getKey()+"::"+sensor.getValue());
                        maptSensorValues.put(device.getValue()+"::"+device.getKey()+sensor.getKey()+"::"+sensor.getValue(), listSensorValues);
                    }
                    mapSensorsNames.put(device.getValue()+"::"+device.getKey(), listSensorsNames);

                }
                return null;
            }

            @Override
            protected void onPostExecute(Void sensors) {
                progressDialog.dismiss();
                FragmentItems fragmentItems = new FragmentItems();
                fragmentItems.setItems(listDevices);
                getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                FragmentTransaction ft = getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_content, fragmentItems);
                ft.commit();
                mode = "devices";
            }
        }.execute(null, null);
    }

    public boolean getPermission(String type, int cod) {
        if (Build.VERSION.SDK_INT >= 23 && ContextCompat.checkSelfPermission(this, type) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{type}, cod);
            return false;
        } else
            return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case permissionStorage: {
                if (grantResults.length < 0
                        || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    finish();

                }
                return;
            }
        }
    }

    public void selectItem(View view) {
        ListView list = (ListView) findViewById(R.id.lvItems);
        final int  p = list.getPositionForView(view);

        FragmentItems fragmentItems = new FragmentItems();

        if (mode.equals("devices")) {
            device = listDevices.get(p).toString();
            fragmentItems.setItems(mapSensorsNames.get(device));
            mode = "name_sensor";
        }else if (mode.equals("name_sensor")){
            nameSensor = mapSensorsNames.get(device).get(p).toString();
            fragmentItems.setItems(maptSensorValues.get(device+nameSensor));
            mode = "items_sensor";
        }else if (mode.equals("items_sensor")){
            if (maptSensorValues.get(device+nameSensor).get(p) instanceof JCL_Sensor) {
                JCL_Sensor jcl_sensor = (JCL_Sensor) maptSensorValues.get(device+nameSensor).get(p);
                jcl_sensor.showData();
            }
            return;
        }

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction().addToBackStack("")
                .replace(R.id.fragment_content, fragmentItems);
        ft.commit();
    }

    @Override
    public void onBackPressed() {
        if (mode.equals("name_sensor")){
            mode = "devices";
        }else if (mode.equals("items_sensor")){
            mode = "name_sensor";
        }
        super.onBackPressed();
    }

    public void startApplication(View view) throws Exception {
        String ip = ((EditText) findViewById(R.id.edtServerIp)).getText().toString();
        String port = ((EditText) findViewById(R.id.edtServerPort)).getText().toString();
        writeHPCProperties(ip, port);
        getSensors();
    }

    public void updateData(View view) throws Exception {
        getSensors();
    }

    private void writeHPCProperties(String serverIp, String serverPort) {
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


            properties.setProperty("serverMainPort", serverPort.trim());
            properties.setProperty("serverMainAdd", serverIp.trim());

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
}
