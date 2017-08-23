package implementations.dm_kernel.user;

import implementations.dm_kernel.ConnectorImpl;
import implementations.dm_kernel.MessageCommonsImpl;
import implementations.dm_kernel.MessageControlImpl;
import implementations.dm_kernel.MessageGenericImpl;
import implementations.dm_kernel.MessageGlobalVarImpl;
import implementations.dm_kernel.MessageGlobalVarObjImpl;
import implementations.dm_kernel.MessageImpl;
import implementations.dm_kernel.MessageLongImpl;
import implementations.dm_kernel.MessageTaskImpl;
import implementations.util.IoT.CryptographyUtils;
import interfaces.kernel.JCL_connector;
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
import interfaces.kernel.JCL_result;
import interfaces.kernel.JCL_task;
import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import commom.JCL_resultImpl;
import commom.JCL_taskImpl;

public class JCL_FacadeImplLamb extends implementations.sm_kernel.JCL_FacadeImpl.Holder{
	
	public static int port;
	public static final ConcurrentHashMap<Long, List<Long>> taskTimes = new ConcurrentHashMap<Long, List<Long>>();
	public static final ConcurrentHashMap<Long, Long> taskMemory = new ConcurrentHashMap<Long, Long>();

	
	public ConcurrentHashMap<Long, List<Long>> getTaskTimes() {
		try {
			return taskTimes;			
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			System.err.println("problem in JCL facade Lamb ConcurrentHashMap<Long, List<Long>> getTaskTimes()");
			return null;
		}
	}
	
	public ConcurrentHashMap<Long,Long> getTaskMemory() {
		try {
			return taskMemory;			
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			System.err.println("problem in JCL facade Lamb ConcurrentHashMap<Long, Long> getTaskMemory()");
			return null;
		}
	}
	
	public Boolean binexecutetask(String host,String port,String mac, String portS, JCL_message_list_task msgTask){
		
		JCL_connector taskConnector = new ConnectorImpl();
		taskConnector.connect(host, Integer.parseInt(port),mac);
		
		//Type execute
		msgTask.setType(25);
		msgTask.setHostPort(host, Integer.parseInt(port));
		
		
		Map<Long, JCL_task> tList = msgTask.getMapTask();
		for(Entry<Long, JCL_task> ent:tList.entrySet()){
			 ent.getValue().setTaskTime(System.nanoTime());
		}
				
		//Send msg
		JCL_message_result msgResult = taskConnector.sendReceive(msgTask,portS);
		Map<Long,Long> tickets = (Map<Long,Long>) msgResult.getResult().getCorrectResult();
		taskConnector.disconnect();
		
		for(Entry<Long, Long> inst:tickets.entrySet()){
			super.updateTicketH(inst.getKey(), new Object[]{inst.getValue(),host,port,mac,portS});			
		}
				
		return true;
	}
	
	public Boolean register(String host,String port, String mac,String portS,JCL_message_register classReg){
		try {
			
			JCL_connector taskConnector = new ConnectorImpl();
			taskConnector.connect(host, Integer.parseInt(port),mac);
			JCL_result result = taskConnector.sendReceive(classReg,portS).getResult();

			return ((Boolean) result.getCorrectResult()).booleanValue();

		} catch (Exception e) {

			System.err
					.println("problem in JCL facade register(File f, String classToBeExecuted)");
			e.printStackTrace();
			return false;
		}
	}
	
	public Boolean containsTask(String host,String port, String mac,String portS, String classreg){
		try {
			
			JCL_connector taskConnector = new ConnectorImpl();
			taskConnector.connect(host, Integer.parseInt(port),mac);
			JCL_message_control msg = new MessageControlImpl();
			msg.setType(18);
			msg.setRegisterData(classreg);
			JCL_message_control resp = (MessageControlImpl)taskConnector.sendReceiveG(msg,portS); 
			

			return (Boolean.parseBoolean(resp.getRegisterData()[0]));

		} catch (Exception e) {

			System.err
					.println("problem in JCL facade containsTask(File f, String classToBeExecuted)");
			e.printStackTrace();
			return false;
		}
	}

	
	public Map<String, String> registerByServer(String host,String port, String key,String value, String classReg){
		try {
			JCL_message_control mc = new MessageControlImpl();
			if(key!=null){
			mc.setRegisterData(classReg,key,value);
			}else mc.setRegisterData(classReg);
			mc.setType(4);
			
			JCL_connector taskConnector = new ConnectorImpl();
			taskConnector.connect(host, Integer.parseInt(port),null);
			JCL_message_generic result = (JCL_message_generic) taskConnector.sendReceiveG(mc,null);

			
			return (Map<String, String>) result.getRegisterData();

		} catch (Exception e) {

			System.err
					.println("problem in JCL facade registerByServer(String host,String port, String mac, String classReg)");
			e.printStackTrace();
			return null;
		}
	}
	
	public Boolean unRegister(String nickName, String host, String port, String mac, String portS){
		
		try {
			//Unregister on Host
			JCL_connector taskConnector = new ConnectorImpl();
			taskConnector.connect(host, Integer.parseInt(port),mac);
			JCL_message_commons msgUn = new MessageCommonsImpl();
			msgUn.setType(2);
			msgUn.setRegisterData(nickName);
			JCL_message_result msgRes = taskConnector.sendReceive(msgUn,portS);
			taskConnector.disconnect();
			return (Boolean) msgRes.getResult().getCorrectResult();
		} catch (Exception e) {

			System.err
					.println("problem in JCL facade unRegister(String nickName, String host, String port)");
			e.printStackTrace();
			return false;
		}
	}
/*	
	public boolean unRegister(String nickName) {
		try {
			JCL_message_control mc = new MessageControlImpl();

			mc.setRegisterData(nickName);
			// calling unregister service to a specific user
			// the unregister service is for unregistering jar files
			mc.setType(2);
			JCL_connector controlConnector = new ConnectorImpl();
			controlConnector.connect(serverAdd, serverPort);
			JCL_message_control mr = controlConnector.sendReceive(mc);
			controlConnector.disconnect();
			if (mr.getRegisterData()[0] .equals("true")) {
				return true;
			} else
				return false;
		} catch (Exception e) {
			System.err.println("JCL problem in unregister method");
			e.printStackTrace();

			return false;
		}

	}

*/	

	
	public Object[] executeAndRegister(String objectNickname,String host,String port, String mac, String portS,JCL_message_register classReg,boolean hostChange, Object... args) {
		try {
				//Register jar				
				JCL_connector taskConnector = new ConnectorImpl();
				taskConnector.connect(host, Integer.parseInt(port),mac);
				JCL_result result = taskConnector.sendReceive(classReg,portS).getResult();

				
				if (((Boolean) result.getCorrectResult()).booleanValue()){

					//Create msg
					JCL_task t = new JCL_taskImpl(null, objectNickname, args);					
					t.setHostChange(hostChange);
					t.setPort(this.port);
					MessageTaskImpl msgTask = new MessageTaskImpl();
					msgTask.setTask(t);
					//Type execute
					msgTask.setType(4);
					
					//Send msg
					t.setTaskTime(System.nanoTime());
					JCL_message_result msgResult = taskConnector.sendReceive(msgTask,portS);
					long ticket = (Long) msgResult.getResult().getCorrectResult();
					taskConnector.disconnect();
					
					return new Object[]{ticket,host,port,mac,portS};
				
				}else{
					System.err.println("Register Erro!!!");
					return null;
				}

		} catch (Exception e) {
			System.err
					.println("JCL facade problem in execute(String className, Object... args)");
					e.printStackTrace();
			return null;
		}
	}
	
