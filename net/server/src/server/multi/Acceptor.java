package server.multi;

import java.io.IOException;
import java.net.*;
import java.nio.channels.*;
import java.util.Collection;

/**
 *
 * @author igel
 */
public abstract class Acceptor implements Runnable {

    private final WorkerPool pool;
    private final Selector selector = Selector.open();
    private final ServerSocketChannel channel = ServerSocketChannel.open();

    public Acceptor( int port, int count ) throws IOException {
        pool = new WorkerPool( count );
        channel.configureBlocking( false );
        ServerSocket socket = channel.socket();
        socket.setReuseAddress( true );
        socket.bind( new InetSocketAddress( port ) );
        channel.register( selector, SelectionKey.OP_ACCEPT );
    }

    @Override
    public void run() {
        pool.start();
        try {
            while ( true ) {
                int n = selector.select();
                if ( n <= 0 )
                    continue;
                Collection<SelectionKey> keys = selector.selectedKeys();
                for ( SelectionKey k : keys ) {
                    SocketChannel ch;
                    while ( (ch = channel.accept()) != null )
                        pool.accept( createSession( ch ) );
                }
                keys.clear();
            }
        } catch ( IOException e ) {
            throw new RuntimeException( e );
        }
    }

    protected abstract AbstractSession createSession( SocketChannel channel );
}
