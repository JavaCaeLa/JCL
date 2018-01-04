package jcl.connection;

/**
 * Created by estevao on 30/06/16.
 */
public class Synchronous {
    public synchronized void waitNow(){
        try {
            this.wait();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public synchronized void wakeUp(){
            this.notifyAll();
    }
}
