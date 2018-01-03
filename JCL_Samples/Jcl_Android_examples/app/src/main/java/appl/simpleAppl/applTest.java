package appl.simpleAppl;

import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import implementations.dm_kernel.IoTuser.JCL_IoTFacadeImpl;
import interfaces.kernel.JCL_IoTfacade;
import interfaces.kernel.JCL_Sensor;

/**
 * Created by estevao on 24/07/17.
 */

public class applTest {
    private int users;

    public applTest(int users) throws ExecutionException, InterruptedException {
        this.users = users;
        Map<String, String> values = new HashMap<>();
        ExecutorService executor = Executors.newFixedThreadPool(users);
        List<Future<Object>>  futureList = new ArrayList<>();
        for (int i = 0; i < users; i++) {
            testUser worker = new testUser();
            futureList.add(executor.submit(worker));
        }
        for (Future<Object> future: futureList) {
            getMap(values, future);
        }
        String rootPath = Environment.getExternalStorageDirectory().toString();
        for (Map.Entry<String, String> value: values.entrySet()){
            recorderTimeTest(value.getValue(), rootPath+"/testesAndroid/user1to16/"+users+"/", value.getKey());
        }
        Log.e("Fim", "terminou");
        executor.shutdown();
    }
    public static void recorderTimeTest(String valor, String file, String arq) {
        try {
            File mediaStorageDir = new File(file);
            if (!mediaStorageDir.exists()) {
                if (!mediaStorageDir.mkdirs()) {
                    System.out.println("Error");
                    return;
                }
            }
            PrintWriter writer = new PrintWriter(file+arq, "UTF-8");
            writer.print(valor);
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();

        }
    }

    public static void getMap(Map<String, String> values, Future<Object> result) throws InterruptedException, ExecutionException {

        Object o = result.get();
        Map<String, String> individualValues = (Map<String, String>) o;
        for (Map.Entry<String, String> value: individualValues.entrySet()){
            String data = values.containsKey(value.getKey())? values.get(value.getKey()): "";
            System.out.println(value.getValue());
            values.put(value.getKey(), data+value.getValue());
        }
    }


    public class testUser implements Callable<Object> {
        Map<String, String> times = new HashMap<>();

        @Override
        public Object call() throws Exception {
            JCL_IoTfacade jcl = JCL_IoTFacadeImpl.getInstance();

            System.out.println("iniciou");
            for (int i = 0; i < 250; i++) {
                System.out.println(i);
                List<Map.Entry<String, String>> devices = jcl.getIoTDevices();
                for (Map.Entry<String, String> device : devices) {

                    long init = System.nanoTime();
                    List<Map.Entry<String, String>> sensors = jcl.getSensors(device);

                    for (Map.Entry<String, String> sensor : sensors) {
                        init = System.nanoTime();
                        JCL_Sensor s = jcl.getSensingDataNow(device, sensor);
                        appendOnMap(times, "getSensingDataNow_" + device.getValue() + "_" + sensor.getValue(), (System.nanoTime() - init));
                    }

                }
            }
            return times;
        }

        private void appendOnMap(Map<String, String> times, String key, String value) {
            times.put(key, (times.containsKey(key)? times.get(key):"") + value + "\n");
        }

        private void appendOnMap(Map<String, String> times, String key, long value) {
            appendOnMap(times, key, TimeUnit.NANOSECONDS.toMillis(value) + "");
        }
    }

}
