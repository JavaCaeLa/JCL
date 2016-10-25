package implementations.sm_kernel;

import implementations.collections.JCLFuture;
import implementations.collections.JCLSFuture;
import implementations.util.CoresAutodetect;
import interfaces.kernel.JCL_facade;
import interfaces.kernel.JCL_orb;
import interfaces.kernel.JCL_result;
import interfaces.kernel.JCL_task;
import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import commom.GenericConsumer;
import commom.GenericResource;
import commom.JCL_resultImpl;
import commom.JCL_taskImpl;

public class JCL_FacadeImpl implements JCL_facade {
	
	//Global variables
	private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
	protected final static Map<Long, JCL_result> results = new ConcurrentHashMap<Long, JCL_result>();
	protected final List<GenericConsumer<JCL_task>> workers	= new ArrayList<GenericConsumer<JCL_task>>();
	protected List<AtomicBoolean> killWorkers = new ArrayList<AtomicBoolean>();
	protected final GenericResource<JCL_task> r;
	protected final JCL_orb<JCL_result> orb;	
	private static final AtomicLong numOfTasks = new AtomicLong(0);
	private static JCL_facade instance;	
	private static JCL_facade instancePacu;
	//End global variables
	
	protected JCL_FacadeImpl(boolean type, GenericResource<JCL_task> re){
		
		if(type){
			orb = JCL_orbImpl.getInstancePacu();
		}else{
			orb = JCL_orbImpl.getInstance();
		}
		
		r = re;

		try{			
			System.err.println("machine with " + CoresAutodetect.cores + " cores");			
			JCL_Crawler crawler = new JCL_Crawler(CoresAutodetect.cores,results,workers,killWorkers,r,orb);			
			scheduler.scheduleAtFixedRate(crawler,0,1000,TimeUnit.MILLISECONDS);			
		}catch ( Exception e ){
			System.err.println("JCL facade constructor error");
			e.printStackTrace();			
		}
		
	}

	//Use only on Pacu JCLUser
	protected static String createTicket(){
		//Create ticket without task
		Long ticket = numOfTasks.getAndIncrement();
		JCL_result jclr = new JCL_resultImpl();	
		results.put(ticket, jclr);
		
		return ticket.toString();
	}	

	//Get num of cores
	protected int coresAutoDetect() {
		return CoresAutodetect.detect();
	}
	
	//Use only on Pacu JCLUser
	protected static boolean updateTicket(long ticket,Object result){
		try {
			JCL_result jResult = results.get(ticket);
			jResult.setCorrectResult(result);
			synchronized (jResult){
				jResult.notifyAll();
			}
			
			return true;
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			return false;
		}
	}
	
	//execute with Method name as arg
	@Override
	public Future<JCL_result> execute(String className, String methodName, Object... args) {
		
		//create ticket
		Long ticket = numOfTasks.getAndIncrement();	
		
		try{
			//create task			
			JCL_task t = new JCL_taskImpl(ticket, className, methodName, args);
			JCL_result jclr = new JCL_resultImpl();	
			jclr.setTime(t.getTaskTime());
			results.put(ticket, jclr);			
			r.putRegister(t);
			
			return new JCLFuture<JCL_result>(ticket);
			
		}catch (Exception e){
			System.err.println("JCL facade problem in execute(String className, String methodName, Object... args)");			
			e.printStackTrace();
			return new JCLSFuture<JCL_result>(null);
		}	
	}
	
	//execute with JCL_taskImpl as arg
	@Override
	public Future<JCL_result> execute(JCL_task task) {
		
		//create ticket
		Long ticket = numOfTasks.getAndIncrement();	
		
		try{
			//create task			
			//JCL_task t = new JCL_taskImpl(ticket, className, methodName, args);
			task.setTaskID(ticket);
			JCL_result jclr = new JCL_resultImpl();	
			jclr.setTime(task.getTaskTime());
			results.put(ticket, jclr);			
			r.putRegister(task);
			
			return new JCLFuture<JCL_result>(ticket);
			
		}catch (Exception e){
			System.err.println("JCL facade problem in execute(String className, String methodName, Object... args)");			
			e.printStackTrace();
			return new JCLSFuture<JCL_result>(null);
		}	
	}
	