	public Object[] executeAndRegisterI(String objectNickname,String host,String port, String mac, String portS,JCL_message_register classReg,boolean hostChange, Object... args) {
		try {
				//Register jar				
				JCL_connector taskConnector = new ConnectorImpl();
				taskConnector.connect(host, Integer.parseInt(port),mac);
				JCL_result result = taskConnector.sendReceive(classReg,portS).getResult();

				if (((Boolean) result.getCorrectResult()).booleanValue()){

					//Create msg
					JCL_task t = new JCL_taskImpl(null, objectNickname, args);					
					t.setHostChange(hostChange);
					t.setPort(this.port);
					MessageTaskImpl msgTask = new MessageTaskImpl();
					msgTask.setTask(t);
					//Type execute
					msgTask.setType(40);
					
					//Send msg
					t.setTaskTime(System.nanoTime());
					JCL_message_result msgResult = taskConnector.sendReceive(msgTask,portS);
					long ticket = (Long) msgResult.getResult().getCorrectResult();
					taskConnector.disconnect();
					
					return new Object[]{ticket,host,port,mac,portS};
				
				}else{
					System.err.println("Register Erro!!!");
					return null;
				}

		} catch (Exception e) {
			System.err
					.println("JCL facade problem in execute(String className, Object... args)");
					e.printStackTrace();
			return null;
		}
	}
	
	public Object[] executeAndRegister(JCL_task task,String host,String port, String mac, String portS,JCL_message_register classReg, boolean hostChange) {
		try {
				
				
				//Register jar				
				JCL_connector taskConnector = new ConnectorImpl();
				taskConnector.connect(host, Integer.parseInt(port),mac);
				JCL_result result = taskConnector.sendReceive(classReg,portS).getResult();


				if (((Boolean) result.getCorrectResult()).booleanValue()){
				
					
					//Create msg					
					task.setHostChange(hostChange);
					MessageTaskImpl msgTask = new MessageTaskImpl();
					task.setPort(this.port);
					msgTask.setTask(task);
					//Type execute
					msgTask.setType(4);
					
					//Send msg
					task.setTaskTime(System.nanoTime());
					JCL_message_result msgResult = taskConnector.sendReceive(msgTask,portS);
					long ticket = (Long) msgResult.getResult().getCorrectResult();
					taskConnector.disconnect();
					
					return new Object[]{ticket,host,port,mac,portS};
				
				}else{
					System.err.println("Register Erro!!!");
					return null;
				}

		} catch (Exception e) {
			System.err
					.println("JCL facade problem in execute(String className, Object... args)");
					e.printStackTrace();
			return null;
		}
	}


	public Object[] executeAndRegister(String objectNickname,String methodName,String host,String port, String mac, String portS,JCL_message_register classReg,boolean hostChange, Object... args) {
		try {
				
				//Register jar
				JCL_connector taskConnector = new ConnectorImpl();
				taskConnector.connect(host, Integer.parseInt(port),mac);
				JCL_result result = taskConnector.sendReceive(classReg,portS).getResult();
				
			
				if (((Boolean) result.getCorrectResult()).booleanValue()){
					//Create msg
					JCL_task t = new JCL_taskImpl(null, objectNickname, methodName, args);					
					t.setHostChange(hostChange);
					MessageTaskImpl msgTask = new MessageTaskImpl();
					t.setPort(this.port);
					msgTask.setTask(t);
					//Type execute
					msgTask.setType(5);
					
					//Send exec msg
					t.setTaskTime(System.nanoTime());
					JCL_message_result msgResult = taskConnector.sendReceive(msgTask,portS);
					long ticket = (Long) msgResult.getResult().getCorrectResult();
					taskConnector.disconnect();
					return new Object[]{ticket,host,port,mac,portS};
				}else{
					System.err.println("Register Erro!!!");
					return null;
				}

		} catch (Exception e) {
			System.err
					.println("JCL facade problem in execute(String className, Object... args)");
					e.printStackTrace();
			return null;
		}
	}
	
//	public Object[] executeAndRegisterI(String objectNickname,String methodName,String host,String port, String mac,JCL_message_register classReg,boolean hostChange, Object... args) {
//		try {
//				
//				
//				//Register jar
//				JCL_connector taskConnector = new ConnectorImpl();
//				taskConnector.connect(host, Integer.parseInt(port),mac);
//				JCL_result result = taskConnector.sendReceive(classReg,null).getResult();
//				
//				if (((Boolean) result.getCorrectResult()).booleanValue()){
//					//Create msg
//					JCL_task t = new JCL_taskImpl(null, objectNickname, methodName, args);					
//					t.setHostChange(hostChange);
//					MessageTaskImpl msgTask = new MessageTaskImpl();
//					t.setPort(this.port);
//					msgTask.setTask(t);
//					//Type execute
//					msgTask.setType(41);
//					
//					//Send exec msg
//					t.setTaskTime(System.nanoTime());
//					JCL_message_result msgResult = taskConnector.sendReceive(msgTask,null);
//					long ticket = (Long) msgResult.getResult().getCorrectResult();
//					taskConnector.disconnect();
//					
//					return new Object[]{ticket,host,port,mac};
//				}else{
//					System.err.println("Register Erro!!!");
//					return null;
//				}
//
//		} catch (Exception e) {
//			System.err
//					.println("JCL facade problem in execute(String className, Object... args)");
//					e.printStackTrace();
//			return null;
//		}
//	}

