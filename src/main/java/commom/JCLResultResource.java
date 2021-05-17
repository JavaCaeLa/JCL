
package commom;

import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import interfaces.kernel.JCL_result;

public class JCLResultResource {
	private Map<String, JCL_result> registers;
	protected boolean finished;
		
	public JCLResultResource(){
		this.registers = new ConcurrentHashMap<String, JCL_result>();
		this.finished = false;		
	}
		
	public synchronized void create(String key, JCL_result value){
		this.registers.put(key, value);
		wakeup();
	}
	
	protected synchronized void wakeup(){
		this.notify();
	}
	
	public synchronized JCL_result read(String key) throws Exception{
//		System.out.println("A " + Thread.currentThread().getId() + " @ " + key);
		if(!this.registers.isEmpty() && registers.containsKey(key)) {
//			System.out.println("B " + Thread.currentThread().getId() + " @ " + key);
			JCL_result r = this.registers.get(key);
//			System.out.println(Thread.currentThread().getId() + " + " + key + " + " + r + " + " + r.getCorrectResult());
			return r;
		}
		else {
//			System.out.println("C " + Thread.currentThread().getId() + " @ " + key);
//			if(!isFinished()) {
//				suspend();
//			}
			return null;	
		}
	}
	
	protected synchronized void suspend()throws Exception{
		wait();
	}
	
	public int getNumOfRegisters(){
		return this.registers.size();
	}
	
	public synchronized void setFinished(){
		this.finished = true;
		this.notifyAll();
	}
	
	public boolean isFinished(){
		return this.finished;
	}
	
	public JCL_result delete(String key) {
		JCL_result removed = this.registers.remove(key);
		wakeup();
		return removed;
	}
	
	public Set<Entry<String, JCL_result>> entrySet(){
		return registers.entrySet();
	}
	
	public void print() {
		registers.forEach((k, v) -> {
			System.out.println("k: " + k + ", v: " + v.getCorrectResult());
		});
	}
}
