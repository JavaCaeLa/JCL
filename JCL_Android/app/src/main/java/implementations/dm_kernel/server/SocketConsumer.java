package implementations.dm_kernel.server;

import implementations.collections.JCLHashMap;
import implementations.dm_kernel.ConnectorImpl;
import implementations.dm_kernel.MessageBoolImpl;
import implementations.dm_kernel.MessageCommonsImpl;
import implementations.dm_kernel.MessageControlImpl;
import implementations.dm_kernel.MessageGenericImpl;
import implementations.dm_kernel.MessageGetHostImpl;
import implementations.dm_kernel.MessageImpl;
import implementations.dm_kernel.MessageLongImpl;
import implementations.dm_kernel.MessageMetadataImpl;
import implementations.dm_kernel.MessageRegisterImpl;
import implementations.dm_kernel.MessageResultImpl;
import implementations.dm_kernel.MessageSensorImpl;
import interfaces.kernel.JCL_Sensor;
import interfaces.kernel.JCL_connector;
import interfaces.kernel.JCL_message;
import interfaces.kernel.JCL_message_bool;
import interfaces.kernel.JCL_message_commons;
import interfaces.kernel.JCL_message_control;
import interfaces.kernel.JCL_message_generic;
import interfaces.kernel.JCL_message_get_host;
import interfaces.kernel.JCL_message_long;
import interfaces.kernel.JCL_message_metadata;
import interfaces.kernel.JCL_message_register;
import interfaces.kernel.JCL_message_result;
import interfaces.kernel.JCL_message_sensor;
import interfaces.kernel.JCL_result;

import java.net.InetAddress;
import java.net.SocketAddress;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicBoolean;

