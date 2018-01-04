package implementations.dm_kernel.server;


import implementations.dm_kernel.ConnectorImpl;
import implementations.dm_kernel.Server;
import implementations.dm_kernel.router.Router;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicBoolean;
import commom.GenericConsumer;
import commom.GenericResource;
import commom.JCL_handler;

public class MainServer extends Server{
	
//	private ConcurrentMap<String,String[]> slaves,jarsName;
	private ConcurrentMap<Integer,ConcurrentMap<String,String[]>> slaves_IoT,jarsName_IoT;
	private ConcurrentMap<Integer,ConcurrentMap<String,Map<String,String>>> metadata_IoT;
	private ConcurrentMap<Object,String[]> globalVarSlaves;
	private ConcurrentMap<String,List<String>> jarsSlaves;
	private ConcurrentMap<Integer,List<String>> slavesIDs_IoT;

	//	private static ConcurrentMap<String,SocketChannel> connect;
	private ConcurrentMap<String,byte[][]> jars;
	private ConcurrentMap<String,String[]> runningUser;	
//	private List<String> slavesIDs;
	private static Boolean verbose;
	private static String nic;
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

//		connect = new ConcurrentHashMap<String,SocketChannel>();
		

		
		// Read properties file.
		Properties properties = new Properties();
		try {
		    properties.load(new FileInputStream("../jcl_conf/config.properties"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		int serverPort = Integer.parseInt(properties.getProperty("serverMainPort"));
		int routerPort = Integer.parseInt(properties.getProperty("routerMainPort"));
		int byteBuffer = Integer.parseInt(properties.getProperty("byteBuffer"));
		
		//JCL_handler.buffersize = byteBuffer;
		//ConnectorImpl.buffersize = byteBuffer;
		//commom.JCL_connector.buffersize = byteBuffer;
		
		nic = properties.getProperty("nic");

//		int timeOut = Integer.parseInt(properties.getProperty("timeOut"));		
		verbose =  Boolean.parseBoolean(properties.getProperty("verbose"));
//		ConnectorImpl.setSocketConst(connect,timeOut);
//		ConnectorImpl.setSocketConst(timeOut);
		
		try {
			new MainServer(serverPort,routerPort);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public MainServer(int portS, int portR) throws IOException{
		
		//Start Server
		super(portS);

		this.globalVarSlaves = new ConcurrentHashMap<Object, String[]>();
//		this.slavesIDs = new LinkedList<String>();
		this.slavesIDs_IoT = new ConcurrentHashMap<Integer,List<String>>();
		this.slaves_IoT = new ConcurrentHashMap<Integer,ConcurrentMap<String,String[]>>();
		this.jarsName_IoT = new ConcurrentHashMap<Integer,ConcurrentMap<String,String[]>>();
		this.metadata_IoT = new ConcurrentHashMap<Integer,ConcurrentMap<String,Map<String,String>>>();		
//		this.slaves = new ConcurrentHashMap<String, String[]>();
		this.jarsSlaves = new ConcurrentHashMap<String,List<String>>();
//		this.jarsName = new ConcurrentHashMap<String, String[]>();
		this.jars = new ConcurrentHashMap<String, byte[][]>();
		this.runningUser = new ConcurrentHashMap<String, String[]>();
		
		System.err.println("JCL server ok!");
		
		//Router Super-Peer 		
		new Thread(new Router(portR,super.getServerR(),nic)).start();
		System.err.println("JCL router ok!");
		
		this.begin();

				
	}

	@Override
	protected void beforeListening() {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void duringListening() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public <K extends JCL_handler> GenericConsumer<K> createSocketConsumer(
			GenericResource<K> r, AtomicBoolean kill){
		// TODO Auto-generated method stub
		return new SocketConsumer<K>(r,kill, this.globalVarSlaves, this.slavesIDs_IoT, this.slaves_IoT,this.jarsSlaves,this.jarsName_IoT,this.jars,verbose,runningUser,metadata_IoT);

	}

}
