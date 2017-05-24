package implementations.dm_kernel.IoTuser;

import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.ListIterator;

import org.eclipse.paho.client.mqttv3.MqttMessage;

import implementations.dm_kernel.ConnectorImpl;
import implementations.dm_kernel.MessageGenericImpl;
import interfaces.kernel.JCL_IoTfacade;
import interfaces.kernel.JCL_connector;
import interfaces.kernel.JCL_message_generic;

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
    
    public void check(float[] value, float[] lastValue) {
    	this.value = value;
        if (expression.check(value, lastValue))
        	if (!mqttContext)
        		action();
        	else
        		mqttAction(value);
        else{
        	if (triggered && Board.getMqttClient().isConnected())
        		mqttAction("done");
            triggered = false;
        }
    }

    public void mqttAction(Object value){
    	try{
		    ByteArrayOutputStream out = new ByteArrayOutputStream();
		    ObjectOutputStream os = new ObjectOutputStream(out);
		    os.writeObject(value);
		    
		    MqttMessage message = new MqttMessage(out.toByteArray());
	        message.setQos(2);
		    
	    	if (Board.getMqttClient().isConnected()){
	    		Board.getMqttClient().publish(contextNickname, message);
	    		triggered = true;
	    	}
    	}catch (Exception e) {
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
                if (mqttContext){
                	
                }
                else if ( action.isActing() ){
                	JCL_IoTfacade iot = JCL_IoTFacadeImpl.getInstance();
                	iot.acting(action.getDeviceNickname(), action.getActuatorNickname(), action.getParam());
                }
                else if (!action.isUseSensorValue()){
                	String superPeerPort = action.getHostTicketPortSuperPeer().equals("null")?null:action.getHostTicketPortSuperPeer();
                	Object[] obj = {action.isUseSensorValue(), action.getTicket()+"", action.getHostTicketIP(), action.getHostTicketPort(), action.getHostTicketMac(), action.getHostTicketPortSuperPeer(), action.getClassName(), action.getMethodName(), action.getParam() };
			    	JCL_message_generic msg1 = new MessageGenericImpl();
			    	msg1.setType(58);
			    	msg1.setRegisterData(obj);
			    	JCL_connector controlConnector1 = new ConnectorImpl(false);
			    	controlConnector1.connect(action.getHostTicketIP(),Integer.parseInt(action.getHostTicketPort()),action.getHostTicketMac());
			    	controlConnector1.sendReceiveG(msg1, superPeerPort);
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
			    	controlConnector1.sendReceiveG(msg1,superPeerPort);
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

	public JCL_Expression getExpression() {
		return expression;
	}

	public float[] getValue() {
		return value;
	}
    
}