	//execute method execute
	@Override
	public Future<JCL_result> execute(String objectNickname, Object... args){
				
		//Create ticket
		Long ticket = numOfTasks.getAndIncrement();
		
		try{
			JCL_task t = new JCL_taskImpl(ticket, objectNickname, args);
			t.setTaskTime(System.nanoTime());			
			JCL_result jclr = new JCL_resultImpl();	
			jclr.setTime(t.getTaskTime());
			results.put(ticket, jclr);			
			r.putRegister(t);
			
			return new JCLFuture<JCL_result>(ticket);
		}catch (Exception e){
			System.err.println("JCL facade problem in execute (String objectNickname, Object... args)");
			e.printStackTrace();
			return new JCLSFuture<JCL_result>(null);
		}		
	}

//	//Wait
//	protected void join(long ID) {
//		try{
//			JCL_result jclr = results.get(ID);
//			if((jclr.getCorrectResult()==null)&&(jclr.getErrorResult()==null)){				
//				synchronized (jclr){
//					//Necessary with use Lambari in parallel (racing condition)
//					if((jclr.getCorrectResult()==null)&&(jclr.getErrorResult()==null)){
//					jclr.wait();
//					}
//				}				
//				join(ID);
//			}
//		}catch (Exception e){
//			System.err.println("problem in JCL facade join ");			
//		}		
//	}
	
	//Lock and get result
//	@Override
//	public JCL_result getResultBlocking(String ID) {
//		try{
//			//lock waiting result
//			join(Long.parseLong(ID,10));
//			return results.get(Long.parseLong(ID));
//			
//		}catch (Exception e){
//			System.err.println("problem in JCL facade getResultBlocking(String ID)");
//			JCL_result jclr = new JCL_resultImpl();
//			jclr.setErrorResult(e);			
//			return jclr;
//		}
//	}
	
//	//Lock and get result
//	@Override
//	public JCL_result getResultBlocking(Long ID) {
//		try{
//			//lock waiting result
//			join(ID);
//			return results.get(ID);
//			
//		}catch (Exception e){
//			System.err.println("problem in JCL facade getResultBlocking(String ID)");
//			JCL_result jclr = new JCL_resultImpl();
//			jclr.setErrorResult(e);			
//			return jclr;
//		}
//	}

	//Get result
//	@Override
//	public JCL_result getResultUnblocking(String ID){
//		try{
//			//get result
//			return results.get(Long.parseLong(ID));
//		}catch (Exception e){
//			System.err.println("problem in JCL facade getResultUnblocking(String ID)");			
//			JCL_result jclr = new JCL_resultImpl();
//			jclr.setErrorResult(e);
//			
//			return jclr;
//		}
//	}
	
	//Get result
//	@Override
//	public JCL_result getResultUnblocking(Long ID){
//		try{
//			//get result
//			return results.get(ID);
//		}catch (Exception e){
//			System.err.println("problem in JCL facade getResultUnblocking(String ID)");			
//			JCL_result jclr = new JCL_resultImpl();
//			jclr.setErrorResult(e);
//			
//			return jclr;
//		}
//	}
	
	//Remove result
//	@Override
//	public JCL_result removeResult(String ID){
//		try{
//			return results.remove(Long.parseLong(ID));
//		}catch(Exception e){
//			System.err.println("problem in JCL facade removeResult(String ID)");			
//			JCL_result jclr = new JCL_resultImpl();
//			jclr.setErrorResult(e);
//			
//			return jclr;
//		}
//	}
	
	//Remove result
//	@Override
//	public JCL_result removeResult(Long ID){
//		try{
//			return results.remove(ID);
//		}catch(Exception e){
//			System.err.println("problem in JCL facade removeResult(String ID)");			
//			JCL_result jclr = new JCL_resultImpl();
//			jclr.setErrorResult(e);
//			
//			return jclr;
//		}
//	}
	
	//Remove global Var
	@Override
	public boolean deleteGlobalVar(Object key) {
		try{
			//exec on orb
			return orb.destroyGlobalVar(key);
		}catch (Exception e){
			System.err.println("problem in JCL facade destroyGlobalVar(String varName)");
			return false;
		}
	}

