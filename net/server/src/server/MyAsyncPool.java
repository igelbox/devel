package server;

import java.io.IOException;
import java.net.*;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Collection;
import java.util.Queue;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 *
 * @author igel
 */
public class MyAsyncPool implements Runnable {

    private static final Queue<ByteBuffer> pool = new ConcurrentLinkedQueue<ByteBuffer>();

    private static class Session {

        private final SelectionKey key;
        private final SocketChannel channel;
        final Queue<ByteBuffer> queue = new ConcurrentLinkedQueue<ByteBuffer>();
        final Runnable reader = new Runnable() {

            @Override
            public void run() {
                try {
                    ByteBuffer buff = pool.poll();
                    if ( buff == null )
                        buff = ByteBuffer.allocate( 16384 );
                    else
                        buff.clear();
                    channel.read( buff );
                    buff.flip();
                    if ( buff.hasRemaining() ) {
                        queue.offer( buff );
                        key.interestOps( key.interestOps() | SelectionKey.OP_WRITE | SelectionKey.OP_READ );
                        key.selector().wakeup();
                    } else {
                        pool.offer( buff );
                        channel.close();
                        key.cancel();
                    }
                } catch ( IOException e ) {
                    throw new RuntimeException( e );
                }
            }
        };
        final Runnable writer = new Runnable() {

            private final AtomicBoolean lock = new AtomicBoolean();

            @Override
            public void run() {
                if ( lock.compareAndSet( false, true ) )
                    try {
                        ByteBuffer buff;
                        while ( (buff = queue.peek()) != null ) {
                            if ( channel.write( buff ) <= 0 )
                                break;
                            if ( !buff.hasRemaining() )
                                pool.offer( queue.remove() );
                        }
                        if ( buff != null ) {
                            key.interestOps( key.interestOps() | SelectionKey.OP_WRITE );
                            key.selector().wakeup();
                        }
                    } catch ( IOException e ) {
                        throw new RuntimeException( e );
                    } finally {
                        lock.set( false );
                    }
            }
        };

        public Session( SelectionKey key ) {
            this.key = key;
            channel = (SocketChannel) key.channel();
        }
    }
    private final ExecutorService executor = Executors.newFixedThreadPool( 8 );
    private final ServerSocketChannel channel = ServerSocketChannel.open();
    private final SelectionKey key;
    private final Selector selector = Selector.open();

    public MyAsyncPool() throws IOException {
        channel.configureBlocking( false );
        ServerSocket socket = channel.socket();
        socket.bind( new InetSocketAddress( 8080 ) );
        key = channel.register( selector, SelectionKey.OP_ACCEPT );
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
                    if ( k.isValid() )
                        if ( k.isAcceptable() ) {
                            SocketChannel ch = channel.accept();
                            ch.configureBlocking( false );
                            SelectionKey sk = ch.register( selector, SelectionKey.OP_READ );
                            sk.attach( new Session( sk ) );
                        } else {
                            Session session = (Session) k.attachment();
                            if ( k.isWritable() ) {
                                k.interestOps( k.interestOps() & ~SelectionKey.OP_WRITE );
                                executor.execute( session.writer );
                            }
                            if ( k.isReadable() ) {
                                k.interestOps( k.interestOps() & ~SelectionKey.OP_READ );
                                executor.execute( session.reader );
                            }
                        }
                keys.clear();
            }
        } catch ( Exception e ) {
            throw new RuntimeException( e );
        }
    }
}
