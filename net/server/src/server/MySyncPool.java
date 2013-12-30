package server;

import java.io.IOException;
import java.net.*;
import java.nio.channels.*;
import java.util.Collection;
import java.util.concurrent.*;

/**
 *
 * @author igel
 */
public class MySyncPool implements Runnable {

    private final ExecutorService executor = Executors.newFixedThreadPool( 8 );
    private final Semaphore semaphore = new Semaphore();
    private final ServerSocketChannel channel = ServerSocketChannel.open();
    private final Selector selector = Selector.open();
    private final Runnable acceptor = new Runnable() {

        @Override
        public void run() {
            try {
                SocketChannel ch;
                while ( (ch = channel.accept()) != null ) {
                    ch.configureBlocking( false );
                    SelectionKey sk = ch.register( selector, SelectionKey.OP_READ );
                    sk.attach( new Session( sk, semaphore ) );
                }
            } catch ( IOException e ) {
                throw new RuntimeException( e );
            } finally {
                semaphore.release();
            }
        }
    };
    private Runnable trun;

    public MySyncPool() throws IOException {
        channel.configureBlocking( false );
        ServerSocket socket = channel.socket();
        socket.bind( new InetSocketAddress( 8080 ) );
        channel.register( selector, SelectionKey.OP_ACCEPT );
    }

    @Override
    public void run() {
        try {
            while ( true ) {
                int n = selector.select();
                if ( n <= 0 )
                    continue;
                Collection<SelectionKey> keys = selector.selectedKeys();
                for ( SelectionKey k : keys )
                    if ( k.isAcceptable() )
                        sheduleExecutable( acceptor );
                    else {
                        Session session = (Session) k.attachment();
                        if ( k.isWritable() )
                            sheduleExecutable( session.writer );
                        if ( k.isReadable() )
                            sheduleExecutable( session.reader );
                    }
                flushExecutables();
                keys.clear();
            }
        } catch ( Exception e ) {
            throw new RuntimeException( e );
        }
    }

    private void sheduleExecutable( Runnable runnable ) {
        semaphore.capture();
        if ( trun != null )
            executor.execute( trun );
        trun = runnable;
    }

    private void flushExecutables() throws InterruptedException {
        if ( trun != null ) {
            trun.run();
            trun = null;
        }
        semaphore.waitForRelease();
        semaphore.clear();
    }
}
