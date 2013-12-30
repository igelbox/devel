package server;

/**
 *
 * @author igel
 */
public class Semaphore {

    private int locks;

    public synchronized void clear() {
        locks = 0;
    }

    public synchronized void capture() {
        locks++;
    }

    public synchronized void release() {
        locks--;
        if ( locks == 0 )
            notify();
    }

    public synchronized void waitForRelease() throws InterruptedException {
        while ( locks > 0 )
            wait();
    }
}