	//get value
	@Override
	public JCL_result getValue(Object key) {
		try{
			//exec on orb
			return orb.getValue(key);
		}catch(Exception e){
			System.err.println("problem in JCL facade getValue(String varName)");
			
			JCL_result jclr = new JCL_resultImpl();
			jclr.setErrorResult(e);
			return jclr;
		}		
	}
	
	//Register file of jars
	@Override
	public boolean register(File[] f, String classToBeExecuted) {
		try{
			//exec on orb
			return orb.register(f, classToBeExecuted);			
		}catch(Exception e){
			
			System.err.println("problem in JCL facade register(File f, String classToBeExecuted)");
			e.printStackTrace();
			return false;
		}
	}
	
	
	//Get server time
	@Override
	public Long getServerTime() {
		try {
			return (new Date().getTime());
		} catch (Exception e) {
			System.err
					.println("JCL facade Lambari problem in getServerTime()");
			return null;
		}
	}
	//Register class
	@Override
	public boolean register(Class<?> serviceClass,String nickName){		
		return orb.register(serviceClass, nickName);
	}

	/*	
	//Register with String 
	@Override
	public boolean register(String object, String nickName) {
				
		try{
	//		updateTask();
			return orb.register(object, nickName);
			
		}catch(Exception e){
			System.err.println("problem in JCL facade register(Class<?> object, String nickName)");
			
			return false;
		}
	}
	 */
	
	//Set value and unlock
	@Override
	public boolean setValueUnlocking(Object key, Object value) {
		try{
			//exec on orb
			return orb.setValueUnlocking(key, value);
		}catch (Exception e){
			System.err.println("problem in JCL facade setValueUnlocking(String varName, Object value)");
			
			return false;
		}
	}

	//Get value and lock
	@Override
	public JCL_result getValueLocking(Object key) {		
		try{
			JCL_result result = orb.getValueLocking(key);
			if (result==null) this.getValueLocking(key);
			return result;	
			
		}catch (Exception e){
			System.err.println("problem in JCL facade getValueLocking(String varName)");
			
			JCL_result jclr = new JCL_resultImpl();
			jclr.setErrorResult(e);
			return jclr;
		}
	}

	@Override
	public void destroy(){
		try{
			
				r.setFinished();
				scheduler.shutdown();
			for(GenericConsumer<JCL_task> gc: workers){
				gc.join();
				gc = null;
			}
			numOfTasks.set(-1);
			instance = null;
			orb.cleanEnvironment();
		
		}catch (Exception e){
			System.err.println("problem in JCL facade destroy");
		}
	}
	
	//Unregister class
	@Override
	public boolean unRegister(String nickName) {
		try{
			//exec on orb
			return orb.unRegister(nickName);		
		}catch(Exception e){
			System.err.println("problem in JCL facade unRegister(String nickName)");
			
			return false;
		}
	}

	//Create global Var
	@Override
	public boolean instantiateGlobalVar(Object key,String nickName, File[] jars,
			Object[] defaultVarValue) {
		try{
			//exec on orb
			return orb.instantiateGlobalVar(key,nickName, jars, defaultVarValue);
		}catch(Exception e){
			System.err.println("problem in JCL facade instantiateGlobalVar(String varName, File[] jars, Object[] defaultVarValue)");
			return false;
		}
	}

	//Create global Var
	@Override
	public boolean instantiateGlobalVar(Object key, Object instance) {
		try{
			//exec on orb
			return orb.instantiateGlobalVar(key, instance);
		}catch(Exception e){
			System.err.println("problem in JCL facade instantiateGlobalVar(String varName, Object instance)");
			return false;
		}
	}	
	
	//Check if a class was registered 
	@Override
	public boolean containsTask(String nickName) {
		try{
			//exec on orb		
			return orb.containsTask(nickName);
			
		}catch(Exception e){
			System.err.println("problem in JCL facade containsTask(String nickName)");
			return false;
		}
	}

	//Check if exist a global var 
	@Override
	public boolean containsGlobalVar(Object key) {
		try{
			//exec on orb
			return orb.containsGlobalVar(key);
			
		}catch(Exception e){
			System.err.println("problem in JCL facade containsGlobalVar(String nickName)");
			return false;
		}
	}

