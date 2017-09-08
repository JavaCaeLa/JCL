package implementations.dm_kernel;


import android.util.Log;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicBoolean;

import commom.GenericConsumer;
import commom.GenericResource;
import commom.JCL_handler;

public class JCL_Crawler<K extends JCL_handler> implements Runnable{

	//Load varibles
	protected List<GenericConsumer<K>> workers;
	protected List<AtomicBoolean> killWorkers;
	protected GenericResource<K> r;
	protected Server serv;
	private boolean host = false;
	private int corePerc = 0;
	private int corePercMin;

public JCL_Crawler(int coreSize, List<GenericConsumer<K>> workers, List<AtomicBoolean> killWorkers, GenericResource<K> r, Server serv){
	//init values
	this.workers = workers;
	this.killWorkers = killWorkers;
	this.serv = serv;
	this.r = r;
	this.corePercMin = 100/coreSize;
}

@Override
	public void run() {
		try {
			//Load from file
			int corePercRead;
//			Properties properties = new Properties();
//			InputStream pro = new FileInputStream("../jcl_conf/config.properties");
//			properties.load(pro);
//			corePercRead =  Integer.parseInt(properties.getProperty("useCore"));
			corePercRead = 100;

			//Core perc was change
			if (corePercRead!=corePerc){
				int coreNumber = corePercRead/corePercMin;
				if (coreNumber==0)coreNumber=1;
				//Need to change number of threads
				if(this.killWorkers.size()>coreNumber){
					//Kill threads
					int total = this.killWorkers.size() - coreNumber;
					for(int i=0;i<total;i++){
						int index = killWorkers.size()-1;
						AtomicBoolean kill = killWorkers.get(index);
						GenericConsumer<K> gc = workers.get(index);
						kill.set(false);
						r.wakeup();
						Log.e("Crawler","1");
						gc.join();
						killWorkers.remove(index);
						workers.remove(index);
					}
				}else{
					//Create threads
					int total = coreNumber - this.killWorkers.size();
					for(int i=0; i<total; i++){
						AtomicBoolean kill = new AtomicBoolean(true);
						GenericConsumer<K> gc = this.serv.createSocketConsumer(r,kill);
						Log.e("Crawler","2");
						gc.start();
						workers.add(gc);
						killWorkers.add(kill);
					}
				}
			}
	//		pro.close();

//		} catch (IOException e) {
//			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
