package jcl.connection;

import android.content.res.ObbInfo;
import android.os.AsyncTask;
import android.util.Log;

import implementations.dm_kernel.ConnectorImpl;
import interfaces.kernel.JCL_message;
import interfaces.kernel.JCL_message_bool;

//import jcl.connection.messages.JCL_message;
//import jcl.connection.messages.JCL_message_bool;

/**
 * Created by estevao on 01/06/16.
 */
public class ConnectionAsync extends AsyncTask<Object,Object,Object> {
    String ip;
    int port;
    JCL_message jcl_message;

    public ConnectionAsync(String ip, int port, JCL_message jcl_message) {
        this.ip = ip;
        this.port = port;
        this.jcl_message = jcl_message;
    }

    @Override
    protected Object doInBackground(Object... params) {
        ConnectorImpl co = new ConnectorImpl();

        co.connect(ip, port,null);
        JCL_message_bool msg = (JCL_message_bool) co.sendReceiveG(jcl_message,null);
        boolean resp = false;
        if (msg!=null && msg.getRegisterData()!=null)
            resp = msg.getRegisterData()[0];
        co.disconnect();
//        if (resp)
//            Log.e("Con","enviou");
//        else
//            Log.e("Con","não enviou");
        return new Object();
    }
}
