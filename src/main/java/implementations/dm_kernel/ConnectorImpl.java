package implementations.dm_kernel;

import interfaces.kernel.JCL_connector;
import interfaces.kernel.JCL_message;
import interfaces.kernel.JCL_message_control;
import interfaces.kernel.JCL_message_result;
import io.protostuff.LinkedBuffer;
import io.protostuff.ProtobufIOUtil;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;


public class ConnectorImpl implements JCL_connector {

	public static int buffersize = 2097152;
	private SocketChannel s;
	private boolean verbose = true;
	private String mac = "00-00-00-00-00-00";
	private short port = 0;
	//	private int hash = 0;
	public static int timeout = 5000;
	public static int SlaveSize = 1;
	public static int PGterm = 1;
//	static final byte[] crcTbl;    
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
    
	private static final ConcurrentMap<String,SocketChannel> socketList = new ConcurrentHashMap<String,SocketChannel>();
	private static final ThreadLocal<LinkedBuffer> buffer = new ThreadLocal<LinkedBuffer>() { 
	    public LinkedBuffer initialValue() {
	        return LinkedBuffer.allocate(buffersize);
	    }};
		    
	public ConnectorImpl() {
		super();
	}
	
	public ConnectorImpl(boolean verbose) {
		super();
		this.verbose = verbose;
	}
		
