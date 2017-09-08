package implementations.util;

import java.util.concurrent.atomic.AtomicInteger;

public class RingBuffer<T> {
	private T[] buffer;	 
	private final int capacity;
	private final AtomicInteger head;
	private final AtomicInteger tail;
	
	@SuppressWarnings("unchecked")
	public RingBuffer(int capacity) {
		 this.capacity = capacity;
		 this.head = new AtomicInteger();
		 this.tail = new AtomicInteger();
		 this.buffer = (T[]) new Object[capacity];
	}
	
	public boolean isEmpty() {
		    return this.head.get() >= this.tail.get();
	}
	
	public int size() {
	    return this.tail.get() - this.head.get();
	}
	  
	public void offer(T toAdd) {
		this.buffer[this.tail.getAndIncrement() % this.capacity] = toAdd;
	}
	
	public T poll() {
		if(!isEmpty()){
			int t = this.head.get();
			if((this.head.compareAndSet(t,(t + 1)))){
			return this.buffer[t % this.capacity];
			}
		}
		return null;
	}
}
