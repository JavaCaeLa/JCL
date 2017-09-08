package implementations.dm_kernel.user;
import android.content.Context;
import android.content.pm.ApplicationInfo;

import org.jf.dexlib2.DexFileFactory;
import org.jf.dexlib2.Opcodes;
import org.jf.dexlib2.iface.ClassDef;
import org.jf.dexlib2.iface.DexFile;
import org.jf.dexlib2.immutable.ImmutableDexFile;

import implementations.collections.JCLFuture;
import implementations.collections.JCLHashMap;
import implementations.collections.JCLPFuture;
import implementations.collections.JCLSFuture;
import implementations.collections.JCLVFuture;
import implementations.dm_kernel.ConnectorImpl;
import implementations.dm_kernel.MessageGenericImpl;
import implementations.dm_kernel.MessageListGlobalVarImpl;
import implementations.dm_kernel.MessageListTaskImpl;
import implementations.dm_kernel.MessageMetadataImpl;
import implementations.dm_kernel.MessageRegisterImpl;
import implementations.dm_kernel.SimpleServer;
import implementations.dm_kernel.server.RoundRobin;
import implementations.util.JCL_ApplicationContext;
import implementations.util.XORShiftRandom;
import interfaces.kernel.JCL_connector;
import interfaces.kernel.JCL_facade;
import interfaces.kernel.JCL_message_bool;
import interfaces.kernel.JCL_message_generic;
import interfaces.kernel.JCL_message_list_global_var;
import interfaces.kernel.JCL_message_list_task;
import interfaces.kernel.JCL_message_metadata;
import interfaces.kernel.JCL_message_register;
import interfaces.kernel.JCL_result;
import interfaces.kernel.JCL_task;
import interfaces.kernel.datatype.Device;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import javassist.ClassPool;
import javassist.CtClass;
import commom.Constants;
import commom.JCL_resultImpl;
import commom.JCL_taskImpl;

