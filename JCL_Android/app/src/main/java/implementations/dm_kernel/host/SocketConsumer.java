
package implementations.dm_kernel.host;

import android.os.Environment;
import android.util.Log;

import com.hpc.jcl_android.JCL_ANDROID_Facade;
//import com.hpc.jcl_android.SuperContext;

//import net.bytebuddy.implementation.bind.annotation.Super;

import org.jf.dexlib2.DexFileFactory;

import commom.JCL_taskImpl;
import dalvik.system.DexClassLoader;
import implementations.collections.JCLFuture;
import implementations.dm_kernel.ConnectorImpl;
import implementations.dm_kernel.IoTuser.JCL_Action;
import implementations.dm_kernel.MessageBoolImpl;
import implementations.dm_kernel.MessageControlImpl;
import implementations.dm_kernel.MessageGenericImpl;
import implementations.dm_kernel.MessageGlobalVarImpl;
import implementations.dm_kernel.MessageImpl;
import implementations.dm_kernel.MessageResultImpl;
import implementations.dm_kernel.MessageSensorImpl;
import implementations.dm_kernel.MessageTaskImpl;
import implementations.sm_kernel.JCL_FacadeImpl;
import implementations.sm_kernel.JCL_orbImpl;
import implementations.sm_kernel.PacuResource;
import implementations.util.JCL_ApplicationContext;
import interfaces.kernel.JCL_connector;
import interfaces.kernel.JCL_message;
import interfaces.kernel.JCL_message_bool;
import interfaces.kernel.JCL_message_commons;
import interfaces.kernel.JCL_message_control;
import interfaces.kernel.JCL_message_generic;
import interfaces.kernel.JCL_message_global_var;
import interfaces.kernel.JCL_message_global_var_obj;
import interfaces.kernel.JCL_message_list_global_var;
import interfaces.kernel.JCL_message_list_task;
import interfaces.kernel.JCL_message_long;
import interfaces.kernel.JCL_message_metadata;
import interfaces.kernel.JCL_message_register;
import interfaces.kernel.JCL_message_result;
import interfaces.kernel.JCL_message_task;
import interfaces.kernel.JCL_orb;
import interfaces.kernel.JCL_result;
import interfaces.kernel.JCL_task;
import commom.JCL_handler;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

//import javassist.ClassPool;
//import javassist.CtClass;
import commom.GenericConsumer;
import commom.GenericResource;
import commom.JCL_resultImpl;
import javassist.android.DexFile;
import implementations.util.JarDexFile;


// exemplo de um consumidor !!!

/*
 * MESSAGES
 *
 *  1 public boolean register(File[] f, String classToBeExecuted) {
    2 public boolean unRegister(String nickName) {
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
	18 public boolean containsTask(String nickName){
	20 public boolean isLock(String nickName){
	22 public boolean cleanEnvironment() {
	26 public JCL_result getAsyValueLocking(String varName) {


	type 17 is containsGlobalVar. It is used only by JCL server
	type 19 is getHosts. It is used only by JCL server
	21 public boolean instantiateGlobalVarOnHost  It is used only by JCL server
	23 register() It is used only by JCL server

	METHOD DEPRECATED in JCL distributed version: public boolean register(Class<?> object, String nickName) {

	-1 slave register

 */

public class SocketConsumer<S extends JCL_handler> extends GenericConsumer<S> {

    private String hostId;
    private static JCL_orb<JCL_result> orb;
    GenericResource<JCL_task> rp;
    private JCL_FacadeImpl jcl;
    private HashSet<String> TaskContain;
    private ConcurrentHashMap<Long, String> JCLTaskMap;
    private ConcurrentHashMap<String, Set<Object>> JclHashMap;
    private static final String rootPath = Environment.getExternalStorageDirectory().toString() + File.separatorChar + "jclAndroid";
    //private URLClassLoader classLoader = (URLClassLoader) ClassLoader.getSystemClassLoader();

    @SuppressWarnings("unchecked")
    public SocketConsumer(GenericResource<S> re, AtomicBoolean kill, HashSet<String> TaskContain, String hostId,
                          Map<Long, JCL_result> results, AtomicLong taskID, ConcurrentHashMap<String, Set<Object>> JclHashMap,
        			GenericResource<JCL_task> rp, ConcurrentHashMap<Long, String> JCLTaskMap, JCL_FacadeImpl jcl){

        super(re, kill);
        this.rp = rp;
        this.jcl = jcl;
        this.TaskContain = TaskContain;
        this.hostId = hostId;
        this.JclHashMap = JclHashMap;
        this.orb = JCL_orbImpl.getInstancePacu();
        this.JCLTaskMap = JCLTaskMap;

    }

    public void addURL(URL url, DexClassLoader classLoader) throws Exception {

        Class<URLClassLoader> clazz = URLClassLoader.class;

//
//        // Use reflection
        Method method = clazz.getDeclaredMethod("addURL", new Class[]{URL.class});
        method.setAccessible(true);
        //
        method.invoke(classLoader, new Object[]{url});
    }

