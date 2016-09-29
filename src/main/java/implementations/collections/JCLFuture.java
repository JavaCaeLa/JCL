package implementations.collections;

import implementations.dm_kernel.user.JCL_FacadeImpl;
import interfaces.kernel.JCL_facade;
import interfaces.kernel.JCL_result;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class JCLFuture<T> implements Future<T>{
    
	private final String ticket;
	private boolean cancel = false;
    private final static JCL_facade jcl = JCL_FacadeImpl.getInstanceLambari();
	
    public JCLFuture(String ticket) {
		// TODO Auto-generated constructor stub
		this.ticket = ticket;
	}

	@Override
	public boolean cancel(boolean mayInterruptIfRunning) {
		// TODO Auto-generated method stub		
		try {
			JCL_result jresult = jcl.getResultUnblocking(ticket);
			if (jresult.getCorrectResult()!=null){
				return false;
			}else{
			if (mayInterruptIfRunning){
				jcl.removeResult(ticket);
			}
			cancel = true;
			return true;
			}
		} catch (Exception e) {
			// TODO: handle exception			
			e.printStackTrace();
			return false;
		}		
	}

	@Override
	public T get() throws InterruptedException, ExecutionException {
		// TODO Auto-generated method stub
		if(cancel)return null;
		JCL_result jresult = jcl.getResultBlocking(ticket);
		return (T)jresult.getCorrectResult();
	}

	@Override
	public T get(long timeout, TimeUnit unit)
			throws InterruptedException, ExecutionException, TimeoutException {
		// TODO Auto-generated method stub
		if(cancel)return null;
		long ini = System.nanoTime();
		JCL_result jresult = jcl.getResultUnblocking(ticket);
		
		while(((System.nanoTime()-ini) < unit.toNanos(timeout)) && (jresult.getCorrectResult()==null)){
			jresult = jcl.getResultUnblocking(ticket);
		}
		
		if (jresult.getCorrectResult() == null){
            throw new TimeoutException();
        }
		
		return (T)jresult.getCorrectResult();
	}

	@Override
	public boolean isCancelled() {
		// TODO Auto-generated method stub
		return cancel;
	}

	@Override
	public boolean isDone() {
		// TODO Auto-generated method stub
		if(cancel)return true;
		JCL_result jresult = jcl.getResultUnblocking(ticket);
		return (jresult.getCorrectResult()!=null);
	}
}
