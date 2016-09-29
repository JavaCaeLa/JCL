package commom;

import interfaces.kernel.Constant;
import interfaces.kernel.JCL_message;
import io.protostuff.LinkedBuffer;
import io.protostuff.ProtobufIOUtil;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;

public abstract class GenericConsumer<S> extends Thread implements Constant{
	protected final GenericResource<S> re; 
	private AtomicBoolean kill = new AtomicBoolean(true);
	private static final ThreadLocal<LinkedBuffer> buffer = new ThreadLocal<LinkedBuffer>() { 
	    public LinkedBuffer initialValue() {
	        return LinkedBuffer.allocate(1048576);
	    }};
	    
	public GenericConsumer(GenericResource<S> re, AtomicBoolean kill){
		this.re = re;
		this.kill = kill;
	}
	
	public GenericConsumer(GenericResource<S> re){
		this.re = re;
	}
	
	@Override
	public void run(){
		try {
				S str = null;
				
				while(((!this.re.isFinished()) || (this.re.getNumOfRegisters() != 0)) && kill.get()){
					str = this.re.getRegister();
					if (str != null){
						//fazer algo com o recurso que foi consumido
						doSomething(str);
					} 
				}					
				
			} catch (Exception e) {
				
				e.printStackTrace();
			}		
	}
	
	protected abstract void doSomething(S str);
	
    
	protected void WriteObjectOnSock(JCL_message obj,JCL_handler handler) throws IOException {
		
    	//Write data
		@SuppressWarnings("unchecked")
		byte[] Out = ProtobufIOUtil.toByteArray(obj, schema[obj.getMsgType()], buffer.get());
		buffer.get().clear();
		byte key = (byte) obj.getMsgType();    			
		handler.send(Out,key);
		//End Write data
    }
    
//    @SuppressWarnings("unchecked")
//    protected Object ReadObjectFromSock(int key,byte[] obj){
// 	   switch (key) {
//  		case MSG:{
//   			MessageImpl msgR = new MessageImpl();
//   			ProtostuffIOUtil.mergeFrom(obj, msgR, schema[msgR.getMsgType()]);
//   			return msgR;
//   		}
//   		case MSG_COMMONS:{
//   			MessageCommonsImpl msgR = new MessageCommonsImpl();
//   			ProtostuffIOUtil.mergeFrom(obj, msgR, schema[msgR.getMsgType()]);
//   			return msgR;
//   		}
//   		case MSG_CONTROL:{
//   			MessageControlImpl msgR = new MessageControlImpl();
//   			ProtostuffIOUtil.mergeFrom(obj, msgR, schema[msgR.getMsgType()]);
//   			return msgR;
//   		}
//   		case MSG_GETHOST:{
//   			MessageGetHostImpl msgR = new MessageGetHostImpl();
//   			ProtostuffIOUtil.mergeFrom(obj, msgR, schema[msgR.getMsgType()]);
//   			return msgR;
//   		}
//   		case MSG_GLOBALVARS:{
//   			MessageGlobalVarImpl msgR = new MessageGlobalVarImpl();
//   			ProtostuffIOUtil.mergeFrom(obj, msgR, schema[msgR.getMsgType()]);
//   			return msgR;
//   		}
//   		case MSG_REGISTER:{
//   			MessageRegisterImpl msgR = new MessageRegisterImpl();
//   			ProtostuffIOUtil.mergeFrom(obj, msgR, schema[msgR.getMsgType()]);
//   			return msgR;
//   		}
//   		case MSG_RESULT:{
//   			MessageResultImpl msgR = new MessageResultImpl();
//   			ProtostuffIOUtil.mergeFrom(obj, msgR, schema[msgR.getMsgType()]);
//   			return msgR;
//   		}
//   		case MSG_TASK:{
//   			MessageTaskImpl msgR = new MessageTaskImpl();
//   			ProtostuffIOUtil.mergeFrom(obj, msgR, schema[msgR.getMsgType()]);
//   			return msgR;
//   		}
//   		
//   		case MSG_LISTTASK:{
//   			MessageListTaskImpl msgR = new MessageListTaskImpl();
//   			ProtostuffIOUtil.mergeFrom(obj, msgR, schema[msgR.getMsgType()]);
//   			return msgR;
//   		}
//   		case MSG_GENERIC:{
//   			MessageGenericImpl msgR = new MessageGenericImpl();
//   			ProtostuffIOUtil.mergeFrom(obj, msgR, schema[msgR.getMsgType()]);
//   			return msgR;
//   		}
//   		case MSG_LONG:{
//   			MessageLongImpl msgR = new MessageLongImpl();
//   			ProtostuffIOUtil.mergeFrom(obj, msgR, schema[msgR.getMsgType()]);
//   			return msgR;
//   		}
//   		case MSG_BOOL:{
//   			MessageBoolImpl msgR = new MessageBoolImpl();
//   			ProtostuffIOUtil.mergeFrom(obj, msgR,schema[msgR.getMsgType()]);
//   			return msgR;
//   		}
//   		case MSG_GLOBALVARSOBJ:{
//   			MessageGlobalVarObjImpl msgR = new MessageGlobalVarObjImpl();
//   			ProtostuffIOUtil.mergeFrom(obj, msgR, schema[msgR.getMsgType()]);
//   			return msgR;
//   		}
//   		case MSG_LISTGLOBALVARS:{
//   			MessageListGlobalVarImpl msgR = new MessageListGlobalVarImpl();
//   			ProtostuffIOUtil.mergeFrom(obj, msgR, schema[msgR.getMsgType()]);
//   			return msgR;
//   		}
//   		
//   		default:{
//   			System.out.println("Class not found!!");
//   			return null;
//   		}
// 	   }
//    }
}
