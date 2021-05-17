
package commom;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import interfaces.kernel.JCL_result;

public class JCLResultResourceContainer {
	private Map<String, JCLResultResource> registers;
	protected boolean finished;
		
	public JCLResultResourceContainer(){
		this.registers = new ConcurrentHashMap<String, JCLResultResource>();
		this.finished = false;		
	}
		
	public synchronized void create(String key, JCLResultResource value){
//		System.out.println("JCLResultResourceContainer key: " + key + ", value: " + value);
		this.registers.put(key, value);
		wakeup();
	}
	
	protected synchronized void wakeup(){
		this.notify();
	}
							
	public synchronized JCLResultResource read(String key) throws Exception{
//		System.out.println("JCLResultResource read(" + key + ")");
//		System.out.println("JCLResultResource registers.size(): " + registers.size());
//		this.registers.forEach((k, v) -> {
//			System.out.println("JCLResultResource k: " + k + ", v: " + v.getCorrectResult());
//		});
		if(!this.registers.isEmpty())
			return this.registers.get(key);
		else {
//			if(finished == false)
//				suspend();
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
	
	public JCLResultResource delete(String key) {
		JCLResultResource removed = this.registers.remove(key);
		wakeup();
		return removed;
	}
}