	public Object[] execute(String objectNickname,String host,String port, String mac, String portS,boolean hostChange, Object... args) {
		try {		
			
				//Create msg
				JCL_task t = new JCL_taskImpl(null, objectNickname, args);				
				t.setHostChange(hostChange);
				MessageTaskImpl msgTask = new MessageTaskImpl();
				t.setPort(this.port);
				msgTask.setTask(t);
				//Type execute
				msgTask.setType(4);
					
				//Send msg
				JCL_connector taskConnector = new ConnectorImpl();
				taskConnector.connect(host, Integer.parseInt(port),mac);	
				t.setTaskTime(System.nanoTime());
								
				JCL_message_result msgResult = taskConnector.sendReceive(msgTask,portS);
				long ticket = (Long) msgResult.getResult().getCorrectResult();
				taskConnector.disconnect();
					
				return new Object[]{ticket,host,port,mac,portS};

		} catch (Exception e) {
			System.err
					.println("JCL facade problem in execute(String className, Object... args)");
					e.printStackTrace();
			return null;
		}
	}
	
//	public Object[] executeI(String objectNickname,String host,String port, String mac,boolean hostChange, Object... args) {
//		try {		
//			
//				//Create msg
//				JCL_task t = new JCL_taskImpl(null, objectNickname, args);				
//				t.setHostChange(hostChange);
//				MessageTaskImpl msgTask = new MessageTaskImpl();
//				t.setPort(this.port);
//				msgTask.setTask(t);
//				//Type execute
//				msgTask.setType(40);
//					
//				//Send msg
//				JCL_connector taskConnector = new ConnectorImpl();
//				taskConnector.connect(host, Integer.parseInt(port),mac);	
//				t.setTaskTime(System.nanoTime());
//								
//				JCL_message_result msgResult = taskConnector.sendReceive(msgTask,null);
//				long ticket = (Long) msgResult.getResult().getCorrectResult();
//				taskConnector.disconnect();
//					
//				return new Object[]{ticket,host,port,mac};
//
//		} catch (Exception e) {
//			System.err
//					.println("JCL facade problem in execute(String className, Object... args)");
//					e.printStackTrace();
//			return null;
//		}
//	}
	
	
	public Object[] execute(JCL_task task,String host,String port, String mac, String portS,boolean hostChange) {
		try {		

				//Create msg				
				MessageTaskImpl msgTask = new MessageTaskImpl();
				task.setPort(this.port);
				task.setHostChange(hostChange);
				msgTask.setTask(task);
				//Type execute
				msgTask.setType(4);
					
				//Send msg
				JCL_connector taskConnector = new ConnectorImpl();
				taskConnector.connect(host, Integer.parseInt(port),mac);
				task.setTaskTime(System.nanoTime());
				
				
				JCL_message_result msgResult = taskConnector.sendReceive(msgTask,portS);
				long ticket = (Long) msgResult.getResult().getCorrectResult();
				taskConnector.disconnect();
					
				return new Object[]{ticket,host,port,mac,portS};

		} catch (Exception e) {
			System.err
					.println("JCL facade problem in execute(String className, Object... args)");
					e.printStackTrace();
			return null;
		}
	}
	

	public Object[] execute(String objectNickname, String methodName,
			String host,String port, String mac, String portS,boolean hostChange,Object... args) {

		try {
				//Create msg
				JCL_task t = new JCL_taskImpl(null, objectNickname, methodName, args);				
				t.setHostChange(hostChange);
				MessageTaskImpl msgTask = new MessageTaskImpl();
				t.setPort(this.port);
				msgTask.setTask(t);
				//Type execute
				msgTask.setType(5);
								
				//Send msg
				JCL_connector taskConnector = new ConnectorImpl();
				taskConnector.connect(host, Integer.parseInt(port),mac);	
				t.setTaskTime(System.nanoTime());
								
				JCL_message_result msgResult = taskConnector.sendReceive(msgTask,portS);
				long ticket = (Long) msgResult.getResult().getCorrectResult();
				taskConnector.disconnect();	

				return new Object[]{ticket,host,port,mac,portS};			

		} catch (Exception e) {
			System.err
					.println("JCL facade problem in execute(String className, String methodName, Object... args)");

			return null;
		}
	}
	
	public Object[] executeI(String objectNickname, String methodName,
			String host,String port, String mac, String portS,boolean hostChange,Object... args) {

		try {
				
				//Create msg
				JCL_task t = new JCL_taskImpl(null, objectNickname, methodName, args);				
				t.setHostChange(hostChange);
				MessageTaskImpl msgTask = new MessageTaskImpl();
				t.setPort(this.port);
				msgTask.setTask(t);
				//Type execute
				msgTask.setType(41);
								
				//Send msg
				JCL_connector taskConnector = new ConnectorImpl();
				taskConnector.connect(host, Integer.parseInt(port),mac);	
				t.setTaskTime(System.nanoTime());
								
				JCL_message_result msgResult = taskConnector.sendReceive(msgTask,portS);
				long ticket = (Long) msgResult.getResult().getCorrectResult();
				taskConnector.disconnect();	

				return new Object[]{ticket,host,port,mac,portS};			

		} catch (Exception e) {
			System.err
					.println("JCL facade problem in execute(String className, String methodName, Object... args)");

			return null;
		}
	}
	
