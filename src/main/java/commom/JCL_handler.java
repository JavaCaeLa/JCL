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
import interfaces.kernel.JCL_message;
import io.protostuff.ProtobufIOUtil;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Arrays;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;

public class JCL_handler implements Runnable{

	
	private GenericResource<JCL_handler> serverR;
	public static AtomicInteger RegisterMsg;
	private SocketChannel socket;
	private ByteBuffer msgRe,msgHeard;
	private JCL_handler from;
	private SelectionKey sk;
	private JCL_message msg;
	private byte[] msgSer;
	private String host;
	private Short port;
	private byte[] mac;
	private Byte key;
	protected Selector selector;
	
	public JCL_handler(Selector sel, SocketChannel c, GenericResource<JCL_handler> serverR) throws IOException {

		this.serverR = serverR;
		this.socket = c;
		this.socket.socket().setTcpNoDelay(true);
		this.socket.socket().setKeepAlive(true);
		this.socket.configureBlocking(false);
		this.host = this.socket.socket().getInetAddress().getHostAddress();
		sel.wakeup();
		this.sk = this.socket.register(sel, SelectionKey.OP_READ);
		this.sk.attach(this);
		sel.wakeup();	
		selector = sel;
	}

	// class Handler continued
	public void run(){

		if ((this.sk.isValid()) && (this.sk.isReadable())){	
			 this.read();
		}
	}
	
	
	public boolean read() {		
		try {

			if (this.from==null){
							
				msgHeard =  ByteBuffer.allocateDirect(5);
						
				while(msgHeard.hasRemaining()){				
					if (this.socket.read(msgHeard) == -1)throw new IOException();			
				}
				
				
			
				msgHeard.flip();
				int size = msgHeard.getInt();
				byte first = msgHeard.get(4);
				msgHeard.limit(4);
				byte k = (byte)(first & 0x3F);
				
//				System.out.println("Read key:"+msgHeard.get(4));			
//				System.out.println("Read size:"+size);

				
				if (k == 5){
					RegisterMsg.incrementAndGet();				
				 }
				
				msgRe =  ByteBuffer.allocateDirect(size);
				msgRe.put(first);

			
				while(msgRe.hasRemaining()){
					if (this.socket.read(msgRe) == -1)throw new IOException();
				}			

			
			this.msg = null;
			msgSer = null;
			port = null;
			mac = null;
			key = null;
			
			serverR.putRegister(this);
			
			
//			for(int cont=0;cont<msgHeard.limit();cont++){
//				System.out.println(msgHeard.get(cont));
//			}
			
			return true;
			
		}else{
			
//			System.out.println("Retorno para o user");
						
			ByteBuffer msgHeard =  ByteBuffer.allocateDirect(4);
						
			while(msgHeard.hasRemaining()){				
				if (this.socket.read(msgHeard) == -1)throw new IOException();
			}
			
			int size = msgHeard.getInt(0);
			this.from.sendB(msgHeard);
			
			ByteBuffer msgRe =  ByteBuffer.allocateDirect(size);
			while(msgRe.hasRemaining()){
				if (this.socket.read(msgRe) == -1)throw new IOException();
			}
			
			this.from.sendB(msgRe);
			this.from = null;
			
			return true;
		}
			
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
	public void send(byte[] obj, byte key, boolean complete) throws Exception {		
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
		
		if (complete)append = append + 8;
		 ByteBuffer output = ByteBuffer.allocate(append + 4 + obj.length);
	
		
		byte secondNumber = (byte) key;		
		key = (byte)((firstNumber << 6) | secondNumber);
		
		
		output.putInt(obj.length + append);
		output.put(key);
		if(complete){
			//Arrumar valores
			output.putShort((short)0); 
			output.put(macConvert("00-00-00-00-00-00"));
		}
		
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
	
	
	public void sendB(ByteBuffer obj) throws IOException {
//		ByteBuffer output = ByteBuffer.allocate(obj.length);
//		output.put(obj);
//		output.flip();		
		obj.flip();
		while(obj.hasRemaining()){
			this.socket.write(obj);
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
	
	public String getMacS() {
		
		StringBuilder sb = new StringBuilder(17);
		for (int i = 0; i < mac.length; i++) {
			sb.append(String.format("%02X%s", mac[i], (i < mac.length - 1) ? "-" : ""));		
		}
		return sb.toString();
	}

	public byte[] getMac() {
		
		if (this.mac==null){
			msgRe.flip();
			byte first = msgRe.get();
			byte start = (byte) ((first >> 6) & (byte) 0x03);
			this.key = (byte) (first & 0x3F);
			port = msgRe.getShort();
			this.mac = new byte[6];
			msgRe.get(this.mac);
		}
		return this.mac;
	}
	
	public void setMac(byte[] mac) {
		this.mac = mac;
	}
	
	public void sendBack() throws Exception{
		this.from.send(msgSer, key,false);
	}
	
	public byte[] getInput() {
		
		if (this.msgSer==null){
			
			msgRe.flip();
			byte first = msgRe.get();
			byte start = (byte) ((first >> 6) & (byte) 0x03);
			this.key = (byte) (first & 0x3F);
			port = msgRe.getShort();
			this.mac = new byte[6];
			msgRe.get(this.mac);
			
//			System.out.println("Input key:"+first);			
//			System.out.println("Input size:"+msgHeard.getInt(0));
//			System.out.println("Input limit:"+msgHeard.limit());
//			System.out.println("Input position:"+msgHeard.position());

//			for(int cont=0;cont<msgHeard.limit();cont++){
//				System.out.println(msgHeard.get(cont));
//			}
				
			switch (start) {
			case 0:{				
				this.msgSer = new byte[(msgHeard.getInt(0)-msgRe.position())];				
				msgRe.get(msgSer);
				break;
				}
			case 1:{	// crypted message
				byte iv[] = new byte[16];
				byte regKey[] = new byte[32];
				msgRe.get(iv);
				msgRe.get(regKey);
				this.msgSer = new byte[(msgHeard.getInt(0)-msgRe.position())];				
				msgRe.get(msgSer);
				if ( !new String(regKey).equals(new String(CryptographyUtils.generateRegitrationKey(msgSer, iv)))) {
					System.out.println("Message Integrity Test failed");
					return null;
				}
				msgSer = CryptographyUtils.decrypt(msgSer, iv);				
				break;
				}
			}
		}
		
		return this.msgSer;
	}
	
	public byte getKey() {
		if(this.key==null){
			byte first = msgRe.get(0);
			this.key = (byte) (first & 0x3F);
		}
		
		return this.key;
	}
	
	public String getHost() {
		return host;
	}
	
	public ByteBuffer getMsgHeard() {
		return msgHeard;
	}

	public void setMsgHeard(ByteBuffer msgHeard) {
//		System.out.println("set heard!!!");
		this.msgHeard = msgHeard;
	}

	public ByteBuffer getMsgRe() {
		return msgRe;
	}

	public void setMsgRe(ByteBuffer msgRe) {
		this.msgRe = msgRe;
	}
	public void setHost(String host) {
		this.host = host;
	}

	public Short getport() {
		if (this.port==null){
			port = msgRe.getShort(1);
		}
		
		return port;
	}

	public void setport(Short hash) {
		this.port = hash;
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
			this.getInput();
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
		
	public static AtomicInteger getRegisterMsg() {
		return RegisterMsg;
	}

	public static void setRegisterMsg(AtomicInteger registerMsg) {
		RegisterMsg = registerMsg;
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
