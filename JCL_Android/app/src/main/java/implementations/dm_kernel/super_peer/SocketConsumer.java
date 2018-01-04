package implementations.dm_kernel.super_peer;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

import commom.GenericConsumer;
import commom.GenericResource;
import commom.JCL_handler;
import commom.JCL_resultImpl;
import implementations.dm_kernel.ConnectorImpl;
import implementations.dm_kernel.MessageCommonsImpl;
import implementations.dm_kernel.MessageControlImpl;
import implementations.dm_kernel.MessageGetHostImpl;
import implementations.dm_kernel.MessageImpl;
import implementations.dm_kernel.MessageListTaskImpl;
import implementations.dm_kernel.MessageMetadataImpl;
import implementations.dm_kernel.MessageResultImpl;
import implementations.dm_kernel.server.RoundRobin;
import implementations.util.XORShiftRandom;
import interfaces.kernel.JCL_connector;
import interfaces.kernel.JCL_message;
import interfaces.kernel.JCL_message_bool;
import interfaces.kernel.JCL_message_commons;
import interfaces.kernel.JCL_message_control;
import interfaces.kernel.JCL_message_generic;
import interfaces.kernel.JCL_message_get_host;
import interfaces.kernel.JCL_message_global_var;
import interfaces.kernel.JCL_message_global_var_obj;
import interfaces.kernel.JCL_message_list_global_var;
import interfaces.kernel.JCL_message_list_task;
import interfaces.kernel.JCL_message_long;
import interfaces.kernel.JCL_message_metadata;
import interfaces.kernel.JCL_message_register;
import interfaces.kernel.JCL_message_result;
import interfaces.kernel.JCL_message_task;
import interfaces.kernel.JCL_result;
import interfaces.kernel.JCL_task;

public class SocketConsumer<S extends JCL_handler> extends GenericConsumer<S> {

	private ConcurrentMap<Integer, ConcurrentMap<String, String[]>> slaves_IoT;
	// private ConcurrentMap<String,String[]> slaves,jarsName;
	private ConcurrentMap<Object, String[]> globalVarSlaves;
	// private ConcurrentMap<String,String[]> runningUser;
	private ConcurrentMap<Long, Object[]> taskLocation;
	private AtomicLong numOfTasks;
	private ConcurrentMap<String, List<String>> jarsSlaves;
	private ConcurrentMap<String, JCL_message_register> register;
	// private List<String> slavesIDs;
	private ConcurrentMap<Integer, List<String>> slavesIDs_IoT;
	private commom.JCL_connector routerLink;
	private static XORShiftRandom rand;
	Map<String,String> metaData;
	private boolean verbose;

	public SocketConsumer(GenericResource<S> re, AtomicBoolean kill, ConcurrentMap<Object, String[]> globalVarSlaves,
			ConcurrentMap<Integer, List<String>> slavesIDs_IoT,
			ConcurrentMap<Integer, ConcurrentMap<String, String[]>> slaves_IoT,
			ConcurrentMap<String, List<String>> jarsSlaves,
			ConcurrentMap<Integer, ConcurrentMap<String, String[]>> jarsName_IoT,
			ConcurrentMap<String, JCL_message_register> register, boolean verbose,
			ConcurrentMap<Long, Object[]> taskLocation, AtomicLong numOfTasks, commom.JCL_connector routerLink,Map<String,String> metaData) {
		// TODO Auto-generated constructor stub
		super(re, kill);
		this.globalVarSlaves = globalVarSlaves;
		this.slavesIDs_IoT = slavesIDs_IoT;
		this.slaves_IoT = slaves_IoT;
		this.verbose = verbose;
		this.jarsSlaves = jarsSlaves;
		// this.jarsName_IoT = jarsName_IoT;
		this.register = register;
		this.taskLocation = taskLocation;
		this.numOfTasks = numOfTasks;
		this.routerLink = routerLink;
		this.metaData = metaData;
		
		// Start seed rand GV
		rand = new XORShiftRandom();
	}

