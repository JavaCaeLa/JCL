package implementations.dm_kernel.user;

import implementations.collections.JCLFuture;
import implementations.collections.JCLHashMap;
import implementations.collections.JCLSFuture;
import implementations.dm_kernel.ConnectorImpl;
import implementations.dm_kernel.MessageGenericImpl;
import implementations.dm_kernel.MessageListGlobalVarImpl;
import implementations.dm_kernel.MessageListTaskImpl;
import implementations.dm_kernel.MessageRegisterImpl;
import implementations.dm_kernel.SimpleServer;
import implementations.dm_kernel.server.RoundRobin;
import implementations.util.XORShiftRandom;
import interfaces.kernel.JCL_connector;
import interfaces.kernel.JCL_facade;
import interfaces.kernel.JCL_message_generic;
import interfaces.kernel.JCL_message_get_host;
import interfaces.kernel.JCL_message_list_global_var;
import interfaces.kernel.JCL_message_list_task;
import interfaces.kernel.JCL_message_long;
import interfaces.kernel.JCL_message_register;
import interfaces.kernel.JCL_result;
import interfaces.kernel.JCL_task;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
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
import commom.JCL_handler;
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
	private JCL_message_list_task msgTask = new MessageListTaskImpl();
	private static ReadWriteLock lock = new ReentrantReadWriteLock();
	private Set<String>  registerClass = new HashSet<String>();
	private static ConcurrentMap<String, JCL_message_register> jars;
	private static ConcurrentMap<String,List<String>> jarsSlaves;
	private static ConcurrentMap<String,String[]> slaves;	
	private boolean watchExecMeth = true;	
	private static JCL_facade instance;
	private SimpleServer simpleSever;
	private static List<String> slavesIDs;
	private static XORShiftRandom rand;
	private boolean JPF = true;
	protected static String serverAdd;	
	private int watchdog = 0;
	private int JPBsize = 50;
	private static JCL_facade jcl;
	protected static int serverPort;
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
			int PGTerm =  Integer.parseInt(properties.getProperty("PGTerm"));
			boolean DA = Boolean.valueOf(properties.getProperty("enableDinamicUp"));
			serverAdd = properties.getProperty("serverMainAdd");
			serverPort = Integer.parseInt(properties.getProperty("serverMainPort"));
			int byteBuffer = Integer.parseInt(properties.getProperty("byteBuffer"));
			int timeOut = Integer.parseInt(properties.getProperty("timeOut"));
			this.port = Integer.parseInt(properties.getProperty("simpleServerPort"));
			jars = new ConcurrentHashMap<String, JCL_message_register>();			
			jarsSlaves = new ConcurrentHashMap<String,List<String>>();			
			jcl = super.getInstance();
			
			 //Set buffer			
			ConnectorImpl.buffersize = byteBuffer;
			ConnectorImpl.PGterm = PGTerm;			


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
			    	  //watchdog end bin exec  
			    	  if((watchdog != 0) && (watchdog == msgTask.taskSize()) && (watchExecMeth)){
			    		  //Get host
			    		  String[] hostPort =RoundRobin.next(slavesIDs,slaves);
			    		  String host = hostPort[0];
			    		  String port = hostPort[1];
			    		  String mac = hostPort[2];
			    		  	
			    		  //Register missing class 
							for(String classReg:registerClass){
								if(!jarsSlaves.get(classReg).contains(host+port+mac)){
									Object[] argsLam = {host,port,mac,jars.get(classReg)};
									Future<JCL_result> ti =jcl.execute("JCL_FacadeImplLamb", "register", argsLam);
									ti.get();
//									jcl.getResultBlocking(ti);
									jarsSlaves.get(classReg).add(host+port+mac);
								}
							}
							
							//Send to host task bin
							Object[] argsLam = {host,port,mac,msgTask};
							jcl.execute("JCL_FacadeImplLamb", "binexecutetask", argsLam);
							msgTask = new MessageListTaskImpl();
			    	  }else{
			    		  //update watchdog
			    		  watchdog = msgTask.taskSize();
			    	  }
			    }

			     },0,500, TimeUnit.MILLISECONDS);
			}
			
			//Start simple server
			if(DA){
				simpleSever = new SimpleServer(this.port,slavesIDs,slaves,lock);
				simpleSever.start();				
			}
						
			//getHosts using lambari
			int type = 5;
			
			Object[] argsLam = {this.port,serverAdd,serverPort,type};
			Future<JCL_result> t = jcl.execute("JCL_FacadeImplLamb", "getSlaveIds", argsLam);
			JCL_message_get_host mgh = (JCL_message_get_host) t.get().getCorrectResult();
			slaves = mgh.getSlaves();
			slavesIDs = mgh.getSlavesIDs();			
			RoundRobin.ini(slaves, slavesIDs);
						
			//Config Slave size
			ConnectorImpl.SlaveSize = slaves.size();
			
			//finish
			System.out.println("client JCL is OK");
			

		} catch (Exception e) {
			System.err.println("JCL facade constructor error");
			e.printStackTrace();			
		}
	}
	
	//Return JCL version
	@Override
	public String version(){
		return new String("Pacu");	
	}
	
	//Get server time
	@Override
	public Long getServerTime() {
		try {
			//exec lamb
			Object[] argsLam = {serverAdd,serverPort};
			String t = jcl.execute("JCL_FacadeImplLamb", "getServerTime", argsLam);
			JCL_message_long mst = (JCL_message_long) jcl.getResultBlocking(t).getCorrectResult();
			return mst.getRegisterData()[0];
		} catch (Exception e) {
			System.err
					.println("JCL facade Lambari problem in getServerTime()");
			return null;
		}
	}
	
	//Get All times of a Task
	@Override
	public List<Long> getTaskTimes(String ID){	
		try {
			String t = jcl.execute("JCL_FacadeImplLamb", "getTaskTimes", null);					
			return ((ConcurrentHashMap<Long, List<Long>>)jcl.getResultBlocking(t).getCorrectResult()).get(Long.parseLong(ID));
			
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			System.err.println("problem in JCL facade ConcurrentHashMap<Long, List<Long>> getTaskTimes()");
			return null;
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
			jars.put(classToBeExecuted, msg);
			jarsSlaves.put(classToBeExecuted, new ArrayList<String>());	
			
			return true;
			
		} catch (Exception e) {

			System.err
					.println("problem in JCL facade register(File f, String classToBeExecuted)");
			e.printStackTrace();
			return false;
		}
	}
	
	//Register just class
	@Override
	public boolean register(Class<?> serviceClass,
			String classToBeExecuted) {
		// TODO Auto-generated method stub		
		try {
						
			// Local register
			ClassPool pool = ClassPool.getDefault();
			CtClass cc = pool.get(serviceClass.getName());
			JCL_message_register msg = new MessageRegisterImpl();
			byte[][] cb = new byte[1][];
			cb[0] = cc.toBytecode();
			msg.setJars(cb);
			msg.setJarsNames(new String[]{cc.getName()});
			msg.setClassName(classToBeExecuted);
			msg.setType(3);
			jars.put(classToBeExecuted, msg);
			jarsSlaves.put(classToBeExecuted, new ArrayList<String>());	
			
			return true;
			
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
			for(String[] oneHostPort: this.slaves.values()){
				if (jarsSlaves.get(nickName).contains(oneHostPort[0]+oneHostPort[1]+oneHostPort[2])){
					// UnRegister using lambari on host
					Object[] argsLam = {nickName,oneHostPort[0],oneHostPort[1],oneHostPort[2]};
					String t = jcl.execute("JCL_FacadeImplLamb", "unRegister", argsLam);					
					if (jcl.getResultBlocking(t).getCorrectResult() != null){
						jarsSlaves.get(nickName).remove(oneHostPort[0]+oneHostPort[1]+oneHostPort[2]);
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
	public String execute(JCL_task task) {
		try {	
			if (!JPF){
				//Get host
				String[] hostPort =RoundRobin.next(slavesIDs, this.slaves);
				String host = hostPort[0];
				String port = hostPort[1];
				String mac = hostPort[2];
			
				//Test if host contain jar
				if(jarsSlaves.get(task.getObjectName()).contains(host+port+mac)){
					//Just exec
					Object[] argsLam = {task,host,port,mac, new Boolean(true)};
					String ticket = jcl.execute("JCL_FacadeImplLamb", "execute", argsLam);
					return ticket;
				} else{
					//Exec and register
					Object[] argsLam = {task,host,port,mac,jars.get(task.getObjectName()),new Boolean(true)};
					String ticket = jcl.execute("JCL_FacadeImplLamb", "executeAndRegister", argsLam);
					jcl.getResultBlocking(ticket);
					jarsSlaves.get(task.getObjectName()).add(host+port+mac);
					return ticket;								
				}
			} else{
				//watch this method
				watchExecMeth = false;
				
				//Create bin task message
				task.setPort(this.port);
				String ticket = super.createTicketH();
				msgTask.addTask(ticket,task);			
				registerClass.add(task.getObjectName());
				
				//Send bin task
				if (this.msgTask.taskSize() == (JPBsize*RoundRobin.core)){
					String[] hostPort =RoundRobin.next(slavesIDs,slaves);
		    		String host = hostPort[0];
					String port = hostPort[1];
					String mac = hostPort[2];
					
					//Register bin task class
					for(String classReg:registerClass){
						if(!jarsSlaves.get(classReg).contains(host+port+mac)){
							Object[] argsLam = {host,port,mac,jars.get(classReg)};
							String ti =jcl.execute("JCL_FacadeImplLamb", "register", argsLam);
							jcl.getResultBlocking(ti);
							jarsSlaves.get(classReg).add(host+port+mac);
						}
					}
					
					//execute lambari
					Object[] argsLam = {host,port,mac,this.msgTask};
					jcl.execute("JCL_FacadeImplLamb", "binexecutetask", argsLam);
					msgTask = new MessageListTaskImpl();
				}				
				
				//watch this method
				watchExecMeth = true;
				return ticket;
			}
		} catch (Exception e) {
			System.err
					.println("JCL facade Lambari problem in execute(String className, Object... args)");
			return null;
		}
	}
	
	@Override
	public String execute(String objectNickname,Object... args) {
		try {	
			if (!JPF){
				//Get host
				String[] hostPort =RoundRobin.next(slavesIDs, this.slaves);
				String host = hostPort[0];
				String port = hostPort[1];
				String mac = hostPort[2];
			
				//Test if host contain jar
				if(jarsSlaves.get(objectNickname).contains(host+port+mac)){
					//Just exec					
					Object[] argsLam = {objectNickname,host,port,mac,new Boolean(true),args};
					String ticket = jcl.execute("JCL_FacadeImplLamb", "execute", argsLam);
					return ticket;
				} else{
					//Exec and register
					Object[] argsLam = {objectNickname,host,port,mac,jars.get(objectNickname),new Boolean(true),args};
					String ticket = jcl.execute("JCL_FacadeImplLamb", "executeAndRegister", argsLam);
					jcl.getResultBlocking(ticket);
					jarsSlaves.get(objectNickname).add(host+port+mac);
					return ticket;								
				}
			} else{
				//watch this method
				watchExecMeth = false;
				
				//Create bin task message
				JCL_task t = new JCL_taskImpl(null, objectNickname, args);
				String ticket = super.createTicketH();
				t.setPort(this.port);
				msgTask.addTask(ticket,t);			
				registerClass.add(objectNickname);
				
				//Send bin task
				if (this.msgTask.taskSize() == (JPBsize*RoundRobin.core)){
					String[] hostPort =RoundRobin.next(slavesIDs,slaves);
		    		String host = hostPort[0];
					String port = hostPort[1];
					String mac = hostPort[2];
					
					//Register bin task class
					for(String classReg:registerClass){
						if(!jarsSlaves.get(classReg).contains(host+port+mac)){
							Object[] argsLam = {host,port,mac,jars.get(classReg)};
							String ti =jcl.execute("JCL_FacadeImplLamb", "register", argsLam);
							jcl.getResultBlocking(ti);
							jarsSlaves.get(classReg).add(host+port+mac);
						}
					}
					
					//execute lambari
					Object[] argsLam = {host,port,mac,this.msgTask};
					jcl.execute("JCL_FacadeImplLamb", "binexecutetask", argsLam);
					msgTask = new MessageListTaskImpl();
				}				
				
				//watch this method
				watchExecMeth = true;
				return ticket;
			}
		} catch (Exception e) {
			System.err
					.println("JCL facade Lambari problem in execute(String className, Object... args)");
			return null;
		}
	}	
	
	@Override
	public String execute(String objectNickname, String methodName,
			Object... args) {
		try {
			if (!JPF){
				//Get host
				String[] hostPort =RoundRobin.next(this.slavesIDs, this.slaves);
				String host = hostPort[0];
				String port = hostPort[1];
				String mac = hostPort[2];
			
				//Test if host contain jar
				if(jarsSlaves.get(objectNickname).contains(host+port+mac)){
					// Just exec
					Object[] argsLam = {objectNickname,methodName,host,port,mac,new Boolean(true),args};
					String ticket = jcl.execute("JCL_FacadeImplLamb", "execute", argsLam);
					return ticket;
				} else{
					//Exec and register
					Object[] argsLam = {objectNickname,methodName,host,port,mac,jars.get(objectNickname),new Boolean(true),args};
					String ticket = jcl.execute("JCL_FacadeImplLamb", "executeAndRegister", argsLam);
					jcl.getResultBlocking(ticket);
					jarsSlaves.get(objectNickname).add(host+port+mac);
					return ticket;								
				}
			} else{
				//watch this method
				watchExecMeth = false;
				
				//Create bin task message
				JCL_task t = new JCL_taskImpl(null, objectNickname, methodName, args);
				String ticket = super.createTicketH();
				t.setPort(this.port);
				msgTask.addTask(ticket,t);				
				registerClass.add(objectNickname);
				
				//Send bin task
				if (this.msgTask.taskSize() == (JPBsize*RoundRobin.core)){
					String[] hostPort =RoundRobin.next(slavesIDs,slaves);
					String host = hostPort[0];
					String port = hostPort[1];
					String mac = hostPort[2];

					//Register bin task class
						for(String classReg:registerClass){
							if(!jarsSlaves.get(classReg).contains(host+port+mac)){
								Object[] argsLam = {host,port,mac,jars.get(classReg)};
								String ti =jcl.execute("JCL_FacadeImplLamb", "register", argsLam);
								jcl.getResultBlocking(ti);
								jarsSlaves.get(classReg).add(host+port+mac);
							}
						}
						
				//execute lambari		
				Object[] argsLam = {host,port,mac,this.msgTask};
				jcl.execute("JCL_FacadeImplLamb", "binexecutetask", argsLam);
				msgTask = new MessageListTaskImpl();
			}				
				//watch this method
				watchExecMeth = true;
				
			return ticket;
		}

		} catch (Exception e) {
			System.err
					.println("JCL facade problem in execute(String className, String methodName, Object... args)");

			return null;
		}
	}
	
	@Override
	public List<String> executeAll(String objectNickname, Object... args) {
		List<String> tickets, hosts;
		tickets = new ArrayList<String>();
		try {
			
			//get all host
			hosts = this.getHosts();
			
			//Exec in all host
			for (String host:hosts) {
				tickets.add(this.executeOnHost(host, objectNickname,args));
			}
			
			return tickets;
		} catch (Exception e) {
			System.err
					.println("JCL facade problem in executeAll(String className, Object... args)");
			return null;
		}
	}
		
	@Override
	public List<String> executeAll(String objectNickname, String methodName,
			Object... args) {
		List<String> tickets, hosts;
		tickets = new ArrayList<String>();
		try {						
			//get all host
			hosts = this.getHosts();
			
			//Exec in all host
			for (String host:hosts) {
				tickets.add(this.executeOnHost(host, objectNickname,methodName,args));
			}
			
			return tickets;

		} catch (Exception e) {
			System.err
					.println("JCL facade problem in executeAll(String objectNickname, String methodName, Object... args)");
			return null;
		}
	}
		
	@Override
	public List<String> executeAll(String objectNickname, Object[][] args) {
		List<String> tickets, hosts;
		tickets = new ArrayList<String>();
		try {
			
			//get all host
			hosts = this.getHosts();
			
			//Exec in all host
			for (int i=0; i < hosts.size(); i++) {
				tickets.add(this.executeOnHost(hosts.get(i), objectNickname,args[i]));
			}
			
			return tickets;
		} catch (Exception e) {
			System.err
					.println("JCL facade problem in executeAll(String objectNickname, Object[][] args)");
			return null;
		}
	}
	
	@Override
	public List<String> executeAll(String objectNickname,String methodName, Object[][] args) {
		List<String> tickets, hosts;
		tickets = new ArrayList<String>();
		try {
			
			//get all host
			hosts = this.getHosts();
			
			//Exec in all host
			for (int i=0; i < hosts.size(); i++) {
				tickets.add(this.executeOnHost(hosts.get(i), objectNickname,methodName,args[i]));
			}
			
			return tickets;
		} catch (Exception e) {
			System.err
					.println("JCL facade problem in executeAll(String objectNickname,String methodName, Object[][] args)");
			return null;
		}
	}
	
	@Override
	public List<String> executeAllCores(String objectNickname, Object... args) {
		List<String> tickets;
		Map<String, Integer> hosts;
		tickets = new ArrayList<String>();
		try {
			
			//get all host
			hosts = this.getAllHostCores();
			
			//Exec in all host
			for (Entry<String, Integer> hostCore:hosts.entrySet()) {
				//Execute o same host all cores 
				for(int j=0; j < hostCore.getValue(); j++){
				tickets.add(this.executeOnHost(hostCore.getKey(), objectNickname,args));
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
	public List<String> executeAllCores(String objectNickname,String methodName, Object... args) {
		List<String> tickets;
		Map<String, Integer> hosts;
		tickets = new ArrayList<String>();
		try {
			
			//get all host
			hosts = this.getAllHostCores();
			
			//Exec in all host
			for (Entry<String, Integer> hostCore:hosts.entrySet()) {
				//Execute o same host all cores 
				for(int j=0; j < hostCore.getValue(); j++){
				tickets.add(this.executeOnHost(hostCore.getKey(), objectNickname,methodName,args));
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
	public List<String> executeAllCores(String objectNickname,String methodName, Object[][] args) {
		List<String> tickets;
		Map<String, Integer> hosts;
		tickets = new ArrayList<String>();
		try {
			
			//get all host
			hosts = this.getAllHostCores();
			
			//Exec in all host
			int cont = 0;
			for (Entry<String, Integer> hostCore:hosts.entrySet()) {
				//Execute o same host all cores 
				for(int j=0; j < hostCore.getValue(); j++){
					tickets.add(this.executeOnHost(hostCore.getKey(), objectNickname, methodName,args[cont]));
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
	public List<String> executeAllCores(String objectNickname, Object[][] args) {
		List<String> tickets;
		Map<String, Integer> hosts;
		tickets = new ArrayList<String>();
		try {
			
			//get all host
			hosts = this.getAllHostCores();
			
			//Exec in all host
			int cont = 0;
			for (Entry<String, Integer> hostCore:hosts.entrySet()) {
				//Execute o same host all cores 
				for(int j=0; j < hostCore.getValue(); j++){
					tickets.add(this.executeOnHost(hostCore.getKey(), objectNickname,args[cont]));
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
	public String executeOnHost(String host, String objectNickname,
			Object... args) {

		try {
			// Get host			
			String[] hostPort = host.split("¬");
			String hostID = hostPort[1];
			String port = hostPort[2];
			String mac = hostPort[0].substring(0, 17);

			//Test if host contain jar
			if(jarsSlaves.get(objectNickname).contains(hostID+port+mac)){
				//Just exec
				Object[] argsLam = {objectNickname,hostID,port,mac,new Boolean(false),args};
				String ticket = jcl.execute("JCL_FacadeImplLamb", "execute", argsLam);
				return ticket;
			} else{
				
				//Exec and register
				Object[] argsLam = {objectNickname,hostID,port,mac,jars.get(objectNickname),new Boolean(false),args};
				String ticket = jcl.execute("JCL_FacadeImplLamb", "executeAndRegister", argsLam);
				jcl.getResultBlocking(ticket);
				jarsSlaves.get(objectNickname).add(hostID+port+mac);
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
	public String executeOnHost(String host, String objectNickname,
			String methodName, Object... args) {
		try {
			// Get host			
			String[] hostPort = host.split("¬");
			String hostID = hostPort[1];
			String port = hostPort[2];
			String mac = hostPort[0].substring(0, 17);
			
			
			//Test if host contain jar
			if(jarsSlaves.get(objectNickname).contains(hostID+port+mac)){
				//Just exec				
				Object[] argsLam = {objectNickname,methodName,hostID,port,mac,new Boolean(false),args};
				String ticket = jcl.execute("JCL_FacadeImplLamb", "execute", argsLam);
				return ticket;
			} else{

				//Exec and register
				Object[] argsLam = {objectNickname,methodName,hostID,port,mac,jars.get(objectNickname),new Boolean(false),args};
				String ticket = jcl.execute("JCL_FacadeImplLamb", "executeAndRegister", argsLam);
				jcl.getResultBlocking(ticket);
				jarsSlaves.get(objectNickname).add(hostID+port+mac);
				return ticket;								
			}
		} catch (Exception e) {
			System.err
					.println("JCL facade problem in executeOnHost(String host,String className, String methodName, Object... args)");

			return null;
		}
	}

	@Override
	public String executeOnHost(Entry<String, String> device, String objectNickname,
			Object... args) {

		try {
			// Get host			
//			String[] hostPort =slaves.get(device.getKey());
//			String hostID = hostPort[1];
//			String port = hostPort[2];
//			String mac = hostPort[0].substring(0, 17);

			String[] hostPort = slaves.get(device.getKey());
			String host = hostPort[0];
			String port = hostPort[1];
			String mac = hostPort[2];
			
			//Test if host contain jar
			if(jarsSlaves.get(objectNickname).contains(host+port+mac)){
				//Just exec
				Object[] argsLam = {objectNickname,host,port,mac,new Boolean(false),args};
				String ticket = jcl.execute("JCL_FacadeImplLamb", "execute", argsLam);
				return ticket;
			} else{
				
				//Exec and register
				Object[] argsLam = {objectNickname,host,port,mac,jars.get(objectNickname),new Boolean(false),args};
				String ticket = jcl.execute("JCL_FacadeImplLamb", "executeAndRegister", argsLam);
				jcl.getResultBlocking(ticket);
				jarsSlaves.get(objectNickname).add(host+port+mac);
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
	public String executeOnHost(Entry<String, String> device, String objectNickname,
			String methodName, Object... args){
		try {
			// Get host			
//			String[] hostPort = slaves.get(device.getKey());
//			String hostID = hostPort[1];
//			String port = hostPort[2];
//			String mac = hostPort[0].substring(0, 17);
			
			String[] hostPort = slaves.get(device.getKey());
			String host = hostPort[0];
			String port = hostPort[1];
			String mac = hostPort[2];
			
			//Test if host contain jar
			if(jarsSlaves.get(objectNickname).contains(host+port+mac)){
				//Just exec				
				Object[] argsLam = {objectNickname,methodName,host,port,mac,new Boolean(false),args};
				String ticket = jcl.execute("JCL_FacadeImplLamb", "execute", argsLam);
				return ticket;
			} else{

				//Exec and register
				Object[] argsLam = {objectNickname,methodName,host,port,mac,jars.get(objectNickname),new Boolean(false),args};
				String ticket = jcl.execute("JCL_FacadeImplLamb", "executeAndRegister", argsLam);
				jcl.getResultBlocking(ticket);
				jarsSlaves.get(objectNickname).add(host+port+mac);
				return ticket;								
			}
		} catch (Exception e) {
			System.err
					.println("JCL facade problem in executeOnHost(String host,String className, String methodName, Object... args)");

			return null;
		}
	}
//	protected String executeOnHostI(String host, String objectNickname,
//			Object... args) {
//
//		try {
//			// Get host			
//			String[] hostPort = host.split("¬");
//			String hostID = hostPort[1];
//			String port = hostPort[2];
//			String mac = hostPort[0].substring(0, 17);
//
//			//Test if host contain jar
//			if(jarsSlaves.get(objectNickname).contains(hostID+port+mac)){
//				//Just exec
//				Object[] argsLam = {objectNickname,hostID,port,mac,new Boolean(false),args};
//				String ticket = jcl.execute("JCL_FacadeImplLamb", "executeI", argsLam);
//				return ticket;
//			} else{
//				//Exec and register
//				Object[] argsLam = {objectNickname,hostID,port,mac,jars.get(objectNickname),new Boolean(false),args};
//				String ticket = jcl.execute("JCL_FacadeImplLamb", "executeAndRegisterI", argsLam);
//				jarsSlaves.get(objectNickname).add(hostID+port+mac);
//				return ticket;								
//			}
//		} catch (Exception e) {
//			System.err
//					.println("JCL facade problem in executeOnHost(String className, Object... args)");
//			return null;
//		}
//	}
	
//	protected String executeOnHostI(String host, String objectNickname,
//			String methodName, Object... args) {
//		try {
//			// Get host			
//			String[] hostPort = host.split("¬");
//			String hostID = hostPort[1];
//			String port = hostPort[2];
//			String mac = hostPort[0].substring(0, 17);
//			
//			
//			//Test if host contain jar
//			if(jarsSlaves.get(objectNickname).contains(hostID+port+mac)){
//				//Just exec
//				Object[] argsLam = {objectNickname,methodName,hostID,port,mac,new Boolean(false),args};
//				String ticket = jcl.execute("JCL_FacadeImplLamb", "executeI", argsLam);
//				return ticket;
//			} else{
//				//Exec and register
//				Object[] argsLam = {objectNickname,methodName,hostID,port,mac,jars.get(objectNickname),new Boolean(false),args};
//				String ticket = jcl.execute("JCL_FacadeImplLamb", "executeAndRegisterI", argsLam);
//				jarsSlaves.get(objectNickname).add(hostID+port+mac);
//				return ticket;								
//			}
//		} catch (Exception e) {
//			System.err
//					.println("JCL facade problem in executeOnHost(String host,String className, String methodName, Object... args)");
//
//			return null;
//		}
//	}
	
	@Override
	public JCL_result getResultBlocking(String ID) {
		try {
			
			JCL_result result,resultF;
			
				//Using lambari to get result
				result = jcl.getResultBlocking(ID);
				Object[] res = (Object[])result.getCorrectResult();
				Object[] arg = {Long.parseLong(ID),res[0],res[1],res[2],res[3]};
				String ticket = jcl.execute("JCL_FacadeImplLamb", "getResultBlocking", arg);				
				resultF = jcl.getResultBlocking(ticket);
				
				return resultF;

		} catch (Exception e) {
			System.err
					.println("problem in JCL facade getResultBlocking(String ID)");
			JCL_result jclr = new JCL_resultImpl();
			jclr.setErrorResult(e);			
			return jclr;
		}
	}
	
	@Override
	public JCL_result getResultBlocking(Long ID) {
		try {
			
			JCL_result result,resultF;
			
				//Using lambari to get result
				result = jcl.getResultBlocking(ID);
				Object[] res = (Object[])result.getCorrectResult();
				Object[] arg = {ID,res[0],res[1],res[2],res[3]};
				String ticket = jcl.execute("JCL_FacadeImplLamb", "getResultBlocking", arg);				
				resultF = jcl.getResultBlocking(ticket);
				
				return resultF;

		} catch (Exception e) {
			System.err
					.println("problem in JCL facade getResultBlocking(String ID)");
			JCL_result jclr = new JCL_resultImpl();
			jclr.setErrorResult(e);			
			return jclr;
		}
	}

	@Override
	public List<JCL_result> getAllResultBlocking(List<String> ID){
		List<JCL_result> result,resultF;
		List<String> Ids = new ArrayList<String>(ID.size());
		try {
			//result = jcl.getAllResultBlocking(ID);
			//Get Pacu results IDs
			
			for (String t:ID){	
				long tL = Long.parseLong(t);
				JCL_result id = jcl.getResultBlocking(tL); 
				Object[] argsLam = (Object[]) id.getCorrectResult(); 
				Object[] arg = {tL,argsLam[0],argsLam[1],argsLam[2],argsLam[3]};
				Ids.add(jcl.execute("JCL_FacadeImplLamb", "getResultBlocking", arg));
			}
			//Get all Results
			resultF = jcl.getAllResultBlocking(Ids);
			
			return resultF;
		} catch (Exception e){
			System.err
					.println("problem in JCL facade getAllResultBlocking(List<String> ID)");
					e.printStackTrace();
			return null;
		}
	}

	@Override
	public JCL_result getResultUnblocking(String ID) {
		try {
			
			//getResultUnblocking using lambari								
			Object[] res = (Object[])jcl.getResultBlocking(ID).getCorrectResult();
			Object[] arg = {Long.parseLong(ID),res[0],res[1],res[2],res[3]};
			String t = jcl.execute("JCL_FacadeImplLamb", "getResultUnblocking", arg);			
			JCL_result result = jcl.getResultBlocking(t);
			if (result.getCorrectResult().equals("NULL")){
				result.setCorrectResult(null);
			}
			return result;

		} catch (Exception e) {
			System.err
					.println("problem in JCL facade getResultUnblocking(String ID)");
			JCL_result jclr = new JCL_resultImpl();
			jclr.setErrorResult(e);

			return jclr;
		}
	}
	
	@Override
	public JCL_result getResultUnblocking(Long ID) {
		try {
			
			//getResultUnblocking using lambari								
			Object[] res = (Object[])jcl.getResultBlocking(ID).getCorrectResult();
			Object[] arg = {ID,res[0],res[1],res[2],res[3]};
			String t = jcl.execute("JCL_FacadeImplLamb", "getResultUnblocking", arg);			
			JCL_result result = jcl.getResultBlocking(t);
			if (result.getCorrectResult().equals("NULL")){
				result.setCorrectResult(null);
			}
			return result;

		} catch (Exception e) {
			System.err
					.println("problem in JCL facade getResultUnblocking(String ID)");
			JCL_result jclr = new JCL_resultImpl();
			jclr.setErrorResult(e);

			return jclr;
		}
	}

	@Override
	public List<JCL_result> getAllResultUnblocking(List<String> ID) {
		//Vars
		List<JCL_result> result,resultF;
		List<String> Ids = new ArrayList<String>(ID.size());
		resultF = new ArrayList<JCL_result>(ID.size());
		try {
		//	result = jcl.getAllResultBlocking(ID);
			
			//Get Pacu results IDs
			for (String t:ID){
				long tL = Long.parseLong(t);
				JCL_result id = jcl.getResultBlocking(tL); 
				Object[] res = (Object[])id.getCorrectResult();
				Object[] arg = {tL,res[0],res[1],res[2],res[3]};
				Ids.add(jcl.execute("JCL_FacadeImplLamb", "getResultUnblocking", arg));
			}
			
			//Get all Results
			for(String t:Ids){
				JCL_result res = jcl.getResultBlocking(t);
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
	public JCL_result removeResult(String ID) {
		try {

			//getResultUnblocking using lambari								
			Object[] res = (Object[])jcl.getResultBlocking(ID).getCorrectResult();
			Object[] arg = {Long.parseLong(ID),res[0],res[1],res[2],res[3]};
			String t = jcl.execute("JCL_FacadeImplLamb", "removeResult", arg);
			jcl.removeResult(ID);
			
			return jcl.getResultBlocking(t);

		} catch (Exception e) {
			System.err
					.println("problem in JCL facade removeResult(String ID)");
			JCL_result jclr = new JCL_resultImpl();
			jclr.setErrorResult(e);

			return jclr;
		}
	}
	
	@Override
	public JCL_result removeResult(Long ID) {
		try {

			//getResultUnblocking using lambari								
			Object[] res = (Object[])jcl.getResultBlocking(ID).getCorrectResult();
			Object[] arg = {ID,res[0],res[1],res[2],res[3]};
			String t = jcl.execute("JCL_FacadeImplLamb", "removeResult", arg);
			jcl.removeResult(ID);
			
			return jcl.getResultBlocking(t);

		} catch (Exception e) {
			System.err
					.println("problem in JCL facade removeResult(String ID)");
			JCL_result jclr = new JCL_resultImpl();
			jclr.setErrorResult(e);

			return jclr;
		}
	}

	@Override
	public boolean instantiateGlobalVar(Object key,String nickName,
			File[] jar, Object[] defaultVarValue) {
		lock.readLock().lock();
		try {
			//Get Host
			int hostId = rand.nextInt(delta, key.hashCode(), slavesIDs.size());
			String[] hostPort = slaves.get(slavesIDs.get(hostId));
			String host = hostPort[0];
			String port = hostPort[1];
			String mac = hostPort[2];
			
			
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
			
			
						
			if(jarsSlaves.get(nickName).contains(host+port+mac)){
				//instantiateGlobalVar using lambari
				Object[] argsLam = {key,nickName,defaultVarValue,host,port,mac,hostId};
				String t = jcl.execute("JCL_FacadeImplLamb", "instantiateGlobalVar", argsLam);
				return (Boolean) jcl.getResultBlocking(t).getCorrectResult();
			}else{
				//instantiateGlobalVar using lambari
				Object[] argsLam = {key,nickName,jars.get(nickName),defaultVarValue,host,port,mac,hostId};
				String t = jcl.execute("JCL_FacadeImplLamb", "instantiateGlobalVarAndReg", argsLam);
				jarsSlaves.get(nickName).add(host+port+mac);
				return (Boolean)jcl.getResultBlocking(t).getCorrectResult();
				
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
			int hostId = rand.nextInt(delta, key.hashCode(), slavesIDs.size());
			String[] hostPort = slaves.get(slavesIDs.get(hostId));
			String host = hostPort[0];
			String port = hostPort[1];	
			String mac = hostPort[2];

			//instantiateGlobalVar using lambari
			Object[] argsLam = {key,instance,host,port,mac,hostId};
			String t = jcl.execute("JCL_FacadeImplLamb", "instantiateGlobalVar", argsLam);
			return (Boolean) jcl.getResultBlocking(t).getCorrectResult();
			
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
		
				int hostId = rand.nextInt(0, key.hashCode(), slavesIDs.size());
				if (gvList.containsKey(hostId)){
					JCL_message_list_global_var gvm = gvList.get(hostId);
					gvm.putVarKeyInstance(key, value);
				}else{
					JCL_message_list_global_var gvm = new MessageListGlobalVarImpl(key,value);
					gvm.setType(35);
					gvList.put(hostId, gvm);
				}
			}
			
			
			List<String> tick = new ArrayList<String>();
			
			//Create on host using lambari
			for(Entry<Integer, JCL_message_list_global_var> ent:gvList.entrySet()){
				Integer key = ent.getKey();
				JCL_message_list_global_var value = ent.getValue();

				//Get Host
				String[] hostPort = slaves.get(slavesIDs.get(key));
				String host = hostPort[0];
				String port = hostPort[1];
				String mac = hostPort[2];


				if (!regClass){
				//instantiateGlobalVar using lambari
				Object[] argsLam = {host,port,mac,value,key};
				tick.add(jcl.execute("JCL_FacadeImplLamb", "instantiateGlobalVar", argsLam));
				}else{
					if (jarsSlaves.get(clname).contains(host+port+mac)){
						//instantiateGlobalVar using lambari
						Object[] argsLam = {host,port,mac,value,key};
						tick.add(jcl.execute("JCL_FacadeImplLamb", "instantiateGlobalVar", argsLam));						
					}else{
						//instantiateGlobalVar using lambari
						Object[] argsLam = {host,port,mac,value,jars.get(clname),key};
						tick.add(jcl.execute("JCL_FacadeImplLamb", "instantiateGlobalVarAndReg", argsLam));
						jarsSlaves.get(clname).add(host+port+mac);
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
				int hostId = rand.nextInt(0, key.hashCode(), slavesIDs.size());
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
	
	@Override
	public boolean instantiateGlobalVar(Object key, Object instance,
			String classVar, boolean Registers) {
		lock.readLock().lock();
		try {					
		// TODO Auto-generated method stub		
		//Get Host
		int hostId = rand.nextInt(delta, key.hashCode(), slavesIDs.size());
		String[] hostPort = slaves.get(slavesIDs.get(hostId));
		String host = hostPort[0];
		String port = hostPort[1];
		String mac = hostPort[2];

		
		if(!Registers){
			//instantiateGlobalVar using lambari
			Object[] argsLam = {key,instance,host,port,mac,hostId};
			String t = jcl.execute("JCL_FacadeImplLamb", "instantiateGlobalVar", argsLam);
			return (Boolean) jcl.getResultBlocking(t).getCorrectResult();
		}else{
			if(jarsSlaves.get(classVar).contains(host+port+mac)){
				//instantiateGlobalVar using lambari
				Object[] argsLam = {key,instance,host,port,mac,hostId};
				String t = jcl.execute("JCL_FacadeImplLamb", "instantiateGlobalVar", argsLam);
				return (Boolean) jcl.getResultBlocking(t).getCorrectResult();
			}else{
				Object[] argsLam = {key,instance,host,port,mac,jars.get(classVar),hostId};
				String t = jcl.execute("JCL_FacadeImplLamb", "instantiateGlobalVarAndReg", argsLam);
				jarsSlaves.get(classVar).add(host+port+mac);
				return (Boolean) jcl.getResultBlocking(t).getCorrectResult();				
			}
		}
		} finally {
			lock.readLock().unlock();
		}
	}
	
	@Override
	public Future<Boolean> instantiateGlobalVarAsy(Object key, Object instance,
			String classVar, boolean Registers) {
		lock.readLock().lock();
		try {					
		// TODO Auto-generated method stub		
		//Get Host
		int hostId = rand.nextInt(delta, key.hashCode(), slavesIDs.size());
		String[] hostPort = slaves.get(slavesIDs.get(hostId));
		String host = hostPort[0];
		String port = hostPort[1];
		String mac = hostPort[2];

		
		if(!Registers){
			//instantiateGlobalVar using lambari
			Object[] argsLam = {key,instance,host,port,mac,hostId};
			String t = jcl.execute("JCL_FacadeImplLamb", "instantiateGlobalVar", argsLam);
			return new JCLFuture<Boolean>(t);
		}else{
			if(jarsSlaves.get(classVar).contains(host+port+mac)){
				//instantiateGlobalVar using lambari
				Object[] argsLam = {key,instance,host,port,mac,hostId};
				String t = jcl.execute("JCL_FacadeImplLamb", "instantiateGlobalVar", argsLam);
				return new JCLFuture<Boolean>(t);
			}else{
				Object[] argsLam = {key,instance,host,port,mac,jars.get(classVar),hostId};
				String t = jcl.execute("JCL_FacadeImplLamb", "instantiateGlobalVarAndReg", argsLam);
				jarsSlaves.get(classVar).add(host+port+mac);
				return new JCLFuture<Boolean>(t);				
			}
		}
		} finally {
			lock.readLock().unlock();
		}
	}

	
	//Use on JCLHashMap put method
	protected static Object instantiateGlobalVarAndReturn(Object key, Object instance,
			String classVar, boolean Registers) {
		// TODO Auto-generated method stub
		lock.readLock().lock();
		try {
			
		//Get Host
		int hostId = rand.nextInt(0, key.hashCode(), slavesIDs.size());
		String[] hostPort = slaves.get(slavesIDs.get(hostId));
		String host = hostPort[0];
		String port = hostPort[1];
		String mac = hostPort[2];

		
		if(!Registers){
			//instantiateGlobalVar using lambari
			Object[] argsLam = {key,instance,host,port,mac,hostId};
			String t = jcl.execute("JCL_FacadeImplLamb", "instantiateGlobalVarReturn", argsLam);
			return jcl.getResultBlocking(t).getCorrectResult();
		}else{
			if(jarsSlaves.get(classVar).contains(host+port+mac)){
				//instantiateGlobalVar using lambari
				Object[] argsLam = {key,instance,host,port,mac,hostId};
				String t = jcl.execute("JCL_FacadeImplLamb", "instantiateGlobalVarReturn", argsLam);
				return  jcl.getResultBlocking(t).getCorrectResult();
			}else{
				Object[] argsLam = {key,instance,host,port,mac,jars.get(classVar),hostId};
				String t = jcl.execute("JCL_FacadeImplLamb", "instantiateGlobalVarAndReg", argsLam);
				jarsSlaves.get(classVar).add(host+port+mac);
				return  jcl.getResultBlocking(t).getCorrectResult();
			}
		}
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
			int hostId = rand.nextInt(delta, key.hashCode(), slavesIDs.size());
			String[] hostPort = slaves.get(slavesIDs.get(hostId));
			String host = hostPort[0];
			String port = hostPort[1];
			String mac = hostPort[2];
			
			
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
			
			
						
			if(jarsSlaves.get(nickName).contains(host+port+mac)){
				//instantiateGlobalVar using lambari
				Object[] argsLam = {key,nickName,defaultVarValue,host,port,mac,hostId};
				String t = jcl.execute("JCL_FacadeImplLamb", "instantiateGlobalVar", argsLam);
				return new JCLFuture<Boolean>(t);
			}else{
				//instantiateGlobalVar using lambari
				Object[] argsLam = {key,nickName,jars.get(nickName),defaultVarValue,host,port,mac,hostId};
				String t = jcl.execute("JCL_FacadeImplLamb", "instantiateGlobalVarAndReg", argsLam);
				jarsSlaves.get(nickName).add(host+port+mac);
				return new JCLFuture<Boolean>(t);
				
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
			int hostId = rand.nextInt(delta, key.hashCode(), slavesIDs.size());
			String[] hostPort = slaves.get(slavesIDs.get(hostId));
			String host = hostPort[0];
			String port = hostPort[1];
			String mac = hostPort[2];
			
			
			//instantiateGlobalVarAsy using lambari
			Object[] argsLam = {key,instance,host,port,mac,hostId};
			String t = jcl.execute("JCL_FacadeImplLamb", "instantiateGlobalVar", argsLam);
			return new JCLFuture<Boolean>(t);
			
		} catch (Exception e) {
			System.err
					.println("problem in JCL facade instantiateGlobalVar(String varName, Object instance)");
			return null;
		}finally {
			lock.readLock().unlock();
		}
	}

	//Arrumar
	@Override
	public Object instantiateGlobalVarOnHost(String host,String nickname,
			Object key, File[] jars, Object[] defaultVarValue) {
		try {
			int hostId = rand.nextInt(delta, key.hashCode(), slavesIDs.size());
			//instantiateGlobalVarOnHost using lambari
			Object[] argsLam = {host,nickname,key,jars,defaultVarValue,serverAdd,serverPort,hostId};
			String t = jcl.execute("JCL_FacadeImplLamb", "instantiateGlobalVarOnHost", argsLam);
			return jcl.getResultBlocking(t).getCorrectResult();
			
		} catch (Exception e) {
			System.err
					.println("problem in JCL facade instantiateGlobalVarOnHost(String host, String nickName, String varName, File[] jars, Object[] defaultVarValue)");
			return null;
		}
	}
	
	//Arrumar	
	@Override
	public boolean instantiateGlobalVarOnHost(String host, Object key,
			Object instance) {
		try {
			int hostId = rand.nextInt(delta, key.hashCode(), slavesIDs.size());
			//instantiateGlobalVarHost using lambari
			Object[] argsLam = {host,key,instance,serverAdd,serverPort,hostId};
			String t = jcl.execute("JCL_FacadeImplLamb", "instantiateGlobalVarOnHost", argsLam);
			return (Boolean) jcl.getResultBlocking(t).getCorrectResult();
			
		} catch (Exception e) {
			System.err
					.println("problem in JCL facade instantiateGlobalVarOnHost(String host, String varName, Object instance)");
			return false;
		}
	}

	@Override
	public boolean destroyGlobalVar(Object key) {
		lock.readLock().lock();
		try {
			
			//Get Host
			int[] t = rand.HostList(delta, key.hashCode(), slaves.size());
			List<String> ticks = new ArrayList<String>();
			for(int hostId:t){			
				//get host
				String[] hostPort = slaves.get(slavesIDs.get(hostId));
				String host = hostPort[0];
				String port = hostPort[1];
				String mac = hostPort[2];
						
				//destroyGlobalVar using lambari
				Object[] argsLamS = {key,serverAdd,serverPort,hostId};
				ticks.add(jcl.execute("JCL_FacadeImplLamb", "destroyGlobalVarOnHost", argsLamS));

				//destroyGlobalVar using lambari
				Object[] argsLam = {key,host,port,mac,hostId};
				ticks.add(jcl.execute("JCL_FacadeImplLamb", "destroyGlobalVar", argsLam));
			}

			//return value
			for(String tick:ticks){
				JCL_result result = jcl.getResultBlocking(tick);
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
			int[] t = rand.HostList(delta, key.hashCode(), slaves.size());
			List<String> ticks = new ArrayList<String>();
			for(int hostId:t){
				String[] hostPort = slaves.get(slavesIDs.get(hostId));
				String host = hostPort[0];
				String port = hostPort[1];
				String mac = hostPort[2];
				
				//setValueUnlocking using lambari
				Object[] argsLam = {key,value,host,port,mac,hostId};
				ticks.add(jcl.execute("JCL_FacadeImplLamb", "setValueUnlocking", argsLam));
			}
			
			//return value
			for(String tick:ticks){
				JCL_result result = jcl.getResultBlocking(tick);
				if((Boolean)result.getCorrectResult()){
				return 	true;
				}			
			}
			
			//getValue using lambari on Server
			int hostId = rand.nextInt(0, key.hashCode(), slavesIDs.size());
			Object[] argsLam = {key,value,serverAdd,serverPort,hostId};
			String tick = jcl.execute("JCL_FacadeImplLamb", "setValueUnlockingOnHost", argsLam);

			return (Boolean) jcl.getResultBlocking(tick).getCorrectResult();
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
			int[] t = rand.HostList(delta, key.hashCode(), slaves.size());
			List<String> ticks = new ArrayList<String>();
			for(int hostId:t){
			String[] hostPort = slaves.get(slavesIDs.get(hostId));
			String host = hostPort[0];
			String port = hostPort[1];
			String mac = hostPort[2];
			
			//getValue using lambari
			Object[] argsLam = {key,host,port,mac,hostId};
			ticks.add(jcl.execute("JCL_FacadeImplLamb", "getValue", argsLam));
			}
			
			for(String tick:ticks){
				JCL_result result = jcl.getResultBlocking(tick);
				if(!result.getCorrectResult().toString().equals("No value found!")){
				return 	result;
				}			
			}
			
			//getValue using lambari on Server
			int hostId = rand.nextInt(delta, key.hashCode(), slavesIDs.size());
			Object[] argsLam = {key,serverAdd,serverPort,hostId};
			String tick = jcl.execute("JCL_FacadeImplLamb", "getValueOnHost", argsLam);

			return jcl.getResultBlocking(tick);

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
			int[] t = rand.HostList(delta, key.hashCode(), slaves.size());
			List<String> ticks = new ArrayList<String>();
			
			for(int hostId:t){
				String[] hostPort = slaves.get(slavesIDs.get(hostId));
				String host = hostPort[0];
				String port = hostPort[1];
				String mac = hostPort[2];
			
				//getValueLocking using lambari
				Object[] argsLam = {key,host,port,mac,hostId};
				ticks.add(jcl.execute("JCL_FacadeImplLamb", "getValueLocking", argsLam));
			}
			
			for(String tick:ticks){
				JCL_result result = jcl.getResultBlocking(tick);
				if(!result.getCorrectResult().toString().equals("No value found!")){
					return 	result;
				}	
			}
			
			//getValue using lambari on Server
			int hostId = rand.nextInt(delta, key.hashCode(), slavesIDs.size());
			Object[] argsLam = {key,serverAdd,serverPort,hostId};
			String tick = jcl.execute("JCL_FacadeImplLamb", "getValueLockingOnHost", argsLam);

			return jcl.getResultBlocking(tick);

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
				String t = jcl.execute("JCL_FacadeImplLamb", "removeClient", argsLam);
				jcl.getResultBlocking(t);
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
			return jars.containsKey(nickName);

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
			int[] t = rand.HostList(delta, key.hashCode(), slaves.size());
			List<String> ticks = new ArrayList<String>();
			
			for(int hostId:t){
				String[] hostPort = slaves.get(slavesIDs.get(hostId));
				String host = hostPort[0];
				String port = hostPort[1];
				String mac = hostPort[2];
			
				
				//containsGlobalVar using lambari
				Object[] argsLam = {key,host,Integer.parseInt(port),mac,hostId};
				ticks.add(jcl.execute("JCL_FacadeImplLamb", "containsGlobalVar", argsLam));
			}

			//return value
			for(String tick:ticks){
				JCL_result result = jcl.getResultBlocking(tick);
				if((Boolean)result.getCorrectResult()){
				return 	true;
				}			
			}
			
			//containsGlobalVar using lambari
			int hostId = rand.nextInt(delta, key.hashCode(), slavesIDs.size());
			
			Object[] argsLam = {key,serverAdd,serverPort,serverAdd,hostId};
			String tick = jcl.execute("JCL_FacadeImplLamb", "containsGlobalVar", argsLam);
			return (Boolean) jcl.getResultBlocking(tick).getCorrectResult();
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
	public List<String> getHosts() {

		try {
			
			//getHosts
			List<String> result = new ArrayList<String>();	
			for(String ids:slavesIDs){
				result.add(ids+"¬"+this.slaves.get(ids)[0]+"¬"+this.slaves.get(ids)[1]);
			}			
			return result;
			
		} catch (Exception e) {
			System.err.println("problem in JCL facade getHosts()");
			e.printStackTrace();
			return null;
		}
	}
	
	@Override
	public int getHostCore(String hostID){
		try {
			// Get host ID			
			String[] hostPort = hostID.split("¬");
			String ID = hostPort[0];
		
			return Integer.parseInt(this.slaves.get(ID)[3]);
		
		} catch (Exception e) {
			System.err.println("problem in JCL facade getHostCore(String hostID)");
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
			for(String[] slave:this.slaves.values()){
				core+=Integer.parseInt(slave[3]);
			}
		
			return core;
		
		} catch (Exception e) {
			System.err.println("problem in JCL facade getClusterCores()");
			e.printStackTrace();
			return 0;
		}
	}

	@Override
	public Map<String, Integer> getAllHostCores() {
		try {
			//var
			Map<String, Integer> hosts = new HashMap<String, Integer>();

			//create map
			for(Entry<String, String[]> slave:this.slaves.entrySet()){
				hosts.put((slave.getKey()+"¬"+slave.getValue()[0]+"¬"+slave.getValue()[1]), Integer.parseInt(slave.getValue()[3]));
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
			int[] t = rand.HostList(delta, key.hashCode(), slaves.size());
			List<String> ticks = new ArrayList<String>();
			for(int hostId:t){
				String[] hostPort = slaves.get(slavesIDs.get(hostId));
				String host = hostPort[0];
				String port = hostPort[1];
				String mac = hostPort[2];
				
				//containsGlobalVar using lambari
				Object[] argsLam = {key,host,port,mac,hostId};
				ticks.add(jcl.execute("JCL_FacadeImplLamb", "isLock", argsLam));
			}
			
			//return value
			for(String tick:ticks){
				JCL_result result = jcl.getResultBlocking(tick);
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
			String t = jcl.execute("JCL_FacadeImplLamb", "cleanEnvironment", argsLam);
			return (Boolean) jcl.getResultBlocking(t).getCorrectResult();

		} catch (Exception e) {
			System.err.println("problem in JCL facade cleanEnvironment()");
			e.printStackTrace();
			return false;
		}
	}
	
	@Override
	public boolean insertHost(String mac, String ip, String port) {
		try {
			
			//insertHost using lambari
			Object[] argsLam = {mac,ip,port,serverAdd,serverPort};
			String t = jcl.execute("JCL_FacadeImplLamb", "insertHost", argsLam);
			return (Boolean) jcl.getResultBlocking(t).getCorrectResult();

		} catch (Exception e) {
			System.err.println("problem in JCL facade insertHost(String mac, String ip, String port)");
			e.printStackTrace();
			return false;
		}
	}
	
	@Override
	public boolean removeHost(String mac, String ip, String port) {
		try {
			//removeHost using lambari
			Object[] argsLam = {mac,ip,port,serverAdd,serverPort};
			String t = jcl.execute("JCL_FacadeImplLamb", "removeHost", argsLam);
			return (Boolean) jcl.getResultBlocking(t).getCorrectResult();

		} catch (Exception e) {
			System.err.println("problem in JCL facade removeHost(String mac, String ip, String port)");
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
			properties.load(new FileInputStream("../jcl_conf/config.properties"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return Holder.getInstancePacu(properties);
	}
	
	public static JCL_facade getInstanceLambari(){
		return Holder.getInstanceLambari();
	}
	
	public static class Holder {
		
		protected static String ServerIP(){
			return serverAdd;
		}
		
		protected static int ServerPort(){
			return serverPort;
		}
				
		protected synchronized static JCL_facade getInstance(){
						
			Properties properties = new Properties();
			try {
				properties.load(new FileInputStream("../jcl_conf/config.properties"));
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
		
		//create hash key map
		protected boolean createhashKey(String gvName,String hostIp, int IDhost){
			
			//Get Ip host
			String[] hostPort = hostIp.split("¬");
			String mac = hostPort[0].substring(0, 17);
			String hostID = hostPort[1];
			String port = hostPort[2];
			
			//createhashKey using lambari
			JCL_message_generic mc = new MessageGenericImpl();
			mc.setRegisterData(gvName);
			mc.setType(28);
			JCL_connector controlConnector = new ConnectorImpl();
			controlConnector.connect(hostID,Integer.parseInt(port),mac);
			JCL_message_generic mr = (JCL_message_generic) controlConnector.sendReceiveG(mc,(short)IDhost);
			controlConnector.disconnect();
			return (Boolean) mr.getRegisterData();
		}
		
		//add key to hash key map
		protected boolean hashAdd(String gvName,String hostIp,Object Key, int IDhost){
			
			//Get Ip host
			String[] hostPort = hostIp.split("¬");
			String mac = hostPort[0].substring(0, 17);
			String hostID = hostPort[1];
			String port = hostPort[2];
			
			//hashAdd using lambari
			JCL_message_generic mc = new MessageGenericImpl();
			Object[] ob = {gvName,Key};
			mc.setRegisterData(ob);
			mc.setType(29);
			JCL_connector controlConnector = new ConnectorImpl();
			controlConnector.connect(hostID,Integer.parseInt(port),mac);
			JCL_message_generic mr = (JCL_message_generic) controlConnector.sendReceiveG(mc,(short)IDhost);
			controlConnector.disconnect();
			return (Boolean) mr.getRegisterData();
		}
		
		//add key list to hash key map
		protected boolean hashAdd(String gvName,String hostIp,List<Object> keys, int IDhost){
			
			//Get Ip host
			String[] hostPort = hostIp.split("¬");
			String mac = hostPort[0].substring(0, 17);
			String hostID = hostPort[1];
			String port = hostPort[2];
			
			// hashAdd using lambari
			JCL_message_generic mc = new MessageGenericImpl();
			Object[] ob = {gvName,keys};
			mc.setRegisterData(ob);
			mc.setType(36);
			JCL_connector controlConnector = new ConnectorImpl();
			controlConnector.connect(hostID,Integer.parseInt(port),mac);
			JCL_message_generic mr = (JCL_message_generic) controlConnector.sendReceiveG(mc, (short)IDhost);
			controlConnector.disconnect();
			return (Boolean) mr.getRegisterData();
		}
	
		//remove key from hash key map
		protected boolean hashRemove(String gvName,String hostIp,Object Key, int IDhost){
			//Get Ip host
			String[] hostPort = hostIp.split("¬");
			String mac = hostPort[0].substring(0, 17);
			String hostID = hostPort[1];
			String port = hostPort[2];
			
			//hashRemove using lambari
			JCL_message_generic mc = new MessageGenericImpl();
			Object[] ob = {gvName,Key};
			mc.setRegisterData(ob);
			mc.setType(30);
			JCL_connector controlConnector = new ConnectorImpl();
			controlConnector.connect(hostID,Integer.parseInt(port),mac);
			JCL_message_generic mr = (JCL_message_generic) controlConnector.sendReceiveG(mc,(short)IDhost);
			controlConnector.disconnect();
			return (Boolean) mr.getRegisterData();
		}
		
		//hash key map contain key
		protected boolean containsKey(String gvName,String hostIp,Object Key, int IDhost){
			//Get Ip host
			String[] hostPort = hostIp.split("¬");
			String mac = hostPort[0].substring(0, 17);
			String hostID = hostPort[1];
			String port = hostPort[2];
			
			//containsKey using lambari
			JCL_message_generic mc = new MessageGenericImpl();
			Object[] ob = {gvName,Key};
			mc.setRegisterData(ob);
			mc.setType(31);
			JCL_connector controlConnector = new ConnectorImpl();
			controlConnector.connect(hostID,Integer.parseInt(port),mac);
			JCL_message_generic mr = (JCL_message_generic) controlConnector.sendReceiveG(mc,(short)IDhost);
			controlConnector.disconnect();
			return (Boolean) mr.getRegisterData();
		}
		
		//hash key map size
		protected int hashSize(String gvName,String hostIp, int IDhost){
			//Get Ip host
			String[] hostPort = hostIp.split("¬");
			String mac = hostPort[0].substring(0, 17);
			String hostID = hostPort[1];
			String port = hostPort[2];
			
			//hashSize using lambari
			JCL_message_generic mc = new MessageGenericImpl();
			mc.setRegisterData(gvName);
			mc.setType(32);
			JCL_connector controlConnector = new ConnectorImpl();
			controlConnector.connect(hostID,Integer.parseInt(port),mac);
			JCL_message_generic mr = (JCL_message_generic) controlConnector.sendReceiveG(mc,(short)IDhost);
			controlConnector.disconnect();
			return (Integer) mr.getRegisterData();
		}		
		
		//clean hash key map
		protected Set hashClean(String gvName,String hostIp, int IDhost){
			//Get Ip host
			String[] hostPort = hostIp.split("¬");
			String mac = hostPort[0].substring(0, 17);
			String hostID = hostPort[1];
			String port = hostPort[2];
			
			//hashClean using lambari
			JCL_message_generic mc = new MessageGenericImpl();
			mc.setRegisterData(gvName);
			mc.setType(33);
			JCL_connector controlConnector = new ConnectorImpl();
			controlConnector.connect(hostID,Integer.parseInt(port),mac);
			JCL_message_generic mr = (JCL_message_generic) controlConnector.sendReceiveG(mc,(short)IDhost);
			controlConnector.disconnect();
			return (Set) mr.getRegisterData();
		}
		
		//get set of keys
		protected Set getHashSet(String gvName,String hostIp, int IDhost){
			
			//Get Ip host
			String[] hostPort = hostIp.split("¬");
			String mac = hostPort[0].substring(0, 17);
			String hostID = hostPort[1];
			String port = hostPort[2];
			
			//getHashSet using lambari
			JCL_message_generic mc = new MessageGenericImpl();
			mc.setRegisterData(gvName);
			mc.setType(34);
			JCL_connector controlConnector = new ConnectorImpl();
			controlConnector.connect(hostID,Integer.parseInt(port),mac);
			JCL_message_generic mr = (JCL_message_generic) controlConnector.sendReceiveG(mc,(short)IDhost);
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

			
			
			Map<Integer,JCL_message_generic> gvList = getBinValueInterator(key, gvname);			
			
			//getHashQueue using lambari
			Iterator<Entry<Integer,JCL_message_generic>> intGvList = gvList.entrySet().iterator();
			
			if (intGvList.hasNext()){

			Entry<Integer,JCL_message_generic> entHost = intGvList.next();
			JCL_message_generic mc = entHost.getValue();				

			//Get Host
			String[] hostPort = slaves.get(slavesIDs.get(entHost.getKey()));
			String host = hostPort[0];
			String port = hostPort[1];
			String mac = hostPort[2];
			
			
			//Using lambari			
			Object[] argsLam = {mc,queue,host,port,mac,entHost.getKey()};
			String t = jcl.execute("JCL_FacadeImplLamb", "getHashValues", argsLam);			
			jcl.getResultBlocking(t).getCorrectResult();			
			
			intGvList.remove();
			}
			
			return gvList;
		}
		
		//get value from cluster
		protected String getHashValues(Queue queue,JCL_message_generic mc, int key){
			//Get Host
			String[] hostPort = slaves.get(slavesIDs.get(key));
			String host = hostPort[0];
			String port = hostPort[1];	
			String mac = hostPort[2];
			
			
			//Using lambari
			Object[] argsLam = {mc,queue,host,port,mac,key};
			String t = jcl.execute("JCL_FacadeImplLamb", "getHashValues", argsLam);
			
			return t;
		}
		
		
		protected Object getResultBlocking(String t){
			return jcl.getResultBlocking(t).getCorrectResult();
		}
		
	}	
}
