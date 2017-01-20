package commom;

import implementations.dm_kernel.ConnectorImpl;
import implementations.dm_kernel.MessageBoolImpl;
import implementations.dm_kernel.MessageCommonsImpl;
import implementations.dm_kernel.MessageControlImpl;
import implementations.dm_kernel.MessageGenericImpl;
import implementations.dm_kernel.MessageGetHostImpl;
import implementations.dm_kernel.MessageGlobalVarImpl;
import implementations.dm_kernel.MessageGlobalVarObjImpl;
import implementations.dm_kernel.MessageImpl;
import implementations.dm_kernel.MessageListGlobalVarImpl;
import implementations.dm_kernel.MessageListTaskImpl;
import implementations.dm_kernel.MessageLongImpl;
import implementations.dm_kernel.MessageMetadataImpl;
import implementations.dm_kernel.MessageRegisterImpl;
import implementations.dm_kernel.MessageResultImpl;
import implementations.dm_kernel.MessageSensorImpl;
import implementations.dm_kernel.MessageTaskImpl;
import implementations.util.IoT.CryptographyUtils;
import interfaces.kernel.Constant;
import interfaces.kernel.JCL_message;
import io.protostuff.ProtobufIOUtil;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Arrays;
import java.util.Set;
import java.util.concurrent.locks.ReentrantLock;

public class JCL_handler implements Runnable,Constant {
		
	private GenericResource<JCL_handler> serverR;
//	public static int buffersize = 2097152;
	private SocketChannel socket;
	private JCL_handler from;
	private SelectionKey sk;
	private JCL_message msg;
	private byte[] msgSer;
	private String host;
	private Short hash;
	private byte[] mac;
	private Byte key;
	
	protected Selector selector;
	
//    static final byte[] crcTbl;    
//    static
//    {
//        crcTbl = new byte[256];
//        byte polynomial = 0x07; // 0x107 less the leading x^8
// 
//        for (int i = 0; i < 256; i++)
//        {
//            byte j = (byte)i;
//            for (int k = 0; k < 8; k++)
//            {
//                j = (byte)((j < 0) ? (j << 1) ^ polynomial : j << 1);
//            }
// 
//            crcTbl[i] = j;
//        }
//    }


//	public JCL_handler(Selector sel,ReentrantLock lock, SocketChannel c, GenericResource<JCL_handler> serverR) throws IOException {
	public JCL_handler(Selector sel, SocketChannel c, GenericResource<JCL_handler> serverR) throws IOException {

		this.serverR = serverR;
		this.socket = c;
		this.socket.socket().setTcpNoDelay(true);
		this.socket.socket().setKeepAlive(true);
		this.socket.configureBlocking(false);
		this.host = this.socket.socket().getInetAddress().getHostAddress();
//		lock.lock();
		sel.wakeup();
		this.sk = this.socket.register(sel, SelectionKey.OP_READ);
		this.sk.attach(this);
//		lock.unlock();
		sel.wakeup();	
		selector = sel;
	}

//	static public void setResource(GenericResource<JCL_handler> serverR) {
//
//		JCL_handler.serverR = serverR;
//	}

	// class Handler continued
	public void run(){

		if ((this.sk.isValid()) && (this.sk.isReadable())){	
			 this.read();
		}
	}
	
	
//    public static byte crc8(int data)
//    {    	
//    	 byte crcReg = 0;
//    	 crcReg = crcTbl[(crcReg ^ (data & 0xFF)) & 0xFF];
//         for (int i = 1; i < 4; i++)
//         {
//              crcReg = crcTbl[(crcReg ^ ((data >> (i*8)) & 0xFF)) & 0xFF];
//         }
//         return crcReg;
//    }

