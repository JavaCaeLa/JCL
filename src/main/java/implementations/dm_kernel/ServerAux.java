package implementations.dm_kernel;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.locks.ReentrantLock;

import commom.GenericResource;
import commom.JCL_handler;

public class ServerAux extends Thread{
	protected GenericResource<JCL_handler> serverR;
	private Selector selector;
	private ReentrantLock selectorLock;
	
	public ServerAux(GenericResource<JCL_handler> serverR, Selector selector, ReentrantLock lock) {
		
		this.serverR = serverR;
		this.selector = selector;
		this.selectorLock = lock;
	}
	
	@Override
	public void run(){
		try {
			
	        while(! this.serverR.isStopped()){
	        	
	        	if(this.selector.selectNow()==0){	        		
	        		//Register
	        		this.selector.select();
	        		this.selectorLock.lock();
		        	this.selectorLock.unlock();	        		
	        	}
	        	
	        	Set<SelectionKey> selected = this.selector.selectedKeys();
            	Iterator<SelectionKey> it = selected.iterator();
            	while (it.hasNext()){           			
            			SelectionKey key = it.next();
            			if(key.isValid()){
            				Runnable r = (Runnable)key.attachment();
            				//executor.execute(r);
            				r.run();
            			}
            	}            	
            	selected.clear();
	        }
	        System.out.println("Server Stopped.") ;
		} catch (IOException e) {
            if(this.serverR.isStopped()) {
                System.out.println("Server Stopped.") ;
                return;
            }
            throw new RuntimeException(
                "Error accepting client connection", e);
        } 
	
	}
}
