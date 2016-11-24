package implementations.sm_kernel;

import interfaces.kernel.JCL_orb;
import interfaces.kernel.JCL_result;
import interfaces.kernel.JCL_task;

import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import commom.GenericConsumer;
import commom.GenericResource;

public class TaskConsumer<S extends JCL_task> extends GenericConsumer<S>{
	
//	private Map<Long, JCL_result> results;
	private JCL_orb<JCL_result> orb;

	public TaskConsumer(GenericResource<S> re, AtomicBoolean kill,JCL_orb<JCL_result> orb){
		
		super(re,kill);	
		this.orb = orb;
//		this.results = results;
	}

	@Override
	protected void doSomething(S str) {
		// TODO Auto-generated method stub
//		if(str != null){
			orb.execute(str);	
//		}
				
	}
	
}