	public boolean read() {		
		try {

			ByteBuffer msgHeard =  ByteBuffer.allocateDirect(4);

			while(msgHeard.hasRemaining()){				
				if (this.socket.read(msgHeard) == -1)throw new IOException();			
			}
			
			msgHeard.flip();
			int size = msgHeard.getInt();			
			ByteBuffer msgRe =  ByteBuffer.allocateDirect(size);

			while(msgRe.hasRemaining()){
//			do{				
				if (this.socket.read(msgRe) == -1)throw new IOException();
//				if (this.socket.read(msgRe) > 0){
//				System.out.println(msgRe.get(0)+" id:"+Thread.currentThread().getId());	
//				System.out.println(msgRe.get(msgRe.position()-1)+" id:"+Thread.currentThread().getId());	
//				System.out.println(crc8(msgRe.position())+" id:"+Thread.currentThread().getId());	
//				System.out.println(msgRe.get(msgRe.position()-2)+" id:"+Thread.currentThread().getId());	
//				}
//				System.out.println("Tam:"+msgRe.position());
//				System.out.println("Tam lido:"+msgRe.getInt(0));
//				System.out.println("key:"+msgRe.get(4));
			}			
//			 }while(!((msgRe.position()>4) && ((msgRe.position()+4)==msgHeard.getInt(0))));				
//		 } while(!((msgRe.position()>3) && (msgRe.get(0)==msgRe.get(msgRe.position()-1)) && (crc8(msgRe.position())==msgRe.get(msgRe.position()-2))));				
						
			// && (crc8(msgRe.position())==msgRe.get(msgRe.position()-2))
			msgRe.flip();
			byte first = msgRe.get();
			byte start = (byte) ((first >> 6) & (byte) 0x03);
			this.key = (byte) (first & 0x3F);

//			System.out.println("Read Limit:"+msgRe.position());
			
//			System.out.println("key"+first);
//			System.out.println("key"+start);
//			System.out.println("key"+this.key);

//			System.out.println("key:"+key);
//			System.out.println("crc:"+crc8(msgRe.position()));
//			System.out.println("crc size:"+msgRe.position());
			
			switch (start) {
			case 0:{
//				msgRe.flip();
//				msgRe.position(1);
				hash = msgRe.getShort();
				this.mac = new byte[6];
				msgRe.get(this.mac);				
				this.msgSer = new byte[(msgHeard.getInt(0)-msgRe.position())];				
				msgRe.get(msgSer);
				break;
				}
			case 1:{	// crypted message
				byte iv[] = new byte[16];
				byte regKey[] = new byte[32];
//				msgRe.flip();
//				msgRe.position(1);
				hash = msgRe.getShort();
				this.mac = new byte[6];
				msgRe.get(this.mac);				
				msgRe.get(iv);
				msgRe.get(regKey);
				this.msgSer = new byte[(msgHeard.getInt(0)-msgRe.position())];				
				msgRe.get(msgSer);
				if ( !new String(regKey).equals(new String(CryptographyUtils.generateRegitrationKey(msgSer, iv)))) {
					System.out.println("Message Integrity Test failed");
					return false;
				}
				msgSer = CryptographyUtils.decrypt(msgSer, iv);				
				break;
				}
			}
			
			this.msg = null;
			msgRe = null;
			msgHeard = null;
			serverR.putRegister(this);

			return true;
		} catch (IOException e) {
			try {
				this.socket.close();
				return false;				
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			return false;
		}
	}
	
	
//	public boolean read() {		
//		try {
//			
//			ByteBuffer header =  ByteBuffer.allocateDirect(8);
//			while(header.hasRemaining()){
//				if (this.socket.read(header) == -1)throw new IOException();
//			}
//			
//			header.flip();
//			int sizeint = header.getInt();
//			this.key = header.getInt();
//			ByteBuffer msgRet =  ByteBuffer.allocateDirect(sizeint);
//			while(msgRet.hasRemaining()){
//				if(this.socket.read(msgRet) == -1)throw new IOException();
//			}
//			
//			msgRet.flip();
//			this.out = new byte[sizeint];
//			msgRet.get(this.out);
//				
//			//test
//			// msg = (JCL_message)this.ReadObjectFromSock(this.key, this.out);
//			this.msg = null;	
//			
//			
//			serverR.putRegister(this);
//
//			return true;
//		} catch (IOException e) {
//			try {
//				this.socket.close();
//				return false;				
//			} catch (IOException e1) {
//				e1.printStackTrace();
//			}
//			return false;
//		}
//	}
//	public void send(byte[] obj, byte key, Short hash, byte[] mac) throws IOException {
	public void send(byte[] obj, byte key) throws Exception {		
		byte iv[] = CryptographyUtils.generateIV();
		int append;
		byte firstNumber;
		if ( ConnectorImpl.encryption ){
			obj = CryptographyUtils.crypt(obj, iv);
//			append = 61;
			append = 49;
			firstNumber = 1;
		}else{
//			append = 13;
			append = 1;
			firstNumber = 0;
		}
		ByteBuffer output;
		
		output = ByteBuffer.allocate(append + 4 + obj.length);
	
		
		byte secondNumber = (byte) key;		
		key = (byte)((firstNumber << 6) | secondNumber);
		

		output.putInt(obj.length + append);
		output.put(key);
		
/*		output.putShort(hash);
		output.put(mac);*/	

		if ( ConnectorImpl.encryption ){
			output.put(iv);
			output.put(CryptographyUtils.generateRegitrationKey(obj, iv));
		}
		output.put(obj);
		output.flip();
				
		while(output.hasRemaining()){
			this.socket.write(output);
		}		
		
		output = null;
	}
	
	
	public void sendB(byte[] obj) throws IOException {
		ByteBuffer output = ByteBuffer.allocate(obj.length);
		output.put(obj);
		output.flip();
		while(output.hasRemaining()){
			this.socket.write(output);
		}
	}
	
	public String getMacS() {
		
		StringBuilder sb = new StringBuilder(17);
		for (int i = 0; i < mac.length; i++) {
			sb.append(String.format("%02X%s", mac[i], (i < mac.length - 1) ? "-" : ""));		
		}
		return sb.toString();
	}

	public byte[] getMac() {
		return this.mac;
	}
	
	public void setMac(byte[] mac) {
		this.mac = mac;
	}
	
	public void sendBack() throws Exception{
		this.from.send(msgSer, key);
	}
	
	public byte[] getInput() {
		return this.msgSer;
	}
	
	public byte getKey() {
		return this.key;
	}
	
	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public Short getHash() {
		return hash;
	}

	public void setHash(Short hash) {
		this.hash = hash;
	}

	public void setKey(Byte key) {
		this.key = key;
	}
	
	public String getSocketAddress(){
			return this.host;
	}
	
	public void putOnQueue() {
		serverR.putRegister(this);
	}
	
	public JCL_message getMsg() {
		if (this.msg == null){
			this.msg = (JCL_message) this.ReadObjectFromSock(this.key, this.msgSer); 
		}
		
		return this.msg;
	}
	public void setMsg(JCL_message msg) {
		this.msg = msg;
	}
	
	public JCL_handler getFrom() {
		return from;
	}

	public void setFrom(JCL_handler from) {
		this.from = from;
	}
	
	public void sendTo() throws Exception {
		
		System.out.println("Size:"+this.from.getInput().length);
		System.out.println("Key:"+this.from.getKey());
		System.out.println(this.socket.socket().isClosed());
		System.out.println(this.socket.socket().isConnected());		
		
		
		this.send(this.from.getInput(), this.from.getKey());
	}
	
    protected Object ReadObjectFromSock(int key,byte[] obj){
  	   switch (key) {
   		case MSG:{
    			MessageImpl msgR = new MessageImpl();
    			ProtobufIOUtil.mergeFrom(obj, msgR, schema[msgR.getMsgType()]);
    			return msgR;
    		}
    		case MSG_COMMONS:{
    			MessageCommonsImpl msgR = new MessageCommonsImpl();
    			ProtobufIOUtil.mergeFrom(obj, msgR, schema[msgR.getMsgType()]);
    			return msgR;
    		}
    		case MSG_CONTROL:{
    			MessageControlImpl msgR = new MessageControlImpl();
    			ProtobufIOUtil.mergeFrom(obj, msgR, schema[msgR.getMsgType()]);
    			return msgR;
    		}
    		case MSG_GETHOST:{
    			MessageGetHostImpl msgR = new MessageGetHostImpl();
    			ProtobufIOUtil.mergeFrom(obj, msgR, schema[msgR.getMsgType()]);
    			return msgR;
    		}
    		case MSG_GLOBALVARS:{
    			MessageGlobalVarImpl msgR = new MessageGlobalVarImpl();
//    			MessageGlobalVarImpl msgR = (MessageGlobalVarImpl) schema[key].newMessage();
    			ProtobufIOUtil.mergeFrom(obj, msgR, schema[msgR.getMsgType()]);
    			return msgR;
    		}
    		case MSG_REGISTER:{
    			MessageRegisterImpl msgR = new MessageRegisterImpl();
    			ProtobufIOUtil.mergeFrom(obj, msgR, schema[msgR.getMsgType()]);
    			return msgR;
    		}
    		case MSG_RESULT:{
    			MessageResultImpl msgR = new MessageResultImpl();
    			ProtobufIOUtil.mergeFrom(obj, msgR, schema[msgR.getMsgType()]);
    			return msgR;
    		}
    		case MSG_TASK:{
    			MessageTaskImpl msgR = new MessageTaskImpl();
    			ProtobufIOUtil.mergeFrom(obj, msgR, schema[msgR.getMsgType()]);
    			return msgR;
    		}
    		
    		case MSG_LISTTASK:{
    			MessageListTaskImpl msgR = new MessageListTaskImpl();
    			ProtobufIOUtil.mergeFrom(obj, msgR, schema[msgR.getMsgType()]);
    			return msgR;
    		}
    		case MSG_GENERIC:{
    			MessageGenericImpl msgR = new MessageGenericImpl();
    			ProtobufIOUtil.mergeFrom(obj, msgR, schema[msgR.getMsgType()]);
    			return msgR;
    		}
    		case MSG_LONG:{
    			MessageLongImpl msgR = new MessageLongImpl();
    			ProtobufIOUtil.mergeFrom(obj, msgR, schema[msgR.getMsgType()]);
    			return msgR;
    		}
    		case MSG_BOOL:{
    			MessageBoolImpl msgR = new MessageBoolImpl();
    			ProtobufIOUtil.mergeFrom(obj, msgR,schema[msgR.getMsgType()]);
    			return msgR;
    		}
    		case MSG_GLOBALVARSOBJ:{
    			MessageGlobalVarObjImpl msgR = new MessageGlobalVarObjImpl();
    			ProtobufIOUtil.mergeFrom(obj, msgR, schema[msgR.getMsgType()]);
    			return msgR;
    		}
    		case MSG_LISTGLOBALVARS:{
    			MessageListGlobalVarImpl msgR = new MessageListGlobalVarImpl();
    			ProtobufIOUtil.mergeFrom(obj, msgR, schema[msgR.getMsgType()]);
    			return msgR;
    		}
	   		case MSG_METADATA:{
	   			MessageMetadataImpl msgR = new MessageMetadataImpl();
	   			ProtobufIOUtil.mergeFrom(obj, msgR, schema[msgR.getMsgType()]);
	   			return msgR;
	   		}
	   		case MSG_SENSOR:{
	   			MessageSensorImpl msgR = new MessageSensorImpl();
	   			ProtobufIOUtil.mergeFrom(obj, msgR, schema[msgR.getMsgType()]);
	   			return msgR;
	   		}
    		
    		default:{
    			System.out.println("Class not found!!");
    			return null;
    		}
  	   }
     }
}
