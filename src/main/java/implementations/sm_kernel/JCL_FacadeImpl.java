package implementations.sm_kernel;

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
	public String execute(String className, String methodName, Object... args) {
		
		//create ticket
		Long ticket = numOfTasks.getAndIncrement();	
		
		try{
			//create task			
			JCL_task t = new JCL_taskImpl(ticket, className, methodName, args);
			JCL_result jclr = new JCL_resultImpl();	
			jclr.setTime(t.getTaskTime());
			results.put(ticket, jclr);			
			r.putRegister(t);
			
			return ticket.toString();
			
		}catch (Exception e){
			System.err.println("JCL facade problem in execute(String className, String methodName, Object... args)");			
			return String.valueOf(-1);
		}	
	}
	
	//execute with JCL_taskImpl as arg
	@Override
	public String execute(JCL_task task) {
		
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
			
			return ticket.toString();
			
		}catch (Exception e){
			System.err.println("JCL facade problem in execute(String className, String methodName, Object... args)");			
			return String.valueOf(-1);
		}	
	}
	
	//execute method execute
	@Override
	public String execute(String objectNickname, Object... args){
				
		//Create ticket
		Long ticket = numOfTasks.getAndIncrement();
		
		try{
			JCL_task t = new JCL_taskImpl(ticket, objectNickname, args);
			t.setTaskTime(System.nanoTime());			
			JCL_result jclr = new JCL_resultImpl();	
			jclr.setTime(t.getTaskTime());
			results.put(ticket, jclr);			
			r.putRegister(t);
			
			return ticket.toString();
		}catch (Exception e){
			System.err.println("JCL facade problem in execute (String objectNickname, Object... args)");
			e.printStackTrace();
			return String.valueOf(-1);
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
	
	//Lock and get result
	@Override
	public JCL_result getResultBlocking(String ID) {
		try{
			//lock waiting result
			join(Long.parseLong(ID,10));
			return results.get(Long.parseLong(ID));
			
		}catch (Exception e){
			System.err.println("problem in JCL facade getResultBlocking(String ID)");
			JCL_result jclr = new JCL_resultImpl();
			jclr.setErrorResult(e);			
			return jclr;
		}
	}
	
	//Lock and get result
	@Override
	public JCL_result getResultBlocking(Long ID) {
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
	@Override
	public JCL_result getResultUnblocking(String ID){
		try{
			//get result
			return results.get(Long.parseLong(ID));
		}catch (Exception e){
			System.err.println("problem in JCL facade getResultUnblocking(String ID)");			
			JCL_result jclr = new JCL_resultImpl();
			jclr.setErrorResult(e);
			
			return jclr;
		}
	}
	
	//Get result
	@Override
	public JCL_result getResultUnblocking(Long ID){
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
	
	//Remove result
	@Override
	public JCL_result removeResult(String ID){
		try{
			return results.remove(Long.parseLong(ID));
		}catch(Exception e){
			System.err.println("problem in JCL facade removeResult(String ID)");			
			JCL_result jclr = new JCL_resultImpl();
			jclr.setErrorResult(e);
			
			return jclr;
		}
	}
	
	//Remove result
	@Override
	public JCL_result removeResult(Long ID){
		try{
			return results.remove(ID);
		}catch(Exception e){
			System.err.println("problem in JCL facade removeResult(String ID)");			
			JCL_result jclr = new JCL_resultImpl();
			jclr.setErrorResult(e);
			
			return jclr;
		}
	}
	
	//Remove global Var
	@Override
	public boolean destroyGlobalVar(Object key) {
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
	public List<JCL_result> getAllResultBlocking(List<String> ID) {
		try{
			// return list of results
			List<JCL_result> result = new ArrayList<JCL_result>(ID.size());
			
			for(String t: ID){
				join(Long.parseLong(t,10));
				result.add(results.get(Long.parseLong(t)));
			}
			
			return result;
			
		}catch (Exception e){
			
			System.err.println("problem in JCL facade getAllResultBlocking(List<String> ID)");
			
			return null;
		}
	}

	//Get all result
	@Override
	public List<JCL_result> getAllResultUnblocking(List<String> ID) {
		try{
			// return list of results
			List<JCL_result> result = new ArrayList<JCL_result>(ID.size());
			
			for(String t: ID) {
				result.add(results.get(t));
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
	public List<String> executeAll(String objectNickname, Object... args) {
			//Create ticket
				Long ticket = numOfTasks.getAndIncrement();
				
				try{
					List<String> tickets = new ArrayList<String>();
					tickets.add(ticket.toString());
					JCL_task t = new JCL_taskImpl(ticket, objectNickname, args);
					t.setTaskTime(System.nanoTime());			
					JCL_result jclr = new JCL_resultImpl();	
					jclr.setTime(t.getTaskTime());
					results.put(ticket, jclr);			
					r.putRegister(t);
					
					return tickets;
				}catch (Exception e){
					System.err.println("JCL facade problem in execute (String objectNickname, Object... args)");
					e.printStackTrace();
					return new ArrayList<String>();
				}
	}

	//Execute OnHost just Pacu. Lambari execute on localhost
	@Override
	@Deprecated
	public String executeOnHost(Entry<String, String> device, String objectNickname,
			Object... args) {
		//Create ticket
		Long ticket = numOfTasks.getAndIncrement();
		
		try{
			JCL_task t = new JCL_taskImpl(ticket, objectNickname, args);
			t.setTaskTime(System.nanoTime());			
			JCL_result jclr = new JCL_resultImpl();	
			jclr.setTime(t.getTaskTime());
			results.put(ticket, jclr);			
			r.putRegister(t);
			
			return ticket.toString();
		}catch (Exception e){
			System.err.println("JCL facade problem in execute (String objectNickname, Object... args)");
			e.printStackTrace();
			return String.valueOf(-1);
		}		
	}
	
	//Execute OnHost just Pacu. Lambari execute on localhost
	@Override
	@Deprecated
	public String executeOnHost(String host, String objectNickname,
			Object... args) {
		//Create ticket
		Long ticket = numOfTasks.getAndIncrement();
		
		try{
			JCL_task t = new JCL_taskImpl(ticket, objectNickname, args);
			t.setTaskTime(System.nanoTime());			
			JCL_result jclr = new JCL_resultImpl();	
			jclr.setTime(t.getTaskTime());
			results.put(ticket, jclr);			
			r.putRegister(t);
			
			return ticket.toString();
		}catch (Exception e){
			System.err.println("JCL facade problem in execute (String objectNickname, Object... args)");
			e.printStackTrace();
			return String.valueOf(-1);
		}		
	}

	//Execute All just Pacu. Lambari execute on localhost
	@Override
	@Deprecated
	public List<String> executeAll(String className, String methodName,
			Object... args) {
		//create ticket
				Long ticket = numOfTasks.getAndIncrement();	
				
				try{
					//create task	
					List<String> tickets = new ArrayList<String>();
					tickets.add(ticket.toString());
					JCL_task t = new JCL_taskImpl(ticket, className, methodName, args);
					JCL_result jclr = new JCL_resultImpl();	
					jclr.setTime(t.getTaskTime());
					results.put(ticket, jclr);			
					r.putRegister(t);
					
					return tickets;
					
				}catch (Exception e){
					System.err.println("JCL facade problem in execute(String className, String methodName, Object... args)");			
					return new ArrayList<String>();
				}
	}

	//Execute OnHost just Pacu. Lambari execute on localhost
	@Override
	@Deprecated
	public String executeOnHost(Entry<String, String> device, String className,
			String methodName, Object... args){
		//create ticket
				Long ticket = numOfTasks.getAndIncrement();	
				
				try{
					//create task			
					JCL_task t = new JCL_taskImpl(ticket, className, methodName, args);
					JCL_result jclr = new JCL_resultImpl();
					jclr.setTime(t.getTaskTime());
					results.put(ticket, jclr);			
					r.putRegister(t);
					
					return ticket.toString();
					
				}catch (Exception e){
					System.err.println("JCL facade problem in execute(String className, String methodName, Object... args)");			
					return String.valueOf(-1);
				}
	}

	//Execute OnHost just Pacu. Lambari execute on localhost
	@Override
	@Deprecated
	public String executeOnHost(String host, String className,
			String methodName, Object... args) {
		//create ticket
				Long ticket = numOfTasks.getAndIncrement();	
				
				try{
					//create task			
					JCL_task t = new JCL_taskImpl(ticket, className, methodName, args);
					JCL_result jclr = new JCL_resultImpl();
					jclr.setTime(t.getTaskTime());
					results.put(ticket, jclr);			
					r.putRegister(t);
					
					return ticket.toString();
					
				}catch (Exception e){
					System.err.println("JCL facade problem in execute(String className, String methodName, Object... args)");			
					return String.valueOf(-1);
				}
	}

	//Return localHost Lambari.
	@Override
	@Deprecated
	public List<String> getHosts() {
		try {
			List<String> host = new ArrayList<String>();			
			host.add("localhost");
			return host;
			
		} catch (Exception e) {

			System.err.println("cannot use this method with JCL distributed");

			return null;
		}
	}

	@Override
	@Deprecated
	public Object instantiateGlobalVarOnHost(String host, String nickname,
			Object varName, File[] jars, Object[] defaultVarValue) {
		try{
			//exec on orb
			return orb.instantiateGlobalVar(host,nickname, jars, defaultVarValue);
		}catch(Exception e){
			System.err.println("problem in JCL facade instantiateGlobalVar(String varName, File[] jars, Object[] defaultVarValue)");
			return false;
		}
	}
	
	//Create global var on local host
	@Override
	@Deprecated
	public boolean instantiateGlobalVarOnHost(String host, Object key,
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
	@Override
	@Deprecated
	public boolean insertHost(String mac, String ip, String port) {
		try {
			return false;
		} catch (Exception e) {
			return false;
		}
	}

	//Always return false
	@Override
	@Deprecated
	public boolean removeHost(String mac, String ip, String port) {
		try {
			return false;
		} catch (Exception e) {
			return false;
		}
	}
	
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
	public List<String> executeAll(String objectNickname, Object[][] args) {
		try{
			List<String> tickets = new ArrayList<String>();
			tickets.add(this.execute(objectNickname, args[0]));
			return tickets;
		}catch (Exception e){
			System.err.println("JCL facade problem in execute (String objectNickname, Object... args)");
			e.printStackTrace();
			return new ArrayList<String>();
		}
	}

	@Override
	public List<String> executeAllCores(String objectNickname,
			String methodName, Object... args) {
		try{
			List<String> tickets = new ArrayList<String>();
			int core = JCL_Crawler.getCoreNumber();
			for(int i = 0; i < core; i++){
				tickets.add(this.execute(objectNickname,methodName, args));
			}
			return tickets;
		}catch (Exception e){
			System.err.println("JCL facade problem in execute (String objectNickname, Object... args)");
			e.printStackTrace();
			return new ArrayList<String>();
		}

	}

	@Override
	public List<String> executeAllCores(String objectNickname, Object... args) {
		try{
			List<String> tickets = new ArrayList<String>();
			int core = JCL_Crawler.getCoreNumber();
			for(int i = 0; i < core; i++){
				tickets.add(this.execute(objectNickname, args));
			}
			return tickets;
		}catch (Exception e){
			System.err.println("JCL facade problem in execute (String objectNickname, Object... args)");
			e.printStackTrace();
			return new ArrayList<String>();
		}
	}

	@Override
	public List<String> executeAllCores(String objectNickname, Object[][] args) {
		try{
			List<String> tickets = new ArrayList<String>();
			int core = JCL_Crawler.getCoreNumber();
			for(int i = 0; i < core; i++){
				tickets.add(this.execute(objectNickname, args[i]));
			}
			return tickets;
		}catch (Exception e){
			System.err.println("JCL facade problem in execute (String objectNickname, Object... args)");
			e.printStackTrace();
			return new ArrayList<String>();
		}
	}

	@Override
	public List<String> executeAllCores(String objectNickname,
			String methodName, Object[][] args) {
		try{
			List<String> tickets = new ArrayList<String>();
			int core = JCL_Crawler.getCoreNumber();
			for(int i = 0; i < core; i++){
				tickets.add(this.execute(objectNickname,methodName, args[i]));
			}
			return tickets;
		}catch (Exception e){
			System.err.println("JCL facade problem in execute (String objectNickname, Object... args)");
			e.printStackTrace();
			return new ArrayList<String>();
		}
	}

	@Override
	public List<String> executeAll(String className, String methodName,
			Object[][] args) {
		try{
			List<String> tickets = new ArrayList<String>();
			tickets.add(this.execute(className,methodName, args[0]));
			return tickets;
		}catch (Exception e){
			System.err.println("JCL facade problem in execute (String objectNickname, Object... args)");
			e.printStackTrace();
			return new ArrayList<String>();
		}
	}

	@Override
	public int getHostCore(String HostID) {
		return JCL_Crawler.getCoreNumber();
	}

	@Override
	public Map<String, Integer> getAllHostCores() {
		Map<String, Integer> host = new HashMap<String, Integer>();	
		host.put("localhost",JCL_Crawler.getCoreNumber());
		
		return host;
	}

	@Override
	public int getClusterCores() {
		return JCL_Crawler.getCoreNumber();
	}

	@Override
	public List<Long> getTaskTimes(String ID) {
		// TODO Auto-generated method stub
		return results.get(ID).getTime();
	}
}
