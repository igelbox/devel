package server.etty;

import java.io.IOException;
import java.net.*;
import java.nio.channels.*;

/**
 *
 * @author igel
 */
public abstract class Acceptor extends Thread {

    private final Selector selector = Selector.open();
    private final ServerSocketChannel channel = ServerSocketChannel.open();
    private final int count;
    private final NetWorker[] workers;
    private int lastWorker;
    int usage;

    public Acceptor( int port, int count ) throws IOException {
        channel.configureBlocking( false );
        ServerSocket socket = channel.socket();
        socket.setReuseAddress( true );
        socket.bind( new InetSocketAddress( port ) );
        channel.register( selector, SelectionKey.OP_ACCEPT );

        this.count = count;
        workers = new NetWorker[count];
        for ( int i = count - 1; i >= 0; i-- ) {
            NetWorker w = new NetWorker( this );
            w.start();
            workers[i] = w;
        }
        setName( getClass().getSimpleName() );
    }

    public void accept( AbstractSession session ) throws IOException {
        SocketChannel ch = session.channel;
        ch.configureBlocking( false );
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
                int n = selector.select( 100 );
                if ( n > 0 ) {
                    SocketChannel ch;
                    while ( (ch = channel.accept()) != null )
                        accept( createSession( ch ) );
                    selector.selectedKeys().clear();
                }
                NetWorker.time = System.currentTimeMillis();
            }
        } catch ( IOException e ) {
            throw new RuntimeException( e );
        }
    }

    protected abstract AbstractSession createSession( SocketChannel channel );
}
