package implementations.sm_kernel;

import commom.Constants;
import interfaces.kernel.JCL_orb;
import interfaces.kernel.JCL_result;
import interfaces.kernel.JCL_task;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicBoolean;

import commom.GenericConsumer;
import commom.GenericResource;

public class JCL_Crawler implements Runnable{

    //Load varibles
    protected List<GenericConsumer<JCL_task>> workers;
    protected List<AtomicBoolean> killWorkers;
    protected JCL_orb<JCL_result> orb;
    protected GenericResource<JCL_task> r;
    protected static int coreNumber;

    private int corePerc = 0;
    private int corePercMin;

    public JCL_Crawler(int coreSize, List<GenericConsumer<JCL_task>> workers, List<AtomicBoolean> killWorkers, GenericResource<JCL_task> r,JCL_orb<JCL_result> orb){
        //init values
        this.workers = workers;
        this.killWorkers = killWorkers;
        this.r = r;
        this.orb = orb;
        corePercMin = 100/coreSize;
        coreNumber = coreSize;
    }
    public static int getCoreNumber(){
        return coreNumber;
    }
    @Override
    public void run() {
        try {
            //Load from file
            int corePercRead;
            Properties properties = new Properties();
            InputStream pro = new FileInputStream(Constants.Environment.JCLConfig());
            properties.load(pro);
            corePercRead =  Integer.parseInt(properties.getProperty("useCore"));

            //Core perc was change
            if (corePercRead!=corePerc){
                coreNumber = corePercRead/corePercMin;
                if (coreNumber==0)coreNumber=1;
                //Need to change number of threads
                if(this.killWorkers.size()>coreNumber){
                    //Kill threads
                    int total = this.killWorkers.size() - coreNumber;
                    for(int i=0;i<total;i++){
                        int index = killWorkers.size()-1;
                        AtomicBoolean kill = killWorkers.get(index);
                        GenericConsumer<JCL_task> gc = workers.get(index);
                        kill.set(false);
                        r.wakeup();
                        gc.join();
                        killWorkers.remove(index);
                        workers.remove(index);
                    }
                }else{
                    //Create threads
                    int total = coreNumber - this.killWorkers.size();
                    for(int i=0; i<total; i++){
                        AtomicBoolean kill = new AtomicBoolean(true);
                        GenericConsumer<JCL_task> gc = new TaskConsumer<JCL_task>(r,kill,orb);
                        gc.start();
                        workers.add(gc);
                        killWorkers.add(kill);
                    }
                }
            }
            pro.close();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}