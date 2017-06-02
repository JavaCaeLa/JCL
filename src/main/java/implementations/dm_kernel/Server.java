package implementations.dm_kernel;

import implementations.util.CoresAutodetect;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.ReentrantLock;

import commom.GenericConsumer;
import commom.GenericResource;
import commom.JCL_acceptor;
import commom.JCL_handler;

public abstract class Server{
	
	private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
	protected final List<GenericConsumer<JCL_handler>> serverThreads = new ArrayList<GenericConsumer<JCL_handler>>();		
	protected List<AtomicBoolean> killWorkers = new ArrayList<AtomicBoolean>();
	protected final GenericResource<JCL_handler> serverR;	
	protected final int port;
	protected final Selector selector;
	protected final ServerSocketChannel serverSocket;
	protected final long initialTime;
	
	
	public Server(int port) throws IOException{
				
		this.serverSocket = ServerSocketChannel.open();
		this.initialTime = System.nanoTime();		
		this.port = port;
		this.selector = Selector.open();		
		this.serverR = new GenericResource<JCL_handler>(); 

	}
	
	public void begin(){
		try{
			
			JCL_Crawler<JCL_handler> crawler = new JCL_Crawler<JCL_handler>(CoresAutodetect.cores,serverThreads,killWorkers,serverR,this);			
			scheduler.scheduleAtFixedRate(crawler,0,20000,TimeUnit.MILLISECONDS);															

			//start listening 			
			listen();
		}catch (Exception e){
			e.printStackTrace();
		}
	}
				
	protected void listen(){
		try {
			 openServerSocket();
			 //any other verification can be done here
			 //including send a message to another peer!!!
			 beforeListening();
			
	        while(! this.serverR.isStopped()){
            	        	
	        	this.selector.select();
	        	Set<SelectionKey> selected = this.selector.selectedKeys();
            	Iterator<SelectionKey> it = selected.iterator();
            	while (it.hasNext()){           			
            			SelectionKey key = it.next();
            			if(key.isValid()){
            				Runnable r = (Runnable)key.attachment();
            				r.run();
            			}
            	}            	
            	selected.clear();
 
	            //any other verification can be done here
	            //including send a message to another peer!!!
	            duringListening();
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

	
	
    private void openServerSocket() {
        try {
        	this.serverSocket.configureBlocking(false);
            //set some options
            this.serverSocket.socket().setReuseAddress(true);
            this.serverSocket.socket().bind(new InetSocketAddress(this.port));            
                        
            SelectionKey sk = this.serverSocket.register(this.selector,SelectionKey.OP_ACCEPT);
//            sk.attach(new JCL_acceptor(this.serverSocket,this.selector));
            sk.attach(new JCL_acceptor(this.serverSocket,this.selector,this.serverR));

            
        } catch (IOException e) {
            throw new RuntimeException("Cannot open port " + this.port, e);
        }
    }
    
    protected GenericResource<JCL_handler> getServerR() {
		return serverR;
	}
    
    public abstract <K extends JCL_handler> GenericConsumer<K> createSocketConsumer( GenericResource<K> r, AtomicBoolean kill);
    protected abstract void beforeListening();
    protected abstract void duringListening();
    
}
