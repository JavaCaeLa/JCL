package jcl.connection;

import android.os.AsyncTask;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import implementations.dm_kernel.MessageSensorImpl;
import io.protostuff.LinkedBuffer;
import io.protostuff.ProtostuffIOUtil;
import io.protostuff.Schema;
import io.protostuff.runtime.RuntimeSchema;
//import jcl.connection.messages.JCL_message;

/**
 * Created by estevao on 01/06/16.
 */
public class ConnectionAsync2 extends AsyncTask<Object,Object,Object> {
    String ip;
    int port;
    MessageSensorImpl jcl_message;

    public ConnectionAsync2(String ip, int port, MessageSensorImpl jcl_message) {
        this.ip = ip;
        this.port = port;
        this.jcl_message = jcl_message;
    }

    @Override
    protected Object doInBackground(Object... params) {
        sendReceive();
        return null;
    }

    public void sendReceive(){
        try {
            Socket s = new Socket(ip, port);
            ObjectOutputStream out = new ObjectOutputStream(
                    new BufferedOutputStream(s.getOutputStream()));

            Schema<MessageSensorImpl> schema = RuntimeSchema.getSchema(MessageSensorImpl.class);
            LinkedBuffer buffer = getApplicationBuffer();

            byte[] protostuff = null;
            try {
                protostuff = ProtostuffIOUtil.toByteArray(jcl_message, schema, buffer);
            } finally {
                buffer.clear();
            }

            out.writeObject(protostuff);
            out.flush();

            ObjectInputStream in = new ObjectInputStream(
                    new BufferedInputStream(s.getInputStream()));
            String response = (String) in.readObject();

            System.err.println("client: " + response);

            in.close();
            out.close();
            s.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    static final ThreadLocal<LinkedBuffer> localBuffer = new ThreadLocal<LinkedBuffer>() {
        public LinkedBuffer initialValue() {
            return LinkedBuffer.allocate(2048);
        }
    };

    public static LinkedBuffer getApplicationBuffer() {
        return localBuffer.get();
    }
}
