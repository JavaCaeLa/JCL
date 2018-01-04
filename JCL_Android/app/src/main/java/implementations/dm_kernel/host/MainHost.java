package implementations.dm_kernel.host;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import com.hpc.jcl_android.JCL_ANDROID_Facade;

import implementations.collections.JCLHashMap;
import implementations.dm_kernel.ConnectorImpl;
import implementations.dm_kernel.MessageControlImpl;
import implementations.dm_kernel.MessageMetadataImpl;
import implementations.dm_kernel.Server;
import implementations.sm_kernel.JCL_FacadeImpl;
import implementations.sm_kernel.JCL_orbImpl;
import implementations.sm_kernel.PacuResource;
import implementations.util.DirCreation;
import interfaces.kernel.JCL_connector;
import interfaces.kernel.JCL_message_control;
import interfaces.kernel.JCL_message_get_host;
import interfaces.kernel.JCL_message_metadata;
import interfaces.kernel.JCL_result;
import interfaces.kernel.JCL_task;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import commom.GenericConsumer;
import commom.GenericResource;
import commom.JCL_handler;

public class MainHost extends Server {
    private String hostPort;
    private static String nic;
    //private Map<String, String> hostIp;
    //private String[] hostIp = new String[5];
    private static Map<String, String> metaData;
    static boolean twoStep = false;
    private HashSet<String> TaskContain;
    private Map<Long, JCL_result> results;
    private ConcurrentHashMap<String, Set<Object>> JclHashMap;
    private GenericResource<JCL_task> rp;
    private ConcurrentHashMap<Long, String> JCLTaskMap;
    private ConcurrentMap<String, String[]> slaves;
    private List<String> slavesIDs;
    private AtomicLong taskID;
    private String serverAdd;
    private int serverPort;
    private Context context;
    private String myIp;
    //private Map<String, String> hostIp2;
    private static MainHost main;
    private JCL_message_get_host message_host;
    private JCL_FacadeImpl jcl;
    private AtomicInteger registerMsg;