/*
 *
 *
 1 public boolean register(File[] f, String classToBeExecuted) {
 2 public boolean unRegister(String nickName) {
 3 public void executeSimple(String objectNickname, Object... args) {
 4 public String execute(String objectNickname, Object... args) {
 5 public String execute(String className, String methodName, Object... args) {
 6 public JCL_result getResultBlocking(String ID) {
 7 public JCL_result getResultUnblocking(String ID) {
 8 public JCL_result removeResult(String ID) {
 9 public Object instantiateGlobalVar(String varName, Class<?> varType,
 10 public boolean instantiateGlobalVar(String varName, Object instance) {
 11 public boolean destroyGlobalVar(String varName) {
 12 public boolean setValue(String varName, Object value) {
 13 public boolean setValueUnlocking(String varName, Object value) {
 14 public JCL_result getValue(String varName) {
 15 public JCL_result getValueLocking(String varName) {
 16 public void destroy() {
 17 public boolean containsGlobalVar(String ninckName){
 18 public boolean containsTask(String ninckName){
 19 public List<String> getHosts() {

 METHOD DEPRECATED in JCL distributed version: public boolean register(Class<?> object, String nickName) {

 */

    public class JCL_FacadeImpl extends implementations.sm_kernel.JCL_FacadeImpl.Holder implements JCL_facade{

    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
    private Map<Integer,Map<String,Map<String,String>>> devices;
    //	private Map<String, Map<String, String>> devicesExec;
    private static List<Entry<String, Map<String, String>>> devicesStorage,devicesExec;
    private JCL_message_list_task msgTask = new MessageListTaskImpl();
    private static ReadWriteLock lock = new ReentrantReadWriteLock();
    private Set<String>  registerClass = new HashSet<String>();
    private static ConcurrentMap<String, JCL_message_register> jars;
    private static ConcurrentMap<String,List<String>> jarsSlaves;
    //	private static ConcurrentMap<String,String[]> slaves;
    private boolean watchExecMeth = true;
    private static JCL_facade instance;
    private SimpleServer simpleSever;
    //	private static List<String> slavesIDs;
    private static XORShiftRandom rand;
    private boolean JPF = true;
    public static String serverAdd;
    private int watchdog = 0;
    private int JPBsize = 50;
    private static JCL_facade jcl;
    public static int serverPort;
    private static int delta;
    private int port;

    protected JCL_FacadeImpl(Properties properties){
        try {
            //single pattern
            if (instance == null){
                instance = this;
            }

            //ini variables
            JPF = Boolean.valueOf(properties.getProperty("enablePBA"));
            JPBsize =  Integer.parseInt(properties.getProperty("PBAsize"));
            delta =  Integer.parseInt(properties.getProperty("delta"));
            boolean DA = Boolean.valueOf(properties.getProperty("enableDinamicUp"));
            serverAdd = properties.getProperty("serverMainAdd");
            serverPort = Integer.parseInt(properties.getProperty("serverMainPort"));
            int timeOut = Integer.parseInt(properties.getProperty("timeOut"));
            this.port = Integer.parseInt(properties.getProperty("simpleServerPort"));
            jars = new ConcurrentHashMap<String, JCL_message_register>();
            jarsSlaves = new ConcurrentHashMap<String,List<String>>();
            jcl = super.getInstance();

            //Start seed rand GV
            rand = new XORShiftRandom();

            //config connection
            ConnectorImpl.timeout = timeOut;

            //ini jcl lambari
            jcl.register(JCL_FacadeImplLamb.class, "JCL_FacadeImplLamb");

            // scheduler flush in execute
            if(JPF){
                scheduler.scheduleAtFixedRate(
                        new Runnable() {
                            public void run(){
                                try {
                                    //watchdog end bin exec
                                    if((watchdog != 0) && (watchdog == msgTask.taskSize()) && (watchExecMeth)){
                                        //Get host
                                        //Init RoundRobin
                                        Map<String, String> hostPort =RoundRobin.getDevice();
                                        String host = hostPort.get("IP");
                                        String port = hostPort.get("PORT");
                                        String mac = hostPort.get("MAC");
                                        String portS = hostPort.get("PORT_SUPER_PEER");

                                        //Register missing class
                                        for(String classReg:registerClass){
                                            if(!jarsSlaves.get(classReg).contains(host+port+mac+portS)){
                                                Object[] argsLam = {host,port,mac,portS,jars.get(classReg)};
                                                Future<JCL_result> ti =jcl.execute("JCL_FacadeImplLamb", "register", argsLam);
                                                ti.get();
                                                //									jcl.getResultBlocking(ti);
                                                jarsSlaves.get(classReg).add(host+port+mac+portS);
                                            }
                                        }

                                        //Send to host task bin
                                        Object[] argsLam = {host,port,mac,portS,msgTask};
                                        jcl.execute("JCL_FacadeImplLamb", "binexecutetask", argsLam);
                                        msgTask = new MessageListTaskImpl();
                                    }else{
                                        //update watchdog
                                        watchdog = msgTask.taskSize();
                                    }

                                } catch (Exception e) {
                                    // TODO Auto-generated catch block
                                    System.err.println("JCL facade watchdog error");
                                    e.printStackTrace();
                                }
                            }

                        },0,5, TimeUnit.SECONDS);
            }

            //Start simple server
            if(DA){
                simpleSever = new SimpleServer(this.port,devices,lock);
                simpleSever.start();
            }

            //getHosts using lambari
            int type = 5;


            //Get devices thar compose the cluster

            this.update();

            RoundRobin.ini(devicesExec);

            //finish
            System.out.println("client JCL is OK");


        } catch (Exception e) {
            System.err.println("JCL facade constructor error");
            e.printStackTrace();
        }
    }

    //Return JCL version
    //	@Override
    //	public String version(){
    //		return new String("Pacu");
    //	}

    //Get server time
    //	@Override
    //	public Long getServerTime(){
    //		try {
    //			//exec lamb
    //			Object[] argsLam = {serverAdd,serverPort};
    //			Future<JCL_result> t = jcl.execute("JCL_FacadeImplLamb", "getServerTime", argsLam);
    //			JCL_message_long mst = (JCL_message_long) (t.get()).getCorrectResult();
    //			return mst.getRegisterData()[0];
    //		} catch (Exception e) {
    //			System.err
    //					.println("JCL facade Lambari problem in getServerTime()");
    //			return null;
    //		}
    //	}


    public void update(){
        try{
            Object[] argsLam = {serverAdd, serverPort,3};
            Future<JCL_result> t = jcl.execute("JCL_FacadeImplLamb", "getSlaveIds", argsLam);
            JCL_message_generic mgh = (JCL_message_generic) (t.get()).getCorrectResult();

            Object obj[] = (Object[]) mgh.getRegisterData();
            devices = (Map<Integer, Map<String, Map<String, String>>>) obj[0];

            //Init RoundRobin
            devicesExec = new ArrayList<Entry<String, Map<String, String>>>();
            devicesStorage = new ArrayList<Entry<String, Map<String, String>>>();

            devicesExec.addAll(devices.get(2).entrySet());
            devicesExec.addAll(devices.get(3).entrySet());
            devicesExec.addAll(devices.get(6).entrySet());
            devicesExec.addAll(devices.get(7).entrySet());

            devicesStorage.addAll(devices.get(1).entrySet());
            devicesStorage.addAll(devices.get(3).entrySet());
            devicesStorage.addAll(devices.get(5).entrySet());
            devicesStorage.addAll(devices.get(7).entrySet());



            // Sorting
            Comparator com = new Comparator<Entry<String, Map<String, String>>>() {
                @Override
                public int compare(Entry<String, Map<String, String>> entry2, Entry<String, Map<String, String>> entry1)
                {
                    return  entry1.getKey().compareTo(entry2.getKey());
                }
            };

            Collections.sort(devicesExec, com);
            Collections.sort(devicesStorage, com);
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    //Register a file of jars
    @Override
    public boolean register(File[] f, String classToBeExecuted) {
        try {

            // Local register
            JCL_message_register msg = new MessageRegisterImpl();
            msg.setJars(f);
            msg.setJarsNames(f);
            msg.setClassName(classToBeExecuted);
            msg.setType(1);


            Object[] argsLam = {serverAdd, String.valueOf(serverPort),null,"0",msg};
            Future<JCL_result> t = jcl.execute("JCL_FacadeImplLamb", "register", argsLam);

            if(((Boolean)t.get().getCorrectResult()).booleanValue()){
                jars.put(classToBeExecuted, msg);
                jarsSlaves.put(classToBeExecuted, new ArrayList<String>());

                return true;

            } else{

                return false;
            }

        } catch (Exception e) {

            System.err
                    .println("problem in JCL facade register(File f, String classToBeExecuted)");
            e.printStackTrace();
            return false;
        }
    }

    //Register just class
    @Override
    public boolean register(Class<?> serviceClass, String classToBeExecuted) {
        byte[] dex = null;
        // TODO Auto-generated method stub
        try {

            String apkPath = JCL_ApplicationContext.getContext().getApplicationInfo().sourceDir;
            //String[] g = {"mail.dex", "sorting.dex", "disjkstra.dex"};
            //for (String k:g) {

            Opcodes op = Opcodes.forApi(20);
            //Carrega o dexFile
            DexFile dexFile1 = DexFileFactory.loadDexFile(apkPath, op);
            String name = "L" + serviceClass.getName().replace(".", "/") + ";";
            List<ClassDef> classes = new ArrayList<ClassDef>();

            //Cria um novo dex somente com a classe desejada
            for (ClassDef classDef : dexFile1.getClasses()) {
                if (classDef.getType().equals(name)) {
                    classes.add(classDef);
                    break;
                }
            }

            String fileName = Constants.Environment.JCLRoot()+serviceClass.getName();
            org.jf.dexlib2.iface.DexFile dexFile2 = new ImmutableDexFile(op, classes);
            DexFileFactory.writeDexFile(fileName, dexFile2);

            RandomAccessFile f = new RandomAccessFile(fileName, "r");
            dex = new byte[(int)f.length()];
            f.readFully(dex);

            //Cria um byte[] do dexFile
//            ByteArrayOutputStream bos = new ByteArrayOutputStream();
//            ObjectOutput out = null;
//            try {
//                out = new ObjectOutputStream(bos);
//                out.writeObject(new ImmutableDexFile(op, classes));
//                out.flush();
//                dex = bos.toByteArray();
//            } finally {
//                try {
//                    bos.close();
//                } catch (IOException ex) {
//                    // ignore close exception
//                }
//            }
//                FileOutputStream fos = new FileOutputStream(android.os.Environment.getExternalStorageDirectory().toString()+"/dex2/"+k);
//                fos.write(dex);
//                fos.close();
            //
            JCL_message_register msg = new MessageRegisterImpl();
            byte[][] cb = new byte[1][];
            cb[0] = dex;
            msg.setJars(cb);
            msg.setJarsNames(new String[]{serviceClass.getName()});
            msg.setClassName(classToBeExecuted);
            msg.setType(60);

            Object[] argsLam = {serverAdd, String.valueOf(serverPort), null, "0", msg};
            Future<JCL_result> t = jcl.execute("JCL_FacadeImplLamb", "register", argsLam);

            if (((Boolean) t.get().getCorrectResult()).booleanValue()) {
                jars.put(classToBeExecuted, msg);
                jarsSlaves.put(classToBeExecuted, new ArrayList<String>());

                return true;

            } else {

                return false;
            }

            //jars.put(classToBeExecuted, msg);
            //jarsSlaves.put(classToBeExecuted, new ArrayList<String>());

        } catch (Exception e) {

            System.err
                    .println("problem in JCL facade register(Class<?> serviceClass,String classToBeExecuted)");
            e.printStackTrace();
            return false;
        }
    }

    //unRegister a class
    @Override
    public boolean unRegister(String nickName) {
        boolean ok = true;
        try {
            //List host
            for(Entry<String, Map<String, String>> oneHostPort: devicesExec){

                if (jarsSlaves.get(nickName).contains(oneHostPort.getValue().get("IP")+oneHostPort.getValue().get("PORT")+oneHostPort.getValue().get("MAC")+oneHostPort.getValue().get("PORT_SUPER_PEER"))){
                    // UnRegister using lambari on host
                    Object[] argsLam = {nickName,oneHostPort.getValue().get("IP"),oneHostPort.getValue().get("PORT"),oneHostPort.getValue().get("MAC")};
                    Future<JCL_result> t = jcl.execute("JCL_FacadeImplLamb", "unRegister", argsLam);
                    if ((t.get()).getCorrectResult() != null){
                        jarsSlaves.get(nickName).remove(oneHostPort.getValue().get("IP")+oneHostPort.getValue().get("PORT")+oneHostPort.getValue().get("MAC")+oneHostPort.getValue().get("PORT_SUPER_PEER"));
                    }
                    else{
                        ok = false;
                    }
                }
            }
            //remove class
            jars.remove(nickName);
            jarsSlaves.remove(nickName);
            return ok;

        } catch (Exception e) {
            System.err.println("JCL problem in unRegister(String nickName) method");
            e.printStackTrace();

            return false;
        }
    }

    @Override
    public Future<JCL_result> execute(String objectNickname,Object... args) {
        try {
            if (!JPF){
                //Get host
                String host = null,port = null,mac = null,portS=null;


                if (jars.containsKey(objectNickname)){
                    // Get host

                    Map<String, String> hostPort = RoundRobin.getDevice();

                    host = hostPort.get("IP");
                    port = hostPort.get("PORT");
                    mac = hostPort.get("MAC");
                    portS = hostPort.get("PORT_SUPER_PEER");

                }else{

                    Object[] argsLam = {serverAdd, String.valueOf(serverPort),null,null,objectNickname};
                    Future<JCL_result> ticket = jcl.execute("JCL_FacadeImplLamb", "registerByServer", argsLam);

                    Map<String, String> hostPort = (Map<String, String>) ticket.get().getCorrectResult();

                    if(hostPort.size()==0){
                        System.err.println("No class Found!!!");
                    }

                    host = hostPort.get("IP");
                    port = hostPort.get("PORT");
                    mac = hostPort.get("MAC");
                    portS = hostPort.get("PORT_SUPER_PEER");

                    List<String> js = new ArrayList<String>();
                    js.add(host+port+mac+portS);
                    jarsSlaves.put(objectNickname,js);

                }

                //Test if host contain jar
                if(jarsSlaves.get(objectNickname).contains(host+port+mac+portS)){
                    //Just exec
                    Object[] argsLam = {objectNickname,host,port,mac,portS,new Boolean(true),args};
                    Future<JCL_result> ticket = super.execute("JCL_FacadeImplLamb", "execute", argsLam);
                    return ticket;
                } else{
                    //Exec and register
                    Object[] argsLam = {objectNickname,host,port,mac,portS,jars.get(objectNickname),new Boolean(true),args};
                    Future<JCL_result> ticket = super.execute("JCL_FacadeImplLamb", "executeAndRegister", argsLam);
                    //ticket.get();
                    jarsSlaves.get(objectNickname).add(host+port+mac+portS);
                    return ticket;
                }
            } else{
                //watch this method
                watchExecMeth = false;

                //Create bin task message
                JCL_task t = new JCL_taskImpl(null, objectNickname, args);
                Long ticket = super.createTicketH();
                t.setPort(this.port);
                msgTask.addTask(ticket,t);
                registerClass.add(objectNickname);

                //Send bin task
                if (this.msgTask.taskSize() == (JPBsize*RoundRobin.core)){
                    Map<String, String> hostPort =RoundRobin.getDevice();
                    String host = hostPort.get("IP");
                    String port = hostPort.get("PORT");
                    String mac = hostPort.get("MAC");
                    String portS = hostPort.get("PORT_SUPER_PEER");

                    //Register bin task class
                    for(String classReg:registerClass){
                        if(!jarsSlaves.get(classReg).contains(host+port+mac+portS)){
                            Object[] argsLam = {host,port,mac,portS,jars.get(classReg)};
                            Future<JCL_result> ti =jcl.execute("JCL_FacadeImplLamb", "register", argsLam);
                            ti.get();
                            jarsSlaves.get(classReg).add(host+port+mac+portS);
                        }
                    }

                    //execute lambari
                    Object[] argsLam = {host,port,mac,portS,this.msgTask};
                    jcl.execute("JCL_FacadeImplLamb", "binexecutetask", argsLam);
                    msgTask = new MessageListTaskImpl();
                }

                //watch this method
                watchExecMeth = true;
                return new JCLFuture<JCL_result>(ticket);
            }
        } catch (Exception e) {
            System.err
                    .println("JCL facade Pacu problem in execute(String className, Object... args)");
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public Future<JCL_result> execute(String objectNickname, String methodName,
                                      Object... args) {
        try {
            if (!JPF){
                //Get host
                String host = null,port = null,mac = null, portS = null;


                if (jars.containsKey(objectNickname)){
                    // Get host

                    Map<String, String> hostPort = RoundRobin.getDevice();

                    host = hostPort.get("IP");
                    port = hostPort.get("PORT");
                    mac = hostPort.get("MAC");
                    portS = hostPort.get("PORT_SUPER_PEER");

                }else{

                    Object[] argsLam = {serverAdd, String.valueOf(serverPort),null,null,objectNickname};
                    Future<JCL_result> ticket = jcl.execute("JCL_FacadeImplLamb", "registerByServer", argsLam);

                    Map<String, String> hostPort = (Map<String, String>) ticket.get().getCorrectResult();

                    if(hostPort.size()==0){
                        System.err.println("No class Found!!!");
                    }

                    host = hostPort.get("IP");
                    port = hostPort.get("PORT");
                    mac = hostPort.get("MAC");
                    portS = hostPort.get("PORT_SUPER_PEER");

                    List<String> js = new ArrayList<String>();
                    js.add(host+port+mac+portS);
                    jarsSlaves.put(objectNickname,js);

                }

                //Test if host contain jar
                if(jarsSlaves.get(objectNickname).contains(host+port+mac+portS)){
                    // Just exec
                    Object[] argsLam = {objectNickname,methodName,host,port,mac,portS,new Boolean(true),args};
                    Future<JCL_result> ticket = super.execute("JCL_FacadeImplLamb", "execute", argsLam);
                    return ticket;
                } else{
                    //Exec and register
                    Object[] argsLam = {objectNickname,methodName,host,port,mac,portS,jars.get(objectNickname),new Boolean(true),args};
                    Future<JCL_result> ticket = super.execute("JCL_FacadeImplLamb", "executeAndRegister", argsLam);
                    //	ticket.get();
                    jarsSlaves.get(objectNickname).add(host+port+mac+portS);
                    return ticket;
                }
            } else{
                //watch this method
                watchExecMeth = false;

                //Create bin task message
                JCL_task t = new JCL_taskImpl(null, objectNickname, methodName, args);
                Long ticket = super.createTicketH();
                t.setPort(this.port);
                msgTask.addTask(ticket,t);
                registerClass.add(objectNickname);

                //Send bin task
                if (this.msgTask.taskSize() == (JPBsize*RoundRobin.core)){
                    Map<String, String> hostPort =RoundRobin.getDevice();
                    String host = hostPort.get("IP");
                    String port = hostPort.get("PORT");
                    String mac = hostPort.get("MAC");
                    String portS = hostPort.get("PORT_SUPER_PEER");

                    //Register bin task class
                    for(String classReg:registerClass){
                        if(!jarsSlaves.get(classReg).contains(host+port+mac+portS)){
                            Object[] argsLam = {host,port,mac,portS,jars.get(classReg)};
                            Future<JCL_result> ti =jcl.execute("JCL_FacadeImplLamb", "register", argsLam);
                            ti.get();
                            jarsSlaves.get(classReg).add(host+port+mac+portS);
                        }
                    }

                    //execute lambari
                    Object[] argsLam = {host,port,mac,portS,this.msgTask};
                    jcl.execute("JCL_FacadeImplLamb", "binexecutetask", argsLam);
                    msgTask = new MessageListTaskImpl();
                }
                //watch this method
                watchExecMeth = true;

                return new JCLFuture<JCL_result>(ticket);
            }

        } catch (Exception e) {
            System.err
                    .println("JCL facade problem in execute(String className, String methodName, Object... args)");

            return null;
        }
    }

    @Override
    public List<Future<JCL_result>> executeAll(String objectNickname, Object... args) {
        List<Entry<String, String>> hosts;
        List<Future<JCL_result>> tickets;
        tickets = new ArrayList<Future<JCL_result>>();
        try {

            //get all host
            int[] d = {2,3,6,7};
            hosts = this.getDevices(d);

            //Exec in all host
            for (Entry<String, String> host:hosts) {
                tickets.add(this.executeOnDevice(host, objectNickname,args));
            }

            return tickets;
        } catch (Exception e) {
            System.err
                    .println("JCL facade problem in executeAll(String className, Object... args)");
            return null;
        }
    }

    @Override
    public List<Future<JCL_result>> executeAll(String objectNickname, String methodName,
                                               Object... args) {
        List<Entry<String, String>> hosts;
        List<Future<JCL_result>> tickets;
        tickets = new ArrayList<Future<JCL_result>>();
        try {
            //get all host
            int[] d = {2,3,6,7};
            hosts = this.getDevices(d);

            //Exec in all host
            for (Entry<String, String> host:hosts) {
                tickets.add(this.executeOnDevice(host, objectNickname,methodName,args));
            }

            return tickets;

        } catch (Exception e) {
            System.err
                    .println("JCL facade problem in executeAll(String objectNickname, String methodName, Object... args)");
            return null;
        }
    }

    @Override
    public List<Future<JCL_result>> executeAll(String objectNickname, Object[][] args) {
        List<Entry<String, String>> hosts;
        List<Future<JCL_result>> tickets;
        tickets = new ArrayList<Future<JCL_result>>();
        try {

            //get all host
            int[] d = {2,3,6,7};
            hosts = this.getDevices(d);

            //Exec in all host
            for (int i=0; i < hosts.size(); i++) {
                tickets.add(this.executeOnDevice(hosts.get(i), objectNickname,args[i]));
            }

            return tickets;
        } catch (Exception e){
            System.err
                    .println("JCL facade problem in executeAll(String objectNickname, Object[][] args)");
            return null;
        }
    }

    @Override
    public List<Future<JCL_result>> executeAll(String objectNickname,String methodName, Object[][] args) {
        List<Entry<String, String>> hosts;
        List<Future<JCL_result>> tickets;
        tickets = new ArrayList<Future<JCL_result>>();
        try {

            //get all host
            int[] d = {2,3,6,7};
            hosts = this.getDevices(d);

            //Exec in all host
            for (int i=0; i < hosts.size(); i++) {
                tickets.add(this.executeOnDevice(hosts.get(i), objectNickname,methodName,args[i]));
            }

            return tickets;
        } catch (Exception e) {
            System.err
                    .println("JCL facade problem in executeAll(String objectNickname,String methodName, Object[][] args)");
            return null;
        }
    }

    @Override
    public List<Future<JCL_result>> executeAllCores(String objectNickname, Object... args) {
        List<Future<JCL_result>> tickets;
        List<Entry<String, String>> hosts;
        tickets = new ArrayList<Future<JCL_result>>();
        try {

            //get all host
            //get all host
            int[] d = {2,3,6,7};
            hosts = this.getDevices(d);

            //		hosts = this.getAllDevicesCores();


            //Exec in all host
            for (int i=0; i < hosts.size(); i++) {
                //Execute o same host all cores
                Entry<String, String> device = hosts.get(i);
                int core = this.getDeviceCore(device);
                for(int j=0; j < core; j++){
                    tickets.add(this.executeOnDevice(device, objectNickname,args));
                }
            }

            return tickets;
        } catch (Exception e) {
            System.err
                    .println("JCL facade problem in executeAllCores(String objectNickname, Object... args)");
            return null;
        }
    }

    @Override
    public List<Future<JCL_result>> executeAllCores(String objectNickname,String methodName, Object... args) {
        List<Future<JCL_result>> tickets;
        List<Entry<String, String>> hosts;
        tickets = new ArrayList<Future<JCL_result>>();
        try {

            //get all host
            //get all host
            int[] d = {2,3,6,7};
            hosts = this.getDevices(d);

            //		hosts = this.getAllDevicesCores();


            //Exec in all host
            for (int i=0; i < hosts.size(); i++) {
                //Execute o same host all cores
                Entry<String, String> device = hosts.get(i);
                int core = this.getDeviceCore(device);
                for(int j=0; j < core; j++){
                    tickets.add(this.executeOnDevice(device, objectNickname,methodName,args));
                }
            }

            return tickets;
        } catch (Exception e) {
            System.err
                    .println("JCL facade problem in executeAllCores(String objectNickname,String methodName, Object... args)");
            return null;
        }
    }

    @Override
    public List<Future<JCL_result>> executeAllCores(String objectNickname,String methodName, Object[][] args) {
        List<Future<JCL_result>> tickets;
        List<Entry<String, String>> hosts;
        tickets = new ArrayList<Future<JCL_result>>();
        try {

            //get all host
            //get all host
            int[] d = {2,3,6,7};
            hosts = this.getDevices(d);

            //Exec in all host
            int cont = 0;
            for (int i=0; i < hosts.size(); i++) {
                //Execute o same host all cores
                Entry<String, String> device = hosts.get(i);
                int core = this.getDeviceCore(device);
                for(int j=0; j < core; j++){
                    tickets.add(this.executeOnDevice(device, objectNickname, methodName,args[cont]));
                    ++cont;
                }
            }

            return tickets;
        } catch (Exception e) {
            System.err
                    .println("JCL facade problem in executeAllCores(String objectNickname,String methodName, Object[][] args)");
            return null;
        }
    }

    @Override
    public List<Future<JCL_result>> executeAllCores(String objectNickname, Object[][] args) {
        List<Future<JCL_result>> tickets;
        List<Entry<String, String>> hosts;
        tickets = new ArrayList<Future<JCL_result>>();
        try {

            //get all host
            //get all host
            int[] d = {2,3,6,7};
            hosts = this.getDevices(d);

            //Exec in all host
            int cont = 0;
            for (int i=0; i < hosts.size(); i++) {
                //Execute o same host all cores
                Entry<String, String> device = hosts.get(i);
                int core = this.getDeviceCore(device);
                for(int j=0; j < core; j++){
                    tickets.add(this.executeOnDevice(device, objectNickname,args[cont]));
                    ++cont;
                }
            }

            return tickets;
        } catch (Exception e) {
            System.err
                    .println("JCL facade problem in executeAllCores(String objectNickname, Object[][] args)");
            return null;
        }
    }

    @Override
    public Future<JCL_result> executeOnDevice(Entry<String, String> device, String objectNickname,
                                              Object... args) {

        try {

            //Get host
            String host = null,port = null,mac = null, portS = null;


            if (jars.containsKey(objectNickname)){
                // Get host

                // Get host
                Map<String, String> hostPort = this.getDeviceMetadata(device);

                host = hostPort.get("IP");
                port = hostPort.get("PORT");
                mac = hostPort.get("MAC");
                portS = hostPort.get("PORT_SUPER_PEER");

            }else{

                Object[] argsLam = {serverAdd, String.valueOf(serverPort),device.getKey(),device.getValue(),objectNickname};
                Future<JCL_result> ticket = jcl.execute("JCL_FacadeImplLamb", "registerByServer", argsLam);

                Map<String, String> hostPort = (Map<String, String>) ticket.get().getCorrectResult();

                if(hostPort.size()==0){
                    System.err.println("No class Found!!!");
                }

                host = hostPort.get("IP");
                port = hostPort.get("PORT");
                mac = hostPort.get("MAC");
                portS = hostPort.get("PORT_SUPER_PEER");

                List<String> js = new ArrayList<String>();
                js.add(host+port+mac+portS);
                jarsSlaves.put(objectNickname,js);
            }

            //Test if host contain jar
            if(jarsSlaves.get(objectNickname).contains(host+port+mac+portS)){
                //Just exec
                Object[] argsLam = {objectNickname,host,port,mac,portS,new Boolean(false),args};
                Future<JCL_result> ticket = super.execute("JCL_FacadeImplLamb", "execute", argsLam);
                return ticket;
            } else{

                //Exec and register
                Object[] argsLam = {objectNickname,host,port,mac,portS,jars.get(objectNickname),new Boolean(false),args};
                Future<JCL_result> ticket = super.execute("JCL_FacadeImplLamb", "executeAndRegister", argsLam);
                //ticket.get();
                jarsSlaves.get(objectNickname).add(host+port+mac+portS);
                return ticket;
            }
        } catch (Exception e) {
            System.err
                    .println("JCL facade problem in executeOnHost(String className, Object... args)");
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public Map<String, String> getDeviceMetadata(Entry<String, String> device) {
        try {

            //getHosts
            for(Map<String, Map<String, String>> ids:devices.values()){
                for (Entry<String, Map<String, String>>  d: ids.entrySet()) {
                    if (d.getKey().equals(device.getKey()))
                        return d.getValue();
                }
            }

            System.err.println("Device not found!!!");
            return null;

        } catch (Exception e) {
            System.err.println("problem in JCL facade getHosts()");
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public boolean setDeviceMetadata(Entry<String, String> device, Map<String, String> metadata) {
        try {

            //getHosts
            for(Map<String, Map<String, String>> ids:devices.values()){
                for (Entry<String, Map<String, String>>  d: ids.entrySet()) {
                    if (d.getKey().equals(device.getKey()))
                        return true;
                }
            }

            return false;

        } catch (Exception e) {
            System.err.println("problem in JCL facade getHosts()");
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public Future<JCL_result> executeOnDevice(Entry<String, String> device, String objectNickname,
                                              String methodName, Object... args) {
        try {
            String host = null,port = null,mac = null, portS=null;

            if (jars.containsKey(objectNickname)){
                // Get host

                Map<String, String> hostPort = this.getDeviceMetadata(device);


                host = hostPort.get("IP");
                port = hostPort.get("PORT");
                mac = hostPort.get("MAC");
                portS = hostPort.get("PORT_SUPER_PEER");

            }else{

                Object[] argsLam = {serverAdd, String.valueOf(serverPort),device.getKey(),device.getValue(),objectNickname};
                Future<JCL_result> ticket = jcl.execute("JCL_FacadeImplLamb", "registerByServer", argsLam);

                Map<String, String> hostPort = (Map<String, String>) ticket.get().getCorrectResult();

                if(hostPort.size()==0){
                    System.err.println("No class Found!!!");
                }

                host = hostPort.get("IP");
                port = hostPort.get("PORT");
                mac = hostPort.get("MAC");
                portS = hostPort.get("PORT_SUPER_PEER");

                List<String> js = new ArrayList<String>();
                js.add(host+port+mac+portS);
                jarsSlaves.put(objectNickname,js);
            }


            //Test if host contain jar
            if(jarsSlaves.get(objectNickname).contains(host+port+mac+portS)){
                //Just exec
                Object[] argsLam = {objectNickname,methodName,host,port,mac,portS,new Boolean(false),args};
                Future<JCL_result> ticket = super.execute("JCL_FacadeImplLamb", "execute", argsLam);
                return ticket;
            } else{

                //Exec and register
                Object[] argsLam = {objectNickname,methodName,host,port,mac,portS,jars.get(objectNickname),new Boolean(false),args};
                Future<JCL_result> ticket = super.execute("JCL_FacadeImplLamb", "executeAndRegister", argsLam);
                //ticket.get();
                jarsSlaves.get(objectNickname).add(host+port+mac+portS);
                return ticket;
            }

        } catch (Exception e) {
            System.err
                    .println("JCL facade problem in executeOnDevice(String host,String className, String methodName, Object... args)");
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public List<JCL_result> getAllResultBlocking(List<Future<JCL_result>> ID){
        List<JCL_result> result = new ArrayList<JCL_result>(ID.size());
        //		List<Future<JCL_result>> Ids = new ArrayList<Future<JCL_result>>(ID.size());
        try {
            //result = jcl.getAllResultBlocking(ID);
            //Get Pacu results IDs

            for (Future<JCL_result> t:ID){
                JCL_result re = t.get();
                result.add(re);
            }
            //Get all Results
            //			resultF = jcl.getAllResultBlocking(Ids);

            return result;
        } catch (Exception e){
            System.err
                    .println("problem in JCL facade getAllResultBlocking(List<String> ID)");
            e.printStackTrace();
            return null;
        }
    }


    @Override
    public List<JCL_result> getAllResultUnblocking(List<Future<JCL_result>> ID) {
        //Vars
        List<JCL_result> result,resultF;
        List<Future<JCL_result>> Ids = new ArrayList<Future<JCL_result>>(ID.size());
        resultF = new ArrayList<JCL_result>(ID.size());
        try {
            //	result = jcl.getAllResultBlocking(ID);

            //Get Pacu results IDs
            for (Future<JCL_result> t:ID){
                //				long tL = Long.parseLong(t);
                JCL_result id = t.get();
                Object[] res = (Object[])id.getCorrectResult();
                Object[] arg = {((JCLFuture)t).getTicket(),res[0],res[1],res[2],res[3],res[4]};
                Ids.add(jcl.execute("JCL_FacadeImplLamb", "getResultUnblocking", arg));
            }

            //Get all Results
            for(Future<JCL_result> t:Ids){
                JCL_result res = t.get();
                if (res.getCorrectResult().equals("NULL")){
                    res.setCorrectResult(null);
                }
                resultF.add(res);
            }

            return resultF;

        } catch (Exception e) {
            System.err
                    .println("problem in JCL facade getAllResultUnblocking(String ID)");
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public JCL_result removeResult(Future<JCL_result> ID) {
        try {

            //getResultUnblocking using lambari
            Long ticket = ((JCLPFuture)ID).getTicket();

            Object[] res = (Object[])super.getResultBlocking(ticket).getCorrectResult();
            Object[] arg = {ticket,res[0],res[1],res[2],res[3],res[4]};
            Future<JCL_result> t = jcl.execute("JCL_FacadeImplLamb", "removeResult", arg);
            //			jcl.removeResult(ticket);
            super.removeResult(ticket);
            return t.get();

        } catch (Exception e) {
            System.err
                    .println("problem in JCL facade removeResult(Future<JCL_result> ID)");
            JCL_result jclr = new JCL_resultImpl();
            jclr.setErrorResult(e);
            e.printStackTrace();

            return jclr;
        }
    }


    @Override
    public boolean instantiateGlobalVar(Object key,String nickName,
                                        File[] jar, Object[] defaultVarValue) {
        lock.readLock().lock();
        try {
            //Get Host
            int hostId = rand.nextInt(delta, key.hashCode(), devicesStorage.size());
            Entry<String, Map<String, String>> hostPort = devicesStorage.get(hostId);

            String host = hostPort.getValue().get("IP");
            String port = hostPort.getValue().get("PORT");
            String mac = hostPort.getValue().get("MAC");
            String portS = hostPort.getValue().get("PORT_SUPER_PEER");


            if(!jarsSlaves.containsKey(nickName)){
                // Local register
                JCL_message_register msg = new MessageRegisterImpl();
                msg.setJars(jar);
                msg.setJarsNames(jar);
                msg.setClassName(nickName);
                msg.setType(1);
                jars.put(nickName, msg);
                jarsSlaves.put(nickName, new ArrayList<String>());
            }



            if(jarsSlaves.get(nickName).contains(host+port+mac+portS)){
                //instantiateGlobalVar using lambari
                Object[] argsLam = {key,nickName,defaultVarValue,host,port,mac,portS,hostId};
                Future<JCL_result> t = jcl.execute("JCL_FacadeImplLamb", "instantiateGlobalVar", argsLam);
                return (Boolean) (t.get()).getCorrectResult();
            }else{
                //instantiateGlobalVar using lambari
                Object[] argsLam = {key,nickName,jars.get(nickName),defaultVarValue,host,port,mac,portS,hostId};
                Future<JCL_result> t = jcl.execute("JCL_FacadeImplLamb", "instantiateGlobalVarAndReg", argsLam);
                jarsSlaves.get(nickName).add(host+port+mac+portS);
                return (Boolean)(t.get()).getCorrectResult();
            }

        } catch (Exception e) {
            System.err
                    .println("problem in JCL facade instantiateGlobalVar(Object key, String nickName,File[] jars, Object[] defaultVarValue)");
            e.printStackTrace();
            return false;
        }finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public boolean instantiateGlobalVar(Object key, Object instance){
        lock.readLock().lock();
        try {
            //Get Host
            int hostId = rand.nextInt(delta, key.hashCode(), devicesStorage.size());
            Entry<String, Map<String, String>> hostPort = devicesStorage.get(hostId);

            String host = hostPort.getValue().get("IP");
            String port = hostPort.getValue().get("PORT");
            String mac = hostPort.getValue().get("MAC");
            String portS = hostPort.getValue().get("PORT_SUPER_PEER");



            //instantiateGlobalVar using lambari
            Object[] argsLam = {key,instance,host,port,mac,portS,hostId};
            Future<JCL_result> t = jcl.execute("JCL_FacadeImplLamb", "instantiateGlobalVar", argsLam);
            return (Boolean) (t.get()).getCorrectResult();

        } catch (Exception e) {
            System.err
                    .println("problem in JCL facade instantiateGlobalVar(Object key, Object instance)");
            return false;
        }finally {
            lock.readLock().unlock();
        }
    }

    //Use on JCLHashMap to inst bins values
    protected static boolean instantiateGlobalVar(Set<Entry<?,?>> set,String clname, String gvname,boolean regClass){
        lock.readLock().lock();
        try {

            Map<Integer,JCL_message_list_global_var> gvList = new HashMap<Integer,JCL_message_list_global_var>();

            //Create bin of global vars
            for(Entry<?,?> ent:set){
                Object key = (ent.getKey().toString()+"¬Map¬"+gvname);
                Object value = ent.getValue();

                int hostId = rand.nextInt(0, key.hashCode(), devicesStorage.size());

                if (gvList.containsKey(hostId)){
                    JCL_message_list_global_var gvm = gvList.get(hostId);
                    gvm.putVarKeyInstance(key, value);
                }else{
                    JCL_message_list_global_var gvm = new MessageListGlobalVarImpl(key,value);
                    gvm.setType(35);
                    gvList.put(hostId, gvm);
                }
            }


            List<Future<JCL_result>> tick = new ArrayList<Future<JCL_result>>();

            //Create on host using lambari
            for(Entry<Integer, JCL_message_list_global_var> ent:gvList.entrySet()){
                Integer key = ent.getKey();
                JCL_message_list_global_var value = ent.getValue();

                //Get Host
                Entry<String, Map<String, String>> hostPort = devicesStorage.get(key);

                String host = hostPort.getValue().get("IP");
                String port = hostPort.getValue().get("PORT");
                String mac = hostPort.getValue().get("MAC");
                String portS = hostPort.getValue().get("PORT_SUPER_PEER");


                if (!regClass){
                    //instantiateGlobalVar using lambari
                    Object[] argsLam = {host,port,mac,portS,value,key};
                    tick.add(jcl.execute("JCL_FacadeImplLamb", "instantiateGlobalVar", argsLam));
                }else{
                    if (jarsSlaves.get(clname).contains(host+port+mac+portS)){
                        //instantiateGlobalVar using lambari
                        Object[] argsLam = {host,port,mac,portS,value,key};
                        tick.add(jcl.execute("JCL_FacadeImplLamb", "instantiateGlobalVar", argsLam));
                    }else{
                        //instantiateGlobalVar using lambari
                        Object[] argsLam = {host,port,mac,portS,value,jars.get(clname),key};
                        tick.add(jcl.execute("JCL_FacadeImplLamb", "instantiateGlobalVarAndReg", argsLam));
                        jarsSlaves.get(clname).add(host+port+mac+portS);
                    }
                }
            }

            List<JCL_result> result = jcl.getAllResultBlocking(tick);

            for(JCL_result res:result){
                if(!((Boolean)res.getCorrectResult())){
                    return false;
                }
            }

            return true;
        } catch (Exception e){
            System.err
                    .println("problem in JCL facade instantiateGlobalVar JCLHashMap.");
            return false;
        }finally {
            lock.readLock().unlock();
        }
    }

    //Create bins of request JCLHashMap.
    protected static Map<Integer,JCL_message_generic> getBinValueInterator(Set set, String gvname){
        lock.readLock().lock();
        try {

            Map<Integer,JCL_message_generic> gvList = new HashMap<Integer,JCL_message_generic>();

            //Create bin request
            for(Object k:set){
                String key = (k.toString()+"¬Map¬"+gvname);
                int hostId = rand.nextInt(0, key.hashCode(), devicesStorage.size());

                if (gvList.containsKey(hostId)){
                    JCL_message_generic gvm = gvList.get(hostId);
                    ((Set<implementations.util.Entry<String, Object>>)gvm.getRegisterData()).add(new implementations.util.Entry<String, Object>(key, k));
                }else{
                    Set<implementations.util.Entry<String, Object>> gvs = new HashSet();
                    gvs.add(new implementations.util.Entry<String, Object>(key, k));
                    JCL_message_generic gvm = new MessageGenericImpl();
                    gvm.setRegisterData(gvs);
                    gvm.setType(38);
                    gvList.put(hostId, gvm);
                }
            }

            return gvList;
        } catch (Exception e) {
            System.err
                    .println("problem in JCL facade getBinValueInterator(Set set)");
            return null;
        } finally {
            lock.readLock().unlock();
        }
    }




    //Use on JCLHashMap put method
    protected static Object instantiateGlobalVarAndReturn(Object key, Object instance,
                                                          String classVar, boolean Registers){
        // TODO Auto-generated method stub
        lock.readLock().lock();
        try {

            //Get Host
            int hostId = rand.nextInt(0, key.hashCode(), devicesStorage.size());
            Entry<String, Map<String, String>> hostPort = devicesStorage.get(hostId);

            String host = hostPort.getValue().get("IP");
            String port = hostPort.getValue().get("PORT");
            String mac = hostPort.getValue().get("MAC");
            String portS = hostPort.getValue().get("PORT_SUPER_PEER");


            if(!Registers){

                //instantiateGlobalVar using lambari
                Object[] argsLam = {key,instance,host,port,mac,portS,hostId};
                Future<JCL_result> t = jcl.execute("JCL_FacadeImplLamb", "instantiateGlobalVarReturn", argsLam);
                return (t.get()).getCorrectResult();
            }else{

                if(jarsSlaves.get(classVar).contains(host+port+mac+portS)){
                    //instantiateGlobalVar using lambari
                    Object[] argsLam = {key,instance,host,port,mac,portS,hostId};
                    Future<JCL_result> t = jcl.execute("JCL_FacadeImplLamb", "instantiateGlobalVarReturn", argsLam);
                    return  (t.get()).getCorrectResult();
                }else{
                    Object[] argsLam = {key,instance,host,port,mac,portS,jars.get(classVar),hostId};
                    Future<JCL_result> t = jcl.execute("JCL_FacadeImplLamb", "instantiateGlobalVarAndReg", argsLam);
                    jarsSlaves.get(classVar).add(host+port+mac+portS);
                    return  (t.get()).getCorrectResult();
                }
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            System.out.println("Erro in instantiateGlobalVar(Object key, Object instance,String classVar, boolean Registers)");
            e.printStackTrace();
            return false;
        } finally {
            lock.readLock().unlock();
        }
    }


    @Override
    public Future<Boolean> instantiateGlobalVarAsy(Object key,String nickName,
                                                   File[] jar, Object[] defaultVarValue) {
        lock.readLock().lock();
        try {
            //Get Host
            int hostId = rand.nextInt(delta, key.hashCode(), devicesStorage.size());
            Entry<String, Map<String, String>> hostPort = devicesStorage.get(hostId);

            String host = hostPort.getValue().get("IP");
            String port = hostPort.getValue().get("PORT");
            String mac = hostPort.getValue().get("MAC");
            String portS = hostPort.getValue().get("PORT_SUPER_PEER");


            if(!jarsSlaves.containsKey(nickName)){
                // Local register
                JCL_message_register msg = new MessageRegisterImpl();
                msg.setJars(jar);
                msg.setJarsNames(jar);
                msg.setClassName(nickName);
                msg.setType(1);
                jars.put(nickName, msg);
                jarsSlaves.put(nickName, new ArrayList<String>());
            }



            if(jarsSlaves.get(nickName).contains(host+port+mac+portS)){
                //instantiateGlobalVar using lambari
                Object[] argsLam = {key,nickName,defaultVarValue,host,port,mac,portS,hostId};
                Future<JCL_result> t = jcl.execute("JCL_FacadeImplLamb", "instantiateGlobalVar", argsLam);
                return new JCLVFuture<Boolean>(((JCLFuture)t).getTicket());
            }else{
                //instantiateGlobalVar using lambari
                Object[] argsLam = {key,nickName,jars.get(nickName),defaultVarValue,host,port,mac,portS,hostId};
                Future<JCL_result> t = jcl.execute("JCL_FacadeImplLamb", "instantiateGlobalVarAndReg", argsLam);
                jarsSlaves.get(nickName).add(host+port+mac+portS);
                return new JCLVFuture<Boolean>(((JCLFuture)t).getTicket());

            }

        } catch (Exception e) {
            System.err
                    .println("problem in JCL facade instantiateGlobalVar(Object key, String nickName,File[] jars, Object[] defaultVarValue)");
            e.printStackTrace();
            return new JCLSFuture<Boolean>(false);
        }finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public Future<Boolean> instantiateGlobalVarAsy(Object key, Object instance) {
        lock.readLock().lock();
        try {
            //Get Host
            int hostId = rand.nextInt(delta, key.hashCode(), devicesStorage.size());
            Entry<String, Map<String, String>> hostPort = devicesStorage.get(hostId);

            String host = hostPort.getValue().get("IP");
            String port = hostPort.getValue().get("PORT");
            String mac = hostPort.getValue().get("MAC");
            String portS = hostPort.getValue().get("PORT_SUPER_PEER");

            //instantiateGlobalVarAsy using lambari
            Object[] argsLam = {key,instance,host,port,mac,portS,hostId};
            Future<JCL_result> t = jcl.execute("JCL_FacadeImplLamb", "instantiateGlobalVar", argsLam);
            return new JCLVFuture<Boolean>(((JCLFuture)t).getTicket());

        } catch (Exception e) {
            System.err
                    .println("problem in JCL facade instantiateGlobalVar(String varName, Object instance)");
            return null;
        }finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public boolean instantiateGlobalVarOnDevice(Entry<String, String> device, Object key, String className, File[] jar,
                                                Object[] args){
        try {

            Map<String, String> hostPort = this.getDeviceMetadata(device);

            String host = hostPort.get("IP");
            String port = hostPort.get("PORT");
            String mac = hostPort.get("MAC");
            String portS = hostPort.get("PORT_SUPER_PEER");


            if(!jarsSlaves.containsKey(className)){
                // Local register
                JCL_message_register msg = new MessageRegisterImpl();
                msg.setJars(jar);
                msg.setJarsNames(jar);
                msg.setClassName(className);
                msg.setType(1);
                jars.put(className, msg);
                jarsSlaves.put(className, new ArrayList<String>());
            }

            Object[] argsLamS = {hostPort,className,key,jar,args,serverAdd,serverPort};
            Future<JCL_result> tS = jcl.execute("JCL_FacadeImplLamb", "instantiateGlobalVarOnHost", argsLamS);

            if (!(boolean)tS.get().getCorrectResult()){
                System.err
                        .println("problem in JCL facade instantiateGlobalVarOnHost(String host, String nickName, String varName, File[] jars, Object[] defaultVarValue)");
                System.err
                        .println("Erro in Server Register!!!!");
                return false;
            }

            if(jarsSlaves.get(className).contains(host+port+mac+portS)){
                //instantiateGlobalVar using lambari
                Object[] argsLam = {key,className,args,host,port,mac,portS,new Integer(0)};
                Future<JCL_result> t = jcl.execute("JCL_FacadeImplLamb", "instantiateGlobalVar", argsLam);
                return (Boolean) (t.get()).getCorrectResult();
            }else{
                //instantiateGlobalVar using lambari
                Object[] argsLam = {key,className,jars.get(className),args,host,port,mac,portS,new Integer(0)};
                Future<JCL_result> t = jcl.execute("JCL_FacadeImplLamb", "instantiateGlobalVarAndReg", argsLam);
                jarsSlaves.get(className).add(host+port+mac+portS);
                return (Boolean)(t.get()).getCorrectResult();
            }

        } catch (Exception e) {
            System.err
                    .println("problem in JCL facade instantiateGlobalVarOnHost(String host, String nickName, String varName, File[] jars, Object[] defaultVarValue)");
            return false;
        }
    }

    //Arrumar
    @Override
    public boolean instantiateGlobalVarOnDevice(Entry<String, String> device, Object key,
                                                Object instance) {
        try {
            //	int hostId = rand.nextInt(delta, key.hashCode(), devicesStorage.size());
            //instantiateGlobalVarHost using lambari

            for(Map.Entry<String,Map<String,String>> deviceI:devicesStorage){
                if(deviceI.getKey().equals(device.getKey())){

                    Object[] argsLam = {deviceI.getValue(),key,instance,serverAdd,serverPort};
                    Future<JCL_result> t = jcl.execute("JCL_FacadeImplLamb", "instantiateGlobalVarOnHost", argsLam);
                    return (Boolean) (t.get()).getCorrectResult();
                }
            }
            return false;

        } catch (Exception e) {
            System.err
                    .println("problem in JCL facade instantiateGlobalVarOnHost(String host, String varName, Object instance)");
            return false;
        }
    }

    @Override
    public boolean deleteGlobalVar(Object key) {
        lock.readLock().lock();
        try {

            //Get Host
            int[] t = rand.HostList(delta, key.hashCode(), devicesStorage.size());
            List<Future<JCL_result>> ticks = new ArrayList<Future<JCL_result>>();
            for(int hostId:t){
                //get host
                Entry<String, Map<String, String>> hostPort = devicesStorage.get(hostId);
                String host = hostPort.getValue().get("IP");
                String port = hostPort.getValue().get("PORT");
                String mac = hostPort.getValue().get("MAC");
                String portS = hostPort.getValue().get("PORT_SUPER_PEER");

                //destroyGlobalVar using lambari
                Object[] argsLamS = {key,serverAdd,serverPort};
                ticks.add(jcl.execute("JCL_FacadeImplLamb", "destroyGlobalVarOnHost", argsLamS));

                //destroyGlobalVar using lambari
                Object[] argsLam = {key,host,port,mac,portS,hostId};
                ticks.add(jcl.execute("JCL_FacadeImplLamb", "destroyGlobalVar", argsLam));
            }

            //return value
            for(Future<JCL_result> tick:ticks){
                JCL_result result = tick.get();
                if((Boolean)result.getCorrectResult()){
                    return 	true;
                }
            }

            return false;

        } catch (Exception e) {
            System.err.println("problem in JCL facade destroyGlobalVar(Object key)");
            return false;
        }finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public boolean setValueUnlocking(Object key, Object value) {
        lock.readLock().lock();
        try {
            //Get Host
            int[] t = rand.HostList(delta, key.hashCode(), devicesStorage.size());
            List<Future<JCL_result>> ticks = new ArrayList<Future<JCL_result>>();
            for(int hostId:t){
                Entry<String, Map<String, String>> hostPort = devicesStorage.get(hostId);
                String host = hostPort.getValue().get("IP");
                String port = hostPort.getValue().get("PORT");
                String mac = hostPort.getValue().get("MAC");
                String portS = hostPort.getValue().get("PORT_SUPER_PEER");

                //setValueUnlocking using lambari
                Object[] argsLam = {key,value,host,port,mac,portS,hostId};
                ticks.add(jcl.execute("JCL_FacadeImplLamb", "setValueUnlocking", argsLam));
            }

            //return value
            for(Future<JCL_result> tick:ticks){
                JCL_result result = tick.get();
                if((Boolean)result.getCorrectResult()){
                    return 	true;
                }
            }

            //getValue using lambari on Server
            int hostId = rand.nextInt(0, key.hashCode(), devicesStorage.size());
            Object[] argsLam = {key,value,serverAdd,serverPort,hostId};
            Future<JCL_result> tick = jcl.execute("JCL_FacadeImplLamb", "setValueUnlockingOnHost", argsLam);

            return (Boolean) (tick.get()).getCorrectResult();
        } catch (Exception e) {
            System.err.println("problem in JCL facade setValueUnlocking(Object key, Object value)");
            return false;
        }finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public JCL_result getValue(Object key) {
        lock.readLock().lock();
        try {
            //Get Host
            int[] t = rand.HostList(delta, key.hashCode(), devicesStorage.size());
            List<Future<JCL_result>> ticks = new ArrayList<Future<JCL_result>>();
            for(int hostId:t){
                Entry<String, Map<String, String>> hostPort = devicesStorage.get(hostId);

                String host = hostPort.getValue().get("IP");
                String port = hostPort.getValue().get("PORT");
                String mac = hostPort.getValue().get("MAC");
                String portS = hostPort.getValue().get("PORT_SUPER_PEER");

                //getValue using lambari
                Object[] argsLam = {key,host,port,mac,portS,hostId};
                ticks.add(jcl.execute("JCL_FacadeImplLamb", "getValue", argsLam));
            }

            for(Future<JCL_result> tick:ticks){
                JCL_result result = tick.get();
                if(!result.getCorrectResult().toString().equals("No value found!")){
                    return 	result;
                }
            }

            //getValue using lambari on Server
            int hostId = rand.nextInt(delta, key.hashCode(), devicesStorage.size());
            Object[] argsLam = {key,serverAdd,serverPort,hostId};
            Future<JCL_result> tick = jcl.execute("JCL_FacadeImplLamb", "getValueOnHost", argsLam);

            return tick.get();

        } catch (Exception e) {
            System.err.println("problem in JCL facade getValue(Object key)");
            JCL_result jclr = new JCL_resultImpl();
            jclr.setErrorResult(e);

            return jclr;
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public JCL_result getValueLocking(Object key) {
        lock.readLock().lock();
        try {
            //Get Host
            int[] t = rand.HostList(delta, key.hashCode(), devicesStorage.size());
            List<Future<JCL_result>> ticks = new ArrayList<Future<JCL_result>>();

            for(int hostId:t){
                Entry<String, Map<String, String>> hostPort = devicesStorage.get(hostId);

                String host = hostPort.getValue().get("IP");
                String port = hostPort.getValue().get("PORT");
                String mac = hostPort.getValue().get("MAC");
                String portS = hostPort.getValue().get("PORT_SUPER_PEER");

                //getValueLocking using lambari
                Object[] argsLam = {key,host,port,mac,portS,hostId};
                ticks.add(jcl.execute("JCL_FacadeImplLamb", "getValueLocking", argsLam));
            }

            for(Future<JCL_result> tick:ticks){
                JCL_result result = tick.get();
                if(!result.getCorrectResult().toString().equals("No value found!")){
                    return 	result;
                }
            }

            //getValue using lambari on Server
            int hostId = rand.nextInt(delta, key.hashCode(), devicesStorage.size());
            Object[] argsLam = {key,serverAdd,serverPort,hostId};
            Future<JCL_result> tick = jcl.execute("JCL_FacadeImplLamb", "getValueLockingOnHost", argsLam);

            return tick.get();

        } catch (Exception e){
            System.err.println("problem in JCL facade getValueLocking(Object key)");
            JCL_result jclr = new JCL_resultImpl();
            jclr.setErrorResult(e);

            return jclr;
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public void destroy() {
        try {

            scheduler.shutdown();

            if (simpleSever!=null){
                simpleSever.end();
                Object[] argsLam = {serverAdd,serverPort};
                Future<JCL_result> t = jcl.execute("JCL_FacadeImplLamb", "removeClient", argsLam);
                t.get();
            }

            ConnectorImpl.closeSocketMap();
            jcl.destroy();
            instance = null;
            jcl = null;

        } catch (Exception e) {
            System.err.println("problem in JCL facade destroy()");
            e.printStackTrace();
        }

    }

    @Override
    public boolean containsTask(String nickName){

        try {

            if (jars.containsKey(nickName))
                return true;


            Object[] argsLam = {serverAdd, String.valueOf(serverPort),null,"0",nickName};
            Future<JCL_result> t = jcl.execute("JCL_FacadeImplLamb", "containsTask", argsLam);
            return (boolean)t.get().getCorrectResult();

        } catch (Exception e) {
            System.err
                    .println("problem in JCL facade containsTask(String nickName)");

            return false;
        }
    }

    @Override
    public boolean containsGlobalVar(Object key) {
        lock.readLock().lock();
        try {
            //Get Host
            int[] t = rand.HostList(delta, key.hashCode(), devicesStorage.size());
            List<Future<JCL_result>> ticks = new ArrayList<Future<JCL_result>>();

            for(int hostId:t){
                Entry<String, Map<String, String>> hostPort = devicesStorage.get(hostId);

                String host = hostPort.getValue().get("IP");
                String port = hostPort.getValue().get("PORT");
                String mac = hostPort.getValue().get("MAC");
                String portS = hostPort.getValue().get("PORT_SUPER_PEER");


                //containsGlobalVar using lambari
                Object[] argsLam = {key,host,Integer.parseInt(port),mac,portS,hostId};
                ticks.add(jcl.execute("JCL_FacadeImplLamb", "containsGlobalVar", argsLam));
            }

            //return value
            for(Future<JCL_result> tick:ticks){
                JCL_result result = tick.get();
                if((Boolean)result.getCorrectResult()){
                    return 	true;
                }
            }

            //containsGlobalVar using lambari
            int hostId = rand.nextInt(delta, key.hashCode(), devicesStorage.size());

            Object[] argsLam = {key,serverAdd,serverPort,serverAdd,"0",hostId};
            Future<JCL_result> tick = jcl.execute("JCL_FacadeImplLamb", "containsGlobalVar", argsLam);
            return (Boolean) (tick.get()).getCorrectResult();
        } catch (Exception e) {
            System.err
                    .println("problem in JCL facade containsGlobalVar(String nickName)");
            e.printStackTrace();
            return false;
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public <T extends java.util.Map.Entry<String, String>> List<T> getDevices(){

        try {

            //getHosts

            List<T> result = new ArrayList<T>();
            for(Map<String, Map<String, String>> ids:devices.values()){
                for (Entry<String, Map<String, String>>  d: ids.entrySet()) {
                    result.add((T)new Device(d.getKey(), d.getValue().get("DEVICE_ID")));
                }
            }

            return result;

        } catch (Exception e) {
            System.err.println("problem in JCL facade getHosts()");
            e.printStackTrace();
            return null;
        }
    }

    //	@Override
    public List<Entry<String, String>> getDevices(int type[]){

        try {

            //getHosts

            List<Entry<String, String>> result = new ArrayList<Entry<String, String>>();
            for(int ids:type){
                for (Entry<String, Map<String, String>>  d: devices.get(ids).entrySet()) {
                    result.add(new implementations.util.Entry(d.getKey(), d.getValue().get("DEVICE_ID")));
                }
            }
            return result;

        } catch (Exception e) {
            System.err.println("problem in JCL facade getHosts()");
            e.printStackTrace();
            return null;
        }
    }

    public Map<String, Map<String, String>> getDevicesMetadados(int type[]){

        try {

            //getHosts

            Map<String, Map<String, String>> result = new HashMap<String, Map<String, String>>();
            for(int ids:type){
                result.putAll(devices.get(ids));
                //				for (Entry<String, Map<String, String>>  d: devices.get(ids).entrySet()) {
                //					result.add(new implementations.util.Entry(d.getKey(), d.getValue().get("DEVICE_ID")));
                //				}
            }
            return result;

        } catch (Exception e) {
            System.err.println("problem in JCL facade getHosts()");
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public int getDeviceCore(Entry<String, String> device){
        try {
            // Get host ID

            for(Map<String, Map<String, String>> ids:devices.values()){
                if (ids.containsKey(device.getKey())){
                    return Integer.parseInt(ids.get(device.getKey()).get("CORE(S)"));
                }
            }

            return 0;

        } catch (Exception e) {
            System.err.println("problem in JCL facade getDeviceCore(String hostID)");
            e.printStackTrace();
            return 0;
        }
    }

    @Override
    public int getClusterCores() {
        try {
            //var
            int core = 0;
            //sun all cores

            for(Entry<String, Map<String, String>> ids:devicesExec){
                core+=Integer.parseInt(ids.getValue().get("CORE(S)"));
            }

            return core;

        } catch (Exception e) {
            System.err.println("problem in JCL facade getClusterCores()");
            e.printStackTrace();
            return 0;
        }
    }

    @Override
    public <T extends java.util.Map.Entry<String, String>> Map<T, Integer> getAllDevicesCores() {
        try {
            //var
            Map<T, Integer> hosts = new HashMap<T, Integer>();
            List<Entry<String, String>> list = new ArrayList<>();

            //getHosts
            List<Entry<String, String>> result = new ArrayList<Entry<String, String>>();
            for(Map<String, Map<String, String>> ids:devices.values()){
                for (Entry<String, Map<String, String>>  d: ids.entrySet()) {
                    hosts.put((T) new Device(d.getKey(), d.getValue().get("DEVICE_ID")), Integer.parseInt(d.getValue().get("CORE(S)")));
                }
            }

            return hosts;

        } catch (Exception e) {
            System.err.println("problem in JCL facade getAllHostCores()");
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public boolean isLock(Object key){
        lock.readLock().lock();
        try {
            //Get Host
            int[] t = rand.HostList(delta, key.hashCode(), devicesStorage.size());
            List<Future<JCL_result>> ticks = new ArrayList<Future<JCL_result>>();
            for(int hostId:t){
                Entry<String, Map<String, String>> hostPort = devicesStorage.get(hostId);

                String host = hostPort.getValue().get("IP");
                String port = hostPort.getValue().get("PORT");
                String mac = hostPort.getValue().get("MAC");
                String portS = hostPort.getValue().get("PORT_SUPER_PEER");

                //containsGlobalVar using lambari
                Object[] argsLam = {key,host,port,mac,portS,hostId};
                ticks.add(jcl.execute("JCL_FacadeImplLamb", "isLock", argsLam));
            }

            //return value
            for(Future<JCL_result> tick:ticks){
                JCL_result result = tick.get();
                if((Boolean)result.getCorrectResult()){
                    return 	true;
                }
            }

            return false;

        } catch (Exception e) {
            System.err
                    .println("problem in JCL facade isLock(Object key)");
            return false;
        }finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public boolean cleanEnvironment() {

        try {

            //cleanEnvironment using lambari
            Object[] argsLam = {serverAdd,serverPort};
            Future<JCL_result> t = jcl.execute("JCL_FacadeImplLamb", "cleanEnvironment", argsLam);
            return (Boolean) (t.get()).getCorrectResult();

        } catch (Exception e) {
            System.err.println("problem in JCL facade cleanEnvironment()");
            e.printStackTrace();
            return false;
        }
    }

    //Get HashMap
    public static <K, V> Map<K, V> GetHashMap(String gvName){
        return new JCLHashMap<K, V>(gvName);
    }

    //Get HashMap
    public static <K, V> Map<K, V> GetHashMap(String gvName,String ClassName,File[] f){
        return new JCLHashMap<K, V>(gvName,ClassName,f);
    }

    public static JCL_facade getInstance() {
        return Holder.getInstance();
    }

    public static JCL_facade getInstancePacu() {
        Properties properties = new Properties();
        try {
            properties.load(new FileInputStream(Constants.Environment.JCLConfig()));
        } catch (IOException e) {
            e.printStackTrace();
        }

        return Holder.getInstancePacu(properties);
    }

    public static JCL_facade getInstanceLambari(){
        return Holder.getInstanceLambari();
    }
    public static class Holder extends implementations.sm_kernel.JCL_FacadeImpl.Holder{

        protected static String ServerIP(){
            return serverAdd;
        }

        protected static int ServerPort(){
            return serverPort;
        }

        protected synchronized static JCL_facade getInstance(){

            Properties properties = new Properties();
            try {
                properties.load(new FileInputStream(Constants.Environment.JCLConfig()));
            } catch (IOException e) {
                e.printStackTrace();
            }

            //get type of Instance
            if (Boolean.valueOf(properties.getProperty("distOrParell"))){
                return getInstancePacu(properties);
            }else{
                return getInstanceLambari();
            }
        }

        protected synchronized static JCL_facade getInstancePacu(Properties properties){
            //Pacu type

            if (instance == null){
                instance = new JCL_FacadeImpl(properties);
            }

            return instance;
        }

        protected synchronized static JCL_facade getInstanceLambari(){
            //Lambari type
            if (jcl == null){
                jcl = implementations.sm_kernel.JCL_FacadeImpl.getInstance();
            }
            return jcl;
        }

        protected List<Entry<String, Map<String, String>>> getDeviceS(){
            return devicesStorage;
        }

        //create hash key map
        protected boolean createhashKey(String gvName,String clName,boolean Regclass, int IDhost){

            //Get Ip host
            Entry<String, Map<String, String>> hostPort = devicesStorage.get(IDhost);


            String host = hostPort.getValue().get("IP");
            String port = hostPort.getValue().get("PORT");
            String mac = hostPort.getValue().get("MAC");
            String portS = hostPort.getValue().get("PORT_SUPER_PEER");




            JCL_message_register msgReg = null;

            if((Regclass) && (!jarsSlaves.get(clName).contains(host+port+mac+portS))){
                //instantiateGlobalVar using lambari
                msgReg = jars.get(clName);
                jarsSlaves.get(clName).add(host+port+mac+portS);
            }


            //Create connection
            JCL_connector controlConnector = new ConnectorImpl();
            controlConnector.connect(host,Integer.parseInt(port),mac);

            //createhashKey using lambari
            JCL_message_generic mc = new MessageGenericImpl();
            Object[] data = {gvName,Regclass,msgReg};
            mc.setRegisterData(data);
            mc.setType(28);
            JCL_message_generic mr = (JCL_message_generic) controlConnector.sendReceiveG(mc,portS);
            controlConnector.disconnect();
            return (Boolean) mr.getRegisterData();
        }

        //add key to hash key map
        protected boolean hashAdd(String gvName,Object Key, int IDhost){


            //Get Ip host
            Entry<String, Map<String, String>> hostPort = devicesStorage.get(IDhost);

            String host = hostPort.getValue().get("IP");
            String port = hostPort.getValue().get("PORT");
            String mac = hostPort.getValue().get("MAC");
            String portS = hostPort.getValue().get("PORT_SUPER_PEER");

            //hashAdd using lambari
            JCL_message_generic mc = new MessageGenericImpl();
            Object[] ob = {gvName,Key};
            mc.setRegisterData(ob);
            mc.setType(29);
            JCL_connector controlConnector = new ConnectorImpl();
            controlConnector.connect(host,Integer.parseInt(port),mac);
            JCL_message_generic mr = (JCL_message_generic) controlConnector.sendReceiveG(mc,portS);
            controlConnector.disconnect();
            return (Boolean) mr.getRegisterData();
        }

        //add key list to hash key map
        protected boolean hashAdd(String gvName,java.util.Map.Entry<String, String> hostIp,List<Object> keys, int IDhost){

            //Get Ip host
            Entry<String, Map<String, String>> hostPort = devicesStorage.get(IDhost);

            String host = hostPort.getValue().get("IP");
            String port = hostPort.getValue().get("PORT");
            String mac = hostPort.getValue().get("MAC");
            String portS = hostPort.getValue().get("PORT_SUPER_PEER");

            // hashAdd using lambari
            JCL_message_generic mc = new MessageGenericImpl();
            Object[] ob = {gvName,keys};
            mc.setRegisterData(ob);
            mc.setType(36);
            JCL_connector controlConnector = new ConnectorImpl();
            controlConnector.connect(host,Integer.parseInt(port),mac);
            JCL_message_generic mr = (JCL_message_generic) controlConnector.sendReceiveG(mc, portS);
            controlConnector.disconnect();
            return (Boolean) mr.getRegisterData();
        }

        //remove key from hash key map
        protected boolean hashRemove(String gvName,Object Key, int IDhost){
            //Get Ip host
            Entry<String, Map<String, String>> hostPort = devicesStorage.get(IDhost);

            String host = hostPort.getValue().get("IP");
            String port = hostPort.getValue().get("PORT");
            String mac = hostPort.getValue().get("MAC");
            String portS = hostPort.getValue().get("PORT_SUPER_PEER");

            //hashRemove using lambari
            JCL_message_generic mc = new MessageGenericImpl();
            Object[] ob = {gvName,Key};
            mc.setRegisterData(ob);
            mc.setType(30);
            JCL_connector controlConnector = new ConnectorImpl();
            controlConnector.connect(host,Integer.parseInt(port),mac);
            JCL_message_generic mr = (JCL_message_generic) controlConnector.sendReceiveG(mc,portS);
            controlConnector.disconnect();
            return (Boolean) mr.getRegisterData();
        }

        //hash key map contain key
        protected boolean containsKey(String gvName,Object Key, int IDhost){

            //Get Ip host
            Entry<String, Map<String, String>> hostPort = devicesStorage.get(IDhost);

            String host = hostPort.getValue().get("IP");
            String port = hostPort.getValue().get("PORT");
            String mac = hostPort.getValue().get("MAC");
            String portS = hostPort.getValue().get("PORT_SUPER_PEER");

            //containsKey using lambari
            JCL_message_generic mc = new MessageGenericImpl();
            Object[] ob = {gvName,Key};
            mc.setRegisterData(ob);
            mc.setType(31);
            JCL_connector controlConnector = new ConnectorImpl();
            controlConnector.connect(host,Integer.parseInt(port),mac);
            JCL_message_generic mr = (JCL_message_generic) controlConnector.sendReceiveG(mc,portS);
            controlConnector.disconnect();
            return (Boolean) mr.getRegisterData();
        }

        //hash key map size
        protected int hashSize(String gvName, int IDhost){

            //Get Ip host
            Entry<String, Map<String, String>> hostPort = devicesStorage.get(IDhost);

            String host = hostPort.getValue().get("IP");
            String port = hostPort.getValue().get("PORT");
            String mac = hostPort.getValue().get("MAC");
            String portS = hostPort.getValue().get("PORT_SUPER_PEER");

            //hashSize using lambari
            JCL_message_generic mc = new MessageGenericImpl();
            mc.setRegisterData(gvName);
            mc.setType(32);
            JCL_connector controlConnector = new ConnectorImpl();
            controlConnector.connect(host,Integer.parseInt(port),mac);
            JCL_message_generic mr = (JCL_message_generic) controlConnector.sendReceiveG(mc,portS);
            controlConnector.disconnect();
            return (Integer) mr.getRegisterData();
        }

        //clean hash key map
        protected Set hashClean(String gvName, int IDhost){
            //Get Ip host
            Entry<String, Map<String, String>> hostPort = devicesStorage.get(IDhost);

            String host = hostPort.getValue().get("IP");
            String port = hostPort.getValue().get("PORT");
            String mac = hostPort.getValue().get("MAC");
            String portS = hostPort.getValue().get("PORT_SUPER_PEER");

            //hashClean using lambari
            JCL_message_generic mc = new MessageGenericImpl();
            mc.setRegisterData(gvName);
            mc.setType(33);
            JCL_connector controlConnector = new ConnectorImpl();
            controlConnector.connect(host,Integer.parseInt(port),mac);
            JCL_message_generic mr = (JCL_message_generic) controlConnector.sendReceiveG(mc,portS);
            controlConnector.disconnect();
            return (Set) mr.getRegisterData();
        }

        //get set of keys
        protected Set getHashSet(String gvName, int IDhost){

            //Get Ip host
            Entry<String, Map<String, String>> hostPort = devicesStorage.get(IDhost);

            String host = hostPort.getValue().get("IP");
            String port = hostPort.getValue().get("PORT");
            String mac = hostPort.getValue().get("MAC");
            String portS = hostPort.getValue().get("PORT_SUPER_PEER");

            //getHashSet using lambari
            JCL_message_generic mc = new MessageGenericImpl();
            mc.setRegisterData(gvName);
            mc.setType(34);
            JCL_connector controlConnector = new ConnectorImpl();
            controlConnector.connect(host,Integer.parseInt(port),mac);
            JCL_message_generic mr = (JCL_message_generic) controlConnector.sendReceiveG(mc,portS);
            controlConnector.disconnect();
            return (Set) mr.getRegisterData();
        }

        //Inst key and values bins
        protected boolean instantiateBin(Object object,String clname,String gvname,boolean regClass){
            return instantiateGlobalVar((Set<Entry<?, ?>>) object,clname,gvname,regClass);
        }

        //put on cluster
        protected Object hashPut(Object key, Object instance,
                                 String classVar, boolean Registers){
            return instantiateGlobalVarAndReturn(key,instance,classVar,Registers);
        }

        //Get queue interator
        protected Map<Integer,JCL_message_generic> getHashQueue(Queue queue,Set key, String gvname){
            try {
                Map<Integer,JCL_message_generic> gvList = getBinValueInterator(key, gvname);

                //getHashQueue using lambari
                Iterator<Entry<Integer,JCL_message_generic>> intGvList = gvList.entrySet().iterator();

                if (intGvList.hasNext()){

                    Entry<Integer,JCL_message_generic> entHost = intGvList.next();
                    JCL_message_generic mc = entHost.getValue();

                    //Get Host
                    Entry<String, Map<String, String>> hostPort = devicesStorage.get(entHost.getKey());

                    String host = hostPort.getValue().get("IP");
                    String port = hostPort.getValue().get("PORT");
                    String mac = hostPort.getValue().get("MAC");
                    String portS = hostPort.getValue().get("PORT_SUPER_PEER");


                    //Using lambari
                    Object[] argsLam = {mc,queue,host,port,mac,portS,entHost.getKey()};
                    Future<JCL_result> t = jcl.execute("JCL_FacadeImplLamb", "getHashValues", argsLam);
                    t.get();
                    //			jcl.getResultBlocking(t).getCorrectResult();

                    intGvList.remove();
                }

                return gvList;
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                return null;
            }
        }

        //get value from cluster
        protected Future<JCL_result> getHashValues(Queue queue,JCL_message_generic mc, int key){
            //Get Host
            Entry<String, Map<String, String>> hostPort = devicesStorage.get(key);
            String host = hostPort.getValue().get("IP");
            String port = hostPort.getValue().get("PORT");
            String mac = hostPort.getValue().get("MAC");
            String portS = hostPort.getValue().get("PORT_SUPER_PEER");


            //Using lambari
            Object[] argsLam = {mc,queue,host,port,mac,portS,key};
            Future<JCL_result> t = jcl.execute("JCL_FacadeImplLamb", "getHashValues", argsLam);

            return t;
        }


        protected Object getResultBlocking(Future<JCL_result> t){
            try {
                return (t.get()).getCorrectResult();
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                return null;
            }
        }

        protected JCL_result getResultBlocking(Long ID) {
            try {

                JCL_result result,resultF;

                //Using lambari to get result

                result = super.getResultBlocking(ID);
                Object[] res = (Object[])result.getCorrectResult();
                Object[] arg = {(ID),res[0],res[1],res[2],res[3],res[4]};
                Future<JCL_result> ticket = jcl.execute("JCL_FacadeImplLamb", "getResultBlocking", arg);
                resultF = ticket.get();

                return resultF;

            } catch (Exception e) {
                System.err
                        .println("problem in JCL facade getResultBlocking(String ID)");
                JCL_result jclr = new JCL_resultImpl();
                jclr.setErrorResult(e);
                return jclr;
            }
        }


    }

    @Override
    public Map<String, String> getDeviceConfig(Entry<String, String> deviceNickname) {
        try {
            Map<String, String> hostData = this.getDeviceMetadata(deviceNickname);

            String DeviceIP = hostData.get("IP");
            String DevicePort = hostData.get("PORT");
            String MAC = hostData.get("MAC");
            String portSP = hostData.get("PORT_SUPER_PEER");

            JCL_message_metadata msg = new MessageMetadataImpl();

            msg.setType(42);

            JCL_connector controlConnector = new ConnectorImpl(false);
            controlConnector.connect(DeviceIP,Integer.parseInt(DevicePort),MAC);

            JCL_message_metadata jclR = (JCL_message_metadata) controlConnector.sendReceiveG(msg, portSP);

            if(jclR != null){
                JCL_connector conn = new ConnectorImpl(false);
                conn.connect(Holder.ServerIP(), Holder.ServerPort(),null);
                JCL_message_metadata jclRe = (JCL_message_metadata) controlConnector.sendReceiveG(msg, portSP);
                return jclR.getMetadados();
            }

        } catch (Exception e) {
            System.err.println("Problem at JCL in getDeviceMetadata(Entry<String, String> deviceNickname)");
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public boolean setDeviceConfig(Entry<String, String> deviceNickname, Map<String, String> metadata) {
        try {

            if(metadata==null)return false;

            Map<String, String> hostData = this.getDeviceMetadata(deviceNickname);

            String deviceIP = hostData.get("IP");
            String devicePort = hostData.get("PORT");
            String MAC = hostData.get("MAC");
            String portSP = hostData.get("PORT_SUPER_PEER");
            JCL_message_metadata msg = new MessageMetadataImpl();

            msg.setType(43);

            msg.setMetadados(metadata);

            JCL_connector controlConnector = new ConnectorImpl(false);
            controlConnector.connect(deviceIP,Integer.parseInt(devicePort), MAC);

            JCL_message_bool jclR = (JCL_message_bool) controlConnector.sendReceiveG(msg, portSP);

            if(jclR.getRegisterData()[0]){
                JCL_connector conn = new ConnectorImpl(false);
                conn.connect(Holder.ServerIP(), Holder.ServerPort(), MAC);
                JCL_message_bool jclRe = (JCL_message_bool) controlConnector.sendReceiveG(msg, portSP);
                return true;
            } else{
                return false;
            }
        } catch (Exception e){
            System.err.println("Problem at JCL in setDeviceMetadata(Entry<String, String> deviceNickname, Map<String, String> metadata)");
            e.printStackTrace();
            return false;
        }
    }
}