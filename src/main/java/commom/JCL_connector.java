package commom;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Arrays;
import java.util.concurrent.locks.ReentrantLock;

import interfaces.kernel.Constant;
import interfaces.kernel.JCL_message;
import io.protostuff.LinkedBuffer;
import io.protostuff.ProtobufIOUtil;

public class JCL_connector implements Runnable,Constant {

	private GenericResource<JCL_handler> serverR;
	public static int buffersize = 2097152;
	private SocketChannel Socket;
//	private ReentrantLock lock;
	private SelectionKey sk;
	private Selector sel;
	private JCL_message msg;
	private byte[] mac;
	
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
	
	public JCL_connector(){
		// TODO Auto-generated constructor stub
	}
	
 //   public JCL_connector(SocketChannel Socket, SelectionKey sk, Selector sel, ReentrantLock lock,GenericResource<JCL_handler> serverR, JCL_message msg,byte[] mac){
    public JCL_connector(SocketChannel Socket, SelectionKey sk, Selector sel,GenericResource<JCL_handler> serverR, JCL_message msg,byte[] mac){

    	// TODO Auto-generated constructor stub
		this.Socket = Socket;
		this.sk = sk;
//		this.lock = lock;
		this.sel =sel;
		this.serverR = serverR;
        this.msg = msg;
        this.mac = mac;
	}

