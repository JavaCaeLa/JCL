package interfaces.kernel;

import java.io.Serializable;

/**
 * Created by estevao on 06/08/16.
 */
public interface JCL_SetSensor extends JCL_message {
    public void setParameters(Object... parameters);
    public Object[] getParameters();
}
