package implementations.dm_kernel.IoTuser;

import android.util.Log;

import com.hpc.jcl_android.JCL_ANDROID_Facade;

import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.MqttPersistenceException;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.ListIterator;

import implementations.dm_kernel.ConnectorImpl;
import implementations.dm_kernel.MessageGenericImpl;
import implementations.dm_kernel.user.JCL_FacadeImpl;
import interfaces.kernel.JCL_IoTfacade;
import interfaces.kernel.JCL_connector;
import interfaces.kernel.JCL_facade;
import interfaces.kernel.JCL_message_generic;
import interfaces.kernel.JCL_message_result;

public class JCL_Context {

    private String contextNickname;
    private JCL_Expression expression;
    private boolean triggered;
    private ArrayList<JCL_Action> actionList;
    private float[] value;
    boolean mqttContext;


    public JCL_Context(JCL_Expression expression, String contextNickname) {
        this.expression = expression;
        this.contextNickname = contextNickname;
        triggered = false;
        actionList = new ArrayList<>();
        this.mqttContext = false;

    }

    public JCL_Context(JCL_Expression expression, String contextNickname, boolean mqttContext) {
        this.expression = expression;
        this.contextNickname = contextNickname;
        triggered = false;
        actionList = new ArrayList<>();
        this.mqttContext = mqttContext;
    }

    public void check(float[] value) {
        this.value = value;
        if (expression.check(value)) {
            if (!mqttContext)
                action();
            else
                mqttAction(value);
        }
        else {
            if (mqttContext && triggered)
                mqttAction("done");
            triggered = false;
        }
    }

    private void mqttAction(Object value) {
        try {
            triggered = true;
            JCL_ANDROID_Facade jcl = JCL_ANDROID_Facade.getInstance();
            if (jcl.getMqttClient()!=null && jcl.getMqttClient().isConnected()) {
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                ObjectOutputStream os = new ObjectOutputStream(out);

                os.writeObject(value);


                MqttMessage message = new MqttMessage((out.toByteArray()));
                message.setQos(2);
                jcl.getMqttClient().publish(contextNickname, message);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (MqttPersistenceException e) {
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }


    public void action() {
        if (!triggered) {
            System.out.println("*** Context reached ***");
            triggered = true;
            ListIterator<JCL_Action> it = actionList.listIterator();
            while (it.hasNext()) {
                JCL_Action action = it.next();
                if ( action.isActing() ){
                    JCL_IoTfacade iot = JCL_IoTFacadeImpl.getInstance();
                    iot.acting(action.getDeviceNickname(), action.getActuatorNickname(), action.getParam());
                }
                else if (!action.isUseSensorValue()){
                    String superPeerPort = action.getHostTicketPortSuperPeer().equals("null")?null:action.getHostTicketPortSuperPeer();
                    Object[] obj = {action.isUseSensorValue(), action.getTicket()+"", action.getHostTicketIP(), action.getHostTicketPort(), action.getHostTicketMac(), action.getHostTicketPortSuperPeer(), action.getClassName(), action.getMethodName(), action.getParam() };
                    JCL_message_generic msg1 = new MessageGenericImpl();
                    msg1.setType(58);
                    msg1.setRegisterData(obj);
                    JCL_connector controlConnector1 = new ConnectorImpl(true);
                    controlConnector1.connect(action.getHostTicketIP(),Integer.parseInt(action.getHostTicketPort()),action.getHostTicketMac());
                    Log.e("IP", "ip:"+action.getHostTicketIP()+"; port:"+action.getHostTicketPort()+"; mac:"+action.getHostTicketMac()+"; ports:"+action.getHostTicketPortSuperPeer());
                    JCL_message_result r = (JCL_message_result) controlConnector1.sendReceiveG(msg1, superPeerPort);
                }
                else{
                    String superPeerPort = action.getHostTicketPortSuperPeer().equals("null")?null:action.getHostTicketPortSuperPeer();
                    Object[] newParam = new Object[action.getParam().length + 1];
                    if (value.length == 1)
                        newParam [0] = value[0];
                    else
                        newParam[0] = value;
                    for (int i=0; i< action.getParam().length; i++)
                        newParam[i + 1] = action.getParam()[i];
                    Object[] obj = {action.isUseSensorValue(), action.getTicket()+"", action.getHostTicketIP(), action.getHostTicketPort(), action.getHostTicketMac(), action.getHostTicketPortSuperPeer(), action.getClassName(), action.getMethodName(), newParam };
                    JCL_message_generic msg1 = new MessageGenericImpl();
                    msg1.setType(58);
                    msg1.setRegisterData(obj);
                    JCL_connector controlConnector1 = new ConnectorImpl(false);
                    controlConnector1.connect(action.getHostTicketIP(),Integer.parseInt(action.getHostTicketPort()), action.getHostTicketMac());
                    JCL_message_result r = (JCL_message_result) controlConnector1.sendReceiveG(msg1,superPeerPort);
                }
            }
        }
    }

    public void addAction(JCL_Action action) {
        ListIterator<JCL_Action> it = actionList.listIterator();
        it.add(action);
    }

    public String getContextNickname() {
        return contextNickname;
    }

    public void setContextNickname(String contextNickname) {
        this.contextNickname = contextNickname;
    }

    public boolean isTriggered() {
        return triggered;
    }

    public void setTriggered(boolean triggered) {
        this.triggered = triggered;
    }

    public ArrayList<JCL_Action> getActionList() {
        return actionList;
    }

}