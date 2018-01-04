package implementations.collections;

import interfaces.kernel.JCL_result;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class JCLVFuture<T> extends implementations.sm_kernel.JCL_FacadeImpl.Holder implements Future<T>{
    
	private final Long ticket;
	private boolean cancel = false;

	
	
    public JCLVFuture(Long ticket) {
		// TODO Auto-generated constructor stub
		this.ticket = ticket;
	}

    public Long getTicket() {
		return ticket;
	}
    
	@Override
	public boolean cancel(boolean mayInterruptIfRunning) {
		// TODO Auto-generated method stub		
		try {
			JCL_result jresult = super.getResultBlocking(ticket);
//			JCL_result jresult = jcl.getResultUnblocking(ticket);
			if (jresult.getCorrectResult()!=null){
				return false;
			}else{
			if (mayInterruptIfRunning){
				super.removeResult(ticket);
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
		JCL_result jresult = super.getResultBlocking(ticket);		
//		JCL_result jresult = jcl.getResultBlocking(ticket);
		return (T)jresult.getCorrectResult();
	}

	@Override
	public T get(long timeout, TimeUnit unit)
			throws InterruptedException, ExecutionException, TimeoutException {
		// TODO Auto-generated method stub
		if(cancel)return null;
		long ini = System.nanoTime();
		JCL_result jresult = super.getResultUnblocking(ticket);
		
		while(((System.nanoTime()-ini) < unit.toNanos(timeout)) && (jresult.getCorrectResult()==null)){
			jresult = super.getResultUnblocking(ticket);
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
		JCL_result jresult = super.getResultUnblocking(ticket);
		return (jresult.getCorrectResult()!=null);
	}
}
