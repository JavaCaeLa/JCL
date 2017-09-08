package com.hpc.jcl_android;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Properties;

import fragments.Fragment1;
import implementations.dm_kernel.host.MainHost;
import implementations.util.JCL_ApplicationContext;
import implementations.util.android.AndroidSensor;
import sensor.JCL_Camera;
import services.JCL_HostService;


public class MainActivity extends AppCompatActivity {
    private Fragment1 fragment1 = new Fragment1();
    private static final int permissionCamera = 1;
    private static final int permissionLocation = 2;
    private static final int permissionMicrophone = 3;
    private static final int permissionStorage = 4;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction()
                .replace(R.id.content_frame, fragment1);
        ft.commit();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    protected void onPause() {
        super.onPause();
    }

    protected void onResume() {
        super.onResume();
        if (getPermission(Manifest.permission.CAMERA, permissionCamera))
            JCL_Camera.getMaxResolution();
        getPermission(Manifest.permission.ACCESS_FINE_LOCATION, permissionLocation);
        getPermission(Manifest.permission.RECORD_AUDIO, permissionMicrophone);
        if (getPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, permissionStorage))
            createProperties();

        JCL_ApplicationContext.setContext(this);

    }

    private String getFileVersion() {
        try {
            FileInputStream inputStream = openFileInput("version_file.jcl");
            ObjectInputStream objectInputStream = new ObjectInputStream(inputStream);
            String version = (String) objectInputStream.readObject();
            return version;
        } catch (Exception e) {
            e.printStackTrace();
            return "0";
        }
    }



    private void setFileVersion(String version) {
        try {
            FileOutputStream outputStream = openFileOutput("version_file.jcl", MODE_PRIVATE);
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream);
            objectOutputStream.writeObject(version);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void run(View view) {
        JCL_ANDROID_Facade jcl = JCL_ANDROID_Facade.getInstance();
        Button b = (Button) findViewById(R.id.btnrun);
        if (!JCL_HostService.isWorking) {
            jcl.setTerminal("");
            startService(new Intent(this, JCL_HostService.class));
            JCL_HostService.isWorking = true;
            b.setText("Stop");
        } else {
            try {
                String[] ipPort = new String[0];
                try {
                    ipPort = jcl.getIpPort(this);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                String[] inf = {jcl.getMyIp(this), ipPort[0], ipPort[1]};
                MainHost.unRegister(inf);

                JCL_HostService.isWorking = false;
                stopService(new Intent(this, JCL_HostService.class));
                b.setText("Run");
            }catch (Exception e){
                e.printStackTrace();
                JCL_HostService.isWorking = false;
                stopService(new Intent(this, JCL_HostService.class));
            }
        }
    }

    public void saveSize(View view) {
        JCL_ANDROID_Facade jcl = JCL_ANDROID_Facade.getInstance();
        EditText et = (EditText) findViewById(R.id.edtSizeProperties);
        jcl.recorderProperties(this, et.getText().toString(), "size");
    }

    public void saveDelay(View view) {
        JCL_ANDROID_Facade jcl = JCL_ANDROID_Facade.getInstance();
        EditText et = (EditText) findViewById(R.id.edtDelayProperties);
        jcl.recorderProperties(this, et.getText().toString(), "delay");

    }

    public void saveConfiguration(View view) {
        JCL_ANDROID_Facade jcl = JCL_ANDROID_Facade.getInstance();
        EditText et = (EditText) findViewById(R.id.edtConfigurationProperties);
        jcl.recorderProperties(this, et.getText().toString(), "configuration");
    }

    public void saveParticipation(View view) {
        JCL_ANDROID_Facade jcl = JCL_ANDROID_Facade.getInstance();
        EditText et = (EditText) findViewById(R.id.edtParticipationProperties);
        jcl.recorderProperties(this, et.getText().toString(), "participation");
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
            case permissionCamera: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    JCL_Camera.getMaxResolution();

                } else {
                    finish();
                }
                return;
            }
            case permissionLocation: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                } else {
                    finish();
                }
                return;
            }
            case permissionMicrophone: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                } else {
                    finish();
                }
                return;
            }
            case permissionStorage: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    createProperties();

                } else {
                    finish();
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    public static byte[] getBytes(InputStream is) throws IOException {

        ByteArrayOutputStream buffer = new ByteArrayOutputStream();

        int nRead;
        byte[] data = new byte[16384];

        while ((nRead = is.read(data, 0, data.length)) != -1) {
            buffer.write(data, 0, nRead);
        }

        buffer.flush();

        return buffer.toByteArray();
    }

    public void createProperties() {
        JCL_ANDROID_Facade jcl = JCL_ANDROID_Facade.getInstance();
        if (!getString(R.string.file_version).equals(getFileVersion())) {
            jcl.deleteRecursive(new File(Environment.getExternalStorageDirectory().toString() + "/jclAndroid/"));
            setFileVersion(getString(R.string.file_version));
        }


        String rootPath = Environment.getExternalStorageDirectory().toString() + "/jclAndroid";
        if (!new File(rootPath + "/jcl." + "size" + ".properties").exists())
            jcl.getSizeProperties(MainActivity.this);
        if (!new File(rootPath + "/jcl." + "participation" + ".properties").exists())
            jcl.getParticipationProperties(MainActivity.this);
        if (!new File(rootPath + "/jcl." + "configuration" + ".properties").exists())
            jcl.getConfigurationProperties(MainActivity.this);
        if (!new File(rootPath + "/jcl." + "delay" + ".properties").exists())
            jcl.getDelayProperties(MainActivity.this);
    }


    public void editSensor(View view) {
        ListView list = (ListView) findViewById(R.id.lvSensors);
        final JCL_ANDROID_Facade jcl = JCL_ANDROID_Facade.getInstance();
        final int  p = list.getPositionForView(view);
        AndroidSensor sensor = ((ListViewSensorAdapter) list.getAdapter()).getItem(p);

        final String pos = sensor.getName();
        final Properties properties = jcl.getParticipationProperties(this);
        final Properties delayProperties = jcl.getDelayProperties(this);
        final Properties sizeProperties = jcl.getSizeProperties(this);
        String delay = delayProperties.getProperty(pos).trim();
        String size = sizeProperties.getProperty(pos).trim();
        Boolean part = new Boolean(properties.getProperty(pos).trim());

        final View view1 = getLayoutInflater().inflate(
                R.layout.builder_sensor_configuration, null);

        EditText de = (EditText) view1.findViewById(R.id.edtDelay);
        EditText si = (EditText) view1.findViewById(R.id.edtSize);
        CheckBox ch = (CheckBox) view1.findViewById(R.id.cbxEnable);
        if (sensor.getAudioTime()!=null){
            TextInputLayout txi = (TextInputLayout) view1.findViewById(R.id.tiTime);
            txi.setVisibility(View.VISIBLE);
            EditText ti = (EditText) view1.findViewById(R.id.edtTime);
            ti.setText(sensor.getAudioTime());
        }
        de.setText(delay);
        si.setText(size);
        ch.setChecked(part);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(view1);
        builder.setTitle(pos);
        builder.setNegativeButton("Back", null);

        builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(final DialogInterface dialogInterface, int i) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            ListView list = (ListView) findViewById(R.id.lvSensors);
                            ListViewSensorAdapter adapter = (ListViewSensorAdapter) list.getAdapter();
                            final AndroidSensor a = adapter.getItem(p);
                            String name = Environment.getExternalStorageDirectory().toString() + "/jclAndroid/";

                            String de = ((EditText) view1.findViewById(R.id.edtDelay)).getText().toString();
                            String si = ((EditText) view1.findViewById(R.id.edtSize)).getText().toString();
                            final String en = ((CheckBox) view1.findViewById(R.id.cbxEnable)).isChecked()? "true": "false";

                            a.setDelay(de);
                            a.setParticipation(en);
                            a.setSize(si);

                            if (JCL_HostService.isWorking){
                                if (a.getAudioTime()!=null){
                                    String ti = ((EditText) view1.findViewById(R.id.edtTime)).getText().toString();
                                    delayProperties.setProperty("TIME_AUDIO", ti);
                                    a.setAudioTime(ti);
                                    FileOutputStream out = new FileOutputStream(name + "jcl.delay.properties");
                                    delayProperties.store(out, "");
                                    out.close();
                                }

                                new AsyncTask<Void, Void, Void>(){
                                    @Override
                                    protected Void doInBackground(Void... voids) {
                                        if (en.equals("true")){
                                            jcl.setSensor(a.getName(),a.getId(),Integer.valueOf(a.getSize()),Integer.valueOf(a.getDelay()), true);
                                        }else{
                                            jcl.removeSensor(a.getId(), true);
                                        }
                                        return null;
                                    }
                                }.execute(null, null);
                            }else{
                                properties.setProperty(pos, en);
                                FileOutputStream out = new FileOutputStream(name + "jcl.participation.properties");
                                properties.store(out, "");
                                out.close();
                                sizeProperties.setProperty(pos, si);
                                out = new FileOutputStream(name + "jcl.size.properties");
                                sizeProperties.store(out, "");
                                out.close();

                                if (a.getAudioTime()!=null){
                                    String ti = ((EditText) view1.findViewById(R.id.edtTime)).getText().toString();
                                    delayProperties.setProperty("TIME_AUDIO", ti);
                                    a.setAudioTime(ti);
                                }
                                delayProperties.setProperty(pos, de);
                                out = new FileOutputStream(name + "jcl.delay.properties");
                                delayProperties.store(out, "");
                                out.close();
                            }

                            adapter.notifyDataSetChanged();



                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        dialogInterface.dismiss();
                    }

                });
            }
        });
        AlertDialog alert = builder.create();
        alert.show();

    }

    public void editConfiguration(View view) {
        ListView list = (ListView) findViewById(R.id.lvConf);
        final int  p = list.getPositionForView(view);
        if (p>8) {
            new AlertDialog.Builder(this).setTitle("Info").setMessage("This is just a information.").create().show();
            return;
        }
        JCL_ANDROID_Facade jcl = JCL_ANDROID_Facade.getInstance();
        final String conf = ((ListViewConfAdapter) list.getAdapter()).getMapItems().get(p);
        final Properties properties = jcl.getConfigurationProperties(this);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final EditText editText = new EditText(this);
        editText.setGravity(Gravity.CENTER);
        editText.setText(properties.getProperty(conf).trim());
        builder.setView(editText);
        builder.setTitle(conf);
        builder.setNegativeButton("Back", null);

        builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(final DialogInterface dialogInterface, int i) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            ListView list = (ListView) findViewById(R.id.lvConf);
                            ListViewConfAdapter adapter = (ListViewConfAdapter) list.getAdapter();
                            String name = Environment.getExternalStorageDirectory().toString() + "/jclAndroid/";
                            String c = editText.getText().toString().trim();
                            properties.setProperty(conf, c);
                            properties.store(new FileOutputStream(name + "jcl.configuration.properties"), "");
                            adapter.getItem(p)[0] = conf+" = "+c;
                            adapter.notifyDataSetChanged();



                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        dialogInterface.dismiss();
                    }

                });
            }
        });
        AlertDialog alert = builder.create();
        alert.show();

    }
}