    @Override
    protected void doSomething(S str) {
        try {
            // JCL_message msg =
            // (JCL_message)super.ReadObjectFromSock(str.getKey(),
            // str.getInput());
            JCL_message msg = str.getMsg();
            //Log.e("Mensagem", msg.getType()+"");

            switch (msg.getType()) {

                // Register Jars
                case 1: {
                    Log.e("Case", "1");
                    // Register Jars
                    JCL_message_register msgR = (JCL_message_register) msg;
                    if (!TaskContain.contains(msgR.getClassName())) {
                        File fout0 = new File(rootPath + "/user_jars");
                        if (!fout0.exists())
                            fout0.mkdirs();
                        JarDexFile d = new JarDexFile();
                        System.err.println("Registering Class Name: " + msgR.getClassName());
                        Boolean b = new Boolean(orb.register(d.loadingClassOnAndroid(msgR, msgR.getClassName()), msgR.getClassName()));
                        JCL_result r = new JCL_resultImpl();
                        r.setCorrectResult(b);

                        JCL_message_result RESULT = new MessageResultImpl();
                        RESULT.setType(1);
                        RESULT.setResult(r);

                        // Write data
                        str.RegisterMsg.decrementAndGet();
                        super.WriteObjectOnSock(RESULT, str, false);
                        // End Write data

                        TaskContain.add(msgR.getClassName());
                    } else {

                        Boolean b = true;
                        JCL_result r = new JCL_resultImpl();
                        r.setCorrectResult(b);

                        JCL_message_result RESULT = new MessageResultImpl();
                        RESULT.setType(1);
                        RESULT.setResult(r);

                        // Write data
                        str.RegisterMsg.decrementAndGet();
                        super.WriteObjectOnSock(RESULT, str, false);
                        // End Write data
                    }
                    break;
                }

                // Unregister class
                case 2: {
                    // Unregister class
                    JCL_message_commons jclC = (JCL_message_commons) msg;
                    boolean b = orb.unRegister(jclC.getRegisterData()[0]);

                    JCL_result r = new JCL_resultImpl();
                    r.setCorrectResult(b);

                    JCL_message_result RESULT = new MessageResultImpl();
                    RESULT.setType(2);
                    RESULT.setResult(r);

                    // Write data
                    super.WriteObjectOnSock(RESULT, str, false);
                    // End Write data

                    break;
                }

                // Register *.class
                case 3: {

                    JCL_message_register msgR = (JCL_message_register) msg;
                    //if (!TaskContain.contains(msgR.getClassName())) {
                        //ClassPool cp = ClassPool.getDefault();
                    byte[] by = msgR.getJars()[0];
                    String name = msgR.getJarsNames()[0];

                        //InputStream myInputStream = new ByteArrayInputStream(by);
                        //CtClass cc = cp.makeClass(myInputStream);
                    System.err.println("Registering Class Name: " + msgR.getClassName());
                        //Boolean b = new Boolean(orb.register(loadingClassOnAndroid(by,name), msgR.getClassName()));
                    Boolean b = new Boolean(orb.register(loadingClassOnAndroid(by, name), msgR.getClassName()));
                    JCL_result r = new JCL_resultImpl();
                    r.setCorrectResult(b);
                    JCL_message_result RESULT = new MessageResultImpl();
                    RESULT.setType(1);
                    RESULT.setResult(r);
                    str.RegisterMsg.decrementAndGet();
                        // Write data
                    super.WriteObjectOnSock(RESULT, str, false);
                        // End Write data

                    TaskContain.add(msgR.getClassName());

//                    } else {
//
//                        Boolean b = true;
//                        JCL_result r = new JCL_resultImpl();
//                        r.setCorrectResult(b);
//
//                        JCL_message_result RESULT = new MessageResultImpl();
//                        RESULT.setType(1);
//                        RESULT.setResult(r);
//
//                        // Write data
//                        super.WriteObjectOnSock(RESULT, str, false);
//                        // End Write data
//                    }

                    break;
                }

                // Execute Task
                case 4: {
                    // Execute Task
                    JCL_message_task jclT = (JCL_message_task) msg;
                    JCL_task t = jclT.getTask();
                    t.setTaskTime(System.nanoTime());

                    t.setHost(str.getSocketAddress());
                    JCLFuture<JCL_result> ticket = (JCLFuture<JCL_result>) jcl.execute(t);
                    JCL_result r = new JCL_resultImpl();
                    r.setCorrectResult(ticket.getTicket());
                    JCL_message_result RESULT = new MessageResultImpl();
                    RESULT.setType(4);
                    RESULT.setResult(r);

                    // Write data
                    super.WriteObjectOnSock(RESULT, str, false);
                    // End Write data

                    break;
                }

                // Execute Task
                case 5: {
                    JCL_message_task jclT = (JCL_message_task) msg;

                    // Execute class
                    JCL_task t = jclT.getTask();
                    t.setTaskTime(System.nanoTime());
                    t.setHost(str.getSocketAddress());
                    JCLFuture<JCL_result> ticket = (JCLFuture<JCL_result>) jcl.execute(t);
                    JCL_result r = new JCL_resultImpl();
                    r.setCorrectResult(ticket.getTicket());
                    JCL_message_result RESULT = new MessageResultImpl();
                    RESULT.setType(5);
                    RESULT.setResult(r);

                    // Write data
                    super.WriteObjectOnSock(RESULT, str, false);
                    // End Write data

                    break;
                }

                // getResultBlocking(id) type 6
                case 6: {
                    // getResultBlocking(id) type 6
                    JCL_message_long jclC = (JCL_message_long) msg;
                    long id = jclC.getRegisterData()[0];
                    JCL_result jclR = orb.getResults().get(id);
                    if (jclR != null) {

                        if (!((jclR.getCorrectResult() == null) && (jclR.getErrorResult() == null))) {

                            JCL_message_result RESULT = new MessageResultImpl();
                            RESULT.setType(6);
                            jclR.addTime(System.nanoTime());

                            RESULT.setResult(jclR);

                            // Write data
                            super.WriteObjectOnSock(RESULT, str, false);
                            // End Write data

                        } else {
                            str.putOnQueue();
                        }
                    } else {
                        synchronized (JCLTaskMap) {
                            if (JCLTaskMap.containsKey(id)) {
                                JCL_message_control msgctr = new MessageControlImpl();
                                msgctr.setType(6);
                                msgctr.setRegisterData(JCLTaskMap.get(id));

                                // Write data
                                super.WriteObjectOnSock(msgctr, str, false);
                                // End Write data
                            }
                        }
                    }

                    break;
                }

                // getResultUnblocking(id) type 7
                case 7: {

                    // getResultUnblocking(id) type 7
                    JCL_message_long jclC = (JCL_message_long) msg;
                    long id = jclC.getRegisterData()[0];
                    JCL_result jclR =  orb.getResults().get(id);

                    if (jclR != null) {
                        JCL_message_result RESULT = new MessageResultImpl();
                        RESULT.setType(7);
                        RESULT.setResult(jclR);
                        // jclR.addTime(System.nanoTime());

                        // Write data
                        super.WriteObjectOnSock(RESULT, str, false);
                        // End Write data
                    } else {
                        synchronized (JCLTaskMap) {
                            if (JCLTaskMap.containsKey(id)) {
                                JCL_message_control msgctr = new MessageControlImpl();
                                msgctr.setType(7);
                                msgctr.setRegisterData(JCLTaskMap.get(id));

                                // Write data
                                super.WriteObjectOnSock(msgctr, str, false);
                                // End Write data
                            }
                        }
                    }

                    break;
                }

                // removeResult(id) type 8
                case 8: {

                    // removeResult(id) type 8
                    JCL_message_long jclC = (JCL_message_long) msg;
                    long id = jclC.getRegisterData()[0];
                    JCL_result jclR = orb.getResults().remove(id);
                    if (jclR != null) {
                        JCL_message_result RESULT = new MessageResultImpl();
                        RESULT.setType(8);
                        RESULT.setResult(jclR);

                        // Write data
                        super.WriteObjectOnSock(RESULT, str, false);
                        // End Write data
                    } else {

                        if (JCLTaskMap.containsKey(id)) {
                            JCL_message_control msgctr = new MessageControlImpl();
                            msgctr.setType(8);
                            msgctr.setRegisterData(JCLTaskMap.get(id));

                            // Write data
                            super.WriteObjectOnSock(msgctr, str, false);
                            // End Write data
                        }
                    }

                    break;
                }

                // instantiateGlobalVar(id) type 9
                case 9: {

                    // instantiateGlobalVar(id) type 9
                    JCL_message_global_var_obj jclGV = (JCL_message_global_var_obj) msg;
                    JCL_result jclR = new JCL_resultImpl();
                    jclR.setCorrectResult(
                            orb.instantiateGlobalVar(jclGV.getVarKey(), jclGV.getNickName(), jclGV.getDefaultValues()));

                    JCL_message_result RESULT = new MessageResultImpl();
                    RESULT.setType(9);
                    RESULT.setResult(jclR);

                    // Write data
                    super.WriteObjectOnSock(RESULT, str, false);
                    // End Write data

                    break;
                }

                // instantiateGlobalVar(id) type 10
                case 10: {
                    // instantiateGlobalVar(id) type 10
                    JCL_message_global_var jclGV = (JCL_message_global_var) msg;
                    JCL_result jclR = new JCL_resultImpl();
                    jclR.setCorrectResult(orb.instantiateGlobalVar(jclGV.getVarKey(), jclGV.getVarInstance()));

                    JCL_message_result RESULT = new MessageResultImpl();
                    RESULT.setType(10);
                    RESULT.setResult(jclR);

                    // Write data
                    super.WriteObjectOnSock(RESULT, str, false);
                    // End Write data

                    break;
                }

                // destroyGlobalVar(id) type 11
                case 11: {

                    // destroyGlobalVar(id) type 11
                    JCL_message_generic jclC = (JCL_message_generic) msg;
                    JCL_result jclR = new JCL_resultImpl();
                    jclR.setCorrectResult(orb.destroyGlobalVar(jclC.getRegisterData()));

                    JCL_message_result RESULT = new MessageResultImpl();
                    RESULT.setType(11);
                    RESULT.setResult(jclR);

                    // Write data
                    super.WriteObjectOnSock(RESULT, str, false);
                    // End Write data

                    break;
                }

                // setValue(id) type 12
                case 12: {

                    // setValue(id) type 12
                    JCL_message_global_var jclGV = (JCL_message_global_var) msg;
                    JCL_result jclR = new JCL_resultImpl();
                    jclR.setCorrectResult(new Boolean(orb.setValue(jclGV.getVarKey(), jclGV.getVarInstance())));

                    JCL_message_result RESULT = new MessageResultImpl();
                    RESULT.setType(12);
                    RESULT.setResult(jclR);

                    // Write data
                    super.WriteObjectOnSock(RESULT, str, false);
                    // End Write data

                    break;
                }

                // setValueUnlocking(id) type 13
                case 13: {

                    // setValueUnlocking(id) type 13
                    JCL_message_global_var jclGV = (JCL_message_global_var) msg;
                    JCL_result jclR = new JCL_resultImpl();

                    jclR.setCorrectResult(new Boolean(orb.setValueUnlocking(jclGV.getVarKey(), jclGV.getVarInstance())));

                    JCL_message_result RESULT = new MessageResultImpl();
                    RESULT.setType(13);
                    RESULT.setResult(jclR);

                    // Write data
                    super.WriteObjectOnSock(RESULT, str, false);
                    // End Write data

                    break;
                }

                // getValue(id) type:14
                case 14: {

                    // getValue(id) type:14
                    JCL_message_generic jclC = (JCL_message_generic) msg;

                    JCL_result jclR = orb.getValue(jclC.getRegisterData());

                    JCL_message_result RESULT = new MessageResultImpl();
                    RESULT.setType(14);
                    RESULT.setResult(jclR);

                    // Write data
                    super.WriteObjectOnSock(RESULT, str, false);
                    // End Write data

                    break;
                }

                // getValueLocking(id) type:15
                case 15: {

                    // getValueLocking(id) type:15
                    JCL_message_generic jclC = (JCL_message_generic) msg;
                    JCL_result jclR = orb.getValueLocking(jclC.getRegisterData());
                    if (jclR != null) {
                        JCL_message_result RESULT = new MessageResultImpl();
                        RESULT.setType(15);
                        RESULT.setResult(jclR);

                        // Write data
                        super.WriteObjectOnSock(RESULT, str, false);
                        // End Write data

                    } else {
                        str.putOnQueue();
                    }
                    break;
                }

                // containsGlobalVar(id) type 17
                case 17: {

                    // containsGlobalVar(id) type 17
                    JCL_message_generic aux = (JCL_message_generic) msg;
                    boolean b = this.orb.containsGlobalVar(aux.getRegisterData());

                    JCL_message_generic resp = new MessageGenericImpl();
                    resp.setRegisterData(b);
                    // Write data
                    super.WriteObjectOnSock(resp, str, false);
                    // End Write data
                    break;
                }

                // containsTask type 18
                case 18: {

                    // containsTask type 18
                    JCL_message_control aux = (JCL_message_control) msg;
                    boolean b = this.orb.containsTask(aux.getRegisterData()[0]);

                    JCL_message_control resp = new MessageControlImpl();
                    resp.setRegisterData(String.valueOf(b));
                    // Write data
                    super.WriteObjectOnSock(resp, str, false);
                    // End Write data
                    break;
                }

                // isLock(id) type: 20
                case 20: {

                    // isLock(id) type: 20
                    JCL_message_generic jclGV = (JCL_message_generic) msg;
                    JCL_result jclR = new JCL_resultImpl();
                    jclR.setCorrectResult(new Boolean(orb.isLock(jclGV.getRegisterData())));

                    JCL_message_result RESULT = new MessageResultImpl();
                    RESULT.setType(20);
                    RESULT.setResult(jclR);
                    // Write data
                    super.WriteObjectOnSock(RESULT, str, false);
                    // End Write data

                    break;
                }

                // cleanEnvironment() type 22
                case 22: {

                    // cleanEnvironment() type 22
                    JCL_result jclR = new JCL_resultImpl();
                    jclR.setCorrectResult(new Boolean(orb.cleanEnvironment()));
                    JCL_message_result RESULT = new MessageResultImpl();
                    RESULT.setType(22);
                    RESULT.setResult(jclR);
                    TaskContain.clear();
                    JclHashMap.clear();

                    // Write data
                    super.WriteObjectOnSock(RESULT, str, false);
                    // End Write data

                    break;
                }

                // binexecutetask(bin task) type 25
                case 25: {

                    // binexecutetask(bin task) type 25
                    JCL_message_list_task jclT = (JCL_message_list_task) msg;
                    Map<Long, JCL_task> binMap = jclT.getMapTask();
                    Map<Long, Long> binTicket = new HashMap<Long, Long>();

                    // Execute class
                    for (Entry<Long, JCL_task> inst : binMap.entrySet()) {
                        JCL_task t = inst.getValue();
                        t.setTaskTime(System.nanoTime());
                        t.setHost(str.getSocketAddress());
                        JCLFuture<JCL_result> ticket = (JCLFuture<JCL_result>) jcl.execute(t);
                        binTicket.put(inst.getKey(), ticket.getTicket());
                    }

                    JCL_result r = new JCL_resultImpl();
                    r.setCorrectResult(binTicket);
                    JCL_message_result RESULT = new MessageResultImpl();
                    RESULT.setType(25);
                    RESULT.setResult(r);

                    // Write data
                    super.WriteObjectOnSock(RESULT, str, false);
                    // End Write data

                    break;
                }

				/*
                 * case 26:{ JCL_message_generic jclC =
				 * (JCL_message_generic)msg; JCL_result jclR =
				 * orb.getValueLocking(jclC.getRegisterData());
				 * JCL_message_result RESULT = new MessageResultImpl();
				 * RESULT.setType(26); RESULT.setResult(jclR);
				 *
				 * //Write data super.WriteObjectOnSock(RESULT, str); //End
				 * Write data
				 *
				 * break; }
				 */
                // instantiateGlobalVarAndReg() type 27
                case 27: {
                    // Register Jars
                    JCL_message_register msgR = (JCL_message_register) msg;
                    try {
                        // Register
                        if (!TaskContain.contains(msgR.getClassName())) {
                            JarDexFile d = new JarDexFile();
                                //d.loadingClassOnAndroid(msgR);
                            orb.registerGV(d.loadingClassOnAndroid(msgR, msgR.getClassName()), msgR.getClassName());

                            System.err.println("Registering GVClass Name: " + msgR.getClassName());
                            TaskContain.add(msgR.getClassName());
                        }

                        // Answer
                        Boolean b = true;
                        JCL_result r = new JCL_resultImpl();
                        r.setCorrectResult(b);

                        JCL_message_result RESULT = new MessageResultImpl();
                        RESULT.setType(1);
                        RESULT.setResult(r);

                        str.RegisterMsg.decrementAndGet();
                        // Write data
                        super.WriteObjectOnSock(RESULT, str, false);
                        // End Write data

                    } catch (Exception e) {
                        // TODO: handle exception
                        // Answer
                        Boolean b = false;
                        JCL_result r = new JCL_resultImpl();
                        r.setCorrectResult(b);

                        JCL_message_result RESULT = new MessageResultImpl();
                        RESULT.setType(1);
                        RESULT.setResult(r);

                        // Write data
                        super.WriteObjectOnSock(RESULT, str, false);
                        // End Write data
                    }

                    break;
                }

                // createhashKey() type 28
                case 28: {

                    //Object[] data = {gvName,Regclass,msgReg};
                    // createhashKey() type 28
                    JCL_message_generic aux = (JCL_message_generic) msg;
                    Object[] data = (Object[]) aux.getRegisterData();
                    String name = (String)data[0];
                    boolean Regclass = (boolean) data[1];

                    if(Regclass){
                        JCL_message_register msgR = (JCL_message_register) data[2];
                        // Register
                        if (!TaskContain.contains(msgR.getClassName())) {

                            File fout0 = new File(rootPath + "/user_jars");
                            if (!fout0.exists())
                                fout0.mkdirs();
                            JarDexFile d = new JarDexFile();
                            System.err.println("Registering Class Name: " + msgR.getClassName());
                            Boolean b = new Boolean(orb.register(d.loadingClassOnAndroid(msgR, msgR.getClassName()), msgR.getClassName()));
                            JCL_result r = new JCL_resultImpl();
                            r.setCorrectResult(b);

                            JCL_message_result RESULT = new MessageResultImpl();
                            RESULT.setType(1);
                            RESULT.setResult(r);

                            // Write data
                            str.RegisterMsg.decrementAndGet();
                            super.WriteObjectOnSock(RESULT, str, false);
                            // End Write data

                            TaskContain.add(msgR.getClassName());
                        }
                    }

                    if (!JclHashMap.containsKey(name)) {

                        JclHashMap.put(name, new HashSet<Object>());
                    }
                    JCL_message_generic resp = new MessageGenericImpl();
                    resp.setRegisterData(true);

                    // Write data
                    super.WriteObjectOnSock(resp, str,false);
                    // End Write data
                    break;
                }

                // hashAdd() type 29
                case 29: {

                    // hashAdd() type 29
                    JCL_message_generic aux = (JCL_message_generic) msg;
                    Object[] dados = (Object[]) aux.getRegisterData();
                    JclHashMap.get(dados[0]).add(dados[1]);
                    JCL_message_generic resp = new MessageGenericImpl();
                    resp.setRegisterData(true);

                    // Write data
                    super.WriteObjectOnSock(resp, str, false);
                    // End Write data
                    break;
                }

                // hashRemove() type 30
                case 30: {

                    JCL_message_generic aux = (JCL_message_generic) msg;
                    Object[] dados = (Object[]) aux.getRegisterData();
                    JclHashMap.get(dados[0]).remove(dados[1]);
                    JCL_message_generic resp = new MessageGenericImpl();
                    resp.setRegisterData(true);

                    // Write data
                    super.WriteObjectOnSock(resp, str, false);
                    // End Write data
                    break;
                }

                // containsKey() type 31
                case 31: {

                    // containsKey() type 31
                    JCL_message_generic aux = (JCL_message_generic) msg;
                    Object[] dados = (Object[]) aux.getRegisterData();
                    JCL_message_generic resp = new MessageGenericImpl();
                    resp.setRegisterData(JclHashMap.get(dados[0]).contains(dados[1]));

                    // Write data
                    super.WriteObjectOnSock(resp, str, false);
                    // End Write data
                    break;
                }

                // hashSize() type 32
                case 32: {

                    // hashSize() type 32
                    JCL_message_generic aux = (JCL_message_generic) msg;
                    String dados = (String) aux.getRegisterData();
                    JCL_message_generic resp = new MessageGenericImpl();
                    resp.setRegisterData(JclHashMap.get(dados).size());

                    // Write data
                    super.WriteObjectOnSock(resp, str, false);
                    // End Write data
                    break;
                }

                // hashClean() type 33
                case 33: {

                    // hashClean() type 33
                    JCL_message_generic aux = (JCL_message_generic) msg;
                    String dados = (String) aux.getRegisterData();
                    JCL_message_generic resp = new MessageGenericImpl();
                    resp.setRegisterData(JclHashMap.get(dados));
                    JclHashMap.get(dados).clear();

                    // Write data
                    super.WriteObjectOnSock(resp, str, false);
                    // End Write data
                    break;
                }

                // getHashSet() type 34
                case 34: {

                    // getHashSet() type 34
                    JCL_message_generic aux = (JCL_message_generic) msg;
                    String dados = (String) aux.getRegisterData();
                    JCL_message_generic resp = new MessageGenericImpl();
                    resp.setRegisterData(JclHashMap.get(dados));

                    // Write data
                    super.WriteObjectOnSock(resp, str, false);
                    // End Write data
                    break;
                }

                // instantiateGlobalVar for hash type 35
                case 35: {

                    // instantiateGlobalVar for hash type 35
                    JCL_message_list_global_var jclGV = (JCL_message_list_global_var) msg;
                    JCL_result jclR = new JCL_resultImpl();

                    boolean status = true;
                    Map<Object, Object> mKV = jclGV.getKeyValue();
                    for (Entry<Object, Object> KV : mKV.entrySet()) {
                        if (!(orb.instantiateGlobalVar(KV.getKey(), KV.getValue()))) {
                            status = false;
                        }
                    }
                    jclR.setCorrectResult(status);
                    JCL_message_result RESULT = new MessageResultImpl();
                    RESULT.setType(35);
                    RESULT.setResult(jclR);

                    // Write data
                    super.WriteObjectOnSock(RESULT, str, false);
                    // End Write data

                    break;
                }

                // hashAdd() type 36
                case 36: {

                    // hashAdd() type 36
                    JCL_message_generic aux = (JCL_message_generic) msg;
                    Object[] dados = (Object[]) aux.getRegisterData();
                    List<Object> setk = (List<Object>) dados[1];

                    for (Object obj : setk) {
                        JclHashMap.get(dados[0]).add(obj);
                    }

                    JCL_message_generic resp = new MessageGenericImpl();
                    resp.setRegisterData(true);

                    // Write data
                    super.WriteObjectOnSock(resp, str, false);
                    // End Write data
                    break;
                }

                // instantiateGlobalVarReturn type 37 use in hash
                case 37: {
                    JCL_message_global_var jclGV = (JCL_message_global_var) msg;
                    JCL_result jclR = new JCL_resultImpl();
                    if (orb.containsGlobalVar(jclGV.getVarKey())) {
                        jclR.setCorrectResult(orb.getValue(jclGV.getVarKey()));
                        orb.setValue(jclGV.getVarKey(), jclGV.getVarInstance());
                    } else {
                        jclR.setCorrectResult(orb.instantiateGlobalVar(jclGV.getVarKey(), jclGV.getVarInstance()));
                    }
                    JCL_message_result RESULT = new MessageResultImpl();
                    RESULT.setType(10);
                    RESULT.setResult(jclR);

                    // Write data
                    super.WriteObjectOnSock(RESULT, str, false);
                    // End Write data

                    break;
                }

                // getBinValueInterator() type 38 use on hash
                case 38: {

                    // getBinValueInterator() type 38 use on hash
                    JCL_message_generic jclGV = (JCL_message_generic) msg;
                    Set<implementations.util.Entry<String, Object>> setGetBinValue = (Set<implementations.util.Entry<String, Object>>) jclGV
                            .getRegisterData();
                    Set setResult = new HashSet();
                    for (implementations.util.Entry<String, Object> key : setGetBinValue) {
                        JCL_result value = orb.getValue(key.getKey());
                        setResult.add(new implementations.util.Entry(key.getValue(), value.getCorrectResult()));
                    }

                    JCL_message_generic RESULT = new MessageGenericImpl();
                    RESULT.setRegisterData(setResult);
                    RESULT.setType(38);

                    // Write data
                    super.WriteObjectOnSock(RESULT, str, false);
                    // End Write data

                    break;
                }
                // Execute Task
                case 40: {
                    // Execute Task
                    JCL_message_task jclT = (JCL_message_task) msg;
                    JCL_task t = jclT.getTask();
                    t.setTaskTime(System.nanoTime());

                    t.setHost(str.getSocketAddress());
                    JCLFuture<JCL_result> ticket = (JCLFuture<JCL_result>) jcl.execute(t);
                    JCL_result r = new JCL_resultImpl();
                    r.setCorrectResult(ticket.getTicket());
                    JCL_message_result RESULT = new MessageResultImpl();
                    RESULT.setType(4);
                    RESULT.setResult(r);

                    // Write data
                    super.WriteObjectOnSock(RESULT, str, false);
                    // End Write data

                    break;
                }

                // Execute Task
                case 41: {
                    JCL_message_task jclT = (JCL_message_task) msg;

                    // Execute class
                    JCL_task t = jclT.getTask();
                    t.setTaskTime(System.nanoTime());
                    t.setHost(str.getSocketAddress());
                    JCLFuture<JCL_result> ticket = (JCLFuture<JCL_result>) jcl.execute(t);
                    JCL_result r = new JCL_resultImpl();
                    r.setCorrectResult(ticket.getTicket());
                    JCL_message_result RESULT = new MessageResultImpl();
                    RESULT.setType(5);
                    RESULT.setResult(r);

                    // Write data
                    super.WriteObjectOnSock(RESULT, str, false);
                    // End Write data

                    break;
                }
                // Consisting Host
                case -3: {

                    // Consisting Host
                    JCL_message_control jclC = (JCL_message_control) msg;
                    String[] hostPortId = jclC.getRegisterData();
                    // ConcurrentMap<String,String[]> slaves =
                    // (ConcurrentMap<String, String[]>) objs[0];
                    // List<String> slavesIDs = (List<String>) objs[1];

                    ConcurrentMap<String, String[]> slaves = ((PacuResource) rp).getSlaves();
                    List<String> slavesIDs = ((PacuResource) rp).getSlavesIDs();

                    String address = hostPortId[0];
                    String port = hostPortId[1];
                    String slaveName = hostPortId[2];
                    String cores = hostPortId[3];

                    System.out.println("Consisting cluster!!!");
                    System.out.println("Host add: " + Arrays.toString(hostPortId));

                    Set<Entry<Object, Object>> gvSet = orb.getGlobalVarEntrySet();
                    slaves.put((slaveName + port), hostPortId);
                    slavesIDs.add(slaveName + port);
                    int total = gvSet.size();
                    int x = 0;
                    boolean status = true;
                    for (Entry<Object, Object> gv : gvSet) {
                        Object key = gv.getKey();
                        Object value = gv.getValue();
                        int sizeClus = slaves.size();
                        int index = (slavesIDs.indexOf(hostId));
                        int hashId = key.hashCode() / (sizeClus - 1);
                        int pHostId = key.hashCode() % (sizeClus - 1);

                        int f1 = hashId % sizeClus;
                        if ((f1 != 0) && (index == pHostId)) {
                            int f2 = sizeClus + index - f1;
                            int f3 = f2 % sizeClus;

                            String[] hostPort = slaves.get(slavesIDs.get(f3));
                            String hostGv = hostPort[0];
                            String portGv = hostPort[1];
                            String macGv = hostPort[2];

                            orb.lockGlobalVar(key);
                            JCL_message_global_var gvMessage = new MessageGlobalVarImpl(key, value);
                            gvMessage.setType(10);
                            JCL_connector globalVarConnector = new ConnectorImpl();
                            globalVarConnector.connect(hostGv, Integer.parseInt(portGv), macGv);
                            JCL_result result = globalVarConnector.sendReceive(gvMessage, null).getResult();
                            Boolean b = (Boolean) result.getCorrectResult();
                            if (!b)
                                status = false;
                            globalVarConnector.disconnect();
                            orb.unLockGlobalVar(key);
                        }
                        x++;
                        this.status(x * ((100.0) / total));
                    }
                    System.out.print(System.getProperty("line.separator"));

                    // Write data
                    JCL_result jclR = new JCL_resultImpl();
                    jclR.setCorrectResult(status);
                    JCL_message_result RESULT = new MessageResultImpl();
                    RESULT.setType(-3);
                    RESULT.setResult(jclR);
                    super.WriteObjectOnSock(RESULT, str, false);
                    // End Write data

                    break;
                }

                // Collaborative scheduler
                case -6: {

                    // Collaborative scheduler
                    JCL_message_control jclmg = (JCL_message_control) msg;
                    MessageTaskImpl msgTask = new MessageTaskImpl();
                    JCL_task t = this.rp.registers.poll();
                    if (t != null) {
                        if (t.getHostChange()) {
                            synchronized (JCLTaskMap) {

                                msgTask.setTask(t);
                                orb.getResults().remove(t.getTaskID());
                                JCLTaskMap.put(t.getTaskID(), str.getSocketAddress() + "¬" + jclmg.getRegisterData()[0]
                                        + "¬" + jclmg.getRegisterData()[1] + "¬" + jclmg.getRegisterData()[2]+ "¬" + jclmg.getRegisterData()[3]);
                                t.setTaskTime(System.nanoTime());

                            }
                        } else {
                            msgTask.setTask(null);
                            this.rp.registers.offer(t);
                            this.rp.wakeup();
                        }
                    } else {
                        msgTask.setTask(t);
                    }

                    // Type execute
                    msgTask.setType(-6);

                    // Write data
                    super.WriteObjectOnSock(msgTask, str, false);
                    // End Write data
                    break;
                }
                case -100: {
                    Log.e("Case", "-100");
                    JCL_message jclT = (JCL_message) msg;

                    // Execute class
                    MessageImpl imp = new MessageImpl();
                    imp.setType(0);

                    // Write data
                    super.WriteObjectOnSock(imp, str, false);
                    // End Write data

                    break;
                }
                case 42: {//get
                    try {
                        Properties properties = new Properties();
                        properties.load(new FileInputStream(rootPath+"/jcl_conf/config.properties"));
                        Hashtable<String, String> metadados = new Hashtable<>();
                        metadados = (Hashtable<String, String>) properties.clone();
                        JCL_message_metadata jclMsg = (JCL_message_metadata) msg;
                        jclMsg.setType(42);
                        jclMsg.setMetadados(metadados);
                        super.WriteObjectOnSock(jclMsg, str, false);

                    } catch (Exception e ) {
                        e.printStackTrace();
                    }

                    break;
                }

                case 43: {//set
                    try {
                        JCL_message_metadata jclMsg = (JCL_message_metadata) msg;
//                        Properties properties = new Properties();
//                        properties.load(new FileInputStream("../jcl_conf/config.properties"));
//                        properties.putAll(jclMsg.getMetadados());
//                        properties.store(new FileOutputStream("../jcl_conf/config.properties"), "new settings");
                        JCL_message_bool RESULT = new MessageBoolImpl();
                        RESULT.setType(1);
                        RESULT.setRegisterData(false);
                        super.WriteObjectOnSock(RESULT, str, false);

                    } catch (Exception e ) {
                        e.printStackTrace();
                    }
                    break;
                }
                case 44: {
                    //getSensorNow
                    Log.e("Case", "44");
                    JCL_message_generic jclT = (JCL_message_generic) msg;
                    MessageSensorImpl resp = JCL_ANDROID_Facade.getInstance().getSensorNow(jclT.getRegisterData());


                    // Execute class

                    // Write data
                    super.WriteObjectOnSock(resp, str, false);
                    // End Write data

                    break;
                }
                case 45: {
                    //turnOn
                    Log.e("Case", "45");
                    //JCL_Message_Sensor_Now jclT = (JCL_Message_Sensor_Now) msg;
                    //MessageSensorImpl resp = JCL_ANDROID_Facade.getInstance().getSensorNow(jclT.getArgs());
                    MessageImpl resp = new MessageImpl();
                    resp.setType(101);


                    // Execute class

                    JCL_ANDROID_Facade.getInstance().turnOnNativeSensor();
                    // Write data

                    super.WriteObjectOnSock(resp, str, false);
                    //Colocando depois da resposta, fica assyncono pro user, pois ele não precisa esperar a realização da da tarefa pra ser respondido

                    // End Write data

                    break;
                }
                case 46: {
                    //standBy
                    Log.e("Case", "46" + " Standy By");
                    //JCL_Message_Sensor_Now jclT = (JCL_Message_Sensor_Now) msg;
                    //MessageSensorImpl resp = JCL_ANDROID_Facade.getInstance().getSensorNow(jclT.getArgs());
                    JCL_message_bool resp = new MessageBoolImpl();
                    resp.setType(102);


                    // Execute class

                    // Write data
                    JCL_ANDROID_Facade.getInstance().standByNativeSensor();
                    super.WriteObjectOnSock(resp, str, false);

                    // End Write data

                    break;
                }

                case 47: {
                    //setMeta
                    Log.e("Case", "47");
                    JCL_message_metadata jclMet = (JCL_message_metadata) msg;
                    //JCL_Message_Sensor_Now jclT = (JCL_Message_Sensor_Now) msg;
                    //MessageSensorImpl resp = JCL_ANDROID_Facade.getInstance().getSensorNow(jclT.getArgs());
                    JCL_message_bool res = new MessageBoolImpl();
                    res.setRegisterData(JCL_ANDROID_Facade.getInstance().changeMetadata(jclMet.getMetadados()));
                    res.setType(1);


                    // Write data
                    super.WriteObjectOnSock(res, str, false);
                    // End Write data

                    break;
                }
                case 49: {
                    //setSensor
                    Log.e("Case", "49");
                    JCL_message_control jclMet = (JCL_message_control) msg;
                    String[] args = jclMet.getRegisterData();
                    //JCL_Message_Sensor_Now jclT = (JCL_Message_Sensor_Now) msg;
                    //MessageSensorImpl resp = JCL_ANDROID_Facade.getInstance().getSensorNow(jclT.getArgs());
                    JCL_message_bool res = new MessageBoolImpl();
                    res.setRegisterData(JCL_ANDROID_Facade.getInstance().setSensor(args[0], Integer.parseInt(args[1]), Integer.parseInt(args[2]), Integer.parseInt(args[3]), false));
                    res.setType(1);
                    //Log.e("Retornando", res.getRegisterData()+"");


                    // Write data
                    super.WriteObjectOnSock(res, str, false);
                    // End Write data

                    break;
                }
                case 50: {
                    //RemoveSensor
                    Log.e("Case", "50");
                    JCL_message_generic jclMet = (JCL_message_generic) msg;
                    Object[] args = (Object[]) jclMet.getRegisterData();


                    //JCL_Message_Sensor_Now jclT = (JCL_Message_Sensor_Now) msg;
                    //MessageSensorImpl resp = JCL_ANDROID_Facade.getInstance().getSensorNow(jclT.getArgs());
                    JCL_message_bool res = new MessageBoolImpl();
                    res.setRegisterData(JCL_ANDROID_Facade.getInstance().removeSensor(Integer.parseInt(args[0].toString()), false));
                    res.setType(1);
                    //Log.e("Retornando", res.getRegisterData()+"");


                    // Write data
                    super.WriteObjectOnSock(res, str, false);
                    // End Write data

                    break;
                }
                case 51: {
                    //actSensor
                    Log.e("Case", "51");
                    JCL_message_bool res = new MessageBoolImpl();
                    res.setRegisterData(false);
                    res.setType(1);

                    // Write data
                    super.WriteObjectOnSock(res, str, false);
                    // End Write data

                    break;
                }
                case 52: {
                    //restart
                    Log.e("Case", "52");
                    JCL_message jclT = (JCL_message) msg;


                    // Execute class
                    MessageImpl imp = new MessageImpl();
                    imp.setType(100);

                    // Write data
                    super.WriteObjectOnSock(imp, str, false);
                    // End Write data
                    Log.e("Restart", "Restart");
                    JCL_ANDROID_Facade.getInstance().restart(0);

                    break;
                }
                case 53: {
                    //set encryption
                    // Log.e("Case","53");
                    JCL_message_bool RESULT = new MessageBoolImpl();
                    RESULT.setType(1);
                    if (JCL_ANDROID_Facade.getInstance().isStandBySen()){
                        RESULT.setRegisterData(false);
                    }else{
                        JCL_message_control jclMsg = (JCL_message_control) msg;
                        System.out.println("encryption: " + jclMsg.getRegisterData()[0]);
                        ConnectorImpl.encryption = Boolean.valueOf(jclMsg.getRegisterData()[0]);

                        RESULT.setRegisterData(true);
                    }
                    super.WriteObjectOnSock(RESULT, str, false);

                    // End Write data
                    break;
                }
                case 54:{
                    // set context
                    Log.e("Case", "54");
                    JCL_message_generic jclMsgSN = (JCL_message_generic) msg;
                    boolean resp = JCL_ANDROID_Facade.getInstance().setContext(jclMsgSN.getRegisterData());

                    JCL_message_bool mResp = new MessageBoolImpl();
                    //result.setCorrectResult(contextNickname);
                    mResp.setType(54);
                    mResp.setRegisterData(resp);
                    super.WriteObjectOnSock(mResp, str, false);
                    break;
                }
                case 55:{
                    Log.e("Case", "55");
                    // add task on context
                    JCL_message_generic jclMsgSN = (JCL_message_generic) msg;
                    boolean b = JCL_ANDROID_Facade.getInstance().addTaskOnContext(jclMsgSN.getRegisterData());

                    JCL_result r = new JCL_resultImpl();
                    r.setCorrectResult(b);

                    JCL_message_bool RESULT = new MessageBoolImpl();
                    RESULT.setType(55);
                    RESULT.setRegisterData(b);

                    //write data
                    super.WriteObjectOnSock(RESULT, str, false);

                    break;
                }
                case 56:{
                    // add task on context
                    JCL_message_generic jclMsgSN = (JCL_message_generic) msg;
                    boolean b = JCL_ANDROID_Facade.getInstance().addActingOnContext(jclMsgSN.getRegisterData());

                    JCL_result r = new JCL_resultImpl();
                    r.setCorrectResult(b);

                    JCL_message_bool RESULT = new MessageBoolImpl();
                    RESULT.setType(56);
                    RESULT.setRegisterData(b);
                    super.WriteObjectOnSock(RESULT, str, false);
                    break;
                }
                case 57:{
                    // reserve ticket for context
                    JCL_message_generic jclMsgSN = (JCL_message_generic) msg;
                    JCL_message_generic resp = new MessageGenericImpl();
                    Long ticket = JCL_FacadeImpl.createTicket();
                    resp.setType(57);
                    resp.setRegisterData(ticket);
                    // Write data
                    super.WriteObjectOnSock(resp, str, false);
                    break;
                }
                case 58:{
                    // execute context task
                    JCL_message_generic jclMsgSN = (JCL_message_generic) msg;
                    Object[] obj = (Object[]) jclMsgSN.getRegisterData();
                    JCL_Action action = new JCL_Action(Boolean.valueOf(obj[0]+""), Long.valueOf(obj[1]+""), obj[2]+"", obj[3]+"", obj[4]+"", obj[5]+"", obj[6]+"",obj[7]+"",  (Object[])  obj[8] );
                    // Execute Task
                    JCL_task task = new JCL_taskImpl();
                    task.setTaskTime(System.nanoTime());
                    task.setHost(str.getSocketAddress());

                    jcl.execute(action.getTicket(), action.getClassName(), action.getMethodName(), action.getParam());
                    JCL_result r = new JCL_resultImpl();
                    JCL_message_result RESULT = new MessageResultImpl();
                    RESULT.setType(58);
                    RESULT.setResult(r);

                    super.WriteObjectOnSock(RESULT, str, false);


                    break;
                }
                case 59:{
                    // remove context result
                    JCL_message_generic jclMsgSN = (JCL_message_generic) msg;
                    Object[] obj = (Object[]) jclMsgSN.getRegisterData();
                    Long ticket = (Long) obj[1];

                    boolean b = jcl.removeContextResult(ticket);
                    JCL_message_bool msgR = new MessageBoolImpl();
                    msgR.setType(59);
                    msgR.setRegisterData(b);
                    super.WriteObjectOnSock(msgR, str, false);
                    break;
                }
                case 60: {
                    Log.e("Case", "60");
                    long ini = System.currentTimeMillis();
                    JCL_message_register msgR = (JCL_message_register) msg;
                    if (!TaskContain.contains(msgR.getClassName())) {
                        //ClassPool cp = ClassPool.getDefault();
                        byte[] by = msgR.getJars()[0];
                        String name = msgR.getJarsNames()[0];

                        //InputStream myInputStream = new ByteArrayInputStream(by);
                        //CtClass cc = cp.makeClass(myInputStream);
                        System.err.println("Registering Class Name: " + msgR.getClassName());
                        //Boolean b = new Boolean(orb.register(loadingClassOnAndroid(by,name), msgR.getClassName()));
                        Boolean b = new Boolean(orb.register(loadingClassWithDex(by, name, true), msgR.getClassName()));
                        JCL_result r = new JCL_resultImpl();
                        r.setCorrectResult(b);
                        JCL_message_result RESULT = new MessageResultImpl();
                        RESULT.setType(1);
                        RESULT.setResult(r);

                        // Write data
                        str.RegisterMsg.decrementAndGet();
                        super.WriteObjectOnSock(RESULT, str, false);
                        // End Write data

                        TaskContain.add(msgR.getClassName());

                    } else {

                        Boolean b = true;
                        JCL_result r = new JCL_resultImpl();
                        r.setCorrectResult(b);

                        JCL_message_result RESULT = new MessageResultImpl();
                        RESULT.setType(1);
                        RESULT.setResult(r);

                        // Write data
                        super.WriteObjectOnSock(RESULT, str, false);
                        // End Write data
                    }
                    System.out.println("Tempo: "+(System.currentTimeMillis()-ini)+";\n");
                    break;
                }
                case 61:{
                    // creat new Topic
                    JCL_message_generic jclMsgSN = (JCL_message_generic) msg;
                    boolean b = JCL_ANDROID_Facade.getInstance().createNewTopic(jclMsgSN.getRegisterData());

                    JCL_message_bool RESULT = new MessageBoolImpl();
                    RESULT.setType(61);
                    RESULT.setRegisterData(b);
                    super.WriteObjectOnSock(RESULT, str,false);
                    break;
                }

                case 62:{
                    // unregister Context
                    JCL_message_generic jclMsgSN = (JCL_message_generic) msg;
                    boolean b = JCL_ANDROID_Facade.getInstance().unregisterContext(jclMsgSN.getRegisterData());

                    JCL_message_bool RESULT = new MessageBoolImpl();
                    RESULT.setType(61);
                    RESULT.setRegisterData(b);
                    super.WriteObjectOnSock(RESULT, str,false);
                    break;
                }
                case 63:{
                    // unregister MQTT Context
                    JCL_message_generic jclMsgSN = (JCL_message_generic) msg;
                    boolean b = JCL_ANDROID_Facade.getInstance().unregisterContext(jclMsgSN.getRegisterData());

                    JCL_message_bool RESULT = new MessageBoolImpl();
                    RESULT.setType(61);
                    RESULT.setRegisterData(b);
                    super.WriteObjectOnSock(RESULT, str,false);
                    break;
                }
                case 64:{
                    // remove context action
                    JCL_message_generic jclMsgSN = (JCL_message_generic) msg;
                    boolean b = JCL_ANDROID_Facade.getInstance().removeActingOnContext(jclMsgSN.getRegisterData());

                    JCL_message_bool RESULT = new MessageBoolImpl();
                    RESULT.setType(61);
                    RESULT.setRegisterData(b);
                    super.WriteObjectOnSock(RESULT, str,false);
                    break;
                }
                case 65:{
                    // remove context action
                    JCL_message_generic jclMsgSN = (JCL_message_generic) msg;
                    boolean b = JCL_ANDROID_Facade.getInstance().removeTaskOnContext(jclMsgSN.getRegisterData());

                    JCL_message_bool RESULT = new MessageBoolImpl();
                    RESULT.setType(61);
                    RESULT.setRegisterData(b);
                    super.WriteObjectOnSock(RESULT, str,false);
                    break;
                }

            }
        } catch (Exception e) {
            e.printStackTrace();

        }
    }