    /**
     * @param args
     */
    public static void main(String[] args, Context context, JCL_message_get_host msgr) {
        // TODO Auto-generated method stub
        // Read properties file.
        //Properties properties = new Properties();
//		try {
//		    properties.load(new FileInputStream("../jcl_conf/config.properties"));
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//		int hostPort = Integer.parseInt(properties.getProperty("hostPort"));
//		nic = properties.getProperty("nic");
//		twoStep = Boolean.parseBoolean(properties.getProperty("twoStep").trim());
//		int byteBuffer = Integer.parseInt(properties.getProperty("byteBuffer"));
//		String deviceID = properties.getProperty("deviceID");


        //JCL_handler.buffersize = 2097152;
        //5995992
        //JCL_handler.buffersize = 10000;
        //ConnectorImpl.buffersize = 10000;
        //commom.JCL_connector.buffersize = 10000;


//		int timeOut = Integer.parseInt(properties.getProperty("timeOut"));
        File sdCard = Environment.getExternalStorageDirectory();
        DirCreation.createDirs(sdCard.getAbsolutePath().toString() + "/jcl_temp/");
        //DirCreation.createDirs("../user_jars/");

//		connect = new ConcurrentHashMap<String,SocketChannel>();
//		ConnectorImpl.setSocketConst(connect,timeOut);	
//		ConnectorImpl.setSocketConst(timeOut);	

        try {

            main = new MainHost(JCL_ANDROID_Facade.getInstance().getMyPort(), JCL_ANDROID_Facade.getInstance().getDevice(), context, msgr);
            main.start();

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public MainHost(int port, String deviceID, Context context, JCL_message_get_host msgr) throws IOException {
        super(port);
        this.setMessage_host(msgr);
        this.context = context;
        this.hostPort = Integer.toString(port);
        ;
//		this.hostPort = Integer.toString(port);
//		this.metaData = getNameIPPort();
//		this.metaData.put("DEVICE_TYPE","5");
//		this.metaData.put("DEVICE_ID",deviceID);
    }

    private void start() {
        JCL_ANDROID_Facade jcl = JCL_ANDROID_Facade.getInstance();


        this.metaData = jcl.getMetadata(context);
        //this.hostIp[0] = metaData.get("IP");
        //this.hostIp[1] = metaData.get("PORT");
        //this.hostIp[2] = metaData.get("MAC");
        //this.hostIp[3] = metaData.get("CORE(S)");
        //this.hostIp[4] = metaData.get("DEVICE_TYPE");
        //hostIp2 = hostIp;
        this.slavesIDs = new LinkedList<String>();
        this.slaves = new ConcurrentHashMap<String, String[]>();
        this.rp = new PacuResource<JCL_task>(this.slavesIDs, this.slaves, twoStep);
        //	this.jcl = JCL_FacadeImpl.getInstance();
        //	List<String> hosts = JCL_FacadeImpl.Holder.getInstancePacu().getHosts();
        this.JCLTaskMap = new ConcurrentHashMap<Long, String>();
        this.TaskContain = new HashSet<String>();
        this.results = new ConcurrentHashMap<Long, JCL_result>();
        this.JclHashMap = new ConcurrentHashMap<String, Set<Object>>();
        this.taskID = new AtomicLong();
        this.jcl = (JCL_FacadeImpl)JCL_FacadeImpl.Holder.getInstancePacu(rp);

        this.registerMsg = new AtomicInteger();
        JCL_handler.setRegisterMsg(registerMsg);
        JCL_orbImpl.setRegisterMsg(registerMsg);

        Log.e("Socket", "Begin");
        this.begin();
    }

    @Override
    protected void beforeListening() {


        // Read properties file.
//		final Properties properties = new Properties();
//		try {
//		    properties.load(new FileInputStream("../jcl_conf/config.properties"));
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
        JCL_ANDROID_Facade jcl = JCL_ANDROID_Facade.getInstance();
        try {
            final String serverIpPort[];

            serverIpPort = jcl.getIpPort(context);

            serverAdd = serverIpPort[0];
            serverPort = Integer.parseInt(serverIpPort[1]);
            myIp = jcl.getMyIp(context);
        } catch (IOException e) {
            e.printStackTrace();
        }

//		serverPort = Integer.parseInt(properties.getProperty("serverMainPort"));
//		final int superPeerPort = Integer.parseInt(properties.getProperty("superPeerMainPort"));

//        Thread threadRegister = new Thread() {
//            public void run() {
//		    	JCL_connector controlConnector = new ConnectorImpl(false);
//		    	if(!controlConnector.connect(serverAdd, serverPort,null)){
//		    		serverPort = Integer.parseInt(serverIpPort[1]);
//		    		controlConnector.connect(serverAdd, serverPort,null);
//		    	}
//		    	JCL_message_control msg = new MessageControlImpl()
//;
//                JCL_ANDROID_Facade jcl = JCL_ANDROID_Facade.getInstance();
//                MessageMetadataImpl m = new MessageMetadataImpl();
//
//                m.setType(-1);
//                m.setMetadados(jcl.getMetadata(context));
//
//                ConnectorImpl co = new ConnectorImpl();
//                co.connect(serverAdd, serverPort, null);
//                JCL_message_get_host msgr = (JCL_message_get_host) co.sendReceiveG(m, null);


        if ((message_host.getSlaves() != null)) {
            //	((PacuResource)rp).setSlaves(slaves);
            //	((PacuResource)rp).setSlavesIDs(slavesIDs);
            slaves.putAll(message_host.getSlaves());
            slavesIDs.addAll(message_host.getSlavesIDs());
            ((PacuResource) rp).setHostIp(metaData);
            rp.wakeup();
            System.out.println("HOST JCL is OK");
        } else System.err.println("HOST JCL NOT STARTED");


//                System.out.println(m.getMetadados());

        //ShutDownHook();
//                co.disconnect();
//                jcl.wakeUp("Connection");
//            }
//        };
//        threadRegister.start();
    }

//	private String[] getNameIPPort(){

    @Override
    protected void duringListening() {
        // TODO Auto-generated method stub
    }

    @Override
    public <K extends JCL_handler> GenericConsumer<K> createSocketConsumer(
            GenericResource<K> r, AtomicBoolean kill) {
        // TODO Auto-generated method stub
        String hostID = this.metaData.get("MAC")+this.metaData.get("PORT");
        return new SocketConsumer<K>(r,kill,TaskContain,hostID,results,this.taskID,this.JclHashMap,this.rp,this.JCLTaskMap,this.jcl);
    }

    private void ShutDownHook() {
        Runtime.getRuntime().addShutdownHook(new Thread() {

            @Override
            public void run() {
                String[] info = {myIp, serverAdd, serverPort + ""};
                unRegister(info);
            }
        });
    }

    public static void unRegister(final String[] info) {
//        JCL_FacadeImpl.getInstance().destroy();
//        JCL_orbImpl.getInstance().cleanEnvironment();
//        JCL_orbImpl.getInstancePacu().cleanEnvironment();

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    JCL_ANDROID_Facade jcl = JCL_ANDROID_Facade.getInstance();
                    JCL_message_metadata msg = new MessageMetadataImpl();
                    msg.setType(-2);
                    msg.setMetadados(metaData);
                    JCL_connector controlConnector = new ConnectorImpl(false);
                    controlConnector.connect(info[1], Integer.parseInt(info[2]), null);
                    JCL_message_control msgr = (JCL_message_control) controlConnector.sendReceiveG(msg,null);
                    if (msgr.getRegisterData().length == 1) {
                        System.out.println("HOST JCL WAS UNREGISTERED!");
                    } else System.err.println("HOST JCL WAS NOT UNREGISTERED!");
                    JCL_FacadeImpl.getInstance().destroy();
                    implementations.dm_kernel.user.JCL_FacadeImpl.getInstance().destroy();
                    JCL_orbImpl.getInstance().cleanEnvironment();
                    JCL_orbImpl.getInstancePacu().cleanEnvironment();
                    controlConnector.disconnect();
                    ConnectorImpl.closeSocketMap();
                    MainHost.closeServer();
                } catch (Exception e) {
                    JCL_FacadeImpl.getInstance().destroy();
                    implementations.dm_kernel.user.JCL_FacadeImpl.getInstance().destroy();
                    JCL_orbImpl.getInstance().cleanEnvironment();
                    JCL_orbImpl.getInstancePacu().cleanEnvironment();

                    System.err.println("Erro in unregister host!");
                    try{
                        JCL_connector controlConnector = new ConnectorImpl(false);
                        controlConnector.disconnect();
                        ConnectorImpl.closeSocketMap();
                        MainHost.closeServer();
                    }catch (Exception e1){
                    }finally {
                        JCL_FacadeImpl.getInstance().destroy();
                        implementations.dm_kernel.user.JCL_FacadeImpl.getInstance().destroy();
                        JCL_orbImpl.getInstance().cleanEnvironment();
                        JCL_orbImpl.getInstancePacu().cleanEnvironment();
                        MainHost.closeServer();
                    }
                }
            }

        }).start();

    }

    public static void unRegisterSync(final String[] info) {


        try {
            JCL_ANDROID_Facade jcl = JCL_ANDROID_Facade.getInstance();
            JCL_message_metadata msg = new MessageMetadataImpl();
            msg.setType(-2);
            msg.setMetadados(metaData);
            JCL_connector controlConnector = new ConnectorImpl(false);
            controlConnector.connect(info[1], Integer.parseInt(info[2]), null);
            JCL_message_control msgr = (JCL_message_control) controlConnector.sendReceiveG(msg,null);
            if (msgr.getRegisterData().length == 1) {
                System.out.println("HOST JCL WAS UNREGISTERED!");
            } else System.err.println("HOST JCL WAS NOT UNREGISTERED!");
            JCL_FacadeImpl.getInstance().destroy();
            controlConnector.disconnect();
            ConnectorImpl.closeSocketMap();
            MainHost.closeServer();
        } catch (Exception e) {
            JCL_FacadeImpl.getInstance().destroy();
            System.err.println("Erro in unregister host!");
        }

    }

    public static void closeServer() {
        if (main!=null) {
            main.getServerR().stopServer();
            main.getServerR().setFinished();
            main.closeSocket();
        }

    }

    public JCL_message_get_host getMessage_host() {
        return message_host;
    }

    public void setMessage_host(JCL_message_get_host message_host) {
        this.message_host = message_host;
    }
}