	public Object getResultBlocking(long IDLamb, long ID, String Host, String Port, String mac, String portS) {
		try {				
				
				//Create msg
				JCL_message_long mc = new MessageLongImpl();
				mc.setRegisterData(ID);
				// calling get result from a host in JCL
				mc.setType(6);
				//End msg
				
				//Connection 
				JCL_connector taskConnector = new ConnectorImpl();
				taskConnector.connect(Host,Integer.parseInt(Port),mac);
				JCL_message msgResult = taskConnector.sendReceiveG(mc,portS);
				taskConnector.disconnect();
				//Connection
				
				
				//Collaborative schedule
				if (msgResult instanceof JCL_message_result){
					JCL_result result = ((JCL_message_result)msgResult).getResult(); 
					result.addTime(System.nanoTime());
					taskTimes.put(IDLamb, result.getTime());
					taskMemory.put(IDLamb, result.getMemorysize());
					return result.getCorrectResult();
				} else{	
					
					//Get new Host
					String[] hostPortID = ((JCL_message_control)msgResult).getRegisterData()[0].split("¬");
		  		  	Host = hostPortID[0];
		  		  	Port = hostPortID[1];
		  		  	String newID = hostPortID[2];
		  		  	String macN = hostPortID[3];
		  		  	String portSN = hostPortID[4];
		  		  	
		  		  
					//Update Host Location
		  		  	super.updateTicketH(IDLamb, new Object[]{newID,Host,Port,macN,portSN});	
		  		  	mc.setRegisterData(Long.parseLong(newID));

					//Call for result		  		  
					taskConnector.connect(Host,Integer.parseInt(Port),macN);
					JCL_message_result msgResultF = taskConnector.sendReceive(mc,portSN);
					taskConnector.disconnect();
					JCL_result result = msgResultF.getResult(); 
					result.addTime(System.nanoTime());
					taskTimes.put(IDLamb, result.getTime());
					taskMemory.put(IDLamb, result.getMemorysize());
					
					return result.getCorrectResult();					
				}
				
		} catch (Exception e) {
			System.err
					.println("problem in JCL facade getResultBlocking(String ID)");
			e.printStackTrace();
			return null;
		}
	}
	
	
	public Object getResultUnblocking(long IDLamb, long ID,String Host, String Port, String mac, String portS) {
		try {
				JCL_message_long mc = new MessageLongImpl();
				mc.setRegisterData(ID);
				// calling get result unblocking from a host in JCL
				mc.setType(7);

				JCL_connector taskConnector = new ConnectorImpl();
				taskConnector.connect(Host,Integer.parseInt(Port),mac);
				JCL_message msgResult = taskConnector.sendReceiveG(mc,portS);
				taskConnector.disconnect();

				Object result;
				//Collaborative schedule
				if (msgResult instanceof JCL_message_result){
					JCL_result resultI = ((JCL_message_result)msgResult).getResult();
					resultI.addTime(System.nanoTime());
					taskTimes.put(IDLamb, resultI.getTime());
					taskMemory.put(IDLamb, resultI.getMemorysize());

					
					result = resultI.getCorrectResult();
				
				} else{					
					String[] hostPortID = ((JCL_message_control)msgResult).getRegisterData()[0].split("¬");
		  		  	Host = hostPortID[0];
		  		  	Port = hostPortID[1];
		  		  	String newID = hostPortID[2];
		  		  	String macN = hostPortID[3];
		  		  	String portSN = hostPortID[4];
		  		  	
					//Update Host Location
		  		  	super.updateTicketH(IDLamb, new Object[]{newID,Host,Port,macN,portSN});	
		  		  	mc.setRegisterData(Long.parseLong(newID));			

					taskConnector.connect(Host,Integer.parseInt(Port),macN);
					JCL_message_result msgResultF = taskConnector.sendReceive(mc,portSN);
					taskConnector.disconnect();
					JCL_result resultI = msgResultF.getResult();
					resultI.addTime(System.nanoTime());
					taskTimes.put(IDLamb, resultI.getTime());
					taskMemory.put(IDLamb, resultI.getMemorysize());

					
					result = resultI.getCorrectResult();
				}			
				
				if (result == null){
					result = new String("NULL");
				}
				
				return result;

		} catch (Exception e){
			System.err
					.println("problem in JCL facade getResultBlocking(String ID)");
			JCL_result jclr = new JCL_resultImpl();
			jclr.setErrorResult(e);

			return jclr;
		}
	}
	
	public Object removeResult(long IDLamb, long ID,String Host, String Port, String mac, String portS) {
		try {			
				JCL_message_long mc = new MessageLongImpl();
				mc.setRegisterData(ID);
				// calling remove result from a host in JCL
				mc.setType(8);

				JCL_connector taskConnector = new ConnectorImpl();
				taskConnector.connect(Host,Integer.parseInt(Port),mac);
				JCL_message msgResult = taskConnector.sendReceiveG(mc,portS);
				taskConnector.disconnect();
				
				//Collaborative schedule
				if (msgResult instanceof JCL_message_result){ 				

					return ((JCL_message_result)msgResult).getResult().getCorrectResult();

				}else{
					
					String[] hostPortID = ((JCL_message_control)msgResult).getRegisterData()[0].split("¬");
		  		  	Host = hostPortID[0];
		  		  	Port = hostPortID[1];
		  		  	String newID = hostPortID[2];
		  		  	String macN = hostPortID[3];
		  		  	String portSN = hostPortID[4];

		  		  			  		  	
		  		  	//Update Host Location
		  		  	super.updateTicketH(IDLamb, new Object[]{ID,Host,Port,macN,portSN});
		  		  	
		  		  	mc = new MessageLongImpl();
					mc.setRegisterData(ID);
					// calling remove result from a host in JCL
					mc.setType(8);

					taskConnector.connect(Host,Integer.parseInt(Port),macN);
					msgResult = taskConnector.sendReceive(mc,portSN);
					taskConnector.disconnect();

					return ((JCL_message_result)msgResult).getResult().getCorrectResult();
				}

		} catch (Exception e) {
			System.err
					.println("problem in JCL facade removeResult(String ID)");
			JCL_result jclr = new JCL_resultImpl();
			jclr.setErrorResult(e);
			e.printStackTrace();

			return jclr;
		}
	}

	//inst global variable with jar
	public boolean instantiateGlobalVar(Object key,String nickName, Object[] defaultVarValue,String host,String port, String mac, String portS, int hostId) {
		try {

				JCL_message_global_var_obj gvMessage = new MessageGlobalVarObjImpl(nickName, key, defaultVarValue);
				gvMessage.setType(9);
				
				JCL_connector globalVarConnector = new ConnectorImpl();
				globalVarConnector.connect(host, Integer.parseInt(port),mac);
				JCL_result result = globalVarConnector.sendReceive(gvMessage,portS).getResult();
				globalVarConnector.disconnect();
			
				// result from host
				return (Boolean) result.getCorrectResult();

		} catch (Exception e) {
			System.err
					.println("problem in JCL facade instantiateGlobalVar(String nickName, String varName, File[] jars, Object[] defaultVarValue)");
			return false;
		}
	}
	
	//inst global variable with jar
	public boolean instantiateGlobalVarAndReg(Object key,String nickName,JCL_message_register classReg, Object[] defaultVarValue,String host,String port, String mac, String portS, int hostId) {
		try {

			//Register jar				
			JCL_connector connector = new ConnectorImpl();
			connector.connect(host, Integer.parseInt(port),mac);
			classReg.setType(27);
			
			JCL_result resultR = connector.sendReceive(classReg,portS).getResult();	
			
			if (((Boolean) resultR.getCorrectResult()).booleanValue()){
					
			JCL_message_global_var_obj gvMessage = new MessageGlobalVarObjImpl(nickName, key, defaultVarValue);
				gvMessage.setType(9);
				connector.connect(host, Integer.parseInt(port),mac);
				JCL_result result = connector.sendReceive(gvMessage,portS).getResult();
				connector.disconnect();
			
				// result from host
				return (Boolean) result.getCorrectResult();
				
			}else{
				connector.disconnect();
				System.err.println("Register Erro!!!");
				return false;
			}
			
		} catch (Exception e) {
			System.err
					.println("problem in JCL facade instantiateGlobalVar(String nickName, String varName, File[] jars, Object[] defaultVarValue)");
			return false;
		}
	}

