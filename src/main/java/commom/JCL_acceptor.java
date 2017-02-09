package commom;

import java.io.IOException;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;

//class Reactor continued
public class JCL_acceptor implements Runnable { // inner
	
	//Global Variable
	private final ServerSocketChannel serverSocket;
	private final Selector selector;
//	private final List<Selector> selector;
//	private final List<ReentrantLock> selectorReadLock;
	private final GenericResource<JCL_handler> serverR;
	private static AtomicInteger current = new AtomicInteger(0);
	//End declaration
	
	//construct
	public JCL_acceptor(ServerSocketChannel serverSocket, Selector selector, GenericResource<JCL_handler> serverR){
//	public JCL_acceptor(ServerSocketChannel serverSocket, List<Selector> selector, List<ReentrantLock> selectorReadLock, GenericResource<JCL_handler> serverR){

		this.serverSocket = serverSocket;
		this.selector = selector;
//		this.selectorReadLock = selectorReadLock;
		this.serverR = serverR;
	}
	
	//accept connection
	public void run(){
		try {
			
			SocketChannel sock = this.serverSocket.accept();
				if (sock != null){					
				//	int index = this.current.getAndIncrement()%this.selector.size();
				//	Selector sel = this.selector.get(index);					
				//	ReentrantLock lock = this.selectorReadLock.get(index);
				//	new JCL_handler(sel,lock, sock,this.serverR);					
					new JCL_handler(this.selector, sock,this.serverR);					
				}
			}
		catch(IOException ex) { 
			ex.printStackTrace(); 
		}
	}	
}
