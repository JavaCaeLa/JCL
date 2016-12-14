package implementations.dm_kernel.IoTuser;

import java.util.ArrayList;
import java.util.ListIterator;

import implementations.dm_kernel.user.JCL_FacadeImpl;
import interfaces.kernel.JCL_facade;

public class JCL_Context {

    private String contextNickname;
    private JCL_Expression expression;
    private boolean triggered;
    private ArrayList<JCL_Action> actionList;
    private float[] value;
    
    public JCL_Context(JCL_Expression expression, String contextNickname) {
        this.expression = expression;
        this.contextNickname = contextNickname;
        triggered = false;
        actionList = new ArrayList<>();
    }

    public void check(float[] value) {
    	this.value = value;
        if (expression.check(value))
            action();
        else
            triggered = false;
    }

    public void action() {
        if (!triggered) {
            System.out.println("*** Context reached ***");
            triggered = true;
            ListIterator<JCL_Action> it = actionList.listIterator();
            while (it.hasNext()) {
                JCL_Action action = it.next();
                JCL_facade facade = JCL_FacadeImpl.getInstance();
                if (!action.isUseSensorValue())
                	facade.execute(action.getClassName(), action.getMethodName(), action.getParam());
                else{
                	Object[] newParam = new Object[action.getParam().length + 1];
                	if (value.length == 1)
                		newParam [0] = value[0];
                	else
                		newParam[0] = value;
                	for (int i=0; i< action.getParam().length; i++)
                		newParam[i + 1] = action.getParam()[i];
                	facade.execute(action.getClassName(), action.getMethodName(), newParam);
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