	//POssível problema
	//inst global variable with jar on a specific host
	public Object instantiateGlobalVarOnHost(Map<String,String> hostP, String nickName,
			Object key, File[] jars, Object[] defaultVarValue,String serverAdd,int serverPort) {
		try {
			JCL_message_generic mc = new MessageGenericImpl();
			//String[] hostPort = null;
			//= host.split("¬");
			
//	  		  String host = hostP.get("IP");
//	  		  String port = hostP.get("PORT");
	  		  String mac = hostP.get("MAC");
	  		  String portS = hostP.get("PORT_SUPER_PEER");
	  		  
			if (hostP.size() >= 4) {
				
				// register host on server
				Object[] obj = {key, hostP};
				mc.setRegisterData(obj);
				mc.setType(21);
				JCL_connector controlConnector = new ConnectorImpl();
				controlConnector.connect(serverAdd, serverPort,mac);
				JCL_message_control mr = (JCL_message_control) controlConnector.sendReceiveG(mc,portS);
				controlConnector.disconnect();
				return new Boolean(mr.getRegisterData()[0]).booleanValue();
				
			} else{
				return null;
			}
		} catch (Exception e){
			System.err
					.println("problem in JCL facade instantiateGlobalVarOnHost(String host, String nickName, String varName, File[] jars, Object[] defaultVarValue)");
			return null;
		}
	}

	
	//inst global variable
	public Boolean instantiateGlobalVar(Object key, Object instance,String host,String port, String mac,String portS,int hostId) {
		try {
											
				JCL_message_global_var gvMessage = new MessageGlobalVarImpl(key, instance);
				gvMessage.setType(10);
				JCL_connector globalVarConnector = new ConnectorImpl();
				globalVarConnector.connect(host, Integer.parseInt(port),mac);
				JCL_result result = globalVarConnector.sendReceive(gvMessage,portS).getResult();
				Boolean b = (Boolean) result.getCorrectResult();
				globalVarConnector.disconnect();
				
				// result from host
				return b;


		} catch (Exception e) {
			System.err
					.println("problem in JCL facade instantiateGlobalVar(String varName, Object instance)");
			return false;
		}
	}
	
	//inst global variable
	public Object instantiateGlobalVarReturn(Object key, Object instance,String host,String port, String mac,String portS,int hostId) {
		try {
				JCL_message_global_var gvMessage = new MessageGlobalVarImpl(key, instance);
				gvMessage.setType(37);
				JCL_connector globalVarConnector = new ConnectorImpl();
				globalVarConnector.connect(host, Integer.parseInt(port),mac);
				JCL_result result = globalVarConnector.sendReceive(gvMessage,portS).getResult();				
				globalVarConnector.disconnect();
				
				// result from host
				return result.getCorrectResult();

		} catch (Exception e) {
			System.err
					.println("problem in JCL facade instantiateGlobalVar(String varName, Object instance)");
			return false;
		}
	}
	
	
	//inst global variable
	public Boolean instantiateGlobalVar(String host,String port, String mac, String portS, JCL_message_list_global_var gvList, int hostId) {
		try {
				JCL_connector globalVarConnector = new ConnectorImpl();
				globalVarConnector.connect(host, Integer.parseInt(port),mac);
				JCL_result result = globalVarConnector.sendReceive(gvList,portS).getResult();
				Boolean b = (Boolean) result.getCorrectResult();
				globalVarConnector.disconnect();
				
				return b;

		} catch (Exception e) {
			System.err
					.println("problem in JCL facade instantiateGlobalVar(String varName, Object instance)");
			return false;
		}
	}
	
	//inst global variable
	public Boolean instantiateGlobalVarAndReg(String host,String port, String mac, String portS , JCL_message_list_global_var gvList,JCL_message_register classReg, int hostId) {
		try {
			//Register jar				
			JCL_connector connector = new ConnectorImpl();
			connector.connect(host, Integer.parseInt(port),mac);
			classReg.setType(27);
			JCL_result resultR = connector.sendReceive(classReg,portS).getResult();
			
			if (((Boolean) resultR.getCorrectResult()).booleanValue()){
				JCL_result result = connector.sendReceive(gvList,portS).getResult();
				Boolean b = (Boolean) result.getCorrectResult();
				connector.disconnect();
				
				return b;
			}else{
				connector.disconnect();
				System.err.println("Register Erro!!!");
				return null;
			}

		} catch (Exception e) {
			System.err
					.println("problem in JCL facade nstantiateGlobalVarAndReg(String varName, Object instance)");
			return false;
		}
	}
	
	//Inst global variable
	public Boolean instantiateGlobalVarAndReg(Object key, Object instance,String host,String port, String mac,String portS,JCL_message_register classReg, int hostId){
		try {
				
			//Register jar				
			JCL_connector connector = new ConnectorImpl();
			connector.connect(host, Integer.parseInt(port),mac);
			classReg.setType(27);
			JCL_result resultR = connector.sendReceive(classReg,portS).getResult();

			if (((Boolean) resultR.getCorrectResult()).booleanValue()){
				JCL_message_global_var gvMessage = new MessageGlobalVarImpl(key, instance);
				gvMessage.setType(10);
				JCL_result result = connector.sendReceive(gvMessage,portS).getResult();
				Boolean b = (Boolean) result.getCorrectResult();
				connector.disconnect();
				
				// result from host
				return b;
			}else{
				connector.disconnect();
				System.err.println("Register Erro!!!");
				return null;
			}
		} catch (Exception e) {
			System.err
					.println("problem in JCL facade instantiateGlobalVarAndReg(String varName, Object instance)");
			return false;
		}
	}
	
	//Inst global variable
	public Object instantiateGlobalVarAndRegReturn(Object key, Object instance,String host,String port, String mac, String portS,JCL_message_register classReg,int hostId){
		try {
				
			//Register jar				
			JCL_connector connector = new ConnectorImpl();
			connector.connect(host, Integer.parseInt(port),mac);
			classReg.setType(27);
			JCL_result resultR = connector.sendReceive(classReg,portS).getResult();

			if (((Boolean) resultR.getCorrectResult()).booleanValue()){
				JCL_message_global_var gvMessage = new MessageGlobalVarImpl(key, instance);
				gvMessage.setType(37);
				JCL_result result = connector.sendReceive(gvMessage,portS).getResult();
				connector.disconnect();
				
				// result from host
				return result.getCorrectResult();
			}else{
				connector.disconnect();
				System.err.println("Register Erro!!!");
				return null;
			}
		} catch (Exception e) {
			System.err
					.println("problem in JCL facade instantiateGlobalVarAndReg(String varName, Object instance)");
			return false;
		}
	}

