package com.hpc.jcl_android;

import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import implementations.collections.JCLHashMap;
import implementations.dm_kernel.IoTuser.JCL_Expression;
import implementations.dm_kernel.IoTuser.JCL_IoTFacadeImpl;
import implementations.dm_kernel.user.JCL_FacadeImpl;
import interfaces.kernel.JCL_IoTfacade;
import interfaces.kernel.JCL_Sensor;
import interfaces.kernel.JCL_facade;
import interfaces.kernel.JCL_result;

/**
 * Created by estevao on 12/02/17.
 */

public class Main {
    public void main() throws ExecutionException, InterruptedException {
//        JCL_facade jcl = JCL_FacadeImpl.getInstance();
//        JCL_IoTfacade jclIoT = JCL_IoTFacadeImpl.getInstance();
//
//        //registers HelloWorld class
//        //jcl.register(HelloWorld.class, "HelloWorld");
//        //executes the method print on cluster asynchronously
//        jcl.execute("HelloWorld", "print", null);
//
//        //register SendEmail class
//        jcl.register(SendEmail.class, "SendEmail");
//        //gets device
//        Map.Entry<String, String> device = jclIoT.getDeviceByName("Android").get(0);
//        //gets Sensor
//        Map.Entry<String, String> sensor = jclIoT.getSensorByName(device, "TYPE_GPS").get(0);
//        //it registers a context. The expression defines when device enter in a area
//        jclIoT.registerContext(device, sensor, new JCL_Expression("S0>-20.3993523; S0<-20.3992711; S1>-43.5137469; S1<-43.5135971"), "gpsContext");
//        Object[] args = new Object[]{"jcl@jcl.org", "user@jcl.org", "JCL context reached","User has just walked in"};
//        jcl.execute("SendEmail", "sendEmail", args);
//        //set a contextAction passing the name's context, with use the sensor value or not on method, class's and method's name and the arguments of method
//        Future<JCL_result> result = jclIoT.addContextAction("gpsContext", false,"SendEmail", "sendEmail", args);
//
//
//        Object[][] ar =  new Object[1][];
//        JCLHashMap m =null;
//        m.putAll();
//        //wait execution
//        result.get();
//
//        new  String().hashCode();
//
//        //Map<Integer, JCL_Sensor> sensorData  = jclIoT.getSensingData()
//        //sensorData1.getValue().showData();
//        doSomething();

    }

    private void doSomething() {
    }
}
