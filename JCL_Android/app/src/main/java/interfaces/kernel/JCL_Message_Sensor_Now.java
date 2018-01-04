package interfaces.kernel;

/**
 * Created by estevao on 11/07/16.
 */
public interface JCL_Message_Sensor_Now extends JCL_message{

    public abstract void setArgs(Object... args);

    public abstract Object[] getArgs();
}
