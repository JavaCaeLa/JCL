package implementations.dm_kernel;

import implementations.sm_kernel.JCL_FacadeImpl;
import interfaces.kernel.JCL_facade;
import interfaces.kernel.JCL_message;
import interfaces.kernel.JCL_message_control;
import interfaces.kernel.JCL_message_result;
import interfaces.kernel.JCL_result;
import io.protostuff.LinkedBuffer;
import io.protostuff.ProtobufIOUtil;
import io.protostuff.ProtostuffIOUtil;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.locks.ReadWriteLock;
import commom.Constants;
import commom.JCL_resultImpl;

public class SimpleServer extends Thread{
	
	protected final int port;	
	protected final Selector selector;
	private  ReadWriteLock lock;
	protected final ServerSocketChannel serverSocket;
	private LinkedBuffer buffer = LinkedBuffer.allocate(1048576);
	private Map<Integer,Map<String,Map<String,String>>> devices;
		
	
	public SimpleServer(int port, Map<Integer,Map<String,Map<String,String>>> devices,ReadWriteLock lock) throws IOException{
				
		//Init varible
		this.port = port;
		this.devices = devices;
		this.selector = Selector.open();
		this.lock = lock;
		this.serverSocket = ServerSocketChannel.open();				
	//	this.jcl = JCL_FacadeImpl.getInstance();
	}
	
	public void end(){
		try {
			//Close server
			selector.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
		}
	}
				
	public void run(){
		openServerSocket();
		SelectionKey key;
		Iterator<SelectionKey> iter;
		
		try {			
	        while(this.serverSocket.isOpen()){
	        	
	        	//loop wait for connection
	        	this.selector.select();
	        	iter = this.selector.selectedKeys().iterator();
            	
            	//new iterator
            	while (iter.hasNext()){           			
            			key = iter.next();
            			iter.remove();
            			
            			if(!key.isValid()){
            				continue;
            			}
            			
            			if (key.isConnectable()) { 
            				((SocketChannel)key.channel()).finishConnect(); 
            			}
            			
        				// accept connection 
            			if (key.isAcceptable()) this.handleAccept(key);
            			
        				// ...read messages...
            			if (key.isReadable())handleRead(key); 
            			
            	}            	
	        }	        	        
		} catch (Exception e) {           
		//	System.err.println( "Error accepting client connection.");
		//	e.printStackTrace();
       } finally {
        	try {
        		selector.close();
        		serverSocket.socket().close();
        		serverSocket.close();
        	} catch (Exception e) {
        		// do nothing - server failed
        	}
        }
	}	

	
    private void openServerSocket() {
        try {
        	this.serverSocket.configureBlocking(false);
            
        	//set some options
            this.serverSocket.socket().setReuseAddress(true);
            this.serverSocket.socket().bind(new InetSocketAddress(this.port));                                  
            this.serverSocket.register(this.selector,SelectionKey.OP_ACCEPT);

            
        } catch (IOException e) {
            throw new RuntimeException("Cannot open port " + this.port, e);
        }
    }  
    
    private void handleAccept(SelectionKey key) throws IOException {    	
		SocketChannel sc = ((ServerSocketChannel) key.channel()).accept();
		String address = (new StringBuilder( sc.socket().getInetAddress().toString() )).append(":").append( sc.socket().getPort() ).toString();
		sc.configureBlocking(false);
		sc.socket().setTcpNoDelay(true);
		sc.register(selector, SelectionKey.OP_READ, address);
		System.out.println("accepted connection from: "+address);
	}

	private void handleRead(SelectionKey key) throws IOException {
		SocketChannel ch = (SocketChannel) key.channel();
		
		ByteBuffer header =  ByteBuffer.allocateDirect(8);
		while(header.hasRemaining()){
			if (ch.read(header) == -1)throw new IOException();
		}
		
		header.flip();
		int sizeint = header.getInt();
		int keyMsg = header.getInt();
		
		ByteBuffer msgRet =  ByteBuffer.allocateDirect(sizeint);
		while(msgRet.hasRemaining()){
			if(ch.read(msgRet) == -1)throw new IOException();
		}
		
		msgRet.flip();
		byte[] out = new byte[sizeint];
		msgRet.get(out);
		
		//test
		JCL_message msg = (JCL_message)this.ReadObjectFromSock(keyMsg, out);
		JCL_message msgR = doSomething(msg);
		
    	//Write data
		byte[] Out = ProtostuffIOUtil.toByteArray(msgR, Constants.Serialization.schema[msgR.getMsgType()], buffer);
		buffer.clear();
		int keyId = msgR.getMsgType();  
		
		send(Out,keyId,ch);
		//End Write data


	}
	