	//Check if a global var is on lock status
	@Override
	public boolean isLock(Object key) {
		try{
			//exec on orb
			return orb.isLock(key);
			
		}catch(Exception e){
			System.err.println("problem in JCL facade isLock(String nickName)");
			return false;
		}
	}	
	
	//Clear all environment
	@Override
	public boolean cleanEnvironment() {		
		return orb.cleanEnvironment();
	}
	
	//Get all result
	@Override
	public List<JCL_result> getAllResultBlocking(List<Future<JCL_result>> tickets) {
		try{
			// return list of results
			List<JCL_result> result = new ArrayList<JCL_result>(tickets.size());
			
			for(Future<JCL_result> t: tickets){				
				result.add(t.get());
			}
			
			return result;
			
		}catch (Exception e){
			
			System.err.println("problem in JCL facade getAllResultBlocking(List<String> ID)");
			
			return null;
		}
	}

	//Get all result
	@Override
	public List<JCL_result> getAllResultUnblocking(List<Future<JCL_result>> tickets) {
		try{
			// return list of results
			List<JCL_result> result = new ArrayList<JCL_result>(tickets.size());
			
			for(Future<JCL_result> t: tickets) {
				result.add(t.get(0, TimeUnit.SECONDS));
			}
						
			return result;
		}catch (Exception e){
			
			System.err.println("problem in JCL facade getAllResultUnblocking(List<String> ID)");			
			return null;
		}
	}
	
	//Execute All just Pacu. Lambari execute on localhost
	@Override
	@Deprecated
	public List<Future<JCL_result>> executeAll(String objectNickname, Object... args) {
			//Create ticket
				Long ticket = numOfTasks.getAndIncrement();
				
				try{
					List<Future<JCL_result>> tickets = new ArrayList<Future<JCL_result>>();
					tickets.add(this.execute(objectNickname, args));
					
					return tickets;
					
				}catch (Exception e){
					System.err.println("JCL facade problem in execute (String objectNickname, Object... args)");
					e.printStackTrace();
					return new ArrayList<Future<JCL_result>>();
				}
	}

	//Execute OnHost just Pacu. Lambari execute on localhost
//	@Override
//	@Deprecated
//	public Future<JCL_result> executeOnHost(Entry<String, String> device, String objectNickname,
//			Object... args) {
//		//Create ticket
//		Long ticket = numOfTasks.getAndIncrement();
//		
//		try{
//			JCL_task t = new JCL_taskImpl(ticket, objectNickname, args);
//			t.setTaskTime(System.nanoTime());			
//			JCL_result jclr = new JCL_resultImpl();	
//			jclr.setTime(t.getTaskTime());
//			results.put(ticket, jclr);			
//			r.putRegister(t);
//			
//			return ticket.toString();
//		}catch (Exception e){
//			System.err.println("JCL facade problem in execute (String objectNickname, Object... args)");
//			e.printStackTrace();
//			return String.valueOf(-1);
//		}		
//	}
	
	//Execute OnHost just Pacu. Lambari execute on localhost
	@Override
	@Deprecated
	public Future<JCL_result> executeOnDevice(Entry<String, String> device, String className,
			Object... args) {
		//Create ticket
		try{					
			return this.execute(className, args);
			
		}catch (Exception e){
			System.err.println("JCL facade problem in executeOnDevice(Entry<String, String> device, String className, Object... args)");			
			return new JCLSFuture<JCL_result>(null);
		}	
	}

	//Execute All just Pacu. Lambari execute on localhost
	@Override
	@Deprecated
	public List<Future<JCL_result>> executeAll(String className, String methodName,
			Object... args) {
		//create ticket
				try{
					//create task	
					List<Future<JCL_result>> tickets = new ArrayList<Future<JCL_result>>();
					tickets.add(this.execute(className, methodName, args));
										
					return tickets;
					
				}catch (Exception e){
					System.err.println("JCL facade problem in execute(String className, String methodName, Object... args)");			
					return new ArrayList<Future<JCL_result>>();
				}
	}

