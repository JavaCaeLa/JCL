package com.jcl.jcl_examples;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.Properties;
import java.util.concurrent.ExecutionException;

import appl.simpleAppl.appl1;
import appl.simpleAppl.appl10;
import appl.simpleAppl.appl2;
import appl.simpleAppl.appl3;
import appl.simpleAppl.appl4ExecutingJars;
import appl.simpleAppl.appl5;

import appl.simpleAppl.appl6;
import appl.simpleAppl.appl7;
import appl.simpleAppl.appl8;
import appl.simpleAppl.appl9;
import appl.simpleAppl.applTest;
import fragments.FragmentConfiguration;
import fragments.FragmentRun;
import implementations.util.JCL_ApplicationContext;



public class MainActivity extends AppCompatActivity {
    String application = "";
    private static final int permissionStorage = 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FragmentConfiguration fragmentConfiguration = new FragmentConfiguration();
        getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction()
                .replace(R.id.content_frame, fragmentConfiguration);
        ft.commit();
    }



    public void startApplication(View view) throws Exception {
        String ip = ((EditText) findViewById(R.id.edtServerIp)).getText().toString();
        String port = ((EditText) findViewById(R.id.edtServerPort)).getText().toString();
        application = ((EditText) findViewById(R.id.edtApp)).getText().toString();
        writeHPCProperties(ip, port);

        FragmentRun fragmentRun = new FragmentRun();
        getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction()
                .replace(R.id.content_frame, fragmentRun);
        ft.commit();
    }

    @Override
    protected void onResume() {
        super.onResume();
        JCL_ApplicationContext.setContext(this);
        getPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, permissionStorage);


    }

    public void run(View view){
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
//                int user = Integer.parseInt(application.split(":")[1]);
                application = application.split(":")[0];


                switch (application){
                    case "1":{
                        new appl1();
                        break;
                    }
                    case "2":{
                        new appl2();
                        break;
                    }
                    case "3":{
                        new appl3();
                        break;
                    }
                    case "4":{
                        new appl4ExecutingJars();
                        break;
                    }
                    case "5":{
                        new appl5();
                        break;
                    }
                    case "6":{
                        new appl6();
                        break;
                    }
                    case "7":{
                        new appl7();
                        break;
                    }
                    case "8":{
                        new appl8();
                        break;
                    }
                    case "9":{
                        try {
                            new appl9();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        break;
                    }
                    case "10":{
                        new appl10();
                        break;
                    }
//                    case "11":{
//                        try {
//                            new applTest(user);
//                        } catch (ExecutionException e) {
//                            e.printStackTrace();
//                        } catch (InterruptedException e) {
//                            e.printStackTrace();
//                        }
//                        break;
//                    }
                }
                return null;
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

    private void writeHPCProperties(String serverIp, String serverPort) {
        try {


            String rootPath = Environment.getExternalStorageDirectory().toString();

            Properties properties = new Properties();
            File file = new File(rootPath + "/jcl_conf/config.properties");
            File file2 = new File(rootPath + "/jcl_useful_jars/book.jar");
            File file3 = new File(rootPath + "/jcl_useful_jars/sorting.jar");

            if (!file2.exists() || !file3.exists()){
                InputStream in = JCL_ApplicationContext.getContext().getResources().openRawResource(R.raw.book);
                InputStream in2 = JCL_ApplicationContext.getContext().getResources().openRawResource(R.raw.sorting);
                File mediaStorageDir = new File(rootPath + "/jcl_useful_jars/");
                if (!mediaStorageDir.exists()) {
                    if (!mediaStorageDir.mkdirs()) {
                        return;
                    }
                }
                recorderOnDisk(rootPath + "/jcl_useful_jars/book.jar", in);
                recorderOnDisk(rootPath + "/jcl_useful_jars/sorting.jar", in2);

            }

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

    public void recorderOnDisk(String file, InputStream in) throws IOException {//
        OutputStream outputStream = new FileOutputStream(file);
        byte[] data = new byte[891];
        int nRead;

        while ((nRead = in.read(data, 0, data.length)) != -1) {
            outputStream.write(data, 0, nRead);
        }

        outputStream.flush();
        outputStream.close();
        in.close();
    }
}
