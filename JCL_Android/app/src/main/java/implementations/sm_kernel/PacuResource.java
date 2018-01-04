package implementations.sm_kernel;

import implementations.dm_kernel.ConnectorImpl;
import implementations.dm_kernel.MessageControlImpl;
import interfaces.kernel.JCL_connector;
import interfaces.kernel.JCL_message_control;
import interfaces.kernel.JCL_message_task;
import interfaces.kernel.JCL_result;
import interfaces.kernel.JCL_task;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicLong;

import commom.GenericResource;
import commom.JCL_resultImpl;

public class PacuResource<S> extends GenericResource<S> {

	private ConcurrentMap<String,String[]> slaves;
	private Map<Long, JCL_result> results;
	private List<String> slavesIDs;
	private AtomicLong numOfTasks;
	private Long currentID;
	private Map<String,String> hostIp;
	private boolean enable;

	public PacuResource(List<String> slavesIDs, ConcurrentMap<String,String[]> slaves, boolean enable) {
		// TODO Auto-generated constructor stub
		this.slavesIDs = slavesIDs;
		this.slaves = slaves;
		this.enable = enable;
	}

	public void setInJCLLamb(AtomicLong numOfTasks, Map<Long, JCL_result> results){
		this.numOfTasks = numOfTasks;
		this.results = results;
		currentID = createTicket();
	}

	public List<String> getSlavesIDs(){
		return this.slavesIDs;
	}

	public ConcurrentMap<String,String[]> getSlaves(){
		return this.slaves;
	}

	public void setSlavesIDs(List<String> slavesIDs){
		this.slavesIDs = slavesIDs;
	}

	public void setSlaves(ConcurrentMap<String,String[]> slaves){
		this.slaves = slaves;
	}

	public void setHostIp(Map<String,String> hostIp){
		this.hostIp = hostIp;
//		this.hostIp[0] = this.metaData.get("IP");
//		this.hostIp[1] = this.metaData.get("PORT");
//		this.hostIp[2] = this.metaData.get("MAC");
//		this.hostIp[3] = this.metaData.get("CORE(S)");
//		this.hostIp[4] = this.metaData.get("DEVICE_TYPE");
	}

	@Override
	public S getRegister() throws Exception{
		if(!this.registers.isEmpty()){
			return this.registers.poll();
		}else {
			if(!this.finished){
				if (this.enable){
					S task = tryBalance();
					if(task==null){
						suspend();
					}else{
						return task;
					}
				}else{
					suspend();
				}
			}
			return null;
		}
	}

	private S tryBalance(){

		List<String> slavesIDsCopy = pickNRandom(this.slavesIDs);

		//System.out.println("try balance:"+slavesIDsCopy.toString());
		//Try to get task
		for(String hostId:slavesIDsCopy){
			//Get Host
			String[] hostPort = slaves.get(hostId);
			String host = hostPort[0];
			String port = hostPort[1];
			String mac = hostPort[2];
			String portS = hostPort[4];


			if((!this.hostIp.get("IP").equals(host)) || ((!this.hostIp.get("PORT").equals(port)) && (!this.hostIp.get("PORT").equals(portS)))){
				synchronized(currentID){
					JCL_message_control mc = new MessageControlImpl();
//				mc.setRegisterData(addElement(hostIp,String.valueOf(currentID)));
					mc.setRegisterData(this.hostIp.get("PORT"),String.valueOf(currentID),this.hostIp.get("MAC"),this.hostIp.get("PORT_SUPER_PEER"));
					mc.setType(-6);
					JCL_connector controlConnector = new ConnectorImpl();

					System.out.println(slaves);
					System.out.println(hostIp);

					System.out.println("Aki:"+host+" "+port);

					controlConnector.connect(host, Integer.parseInt(port),mac);
					JCL_message_task mr = (JCL_message_task) controlConnector.sendReceiveG(mc,portS);
					controlConnector.disconnect();
					JCL_task t = mr.getTask();
//  		  		S t = (S)mr.getTask();

					if (t!=null){

						t.setTaskTime(System.nanoTime());
						t.setTaskID(currentID);
						currentID = createTicket();
						return (S)t;
					}
				}
			}
		}
		return null;
	}


	public List<String> pickNRandom(List<String> lst) {
		if (lst!=null){
			List<String> copy = new LinkedList<String>(lst);
			Collections.shuffle(copy);
			return copy;

		}else{
			return new LinkedList<String>();
		}
	}

	private String[] addElement(String[] a, String e) {
		a  = Arrays.copyOf(a, a.length + 1);
		a[a.length - 1] = e;
		return a;
	}

	//Use only on Pacu Facade
	private Long createTicket(){
		//Create ticket without task
		Long ticket = numOfTasks.getAndIncrement();
		JCL_result jclr = new JCL_resultImpl();
		results.put(ticket, jclr);

		return ticket;
	}
}