	//Execute OnHost just Pacu. Lambari execute on localhost
//	@Override
//	@Deprecated
//	public String executeOnHost(Entry<String, String> device, String className,
//			String methodName, Object... args){
//		//create ticket
//				Long ticket = numOfTasks.getAndIncrement();	
//				
//				try{
//					//create task			
//					JCL_task t = new JCL_taskImpl(ticket, className, methodName, args);
//					JCL_result jclr = new JCL_resultImpl();
//					jclr.setTime(t.getTaskTime());
//					results.put(ticket, jclr);			
//					r.putRegister(t);
//					
//					return ticket.toString();
//					
//				}catch (Exception e){
//					System.err.println("JCL facade problem in execute(String className, String methodName, Object... args)");			
//					return String.valueOf(-1);
//				}
//	}

	//Execute OnHost just Pacu. Lambari execute on localhost
	@Override
	@Deprecated
	public Future<JCL_result> executeOnDevice(Entry<String, String> device, String className,
			String methodName, Object... args) {
				
				try{					
					return this.execute(className, methodName, args);
					
				}catch (Exception e){
					System.err.println("JCL facade problem in executeOnDevice(Entry<String, String> device, String className,String methodName, Object... args)");			
					return new JCLSFuture<JCL_result>(null);
				}
	}

	//Return localHost Lambari.
	@Override
	@Deprecated
	public List<Entry<String, String>> getDevices() {
		try {
			List<Entry<String, String>> host = new ArrayList<Entry<String, String>>();			
			host.add(new implementations.util.Entry<String, String>("Localhost", "Localhost"));
			return host;
			
		} catch (Exception e) {

			System.err.println("cannot use this method with JCL distributed");

			return null;
		}
	}

	@Override
	@Deprecated
	public Object instantiateGlobalVarOnDevice(Entry<String, String> device, String nickname,
			Object varName, File[] jars, Object[] defaultVarValue) {
		try{
			//exec on orb
			return orb.instantiateGlobalVar(varName,nickname, jars, defaultVarValue);
		}catch(Exception e){
			System.err.println("problem in JCL facade instantiateGlobalVar(String varName, File[] jars, Object[] defaultVarValue)");
			return false;
		}
	}
	
	//Create global var on local host
	@Override
	@Deprecated
	public boolean instantiateGlobalVarOnDevice(Entry<String, String> device, Object key,
			Object instance) {
		try{
			//exec on orb
			return orb.instantiateGlobalVar(key, instance);
		}catch(Exception e){
			System.err.println("problem in JCL facade instantiateGlobalVar(String varName, Object instance)");
			return false;
		}
	}

	// Always return false
//	@Override
//	@Deprecated
//	public boolean insertHost(String mac, String ip, String port) {
//		try {
//			return false;
//		} catch (Exception e) {
//			return false;
//		}
//	}

	//Always return false
//	@Override
//	@Deprecated
//	public boolean removeHost(String mac, String ip, String port) {
//		try {
//			return false;
//		} catch (Exception e) {
//			return false;
//		}
//	}
	
	@Override
	public String version(){
		return new String("Lambari");	
	}

	
	//Create global var
	@Override
	@Deprecated
	public Future<Boolean> instantiateGlobalVarAsy(Object key, Object instance) {
		try{
			//exec on orb
			return new JCLSFuture<Boolean>(orb.instantiateGlobalVar(key, instance));
		}catch(Exception e){
			System.err.println("problem in JCL facade instantiateGlobalVar(String varName, Object instance)");
			return new JCLSFuture<Boolean>(false);
		}
	}

	//Create global Var
	@Override
	@Deprecated
	public Future<Boolean> instantiateGlobalVarAsy(Object key, String nickName,
			File[] jars, Object[] defaultVarValue) {
		try{
			//exec on orb
			return new JCLSFuture<Boolean>(orb.instantiateGlobalVar(key,nickName, jars, defaultVarValue));			
		}catch(Exception e){
			System.err.println("problem in JCL facade instantiateGlobalVar(String varName, File[] jars, Object[] defaultVarValue)");
			return new JCLSFuture<Boolean>(false);
		}
	}
	
