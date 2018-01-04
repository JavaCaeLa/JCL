package interfaces.kernel;

/**
 * Created by estevao on 11/07/16.
 */
public interface JCL_message_restart extends JCL_message{

    public abstract void setTimeToRestart(long timeToRestart);

    public abstract long getTimeToRestart();

}