import commom.GenericConsumer;
import commom.GenericResource;
import commom.JCL_SensorImpl;
import commom.JCL_resultImpl;
import commom.JCL_handler;

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
	9 public Object instantiateGlobalVar(String nickName, String varName, File[] jars, Object[] defaultVarValue) {
	10 public boolean instantiateGlobalVar(String varName, Object instance) {
	11 public boolean destroyGlobalVar(String varName) {
	12 public boolean setValue(String varName, Object value) {
	13 public boolean setValueUnlocking(String varName, Object value) {
	14 public JCL_result getValue(String varName) {
	15 public JCL_result getValueLocking(String varName) {
	16 public void destroy() {
	17 public boolean containsGlobalVar(String ninckName){
	18 public boolean containsTask(String nickName){
	19 public List<String> getHosts() {
	20 public boolean isLock(String nickName){ 
	21 public boolean instantiateGlobalVarOnHost(String varName, Object instance) {
	21 public Object instantiateGlobalVarOnHost(String nickName, String varName, File[] jars, Object[] defaultVarValue) {
	22 public boolean cleanEnvironment() { 
	23 public boolean registerOnHost() {
		
	METHOD DEPRECATED in JCL distributed version: public boolean register(Class<?> object, String nickName) {
	
	-1 slave register
	-2 slave unregister

 */

public class SocketConsumer<S extends JCL_handler> extends GenericConsumer<S>{
	
	private ConcurrentMap<Integer, ConcurrentMap<String, String[]>> slaves_IoT,jarsName_IoT;
	private ConcurrentMap<Integer,ConcurrentMap<String,Map<String,String>>> metadata_IoT;
	//	private ConcurrentMap<String,String[]> slaves,jarsName;
	private ConcurrentMap<Object,String[]> globalVarSlaves;
	private ConcurrentMap<String,String[]> runningUser;
	private ConcurrentMap<String,List<String>> jarsSlaves;	
	private ConcurrentMap<String, byte[][]> jars;
//	private List<String> slavesIDs;
	private ConcurrentMap<Integer, List<String>> slavesIDs_IoT;
	private boolean verbose;

//	public SocketConsumer(GenericResource<S> re, AtomicBoolean kill, ConcurrentMap<Object, String[]>globalVarSlaves, List<String> slavesIDs, ConcurrentMap<String, String[]> slaves, ConcurrentMap<String, List<String>> jarsSlaves, ConcurrentMap<String, String[]> jarsName, ConcurrentMap<String, byte[][]> jars, boolean verbose,ConcurrentMap<String,String[]> runningUser) {
	public SocketConsumer(GenericResource<S> re, AtomicBoolean kill, ConcurrentMap<Object, String[]> globalVarSlaves, ConcurrentMap<Integer,List<String>> slavesIDs_IoT, ConcurrentMap<Integer,ConcurrentMap<String, String[]>> slaves_IoT, ConcurrentMap<String, List<String>> jarsSlaves, ConcurrentMap<Integer,ConcurrentMap<String, String[]>> jarsName_IoT, ConcurrentMap<String, byte[][]> jars, boolean verbose,ConcurrentMap<String,String[]> runningUser,ConcurrentMap<Integer,ConcurrentMap<String,Map<String,String>>> metadata_IoT) {
		
		super(re,kill);	
		this.globalVarSlaves = globalVarSlaves;
		this.slavesIDs_IoT = slavesIDs_IoT;
		this.slaves_IoT = slaves_IoT;
		this.verbose = verbose;
		this.jarsSlaves = jarsSlaves;
		this.jarsName_IoT = jarsName_IoT;
		this.jars = jars;
		this.runningUser = runningUser;
		this.metadata_IoT = metadata_IoT;
	}

	@Override
	protected void doSomething(S str) {
		try{				 
			
			// JCL_message msg = (JCL_message)super.ReadObjectFromSock(str.getKey(), str.getInput());
			 JCL_message msg = str.getMsg();
			 
			//Get local time
			Locale locale = new Locale("pt","BR"); 
			GregorianCalendar calendar = new GregorianCalendar(); 
			SimpleDateFormat formatador = new SimpleDateFormat("dd' de 'MMMMM' de 'yyyy' - 'HH':'mm'h'",locale); 
			switch (msg.getType()){
			
			case 1:{						
				if (verbose) System.err.println(msg.getType()+" - "+"register() - "+formatador.format(calendar.getTime()));				
				JCL_message_register msgR = (JCL_message_register) msg;
				if (!jars.containsKey(msgR.getClassName())){
					jars.put(msgR.getClassName(), msgR.getJars());
					ConcurrentMap<String, String[]> jarsName = this.jarsName_IoT.get(5);
					jarsName.put(msgR.getClassName(), msgR.getJarsNames());
					JCL_result r = new JCL_resultImpl();
					r.setCorrectResult(Boolean.TRUE);					
					JCL_message_result RESULT = new MessageResultImpl();
					RESULT.setType(1);
					RESULT.setResult(r);
					
					//Write data
					//super.WriteObjectOnSock(RESULT, str);
					//End Write data
										
				}else{
					JCL_result r = new JCL_resultImpl();
					r.setCorrectResult(Boolean.FALSE);					
					JCL_message_result RESULT = new MessageResultImpl();
					RESULT.setType(1);
					RESULT.setResult(r);

					//Write data
					//super.WriteObjectOnSock(RESULT, str);
					//End Write data
				}
		
				break;
			}
			case 2:{				
				if (verbose) System.err.println(msg.getType()+" - "+"unRegister() - "+formatador.format(calendar.getTime()));
				JCL_message_control msgR = (JCL_message_control) msg;
				JCL_message_control mc = new MessageControlImpl();
				boolean ok = true;
				
				ConcurrentMap<String, String[]> slaves = this.slaves_IoT.get(5);
				
				for(String[] oneHostPort: slaves.values()){
					if (jarsSlaves.get(oneHostPort[2]).contains(msgR.getRegisterData()[0])){
					JCL_connector taskConnector = new ConnectorImpl();
					taskConnector.connect(oneHostPort[0], Integer.parseInt(oneHostPort[1]),oneHostPort[2]);
					JCL_message_commons msgUn = new MessageCommonsImpl();
					msgUn.setType(2);
					msgUn.setRegisterData(msgR.getRegisterData()[0]);
					JCL_message_result msgRes = taskConnector.sendReceive(msgUn,null);
					if (msgRes.getResult().getCorrectResult() != null){
						jarsSlaves.get(oneHostPort[2]).remove(msgR.getRegisterData()[0]);
					}
					else{
						msgRes.getResult().getErrorResult().printStackTrace();
						ok = false;
					}
					taskConnector.disconnect();
					
					}
				}
				jars.remove(msgR.getRegisterData()[0]);
				ConcurrentMap<String, String[]> jarsName = this.jarsName_IoT.get(5);
				jarsName.remove(msgR.getRegisterData()[0]);
				mc.setRegisterData(Boolean.toString(ok));

				//Write data
				//super.WriteObjectOnSock(mc, str);
				//End Write data
				
				break;
			}
			
//			case 4:{
//				if (verbose) System.err.println(msg.getType()+" - "+"execute() - "+formatador.format(calendar.getTime()));	
//				JCL_message_control msgR = (JCL_message_control) msg;
//				JCL_message_control mc = new MessageControlImpl();
//				
//				ConcurrentMap<String, String[]> slaves = this.slaves_IoT.get(5);
//				List<String> slavesIDs = this.slavesIDs_IoT.get(5);
//
//				String[] hostPort =RoundRobin.next(slavesIDs, slaves);
//				
//				if(jarsSlaves.get(hostPort[2]).contains(msgR.getRegisterData()[0])){
//					mc.setRegisterData(hostPort);
//					
//					//Write data
//					super.WriteObjectOnSock(mc, str);
//					//End Write data
//
//				}else{
//					synchronized(jarsSlaves){
//						if(jarsSlaves.get(hostPort[2]).contains(msgR.getRegisterData()[0])){
//							mc.setRegisterData(hostPort);
//							
//							//Write data
//							super.WriteObjectOnSock(mc, str);
//							//End Write data	
//						}else{
//						// we must register before submit a task
//						MessageRegisterImpl msgRe = new MessageRegisterImpl();
//						msgRe.setJars(jars.get(msgR.getRegisterData()[0]));
//						ConcurrentMap<String, String[]> jarsName = this.jarsName_IoT.get(5);
//						msgRe.setJarsNames(jarsName.get(msgR.getRegisterData()[0]));
//						msgRe.setClassName(msgR.getRegisterData()[0]);
//						// type 1 for registering
//						msgRe.setType(1);
//
//						JCL_connector taskConnector = new ConnectorImpl();
//						taskConnector.connect(hostPort[0], Integer.parseInt(hostPort[1]));
//						JCL_result result = taskConnector.sendReceive(msgRe).getResult();
//						taskConnector.disconnect();
//						boolean flag = false;
//						if (result.getCorrectResult() != null) {
//							flag = ((Boolean) result.getCorrectResult()).booleanValue();
//							if (flag) {
//								jarsSlaves.get(hostPort[2]).add(msgR.getRegisterData()[0]);
//								mc.setRegisterData(hostPort);
//								
//								//Write data
//								super.WriteObjectOnSock(mc, str);
//								//End Write data
//								
//							}else{
//
//								//Write data
//								super.WriteObjectOnSock(mc, str);
//								//End Write data
//
//								System.err.println("cannot register class("+msgR.getRegisterData()[0]+")");
//								}
//							}
//					}	
//					}
//				}

//				break;
//			}
			
//			case 5:{
//				if (verbose) System.err.println(msg.getType()+" - "+"execute() - "+formatador.format(calendar.getTime()));
//				JCL_message_control msgR = (JCL_message_control) msg;
//				JCL_message_control mc = new MessageControlImpl();
//				
//				ConcurrentMap<String, String[]> slaves = this.slaves_IoT.get(5);
//				List<String> slavesIDs = this.slavesIDs_IoT.get(5);
//
//				String[] hostPort =RoundRobin.next(slavesIDs, slaves);
//				synchronized(jarsSlaves){
//				if(jarsSlaves.get(hostPort[2]).contains(msgR.getRegisterData()[0])){
//					mc.setRegisterData(hostPort);
//
//					//Write data
//					super.WriteObjectOnSock(mc, str);
//					//End Write data
//					
//				}else{
//					
//						// we must register before submit a task
//						MessageRegisterImpl msgRe = new MessageRegisterImpl();
//						msgRe.setJars(jars.get(msgR.getRegisterData()[0]));
//						ConcurrentMap<String, String[]> jarsName = this.jarsName_IoT.get(5);
//						msgRe.setJarsNames(jarsName.get(msgR.getRegisterData()[0]));
//						msgRe.setClassName(msgR.getRegisterData()[0]);
//						// type 1 for registering
//						msgRe.setType(1);
//
//						JCL_connector taskConnector = new ConnectorImpl();
//						taskConnector.connect(hostPort[0], Integer.parseInt(hostPort[1]));
//						JCL_result result = taskConnector.sendReceive(msgRe).getResult();
//						taskConnector.disconnect();
//						boolean flag = false;
//						if (result.getCorrectResult() != null) {
//							flag = ((Boolean) result.getCorrectResult()).booleanValue();
//							if (flag) {
//								jarsSlaves.get(hostPort[2]).add(msgR.getRegisterData()[0]);
//								mc.setRegisterData(hostPort);
//							
//								//Write data
//								super.WriteObjectOnSock(mc, str);
//								//End Write data
//								
//							}else{
//								//Write data
//								super.WriteObjectOnSock(mc, str);
//								//End Write data
//								
//								System.err.println("cannot register class("+msgR.getRegisterData()[0]+")");
//								}
//							}
//						}
//				}
//				// in.close();
//				break;
//			}
			
			case 9:{
				if (verbose) System.err.println(msg.getType()+" - "+"instantiateGlobalVar() - "+formatador.format(calendar.getTime()));				
				synchronized (globalVarSlaves) {
					JCL_message_control aux = (JCL_message_control) msg;
					if(globalVarSlaves.containsKey(aux.getRegisterData()[0])){
						JCL_message_control mc = new MessageControlImpl();
						String[] hostPort = {};
						mc.setRegisterData(hostPort);
						//Write data
						//super.WriteObjectOnSock(mc, str);
						//End Write data
						
					}else{
						ConcurrentMap<String, String[]> slaves = this.slaves_IoT.get(5);
						List<String> slavesIDs = this.slavesIDs_IoT.get(5);

						String[] hostPort = RoundRobin.nextGV(slavesIDs, slaves);
						globalVarSlaves.put(aux.getRegisterData()[0],hostPort);
						JCL_message_control mc = new MessageControlImpl();
						mc.setRegisterData(hostPort);
						//Write data
						//super.WriteObjectOnSock(mc, str);
						//End Write data
					}
				}	
				break;
			}
			case 10:{
				if (verbose) System.err.println(msg.getType()+" - "+"instantiateGlobalVar() - "+formatador.format(calendar.getTime()));				
				synchronized (globalVarSlaves) {
					JCL_message_control aux = (JCL_message_control) msg;
					if(globalVarSlaves.containsKey(aux.getRegisterData()[0])){
						JCL_message_control mc = new MessageControlImpl();
						String[] hostPort = {};
						mc.setRegisterData(hostPort);
						//Write data
						//super.WriteObjectOnSock(mc, str);
						//End Write data
						
					}else{
						ConcurrentMap<String, String[]> slaves = this.slaves_IoT.get(5);
						List<String> slavesIDs = this.slavesIDs_IoT.get(5);

						String[] hostPort = RoundRobin.nextGV(slavesIDs, slaves);
						globalVarSlaves.put(aux.getRegisterData()[0],hostPort);
						JCL_message_control mc = new MessageControlImpl();
						mc.setRegisterData(hostPort);
						//Write data
						//super.WriteObjectOnSock(mc, str);
						//End Write data
					}
				}
				break;
			}
			case 11:{
				if (verbose) System.err.println(msg.getType()+" - "+"destroyGlobalVar() - "+formatador.format(calendar.getTime()));				
				synchronized (globalVarSlaves) {
					JCL_message_generic aux = (JCL_message_generic) msg;
					if(globalVarSlaves.containsKey(aux.getRegisterData())){
						
						JCL_result jclR = new JCL_resultImpl();
						String[] hostPort = globalVarSlaves.remove(aux.getRegisterData());
						jclR.setCorrectResult(hostPort);
						JCL_message_result mc = new MessageResultImpl();
						mc.setType(11);
						mc.setResult(jclR);
						
						//Write data
						//super.WriteObjectOnSock(mc, str);
						//End Write data
						
					}else{
						JCL_result jclR = new JCL_resultImpl();
						String[] hostPort ={};
						jclR.setCorrectResult(hostPort);
						JCL_message_result mc = new MessageResultImpl();
						mc.setType(11);
						mc.setResult(jclR);
						
						//Write data
						//super.WriteObjectOnSock(mc, str);
						//End Write data						
					}
				}	
				break;
			}
			case 12:{
				if (verbose) System.err.println(msg.getType()+" - "+"setValue() - "+formatador.format(calendar.getTime()));				
				JCL_message_control aux = (JCL_message_control) msg;
				if(globalVarSlaves.containsKey(aux.getRegisterData()[0])){
					JCL_message_control mc = new MessageControlImpl();
					String[] hostPort = globalVarSlaves.get(aux.getRegisterData()[0]);
					mc.setRegisterData(hostPort);
					//Write data
					//super.WriteObjectOnSock(mc, str);
					//End Write data
					
				}else{
					String[] hostPort = {};
					JCL_message_control mc = new MessageControlImpl();
					mc.setRegisterData(hostPort);
					//Write data
					//super.WriteObjectOnSock(mc, str);
					//End Write data
				}				
				break;
			}
			case 13:{
				if (verbose) System.err.println(msg.getType()+" - "+"setValueUnlocking() - "+formatador.format(calendar.getTime()));				
				JCL_message_control aux = (JCL_message_control) msg;
				if(globalVarSlaves.containsKey(aux.getRegisterData()[0])){
					JCL_message_control mc = new MessageControlImpl();
					String[] hostPort = globalVarSlaves.get(aux.getRegisterData()[0]);
					mc.setRegisterData(hostPort);
					//Write data
					//super.WriteObjectOnSock(mc, str);
					//End Write data
					
				}else{
					String[] hostPort = {};
					JCL_message_control mc = new MessageControlImpl();
					mc.setRegisterData(hostPort);
					//Write data
					//super.WriteObjectOnSock(mc, str);
					//End Write data							
				}
				break;
			}
			case 14:{	
				if (verbose) System.err.println(msg.getType()+" - "+"getValue() - "+formatador.format(calendar.getTime()));				
				JCL_message_generic aux = (JCL_message_generic) msg;
				if(globalVarSlaves.containsKey(aux.getRegisterData())){
					
					JCL_result jclR = new JCL_resultImpl();
					jclR.setCorrectResult(globalVarSlaves.get(aux.getRegisterData()));
					JCL_message_result mc = new MessageResultImpl();
					mc.setType(14);
					mc.setResult(jclR);
					
					//Write data
					//super.WriteObjectOnSock(mc, str);
					//End Write data					
				}else{
					String[] hostPort = {};
					JCL_result jclR = new JCL_resultImpl();
					jclR.setCorrectResult(hostPort);
					JCL_message_result mc = new MessageResultImpl();
					mc.setType(14);
					mc.setResult(jclR);

					
					//Write data
					//super.WriteObjectOnSock(mc, str);
					//End Write data
				}
				break;
			}
			case 15:{	
				if (verbose) System.err.println(msg.getType()+" - "+"getValueLocking() - "+formatador.format(calendar.getTime()));				
				JCL_message_generic aux = (JCL_message_generic) msg;
				if(globalVarSlaves.containsKey(aux.getRegisterData())){
					
					JCL_result jclR = new JCL_resultImpl();
					jclR.setCorrectResult(globalVarSlaves.get(aux.getRegisterData()));
					JCL_message_result mc = new MessageResultImpl();
					mc.setType(14);
					mc.setResult(jclR);
					
					//Write data
					//super.WriteObjectOnSock(mc, str);
					//End Write data					
				}else{
					String[] hostPort = {};
					JCL_result jclR = new JCL_resultImpl();
					jclR.setCorrectResult(hostPort);
					JCL_message_result mc = new MessageResultImpl();
					mc.setType(14);
					mc.setResult(jclR);

					
					//Write data
					//super.WriteObjectOnSock(mc, str);
					//End Write data
				}
				break;
			}
			case 16:{	
				//devemos parar todos os slaves antes...
				if (verbose) System.err.println(msg.getType()+" - "+"destroy() - "+formatador.format(calendar.getTime()));				
				JCL_message_control mc = new MessageControlImpl();
				
				ConcurrentMap<String, String[]> slaves = this.slaves_IoT.get(5);

				String[] hostPort =new String[slaves.size()*2];
				int i=0;
				
				for(String[] oneHostPort: slaves.values()){
					hostPort[i] = oneHostPort[0];
					hostPort[i+1] = oneHostPort[1];
					i+=2;
				}
				
				mc.setRegisterData(hostPort);
				//Write data
				//super.WriteObjectOnSock(mc, str);
				//End Write data

				jars.clear();
				ConcurrentMap<String, String[]> jarsName = this.jarsName_IoT.get(5);
				jarsName.clear();
				re.stopServer();
				re.setFinished();
				break;
			}
			
			case 17:{		
				if (verbose) System.err.println(msg.getType()+" - "+"containsGlobalVar() - "+formatador.format(calendar.getTime()));				
				JCL_message_generic aux = (JCL_message_generic) msg;
				JCL_message_generic mc = new MessageGenericImpl();
				
				if(globalVarSlaves.containsKey(aux.getRegisterData())){
					mc.setRegisterData(new Boolean(true));
					}
				else {mc.setRegisterData(new Boolean(false));}				
				//Write data
				//super.WriteObjectOnSock(mc, str);
				//End Write data

				break;
			}
			
			case 18:{		
				if (verbose) System.err.println(msg.getType()+" - "+"containsTask() - "+formatador.format(calendar.getTime()));				
				JCL_message_control aux = (JCL_message_control) msg;
				JCL_message_control mc = new MessageControlImpl();
				ConcurrentMap<String, String[]> jarsName = this.jarsName_IoT.get(5);
				if((jars.containsKey(aux.getRegisterData()[0])) && (jarsName.containsKey(aux.getRegisterData()[0]))){
					mc.setRegisterData("true");
					} else {mc.setRegisterData("false");}
				
				//Write data
				//super.WriteObjectOnSock(mc, str);
				//End Write data
				
				
				break;
			}
			
			case 19:{	
				if (verbose) System.err.println(msg.getType()+" - "+"getHosts() - "+formatador.format(calendar.getTime()));				
				JCL_result jclR = new JCL_resultImpl();
				List<String> result = new ArrayList<String>();
				ConcurrentMap<String, String[]> slaves = this.slaves_IoT.get(5);
				List<String> slavesIDs = this.slavesIDs_IoT.get(5);

					for(int i = 0;i < slavesIDs.size();i++){
						result.add(slavesIDs.get(i)+":"+slaves.get(slavesIDs.get(i))[0]+":"+slaves.get(slavesIDs.get(i))[1]);
					}
				jclR.setCorrectResult(result);
				JCL_message_result RESULT = new MessageResultImpl();
				RESULT.setType(19);
				RESULT.setResult(jclR);
				
				//Write data
				//super.WriteObjectOnSock(RESULT, str);
				//End Write data
				
				break;
			}
			
			case 20:{	
				if (verbose) System.err.println(msg.getType()+" - "+"isLock() - "+formatador.format(calendar.getTime()));				
				JCL_message_control aux = (JCL_message_control) msg;
				if(globalVarSlaves.containsKey(aux.getRegisterData()[0])){
					JCL_message_control mc = new MessageControlImpl();
					String[] hostPort = globalVarSlaves.get(aux.getRegisterData()[0]);
					mc.setRegisterData(hostPort);
					//Write data
					//super.WriteObjectOnSock(mc, str);
					//End Write data
					
				}else{
					String[] hostPort = {};
					JCL_message_control mc = new MessageControlImpl();
					mc.setRegisterData(hostPort);
					//Write data
					//super.WriteObjectOnSock(mc, str);
					//End Write data
				}				
				break;				
			}
			case 21:{	
				if (verbose) System.err.println(msg.getType()+" - "+"instantiateGlobalVarOnHost() - "+formatador.format(calendar.getTime()));				
				synchronized (globalVarSlaves) {
					JCL_message_generic aux = (JCL_message_generic) msg;
					Object[] obj = (Object[]) aux.getRegisterData();
					String[] hostPort = {(String) obj[1],(String) obj[2],(String) obj[3]};
					int i = 0;
					boolean contain = true;
					ConcurrentMap<String, String[]> slaves = this.slaves_IoT.get(5);
					List<String> slavesIDs = this.slavesIDs_IoT.get(5);

					while((i<slavesIDs.size())&& (contain)){
						if (slaves.get(slavesIDs.get(i))[0].equals(obj[1])) contain = false;
						i++;
					}
					
					if((globalVarSlaves.containsKey(obj[0])) || (contain)){
						JCL_message_control mc = new MessageControlImpl();
						mc.setRegisterData("false");
						//Write data
					//	super.WriteObjectOnSock(mc, str);
						//End Write data
						
					}else{
						globalVarSlaves.put(obj[0],hostPort);
						JCL_message_control mc = new MessageControlImpl();
						mc.setRegisterData("true");
						//Write data
					//	super.WriteObjectOnSock(mc, str);
						//End Write data
					}
				}	
				break;
			}	
			case 22:{
				if (verbose) System.err.println(msg.getType()+" - "+"cleanEnvironment() - "+formatador.format(calendar.getTime()));				
				JCL_result jclR = new JCL_resultImpl();
				List<String[]> result = new ArrayList<String[]>();
				ConcurrentMap<String, String[]> slaves = this.slaves_IoT.get(5);
				List<String> slavesIDs = this.slavesIDs_IoT.get(5);

					for(int i = 0;i < slavesIDs.size();i++){
						result.add(new String[]{slaves.get(slavesIDs.get(i))[0],slaves.get(slavesIDs.get(i))[1]});
						List<String> jS = jarsSlaves.get(slavesIDs.get(i));
						if (jS != null){
							jS.clear();
						}					
					}
				jclR.setCorrectResult(result);
				//clean globalvar map.
				globalVarSlaves.clear();
				jars.clear();
				ConcurrentMap<String, String[]> jarsName = this.jarsName_IoT.get(5);
				jarsName.clear();
				JCL_message_result RESULT = new MessageResultImpl();
				RESULT.setType(22);
				RESULT.setResult(jclR);				
				//Write data
				//super.WriteObjectOnSock(RESULT, str);
				//End Write data
				break;				
			}
			case 23:{
				
				if (verbose) System.err.println(msg.getType()+" - "+"registerOnHost() - "+formatador.format(calendar.getTime()));	
				JCL_message_control msgR = (JCL_message_control) msg;
				JCL_message_control mc = new MessageControlImpl();
				synchronized(jarsSlaves){
				if(jarsSlaves.get(msgR.getRegisterData()[3]).contains(msgR.getRegisterData()[0])){
					mc.setRegisterData("true");
					//Write data
				//	super.WriteObjectOnSock(mc, str);
					//End Write data
					
				}else{
					
						// we must register before submit a task
						MessageRegisterImpl msgRe = new MessageRegisterImpl();
						msgRe.setJars(jars.get(msgR.getRegisterData()[0]));
						ConcurrentMap<String, String[]> jarsName = this.jarsName_IoT.get(5);
						msgRe.setJarsNames(jarsName.get(msgR.getRegisterData()[0]));
						msgRe.setClassName(msgR.getRegisterData()[0]);
						// type 1 for registering
						msgRe.setType(1);

						JCL_connector taskConnector = new ConnectorImpl();
						taskConnector.connect(msgR.getRegisterData()[1], Integer.parseInt(msgR.getRegisterData()[2]), msgR.getRegisterData()[3]);
						JCL_result result = taskConnector.sendReceive(msgRe,null).getResult();
						taskConnector.disconnect();
						boolean flag = false;
						if (result.getCorrectResult() != null) {
							flag = ((Boolean) result.getCorrectResult()).booleanValue();
							if (flag) {
								jarsSlaves.get(msgR.getRegisterData()[3]).add(msgR.getRegisterData()[0]);
								mc.setRegisterData("true");
								//Write data
				//				super.WriteObjectOnSock(mc, str);
								//End Write data
							}else{
								mc.setRegisterData("false");
								//Write data
				//				super.WriteObjectOnSock(mc, str);
								//End Write data
								
								System.err.println("cannot register class("+msgR.getRegisterData()[0]+")");
								}
							}
						}
			          }
				break;
			}
			
			case 24:{
				
				synchronized (slaves_IoT){
				if (verbose) System.err.println(msg.getType()+" - "+"slavesIDs() - "+formatador.format(calendar.getTime()));
				JCL_message_generic msgR = (JCL_message_generic) msg;
				JCL_message_get_host jclR = new MessageGetHostImpl();
				
				ConcurrentMap<String, String[]> slaves = this.slaves_IoT.get(msgR.getRegisterData());
				List<String> slavesIDs = this.slavesIDs_IoT.get(5);

				
				jclR.setSlaves(slaves);
				jclR.setSlavesIDs(slavesIDs);
				jclR.setType(24);
				
				//register Running clients
			//	String[] strUser ={str.getSocketAddress(),msgR.getRegisterData().toString()}; 
			//	runningUser.put(strUser[0]+"¬"+strUser[1], strUser);
				
				//Write data
				//super.WriteObjectOnSock(jclR, str);
				//End Write data
				}
				break;
			}
			
			//removeClient() type 25
			case 25:{	

				//removeClient() type 25
				synchronized (runningUser){
				if (verbose) System.err.println(msg.getType()+" - "+"removeClient() - "+formatador.format(calendar.getTime()));				
				JCL_message_generic msgR = (JCL_message_generic) msg;
				JCL_message_generic jclR = new MessageGenericImpl();
				jclR.setRegisterData(true);
				jclR.setType(25);
				String[] strUser ={str.getSocketAddress(),msgR.getRegisterData().toString()}; 
				runningUser.remove(strUser[0]+"¬"+strUser[1]);
				
				//Write data
				//super.WriteObjectOnSock(jclR, str);
				//End Write data
				}
				break;
			}
			
			case 26:{	
				if (verbose) System.err.println(msg.getType()+" - "+"getServerTime() - "+formatador.format(calendar.getTime()));				
				JCL_message_long jclR = new MessageLongImpl();
				jclR.setRegisterData(new Date().getTime());
				jclR.setType(25);
				
				//Write data
				//super.WriteObjectOnSock(jclR, str);
				//End Write data
				
				break;
			}
			
			case 27:{
								
				if (verbose) System.err.println(msg.getType()+" - "+"SamplingSensor() - "+formatador.format(calendar.getTime()));				
				JCL_message_sensor msgR = (JCL_message_sensor) msg;
				msgR.setTime(System.currentTimeMillis());
				
			//	System.out.println("Type:"+msgR.getSensor()+" "+msgR.getDataType());
				
				JCL_Sensor sensor = new JCL_SensorImpl();
				sensor.setDataType(msgR.getDataType());
				sensor.setTime(System.currentTimeMillis());
				sensor.setObject(msgR.getValue());
				JCLHashMap<Integer,JCL_Sensor> values = new JCLHashMap<Integer,JCL_Sensor>(msgR.getDevice()+msgR.getSensor()+"_value");
				values.put((values.size()+1),sensor);
								
				JCL_message_bool jclR = new MessageBoolImpl();
				jclR.setRegisterData(Boolean.TRUE);
				jclR.setType(27);
				
				//Write data
				//super.WriteObjectOnSock(jclR, str);
				//End Write data
				
				break;
			}
			
			case 39:{
				
				if (verbose) System.err.println(msg.getType()+" - "+"updadeCoreNumber() - "+formatador.format(calendar.getTime()));				
				JCL_message_metadata aux = (JCL_message_metadata) msg;
				JCL_message_bool jclR = new MessageBoolImpl();

				if(aux.getMetadados().size()>=5){
					String address = aux.getMetadados().get("IP");
					String port = aux.getMetadados().get("PORT");
					String slaveName = aux.getMetadados().get("MAC");
					String cores = aux.getMetadados().get("CORE(S)");
					Integer device = Integer.valueOf(aux.getMetadados().get("DEVICE_TYPE"));
					
					String[] hostPortId = {address, port, slaveName,cores};
					this.slaves_IoT.get(device).put((slaveName+port), hostPortId);

					jclR.setRegisterData(Boolean.TRUE);
					jclR.setType(-7);
					
					
				} else{
					jclR.setRegisterData(Boolean.FALSE);
					jclR.setType(-7);					
				}
				
				//Write data
				//super.WriteObjectOnSock(jclR, str);
				//End Write data
				
				break;
			}
			
			case 40:{
				
				if (verbose) System.err.println(msg.getType()+" - "+"setMetadata() - "+formatador.format(calendar.getTime()));				
				JCL_message_metadata aux = (JCL_message_metadata) msg;
				JCL_message_bool jclR = new MessageBoolImpl();

				if(aux.getMetadados().size()>=5){
					String address = aux.getMetadados().get("IP");
					String port = aux.getMetadados().get("PORT");
					String slaveName = aux.getMetadados().get("MAC");
					String cores = aux.getMetadados().get("CORE(S)");
					Integer device = Integer.valueOf(aux.getMetadados().get("DEVICE_TYPE"));
					
					ConcurrentMap<String,Map<String,String>> metadata = this.metadata_IoT.get(device);
					
					if (metadata!=null){
					metadata.put(slaveName+port, aux.getMetadados());
					jclR.setRegisterData(Boolean.TRUE);
					}else{
						jclR.setRegisterData(Boolean.FALSE);						
					}
				} else{
					jclR.setRegisterData(Boolean.FALSE);
				}

				jclR.setType(40);					
				
				//Write data
				//super.WriteObjectOnSock(jclR, str);
				//End Write data
				
				break;
			}
			
			case 41:{
				
				if (verbose) System.err.println(msg.getType()+" - "+"getMetadata() - "+formatador.format(calendar.getTime()));				
				JCL_message_control aux = (JCL_message_control)msg;
				
				
				JCL_message_metadata jclR = new MessageMetadataImpl();
				jclR.setType(41);				
				

				if(aux.getRegisterData().length==2){
					Integer device = Integer.valueOf(aux.getRegisterData()[0]);
					
					ConcurrentMap<String,Map<String,String>> metadata = this.metadata_IoT.get(device);
					
					if (metadata!=null){
						Map<String,String> meta = metadata.get(aux.getRegisterData()[1]);
						if (meta!=null){
							jclR.setMetadados(meta);
						}
					}
				}
				
				//Write data
				//super.WriteObjectOnSock(jclR, str);
				//End Write data
				
				break;
			}
			
			case 42:{
				
				synchronized (slaves_IoT){
				if (verbose) System.err.println(msg.getType()+" - "+"slavesIDsIoT() - "+formatador.format(calendar.getTime()));
				JCL_message_generic msgR = (JCL_message_generic) msg;
				JCL_message_generic jclR = new MessageGenericImpl();
				jclR.setType(42);
				jclR.setRegisterData(this.metadata_IoT.get(msgR.getRegisterData()));
								
				//register Running clients
			//	String[] strUser ={str.getSocketAddress(),msgR.getRegisterData().toString()}; 
			//	runningUser.put(strUser[0]+"¬"+strUser[1], strUser);
				
				//Write data
				//super.WriteObjectOnSock(jclR, str);
				//End Write data
				}
				break;
			}

						
			case -1:{	
//				JCL_message_control aux = (JCL_message_control) msg;
				JCL_message_metadata aux = (JCL_message_metadata) msg;
								
				if(aux.getMetadados().size()>=5){
					
					synchronized (slaves_IoT){
						
						String address = aux.getMetadados().get("IP");
						String port = aux.getMetadados().get("PORT");
						String slaveName = aux.getMetadados().get("MAC");
						String cores = aux.getMetadados().get("CORE(S)");
						Integer device = Integer.valueOf(aux.getMetadados().get("DEVICE_TYPE"));
						
//						String address = aux.getRegisterData()[0];
//						String port = aux.getRegisterData()[1];
//						String slaveName = aux.getRegisterData()[2];
//						String cores = aux.getRegisterData()[3];

						ConcurrentMap<String, String[]> jarsName;
						ConcurrentMap<String, String[]> slaves;	
						ConcurrentMap<String,Map<String,String>> metadata;
						List<String> slavesIDs;
						
						
						if (slaves_IoT.containsKey(device)){
							
							slaves = this.slaves_IoT.get(device);
							slavesIDs = this.slavesIDs_IoT.get(device);
							jarsName = this.jarsName_IoT.get(device);
							jarsName = this.jarsName_IoT.get(device);
							metadata = this.metadata_IoT.get(device);

						}else{
							
							jarsName = new ConcurrentHashMap<String, String[]>();
							slaves = new ConcurrentHashMap<String, String[]>();
							slavesIDs = new LinkedList<String>();
							metadata = new ConcurrentHashMap<String,Map<String,String>>();

							this.slaves_IoT.put(device,slaves);
							this.jarsName_IoT.put(device,jarsName);
							this.slavesIDs_IoT.put(device, slavesIDs);	
							this.metadata_IoT.put(device, metadata);	

						}
						
						if(slaves.containsKey(slaveName+port)){
							
							JCL_message_get_host mc = new MessageGetHostImpl();
							mc.setType(-4);
							mc.setSlaves(null);
							mc.setSlavesIDs(null);
							

							//Write data
				//			super.WriteObjectOnSock(mc, str);
							//End Write data
							
						}else{
														
							String[] hostPortId = {address, port, slaveName,cores};
							List<JCL_connector> conecList = new ArrayList<JCL_connector>();
							JCL_message_control mgc = new MessageControlImpl(); 
							mgc.setType(-3);							
							mgc.setRegisterData(hostPortId);
							
							for(String[] hostPort:runningUser.values()){
								JCL_connector mgcConnector = new ConnectorImpl();
								if (mgcConnector.connect(hostPort[0], Integer.parseInt(hostPort[1]),null)){
									if(mgcConnector.send(mgc,null)){
										System.out.println("Lock Client(Target:"+Arrays.toString(hostPort)+" Host add:"+Arrays.toString(hostPortId)+").");
									}
								conecList.add(mgcConnector);
								}else{
									System.err.println("Problem: Cannot Connect to Client(Target:"+Arrays.toString(hostPort));
								}
							}

							//Arrumar server IP primeiro
							
//							for(String hostName:slavesIDs){
//								String[] hostPort = slaves.get(hostName); 
//								JCL_connector mgcConnector = new ConnectorImpl();
//								mgcConnector.connect(hostPort[0], Integer.parseInt(hostPort[1]),hostPort[2]);
//								if(mgcConnector.send(mgc,null)){
//									System.out.println("Consisting cluster(Target:"+Arrays.toString(hostPort)+" Host add:"+Arrays.toString(hostPortId)+").");
//								}
//								conecList.add(mgcConnector);
//							}
//																					
//							
//							for(JCL_connector e:conecList){
//								JCL_message_result resp = (JCL_message_result) e.receive();
//								if (!(Boolean) resp.getResult().getCorrectResult()){
//									System.out.println("Problem in Consist cluster.");
//								}
//							}

							
							conecList.clear();
							for(String[] hostPort:runningUser.values()){
								JCL_connector mgcConnector = new ConnectorImpl();
								if (mgcConnector.connect(hostPort[0], Integer.parseInt(hostPort[1]),null)){
									JCL_message msgU = new MessageImpl();
									msgU.setType(-2);
									if(mgcConnector.send(msgU,null)){
										System.out.println("Try UnLock Client(Target:"+Arrays.toString(hostPort)+" Host add:"+Arrays.toString(hostPortId)+").");
									}
								conecList.add(mgcConnector);
								}else{
									System.err.println("Problem: Cannot Connect to Client(Target:"+Arrays.toString(hostPort));
								}
							}

							for(JCL_connector e:conecList){
								JCL_message_result resp = (JCL_message_result) e.receive();
								if (!(Boolean) resp.getResult().getCorrectResult()){
									System.out.println("Problem in UnLock client.");
								}
							}
							
							slaves.put((slaveName+port), hostPortId);
							slavesIDs.add(slaveName+port);
							metadata.put(slaveName+port, aux.getMetadados());
							jarsSlaves.put(slaveName, new ArrayList<String>());
		
							
							MessageGetHostImpl mc = new MessageGetHostImpl();
							mc.setType(-4);								
							mc.setSlavesIDs(slavesIDs);
							mc.setSlaves(slaves);
							
							
							//Write data
				//			super.WriteObjectOnSock(mc, str);
							//End Write data
																					
							
							System.err.println("JCL HOST " + slaveName + " registered!");
						}
					}
				}else{

//					String[] empty = {};
//					JCL_message_control mc = new MessageControlImpl();
//					mc.setType(-1);
//					mc.setRegisterData(empty);
										
					JCL_message_get_host mc = new MessageGetHostImpl();
					mc.setType(-4);
					mc.setSlaves(null);
					mc.setSlavesIDs(null);
					
					//Write data
				//	super.WriteObjectOnSock(mc, str);
					//End Write data
				}
				
				break;
			}
			
			case -2:{	
				JCL_message_control aux = (JCL_message_control) msg;
				
				if(aux.getRegisterData().length>=5){
					synchronized (slaves_IoT) {
						String address = aux.getRegisterData()[0];
						String port = aux.getRegisterData()[1];
						String slaveName = aux.getRegisterData()[2];
//  					Integer devide = 
								

						ConcurrentMap<String, String[]> slaves = this.slaves_IoT.get(5);
						List<String> slavesIDs = this.slavesIDs_IoT.get(5);
//						ConcurrentMap<String, String[]> jarsName = this.jarsName_IoT.get(5);
						
						if(slaves.containsKey(slaveName+port)){							
							Iterator<Entry<Object, String[]>> iterator = globalVarSlaves.entrySet().iterator();
							while(iterator.hasNext()){
							   Entry<Object, String[]> entry = iterator.next();
							   if (entry.getValue()[0].equals(address)){
							   iterator.remove();  
							   }                   
							}
							slaves.remove(slaveName+port);
							slavesIDs.remove(slaveName+port);
							jarsSlaves.remove(slaveName);
							String[] empty = {"unregistered"};
							JCL_message_control mc = new MessageControlImpl();
							mc.setRegisterData(empty);
							mc.setType(-5);
							//Write data
				//			super.WriteObjectOnSock(mc, str);
							//End Write data
							
							System.err.println("JCL HOST " + slaveName + " unregistered!");
						}else{
							String[] empty = {};
							JCL_message_control mc = new MessageControlImpl();
							mc.setRegisterData(empty);
							mc.setType(-5);
							//Write data
				//			super.WriteObjectOnSock(mc, str);
							//End Write data
						}
					}
				}else{
					String[] empty = {};
					JCL_message_control mc = new MessageControlImpl();
					mc.setType(-5);
					mc.setRegisterData(empty);
					//Write data
				//	super.WriteObjectOnSock(mc, str);
					//End Write data
				}
				
				break;
			}
			}

		}catch (Exception e){
			e.printStackTrace();
			
		}
				
	}	

}
