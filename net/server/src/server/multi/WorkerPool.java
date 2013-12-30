package server.multi;

import java.io.IOException;
import java.nio.channels.SocketChannel;

/**
 *
 * @author igel
 */
public class WorkerPool extends Thread {

    private final int count;
    private final NetWorker[] workers;
    private int lastWorker;
    int usage;

    public WorkerPool( int count ) throws IOException {
        this.count = count;
        workers = new NetWorker[count];
        for ( int i = count - 1; i >= 0; i-- ) {
            NetWorker w = new NetWorker( this );
            w.start();
            workers[i] = w;
        }
    }

    public void accept( AbstractSession session ) throws IOException {
        SocketChannel channel = session.channel;
        channel.configureBlocking( false );
        workers[lastWorker].offer( session );
        next();
    }

    public synchronized void rebalance( AbstractSession session, int pool ) throws IOException {
        if ( lastWorker == pool )
            next();
        workers[lastWorker].offer( session );
    }

    private void next() {
        lastWorker++;
        if ( lastWorker >= count )
            lastWorker = 0;
    }

    @Override
    public void run() {
        try {
            while ( true ) {
                sleep( 100 );
                int au = 0;
                for ( NetWorker w : workers )
                    au += w.usage;
                usage = au / count;

//                int imin = 0, imax = 0, min = Integer.MAX_VALUE, max = Integer.MIN_VALUE, count = workers.size();
//                for ( int i = count - 1; i >= 0; i-- ) {
//                    int u = usages[i];
//                    if ( u < min ) {
//                        min = u;
//                        imin = i;
//                    }
//                    if ( u > max ) {
//                        max = u;
//                        imax = i;
//                    }
//                }
//                if ( (imin != imax) && (max > min) )
//                    workers.get( imax ).hiload = true;
////                    System.out.println( "HL:" + imax );
//                float au = 0;
//                for ( NetWorker w : workers )
//                    au += w.updateUsage();
//                au /= workers.size();
//                if ( au > 0.25f )
//                    grow( null );
//                System.out.println( au + ":" + workers );
            }
        } catch ( Exception e ) {
            throw new RuntimeException( e );
        }
    }
}
