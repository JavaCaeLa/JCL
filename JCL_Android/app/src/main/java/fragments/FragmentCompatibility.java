package fragments;

import com.hpc.jcl_android.JCL_ANDROID_Facade;
import com.hpc.jcl_android.R;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.PrintStream;

import implementations.dm_kernel.host.MainHost;
import javassist.environment.Environment;
import services.JCL_HostService;

public class FragmentCompatibility extends Fragment {

    private String[] incompatibleSensor;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_compatibility, container, false);

        SensorManager sensorManager = (SensorManager) getContext().getSystemService(getContext().SENSOR_SERVICE);
        JCL_ANDROID_Facade jcl = JCL_ANDROID_Facade.getInstance();
        incompatibleSensor = jcl.getCompatibleSensor(sensorManager);

        PrintStream origOut = System.out;
        PrintStream origErr = System.err;
        PrintStream interceptor = new Interceptor(origOut);
        PrintStream interceptor2 = new Interceptor(origErr);
        System.setOut(interceptor);
        System.setErr(interceptor2);


        return view;

    }

    @Override
    public void onResume() {
        super.onResume();
        View v = getView();
        TextView tx = (TextView) v.findViewById(R.id.txList);
        JCL_ANDROID_Facade jcl = JCL_ANDROID_Facade.getInstance();
        if (!JCL_HostService.isWorking) {
            tx.setText("Your device doesn't support these sensors:\n\n"
                    + incompatibleSensor[0] + "\n\n"
                    + "Your device supports these sensors:\n\n"
                    + incompatibleSensor[1]);
        } else
            tx.setText(jcl.getTerminal());


        tx.setMovementMethod(new ScrollingMovementMethod());
        Button b = (Button) v.findViewById(R.id.btnrun);
        if (!JCL_HostService.isWorking)
            b.setText("Run");
        else
            b.setText("Stop");

    }

    public class Interceptor extends PrintStream {
        public Interceptor(OutputStream out) {
            super(out, true);
        }

        @Override
        public void print(final String s) {//do what ever you like
            super.print(s);
            if (JCL_HostService.isWorking) {
                final JCL_ANDROID_Facade jcl = JCL_ANDROID_Facade.getInstance();
                jcl.setTerminal(jcl.getTerminal() + s);
                try {
                    if (getActivity() != null) {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    FileOutputStream o = new FileOutputStream(android.os.Environment.getExternalStorageDirectory() + "/lo.txt");
                                    ObjectOutputStream objectInputStrea = new ObjectOutputStream(o);
                                    objectInputStrea.writeUTF(jcl.getTerminal());
                                    objectInputStrea.close();
                                    o.close();

                                    TextView tx = (TextView) getView().findViewById(R.id.txList);
                                    tx.setText(jcl.getTerminal());
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                    }
                } catch (Exception e) {
                    Log.e("Error Print", e.getMessage());
                }
            }

        }
    }

}