	protected JCL_message doSomething(JCL_message msg){
		
		switch (msg.getType()){
		case -3:{
			
			lock.writeLock().lock();
			
			//Consisting Host
			JCL_message_control jclC = (JCL_message_control)msg;
			String[] hostPortId = jclC.getRegisterData();
						
			//String address = hostPortId[0]; 
			String port = hostPortId[1];
			String slaveName = hostPortId[2];
			//String cores = hostPortId[3];
										
			System.out.println("Host add: "+Arrays.toString(hostPortId));
						
//			devices.put(key, value);
			
			
			JCL_result jclR = new JCL_resultImpl();
			jclR.setCorrectResult(true);
			JCL_message_result RESULT = new MessageResultImpl();
			RESULT.setType(-3);
			RESULT.setResult(jclR);
						
			return RESULT;
		//	break;
			}
		case -2:{
			lock.writeLock().unlock();
			
			JCL_result jclR = new JCL_resultImpl();
			jclR.setCorrectResult(true);
			JCL_message_result RESULT = new MessageResultImpl();
			RESULT.setType(-2);
			RESULT.setResult(jclR);
						
			return RESULT;
		//	break;
		}
		
		default:{
			return null;
		//	break;
			}
		}	
	}
	
	
	public void send(byte[] obj, int keyId,SocketChannel ch) throws IOException {
		ByteBuffer output = ByteBuffer.allocate(8 + obj.length);
		output.putInt(obj.length);
		output.putInt(keyId);
		output.put(obj);
		output.flip();
		while(output.hasRemaining()){
			ch.write(output);
		}
	}
	
	
    protected Object ReadObjectFromSock(int key,byte[] obj){
 	   switch (key) {
   		case Constants.Serialization.MSG:{
   			MessageImpl msgR = new MessageImpl();
   			ProtobufIOUtil.mergeFrom(obj, msgR, Constants.Serialization.schema[msgR.getMsgType()]);
   			return msgR;
   		}
   		case Constants.Serialization.MSG_COMMONS:{
   			MessageCommonsImpl msgR = new MessageCommonsImpl();
   			ProtobufIOUtil.mergeFrom(obj, msgR, Constants.Serialization.schema[msgR.getMsgType()]);
   			return msgR;
   		}
   		case Constants.Serialization.MSG_CONTROL:{
   			MessageControlImpl msgR = new MessageControlImpl();
   			ProtobufIOUtil.mergeFrom(obj, msgR, Constants.Serialization.schema[msgR.getMsgType()]);
   			return msgR;
   		}
   		case Constants.Serialization.MSG_GETHOST:{
   			MessageGetHostImpl msgR = new MessageGetHostImpl();
   			ProtobufIOUtil.mergeFrom(obj, msgR, Constants.Serialization.schema[msgR.getMsgType()]);
   			return msgR;
   		}
   		case Constants.Serialization.MSG_GLOBALVARS:{
   			MessageGlobalVarImpl msgR = new MessageGlobalVarImpl();
   			ProtobufIOUtil.mergeFrom(obj, msgR, Constants.Serialization.schema[msgR.getMsgType()]);
   			return msgR;
   		}
   		case Constants.Serialization.MSG_REGISTER:{
   			MessageRegisterImpl msgR = new MessageRegisterImpl();
   			ProtobufIOUtil.mergeFrom(obj, msgR, Constants.Serialization.schema[msgR.getMsgType()]);
   			return msgR;
   		}
   		case Constants.Serialization.MSG_RESULT:{
   			MessageResultImpl msgR = new MessageResultImpl();
   			ProtobufIOUtil.mergeFrom(obj, msgR, Constants.Serialization.schema[msgR.getMsgType()]);
   			return msgR;
   		}
   		case Constants.Serialization.MSG_TASK:{
   			MessageTaskImpl msgR = new MessageTaskImpl();
   			ProtobufIOUtil.mergeFrom(obj, msgR, Constants.Serialization.schema[msgR.getMsgType()]);
   			return msgR;
   		}
   		
   		case Constants.Serialization.MSG_LISTTASK:{
   			MessageListTaskImpl msgR = new MessageListTaskImpl();
   			ProtobufIOUtil.mergeFrom(obj, msgR, Constants.Serialization.schema[msgR.getMsgType()]);
   			return msgR;
   		}
   		case Constants.Serialization.MSG_GENERIC:{
   			MessageGenericImpl msgR = new MessageGenericImpl();
   			ProtobufIOUtil.mergeFrom(obj, msgR, Constants.Serialization.schema[msgR.getMsgType()]);
   			return msgR;
   		}
   		case Constants.Serialization.MSG_LONG:{
   			MessageLongImpl msgR = new MessageLongImpl();
   			ProtobufIOUtil.mergeFrom(obj, msgR, Constants.Serialization.schema[msgR.getMsgType()]);
   			return msgR;
   		}
   		case Constants.Serialization.MSG_BOOL:{
   			MessageBoolImpl msgR = new MessageBoolImpl();
   			ProtobufIOUtil.mergeFrom(obj, msgR,Constants.Serialization.schema[msgR.getMsgType()]);
   			return msgR;
   		}
   		case Constants.Serialization.MSG_GLOBALVARSOBJ:{
   			MessageGlobalVarObjImpl msgR = new MessageGlobalVarObjImpl();
   			ProtobufIOUtil.mergeFrom(obj, msgR, Constants.Serialization.schema[msgR.getMsgType()]);
   			return msgR;
   		}
   		case Constants.Serialization.MSG_LISTGLOBALVARS:{
   			MessageListGlobalVarImpl msgR = new MessageListGlobalVarImpl();
   			ProtobufIOUtil.mergeFrom(obj, msgR, Constants.Serialization.schema[msgR.getMsgType()]);
   			return msgR;
   		}
   		case Constants.Serialization.MSG_METADATA:{
   			MessageMetadataImpl msgR = new MessageMetadataImpl();
   			ProtobufIOUtil.mergeFrom(obj, msgR, Constants.Serialization.schema[msgR.getMsgType()]);
   			return msgR;
   		}
   		case Constants.Serialization.MSG_SENSOR:{
   			MessageSensorImpl msgR = new MessageSensorImpl();
   			ProtobufIOUtil.mergeFrom(obj, msgR, Constants.Serialization.schema[msgR.getMsgType()]);
   			return msgR;
   		}
   		default:{
   			System.out.println("Class not found!!");
   			return null;
   		}
   		}
      }
}