	//Possível problema
	//inst global variable on a specific host
	public Boolean instantiateGlobalVarOnHost(Map<String,String> hostP, Object key,
			Object instance,String serverAdd,int serverPort) {
		try {
			
			JCL_message_generic mc = new MessageGenericImpl();
		//	String[] hostPort = null;
			
  		  String host = hostP.get("IP");
  		  String port = hostP.get("PORT");
  		  String mac = hostP.get("MAC");
  		  String portS = hostP.get("PORT_SUPER_PEER");
			
					//host.split(":");
			if (hostP.size() >= 4) {
				
				// register host on server
				Object[] obj = {key, hostP};
				mc.setRegisterData(obj);				
				mc.setType(21);
				JCL_connector controlConnector = new ConnectorImpl();
				controlConnector.connect(serverAdd, serverPort,mac);
				JCL_message_control mr = (JCL_message_control) controlConnector.sendReceiveG(mc,portS);
				controlConnector.disconnect();
				
				//Register global var on host
				if (new Boolean(mr.getRegisterData()[0]).booleanValue()) {

					JCL_message_global_var gvMessage = new MessageGlobalVarImpl(
							key, instance);
					gvMessage.setType(10);
					JCL_connector globalVarConnector = new ConnectorImpl();
					globalVarConnector.connect(host,
							Integer.parseInt(port),mac);
					JCL_result result = globalVarConnector.sendReceive(
							gvMessage,portS).getResult();
					Boolean b = (Boolean) result.getCorrectResult();
					globalVarConnector.disconnect();

					// result from host
					return b;
				}
				return false;
			}
			return false;
		} catch (Exception e) {
			System.err
					.println("problem in JCL facade instantiateGlobalVarOnHost(String host, String varName, Object instance)");
			return false;
		}
	}

	//Destroy global variable
	public Boolean destroyGlobalVar(Object key,String host,String port, String mac, String portS, int hostId) {
		try {

				JCL_message_generic gvMessage = new MessageGenericImpl();
				gvMessage.setRegisterData(key);
				gvMessage.setType(11);
				JCL_connector globalVarConnector = new ConnectorImpl();
				globalVarConnector.connect(host, Integer.parseInt(port),mac);
				JCL_result result = globalVarConnector.sendReceive(gvMessage,portS)
						.getResult();
				Boolean b = (Boolean) result.getCorrectResult();
				globalVarConnector.disconnect();

				return b;

		} catch (Exception e) {
			System.err.println("problem in JCL facade destroyGlobalVar");
			e.printStackTrace();
			return false;
		}
	}
	//possível problema
	//Destroy global variable
	public boolean destroyGlobalVarOnHost(Object key,String serverAdd,int serverPort) {
		try {

			//Get global variable location
			JCL_message_generic mc = new MessageGenericImpl();
			mc.setRegisterData(key);
			mc.setType(11);
			JCL_connector controlConnector = new ConnectorImpl();
			controlConnector.connect(serverAdd, serverPort,null);
			JCL_result rslt = controlConnector.sendReceive(mc,null).getResult();
			controlConnector.disconnect();
			Map<String, String> mr = (Map<String,String>) rslt.getCorrectResult();
			
			//Destroy global variable on host
			if (mr.size()>= 4) {
				
		  		  String host = mr.get("IP");
		  		  String port = mr.get("PORT");
		  		  String mac = mr.get("MAC");
		  		  String portS = mr.get("PORT_SUPER_PEER");
				
				JCL_message_generic gvMessage = new MessageGenericImpl();
				gvMessage.setRegisterData(key);
				gvMessage.setType(11);
				JCL_connector globalVarConnector = new ConnectorImpl();
				globalVarConnector.connect(host,Integer.parseInt(port),mac);
				JCL_result result = globalVarConnector.sendReceive(gvMessage,portS)
						.getResult();
				Boolean b = (Boolean) result.getCorrectResult();
				globalVarConnector.disconnect();

				// result from host
				return b;
				
			} else{
				return false;
			}

		} catch (Exception e) {
			System.err.println("problem in JCL facade destroyGlobalVar");
			e.printStackTrace();
			return false;
		}
	}
	
	//Destroy global variable
	public Boolean getHashValues(JCL_message_generic gvMessage,Queue queue,String host,String port, String mac,String portS,int hostId) {
		try {
				
			
			JCL_connector globalVarConnector = new ConnectorImpl();
				globalVarConnector.connect(host, Integer.parseInt(port),mac);
				JCL_message_generic result = (JCL_message_generic) globalVarConnector.sendReceiveG(gvMessage,portS);
				globalVarConnector.disconnect();
				Set entry = (Set)result.getRegisterData();
				queue.addAll(entry);
				
				return new Boolean(true);

		} catch (Exception e) {
			System.err.println("problem in JCL facade getHashValues(JCL_message_generic gvMessage,String host,String port)");
			return null;
		}
	}

	
	//set a value to global variable
	public Boolean setValue(String varName, Object value,String host,String port, String mac, String portS, int hostId) {
		try {

				JCL_message_global_var gvMessage = new MessageGlobalVarImpl(
						varName, value);
				gvMessage.setType(12);
				JCL_connector globalVarConnector = new ConnectorImpl();
				globalVarConnector.connect(host, Integer.parseInt(port),mac);
				JCL_result result = globalVarConnector.sendReceive(gvMessage,portS)
						.getResult();
				Boolean b = (Boolean) result.getCorrectResult();
				globalVarConnector.disconnect();
				
				// result from host
				return b;


		} catch (Exception e) {
			System.err.println("problem in JCL facade setValue");
			return false;
		}
	}
	
	
	//set a value to global variable and unlock
	public Boolean setValueUnlocking( Object key, Object value, String host,String port, String mac, String portS, int hostId) {
		try {

				JCL_message_global_var gvMessage = new MessageGlobalVarImpl(
						key, value);
				gvMessage.setType(13);
				JCL_connector globalVarConnector = new ConnectorImpl();
				globalVarConnector.connect(host, Integer.parseInt(port),mac);
				JCL_result result = globalVarConnector.sendReceive(gvMessage,portS)
						.getResult();
				Boolean b = (Boolean) result.getCorrectResult();
				globalVarConnector.disconnect();

				// result from host
				return b;

		} catch (Exception e) {
			return false;
		}
	}
	
	//POssível problema
	//set a value to global variable and unlock
	public boolean setValueUnlockingOnHost(Object key, Object value, String serverAdd,int serverPort,int hostId) {
		try {
			
			//Get global variable location
			JCL_message_generic mc = new MessageGenericImpl();
			mc.setRegisterData(key);
			mc.setType(14);
			JCL_connector controlConnector = new ConnectorImpl();
			controlConnector.connect(serverAdd, serverPort,null);
			JCL_result rslt = controlConnector.sendReceive(mc,null).getResult();
			controlConnector.disconnect();
			Map<String, String> mr = (Map<String,String>) rslt.getCorrectResult();
			
			//Destroy global variable on host
			if (mr.size()>= 4) {
				
		  		  String host = mr.get("IP");
		  		  String port = mr.get("PORT");
		  		  String mac = mr.get("MAC");
		  		  String portS = mr.get("PORT_SUPER_PEER");

				
				JCL_message_global_var gvMessage = new MessageGlobalVarImpl(
						key, value);
				gvMessage.setType(13);
				JCL_connector globalVarConnector = new ConnectorImpl();
				globalVarConnector.connect(host, Integer.parseInt(port),mac);
				JCL_result result = globalVarConnector.sendReceive(gvMessage,portS)
						.getResult();
				Boolean b = (Boolean) result.getCorrectResult();
				globalVarConnector.disconnect();

				// result from host
				return b;
			} else{
				return false;
			}

		} catch (Exception e) {
			return false;
		}
	}
	
