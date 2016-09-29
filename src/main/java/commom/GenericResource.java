package commom;

import java.util.concurrent.ConcurrentLinkedQueue;

public class GenericResource<S> {
	public ConcurrentLinkedQueue<S> registers;
	protected boolean finished, serverFinished;
	
	public GenericResource(){
		this.registers = new ConcurrentLinkedQueue<S>();
		this.finished= false;
		this.serverFinished=false;
	}
	
	public void putRegister(S register){
//			if(register!=null){
				this.registers.offer(register);
				wakeup();
//			}
		
	}	

	public S getRegister() throws Exception{		
			if(!this.registers.isEmpty()){
				return this.registers.poll();			
			}else {
				if(!this.finished) suspend();					
				return null;		
			}		
	}

	protected synchronized void suspend()throws Exception{
		wait();
	}
	
	public synchronized void wakeup(){	
		this.notifyAll();
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
	
	public boolean isStopped(){
		return this.serverFinished;
	}
	
	public synchronized void stopServer(){
		this.serverFinished = true;
	}
}