	//Create global Var
	@Override
	@Deprecated
	public boolean instantiateGlobalVar(Object key, Object instance,
			String classVar, boolean Registers) {		
		try{
			//exec on orb
			return orb.instantiateGlobalVar(key, instance);
		}catch(Exception e){
			System.err.println("problem in JCL facade instantiateGlobalVar(String varName, Object instance)");
			return false;
		}
	}

	//Create global Var
	@Override
	@Deprecated
	public Future<Boolean> instantiateGlobalVarAsy(Object key, Object instance,
			String classVar, boolean Registers) {		
		try{
			//exec on orb
			return new JCLSFuture<Boolean>(orb.instantiateGlobalVar(key, instance));
		}catch(Exception e){
			System.err.println("problem in JCL facade instantiateGlobalVar(String varName, Object instance)");
			return new JCLSFuture<Boolean>(false);
		}
	}
	public static JCL_facade getInstance(){
		return Holder.getInstance();
	}	
	
	public static class Holder{
		
		public Holder() {
			if (instance == null){
				instance = new JCL_FacadeImpl(false,new GenericResource<JCL_task>());
			}
		}
		
		//Lock and get result
		protected JCL_result getResultBlocking(Long ID) {
			try{
				//lock waiting result
				join(ID);
				return results.get(ID);
				
			}catch (Exception e){
				System.err.println("problem in JCL facade getResultBlocking(String ID)");
				JCL_result jclr = new JCL_resultImpl();
				jclr.setErrorResult(e);			
				return jclr;
			}
		}
		
		//Get result
		protected JCL_result getResultUnblocking(Long ID){
			try{
				//get result
				return results.get(ID);
			}catch (Exception e){
				System.err.println("problem in JCL facade getResultUnblocking(String ID)");			
				JCL_result jclr = new JCL_resultImpl();
				jclr.setErrorResult(e);
				
				return jclr;
			}
		}
		
		protected JCL_result removeResult(Long ID){
			try{
				return results.remove(ID);
			}catch(Exception e){
				System.err.println("problem in JCL facade removeResult(String ID)");			
				JCL_result jclr = new JCL_resultImpl();
				jclr.setErrorResult(e);
				
				return jclr;
			}
		}
		
		//Wait
		private void join(long ID) {
			try{
				JCL_result jclr = results.get(ID);
				if((jclr.getCorrectResult()==null)&&(jclr.getErrorResult()==null)){				
					synchronized (jclr){
						//Necessary with use Lambari in parallel (racing condition)
						if((jclr.getCorrectResult()==null)&&(jclr.getErrorResult()==null)){
						jclr.wait();
						}
					}				
					join(ID);
				}
			}catch (Exception e){
				System.err.println("problem in JCL facade join ");			
			}		
		}
		
		protected static JCL_facade getInstance(){
			if (instance == null){
				instance = new JCL_FacadeImpl(false,new GenericResource<JCL_task>());
			}			
			return instance;
		} 
		
		public static JCL_facade getInstancePacu(GenericResource<JCL_task> re){
			if (instancePacu == null){
				((PacuResource)re).setInJCLLamb(numOfTasks, results);
				instancePacu = new JCL_FacadeImpl(true,re);
			}
			
			return instancePacu;
		} 
		
		protected boolean updateTicketH(long ticket,Object result){
			return updateTicket(ticket,result);	
		}
		
		protected String createTicketH(){
			return createTicket();
		}
	}

	@Override
	public List<Future<JCL_result>> executeAll(String objectNickname, Object[][] args) {
		try{
			List<Future<JCL_result>> tickets = new ArrayList<Future<JCL_result>>();
			tickets.add(this.execute(objectNickname, args[0]));
			return tickets;
		}catch (Exception e){
			System.err.println("JCL facade problem in execute (String objectNickname, Object... args)");
			e.printStackTrace();
			return new ArrayList<Future<JCL_result>>();
		}
	}

	@Override
	public List<Future<JCL_result>> executeAllCores(String objectNickname,
			String methodName, Object... args) {
		try{
			List<Future<JCL_result>> tickets = new ArrayList<Future<JCL_result>>();
			int core = JCL_Crawler.getCoreNumber();
			for(int i = 0; i < core; i++){
				tickets.add(this.execute(objectNickname,methodName, args));
			}
			return tickets;
		}catch (Exception e){
			System.err.println("JCL facade problem in execute (String objectNickname, Object... args)");
			e.printStackTrace();
			return new ArrayList<Future<JCL_result>>();
		}

	}