	@Override
	public void run(){
		// TODO Auto-generated method stub
		try {
			if (sk.isValid() & sk.isConnectable()){
				//close pending connections
				if (Socket.isConnectionPending()) {				
					Socket.finishConnect();				
				}
				
				if (msg!=null){
					send(msg);
				}else{
					sendHello((byte)-100,(short)0,this.mac);
				}
				
				new JCL_handler(sel, Socket,this.serverR);					
			}
		} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
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
	
//	public boolean send(JCL_message msgS, Short idHost) {
//		// TODO Auto-generated method stub
//		LinkedBuffer buffer = LinkedBuffer.allocate(1048576);
//		try {			
//			//Write data
//			@SuppressWarnings("unchecked")
//			byte[] Out = ProtobufIOUtil.toByteArray(msgS, schema[msgS.getMsgType()], buffer);
//
//			buffer.clear();
//			int size = Out.length;
//									
//			ByteBuffer Send =  ByteBuffer.allocate(10+size);			
//			if(idHost != null) Send.putShort(idHost); 
//			if(mac != null) Send.put(mac);
//			Send.put((byte)msgS.getMsgType());
//			Send.put(Out);
//			byte last = (byte)~msgS.getMsgType();
//			Send.put(last);
//			Send.flip();
//				
//			while(Send.hasRemaining()){
//				Socket.write(Send);
//			}
//						
//			//End Write data	
//
//			return !Send.hasRemaining();
//
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			try {
//				Socket.close();
//			} catch (IOException e1) {
//				// TODO Auto-generated catch block
//				e1.printStackTrace();
//			}
//			e.printStackTrace();
//			System.err.println("error in send method");
//			return false;
//		}
//	}

	public boolean send(JCL_message msgS) throws IOException {
		
		
		LinkedBuffer buffer = LinkedBuffer.allocate(buffersize);
		
		byte key = (byte) msgS.getMsgType();
		byte[] obj = ProtobufIOUtil.toByteArray(msgS, schema[key], buffer);
		
		
		ByteBuffer output = ByteBuffer.allocate(5 + obj.length);
		
//		byte firstNumber = 0;
//		byte secondNumber = (byte) key;		
//		if(mac != null) {
//			firstNumber = 1;
//		}
//		if(hash != null){
//			firstNumber = 2;
//			output.putInt(obj.length+13);		
//		} 

		
//		byte keyF = (byte)((firstNumber << 6) | secondNumber);	
		
		
//		if (firstNumber==0){
//			
//		}else if{
//			output.putInt(obj.length+11);								
//		}else if{
//			output.putInt(obj.length+11);					
//		}
		
		output.putInt(obj.length+5);
		output.put(key);
		
//		if(hash != null){output.putShort(hash);} 		
//		if(mac != null) {output.put(mac);}		

		output.put(obj);
		output.flip();
		
		while(output.hasRemaining()){
			Socket.write(output);
		}
		
		return !output.hasRemaining();
	}

//	public boolean send(JCL_message msgS, Short hash, byte[] mac) throws IOException {
//		
//		LinkedBuffer buffer = LinkedBuffer.allocate(buffersize);
//		try {
//			
//			@SuppressWarnings("unchecked")
//			byte key = (byte) msgS.getMsgType();
//			byte[] Out = ProtobufIOUtil.toByteArray(msgS, schema[key], buffer);
//
//			ByteBuffer output = ByteBuffer.allocate(14 + Out.length);		
//			byte firstNumber = 0;
//			byte secondNumber = key;		
//			if(mac != null) {firstNumber = 1;}
//			if(hash != null){firstNumber = 2;} 
//
//		
//			byte keyF = (byte)((firstNumber << 6) | secondNumber);	
//
//			output.put(keyF);
//			if(hash != null){output.putShort(hash);} 		
//			if(mac != null) {output.put(mac);}
//			output.put(Out);
//			output.putInt(output.position()+4);
//		//	output.put(crc8(output.position()+2));
//		//	output.put(keyF);
//			output.flip();
//			while(output.hasRemaining()){
//				Socket.write(output);
//			}
//			
//
//			return !output.hasRemaining();
//		
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			try {
//				Socket.close();
//			} catch (IOException e1) {
//				// TODO Auto-generated catch block
//				e1.printStackTrace();
//			}
//			e.printStackTrace();
//			System.err.println("error in send method");
//			return false;
//		}
//	}
	
//	public void send(byte[] obj, byte key, Short hash, byte[] mac) throws IOException {
//		
//		ByteBuffer output = ByteBuffer.allocate(14 + obj.length);
//	//	System.out.println("Ini Resposta");
//		
//		byte firstNumber = 0;
//		byte secondNumber = (byte) key;		
//		if(mac != null) {firstNumber = 1;}
//		if(hash != null){firstNumber = 2;} 
//
//		
//		byte keyF = (byte)((firstNumber << 6) | secondNumber);	
//		
//		output.put(keyF);
//		if(hash != null){output.putShort(hash);} 		
//		if(mac != null) {output.put(mac);}		
//		output.put(obj);
//		output.putInt(output.position()+4);
//	//	output.put(crc8(output.position()+2));
//	//	output.put(keyF);
//		output.flip();
//		while(output.hasRemaining()){
//			Socket.write(output);
//		}
//	//	System.out.println("Fim Resposta");
//	}
	
	
	public void send(byte[] obj, byte key) throws IOException {
		
		ByteBuffer output = ByteBuffer.allocate(5 + obj.length);
		
//		byte firstNumber = 0;
//		byte secondNumber = (byte) key;		
//		if(mac != null) {
//			firstNumber = 1;
//		}
//		if(hash != null){
//			firstNumber = 2;
//			output.putInt(obj.length+13);		
//		} 

		
//		byte keyF = (byte)((firstNumber << 6) | secondNumber);	
		
		
//		if (firstNumber==0){
//			
//		}else if{
//			output.putInt(obj.length+11);								
//		}else if{
//			output.putInt(obj.length+11);					
//		}
		
		output.putInt(obj.length+5);
		output.put(key);
		
//		if(hash != null){output.putShort(hash);} 		
//		if(mac != null) {output.put(mac);}		

		output.put(obj);
		output.flip();
		
		while(output.hasRemaining()){
			Socket.write(output);
		}
		output = null;
	}
	
	public boolean sendHello(byte type,Short hash, byte[] mac) {
		// TODO Auto-generated method stub
		try {			
			@SuppressWarnings("unchecked")
			byte key = 127;
			byte[] Out= new byte[2];
			Out[0] = 8;
			Out[1] = type;
			
			ByteBuffer output = ByteBuffer.allocate(15);		
			output.putInt(15);
			output.put(key);
			output.putShort(hash);
			output.put(mac);
			output.put(Out);
			
			
//			byte firstNumber = 0;
//			byte secondNumber = key;		
//			if(mac != null) {firstNumber = 1;}
//			if(hash != null){firstNumber = 2;} 
//
//		
//			byte keyF = (byte)((firstNumber << 6) | secondNumber);	
//		
//			output.put(keyF);
//			if(hash != null){output.putShort(hash);} 		
//			if(mac != null) {output.put(mac);}			
//			output.put(Out);			
//			output.putInt(output.position()+4);
		//	output.put(crc8(output.position()+2));
		//	output.put(keyF);
			output.flip();
			while(output.hasRemaining()){
				Socket.write(output);
			}
		
			return !output.hasRemaining();

		} catch (Exception e) {
			// TODO Auto-generated catch block
			try {
				Socket.close();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			e.printStackTrace();
			System.err.println("error in send method");
			return false;
		}
	}
	
	public GenericResource<JCL_handler> getServerR() {
		return serverR;
	}

	public void setServerR(GenericResource<JCL_handler> serverR) {
		this.serverR = serverR;
	}

	public SocketChannel getSocket() {
		return Socket;
	}

	public void setSocket(SocketChannel socket) {
		Socket = socket;
	}

	public SelectionKey getSk() {
		return sk;
	}

	public void setSk(SelectionKey sk) {
		this.sk = sk;
	}

	public Selector getSel() {
		return sel;
	}

	public void setSel(Selector sel) {
		this.sel = sel;
	}

	public JCL_message getMsg() {
		return msg;
	}

	public void setMsg(JCL_message msg) {
		this.msg = msg;
	}

	public byte[] getMac() {
		return mac;
	}

	public void setMac(byte[] mac) {
		this.mac = mac;
	}
}
