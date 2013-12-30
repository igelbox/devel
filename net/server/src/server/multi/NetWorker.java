package server.multi;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Collection;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 *
 * @author igel
 */
public class NetWorker extends Thread {

    private static int idGen;
    private static final Queue<ByteBuffer> pool = new ConcurrentLinkedQueue<ByteBuffer>();
    private final int id = idGen++;
    private final Selector selector = Selector.open();
    private final Queue<AbstractSession> pending = new ConcurrentLinkedQueue<AbstractSession>();
    private final WorkerPool wpool;
    int usage;
    private volatile boolean sel;

    public NetWorker( WorkerPool wpool ) throws IOException {
        this.wpool = wpool;
        setName( getClass().getSimpleName() + id );
    }

    @Override
    public void run() {
        try {
            long lastRebalance = 0;
            while ( true ) {
                sel = true;
                int n = selector.select( 100 );
                sel = false;
                int add = n;
                if ( !pending.isEmpty() )
                    synchronized ( this ) {
                        selector.selectNow();
                        AbstractSession s;
                        while ( (s = pending.poll()) != null ) {
                            add++;
                            int ops = SelectionKey.OP_READ;
                            if ( !s.queue.isEmpty() )
                                ops |= SelectionKey.OP_WRITE;
                            SelectionKey k = s.channel.register( selector, ops );
                            k.attach( s );
                        }
                    }
                if ( n > 0 ) {
                    SelectionKey some = null;
                    Collection<SelectionKey> keys = selector.selectedKeys();
                    for ( SelectionKey k : keys ) {
                        int iops = k.interestOps(), iopso = iops, rops = k.readyOps();
                        AbstractSession session = (AbstractSession) k.attachment();
                        SocketChannel channel = (SocketChannel) k.channel();
                        if ( (rops & SelectionKey.OP_WRITE) != 0 ) {
                            ByteBuffer buff = session.peek();
                            if ( buff != null ) {
                                channel.write( buff );
                                if ( !buff.hasRemaining() ) {
                                    pool.offer( buff );
                                    if ( !session.rem() )
                                        iops &= ~SelectionKey.OP_WRITE;
                                }
                            } else
                                iops &= ~SelectionKey.OP_WRITE;
                        }
                        if ( (rops & SelectionKey.OP_READ) != 0 ) {
                            ByteBuffer buff = pool.poll();
                            if ( buff == null )
                                buff = ByteBuffer.allocate( 32768 );
                            else
                                buff.clear();
                            if ( channel.read( buff ) > 0 ) {
                                buff.flip();
                                session.queue.offer( buff );
                                iops |= SelectionKey.OP_WRITE;
                            } else {
                                pool.offer( buff );
                                k.channel().close();
                                k.cancel();
                                continue;
                            }
                        }
                        if ( iops != iopso )
                            k.interestOps( iops );
                        some = k;
                    }
                    if ( some != null ) {
                        int count = selector.keys().size();
                        usage = count < 8 ? 0 : 65536 * add / count;
                        if ( (usage > 60000) && (wpool.usage < 4096) ) {
                            long t = System.currentTimeMillis();
                            if ( (t - lastRebalance) > 500 ) {
                                AbstractSession session = (AbstractSession) some.attachment();
                                some.cancel();
                                wpool.rebalance( session, id );
                                lastRebalance = t;
                            }
                        }
                    }
                    keys.clear();
                }
            }
        } catch ( IOException e ) {
            throw new RuntimeException( e );
        }
    }

    void offer( AbstractSession session ) {
        pending.offer( session );
        if ( sel )
            selector.wakeup();
    }

    @Override
    public String toString() {
//        return Integer.toString( id );
//        return String.format( "W(%d):%.5f/%d/%d", id, usage(), selector.keys().size(), cnt );
        return String.format( "W(%d):%d", id, usage );
    }
}