    private void status(double d) {
        int x = (int) d;
        if (x >= 99)
            x = 100;
        StringBuilder bar = new StringBuilder("[");
        for (int i = 0; i < 50; i++) {
            if (i < (x / 2)) {
                bar.append("=");
            } else if (i == (x / 2)) {
                bar.append(">");
            } else {
                bar.append(" ");
            }
        }
        bar.append("]   " + x + "%     ");
        System.out.print("\r" + bar.toString());
    }


    public Class loadingClassOnAndroid(byte[] b, String name) {
        final String DEX_FILE_NAME_MYCLASSES = name+".dex";
        try {
            final File dexFile = new File(JCL_ApplicationContext.getContext().getFilesDir(), DEX_FILE_NAME_MYCLASSES);

            final DexFile df = new DexFile();
            final String dexFilePath = dexFile.getAbsolutePath();

            String name1 = null;
            int lasIndex = name.lastIndexOf(".")+1;
            if ( lasIndex < name.length() && lasIndex>=0)
                name1 = name.substring(lasIndex) + ".class";
            else
                name1 = name+".class";
            df.addClass(name1,b);
            df.writeFile(dexFilePath);


            final DexClassLoader dcl = new DexClassLoader(
                    dexFile.getAbsolutePath(),
                    JCL_ApplicationContext.getContext().getCacheDir().getAbsolutePath(),
                    JCL_ApplicationContext.getContext().getApplicationInfo().nativeLibraryDir,
                    JCL_ApplicationContext.getContext().getClassLoader());
            Class cla = dcl.loadClass(name.replace(".", "/"));
            return cla;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        return null;

    }

    public Class loadingClassWithDex(byte[] dex, String name, boolean finalDex) {
        try {
            final File dexFile = new File(JCL_ApplicationContext.getContext().getFilesDir(), name + ".dex");
            if (!finalDex) {
                org.jf.dexlib2.iface.DexFile dexFile1 = null;
                ByteArrayInputStream bis = new ByteArrayInputStream(dex);
                ObjectInput inp = null;
                try {
                    inp = new ObjectInputStream(bis);
                    dexFile1 = (org.jf.dexlib2.iface.DexFile) inp.readObject();
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    try {
                        if (inp != null) {
                            inp.close();
                        }
                    } catch (IOException ex) {
                        // ignore close exception
                    }
                }



                DexFileFactory.writeDexFile(dexFile.getAbsolutePath(), dexFile1);
            }else{
                FileOutputStream out = new FileOutputStream(dexFile);
                out.write(dex);
                out.close();
            }


            final DexClassLoader dcl = new DexClassLoader(
                    dexFile.getAbsolutePath(),
                    JCL_ApplicationContext.getContext().getCacheDir().getAbsolutePath(),
                    JCL_ApplicationContext.getContext().getApplicationInfo().nativeLibraryDir,
                    JCL_ApplicationContext.getContext().getClassLoader());

            Class clas = dcl.loadClass(name.replace(".", "/"));
            return clas;

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

}