	public static void closeSocketMap(){		
		
		try {
		for(Entry<String, SocketChannel> hostSock:socketList.entrySet()){			
			
				hostSock.getValue().close();			
		}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		socketList.clear();
	}
	
	@Override
	public boolean  connect(String host, int port, String mac) {
		// TODO Auto-generated method stub
		try {
			if (mac!=null){
				this.mac = mac;
			}
			
			long id= Thread.currentThread().getId();
			this.s = socketList.get(host+":"+port+":"+id);
			if(this.s!=null){ 
				return true;
				}
			else{	
					Selector selector;
					selector = Selector.open();
					this.s = SocketChannel.open();
					this.s.configureBlocking(false);					
					this.s.socket().setTcpNoDelay(true);
					this.s.socket().setKeepAlive(true);

					this.s.register(selector, SelectionKey.OP_CONNECT);
					this.s.connect(new java.net.InetSocketAddress(host, port));
					while(selector.select(timeout)>0){
						Set<SelectionKey> keys = selector.selectedKeys();
						Iterator<SelectionKey> its = keys.iterator();
						while (its.hasNext()){
							SelectionKey key = its.next();
							its.remove();
							if (key.isValid() & key.isConnectable()){
								//close pending connections
								if (this.s.isConnectionPending()) {
									this.s.finishConnect();
								}
								socketList.put(host+":"+port+":"+id,this.s);
								key.cancel();
								selector.close();
								return true;
							}
						}					
				return true;	
					}
						System.err.println("problem in connect method (Timeout) " + host);
					return false;
			}
			
		} catch (Exception e) {
			System.err.println("problem in connect method " + host);
			if(verbose)e.printStackTrace();			
			return false;
		}
	}

	@Override
	public JCL_message_result sendReceive(JCL_message msg, Short idHost){
		// TODO Auto-generated method stub
		JCL_message_result fromServer = null;
		try {
			
			fromServer = (JCL_message_result)this.sendReceiveG(msg, idHost);			
			return fromServer;

		} catch (Exception e) {
			// TODO Auto-generated catch block
			try {
				this.s.close();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			if(verbose){
				e.printStackTrace();
			}
			System.err.println("error in sendreceive method");
			return null;
		}

	}
	
	@Override
	public JCL_message_control sendReceive(JCL_message_control msg, Short idHost) {
		// TODO Auto-generated method stub
		JCL_message_control fromServer = null;
		try {			
						
			fromServer = (JCL_message_control)this.sendReceiveG(msg, idHost);			
			return fromServer;

		} catch (Exception e) {
			// TODO Auto-generated catch block
			try {
				this.s.close();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			if(verbose){
				e.printStackTrace();
			}
			System.err.println("error in sendreceive method");
			return null;
		}

	}
	
	
   @Override
	public JCL_message sendReceiveG(JCL_message msg, Short idHost) {
		// TODO Auto-generated method stub
		JCL_message fromServer = null;
		try {			
			//Write data
			@SuppressWarnings("unchecked")
			byte[] Out = ProtobufIOUtil.toByteArray(msg, schema[msg.getMsgType()], buffer.get());

			buffer.get().clear();
			int size = Out.length;
			
			byte firstNumber = 1;
			byte secondNumber = (byte) msg.getMsgType();

//			if(mac != null) {firstNumber = 1;}
//			if(idHost != null){firstNumber = 2;} 

			
			ByteBuffer Send =  ByteBuffer.allocate(13+size);	
			byte key = (byte)((firstNumber << 6) | secondNumber);
			
			Send.putInt(size+13);
			Send.put(key);			
			Send.putShort(port); 
			Send.put(macConvert(this.mac));			
			Send.put(Out);

	//		byte crc = crc8(Send.position()+2);
	//		Send.put(key);
			Send.flip();
			
//			byte[] envi = Send.array();
//			System.out.println("key:"+key+" crc:"+crc);
//			System.out.println("final"+Send.limit());
//			System.out.println("final"+envi.length);
//			System.out.println("final"+envi[Send.limit()-2]);
//			System.out.println("final"+envi[Send.limit()-1]);
//			
//			for(int i=1;i < (envi.length);i++){
//				if((envi[i-1]==crc) && (envi[i]==key)){
//					System.out.println("pos:"+i);
//				}
//			}
						
			while(Send.hasRemaining()){
				this.s.write(Send);
			}
			//End Write data	
			
			
			//Read result
			ByteBuffer msgRet =  ByteBuffer.allocateDirect(buffersize);
			
			
//			while(!((msgRet.position()>4) && (msgRet.position()==msgRet.get(msgRet.position()-2)) && (msgRet.get(0) == msgRet.get(msgRet.position()-1)))){				
				
			while(!((msgRet.position()>4) && (msgRet.position()==msgRet.getInt(0)))){				
				this.s.read(msgRet);
//				System.out.println("Tam:"+msgRet.position());
//				System.out.println("Tam envia:"+msgRet.getInt(0));
//				System.out.println("key:"+msgRet.get(4));
			}
			
			msgRet.flip();
		//	key = (byte)(msgRet.get() & 0x3F);
			msgRet.position(4);
			key = msgRet.get();
			byte[] obj = new byte[(msgRet.limit()-msgRet.position())];
			msgRet.get(obj);			
			
			fromServer = this.desProtoStuff(key, obj);
			//End read result
			
			Send = null;
			msgRet = null;
			
			return fromServer;

		} catch (Exception e) {
			// TODO Auto-generated catch block
			try {
				this.s.close();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			if(verbose){
				e.printStackTrace();
			}
			System.err.println("error in sendreceiveG method");
			e.printStackTrace();

			return null;
		}
	}
   
   @Override
	public byte[] sendReceiveB(byte[] msg, byte key){
		// TODO Auto-generated method stub
		try {
			
//			int size = msg.length;
//			byte firstNumber = 0;
//			byte secondNumber = (byte) key;
//			byte keyN = (byte)((firstNumber << 6) | secondNumber);
			ByteBuffer Send =  ByteBuffer.allocate(5+msg.length);
			Send.putInt(msg.length+5);
			Send.put(key);
			Send.put(msg);
		//	Send.put(key);
			Send.flip();
			while(Send.hasRemaining()){
				this.s.write(Send);
			}
			//End Write data
		
			//Read result
			ByteBuffer msgRet =  ByteBuffer.allocateDirect(buffersize);

//			while(!((msgRet.position()>3) && (crc8(msgRet.position())==msgRet.get(msgRet.position()-2)) && (msgRet.get(0) == msgRet.get(msgRet.position()-1)))){				
//				this.s.read(msgRet);
//			}
			
			while(!((msgRet.position()>4) && (msgRet.position()==msgRet.getInt(0)))){				
				this.s.read(msgRet);
			}
			
			msgRet.flip();
		//	key = (byte)(msgRet.get() & 0x3F);
		//	key = msgRet.get();
			byte[] obj = new byte[(msgRet.limit()-msgRet.position())];
			msgRet.get(obj);
			
			return obj;
			//End read result			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			try {
				this.s.close();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			if(verbose){
				e.printStackTrace();
			}
			System.err.println("error in sendReceiveB method");
			return null;
		}

	}

   
	@Override
	public boolean send(JCL_message msg, Short idHost) {
		// TODO Auto-generated method stub

		try {			
			@SuppressWarnings("unchecked")
			byte[] Out = ProtobufIOUtil.toByteArray(msg, schema[msg.getMsgType()], buffer.get());

			buffer.get().clear();
			int size = Out.length;
			
			byte firstNumber = 1;
			byte secondNumber = (byte) msg.getMsgType();

//			if(mac != null) {firstNumber = 1;}
//			if(idHost != null){firstNumber = 2;} 

			
			ByteBuffer Send =  ByteBuffer.allocate(13+size);	
			byte key = (byte)((firstNumber << 6) | secondNumber);
			
			Send.putInt(size+13);
			Send.put(key);			
			Send.putShort(port); 
			Send.put(macConvert(this.mac));			
			Send.put(Out);

	//		byte crc = crc8(Send.position()+2);
	//		Send.put(key);
			Send.flip();
			
//			byte[] envi = Send.array();
//			System.out.println("key:"+key+" crc:"+crc);
//			System.out.println("final"+Send.limit());
//			System.out.println("final"+envi.length);
//			System.out.println("final"+envi[Send.limit()-2]);
//			System.out.println("final"+envi[Send.limit()-1]);
//			
//			for(int i=1;i < (envi.length);i++){
//				if((envi[i-1]==crc) && (envi[i]==key)){
//					System.out.println("pos:"+i);
//				}
//			}
						
			while(Send.hasRemaining()){
				this.s.write(Send);
			}
			//End Write data	
						
			return !Send.hasRemaining();

		} catch (Exception e) {
			// TODO Auto-generated catch block
			try {
				this.s.close();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			if(verbose){
				e.printStackTrace();
			}
			System.err.println("error in sendreceive method");
			return false;
		}
	}
	
	@Override
	public JCL_message receive() {
		// TODO Auto-generated method stub
		JCL_message fromServer = null;
		try {				

			//Read result
			ByteBuffer msgRet =  ByteBuffer.allocateDirect(buffersize);

//			while(!((msgRet.position()>3) && (crc8(msgRet.position())==msgRet.get(msgRet.position()-2)) && (msgRet.get(0) == msgRet.get(msgRet.position()-1)))){				
//				this.s.read(msgRet);
//			}
			
			while(!((msgRet.position()>4) && (msgRet.position()==msgRet.getInt(0)))){				
				this.s.read(msgRet);
			}
			
			msgRet.flip();
//			byte key = (byte)(msgRet.get() & 0x3F);
			msgRet.position(4);
			byte key = msgRet.get();			
			byte[] obj = new byte[(msgRet.limit()-msgRet.position())];
			msgRet.get(obj);
			
			fromServer = (JCL_message)this.desProtoStuff(key, obj);
			//End read result
			
			return fromServer;

		} catch (Exception e) {
			// TODO Auto-generated catch block
			try {
				this.s.close();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			if(verbose){
				e.printStackTrace();
			}
			System.err.println("error in sendreceive method");
			return null;
		}

	}
	
   @SuppressWarnings("unchecked")
   public JCL_message desProtoStuff(int key,byte[] obj){
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
	@Override
	public boolean disconnect() {
		try {
//			this.selector.close();
//			this.s.close();
	 /*		if (in != null)
				in.close();

			if (out != null) {
				out.flush();
				out.close();
			}

			if (s != null)
				s.close();
   */
			return true;
		} catch (Exception e) {
			System.err.println("problem in disconnect method");
			return false;
		}
	}
	
	public byte[] macConvert(String macAddress){
		
		String[] macAddressParts = macAddress.split("-");
		byte[] macAddressBytes = new byte[6];

		if (macAddressParts.length == 6){
		// convert hex string to byte values
			for(int i=0; i<6; i++){
				Integer hex = Integer.parseInt(macAddressParts[i], 16);
				macAddressBytes[i] = hex.byteValue();
			}
		
		}else{
			String[] ipAddressParts = macAddress.split("\\.");
			for(int i=0; i<4; i++){
			    Integer integer = Integer.parseInt(ipAddressParts[i]);
			    macAddressBytes[i] = integer.byteValue();
			}
			Integer integer = 0;
			macAddressBytes[4] =  integer.byteValue();
			macAddressBytes[5] =  integer.byteValue();
		}		
			return macAddressBytes;
	}

	public byte[] ipConvert(String ipAddress){
		String[] ipAddressParts = ipAddress.split("\\.");

		// convert int string to byte values
		byte[] ipAddressBytes = new byte[4];
		for(int i=0; i<4; i++){
		    Integer integer = Integer.parseInt(ipAddressParts[i]);
		    ipAddressBytes[i] = integer.byteValue();
		}	
	return ipAddressBytes;	
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

}