	//Get the global variable value
	public Object getValue(Object key,String host,String port, String mac, String portS,int hostId) {
		try {				
				JCL_message_generic gvMessage = new MessageGenericImpl();
				gvMessage.setType(14);
				gvMessage.setRegisterData(key);
				JCL_connector globalVarConnector = new ConnectorImpl();
				globalVarConnector.connect(host, Integer.parseInt(port),mac);
				JCL_result result = globalVarConnector.sendReceive(gvMessage,portS)
						.getResult();
				globalVarConnector.disconnect();

				// result from host
				return result.getCorrectResult();
				

		} catch (Exception e) {
			System.err.println("problem in JCL facade getValue");
			e.printStackTrace();
			return null;
		}
	}

	
	//Get the global variable value and lock
	public Object getValueLocking(Object key,String host,String port, String mac, String portS,int hostId) {
		try {

				JCL_message_generic gvMessage = new MessageGenericImpl();
				gvMessage.setType(15);
				gvMessage.setRegisterData(key);
				JCL_connector globalVarConnector = new ConnectorImpl();
				globalVarConnector.connect(host,Integer.parseInt(port),mac);
				JCL_result result = globalVarConnector.sendReceive(gvMessage,portS)
						.getResult();
				globalVarConnector.disconnect();

				// result from host
				return result.getCorrectResult();

		} catch (Exception e) {
			System.err.println("problem in JCL facade getValueLocking");
			e.printStackTrace();
			return null;
		}
	}

	//Possível Problema
	//Get the global variable value
		public Object getValueOnHost(Object key,String serverAdd,int serverPort,int hostId) {
			try {

				//Get global variable location
				JCL_message_generic mc = new MessageGenericImpl();
				mc.setRegisterData(key);
				mc.setType(14);
				JCL_connector controlConnector = new ConnectorImpl();
				controlConnector.connect(serverAdd, serverPort,null);
				JCL_result rslt = controlConnector.sendReceive(mc,null).getResult();
				controlConnector.disconnect();
				Map<String, String> mr = (Map<String,String>) rslt.getCorrectResult();
				
				//Destroy global variable on host
				if (mr.size()>= 4) {
					
			  		  String host = mr.get("IP");
			  		  String port = mr.get("PORT");
			  		  String mac = mr.get("MAC");
			  		  String portS = mr.get("PORT_SUPER_PEER");

					
					
					JCL_message_generic gvMessage = new MessageGenericImpl();
					gvMessage.setType(14);
					gvMessage.setRegisterData(key);
					JCL_connector globalVarConnector = new ConnectorImpl();
					globalVarConnector.connect(host, Integer.parseInt(port),mac);
					JCL_result result = globalVarConnector.sendReceive(gvMessage,portS)
							.getResult();
					globalVarConnector.disconnect();

					// result from host
					return result.getCorrectResult();
					
				} else{
					return null;
				}

			} catch (Exception e) {
				System.err.println("problem in JCL facade getValue");
				e.printStackTrace();
				return null;
			}
		}

		//Possível problema
		//Get the global variable value and lock
		public Object getValueLockingOnHost(Object key,String serverAdd,int serverPort,int hostId) {
			try {
				
				//Get global variable location
				JCL_message_generic mc = new MessageGenericImpl();
				mc.setRegisterData(key);
				mc.setType(14);
				JCL_connector controlConnector = new ConnectorImpl();
				controlConnector.connect(serverAdd, serverPort, null);
				JCL_result rslt = controlConnector.sendReceive(mc,null).getResult();
				controlConnector.disconnect();
				Map<String, String> mr = (Map<String,String>) rslt.getCorrectResult();
				
				//Destroy global variable on host
				if (mr.size()>= 4) {
					
			  		  String host = mr.get("IP");
			  		  String port = mr.get("PORT");
			  		  String mac = mr.get("MAC");
			  		  String portS = mr.get("PORT_SUPER_PEER");

					
					String[] hostPort = { host, port };
					JCL_message_generic gvMessage = new MessageGenericImpl();
					gvMessage.setType(15);
					gvMessage.setRegisterData(key);
					JCL_connector globalVarConnector = new ConnectorImpl();
					globalVarConnector.connect(hostPort[0],
							Integer.parseInt(hostPort[1]),mac);
					JCL_result result = globalVarConnector.sendReceive(gvMessage,portS)
							.getResult();
					globalVarConnector.disconnect();

					// result from host
					return result.getCorrectResult();
					
				} else{
					return null;
				}

			} catch (Exception e) {
				System.err.println("problem in JCL facade getValueLocking");
				e.printStackTrace();
				return null;
			}
		}	

	//Test if exist global variable 
	public Boolean containsGlobalVar(Object nickName,String serverAdd,int serverPort,String mac,String portS,int hostId) {
		try {
			
			// Test if exist global variable on server 
			JCL_message_generic mc = new MessageGenericImpl();
			mc.setRegisterData(nickName);
			mc.setType(17);
			JCL_connector controlConnector = new ConnectorImpl();
			controlConnector.connect(serverAdd, serverPort,mac);
			JCL_message_generic mr = (JCL_message_generic) controlConnector.sendReceiveG(mc,portS);
			controlConnector.disconnect();
			
			// result from server
			return (Boolean) mr.getRegisterData();

		} catch (Exception e) {
			System.err
					.println("problem in JCL facade containsGlobalVar(String nickName)");

			return false;
		}
	}

	
	//Get Server Time
	public JCL_message getServerTime(String serverAdd,int serverPort, int type){

		try {
			
			//Get a list of hosts
			JCL_message mc = new MessageImpl();
			mc.setType(type);
			JCL_connector controlConnector = new ConnectorImpl();
			controlConnector.connect(serverAdd, serverPort,null);
			JCL_message mr = controlConnector.sendReceiveG(mc,null);
			controlConnector.disconnect();
			
			return mr;

		} catch (Exception e) {
			System.err.println("problem in JCL facade getServerTime()");
			e.printStackTrace();
			return null;
		}
	}
	
