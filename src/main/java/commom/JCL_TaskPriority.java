package commom;

import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicLong;

import implementations.collections.JCLFuture;
import implementations.sm_kernel.JCL_orbImpl;
import interfaces.kernel.JCL_orb;
import interfaces.kernel.JCL_result;
import interfaces.kernel.JCL_task;

public class JCL_TaskPriority<S extends JCL_task> implements Callable<JCLFuture<JCL_result>>{

	private JCL_orb<JCL_result> orb = JCL_orbImpl.getInstancePacu();
	private Map<Long, JCL_result> results;
	private AtomicLong numOfTasks;
	private S task;
	
	public JCL_TaskPriority(S task,JCL_orb<JCL_result> orb){
		this.task = task;
		this.orb = orb;
		this.results = orb.getResults();
		this.numOfTasks = orb.getNumOfTasks();		
	}	

	@Override
	public JCLFuture<JCL_result> call() throws Exception{
		// TODO Auto-generated method stub
		Long ticket = numOfTasks.getAndIncrement();	
		task.setTaskID(ticket);
		JCL_result jclr = new JCL_resultImpl();	
		jclr.setTime(task.getTaskTime());
		results.put(ticket, jclr);			
		orb.execute(task);	
					
		return new JCLFuture<JCL_result>(ticket);
	}

}
