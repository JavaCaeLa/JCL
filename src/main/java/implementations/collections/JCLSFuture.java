package implementations.collections;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class JCLSFuture<T>  implements Future<T>{
    
	private T obj;	
    public JCLSFuture(T obj) {
		this.obj = obj;
	}

	@Override
	public boolean cancel(boolean mayInterruptIfRunning) {
		return false;
	}

	@Override
	public T get() throws InterruptedException, ExecutionException {
		return obj;
	}

	@Override
	public T get(long timeout, TimeUnit unit)
			throws InterruptedException, ExecutionException, TimeoutException {
		return obj;
	}

	@Override
	public boolean isCancelled() {
		return false;
	}

	@Override
	public boolean isDone() {
		return true;
	}
}