	//Get a list of hosts
	public JCL_message getSlaveIds(int port,String serverAdd,int serverPort, int deviceType){

		try {
			//Get a list of hosts
			this.port = port;
			JCL_message_generic mc = new MessageGenericImpl();
			mc.setType(24);
			
			mc.setRegisterData(deviceType);
			JCL_connector controlConnector = new ConnectorImpl();
			controlConnector.connect(serverAdd, serverPort,null);
			JCL_message mr = controlConnector.sendReceiveG(mc,null);
			controlConnector.disconnect();
			
			return mr;

		} catch (Exception e) {
			System.err.println("problem in JCL facade getSlaveIds()");
			e.printStackTrace();
			return null;
		}
	}
	
	//Get a list of hosts
	public JCL_message getSlaveIds(String serverAdd,int serverPort, int deviceType){

		try {
			//Get a list of hosts
			//this.port = port;
			JCL_message_generic mc = new MessageGenericImpl();
			mc.setType(42);
			mc.setRegisterData(deviceType);
			boolean activateEncryption = false;
			if (ConnectorImpl.encryption){
				activateEncryption = true;
				ConnectorImpl.encryption = false;
			}
			JCL_connector controlConnector = new ConnectorImpl();
			controlConnector.connect(serverAdd, serverPort,null);
			JCL_message mr = controlConnector.sendReceiveG(mc,null);
			JCL_message_generic mg = (MessageGenericImpl)  mr;
			Object obj[] = (Object[])  mg.getRegisterData();
			CryptographyUtils.setClusterPassword(obj[1]+"");
			controlConnector.disconnect();
			if( activateEncryption )
				ConnectorImpl.encryption = true;
			return mr;

		} catch (Exception e) {
			System.err.println("problem in JCL facade getSlaveIds()");
			e.printStackTrace();
			return null;
		}
	}
	
	//test if a global variable is lock
	public Boolean isLock(Object key,String host,String port, String mac, String portS, int hostId) {
		try {
				JCL_message_generic gvMessage = new MessageGenericImpl();
				gvMessage.setRegisterData(key);
				gvMessage.setType(20);
				JCL_connector globalVarConnector = new ConnectorImpl();
				globalVarConnector.connect(host, Integer.parseInt(port),mac);
				JCL_result result = globalVarConnector.sendReceive(gvMessage,portS)
						.getResult();
				Boolean b = (Boolean) result.getCorrectResult();
				globalVarConnector.disconnect();
				
				// result from host
				return b;

		} catch (Exception e) {
			return false;
		}
	}
	
	@SuppressWarnings("unchecked")
	// Remove all global variable and result from the cluster
	public Boolean cleanEnvironment(String serverAdd,int serverPort) {

		try {
			//remove from server
			JCL_message mc = new MessageImpl();
			mc.setType(22);
			JCL_connector controlConnector = new ConnectorImpl();
			controlConnector.connect(serverAdd, serverPort,null);
			JCL_message_result mr = controlConnector.sendReceive(mc,null);
			controlConnector.disconnect();
			
			//remove from host
//			if (mr.getResult().getCorrectResult() instanceof List) {
				ConcurrentMap<Integer,ConcurrentMap<String,Map<String,String>>> hosts = (ConcurrentMap<Integer,ConcurrentMap<String,Map<String,String>>>) mr.getResult().getCorrectResult();

				JCL_message mclean = new MessageImpl();
				mclean.setType(22);
				JCL_connector controlConnectorClean = new ConnectorImpl();
				
				for (ConcurrentMap<String,Map<String,String>> host:hosts.values()) {
					
				for(Map<String,String> meta:host.values()){	
					
					String h = meta.get("IP");
		   		  	String port = meta.get("PORT");
		   		  	String mac = meta.get("MAC");
		   		  	String portS = meta.get("PORT_SUPER_PEER");

					
					controlConnectorClean.connect(h,
							Integer.parseInt(port),mac);
					JCL_message_result mrclean = controlConnectorClean
							.sendReceive(mclean,portS);
					controlConnectorClean.disconnect();
										
					if (!((Boolean) mrclean.getResult().getCorrectResult())){
						return false;
					}
				}
				}

				return true;
//			}
//
//			return false;

		} catch (Exception e) {
			System.err.println("problem in JCL facade cleanEnvironment()");
			e.printStackTrace();
			return false;
		}
	}

	//Possível problema
	//Insert host in the cluster
//	public Boolean insertHost(String mac, String ip, String port, String serverAdd,int serverPort) {
//		try {
//			
//			//Register host on server
//			JCL_message_control msg = new MessageControlImpl();
//			msg.setType(-1);
//			msg.setRegisterData(ip,port, mac);
//			JCL_connector controlConnector = new ConnectorImpl();
//			controlConnector.connect(serverAdd, serverPort,null);
//			JCL_message_control msgr = controlConnector.sendReceive(msg,null);
//			controlConnector.disconnect();
//			if (msgr.getRegisterData().length == 1) {
//				System.out.println("HOST JCL WAS REGISTERED");
//				return true;
//			} else{
//				System.err.println("HOST JCL WAS NOT REGISTERED");
//			}
//		} catch (Exception e) {
//			System.err.println("Erro in insertHost!");
//			e.printStackTrace();
//			return false;
//		}
//		return false;
//	}

	//Póssivel problema
	//Remove host from cluster
//	public Boolean removeHost(String mac, String ip, String port,String serverAdd,int serverPort) {
//		try {
//			
//			//Remove host from cluster on server
//			JCL_message mclean = new MessageImpl();
//			mclean.setType(22);
//			JCL_connector controlConnectorClean = new ConnectorImpl();
//			controlConnectorClean.connect(ip, Integer.parseInt(port),null);
//			JCL_message_result mrclean = controlConnectorClean
//					.sendReceive(mclean,null);
//			controlConnectorClean.disconnect();
//			if (!((Boolean) mrclean.getResult().getCorrectResult())){
//				return false;
//			}
//			
//			JCL_message_control msg = new MessageControlImpl();
//			msg.setType(-2);
//			msg.setRegisterData(ip, port, mac);
//			JCL_connector controlConnector = new ConnectorImpl();
//			if (controlConnector.connect(serverAdd, serverPort,null)) {
//				JCL_message_control msgr = controlConnector.sendReceive(msg,null);
//				controlConnector.disconnect();
//				if (msgr.getRegisterData().length == 1) {
//					System.out.println("HOST JCL IS UNREGISTERED!");
//					return true;
//				} else{
//					System.err.println("HOST JCL IS NOT UNREGISTERED!");
//				}
//			}
//		} catch (Exception e) {
//			System.err.println("Erro in removeHost!");
//			e.printStackTrace();
//			return false;
//		}
//		return false;
//	}
	
	public Object[] getTicketData(Long ticket){
		return (Object[]) super.getResultBlocking(ticket).getCorrectResult();
	}
	
}
