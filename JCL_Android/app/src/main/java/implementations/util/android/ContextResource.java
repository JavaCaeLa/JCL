package implementations.util.android;

import java.util.Map;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ConcurrentMap;

/**
 * Created by estevao on 02/03/17.
 */

public class ContextResource {
    private ConcurrentLinkedQueue<Object[]> resource;
    private boolean finished;

    public ContextResource(){
        resource = new ConcurrentLinkedQueue<>();
        finished = false;
    }

    public synchronized Object[] getElement(){
        if (resource.isEmpty() && !finished)
            try {
                wait();
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        return resource.poll();
    }

    public synchronized void putElement(Object[] account){
        resource.add(account);
        notify();
    }
    public boolean isFinished(){
        return finished&&resource.isEmpty();
    }

    public synchronized void setFinished(boolean f){
        finished = f;
        notifyAll();
    }
}
