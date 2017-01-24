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
import implementations.dm_kernel.IoTuser.JCL_IoTFacadeImpl;
import implementations.util.IoT.CryptographyUtils;
import interfaces.kernel.JCL_IoTfacade;
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
import jdk.nashorn.internal.ir.debug.ObjectSizeCalculator;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
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
	
	private ConcurrentMap<Integer, ConcurrentMap<String, String[]>> slaves;
	private ConcurrentMap<Integer,ConcurrentMap<String,Map<String,String>>> metadata;
	private List<Entry<String, Map<String, String>>> devicesExec;
//	private ConcurrentMap<String, String[]> jarsName;
	private ConcurrentMap<Object,String[]> globalVarSlaves;
	private ConcurrentMap<String,String[]> runningUser;
	private ConcurrentMap<String,List<String>> jarsSlaves;	
	private ConcurrentMap<String, JCL_message_register> jars;
	private ConcurrentMap<Integer, List<String>> slavesIDs;
	private boolean verbose;

	public SocketConsumer(GenericResource<S> re, AtomicBoolean kill, ConcurrentMap<Object, String[]> globalVarSlaves, ConcurrentMap<Integer,List<String>> slavesIDs, ConcurrentMap<Integer,ConcurrentMap<String, String[]>> slaves, ConcurrentMap<String, List<String>> jarsSlaves, ConcurrentMap<String, JCL_message_register> jars, boolean verbose,ConcurrentMap<String,String[]> runningUser,ConcurrentMap<Integer,ConcurrentMap<String,Map<String,String>>> metadata, List<Entry<String, Map<String, String>>> devicesExec) {
		
		super(re,kill);	
		this.globalVarSlaves = globalVarSlaves;
		this.slavesIDs = slavesIDs;
		this.slaves = slaves;
		this.verbose = verbose;
		this.jarsSlaves = jarsSlaves;
//		this.jarsName = jarsName;
		this.jars = jars;
		this.runningUser = runningUser;
		this.metadata = metadata;
		this.devicesExec = devicesExec;
		
		
		this.slaves.put(0,new ConcurrentHashMap<String, String[]>());
//		this.jarsName.put(0,new ConcurrentHashMap<String, String[]>());
		this.slavesIDs.put(0, new LinkedList<String>());	
		this.metadata.put(0, new ConcurrentHashMap<String,Map<String,String>>());

		this.slaves.put(1,new ConcurrentHashMap<String, String[]>());
//		this.jarsName.put(1,new ConcurrentHashMap<String, String[]>());
		this.slavesIDs.put(1, new LinkedList<String>());	
		this.metadata.put(1, new ConcurrentHashMap<String,Map<String,String>>());

		this.slaves.put(2,new ConcurrentHashMap<String, String[]>());
//		this.jarsName.put(2,new ConcurrentHashMap<String, String[]>());
		this.slavesIDs.put(2, new LinkedList<String>());	
		this.metadata.put(2, new ConcurrentHashMap<String,Map<String,String>>());

		this.slaves.put(3,new ConcurrentHashMap<String, String[]>());
//		this.jarsName.put(3,new ConcurrentHashMap<String, String[]>());
		this.slavesIDs.put(3, new LinkedList<String>());	
		this.metadata.put(3, new ConcurrentHashMap<String,Map<String,String>>());

		this.slaves.put(4,new ConcurrentHashMap<String, String[]>());
//		this.jarsName.put(4,new ConcurrentHashMap<String, String[]>());
		this.slavesIDs.put(4, new LinkedList<String>());	
		this.metadata.put(4, new ConcurrentHashMap<String,Map<String,String>>());

		this.slaves.put(5,new ConcurrentHashMap<String, String[]>());
//		this.jarsName.put(5,new ConcurrentHashMap<String, String[]>());
		this.slavesIDs.put(5, new LinkedList<String>());	
		this.metadata.put(5, new ConcurrentHashMap<String,Map<String,String>>());

		this.slaves.put(6,new ConcurrentHashMap<String, String[]>());
//		this.jarsName.put(6,new ConcurrentHashMap<String, String[]>());
		this.slavesIDs.put(6, new LinkedList<String>());	
		this.metadata.put(6, new ConcurrentHashMap<String,Map<String,String>>());

		this.slaves.put(7,new ConcurrentHashMap<String, String[]>());
//		this.jarsName.put(7,new ConcurrentHashMap<String, String[]>());
		this.slavesIDs.put(7, new LinkedList<String>());	
		this.metadata.put(7, new ConcurrentHashMap<String,Map<String,String>>());
		
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
//				if (!jars.containsKey(msgR.getClassName())){
					jars.put(msgR.getClassName(), msgR);
				//	ConcurrentMap<String, String[]> jarsName = this.jarsName.get(msgR.getTypeDevice());
//					jarsName.put(msgR.getClassName(), msgR.getJarsNames());
					JCL_result r = new JCL_resultImpl();
					r.setCorrectResult(Boolean.TRUE);					
					JCL_message_result RESULT = new MessageResultImpl();
					RESULT.setType(1);
					RESULT.setResult(r);
					
					//Write data
					super.WriteObjectOnSock(RESULT, str);
					//End Write data
										
//				}else{
//					JCL_result r = new JCL_resultImpl();
//					r.setCorrectResult(Boolean.FALSE);					
//					JCL_message_result RESULT = new MessageResultImpl();
//					RESULT.setType(1);
//					RESULT.setResult(r);
//
//					//Write data
//					super.WriteObjectOnSock(RESULT, str);
//					//End Write data
//				}
		
				break;
			}
			case 2:{				
				if (verbose) System.err.println(msg.getType()+" - "+"unRegister() - "+formatador.format(calendar.getTime()));
				JCL_message_control msgR = (JCL_message_control) msg;
				JCL_message_control mc = new MessageControlImpl();
				boolean ok = true;
				
				ConcurrentMap<String, String[]> slaves = this.slaves.get(msgR.getMsgType());
				
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
			//	ConcurrentMap<String, String[]> jarsName = this.jarsName.get(msgR.getTypeDevice());
//				jarsName.remove(msgR.getRegisterData()[0]);
				mc.setRegisterData(Boolean.toString(ok));

				//Write data
				super.WriteObjectOnSock(mc, str);
				//End Write data
				
				break;
			}
			
			case 3:{						
				if (verbose) System.err.println(msg.getType()+" - "+"register() - "+formatador.format(calendar.getTime()));				
				JCL_message_register msgR = (JCL_message_register) msg;
//				if (!jars.containsKey(msgR.getClassName())){
					jars.put(msgR.getClassName(), msgR);
				//	ConcurrentMap<String, String[]> jarsName = this.jarsName.get(msgR.getTypeDevice());
//					jarsName.put(msgR.getClassName(), msgR.getJarsNames());
					JCL_result r = new JCL_resultImpl();
					r.setCorrectResult(Boolean.TRUE);					
					JCL_message_result RESULT = new MessageResultImpl();
					RESULT.setType(1);
					RESULT.setResult(r);
					
					//Write data
					super.WriteObjectOnSock(RESULT, str);
					//End Write data
										
//				}else{
//					JCL_result r = new JCL_resultImpl();
//					r.setCorrectResult(Boolean.FALSE);					
//					JCL_message_result RESULT = new MessageResultImpl();
//					RESULT.setType(1);
//					RESULT.setResult(r);
//
//					//Write data
//					super.WriteObjectOnSock(RESULT, str);
//					//End Write data
//				}
		
				break;
			}

			case 4:{
				if (verbose) System.err.println(msg.getType()+" - "+"execute() - "+formatador.format(calendar.getTime()));	
				JCL_message_control msgR = (JCL_message_control) msg;
				JCL_message_generic mc = new MessageGenericImpl();
				
		//		ConcurrentMap<String, String[]> slaves = this.slaves_IoT.get(5);
		//		List<String> slavesIDs = this.slavesIDs_IoT.get(5);

				 Map<String, String> hostPort =RoundRobin.getDevice(this.devicesExec);
	    		  String host = hostPort.get("IP");
	    		  String port = hostPort.get("PORT");
	    		  String mac = hostPort.get("MAC");
				
				if(jarsSlaves.get(mac).contains(msgR.getRegisterData()[0])){
					mc.setRegisterData(hostPort);
					
					//Write data
					super.WriteObjectOnSock(mc, str);
					//End Write data

				}else{
					synchronized(jarsSlaves){
						if(jarsSlaves.get(mac).contains(msgR.getRegisterData()[0])){
							mc.setRegisterData(hostPort);
							
							//Write data
							super.WriteObjectOnSock(mc, str);
							//End Write data	
						}else{
						// we must register before submit a task
//						MessageRegisterImpl msgRe = new MessageRegisterImpl();
//						msgRe.setJars(jars.get(msgR.getRegisterData()[0]));
			//			ConcurrentMap<String, String[]> jarsName = this.jarsName_IoT.get(5);
//						msgRe.setJarsNames(jarsName.get(msgR.getRegisterData()[0]));
//						msgRe.setClassName(msgR.getRegisterData()[0]);
						// type 1 for registering
//						msgRe.setType(1);

						JCL_connector taskConnector = new ConnectorImpl();
						taskConnector.connect(host, Integer.parseInt(port),null);
						JCL_result result = taskConnector.sendReceive(jars.get(msgR.getRegisterData()[0]),null).getResult();
						taskConnector.disconnect();
						boolean flag = false;
						if (result.getCorrectResult() != null) {
							flag = ((Boolean) result.getCorrectResult()).booleanValue();
							if (flag) {
								jarsSlaves.get(mac).add(msgR.getRegisterData()[0]);
								mc.setRegisterData(hostPort);
								
								//Write data
								super.WriteObjectOnSock(mc, str);
								//End Write data
								
							}else{

								//Write data
								super.WriteObjectOnSock(mc, str);
								//End Write data

								System.err.println("cannot register class("+msgR.getRegisterData()[0]+")");
								}
							}
					}	
					}
				}
				break;
			}
			
			case 5:{
				if (verbose) System.err.println(msg.getType()+" - "+"execute() - "+formatador.format(calendar.getTime()));
				JCL_message_control msgR = (JCL_message_control) msg;
				JCL_message_generic mc = new MessageGenericImpl();

				

				 Map<String, String> hostPort =RoundRobin.getDevice(this.devicesExec);
	    		  String host = hostPort.get("IP");
	    		  String port = hostPort.get("PORT");
	    		  String mac = hostPort.get("MAC");

				
				synchronized(jarsSlaves){
				if(jarsSlaves.get(mac).contains(msgR.getRegisterData()[0])){
					mc.setRegisterData(hostPort);

					//Write data
					super.WriteObjectOnSock(mc, str);
					//End Write data
					
				}else{
					
						// we must register before submit a task
//						MessageRegisterImpl msgRe = new MessageRegisterImpl();
//						msgRe.setJars(jars.get(msgR.getRegisterData()[0]));
//						msgRe.setJarsNames(jarsName.get(msgR.getRegisterData()[0]));
//						msgRe.setClassName(msgR.getRegisterData()[0]);
//						// type 1 for registering
//						msgRe.setType(1);

						JCL_connector taskConnector = new ConnectorImpl();
						taskConnector.connect(host, Integer.parseInt(port),null);
						JCL_result result = taskConnector.sendReceive(jars.get(msgR.getRegisterData()[0]),null).getResult();
						taskConnector.disconnect();
						boolean flag = false;
						if (result.getCorrectResult() != null) {
							flag = ((Boolean) result.getCorrectResult()).booleanValue();
							if (flag) {
								jarsSlaves.get(mac).add(msgR.getRegisterData()[0]);
								mc.setRegisterData(hostPort);
							
								//Write data
								super.WriteObjectOnSock(mc, str);
								//End Write data
								
							}else{
								//Write data
								super.WriteObjectOnSock(mc, str);
								//End Write data
								
								System.err.println("cannot register class("+msgR.getRegisterData()[0]+")");
								}
							}
						}
				}
				// in.close();
				break;
			}
			
			case 9:{
				if (verbose) System.err.println(msg.getType()+" - "+"instantiateGlobalVar() - "+formatador.format(calendar.getTime()));				
				synchronized (globalVarSlaves) {
					JCL_message_control aux = (JCL_message_control) msg;
					if(globalVarSlaves.containsKey(aux.getRegisterData()[0])){
						JCL_message_control mc = new MessageControlImpl();
						String[] hostPort = {};
						mc.setRegisterData(hostPort);
						//Write data
						super.WriteObjectOnSock(mc, str);
						//End Write data
						
					}else{
						ConcurrentMap<String, String[]> slaves = this.slaves.get(aux.getTypeDevice());
						List<String> slavesIDs = this.slavesIDs.get(aux.getTypeDevice());

						String[] hostPort = RoundRobin.nextGV(slavesIDs, slaves);
						globalVarSlaves.put(aux.getRegisterData()[0],hostPort);
						JCL_message_control mc = new MessageControlImpl();
						mc.setRegisterData(hostPort);
						//Write data
						super.WriteObjectOnSock(mc, str);
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
						super.WriteObjectOnSock(mc, str);
						//End Write data
						
					}else{
						ConcurrentMap<String, String[]> slaves = this.slaves.get(aux.getTypeDevice());
						List<String> slavesIDs = this.slavesIDs.get(aux.getTypeDevice());

						String[] hostPort = RoundRobin.nextGV(slavesIDs, slaves);
						globalVarSlaves.put(aux.getRegisterData()[0],hostPort);
						JCL_message_control mc = new MessageControlImpl();
						mc.setRegisterData(hostPort);
						//Write data
						super.WriteObjectOnSock(mc, str);
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
						super.WriteObjectOnSock(mc, str);
						//End Write data
						
					}else{
						JCL_result jclR = new JCL_resultImpl();
						String[] hostPort ={};
						jclR.setCorrectResult(hostPort);
						JCL_message_result mc = new MessageResultImpl();
						mc.setType(11);
						mc.setResult(jclR);
						
						//Write data
						super.WriteObjectOnSock(mc, str);
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
					super.WriteObjectOnSock(mc, str);
					//End Write data
					
				}else{
					String[] hostPort = {};
					JCL_message_control mc = new MessageControlImpl();
					mc.setRegisterData(hostPort);
					//Write data
					super.WriteObjectOnSock(mc, str);
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
					super.WriteObjectOnSock(mc, str);
					//End Write data
					
				}else{
					String[] hostPort = {};
					JCL_message_control mc = new MessageControlImpl();
					mc.setRegisterData(hostPort);
					//Write data
					super.WriteObjectOnSock(mc, str);
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
					super.WriteObjectOnSock(mc, str);
					//End Write data					
				}else{
					String[] hostPort = {};
					JCL_result jclR = new JCL_resultImpl();
					jclR.setCorrectResult(hostPort);
					JCL_message_result mc = new MessageResultImpl();
					mc.setType(14);
					mc.setResult(jclR);

					
					//Write data
					super.WriteObjectOnSock(mc, str);
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
					super.WriteObjectOnSock(mc, str);
					//End Write data					
				}else{
					String[] hostPort = {};
					JCL_result jclR = new JCL_resultImpl();
					jclR.setCorrectResult(hostPort);
					JCL_message_result mc = new MessageResultImpl();
					mc.setType(14);
					mc.setResult(jclR);

					
					//Write data
					super.WriteObjectOnSock(mc, str);
					//End Write data
				}
				break;
			}
			case 16:{	
				//devemos parar todos os slaves antes...
				if (verbose) System.err.println(msg.getType()+" - "+"destroy() - "+formatador.format(calendar.getTime()));				
				JCL_message_control mc = new MessageControlImpl();
				
				ConcurrentMap<String, String[]> slaves = this.slaves.get(mc.getTypeDevice());

				String[] hostPort =new String[slaves.size()*2];
				int i=0;
				
				for(String[] oneHostPort: slaves.values()){
					hostPort[i] = oneHostPort[0];
					hostPort[i+1] = oneHostPort[1];
					i+=2;
				}
				
				mc.setRegisterData(hostPort);
				//Write data
				super.WriteObjectOnSock(mc, str);
				//End Write data

				jars.clear();
			//	ConcurrentMap<String, String[]> jarsName = this.jarsName.get(mc.getTypeDevice());
			//	jarsName.clear();
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
				super.WriteObjectOnSock(mc, str);
				//End Write data

				break;
			}
			
			case 18:{		
				if (verbose) System.err.println(msg.getType()+" - "+"containsTask() - "+formatador.format(calendar.getTime()));				
				JCL_message_control aux = (JCL_message_control) msg;
				JCL_message_control mc = new MessageControlImpl();
			//	ConcurrentMap<String, String[]> jarsName = this.jarsName.get(aux.getTypeDevice());
				if(jars.containsKey(aux.getRegisterData()[0])){
					mc.setRegisterData("true");
					} else {mc.setRegisterData("false");}
				
				//Write data
				super.WriteObjectOnSock(mc, str);
				//End Write data
				
				
				break;
			}
			
//			case 19:{	
//				if (verbose) System.err.println(msg.getType()+" - "+"getHosts() - "+formatador.format(calendar.getTime()));				
//				JCL_result jclR = new JCL_resultImpl();
//				List<String> result = new ArrayList<String>();
//				ConcurrentMap<String, String[]> slaves = this.slaves_IoT.get(5);
//				List<String> slavesIDs = this.slavesIDs_IoT.get(5);
//
//					for(int i = 0;i < slavesIDs.size();i++){
//						result.add(slavesIDs.get(i)+":"+slaves.get(slavesIDs.get(i))[0]+":"+slaves.get(slavesIDs.get(i))[1]);
//					}
//				jclR.setCorrectResult(result);
//				JCL_message_result RESULT = new MessageResultImpl();
//				RESULT.setType(19);
//				RESULT.setResult(jclR);
//				
//				//Write data
//				super.WriteObjectOnSock(RESULT, str);
//				//End Write data
//				
//				break;
//			}
			
			case 20:{	
				if (verbose) System.err.println(msg.getType()+" - "+"isLock() - "+formatador.format(calendar.getTime()));				
				JCL_message_control aux = (JCL_message_control) msg;
				if(globalVarSlaves.containsKey(aux.getRegisterData()[0])){
					JCL_message_control mc = new MessageControlImpl();
					String[] hostPort = globalVarSlaves.get(aux.getRegisterData()[0]);
					mc.setRegisterData(hostPort);
					//Write data
					super.WriteObjectOnSock(mc, str);
					//End Write data
					
				}else{
					String[] hostPort = {};
					JCL_message_control mc = new MessageControlImpl();
					mc.setRegisterData(hostPort);
					//Write data
					super.WriteObjectOnSock(mc, str);
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
					ConcurrentMap<String, String[]> slaves = this.slaves.get(aux.getTypeDevice());
					List<String> slavesIDs = this.slavesIDs.get(aux.getTypeDevice());

					while((i<slavesIDs.size())&& (contain)){
						if (slaves.get(slavesIDs.get(i))[0].equals(obj[1])) contain = false;
						i++;
					}
					
					if((globalVarSlaves.containsKey(obj[0])) || (contain)){
						JCL_message_control mc = new MessageControlImpl();
						mc.setRegisterData("false");
						//Write data
						super.WriteObjectOnSock(mc, str);
						//End Write data
						
					}else{
						globalVarSlaves.put(obj[0],hostPort);
						JCL_message_control mc = new MessageControlImpl();
						mc.setRegisterData("true");
						//Write data
						super.WriteObjectOnSock(mc, str);
						//End Write data
					}
				}	
				break;
			}	
			case 22:{
				if (verbose) System.err.println(msg.getType()+" - "+"cleanEnvironment() - "+formatador.format(calendar.getTime()));				
				JCL_result jclR = new JCL_resultImpl();
//				List<String[]> result = new ArrayList<String[]>();
//				ConcurrentMap<String, String[]> slaves = this.slaves.get(msg.getTypeDevice());
//				List<String> slavesIDs = this.slavesIDs.get(msg.getTypeDevice());

//					for(int i = 0;i < slavesIDs.size();i++){
//						result.add(new String[]{slaves.get(slavesIDs.get(i))[0],slaves.get(slavesIDs.get(i))[1]});
//						List<String> jS = jarsSlaves.get(slavesIDs.get(i));
//						if (jS != null){
//							jS.clear();
//						}					
//					}
				jclR.setCorrectResult(this.metadata);
				//clean globalvar map.
				globalVarSlaves.clear();
				jars.clear();
			//	ConcurrentMap<String, String[]> jarsName = this.jarsName.get(msg.getTypeDevice());
			//	jarsName.clear();
				JCL_message_result RESULT = new MessageResultImpl();
				RESULT.setType(22);
				RESULT.setResult(jclR);				
				//Write data
				super.WriteObjectOnSock(RESULT, str);
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
					super.WriteObjectOnSock(mc, str);
					//End Write data
					
				}else{
					
						// we must register before submit a task
			//			MessageRegisterImpl msgRe = new MessageRegisterImpl();
			//			msgRe.setJars(jars.get(msgR.getRegisterData()[0]));
					//	ConcurrentMap<String, String[]> jarsName = this.jarsName.get(msgR.getTypeDevice());
			//			msgRe.setJarsNames(jarsName.get(msgR.getRegisterData()[0]));
			//			msgRe.setClassName(msgR.getRegisterData()[0]);
						// type 1 for registering
			//			msgRe.setType(1);

						JCL_connector taskConnector = new ConnectorImpl();
						taskConnector.connect(msgR.getRegisterData()[1], Integer.parseInt(msgR.getRegisterData()[2]), msgR.getRegisterData()[3]);
						JCL_result result = taskConnector.sendReceive(jars.get(msgR.getRegisterData()[0]),null).getResult();
						taskConnector.disconnect();
						boolean flag = false;
						if (result.getCorrectResult() != null) {
							flag = ((Boolean) result.getCorrectResult()).booleanValue();
							if (flag) {
								jarsSlaves.get(msgR.getRegisterData()[3]).add(msgR.getRegisterData()[0]);
								mc.setRegisterData("true");
								//Write data
								super.WriteObjectOnSock(mc, str);
								//End Write data
							}else{
								mc.setRegisterData("false");
								//Write data
								super.WriteObjectOnSock(mc, str);
								//End Write data
								
								System.err.println("cannot register class("+msgR.getRegisterData()[0]+")");
								}
							}
						}
			          }
				break;
			}
			
			case 24:{
				
				synchronized (slaves){
				if (verbose) System.err.println(msg.getType()+" - "+"slavesIDs() - "+formatador.format(calendar.getTime()));
				JCL_message_generic msgR = (JCL_message_generic) msg;
				JCL_message_get_host jclR = new MessageGetHostImpl();
				
				ConcurrentMap<String, String[]> slaves = this.slaves.get(msgR.getTypeDevice());
				List<String> slavesIDs = this.slavesIDs.get(msgR.getTypeDevice());

				
				jclR.setSlaves(slaves);
				jclR.setSlavesIDs(slavesIDs);
				jclR.setType(24);
				
				//register Running clients
			//	String[] strUser ={str.getSocketAddress(),msgR.getRegisterData().toString()}; 
			//	runningUser.put(strUser[0]+"¬"+strUser[1], strUser);
				
				//Write data
				super.WriteObjectOnSock(jclR, str);
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
				super.WriteObjectOnSock(jclR, str);
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
				super.WriteObjectOnSock(jclR, str);
				//End Write data
				
				break;
			}
			
			case 27:{
				
				if (verbose) System.err.println(msg.getType()+" - "+"SamplingSensor() - "+formatador.format(calendar.getTime()));				
				JCL_message_sensor msgR = (JCL_message_sensor) msg;
				msgR.setTime(System.currentTimeMillis());
				
				JCL_Sensor sensor = new JCL_SensorImpl();
				sensor.setDataType(msgR.getDataType());
				sensor.setTime(System.currentTimeMillis());
				sensor.setObject(msgR.getValue());
				JCLHashMap<Integer,JCL_Sensor> values = new JCLHashMap<Integer,JCL_Sensor>(msgR.getDevice()+msgR.getSensor()+"_value");
				int key = values.keySet().stream().max(Integer::compareTo).orElse(0);
				values.put((key+1),sensor);
								
				JCL_message_bool jclR = new MessageBoolImpl();
				jclR.setRegisterData(Boolean.TRUE);
				jclR.setType(27);
				
				//Write data
				super.WriteObjectOnSock(jclR, str);
				//End Write data
				
				
				// Automatic clean of sensor data
				int size = 1;
				Iterator<ConcurrentMap<String, Map<String, String>>> it = metadata.values().iterator();
				while (it.hasNext()){
					ConcurrentMap<String, Map<String, String>> cmMap = it.next();
					if ( cmMap.containsKey(msgR.getDevice()) ){
						Map<String, String>map = cmMap.get(msgR.getDevice());
						size = Integer.valueOf(map.get("SENSOR_SIZE_" + msgR.getSensor()));
					}
				}
				
				
				int maxSize = 1024 * 1024 * size;	// Put the value in bytes
				int hashSize = 0;
				for (JCL_Sensor s: values.values())
					hashSize += ObjectSizeCalculator.getObjectSize(s.getObject());
				
				if ( hashSize > maxSize )
					System.out.println("** Automatic sensor data cleaning **");
				
				while ( hashSize > maxSize ){
					int index = values.keySet().iterator().next();
					hashSize -= ObjectSizeCalculator.getObjectSize(values.get(index).getObject());
					values.remove(index);
				}
				
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
					this.slaves.get(device).put((slaveName+port), hostPortId);

					jclR.setRegisterData(Boolean.TRUE);
					jclR.setType(-7);
					
					
				} else{
					jclR.setRegisterData(Boolean.FALSE);
					jclR.setType(-7);					
				}
				
				//Write data
				super.WriteObjectOnSock(jclR, str);
				//End Write data
				
				break;
			}
			
			case 40:{
				
				if (verbose) System.err.println(msg.getType()+" - "+"setMetadata() - "+formatador.format(calendar.getTime()));				
				JCL_message_metadata aux = (JCL_message_metadata) msg;
				JCL_message_bool jclR = new MessageBoolImpl();

				if(aux.getMetadados().size()>=5){
//					String address = aux.getMetadados().get("IP");
					String port = aux.getMetadados().get("PORT");
					String slaveName = aux.getMetadados().get("MAC");
//					String cores = aux.getMetadados().get("CORE(S)");
					Integer device = Integer.valueOf(aux.getMetadados().get("DEVICE_TYPE"));
					
					ConcurrentMap<String,Map<String,String>> metadata = this.metadata.get(device);
					
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
				super.WriteObjectOnSock(jclR, str);
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
					
					ConcurrentMap<String,Map<String,String>> metadata = this.metadata.get(device);
					
					if (metadata!=null){
						Map<String,String> meta = metadata.get(aux.getRegisterData()[1]);
						if (meta!=null){
							jclR.setMetadados(meta);
						}
					}
				}
				
				//Write data
				super.WriteObjectOnSock(jclR, str);
				//End Write data
				
				break;
			}
			
			case 42:{
				boolean activateEncryption = false;
				if (ConnectorImpl.encryption){
					activateEncryption = true;
					ConnectorImpl.encryption = false;
				}
				
				synchronized(slaves){
				if (verbose) System.err.println(msg.getType()+" - "+"slavesIDsIoT() - "+formatador.format(calendar.getTime()));
				JCL_message_generic jclR = new MessageGenericImpl();
				jclR.setType(42);
				Object data[] = {this.metadata, CryptographyUtils.getClusterPassword()};
				jclR.setRegisterData(data);
				//register Running clients
			//	String[] strUser ={str.getSocketAddress(),msgR.getRegisterData().toString()}; 
			//	runningUser.put(strUser[0]+"¬"+strUser[1], strUser);
				
				//Write data
				super.WriteObjectOnSock(jclR, str);
				//End Write data
				}
				
				if (activateEncryption)
					ConnectorImpl.encryption = true;
				
				break;
			}
			
			case 60:{						
				if (verbose) System.err.println(msg.getType()+" - "+"register() - "+formatador.format(calendar.getTime()));				
				JCL_message_register msgR = (JCL_message_register) msg;
//				if (!jars.containsKey(msgR.getClassName())){
					jars.put(msgR.getClassName(), msgR);
				//	ConcurrentMap<String, String[]> jarsName = this.jarsName.get(msgR.getTypeDevice());
//					jarsName.put(msgR.getClassName(), msgR.getJarsNames());
					JCL_result r = new JCL_resultImpl();
					r.setCorrectResult(Boolean.TRUE);					
					JCL_message_result RESULT = new MessageResultImpl();
					RESULT.setType(1);
					RESULT.setResult(r);
					
					//Write data
					super.WriteObjectOnSock(RESULT, str);
					//End Write data
										
//				}else{
//					JCL_result r = new JCL_resultImpl();
//					r.setCorrectResult(Boolean.FALSE);					
//					JCL_message_result RESULT = new MessageResultImpl();
//					RESULT.setType(1);
//					RESULT.setResult(r);
//
//					//Write data
//					super.WriteObjectOnSock(RESULT, str);
//					//End Write data
//				}
		
				break;
			}

						
			case -1:{	
				JCL_message_metadata aux = (JCL_message_metadata) msg;
				boolean activateEncryption = false;
				if (ConnectorImpl.encryption){
					ConnectorImpl.encryption = false;
					activateEncryption = true;
				}
				
				if(aux.getMetadados().size()>=5){
					
					synchronized (slaves){
						
						String address = aux.getMetadados().get("IP");
						String port = aux.getMetadados().get("PORT");
						String slaveName = aux.getMetadados().get("MAC");
						String cores = aux.getMetadados().get("CORE(S)");
						Integer device = Integer.valueOf(aux.getMetadados().get("DEVICE_TYPE"));
						
						ConcurrentMap<String, String[]> jarsName;
						ConcurrentMap<String, String[]> slavesInt;	
						ConcurrentMap<String,Map<String,String>> metadata;
						List<String> slavesIDs;
						
						
						if (slaves.containsKey(device)){
							
							slavesInt = this.slaves.get(device);
							slavesIDs = this.slavesIDs.get(device);
					//		jarsName = this.jarsName.get(device);
							metadata = this.metadata.get(device);

						}else{
							
					//		jarsName = new ConcurrentHashMap<String, String[]>();
							slavesInt = new ConcurrentHashMap<String, String[]>();
							slavesIDs = new LinkedList<String>();
							metadata = new ConcurrentHashMap<String,Map<String,String>>();

							this.slaves.put(device,slavesInt);
						//	this.jarsName.put(device,jarsName);
							this.slavesIDs.put(device, slavesIDs);	
							this.metadata.put(device, metadata);	

						}
						
						if(slaves.containsKey(slaveName+port)){
							
							JCL_message_get_host mc = new MessageGetHostImpl();
							mc.setType(-4);
							mc.setSlaves(null);
							mc.setSlavesIDs(null);
							

							//Write data
							super.WriteObjectOnSock(mc, str);
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
							
							slavesInt.put((slaveName+port), hostPortId);
							slavesIDs.add(slaveName+port);
							metadata.put(slaveName+port, aux.getMetadados());
							jarsSlaves.put(slaveName, new ArrayList<String>());
							this.devicesExec.add(new implementations.util.Entry(slaveName+port, aux.getMetadados()));

		
							
							MessageGetHostImpl mc = new MessageGetHostImpl();
							mc.setType(-4);								
							mc.setSlavesIDs(slavesIDs);
							mc.setSlaves(slavesInt);
							mc.setMAC(CryptographyUtils.getClusterPassword());
							
							
							//Write data
							super.WriteObjectOnSock(mc, str);
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
					super.WriteObjectOnSock(mc, str);
					//End Write data
				}
				
				if (activateEncryption)
					ConnectorImpl.encryption = true;
				break;
			}
			
			case -2:{	
				JCL_message_control aux = (JCL_message_control) msg;
				
				if(aux.getRegisterData().length>=5){
					synchronized (slaves) {
						String address = aux.getRegisterData()[0];
						String port = aux.getRegisterData()[1];
						String slaveName = aux.getRegisterData()[2];
						Integer device = Integer.valueOf(aux.getRegisterData()[4]);
								
//						ConcurrentMap<String, String[]> slaves = this.slaves.get(device);
//						List<String> slavesIDs = this.slavesIDs.get(device);
						
					//	ConcurrentMap<String, String[]> jarsName;
						ConcurrentMap<String, String[]> slaves;	
						ConcurrentMap<String,Map<String,String>> metadata;
						List<String> slavesIDs;
						
						slaves = this.slaves.get(device);
						slavesIDs = this.slavesIDs.get(device);
					//	jarsName = this.jarsName.get(device);
						metadata = this.metadata.get(device);
						
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
							this.devicesExec.remove(new implementations.util.Entry(slaveName+port, metadata.get(slaveName+port)));
							metadata.remove(slaveName+port);
							jarsSlaves.remove(slaveName);

							
							String[] empty = {"unregistered"};
							JCL_message_control mc = new MessageControlImpl();
							mc.setRegisterData(empty);
							mc.setType(-5);
							//Write data
							super.WriteObjectOnSock(mc, str);
							//End Write data
							
							System.err.println("JCL HOST " + slaveName + " unregistered!");
						}else{
							String[] empty = {};
							JCL_message_control mc = new MessageControlImpl();
							mc.setRegisterData(empty);
							mc.setType(-5);
							//Write data
							super.WriteObjectOnSock(mc, str);
							//End Write data
						}
					}
				}else{
					String[] empty = {};
					JCL_message_control mc = new MessageControlImpl();
					mc.setType(-5);
					mc.setRegisterData(empty);
					//Write data
					super.WriteObjectOnSock(mc, str);
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