	@Override
	public List<Future<JCL_result>> executeAllCores(String objectNickname, Object... args) {
		try{
			List<Future<JCL_result>> tickets = new ArrayList<Future<JCL_result>>();
			int core = JCL_Crawler.getCoreNumber();
			for(int i = 0; i < core; i++){
				tickets.add(this.execute(objectNickname, args));
			}
			return tickets;
		}catch (Exception e){
			System.err.println("JCL facade problem in execute (String objectNickname, Object... args)");
			e.printStackTrace();
			return new ArrayList<Future<JCL_result>>();
		}
	}

	@Override
	public List<Future<JCL_result>> executeAllCores(String objectNickname, Object[][] args) {
		try{
			List<Future<JCL_result>> tickets = new ArrayList<Future<JCL_result>>();
			int core = JCL_Crawler.getCoreNumber();
			for(int i = 0; i < core; i++){
				tickets.add(this.execute(objectNickname, args[i]));
			}
			return tickets;
		}catch (Exception e){
			System.err.println("JCL facade problem in execute (String objectNickname, Object... args)");
			e.printStackTrace();
			return new ArrayList<Future<JCL_result>>();
		}
	}

	@Override
	public List<Future<JCL_result>> executeAllCores(String objectNickname,
			String methodName, Object[][] args) {
		try{
			List<Future<JCL_result>> tickets = new ArrayList<Future<JCL_result>>();
			int core = JCL_Crawler.getCoreNumber();
			for(int i = 0; i < core; i++){
				tickets.add(this.execute(objectNickname,methodName, args[i]));
			}
			return tickets;
		}catch (Exception e){
			System.err.println("JCL facade problem in execute (String objectNickname, Object... args)");
			e.printStackTrace();
			return new ArrayList<Future<JCL_result>>();
		}
	}

	@Override
	public List<Future<JCL_result>> executeAll(String className, String methodName,
			Object[][] args) {
		try{
			List<Future<JCL_result>> tickets = new ArrayList<Future<JCL_result>>();
			tickets.add(this.execute(className,methodName, args[0]));
			return tickets;
		}catch (Exception e){
			System.err.println("JCL facade problem in execute (String objectNickname, Object... args)");
			e.printStackTrace();
			return new ArrayList<Future<JCL_result>>();
		}
	}

	@Override
	public int getDeviceCore(Entry<String, String> device) {
		return JCL_Crawler.getCoreNumber();
	}

	@Override
	public Map<Entry<String, String>, Integer> getAllDevicesCores() {
		Map<Entry<String, String>, Integer> host = new HashMap<Entry<String, String>, Integer>();	
		host.put(new implementations.util.Entry<String, String>("localhost", "localhost"),JCL_Crawler.getCoreNumber());
		
		return host;
	}

	@Override
	public int getClusterCores() {
		return JCL_Crawler.getCoreNumber();
	}

	@Override
	public JCL_result removeResult(Future<JCL_result> ticket) {
		// TODO Auto-generated method stub
		try{
			return results.remove(((JCLFuture<JCL_result>)ticket).getTicket());
		}catch(Exception e){
			System.err.println("problem in JCL facade removeResult(Future<JCL_result> ticket)");			
			JCL_result jclr = new JCL_resultImpl();
			jclr.setErrorResult(e);
			
			return jclr;
		}
	}

	@Override
	public Long getDeviceTime() {
		// TODO Auto-generated method stub
		return System.nanoTime();
	}

	@Override
	public Long getSuperPeerTime() {
		// TODO Auto-generated method stub
		return System.nanoTime();
	}

	@Override
	public Map<String, String> getDeviceMetadata(Entry<String, String> deviceNickname) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean setDeviceMetadata(Entry<String, String> deviceNickname, Map<String, String> metadata) {
		// TODO Auto-generated method stub
		return false;
	}

//	@Override
//	public List<Long> getTaskTimes(String ID) {
//		// TODO Auto-generated method stub
//		return results.get(ID).getTime();
//	}
}