	@Override
	protected void doSomething(S str) {
//		try {
//
//			if (str.getHash() != null) {
//				// Get Host
//			//	 System.out.println("Aki Super Peer");
//				List<String> slavesIDs = slavesIDs_IoT.get(5);
//				ConcurrentMap<String, String[]> slaves = slaves_IoT.get(5);
//
//				int hostId = rand.nextInt(0, str.getHash(), slavesIDs.size());
//				String[] hostPort = slaves.get(slavesIDs.get(hostId));
//				String host = hostPort[0];
//				String port = hostPort[1];
//
//				JCL_connector globalVarConnector = new ConnectorImpl();
//				globalVarConnector.connect(host, Integer.parseInt(port), null);
//				byte[] result = globalVarConnector.sendReceiveB(str.getInput(), str.getKey());
//				globalVarConnector.disconnect();
//
//			//	 System.out.println("Aki Super Peer 1");
//
//				// Write data
//				str.sendB(result);
//				// End Write data
//
//			//	System.out.println("Aki Super Peer 2");
//			} else {
//
//				JCL_message msg = str.getMsg();
//
//				// Get local time
//				Locale locale = new Locale("pt", "BR");
//				GregorianCalendar calendar = new GregorianCalendar();
//				SimpleDateFormat formatador = new SimpleDateFormat("dd' de 'MMMMM' de 'yyyy' - 'HH':'mm'h'", locale);
//
//				switch (msg.getType()) {
//
//				case 1: {
//					if (verbose)
//						System.err.println(
//								msg.getType() + " - " + "register() - " + formatador.format(calendar.getTime()));
//
//					JCL_message_register msgR = (JCL_message_register) msg;
//					if (!register.containsKey(msgR.getClassName())) {
//						register.put(msgR.getClassName(), msgR);
//						JCL_result r = new JCL_resultImpl();
//						r.setCorrectResult(Boolean.TRUE);
//						JCL_message_result RESULT = new MessageResultImpl();
//						RESULT.setType(1);
//						RESULT.setResult(r);
//
//						// Write data
//						super.WriteObjectOnSock(RESULT, str);
//						// End Write data
//
//					//	System.out.println("Send register");
//
//					} else {
//
//						JCL_result r = new JCL_resultImpl();
//						r.setCorrectResult(Boolean.TRUE);
//						JCL_message_result RESULT = new MessageResultImpl();
//						RESULT.setType(1);
//						RESULT.setResult(r);
//
//						// Write data
//						super.WriteObjectOnSock(RESULT, str);
//						// End Write data
//				//		System.out.println("Send register2");
//
//					}
//
//					break;
//				}
//
//				case 2: {
//					if (verbose)
//						System.err.println(
//								msg.getType() + " - " + "unRegister() - " + formatador.format(calendar.getTime()));
//					JCL_message_commons msgR = (JCL_message_commons) msg;
//					// JCL_message_control mc = new MessageControlImpl();
//					boolean ok = true;
//
//					ConcurrentMap<String, String[]> slaves = this.slaves_IoT.get(5);
//
//					for (String[] oneHostPort : slaves.values()) {
//						if (jarsSlaves.get(oneHostPort[2] + oneHostPort[1]).contains(msgR.getRegisterData()[0])) {
//							JCL_connector taskConnector = new ConnectorImpl();
//							taskConnector.connect(oneHostPort[0], Integer.parseInt(oneHostPort[1]), null);
//							// JCL_message_commons msgUn = new
//							// MessageCommonsImpl();
//							// msgUn.setType(2);
//							// msgUn.setRegisterData(msgR.getRegisterData()[0]);
//							JCL_message_result msgRes = taskConnector.sendReceive(msgR, null);
//							if (msgRes.getResult().getCorrectResult() != null) {
//								jarsSlaves.get(oneHostPort[2] + oneHostPort[1]).remove(msgR.getRegisterData()[0]);
//							} else {
//								msgRes.getResult().getErrorResult().printStackTrace();
//								ok = false;
//							}
//							taskConnector.disconnect();
//
//						}
//					}
//					register.remove(msgR.getRegisterData()[0]);
//					// ConcurrentMap<String, String[]> jarsName =
//					// this.jarsName_IoT.get(5);
//					// jarsName.remove(msgR.getRegisterData()[0]);
//
//					// mc.setRegisterData(Boolean.toString());
//
//					JCL_result r = new JCL_resultImpl();
//					r.setCorrectResult(ok);
//					JCL_message_result RESULT = new MessageResultImpl();
//					RESULT.setType(2);
//					RESULT.setResult(r);
//
//					// Write data
//					super.WriteObjectOnSock(RESULT, str);
//					// End Write data
//
//					break;
//				}
//
//				case 3: {
//					if (verbose)
//						System.err.println(
//								msg.getType() + " - " + "register() - " + formatador.format(calendar.getTime()));
//					JCL_message_register msgR = (JCL_message_register) msg;
//					if (!register.containsKey(msgR.getClassName())){
//						register.put(msgR.getClassName(), msgR);
//						// ConcurrentMap<String, String[]> jarsName =
//						// this.jarsName_IoT.get(5);
//						// jarsName.put(msgR.getClassName(),
//						// msgR.getJarsNames());
//						JCL_result r = new JCL_resultImpl();
//						r.setCorrectResult(Boolean.TRUE);
//						JCL_message_result RESULT = new MessageResultImpl();
//						RESULT.setType(3);
//						RESULT.setResult(r);
//
//						// Write data
//						super.WriteObjectOnSock(RESULT, str);
//						// End Write data
//
//					} else {
//
//						JCL_result r = new JCL_resultImpl();
//						r.setCorrectResult(Boolean.TRUE);
//						JCL_message_result RESULT = new MessageResultImpl();
//						RESULT.setType(1);
//						RESULT.setResult(r);
//
//						// Write data
//						super.WriteObjectOnSock(RESULT, str);
//						// End Write data
//					}
//
//					break;
//				}
//
//				case 4: {
//					if (verbose)
//						System.err.println(
//								msg.getType() + " - " + "execute() - " + formatador.format(calendar.getTime()));
//
//					// Execute Task
//					JCL_message_task jclT = (JCL_message_task) msg;
//					JCL_task t = jclT.getTask();
//					// t.setTaskTime(System.nanoTime());
//
//					// t.setHost(str.getSocketAddress());
//					ConcurrentMap<String, String[]> slaves = this.slaves_IoT.get(5);
//					List<String> slavesIDs = this.slavesIDs_IoT.get(5);
//
//					//String[] hostPort = RoundRobin.next(slavesIDs, slaves);
//
////					String host = hostPort[0];
////					String port = hostPort[1];
//					long ticket = 0;
//
//					JCL_connector taskConnector = new ConnectorImpl();
//					taskConnector.connect(host, Integer.parseInt(port), null);
//
////					if (jarsSlaves.get(hostPort[2] + port).contains(t.getObjectName())) {
////
////						JCL_message_result msgResult = taskConnector.sendReceive(jclT, null);
////						ticket = (Long) msgResult.getResult().getCorrectResult();
////						taskConnector.disconnect();
////
////					} else {
////
////						synchronized (jarsSlaves) {
////
////							if (jarsSlaves.get(hostPort[2] + port).contains(t.getObjectName())) {
////
////								JCL_message_result msgResult = taskConnector.sendReceive(jclT, null);
////								ticket = (Long) msgResult.getResult().getCorrectResult();
////								taskConnector.disconnect();
////
////							} else {
////
////								if (register.containsKey(t.getObjectName())) {
////									JCL_result result = taskConnector.sendReceive(register.get(t.getObjectName()), null)
////											.getResult();
////
////									if (((Boolean) result.getCorrectResult()).booleanValue()) {
////										jarsSlaves.get(hostPort[2] + port).add(t.getObjectName());
////										JCL_message_result msgResult = taskConnector.sendReceive(jclT, null);
////										ticket = (Long) msgResult.getResult().getCorrectResult();
////										taskConnector.disconnect();
////
////									}
////								} else {
////									System.out.println("CLASS NOT REGISTERED - TRY AGAIN");
////									str.putOnQueue();
////									break;
////								}
////							}
////						}
////					}
//
//					JCL_result r = new JCL_resultImpl();
//
//					long tick = numOfTasks.getAndIncrement();
//					//this.taskLocation.put(tick, new Object[] { ticket, host, port });
//					r.setCorrectResult(tick);
//					JCL_message_result RESULT = new MessageResultImpl();
//					RESULT.setType(4);
//					RESULT.setResult(r);
//
//					// Write data
//					super.WriteObjectOnSock(RESULT, str);
//					// End Write data
//
//					break;
//				}
//
//				case 5: {
//					if (verbose)
//						System.err.println(
//								msg.getType() + " - " + "execute() - " + formatador.format(calendar.getTime()));
//
//					// Execute Task
//					JCL_message_task jclT = (JCL_message_task) msg;
//					JCL_task t = jclT.getTask();
//					// t.setTaskTime(System.nanoTime());
//
//					// t.setHost(str.getSocketAddress());
//					ConcurrentMap<String, String[]> slaves = this.slaves_IoT.get(5);
//					List<String> slavesIDs = this.slavesIDs_IoT.get(5);
//
//					//String[] hostPort = RoundRobin.next(slavesIDs, slaves);
//
//					String host = hostPort[0];
//					String port = hostPort[1];
//					long ticket = 0;
//
//					JCL_connector taskConnector = new ConnectorImpl();
//					taskConnector.connect(host, Integer.parseInt(port), null);
//
//					if (jarsSlaves.get(hostPort[2] + port).contains(t.getObjectName())) {
//
//						JCL_message_result msgResult = taskConnector.sendReceive(jclT, null);
//						ticket = (Long) msgResult.getResult().getCorrectResult();
//						taskConnector.disconnect();
//
//					} else {
//
//						synchronized (jarsSlaves){
//
//							if (jarsSlaves.get(hostPort[2] + port).contains(t.getObjectName())) {
//
//								JCL_message_result msgResult = taskConnector.sendReceive(jclT, null);
//								ticket = (Long) msgResult.getResult().getCorrectResult();
//								taskConnector.disconnect();
//
//							} else {
//								if (register.containsKey(t.getObjectName())) {
//									JCL_result result = taskConnector.sendReceive(register.get(t.getObjectName()), null)
//											.getResult();
//
//									if (((Boolean) result.getCorrectResult()).booleanValue()) {
//										jarsSlaves.get(hostPort[2] + port).add(t.getObjectName());
//										JCL_message_result msgResult = taskConnector.sendReceive(jclT, null);
//										ticket = (Long) msgResult.getResult().getCorrectResult();
//										taskConnector.disconnect();
//
//									}
//								} else {
//									System.out.println("CLASS NOT REGISTERED - TRY AGAIN");
//									str.putOnQueue();
//									break;
//								}
//							}
//						}
//					}
//
//					JCL_result r = new JCL_resultImpl();
//
//					long tick = numOfTasks.getAndIncrement();
//					this.taskLocation.put(tick, new Object[] { ticket, host, port });
//					r.setCorrectResult(tick);
//					JCL_message_result RESULT = new MessageResultImpl();
//					RESULT.setType(5);
//					RESULT.setResult(r);
//
//					// Write data
//					super.WriteObjectOnSock(RESULT, str);
//					// End Write data
//
//					break;
//				}
//
//					// getResultBlocking(id) type 6
//				case 6: {
//					// getResultBlocking(id) type 6
//					JCL_message_long jclC = (JCL_message_long) msg;
//					long id = jclC.getRegisterData()[0];
//					Object[] hostPortTicket = this.taskLocation.get(id);
//
//					long newID = (long) hostPortTicket[0];
//					String Host = (String) hostPortTicket[1];
//					String Port = (String) hostPortTicket[2];
//
//					// config msg
//					jclC.setType(7);
//					jclC.setRegisterData(newID);
//
//					// Connection
//					JCL_connector taskConnector = new ConnectorImpl();
//					taskConnector.connect(Host, Integer.parseInt(Port), null);
//					JCL_message msgResult = taskConnector.sendReceiveG(jclC, null);
//					taskConnector.disconnect();
//					// Connection
//
//					// Scheduler to new host
//					if (msgResult instanceof JCL_message_result) {
//
//						JCL_result result = ((JCL_message_result) msgResult).getResult();
//
//						// Get result
//						if (!((result.getCorrectResult() == null) && (result.getErrorResult() == null))) {
//
//							// result.addTime(System.nanoTime());
//							msgResult.setType(6);
//
//							// Write data
//							super.WriteObjectOnSock(msgResult, str);
//							// End Write data
//						} else {
//
//							// config msg
//							jclC.setType(6);
//							jclC.setRegisterData(id);
//
//							str.putOnQueue();
//							break;
//
//						}
//					} else {
//
//						// Get new Host
//						String[] hostPortID = ((JCL_message_control) msgResult).getRegisterData()[0].split("¬");
//						Host = hostPortID[0];
//						Port = hostPortID[1];
//						String newID2 = hostPortID[2];
//
//
//						// Update Host Location
//						this.taskLocation.put(id, new Object[] { newID2, Host, Port });
//
//						// config msg
//						jclC.setType(6);
//						jclC.setRegisterData(id);
//
//						// put on Queue
//						str.putOnQueue();
//						break;
//					}
//
//					break;
//				}
//
//					// getResultUnblocking(id) type 7
//				case 7: {
//
//					JCL_message_long jclC = (JCL_message_long) msg;
//					long id = jclC.getRegisterData()[0];
//					Object[] hostPortTicket = this.taskLocation.get(id);
//
//					long newID = (long) hostPortTicket[0];
//					String Host = (String) hostPortTicket[1];
//					String Port = (String) hostPortTicket[2];
//
//					// config msg
//					jclC.setRegisterData(newID);
//
//					// Connection
//					JCL_connector taskConnector = new ConnectorImpl();
//					taskConnector.connect(Host, Integer.parseInt(Port), null);
//					JCL_message msgResult = taskConnector.sendReceiveG(jclC, null);
//					taskConnector.disconnect();
//					// Connection
//
//					// Scheduler to new host
//					if (msgResult instanceof JCL_message_result) {
//
//						JCL_result result = ((JCL_message_result) msgResult).getResult();
//
//						// result.addTime(System.nanoTime());
//						msgResult.setType(7);
//
//						// Write data
//						super.WriteObjectOnSock(msgResult, str);
//						// End Write data
//
//					} else {
//
//
//						// Get new Host
//						String[] hostPortID = ((JCL_message_control) msgResult).getRegisterData()[0].split("¬");
//						Host = hostPortID[0];
//						Port = hostPortID[1];
//						String newID2 = hostPortID[2];
//
//						// Update Host Location
//						this.taskLocation.put(id, new Object[] { newID2, Host, Port });
//
//						// config msg
//						jclC.setRegisterData(id);
//
//						// put on Queue
//						str.putOnQueue();
//						break;
//					}
//
//					break;
//				}
//
//					// removeResult(id) type 8
//				case 8: {
//
//					// removeResult(id) type 8
//					JCL_message_long jclC = (JCL_message_long) msg;
//					long id = jclC.getRegisterData()[0];
//					Object[] hostPortTicket = this.taskLocation.get(id);
//
//					long newID = (long) hostPortTicket[0];
//					String Host = (String) hostPortTicket[1];
//					String Port = (String) hostPortTicket[2];
//
//					// config msg
//					jclC.setRegisterData(newID);
//
//					// Connection
//					JCL_connector taskConnector = new ConnectorImpl();
//					taskConnector.connect(Host, Integer.parseInt(Port), null);
//					JCL_message msgResult = taskConnector.sendReceiveG(jclC, null);
//					taskConnector.disconnect();
//					// Connection
//
//					// Scheduler to new host
//					if (msgResult instanceof JCL_message_result) {
//
//						// Write data
//						super.WriteObjectOnSock(msgResult, str);
//						// End Write data
//
//					} else {
//
//						// Get new Host
//						String[] hostPortID = ((JCL_message_control) msgResult).getRegisterData()[0].split("¬");
//						Host = hostPortID[0];
//						Port = hostPortID[1];
//						String newID2 = hostPortID[2];
//
//						// Update Host Location
//						this.taskLocation.put(id, new Object[] { newID2, Host, Port });
//
//						// config msg
//						jclC.setRegisterData(id);
//
//						// put on Queue
//						str.putOnQueue();
//						break;
//					}
//
//					break;
//				}
//
//					// instantiateGlobalVar(id) type 9
//				case 9: {
//
//					// instantiateGlobalVar(id) type 9
//					JCL_message_global_var_obj jclGV = (JCL_message_global_var_obj) msg;
//
//					// Get Host
//					List<String> slavesIDs = slavesIDs_IoT.get(5);
//					ConcurrentMap<String, String[]> slaves = slaves_IoT.get(5);
//
//					int hostId = rand.nextInt(0, jclGV.getVarKey().hashCode(), slavesIDs.size());
//					String[] hostPort = slaves.get(slavesIDs.get(hostId));
//					String host = hostPort[0];
//					String port = hostPort[1];
//
//					JCL_connector globalVarConnector = new ConnectorImpl();
//					globalVarConnector.connect(host, Integer.parseInt(port), null);
//					JCL_message_result result = globalVarConnector.sendReceive(jclGV, null);
//					globalVarConnector.disconnect();
//
//					// Write data
//					super.WriteObjectOnSock(result, str);
//					// End Write data
//
//					break;
//				}
//
//				case 10: {
//
//					// instantiateGlobalVar(id) type 9
//					JCL_message_global_var jclGV = (JCL_message_global_var) msg;
//
//					// Get Host
//					List<String> slavesIDs = slavesIDs_IoT.get(5);
//					ConcurrentMap<String, String[]> slaves = slaves_IoT.get(5);
//
//					int hostId = rand.nextInt(0, jclGV.getVarKey().hashCode(), slavesIDs.size());
//					String[] hostPort = slaves.get(slavesIDs.get(hostId));
//					String host = hostPort[0];
//					String port = hostPort[1];
//
//					JCL_connector globalVarConnector = new ConnectorImpl();
//					globalVarConnector.connect(host, Integer.parseInt(port), null);
//					JCL_message_result result = globalVarConnector.sendReceive(jclGV, null);
//					globalVarConnector.disconnect();
//
//					// Write data
//					super.WriteObjectOnSock(result, str);
//					// End Write data
//
//					break;
//				}
//
//				case 11: {
//
//					// instantiateGlobalVar(id) type 9
//					JCL_message_generic jclGV = (JCL_message_generic) msg;
//
//					// Get Host
//					List<String> slavesIDs = slavesIDs_IoT.get(5);
//					ConcurrentMap<String, String[]> slaves = slaves_IoT.get(5);
//
//					int hostId = rand.nextInt(0, jclGV.getRegisterData().hashCode(), slavesIDs.size());
//					String[] hostPort = slaves.get(slavesIDs.get(hostId));
//					String host = hostPort[0];
//					String port = hostPort[1];
//
//					JCL_connector globalVarConnector = new ConnectorImpl();
//					globalVarConnector.connect(host, Integer.parseInt(port), null);
//					JCL_message_result result = globalVarConnector.sendReceive(jclGV, null);
//					globalVarConnector.disconnect();
//
//					// Write data
//					super.WriteObjectOnSock(result, str);
//					// End Write data
//
//					break;
//				}
//
//				case 12: {
//
//					// instantiateGlobalVar(id) type 9
//					JCL_message_global_var jclGV = (JCL_message_global_var) msg;
//
//					// Get Host
//					List<String> slavesIDs = slavesIDs_IoT.get(5);
//					ConcurrentMap<String, String[]> slaves = slaves_IoT.get(5);
//
//					int hostId = rand.nextInt(0, jclGV.getVarKey().hashCode(), slavesIDs.size());
//					String[] hostPort = slaves.get(slavesIDs.get(hostId));
//					String host = hostPort[0];
//					String port = hostPort[1];
//
//					JCL_connector globalVarConnector = new ConnectorImpl();
//					globalVarConnector.connect(host, Integer.parseInt(port), null);
//					JCL_message_result result = globalVarConnector.sendReceive(jclGV, null);
//					globalVarConnector.disconnect();
//
//					// Write data
//					super.WriteObjectOnSock(result, str);
//					// End Write data
//
//					break;
//				}
//
//				case 13: {
//
//					// instantiateGlobalVar(id) type 9
//					JCL_message_global_var jclGV = (JCL_message_global_var) msg;
//
//					// Get Host
//					List<String> slavesIDs = slavesIDs_IoT.get(5);
//					ConcurrentMap<String, String[]> slaves = slaves_IoT.get(5);
//
//					int hostId = rand.nextInt(0, jclGV.getVarKey().hashCode(), slavesIDs.size());
//					String[] hostPort = slaves.get(slavesIDs.get(hostId));
//					String host = hostPort[0];
//					String port = hostPort[1];
//
//					JCL_connector globalVarConnector = new ConnectorImpl();
//					globalVarConnector.connect(host, Integer.parseInt(port), null);
//					JCL_message_result result = globalVarConnector.sendReceive(jclGV, null);
//					globalVarConnector.disconnect();
//
//					// Write data
//					super.WriteObjectOnSock(result, str);
//					// End Write data
//
//					break;
//				}
//
//				case 14: {
//
//					JCL_message_generic jclGV = (JCL_message_generic) msg;
//
//					// Get Host
//					List<String> slavesIDs = slavesIDs_IoT.get(5);
//					ConcurrentMap<String, String[]> slaves = slaves_IoT.get(5);
//
//					int hostId = rand.nextInt(0, jclGV.getRegisterData().hashCode(), slavesIDs.size());
//					String[] hostPort = slaves.get(slavesIDs.get(hostId));
//					String host = hostPort[0];
//					String port = hostPort[1];
//
//					JCL_connector globalVarConnector = new ConnectorImpl();
//					globalVarConnector.connect(host, Integer.parseInt(port), null);
//					byte[] result = globalVarConnector.sendReceiveB(str.getInput(), str.getKey());
//					// JCL_message_result result =
//					// globalVarConnector.sendReceive(jclGV);
//					globalVarConnector.disconnect();
//
//					// Write data
//					str.sendB(result);
//					// super.WriteObjectOnSock(result, str);
//					// End Write data
//
//					break;
//				}
//
//				case 15: {
//
//					// instantiateGlobalVar(id) type 9
//					JCL_message_generic jclGV = (JCL_message_generic) msg;
//
//					// Get Host
//					List<String> slavesIDs = slavesIDs_IoT.get(5);
//					ConcurrentMap<String, String[]> slaves = slaves_IoT.get(5);
//
//					int hostId = rand.nextInt(0, jclGV.getRegisterData().hashCode(), slavesIDs.size());
//					String[] hostPort = slaves.get(slavesIDs.get(hostId));
//					String host = hostPort[0];
//					String port = hostPort[1];
//
//					JCL_connector globalVarConnector = new ConnectorImpl();
//					globalVarConnector.connect(host, Integer.parseInt(port), null);
//					JCL_message_result result = globalVarConnector.sendReceive(jclGV, null);
//					globalVarConnector.disconnect();
//
//					// Write data
//					super.WriteObjectOnSock(result, str);
//					// End Write data
//
//					break;
//				}
//
//				case 17: {
//
//					JCL_message_generic jclGV = (JCL_message_generic) msg;
//
//					// Get Host
//					List<String> slavesIDs = slavesIDs_IoT.get(5);
//					ConcurrentMap<String, String[]> slaves = slaves_IoT.get(5);
//
//					int hostId = rand.nextInt(0, jclGV.getRegisterData().hashCode(), slavesIDs.size());
//					String[] hostPort = slaves.get(slavesIDs.get(hostId));
//					String host = hostPort[0];
//					String port = hostPort[1];
//
//					JCL_connector globalVarConnector = new ConnectorImpl();
//					globalVarConnector.connect(host, Integer.parseInt(port), null);
//					byte[] result = globalVarConnector.sendReceiveB(str.getInput(), str.getKey());
//					globalVarConnector.disconnect();
//
//					// Write data
//					str.sendB(result);
//					// super.WriteObjectOnSock(result, str);
//					// End Write data
//
//					break;
//				}
//
//					// containsTask type 18
//				case 18: {
//
//					// containsTask type 18
//					JCL_message_control aux = (JCL_message_control) msg;
//					boolean b = register.containsKey(aux.getRegisterData()[0]);
//
//					JCL_message_control resp = new MessageControlImpl();
//					resp.setRegisterData(String.valueOf(b));
//
//					// Write data
//					super.WriteObjectOnSock(resp, str);
//					// End Write data
//					break;
//				}
//
//				case 20: {
//
//					JCL_message_generic jclGV = (JCL_message_generic) msg;
//
//					// Get Host
//					List<String> slavesIDs = slavesIDs_IoT.get(5);
//					ConcurrentMap<String, String[]> slaves = slaves_IoT.get(5);
//
//					int hostId = rand.nextInt(0, jclGV.getRegisterData().hashCode(), slavesIDs.size());
//					String[] hostPort = slaves.get(slavesIDs.get(hostId));
//					String host = hostPort[0];
//					String port = hostPort[1];
//
//					JCL_connector globalVarConnector = new ConnectorImpl();
//					globalVarConnector.connect(host, Integer.parseInt(port), null);
//					byte[] result = globalVarConnector.sendReceiveB(str.getInput(), str.getKey());
//					globalVarConnector.disconnect();
//
//					// Write data
//					str.sendB(result);
//					// super.WriteObjectOnSock(result, str);
//					// End Write data
//
//					break;
//				}
//
//				case 22: {
//
//					// Get Host
//					List<String> slavesIDs = slavesIDs_IoT.get(5);
//					ConcurrentMap<String, String[]> slaves = slaves_IoT.get(5);
//
//					boolean clean = true;
//					JCL_message mclean = new MessageImpl();
//					mclean.setType(22);
//					JCL_connector controlConnectorClean = new ConnectorImpl();
//
//					for (String id : slavesIDs) {
//						String[] hostPort = slaves.get(id);
//						String host = hostPort[0];
//						String port = hostPort[1];
//
//						controlConnectorClean.connect(host, Integer.parseInt(port), null);
//						JCL_message_result mrclean = controlConnectorClean.sendReceive(mclean, null);
//						controlConnectorClean.disconnect();
//						if (!((Boolean) mrclean.getResult().getCorrectResult())) {
//							clean = false;
//						}
//					}
//
//					JCL_result jclR = new JCL_resultImpl();
//					jclR.setCorrectResult(clean);
//					JCL_message_result RESULT = new MessageResultImpl();
//					RESULT.setType(22);
//					RESULT.setResult(jclR);
//
//					// Write data
//					super.WriteObjectOnSock(RESULT, str);
//					// End Write data
//
//					break;
//				}
//
//					// Fazer
//				case 25: {
//					if (verbose)
//						System.err.println(
//								msg.getType() + " - " + "executeBin() - " + formatador.format(calendar.getTime()));
//
//
//					JCL_message_list_task jclT = (JCL_message_list_task) msg;
//					//Map<String, JCL_task> binMap = jclT.getMapTask();
//					Map<String, Long> binTicket = new HashMap<String, Long>();
//
//					int coresT = Integer.parseInt(metaData.get("CORE(S)"));
//					int size = binMap.size();
//					int contN = 0;
//
//					List<String> slavesIDs = slavesIDs_IoT.get(5);
//					ConcurrentMap<String, String[]> slaves = slaves_IoT.get(5);
//
//
//					// Execute class
//					int hostID = 0;
//					int cores = Integer.parseInt(slaves.get(slavesIDs.get(hostID))[3]);
//					int k = (int)(size/(float)coresT);
//					String[] hostPort = slaves.get(slavesIDs.get(hostID));
//					String host = hostPort[0];
//					String port = hostPort[1];
//					String mac = hostPort[2];
//
//					int total = (cores*((k <= 0)?1:k));
//					JCL_message_list_task msgTask = new MessageListTaskImpl();
//					msgTask.setType(25);
//
//					Iterator<Entry<String, JCL_task>> taskIDTask = binMap.entrySet().iterator();
//
//					while(taskIDTask.hasNext()){
//
//						Entry<String, JCL_task> inst = taskIDTask.next();
//
//						if(contN < total){
//
//							JCL_task t = inst.getValue();
//
//							if (jarsSlaves.get(mac + port).contains(t.getObjectName())) {
//
//								Long tick = numOfTasks.getAndIncrement();
//								binTicket.put(inst.getKey(),tick);
//								msgTask.addTask(tick.toString(),t);
//
//							}else {
//
//								synchronized (jarsSlaves) {
//
//									if (jarsSlaves.get(mac + port).contains(t.getObjectName())) {
//
//										Long tick = numOfTasks.getAndIncrement();
//										binTicket.put(inst.getKey(),tick);
//										msgTask.addTask(tick.toString(),t);
//
//									} else {
//
//										if (register.containsKey(t.getObjectName())) {
//
//											JCL_connector taskConnector = new ConnectorImpl();
//											taskConnector.connect(host, Integer.parseInt(port), null);
//											JCL_result result = taskConnector.sendReceive(register.get(t.getObjectName()), null)
//													.getResult();
//											taskConnector.disconnect();
//
//											if (((Boolean) result.getCorrectResult()).booleanValue()) {
//												jarsSlaves.get(mac + port).add(t.getObjectName());
//
//												Long tick = numOfTasks.getAndIncrement();
//												binTicket.put(inst.getKey(),tick);
//												msgTask.addTask(tick.toString(),t);
//
//											}
//										} else {
//											System.out.println("CLASS NOT REGISTERED - TRY AGAIN");
//											str.putOnQueue();
//											break;
//										}
//									}
//								}
//							}
//
//							contN++;
//						}
//						if((contN==total) || (!taskIDTask.hasNext())){
//							hostPort = slaves.get(slavesIDs.get(hostID));
//							host = hostPort[0];
//							port = hostPort[1];
//							mac = hostPort[2];
//
//							JCL_connector Connector = new ConnectorImpl();
//							Connector.connect(host, Integer.parseInt(port), null);
//							JCL_message_result msgResult = Connector.sendReceive(msgTask, null);
//							Connector.disconnect();
//
//							Map<String,Long> tickets = (Map<String,Long>) msgResult.getResult().getCorrectResult();
//
//							for(Entry<String, Long> i:tickets.entrySet()){
//								this.taskLocation.put(Long.parseLong(i.getKey()), new Object[] { i.getValue(), host, port });
//							}
//
//							hostID++;
//							cores = Integer.parseInt(slaves.get(slavesIDs.get(hostID))[3]);
//							total = (int)(cores*(size/(float)coresT));
//							contN = 0;
//							if (slaves.size()==(hostID-1)) total = binMap.size();
//							msgTask = new MessageListTaskImpl();
//							msgTask.setType(25);
//						}
//					}
//
//					JCL_result r = new JCL_resultImpl();
//					r.setCorrectResult(binTicket);
//					JCL_message_result RESULT = new MessageResultImpl();
//					RESULT.setType(25);
//					RESULT.setResult(r);
//
//					// Write data
//					super.WriteObjectOnSock(RESULT, str);
//					// End Write data
//
//					break;
//				}
//
//				case 27: {
//					if (verbose)
//						System.err.println(
//								msg.getType() + " - " + "GVregister() - " + formatador.format(calendar.getTime()));
//					JCL_message_register msgR = (JCL_message_register) msg;
//
//					if (!register.containsKey(msgR.getClassName())) {
//						register.put(msgR.getClassName(), msgR);
//
//						// Get Host
//						List<String> slavesIDs = slavesIDs_IoT.get(5);
//						ConcurrentMap<String, String[]> slaves = slaves_IoT.get(5);
//
//						boolean reg = true;
//						JCL_connector Connector = new ConnectorImpl();
//
//						for (String id : slavesIDs) {
//							String[] hostPort = slaves.get(id);
//							String host = hostPort[0];
//							String port = hostPort[1];
//
//							Connector.connect(host, Integer.parseInt(port), null);
//							JCL_message_result mrclean = Connector.sendReceive(msgR, null);
//							Connector.disconnect();
//
//							if (!((Boolean) mrclean.getResult().getCorrectResult())) {
//								reg = false;
//							}
//						}
//
//						// Mount result
//						JCL_result r = new JCL_resultImpl();
//						r.setCorrectResult(reg);
//						JCL_message_result RESULT = new MessageResultImpl();
//						RESULT.setType(1);
//						RESULT.setResult(r);
//						// End mount result
//
//						// Write data
//						super.WriteObjectOnSock(RESULT, str);
//						// End Write data
//
//					} else {
//
//						JCL_result r = new JCL_resultImpl();
//						r.setCorrectResult(Boolean.TRUE);
//						JCL_message_result RESULT = new MessageResultImpl();
//						RESULT.setType(1);
//						RESULT.setResult(r);
//
//						// Write data
//						super.WriteObjectOnSock(RESULT, str);
//						// End Write data
//					}
//
//					break;
//				}
//
//				case 28: {
//					JCL_message_generic aux = (JCL_message_generic) msg;
//					String name = (String) aux.getRegisterData();
//
//					// Get Host
//					List<String> slavesIDs = slavesIDs_IoT.get(5);
//					ConcurrentMap<String, String[]> slaves = slaves_IoT.get(5);
//
//					int hostId = rand.nextInt(0, name.hashCode(), slavesIDs.size());
//					String[] hostPort = slaves.get(slavesIDs.get(hostId));
//					String host = hostPort[0];
//					String port = hostPort[1];
//
//					JCL_connector controlConnector = new ConnectorImpl();
//					controlConnector.connect(host, Integer.parseInt(port), null);
//					JCL_message_generic mr = (JCL_message_generic) controlConnector.sendReceiveG(aux, null);
//					controlConnector.disconnect();
//
//					// Write data
//					super.WriteObjectOnSock(mr, str);
//					// End Write data
//
//					break;
//				}
//
//				case 29: {
//					JCL_message_generic aux = (JCL_message_generic) msg;
//					Object[] dados = (Object[]) aux.getRegisterData();
//
//					// Get Host
//					List<String> slavesIDs = slavesIDs_IoT.get(5);
//					ConcurrentMap<String, String[]> slaves = slaves_IoT.get(5);
//
//					int hostId = rand.nextInt(0, dados[0].hashCode(), slavesIDs.size());
//					String[] hostPort = slaves.get(slavesIDs.get(hostId));
//					String host = hostPort[0];
//					String port = hostPort[1];
//
//					JCL_connector controlConnector = new ConnectorImpl();
//					controlConnector.connect(host, Integer.parseInt(port), null);
//					JCL_message_generic mr = (JCL_message_generic) controlConnector.sendReceiveG(aux, null);
//					controlConnector.disconnect();
//
//					// Write data
//					super.WriteObjectOnSock(mr, str);
//					// End Write data
//
//					break;
//				}
//
//				case 30: {
//					JCL_message_generic aux = (JCL_message_generic) msg;
//					Object[] dados = (Object[]) aux.getRegisterData();
//
//					// Get Host
//					List<String> slavesIDs = slavesIDs_IoT.get(5);
//					ConcurrentMap<String, String[]> slaves = slaves_IoT.get(5);
//
//					int hostId = rand.nextInt(0, dados[0].hashCode(), slavesIDs.size());
//					String[] hostPort = slaves.get(slavesIDs.get(hostId));
//					String host = hostPort[0];
//					String port = hostPort[1];
//
//					JCL_connector controlConnector = new ConnectorImpl();
//					controlConnector.connect(host, Integer.parseInt(port), null);
//					JCL_message_generic mr = (JCL_message_generic) controlConnector.sendReceiveG(aux, null);
//					controlConnector.disconnect();
//
//					// Write data
//					super.WriteObjectOnSock(mr, str);
//					// End Write data
//
//					break;
//				}
//
//				case 31: {
//					JCL_message_generic aux = (JCL_message_generic) msg;
//					Object[] dados = (Object[]) aux.getRegisterData();
//
//					// Get Host
//					List<String> slavesIDs = slavesIDs_IoT.get(5);
//					ConcurrentMap<String, String[]> slaves = slaves_IoT.get(5);
//
//					int hostId = rand.nextInt(0, dados[0].hashCode(), slavesIDs.size());
//					String[] hostPort = slaves.get(slavesIDs.get(hostId));
//					String host = hostPort[0];
//					String port = hostPort[1];
//
//					JCL_connector controlConnector = new ConnectorImpl();
//					controlConnector.connect(host, Integer.parseInt(port), null);
//					JCL_message_generic mr = (JCL_message_generic) controlConnector.sendReceiveG(aux, null);
//					controlConnector.disconnect();
//
//					// Write data
//					super.WriteObjectOnSock(mr, str);
//					// End Write data
//
//					break;
//				}
//
//				case 32: {
//					JCL_message_generic aux = (JCL_message_generic) msg;
//					String dados = (String) aux.getRegisterData();
//
//					// Get Host
//					List<String> slavesIDs = slavesIDs_IoT.get(5);
//					ConcurrentMap<String, String[]> slaves = slaves_IoT.get(5);
//
//					int hostId = rand.nextInt(0, dados.hashCode(), slavesIDs.size());
//					String[] hostPort = slaves.get(slavesIDs.get(hostId));
//					String host = hostPort[0];
//					String port = hostPort[1];
//
//					JCL_connector controlConnector = new ConnectorImpl();
//					controlConnector.connect(host, Integer.parseInt(port), null);
//					JCL_message_generic mr = (JCL_message_generic) controlConnector.sendReceiveG(aux, null);
//					controlConnector.disconnect();
//
//					// Write data
//					super.WriteObjectOnSock(mr, str);
//					// End Write data
//
//					break;
//				}
//
//				case 33: {
//					JCL_message_generic aux = (JCL_message_generic) msg;
//					String dados = (String) aux.getRegisterData();
//
//					// Get Host
//					List<String> slavesIDs = slavesIDs_IoT.get(5);
//					ConcurrentMap<String, String[]> slaves = slaves_IoT.get(5);
//
//					int hostId = rand.nextInt(0, dados.hashCode(), slavesIDs.size());
//					String[] hostPort = slaves.get(slavesIDs.get(hostId));
//					String host = hostPort[0];
//					String port = hostPort[1];
//
//					JCL_connector controlConnector = new ConnectorImpl();
//					controlConnector.connect(host, Integer.parseInt(port), null);
//					JCL_message_generic mr = (JCL_message_generic) controlConnector.sendReceiveG(aux, null);
//					controlConnector.disconnect();
//
//					// Write data
//					super.WriteObjectOnSock(mr, str);
//					// End Write data
//
//					break;
//				}
//
//				case 34: {
//					JCL_message_generic aux = (JCL_message_generic) msg;
//					String dados = (String) aux.getRegisterData();
//
//					// Get Host
//					List<String> slavesIDs = slavesIDs_IoT.get(5);
//					ConcurrentMap<String, String[]> slaves = slaves_IoT.get(5);
//
//					int hostId = rand.nextInt(0, dados.hashCode(), slavesIDs.size());
//					String[] hostPort = slaves.get(slavesIDs.get(hostId));
//					String host = hostPort[0];
//					String port = hostPort[1];
//
//					JCL_connector controlConnector = new ConnectorImpl();
//					controlConnector.connect(host, Integer.parseInt(port), null);
//					JCL_message_generic mr = (JCL_message_generic) controlConnector.sendReceiveG(aux, null);
//					controlConnector.disconnect();
//
//					// Write data
//					super.WriteObjectOnSock(mr, str);
//					// End Write data
//
//					break;
//				}
//
//				case 35: {
//
//					JCL_message_list_global_var jclGV = (JCL_message_list_global_var) msg;
//					Object key = jclGV.getKeyValue().keySet().iterator().next();
//
//					// Get Host
//					List<String> slavesIDs = slavesIDs_IoT.get(5);
//					ConcurrentMap<String, String[]> slaves = slaves_IoT.get(5);
//
//					int hostId = rand.nextInt(0, key.hashCode(), slavesIDs.size());
//					String[] hostPort = slaves.get(slavesIDs.get(hostId));
//					String host = hostPort[0];
//					String port = hostPort[1];
//
//					JCL_connector globalVarConnector = new ConnectorImpl();
//					globalVarConnector.connect(host, Integer.parseInt(port), null);
//					JCL_message_result result = globalVarConnector.sendReceive(jclGV, null);
//					globalVarConnector.disconnect();
//
//					// Write data
//					super.WriteObjectOnSock(result, str);
//					// End Write data
//
//					break;
//				}
//
//				case 36: {
//					JCL_message_generic aux = (JCL_message_generic) msg;
//					Object[] dados = (Object[]) aux.getRegisterData();
//
//					// Get Host
//					List<String> slavesIDs = slavesIDs_IoT.get(5);
//					ConcurrentMap<String, String[]> slaves = slaves_IoT.get(5);
//
//					int hostId = rand.nextInt(0, dados[0].hashCode(), slavesIDs.size());
//					String[] hostPort = slaves.get(slavesIDs.get(hostId));
//					String host = hostPort[0];
//					String port = hostPort[1];
//
//					JCL_connector controlConnector = new ConnectorImpl();
//					controlConnector.connect(host, Integer.parseInt(port), null);
//					JCL_message_generic mr = (JCL_message_generic) controlConnector.sendReceiveG(aux, null);
//					controlConnector.disconnect();
//
//					// Write data
//					super.WriteObjectOnSock(mr, str);
//					// End Write data
//
//					break;
//				}
//
//				case 37: {
//
//					// instantiateGlobalVar(id) type 9
//					JCL_message_global_var jclGV = (JCL_message_global_var) msg;
//
//					// Get Host
//					List<String> slavesIDs = slavesIDs_IoT.get(5);
//					ConcurrentMap<String, String[]> slaves = slaves_IoT.get(5);
//
//					int hostId = rand.nextInt(0, jclGV.getVarKey().hashCode(), slavesIDs.size());
//					String[] hostPort = slaves.get(slavesIDs.get(hostId));
//					String host = hostPort[0];
//					String port = hostPort[1];
//
//					JCL_connector globalVarConnector = new ConnectorImpl();
//					globalVarConnector.connect(host, Integer.parseInt(port), null);
//					JCL_message_result result = globalVarConnector.sendReceive(jclGV, null);
//					globalVarConnector.disconnect();
//
//					// Write data
//					super.WriteObjectOnSock(result, str);
//					// End Write data
//
//					break;
//				}
//
//				case 38: {
//					JCL_message_generic aux = (JCL_message_generic) msg;
//					Set<implementations.util.Entry<String, Object>> setGetBinValue = (Set<implementations.util.Entry<String, Object>>) aux
//							.getRegisterData();
//					String dados = setGetBinValue.iterator().next().getKey();
//
//					// Get Host
//					List<String> slavesIDs = slavesIDs_IoT.get(5);
//					ConcurrentMap<String, String[]> slaves = slaves_IoT.get(5);
//
//					int hostId = rand.nextInt(0, dados.hashCode(), slavesIDs.size());
//					String[] hostPort = slaves.get(slavesIDs.get(hostId));
//					String host = hostPort[0];
//					String port = hostPort[1];
//
//					JCL_connector controlConnector = new ConnectorImpl();
//					controlConnector.connect(host, Integer.parseInt(port), null);
//					JCL_message_generic mr = (JCL_message_generic) controlConnector.sendReceiveG(aux, null);
//					controlConnector.disconnect();
//
//					// Write data
//					super.WriteObjectOnSock(mr, str);
//					// End Write data
//
//					break;
//				}
//
//				case 40: {
//					if (verbose)
//						System.err.println(
//								msg.getType() + " - " + "execute() - " + formatador.format(calendar.getTime()));
//
//					// Execute Task
//					JCL_message_task jclT = (JCL_message_task) msg;
//					JCL_task t = jclT.getTask();
//					// t.setTaskTime(System.nanoTime());
//
//					// t.setHost(str.getSocketAddress());
//					ConcurrentMap<String, String[]> slaves = this.slaves_IoT.get(5);
//					List<String> slavesIDs = this.slavesIDs_IoT.get(5);
//
//					String[] hostPort = RoundRobin.next(slavesIDs, slaves);
//
//					String host = hostPort[0];
//					String port = hostPort[1];
//					long ticket = 0;
//
//					JCL_connector taskConnector = new ConnectorImpl();
//					taskConnector.connect(host, Integer.parseInt(port), null);
//
//					if (jarsSlaves.get(hostPort[2] + port).contains(t.getObjectName())) {
//
//						JCL_message_result msgResult = taskConnector.sendReceive(jclT, null);
//						ticket = (Long) msgResult.getResult().getCorrectResult();
//						taskConnector.disconnect();
//
//					} else {
//
//						synchronized (jarsSlaves) {
//
//							if (jarsSlaves.get(hostPort[2] + port).contains(t.getObjectName())) {
//
//								JCL_message_result msgResult = taskConnector.sendReceive(jclT, null);
//								ticket = (Long) msgResult.getResult().getCorrectResult();
//								taskConnector.disconnect();
//
//							} else {
//
//								if (register.containsKey(t.getObjectName())) {
//									JCL_result result = taskConnector.sendReceive(register.get(t.getObjectName()), null)
//											.getResult();
//
//									if (((Boolean) result.getCorrectResult()).booleanValue()) {
//										jarsSlaves.get(hostPort[2] + port).add(t.getObjectName());
//										JCL_message_result msgResult = taskConnector.sendReceive(jclT, null);
//										ticket = (Long) msgResult.getResult().getCorrectResult();
//										taskConnector.disconnect();
//
//									}
//								} else {
//									System.out.println("CLASS NOT REGISTERED - TRY AGAIN");
//									str.putOnQueue();
//									break;
//								}
//							}
//						}
//					}
//
//					JCL_result r = new JCL_resultImpl();
//
//					long tick = numOfTasks.getAndIncrement();
//					this.taskLocation.put(tick, new Object[] { ticket, host, port });
//					r.setCorrectResult(tick);
//					JCL_message_result RESULT = new MessageResultImpl();
//					RESULT.setType(4);
//					RESULT.setResult(r);
//
//					// Write data
//					super.WriteObjectOnSock(RESULT, str);
//					// End Write data
//
//					break;
//				}
//
//				case 41: {
//					if (verbose)
//						System.err.println(
//								msg.getType() + " - " + "execute() - " + formatador.format(calendar.getTime()));
//
//					// Execute Task
//					JCL_message_task jclT = (JCL_message_task) msg;
//					JCL_task t = jclT.getTask();
//					// t.setTaskTime(System.nanoTime());
//
//					// t.setHost(str.getSocketAddress());
//					ConcurrentMap<String, String[]> slaves = this.slaves_IoT.get(5);
//					List<String> slavesIDs = this.slavesIDs_IoT.get(5);
//
//					String[] hostPort = RoundRobin.next(slavesIDs, slaves);
//
//					String host = hostPort[0];
//					String port = hostPort[1];
//					long ticket = 0;
//
//					JCL_connector taskConnector = new ConnectorImpl();
//					taskConnector.connect(host, Integer.parseInt(port), null);
//
//					if (jarsSlaves.get(hostPort[2] + port).contains(t.getObjectName())) {
//
//						JCL_message_result msgResult = taskConnector.sendReceive(jclT, null);
//						ticket = (Long) msgResult.getResult().getCorrectResult();
//						taskConnector.disconnect();
//
//					} else {
//
//						synchronized (jarsSlaves) {
//
//							if (jarsSlaves.get(hostPort[2] + port).contains(t.getObjectName())) {
//
//								JCL_message_result msgResult = taskConnector.sendReceive(jclT, null);
//								ticket = (Long) msgResult.getResult().getCorrectResult();
//								taskConnector.disconnect();
//
//							} else {
//								if (register.containsKey(t.getObjectName())) {
//									JCL_result result = taskConnector.sendReceive(register.get(t.getObjectName()), null)
//											.getResult();
//
//									if (((Boolean) result.getCorrectResult()).booleanValue()) {
//										jarsSlaves.get(hostPort[2] + port).add(t.getObjectName());
//										JCL_message_result msgResult = taskConnector.sendReceive(jclT, null);
//										ticket = (Long) msgResult.getResult().getCorrectResult();
//										taskConnector.disconnect();
//
//									}
//								} else {
//									System.out.println("CLASS NOT REGISTERED - TRY AGAIN");
//									str.putOnQueue();
//									break;
//								}
//							}
//						}
//					}
//
//					JCL_result r = new JCL_resultImpl();
//
//					long tick = numOfTasks.getAndIncrement();
//					this.taskLocation.put(tick, new Object[] { ticket, host, port });
//					r.setCorrectResult(tick);
//					JCL_message_result RESULT = new MessageResultImpl();
//					RESULT.setType(5);
//					RESULT.setResult(r);
//
//					// Write data
//					super.WriteObjectOnSock(RESULT, str);
//					// End Write data
//
//					break;
//				}
//
//				case -1: {
//					// JCL_message_control aux = (JCL_message_control) msg;
//					JCL_message_metadata aux = (JCL_message_metadata) msg;
//
//					if (aux.getMetadados().size() == 5) {
//						synchronized (slaves_IoT) {
//
//							String address = aux.getMetadados().get("IP");
//							String port = aux.getMetadados().get("PORT");
//							String slaveName = aux.getMetadados().get("MAC");
//							String cores = aux.getMetadados().get("CORE(S)");
//							Integer device = Integer.valueOf(aux.getMetadados().get("DEVICE_TYPE"));
//
//							// String address = aux.getRegisterData()[0];
//							// String port = aux.getRegisterData()[1];
//							// String slaveName = aux.getRegisterData()[2];
//							// String cores = aux.getRegisterData()[3];
//
//							// ConcurrentMap<String, String[]> jarsName;
//							ConcurrentMap<String, String[]> slaves;
//							List<String> slavesIDs;
//
//							if (slaves_IoT.containsKey(device)) {
//								slaves = this.slaves_IoT.get(device);
//								slavesIDs = this.slavesIDs_IoT.get(device);
//								// jarsName = this.jarsName_IoT.get(device);
//							} else {
//								// jarsName = new ConcurrentHashMap<String,
//								// String[]>();
//								slaves = new ConcurrentHashMap<String, String[]>();
//								slavesIDs = new LinkedList<String>();
//
//								this.slaves_IoT.put(device, slaves);
//								// this.jarsName_IoT.put(device,jarsName);
//								this.slavesIDs_IoT.put(device, slavesIDs);
//							}
//
//							if (slaves.containsKey(slaveName + port)) {
//
//								JCL_message_get_host mc = new MessageGetHostImpl();
//								mc.setType(-4);
//								mc.setSlaves(null);
//								mc.setSlavesIDs(null);
//
//								// Write data
//								super.WriteObjectOnSock(mc, str);
//								// End Write data
//
//							} else {
//
//								String[] hostPortId = { address, port, slaveName, cores };
//								List<JCL_connector> conecList = new ArrayList<JCL_connector>();
//								JCL_message_control mgc = new MessageControlImpl();
//								mgc.setType(-3);
//								mgc.setRegisterData(hostPortId);
//
//								// for(String[] hostPort:runningUser.values()){
//								// JCL_connector mgcConnector = new
//								// ConnectorImpl();
//								// if (mgcConnector.connect(hostPort[0],
//								// Integer.parseInt(hostPort[1]))){
//								// if(mgcConnector.send(mgc)){
//								// System.out.println("Lock
//								// Client(Target:"+Arrays.toString(hostPort)+"
//								// Host add:"+Arrays.toString(hostPortId)+").");
//								// }
//								// conecList.add(mgcConnector);
//								// }else{
//								// System.err.println("Problem: Cannot Connect
//								// to
//								// Client(Target:"+Arrays.toString(hostPort));
//								// }
//								// }
//
//								for (String hostName : slavesIDs) {
//									String[] hostPort = slaves.get(hostName);
//									JCL_connector mgcConnector = new ConnectorImpl();
//									mgcConnector.connect(hostPort[0], Integer.parseInt(hostPort[1]), null);
//									if (mgcConnector.send(mgc, null)) {
//										System.out.println("Consisting cluster(Target:" + Arrays.toString(hostPort)
//												+ " Host add:" + Arrays.toString(hostPortId) + ").");
//									}
//									conecList.add(mgcConnector);
//								}
//
//								for (JCL_connector e : conecList) {
//									JCL_message_result resp = (JCL_message_result) e.receive();
//									if (!(Boolean) resp.getResult().getCorrectResult()) {
//										System.out.println("Problem in Consist cluster.");
//									}
//								}
//
//								conecList.clear();
//								// for(String[] hostPort:runningUser.values()){
//								// JCL_connector mgcConnector = new
//								// ConnectorImpl();
//								// if (mgcConnector.connect(hostPort[0],
//								// Integer.parseInt(hostPort[1]))){
//								// JCL_message msgU = new MessageImpl();
//								// msgU.setType(-2);
//								// if(mgcConnector.send(msgU)){
//								// System.out.println("Try UnLock
//								// Client(Target:"+Arrays.toString(hostPort)+"
//								// Host add:"+Arrays.toString(hostPortId)+").");
//								// }
//								// conecList.add(mgcConnector);
//								// }else{
//								// System.err.println("Problem: Cannot Connect
//								// to
//								// Client(Target:"+Arrays.toString(hostPort));
//								// }
//								// }
//
//								for (JCL_connector e : conecList) {
//									JCL_message_result resp = (JCL_message_result) e.receive();
//									if (!(Boolean) resp.getResult().getCorrectResult()) {
//										System.out.println("Problem in UnLock client.");
//									}
//								}
//
//								JCL_message_get_host mc = new MessageGetHostImpl();
//
//								mc.setType(-4);
//								mc.setSlaves(slaves);
//								mc.setSlavesIDs(slavesIDs);
//
//								slaves.put((slaveName + port), hostPortId);
//								slavesIDs.add(slaveName + port);
//								jarsSlaves.put((slaveName + port), new ArrayList<String>());
//
//
//								Integer newCore = (Integer.parseInt(metaData.get("CORE(S)"))+Integer.parseInt(cores));
//					    		metaData.put("CORE(S)",newCore.toString());
//					    		JCL_message_metadata msgToS = new MessageMetadataImpl();
//					    		msgToS.setType(39);
//					    		msgToS.setMetadados(metaData);
//
//					            routerLink.send(msgToS);
//
//					  //  		routerLink.send(str.getInput(),(byte)-7, null, macConvert(metaData.get("MAC")));
//
//
//								// Write data
//								super.WriteObjectOnSock(mc, str);
//								// End Write data
//
//								System.err.println("JCL HOST " + slaveName + " registered!");
//							}
//						}
//					} else {
//
//						// String[] empty = {};
//						// JCL_message_control mc = new MessageControlImpl();
//						// mc.setType(-1);
//						// mc.setRegisterData(empty);
//
//						JCL_message_get_host mc = new MessageGetHostImpl();
//						mc.setType(-4);
//						mc.setSlaves(null);
//						mc.setSlavesIDs(null);
//
//						// Write data
//						super.WriteObjectOnSock(mc, str);
//						// End Write data
//					}
//
//					break;
//				}
//
//				case -2: {
//					JCL_message_control aux = (JCL_message_control) msg;
//
//					if (aux.getRegisterData().length == 5) {
//						synchronized (slaves_IoT) {
//							String address = aux.getRegisterData()[0];
//							String port = aux.getRegisterData()[1];
//							String slaveName = aux.getRegisterData()[2];
//							// Integer devide =
//
//							ConcurrentMap<String, String[]> slaves = this.slaves_IoT.get(5);
//							List<String> slavesIDs = this.slavesIDs_IoT.get(5);
//							// ConcurrentMap<String, String[]> jarsName =
//							// this.jarsName_IoT.get(5);
//
//							if (slaves.containsKey(slaveName + port)) {
//								Iterator<Entry<Object, String[]>> iterator = globalVarSlaves.entrySet().iterator();
//								while (iterator.hasNext()) {
//									Entry<Object, String[]> entry = iterator.next();
//									if (entry.getValue()[0].equals(address)) {
//										iterator.remove();
//									}
//								}
//								slaves.remove(slaveName + port);
//								slavesIDs.remove(slaveName + port);
//								jarsSlaves.remove(slaveName + port);
//								String[] empty = { "unregistered" };
//								JCL_message_control mc = new MessageControlImpl();
//								mc.setRegisterData(empty);
//								// Write data
//								super.WriteObjectOnSock(mc, str);
//								// End Write data
//
//								System.err.println("JCL HOST " + slaveName + " unregistered!");
//							} else {
//								String[] empty = {};
//								JCL_message_control mc = new MessageControlImpl();
//								mc.setRegisterData(empty);
//								// Write data
//								super.WriteObjectOnSock(mc, str);
//								// End Write data
//							}
//						}
//					} else {
//						String[] empty = {};
//						JCL_message_control mc = new MessageControlImpl();
//						mc.setType(-1);
//						mc.setRegisterData(empty);
//						// Write data
//						super.WriteObjectOnSock(mc, str);
//						// End Write data
//					}
//
//					break;
//				}
//
//				case -3: {
//
//					// Consisting Host
//					JCL_message_control jclC = (JCL_message_control) msg;
//					String[] hostPortId = jclC.getRegisterData();
//
//					System.out.println("Consisting cluster!!!");
//					System.out.println("Host add: " + Arrays.toString(hostPortId));
//
//					JCL_result jclR = new JCL_resultImpl();
//					jclR.setCorrectResult(new Boolean(true));
//					JCL_message_result RESULT = new MessageResultImpl();
//					RESULT.setType(-3);
//					RESULT.setResult(jclR);
//
//					//Send answer
//					super.WriteObjectOnSock(RESULT, str);
//					//End answer
//
//				break;
//				}
//
//				case -4: {
//
//					JCL_message_get_host msgr = (JCL_message_get_host) msg;
//
//					if ((msgr.getSlaves() != null)) {
//						System.out.println("SUPER PEER JCL is OK");
//					} else {
//						System.err.println("SUPER PEER JCL NOT STARTED");
//					}
//
//					break;
//				}
//
//				case -5: {
//
//					JCL_message_control msgr = (JCL_message_control) msg;
//
//					if (msgr.getRegisterData().length == 1) {
//						System.out.println("SUPER PEER JCL WAS UNREGISTERED!");
//					} else {
//						System.err.println("SUPER PEER JCL WAS NOT UNREGISTERED!");
//					}
//					break;
//				}
//
//				case -7: {
//
//					JCL_message_bool msgr = (JCL_message_bool) msg;
//
//					if (msgr.getRegisterData()[0]) {
//						System.out.println("UPDATE CORE OK!");
//					} else {
//						System.err.println("PROBLEM IN UPDATE CORE!");
//					}
//					break;
//				}
//				}
//			}
//
//		} catch (Exception e) {
//			e.printStackTrace();
//
//		}
	}
	
	public byte[] macConvert(String macAddress){
		String[] macAddressParts = macAddress.split("-");
		byte[] macAddressBytes = new byte[6];

		if (macAddressParts.length == 6){
		// convert hex string to byte values
			for(int i=0; i<6; i++){
				Integer hex = Integer.parseInt(macAddressParts[i], 16);
				macAddressBytes[i] = hex.byteValue();
			}
		
		}else{
			String[] ipAddressParts = macAddress.split("\\.");
			for(int i=0; i<4; i++){
			    Integer integer = Integer.parseInt(ipAddressParts[i]);
			    macAddressBytes[i] = integer.byteValue();
			}
			Integer integer = 0;
			macAddressBytes[4] =  integer.byteValue();
			macAddressBytes[5] =  integer.byteValue();
		}		
			return macAddressBytes;
	}
}
