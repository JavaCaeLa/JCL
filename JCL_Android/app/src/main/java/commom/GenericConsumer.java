package commom;

import interfaces.kernel.JCL_message;
import io.protostuff.LinkedBuffer;
import io.protostuff.ProtobufIOUtil;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;

public abstract class GenericConsumer<S> extends Thread{
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


    protected void WriteObjectOnSock(JCL_message obj,JCL_handler handler, boolean complete) throws Exception {

        //Write data
        @SuppressWarnings("unchecked")
        byte[] Out = ProtobufIOUtil.toByteArray(obj, Constants.Serialization.schema[obj.getMsgType()], buffer.get());
        buffer.get().clear();
        byte key = (byte) obj.getMsgType();
        handler.send(Out,key,complete);
        //End Write data
    }

}