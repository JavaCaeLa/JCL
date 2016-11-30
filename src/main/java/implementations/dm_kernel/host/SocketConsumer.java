package implementations.dm_kernel.host;

import implementations.collections.JCLFuture;
import implementations.dm_kernel.ConnectorImpl;
import implementations.dm_kernel.MessageControlImpl;
import implementations.dm_kernel.MessageGenericImpl;
import implementations.dm_kernel.MessageGlobalVarImpl;
import implementations.dm_kernel.MessageResultImpl;
import implementations.dm_kernel.MessageTaskImpl;
import implementations.sm_kernel.JCL_FacadeImpl;
import implementations.sm_kernel.JCL_orbImpl;
import implementations.sm_kernel.PacuResource;
import interfaces.kernel.JCL_connector;
import interfaces.kernel.JCL_facade;
import interfaces.kernel.JCL_message;
import interfaces.kernel.JCL_message_commons;
import interfaces.kernel.JCL_message_control;
import interfaces.kernel.JCL_message_generic;
import interfaces.kernel.JCL_message_global_var;
import interfaces.kernel.JCL_message_global_var_obj;
import interfaces.kernel.JCL_message_list_global_var;
import interfaces.kernel.JCL_message_list_task;
import interfaces.kernel.JCL_message_long;
import interfaces.kernel.JCL_message_register;
import interfaces.kernel.JCL_message_result;
import interfaces.kernel.JCL_message_task;
import interfaces.kernel.JCL_orb;
import interfaces.kernel.JCL_result;
import interfaces.kernel.JCL_task;
import commom.JCL_handler;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import javassist.ClassPool;
import javassist.CtClass;
import commom.GenericConsumer;
import commom.GenericResource;
import commom.JCL_resultImpl;

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
	private static JCL_FacadeImpl jcl;
	private HashSet<String> TaskContain;
	private ConcurrentHashMap<Long, String> JCLTaskMap;
	private ConcurrentHashMap<String, Set<Object>> JclHashMap;
	private URLClassLoader classLoader = (URLClassLoader) ClassLoader.getSystemClassLoader();

	@SuppressWarnings("unchecked")
	public SocketConsumer(GenericResource<S> re, AtomicBoolean kill, HashSet<String> TaskContain, String hostId,
			Map<Long, JCL_result> results, AtomicLong taskID, ConcurrentHashMap<String, Set<Object>> JclHashMap,
			GenericResource<JCL_task> rp, ConcurrentHashMap<Long, String> JCLTaskMap) {

		super(re, kill);
		this.rp = rp;
		this.jcl = (JCL_FacadeImpl)JCL_FacadeImpl.Holder.getInstancePacu(rp);
		this.TaskContain = TaskContain;
		this.hostId = hostId;
		this.JclHashMap = JclHashMap;
		this.orb = JCL_orbImpl.getInstancePacu();
		this.JCLTaskMap = JCLTaskMap;

	}

	public void addURL(URL url) throws Exception {

		Class<URLClassLoader> clazz = URLClassLoader.class;

		// Use reflection
		Method method = clazz.getDeclaredMethod("addURL", new Class[] { URL.class });
		method.setAccessible(true);
		method.invoke(classLoader, new Object[] { url });
	}

	@Override
	protected void doSomething(S str) {
		try {
			// JCL_message msg =
			// (JCL_message)super.ReadObjectFromSock(str.getKey(),
			// str.getInput());
			JCL_message msg = str.getMsg();

			switch (msg.getType()) {
			// Register Jars
			case 1: {
				// Register Jars
				JCL_message_register msgR = (JCL_message_register) msg;
				if (!TaskContain.contains(msgR.getClassName())) {
					int size = msgR.getJars().length;
					for (int i = 0; i < size; i++) {
						FileOutputStream fout = new FileOutputStream("../user_jars/" + msgR.getJarsNames()[i], false);
						fout.write(msgR.getJars()[i]);
						fout.flush();
						fout.close();
						this.addURL((new File("../user_jars/" + msgR.getJarsNames()[i]).toURI().toURL()));
					}

					JarFile jar = new JarFile("../user_jars/" + msgR.getJarsNames()[0]);
					for (Enumeration<JarEntry> entries = jar.entries(); entries.hasMoreElements();) {
						JarEntry entry = entries.nextElement();
						String file = entry.getName();

						if (file.endsWith(msgR.getClassName() + ".class")) {
							String classname = file.replace('/', '.').substring(0, file.length() - 6);
							System.err.println("Registering Class Name: " + msgR.getClassName());
							Boolean b = new Boolean(orb.register(classname, msgR.getClassName()));
							JCL_result r = new JCL_resultImpl();
							r.setCorrectResult(b);

							JCL_message_result RESULT = new MessageResultImpl();
							RESULT.setType(1);
							RESULT.setResult(r);

							// Write data
							super.WriteObjectOnSock(RESULT, str);
							// End Write data

							TaskContain.add(msgR.getClassName());
							jar.close();
							break;
						}
					}
				} else {

					Boolean b = true;
					JCL_result r = new JCL_resultImpl();
					r.setCorrectResult(b);

					JCL_message_result RESULT = new MessageResultImpl();
					RESULT.setType(1);
					RESULT.setResult(r);

					// Write data
					super.WriteObjectOnSock(RESULT, str);
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
				super.WriteObjectOnSock(RESULT, str);
				// End Write data

				break;
			}

				// Register *.class
			case 3: {
				// Register *.class
				JCL_message_register msgR = (JCL_message_register) msg;
				if (!TaskContain.contains(msgR.getClassName())) {
					ClassPool cp = ClassPool.getDefault();
					byte[] by = msgR.getJars()[0];
					String name = msgR.getJarsNames()[0];

					InputStream myInputStream = new ByteArrayInputStream(by);
					CtClass cc = cp.makeClass(myInputStream);
					System.err.println("Registering Class Name: " + msgR.getClassName());
					Boolean b = new Boolean(orb.register(cc, msgR.getClassName()));
					JCL_result r = new JCL_resultImpl();
					r.setCorrectResult(b);
					JCL_message_result RESULT = new MessageResultImpl();
					RESULT.setType(1);
					RESULT.setResult(r);

					// Write data
					super.WriteObjectOnSock(RESULT, str);
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
					super.WriteObjectOnSock(RESULT, str);
					// End Write data
				}

				break;
			}

				// Execute Task
			case 4: {
				// Execute Task
				JCL_message_task jclT = (JCL_message_task) msg;
				JCL_task t = jclT.getTask();
				t.setTaskTime(System.nanoTime());

				t.setHost(str.getSocketAddress());
				JCLFuture<JCL_result> ticket = (JCLFuture)jcl.execute(t);
				JCL_result r = new JCL_resultImpl();
				r.setCorrectResult(ticket.getTicket());
				JCL_message_result RESULT = new MessageResultImpl();
				RESULT.setType(4);
				RESULT.setResult(r);

				// Write data
				super.WriteObjectOnSock(RESULT, str);
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
				
				JCLFuture<JCL_result> ticket = (JCLFuture)jcl.execute(t);
				JCL_result r = new JCL_resultImpl();
				r.setCorrectResult(ticket.getTicket());
				
				JCL_message_result RESULT = new MessageResultImpl();
				RESULT.setType(5);
				RESULT.setResult(r);

				// Write data
				super.WriteObjectOnSock(RESULT, str);
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
						super.WriteObjectOnSock(RESULT, str);
						// End Write data

					} else {
						str.putOnQueue();
					}
				} else {
					synchronized (JCLTaskMap){
						if (JCLTaskMap.containsKey(id)) {
							JCL_message_control msgctr = new MessageControlImpl();
							msgctr.setType(6);
							msgctr.setRegisterData(JCLTaskMap.get(id));

							// Write data
							super.WriteObjectOnSock(msgctr, str);
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
					super.WriteObjectOnSock(RESULT, str);
					// End Write data
				} else {
					synchronized (JCLTaskMap){
						if (JCLTaskMap.containsKey(id)) {
							JCL_message_control msgctr = new MessageControlImpl();
							msgctr.setType(7);
							msgctr.setRegisterData(JCLTaskMap.get(id));

							// Write data
							super.WriteObjectOnSock(msgctr, str);
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
					super.WriteObjectOnSock(RESULT, str);
					// End Write data
				} else {

					if (JCLTaskMap.containsKey(id)) {
						JCL_message_control msgctr = new MessageControlImpl();
						msgctr.setType(8);
						msgctr.setRegisterData(JCLTaskMap.get(id));

						// Write data
						super.WriteObjectOnSock(msgctr, str);
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
				super.WriteObjectOnSock(RESULT, str);
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
				super.WriteObjectOnSock(RESULT, str);
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
				super.WriteObjectOnSock(RESULT, str);
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
				super.WriteObjectOnSock(RESULT, str);
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
				super.WriteObjectOnSock(RESULT, str);
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
				super.WriteObjectOnSock(RESULT, str);
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
					super.WriteObjectOnSock(RESULT, str);
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
				super.WriteObjectOnSock(resp, str);
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
				super.WriteObjectOnSock(resp, str);
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
				super.WriteObjectOnSock(RESULT, str);
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
				super.WriteObjectOnSock(RESULT, str);
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
					JCLFuture<JCL_result> ticket = (JCLFuture)jcl.execute(t);
					binTicket.put(inst.getKey(), ticket.getTicket());
				}

				JCL_result r = new JCL_resultImpl();
				r.setCorrectResult(binTicket);
				JCL_message_result RESULT = new MessageResultImpl();
				RESULT.setType(25);
				RESULT.setResult(r);

				// Write data
				super.WriteObjectOnSock(RESULT, str);
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
						int size = msgR.getJars().length;
						for (int i = 0; i < size; i++) {
							FileOutputStream fout = new FileOutputStream("../user_jars/" + msgR.getJarsNames()[i],
									false);
							fout.write(msgR.getJars()[i]);
							fout.flush();
							fout.close();
							this.addURL((new File("../user_jars/" + msgR.getJarsNames()[i]).toURI().toURL()));
						}

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

					// Write data
					super.WriteObjectOnSock(RESULT, str);
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
					super.WriteObjectOnSock(RESULT, str);
					// End Write data
				}

				break;
			}

				// createhashKey() type 28
			case 28: {

				// createhashKey() type 28
				JCL_message_generic aux = (JCL_message_generic) msg;
				String name = (String) aux.getRegisterData();
				if (!JclHashMap.containsKey(name)) {
					JclHashMap.put((String) aux.getRegisterData(), new HashSet<Object>());
				}
				JCL_message_generic resp = new MessageGenericImpl();
				resp.setRegisterData(true);

				// Write data
				super.WriteObjectOnSock(resp, str);
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
				super.WriteObjectOnSock(resp, str);
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
				super.WriteObjectOnSock(resp, str);
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
				super.WriteObjectOnSock(resp, str);
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
				super.WriteObjectOnSock(resp, str);
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
				super.WriteObjectOnSock(resp, str);
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
				super.WriteObjectOnSock(resp, str);
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
				super.WriteObjectOnSock(RESULT, str);
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
				super.WriteObjectOnSock(resp, str);
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
				super.WriteObjectOnSock(RESULT, str);
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
				super.WriteObjectOnSock(RESULT, str);
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
			JCLFuture<JCL_result> ticket = (JCLFuture)jcl.execute(t);
			JCL_result r = new JCL_resultImpl();
			r.setCorrectResult(ticket.getTicket());
			JCL_message_result RESULT = new MessageResultImpl();
			RESULT.setType(4);
			RESULT.setResult(r);

			// Write data
			super.WriteObjectOnSock(RESULT, str);
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
						
			JCLFuture<JCL_result> ticket = (JCLFuture)jcl.execute(t);
			JCL_result r = new JCL_resultImpl();
			r.setCorrectResult(ticket.getTicket());
						
			JCL_message_result RESULT = new MessageResultImpl();
			RESULT.setType(5);
			RESULT.setResult(r);

			// Write data
			super.WriteObjectOnSock(RESULT, str);
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
				super.WriteObjectOnSock(RESULT, str);
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
						synchronized (JCLTaskMap){
							
							msgTask.setTask(t);
							orb.getResults().remove(t.getTaskID());
							JCLTaskMap.put(t.getTaskID(), str.getSocketAddress() + "¬" + jclmg.getRegisterData()[0]
									+ "¬" + jclmg.getRegisterData()[1] + "¬" + jclmg.getRegisterData()[2]);
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
				super.WriteObjectOnSock(msgTask, str);
				// End Write data
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
}
